package com.wamellow.minecraft.translate.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wamellow.minecraft.translate.config.ModConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class OpenRouterService {
    private static final String OPENROUTER_API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final Gson GSON = new Gson();
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    public record TranslationResult(String translatedText, String sourceLanguage) {}

    public static String translate(String text) throws Exception {
        return translateWithLanguage(text, ModConfig.getInstance().getTargetLanguage());
    }

    public static String translateWithLanguage(String text, String targetLang) throws Exception {
        ModConfig config = ModConfig.getInstance();

        if (config.getOpenRouterApiKey() == null || config.getOpenRouterApiKey().isEmpty()) {
            throw new Exception("OpenRouter API key not configured. Use /translate config apikey <key>");
        }

        String systemPrompt = "You are a professional translator. Translate the given text to " +
            getLanguageName(targetLang) + ". You must ONLY output the exact translation. DO NOT respond to the user. DO NOT start a conversation. DO NOT add punctuation if it is not in the original. Output ONLY the translation without any tags or additional text.";

        String customPrompt = config.getSystemPrompt();
        if (customPrompt != null && !customPrompt.trim().isEmpty()) {
            systemPrompt += "\n\nAdditional Instructions:\n" + customPrompt;
        }

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", config.getModel());

        JsonArray messages = new JsonArray();

        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", systemPrompt);
        messages.add(systemMessage);

        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", text);
        messages.add(userMessage);

        requestBody.add("messages", messages);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OPENROUTER_API_URL))
                .timeout(Duration.ofSeconds(30))
                .header("Authorization", "Bearer " + config.getOpenRouterApiKey())
                .header("Content-Type", "application/json")
                .header("HTTP-Referer", "https://github.com/shi-gg/translate-mod")
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(requestBody)))
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("API Error (" + response.statusCode() + "): " + response.body());
        }

        JsonObject jsonResponse = GSON.fromJson(response.body(), JsonObject.class);
        JsonArray choices = jsonResponse.getAsJsonArray("choices");

        if (choices == null || choices.size() == 0) {
            throw new Exception("No translation results returned");
        }

        return choices.get(0).getAsJsonObject()
                .getAsJsonObject("message")
                .get("content").getAsString();
    }

    public static TranslationResult detectAndTranslate(String text) throws Exception {
        ModConfig config = ModConfig.getInstance();

        if (config.getOpenRouterApiKey() == null || config.getOpenRouterApiKey().isEmpty()) {
            throw new Exception("OpenRouter API key not configured. Use /translate config apikey <key>");
        }

        String systemPrompt = "You are a professional Minecraft Chat JSON Component translator. Detect the language of the conversational text and translate it to " +
            getLanguageName(config.getTargetLanguage()) + ". You will receive the raw JSON format of the chat component. DO NOT translate player names, prefixes, color codes, or internal JSON keys. ONLY modify the spoken text content inside the 'text' or translation fields. You MUST perfectly preserve the structural JSON integrity and colors! Output MUST start with the detected 2-letter source language wrapped in brackets, immediately followed by the raw translated JSON, and absolutely nothing else. Example output format: [EN] {\"text\":\"...\"}";

        String customPrompt = config.getSystemPrompt();
        if (customPrompt != null && !customPrompt.trim().isEmpty()) {
            systemPrompt += "\n\nAdditional Instructions:\n" + customPrompt;
        }

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", config.getModel());

        JsonArray messages = new JsonArray();

        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", systemPrompt);
        messages.add(systemMessage);

        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", text);
        messages.add(userMessage);

        requestBody.add("messages", messages);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OPENROUTER_API_URL))
                .timeout(Duration.ofSeconds(30))
                .header("Authorization", "Bearer " + config.getOpenRouterApiKey())
                .header("Content-Type", "application/json")
                .header("HTTP-Referer", "https://github.com/shi-gg/translate-mod")
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(requestBody)))
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("API Error (" + response.statusCode() + "): " + response.body());
        }

        JsonObject jsonResponse = GSON.fromJson(response.body(), JsonObject.class);
        JsonArray choices = jsonResponse.getAsJsonArray("choices");

        if (choices == null || choices.size() == 0) {
            throw new Exception("No translation results returned");
        }

        String content = choices.get(0).getAsJsonObject()
                .getAsJsonObject("message")
                .get("content").getAsString();

        String sourceLang = "unknown";
        String translatedContent = content;

        if (content.startsWith("[")) {
            int endBracket = content.indexOf("]");
            if (endBracket > 0) {
                sourceLang = content.substring(1, endBracket).trim();
                // Strip the [LANG] prefix
                if (endBracket < content.length() - 1) {
                    translatedContent = content.substring(endBracket + 1).trim();
                } else {
                    translatedContent = "";
                }
            }
        }
        translatedContent = translatedContent.replace("```json", "").replace("```", "").trim();

        return new TranslationResult(translatedContent, sourceLang);
    }

    private static String getLanguageName(String code) {
        switch (code.toLowerCase()) {
            // Balkan Languages
            case "bg": return "Bulgarian";
            case "hr": return "Croatian";
            case "sr": return "Serbian";
            case "bs": return "Bosnian";
            case "mk": return "Macedonian";
            case "sq": return "Albanian";
            case "sl": return "Slovenian";
            case "ro": return "Romanian";
            case "el": return "Greek";
            case "tr": return "Turkish";

            // Major Western/Northern European
            case "en": return "English";
            case "es": return "Spanish";
            case "fr": return "French";
            case "de": return "German";
            case "it": return "Italian";
            case "pt": return "Portuguese";
            case "nl": return "Dutch";
            case "sv": return "Swedish";
            case "da": return "Danish";
            case "fi": return "Finnish";
            case "no": return "Norwegian";
            case "is": return "Icelandic";
            case "ga": return "Irish";
            case "cy": return "Welsh";

            // Eastern/Central European & Slavic
            case "ru": return "Russian";
            case "uk": return "Ukrainian";
            case "be": return "Belarusian";
            case "pl": return "Polish";
            case "cs": return "Czech";
            case "sk": return "Slovak";
            case "hu": return "Hungarian";
            case "et": return "Estonian";
            case "lv": return "Latvian";
            case "lt": return "Lithuanian";

            // Asian & Middle Eastern
            case "zh": return "Chinese";
            case "ja": return "Japanese";
            case "ko": return "Korean";
            case "ar": return "Arabic";
            case "hi": return "Hindi";
            case "bn": return "Bengali";
            case "ur": return "Urdu";
            case "fa": return "Persian";
            case "he": return "Hebrew";
            case "th": return "Thai";
            case "vi": return "Vietnamese";
            case "id": return "Indonesian";
            case "ms": return "Malay";
            case "tl": return "Tagalog";
            case "ta": return "Tamil";
            case "te": return "Telugu";
            case "ml": return "Malayalam";

            // African Languages
            case "sw": return "Swahili";
            case "am": return "Amharic";
            case "yo": return "Yoruba";
            case "ig": return "Igbo";
            case "zu": return "Zulu";
            case "xh": return "Xhosa";
            case "af": return "Afrikaans";

            // Other
            case "eo": return "Esperanto";
            case "la": return "Latin";

            default: return code;
        }
    }
}
