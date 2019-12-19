package org.kilocraft.essentials.commands.play;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.kilocraft.essentials.CommandPermission;
import org.kilocraft.essentials.KiloCommands;
import org.kilocraft.essentials.api.KiloServer;
import org.kilocraft.essentials.api.command.TabCompletions;
import org.kilocraft.essentials.api.user.OnlineUser;
import org.kilocraft.essentials.chat.KiloChat;
import org.kilocraft.essentials.commands.CommandHelper;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.BoolArgumentType.getBool;
import static net.minecraft.command.arguments.EntityArgumentType.getPlayer;
import static net.minecraft.command.arguments.EntityArgumentType.player;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class InvulnerablemodeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> argumentBuilder = literal("invulnerable")
                .requires(s -> KiloCommands.hasPermission(s, CommandPermission.INVULNERAVLE))
                .executes(c -> executeToggle(c.getSource(), c.getSource().getPlayer()))
                .then(
                        argument("player", player())
                            .suggests(TabCompletions::allPlayers)
                            .executes(c -> executeToggle(c.getSource(), getPlayer(c, "player")))
                            .then(argument("set", bool())
                                    .executes(c -> executeSet(c.getSource(), getPlayer(c, "player"), getBool(c, "set")))
                            )
                );

        dispatcher.register(argumentBuilder);
    }

    private static int executeToggle(ServerCommandSource source, ServerPlayerEntity player) {
        executeSet(source, player, !player.isInvulnerable());
        return 1;
    }

    private static int executeSet(ServerCommandSource source, ServerPlayerEntity player, boolean set) {
        player.setInvulnerable(set);
        KiloChat.sendLangMessageTo(source, "template.#1", "Invulnerable", set, player.getName().asString());

        OnlineUser user = KiloServer.getServer().getUserManager().getOnline(player);
        user.setInvulnerable(set);
        
        if (!CommandHelper.areTheSame(source, player))
            KiloChat.sendLangMessageTo(player, "template.#1.announce", source.getName(), "Invulnerable", set);

        player.sendAbilitiesUpdate();
        return 1;
    }
}