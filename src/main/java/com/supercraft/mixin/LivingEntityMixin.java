package com.supercraft.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.supercraft.item.custom.SuperPorkchopItem;

/**
 * 生物实体Mixin类
 * 用于处理永久效果的定期刷新
 */
@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	
	/**
	 * 注入到生物实体的tick方法中
	 * 用于定期刷新永久效果
	 * @param ci 回调信息
	 */
	@Inject(method = "tick", at = @At("HEAD"))
	private void onTick(CallbackInfo ci) {
		LivingEntity entity = (LivingEntity) (Object) this;
		
		// 只处理玩家实体
		if (entity instanceof PlayerEntity player) {
			// 每20tick（1秒）检查一次永久效果
			if (entity.age % 20 == 0) {
				// 应用超级猪排的永久效果
				SuperPorkchopItem.applyPermanentEffects(player);
			}
		}
	}
}