package com.yuushya.block.blockstate;

import net.minecraft.util.StringRepresentable;

public enum HalfSlabState implements StringRepresentable {
    TOP("top"),
    BOTH("both"),
    BOTTOM("bottom"),
    NONE("none");
    private final String name;
    HalfSlabState(String name) {
        this.name = name;
    }
    public String getSerializedName() {
        return this.name;
    }
}
