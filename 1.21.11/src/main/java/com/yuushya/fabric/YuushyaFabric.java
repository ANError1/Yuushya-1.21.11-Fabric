package com.yuushya.fabric;

import com.yuushya.Yuushya;
import net.fabricmc.api.ModInitializer;

public class YuushyaFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Yuushya.init();
    }
}
