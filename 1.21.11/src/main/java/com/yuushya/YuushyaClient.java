package com.yuushya;

import com.yuushya.collision.CollisionFileReader;
import com.yuushya.registries.YuushyaConfig;
import com.yuushya.registries.YuushyaRegistries;
import com.yuushya.utils.CheckFileUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

public class YuushyaClient {
    private static boolean openOnce = true;

    public static void onInitializeClient() {
        YuushyaConfig.CLIENT_CONFIG.read();
        YuushyaRegistries.registerClient();

        ClientLifecycleEvents.CLIENT_STARTED.register((client) -> { // client render thread
            CollisionFileReader.readAllCollision();
        });

        if (YuushyaConfig.CLIENT_CONFIG.check) {
            CheckFileUtils.loadInformation();
        }
    }

    /** 标题界面第一次出现时，用检测界面替换它（由 MixinMinecraft 调用）。 */
    public static boolean tryConsumeOpenFlag() {
        if (openOnce && YuushyaConfig.CLIENT_CONFIG.check) {
            openOnce = false;
            return true;
        }
        return false;
    }
}
