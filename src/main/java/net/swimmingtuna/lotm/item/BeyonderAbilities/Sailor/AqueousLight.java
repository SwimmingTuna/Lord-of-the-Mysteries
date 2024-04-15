package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.AqueousLightEntity;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.xml.stream.Location;
import java.util.List;

import static net.minecraft.commands.arguments.EntityArgument.getPlayer;

public class AqueousLight extends Item {
    public AqueousLight(Properties pProperties) {
        super(pProperties);
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        if (!pPlayer.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            if (!holder.isSailorClass()) {
                pPlayer.displayClientMessage(Component.literal("You are not of the Spectator pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA), true);
            }
            if (holder.getSpirituality() < 75) {
                pPlayer.displayClientMessage(Component.literal("You need 75 spirituality in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA), true);
            }
        }        BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(tyrantSequence -> {
            if (tyrantSequence.getCurrentSequence() <= 7 && tyrantSequence.useSpirituality(75)) {
                shootLight(pPlayer);
            }
            if (!pPlayer.getAbilities().instabuild)
                pPlayer.getCooldowns().addCooldown(this, 10);
        });
        return super.use(level, pPlayer, hand);
    }

    public static void shootLight(Player pPlayer) {
        Vec3 eyePosition = pPlayer.getEyePosition(1.0f);
        Vec3 direction = pPlayer.getViewVector(1.0f);
        Vec3 initialVelocity = direction.scale(2.0);
        AqueousLightEntity.summonEntityWithSpeed(direction, initialVelocity, eyePosition, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), pPlayer);
    }
    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, shoots a dragons breath\n" +
                    "Spirituality Used: 100\n" +
                    "Cooldown: 0.5 seconds"));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }

    @SubscribeEvent
    public static void lightTickEvent(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        Level level = entity.level();
        CompoundTag compoundTag = entity.getPersistentData();
        if (!entity.level().isClientSide()) {
            int aqueousLight = compoundTag.getInt("lightDrowning");
            if (aqueousLight >= 1) {
                compoundTag.putInt("lightDrowning", aqueousLight + 1);
                BlockPos headPos = entity.getOnPos();
                level.setBlockAndUpdate(headPos, Blocks.WATER.defaultBlockState());
                List<Player> pPlayers = (List<Player>) level.players();
                for (Player pPlayer : pPlayers) {
                    pPlayer.sendSystemMessage(Component.literal("aqueousLight is" + aqueousLight));
                }
            }
            if (aqueousLight >= 400) {
                aqueousLight = 0;
                compoundTag.putInt("lightDrowning", 0);
            }
        }
    }
}
