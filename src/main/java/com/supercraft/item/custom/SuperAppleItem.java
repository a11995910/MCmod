package com.supercraft.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import com.supercraft.util.PlayerDataUtil;

/**
 * 超级苹果物品类
 * 食用后永久增加攻击距离1格，并提供范围伤害能力
 */
public class SuperAppleItem extends Item {
	
	/**
	 * 构造函数
	 * @param settings 物品设置
	 */
	public SuperAppleItem(Settings settings) {
		super(settings);
	}
	
	/**
	 * 当物品被食用完成时调用
	 * @param stack 物品堆叠
	 * @param world 世界
	 * @param user 使用者
	 * @return 剩余的物品堆叠
	 */
	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		// 调用父类方法处理基本的食用逻辑
		ItemStack result = super.finishUsing(stack, world, user);
		
		// 只在服务端处理效果
		if (!world.isClient && user instanceof PlayerEntity player) {
			// 增加攻击距离
			increaseAttackRange(player);
			
			// 发送消息给玩家
			player.sendMessage(Text.literal("你的攻击距离增加了！现在你可以进行范围攻击！")
					.formatted(Formatting.GOLD), false);
		}
		
		return result;
	}
	
	/**
	 * 增加玩家的攻击距离
	 * @param player 玩家
	 */
	private void increaseAttackRange(PlayerEntity player) {
		// 获取玩家的持久化数据
		NbtCompound playerData = PlayerDataUtil.getPlayerData(player);
		
		// 获取当前的攻击距离加成
		int currentRangeBonus = playerData.getInt("SuperAppleRangeBonus");
		
		// 增加1格攻击距离（最多叠加到10格）
		if (currentRangeBonus < 10) {
			currentRangeBonus++;
			playerData.putInt("SuperAppleRangeBonus", currentRangeBonus);
			
			// 同时设置范围伤害标记
			playerData.putBoolean("HasSuperAppleEffect", true);
			
			// 保存数据
			PlayerDataUtil.savePlayerData(player, playerData);
			
			player.sendMessage(Text.literal("攻击距离增加！当前加成: +" + currentRangeBonus + " 格")
					.formatted(Formatting.YELLOW), true);
		} else {
			player.sendMessage(Text.literal("攻击距离已达到最大值！")
					.formatted(Formatting.RED), true);
		}
	}
}