package com.supercraft.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import com.supercraft.component.ModComponents;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.util.Identifier;
import com.mojang.serialization.Codec;
import com.supercraft.SuperCraftMod;

/**
 * 玩家数据工具类
 * 用于处理玩家持久化数据的读取和保存
 * 使用Fabric Data Attachment API替代传统NBT存储
 */
public class PlayerDataUtil {
	
	/**
	 * 超级苹果效果附件类型
	 * 用于存储玩家是否拥有超级苹果效果
	 */
	public static final AttachmentType<Boolean> SUPER_APPLE_EFFECT = AttachmentRegistry.create(
			Identifier.of(SuperCraftMod.MOD_ID, "super_apple_effect"),
			builder -> builder
					.initializer(() -> false)
					.persistent(Codec.BOOL)
	);
	
	/**
	 * 超级苹果攻击范围加成附件类型
	 */
	public static final AttachmentType<Integer> SUPER_APPLE_RANGE = AttachmentRegistry.create(
			Identifier.of(SuperCraftMod.MOD_ID, "super_apple_range"),
			builder -> builder
					.initializer(() -> 0)
					.persistent(Codec.INT)
	);
	
	/**
	 * 超级面包效果附件类型
	 */
	public static final AttachmentType<Boolean> SUPER_BREAD_EFFECT = AttachmentRegistry.create(
			Identifier.of(SuperCraftMod.MOD_ID, "super_bread_effect"),
			builder -> builder
					.initializer(() -> false)
					.persistent(Codec.BOOL)
	);
	
	/**
	 * 超级猪排效果附件类型
	 */
	public static final AttachmentType<Boolean> SUPER_PORKCHOP_EFFECT = AttachmentRegistry.create(
			Identifier.of(SuperCraftMod.MOD_ID, "super_porkchop_effect"),
			builder -> builder
					.initializer(() -> false)
					.persistent(Codec.BOOL)
	);
	
	/**
	 * 设置玩家的超级苹果效果
	 * @param player 玩家实体
	 * @param hasEffect 是否拥有效果
	 */
	public static void setSuperAppleEffect(PlayerEntity player, boolean hasEffect) {
		player.setAttached(SUPER_APPLE_EFFECT, hasEffect);
	}
	
	/**
	 * 设置玩家的超级苹果攻击范围加成
	 * @param player 玩家实体
	 * @param rangeBonus 攻击范围加成值
	 */
	public static void setSuperAppleRange(PlayerEntity player, int rangeBonus) {
		player.setAttached(SUPER_APPLE_RANGE, rangeBonus);
	}
	
	/**
	 * 设置玩家的超级面包效果
	 * @param player 玩家实体
	 * @param hasEffect 是否拥有效果
	 */
	public static void setSuperBreadEffect(PlayerEntity player, boolean hasEffect) {
		player.setAttached(SUPER_BREAD_EFFECT, hasEffect);
	}
	
	/**
	 * 设置玩家的超级猪排效果
	 * @param player 玩家实体
	 * @param hasEffect 是否拥有效果
	 */
	public static void setSuperPorkchopEffect(PlayerEntity player, boolean hasEffect) {
		player.setAttached(SUPER_PORKCHOP_EFFECT, hasEffect);
	}
	
	/**
	 * 获取玩家的攻击距离加成
	 * @param player 玩家实体
	 * @return 攻击距离加成值
	 */
	public static int getAttackRangeBonus(PlayerEntity player) {
		return player.getAttached(SUPER_APPLE_RANGE);
	}
	
	/**
	 * 检查玩家是否有超级苹果效果
	 * @param player 玩家实体
	 * @return 是否有超级苹果效果
	 */
	public static boolean hasSuperAppleEffect(PlayerEntity player) {
		return player.getAttached(SUPER_APPLE_EFFECT);
	}
	
	/**
	 * 检查玩家是否有永久夜视效果（超级面包）
	 * @param player 玩家实体
	 * @return 是否有永久夜视效果
	 */
	public static boolean hasPermanentNightVision(PlayerEntity player) {
		return player.getAttached(SUPER_BREAD_EFFECT);
	}
	
	/**
	 * 检查玩家是否有永久水下呼吸效果（超级猪排）
	 * @param player 玩家实体
	 * @return 是否有永久水下呼吸效果
	 */
	public static boolean hasPermanentWaterBreathing(PlayerEntity player) {
		return player.getAttached(SUPER_PORKCHOP_EFFECT);
	}
	
	/**
	 * 检查玩家是否有永久抗火效果（超级猪排）
	 * @param player 玩家实体
	 * @return 是否有永久抗火效果
	 */
	public static boolean hasPermanentFireResistance(PlayerEntity player) {
		return player.getAttached(SUPER_PORKCHOP_EFFECT);
	}
}