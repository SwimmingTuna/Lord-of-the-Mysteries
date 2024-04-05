package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
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



    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        BlockPos playerPos = pPlayer.getOnPos();
        AttributeInstance dreamIntoReality = pPlayer.getAttribute(ModAttributes.DIR.get());
        BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
            if (spectatorSequence.getCurrentSequence() <= 0 && spectatorSequence.useSpirituality((int) (3500 / dreamIntoReality.getValue()))) {
                generateCathedral(pPlayer, level, playerPos);
                if (!pPlayer.getAbilities().instabuild)
                    pPlayer.getCooldowns().addCooldown(this, 900);
            }
        });
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

    ResourceLocation Cathedral = new ResourceLocation(LOTM.MOD_ID, "structures/corpse_cathedral.nbt");
    private void generateCathedral(Player pPlayer, Level level, BlockPos playerPos) {
        if (!pPlayer.level().isClientSide()) {
            ServerLevel serverLevel = (ServerLevel) level;
            RandomSource random = serverLevel.getRandom();
            StructurePlaceSettings settings = getStructurePlaceSettings(playerPos);
            StructureTemplate template = new StructureTemplate();


            template.placeInWorld(serverLevel, playerPos, BlockPos.ZERO, settings, random, 3);
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