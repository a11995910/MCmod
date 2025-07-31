package com.supercraft.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import com.supercraft.util.PlayerDataUtil;

/**
 * 超级猪排物品类
 * 食用后获得永久的夜视、水下呼吸和抗火效果
 */
public class SuperPorkchopItem extends Item {
	
	/**
	 * 构造函数
	 * @param settings 物品设置
	 */
	public SuperPorkchopItem(Settings settings) {
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
			// 添加永久效果
			addPermanentEffects(player);
			
			// 发送消息给玩家
			player.sendMessage(Text.literal("你获得了永久的特殊能力！")
					.formatted(Formatting.LIGHT_PURPLE), false);
		}
		
		return result;
	}
	
	/**
	 * 为玩家添加永久效果
	 * @param player 玩家
	 */
	private void addPermanentEffects(PlayerEntity player) {
		// 获取玩家的持久化数据
		NbtCompound playerData = PlayerDataUtil.getPlayerData(player);
		
		// 检查玩家是否已经有这些效果
		boolean hasNightVision = playerData.getBoolean("HasPermanentNightVision");
		boolean hasWaterBreathing = playerData.getBoolean("HasPermanentWaterBreathing");
		boolean hasFireResistance = playerData.getBoolean("HasPermanentFireResistance");
		
		// 添加永久夜视效果
		if (!hasNightVision) {
			playerData.putBoolean("HasPermanentNightVision", true);
			player.sendMessage(Text.literal("获得永久夜视！")
					.formatted(Formatting.YELLOW), true);
		}
		
		// 添加永久水下呼吸效果
		if (!hasWaterBreathing) {
			playerData.putBoolean("HasPermanentWaterBreathing", true);
			player.sendMessage(Text.literal("获得永久水下呼吸！")
					.formatted(Formatting.AQUA), true);
		}
		
		// 添加永久抗火效果
		if (!hasFireResistance) {
			playerData.putBoolean("HasPermanentFireResistance", true);
			player.sendMessage(Text.literal("获得永久抗火！")
					.formatted(Formatting.RED), true);
		}
		
		// 保存数据
		PlayerDataUtil.savePlayerData(player, playerData);
		
		// 立即应用效果
		applyPermanentEffects(player);
	}
	
	/**
	 * 应用永久效果到玩家
	 * @param player 玩家
	 */
	public static void applyPermanentEffects(PlayerEntity player) {
		NbtCompound playerData = PlayerDataUtil.getPlayerData(player);
		
		// 应用夜视效果（持续时间设置为很长，但会定期刷新）
		if (playerData.getBoolean("HasPermanentNightVision")) {
			player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 300, 0, false, false));
		}
		
		// 应用水下呼吸效果
		if (playerData.getBoolean("HasPermanentWaterBreathing")) {
			player.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 300, 0, false, false));
		}
		
		// 应用抗火效果
		if (playerData.getBoolean("HasPermanentFireResistance")) {
			player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 300, 0, false, false));
		}
	}
}