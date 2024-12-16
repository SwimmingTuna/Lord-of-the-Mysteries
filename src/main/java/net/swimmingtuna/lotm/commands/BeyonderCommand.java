package net.swimmingtuna.lotm.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.swimmingtuna.lotm.beyonder.api.BeyonderClass;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;


public class BeyonderCommand {
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_BEYONDER_CLASS = new DynamicCommandExceptionType(arg1 -> Component.translatable("argument.lotm.beyonder_class.id.invalid", arg1));

    public static void register(CommandBuildContext buildContext, CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("beyonder")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("pathway", BeyonderClassArgument.beyonderClass())
                        .then(Commands.argument("sequence", IntegerArgumentType.integer(0, 9))
                                .executes(context -> {
                                    BeyonderClass result = BeyonderClassArgument.getBeyonderClass(context, "pathway");
                                    int level = IntegerArgumentType.getInteger(context, "sequence");
                                    if (result == null) {
                                        throw ERROR_UNKNOWN_BEYONDER_CLASS.create(context.getInput());
                                    }
                                    BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(context.getSource().getPlayerOrException());
                                    holder.setClassAndSequence(result, level);

                                    String sequenceName = result.sequenceNames().get(level);
                                    context.getSource().getPlayerOrException().sendSystemMessage(Component.translatable("item.lotm.beholder_potion.alert", sequenceName)
                                            .withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.BOLD));
                                    return 1;
                                })
                        )
                )
                .then(Commands.literal("remove")
                        .executes(context -> {
                            Player player = context.getSource().getPlayerOrException();
                            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
                            holder.removeClass();
                            Abilities playerAbilities = player.getAbilities();
                            playerAbilities.setFlyingSpeed(0.05F);
                            playerAbilities.setWalkingSpeed(0.1F);
                            player.onUpdateAbilities();
                            if (player instanceof ServerPlayer serverPlayer) {
                                serverPlayer.connection.send(new ClientboundPlayerAbilitiesPacket(playerAbilities));
                            }
                            return 1;
                        })
                )
        );
    }
}

