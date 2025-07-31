package com.supercraft;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.supercraft.item.ModItems;
import com.supercraft.recipe.ModRecipes;

/**
 * 超级合成模组主类
 * 这是模组的主要入口点，负责初始化所有模组内容
 */
public class SuperCraftMod implements ModInitializer {
	/**
	 * 模组ID，用于标识模组
	 */
	public static final String MOD_ID = "supercraft";
	
	/**
	 * 日志记录器，用于输出模组相关信息
	 */
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	/**
	 * 模组初始化方法
	 * 在游戏启动时被调用，用于注册模组的所有内容
	 */
	@Override
	public void onInitialize() {
		// 输出模组加载信息
		LOGGER.info("正在加载超级合成模组...");
		
		// 注册模组物品
		ModItems.initialize();
		
		// 注册模组配方
		ModRecipes.initialize();
		
		LOGGER.info("超级合成模组加载完成！");
	}
}