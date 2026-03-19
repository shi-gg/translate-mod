package com.wamellow.minecraft.translate.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.wamellow.minecraft.translate.config.ModConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.util.Set;

public class WhitelistCommand {
    private static final String GREEN = "§a";
    private static final String RED = "§c";
    private static final String GRAY = "§7";
    private static final String AQUA = "§b";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("translate")
            .then(Commands.literal("whitelist")
                .then(Commands.literal("add")
                    .then(Commands.argument("player", StringArgumentType.word())
                        .executes(WhitelistCommand::addPlayer)))
                .then(Commands.literal("remove")
                    .then(Commands.argument("player", StringArgumentType.word())
                        .executes(WhitelistCommand::removePlayer)))
                .then(Commands.literal("list")
                    .executes(WhitelistCommand::listWhitelist))
                .then(Commands.literal("clear")
                    .executes(WhitelistCommand::clearWhitelist))
                .executes(WhitelistCommand::showWhitelistHelp)));
    }

    private static int addPlayer(CommandContext<CommandSourceStack> ctx) {
        String player = StringArgumentType.getString(ctx, "player");
        ModConfig.getInstance().addToWhitelist(player);
        ModConfig.save();
        ctx.getSource().sendSuccess(() -> Component.literal(GREEN + "✓ Added " + AQUA + player + GREEN + " to whitelist"), true);
        return 1;
    }

    private static int removePlayer(CommandContext<CommandSourceStack> ctx) {
        String player = StringArgumentType.getString(ctx, "player");
        ModConfig.getInstance().removeFromWhitelist(player);
        ModConfig.save();
        ctx.getSource().sendSuccess(() -> Component.literal(GREEN + "✓ Removed " + RED + player + GREEN + " from whitelist"), true);
        return 1;
    }

    private static int listWhitelist(CommandContext<CommandSourceStack> ctx) {
        Set<String> whitelist = ModConfig.getInstance().getWhitelist();

        if (whitelist.isEmpty()) {
            ctx.getSource().sendSuccess(() -> Component.literal(GRAY + "Whitelist is empty " + GRAY + "- all players will be translated"), false);
        } else {
            for (String player : whitelist) {
                ctx.getSource().sendSuccess(() -> Component.literal(GRAY + "  " + AQUA + "• " + player), false);
            }
        }
        return 1;
    }

    private static int clearWhitelist(CommandContext<CommandSourceStack> ctx) {
        ModConfig.getInstance().getWhitelist().clear();
        ModConfig.save();
        ctx.getSource().sendSuccess(() -> Component.literal(GREEN + "✓ Whitelist cleared - all players will be translated"), true);
        return 1;
    }

    private static int showWhitelistHelp(CommandContext<CommandSourceStack> ctx) {
        ctx.getSource().sendSuccess(() -> Component.literal(GRAY + "/translate whitelist add <player> " + GRAY + "- Add player"), false);
        ctx.getSource().sendSuccess(() -> Component.literal(GRAY + "/translate whitelist remove <player> " + GRAY + "- Remove player"), false);
        ctx.getSource().sendSuccess(() -> Component.literal(GRAY + "/translate whitelist list " + GRAY + "- Show whitelist"), false);
        ctx.getSource().sendSuccess(() -> Component.literal(GRAY + "/translate whitelist clear " + GRAY + "- Clear whitelist"), false);
        ctx.getSource().sendSuccess(() -> Component.literal(GRAY + "Note: Empty = all players translated"), false);
        return 1;
    }
}
