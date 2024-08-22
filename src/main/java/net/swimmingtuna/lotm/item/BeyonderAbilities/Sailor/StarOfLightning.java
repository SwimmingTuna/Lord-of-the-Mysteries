package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.LightningEntity;
import net.swimmingtuna.lotm.entity.RoarEntity;
import net.swimmingtuna.lotm.events.ReachChangeUUIDs;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.spirituality.ModAttributes;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class StarOfLightning extends Item implements ReachChangeUUIDs {

    public StarOfLightning(Properties pProperties) { //IMPORTANT!!!! FIGURE OUT HOW TO MAKE THIS WORK BY CLICKING ON A
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        if (!pPlayer.level().isClientSide()) {

            // If no block or entity is targeted, proceed with the original functionality
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            if (!holder.isSailorClass()) {
                pPlayer.displayClientMessage(Component.literal("You are not of the Sailor pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
            }
            if (holder.getSpirituality() < 300) {
                pPlayer.displayClientMessage(Component.literal("You need 300 spirituality in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
            }
            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(sailorSequence -> {
                if (holder.isSailorClass() && sailorSequence.getCurrentSequence() <= 4 && sailorSequence.useSpirituality(300)) {
                    summonLightning(pPlayer);
                    if (!pPlayer.getAbilities().instabuild)
                        pPlayer.getCooldowns().addCooldown(this, 240);
                }
            });
        }
        return super.use(level, pPlayer, hand);
    }

    private static void summonLightning(Player pPlayer) {
        if (!pPlayer.level().isClientSide()) {
            pPlayer.getPersistentData().putInt("sailorLightningStar", 40);
        }
    }

    @SubscribeEvent
    public static void lightningThing(TickEvent.PlayerTickEvent event) {
        Player pPlayer = event.player;
        if (!pPlayer.level().isClientSide() && event.phase == TickEvent.Phase.END) {
            CompoundTag tag = pPlayer.getPersistentData();
            AttributeInstance attributeInstance = pPlayer.getAttribute(ModAttributes.PARTICLE_HELPER4.get());
            int x = tag.getInt("sailorLightningStar");
            double y = (Math.random() * 2 - 1);
            if (x >= 2) {
                attributeInstance.setBaseValue(1.0f);
                tag.putInt("sailorLightningStar", x - 1);
            }
            if (x == 1) {
                tag.putInt("sailorLightningStar", 0);
                attributeInstance.setBaseValue(0);
                for (int i = 0; i < 500; i++) {
                    LightningEntity lightningEntity = new LightningEntity(EntityInit.LIGHTNING_ENTITY.get(), pPlayer.level());
                    lightningEntity.setSpeed(50);
                    double x1 = (Math.random() * 2 - 1);
                    double y1 = (Math.random() * 2 - 1); // You might want different random values for y and z
                    double z1 = (Math.random() * 2 - 1);
                    lightningEntity.setDeltaMovement(x1, y1, z1);
                    lightningEntity.setMaxLength(10);
                    lightningEntity.setOwner(pPlayer);
                    lightningEntity.teleportTo(pPlayer.getX(), pPlayer.getY(), pPlayer.getZ());
                    pPlayer.level().addFreshEntity(lightningEntity);
                }
            }
        }
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        if (entity instanceof Player pPlayer) {
            AttributeInstance attributeInstance = pPlayer.getAttribute(ModAttributes.PARTICLE_HELPER4.get());
            if (attributeInstance != null && attributeInstance.getValue() == 1) {
                for (int i = 0; i < 500; i++) {
                    double offsetX = (Math.random() * 5) - 2.5;
                    double offsetY = (Math.random() * 5) - 2.5;
                    double offsetZ = (Math.random() * 5) - 2.5;
                    if (Math.sqrt(offsetX * offsetX + offsetY * offsetY + offsetZ * offsetZ) <= 2.5) {
                        level.addParticle(ParticleTypes.ELECTRIC_SPARK,
                                pPlayer.getX() + offsetX,
                                pPlayer.getY() + offsetY,
                                pPlayer.getZ() + offsetZ,
                                0.0, 0.0, 0.0);
                    }
                }
            }
            if (!pPlayer.level().isClientSide()) {
                pPlayer.level().playSound(pPlayer, pPlayer.getOnPos(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 10,1);
            }
        }
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);
    }

}
