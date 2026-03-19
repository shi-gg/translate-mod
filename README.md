[![](https://img.shields.io/discord/828676951023550495?color=5865F2&logo=discord&logoColor=white)](https://discord.com/invite/yYd6YKHQZH)
![](https://img.shields.io/github/repo-size/shi-gg/translate-mod?maxAge=3600)

[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/I3I6AFVAP)

**⚠️ In development, breaking changes ⚠️**

## About

A Minecraft Forge 1.20.1 mod that translates chat messages using OpenRouter AI models.

## Features

- Automatic translation of chat messages from other players
- Configurable AI system prompt
- Player whitelist (players who won't be translated)
- Translation indicator showing source language
- Translate your own messages before sending
- Client-side only

## Building

### Prerequisites

- Java 17
- Gradle 8.5

### Build Commands

```bash
# Build the mod
./gradlew build

# The output jar will be in build/libs/
```

### Using with Modrinth/Forge Profile

If you're using the Modrinth Launcher with Forge 1.20.1 profile at:
`$HOME/.var/app/com.modrinth.ModrinthApp/data/ModrinthApp/profiles/Forge 1.20.1`

Copy the built jar to your mods folder:
```bash
cp build/libs/chat_translate-1.0.0.jar $HOME/.var/app/com.modrinth.ModrinthApp/data/ModrinthApp/profiles/Forge\ 1.20.1/mods/
```

## Configuration

The config file is located at: `.minecraft/config/chat_translate.json`

### Getting an API Key

1. Go to https://openrouter.ai/settings
2. Create a free* account
3. Get your API key

*Free if you use models suffixed with `:free`. Even when using free models, add 10$ or more to your account to increase ratelimits.

## Commands

All commands require OP permissions (level 2).

### Configuration

```bash
# Set your OpenRouter API key
/translate config apikey <your-api-key>

# Set custom system prompt for AI
/translate config prompt <your-prompt>

# Set AI model (default: openai/gpt-oss-120b)
/translate config model <model-name>

# Set target language (default: en)
/translate config targetlang <language-code>

# Toggle auto-translate
/translate config autotranslate <true/false>

# Toggle translation indicator
/translate config showindicator <true/false>
```

### Whitelist

```bash
# Add player to whitelist (only whitelisted players will be translated)
# Note: If the whitelist is completely empty, it defaults to translating EVERYONE.
/translate whitelist add <player>

# Remove player from whitelist
/translate whitelist remove <player>

# Show whitelist
/translate whitelist list

# Clear whitelist (reverts to translating everyone)
/translate whitelist clear
```

### Other

```bash
# Translate your message before sending
/translate me <message>

# Show current target language
/translate lang

# Show mod status
/translate status
```

## Supported Languages

The mod natively supports translation to and from dozens of languages worldwide. It relies on ISO 639-1 language codes.

**Some Supported Languages:**
- **Balkan:** Bulgarian (bg), Croatian (hr), Serbian (sr), Bosnian (bs), Macedonian (mk), Albanian (sq), Slovenian (sl), Romanian (ro), Greek (el), Turkish (tr)
- **European:** English (en), Spanish (es), French (fr), German (de), Italian (it), Portuguese (pt), Dutch (nl), Russian (ru), Ukrainian (uk), Polish (pl), and more...
- **Asian:** Chinese (zh), Japanese (ja), Korean (ko), Hindi (hi), Arabic (ar), Thai (th), Vietnamese (vi), Indonesian (id), and more...
- **African & Other:** Swahili (sw), Afrikaans (af), Esperanto (eo), Latin (la), and many more!

## System Prompt

The default system prompt has been optimized for reliability across servers with custom chat formatting plugins:
```
You are a language detector and translator. Detect the language of the following text and translate it to {TARGET_LANGUAGE}. Output ONLY the translation in the format: "[SOURCE_LANGUAGE] Translation" where SOURCE_LANGUAGE is the 2-letter language code of the detected source language. Keep any player names, prefixes, tags, or emojis intact exactly as they appear in the original text (e.g. <Player> or [VIP] Player >>). Nothing else.
```

You can customize this with `/translate config prompt` to change how the AI responds.

## License

MIT
