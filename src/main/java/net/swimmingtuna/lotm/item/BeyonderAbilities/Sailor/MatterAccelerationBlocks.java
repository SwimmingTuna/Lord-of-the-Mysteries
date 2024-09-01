package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.EndStoneEntity;
import net.swimmingtuna.lotm.entity.NetherrackEntity;
import net.swimmingtuna.lotm.entity.StoneEntity;
import net.swimmingtuna.lotm.events.ReachChangeUUIDs;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.networking.LOTMNetworkHandler;
import net.swimmingtuna.lotm.networking.packet.MatterAccelerationBlockC2S;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MatterAccelerationBlocks extends Item implements ReachChangeUUIDs {

    public MatterAccelerationBlocks(Properties pProperties) { //IMPORTANT!!!! FIGURE OUT HOW TO MAKE THIS WORK BY CLICKING ON A
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
            if (holder.getSpirituality() < 1500) {
                pPlayer.displayClientMessage(Component.literal("You need 2000 spirituality in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
            }
            BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(sailorSequence -> {
                if (holder.isSailorClass() && sailorSequence.getCurrentSequence() <= 0 && sailorSequence.useSpirituality(2000)) {
                    useItem(pPlayer);
                    if (!pPlayer.getAbilities().instabuild)
                        pPlayer.getCooldowns().addCooldown(this, 300);
                }
            });
        }
        return super.use(level, pPlayer, hand);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, summons 10 blocks from around the player, making the next left clicks the player does shoot them towards the direction they look at with incredible speed\n" +
                    "Spirituality Used: 2000\n" +
                    "Cooldown: 15 seconds"));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }
    public static void useItem(Player pPlayer) {
        if (!pPlayer.level().isClientSide()) {
            pPlayer.getPersistentData().putInt("matterAccelerationBlockTimer", 480);
            Level level = pPlayer.level();
            BlockPos playerPos = pPlayer.blockPosition();
            BlockPos surfacePos = findSurfaceBelow(level, playerPos);

            if (surfacePos != null) {
                for (int i = 0; i < 10; i++) {
                    BlockPos posToRemove = surfacePos.below(i);
                    level.destroyBlock(posToRemove, false);
                    if (level.dimension() == Level.OVERWORLD) {
                        StoneEntity stoneEntity = new StoneEntity(EntityInit.STONE_ENTITY.get(), pPlayer.level());
                        float randomStayX;
                        do { randomStayX = (float) ((Math.random() * 6) - 3);} while (randomStayX > -0.5 && randomStayX < 0.5);
                        float randomStayY = (float) ((Math.random() * 6) - 3);
                        float randomStayZ = (float) ((Math.random() * 6) - 3);
                        int randomXRot = (int) ((Math.random() * 10) - 5);
                        int randomYRot = (int) ((Math.random() * 10) - 5);
                        stoneEntity.setStoneStayAtX(randomStayX);
                        stoneEntity.setStoneStayAtY(randomStayY);
                        stoneEntity.setStoneStayAtZ(randomStayZ);
                        stoneEntity.setOwner(pPlayer);
                        stoneEntity.setRemoveAndHurt(true);
                        stoneEntity.setSent(false);
                        stoneEntity.setPos(surfacePos.getX() + 0.5, surfacePos.getY() + 1, surfacePos.getZ() + 0.5);
                        stoneEntity.setShouldntDamage(true);


                        pPlayer.level().addFreshEntity(stoneEntity);
                    }
                    if (level.dimension() == Level.NETHER) {
                        NetherrackEntity stoneEntity = new NetherrackEntity(EntityInit.NETHERRACK_ENTITY.get(), pPlayer.level());
                        float randomStayX;
                        do { randomStayX = (float) ((Math.random() * 6) - 3);} while (randomStayX > -0.5 && randomStayX < 0.5);
                        float randomStayY = (float) ((Math.random() * 6) - 3);
                        float randomStayZ = (float) ((Math.random() * 6) - 3);
                        int randomXRot = (int) ((Math.random() * 10) - 5);
                        int randomYRot = (int) ((Math.random() * 10) - 5);
                        stoneEntity.setNetherrackStayAtX(randomStayX);
                        stoneEntity.setNetherrackStayAtY(randomStayY);
                        stoneEntity.setNetherrackStayAtZ(randomStayZ);
                        stoneEntity.setOwner(pPlayer);
                        stoneEntity.setRemoveAndHurt(true);
                        stoneEntity.setSent(false);
                        stoneEntity.setPos(surfacePos.getX() + 0.5, surfacePos.getY() + 1, surfacePos.getZ() + 0.5);
                        stoneEntity.setShouldntDamage(true);
                        stoneEntity.setNetherrackXRot(randomXRot);
                        stoneEntity.setNetherrackYRot(randomYRot);

                        pPlayer.level().addFreshEntity(stoneEntity);
                    }
                    if (level.dimension() == Level.NETHER) {
                        EndStoneEntity stoneEntity = new EndStoneEntity(EntityInit.ENDSTONE_ENTITY.get(), pPlayer.level());
                        float randomStayX;
                        do { randomStayX = (float) ((Math.random() * 6) - 3);} while (randomStayX > -0.5 && randomStayX < 0.5);
                        float randomStayY = (float) ((Math.random() * 6) - 3);
                        float randomStayZ = (float) ((Math.random() * 6) - 3);
                        int randomXRot = (int) ((Math.random() * 10) - 5);
                        int randomYRot = (int) ((Math.random() * 10) - 5);
                        stoneEntity.setEndstoneStayAtX(randomStayX);
                        stoneEntity.setEndstoneStayAtY(randomStayY);
                        stoneEntity.setEndstoneStayAtZ(randomStayZ);
                        stoneEntity.setOwner(pPlayer);
                        stoneEntity.setRemoveAndHurt(true);
                        stoneEntity.setSent(false);
                        stoneEntity.setPos(surfacePos.getX() + 0.5, surfacePos.getY() + 1, surfacePos.getZ() + 0.5);
                        stoneEntity.setShouldntDamage(true);
                        stoneEntity.setEndstoneXRot(randomXRot);
                        stoneEntity.setEndstoneYRot(randomYRot);
                        pPlayer.level().addFreshEntity(stoneEntity);
                    }
                    if (level.dimension() != Level.OVERWORLD && level.dimension() != Level.NETHER && level.dimension() != Level.END) {
                        StoneEntity stoneEntity = new StoneEntity(EntityInit.STONE_ENTITY.get(), pPlayer.level());
                        float randomStayX;
                        do { randomStayX = (float) ((Math.random() * 6) - 3);} while (randomStayX > -0.5 && randomStayX < 0.5);
                        float randomStayY = (float) ((Math.random() * 6) - 3);
                        float randomStayZ = (float) ((Math.random() * 6) - 3);
                        int randomXRot = (int) ((Math.random() * 10) - 5);
                        int randomYRot = (int) ((Math.random() * 10) - 5);
                        stoneEntity.setStoneStayAtX(randomStayX);
                        stoneEntity.setStoneStayAtY(randomStayY);
                        stoneEntity.setStoneStayAtZ(randomStayZ);
                        stoneEntity.setOwner(pPlayer);
                        stoneEntity.setRemoveAndHurt(true);
                        stoneEntity.setSent(false);
                        stoneEntity.setPos(surfacePos.getX() + 0.5, surfacePos.getY() + 1, surfacePos.getZ() + 0.5);
                        stoneEntity.setShouldntDamage(true);
                        stoneEntity.setStoneXRot(randomXRot);
                        stoneEntity.setStoneYRot(randomYRot);
                        pPlayer.level().addFreshEntity(stoneEntity);
                    }
                }
            }
        }
    }

    private static BlockPos findSurfaceBelow(Level level, BlockPos startPos) {
        for (int y = startPos.getY(); y >= level.getMinBuildHeight(); y--) {
            BlockPos checkPos = new BlockPos(startPos.getX(), y, startPos.getZ());
            if (isOnSurface(level, checkPos)) {
                return checkPos;
            }
        }
        return null; // No surface found
    }

    private static boolean isOnSurface(Level level, BlockPos pos) {
        return level.canSeeSky(pos.above()) || !level.getBlockState(pos.above()).isSolid();
    }
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        Player pPlayer = event.getEntity();
        LOTMNetworkHandler.sendToServer(new MatterAccelerationBlockC2S());
        ItemStack heldItem = pPlayer.getMainHandItem();
        int activeSlot = pPlayer.getInventory().selected;
        if (!heldItem.isEmpty() && heldItem.getItem() instanceof MatterAccelerationBlocks) {
            pPlayer.getInventory().setItem(activeSlot, new ItemStack(ItemInit.MatterAccelerationSelf.get()));
            heldItem.shrink(1);
        }
    }
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        Player pPlayer = event.getEntity();
        int x = pPlayer.getPersistentData().getInt("matterAccelerationBlockTimer");
        if (x >= 1) {
            Vec3 lookDirection = pPlayer.getLookAngle().normalize().scale(20);
            if (pPlayer.level().dimension() == Level.OVERWORLD) {
                StoneEntity stoneEntity = pPlayer.level().getEntitiesOfClass(StoneEntity.class, pPlayer.getBoundingBox().inflate(10))
                        .stream()
                        .min(Comparator.comparingDouble(zombie -> zombie.distanceTo(pPlayer)))
                        .orElse(null);
                if (stoneEntity != null) {
                    stoneEntity.setDeltaMovement(lookDirection);
                    stoneEntity.setSent(true);
                    stoneEntity.setShouldntDamage(false);
                    stoneEntity.setTickCount(440);
                }
                if (stoneEntity == null) {
                    pPlayer.getPersistentData().putInt("matterAccelerationBlockTimer", 0);
                }
            }
            if (pPlayer.level().dimension() == Level.NETHER) {
                NetherrackEntity netherrackEntity = pPlayer.level().getEntitiesOfClass(NetherrackEntity.class, pPlayer.getBoundingBox().inflate(10))
                        .stream()
                        .min(Comparator.comparingDouble(zombie -> zombie.distanceTo(pPlayer)))
                        .orElse(null);
                if (netherrackEntity != null) {
                    netherrackEntity.setDeltaMovement(lookDirection);
                    netherrackEntity.setSent(true);
                    netherrackEntity.setShouldntDamage(false);
                    netherrackEntity.setTickCount(440);
                }
                if (netherrackEntity == null) {
                    pPlayer.getPersistentData().putInt("matterAccelerationBlockTimer", 0);
                }
            }
            if (pPlayer.level().dimension() == Level.END) {
                EndStoneEntity endStoneEntity = pPlayer.level().getEntitiesOfClass(EndStoneEntity.class, pPlayer.getBoundingBox().inflate(10))
                        .stream()
                        .min(Comparator.comparingDouble(zombie -> zombie.distanceTo(pPlayer)))
                        .orElse(null);
                if (endStoneEntity != null) {
                    endStoneEntity.setDeltaMovement(lookDirection);
                    endStoneEntity.setSent(true);
                    endStoneEntity.setShouldntDamage(false);
                    endStoneEntity.setTickCount(440);
                }
                if (endStoneEntity == null) {
                    pPlayer.getPersistentData().putInt("matterAccelerationBlockTimer", 0);
                }
            }
        } else {
            int activeSlot = pPlayer.getInventory().selected;
            ItemStack heldItem = pPlayer.getMainHandItem();
            if (!heldItem.isEmpty() && heldItem.getItem() instanceof MatterAccelerationBlocks) {
                pPlayer.getInventory().setItem(activeSlot, new ItemStack(ItemInit.MatterAccelerationSelf.get()));
                heldItem.shrink(1);
            }
        }
    }
}
