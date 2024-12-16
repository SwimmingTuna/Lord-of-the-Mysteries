package net.swimmingtuna.lotm.item.SealedArtifacts;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.entity.LuckBottleEntity;
import net.swimmingtuna.lotm.item.OtherItems.LuckBottleItem;

public class DeathKnell extends Item {
    public DeathKnell(Properties pProperties) {
        super(pProperties.stacksTo(1));
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack stack = pPlayer.getItemInHand(pHand);
        if (!pLevel.isClientSide() && pPlayer.getMainHandItem().getItem() instanceof DeathKnell) {
            if (pPlayer.getInventory().contains(Items.IRON_NUGGET.getDefaultInstance())) {
                pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F));

            }
        }

        return InteractionResultHolder.sidedSuccess(stack, pLevel.isClientSide());
    }
}
