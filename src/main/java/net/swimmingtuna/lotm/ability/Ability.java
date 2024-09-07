package net.swimmingtuna.lotm.ability;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.REQUEST_FILES.BeyonderUtil;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;

public abstract class Ability extends Item{
    private final String requiredClass;
    private final int requiredSequence;
    private final int requiredSpirituality;
    private final boolean useOn;

    public Ability(Properties pProperties, String requiredClass, int requiredSequence, int requiredSpirituality, boolean useOn) {
        super(pProperties);
        this.requiredClass = requiredClass;
        this.requiredSequence = requiredSequence;
        this.requiredSpirituality = requiredSpirituality;
        this.useOn = useOn;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        if (!pPlayer.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            if (holder != null) {
                Style style = BeyonderUtil.getStyle(pPlayer);
                if (holder == null) {
                    return InteractionResultHolder.fail(pPlayer.getItemInHand(hand));
                }
                checkPathway(pPlayer,hand);

                if (!checkSequence(holder)) {
                    pPlayer.displayClientMessage(Component.literal("You need to be sequence " + requiredSequence + " or lower to use this").withStyle(style), true);
                    return InteractionResultHolder.fail(pPlayer.getItemInHand(hand));
                }

                if (!checkSpirituality(holder)) {
                    pPlayer.displayClientMessage(Component.literal("You need " + requiredSpirituality + " spirituality to use this").withStyle(style), true);
                    return InteractionResultHolder.fail(pPlayer.getItemInHand(hand));
                }

                if (holder.useSpirituality(requiredSpirituality)) {
                    return useAbility(level, pPlayer, hand);
                }
            }
        }
        return InteractionResultHolder.pass(pPlayer.getItemInHand(hand));
    }

    protected abstract InteractionResultHolder<ItemStack> useAbility(Level level, Player pPlayer, InteractionHand hand);


    private boolean checkSequence(BeyonderHolder holder) {
        return holder.getCurrentSequence() <= requiredSequence;
    }

    public InteractionResultHolder<ItemStack> checkPathway(Player pPlayer, InteractionHand hand) {
        BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);

        if (holder == null) {
            return InteractionResultHolder.fail(pPlayer.getItemInHand(hand));
        }

        String lowerRequiredClass = requiredClass.toLowerCase();

