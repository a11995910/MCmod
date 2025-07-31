# SuperCraft模组 - API兼容性问题分析

## 📋 概述

本文档详细分析了SuperCraft模组在Minecraft 1.21.x版本中遇到的API兼容性问题，以及这些问题对模组功能的具体影响。

## ⚠️ 主要问题分析

### 1. 装备强化系统问题

**问题位置**: `EnhancementRecipeType.java`

#### 🔴 合成台检测功能失效

**原因**:
- Minecraft 1.21.x移除了`getCraftingInventory()`方法
- `CraftingInventory`类的API发生重大变化
- 无法直接访问玩家的合成台物品栏

**当前状态**:
```java
// 第47行 - 合成台检测被强制禁用
if (false) { // 暂时禁用合成台检测
    // TODO: 在后续版本中重新实现合成台检测
```

**影响**:
- ❌ 无法自动检测玩家合成台中的强化配方
- ❌ 三个相同装备合成强化装备的核心功能无法工作
- ❌ 玩家无法通过合成台进行装备强化

#### 🔴 工具类型检查失效

**问题代码**:
```java
// 第74-82行 - 工具类型检查被注释
// TODO: 适配新的物品类型API
// if (!(item instanceof ToolItem)) {
//     return false;
// }
```

**原因**:
- `ToolItem`类在1.21.x中被重构或移除
- 物品类型检查API发生变化

**影响**:
- ❌ 无法正确识别工具类型物品
- ❌ 可能导致非工具物品也能被强化
- ❌ 强化系统的安全性和逻辑性受损

### 2. NBT数据系统问题

**问题位置**: `EnhancementRecipeType.java`、`PlayerDataUtil.java`

#### 🔴 NBT数据存储方法过时

**问题代码**:
```java
// EnhancementRecipeType.java 第97-101行
private int getEnhancementLevel(ItemStack stack) {
    // TODO: 使用新的组件系统替代NBT
    return 0; // 暂时返回0
}

// 第134-145行 - 属性增强被禁用
private void enhanceAttributes(ItemStack stack, int level) {
    // TODO: 使用新的组件系统实现属性增强
    // 原有的NBT数据存储逻辑已注释
}
```

**原因**:
- Minecraft 1.21.x引入了新的组件系统(Component System)
- 传统的NBT数据存储方法`getOrCreateNbt()`被移除
- 需要迁移到`DataComponentType`系统

**影响**:
- ❌ 无法保存装备的强化等级
- ❌ 强化属性无法持久化
- ❌ 装备重启游戏后会丢失强化效果

#### 🔴 玩家数据持久化风险

**问题位置**: `PlayerDataUtil.java`

**潜在问题**:
```java
// 第42-50行 - 使用过时的NBT读写方法
public static void savePlayerData(PlayerEntity player, NbtCompound data) {
    if (player instanceof ServerPlayerEntity serverPlayer) {
        NbtCompound playerNbt = serverPlayer.writeNbt(new NbtCompound());
        playerNbt.put(SUPERCRAFT_DATA_KEY, data);
        serverPlayer.readNbt(playerNbt); // 可能导致数据丢失
    }
}
```

**风险**:
- ⚠️ 直接操作玩家NBT可能导致数据不一致
- ⚠️ 在某些情况下可能导致玩家数据损坏
- ⚠️ 服务器重启后数据可能无法正确恢复

### 3. Mixin注入兼容性问题

**问题位置**: `PlayerEntityMixin.java`

#### 🟡 伤害系统API变化

**已修复问题**:
```java
// 第75行 - 已适配新的伤害API
if (entity instanceof LivingEntity livingEntity && player.getWorld() instanceof ServerWorld serverWorld) {
    livingEntity.damage(serverWorld, player.getDamageSources().playerAttack(player), 4.0f);
}
```

**说明**:
- ✅ 已成功适配1.21.x的伤害系统API
- ✅ 添加了`ServerWorld`参数要求
- ✅ 使用了新的伤害源系统

## 🎯 功能影响评估

### 完全失效的功能

1. **装备强化合成** - 🔴 100%失效
   - 无法检测合成台配方
   - 无法执行强化逻辑

2. **强化等级保存** - 🔴 100%失效
   - 无法读取/保存强化等级
   - 强化效果无法持久化

3. **工具类型验证** - 🔴 100%失效
   - 无法正确识别工具类型
   - 安全性检查失效

### 部分受影响的功能

1. **玩家数据持久化** - 🟡 50%风险
   - 当前仍可工作，但存在数据丢失风险
   - 需要迁移到更安全的数据存储方式

2. **超级食物效果** - 🟢 90%正常
   - 基础功能正常工作
   - 范围攻击效果正常

### 正常工作的功能

1. **自定义物品注册** - 🟢 100%正常
2. **物品模型和贴图** - 🟢 100%正常
3. **合成配方** - 🟢 100%正常
4. **本地化文本** - 🟢 100%正常
5. **Mixin注入** - 🟢 100%正常

## 🔧 解决方案规划

### 短期解决方案（v1.1.0）

1. **实现新的组件系统**
   ```java
   // 使用DataComponentType替代NBT
   public static final ComponentType<Integer> ENHANCEMENT_LEVEL = 
       ComponentType.<Integer>builder().codec(Codec.INT).build();
   ```

2. **重构合成台检测**
   ```java
   // 使用新的合成事件系统
   CraftingEvents.CRAFT.register((inventory, recipe, result) -> {
       // 检测强化配方
   });
   ```

3. **适配新的物品类型API**
   ```java
   // 使用新的物品标签系统
   if (stack.isIn(ItemTags.TOOLS)) {
       // 工具类型检查
   }
   ```

### 中期解决方案（v1.2.0）

1. **完整的数据持久化系统**
2. **高级强化属性系统**
3. **GUI界面支持**

### 长期解决方案（v2.0.0）

1. **完全重构为1.21.x原生架构**
2. **性能优化和稳定性提升**
3. **扩展功能和新特性**

## 📊 开发优先级

| 优先级 | 功能 | 预计工作量 | 影响程度 |
|--------|------|------------|----------|
| 🔴 高 | 组件系统迁移 | 3-5天 | 核心功能 |
| 🔴 高 | 合成台检测重构 | 2-3天 | 核心功能 |
| 🟡 中 | 工具类型检查 | 1-2天 | 安全性 |
| 🟡 中 | 数据持久化优化 | 2-3天 | 稳定性 |
| 🟢 低 | 性能优化 | 1-2天 | 用户体验 |

## 🚨 风险提示

1. **数据丢失风险**: 当前版本的玩家数据可能在某些情况下丢失
2. **功能缺失**: 核心的装备强化功能完全无法使用
3. **安全性问题**: 缺少工具类型检查可能导致意外行为
4. **兼容性问题**: 未来Minecraft版本更新可能导致更多API变化

## 📝 建议

### 对用户

1. **当前版本仅适合体验基础功能**（超级食物）
2. **避免在重要存档中使用**，等待稳定版本
3. **定期备份存档数据**

### 对开发者

1. **优先修复核心功能**（组件系统、合成检测）
2. **建立完整的测试环境**
3. **关注Fabric API更新**，及时适配新特性
4. **考虑向后兼容性**，为老版本用户提供迁移路径

---

*本文档将随着开发进度持续更新*

**最后更新**: 2024年12月
**文档版本**: 1.0
**对应模组版本**: SuperCraft v1.0.0