package net.swimmingtuna.lotm.util.PlayerMobs;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.Util;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.swimmingtuna.lotm.client.Configs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NameManager {

    public static final NameManager INSTANCE = new NameManager();
    private static final Logger LOGGER = LogManager.getLogger();
    private final Set<PlayerName> allNames = ConcurrentHashMap.newKeySet();
    private final Set<PlayerName> remoteNames = ConcurrentHashMap.newKeySet();
    private final Set<PlayerName> usedNames = ConcurrentHashMap.newKeySet();
    private final Queue<PlayerName> namePool = new ConcurrentLinkedQueue<>();

    private boolean firstSync = true;
    private int tickTime = 0;
    private int syncTime = 0;
    @Nullable
    private CompletableFuture<Integer> syncFuture = null;
    private boolean setup = false;

    private NameManager() {
    }

    public void init() {
        if (!setup) {
            MinecraftForge.EVENT_BUS.addListener(this::serverTick);
            setup = true;
            updateNameList();
        }
    }

    public PlayerName getRandomName() {
        PlayerName name = namePool.poll();
        if (name == null)
            name = new PlayerName("Gory_Moon");
        useName(name);
        return name;
    }

    public void useName(PlayerName name) {
        namePool.remove(name);
        usedNames.add(name);
        if (namePool.isEmpty()) {
            updateNameList();
        }
    }

    public Optional<PlayerName> findName(String name) {
        for (PlayerName playerName : allNames) {
            if (playerName.getDisplayName().equalsIgnoreCase(name))
                return Optional.of(playerName);
        }
        return Optional.empty();
    }

    private void updateNameList() {
        Set<PlayerName> allNames = new ObjectOpenHashSet<>();
        for (String name : Configs.COMMON.mobNames.get()) {
            allNames.add(new PlayerName(name));
        }
        allNames.addAll(remoteNames);

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (setup && Configs.COMMON.useWhitelist.get() && server != null) {
            for (String name : server.getPlayerList().getWhiteListNames()) {
                allNames.add(new PlayerName(name));
            }
        }

        allNames.removeIf(PlayerName::isInvalid);
        this.allNames.clear();
        this.allNames.addAll(allNames);

        if (!namePool.isEmpty()) {
            allNames.removeAll(usedNames);
            allNames.removeAll(namePool);
        } else {
            usedNames.clear();
        }
        ObjectArrayList<PlayerName> names = new ObjectArrayList<>(allNames);
        Collections.shuffle(names);
        namePool.addAll(names);
    }

    // SubscribeEvent
    private void serverTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            syncTime++;

            if (tickTime > 0 && syncTime >= tickTime || firstSync) {
                syncTime = 0;
                firstSync = false;
                reloadRemoteLinks();
            }
        }
    }

    public void configLoad() {
        tickTime = Configs.COMMON.nameLinksSyncTime.get() * 1200; // time * 60 seconds * 20 ticks
        updateNameList();
    }

    public CompletableFuture<Integer> reloadRemoteLinks() {
        if (syncFuture != null && !syncFuture.isDone())
            return CompletableFuture.completedFuture(0);

        syncFuture = CompletableFuture.supplyAsync(() -> {
            Set<PlayerName> nameList = new ObjectOpenHashSet<>();
            for (String link : Configs.COMMON.nameLinks.get()) {
                try {
                    URL url = new URL(link);
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            nameList.add(new PlayerName(line));
                        }
                    }
                } catch (IOException e) {
                    LOGGER.error(String.format("Error fetching names from %s", link), e);
                }
            }

            int diff = nameList.size();

            ThreadUtils.tryRunOnMain(() -> {
                this.remoteNames.clear();
                this.remoteNames.addAll(nameList);
                updateNameList();
            });
            return diff;
        }, Util.backgroundExecutor());
        return syncFuture;
    }
}

