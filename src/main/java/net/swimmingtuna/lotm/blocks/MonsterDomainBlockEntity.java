package net.swimmingtuna.lotm.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;
import net.minecraftforge.registries.RegistryObject;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.BlockEntityInit;
import net.swimmingtuna.lotm.init.BlockInit;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.TickableBlockEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.ibm.icu.text.PluralRules.Operand.i;

public class MonsterDomainBlockEntity extends BlockEntity implements TickableBlockEntity {
    private int ticks;
    private int radius;
    private boolean isBad;

    public MonsterDomainBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityInit.MONSTER_DOMAIN_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void tick() {
        if (this.level.isClientSide()) {
            return;
        }
        ticks++;
        AABB affectedArea = new AABB(
                worldPosition.getX() - radius, worldPosition.getY() - radius, worldPosition.getZ() - radius,
                worldPosition.getX() + radius, worldPosition.getY() + radius, worldPosition.getZ() + radius
        );


        List<Player> players = level.getEntitiesOfClass(Player.class, affectedArea);
        List<LivingEntity> livingEntities = level.getEntitiesOfClass(LivingEntity.class, affectedArea);


        if (isBad) {
            if (ticks % 10 == 0) {
                for (LivingEntity entity : livingEntities) {
                    if (entity instanceof Mob mob) {
                        if (!mob.shouldDespawnInPeaceful()) {
                            if (mob.hasEffect(MobEffects.POISON)) {
                                mob.removeEffect(MobEffects.POISON);
                            }
                            if (mob.hasEffect(MobEffects.WITHER)) {
                                mob.removeEffect(MobEffects.WITHER);
                            }
                            if (mob.hasEffect(MobEffects.HUNGER)) {
                                mob.removeEffect(MobEffects.HUNGER);
                            }
                            BeyonderUtil.applyMobEffect(mob, MobEffects.REGENERATION, 100, 2, false, false);
                        }
                        mob.getPersistentData().putInt("inMonsterProvidenceDomain", 20);
                    }
                }
            }


            for (Player player : players) {
                AttributeInstance luck = player.getAttribute(ModAttributes.LOTM_LUCK.get());
                AttributeInstance misfortune = player.getAttribute(ModAttributes.MISFORTUNE.get());

                if (ticks % 40 == 0) {
                    BeyonderUtil.applyMobEffect(player, MobEffects.REGENERATION, 100, 2, false, false); //configure this to make it scale with how small the radius is compared to max radius
                    BeyonderUtil.applyMobEffect(player, MobEffects.SATURATION, 100, 1, false, false); //configure this to make it scale with how small the radius is compared to max radius
                }
                List<ItemStack> itemStacks = new ArrayList<>(player.getInventory().items);
                itemStacks.addAll(player.getInventory().armor);
                itemStacks.add(player.getInventory().offhand.get(0));
                List<ItemStack> nonEmptyStacks = itemStacks.stream()
                        .filter(stack -> !stack.isEmpty())
                        .toList();
                if (ticks % 10 == 0) {
                    player.giveExperiencePoints(8);
                    for (ItemStack stack : nonEmptyStacks) {
                        if (stack.isDamageableItem()) {
                            stack.setDamageValue(Math.max(0, stack.getDamageValue() - 1)); //configure this to make it scale with how small the radius is compared to max radius
                        }
                    }
                    if (player.hasEffect(MobEffects.POISON)) {
                        player.removeEffect(MobEffects.POISON);
                    }
                    if (player.hasEffect(MobEffects.WITHER)) {
                        player.removeEffect(MobEffects.WITHER);
                    }
                    if (player.hasEffect(MobEffects.HUNGER)) {
                        player.removeEffect(MobEffects.HUNGER);
                    }
                    BeyonderUtil.applyMobEffect(player, MobEffects.DAMAGE_BOOST, 100, 1, false, false);
                    BeyonderUtil.applyMobEffect(player, MobEffects.DIG_SPEED, 100, 1, false, false);
                    BeyonderUtil.applyMobEffect(player, MobEffects.MOVEMENT_SPEED, 100, 1, false, false);
                    BeyonderUtil.applyMobEffect(player, MobEffects.JUMP, 100, 1, false, false);

                    // Survival benefits
                    player.getFoodData().setFoodLevel(Math.min(20, player.getFoodData().getFoodLevel() + 1));
                }
                if (ticks % 200 == 0) {
                    luck.setBaseValue(Math.min(100, luck.getBaseValue() + 1));
                    misfortune.setBaseValue(Math.max(0, misfortune.getBaseValue() - 1));
                }
            }

            if (ticks % 200 == 0) {
                for (int x = -radius; x <= radius; x++) {
                    for (int y = -30; y <= radius; y++) {
                        for (int z = -radius; z <= radius; z++) {
                            BlockPos targetPos = new BlockPos(worldPosition.getX() + x, Math.min(worldPosition.getY() + y, worldPosition.getY() - 30), worldPosition.getZ() + z);
                            double distance = Math.sqrt(x * x + y * y + z * z);
                            if (distance <= radius) {
                                BlockState targetBlock = level.getBlockState(targetPos);
                                if (targetBlock.getBlock() instanceof GrassBlock) {
                                    if (targetBlock.getBlock() instanceof GrassBlock) {
                                        if (level.random.nextInt(100) == 0) {
                                            level.setBlock(targetPos, Blocks.DIRT.defaultBlockState(), 3);
                                        }
                                    }
                                    if (targetPos.getY() <= 15 && targetPos.getY() >= 5) {
                                        if (targetBlock.getBlock() == Blocks.DEEPSLATE || targetBlock.getBlock() == Blocks.STONE) {
                                            if (level.random.nextInt(1000) == 1) {
                                                level.setBlock(targetPos, Blocks.DIAMOND_ORE.defaultBlockState(), 3);
                                            }
                                        }
                                    }
                                    if (targetPos.getY() <= 40 && targetPos.getY() >= 10) {
                                        if (targetBlock.getBlock() == Blocks.DEEPSLATE || targetBlock.getBlock() == Blocks.STONE) {
                                            if (level.random.nextInt(300) == 1) {
                                                level.setBlock(targetPos, Blocks.IRON_ORE.defaultBlockState(), 3);
                                            }
                                        }
                                    }
                                    if (targetPos.getY() <= 25 && targetPos.getY() >= 10) {
                                        if (targetBlock.getBlock() == Blocks.DEEPSLATE || targetBlock.getBlock() == Blocks.STONE) {
                                            if (level.random.nextInt(500) == 1) {
                                                level.setBlock(targetPos, Blocks.IRON_ORE.defaultBlockState(), 3);
                                            }
                                        }
                                    }
                                }
                                if (targetBlock.getBlock() instanceof CropBlock cropBlock) {
                                    IntegerProperty ageProperty = cropBlock.getAgeProperty();
                                    int currentAge = targetBlock.getValue(ageProperty);
                                    if (currentAge < cropBlock.getMaxAge()) {
                                        level.setBlock(targetPos, targetBlock.setValue(ageProperty, currentAge + 1), 3);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("radius", radius);
        tag.putInt("ticks", ticks);
        tag.putBoolean("isBad", isBad);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        radius = tag.getInt("radius");
        ticks = tag.getInt("ticks");
        isBad = tag.getBoolean("isBad");
    }

    // Method to set radius if you want to change it programmatically
    public void setRadius(int newRadius) {
        this.radius = newRadius;
        setChanged();
    }
}
