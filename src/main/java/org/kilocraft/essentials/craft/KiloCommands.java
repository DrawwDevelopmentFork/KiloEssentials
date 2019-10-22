package org.kilocraft.essentials.craft;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.indicode.fabric.permissions.Thimble;
import io.github.indicode.fabric.permissions.command.CommandPermission;
import net.minecraft.SharedConstants;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import org.kilocraft.essentials.api.Mod;
import org.kilocraft.essentials.api.chat.LangText;
import org.kilocraft.essentials.api.util.SomeGlobals;
import org.kilocraft.essentials.craft.commands.GamemodeCommand;
import org.kilocraft.essentials.craft.commands.KiloInfoCommand;
import org.kilocraft.essentials.craft.commands.MessageCommand;
import org.kilocraft.essentials.craft.commands.donatorcommands.PlayerParticlesCommand;
import org.kilocraft.essentials.craft.commands.essentials.*;
import org.kilocraft.essentials.craft.commands.essentials.ItemCommands.ItemCommand;
import org.kilocraft.essentials.craft.commands.essentials.locateCommands.LocateCommand;
import org.kilocraft.essentials.craft.commands.servermanagement.*;
import org.kilocraft.essentials.craft.config.KiloConifg;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KiloCommands {
    private CommandDispatcher<ServerCommandSource> dispatcher;
    public KiloCommands() {
        this.dispatcher = SomeGlobals.commandDispatcher;
        register(true);
    }

    public static KiloCommands INSTANCE;
    private static List<String> initializedPerms = new ArrayList<>();

    public static String getCommandPermission(String command) {
        if (!initializedPerms.contains(command)) {
            initializedPerms.add(command);
        }
        return "kiloessentials.command." + command;
    }

    private void register(boolean devEnv) {
        if (devEnv) {
            Mod.getLogger().debug("Server is running in debug mode!");
            SharedConstants.isDevelopment = true;
        }

        /**
         * @Misc
         */
        KiloInfoCommand.register(this.dispatcher);
        ReloadCommand.register(this.dispatcher);

        /**
         * @Essentials
         */
        RandomTeleportCommand.register(this.dispatcher);
        ColorsCommand.register(this.dispatcher);
        DiscordCommand.register(this.dispatcher);
        HealCommand.register(this.dispatcher);
        KiloInfoCommand.register(this.dispatcher);
        TimeCommand.register(this.dispatcher);
        GamemodeCommand.register(this.dispatcher);
        LocateCommand.register(this.dispatcher);
        EnderchestCommand.register(this.dispatcher);
        TpaCommand.register(this.dispatcher);
        ItemCommand.register(this.dispatcher);
        AnvilCommand.register(this.dispatcher);
        CraftingbenchCommand.register(this.dispatcher);
        NickCommand.register(this.dispatcher);
        KillCommand.register(this.dispatcher);
        RealNameCommand.register(this.dispatcher);
        FlyCommand.register(this.dispatcher);
        BackCommand.register(this.dispatcher);
        PlayerParticlesCommand.register(this.dispatcher);
        MessageCommand.register(this.dispatcher);
        //InfoCommand.register(this.dispatcher);

        /**
         * @ServerManagement
         */
        ServerCommand.register(this.dispatcher);
        ServerModNameCommand.register(this.dispatcher);
        StopCommand.register(this.dispatcher);
        OperatorCommand.register(this.dispatcher);

        Thimble.permissionWriters.add(pair -> {
            try {
                pair.getLeft().getPermission("kiloessentials", CommandPermission.class);
                pair.getLeft().getPermission("kiloessentials.command", CommandPermission.class);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                e.printStackTrace();
            }
            initializedPerms.forEach(perm -> {
                try {
                    pair.getLeft().getPermission("kiloessentials.command." + perm, CommandPermission.class);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    public String buildSmartUsage(LiteralCommandNode<ServerCommandSource> literalCommandNode, ServerCommandSource source) {
        String string = KiloConifg.getFileConfigOfMain().get("command.context.usage");
        Map<CommandNode<ServerCommandSource>, String> usage = this.dispatcher.getSmartUsage(literalCommandNode, source);
        return string.replaceFirst("%s", usage.toString());
    }

    public String buildUsage(String usage, ServerCommandSource source) {
        String string = KiloConifg.getFileConfigOfMain().get("command.context.usage");
        return string.replaceFirst("%s", usage);
    }

    public static LiteralText getPermissionError(String hoverText) {
        LiteralText literalText = LangText.get(true, "command.exception.permission");
        literalText.styled((style) -> {
           style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(hoverText).formatted(Formatting.YELLOW)));
        });
        return literalText;
    }
}
