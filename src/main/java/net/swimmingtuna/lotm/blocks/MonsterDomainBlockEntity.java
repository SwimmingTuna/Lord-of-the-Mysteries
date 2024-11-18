package net.swimmingtuna.lotm.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.BlockEntityInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.Earthquake;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.TickableBlockEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MonsterDomainBlockEntity extends BlockEntity implements TickableBlockEntity {
    private int ticks;
    private int radius;
    private boolean isBad;
    private UUID ownerUUID;
    private int currentX = -getRadius();
    private int currentY = -40;
    private int currentZ = -getRadius();

    public MonsterDomainBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityInit.MONSTER_DOMAIN_BLOCK_ENTITY.get(), pos, state);
    }

    private static void removeMobEffects(Mob mob, int multiplier) {
        if (mob.hasEffect(MobEffects.POISON)) {
            mob.removeEffect(MobEffects.POISON);
        }
        if (mob.hasEffect(MobEffects.WITHER)) {
            mob.removeEffect(MobEffects.WITHER);
        }
        if (mob.hasEffect(MobEffects.HUNGER)) {
            mob.removeEffect(MobEffects.HUNGER);
        }
        BeyonderUtil.applyMobEffect(mob, MobEffects.REGENERATION, 100, 2 * multiplier, false, false);
        mob.getPersistentData().putInt("inMonsterProvidenceDomain", 20);
    }

    private void isGoodPlayerAffect(Player  affectedPlayer, int multiplier) {
        BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(affectedPlayer);
        if (!(holder.getCurrentClass() == BeyonderClassInit.MONSTER.get() && holder.getCurrentSequence() <= 3)) {
            AttributeInstance luck = affectedPlayer.getAttribute(ModAttributes.LOTM_LUCK.get());
            AttributeInstance misfortune = affectedPlayer.getAttribute(ModAttributes.MISFORTUNE.get());

            if (ticks % 40 == 0) {
                BeyonderUtil.applyMobEffect(affectedPlayer, MobEffects.REGENERATION, 100, 2 * multiplier, false, false); //configure this to make it scale with how small the radius is compared to max radius
                BeyonderUtil.applyMobEffect(affectedPlayer, MobEffects.SATURATION, 100, multiplier, false, false); //configure this to make it scale with how small the radius is compared to max radius
            }
            List<ItemStack> itemStacks = new ArrayList<>(affectedPlayer.getInventory().items);
            itemStacks.addAll(affectedPlayer.getInventory().armor);
            itemStacks.add(affectedPlayer.getInventory().offhand.get(0));
            List<ItemStack> nonEmptyStacks = itemStacks.stream()
                    .filter(stack -> !stack.isEmpty())
                    .toList();
            if (ticks % 10 == 0) {
                affectedPlayer.giveExperiencePoints(8);
                for (ItemStack stack : nonEmptyStacks) {
                    if (stack.isDamageableItem()) {
                        stack.setDamageValue(Math.max(0, stack.getDamageValue() - 1)); //configure this to make it scale with how small the radius is compared to max radius
                    }
                }
                if (affectedPlayer.hasEffect(MobEffects.POISON)) {
                    affectedPlayer.removeEffect(MobEffects.POISON);
                }
                if (affectedPlayer.hasEffect(MobEffects.WITHER)) {
                    affectedPlayer.removeEffect(MobEffects.WITHER);
                }
                if (affectedPlayer.hasEffect(MobEffects.HUNGER)) {
                    affectedPlayer.removeEffect(MobEffects.HUNGER);
                }
                BeyonderUtil.applyMobEffect(affectedPlayer, MobEffects.DAMAGE_BOOST, 100, multiplier, false, false);
                BeyonderUtil.applyMobEffect(affectedPlayer, MobEffects.DIG_SPEED, 100, multiplier, false, false);
                BeyonderUtil.applyMobEffect(affectedPlayer, MobEffects.MOVEMENT_SPEED, 100, multiplier, false, false);
                BeyonderUtil.applyMobEffect(affectedPlayer, MobEffects.JUMP, 100, multiplier, false, false);

                // Survival benefits
                affectedPlayer.getFoodData().setFoodLevel(Math.min(20, affectedPlayer.getFoodData().getFoodLevel() + multiplier));
            }
            if (ticks % 200 == 0) {
                luck.setBaseValue(Math.min(100, luck.getBaseValue() + multiplier));
                misfortune.setBaseValue(Math.max(0, misfortune.getBaseValue() - multiplier));
            }
        } else {
            AttributeInstance luck = affectedPlayer.getAttribute(ModAttributes.LOTM_LUCK.get());
            AttributeInstance misfortune = affectedPlayer.getAttribute(ModAttributes.MISFORTUNE.get());

            if (ticks % 40 == 0) {
                BeyonderUtil.applyMobEffect(affectedPlayer, MobEffects.REGENERATION, 100, 3 * multiplier, false, false); //configure this to make it scale with how small the radius is compared to max radius
                BeyonderUtil.applyMobEffect(affectedPlayer, MobEffects.SATURATION, 100, 2 * multiplier, false, false); //configure this to make it scale with how small the radius is compared to max radius
            }
            List<ItemStack> itemStacks = new ArrayList<>(affectedPlayer.getInventory().items);
            itemStacks.addAll(affectedPlayer.getInventory().armor);
            itemStacks.add(affectedPlayer.getInventory().offhand.get(0));
            List<ItemStack> nonEmptyStacks = itemStacks.stream()
                    .filter(stack -> !stack.isEmpty())
                    .toList();
            if (ticks % 10 == 0) {
                affectedPlayer.giveExperiencePoints(16 * multiplier);
                for (ItemStack stack : nonEmptyStacks) {
                    if (stack.isDamageableItem()) {
                        stack.setDamageValue(Math.max(0, stack.getDamageValue() - 4 * multiplier)); //configure this to make it scale with how small the radius is compared to max radius
                    }
                }
                if (affectedPlayer.hasEffect(MobEffects.POISON)) {
                    affectedPlayer.removeEffect(MobEffects.POISON);
                }
                if (affectedPlayer.hasEffect(MobEffects.WITHER)) {
                    affectedPlayer.removeEffect(MobEffects.WITHER);
                }
                if (affectedPlayer.hasEffect(MobEffects.HUNGER)) {
                    affectedPlayer.removeEffect(MobEffects.HUNGER);
                }
                if (affectedPlayer.hasEffect(MobEffects.CONFUSION)) {
                    affectedPlayer.removeEffect(MobEffects.CONFUSION);
                }
                BeyonderUtil.applyMobEffect(affectedPlayer, MobEffects.DAMAGE_BOOST, 100, 2 * multiplier, false, false);
                BeyonderUtil.applyMobEffect(affectedPlayer, MobEffects.DIG_SPEED, 100, 2 * multiplier, false, false);
                BeyonderUtil.applyMobEffect(affectedPlayer, MobEffects.MOVEMENT_SPEED, 100, 2 * multiplier, false, false);
                BeyonderUtil.applyMobEffect(affectedPlayer, MobEffects.JUMP, 100, 2 * multiplier, false, false);

                // Survival benefits
                affectedPlayer.getFoodData().setFoodLevel(Math.min(20, affectedPlayer.getFoodData().getFoodLevel() + 4 * multiplier));
            }
            if (ticks % 200 == 0) {
                luck.setBaseValue(Math.min(100, luck.getBaseValue() + multiplier));
                misfortune.setBaseValue(Math.max(0, misfortune.getBaseValue() - multiplier));
            }
        }
    }

    @Override
    public void tick() {
        if (this.level.isClientSide()) {
            return;
        }
        if (this.level.dimension() != Level.NETHER) {
            if (this.worldPosition.getY() != 100) {
                this.worldPosition.offset(0, 100 - worldPosition.getY(), 0);
            }
        }
        ticks++;
        AABB affectedArea = new AABB(worldPosition.getX() - radius, worldPosition.getY() - radius, worldPosition.getZ() - radius, worldPosition.getX() + radius, worldPosition.getY() + radius, worldPosition.getZ() + radius);
        List<Player> players = level.getEntitiesOfClass(Player.class, affectedArea);
        List<LivingEntity> livingEntities = level.getEntitiesOfClass(LivingEntity.class, affectedArea);
        int multiplier;
        Player player = level.getPlayerByUUID(ownerUUID);
        if (player != null) {
            int safeRadius = Math.max(1, getRadius()); // Ensure radius is at least 1
            BeyonderHolder beyonderHolder = BeyonderHolderAttacher.getHolderUnwrap(player);
            int maxRadius = 250 - (beyonderHolder.getCurrentSequence() * 45);
            multiplier = Math.max(1, (maxRadius / safeRadius) / 2);
        } else multiplier = 1;

        if (!isBad) {
            if (ticks % 10 == 0) {
                for (LivingEntity entity : livingEntities) {
                    if (entity instanceof Mob mob) {
                        removeMobEffects(mob, multiplier);
                    }
                }
            }
            for (Player affectedPlayer : players) {
                isGoodPlayerAffect(affectedPlayer, multiplier);
            }

            if (ticks % 50 == 0) {
                int blocksProcessed = 0;
                while (blocksProcessed < 200 && currentX <= getRadius()) {
                    while (blocksProcessed < 200 && currentY <= getRadius()) {
                        while (blocksProcessed < 200 && currentZ <= getRadius()) {
                            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
                            mutablePos.set(worldPosition.getX() + currentX,
                                    worldPosition.getY() + currentY,
                                    worldPosition.getZ() + currentZ);
                            BlockState targetBlock = level.getBlockState(mutablePos);
                            boolean blockWasProcessed = false;
                            if (!(targetBlock.getBlock() instanceof AirBlock)) {

                                // Process dirt to grass conversion
                                if (targetBlock.getBlock() == Blocks.DIRT && Earthquake.isOnSurface(level, mutablePos)) {
                                    if (level.random.nextInt(100) <= (multiplier) && level.random.nextInt() != 0) {
                                        level.setBlock(mutablePos, Blocks.GRASS_BLOCK.defaultBlockState(), 3);
                                    }
                                    blockWasProcessed = true;
                                }

                                // Process diamond ore generation
                                if (mutablePos.getY() <= 15 && mutablePos.getY() >= 5) {
                                    if (targetBlock.getBlock() == Blocks.DEEPSLATE || targetBlock.getBlock() == Blocks.STONE) {
                                        if (level.random.nextInt(1000) <= (multiplier) && level.random.nextInt() != 0) {
                                            level.setBlock(mutablePos, Blocks.DIAMOND_ORE.defaultBlockState(), 3);
                                        }
                                        blockWasProcessed = true;
                                    }
                                }

                                // Process first iron ore generation layer
                                if (mutablePos.getY() <= 40 && mutablePos.getY() >= 10) {
                                    if (targetBlock.getBlock() == Blocks.DEEPSLATE || targetBlock.getBlock() == Blocks.STONE) {
                                        if (level.random.nextInt(300) <= (multiplier) && level.random.nextInt() != 0) {
                                            level.setBlock(mutablePos, Blocks.IRON_ORE.defaultBlockState(), 3);
                                        }
                                        blockWasProcessed = true;
                                    }
                                }

                                // Process second iron ore generation layer
                                if (mutablePos.getY() <= 25 && mutablePos.getY() >= 10) {
                                    if (targetBlock.getBlock() == Blocks.DEEPSLATE || targetBlock.getBlock() == Blocks.STONE) {
                                        if (level.random.nextInt(500) <= (multiplier) && level.random.nextInt() != 0) {
                                            level.setBlock(mutablePos, Blocks.IRON_ORE.defaultBlockState(), 3);
                                        }
                                        blockWasProcessed = true;
                                    }
                                }

                                // Process crops
                                if (targetBlock.getBlock() instanceof CropBlock cropBlock) {
                                    IntegerProperty ageProperty = cropBlock.getAgeProperty();
                                    int currentAge = targetBlock.getValue(ageProperty);
                                    if (currentAge < cropBlock.getMaxAge()) {
                                        level.setBlock(mutablePos, targetBlock.setValue(ageProperty, currentAge + multiplier), 3);
                                    }
                                    blockWasProcessed = true;
                                }

                                if (blockWasProcessed) {
                                    blocksProcessed++;
                                }
                            }
                            currentZ++;
                        }
                        currentZ = -getRadius();  // Reset Z and increment Y
                        currentY++;
                    }
                    currentY = -30;  // Reset Y and increment X
                    currentX++;
                }

                // Reset everything when we've finished the area
                if (currentX > getRadius()) {
                    currentX = -getRadius();
                    currentY = -30;
                    currentZ = -getRadius();
                }
            }
        }

        else {
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
                            BeyonderUtil.applyMobEffect(mob, MobEffects.REGENERATION, 100, 2 * multiplier, false, false);
                        }
                        mob.getPersistentData().putInt("inMonsterProvidenceDomain", 20);
                    }
                }
            }


            for (Player affectedPlayer : players) {
                BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(affectedPlayer);
                if (!(holder.getCurrentClass() == BeyonderClassInit.MONSTER.get() && holder.getCurrentSequence() <= 3)) {
                    AttributeInstance luck = affectedPlayer.getAttribute(ModAttributes.LOTM_LUCK.get());
                    AttributeInstance misfortune = affectedPlayer.getAttribute(ModAttributes.MISFORTUNE.get());

                    if (ticks % 40 == 0) {
                        BeyonderUtil.applyMobEffect(affectedPlayer, MobEffects.WEAKNESS, 100, multiplier, false, false);
                        BeyonderUtil.applyMobEffect(affectedPlayer, MobEffects.MOVEMENT_SLOWDOWN, 100, multiplier, false, false);
                        BeyonderUtil.applyMobEffect(affectedPlayer, MobEffects.HUNGER, 100, multiplier, false, false);
                        BeyonderUtil.applyMobEffect(affectedPlayer, MobEffects.POISON, 100, multiplier, false, false);
                    }
                    List<ItemStack> itemStacks = new ArrayList<>(affectedPlayer.getInventory().items);
                    itemStacks.addAll(affectedPlayer.getInventory().armor);
                    itemStacks.add(affectedPlayer.getInventory().offhand.get(0));
                    List<ItemStack> nonEmptyStacks = itemStacks.stream()
                            .filter(stack -> !stack.isEmpty())
                            .toList();
                    if (ticks % 10 == 0) {
                        affectedPlayer.giveExperiencePoints(-5 * multiplier);
                        for (ItemStack stack : nonEmptyStacks) {
                            stack.setDamageValue(Math.max(0, stack.getDamageValue() + multiplier)); //configure this to make it scale with how small the radius is compared to max radius
                        }
                    }
                    if (ticks % 2 == 0) {
                        if (affectedPlayer.hasEffect(MobEffects.REGENERATION)) {
                            affectedPlayer.removeEffect(MobEffects.REGENERATION);
                        }
                        if (affectedPlayer.hasEffect(MobEffects.ABSORPTION)) {
                            affectedPlayer.removeEffect(MobEffects.ABSORPTION);
                        }
                        if (affectedPlayer.hasEffect(MobEffects.DIG_SPEED)) {
                            affectedPlayer.removeEffect(MobEffects.DIG_SPEED);
                        }

                        // Survival benefits
                        affectedPlayer.getFoodData().setFoodLevel(Math.min(20, affectedPlayer.getFoodData().getFoodLevel() - multiplier));
                    }
                    if (ticks % 200 == 0) {
                        luck.setBaseValue(Math.max(100, luck.getBaseValue() - multiplier));
                        misfortune.setBaseValue(Math.min(100, misfortune.getBaseValue() + multiplier));
                    }
                }
            }

            if (ticks % 50 == 0) {
                int blocksProcessed = 0;
                while (blocksProcessed < 200 && currentX <= getRadius()) {
                    while (blocksProcessed < 200 && currentY <= getRadius()) {
                        while (blocksProcessed < 200 && currentZ <= getRadius()) {
                            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
                            mutablePos.set(worldPosition.getX() + currentX,
                                    worldPosition.getY() + currentY,
                                    worldPosition.getZ() + currentZ);
                            BlockState targetBlock = level.getBlockState(mutablePos);

                            boolean blockWasProcessed = false;
                            if (!(targetBlock.getBlock() instanceof AirBlock)) {
                                // Process grass blocks
                                if (targetBlock.getBlock() instanceof GrassBlock) {
                                    if (level.random.nextInt(100) <= 5 * multiplier) {
                                        level.setBlock(mutablePos, Blocks.DIRT.defaultBlockState(), 3);
                                    }
                                    blockWasProcessed = true;
                                }

                                // Process ores
                                TagKey<Block> ORE_TAG = BlockTags.create(new ResourceLocation("forge", "ores"));
                                if (mutablePos.getY() <= -30 && mutablePos.getY() >= 40) {
                                    if (targetBlock.getBlock().builtInRegistryHolder().is(ORE_TAG)) {
                                        if (level.random.nextInt(500) <= (multiplier) && level.random.nextInt() != 0) {
                                            level.setBlock(mutablePos, Blocks.COAL_ORE.defaultBlockState(), 3);
                                        }
                                        blockWasProcessed = true;
                                    }
                                }

                                // Process crops
                                if (targetBlock.getBlock() instanceof CropBlock cropBlock) {
                                    IntegerProperty ageProperty = cropBlock.getAgeProperty();
                                    int currentAge = targetBlock.getValue(ageProperty);
                                    if (currentAge < cropBlock.getMaxAge()) {
                                        level.setBlock(mutablePos, targetBlock.setValue(ageProperty, currentAge - multiplier), 3);
                                    }
                                    if (currentAge == 0) {
                                        level.setBlock(mutablePos, Blocks.DIRT.defaultBlockState(), 3);
                                    }
                                    blockWasProcessed = true;
                                }

                                if (blockWasProcessed) {
                                    blocksProcessed++;
                                }
                            }

                            currentZ++;
                        }
                        currentZ = -getRadius();  // Reset Z and increment Y
                        currentY++;
                    }
                    currentY = -30;  // Reset Y and increment X
                    currentX++;
                }

                // Reset everything when we've finished the area
                if (currentX > getRadius()) {
                    currentX = -getRadius();
                    currentY = -30;
                    currentZ = -getRadius();
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
        if (ownerUUID != null) {
            tag.putUUID("ownerUUID", ownerUUID);
            System.out.println("Saving UUID: " + ownerUUID);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        radius = tag.getInt("radius");
        ticks = tag.getInt("ticks");
        isBad = tag.getBoolean("isBad");
        if (tag.contains("ownerUUID")) {
            ownerUUID = tag.getUUID("ownerUUID");
            System.out.println("Loading UUID: " + ownerUUID);
        }
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(getBlockPos()).inflate(0.5);
    }

    public void setRadius(int newRadius) {
        this.radius = newRadius;
        setChanged();
    }

    public void setBad(boolean isBad) {
        this.isBad = isBad;
        setChanged();
    }

    public int getRadius() {
        return this.radius;
    }

    public boolean getBad(boolean isBad) {
        return this.isBad;
    }

    public void setOwner(Player player) {
        this.ownerUUID = player.getUUID();
        setChanged();
    }

    public Player getOwner() {
        if (ownerUUID == null || level == null) return null;
        return level.getPlayerByUUID(ownerUUID);
    }

    public static List<MonsterDomainBlockEntity> getDomainsOwnedBy(Level level, Player player) {
        List<MonsterDomainBlockEntity> ownedDomains = new ArrayList<>();

        ServerLevel serverLevel = (ServerLevel) level;
        ChunkMap chunkMap = serverLevel.getChunkSource().chunkMap;

        for (ChunkHolder chunkHolder : chunkMap.getChunks()) {
            LevelChunk chunk = chunkHolder.getFullChunk() != null
                    ? chunkHolder.getFullChunk()
                    : chunkHolder.getTickingChunk();

            if (chunk == null) {
                continue;
            }

            for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                if (blockEntity instanceof MonsterDomainBlockEntity domain) {
                    UUID domainOwnerUUID = domain.ownerUUID;
                    if (domainOwnerUUID != null && domainOwnerUUID.equals(player.getUUID())) {
                        ownedDomains.add(domain);
                    }
                }
            }
        }

        return ownedDomains;
    }
}