        if (lowerRequiredClass.equals("spectator")) {
            if (holder.isSpectatorClass()) {
                return InteractionResultHolder.pass(pPlayer.getItemInHand(hand));
            } else {
                pPlayer.displayClientMessage(Component.literal("You are not of the Spectator Pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA), true);
                return InteractionResultHolder.fail(pPlayer.getItemInHand(hand));
            }
        } else if (lowerRequiredClass.equals("warrior")) {
            if (holder.isWarriorClass()) {
                return InteractionResultHolder.pass(pPlayer.getItemInHand(hand));
            } else {
                pPlayer.displayClientMessage(Component.literal("You are not of the Warrior Pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.DARK_RED), true);
                return InteractionResultHolder.fail(pPlayer.getItemInHand(hand));
            }
        } else if (lowerRequiredClass.equals("secretssupplicant")) {
            if (holder.isSecretsSupplicantClass()) {
                return InteractionResultHolder.pass(pPlayer.getItemInHand(hand));
            } else {
                pPlayer.displayClientMessage(Component.literal("You are not of the Secrets Supplicant Pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.LIGHT_PURPLE), true);
                return InteractionResultHolder.fail(pPlayer.getItemInHand(hand));
            }
        } else if (lowerRequiredClass.equals("sleepless")) {
            if (holder.isSleeplessClass()) {
                return InteractionResultHolder.pass(pPlayer.getItemInHand(hand));
            } else {
                pPlayer.displayClientMessage(Component.literal("You are not of the Sleepless Pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.DARK_BLUE), true);
                return InteractionResultHolder.fail(pPlayer.getItemInHand(hand));
            }
        } else if (lowerRequiredClass.equals("seer")) {
            if (holder.isSeerClass()) {
                return InteractionResultHolder.pass(pPlayer.getItemInHand(hand));
            } else {
                pPlayer.displayClientMessage(Component.literal("You are not of the Seer Pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.WHITE), true);
                return InteractionResultHolder.fail(pPlayer.getItemInHand(hand));
            }
        } else if (lowerRequiredClass.equals("reader")) {
            if (holder.isReaderClass()) {
                return InteractionResultHolder.pass(pPlayer.getItemInHand(hand));
            } else {
                pPlayer.displayClientMessage(Component.literal("You are not of the Reader Pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GREEN), true);
                return InteractionResultHolder.fail(pPlayer.getItemInHand(hand));
            }
        } else if (lowerRequiredClass.equals("planter")) {
            if (holder.isPlanterClass()) {
                return InteractionResultHolder.pass(pPlayer.getItemInHand(hand));
            } else {
                pPlayer.displayClientMessage(Component.literal("You are not of the Planter Pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GREEN), true);
                return InteractionResultHolder.fail(pPlayer.getItemInHand(hand));
            }
        } else if (lowerRequiredClass.equals("prisoner")) {
            if (holder.isPrisonerClass()) {
                return InteractionResultHolder.pass(pPlayer.getItemInHand(hand));
            } else {
                pPlayer.displayClientMessage(Component.literal("You are not of the Prisoner Pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.DARK_GRAY), true);
                return InteractionResultHolder.fail(pPlayer.getItemInHand(hand));
            }
        } else if (lowerRequiredClass.equals("mysterypryer")) {
            if (holder.isMysteryPryerClass()) {
                return InteractionResultHolder.pass(pPlayer.getItemInHand(hand));
            } else {
                pPlayer.displayClientMessage(Component.literal("You are not of the Mystery Pryer Pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.DARK_PURPLE), true);
                return InteractionResultHolder.fail(pPlayer.getItemInHand(hand));
            }
        } else if (lowerRequiredClass.equals("marauder")) {
            if (holder.isMarauderClass()) {
                return InteractionResultHolder.pass(pPlayer.getItemInHand(hand));
            } else {
                pPlayer.displayClientMessage(Component.literal("You are not of the Marauder Pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.DARK_PURPLE), true);
                return InteractionResultHolder.fail(pPlayer.getItemInHand(hand));
            }
        } else if (lowerRequiredClass.equals("monster")) {
            if (holder.isMonsterClass()) {
                return InteractionResultHolder.pass(pPlayer.getItemInHand(hand));
            } else {
                pPlayer.displayClientMessage(Component.literal("You are not of the Monster Pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.WHITE), true);
                return InteractionResultHolder.fail(pPlayer.getItemInHand(hand));
            }
        } else if (lowerRequiredClass.equals("lawyer")) {
            if (holder.isLawyerClass()) {
                return InteractionResultHolder.pass(pPlayer.getItemInHand(hand));
            } else {
                pPlayer.displayClientMessage(Component.literal("You are not of the Lawyer Pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.DARK_BLUE), true);
                return InteractionResultHolder.fail(pPlayer.getItemInHand(hand));
            }
        } else if (lowerRequiredClass.equals("hunter")) {
            if (holder.isHunterClass()) {
                return InteractionResultHolder.pass(pPlayer.getItemInHand(hand));
            } else {
                pPlayer.displayClientMessage(Component.literal("You are not of the Hunter Pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.DARK_RED), true);
                return InteractionResultHolder.fail(pPlayer.getItemInHand(hand));
            }
        } else if (lowerRequiredClass.equals("corpsecollector")) {
            if (holder.isCorpseCollectorClass()) {
                return InteractionResultHolder.pass(pPlayer.getItemInHand(hand));
            } else {
                pPlayer.displayClientMessage(Component.literal("You are not of the Corpse Collector Pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.DARK_BLUE), true);
                return InteractionResultHolder.fail(pPlayer.getItemInHand(hand));
            }
        } else if (lowerRequiredClass.equals("criminal")) {
            if (holder.isCriminalClass()) {
                return InteractionResultHolder.pass(pPlayer.getItemInHand(hand));
            } else {
                pPlayer.displayClientMessage(Component.literal("You are not of the Criminal Pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GRAY), true);
                return InteractionResultHolder.fail(pPlayer.getItemInHand(hand));
            }
        } else if (lowerRequiredClass.equals("bard")) {
            if (holder.isBardClass()) {
                return InteractionResultHolder.pass(pPlayer.getItemInHand(hand));
            } else {
                pPlayer.displayClientMessage(Component.literal("You are not of the Bard Pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.YELLOW), true);
                return InteractionResultHolder.fail(pPlayer.getItemInHand(hand));
            }
        } else if (lowerRequiredClass.equals("apothecary")) {
            if (holder.isApothecaryClass()) {
                return InteractionResultHolder.pass(pPlayer.getItemInHand(hand));
            } else {
                pPlayer.displayClientMessage(Component.literal("You are not of the Apothecary Pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.DARK_GREEN), true);
                return InteractionResultHolder.fail(pPlayer.getItemInHand(hand));
            }
        } else if (lowerRequiredClass.equals("apprentice")) {
            if (holder.isApprenticeClass()) {
                return InteractionResultHolder.pass(pPlayer.getItemInHand(hand));
            } else {
                pPlayer.displayClientMessage(Component.literal("You are not of the Apprentice Pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
                return InteractionResultHolder.fail(pPlayer.getItemInHand(hand));
            }
        } else if (lowerRequiredClass.equals("arbiter")) {
            if (holder.isArbiterClass()) {
                return InteractionResultHolder.pass(pPlayer.getItemInHand(hand));
            } else {
                pPlayer.displayClientMessage(Component.literal("You are not of the Arbiter Pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GOLD), true);
                return InteractionResultHolder.fail(pPlayer.getItemInHand(hand));
            }
        } else if (lowerRequiredClass.equals("assassin")) {
            if (holder.isAssassinClass()) {
                return InteractionResultHolder.pass(pPlayer.getItemInHand(hand));
            } else {
                pPlayer.displayClientMessage(Component.literal("You are not of the Assassin Pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.RED), true);
                return InteractionResultHolder.fail(pPlayer.getItemInHand(hand));
            }
        } else if (lowerRequiredClass.equals("savant")) {
            if (holder.isSavantClass()) {
                return InteractionResultHolder.pass(pPlayer.getItemInHand(hand));
            } else {
                pPlayer.displayClientMessage(Component.literal("You are not of the Savant Pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GOLD), true);
                return InteractionResultHolder.fail(pPlayer.getItemInHand(hand));
            }
        } else if (lowerRequiredClass.equals("sailor")) {
            if (holder.isSailorClass()) {
                return InteractionResultHolder.pass(pPlayer.getItemInHand(hand));
            } else {
                pPlayer.displayClientMessage(Component.literal("You are not of the Sailor Pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
                return InteractionResultHolder.fail(pPlayer.getItemInHand(hand));
            }
        }
        return InteractionResultHolder.fail(pPlayer.getItemInHand(hand));
    }


    private boolean checkSpirituality(BeyonderHolder holder) {
        return holder.getSpirituality() >= requiredSpirituality;
    }
}
