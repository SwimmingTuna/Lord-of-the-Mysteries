package net.swimmingtuna.lotm.caps;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.simple.SimpleChannel;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.beyonder.api.BeyonderClass;
import net.swimmingtuna.lotm.commands.BeyonderClassArgument;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.networking.LOTMNetworkHandler;
import net.swimmingtuna.lotm.util.CapabilitySyncer.core.PlayerCapability;
import net.swimmingtuna.lotm.util.CapabilitySyncer.network.EntityCapabilityStatusPacket;
import net.swimmingtuna.lotm.util.CapabilitySyncer.network.SimpleEntityCapabilityStatusPacket;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = LOTM.MOD_ID)
public class BeyonderHolder extends PlayerCapability {
    public static final int SEQUENCE_MIN = 0;
    public static final int SEQUENCE_MAX = 9;
    private int currentSequence = -1;
    private BeyonderClass currentClass = null;
    private double spirituality = 100;
    private double maxSpirituality = 100;
    private double spiritualityRegen = 1;
    private final RandomSource random;

    protected BeyonderHolder(Player entity) {
        super(entity);
        random = RandomSource.create();
    }

    public void removeClass() {
        this.currentClass = null;
        this.currentSequence = -1;
        this.spirituality = 100;
        this.maxSpirituality = 100;
        this.spiritualityRegen = 1;
        updateTracking();
    }
    public void setClassAndSequence(BeyonderClass newClass, int sequence) {
        this.currentClass = newClass;
        this.currentSequence = sequence;
        maxSpirituality = currentClass.spiritualityLevels().get(currentSequence);
        spirituality = maxSpirituality;
        spiritualityRegen = currentClass.spiritualityRegen().get(currentSequence);
        player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(currentClass.maxHealth().get(sequence));
        player.setHealth(player.getMaxHealth());
        updateTracking();
    }

    public double getMaxSpirituality() {
        return maxSpirituality;
    }

    public void setMaxSpirituality(int maxSpirituality) {
        this.maxSpirituality = maxSpirituality;
        updateTracking();
    }

    public double getSpiritualityRegen() {
        return spiritualityRegen;
    }

    public void setSpiritualityRegen(int spiritualityRegen) {
        this.spiritualityRegen = spiritualityRegen;
        updateTracking();
    }

    public double getSpirituality() {
        return spirituality;
    }

    public BeyonderClass getCurrentClass() {
        return currentClass;
    }

    public void setCurrentClass(BeyonderClass newClass) {
        this.currentClass = newClass;
        updateTracking();
    }
    public void removeCurrentClass() {
        this.currentClass = null;
        updateTracking();
    }
    public int getCurrentSequence() {
        return currentSequence;
    }

    public void setCurrentSequence(int currentSequence) {
        this.currentSequence = currentSequence;
        maxSpirituality = currentClass.spiritualityLevels().get(currentSequence);
        spiritualityRegen = currentClass.spiritualityRegen().get(currentSequence);
        spirituality = maxSpirituality;
        updateTracking();
    }
    public void incrementSequence() {
        if (currentSequence > SEQUENCE_MIN) {
            currentSequence--;
            maxSpirituality = currentClass.spiritualityLevels().get(currentSequence);
            spirituality = maxSpirituality;
            spiritualityRegen = currentClass.spiritualityRegen().get(currentSequence);
            updateTracking();
        }
    }
    public void decrementSequence() {
        if (currentSequence < SEQUENCE_MAX) {
            currentSequence++;
            maxSpirituality = currentClass.spiritualityLevels().get(currentSequence);
            spirituality = maxSpirituality;
            spiritualityRegen = currentClass.spiritualityRegen().get(currentSequence);
            updateTracking();
        }
    }
    public void setSpirituality(int spirituality) {
        this.spirituality = Mth.clamp(spirituality,0, maxSpirituality);
        updateTracking();
    }
    public boolean useSpirituality(int amount) {
        if(this.spirituality-amount < 0) {
            return false;
        }
        this.spirituality = Mth.clamp(spirituality - amount, 0, maxSpirituality);
        updateTracking();
        return true;
    }
    public void increaseSpirituality(int amount) {
        this.spirituality = Mth.clamp(spirituality + amount, 0, maxSpirituality);
        updateTracking();
    }
    @Override
    public CompoundTag serializeNBT(boolean savingToDisk) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("currentSequence", currentSequence);
        tag.putString("currentClass", currentClass == null ? "" : BeyonderClassInit.getRegistry().getKey(currentClass).toString());
        tag.putDouble("spirituality", spirituality);
        tag.putDouble("maxSpirituality", maxSpirituality);
        tag.putDouble("spiritualityRegen", spiritualityRegen);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {
        currentSequence = nbt.getInt("currentSequence");
        String className = nbt.getString("currentClass");
        if (!className.isEmpty()) {
            currentClass = BeyonderClassInit.getRegistry().getValue(new ResourceLocation(className));
        }
        spirituality = nbt.getDouble("spirituality");
        maxSpirituality = nbt.getDouble("maxSpirituality");
        spiritualityRegen = nbt.getDouble("spiritualityRegen");
    }

    @Override
    public EntityCapabilityStatusPacket createUpdatePacket() {
        return new SimpleEntityCapabilityStatusPacket(this.entity.getId(), BeyonderHolderAttacher.RESOURCE_LOCATION, this);
    }

    @Override
    public SimpleChannel getNetworkChannel() {
        return LOTMNetworkHandler.INSTANCE;
    }

    public void regenSpirituality(Entity pEntity) {
        if (pEntity instanceof Player) {
            if (spirituality < maxSpirituality) {
                double increase = (Mth.nextDouble(random,0.1,1.0) * (spiritualityRegen * 1.5f)) / 5;
                spirituality = Mth.clamp(spirituality + increase,0, maxSpirituality);
                updateTracking();
            }
        }
    }

    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent event) {
        if (!event.player.level().isClientSide && event.phase == TickEvent.Phase.END) {
            BeyonderHolderAttacher.getHolder(event.player).ifPresent(holder -> {
                holder.regenSpirituality(event.player);
                if (holder.getCurrentClass() != null) {
                    holder.getCurrentClass().tick(event.player, holder.getCurrentSequence());
                }
            });
        }

    }
    public boolean isSpectatorClass() {
        return this.currentClass != null && this.currentClass.equals(BeyonderClassInit.SPECTATOR.get());
    }
    public boolean isTyrantClass() {
        return this.currentClass != null && this.currentClass.equals(BeyonderClassInit.TYRANT.get());
    }
}
