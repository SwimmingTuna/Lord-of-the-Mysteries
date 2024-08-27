package net.swimmingtuna.lotm.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems.Awe;
import net.swimmingtuna.lotm.util.BeyonderUtil;

public class TestItem extends Item {
    public TestItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        useAbilities(pPlayer);
        return super.use(level, pPlayer, hand);
    }
    public static void useAbilities(Player pPlayer) {

    }
}
