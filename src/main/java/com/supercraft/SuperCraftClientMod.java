/**
 * 超级合成模组客户端初始化类
 * 处理客户端专用功能，如按键绑定、连锁挖矿系统等
 * 
 * @author SuperCraft Team
 * @version 1.2.0
 */
package com.supercraft;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.supercraft.system.ChainMiningSystem;

/**
 * 超级合成模组客户端主类
 * 负责初始化客户端专用功能
 */
public class SuperCraftClientMod implements ClientModInitializer {
    
    /**
     * 客户端日志记录器
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(SuperCraftMod.MOD_ID + "-client");
    
    /**
     * 客户端初始化方法
     * 在客户端启动时被调用，用于注册客户端专用内容
     */
    @Override
    public void onInitializeClient() {
        // 输出客户端模组加载信息
        LOGGER.info("正在加载超级合成模组客户端功能...");
        
        // 初始化连锁挖矿系统
        initializeChainMiningSystem();
        
        LOGGER.info("超级合成模组客户端功能加载完成！");
    }
    
    /**
     * 初始化连锁挖矿系统
     * 注册按键绑定和相关事件处理器
     */
    private void initializeChainMiningSystem() {
        LOGGER.info("正在初始化连锁挖矿系统...");
        
        // 创建并初始化连锁挖矿系统
        ChainMiningSystem chainMiningSystem = new ChainMiningSystem();
        chainMiningSystem.onInitializeClient();
        
        LOGGER.info("连锁挖矿系统初始化完成！");
    }
}