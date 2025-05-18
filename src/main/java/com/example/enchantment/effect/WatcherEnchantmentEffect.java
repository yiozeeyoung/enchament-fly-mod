package com.example.enchantment.effect;

import com.mojang.serialization.MapCodec;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public record WatcherEnchantmentEffect() implements EnchantmentEntityEffect {

    // 由于此效果目前没有从JSON中读取的参数，我们使用 MapCodec.unit()
    // 它会调用 FlightEnchantmentEffect 的无参构造函数。
    public static final MapCodec<WatcherEnchantmentEffect> CODEC = MapCodec.unit(WatcherEnchantmentEffect::new);

    /**
     * 当此附魔效果被激活时调用。
     * 如果在附魔的JSON定义中通过 "tick" 触发器调用，此方法将在每个游戏刻执行。
     *
     * @param world   服务器世界
     * @param level   附魔等级 (此效果目前未使用)
     * @param context 附魔效果的上下文
     * @param target  效果的目标实体 (在这里应该是穿戴附魔物品的玩家)
     * @param pos     目标实体的位置 (此效果目前未使用具体位置，但遵循接口)
     */
    @Override
    public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity target, Vec3d pos) {
        // 确认目标是玩家实体
        if (target instanceof PlayerEntity player) {
            // 仅当玩家处于生存模式或冒险模式时才授予飞行能力
            // (创造模式和观察者模式默认可以飞行)
            if (!player.isCreative() && !player.isSpectator()) {
                // 如果玩家当前不允许飞行，则启用它并通知客户端
                if (!player.getAbilities().allowFlying) {
                    player.getAbilities().allowFlying = true;
                    player.sendAbilitiesUpdate(); // 非常重要，用于同步客户端玩家的能力
                }
            }
        }
    }

    @Override
    public MapCodec<WatcherEnchantmentEffect> getCodec() { // 注意：这里返回具体的类型 MapCodec<FlightEnchantmentEffect>
        return CODEC;
    }
}
