package com.example;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static Item register(Item item, String id) {
        // Create the identifier for the item.
        Identifier itemID = Identifier.of(TemplateMod.MOD_ID, id);

        // Register the item.
        Item registeredItem = Registry.register(Registries.ITEM, itemID, item);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(registeredItem);
        });

        // Return the registered item!
        return registeredItem;
    }
    public static final Item PURPLESTONE_POWDER = register(
            new Item(new Item.Settings()),
            "purplestone_powder"
    );
    public static final Item POISONOUS_APPLE = register(
            new Item(new Item.Settings().food(new FoodComponent.Builder().build())),
            "poisonous_apple"
    );

    public static void initialize() {
        // Get the event for modifying entries in the ingredients group.
// And register an event handler that adds our suspicious item to the ingredients group.
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
                .register((itemGroup) -> itemGroup.add(ModItems.PURPLESTONE_POWDER));
        // Add the suspicious substance to the composting registry with a 30% chance of increasing the composter's level.
        CompostingChanceRegistry.INSTANCE.add(ModItems.PURPLESTONE_POWDER, 0.3f);
        // Add the suspicious substance to the registry of fuels, with a burn time of 30 seconds.
// Remember, Minecraft deals with logical based-time using ticks.
// 20 ticks = 1 second.
        FuelRegistry.INSTANCE.add(ModItems.PURPLESTONE_POWDER, 300 * 20);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> {
            entries.add(POISONOUS_APPLE);
        });
    }
}