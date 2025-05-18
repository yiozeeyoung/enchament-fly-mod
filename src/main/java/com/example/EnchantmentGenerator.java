package com.example;

import com.example.access.EnchantmentEffectTargetAccess;
import com.example.enchantment.effect.FlightEnchantmentEffect;
import com.example.enchantment.effect.LightningEnchantmentEffect;
import com.example.enchantment.effect.WatcherEnchantmentEffect;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.effect.EnchantmentEffectTarget;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public class EnchantmentGenerator extends FabricDynamicRegistryProvider {
    public EnchantmentGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
        System.out.println("REGISTERING ENCHANTS");
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        //Thundering enchantment
        register(entries, ModEnchantmentEffects.THUNDERING, Enchantment.builder(
                                Enchantment.definition(
                                        registries.getWrapperOrThrow(RegistryKeys.ITEM).getOrThrow(ItemTags.WEAPON_ENCHANTABLE),
                                        // this is the "weight" or probability of our enchantment showing up in the table
                                        10,
                                        // the maximum level of the enchantment
                                        3,
                                        // base cost for level 1 of the enchantment, and min levels required for something higher
                                        Enchantment.leveledCost(1, 10),
                                        // same fields as above but for max cost
                                        Enchantment.leveledCost(1, 15),
                                        // anvil cost
                                        5,
                                        // valid slots
                                        AttributeModifierSlot.HAND
                                )
                        )
                        .addEffect(
                                // enchantment occurs POST_ATTACK
                                EnchantmentEffectComponentTypes.POST_ATTACK,
                                EnchantmentEffectTarget.ATTACKER,
                                EnchantmentEffectTarget.VICTIM,
                                new LightningEnchantmentEffect(EnchantmentLevelBasedValue.linear(0.4f, 0.2f)) // scale the enchantment linearly.
                        )
        );

        // Flying enchantment
        register(entries, ModEnchantmentEffects.FLYING, Enchantment.builder(
                                Enchantment.definition(
                                        // Supported items: All armor enchantable items
                                        registries.getWrapperOrThrow(RegistryKeys.ITEM).getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                                        // Weight: Lower for rarity (e.g., 1 or 2)
                                        2,
                                        // Max level: 1 for an on/off ability
                                        1,
                                        // Min cost: Higher base cost
                                        Enchantment.leveledCost(25, 0),
                                        // Max cost: Higher base cost
                                        Enchantment.leveledCost(50, 0),
                                        // Anvil cost
                                        8,
                                        // Slots applicable: All armor slots
                                        AttributeModifierSlot.ARMOR
                                )
                        )
                .addEffect(
                        EnchantmentEffectComponentTypes.TICK,
                        new FlightEnchantmentEffect()
                )

        );
        // Wacther enchantment
        register(entries, ModEnchantmentEffects.WATCHER, Enchantment.builder(
                                Enchantment.definition(
                                        // Supported items: All armor enchantable items
                                        registries.getWrapperOrThrow(RegistryKeys.ITEM).getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                                        // Weight: Lower for rarity (e.g., 1 or 2)
                                        2,
                                        // Max level: 1 for an on/off ability
                                        1,
                                        // Min cost: Higher base cost
                                        Enchantment.leveledCost(25, 0),
                                        // Max cost: Higher base cost
                                        Enchantment.leveledCost(50, 0),
                                        // Anvil cost
                                        8,
                                        // Slots applicable: All armor slots
                                        AttributeModifierSlot.ARMOR
                                )
                        )
                        .addEffect(
                                EnchantmentEffectComponentTypes.TICK,
                                new WatcherEnchantmentEffect()
                        )

        );
    }

    private void register(FabricDynamicRegistryProvider.Entries entries, RegistryKey<Enchantment> key, Enchantment.Builder builder, ResourceCondition... resourceConditions) {
        entries.add(key, builder.build(key.getValue()), resourceConditions);
    }

    @Override
    public String getName() {
        return "ReferenceDocEnchantmentGenerator";
    }
}
