package net.swimmingtuna.lotm.item.BeyonderAbilities;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.LineEntity;
import net.swimmingtuna.lotm.init.EntityInit;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TestItem extends Item {
    public TestItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        if (!level.isClientSide()) {
            shootLine(pPlayer, level);
        }
        return super.use(level, pPlayer, hand);
    }

    private static void shootLine(Player pPlayer, Level level) {
        if (!level.isClientSide()) {
            Vec3 lookVec = pPlayer.getLookAngle();
            float speed = 20.0f; // Set the speed as desired

            LineEntity lineEntity = new LineEntity(EntityInit.LINE_ENTITY.get(), level);
            lineEntity.setSpeed(speed);
            lineEntity.setDeltaMovement(lookVec.x, lookVec.y, lookVec.z);
            lineEntity.setMaxLength(30);
            lineEntity.teleportTo(pPlayer.getX(), pPlayer.getY(), pPlayer.getZ());
            level.addFreshEntity(lineEntity);
        }
    }
}