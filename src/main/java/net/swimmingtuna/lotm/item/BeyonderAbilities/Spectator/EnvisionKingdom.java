package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.beyonder.api.BeyonderClass;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.commands.BeyonderClassArgument;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems.ManipulateFondness;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EnvisionKingdom extends Item {

    public EnvisionKingdom(Properties pProperties) {
        super(pProperties);
    }

    ResourceLocation CATHEDRAL = new ResourceLocation(LOTM.MOD_ID, "data/structures/teststructure.nbt");

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        AttributeInstance dreamIntoReality = pPlayer.getAttribute(ModAttributes.DIR.get());
        if (!pPlayer.level().isClientSide()) {
            if (!pPlayer.level().isClientSide()) {
                BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
                if (!holder.isSpectatorClass()) {
                    pPlayer.displayClientMessage(Component.literal("You are not of the Spectator pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA), true);
                }
                if (holder.getSpirituality() < (int) 3500/dreamIntoReality.getValue()) {
                    pPlayer.displayClientMessage(Component.literal("You need " + ((int) 3500/dreamIntoReality.getValue()) + " spirituality in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA), true);
                }
            }
            ServerLevel serverLevel = (ServerLevel) pPlayer.level();
            BlockPos playerPos = pPlayer.getOnPos();
            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
            if (spectatorSequence.getCurrentSequence() <= 0 && spectatorSequence.useSpirituality((int) (3500 / dreamIntoReality.getValue()))) {
                generateCathedral(pPlayer, playerPos, serverLevel);
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
        StructurePlaceSettings settings = new StructurePlaceSettings();
        settings.setRotation(Rotation.NONE);
        settings.setMirror(Mirror.NONE);
        settings.setRotationPivot(pos);

        return settings;
    }
    private void generateCathedral(Player pPlayer, BlockPos playerPos, ServerLevel serverLevel) {
        if (!pPlayer.level().isClientSide()) {
            RandomSource random = serverLevel.getRandom();
            StructurePlaceSettings settings = getStructurePlaceSettings(playerPos);
            StructureTemplate template = serverLevel.getStructureManager().getOrCreate(CATHEDRAL);
            template.placeInWorld((ServerLevel) pPlayer.level(), playerPos, playerPos, new StructurePlaceSettings(), random, Block.UPDATE_ALL);
            pPlayer.sendSystemMessage(Component.literal("pos is" + playerPos));
            if (template != null) {
                pPlayer.sendSystemMessage(Component.literal("cathedral isnt null"));
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