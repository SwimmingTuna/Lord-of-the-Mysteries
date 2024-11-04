package net.swimmingtuna.lotm.item.BeyonderAbilities;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import org.apache.commons.lang3.StringUtils;

public class BeyonderAbilityUser extends SimpleAbilityItem {


    public BeyonderAbilityUser(Properties properties) {
        super(properties, BeyonderClassInit.SPECTATOR, 9, 0, 0);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!player.level().isClientSide()) {
            ItemStack heldItem = player.getItemInHand(hand);
            byte[] keysClicked = player.getPersistentData().getByteArray("keysClicked");
            for (int i = 0; i < keysClicked.length; i++) {
                if (keysClicked[i] == 0) {
                    keysClicked[i] = 2;
                    BeyonderAbilityUser.clicked(player, hand);
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


    public static void clicked(Player player, InteractionHand hand) {

        if (player.level().isClientSide()) {
            return;
        }
        byte[] keysClicked = player.getPersistentData().getByteArray("keysClicked");

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

        if (keysClicked[4] == 0) {
            return;
        }

        int abilityNumber = 0;
        for (int i = 0; i < keysClicked.length; i++) {
            abilityNumber |= (keysClicked[i] - 1) << (4 - i);
        }
        ++abilityNumber;
        resetClicks(player);
        BeyonderUtil.useAbilityByNumber(player, abilityNumber, hand);

    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand hand) {
        byte[] keysClicked = player.getPersistentData().getByteArray("keysClicked");
        if (!player.level().isClientSide()) {
            if (!player.getCooldowns().isOnCooldown(this)) {

                player.getCooldowns().addCooldown(this, 4);
                player.sendSystemMessage(Component.literal("success"));
                for (int i = 0; i < keysClicked.length; i++) {
                    if (keysClicked[i] == 0) {
                        keysClicked[i] = 2;
                        BeyonderAbilityUser.clicked(player, hand);
                        return InteractionResult.SUCCESS;
                    }
                }
            } else {
                return InteractionResult.FAIL;
            }
        }
        return InteractionResult.PASS;
    }

}
