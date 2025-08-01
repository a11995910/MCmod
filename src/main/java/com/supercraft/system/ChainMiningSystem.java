/**
 * 连锁挖矿系统
 * 实现连锁挖掘矿物、木头、原石等方块的功能
 * 支持自定义按键绑定，默认为～键
 * 每次连锁上限50个方块，长按按键生效
 * 
 * @author SuperCraft Team
 * @version 1.2.0
 */
package com.supercraft.system;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;

import java.util.*;

/**
 * 连锁挖矿系统主类
 * 处理按键绑定、方块检测和连锁挖掘逻辑
 */
public class ChainMiningSystem implements ClientModInitializer {
    
    /**
     * 连锁挖矿按键绑定
     * 默认绑定到～键（GLFW_KEY_GRAVE_ACCENT）
     */
    private static KeyBinding chainMiningKey;
    
    /**
     * 连锁挖掘的最大方块数量
     */
    private static final int MAX_CHAIN_BLOCKS = 50;
    
    /**
     * 按键按下的时间计数器
     * 用于检测长按
     */
    private static int keyPressTime = 0;
    
    /**
     * 长按触发的最小时间（tick）
     * 20 tick = 1秒
     */
    private static final int LONG_PRESS_THRESHOLD = 10; // 0.5秒
    
    /**
     * 当前是否启用连锁挖矿模式
     */
    private static boolean chainMiningEnabled = false;
    
    /**
     * 可连锁挖掘的方块类型集合
     * 包括矿物、木头、原石等
     */
    private static final Set<Block> CHAINABLE_BLOCKS = new HashSet<>();
    
    /**
     * 初始化连锁挖矿系统
     */
    @Override
    public void onInitializeClient() {
        // 注册按键绑定
        registerKeyBinding();
        
        // 初始化可连锁方块
        initializeChainableBlocks();
        
        // 注册客户端tick事件
        registerClientTickEvents();
        
        // 注册方块破坏事件
        registerBlockBreakEvents();
    }
    
