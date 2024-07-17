package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SirenSongWeaken extends Item {
    public SirenSongWeaken(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        if (!level.isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            if (holder != null) {
                if (!holder.isSailorClass()) {
                    pPlayer.displayClientMessage(Component.literal("You are not of the Sailor pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
                } else if (holder.getSpirituality() < 50) {
                    pPlayer.displayClientMessage(Component.literal("You need 50 spirituality in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
                } else if (holder.isSailorClass() && holder.getCurrentSequence() <= 5 && holder.useSpirituality(150)) {
                    shootAcidicRain(pPlayer, level);
                    if (!pPlayer.getAbilities().instabuild) {
                        pPlayer.getCooldowns().addCooldown(this, 40);
                    }
                }
            }
        }
        return super.use(level, pPlayer, hand);
    }

    private static void shootAcidicRain(Player pPlayer, Level level) {
        CompoundTag tag = pPlayer.getPersistentData();
        if (tag.getInt("sirenSongWeaken") == 0) {
            tag.putInt("sirenSongWeaken", 400);
            tag.putInt("ssParticleAttributeHelper", 400);
        }
        if (tag.getInt("sirenSongWeaken") > 1 && tag.getInt("sirenSongWeaken") < 400) {
            tag.putInt("sirenSongWeaken", 0);
            tag.putInt("ssParticleAttributeHelper", 1);
        }
        if (tag.getInt("sirenSongHarm") > 1) {
            tag.putInt("sirenSongHarm", 0);
            tag.putInt("sirenSongWeaken", 400);
            tag.putInt("ssParticleAttributeHelper", 400);

        }
        if (tag.getInt("sirenSongStun") > 1) {
            tag.putInt("sirenSongStun", 0);
            tag.putInt("sirenSongWeaken", 400);
            tag.putInt("ssParticleAttributeHelper", 400);
        }
        if (tag.getInt("sirenSongStrengthen") > 1) {
            tag.putInt("sirenSongStrengthen", 0);
            tag.putInt("sirenSongWeaken", 400);
            tag.putInt("ssParticleAttributeHelper", 400);
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, summons an acid rain effect around the player\n" +
                    "Spirituality Used: 50\n" +
                    "Cooldown: 2 seconds"));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }
}