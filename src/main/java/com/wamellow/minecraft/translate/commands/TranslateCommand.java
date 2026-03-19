package com.wamellow.minecraft.translate.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.wamellow.minecraft.translate.config.ModConfig;
import com.wamellow.minecraft.translate.service.OpenRouterService;

import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class TranslateCommand {
    private static final String GREEN = "§a";
    private static final String YELLOW = "§e";
    private static final String RED = "§c";
    private static final String GRAY = "§7";
    private static final String AQUA = "§b";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("translate")
            .then(Commands.literal("me")
                .then(Commands.argument("message", StringArgumentType.greedyString())
                    .executes(TranslateCommand::translateMessage)))
            .then(Commands.literal("lang")
                .executes(TranslateCommand::showCurrentLang))
            .then(Commands.literal("status")
                .executes(TranslateCommand::showStatus)));
    }

    private static int translateMessage(CommandContext<CommandSourceStack> ctx) {
        String message = StringArgumentType.getString(ctx, "message");

        if (ModConfig.getInstance().getOpenRouterApiKey() == null || ModConfig.getInstance().getOpenRouterApiKey().isEmpty()) {
            ctx.getSource().sendFailure(Component.literal(RED + "API key not set! Use " + GRAY + "/translate config apikey <key>"));
            return 0;
        }

        new Thread(() -> {
            try {
                String translated = OpenRouterService.translate(message);
                Minecraft instance = Minecraft.getInstance();

                instance.execute(() -> {
                    if (instance.getConnection() == null) return;
                    instance.getConnection().sendChat(translated);
                });
            } catch (Exception e) {
                ctx.getSource().sendFailure(Component.literal(RED + "Translation failed: " + e.getMessage()));
            }
        }).start();

        return 1;
    }

    private static int showCurrentLang(CommandContext<CommandSourceStack> ctx) {
        String lang = ModConfig.getInstance().getTargetLanguage();
        ctx.getSource().sendSuccess(() -> Component.literal(GRAY + "Target language: " + AQUA + lang), false);
        return 1;
    }

    private static int showStatus(CommandContext<CommandSourceStack> ctx) {
        ModConfig config = ModConfig.getInstance();

        String apiKeyStatus = config.getOpenRouterApiKey().isEmpty() ? RED + "Not set" : GREEN + "Set";
        ctx.getSource().sendSuccess(() -> Component.literal(GRAY + "API Key: " + apiKeyStatus), false);

        ctx.getSource().sendSuccess(() -> Component.literal(GRAY + "Model: " + YELLOW + config.getModel()), false);
        ctx.getSource().sendSuccess(() -> Component.literal(GRAY + "Target: " + AQUA + config.getTargetLanguage()), false);

        String autoStatus = config.isAutoTranslate() ? GREEN + "Enabled" : RED + "Disabled";
        ctx.getSource().sendSuccess(() -> Component.literal(GRAY + "Auto-translate: " + autoStatus), false);

        String indicatorStatus = config.isShowTranslationIndicator() ? GREEN + "Enabled" : RED + "Disabled";
        ctx.getSource().sendSuccess(() -> Component.literal(GRAY + "Indicator: " + indicatorStatus), false);

        int whitelistSize = config.getWhitelist().size();
        String whitelistText = whitelistSize == 0 ? GRAY + "(empty - all)" : AQUA + whitelistSize + " players";
        ctx.getSource().sendSuccess(() -> Component.literal(GRAY + "Whitelist: " + whitelistText), false);

        return 1;
    }
}
