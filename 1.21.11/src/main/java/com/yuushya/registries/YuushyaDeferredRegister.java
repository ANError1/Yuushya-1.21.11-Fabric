package com.yuushya.registries;

import com.yuushya.Yuushya;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 纯 Fabric 的延迟注册表实现（替代 Architectury 的 DeferredRegister）。
 * <p>
 * Fabric 允许在 mod 初始化阶段直接注册，因此这里在 {@link #register} 时立即写入注册表，
 * 同时保留名称 -> 实例的映射，供模组内部按名字查询。
 */
public class YuushyaDeferredRegister<T> {

    private final Map<String, RegistrySupplier<T>> OBJECT_MAP = new LinkedHashMap<>();
    private final Registry<T> registry;

    @SuppressWarnings("unchecked")
    public YuushyaDeferredRegister(ResourceKey<Registry<T>> key) {
        Registry<?> found = BuiltInRegistries.REGISTRY.getValue(key.identifier());
        if (found == null) {
            throw new IllegalStateException("Unknown registry: " + key.identifier());
        }
        this.registry = (Registry<T>) found;
    }

    public <I extends T> RegistrySupplier<T> register(String name, Supplier<I> sup) {
        RegistrySupplier<T> existing = OBJECT_MAP.get(name);
        if (existing != null) {
            return existing;
        }
        Identifier id = Identifier.fromNamespaceAndPath(Yuushya.MOD_ID, name);
        T value = sup.get();
        Registry.register(registry, id, value);
        RegistrySupplier<T> supplier = new RegistrySupplier<>(id, value);
        OBJECT_MAP.put(name, supplier);
        return supplier;
    }

    @Nullable
    public T getValue(String name) {
        RegistrySupplier<T> supplier = OBJECT_MAP.get(name);
        return supplier == null ? null : supplier.get();
    }

    @Nullable
    public RegistrySupplier<T> get(String name) {
        return OBJECT_MAP.get(name);
    }

    @NotNull
    public T getInstanceOrDefault(String name, T defaultObject) {
        RegistrySupplier<T> supplier = get(name);
        return supplier == null ? defaultObject : supplier.get();
    }

    public boolean contains(String name) {
        return OBJECT_MAP.containsKey(name);
    }

    public Map<String, RegistrySupplier<T>> getMap() {
        return OBJECT_MAP;
    }
}
