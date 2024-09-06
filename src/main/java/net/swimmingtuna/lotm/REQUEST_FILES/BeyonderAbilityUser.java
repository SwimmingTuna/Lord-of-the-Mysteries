package net.swimmingtuna.lotm.REQUEST_FILES;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.networking.LOTMNetworkHandler;
import net.swimmingtuna.lotm.networking.packet.LeftClickC2S;
import org.apache.commons.lang3.StringUtils;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BeyonderAbilityUser extends Item {

    public BeyonderAbilityUser(Properties pProperties) { //IMPORTANT!!!! FIGURE OUT HOW TO MAKE THIS WORK BY CLICKING ON A
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!player.level().isClientSide()) {
            ItemStack heldItem = player.getItemInHand(hand);
            byte[] keysClicked = player.getPersistentData().getByteArray("keysClicked");
            for (int i = 0; i < keysClicked.length; i++) {
                if (keysClicked[i] == 0) {
                    keysClicked[i] = 2;
                    player.getPersistentData().putInt("keyHasBeenClicked", 40);
                    return InteractionResultHolder.success(heldItem);
                }
            }
        }
        return super.use(level, player, hand);
    }


    public static void resetClicks(Player player) {
        player.getPersistentData().putByteArray("keysClicked", new byte[5]);
        player.displayClientMessage(Component.empty(), true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        if (event.getEntity().getMainHandItem().getItem() instanceof BeyonderAbilityUser) {
            LOTMNetworkHandler.sendToServer(new LeftClickC2S());
        }
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        ItemStack heldItem = player.getMainHandItem();
        byte[] keysClicked = player.getPersistentData().getByteArray("keysClicked");

        if (heldItem.isEmpty() || !(heldItem.getItem() instanceof BeyonderAbilityUser)) {
            return;
        }
        for (int i = 0; i < keysClicked.length; i++) {
            if (keysClicked[i] == 0) {
                keysClicked[i] = 1;
                player.getPersistentData().putInt("keyHasBeenClicked", 40);
                return;
            }
        }
    }

    @SubscribeEvent
    public static void keyTimer(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (!player.level().isClientSide() && event.phase == TickEvent.Phase.END) {
            int keyClicked = player.getPersistentData().getInt("keyHasBeenClicked");
            byte[] keysClicked = player.getPersistentData().getByteArray("keysClicked");
            if (keyClicked >= 1) {
                player.getPersistentData().putInt("keyHasBeenClicked", keyClicked - 1);

                StringBuilder stringBuilder = new StringBuilder(5);
                for (byte b : keysClicked) {
                    char charToAdd = switch (b) {
                        case 1 -> 'L';
                        case 2 -> 'R';
                        default -> '_';
                    };
                    stringBuilder.append(charToAdd);
                }
                String actionBarString = StringUtils.join(stringBuilder.toString().toCharArray(), ' ');

                Component actionBarComponent = Component.literal(actionBarString).withStyle(ChatFormatting.BOLD);
                player.displayClientMessage(actionBarComponent, true);

                if (keysClicked[4] == 0) return;

                int abilityNumber = 0;
                for (int i = 0; i < keysClicked.length; i++) {
                    abilityNumber |= (keysClicked[i] - 1) << (4 - i);
                }
                ++abilityNumber;

                resetClicks(player);
                BeyonderUtil.useAbilityByNumber(player, abilityNumber);

            }
            if (keyClicked == 0) {
                player.getPersistentData().putByteArray("keysClicked", new byte[5]);
            }
        }
    }

    @SubscribeEvent
    public static void rightClick(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        ItemStack heldItem = player.getMainHandItem();
        byte[] keysClicked = player.getPersistentData().getByteArray("keysClicked");

        if (heldItem.isEmpty() || !(heldItem.getItem() instanceof BeyonderAbilityUser)) {
            return;
        }
        for (int i = 0; i < keysClicked.length; i++) {
            if (keysClicked[i] == 0) {
                keysClicked[i] = 2;
                player.getPersistentData().putInt("keyHasBeenClicked", 40);
                return;
            }
        }
    }
}
