package com.wamellow.minecraft.translate.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.wamellow.minecraft.translate.config.ModConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class ConfigCommand {
    private static final String GREEN = "§a";
    private static final String YELLOW = "§e";
    private static final String RED = "§c";
    private static final String GRAY = "§7";
    private static final String AQUA = "§b";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("translate")
            .then(Commands.literal("config")
                .then(Commands.literal("apikey")
                    .then(Commands.argument("key", StringArgumentType.greedyString())
                        .executes(ConfigCommand::setApiKey))
                    .executes(ConfigCommand::showApiKeyStatus))
                .then(Commands.literal("prompt")
                    .then(Commands.argument("prompt", StringArgumentType.greedyString())
                        .executes(ConfigCommand::setPrompt))
                    .executes(ConfigCommand::showCurrentPrompt))
                .then(Commands.literal("model")
                    .then(Commands.argument("model", StringArgumentType.greedyString())
                        .executes(ConfigCommand::setModel))
                    .executes(ConfigCommand::showCurrentModel))
                .then(Commands.literal("targetlang")
                    .then(Commands.argument("language", StringArgumentType.string())
                        .executes(ConfigCommand::setTargetLang))
                    .executes(ConfigCommand::showCurrentTargetLang))
                .then(Commands.literal("autotranslate")
                    .then(Commands.argument("enabled", StringArgumentType.word())
                        .executes(ConfigCommand::setAutoTranslate))
                    .executes(ConfigCommand::showAutoTranslateStatus))
                .then(Commands.literal("showindicator")
                    .then(Commands.argument("enabled", StringArgumentType.word())
                        .executes(ConfigCommand::setShowIndicator))
                    .executes(ConfigCommand::showIndicatorStatus))
                .executes(ConfigCommand::showConfigHelp)));
    }

    private static int setApiKey(CommandContext<CommandSourceStack> ctx) {
        String apiKey = StringArgumentType.getString(ctx, "key");
        ModConfig.getInstance().setOpenRouterApiKey(apiKey);
        ModConfig.save();
        ctx.getSource().sendSuccess(() -> Component.literal(GREEN + "✓ OpenRouter API key set successfully!"), true);
        return 1;
    }

    private static int showApiKeyStatus(CommandContext<CommandSourceStack> ctx) {
        String apiKey = ModConfig.getInstance().getOpenRouterApiKey();
        if (apiKey == null || apiKey.isEmpty()) {
            ctx.getSource().sendSuccess(() -> Component.literal(GRAY + "API Key: " + RED + "Not set " + GRAY + "(use /translate config apikey <key>)"), false);
        } else {
            ctx.getSource().sendSuccess(() -> Component.literal(GRAY + "API Key: " + GREEN + "Set (hidden for security)"), false);
        }
        return 1;
    }

    private static int setPrompt(CommandContext<CommandSourceStack> ctx) {
        String prompt = StringArgumentType.getString(ctx, "prompt");
        ModConfig.getInstance().setSystemPrompt(prompt);
        ModConfig.save();
        ctx.getSource().sendSuccess(() -> Component.literal(GREEN + "✓ System prompt updated!"), true);
        return 1;
    }

    private static int showCurrentPrompt(CommandContext<CommandSourceStack> ctx) {
        String prompt = ModConfig.getInstance().getSystemPrompt();
        ctx.getSource().sendSuccess(() -> Component.literal(GRAY + "Current system prompt: " + AQUA + prompt), false);
        return 1;
    }

    private static int setModel(CommandContext<CommandSourceStack> ctx) {
        String model = StringArgumentType.getString(ctx, "model");
        ModConfig.getInstance().setModel(model);
        ModConfig.save();
        ctx.getSource().sendSuccess(() -> Component.literal(GREEN + "✓ Model set to: " + YELLOW + model), true);
        return 1;
    }

    private static int showCurrentModel(CommandContext<CommandSourceStack> ctx) {
        String model = ModConfig.getInstance().getModel();
        ctx.getSource().sendSuccess(() -> Component.literal(GRAY + "Model: " + YELLOW + model), false);
        return 1;
    }

    private static int setTargetLang(CommandContext<CommandSourceStack> ctx) {
        String lang = StringArgumentType.getString(ctx, "language");
        ModConfig.getInstance().setTargetLanguage(lang);
        ModConfig.save();
        ctx.getSource().sendSuccess(() -> Component.literal(GREEN + "✓ Target language set to: " + AQUA + lang), true);
        return 1;
    }

    private static int showCurrentTargetLang(CommandContext<CommandSourceStack> ctx) {
        String lang = ModConfig.getInstance().getTargetLanguage();
        ctx.getSource().sendSuccess(() -> Component.literal(GRAY + "Target language: " + AQUA + lang), false);
        return 1;
    }

    private static int setAutoTranslate(CommandContext<CommandSourceStack> ctx) {
        String enabled = StringArgumentType.getString(ctx, "enabled");
        boolean isEnabled = enabled.equalsIgnoreCase("true") || enabled.equalsIgnoreCase("on") || enabled.equalsIgnoreCase("1");
        ModConfig.getInstance().setAutoTranslate(isEnabled);
        ModConfig.save();
        String status = isEnabled ? GREEN + "Enabled" : RED + "Disabled";
        ctx.getSource().sendSuccess(() -> Component.literal(GREEN + "✓ Auto-translate " + status), true);
        return 1;
    }

    private static int showAutoTranslateStatus(CommandContext<CommandSourceStack> ctx) {
        boolean enabled = ModConfig.getInstance().isAutoTranslate();
        String status = enabled ? GREEN + "Enabled" : RED + "Disabled";
        ctx.getSource().sendSuccess(() -> Component.literal(GRAY + "Auto-translate: " + status), false);
        return 1;
    }

    private static int setShowIndicator(CommandContext<CommandSourceStack> ctx) {
        String enabled = StringArgumentType.getString(ctx, "enabled");
        boolean isEnabled = enabled.equalsIgnoreCase("true") || enabled.equalsIgnoreCase("on") || enabled.equalsIgnoreCase("1");
        ModConfig.getInstance().setShowTranslationIndicator(isEnabled);
        ModConfig.save();
        String status = isEnabled ? GREEN + "Enabled" : RED + "Disabled";
        ctx.getSource().sendSuccess(() -> Component.literal(GREEN + "✓ Translation indicator " + status), true);
        return 1;
    }

    private static int showIndicatorStatus(CommandContext<CommandSourceStack> ctx) {
        boolean enabled = ModConfig.getInstance().isShowTranslationIndicator();
        String status = enabled ? GREEN + "Enabled" : RED + "Disabled";
        ctx.getSource().sendSuccess(() -> Component.literal(GRAY + "Show indicator: " + status), false);
        return 1;
    }

    private static int showConfigHelp(CommandContext<CommandSourceStack> ctx) {
        ctx.getSource().sendSuccess(() -> Component.literal(GRAY + "/translate config apikey <key> " + GRAY + "- Set API key"), false);
        ctx.getSource().sendSuccess(() -> Component.literal(GRAY + "/translate config prompt <text> " + GRAY + "- Set prompt"), false);
        ctx.getSource().sendSuccess(() -> Component.literal(GRAY + "/translate config model <model> " + GRAY + "- Set AI model"), false);
        ctx.getSource().sendSuccess(() -> Component.literal(GRAY + "/translate config targetlang <lang> " + GRAY + "- Set language"), false);
        ctx.getSource().sendSuccess(() -> Component.literal(GRAY + "/translate config autotranslate <t/f> " + GRAY + "- Toggle"), false);
        ctx.getSource().sendSuccess(() -> Component.literal(GRAY + "/translate config showindicator <t/f> " + GRAY + "- Toggle"), false);
        return 1;
    }
}
