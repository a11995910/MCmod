package com.supercraft.recipe.custom;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.ItemTags;
import com.supercraft.component.ModComponents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.CraftingScreenHandler;

import java.util.*;

/**
 * 装备强化配方类型
 * 处理三个相同装备合成强化装备的逻辑
 */
public class EnhancementRecipeType {
	
	/**
	 * 随机数生成器
	 */
	private static final Random RANDOM = new Random();
	
	/**
	 * 构造函数
	 * 使用新的事件系统来检测合成台操作
	 */
	public EnhancementRecipeType() {
		// 注册物品使用事件来检测合成台交互
		UseItemCallback.EVENT.register((player, world, hand) -> {
			ItemStack stack = player.getStackInHand(hand);
			if (stack.isOf(Items.CRAFTING_TABLE)) {
				// 当玩家使用合成台时，检查是否有强化配方
				if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer) {
					// 延迟检查，等待GUI打开
					world.getServer().execute(() -> {
						checkPlayerCraftingScreen(serverPlayer);
					});
				}
			}
			return ActionResult.PASS;
		});
		
		// 注册服务器tick事件来持续检查合成台
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			// 检查所有在线玩家的合成台界面
			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
				if (player.currentScreenHandler instanceof CraftingScreenHandler) {
					checkPlayerCraftingScreen(player);
				}
			}
		});
	}
	
	/**
	 * 检查玩家的合成台界面是否有强化配方
	 * @param player 玩家
	 */
	private void checkPlayerCraftingScreen(ServerPlayerEntity player) {
		if (player.currentScreenHandler instanceof CraftingScreenHandler craftingHandler) {
			// 获取合成台的物品栏
			// 合成台的输入槽位是1-9（3x3网格）
			ItemStack[] craftingGrid = new ItemStack[9];
			for (int i = 0; i < 9; i++) {
				craftingGrid[i] = craftingHandler.getSlot(i + 1).getStack(); // 跳过结果槽（索引0）
			}
			
			// 检查是否有强化配方
			checkEnhancementRecipe(craftingGrid, player, craftingHandler);
		}
	}
	
	/**
	 * 检查强化配方
	 * @param craftingGrid 合成台网格
	 * @param player 玩家
	 * @param craftingHandler 合成台处理器
	 */
	private void checkEnhancementRecipe(ItemStack[] craftingGrid, ServerPlayerEntity player, CraftingScreenHandler craftingHandler) {
		// 检查是否有三个相同的工具/装备在特定位置
		// 强化配方：三个相同物品放在合成台的中间一列（位置1, 4, 7）
		ItemStack top = craftingGrid[1];    // 上方中间
		ItemStack middle = craftingGrid[4]; // 正中间
		ItemStack bottom = craftingGrid[7]; // 下方中间
		
		if (canEnhance(top, middle, bottom)) {
			// 创建强化后的物品
			ItemStack enhancedItem = createEnhancedItem(middle.copy());
			
			// 设置结果槽的物品
			craftingHandler.getSlot(0).setStack(enhancedItem);
			
			// 发送消息给玩家
			player.sendMessage(Text.literal("检测到强化配方！").formatted(Formatting.GREEN), false);
		} else {
			// 清空结果槽（如果不是强化配方）
			if (craftingHandler.getSlot(0).getStack().isEmpty() == false) {
				// 只有当结果槽有我们的强化物品时才清空
				ItemStack resultStack = craftingHandler.getSlot(0).getStack();
				if (resultStack.contains(ModComponents.ENHANCEMENT_LEVEL)) {
					craftingHandler.getSlot(0).setStack(ItemStack.EMPTY);
				}
			}
		}
	}
	
	/**
	 * 检查是否可以强化（三个相同物品）
	 * @param first 第一个物品
	 * @param second 第二个物品
	 * @param third 第三个物品
	 * @return 是否可以强化
	 */
	private boolean canEnhance(ItemStack first, ItemStack second, ItemStack third) {
		// 检查物品是否为空
		if (first.isEmpty() || second.isEmpty() || third.isEmpty()) {
			return false;
		}
		
		// 检查是否都是相同的物品
		if (!ItemStack.areItemsEqual(first, second) || !ItemStack.areItemsEqual(second, third)) {
			return false;
		}
		
		// 使用新的物品标签API检查工具类型
		if (!isEnhanceableItem(first)) {
			return false;
		}
		
		// 检查强化等级是否已达上限
		int currentLevel = getEnhancementLevel(first);
		return currentLevel < 5; // 最大强化等级为5
	}
	
	/**
	 * 检查物品是否可以被强化
	 * 使用新的物品标签API替代ToolItem检查
	 * @param stack 物品堆
	 * @return 是否可以强化
	 */
	private boolean isEnhanceableItem(ItemStack stack) {
		// 使用物品标签检查是否为工具、武器或装备
		return stack.isIn(ItemTags.AXES) || 
			   stack.isIn(ItemTags.PICKAXES) ||
			   stack.isIn(ItemTags.SHOVELS) ||
			   stack.isIn(ItemTags.HOES) ||
			   stack.isIn(ItemTags.SWORDS) || 
			   stack.isIn(ItemTags.ARMOR_ENCHANTABLE) ||
			   stack.getItem() instanceof ArmorItem ||
			   stack.getItem() instanceof SwordItem ||
			   stack.getItem() instanceof AxeItem ||
			   stack.getItem() instanceof PickaxeItem ||
			   stack.getItem() instanceof ShovelItem ||
			   stack.getItem() instanceof HoeItem;
	}
	
	/**
	 * 获取物品的强化等级
	 * 使用新的组件系统替代NBT数据存储
	 * @param stack 物品堆叠
	 * @return 强化等级
	 */
	private int getEnhancementLevel(ItemStack stack) {
		// 使用新的组件系统获取强化等级
		Integer level = stack.get(ModComponents.ENHANCEMENT_LEVEL);
		return level != null ? level : 0;
	}
	
	/**
	 * 创建强化后的物品
	 * 使用新的组件系统存储强化数据
	 * @param original 原始物品
	 * @return 强化后的物品
	 */
	private ItemStack createEnhancedItem(ItemStack original) {
		ItemStack enhanced = original.copy();
		
		// 获取当前强化等级
		int currentLevel = getEnhancementLevel(original);
		int newLevel = currentLevel + 1;
		
		// 使用新的组件系统设置强化等级
		enhanced.set(ModComponents.ENHANCEMENT_LEVEL, newLevel);
		
		// 设置强化次数
		Integer currentCount = enhanced.get(ModComponents.ENHANCEMENT_COUNT);
		int newCount = (currentCount != null ? currentCount : 0) + 1;
		enhanced.set(ModComponents.ENHANCEMENT_COUNT, newCount);
		
		// 计算并设置属性加成
		float bonus = calculateEnhancementBonus(newLevel);
		enhanced.set(ModComponents.ENHANCEMENT_BONUS, bonus);
		
		// 增强属性
		enhanceAttributes(enhanced, newLevel);
		
		// 添加随机附魔
		if (RANDOM.nextFloat() < 0.3f) { // 30%概率添加附魔
			addRandomEnchantment(enhanced);
		}
		
		// 更新显示名称
		updateDisplayName(enhanced, newLevel);
		
		return enhanced;
	}
	
	/**
	 * 计算强化等级对应的属性加成
	 * @param level 强化等级
	 * @return 属性加成值
	 */
	private float calculateEnhancementBonus(int level) {
		// 每级强化增加10%的属性加成
		return level * 0.1f;
	}
	
	/**
	 * 增强物品属性
	 * 使用新的组件系统实现属性增强
	 * @param stack 物品堆叠
	 * @param level 强化等级
	 */
	private void enhanceAttributes(ItemStack stack, int level) {
		// 注意：在1.21.x中，属性修饰符的处理方式发生了变化
		// 这里我们主要通过组件数据来标记强化状态
		// 实际的属性加成可以通过事件系统或Mixin来实现
		
		// 存储强化信息到组件中，供其他系统使用
		// 例如：在攻击事件中读取这些数据来增加伤害
		// 在耐久度事件中读取这些数据来减少耐久度损失
		
		// 这种方式更加安全和兼容，避免了直接修改物品属性可能导致的问题
	}
	
	/**
	 * 随机添加附魔
	 * @param stack 物品堆叠
	 */
	private void addRandomEnchantment(ItemStack stack) {
		// 30%概率添加附魔
		if (RANDOM.nextFloat() < 0.3f) {
			// 简化附魔系统，直接添加一些基础附魔
			// 由于API变化，暂时简化实现
			// TODO: 在后续版本中重新实现完整的附魔系统
		}
	}
	
	/**
	 * 更新物品显示名称
	 * @param stack 物品堆叠
	 * @param level 强化等级
	 */
	private void updateDisplayName(ItemStack stack, int level) {
		String originalName = stack.getItem().getName().getString();
		Text enhancedName = Text.literal(originalName + " +" + level)
				.formatted(getEnhancementColor(level));
		// 使用组件系统设置自定义名称
		// TODO: 在后续版本中实现正确的名称设置
	}
	
	/**
	 * 根据强化等级获取颜色
	 * @param level 强化等级
	 * @return 格式化颜色
	 */
	private Formatting getEnhancementColor(int level) {
		return switch (level) {
			case 1, 2, 3 -> Formatting.GREEN;
			case 4, 5, 6 -> Formatting.BLUE;
			case 7, 8, 9 -> Formatting.LIGHT_PURPLE;
			default -> Formatting.WHITE;
		};
	}
}