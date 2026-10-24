package com.yuushya;

import com.yuushya.collision.CollisionFileReader;
import com.yuushya.registries.YuushyaRegistries;
import com.yuushya.registries.YuushyaRegistryConfig;
import com.yuushya.utils.AddonLoader;
import com.yuushya.utils.YuushyaLogger;
import com.yuushya.item.AbstractToolItem;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;


public class Yuushya {
    public static final String MOD_ID = "yuushya";

    public static void init() {
        AddonLoader.loadResource(MOD_ID, Yuushya.class);
        AddonLoader.loadPackResource(YuushyaPlatform.getModsDirectory());
        AddonLoader.getRegister();
        if (YuushyaRegistryConfig.YuushyaRawBlockMap.isEmpty()) {
            YuushyaLogger.error("Yuushya: no register data was loaded from data/yuushya/register! "
                    + "Blocks and items will be missing.");
        }
        YuushyaRegistries.registerRegistries();
        YuushyaRegistries.registerAll();

        AddonLoader.getCollision();

        // 1.21.11 移除了 Item#canAttackBlock，工具的左键行为改用 Fabric 事件实现
        AttackBlockCallback.EVENT.register((player, level, hand, pos, direction) -> {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.getItem() instanceof AbstractToolItem tool) {
                return tool.inMainHandLeftClickOnBlock(player, level.getBlockState(pos), level, pos, stack);
            }
            return InteractionResult.PASS;
        });

        ServerLifecycleEvents.SERVER_STARTED.register((server) -> { // server thread
            AddonLoader.getCollision();
            CollisionFileReader.readAllCollision();
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            MutableComponent message = Component.literal("");

            message.append(Component.translatable("declaration.title")
                    .withStyle(style -> style.withColor(ChatFormatting.GOLD).withBold(true)));

            message.append(Component.translatable("declaration.line1")
                    .withStyle(ChatFormatting.WHITE));

            message.append(Component.translatable("declaration.link_text")
                    .withStyle(style -> style.withColor(ChatFormatting.BLUE)
                            .withUnderlined(true)
                            .withClickEvent(new ClickEvent.OpenUrl(java.net.URI.create("https://yuushya.com/townscape")))));

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

            handler.getPlayer().sendSystemMessage(message);
        });

        YuushyaLogger.info("Yuushya has been loaded successfully!");
    }
}
