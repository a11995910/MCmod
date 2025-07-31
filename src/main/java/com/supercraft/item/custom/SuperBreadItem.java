package com.supercraft.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.Random;

/**
 * 超级面包物品类
 * 食用后增加1点血量上限并随机获得正向状态效果
 */
public class SuperBreadItem extends Item {
	
	/**
	 * 随机数生成器
	 */
	private static final Random RANDOM = new Random();
	
	/**
	 * 构造函数
	 * @param settings 物品设置
	 */
	public SuperBreadItem(Settings settings) {
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
			// 增加血量上限
			increaseMaxHealth(player);
			
			// 随机添加正向状态效果
			addRandomPositiveEffect(player);
			
			// 发送消息给玩家
			player.sendMessage(Text.literal("你感到身体变得更加强壮了！")
					.formatted(Formatting.GREEN), false);
		}
		
		return result;
	}
	
	/**
	 * 增加玩家的最大血量
	 * @param player 玩家
	 */
	private void increaseMaxHealth(PlayerEntity player) {
		EntityAttributeInstance healthAttribute = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);
		if (healthAttribute != null) {
			// 获取当前最大血量
			double currentMaxHealth = healthAttribute.getBaseValue();
			// 增加2点血量（1颗心）
			healthAttribute.setBaseValue(currentMaxHealth + 2.0);
			// 恢复玩家血量到新的最大值
			player.setHealth(player.getMaxHealth());
		}
	}
	
	/**
	 * 随机添加正向状态效果
	 * @param player 玩家
	 */
	private void addRandomPositiveEffect(PlayerEntity player) {
		// 定义可能的正向效果数组
		StatusEffectInstance[] positiveEffects = {
			new StatusEffectInstance(StatusEffects.REGENERATION, 1200, 1), // 再生II 60秒
			new StatusEffectInstance(StatusEffects.SPEED, 2400, 0), // 速度I 120秒
			new StatusEffectInstance(StatusEffects.STRENGTH, 1800, 0), // 力量I 90秒
			new StatusEffectInstance(StatusEffects.RESISTANCE, 1200, 0), // 抗性I 60秒
			new StatusEffectInstance(StatusEffects.JUMP_BOOST, 2400, 1), // 跳跃提升II 120秒
			new StatusEffectInstance(StatusEffects.ABSORPTION, 2400, 1), // 伤害吸收II 120秒
			new StatusEffectInstance(StatusEffects.LUCK, 6000, 0) // 幸运I 300秒
		};
		
		// 随机选择一个效果
		StatusEffectInstance randomEffect = positiveEffects[RANDOM.nextInt(positiveEffects.length)];
		
		// 添加效果到玩家
		player.addStatusEffect(randomEffect);
	}
}