package com.yuushya.mixin;

import com.yuushya.YuushyaClient;
import com.yuushya.gui.CheckScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 替代 Architectury 的 {@code ClientGuiEvent.SET_SCREEN}：
 * 首次进入标题界面时替换为依赖检测界面。
 */
@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void yuushya$replaceTitleScreen(Screen screen, CallbackInfo ci) {
        if (screen instanceof TitleScreen && YuushyaClient.tryConsumeOpenFlag()) {
            ci.cancel();
            Minecraft self = (Minecraft) (Object) this;
            self.setScreen(new CheckScreen(screen));
        }
    }
}
