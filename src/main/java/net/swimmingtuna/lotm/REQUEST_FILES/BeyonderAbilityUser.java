package net.swimmingtuna.lotm.REQUEST_FILES;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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
import net.swimmingtuna.lotm.events.ReachChangeUUIDs;
import net.swimmingtuna.lotm.networking.LOTMNetworkHandler;
import net.swimmingtuna.lotm.networking.packet.LeftClickC2S;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Stream;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BeyonderAbilityUser extends Item implements ReachChangeUUIDs {

    public BeyonderAbilityUser(Properties pProperties) { //IMPORTANT!!!! FIGURE OUT HOW TO MAKE THIS WORK BY CLICKING ON A
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        if (!pPlayer.level().isClientSide()) {
            ItemStack heldItem = pPlayer.getItemInHand(hand);
            byte firstKeyClicked = pPlayer.getPersistentData().getByte("firstKeyClicked");
            byte secondKeyClicked = pPlayer.getPersistentData().getByte("secondKeyClicked");
            byte thirdKeyClicked = pPlayer.getPersistentData().getByte("thirdKeyClicked");
            byte fourthKeyClicked = pPlayer.getPersistentData().getByte("fourthKeyClicked");
            byte fifthKeyClicked = pPlayer.getPersistentData().getByte("fifthKeyClicked");
            if (firstKeyClicked == 0 && secondKeyClicked == 0 && thirdKeyClicked == 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                pPlayer.getPersistentData().putByte("firstKeyClicked", (byte) 2);
                pPlayer.getPersistentData().putInt("keyHasBeenClicked", 40);
                return InteractionResultHolder.success(heldItem);
            }
            if (firstKeyClicked != 0 && secondKeyClicked == 0 && thirdKeyClicked == 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                pPlayer.getPersistentData().putByte("secondKeyClicked", (byte) 2);
                pPlayer.getPersistentData().putInt("keyHasBeenClicked", 40);
                return InteractionResultHolder.success(heldItem);
            }
            if (firstKeyClicked != 0 && secondKeyClicked != 0 && thirdKeyClicked == 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                pPlayer.getPersistentData().putByte("thirdKeyClicked", (byte) 2);
                pPlayer.getPersistentData().putInt("keyHasBeenClicked", 40);
                return InteractionResultHolder.success(heldItem);
            }
            if (firstKeyClicked != 0 && secondKeyClicked != 0 && thirdKeyClicked != 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                pPlayer.getPersistentData().putByte("fourthKeyClicked", (byte) 2);
                pPlayer.getPersistentData().putInt("keyHasBeenClicked", 40);
                return InteractionResultHolder.success(heldItem);
            }
            if (firstKeyClicked != 0 && secondKeyClicked != 0 && thirdKeyClicked != 0 && fourthKeyClicked != 0 && fifthKeyClicked == 0) {
                pPlayer.getPersistentData().putByte("fifthKeyClicked", (byte) 2);
                pPlayer.getPersistentData().putInt("keyHasBeenClicked", 40);
                return InteractionResultHolder.success(heldItem);
            }
        }
        return super.use(level, pPlayer, hand);
    }


    public static void resetClicks(Player pPlayer) {
        pPlayer.getPersistentData().putByte("firstKeyClicked", (byte) 0);
        pPlayer.getPersistentData().putByte("secondKeyClicked", (byte) 0);
        pPlayer.getPersistentData().putByte("thirdKeyClicked", (byte) 0);
        pPlayer.getPersistentData().putByte("fourthKeyClicked", (byte) 0);
        pPlayer.getPersistentData().putByte("fifthKeyClicked", (byte) 0);
        pPlayer.displayClientMessage(Component.literal(" "), true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        if (event.getEntity().getMainHandItem().getItem() instanceof BeyonderAbilityUser) {
            LOTMNetworkHandler.sendToServer(new LeftClickC2S());
        }
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        int firstKeyClicked = pPlayer.getPersistentData().getByte("firstKeyClicked");
        int secondKeyClicked = pPlayer.getPersistentData().getByte("secondKeyClicked");
        int thirdKeyClicked = pPlayer.getPersistentData().getByte("thirdKeyClicked");
        int fourthKeyClicked = pPlayer.getPersistentData().getByte("fourthKeyClicked");
        int fifthKeyClicked = pPlayer.getPersistentData().getByte("fifthKeyClicked");

        if (!heldItem.isEmpty() && heldItem.getItem() instanceof BeyonderAbilityUser) {
            if (firstKeyClicked == 0 && secondKeyClicked == 0 && thirdKeyClicked == 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                pPlayer.getPersistentData().putByte("firstKeyClicked", (byte) 1);
                pPlayer.getPersistentData().putInt("keyHasBeenClicked", 40);
            }
            if (firstKeyClicked != 0 && secondKeyClicked == 0 && thirdKeyClicked == 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                pPlayer.getPersistentData().putByte("secondKeyClicked", (byte) 1);
                pPlayer.getPersistentData().putInt("keyHasBeenClicked", 40);
            }
            if (firstKeyClicked != 0 && secondKeyClicked != 0 && thirdKeyClicked == 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                pPlayer.getPersistentData().putByte("thirdKeyClicked", (byte) 1);
                pPlayer.getPersistentData().putInt("keyHasBeenClicked", 40);
            }
            if (firstKeyClicked != 0 && secondKeyClicked != 0 && thirdKeyClicked != 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                pPlayer.getPersistentData().putByte("fourthKeyClicked", (byte) 1);
                pPlayer.getPersistentData().putInt("keyHasBeenClicked", 40);
            }
            if (firstKeyClicked != 0 && secondKeyClicked != 0 && thirdKeyClicked != 0 && fourthKeyClicked != 0 && fifthKeyClicked == 0) {
                pPlayer.getPersistentData().putByte("fifthKeyClicked", (byte) 1);
                pPlayer.getPersistentData().putInt("keyHasBeenClicked", 40);
            }
        }
    }

    @SubscribeEvent
    public static void keyTimer(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (!player.level().isClientSide() && event.phase == TickEvent.Phase.END) {
            int keyClicked = player.getPersistentData().getInt("keyHasBeenClicked");
            byte firstKeyClicked = player.getPersistentData().getByte("firstKeyClicked");
            byte secondKeyClicked = player.getPersistentData().getByte("secondKeyClicked");
            byte thirdKeyClicked = player.getPersistentData().getByte("thirdKeyClicked");
            byte fourthKeyClicked = player.getPersistentData().getByte("fourthKeyClicked");
            byte fifthKeyClicked = player.getPersistentData().getByte("fifthKeyClicked");
            if (keyClicked >= 1) {
                player.getPersistentData().putInt("keyHasBeenClicked", keyClicked - 1);

                List<Byte> keysClicked = List.of(firstKeyClicked, secondKeyClicked, thirdKeyClicked, fourthKeyClicked, fifthKeyClicked);
                Stream<Character> characterStream = keysClicked.stream().flatMap(xKeyClicked -> switch (xKeyClicked) {
                    case 0 -> Stream.of('_');
                    case 1 -> Stream.of('L');
                    case 2 -> Stream.of('R');
                    default -> null;
                });
                String actionBarString = StringUtils.join(characterStream.toList(), ' ');

                Component actionBarComponent = Component.literal(actionBarString).withStyle(ChatFormatting.BOLD);
                player.displayClientMessage(actionBarComponent, true);

                if (fifthKeyClicked == 0) return;

                int abilityNumber = (firstKeyClicked - 1) << 4 | (secondKeyClicked - 1) << 3 | (thirdKeyClicked - 1) << 2 | (fourthKeyClicked - 1) << 1 | (fifthKeyClicked - 1) + 1;
                resetClicks(player);
                BeyonderUtil.useAbilityByNumber(player, abilityNumber);

            }
            if (keyClicked == 0) {
                player.getPersistentData().putByte("firstKeyClicked", (byte) 0);
                player.getPersistentData().putByte("secondKeyClicked", (byte) 0);
                player.getPersistentData().putByte("thirdKeyClicked", (byte) 0);
                player.getPersistentData().putByte("fourthKeyClicked", (byte) 0);
                player.getPersistentData().putByte("fifthKeyClicked", (byte) 0);
            }
        }
    }

    @SubscribeEvent
    public static void rightClick(PlayerInteractEvent.EntityInteract event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        byte firstKeyClicked = pPlayer.getPersistentData().getByte("firstKeyClicked");
        byte secondKeyClicked = pPlayer.getPersistentData().getByte("secondKeyClicked");
        byte thirdKeyClicked = pPlayer.getPersistentData().getByte("thirdKeyClicked");
        byte fourthKeyClicked = pPlayer.getPersistentData().getByte("fourthKeyClicked");
        byte fifthKeyClicked = pPlayer.getPersistentData().getByte("fifthKeyClicked");

        if (!heldItem.isEmpty() && heldItem.getItem() instanceof BeyonderAbilityUser) {
            if (firstKeyClicked == 0 && secondKeyClicked == 0 && thirdKeyClicked == 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                pPlayer.getPersistentData().putByte("firstKeyClicked", (byte) 2);
                pPlayer.getPersistentData().putInt("keyHasBeenClicked", 40);
            }
            if (firstKeyClicked != 0 && secondKeyClicked == 0 && thirdKeyClicked == 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                pPlayer.getPersistentData().putByte("secondKeyClicked", (byte) 2);
                pPlayer.getPersistentData().putInt("keyHasBeenClicked", 40);
            }
            if (firstKeyClicked != 0 && secondKeyClicked != 0 && thirdKeyClicked == 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                pPlayer.getPersistentData().putByte("thirdKeyClicked", (byte) 2);
                pPlayer.getPersistentData().putInt("keyHasBeenClicked", 40);
            }
            if (firstKeyClicked != 0 && secondKeyClicked != 0 && thirdKeyClicked != 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                pPlayer.getPersistentData().putByte("fourthKeyClicked", (byte) 2);
                pPlayer.getPersistentData().putInt("keyHasBeenClicked", 40);
            }
            if (firstKeyClicked != 0 && secondKeyClicked != 0 && thirdKeyClicked != 0 && fourthKeyClicked != 0 && fifthKeyClicked == 0) {
                pPlayer.getPersistentData().putByte("fifthKeyClicked", (byte) 2);
                pPlayer.getPersistentData().putInt("keyHasBeenClicked", 40);
            }
        }
    }
}
