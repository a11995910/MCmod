package com.supercraft.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import com.supercraft.SuperCraftMod;
import com.supercraft.item.custom.SuperBreadItem;
import com.supercraft.item.custom.SuperAppleItem;
import com.supercraft.item.custom.SuperPorkchopItem;

/**
 * 模组物品注册类
 * 负责注册所有自定义物品并将它们添加到创造模式物品栏
 */
public class ModItems {
	
	/**
	 * 超级面包 - 增加1血量上限并提供随机正向状态效果
	 */
	public static final Item SUPER_BREAD = registerItem("super_bread", 
			new SuperBreadItem(new Item.Settings().food(ModFoodComponents.SUPER_BREAD)));
	
	/**
	 * 超级苹果 - 增加攻击距离并提供范围伤害
	 */
	public static final Item SUPER_APPLE = registerItem("super_apple", 
			new SuperAppleItem(new Item.Settings().food(ModFoodComponents.SUPER_APPLE)));
	
	/**
	 * 超级猪排 - 提供永久夜视、水下呼吸和抗火效果
	 */
	public static final Item SUPER_PORKCHOP = registerItem("super_porkchop", 
			new SuperPorkchopItem(new Item.Settings().food(ModFoodComponents.SUPER_PORKCHOP)));
	
	/**
	 * 注册物品的辅助方法
	 * @param name 物品名称
	 * @param item 物品实例
	 * @return 注册后的物品
	 */
	private static Item registerItem(String name, Item item) {
		return Registry.register(Registries.ITEM, Identifier.of(SuperCraftMod.MOD_ID, name), item);
	}
	
	/**
	 * 将物品添加到创造模式物品栏
	 */
	private static void addItemsToItemGroup() {
		// 将物品添加到食物和饮料物品组
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> {
			entries.add(SUPER_BREAD);
			entries.add(SUPER_APPLE);
			entries.add(SUPER_PORKCHOP);
		});
	}
	
	/**
	 * 初始化方法，注册所有物品
	 */
	public static void initialize() {
		SuperCraftMod.LOGGER.info("正在注册超级合成模组物品...");
		
		// 添加物品到创造模式物品栏
		addItemsToItemGroup();
		
		SuperCraftMod.LOGGER.info("超级合成模组物品注册完成！");
	}
}