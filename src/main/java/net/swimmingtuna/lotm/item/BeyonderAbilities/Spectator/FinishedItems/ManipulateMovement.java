package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.events.ReachChangeUUIDs;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ManipulateMovement extends Item implements ReachChangeUUIDs {
    private final LazyOptional<Multimap<Attribute, AttributeModifier>> lazyAttributeMap = LazyOptional.of(() -> createAttributeMap());


    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(@NotNull EquipmentSlot pSlot) {
        if (pSlot == EquipmentSlot.MAINHAND) {
            return lazyAttributeMap.orElseGet(() -> createAttributeMap());
        }
        return super.getDefaultAttributeModifiers(pSlot);
    }

    private Multimap<Attribute, AttributeModifier> createAttributeMap() {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> attributeBuilder = ImmutableMultimap.builder();
        attributeBuilder.putAll(super.getDefaultAttributeModifiers(EquipmentSlot.MAINHAND));
        attributeBuilder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(BeyonderEntityReach, "Reach modifier", 300, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with entities
        attributeBuilder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(BeyonderBlockReach, "Reach modifier", 300, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with blocks, p much useless for this item
        return attributeBuilder.build();
    }

    public ManipulateMovement(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Player pPlayer = pContext.getPlayer();
        BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
        int sequence = holder.getCurrentSequence();
        AttributeInstance dreamIntoReality = pPlayer.getAttribute(ModAttributes.DIR.get());
        if (!pPlayer.level().isClientSide()) {
            if (!holder.isSpectatorClass()) {
                pPlayer.displayClientMessage(Component.literal("You are not of the Spectator pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA), true);
            }
            if (holder.getSpirituality() < (int) 200 / dreamIntoReality.getValue()) {
                pPlayer.displayClientMessage(Component.literal("You need spirituality" + ((int) 200 / dreamIntoReality.getValue()) + " in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA), true);
            }
        }
        if (holder.isSpectatorClass() && sequence <= 4 && holder.useSpirituality(200)) {
            boolean x = pPlayer.getPersistentData().getBoolean("manipulateMovementBoolean");
            if (!x) {
                pPlayer.getPersistentData().putBoolean("manipulateMovementBoolean", true);
                BlockPos pos = pContext.getClickedPos();
                pPlayer.getPersistentData().putInt("manipulateMovementX", pos.getX());
                pPlayer.getPersistentData().putInt("manipulateMovementY", pos.getY());
                pPlayer.getPersistentData().putInt("manipulateMovementZ", pos.getZ());
                pPlayer.displayClientMessage(Component.literal("Manipulate Movement Position is " + pos.getX() + " " + pos.getY() + " " + pos.getZ()).withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA), true);

            }
            if (x) {
                pPlayer.getPersistentData().remove("manipulateMovementX");
                pPlayer.getPersistentData().remove("manipulateMovementY");
                pPlayer.getPersistentData().remove("manipulateMovementZ");
                pPlayer.getPersistentData().putBoolean("manipulateMovementBoolean", false);
                pPlayer.displayClientMessage(Component.literal("Manipulate Movement Position Reset").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA), true);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, all living entities 150 blocks around you move to the location you clicked on\n" +
                    "Left Click for Apply Manipulation\n" +
                    "Spirituality Used: 200\n" +
                    "Cooldown: 30 seconds"));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }

    @SubscribeEvent
    public static void tickEvent(TickEvent.PlayerTickEvent event) {
        Player pPlayer = event.player;
        Level level = pPlayer.level();
        if (!level.isClientSide() && event.phase == TickEvent.Phase.END) {
            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(250))) {
                if (entity != pPlayer && entity.hasEffect(ModEffects.MANIPULATION.get()) && pPlayer.getPersistentData().getBoolean("manipulateMovementBoolean")) {
                    int targetX = pPlayer.getPersistentData().getInt("manipulateMovementX");
                    int targetY = pPlayer.getPersistentData().getInt("manipulateMovementY");
                    int targetZ = pPlayer.getPersistentData().getInt("manipulateMovementZ");

                    if (entity.distanceToSqr(targetX, targetY, targetZ) <= 10) {
                        entity.removeEffect(ModEffects.MANIPULATION.get());
                        continue;
                    }

                    if (entity instanceof Player) {
                        // Existing logic for players
                        double entityX = entity.getX();
                        double entityY = entity.getY();
                        double entityZ = entity.getZ();

                        double dx = targetX - entityX;
                        double dy = targetY - entityY;
                        double dz = targetZ - entityZ;

                        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                        if (distance > 0) {
                            dx /= distance;
                            dy /= distance;
                            dz /= distance;
                        }

                        double speed = 3.0 / 20;

                        BlockPos frontBlockPos = new BlockPos((int) (entityX + dx), (int) (entityY + dy), (int) (entityZ + dz));
                        BlockPos frontBlockPos1 = new BlockPos((int) (entityX + dx * 2), (int) (entityY + dy * 2), (int) (entityZ + dz * 2));
                        boolean pathIsClear = level.getBlockState(frontBlockPos).isAir() && level.getBlockState(frontBlockPos1).isAir();

                        if (pathIsClear) {
                            entity.setDeltaMovement(dx * speed, Math.min(0, dy * speed), dz * speed);
                        } else {
                            entity.setDeltaMovement(dx * speed, 0.25, dz * speed);
                        }
                    } else if (entity instanceof Mob mob) {
                        mob.getNavigation().moveTo(targetX, targetY, targetZ, 1.7);
                    }
                }
            }
        }
    }
}