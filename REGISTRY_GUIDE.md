# Yuushya 注册表完整文档

> 本文档从代码实现角度，详细说明每个字段的含义、作用及填值规范。可用于 UI 界面设计参考。

---

## 目录

1. [文件结构](#1-文件结构)
2. [根结构](#2-根结构yuushyaregistrydata)
3. [创造栏 item_group](#3-创造栏注册item_group)
4. [方块类定义 _class](#4-方块类定义-_class)
5. [方块注册 block](#5-方块注册-block)
6. [物品注册 item](#6-物品注册-item)
7. [粒子注册 particle](#7-粒子注册-particle)
8. [模板系统 template](#8-模板系统-template)
9. [纹理系统 texture](#9-纹理系统-texture)
10. [自动生成 auto_generated](#10-自动生成-auto_generated)
11. [标签系统 tags](#11-标签系统-tags)
12. [碰撞系统 collision](#12-碰撞系统-collision)
13. [附录：所有 kit 类型详解](#13-附录所有-kit-类型详解)
14. [附录：所有枚举值](#14-附录所有枚举值)

---

## 1. 文件结构

```
common/src/main/resources/data/yuushya/register/
├── _class.json          # 方块类定义（模板）
├── block_xxx.json       # 各类方块注册（分类存放）
├── items.json           # 物品注册
├── item_group.json      # 创造栏定义
├── particle.json        # 粒子效果定义
├── texture.json         # 纹理注册（配合 template 使用）
├── texture_remain.json  # 纹理保留注册
├── template.json        # 模板定义
└── structure.json       # 结构实体定义
```

### 加载顺序

`ConfigReader.readRegistryConfig()` 按文件名 ASCII 顺序加载所有 `.json`，然后合并 `inner.json`。后加载的文件会覆盖先加载的同名字段。

---

## 2. 根结构 (YuushyaRegistryData)

```json
{
  "block": [...],        // 方块列表
  "item": [...],         // 物品列表
  "itemGroup": [...],     // 创造栏列表
  "particle": [...],     // 粒子列表
  "version": "1.0"       // 版本号（可选）
}
```

---

## 3. 创造栏注册 (item_group)

### 3.1 字段说明

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `name` | String | 是 | 创造栏唯一ID，全小写，下划线分隔 |
| `icon` | String | 是 | 用作图标的方块/物品注册名 |
| `blueprint` | String | 是 | 蓝图物品注册名（用于从该栏合成方块） |

### 3.2 代码处理

```java
// YuushyaDataProvider.add(Block).case Tags
// 每个方块自动注册到对应创造栏的标签
ResourceLocation groupKey = TagsData.addTag("block",
    ResourceLocation.parse("yuushya:groups/" + block.itemGroup),
    ResourceLocation.parse(MOD_ID, block.name), version);
```

### 3.3 已有创造栏

| name | icon 示例 | blueprint |
|------|----------|-----------|
| `extra_blocks` | yellow_acrylic | block_blueprint |
| `wood` | raw_oak_wood | wood_blueprint |
| `stone` | park_tile | stone_blueprint |
| `extra_shapes` | orange_tile_with_ridge_lower | extra_shapes_blueprint |
| `window` | white_windows_diagonal | window_blueprint |
| `furniture` | classroom_chair | furniture_blueprint |
| `fabric` | hanged_cloth | fabric_blueprint |
| `lighting` | desk_lamp_0 | lighting_blueprint |
| `electrical_appliance` | air_conditioning_condenser | lighting_blueprint |
| `living_being` | potted_jungle_sprout | living_being_blueprint |
| `signs` | sign_12 | sign_blueprint |
| `catering` | bottles | catering_blueprint |
| `store` | goods_shelf_jam | store_blueprint |
| `infrastructure` | fire_hydrant | facility_blueprint |
| `traffic` | electric_motorcycle | traffic_blueprint |
| `structure` | maple_tree | extra_shapes_blueprint |

---

## 4. 方块类定义 (_class)

### 4.1 class_type 字段的所有值

| class_type 值 | 含义 | 代码处理 |
|--------------|------|---------|
| `"class"` | **模板定义**，仅供继承，不生成方块 | 存入 `BlockClass` 映射 |
| `"template"` | **模板方块**，通过 `texture.forClass/forSpecified` 批量生成 | 调用 `generateTemplateProduct()` |
| `"remain"` | **保留方块**，不生成方块，使用现有纹理 | 存入 `BlockRemain`，设置默认纹理 |
| `"_comment"` | **注释**，不生成方块 | 跳过 |
| 其他字符串 | **继承对应类** | `GsonTools.extendJsonObject(blockObject, PREFER_FIRST_OBJ, BlockClass.get(classType))` |

### 4.2 类继承机制

```java
// ConfigReader.mergeYuushyaRegistryBlockJson() 第227行
if ("class".equals(classType))
    BlockClass.put(name, blockObject);  // 存入模板
else if (BlockClass.containsKey(classType))
    GsonTools.extendJsonObject(blockObject, ConflictStrategy.PREFER_FIRST_OBJ,
        BlockClass.get(classType));  // JSON值优先
```

**合并策略 `PREFER_FIRST_OBJ`**：JSON 文件中定义的值会覆盖类模板中的默认值。

### 4.3 _class.json 预定义类

| 类名 | 说明 | 默认 kit | 默认 collision |
|------|------|----------|---------------|
| `block` | 基础方块 | `block` | `block` |
| `HorizontalBlock` | 水平延伸 | `line` | `bound` |
| `VerticalBlock` | 垂直延伸 | `column` | `bound` |
| `CompactBlock` | 紧凑多方块 | `compact` | `bound` |
| `WoodBlock` | 木质方块 | `block` | `block` |
| `IntactBlock` | 完整单模型 | `normal` | `bound` |
| `PlainBlock` | 朴素方块 | `normal` | `block` |
| `TranslucentBlock` | 半透明 | `normal` | `face` |
| `FaceTranslucentBlock` | 面连接半透明 | `normal` | `face` |
| `WindowsBlock` | 窗户 | `normal` | `face` |
| `DecoBlock` | 装饰方块 | `normal` | `detail` |
| `NormalBlock` | 普通方块 | `normal` | `bound` |
| `FixedBlock` | 固定单模型 | `normal` | `bound` |
| `FaceBlock` | 面连接方块 | `face` | `bound` |
| `PlantBlock` | 植物方块 | `normal` | `block` |
| `ChairBlock` | 椅子方块 | `line` | `detail` |
| `RoofBlock` | 屋顶方块 | `line` | `bound` |
| `StairBlock` | 楼梯方块 | `VanillaStairBlock` | - |
| `FlowBlock` | 柱/杆方块 | `pole` | `block` |
| `CableBlock` | 线缆方块 | 无 | `face` |
| `CablePostBlock` | 线缆柱 | `normal` | `detail` |

---

## 5. 方块注册 (block)

### 5.1 完整字段列表

```json
{
  "class_type": "FixedBlock",      // 继承的类名
  "name": "marble_table",          // 方块唯一ID
  "item_group": "furniture",        // 所属创造栏
  "render_type": "cutout",          // 渲染类型
  "itemModel": "yuushya:...",       // 自定义物品模型
  "properties": { ... },            // 物理属性
  "blockstate": { ... },           // 方块状态配置
  "auto_generated": { ... },       // 自动生成配置
  "usage": { ... },                // 交互功能
  "colorTint": { ... },            // 颜色渲染
  "texture": { ... }               // 纹理配置
}
```

### 5.2 字段详解

#### 5.2.1 class_type

| 值 | 说明 |
|----|------|
| `"class"` | 定义模板类 |
| `"template"` | 模板方块 |
| `"remain"` | 保留方块 |
| `"_comment"` | 注释 |
| 其他 | 继承对应类 |

#### 5.2.2 render_type

| 值 | 说明 |
|----|------|
| `"cutout"` | 镂空渲染（无透明通道被丢弃） |
| `"translucent"` | 半透明渲染（支持透明通道） |
| `"cutout_mipped"` | 带 MIPmap 的镂空 |

#### 5.2.3 properties（物理属性）

```json
"properties": {
  "hardness": 0.7,              // 硬度（破坏时间）
  "resistance": 1.0,             // 抗性（爆炸抗性）
  "lightLevel": 0,                // 光照等级 (0-15)
  "ambientocclusionLightLevel": 0.0, // 环境光遮蔽亮度
  "sound": "stone",              // 破坏音效
  "material": "stone",           // 材质（影响外观和工具）
  "isSolid": true,               // 是否为固体（影响光照传播）
  "hasCollision": true,           // 是否有碰撞箱
  "isDelicate": false,           // 是否易碎（一击破坏）
  "offset": null,                // 偏移设置（未使用？）
  "lines": 1,                    // 物品提示显示行数
  "parent": null,                // 父方块（用于 StairBlock 等）
  "food": null                   // 食物属性
}
```

**sound 可选值**：`stone`、`wood`、`wool`、`gravel`、`sand`、`grass`、`metal`、`glass`、`cloth`、`sand`

**material 可选值**：`stone`、`wood`、`leaves`、`plants`、`misc`、`metal`、`sand`、`grass`、`snow`、`fluid`、`lava`、`web`、`solid`、`liquid`、`sponge`、`shulker`、`glass`、`piston`、`cake`、`coral`、`cactus`、`ice`、`snow_block`、`capped`、`frosted_ice`、`石头`、`木头`、`树叶`、`植物`、`混合`、`金属`、`沙子`、`草`、`雪`

#### 5.2.4 blockstate（方块状态配置）

**核心字段**：

| 字段 | 类型 | 说明 |
|------|------|------|
| `kit` | String | 方块状态类型，决定如何生成变体 |
| `forms` | List\<List\<String\>\> | 模型路径数组 |
| `states` | List\<String\> | 自定义属性列表 |
| `models` | List\<String\> | 模型列表（非 forms 模式使用） |

**kit 详解（见第13节）**

**forms 的结构**：

```
forms: List[List[String]]
├── 外层索引 → form 数量（决定生成 FORM2-FORM8 属性）
└── 内层索引 → 模型路径（在对应 form 下的变体）
```

#### 5.2.5 usage（交互功能）

```json
"usage": {
  "sitPos": [0.5, 0.5, 0.5],  // 坐的位置偏移 [x, y, z]
  "sound": "block.wood.hit",    // 交互音效
  "cycleForms": [0, 1]         // 可循环切换的 form 值列表
}
```

**sitPos**：玩家可以坐在方块上（需配合 `yuushya:chairs` 标签）

**cycleForms**：点击方块时，在指定 form 值之间循环切换

#### 5.2.6 colorTint（颜色渲染）

```json
"colorTint": {
  "colorType": "grass",       // 颜色类型
  "colorString": "#00FF00"      // 颜色字符串
}
```

#### 5.2.7 texture（纹理配置）

```json
"texture": {
  "value": "yuushya:block/concrete",     // 默认纹理值
  "type": "all",                         // 纹理类型
  "set": ["all#yuushya:block/concrete"],  // 纹理集
  "slots": ["all", "sides", "top"],      // 槽位列表（模板使用）
  "forClass": ["wool", "concrete"],      // 适用的纹理类型（模板使用）
  "forSpecified": ["block_name"]          // 指定的方块（模板使用）
}
```

---

## 6. 物品注册 (item)

### 6.1 字段说明

```json
{
  "classType": "BlueprintItem",  // 物品类
  "name": "furniture_blueprint", // 物品ID
  "itemGroup": "item",           // 创造栏
  "properties": { ... },          // 物品属性
  "usage": { ... }               // 使用效果
}
```

### 6.2 classType 物品类

| classType | 说明 |
|-----------|------|
| `""`（空） | 普通物品 |
| `"BlueprintItem"` | 蓝图物品（用于雕刻台合成） |
| `"HatItem"` | 帽子/装饰物品（穿戴在头上） |

### 6.3 properties（物品属性）

```json
"properties": {
  "maxCount": 64,          // 最大堆叠数量
  "maxDamage": 0,          // 最大耐久
  "rarity": "common",      // 稀有度：common/uncommon/rare/epic
  "equipment": null,       // 装备栏（未使用？）
  "fireProof": false,      // 是否防火
  "lines": 1,              // 提示行数
  "food": null             // 食物属性
}
```

**rarity 可选值**：`common`、`uncommon`、`rare`、`epic`

### 6.4 usage（物品使用效果）

```json
"usage": {
  "createNbt": "...",     // 创建时添加的NBT数据
  "cancelNbt": "..."       // 取消时移除的NBT数据
}
```

### 6.5 食物属性 food

```json
"food": {
  "nutrition": 4,          // 饱食度
  "saturation": 0.3,       // 饱和度
  "finishedItem": "bowl"    // 吃完后的物品
}
```

---

## 7. 粒子注册 (particle)

### 7.1 字段说明

```json
{
  "classType": "",         // 粒子类（通常为空）
  "name": "dust",          // 粒子ID
  "textures": ["yuushya:particle/dust"], // 纹理列表
  "spawner": { ... }       // 生成该粒子的方块
}
```

### 7.2 生成规则

```java
// ConfigReader.generateRegistries() 第120行
YuushyaDataProvider.of(particle.name)
    .version(version)
    .type(DataType.Particle)
    .json(() -> ParticleData.genParticleDescription(particle.textures))
    .save();
```

---

## 8. 模板系统 (template)

### 8.1 模板工作原理

1. 模板定义了一个"形状"（kit + forms）和"纹理槽位"（slots）
2. `texture.json` 中的方块提供了纹理（通过 `texture.type` 分类）
3. 系统自动为每个适用纹理生成对应的方块

### 8.2 模板字段

```json
{
  "name": "stairs",                 // 模板名
  "class_type": "template",         // 固定值
  "kit": "VanillaStairBlock",       // 形状类型
  "forms": [[...]],                 // 模型路径（模板路径）
  "itemModel": "yuushya:...",       // 物品模型
  "render_type": "cutout",
  "properties": { ... },
  "auto_generated": { ... },
  "item_group": "extra_shapes",
  "texture": {
    "slots": ["all", "sides", "top"],           // 可用槽位
    "for_class": ["wool", "concrete"]            // 适用的纹理类型
  }
}
```

### 8.3 模板处理代码

```java
// ConfigReader.generateRegistries() 第81行
List<String> templateModels = ...;
templateModels.forEach(name ->
    ModelData.setModelTemplate(name, templateBlock.texture.slots));

// 第99-112行：批量生成
for (YuushyaRegistryData.Block block : getTemplateUsageList(templateBlock)) {
    // block.name = "stairs_oak", "stairs_concrete", ...
    // 使用纹理值替换模板路径中的纹理槽位
    generateTemplateProduct(name, templateBlock, templateModels, blockNew, ...);
}
```

### 8.4 模板生成示例

**模板定义**：
```json
{
  "name": "stairs",
  "kit": "VanillaStairBlock",
  "forms": [["yuushya:template/stairs_inner", "yuushya:template/stairs", "yuushya:template/stairs_outer"]],
  "texture": {
    "slots": ["all", "sides", "top"],
    "for_class": ["misc", "wool", "decorative_wool", "raw_wood", "concrete"]
  }
}
```

**texture.json 中的注册**：
```json
{
  "class_type": "block",
  "name": "oak_planks",
  "texture": {
    "value": "yuushya:block/oak_planks",
    "type": "raw_wood"  // 与 for_class 匹配
  }
}
```

**生成结果**：`stairs_oak`，使用 oak_planks 的纹理。

---

## 9. 纹理系统 (texture)

### 9.1 texture.type 可选值

从 `texture.json` 和 `block.json` 中的使用情况：

| type | 说明 | 用于模板 |
|------|------|---------|
| `all` | 单纹理（全部面相同） | 是 |
| `misc` | 杂项 | 是 |
| `wool` | 羊毛 | 是 |
| `decorative_wool` | 装饰羊毛 | 是 |
| `raw_wood` | 原木/木板 | 是 |
| `concrete` | 混凝土 | 是 |
| `acrylic` | 亚克力 | 是 |

### 9.2 texture.value 和 texture.set

```json
"texture": {
  "value": "yuushya:block/concrete",    // 用于 kit=block 的方块模型纹理
  "set": ["all#yuushya:block/concrete"]  // 纹理映射
}
```

**set 格式**：`"槽位#纹理路径"`

---

## 10. 自动生成 (auto_generated)

### 10.1 字段说明

```json
"auto_generated": {
  "blockstate": true,         // 生成 blockstates/*.json
  "lootTable": true,          // 生成 loot_tables/blocks/*.json
  "itemModel": true,           // 生成 models/item/*.json
  "recipe": true,              // 生成 recipes/block/*.json
  "collision": "block",         // 碰撞类型
  "tags": [{"value": "mineable/pickaxe"}]  // 标签
}
```

### 10.2 自动生成的内容

| 字段 | 生成文件 | 代码位置 |
|------|----------|---------|
| `blockstate` | `blockstates/<name>.json` | `BlockStateData.genBlockState()` |
| `lootTable` | `loot_tables/blocks/<name>.json` | `LootTableData.genSingleItemTable()` |
| `itemModel` | `models/item/<name>.json` | `ModelData.genChildItemModel()` |
| `recipe` | `recipes/block/<name>.json` | `RecipeData.genStoneCutterRecipe()` |
| `tags` | `tags/block/<tag>.json` | `TagsData.genTags()` |

### 10.3 配方生成规则

```java
// RecipeData.genStoneCutterRecipe()
RecipeData.getBlueprint(block.itemGroup)  // 获取蓝图物品
// 从蓝图物品用石切器合成，产量由 getResultNumber() 决定
```

**resultNumber 规则**：
- `yuushya_template` → 1
- 其他 → 4

---

## 11. 标签系统 (tags)

### 11.1 auto_generated.tags 格式

```json
"tags": [
  {"type": "block", "value": "mineable/pickaxe"}
]
```

### 11.2 标签类型 type

| type | 说明 |
|------|------|
| `"block"` 或 `"blocks"` | 方块标签 |
| `"item"` 或 `"items"` | 物品标签 |
| `"entity"` 或 `"entity_types"` | 实体标签 |
| 其他 | 见 `TagsData.Type` 枚举 |

### 11.3 常用标签值

| value | 说明 |
|-------|------|
| `mineable/pickaxe` | 可用镐挖掘 |
| `mineable/axe` | 可用斧挖掘 |
| `mineable/hoe` | 可用锄挖掘 |
| `yuushya:windows` | 窗户方块 |
| `yuushya:chairs` | 椅子方块（可坐） |
| `yuushya:plants` | 植物方块 |
| `yuushya:interact_able` | 可交互方块 |
| `climbable` | 可攀爬 |

### 11.4 自动生成的标签

每个方块还会自动注册到：
1. `yuushya:groups/<itemGroup>` - 所属创造栏
2. `yuushya:interact_able` - 如果有 `usage` 字段

---

## 12. 碰撞系统 (collision)

### 12.1 collision 类型

| 值 | 说明 | 代码处理 |
|----|------|---------|
| `"block"` | 使用方块完整碰撞箱 | `Shapes.block()` |
| `"bound"` | 使用模型边界框 | `Shapes.create()` |
| `"face"` | 仅面碰撞（贴墙物品） | `Shapes.block()` 限制 |
| `"detail"` | 使用碰撞文件定义的精细碰撞 | `CollisionFileReader` |

### 12.2 碰撞文件格式

```json
{
  "blockstates": [
    {
      "variant": "xpos=east,zpos=middle",  // 必须与 blockstate 变体匹配
      "collision": [
        {"from": [0.0, 13.5, 0.0], "to": [16.0, 16.0, 16.0]}
      ]
    }
  ],
  "children": ["other_block_name"]  // 其他使用相同碰撞的方块
}
```

---

## 13. 附录：所有 kit 类型详解

### 13.1 kit 与 BlockStateData 生成对应表

| kit | 运行时类 | BlockState 属性 | 变体数量计算 |
|-----|----------|----------------|-------------|
| `normal` | NormalBlock | HORIZONTAL_FACING + **FORM** | 4 × forms.size() |
| `attachment` | AttachmentBlock | FACE + FACING + **FORM** | 3 × 4 × forms.size() |
| `line` | LineBlock | HORIZONTAL_FACING + **FORM** + POS_HORIZON | 4 × forms × 4 |
| `line_corner` | LineCornerBlock | HORIZONTAL_FACING + **FORM** + POS_HORIZON + SHAPE | 4 × forms × 8 |
| `line_cross` | LineCrossBlock | HORIZONTAL_FACING + **FORM** + POS_HORIZON + FRONT + BACK | 4 × forms × 36 |
| `line_cross_simple` | LineCrossBlock | HORIZONTAL_FACING + **FORM** + POS_HORIZON + FRONT + BACK | 4 × forms × 10 |
| `pole_line` | PoleLineBlock | HORIZONTAL_FACING + **FORM** + POS_HORIZON + YPOS | 4 × forms × 16 |
| `face` | FaceBlock | **FORM** + XPOS + ZPOS | forms × 16 |
| `compact` | CompactBlock | **FORM** + XPOS + ZPOS | forms × 16 |
| `pole` | PoleBlock | HORIZONTAL_FACING + **FORM** + POS_VERTICAL | 4 × forms × 4 |
| `column` | ColumnBlock | **FORM** + POS_VERTICAL | forms × 4 |
| `tri_part` | TriPartBlock | HORIZONTAL_FACING + **FORM** + POS_VERTICAL | 4 × forms × 4 |
| `VanillaSlabBlock` | SlabBlock | SLAB_TYPE | 3 |
| `HalfSlabBlock` | HalfSlabBlock | HS_TYPE + HS_OTHER | 16 |
| `VanillaStairBlock` | StairBlock | HORIZONTAL_FACING + HALF + STAIRS_SHAPE | 96 |
| `VanillaDoorBlock` | DoorBlock | HORIZONTAL_FACING + DOUBLE_BLOCK_HALF + DOOR_HINGE + OPEN | 32 |
| `VanillaSnowLayerBlock` | SnowLayerLikeBlock | LAYERS | 8 |
| `block` | 无（普通方块） | 无 | 1 |

### 13.2 forms 排列规则详解

**关键代码**：`BlockStateData.genBlockState()` 第82-250行

#### 13.2.1 POS_HORIZON 索引（kit=line）

```
位置:  left    middle   right    none
索引:   0       1        2        3
```

#### 13.2.2 POS_VERTICAL 索引（kit=column/pole）

```
位置:  top     middle   bottom   none
索引:   0       1        2        3
```

#### 13.2.3 XPOS × ZPOS 索引（kit=face/compact）

```
XPOS: west=0, east=1, middle=2, none=3
ZPOS: north=0, south=1, middle=2, none=3

组合查表 (createXYPosVariant):
- none+none → 模型[0]
- 单侧 → 模型[1]
- 双侧同向(middle) → 模型[2]
- 双侧垂直(both) → 模型[3]
```

### 13.3 kit 详细说明

#### `normal` - 普通/无连接

```json
"blockstate": {
  "kit": "normal",
  "forms": [["model1"], ["model2"]]  // 2个形态
}
```

- 无面连接
- 4 个水平朝向（north/south/east/west）
- 如果 forms > 1，添加 FORM 属性
- 用途：独立的装饰方块

#### `line` - 水平线条连接

```json
"blockstate": {
  "kit": "line",
  "forms": [[
    "yuushya:left", "yuushya:middle", "yuushya:right", "yuushya:none"
  ]]
}
```

- 水平方向连接（左右）
- 根据 `HORIZONTAL_FACING` 确定"前"
- 自动检测左右两侧是否有相同方块
- 用途：桌子、货架、告示牌

#### `face` - 面连接

```json
"blockstate": {
  "kit": "face",
  "forms": [[
    "yuushya:none", "yuushya:single", "yuushya:middle", "yuushya:both"
  ]]
}
```

- X 方向和 Z 方向分别检测
- 两方向都检测相邻方块是否为同一类型
- 用途：桌面（根据周围桌块自动选择模型）

#### `compact` - 紧凑面连接

- 与 `face` 相同，但用于多方块结构
- 用途：组合柜、格子架

#### `pole` - 垂直柱连接

```json
"blockstate": {
  "kit": "pole",
  "forms": [[
    "yuushya:top", "yuushya:middle", "yuushya:bottom", "yuushya:none"
  ]]
}
```

- 垂直方向连接（上下）
- 用途：柱子、杆子

#### `column` - 柱（无朝向）

```json
"blockstate": {
  "kit": "column",
  "forms": [["yuushya:top"], ["yuushya:middle"], ["yuushya:bottom"], ["yuushya:none"]]
}
```

- 与 `pole` 类似，但无朝向属性
- 仅检测上下

#### `VanillaDoorBlock` - 原版门

```json
"blockstate": {
  "kit": "VanillaDoorBlock",
  "forms": [[
    "yuushya:bottom",      // 左铰链关闭
    "yuushya:bottom_hinge", // 右铰链关闭
    "yuushya:top",         // 上半部
    "yuushya:top_hinge"    // 上半部右铰链
  ]]
}
```

- 生成 32 种状态
- 支持旋转、开启/关闭、铰链位置

#### `VanillaStairBlock` - 原版楼梯

```json
"blockstate": {
  "kit": "VanillaStairBlock",
  "forms": [[
    "yuushya:inner", "yuushya:straight", "yuushya:outer"
  ]]
}
```

- 生成 96 种状态
- 支持直型、内弯、外弯

---

## 14. 附录：所有枚举值

### 14.1 PositionDirectionXState（X方向位置）

| 值 | 说明 |
|----|------|
| `west` | 西侧有连接 |
| `east` | 东侧有连接 |
| `middle` | 两侧都有 |
| `none` | 两侧都没有 |

### 14.2 PositionDirectionZState（Z方向位置）

| 值 | 说明 |
|----|------|
| `north` | 北侧有连接 |
| `south` | 南侧有连接 |
| `middle` | 两侧都有 |
| `none` | 两侧都没有 |

### 14.3 PositionHorizonState（水平位置）

| 值 | 说明 |
|----|------|
| `left` | 左侧有连接 |
| `middle` | 左右都有 |
| `right` | 右侧有连接 |
| `none` | 左右都没有 |

### 14.4 PositionVerticalState（垂直位置）

| 值 | 说明 |
|----|------|
| `top` | 上方有连接 |
| `middle` | 上下都有 |
| `bottom` | 下方有连接 |
| `none` | 上下都没有 |

### 14.5 ConnectedHorizonState（水平连接）

| 值 | 说明 |
|----|------|
| `left` | 左侧有 |
| `right` | 右侧有 |
| `none` | 都没有 |

### 14.6 ShapeState（楼梯形状）

| 值 | 说明 |
|----|------|
| `straight` | 直型楼梯 |
| `inner` | 内弯楼梯 |
| `outer` | 外弯楼梯 |

### 14.7 HalfSlabState（半砖状态）

| 值 | 说明 |
|----|------|
| `top` | 顶部 |
| `bottom` | 底部 |
| `both` | 两层（完整方块） |
| `none` | 没有 |

### 14.8 FORM 属性（形态数量）

| forms.size() | 属性名 | 值范围 |
|--------------|--------|--------|
| 1 | 无 | 无 |
| 2 | FORM2 | 0-1 |
| 3 | FORM3 | 0-2 |
| 4 | FORM4 | 0-3 |
| 5 | FORM5 | 0-4 |
| 6 | FORM6 | 0-5 |
| 7 | FORM7 | 0-6 |
| 8+ | FORM8 | 0-7 |

---

## 15. UI 设计建议

### 15.1 方块编辑表单字段

| 分组 | 字段 | 类型 | 说明 |
|------|------|------|------|
| 基本 | name | String | 唯一ID，自动校验格式 |
| 基本 | class_type | Select | 继承的类/模板/注释 |
| 基本 | item_group | Select | 所属创造栏 |
| 外观 | render_type | Select | cutout/translucent/cutout_mipped |
| 外观 | blockstate.kit | Select | normal/line/face/column/... |
| 外观 | blockstate.forms | JSON Editor | 模型路径数组 |
| 外观 | itemModel | String | 自定义物品模型 |
| 外观 | colorTint | Object | 颜色配置 |
| 物理 | properties.hardness | Number | 0.0-∞ |
| 物理 | properties.resistance | Number | 0.0-∞ |
| 物理 | properties.material | Select | stone/wood/... |
| 物理 | properties.sound | Select | stone/wood/wool/... |
| 物理 | properties.isSolid | Checkbox | 是否固体 |
| 物理 | properties.hasCollision | Checkbox | 是否有碰撞 |
| 物理 | properties.isDelicate | Checkbox | 是否易碎 |
| 物理 | properties.lightLevel | Slider | 0-15 |
| 物理 | properties.lines | Number | 提示行数 |
| 物理 | properties.food | Object | 食物属性 |
| 交互 | usage.sitPos | Vector3 | 坐的位置 |
| 交互 | usage.sound | Select | 音效 |
| 交互 | usage.cycleForms | Number[] | 可切换形态 |
| 自动生成 | auto_generated.* | Checkbox | 各自动生成选项 |
| 自动生成 | auto_generated.tags | Tag Input | 标签列表 |
| 高级 | texture | Object | 纹理配置 |
| 高级 | parent | String | 父方块 |

### 15.2 kit 选择指导

| 需求 | 推荐 kit |
|------|---------|
| 独立方块，无连接 | `normal` 或 `block` |
| 水平延伸（左右连接） | `line` |
| 垂直延伸（上下连接） | `column` 或 `pole` |
| 面连接（桌面效果） | `face` |
| 多方块组合 | `compact` |
| 原版门 | `VanillaDoorBlock` |
| 原版楼梯 | `VanillaStairBlock` |
| 原版半砖 | `VanillaSlabBlock` |

### 15.3 forms 预览

建议在 UI 中实时预览 forms 生成的方块状态数量：

```
forms: [[A, B, C, D]] + kit: "line"
→ 生成 4(forms) × 4(FACING) × 4(POS_HORIZON) = 64 种状态
```
