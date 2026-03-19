package com.wamellow.minecraft.translate.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static ModConfig INSTANCE;

    private String openRouterApiKey = "";
    private String systemPrompt = "";
    private String model = "openai/gpt-oss-120b";
    private Set<String> whitelist = new HashSet<>();
    private String targetLanguage = "en";
    private boolean showTranslationIndicator = true;
    private boolean autoTranslate = true;

    public static synchronized ModConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = load();
        }
        return INSTANCE;
    }

    public static synchronized void save() {
        if (INSTANCE == null) return;

        try {
            Path configPath = getConfigPath();
            Files.createDirectories(configPath.getParent());
            try (FileWriter writer = new FileWriter(configPath.toFile())) {
                GSON.toJson(INSTANCE, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static synchronized ModConfig load() {
        Path configPath = getConfigPath();
        File configFile = configPath.toFile();

        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                Type type = new TypeToken<ModConfig>() {}.getType();
                ModConfig loaded = GSON.fromJson(reader, type);
                if (loaded != null) {
                    return loaded;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return new ModConfig();
    }

    private static Path getConfigPath() {
        try {
            Minecraft instance = Minecraft.getInstance();
            if (instance != null && instance.gameDirectory != null) {
                return instance.gameDirectory.toPath().resolve("config").resolve("chat_translate.json");
            }
        } catch (Throwable ignored) {
        }
        return Path.of("config", "chat_translate.json");
    }

    public synchronized String getOpenRouterApiKey() {
        return openRouterApiKey;
    }

    public synchronized void setOpenRouterApiKey(String openRouterApiKey) {
        this.openRouterApiKey = openRouterApiKey;
    }

    public synchronized String getSystemPrompt() {
        return systemPrompt.replace("{TARGET_LANGUAGE}", getTargetLanguage());
    }

    public synchronized void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }

    public synchronized String getModel() {
        return model;
    }

    public synchronized void setModel(String model) {
        this.model = model;
    }

    public synchronized Set<String> getWhitelist() {
        return whitelist;
    }

    public synchronized void setWhitelist(Set<String> whitelist) {
        this.whitelist = whitelist;
    }

    public synchronized void addToWhitelist(String player) {
        this.whitelist.add(player.toLowerCase());
    }

    public synchronized void removeFromWhitelist(String player) {
        this.whitelist.remove(player.toLowerCase());
    }



    public synchronized String getTargetLanguage() {
        return targetLanguage;
    }

    public synchronized void setTargetLanguage(String targetLanguage) {
        this.targetLanguage = targetLanguage;
    }

    public synchronized boolean isShowTranslationIndicator() {
        return showTranslationIndicator;
    }

    public synchronized void setShowTranslationIndicator(boolean showTranslationIndicator) {
        this.showTranslationIndicator = showTranslationIndicator;
    }

    public synchronized boolean isAutoTranslate() {
        return autoTranslate;
    }

    public synchronized void setAutoTranslate(boolean autoTranslate) {
        this.autoTranslate = autoTranslate;
    }
}
