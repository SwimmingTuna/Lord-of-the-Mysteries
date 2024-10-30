package net.swimmingtuna.lotm.item.BeyonderAbilities;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import org.apache.commons.lang3.StringUtils;

import static net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.AcidicRain.spawnAcidicRainParticles;
import static net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.RagingBlows.spawnRagingBlowsParticles;
import static net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.SirenSongWeaken.spawnParticlesInSphere;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BeyonderAbilityUser extends Item {

    public BeyonderAbilityUser(Properties properties) { //IMPORTANT!!!! FIGURE OUT HOW TO MAKE THIS WORK BY CLICKING ON A
        super(properties);
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

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        if (entity instanceof Player player) {

            //Acidic Rain
            double acidicRain = player.getAttributeBaseValue(ModAttributes.PARTICLE_HELPER.get());
            if (acidicRain >= 1) {
                spawnAcidicRainParticles(player);
            }


            //Siren Songs
            if (player.getAttribute(ModAttributes.PARTICLE_HELPER2.get()).getValue() == 1) {
                BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
                spawnParticlesInSphere(player, 50 - (holder.getCurrentSequence() * 6));
            }


            //Wind Manipulation Cushion
            double cushionParticles = player.getAttributeBaseValue(ModAttributes.PARTICLE_HELPER3.get());
            if (cushionParticles == 1) {
                double x = player.getX() - player.getLookAngle().x * 2;
                double y = player.getY() + 1.5; // Slightly above the player's feet
                double z = player.getZ() - player.getLookAngle().z * 2;

                // Add 10 wind particles behind the player
                for (int i = 0; i < 10; i++) {
                    level.addParticle(ParticleTypes.CLOUD,
                            x + (level.random.nextDouble() - 0.5),
                            y + (level.random.nextDouble() - 0.5),
                            z + (level.random.nextDouble() - 0.5),
                            0, 0, 0);
                }
            }

            //Raging Blows
            double ragingBlows = player.getAttributeBaseValue(ModAttributes.PARTICLE_HELPER1.get());
            if (ragingBlows >= 1) {
                spawnRagingBlowsParticles(player);
            }

            //Star of Lightning
            AttributeInstance attributeInstance = player.getAttribute(ModAttributes.PARTICLE_HELPER4.get());
            if (attributeInstance != null && attributeInstance.getValue() == 1) {
                for (int i = 0; i < 500; i++) {
                    double offsetX = (Math.random() * 5) - 2.5;
                    double offsetY = (Math.random() * 5) - 2.5;
                    double offsetZ = (Math.random() * 5) - 2.5;
                    if (Math.sqrt(offsetX * offsetX + offsetY * offsetY + offsetZ * offsetZ) <= 2.5) {
                        level.addParticle(ParticleTypes.ELECTRIC_SPARK,
                                player.getX() + offsetX,
                                player.getY() + offsetY,
                                player.getZ() + offsetZ,
                                0.0, 0.0, 0.0);
                    }
                }
            }
            if (!player.level().isClientSide()) {
                player.level().playSound(player, player.getOnPos(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 10,1);
            }

        }
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);
    }
    public static void resetClicks(Player player) {
        player.getPersistentData().putByteArray("keysClicked", new byte[5]);
        player.displayClientMessage(Component.empty(), true);
    }


    @SubscribeEvent
    public static void keyTimer(TickEvent.PlayerTickEvent event) {

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

        if (keysClicked[4] == 0) return;

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

        for (int i = 0; i < keysClicked.length; i++) {
            if (keysClicked[i] == 0) {
                keysClicked[i] = 2;
                BeyonderAbilityUser.clicked(player, hand);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
}
