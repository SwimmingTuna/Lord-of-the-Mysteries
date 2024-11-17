package net.swimmingtuna.lotm.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.BlockEntityInit;
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

        int multiplier;
        Player player = level.getPlayerByUUID(ownerUUID);
        if (player != null) {
            int safeRadius = Math.max(1, getRadius()); // Ensure radius is at least 1
            if (ticks % 20 == 0) {
                System.out.println(getRadius());
                System.out.println(safeRadius);
            }
            BeyonderHolder beyonderHolder = BeyonderHolderAttacher.getHolderUnwrap(player);
            int maxRadius = 250 - (beyonderHolder.getCurrentSequence() * 45);
            multiplier = Math.max(1, (maxRadius / safeRadius) / 2);
        } else multiplier = 1;
        List<Player> players = level.getEntitiesOfClass(Player.class, affectedArea);
        List<LivingEntity> livingEntities = level.getEntitiesOfClass(LivingEntity.class, affectedArea);

        if (!getBad()) {
            removeBadEffectAndAddRegenAndAddPersistence(livingEntities, multiplier);


            for (Player affectedPlayer : players) {
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
                        removeBadEffect(affectedPlayer);
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
                        removeBadEffect(affectedPlayer);
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

            if (ticks % 200 == 0) {
                for (int x = -radius; x <= radius; x++) {
                    for (int y = -30; y <= radius; y++) {
                        for (int z = -radius; z <= radius; z++) {
                            BlockPos targetPos = new BlockPos(worldPosition.getX() + x, Math.min(worldPosition.getY() + y, worldPosition.getY() - 30), worldPosition.getZ() + z);
                            double distance = Math.sqrt(x * x + y * y + z * z);
                            if (distance <= radius) {
                                BlockState targetBlock = level.getBlockState(targetPos);
                                if (targetBlock.getBlock() == Blocks.DIRT) {
                                    if (level.random.nextInt(100) <= (multiplier) && level.random.nextInt() != 0) {
                                        level.setBlock(targetPos, Blocks.GRASS_BLOCK.defaultBlockState(), 3);
                                    }
                                }
                                if (targetPos.getY() <= 15 && targetPos.getY() >= 5) {
                                    if (targetBlock.getBlock() == Blocks.DEEPSLATE || targetBlock.getBlock() == Blocks.STONE) {
                                        if (level.random.nextInt(1000) <= (multiplier) && level.random.nextInt() != 0) {
                                            level.setBlock(targetPos, Blocks.DIAMOND_ORE.defaultBlockState(), 3);
                                        }
                                    }
                                }
                                if (targetPos.getY() <= 40 && targetPos.getY() >= 10) {
                                    if (targetBlock.getBlock() == Blocks.DEEPSLATE || targetBlock.getBlock() == Blocks.STONE) {
                                        if (level.random.nextInt(300) <= (multiplier) && level.random.nextInt() != 0) {
                                            level.setBlock(targetPos, Blocks.IRON_ORE.defaultBlockState(), 3);
                                        }
                                    }
                                }
                                if (targetPos.getY() <= 25 && targetPos.getY() >= 10) {
                                    if (targetBlock.getBlock() == Blocks.DEEPSLATE || targetBlock.getBlock() == Blocks.STONE) {
                                        if (level.random.nextInt(500) <= (multiplier) && level.random.nextInt() != 0) {
                                            level.setBlock(targetPos, Blocks.IRON_ORE.defaultBlockState(), 3);
                                        }
                                    }
                                }
                                if (targetBlock.getBlock() instanceof CropBlock cropBlock) {
                                    IntegerProperty ageProperty = cropBlock.getAgeProperty();
                                    int currentAge = targetBlock.getValue(ageProperty);
                                    if (currentAge < cropBlock.getMaxAge()) {
                                        level.setBlock(targetPos, targetBlock.setValue(ageProperty, currentAge + multiplier), 3);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            removeBadEffectAndAddRegenAndAddPersistence(livingEntities, multiplier);


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

            if (ticks % 200 == 0) {
                player.sendSystemMessage(Component.literal("Starting block scan..."));
                int blocksScanned = 0;
                int grassBlocksFound = 0;
                int oresFound = 0;
                int cropsFound = 0;

                for (int x = -radius; x <= radius; x++) {
                    for (int y = -30; y <= radius; y++) {
                        for (int z = -radius; z <= radius; z++) {
                            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
                            mutablePos.set(worldPosition.getX() + x, worldPosition.getY() + y, worldPosition.getZ() + z);
                            double distance = Math.sqrt(x * x + y * y + z * z);

                            if (distance <= radius) {
                                blocksScanned++;
                                BlockState targetBlock = level.getBlockState(mutablePos);

                                // Debug Grass Blocks
                                if (targetBlock.getBlock() instanceof GrassBlock) {
                                    grassBlocksFound++;
                                    if (level.random.nextInt(100) <= 5 * multiplier) {
                                        player.sendSystemMessage(Component.literal("Transforming grass block at: " + mutablePos));
                                        level.setBlock(mutablePos, Blocks.DIRT.defaultBlockState(), 3);
                                    }
                                }

                                // Debug Ores
                                TagKey<Block> ORE_TAG = BlockTags.create(new ResourceLocation("forge", "ores"));
                                if (mutablePos.getY() <= -30 && mutablePos.getY() >= 40) {
                                    if (targetBlock.getBlock().builtInRegistryHolder().is(ORE_TAG)) {
                                        oresFound++;
                                        if (level.random.nextInt(500) <= (multiplier) && level.random.nextInt() != 0) {
                                            player.sendSystemMessage(Component.literal("Transforming ore at: " + mutablePos));
                                            level.setBlock(mutablePos, Blocks.IRON_ORE.defaultBlockState(), 3);
                                        }
                                    }
                                }

                                // Debug Crops
                                if (targetBlock.getBlock() instanceof CropBlock cropBlock) {
                                    cropsFound++;
                                    IntegerProperty ageProperty = cropBlock.getAgeProperty();
                                    int currentAge = targetBlock.getValue(ageProperty);
                                    if (currentAge < cropBlock.getMaxAge()) {
                                        player.sendSystemMessage(Component.literal("Modifying crop age at: " + mutablePos));
                                        level.setBlock(mutablePos, targetBlock.setValue(ageProperty, currentAge - multiplier), 3);
                                    }
                                    if (currentAge == 0) {
                                        player.sendSystemMessage(Component.literal("Converting dead crop to dirt at: " + mutablePos));
                                        level.setBlock(mutablePos, Blocks.DIRT.defaultBlockState(), 3);
                                    }
                                }
                            }
                        }
                    }
                }

                // Send summary after scan
                player.sendSystemMessage(Component.literal("Scan complete! Summary:"));
                player.sendSystemMessage(Component.literal("- Total blocks scanned: " + blocksScanned));
                player.sendSystemMessage(Component.literal("- Grass blocks found: " + grassBlocksFound));
                player.sendSystemMessage(Component.literal("- Ores found: " + oresFound));
                player.sendSystemMessage(Component.literal("- Crops found: " + cropsFound));
            }
        }
    }

    private void removeBadEffect(LivingEntity affectedPlayer) {
        if (affectedPlayer.hasEffect(MobEffects.POISON)) {
            affectedPlayer.removeEffect(MobEffects.POISON);
        }
        if (affectedPlayer.hasEffect(MobEffects.WITHER)) {
            affectedPlayer.removeEffect(MobEffects.WITHER);
        }
        if (affectedPlayer.hasEffect(MobEffects.HUNGER)) {
            affectedPlayer.removeEffect(MobEffects.HUNGER);
        }
    }

    private void removeBadEffectAndAddRegenAndAddPersistence(List<LivingEntity> livingEntities, int multiplier) {
        if (ticks % 10 == 0) {
            for (LivingEntity entity : livingEntities) {
                if (entity instanceof Mob mob) {
                    if (!mob.shouldDespawnInPeaceful()) {
                        removeBadEffect(mob);
                        BeyonderUtil.applyMobEffect(mob, MobEffects.REGENERATION, 100, 2 * multiplier, false, false);
                    }
                    mob.getPersistentData().putInt("inMonsterProvidenceDomain", 20);
                }
            }
        }
    }


    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("radius", radius);
        tag.putInt("ticks", ticks);
        tag.putBoolean("isBad", getBad());
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

    // Method to set radius if you want to change it programmatically
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

    public boolean getBad() {
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
