package com.yuushya.registries;

import net.minecraft.resources.Identifier;

import java.util.function.Supplier;

/**
 * 对应原先 Architectury 的 {@code RegistrySupplier}。
 * 由于本移植在注册时即持有实例，这里直接返回该实例。
 */
public class RegistrySupplier<T> implements Supplier<T> {

    private final Identifier id;
    private T value;

    public RegistrySupplier(Identifier id, T value) {
        this.id = id;
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }

    void setValue(T value) {
        this.value = value;
    }

    public Identifier getId() {
        return id;
    }
}
