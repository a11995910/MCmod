package com.supercraft.component;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import com.supercraft.SuperCraftMod;

/**
 * 模组组件注册类
 * 用于注册自定义数据组件，替代传统的NBT数据存储
 * 适配Minecraft 1.21.x的新组件系统
 */
public class ModComponents {
	
	/**
	 * 装备强化等级组件
	 * 存储装备的强化等级（整数值）
	 */
	public static final ComponentType<Integer> ENHANCEMENT_LEVEL = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(SuperCraftMod.MOD_ID, "enhancement_level"),
			ComponentType.<Integer>builder().codec(Codec.INT).build()
	);
	
	/**
	 * 装备强化次数组件
	 * 记录装备被强化的总次数
	 */
	public static final ComponentType<Integer> ENHANCEMENT_COUNT = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(SuperCraftMod.MOD_ID, "enhancement_count"),
			ComponentType.<Integer>builder().codec(Codec.INT).build()
	);
	
	/**
	 * 装备强化属性加成组件
	 * 存储强化带来的属性加成值
	 */
	public static final ComponentType<Float> ENHANCEMENT_BONUS = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(SuperCraftMod.MOD_ID, "enhancement_bonus"),
			ComponentType.<Float>builder().codec(Codec.FLOAT).build()
	);
	
	/**
	 * 超级苹果效果组件
	 * 标记玩家是否拥有超级苹果的范围攻击效果
	 */
	public static final ComponentType<Boolean> SUPER_APPLE_EFFECT = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(SuperCraftMod.MOD_ID, "super_apple_effect"),
			ComponentType.<Boolean>builder().codec(Codec.BOOL).build()
	);
	
	/**
	 * 超级苹果攻击范围加成组件
	 * 存储超级苹果提供的攻击范围加成值
	 */
	public static final ComponentType<Integer> SUPER_APPLE_RANGE = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(SuperCraftMod.MOD_ID, "super_apple_range"),
			ComponentType.<Integer>builder().codec(Codec.INT).build()
	);
	
	/**
	 * 超级面包效果组件
	 * 标记玩家是否拥有超级面包的永久夜视效果
	 */
	public static final ComponentType<Boolean> SUPER_BREAD_EFFECT = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(SuperCraftMod.MOD_ID, "super_bread_effect"),
			ComponentType.<Boolean>builder().codec(Codec.BOOL).build()
	);
	
	/**
	 * 超级猪排效果组件
	 * 标记玩家是否拥有超级猪排的水下呼吸和抗火效果
	 */
	public static final ComponentType<Boolean> SUPER_PORKCHOP_EFFECT = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(SuperCraftMod.MOD_ID, "super_porkchop_effect"),
			ComponentType.<Boolean>builder().codec(Codec.BOOL).build()
	);
	
	/**
	 * 初始化组件注册
	 * 在模组初始化时调用此方法
	 */
	public static void initialize() {
		SuperCraftMod.LOGGER.info("正在注册 {} 个自定义数据组件", SuperCraftMod.MOD_ID);
		// 组件已在静态字段中注册，此方法确保类被加载
		// 这是Fabric推荐的组件注册模式
	}
}