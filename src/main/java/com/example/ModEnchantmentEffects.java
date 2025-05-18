package com.example;

import com.example.enchantment.effect.FlightEnchantmentEffect;
import com.example.enchantment.effect.LightningEnchantmentEffect;
import com.example.enchantment.effect.WatcherEnchantmentEffect;
import com.mojang.serialization.MapCodec;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModEnchantmentEffects {
    public static final RegistryKey<Enchantment> THUNDERING = of("thundering");
    public static MapCodec<LightningEnchantmentEffect> LIGHTNING_EFFECT = register("lightning_effect", LightningEnchantmentEffect.CODEC);

    public static final RegistryKey<Enchantment> FLYING = of("flying");

    // 新增：Flying 效果的 MapCodec
    // 我们将创建一个新的效果类: FlightEnchantmentEffect
    public static MapCodec<FlightEnchantmentEffect> FLIGHT_EFFECT = register("flight_effect", FlightEnchantmentEffect.CODEC);

    public static final RegistryKey<Enchantment> WATCHER = of("watcher");
    public static MapCodec<WatcherEnchantmentEffect> WATCHER_EFFECT = register("watcher_effect", WatcherEnchantmentEffect.CODEC);
    //以下就是固定格式
    private static RegistryKey<Enchantment> of(String path) {
        Identifier id = Identifier.of(TemplateMod.MOD_ID, path);
        return RegistryKey.of(RegistryKeys.ENCHANTMENT, id);
    }

    private static <T extends EnchantmentEntityEffect> MapCodec<T> register(String id, MapCodec<T> codec) {
        return Registry.register(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, Identifier.of(TemplateMod.MOD_ID, id), codec);
    }

    public static void registerModEnchantmentEffects() {

        TemplateMod.LOGGER.info("Registering EnchantmentEffects for" + TemplateMod.MOD_ID);
    }

    public static void initialize() {
    }
}