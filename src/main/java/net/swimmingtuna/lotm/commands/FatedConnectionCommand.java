package net.swimmingtuna.lotm.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Ability;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.item.OtherItems.LuckyGoldCoin;
import net.swimmingtuna.lotm.util.BeyonderUtil;

import java.util.List;

public class FatedConnectionCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        dispatcher.register(Commands.literal("fatedconnection")
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("ability", ItemArgument.item(buildContext))
                                .executes(context -> {
                                    ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                    ServerPlayer commandUser = context.getSource().getPlayer();
                                    Item abilityItem = ItemArgument.getItem(context, "ability").getItem();
                                    boolean hasValidLuckyGoldCoin = player.getInventory().items.stream()
                                            .anyMatch(stack ->
                                                    !stack.isEmpty() &&
                                                            stack.is(ItemInit.LUCKYGOLDCOIN.get()) &&
                                                            LuckyGoldCoin.getUUID(stack) != null &&
                                                            LuckyGoldCoin.getUUID(stack).equals(commandUser.getUUID())
                                            );
                                    if (player.getCooldowns().isOnCooldown(abilityItem)) {
                                        player.sendSystemMessage(Component.literal("is on cooldown" + abilityItem));
                                        return 0;
                                    }
                                    if (hasValidLuckyGoldCoin) {
                                        Ability ability = convertItemToAbility(abilityItem);
                                        SimpleAbilityItem simpleAbilityItem = convertItemToSimpleAbility(abilityItem);
                                        if (ability != null && abilityItem == BeyonderUtil.getAbilities(player)) {
                                            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
                                            holder.setSpirituality(holder.getSpirituality() + simpleAbilityItem.getSpirituality());
                                            BeyonderHolder commandHolder = BeyonderHolderAttacher.getHolderUnwrap(commandUser);
                                            commandHolder.useSpirituality(simpleAbilityItem.getSpirituality());
                                            context.getSource().sendSystemMessage(Component.literal(abilityItem.getDescription().getString() + " used for " + player.getName()));
                                            return executeAbility(player, ability, InteractionHand.MAIN_HAND, context.getSource());
                                        } else {
                                            context.getSource().sendFailure(Component.literal("Ability isn't valid"));
                                            return 0;
                                        }
                                    } else {
                                        context.getSource().sendFailure(Component.literal(player.getName() + " does not have a Lucky Gold Coin owned by you."));
                                        return 0;
                                    }
                                }))));
    }

    private static Ability convertItemToAbility(Item item) {
        if (!(item instanceof Ability)) {
            return null;
        }
        return (Ability) item;
    }
    private static SimpleAbilityItem convertItemToSimpleAbility(Item item) {
        if (!(item instanceof SimpleAbilityItem)) {
            return null;
        }
        return (SimpleAbilityItem) item;
    }


    private static int executeAbility(ServerPlayer player, Ability ability, InteractionHand hand, CommandSourceStack source) {
        double entityReach = ability.getEntityReach();
        double blockReach = ability.getBlockReach();

        // Attempt to use the ability on an entity
        boolean entityUsed = attemptUseOnEntity(player, ability, hand, entityReach);
        if (entityUsed) {
            source.sendSuccess(() -> Component.literal("Ability used on an entity."), true);
            return 1;
        }

        // Attempt to use the ability on a block
        boolean blockUsed = attemptUseOnBlock(player, ability, hand, blockReach);
        if (blockUsed) {
            source.sendSuccess(() -> Component.literal("Ability used on a block."), true);
            return 1;
        }

        // Default use
        ability.useAbility(player.level(), player, hand);
        source.sendSuccess(() -> Component.literal("Ability executed."), true);
        return 1;
    }

    private static boolean attemptUseOnEntity(ServerPlayer player, Ability ability, InteractionHand hand, double reach) {
        Vec3 eyePosition = player.getEyePosition();
        Vec3 lookVector = player.getLookAngle();
        Vec3 reachVector = eyePosition.add(lookVector.x * reach, lookVector.y * reach, lookVector.z * reach);
        AABB searchBox = new AABB(eyePosition.subtract(reach, reach, reach), eyePosition.add(reach, reach, reach));
        List<Entity> nearbyEntities = player.level().getEntities(
                player,
                searchBox,
                entity -> !entity.isSpectator() && entity.isPickable()
        );
        Entity closestEntity = null;
        double closestDistance = Double.MAX_VALUE;

        for (Entity entity : nearbyEntities) {
            Vec3 entityPos = entity.position();
            double distance = entityPos.distanceTo(eyePosition);
            Vec3 toEntityVector = entityPos.subtract(eyePosition).normalize();
            double angle = angleTo(lookVector, toEntityVector);
            if (distance <= reach && angle < Math.toRadians(15)) {
                if (distance < closestDistance) {
                    closestEntity = entity;
                    closestDistance = distance;
                }
            }
        }

        if (closestEntity instanceof LivingEntity livingEntity) {
            ability.useAbilityOnEntity(player.getItemInHand(hand), player, livingEntity, hand);
            return true;
        }
        return false;
    }



    private static boolean attemptUseOnBlock(ServerPlayer player, Ability ability, InteractionHand hand, double reach) {
        Vec3 eyePosition = player.getEyePosition();
        Vec3 lookVector = player.getLookAngle();
        Vec3 reachVector = eyePosition.add(lookVector.x * reach, lookVector.y * reach, lookVector.z * reach);

        BlockHitResult blockHit = player.level().clip(new ClipContext(
                eyePosition,
                reachVector,
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                player
        ));

        if (blockHit.getType() != HitResult.Type.MISS) {
            UseOnContext contextBlock = new UseOnContext(player.level(), player, hand, player.getItemInHand(hand), blockHit);
            ability.useAbilityOnBlock(contextBlock);
            return true;
        }
        return false;
    }

    public static double angleTo(Vec3 vec1, Vec3 vec2) {
        // Normalize the vectors to unit vectors
        Vec3 normalizedVec1 = vec1.normalize();
        Vec3 normalizedVec2 = vec2.normalize();

        // Calculate the dot product of the two vectors
        double dotProduct = normalizedVec1.dot(normalizedVec2);

        // Clamp the dot product value to avoid numerical issues
        dotProduct = Math.max(-1.0, Math.min(1.0, dotProduct));

        // Calculate the angle in radians and return
        return Math.acos(dotProduct); // Angle in radians
    }
}