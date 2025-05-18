package com.example;

import com.example.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.brain.MemoryQuery;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


public class TemplateMod implements ModInitializer {
	public static final String MOD_ID = "template-mod";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// 控制旁观者模式是否可以交互物品栏的开关
	public static boolean allowSpectatorInventoryInteraction = false;

	// 定义键位绑定
	private static KeyBinding keyToggleSpectatorInventory;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		ModItems.initialize();
		ModEnchantmentEffects.initialize();
		LOGGER.info("Hello Fabric world!");
		try {
			LOGGER.info("HOLDER enum value: {}",
					com.example.access.EnchantmentEffectTargetAccess.getHolder());
			LOGGER.info("WEARER enum value: {}",
					com.example.access.EnchantmentEffectTargetAccess.getWearer());
		} catch (Exception e) {
			LOGGER.error("Failed to access custom enum values", e);
		}
		ModEnchantmentEffects.registerModEnchantmentEffects(); // 注册效果类型
		// 您还需要一个地方注册附魔本身（通过数据生成）

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (PlayerEntity player : server.getPlayerManager().getPlayerList()) {
				onPlayerTick(player);
				onPlayerWatcher(player);
			}
			// 其他服务器端Tick事件处理
		});

	}

	// 在方法开始处定义变量
	private void onPlayerTick(PlayerEntity player) {
		boolean hasFlyingEnchant = false;

		// 获取飞行附魔的注册表条目
		RegistryKey<Enchantment> flyingEnchantmentKey = ModEnchantmentEffects.FLYING;
		Optional<RegistryEntry<Enchantment>> flyingEnchantmentEntryOptional =
				player.getWorld().getRegistryManager()
						.getOptional(RegistryKeys.ENCHANTMENT)
						.flatMap(registry -> registry.getEntry(flyingEnchantmentKey));

		// 检查是否成功获取了附魔注册表条目
		if (flyingEnchantmentEntryOptional.isPresent()) {
			// 从Optional中获取实际的RegistryEntry对象
			RegistryEntry<Enchantment> flyingEnchantmentEntry = flyingEnchantmentEntryOptional.get();

			// 定义盔甲槽位数组
			EquipmentSlot[] armorSlots = {
					EquipmentSlot.HEAD,
					EquipmentSlot.CHEST,
					EquipmentSlot.LEGS,
					EquipmentSlot.FEET
			};

			// 遍历检查每个盔甲槽位
			for (EquipmentSlot slot : armorSlots) {
				ItemStack armorPiece = player.getEquippedStack(slot);

				// 使用 RegistryEntry 检查附魔等级
				if (!armorPiece.isEmpty() &&
						EnchantmentHelper.getLevel(flyingEnchantmentEntry, armorPiece) > 0) {
					hasFlyingEnchant = true;
					break;
				}
			}
		}

		// 根据附魔状态设置飞行能力
		if (hasFlyingEnchant) {
			// 允许玩家飞行
			if (!player.getAbilities().allowFlying) {
				player.getAbilities().allowFlying = true;
				player.getAbilities().flying = true;
				player.sendAbilitiesUpdate();
			}
		} else if (!player.isCreative() && !player.isSpectator()) {
			// 取消非创造模式玩家的飞行能力
			if (player.getAbilities().allowFlying) {
				player.getAbilities().allowFlying = false;
				player.getAbilities().flying = false;
				player.sendAbilitiesUpdate();
			}
		}
	}
	private void onPlayerWatcher(PlayerEntity player) {
		boolean hasFlyingEnchant = false;

		// 获取飞行附魔的注册表条目
		RegistryKey<Enchantment> watcherEnchantmentKey = ModEnchantmentEffects.WATCHER;
		Optional<RegistryEntry<Enchantment>> watcherEnchantmentEntryOptional =
				player.getWorld().getRegistryManager()
						.getOptional(RegistryKeys.ENCHANTMENT)
						.flatMap(registry -> registry.getEntry(watcherEnchantmentKey));

		// 检查是否成功获取了附魔注册表条目
		if (watcherEnchantmentEntryOptional.isPresent()) {
			// 从Optional中获取实际的RegistryEntry对象
			RegistryEntry<Enchantment> flyingEnchantmentEntry = watcherEnchantmentEntryOptional.get();

			// 定义盔甲槽位数组
			EquipmentSlot[] armorSlots = {
					EquipmentSlot.FEET
			};

			// 遍历检查每个盔甲槽位
			for (EquipmentSlot slot : armorSlots) {
				ItemStack armorPiece = player.getEquippedStack(slot);

				// 使用 RegistryEntry 检查附魔等级
				if (!armorPiece.isEmpty() &&
						EnchantmentHelper.getLevel(flyingEnchantmentEntry, armorPiece) > 0) {
					hasFlyingEnchant = true;
					break;
				}
			}
		}

		// 根据附魔状态设置飞行能力
		if (hasFlyingEnchant) {
			// 允许玩家进入旁观者模式
			if (!player.isSpectator() && player instanceof net.minecraft.server.network.ServerPlayerEntity serverPlayer) {
				serverPlayer.changeGameMode(net.minecraft.world.GameMode.SPECTATOR);
				player.sendMessage(net.minecraft.text.Text.literal("你已进入旁观者模式"), true);
			}
		} else {
			// 如果玩家当前是旁观者模式且不是创造模式玩家，则恢复为生存模式
			if (player.isSpectator() && !player.isCreative() && player instanceof net.minecraft.server.network.ServerPlayerEntity serverPlayer) {
				serverPlayer.changeGameMode(net.minecraft.world.GameMode.SURVIVAL);
				player.sendMessage(net.minecraft.text.Text.literal("你已恢复为生存模式"), true);
			}
		}
	}
}
