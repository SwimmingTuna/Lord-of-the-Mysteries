package net.swimmingtuna.lotm.item.BeyonderAbilities;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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


@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BeyonderAbilityUser extends Item implements ReachChangeUUIDs {

    public BeyonderAbilityUser(Properties pProperties) { //IMPORTANT!!!! FIGURE OUT HOW TO MAKE THIS WORK BY CLICKING ON A
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        if (!pPlayer.level().isClientSide()) {
            ItemStack heldItem = pPlayer.getMainHandItem();
            int firstKeyClicked = pPlayer.getPersistentData().getInt("firstKeyClicked");
            int secondKeyClicked = pPlayer.getPersistentData().getInt("secondKeyClicked");
            int thirdKeyClicked = pPlayer.getPersistentData().getInt("thirdKeyClicked");
            int fourthKeyClicked = pPlayer.getPersistentData().getInt("fourthKeyClicked");
            int fifthKeyClicked = pPlayer.getPersistentData().getInt("fifthKeyClicked");
            if (firstKeyClicked == 0 && secondKeyClicked == 0 && thirdKeyClicked == 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                pPlayer.getPersistentData().putInt("firstKeyClicked", 2);
                pPlayer.getPersistentData().putInt("keyHasBeenClicked", 40);
            }
            if (firstKeyClicked != 0 && secondKeyClicked == 0 && thirdKeyClicked == 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                pPlayer.getPersistentData().putInt("secondKeyClicked", 2);
                pPlayer.getPersistentData().putInt("keyHasBeenClicked", 40);
            }
            if (firstKeyClicked != 0 && secondKeyClicked != 0 && thirdKeyClicked == 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                pPlayer.getPersistentData().putInt("thirdKeyClicked", 2);
                pPlayer.getPersistentData().putInt("keyHasBeenClicked", 40);
            }
            if (firstKeyClicked != 0 && secondKeyClicked != 0 && thirdKeyClicked != 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                pPlayer.getPersistentData().putInt("fourthKeyClicked", 2);
                pPlayer.getPersistentData().putInt("keyHasBeenClicked", 40);
            }
            if (firstKeyClicked != 0 && secondKeyClicked != 0 && thirdKeyClicked != 0 && fourthKeyClicked != 0 && fifthKeyClicked == 0) {
                pPlayer.getPersistentData().putInt("fifthKeyClicked", 2);
                pPlayer.getPersistentData().putInt("keyHasBeenClicked", 40);
            }
        }
        return super.use(level, pPlayer, hand);
    }


    public static void resetClicks(Player pPlayer) {
        pPlayer.getPersistentData().putInt("firstKeyClicked", 0);
        pPlayer.getPersistentData().putInt("secondKeyClicked", 0);
        pPlayer.getPersistentData().putInt("thirdKeyClicked", 0);
        pPlayer.getPersistentData().putInt("fourthKeyClicked", 0);
        pPlayer.getPersistentData().putInt("fifthKeyClicked", 0);
        pPlayer.displayClientMessage(Component.literal(" "), true);
    }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        Player pPlayer = event.getEntity();
        if (pPlayer.level().isClientSide()) {
            ItemStack heldItem = pPlayer.getMainHandItem();
            pPlayer.sendSystemMessage(Component.literal("working"));
            int firstKeyClicked = pPlayer.getPersistentData().getInt("firstKeyClicked");
            int secondKeyClicked = pPlayer.getPersistentData().getInt("secondKeyClicked");
            int thirdKeyClicked = pPlayer.getPersistentData().getInt("thirdKeyClicked");
            int fourthKeyClicked = pPlayer.getPersistentData().getInt("fourthKeyClicked");
            int fifthKeyClicked = pPlayer.getPersistentData().getInt("fifthKeyClicked");

            if (!heldItem.isEmpty() && heldItem.getItem() instanceof BeyonderAbilityUser) {
                if (firstKeyClicked == 0 && secondKeyClicked == 0 && thirdKeyClicked == 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                    pPlayer.getPersistentData().putInt("firstKeyClicked", 1);
                    pPlayer.getPersistentData().putInt("keyHasBeenClicked", 40);
                }
                if (firstKeyClicked != 0 && secondKeyClicked == 0 && thirdKeyClicked == 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                    pPlayer.getPersistentData().putInt("secondKeyClicked", 1);
                    pPlayer.getPersistentData().putInt("keyHasBeenClicked", 40);
                }
                if (firstKeyClicked != 0 && secondKeyClicked != 0 && thirdKeyClicked == 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                    pPlayer.getPersistentData().putInt("thirdKeyClicked", 1);
                    pPlayer.getPersistentData().putInt("keyHasBeenClicked", 40);
                }
                if (firstKeyClicked != 0 && secondKeyClicked != 0 && thirdKeyClicked != 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                    pPlayer.getPersistentData().putInt("fourthKeyClicked", 1);
                    pPlayer.getPersistentData().putInt("keyHasBeenClicked", 40);
                }
                if (firstKeyClicked != 0 && secondKeyClicked != 0 && thirdKeyClicked != 0 && fourthKeyClicked != 0 && fifthKeyClicked == 0) {
                    pPlayer.getPersistentData().putInt("fifthKeyClicked", 1);
                    pPlayer.getPersistentData().putInt("keyHasBeenClicked", 40);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        int firstKeyClicked = pPlayer.getPersistentData().getInt("firstKeyClicked");
        int secondKeyClicked = pPlayer.getPersistentData().getInt("secondKeyClicked");
        int thirdKeyClicked = pPlayer.getPersistentData().getInt("thirdKeyClicked");
        int fourthKeyClicked = pPlayer.getPersistentData().getInt("fourthKeyClicked");
        int fifthKeyClicked = pPlayer.getPersistentData().getInt("fifthKeyClicked");

        if (!heldItem.isEmpty() && heldItem.getItem() instanceof BeyonderAbilityUser) {
            if (firstKeyClicked == 0 && secondKeyClicked == 0 && thirdKeyClicked == 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                pPlayer.getPersistentData().putInt("firstKeyClicked", 1);
                pPlayer.getPersistentData().putInt("keyHasBeenClicked", 40);
            }
            if (firstKeyClicked != 0 && secondKeyClicked == 0 && thirdKeyClicked == 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                pPlayer.getPersistentData().putInt("secondKeyClicked", 1);
                pPlayer.getPersistentData().putInt("keyHasBeenClicked", 40);
            }
            if (firstKeyClicked != 0 && secondKeyClicked != 0 && thirdKeyClicked == 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                pPlayer.getPersistentData().putInt("thirdKeyClicked", 1);
                pPlayer.getPersistentData().putInt("keyHasBeenClicked", 40);
            }
            if (firstKeyClicked != 0 && secondKeyClicked != 0 && thirdKeyClicked != 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                pPlayer.getPersistentData().putInt("fourthKeyClicked", 1);
                pPlayer.getPersistentData().putInt("keyHasBeenClicked", 40);
            }
            if (firstKeyClicked != 0 && secondKeyClicked != 0 && thirdKeyClicked != 0 && fourthKeyClicked != 0 && fifthKeyClicked == 0) {
                pPlayer.getPersistentData().putInt("fifthKeyClicked", 1);
                pPlayer.getPersistentData().putInt("keyHasBeenClicked", 40);
            }
        }
    }

    @SubscribeEvent
    public static void keyTimer(TickEvent.PlayerTickEvent event) {
        Player pPlayer = event.player;
        if (!pPlayer.level().isClientSide() && event.phase == TickEvent.Phase.END) {
            int keyClicked = pPlayer.getPersistentData().getInt("keyHasBeenClicked");
            int firstKeyClicked = pPlayer.getPersistentData().getInt("firstKeyClicked");
            int secondKeyClicked = pPlayer.getPersistentData().getInt("secondKeyClicked");
            int thirdKeyClicked = pPlayer.getPersistentData().getInt("thirdKeyClicked");
            int fourthKeyClicked = pPlayer.getPersistentData().getInt("fourthKeyClicked");
            int fifthKeyClicked = pPlayer.getPersistentData().getInt("fifthKeyClicked");
            if (keyClicked >= 1) {
                pPlayer.getPersistentData().putInt("keyHasBeenClicked", keyClicked - 1);

                if (firstKeyClicked == 1 && secondKeyClicked == 0 && thirdKeyClicked == 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                    pPlayer.displayClientMessage(Component.literal("L  _  _  _  _").withStyle(ChatFormatting.BOLD), true);
                }
                if (firstKeyClicked == 2 && secondKeyClicked == 0 && thirdKeyClicked == 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                    pPlayer.displayClientMessage(Component.literal("R  _  _  _  _").withStyle(ChatFormatting.BOLD), true);
                }
                if (firstKeyClicked == 1 && secondKeyClicked == 1 && thirdKeyClicked == 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                    pPlayer.displayClientMessage(Component.literal("L  L  _  _  _").withStyle(ChatFormatting.BOLD), true);
                }
                if (firstKeyClicked == 1 && secondKeyClicked == 2 && thirdKeyClicked == 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                    pPlayer.displayClientMessage(Component.literal("L  R  _  _  _").withStyle(ChatFormatting.BOLD), true);
                }
                if (firstKeyClicked == 2 && secondKeyClicked == 1 && thirdKeyClicked == 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                    pPlayer.displayClientMessage(Component.literal("R  L  _  _  _").withStyle(ChatFormatting.BOLD), true);
                }
                if (firstKeyClicked == 2 && secondKeyClicked == 2 && thirdKeyClicked == 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                    pPlayer.displayClientMessage(Component.literal("R  R  _  _  _").withStyle(ChatFormatting.BOLD), true);
                }
                if (firstKeyClicked == 1 && secondKeyClicked == 1 && thirdKeyClicked == 1 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                    pPlayer.displayClientMessage(Component.literal("L  L  L  _  _").withStyle(ChatFormatting.BOLD), true);
                }
                if (firstKeyClicked == 1 && secondKeyClicked == 1 && thirdKeyClicked == 2 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                    pPlayer.displayClientMessage(Component.literal("L  L  R  _  _").withStyle(ChatFormatting.BOLD), true);
                }
                if (firstKeyClicked == 1 && secondKeyClicked == 2 && thirdKeyClicked == 1 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                    pPlayer.displayClientMessage(Component.literal("L  R  L  _  _").withStyle(ChatFormatting.BOLD), true);
                }
                if (firstKeyClicked == 1 && secondKeyClicked == 2 && thirdKeyClicked == 2 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                    pPlayer.displayClientMessage(Component.literal("L  R  R  _  _").withStyle(ChatFormatting.BOLD), true);
                }
                if (firstKeyClicked == 2 && secondKeyClicked == 1 && thirdKeyClicked == 1 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                    pPlayer.displayClientMessage(Component.literal("R  L  L  _  _").withStyle(ChatFormatting.BOLD), true);
                }
                if (firstKeyClicked == 2 && secondKeyClicked == 1 && thirdKeyClicked == 2 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                    pPlayer.displayClientMessage(Component.literal("R  L  R  _  _").withStyle(ChatFormatting.BOLD), true);
                }
                if (firstKeyClicked == 2 && secondKeyClicked == 2 && thirdKeyClicked == 1 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                    pPlayer.displayClientMessage(Component.literal("R  R  L  _  _").withStyle(ChatFormatting.BOLD), true);
                }
                if (firstKeyClicked == 2 && secondKeyClicked == 2 && thirdKeyClicked == 2 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                    pPlayer.displayClientMessage(Component.literal("R  R  R  _  _").withStyle(ChatFormatting.BOLD), true);
                }
                if (firstKeyClicked == 1 && secondKeyClicked == 1 && thirdKeyClicked == 1 && fourthKeyClicked == 1 && fifthKeyClicked == 0) {
                    pPlayer.displayClientMessage(Component.literal("L  L  L  L  _").withStyle(ChatFormatting.BOLD), true);
                }
                if (firstKeyClicked == 1 && secondKeyClicked == 1 && thirdKeyClicked == 1 && fourthKeyClicked == 2 && fifthKeyClicked == 0) {
                    pPlayer.displayClientMessage(Component.literal("L  L  L  R  _").withStyle(ChatFormatting.BOLD), true);
                }
                if (firstKeyClicked == 1 && secondKeyClicked == 1 && thirdKeyClicked == 2 && fourthKeyClicked == 1 && fifthKeyClicked == 0) {
                    pPlayer.displayClientMessage(Component.literal("L  L  R  L  _").withStyle(ChatFormatting.BOLD), true);
                }
                if (firstKeyClicked == 1 && secondKeyClicked == 1 && thirdKeyClicked == 2 && fourthKeyClicked == 2 && fifthKeyClicked == 0) {
                    pPlayer.displayClientMessage(Component.literal("L  L  R  R  _").withStyle(ChatFormatting.BOLD), true);
                }
                if (firstKeyClicked == 1 && secondKeyClicked == 2 && thirdKeyClicked == 1 && fourthKeyClicked == 1 && fifthKeyClicked == 0) {
                    pPlayer.displayClientMessage(Component.literal("L  R  L  L  _").withStyle(ChatFormatting.BOLD), true);
                }
                if (firstKeyClicked == 1 && secondKeyClicked == 2 && thirdKeyClicked == 1 && fourthKeyClicked == 2 && fifthKeyClicked == 0) {
                    pPlayer.displayClientMessage(Component.literal("L  R  L  R  _").withStyle(ChatFormatting.BOLD), true);
                }
                if (firstKeyClicked == 1 && secondKeyClicked == 2 && thirdKeyClicked == 2 && fourthKeyClicked == 1 && fifthKeyClicked == 0) {
                    pPlayer.displayClientMessage(Component.literal("L  R  R  L  _").withStyle(ChatFormatting.BOLD), true);
                }
                if (firstKeyClicked == 1 && secondKeyClicked == 2 && thirdKeyClicked == 2 && fourthKeyClicked == 2 && fifthKeyClicked == 0) {
                    pPlayer.displayClientMessage(Component.literal("L  R  R  R  _").withStyle(ChatFormatting.BOLD), true);
                }
                if (firstKeyClicked == 2 && secondKeyClicked == 1 && thirdKeyClicked == 1 && fourthKeyClicked == 1 && fifthKeyClicked == 0) {
                    pPlayer.displayClientMessage(Component.literal("R  L  L  L  _").withStyle(ChatFormatting.BOLD), true);
                }
                if (firstKeyClicked == 2 && secondKeyClicked == 1 && thirdKeyClicked == 1 && fourthKeyClicked == 2 && fifthKeyClicked == 0) {
                    pPlayer.displayClientMessage(Component.literal("R  L  L  R  _").withStyle(ChatFormatting.BOLD), true);
                }
                if (firstKeyClicked == 2 && secondKeyClicked == 1 && thirdKeyClicked == 2 && fourthKeyClicked == 1 && fifthKeyClicked == 0) {
                    pPlayer.displayClientMessage(Component.literal("R  L  R  L  _").withStyle(ChatFormatting.BOLD), true);
                }
                if (firstKeyClicked == 2 && secondKeyClicked == 1 && thirdKeyClicked == 2 && fourthKeyClicked == 2 && fifthKeyClicked == 0) {
                    pPlayer.displayClientMessage(Component.literal("R  L  R  R  _").withStyle(ChatFormatting.BOLD), true);
                }
                if (firstKeyClicked == 2 && secondKeyClicked == 2 && thirdKeyClicked == 1 && fourthKeyClicked == 1 && fifthKeyClicked == 0) {
                    pPlayer.displayClientMessage(Component.literal("R  R  L  L  _").withStyle(ChatFormatting.BOLD), true);
                }
                if (firstKeyClicked == 2 && secondKeyClicked == 2 && thirdKeyClicked == 1 && fourthKeyClicked == 2 && fifthKeyClicked == 0) {
                    pPlayer.displayClientMessage(Component.literal("R  R  L  R  _").withStyle(ChatFormatting.BOLD), true);
                }
                if (firstKeyClicked == 2 && secondKeyClicked == 2 && thirdKeyClicked == 2 && fourthKeyClicked == 1 && fifthKeyClicked == 0) {
                    pPlayer.displayClientMessage(Component.literal("R  R  R  L  _").withStyle(ChatFormatting.BOLD), true);
                }
                if (firstKeyClicked == 2 && secondKeyClicked == 2 && thirdKeyClicked == 2 && fourthKeyClicked == 2 && fifthKeyClicked == 0) {
                    pPlayer.displayClientMessage(Component.literal("R  R  R  R  _").withStyle(ChatFormatting.BOLD), true);
                }
                if (firstKeyClicked == 1 && secondKeyClicked == 1 && thirdKeyClicked == 1 && fourthKeyClicked == 1 && fifthKeyClicked == 1) {
                    pPlayer.displayClientMessage(Component.literal("L  L  L  L  L").withStyle(ChatFormatting.BOLD), true);
                    resetClicks(pPlayer);
                }
                if (firstKeyClicked == 1 && secondKeyClicked == 1 && thirdKeyClicked == 1 && fourthKeyClicked == 1 && fifthKeyClicked == 2) {
                    pPlayer.displayClientMessage(Component.literal("L  L  L  L  R").withStyle(ChatFormatting.BOLD), true);
                    resetClicks(pPlayer);
                }
                if (firstKeyClicked == 1 && secondKeyClicked == 1 && thirdKeyClicked == 1 && fourthKeyClicked == 2 && fifthKeyClicked == 1) {
                    pPlayer.displayClientMessage(Component.literal("L  L  L  R  L").withStyle(ChatFormatting.BOLD), true);
                    resetClicks(pPlayer);
                }
                if (firstKeyClicked == 1 && secondKeyClicked == 1 && thirdKeyClicked == 1 && fourthKeyClicked == 2 && fifthKeyClicked == 2) {
                    pPlayer.displayClientMessage(Component.literal("L  L  L  R  R").withStyle(ChatFormatting.BOLD), true);
                    resetClicks(pPlayer);
                }
                if (firstKeyClicked == 1 && secondKeyClicked == 1 && thirdKeyClicked == 2 && fourthKeyClicked == 1 && fifthKeyClicked == 1) {
                    pPlayer.displayClientMessage(Component.literal("L  L  R  L  L").withStyle(ChatFormatting.BOLD), true);
                    resetClicks(pPlayer);
                }
                if (firstKeyClicked == 1 && secondKeyClicked == 1 && thirdKeyClicked == 2 && fourthKeyClicked == 1 && fifthKeyClicked == 2) {
                    pPlayer.displayClientMessage(Component.literal("L  L  R  L  R").withStyle(ChatFormatting.BOLD), true);
                    resetClicks(pPlayer);
                }
                if (firstKeyClicked == 1 && secondKeyClicked == 1 && thirdKeyClicked == 2 && fourthKeyClicked == 2 && fifthKeyClicked == 1) {
                    pPlayer.displayClientMessage(Component.literal("L  L  R  R  L").withStyle(ChatFormatting.BOLD), true);
                    resetClicks(pPlayer);
                }
                if (firstKeyClicked == 1 && secondKeyClicked == 1 && thirdKeyClicked == 2 && fourthKeyClicked == 2 && fifthKeyClicked == 2) {
                    pPlayer.displayClientMessage(Component.literal("L  L  R  R  R").withStyle(ChatFormatting.BOLD), true);
                    resetClicks(pPlayer);
                }
                if (firstKeyClicked == 1 && secondKeyClicked == 2 && thirdKeyClicked == 1 && fourthKeyClicked == 1 && fifthKeyClicked == 1) {
                    pPlayer.displayClientMessage(Component.literal("L  R  L  L  L").withStyle(ChatFormatting.BOLD), true);
                    resetClicks(pPlayer);
                }
                if (firstKeyClicked == 1 && secondKeyClicked == 2 && thirdKeyClicked == 1 && fourthKeyClicked == 1 && fifthKeyClicked == 2) {
                    pPlayer.displayClientMessage(Component.literal("L  R  L  L  R").withStyle(ChatFormatting.BOLD), true);
                    resetClicks(pPlayer);
                }
                if (firstKeyClicked == 1 && secondKeyClicked == 2 && thirdKeyClicked == 1 && fourthKeyClicked == 2 && fifthKeyClicked == 1) {
                    pPlayer.displayClientMessage(Component.literal("L  R  L  R  L").withStyle(ChatFormatting.BOLD), true);
                    resetClicks(pPlayer);
                }
                if (firstKeyClicked == 1 && secondKeyClicked == 2 && thirdKeyClicked == 1 && fourthKeyClicked == 2 && fifthKeyClicked == 2) {
                    pPlayer.displayClientMessage(Component.literal("L  R  L  R  R").withStyle(ChatFormatting.BOLD), true);
                    resetClicks(pPlayer);
                }
                if (firstKeyClicked == 1 && secondKeyClicked == 2 && thirdKeyClicked == 2 && fourthKeyClicked == 1 && fifthKeyClicked == 1) {
                    pPlayer.displayClientMessage(Component.literal("L  R  R  L  L").withStyle(ChatFormatting.BOLD), true);
                    resetClicks(pPlayer);
                }
                if (firstKeyClicked == 1 && secondKeyClicked == 2 && thirdKeyClicked == 2 && fourthKeyClicked == 1 && fifthKeyClicked == 2) {
                    pPlayer.displayClientMessage(Component.literal("L  R  R  L  R").withStyle(ChatFormatting.BOLD), true);
                    resetClicks(pPlayer);
                }
                if (firstKeyClicked == 1 && secondKeyClicked == 2 && thirdKeyClicked == 2 && fourthKeyClicked == 2 && fifthKeyClicked == 1) {
                    pPlayer.displayClientMessage(Component.literal("L  R  R  R  L").withStyle(ChatFormatting.BOLD), true);
                    resetClicks(pPlayer);
                }
                if (firstKeyClicked == 1 && secondKeyClicked == 2 && thirdKeyClicked == 2 && fourthKeyClicked == 2 && fifthKeyClicked == 2) {
                    pPlayer.displayClientMessage(Component.literal("L  R  R  R  R").withStyle(ChatFormatting.BOLD), true);
                    resetClicks(pPlayer);
                }
                if (firstKeyClicked == 2 && secondKeyClicked == 1 && thirdKeyClicked == 1 && fourthKeyClicked == 1 && fifthKeyClicked == 1) {
                    pPlayer.displayClientMessage(Component.literal("R  L  L  L  L").withStyle(ChatFormatting.BOLD), true);
                    resetClicks(pPlayer);
                }
                if (firstKeyClicked == 2 && secondKeyClicked == 1 && thirdKeyClicked == 1 && fourthKeyClicked == 1 && fifthKeyClicked == 2) {
                    pPlayer.displayClientMessage(Component.literal("R  L  L  L  R").withStyle(ChatFormatting.BOLD), true);
                    resetClicks(pPlayer);
                }
                if (firstKeyClicked == 2 && secondKeyClicked == 1 && thirdKeyClicked == 1 && fourthKeyClicked == 2 && fifthKeyClicked == 1) {
                    pPlayer.displayClientMessage(Component.literal("R  L  L  R  L").withStyle(ChatFormatting.BOLD), true);
                    resetClicks(pPlayer);
                }
                if (firstKeyClicked == 2 && secondKeyClicked == 1 && thirdKeyClicked == 1 && fourthKeyClicked == 2 && fifthKeyClicked == 2) {
                    pPlayer.displayClientMessage(Component.literal("R  L  L  R  R").withStyle(ChatFormatting.BOLD), true);
                    resetClicks(pPlayer);
                }
                if (firstKeyClicked == 2 && secondKeyClicked == 1 && thirdKeyClicked == 2 && fourthKeyClicked == 1 && fifthKeyClicked == 1) {
                    pPlayer.displayClientMessage(Component.literal("R  L  R  L  L").withStyle(ChatFormatting.BOLD), true);
                    resetClicks(pPlayer);
                }
                if (firstKeyClicked == 2 && secondKeyClicked == 1 && thirdKeyClicked == 2 && fourthKeyClicked == 1 && fifthKeyClicked == 2) {
                    pPlayer.displayClientMessage(Component.literal("R  L  R  L  R").withStyle(ChatFormatting.BOLD), true);
                    resetClicks(pPlayer);
                }
                if (firstKeyClicked == 2 && secondKeyClicked == 1 && thirdKeyClicked == 2 && fourthKeyClicked == 2 && fifthKeyClicked == 1) {
                    pPlayer.displayClientMessage(Component.literal("R  L  R  R  L").withStyle(ChatFormatting.BOLD), true);
                    resetClicks(pPlayer);
                }
                if (firstKeyClicked == 2 && secondKeyClicked == 1 && thirdKeyClicked == 2 && fourthKeyClicked == 2 && fifthKeyClicked == 2) {
                    pPlayer.displayClientMessage(Component.literal("R  L  R  R  R").withStyle(ChatFormatting.BOLD), true);
                    resetClicks(pPlayer);
                }
                if (firstKeyClicked == 2 && secondKeyClicked == 2 && thirdKeyClicked == 1 && fourthKeyClicked == 1 && fifthKeyClicked == 1) {
                    pPlayer.displayClientMessage(Component.literal("R  R  L  L  L").withStyle(ChatFormatting.BOLD), true);
                    resetClicks(pPlayer);
                }
                if (firstKeyClicked == 2 && secondKeyClicked == 2 && thirdKeyClicked == 1 && fourthKeyClicked == 1 && fifthKeyClicked == 2) {
                    pPlayer.displayClientMessage(Component.literal("R  R  L  L  R").withStyle(ChatFormatting.BOLD), true);
                    resetClicks(pPlayer);
                }
                if (firstKeyClicked == 2 && secondKeyClicked == 2 && thirdKeyClicked == 1 && fourthKeyClicked == 2 && fifthKeyClicked == 1) {
                    pPlayer.displayClientMessage(Component.literal("R  R  L  R  L").withStyle(ChatFormatting.BOLD), true);
                    resetClicks(pPlayer);
                }
                if (firstKeyClicked == 2 && secondKeyClicked == 2 && thirdKeyClicked == 1 && fourthKeyClicked == 2 && fifthKeyClicked == 2) {
                    pPlayer.displayClientMessage(Component.literal("R  R  L  R  R").withStyle(ChatFormatting.BOLD), true);
                    resetClicks(pPlayer);
                }
                if (firstKeyClicked == 2 && secondKeyClicked == 2 && thirdKeyClicked == 2 && fourthKeyClicked == 1 && fifthKeyClicked == 1) {
                    pPlayer.displayClientMessage(Component.literal("R  R  R  L  L").withStyle(ChatFormatting.BOLD), true);
                    resetClicks(pPlayer);
                }
                if (firstKeyClicked == 2 && secondKeyClicked == 2 && thirdKeyClicked == 2 && fourthKeyClicked == 1 && fifthKeyClicked == 2) {
                    pPlayer.displayClientMessage(Component.literal("R  R  R  L  R").withStyle(ChatFormatting.BOLD), true);
                    resetClicks(pPlayer);
                }
                if (firstKeyClicked == 2 && secondKeyClicked == 2 && thirdKeyClicked == 2 && fourthKeyClicked == 2 && fifthKeyClicked == 1) {
                    pPlayer.displayClientMessage(Component.literal("R  R  R  R  L").withStyle(ChatFormatting.BOLD), true);
                    resetClicks(pPlayer);
                }
                if (firstKeyClicked == 2 && secondKeyClicked == 2 && thirdKeyClicked == 2 && fourthKeyClicked == 2 && fifthKeyClicked == 2) {
                    pPlayer.displayClientMessage(Component.literal("R  R  R  R  R").withStyle(ChatFormatting.BOLD), true);
                    resetClicks(pPlayer);
                }


            }
            if (keyClicked == 0) {
                pPlayer.getPersistentData().putInt("firstKeyClicked", 0);
                pPlayer.getPersistentData().putInt("secondKeyClicked", 0);
                pPlayer.getPersistentData().putInt("thirdKeyClicked", 0);
                pPlayer.getPersistentData().putInt("fourthKeyClicked", 0);
                pPlayer.getPersistentData().putInt("fifthKeyClicked", 0);
            }
        }
    }

    @SubscribeEvent
    public static void rightClick(PlayerInteractEvent.EntityInteract event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        int firstKeyClicked = pPlayer.getPersistentData().getInt("firstKeyClicked");
        int secondKeyClicked = pPlayer.getPersistentData().getInt("secondKeyClicked");
        int thirdKeyClicked = pPlayer.getPersistentData().getInt("thirdKeyClicked");
        int fourthKeyClicked = pPlayer.getPersistentData().getInt("fourthKeyClicked");
        int fifthKeyClicked = pPlayer.getPersistentData().getInt("fifthKeyClicked");

        if (!heldItem.isEmpty() && heldItem.getItem() instanceof BeyonderAbilityUser) {
            if (firstKeyClicked == 0 && secondKeyClicked == 0 && thirdKeyClicked == 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                pPlayer.getPersistentData().putInt("firstKeyClicked", 2);
                pPlayer.getPersistentData().putInt("keyHasBeenClicked", 40);
            }
            if (firstKeyClicked != 0 && secondKeyClicked == 0 && thirdKeyClicked == 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                pPlayer.getPersistentData().putInt("secondKeyClicked", 2);
                pPlayer.getPersistentData().putInt("keyHasBeenClicked", 40);
            }
            if (firstKeyClicked != 0 && secondKeyClicked != 0 && thirdKeyClicked == 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                pPlayer.getPersistentData().putInt("thirdKeyClicked", 2);
                pPlayer.getPersistentData().putInt("keyHasBeenClicked", 40);
            }
            if (firstKeyClicked != 0 && secondKeyClicked != 0 && thirdKeyClicked != 0 && fourthKeyClicked == 0 && fifthKeyClicked == 0) {
                pPlayer.getPersistentData().putInt("fourthKeyClicked", 2);
                pPlayer.getPersistentData().putInt("keyHasBeenClicked", 40);
            }
            if (firstKeyClicked != 0 && secondKeyClicked != 0 && thirdKeyClicked != 0 && fourthKeyClicked != 0 && fifthKeyClicked == 0) {
                pPlayer.getPersistentData().putInt("fifthKeyClicked", 2);
                pPlayer.getPersistentData().putInt("keyHasBeenClicked", 40);
            }
        }
    }
}
