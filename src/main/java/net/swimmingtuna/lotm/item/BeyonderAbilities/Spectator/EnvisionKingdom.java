package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EnvisionKingdom extends Item {

    public EnvisionKingdom(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        AttributeInstance dreamIntoReality = pPlayer.getAttribute(ModAttributes.DIR.get());
            if (!pPlayer.level().isClientSide()) {
                BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
                if (!holder.isSpectatorClass()) {
                    pPlayer.displayClientMessage(Component.literal("You are not of the Spectator pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA), true);
                }
                if (holder.getSpirituality() < (int) 3500 / dreamIntoReality.getValue()) {
                    pPlayer.displayClientMessage(Component.literal("You need " + ((int) 3500 / dreamIntoReality.getValue()) + " spirituality in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA), true);
                }

                BlockPos playerPos = pPlayer.getOnPos();
                BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
                    if (holder.isSpectatorClass() && spectatorSequence.getCurrentSequence() <= 0 && spectatorSequence.useSpirituality((int) (3500 / dreamIntoReality.getValue()))) {
                        generateCathedral(pPlayer, playerPos, level);
                        if (!pPlayer.getAbilities().instabuild) {
                            pPlayer.getCooldowns().addCooldown(this, 900);
                        }
                    }
                });
            }
        return super.use(level, pPlayer, hand);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, summons your Divine Kingdom, the Corpse Cathedral\n" +
                    "Spirituality Used: 3500\n" +
                    "Cooldown: 45 seconds "));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }

    private StructurePlaceSettings getStructurePlaceSettings(BlockPos pos) {
        BoundingBox boundingBox = new BoundingBox(
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                pos.getX() + 160,
                pos.getY() + 97,
                pos.getZ() + 265
        );
        StructurePlaceSettings settings = new StructurePlaceSettings();
        settings.setRotation(Rotation.NONE);
        settings.setMirror(Mirror.NONE);
        settings.setRotationPivot(pos);
        settings.setBoundingBox(boundingBox);
        return settings;
    }

    private void generateCathedral(Player pPlayer, BlockPos playerPos, Level level) {
        if (!pPlayer.level().isClientSide) {
            double x = pPlayer.getX();
            double y = pPlayer.getY();
            double z = pPlayer.getZ();
            pPlayer.teleportTo(x + 79, y + 10, z + 206);
            for (LivingEntity entity : pPlayer.level().getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(250))) {
                if (entity != pPlayer) {
                    entity.teleportTo(x + 69 + (Math.random() * 10), y + 10, z + 196);
                }
            }
            StructurePlaceSettings settings = getStructurePlaceSettings(playerPos);
            ServerLevel serverLevel = (ServerLevel) level;
            StructureTemplate template = serverLevel.getStructureManager().getOrCreate(new ResourceLocation(LOTM.MOD_ID, "corpse_cathedral"));
            if (template != null) {
                template.placeInWorld(serverLevel, playerPos, playerPos, settings, null, 3);
            }
            CompoundTag compoundTag = pPlayer.getPersistentData();
            compoundTag.putInt("inMindscape", 1);
        }
    }

    @SubscribeEvent
    public static void mindscapeCounter(TickEvent.PlayerTickEvent event) {
        Player pPlayer = event.player;
        if(!pPlayer.level().isClientSide() && event.phase == TickEvent.Phase.START) {
            CompoundTag compoundTag = pPlayer.getPersistentData();
            int mindscape = compoundTag.getInt("inMindscape");
            if (mindscape >= 1) {
                compoundTag.putInt("inMindscape", mindscape + 1);
                pPlayer.sendSystemMessage(Component.literal("working" + mindscape));
            }
            if (mindscape >= 600) {
                compoundTag.putInt("inMindscape", 0);
            }
        }
    }
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        int activeSlot = pPlayer.getInventory().selected;
        if (!pPlayer.level().isClientSide && !heldItem.isEmpty() && heldItem.getItem() instanceof EnvisionKingdom) {
            pPlayer.getInventory().setItem(activeSlot, new ItemStack(ItemInit.EnvisionLocation.get()));
            heldItem.shrink(1);
            event.setCanceled(true);
        }
    }
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        Player pPlayer = event.getEntity();
        ItemStack heldItem = pPlayer.getMainHandItem();
        int activeSlot = pPlayer.getInventory().selected;
        if (!pPlayer.level().isClientSide && !heldItem.isEmpty() && heldItem.getItem() instanceof EnvisionKingdom) {
            pPlayer.getInventory().setItem(activeSlot, new ItemStack(ItemInit.EnvisionLocation.get()));
            heldItem.shrink(1);
            event.setCanceled(true);
        }
    }
}