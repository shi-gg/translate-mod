package com.wamellow.minecraft.translate;

import com.wamellow.minecraft.translate.commands.ConfigCommand;
import com.wamellow.minecraft.translate.commands.TranslateCommand;
import com.wamellow.minecraft.translate.commands.WhitelistCommand;
import com.wamellow.minecraft.translate.config.ModConfig;
import com.wamellow.minecraft.translate.event.ChatEventHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Mod("chat_translate")
@EventBusSubscriber(modid = TranslateMod.MODID, bus = EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class TranslateMod {
    public static final String MODID = "chat_translate";
    private static TranslateMod instance;
    public static final ExecutorService executor = Executors.newCachedThreadPool();

    public TranslateMod() {
        instance = this;
        ModConfig.getInstance();

        MinecraftForge.EVENT_BUS.register(new ChatEventHandler());
        System.out.println("[Translate] Mod initialized!");
    }

    public static TranslateMod getInstance() {
        return instance;
    }

    public static ExecutorService getExecutor() {
        return executor;
    }

    @SubscribeEvent
    public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        System.out.println("[Translate] Registering commands!");

        ConfigCommand.register(event.getDispatcher());
        WhitelistCommand.register(event.getDispatcher());
        TranslateCommand.register(event.getDispatcher());

        System.out.println("[Translate] Commands registered!");
    }
}
