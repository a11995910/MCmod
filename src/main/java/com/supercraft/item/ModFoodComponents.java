package com.supercraft.item;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

/**
 * 模组食物组件定义类
 * 定义所有自定义食物的营养值和效果
 */
public class ModFoodComponents {
	
	/**
	 * 超级面包的食物组件
	 * 提供高营养值和饱和度
	 */
	public static final FoodComponent SUPER_BREAD = new FoodComponent.Builder()
			.nutrition(8) // 营养值
			.saturationModifier(1.2f) // 饱和度修正值
			.alwaysEdible() // 总是可以食用
			.build();
	
	/**
	 * 超级苹果的食物组件
	 * 提供中等营养值和特殊效果
	 */
	public static final FoodComponent SUPER_APPLE = new FoodComponent.Builder()
			.nutrition(6) // 营养值
			.saturationModifier(1.0f) // 饱和度修正值
			.alwaysEdible() // 总是可以食用
			.build();
	
	/**
	 * 超级猪排的食物组件
	 * 提供高营养值和永久效果
	 */
	public static final FoodComponent SUPER_PORKCHOP = new FoodComponent.Builder()
			.nutrition(10) // 营养值
			.saturationModifier(1.5f) // 饱和度修正值
			.alwaysEdible() // 总是可以食用
			.build();
}