    /**
     * 注册连锁挖矿按键绑定
     */
    private void registerKeyBinding() {
        chainMiningKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.supercraft.chain_mining", // 翻译键
            InputUtil.Type.KEYSYM, // 键盘按键类型
            GLFW.GLFW_KEY_GRAVE_ACCENT, // 默认～键
            "category.supercraft.mining" // 按键分类
        ));
    }
    
    /**
     * 初始化可连锁挖掘的方块类型
     */
    private void initializeChainableBlocks() {
        // 矿物方块
        CHAINABLE_BLOCKS.add(Blocks.COAL_ORE);
        CHAINABLE_BLOCKS.add(Blocks.DEEPSLATE_COAL_ORE);
        CHAINABLE_BLOCKS.add(Blocks.IRON_ORE);
        CHAINABLE_BLOCKS.add(Blocks.DEEPSLATE_IRON_ORE);
        CHAINABLE_BLOCKS.add(Blocks.GOLD_ORE);
        CHAINABLE_BLOCKS.add(Blocks.DEEPSLATE_GOLD_ORE);
        CHAINABLE_BLOCKS.add(Blocks.DIAMOND_ORE);
        CHAINABLE_BLOCKS.add(Blocks.DEEPSLATE_DIAMOND_ORE);
        CHAINABLE_BLOCKS.add(Blocks.EMERALD_ORE);
        CHAINABLE_BLOCKS.add(Blocks.DEEPSLATE_EMERALD_ORE);
        CHAINABLE_BLOCKS.add(Blocks.LAPIS_ORE);
        CHAINABLE_BLOCKS.add(Blocks.DEEPSLATE_LAPIS_ORE);
        CHAINABLE_BLOCKS.add(Blocks.REDSTONE_ORE);
        CHAINABLE_BLOCKS.add(Blocks.DEEPSLATE_REDSTONE_ORE);
        CHAINABLE_BLOCKS.add(Blocks.COPPER_ORE);
        CHAINABLE_BLOCKS.add(Blocks.DEEPSLATE_COPPER_ORE);
        CHAINABLE_BLOCKS.add(Blocks.NETHER_GOLD_ORE);
        CHAINABLE_BLOCKS.add(Blocks.NETHER_QUARTZ_ORE);
        CHAINABLE_BLOCKS.add(Blocks.ANCIENT_DEBRIS);
        
        // 原石类型
        CHAINABLE_BLOCKS.add(Blocks.STONE);
        CHAINABLE_BLOCKS.add(Blocks.DEEPSLATE);
        CHAINABLE_BLOCKS.add(Blocks.COBBLESTONE);
        CHAINABLE_BLOCKS.add(Blocks.COBBLED_DEEPSLATE);
        CHAINABLE_BLOCKS.add(Blocks.GRANITE);
        CHAINABLE_BLOCKS.add(Blocks.DIORITE);
        CHAINABLE_BLOCKS.add(Blocks.ANDESITE);
        CHAINABLE_BLOCKS.add(Blocks.TUFF);
        CHAINABLE_BLOCKS.add(Blocks.CALCITE);
        
        // 木头类型（原木和木头）
        CHAINABLE_BLOCKS.add(Blocks.OAK_LOG);
        CHAINABLE_BLOCKS.add(Blocks.OAK_WOOD);
        CHAINABLE_BLOCKS.add(Blocks.STRIPPED_OAK_LOG);
        CHAINABLE_BLOCKS.add(Blocks.STRIPPED_OAK_WOOD);
        CHAINABLE_BLOCKS.add(Blocks.BIRCH_LOG);
        CHAINABLE_BLOCKS.add(Blocks.BIRCH_WOOD);
        CHAINABLE_BLOCKS.add(Blocks.STRIPPED_BIRCH_LOG);
        CHAINABLE_BLOCKS.add(Blocks.STRIPPED_BIRCH_WOOD);
        CHAINABLE_BLOCKS.add(Blocks.SPRUCE_LOG);
        CHAINABLE_BLOCKS.add(Blocks.SPRUCE_WOOD);
        CHAINABLE_BLOCKS.add(Blocks.STRIPPED_SPRUCE_LOG);
        CHAINABLE_BLOCKS.add(Blocks.STRIPPED_SPRUCE_WOOD);
        CHAINABLE_BLOCKS.add(Blocks.JUNGLE_LOG);
        CHAINABLE_BLOCKS.add(Blocks.JUNGLE_WOOD);
        CHAINABLE_BLOCKS.add(Blocks.STRIPPED_JUNGLE_LOG);
        CHAINABLE_BLOCKS.add(Blocks.STRIPPED_JUNGLE_WOOD);
        CHAINABLE_BLOCKS.add(Blocks.ACACIA_LOG);
        CHAINABLE_BLOCKS.add(Blocks.ACACIA_WOOD);
        CHAINABLE_BLOCKS.add(Blocks.STRIPPED_ACACIA_LOG);
        CHAINABLE_BLOCKS.add(Blocks.STRIPPED_ACACIA_WOOD);
        CHAINABLE_BLOCKS.add(Blocks.DARK_OAK_LOG);
        CHAINABLE_BLOCKS.add(Blocks.DARK_OAK_WOOD);
        CHAINABLE_BLOCKS.add(Blocks.STRIPPED_DARK_OAK_LOG);
        CHAINABLE_BLOCKS.add(Blocks.STRIPPED_DARK_OAK_WOOD);
        CHAINABLE_BLOCKS.add(Blocks.MANGROVE_LOG);
        CHAINABLE_BLOCKS.add(Blocks.MANGROVE_WOOD);
        CHAINABLE_BLOCKS.add(Blocks.STRIPPED_MANGROVE_LOG);
        CHAINABLE_BLOCKS.add(Blocks.STRIPPED_MANGROVE_WOOD);
        CHAINABLE_BLOCKS.add(Blocks.CHERRY_LOG);
        CHAINABLE_BLOCKS.add(Blocks.CHERRY_WOOD);
        CHAINABLE_BLOCKS.add(Blocks.STRIPPED_CHERRY_LOG);
        CHAINABLE_BLOCKS.add(Blocks.STRIPPED_CHERRY_WOOD);
        CHAINABLE_BLOCKS.add(Blocks.PALE_OAK_LOG);
        CHAINABLE_BLOCKS.add(Blocks.PALE_OAK_WOOD);
        CHAINABLE_BLOCKS.add(Blocks.STRIPPED_PALE_OAK_LOG);
        CHAINABLE_BLOCKS.add(Blocks.STRIPPED_PALE_OAK_WOOD);
        
        // 下界木头
        CHAINABLE_BLOCKS.add(Blocks.CRIMSON_STEM);
        CHAINABLE_BLOCKS.add(Blocks.CRIMSON_HYPHAE);
        CHAINABLE_BLOCKS.add(Blocks.STRIPPED_CRIMSON_STEM);
        CHAINABLE_BLOCKS.add(Blocks.STRIPPED_CRIMSON_HYPHAE);
        CHAINABLE_BLOCKS.add(Blocks.WARPED_STEM);
        CHAINABLE_BLOCKS.add(Blocks.WARPED_HYPHAE);
        CHAINABLE_BLOCKS.add(Blocks.STRIPPED_WARPED_STEM);
        CHAINABLE_BLOCKS.add(Blocks.STRIPPED_WARPED_HYPHAE);
    }
    
    /**
     * 注册客户端tick事件
     * 处理按键状态检测
     */
    private void registerClientTickEvents() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (chainMiningKey.isPressed()) {
                keyPressTime++;
                
                // 长按触发连锁挖矿模式
                if (keyPressTime >= LONG_PRESS_THRESHOLD && !chainMiningEnabled) {
                    chainMiningEnabled = true;
                    if (client.player != null) {
                        client.player.sendMessage(
                            Text.translatable("message.supercraft.chain_mining.enabled"), 
                            true // 显示在操作栏
                        );
                    }
                }
            } else {
                // 按键释放，重置状态
                if (chainMiningEnabled) {
                    chainMiningEnabled = false;
                    if (client.player != null) {
                        client.player.sendMessage(
                            Text.translatable("message.supercraft.chain_mining.disabled"), 
                            true
                        );
                    }
                }
                keyPressTime = 0;
            }
        });
    }
    
    /**
     * 注册方块破坏事件
     * 在方块被破坏后触发连锁挖掘
     */
    private void registerBlockBreakEvents() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            // 只在服务器端处理连锁挖掘
            if (world.isClient() || !chainMiningEnabled) {
                return;
            }
            
            // 检查是否为可连锁方块
            if (isChainableBlock(state.getBlock())) {
                performChainMining(world, player, pos, state.getBlock());
            }
        });
    }
    
    /**
     * 检查方块是否可以连锁挖掘
     * @param block 要检查的方块
     * @return 是否可以连锁挖掘
     */
    private boolean isChainableBlock(Block block) {
        return CHAINABLE_BLOCKS.contains(block);
    }
    
    /**
     * 执行连锁挖掘
     * @param world 世界
     * @param player 玩家
     * @param startPos 起始位置
     * @param targetBlock 目标方块类型
     */
    private void performChainMining(World world, PlayerEntity player, BlockPos startPos, Block targetBlock) {
        if (!(world instanceof ServerWorld serverWorld)) {
            return;
        }
        
        // 获取玩家当前使用的工具
        ItemStack tool = player.getMainHandStack();
        
        // 检查工具是否适合挖掘该方块
        if (!canHarvestBlock(tool, targetBlock)) {
            return;
        }
        
        // 使用广度优先搜索找到所有连接的相同方块
        Set<BlockPos> blocksToBreak = findConnectedBlocks(serverWorld, startPos, targetBlock);
        
        // 限制连锁数量
        if (blocksToBreak.size() > MAX_CHAIN_BLOCKS) {
            // 只取前MAX_CHAIN_BLOCKS个方块
            blocksToBreak = blocksToBreak.stream()
                .limit(MAX_CHAIN_BLOCKS)
                .collect(java.util.stream.Collectors.toSet());
        }
        
        // 破坏所有找到的方块
        int brokenCount = 0;
        for (BlockPos pos : blocksToBreak) {
            if (pos.equals(startPos)) {
                continue; // 跳过起始方块（已经被破坏）
            }
            
            BlockState state = serverWorld.getBlockState(pos);
            if (state.getBlock() == targetBlock) {
                // 模拟玩家破坏方块
                if (serverWorld.breakBlock(pos, true, player)) {
                    brokenCount++;
                    
                    // 减少工具耐久度
                    if (!tool.isEmpty() && tool.isDamageable()) {
                        tool.damage(1, serverWorld, (ServerPlayerEntity) player, item -> {
                            // 工具破坏时的回调，damage方法会自动处理破坏状态
                        });
                    }
                }
            }
        }
        
        // 发送连锁挖掘结果消息
        if (brokenCount > 0) {
            player.sendMessage(
                Text.translatable("message.supercraft.chain_mining.result", brokenCount), 
                true
            );
        }
    }
    
    /**
     * 使用广度优先搜索找到所有连接的相同方块
     * @param world 世界
     * @param startPos 起始位置
     * @param targetBlock 目标方块类型
     * @return 所有连接的方块位置集合
     */
    private Set<BlockPos> findConnectedBlocks(ServerWorld world, BlockPos startPos, Block targetBlock) {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        Set<BlockPos> result = new HashSet<>();
        
        queue.offer(startPos);
        visited.add(startPos);
        
        // 六个方向的偏移量
        Direction[] directions = Direction.values();
        
        while (!queue.isEmpty() && result.size() < MAX_CHAIN_BLOCKS) {
            BlockPos current = queue.poll();
            result.add(current);
            
            // 检查六个方向的相邻方块
            for (Direction direction : directions) {
                BlockPos neighbor = current.offset(direction);
                
                // 检查是否已访问过
                if (visited.contains(neighbor)) {
                    continue;
                }
                
                // 检查是否为相同类型的方块
                BlockState neighborState = world.getBlockState(neighbor);
                if (neighborState.getBlock() == targetBlock) {
                    visited.add(neighbor);
                    queue.offer(neighbor);
                }
            }
        }
        
        return result;
    }
    
    /**
     * 检查工具是否可以挖掘指定方块
     * @param tool 工具物品
     * @param block 方块
     * @return 是否可以挖掘
     */
    private boolean canHarvestBlock(ItemStack tool, Block block) {
        // 如果是空手，只能挖掘不需要工具的方块
        if (tool.isEmpty()) {
            return !block.getDefaultState().isToolRequired();
        }
        
        // 检查工具是否适合挖掘该方块
        return tool.isSuitableFor(block.getDefaultState());
    }
    
    /**
     * 获取连锁挖矿是否启用
     * @return 是否启用连锁挖矿
     */
    public static boolean isChainMiningEnabled() {
        return chainMiningEnabled;
    }
    
    /**
     * 获取连锁挖矿按键绑定
     * @return 按键绑定对象
     */
    public static KeyBinding getChainMiningKey() {
        return chainMiningKey;
    }
}