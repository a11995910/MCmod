package com.supercraft.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.supercraft.util.PlayerDataUtil;

import java.util.List;

/**
 * 玩家实体Mixin类
 * 用于修改玩家的攻击行为，实现超级苹果的范围攻击效果
 */
@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
	
	/**
	 * 注入到玩家攻击实体方法中
	 * 实现超级苹果的范围攻击效果
	 * @param target 攻击目标
	 * @param ci 回调信息
	 */
	@Inject(method = "attack", at = @At("HEAD"))
	private void onAttack(Entity target, CallbackInfo ci) {
		PlayerEntity player = (PlayerEntity) (Object) this;
		
		// 检查玩家是否有超级苹果效果
		if (PlayerDataUtil.hasSuperAppleEffect(player)) {
			performRangeAttack(player, target);
		}
	}
	
	/**
	 * 执行范围攻击
	 * @param player 攻击者
	 * @param originalTarget 原始目标
	 */
	private void performRangeAttack(PlayerEntity player, Entity originalTarget) {
		// 获取攻击距离加成
		int rangeBonus = PlayerDataUtil.getAttackRangeBonus(player);
		
		// 计算攻击范围（基础范围 + 加成）
		double attackRange = 3.0 + rangeBonus;
		
		// 获取玩家位置
		Vec3d playerPos = player.getPos();
		
		// 创建攻击范围的边界框
		Box attackBox = new Box(
				playerPos.x - attackRange, playerPos.y - 1, playerPos.z - attackRange,
				playerPos.x + attackRange, playerPos.y + 3, playerPos.z + attackRange
		);
		
		// 获取范围内的所有生物实体
		List<LivingEntity> entitiesInRange = player.getWorld().getEntitiesByClass(
				LivingEntity.class, attackBox, 
				entity -> entity != player && entity != originalTarget && 
						 entity.isAlive() && player.canSee(entity)
		);
		
		// 对范围内的实体造成伤害
		for (LivingEntity entity : entitiesInRange) {
			// 计算距离
			double distance = entity.getPos().distanceTo(playerPos);
			
			// 只攻击在攻击范围内的实体
			if (distance <= attackRange) {
				// 计算伤害（距离越远伤害越低）
				// 使用固定基础伤害值，因为getAttackDamage方法在新版本中不可用
				float baseDamage = 4.0f; // 基础攻击伤害
				float damage = (float) (baseDamage * (1.0 - distance / (attackRange * 2)));
				
				// 确保最小伤害
				damage = Math.max(damage, 1.0f);
				
				// 造成伤害
				if (!player.getWorld().isClient && player.getWorld() instanceof net.minecraft.server.world.ServerWorld serverWorld) {
					entity.damage(serverWorld, player.getDamageSources().playerAttack(player), damage);
				}
				
				// 添加击退效果
				Vec3d knockback = entity.getPos().subtract(playerPos).normalize().multiply(0.5);
				entity.addVelocity(knockback.x, 0.1, knockback.z);
				entity.velocityModified = true;
			}
		}
	}
}