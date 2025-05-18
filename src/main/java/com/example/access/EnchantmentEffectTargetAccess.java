package com.example.access;

import net.minecraft.enchantment.effect.EnchantmentEffectTarget;

public interface EnchantmentEffectTargetAccess {
    static EnchantmentEffectTarget getHolder() {
        return EnchantmentEffectTarget.valueOf("HOLDER");
    }

    static EnchantmentEffectTarget getWearer() {
        return EnchantmentEffectTarget.valueOf("WEARER");
    }
}
