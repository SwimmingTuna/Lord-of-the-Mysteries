package net.swimmingtuna.lotm.ability;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.REQUEST_FILES.BeyonderUtil;
import net.swimmingtuna.lotm.beyonder.api.BeyonderClass;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;

import java.util.Map;

public abstract class Ability extends Item {

    private static final Map<? extends BeyonderClass, String> CLASS_TO_NAME = Map.ofEntries(
            Map.entry(BeyonderClassInit.SPECTATOR.get(), "Spectator"),
            Map.entry(BeyonderClassInit.WARRIOR.get(), "Warrior"),
            Map.entry(BeyonderClassInit.SECRETSSUPPLICANT.get(), "Secrets Supplicant"),
            Map.entry(BeyonderClassInit.SLEEPLESS.get(), "Sleepless"),
            Map.entry(BeyonderClassInit.SEER.get(), "Seer"),
            Map.entry(BeyonderClassInit.READER.get(), "Reader"),
            Map.entry(BeyonderClassInit.PLANTER.get(), "Planter"),
            Map.entry(BeyonderClassInit.PRISONER.get(), "Prisoner"),
            Map.entry(BeyonderClassInit.MYSTERYPRYER.get(), "Mystery Pryer"),
            Map.entry(BeyonderClassInit.MARAUDER.get(), "Marauder"),
            Map.entry(BeyonderClassInit.MONSTER.get(), "Monster"),
            Map.entry(BeyonderClassInit.LAWYER.get(), "Lawyer"),
            Map.entry(BeyonderClassInit.HUNTER.get(), "Hunter"),
            Map.entry(BeyonderClassInit.CORPSECOLLECTOR.get(), "Corpse Collector"),
            Map.entry(BeyonderClassInit.CRIMINAL.get(), "Criminal"),
            Map.entry(BeyonderClassInit.BARD.get(), "Bard"),
            Map.entry(BeyonderClassInit.APOTHECARY.get(), "Apothecary"),
            Map.entry(BeyonderClassInit.APPRENTICE.get(), "Apprentice"),
            Map.entry(BeyonderClassInit.ARBITER.get(), "Arbiter"),
            Map.entry(BeyonderClassInit.ASSASSIN.get(), "Assassin"),
            Map.entry(BeyonderClassInit.SAVANT.get(), "Savant"),
            Map.entry(BeyonderClassInit.SAILOR.get(), "Sailor")
    );

    private final BeyonderClass requiredClass;
    private final int requiredSequence;
    private final int requiredSpirituality;
    private final boolean useOn;

    public Ability(Properties pProperties, BeyonderClass requiredClass, int requiredSequence, int requiredSpirituality, boolean useOn) {
        super(pProperties);
        this.requiredClass = requiredClass;
        this.requiredSequence = requiredSequence;
        this.requiredSpirituality = requiredSpirituality;
        this.useOn = useOn;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!player.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            if (holder == null) {
                return InteractionResultHolder.fail(player.getItemInHand(hand));
            }
            Style style = BeyonderUtil.getStyle(player);

            if (!checkRequiredClass(holder)) {
                String name = CLASS_TO_NAME.get(this.requiredClass);
                player.displayClientMessage(Component.literal("You are not of the " + name + " Pathway").withStyle(style), true);
                return InteractionResultHolder.fail(player.getItemInHand(hand));
            }

            if (!checkSequence(holder)) {
                player.displayClientMessage(Component.literal("You need to be sequence " + requiredSequence + " or lower to use this").withStyle(style), true);
                return InteractionResultHolder.fail(player.getItemInHand(hand));
            }

            if (!checkSpirituality(holder)) {
                player.displayClientMessage(Component.literal("You need " + requiredSpirituality + " spirituality to use this").withStyle(style), true);
                return InteractionResultHolder.fail(player.getItemInHand(hand));
            }

            if (holder.useSpirituality(requiredSpirituality)) {
                return useAbility(level, player, hand);
            }
        }
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    protected abstract InteractionResultHolder<ItemStack> useAbility(Level level, Player pPlayer, InteractionHand hand);

    private boolean checkSequence(BeyonderHolder holder) {
        return holder.getCurrentSequence() <= requiredSequence;
    }

    public boolean checkRequiredClass(BeyonderHolder holder) {
        return holder.currentClassMatches(this.requiredClass);
    }

    private boolean checkSpirituality(BeyonderHolder holder) {
        return holder.getSpirituality() >= requiredSpirituality;
    }
}
