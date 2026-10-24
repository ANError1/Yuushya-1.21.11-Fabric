package com.yuushya;

import com.yuushya.collision.CollisionFileReader;
import com.yuushya.registries.YuushyaRegistries;
import com.yuushya.registries.YuushyaRegistryConfig;
import com.yuushya.utils.AddonLoader;
import com.yuushya.utils.YuushyaLogger;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.platform.Platform;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;


public class Yuushya {
    public static final String MOD_ID = "yuushya";

    public static void init() {
        AddonLoader.loadResource(MOD_ID,Yuushya.class);
        AddonLoader.loadPackResource(Platform.getModsFolder());
//        ReloadListenerRegistry.register(PackType.SERVER_DATA,new CollisionFileReadReloadListener());
        //YuushyaRegistryConfig.readRegistryConfig();
//        YuushyaRegistryConfig.readRegistrySelf();
        AddonLoader.getRegister();
        YuushyaRegistries.registerRegistries();
        YuushyaRegistries.registerAll();

        //CollisionFileReader.readAllFileFromConfig();
        AddonLoader.getCollision();
//        CollisionFileReader.readAllFileSelf();
//        CollisionNetWorkChannel.register();

        LifecycleEvent.SERVER_STARTED.register((server)->{ //server thread
            //CollisionFileReader.readAllFileFromConfig();
            AddonLoader.getCollision();
//            CollisionFileReader.readAllFileSelf();
            CollisionFileReader.readAllCollision();
        });
//        LifecycleEvent.SERVER_STOPPED.register((server)->{
//            YuushyaBlockFactory.getYuushyaVoxelShapes().clear();
//        });

        PlayerEvent.PLAYER_JOIN.register((player) -> {
            MutableComponent message = Component.literal("");

            message.append(Component.translatable("declaration.title")
                    .withStyle(style -> style.withColor(ChatFormatting.GOLD).withBold(true)));

            message.append(Component.translatable("declaration.line1")
                    .withStyle(ChatFormatting.WHITE));

            message.append(Component.translatable("declaration.link_text")
                    .withStyle(style -> style.withColor(ChatFormatting.BLUE)
                            .withUnderlined(true)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://yuushya.com/townscape"))));

            message.append(Component.translatable("declaration.line1_end")
                    .withStyle(ChatFormatting.WHITE));

            message.append(Component.translatable("declaration.warning")
                    .withStyle(ChatFormatting.RED));

            message.append(Component.translatable("declaration.suggestion_title")
                    .withStyle(style -> style.withColor(ChatFormatting.YELLOW).withBold(true)));

            message.append(Component.translatable("declaration.suggestion1")
                    .withStyle(ChatFormatting.GREEN));
            message.append(Component.translatable("declaration.suggestion2")
                    .withStyle(ChatFormatting.GREEN));
            message.append(Component.translatable("declaration.suggestion3")
                    .withStyle(ChatFormatting.GREEN));

            message.append(Component.translatable("declaration.thanks")
                    .withStyle(ChatFormatting.AQUA));

            player.sendSystemMessage(message);
        });

        YuushyaLogger.info("Yuushya has been loaded successfully!");


    }
}
