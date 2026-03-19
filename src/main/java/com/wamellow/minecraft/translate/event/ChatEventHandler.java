package com.wamellow.minecraft.translate.event;

import com.wamellow.minecraft.translate.TranslateMod;
import com.wamellow.minecraft.translate.config.ModConfig;
import com.wamellow.minecraft.translate.service.OpenRouterService;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.ExecutorService;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ChatEventHandler {
    private static final ExecutorService executor = TranslateMod.getExecutor();

    private static final String GREEN = "§a";
    private static final String RESET = "§r";

    @SubscribeEvent
    public void onClientChatReceived(ClientChatReceivedEvent event) {
        if (event.getMessage() == null) return;

        Minecraft instance = Minecraft.getInstance();
        if (instance.level == null) return;

        ModConfig config = ModConfig.getInstance();
        if (!config.isAutoTranslate()) return;

        Component message = event.getMessage();
        String plainText = getPlainText(message);

        if (plainText == null || plainText.trim().isEmpty()) return;

        if (!config.getWhitelist().isEmpty()) {
            String lowerText = plainText.toLowerCase();
            boolean whitelisted = config.getWhitelist().stream().anyMatch(lowerText::contains);
            if (!whitelisted) return;
        }

        event.setCanceled(true);

        final String originalJson = Component.Serializer.toJson(message);

        executor.execute(() -> {
            try {
                OpenRouterService.TranslationResult result = OpenRouterService.detectAndTranslate(originalJson);
                String translatedJson = result.translatedText();
                String sourceLang = result.sourceLanguage();

                String indicator = config.isShowTranslationIndicator()
                    ? GREEN + "[" + sourceLang.toUpperCase() + "]" + RESET + " "
                    : "";

                Component translatedMsg = Component.Serializer.fromJson(translatedJson);
                if (translatedMsg == null) translatedMsg = message;

                MutableComponent finalMessage = Component.literal(indicator).append(translatedMsg);

                instance.execute(() -> {
                    instance.gui.getChat().addMessage(finalMessage);
                });
            } catch (Exception e) {
                instance.execute(() -> {
                    instance.gui.getChat().addMessage(message);
                    System.err.println("[Translate] Translation Error: " + e.getMessage());
                });
            }
        });
    }

    private static String getPlainText(Component component) {
        if (component == null) return "";
        return component.getString();
    }
}
