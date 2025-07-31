package com.supercraft;

import net.fabricmc.api.ClientModInitializer;

/**
 * 超级合成模组客户端类
 * 负责处理客户端特定的初始化工作
 */
public class SuperCraftModClient implements ClientModInitializer {
	
	/**
	 * 客户端初始化方法
	 * 在客户端启动时被调用，用于注册客户端特定的内容
	 */
	@Override
	public void onInitializeClient() {
		// 客户端初始化代码
		SuperCraftMod.LOGGER.info("超级合成模组客户端初始化完成！");
	}
}