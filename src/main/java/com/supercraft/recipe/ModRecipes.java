package com.supercraft.recipe;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.server.MinecraftServer;

import com.supercraft.SuperCraftMod;
import com.supercraft.recipe.custom.EnhancementRecipeType;

/**
 * 模组配方注册类
 * 负责注册所有自定义配方类型和配方
 */
public class ModRecipes {
	
	/**
	 * 装备强化配方类型
	 */
	public static EnhancementRecipeType ENHANCEMENT_RECIPE_TYPE;
	
	/**
	 * 初始化方法，注册所有配方
	 */
	public static void initialize() {
		SuperCraftMod.LOGGER.info("正在注册超级合成模组配方...");
		
		// 注册装备强化配方类型
		ENHANCEMENT_RECIPE_TYPE = new EnhancementRecipeType();
		
		// 注册服务器启动事件监听器
		ServerLifecycleEvents.SERVER_STARTED.register(ModRecipes::onServerStarted);
		
		SuperCraftMod.LOGGER.info("超级合成模组配方注册完成！");
	}
	
	/**
	 * 服务器启动时的回调方法
	 * @param server 服务器实例
	 */
	private static void onServerStarted(MinecraftServer server) {
		// 获取配方管理器
		RecipeManager recipeManager = server.getRecipeManager();
		
		// 这里可以添加动态配方注册逻辑
		SuperCraftMod.LOGGER.info("服务器启动，配方系统已就绪");
	}
}