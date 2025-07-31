package com.supercraft.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * 玩家数据工具类
 * 用于处理玩家持久化数据的读取和保存
 */
public class PlayerDataUtil {
	
	/**
	 * 超级合成模组数据的NBT键名
	 */
	private static final String SUPERCRAFT_DATA_KEY = "SuperCraftData";
	
	/**
	 * 获取玩家的超级合成模组数据
	 * @param player 玩家实体
	 * @return 包含模组数据的NBT复合标签
	 */
	public static NbtCompound getPlayerData(PlayerEntity player) {
		if (player instanceof ServerPlayerEntity serverPlayer) {
			// 获取玩家的持久化数据
			NbtCompound playerNbt = serverPlayer.writeNbt(new NbtCompound());
			
			// 如果没有超级合成数据，创建一个新的
			if (!playerNbt.contains(SUPERCRAFT_DATA_KEY)) {
				playerNbt.put(SUPERCRAFT_DATA_KEY, new NbtCompound());
			}
			
			return playerNbt.getCompound(SUPERCRAFT_DATA_KEY);
		}
		
		// 如果不是服务端玩家，返回空的NBT复合标签
		return new NbtCompound();
	}
	
	/**
	 * 保存玩家的超级合成模组数据
	 * @param player 玩家实体
	 * @param data 要保存的数据
	 */
	public static void savePlayerData(PlayerEntity player, NbtCompound data) {
		if (player instanceof ServerPlayerEntity serverPlayer) {
			// 获取玩家的完整NBT数据
			NbtCompound playerNbt = serverPlayer.writeNbt(new NbtCompound());
			
			// 更新超级合成数据
			playerNbt.put(SUPERCRAFT_DATA_KEY, data);
			
			// 将数据写回玩家
			serverPlayer.readNbt(playerNbt);
		}
	}
	
	/**
	 * 获取玩家的攻击距离加成
	 * @param player 玩家实体
	 * @return 攻击距离加成值
	 */
	public static int getAttackRangeBonus(PlayerEntity player) {
		NbtCompound data = getPlayerData(player);
		return data.getInt("SuperAppleRangeBonus");
	}
	
	/**
	 * 检查玩家是否有超级苹果效果
	 * @param player 玩家实体
	 * @return 是否有超级苹果效果
	 */
	public static boolean hasSuperAppleEffect(PlayerEntity player) {
		NbtCompound data = getPlayerData(player);
		return data.getBoolean("HasSuperAppleEffect");
	}
	
	/**
	 * 检查玩家是否有永久夜视效果
	 * @param player 玩家实体
	 * @return 是否有永久夜视效果
	 */
	public static boolean hasPermanentNightVision(PlayerEntity player) {
		NbtCompound data = getPlayerData(player);
		return data.getBoolean("HasPermanentNightVision");
	}
	
	/**
	 * 检查玩家是否有永久水下呼吸效果
	 * @param player 玩家实体
	 * @return 是否有永久水下呼吸效果
	 */
	public static boolean hasPermanentWaterBreathing(PlayerEntity player) {
		NbtCompound data = getPlayerData(player);
		return data.getBoolean("HasPermanentWaterBreathing");
	}
	
	/**
	 * 检查玩家是否有永久抗火效果
	 * @param player 玩家实体
	 * @return 是否有永久抗火效果
	 */
	public static boolean hasPermanentFireResistance(PlayerEntity player) {
		NbtCompound data = getPlayerData(player);
		return data.getBoolean("HasPermanentFireResistance");
	}
}