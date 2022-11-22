package net.geforcemods.securitycraft;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Predicates;

import net.geforcemods.securitycraft.api.NamedBlockEntity;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.geforcemods.securitycraft.blockentities.AllowlistOnlyBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockPocketBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.geforcemods.securitycraft.blockentities.CageTrapBlockEntity;
import net.geforcemods.securitycraft.blockentities.ClaymoreBlockEntity;
import net.geforcemods.securitycraft.blockentities.DisguisableBlockEntity;
import net.geforcemods.securitycraft.blockentities.DisplayCaseBlockEntity;
import net.geforcemods.securitycraft.blockentities.GlowDisplayCaseBlockEntity;
import net.geforcemods.securitycraft.blockentities.IMSBlockEntity;
import net.geforcemods.securitycraft.blockentities.InventoryScannerBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeyPanelBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeycardReaderBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadBlastFurnaceBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadChestBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadDoorBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadFurnaceBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadSmokerBlockEntity;
import net.geforcemods.securitycraft.blockentities.LaserBlockBlockEntity;
import net.geforcemods.securitycraft.blockentities.MotionActivatedLightBlockEntity;
import net.geforcemods.securitycraft.blockentities.PortableRadarBlockEntity;
import net.geforcemods.securitycraft.blockentities.ProjectorBlockEntity;
import net.geforcemods.securitycraft.blockentities.ProtectoBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedCauldronBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedHopperBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedIronBarsBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedPistonMovingBlockEntity;
import net.geforcemods.securitycraft.blockentities.RetinalScannerBlockEntity;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity;
import net.geforcemods.securitycraft.blockentities.ScannerDoorBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecretSignBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.geforcemods.securitycraft.blockentities.TrackMineBlockEntity;
import net.geforcemods.securitycraft.blockentities.TrophySystemBlockEntity;
import net.geforcemods.securitycraft.blockentities.UsernameLoggerBlockEntity;
import net.geforcemods.securitycraft.blockentities.ValidationOwnableBlockEntity;
import net.geforcemods.securitycraft.blocks.AbstractKeypadFurnaceBlock;
import net.geforcemods.securitycraft.blocks.AlarmBlock;
import net.geforcemods.securitycraft.blocks.BlockChangeDetectorBlock;
import net.geforcemods.securitycraft.blocks.BlockPocketBlock;
import net.geforcemods.securitycraft.blocks.BlockPocketManagerBlock;
import net.geforcemods.securitycraft.blocks.BlockPocketWallBlock;
import net.geforcemods.securitycraft.blocks.CageTrapBlock;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.blocks.DisplayCaseBlock;
import net.geforcemods.securitycraft.blocks.FakeLavaBlock;
import net.geforcemods.securitycraft.blocks.FakeWaterBlock;
import net.geforcemods.securitycraft.blocks.FrameBlock;
import net.geforcemods.securitycraft.blocks.InventoryScannerBlock;
import net.geforcemods.securitycraft.blocks.InventoryScannerFieldBlock;
import net.geforcemods.securitycraft.blocks.IronFenceBlock;
import net.geforcemods.securitycraft.blocks.KeyPanelBlock;
import net.geforcemods.securitycraft.blocks.KeycardReaderBlock;
import net.geforcemods.securitycraft.blocks.KeypadBlastFurnaceBlock;
import net.geforcemods.securitycraft.blocks.KeypadBlock;
import net.geforcemods.securitycraft.blocks.KeypadChestBlock;
import net.geforcemods.securitycraft.blocks.KeypadDoorBlock;
import net.geforcemods.securitycraft.blocks.KeypadFurnaceBlock;
import net.geforcemods.securitycraft.blocks.KeypadSmokerBlock;
import net.geforcemods.securitycraft.blocks.LaserBlock;
import net.geforcemods.securitycraft.blocks.LaserFieldBlock;
import net.geforcemods.securitycraft.blocks.MotionActivatedLightBlock;
import net.geforcemods.securitycraft.blocks.PanicButtonBlock;
import net.geforcemods.securitycraft.blocks.PortableRadarBlock;
import net.geforcemods.securitycraft.blocks.ProjectorBlock;
import net.geforcemods.securitycraft.blocks.ProtectoBlock;
import net.geforcemods.securitycraft.blocks.RetinalScannerBlock;
import net.geforcemods.securitycraft.blocks.RiftStabilizerBlock;
import net.geforcemods.securitycraft.blocks.ScannerDoorBlock;
import net.geforcemods.securitycraft.blocks.SecretStandingSignBlock;
import net.geforcemods.securitycraft.blocks.SecretWallSignBlock;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.blocks.SentryDisguiseBlock;
import net.geforcemods.securitycraft.blocks.SonicSecuritySystemBlock;
import net.geforcemods.securitycraft.blocks.TrophySystemBlock;
import net.geforcemods.securitycraft.blocks.UsernameLoggerBlock;
import net.geforcemods.securitycraft.blocks.mines.BaseFullMineBlock;
import net.geforcemods.securitycraft.blocks.mines.BouncingBettyBlock;
import net.geforcemods.securitycraft.blocks.mines.ClaymoreBlock;
import net.geforcemods.securitycraft.blocks.mines.DeepslateMineBlock;
import net.geforcemods.securitycraft.blocks.mines.FallingBlockMineBlock;
import net.geforcemods.securitycraft.blocks.mines.FurnaceMineBlock;
import net.geforcemods.securitycraft.blocks.mines.IMSBlock;
import net.geforcemods.securitycraft.blocks.mines.MineBlock;
import net.geforcemods.securitycraft.blocks.mines.RedstoneOreMineBlock;
import net.geforcemods.securitycraft.blocks.mines.TrackMineBlock;
import net.geforcemods.securitycraft.blocks.reinforced.BaseReinforcedBlock;
import net.geforcemods.securitycraft.blocks.reinforced.HorizontalReinforcedIronBars;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedAmethystBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedBookshelfBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedButtonBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedCarpetBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedCauldronBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedCauldronBlock.IReinforcedCauldronInteraction;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedChainBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedCobwebBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedCryingObsidianBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedDirtPathBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedDoorBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedEndRodBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedFallingBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedFenceGateBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedGlassBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedGlazedTerracottaBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedHopperBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedIronBarsBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedIronTrapDoorBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedLanternBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedLavaCauldronBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedLayeredCauldronBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedLeverBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedMovingPistonBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedMud;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedNyliumBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedObserverBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedObsidianBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedPaneBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedPistonBaseBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedPistonHeadBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedPressurePlateBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedRedstoneBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedRedstoneLampBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedRootedDirtBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedRotatedCrystalQuartzPillar;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedRotatedPillarBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedSlabBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedSnowyDirtBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedStainedGlassBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedStainedGlassPaneBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedStairsBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedTintedGlassBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedWallBlock;
import net.geforcemods.securitycraft.entity.BouncingBetty;
import net.geforcemods.securitycraft.entity.Bullet;
import net.geforcemods.securitycraft.entity.IMSBomb;
import net.geforcemods.securitycraft.entity.Sentry;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.fluids.FakeLavaFluid;
import net.geforcemods.securitycraft.fluids.FakeWaterFluid;
import net.geforcemods.securitycraft.inventory.BlockChangeDetectorMenu;
import net.geforcemods.securitycraft.inventory.BlockPocketManagerMenu;
import net.geforcemods.securitycraft.inventory.BlockReinforcerMenu;
import net.geforcemods.securitycraft.inventory.BriefcaseContainer;
import net.geforcemods.securitycraft.inventory.BriefcaseMenu;
import net.geforcemods.securitycraft.inventory.CustomizeBlockMenu;
import net.geforcemods.securitycraft.inventory.DisguiseModuleMenu;
import net.geforcemods.securitycraft.inventory.InventoryScannerMenu;
import net.geforcemods.securitycraft.inventory.KeycardReaderMenu;
import net.geforcemods.securitycraft.inventory.KeypadBlastFurnaceMenu;
import net.geforcemods.securitycraft.inventory.KeypadFurnaceMenu;
import net.geforcemods.securitycraft.inventory.KeypadSmokerMenu;
import net.geforcemods.securitycraft.inventory.ModuleItemContainer;
import net.geforcemods.securitycraft.inventory.ProjectorMenu;
import net.geforcemods.securitycraft.items.AdminToolItem;
import net.geforcemods.securitycraft.items.BriefcaseItem;
import net.geforcemods.securitycraft.items.CameraMonitorItem;
import net.geforcemods.securitycraft.items.CodebreakerItem;
import net.geforcemods.securitycraft.items.DisplayCaseItem;
import net.geforcemods.securitycraft.items.FakeLiquidBucketItem;
import net.geforcemods.securitycraft.items.KeyPanelItem;
import net.geforcemods.securitycraft.items.KeycardItem;
import net.geforcemods.securitycraft.items.KeypadChestItem;
import net.geforcemods.securitycraft.items.MineRemoteAccessToolItem;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.items.PortableTunePlayerItem;
import net.geforcemods.securitycraft.items.SCManualItem;
import net.geforcemods.securitycraft.items.SecretSignItem;
import net.geforcemods.securitycraft.items.SentryItem;
import net.geforcemods.securitycraft.items.SentryRemoteAccessToolItem;
import net.geforcemods.securitycraft.items.SonicSecuritySystemItem;
import net.geforcemods.securitycraft.items.TaserItem;
import net.geforcemods.securitycraft.items.UniversalBlockModifierItem;
import net.geforcemods.securitycraft.items.UniversalBlockReinforcerItem;
import net.geforcemods.securitycraft.items.UniversalBlockRemoverItem;
import net.geforcemods.securitycraft.items.UniversalKeyChangerItem;
import net.geforcemods.securitycraft.items.UniversalOwnerChangerItem;
import net.geforcemods.securitycraft.misc.LimitedUseKeycardRecipe;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.OwnerDataSerializer;
import net.geforcemods.securitycraft.misc.PageGroup;
import net.geforcemods.securitycraft.misc.conditions.BlockEntityNBTCondition;
import net.geforcemods.securitycraft.util.HasManualPage;
import net.geforcemods.securitycraft.util.OwnableBE;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.RegisterItemBlock;
import net.geforcemods.securitycraft.util.RegisterItemBlock.SCItemGroup;
import net.geforcemods.securitycraft.util.Reinforced;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.PressurePlateBlock.Sensitivity;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistries.Keys;
import net.minecraftforge.registries.RegistryObject;

public class SCContent {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, SecurityCraft.MODID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, SecurityCraft.MODID);
	public static final DeferredRegister<EntityDataSerializer<?>> DATA_SERIALIZERS = DeferredRegister.create(Keys.ENTITY_DATA_SERIALIZERS, SecurityCraft.MODID);
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, SecurityCraft.MODID);
	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, SecurityCraft.MODID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SecurityCraft.MODID);
	public static final DeferredRegister<LootItemConditionType> LOOT_ITEM_CONDITION_TYPES = DeferredRegister.create(Registry.LOOT_ITEM_REGISTRY, SecurityCraft.MODID);
	public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, SecurityCraft.MODID);
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registry.RECIPE_SERIALIZER_REGISTRY, SecurityCraft.MODID);
	public static final String KEYPAD_CHEST_PATH = "keypad_chest";
	public static final String DISPLAY_CASE_PATH = "display_case";
	public static final String GLOW_DISPLAY_CASE_PATH = "glow_display_case";

	//loot item condition types
	public static final RegistryObject<LootItemConditionType> BLOCK_ENTITY_NBT = LOOT_ITEM_CONDITION_TYPES.register("tile_entity_nbt", () -> new LootItemConditionType(new BlockEntityNBTCondition.ConditionSerializer()));

	//recipe serializers
	public static final RegistryObject<SimpleRecipeSerializer<LimitedUseKeycardRecipe>> LIMITED_USE_KEYCARD_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("limited_use_keycard_recipe", () -> new SimpleRecipeSerializer<>(LimitedUseKeycardRecipe::new));

	//data serializer entries
	public static final RegistryObject<EntityDataSerializer<Owner>> OWNER_SERIALIZER = DATA_SERIALIZERS.register("owner", () -> new OwnerDataSerializer());

	//fluids
	public static final RegistryObject<FlowingFluid> FLOWING_FAKE_WATER = FLUIDS.register("flowing_fake_water", () -> new FakeWaterFluid.Flowing(fakeWaterProperties()));
	public static final RegistryObject<FlowingFluid> FAKE_WATER = FLUIDS.register("fake_water", () -> new FakeWaterFluid.Source(fakeWaterProperties()));
	public static final RegistryObject<FlowingFluid> FLOWING_FAKE_LAVA = FLUIDS.register("flowing_fake_lava", () -> new FakeLavaFluid.Flowing(fakeLavaProperties()));
	public static final RegistryObject<FlowingFluid> FAKE_LAVA = FLUIDS.register("fake_lava", () -> new FakeLavaFluid.Source(fakeLavaProperties()));

	//blocks
	@HasManualPage
	@RegisterItemBlock
	public static final RegistryObject<Block> ALARM = BLOCKS.register("alarm", () -> new AlarmBlock(prop(Material.METAL).lightLevel(state -> state.getValue(AlarmBlock.LIT) ? 15 : 0)));
	@HasManualPage
	@RegisterItemBlock
	public static final RegistryObject<Block> BLOCK_CHANGE_DETECTOR = BLOCKS.register("block_change_detector", () -> new BlockChangeDetectorBlock(propDisguisable()));
	@HasManualPage(designedBy = "Henzoid")
	@RegisterItemBlock
	public static final RegistryObject<Block> BLOCK_POCKET_MANAGER = BLOCKS.register("block_pocket_manager", () -> new BlockPocketManagerBlock(prop()));
	@HasManualPage
	@RegisterItemBlock(SCItemGroup.DECORATION)
	public static final RegistryObject<Block> BLOCK_POCKET_WALL = BLOCKS.register("block_pocket_wall", () -> new BlockPocketWallBlock(prop().noCollission().isRedstoneConductor((s, w, p) -> false).isSuffocating(BlockPocketWallBlock::causesSuffocation).isViewBlocking(BlockPocketWallBlock::causesSuffocation)));
	@HasManualPage
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> BOUNCING_BETTY = BLOCKS.register("bouncing_betty", () -> new BouncingBettyBlock(prop(Material.DECORATION, 1.0F)));
	@HasManualPage
	@RegisterItemBlock
	public static final RegistryObject<Block> CAGE_TRAP = BLOCKS.register("cage_trap", () -> new CageTrapBlock(propDisguisable(Material.METAL).sound(SoundType.METAL).noCollission()));
	@RegisterItemBlock(SCItemGroup.DECORATION)
	public static final RegistryObject<Block> CHISELED_CRYSTAL_QUARTZ = BLOCKS.register("chiseled_crystal_quartz", () -> new Block(Block.Properties.of(Material.STONE).strength(0.8F).requiresCorrectToolForDrops()));
	@HasManualPage
	@RegisterItemBlock(SCItemGroup.DECORATION)
	public static final RegistryObject<Block> CRYSTAL_QUARTZ = BLOCKS.register("crystal_quartz", () -> new Block(Block.Properties.of(Material.STONE).strength(0.8F).requiresCorrectToolForDrops()));
	@RegisterItemBlock(SCItemGroup.DECORATION)
	public static final RegistryObject<Block> CRYSTAL_QUARTZ_PILLAR = BLOCKS.register("crystal_quartz_pillar", () -> new RotatedPillarBlock(Block.Properties.of(Material.STONE).strength(0.8F).requiresCorrectToolForDrops()));
	@RegisterItemBlock(SCItemGroup.DECORATION)
	public static final RegistryObject<Block> CRYSTAL_QUARTZ_SLAB = BLOCKS.register("crystal_quartz_slab", () -> new SlabBlock(Block.Properties.of(Material.STONE).strength(2.0F, 6.0F).requiresCorrectToolForDrops()));
	@HasManualPage
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> CLAYMORE = BLOCKS.register("claymore", () -> new ClaymoreBlock(prop(Material.DECORATION)));
	@HasManualPage(PageGroup.DISPLAY_CASES)
	public static final RegistryObject<Block> DISPLAY_CASE = BLOCKS.register(DISPLAY_CASE_PATH, () -> new DisplayCaseBlock(prop(Material.METAL).sound(SoundType.METAL), false));
	@HasManualPage
	@OwnableBE
	@RegisterItemBlock
	public static final RegistryObject<Block> FRAME = BLOCKS.register("keypad_frame", () -> new FrameBlock(prop().sound(SoundType.METAL)));
	@HasManualPage(PageGroup.DISPLAY_CASES)
	public static final RegistryObject<Block> GLOW_DISPLAY_CASE = BLOCKS.register(GLOW_DISPLAY_CASE_PATH, () -> new DisplayCaseBlock(prop(Material.METAL).sound(SoundType.METAL), true));
	@HasManualPage
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> IMS = BLOCKS.register("ims", () -> new IMSBlock(prop(Material.METAL, 0.7F).sound(SoundType.METAL)));
	@HasManualPage
	@RegisterItemBlock
	public static final RegistryObject<Block> INVENTORY_SCANNER = BLOCKS.register("inventory_scanner", () -> new InventoryScannerBlock(propDisguisable()));
	public static final RegistryObject<Block> INVENTORY_SCANNER_FIELD = BLOCKS.register("inventory_scanner_field", () -> new InventoryScannerFieldBlock(prop(Material.GLASS)));
	@HasManualPage
	@RegisterItemBlock(SCItemGroup.DECORATION)
	public static final RegistryObject<Block> IRON_FENCE = BLOCKS.register("electrified_iron_fence", () -> new IronFenceBlock(prop(Material.METAL, MaterialColor.METAL).sound(SoundType.METAL)));
	public static final RegistryObject<Block> KEY_PANEL_BLOCK = BLOCKS.register("key_panel", () -> new KeyPanelBlock(prop(Material.METAL)));
	@HasManualPage
	@RegisterItemBlock
	public static final RegistryObject<Block> KEYCARD_READER = BLOCKS.register("keycard_reader", () -> new KeycardReaderBlock(propDisguisable(Material.METAL).sound(SoundType.METAL)));
	@HasManualPage(hasRecipeDescription = true)
	@RegisterItemBlock
	public static final RegistryObject<Block> KEYPAD = BLOCKS.register("keypad", () -> new KeypadBlock(propDisguisable(Material.METAL)));
	@HasManualPage(hasRecipeDescription = true)
	public static final RegistryObject<Block> KEYPAD_CHEST = BLOCKS.register(KEYPAD_CHEST_PATH, () -> new KeypadChestBlock(prop(Material.WOOD).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> KEYPAD_DOOR = BLOCKS.register("keypad_door", () -> new KeypadDoorBlock(prop(Material.METAL).sound(SoundType.METAL).noOcclusion()));
	@HasManualPage(hasRecipeDescription = true)
	@RegisterItemBlock
	public static final RegistryObject<AbstractKeypadFurnaceBlock> KEYPAD_FURNACE = BLOCKS.register("keypad_furnace", () -> new KeypadFurnaceBlock(prop(Material.METAL).sound(SoundType.METAL).lightLevel(state -> state.getValue(AbstractKeypadFurnaceBlock.LIT) ? 13 : 0)));
	@HasManualPage(hasRecipeDescription = true)
	@RegisterItemBlock
	public static final RegistryObject<AbstractKeypadFurnaceBlock> KEYPAD_SMOKER = BLOCKS.register("keypad_smoker", () -> new KeypadSmokerBlock(prop(Material.METAL).sound(SoundType.METAL).lightLevel(state -> state.getValue(AbstractKeypadFurnaceBlock.LIT) ? 13 : 0)));
	@HasManualPage(hasRecipeDescription = true)
	@RegisterItemBlock
	public static final RegistryObject<AbstractKeypadFurnaceBlock> KEYPAD_BLAST_FURNACE = BLOCKS.register("keypad_blast_furnace", () -> new KeypadBlastFurnaceBlock(prop(Material.METAL).sound(SoundType.METAL).lightLevel(state -> state.getValue(AbstractKeypadFurnaceBlock.LIT) ? 13 : 0)));
	@HasManualPage
	@RegisterItemBlock
	public static final RegistryObject<Block> LASER_BLOCK = BLOCKS.register("laser_block", () -> new LaserBlock(propDisguisable(Material.METAL).sound(SoundType.METAL)));
	public static final RegistryObject<Block> LASER_FIELD = BLOCKS.register("laser", () -> new LaserFieldBlock(prop()));
	@HasManualPage
	@RegisterItemBlock
	public static final RegistryObject<Block> MOTION_ACTIVATED_LIGHT = BLOCKS.register("motion_activated_light", () -> new MotionActivatedLightBlock(prop(Material.GLASS).sound(SoundType.GLASS).lightLevel(state -> state.getValue(MotionActivatedLightBlock.LIT) ? 15 : 0)));
	@HasManualPage
	@OwnableBE
	@RegisterItemBlock
	public static final RegistryObject<Block> PANIC_BUTTON = BLOCKS.register("panic_button", () -> new PanicButtonBlock(false, prop().lightLevel(state -> state.getValue(PanicButtonBlock.POWERED) ? 4 : 0)));
	@HasManualPage
	@RegisterItemBlock
	public static final RegistryObject<Block> PORTABLE_RADAR = BLOCKS.register("portable_radar", () -> new PortableRadarBlock(prop(Material.DECORATION)));
	@HasManualPage
	@OwnableBE
	@RegisterItemBlock
	public static final RegistryObject<Block> PROJECTOR = BLOCKS.register("projector", () -> new ProjectorBlock(propDisguisable(Material.METAL).sound(SoundType.METAL)));
	@HasManualPage
	@RegisterItemBlock
	public static final RegistryObject<Block> PROTECTO = BLOCKS.register("protecto", () -> new ProtectoBlock(propDisguisable(Material.METAL).sound(SoundType.METAL).lightLevel(state -> 7)));
	@OwnableBE
	public static final RegistryObject<Block> REINFORCED_DOOR = BLOCKS.register("iron_door_reinforced", () -> new ReinforcedDoorBlock(prop(Material.METAL).sound(SoundType.METAL).noOcclusion()));
	@HasManualPage
	@RegisterItemBlock(SCItemGroup.DECORATION)
	public static final RegistryObject<Block> REINFORCED_FENCEGATE = BLOCKS.register("reinforced_fence_gate", () -> new ReinforcedFenceGateBlock(prop(Material.METAL).sound(SoundType.METAL)));
	@HasManualPage
	@RegisterItemBlock
	public static final RegistryObject<Block> RETINAL_SCANNER = BLOCKS.register("retinal_scanner", () -> new RetinalScannerBlock(propDisguisable(Material.METAL).sound(SoundType.METAL)));
	public static final RegistryObject<Block> RIFT_STABILIZER = BLOCKS.register("rift_stabilizer", () -> new RiftStabilizerBlock(propDisguisable(Material.METAL).sound(SoundType.METAL)));
	public static final RegistryObject<Block> SCANNER_DOOR = BLOCKS.register("scanner_door", () -> new ScannerDoorBlock(prop(Material.METAL).sound(SoundType.METAL).noOcclusion()));
	public static final RegistryObject<Block> SECRET_OAK_SIGN = BLOCKS.register("secret_sign_standing", () -> new SecretStandingSignBlock(prop(Material.WOOD).sound(SoundType.WOOD), WoodType.OAK));
	public static final RegistryObject<Block> SECRET_OAK_WALL_SIGN = BLOCKS.register("secret_sign_wall", () -> new SecretWallSignBlock(prop(Material.WOOD).sound(SoundType.WOOD), WoodType.OAK));
	public static final RegistryObject<Block> SECRET_SPRUCE_SIGN = BLOCKS.register("secret_spruce_sign_standing", () -> new SecretStandingSignBlock(prop(Material.WOOD).sound(SoundType.WOOD), WoodType.SPRUCE));
	public static final RegistryObject<Block> SECRET_SPRUCE_WALL_SIGN = BLOCKS.register("secret_spruce_sign_wall", () -> new SecretWallSignBlock(prop(Material.WOOD).sound(SoundType.WOOD), WoodType.SPRUCE));
	public static final RegistryObject<Block> SECRET_BIRCH_SIGN = BLOCKS.register("secret_birch_sign_standing", () -> new SecretStandingSignBlock(prop(Material.WOOD).sound(SoundType.WOOD), WoodType.BIRCH));
	public static final RegistryObject<Block> SECRET_BIRCH_WALL_SIGN = BLOCKS.register("secret_birch_sign_wall", () -> new SecretWallSignBlock(prop(Material.WOOD).sound(SoundType.WOOD), WoodType.BIRCH));
	public static final RegistryObject<Block> SECRET_JUNGLE_SIGN = BLOCKS.register("secret_jungle_sign_standing", () -> new SecretStandingSignBlock(prop(Material.WOOD).sound(SoundType.WOOD), WoodType.JUNGLE));
	public static final RegistryObject<Block> SECRET_JUNGLE_WALL_SIGN = BLOCKS.register("secret_jungle_sign_wall", () -> new SecretWallSignBlock(prop(Material.WOOD).sound(SoundType.WOOD), WoodType.JUNGLE));
	public static final RegistryObject<Block> SECRET_ACACIA_SIGN = BLOCKS.register("secret_acacia_sign_standing", () -> new SecretStandingSignBlock(prop(Material.WOOD).sound(SoundType.WOOD), WoodType.ACACIA));
	public static final RegistryObject<Block> SECRET_ACACIA_WALL_SIGN = BLOCKS.register("secret_acacia_sign_wall", () -> new SecretWallSignBlock(prop(Material.WOOD).sound(SoundType.WOOD), WoodType.ACACIA));
	public static final RegistryObject<Block> SECRET_DARK_OAK_SIGN = BLOCKS.register("secret_dark_oak_sign_standing", () -> new SecretStandingSignBlock(prop(Material.WOOD).sound(SoundType.WOOD), WoodType.DARK_OAK));
	public static final RegistryObject<Block> SECRET_DARK_OAK_WALL_SIGN = BLOCKS.register("secret_dark_oak_sign_wall", () -> new SecretWallSignBlock(prop(Material.WOOD).sound(SoundType.WOOD), WoodType.DARK_OAK));
	public static final RegistryObject<Block> SECRET_MANGROVE_SIGN = BLOCKS.register("secret_mangrove_sign_standing", () -> new SecretStandingSignBlock(prop(Material.WOOD).sound(SoundType.WOOD), WoodType.MANGROVE));
	public static final RegistryObject<Block> SECRET_MANGROVE_WALL_SIGN = BLOCKS.register("secret_mangrove_sign_wall", () -> new SecretWallSignBlock(prop(Material.WOOD).sound(SoundType.WOOD), WoodType.MANGROVE));
	public static final RegistryObject<Block> SECRET_CRIMSON_SIGN = BLOCKS.register("secret_crimson_sign_standing", () -> new SecretStandingSignBlock(prop(Material.WOOD).sound(SoundType.WOOD), WoodType.CRIMSON));
	public static final RegistryObject<Block> SECRET_CRIMSON_WALL_SIGN = BLOCKS.register("secret_crimson_sign_wall", () -> new SecretWallSignBlock(prop(Material.WOOD).sound(SoundType.WOOD), WoodType.CRIMSON));
	public static final RegistryObject<Block> SECRET_WARPED_SIGN = BLOCKS.register("secret_warped_sign_standing", () -> new SecretStandingSignBlock(prop(Material.WOOD).sound(SoundType.WOOD), WoodType.WARPED));
	public static final RegistryObject<Block> SECRET_WARPED_WALL_SIGN = BLOCKS.register("secret_warped_sign_wall", () -> new SecretWallSignBlock(prop(Material.WOOD).sound(SoundType.WOOD), WoodType.WARPED));
	@HasManualPage
	@RegisterItemBlock
	public static final RegistryObject<Block> SECURITY_CAMERA = BLOCKS.register("security_camera", () -> new SecurityCameraBlock(prop(Material.METAL)));
	@HasManualPage
	public static final RegistryObject<Block> SONIC_SECURITY_SYSTEM = BLOCKS.register("sonic_security_system", () -> new SonicSecuritySystemBlock(prop(Material.METAL).sound(SoundType.METAL).isRedstoneConductor(SonicSecuritySystemBlock::isNormalCube).noCollission()));
	@RegisterItemBlock(SCItemGroup.DECORATION)
	public static final RegistryObject<Block> STAIRS_CRYSTAL_QUARTZ = BLOCKS.register("crystal_quartz_stairs", () -> new StairBlock(() -> CRYSTAL_QUARTZ.get().defaultBlockState(), Block.Properties.copy(CRYSTAL_QUARTZ.get())));
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> TRACK_MINE = BLOCKS.register("track_mine", () -> new TrackMineBlock(prop(Material.METAL, 0.7F).noCollission().sound(SoundType.METAL)));
	@HasManualPage
	@RegisterItemBlock(SCItemGroup.TECHNICAL)
	public static final RegistryObject<Block> TROPHY_SYSTEM = BLOCKS.register("trophy_system", () -> new TrophySystemBlock(propDisguisable(Material.METAL).sound(SoundType.METAL)));
	@HasManualPage
	@RegisterItemBlock
	public static final RegistryObject<Block> USERNAME_LOGGER = BLOCKS.register("username_logger", () -> new UsernameLoggerBlock(propDisguisable()));
	@HasManualPage
	@OwnableBE
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> MINE = BLOCKS.register("mine", () -> new MineBlock(prop(Material.DECORATION, 1.0F)));
	public static final RegistryObject<? extends LiquidBlock> FAKE_WATER_BLOCK = BLOCKS.register("fake_water_block", () -> new FakeWaterBlock(prop(Material.WATER).noCollission(), FAKE_WATER));
	public static final RegistryObject<? extends LiquidBlock> FAKE_LAVA_BLOCK = BLOCKS.register("fake_lava_block", () -> new FakeLavaBlock(prop(Material.LAVA).noCollission().randomTicks().lightLevel(state -> 15), FAKE_LAVA));

	//block mines
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> STONE_MINE = BLOCKS.register("stone_mine", () -> new BaseFullMineBlock(mineProp(Blocks.STONE), Blocks.STONE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> DEEPSLATE_MINE = BLOCKS.register("deepslate_mine", () -> new DeepslateMineBlock(mineProp(Blocks.DEEPSLATE), Blocks.DEEPSLATE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> COBBLED_DEEPSLATE_MINE = BLOCKS.register("cobbled_deepslate_mine", () -> new BaseFullMineBlock(mineProp(Blocks.COBBLED_DEEPSLATE), Blocks.COBBLED_DEEPSLATE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> DIRT_MINE = BLOCKS.register("dirt_mine", () -> new BaseFullMineBlock(mineProp(Blocks.DIRT), Blocks.DIRT));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> COBBLESTONE_MINE = BLOCKS.register("cobblestone_mine", () -> new BaseFullMineBlock(mineProp(Blocks.COBBLESTONE), Blocks.COBBLESTONE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> SAND_MINE = BLOCKS.register("sand_mine", () -> new FallingBlockMineBlock(mineProp(Blocks.SAND), Blocks.SAND));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> GRAVEL_MINE = BLOCKS.register("gravel_mine", () -> new FallingBlockMineBlock(mineProp(Blocks.GRAVEL), Blocks.GRAVEL));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> COAL_ORE_MINE = BLOCKS.register("coal_mine", () -> new BaseFullMineBlock(mineProp(Blocks.COAL_ORE), Blocks.COAL_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> DEEPSLATE_COAL_ORE_MINE = BLOCKS.register("deepslate_coal_mine", () -> new BaseFullMineBlock(mineProp(Blocks.DEEPSLATE_COAL_ORE), Blocks.DEEPSLATE_COAL_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> IRON_ORE_MINE = BLOCKS.register("iron_mine", () -> new BaseFullMineBlock(mineProp(Blocks.IRON_ORE), Blocks.IRON_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> DEEPSLATE_IRON_ORE_MINE = BLOCKS.register("deepslate_iron_mine", () -> new BaseFullMineBlock(mineProp(Blocks.DEEPSLATE_IRON_ORE), Blocks.DEEPSLATE_IRON_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> GOLD_ORE_MINE = BLOCKS.register("gold_mine", () -> new BaseFullMineBlock(mineProp(Blocks.GOLD_ORE), Blocks.GOLD_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> DEEPSLATE_GOLD_ORE_MINE = BLOCKS.register("deepslate_gold_mine", () -> new BaseFullMineBlock(mineProp(Blocks.DEEPSLATE_GOLD_ORE), Blocks.DEEPSLATE_GOLD_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> COPPER_ORE_MINE = BLOCKS.register("copper_mine", () -> new BaseFullMineBlock(mineProp(Blocks.COPPER_ORE), Blocks.COPPER_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> DEEPSLATE_COPPER_ORE_MINE = BLOCKS.register("deepslate_copper_mine", () -> new BaseFullMineBlock(mineProp(Blocks.DEEPSLATE_COPPER_ORE), Blocks.DEEPSLATE_COPPER_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> REDSTONE_ORE_MINE = BLOCKS.register("redstone_mine", () -> new RedstoneOreMineBlock(mineProp(Blocks.REDSTONE_ORE), Blocks.REDSTONE_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> DEEPSLATE_REDSTONE_ORE_MINE = BLOCKS.register("deepslate_redstone_mine", () -> new RedstoneOreMineBlock(mineProp(Blocks.DEEPSLATE_REDSTONE_ORE), Blocks.DEEPSLATE_REDSTONE_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> EMERALD_ORE_MINE = BLOCKS.register("emerald_mine", () -> new BaseFullMineBlock(mineProp(Blocks.EMERALD_ORE), Blocks.EMERALD_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> DEEPSLATE_EMERALD_ORE_MINE = BLOCKS.register("deepslate_emerald_mine", () -> new BaseFullMineBlock(mineProp(Blocks.DEEPSLATE_EMERALD_ORE), Blocks.DEEPSLATE_EMERALD_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> LAPIS_ORE_MINE = BLOCKS.register("lapis_mine", () -> new BaseFullMineBlock(mineProp(Blocks.LAPIS_ORE), Blocks.LAPIS_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> DEEPSLATE_LAPIS_ORE_MINE = BLOCKS.register("deepslate_lapis_mine", () -> new BaseFullMineBlock(mineProp(Blocks.DEEPSLATE_LAPIS_ORE), Blocks.DEEPSLATE_LAPIS_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> DIAMOND_ORE_MINE = BLOCKS.register("diamond_mine", () -> new BaseFullMineBlock(mineProp(Blocks.DIAMOND_ORE), Blocks.DIAMOND_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> DEEPSLATE_DIAMOND_ORE_MINE = BLOCKS.register("deepslate_diamond_mine", () -> new BaseFullMineBlock(mineProp(Blocks.DEEPSLATE_DIAMOND_ORE), Blocks.DEEPSLATE_DIAMOND_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> NETHER_GOLD_ORE_MINE = BLOCKS.register("nether_gold_mine", () -> new BaseFullMineBlock(mineProp(Blocks.NETHER_GOLD_ORE), Blocks.NETHER_GOLD_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> QUARTZ_ORE_MINE = BLOCKS.register("quartz_mine", () -> new BaseFullMineBlock(mineProp(Blocks.NETHER_QUARTZ_ORE), Blocks.NETHER_QUARTZ_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	public static final RegistryObject<Block> ANCIENT_DEBRIS_MINE = BLOCKS.register("ancient_debris_mine", () -> new BaseFullMineBlock(mineProp(Blocks.ANCIENT_DEBRIS), Blocks.ANCIENT_DEBRIS));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> GILDED_BLACKSTONE_MINE = BLOCKS.register("gilded_blackstone_mine", () -> new BaseFullMineBlock(mineProp(Blocks.GILDED_BLACKSTONE), Blocks.GILDED_BLACKSTONE));
	@HasManualPage(PageGroup.FURNACE_MINES)
	@OwnableBE
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> FURNACE_MINE = BLOCKS.register("furnace_mine", () -> new FurnaceMineBlock(prop(Material.STONE, 3.5F).requiresCorrectToolForDrops(), Blocks.FURNACE));
	@HasManualPage(PageGroup.FURNACE_MINES)
	@OwnableBE
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> SMOKER_MINE = BLOCKS.register("smoker_mine", () -> new FurnaceMineBlock(prop(Material.STONE, 3.5F).requiresCorrectToolForDrops(), Blocks.SMOKER));
	@HasManualPage(PageGroup.FURNACE_MINES)
	@OwnableBE
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> BLAST_FURNACE_MINE = BLOCKS.register("blast_furnace_mine", () -> new FurnaceMineBlock(prop(Material.STONE, 3.5F).requiresCorrectToolForDrops(), Blocks.BLAST_FURNACE));

	//reinforced blocks (ordered by vanilla building blocks creative tab order)
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STONE = BLOCKS.register("reinforced_stone", () -> new BaseReinforcedBlock(prop(), Blocks.STONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_GRANITE = BLOCKS.register("reinforced_granite", () -> new BaseReinforcedBlock(prop(), Blocks.GRANITE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_GRANITE = BLOCKS.register("reinforced_polished_granite", () -> new BaseReinforcedBlock(prop(), Blocks.POLISHED_GRANITE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DIORITE = BLOCKS.register("reinforced_diorite", () -> new BaseReinforcedBlock(prop(), Blocks.DIORITE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_DIORITE = BLOCKS.register("reinforced_polished_diorite", () -> new BaseReinforcedBlock(prop(), Blocks.POLISHED_DIORITE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_ANDESITE = BLOCKS.register("reinforced_andesite", () -> new BaseReinforcedBlock(prop(), Blocks.ANDESITE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_ANDESITE = BLOCKS.register("reinforced_polished_andesite", () -> new BaseReinforcedBlock(prop(), Blocks.POLISHED_ANDESITE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DEEPSLATE = BLOCKS.register("reinforced_deepslate", () -> new ReinforcedRotatedPillarBlock(prop().sound(SoundType.DEEPSLATE), Blocks.DEEPSLATE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_COBBLED_DEEPSLATE = BLOCKS.register("reinforced_cobbled_deepslate", () -> new BaseReinforcedBlock(prop().sound(SoundType.DEEPSLATE), Blocks.COBBLED_DEEPSLATE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_DEEPSLATE = BLOCKS.register("reinforced_polished_deepslate", () -> new BaseReinforcedBlock(prop().sound(SoundType.POLISHED_DEEPSLATE), Blocks.POLISHED_DEEPSLATE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CALCITE = BLOCKS.register("reinforced_calcite", () -> new BaseReinforcedBlock(prop().sound(SoundType.CALCITE), Blocks.CALCITE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_TUFF = BLOCKS.register("reinforced_tuff", () -> new BaseReinforcedBlock(prop().sound(SoundType.TUFF), Blocks.TUFF));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DRIPSTONE_BLOCK = BLOCKS.register("reinforced_dripstone_block", () -> new BaseReinforcedBlock(prop().sound(SoundType.DRIPSTONE_BLOCK), Blocks.DRIPSTONE_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_GRASS_BLOCK = BLOCKS.register("reinforced_grass_block", () -> new ReinforcedSnowyDirtBlock(prop(Material.GRASS).sound(SoundType.GRASS), Blocks.GRASS_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DIRT = BLOCKS.register("reinforced_dirt", () -> new BaseReinforcedBlock(prop(Material.DIRT).sound(SoundType.GRAVEL), Blocks.DIRT));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_COARSE_DIRT = BLOCKS.register("reinforced_coarse_dirt", () -> new BaseReinforcedBlock(prop(Material.DIRT).sound(SoundType.GRAVEL), Blocks.COARSE_DIRT));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PODZOL = BLOCKS.register("reinforced_podzol", () -> new ReinforcedSnowyDirtBlock(prop(Material.DIRT).sound(SoundType.GRAVEL), Blocks.PODZOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_MUD = BLOCKS.register("reinforced_mud", () -> new ReinforcedMud(prop(Material.DIRT, MaterialColor.TERRACOTTA_CYAN).isValidSpawn(SCContent::always).isRedstoneConductor(SCContent::always).isViewBlocking(SCContent::always).isSuffocating(SCContent::always).sound(SoundType.MUD), Blocks.MUD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CRIMSON_NYLIUM = BLOCKS.register("reinforced_crimson_nylium", () -> new ReinforcedNyliumBlock(prop().sound(SoundType.NYLIUM), Blocks.CRIMSON_NYLIUM));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_WARPED_NYLIUM = BLOCKS.register("reinforced_warped_nylium", () -> new ReinforcedNyliumBlock(prop().sound(SoundType.NYLIUM), Blocks.WARPED_NYLIUM));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_ROOTED_DIRT = BLOCKS.register("reinforced_rooted_dirt", () -> new ReinforcedRootedDirtBlock(prop(Material.DIRT).sound(SoundType.ROOTED_DIRT), Blocks.ROOTED_DIRT));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_COBBLESTONE = BLOCKS.register("reinforced_cobblestone", () -> new BaseReinforcedBlock(prop(), Blocks.COBBLESTONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_OAK_PLANKS = BLOCKS.register("reinforced_oak_planks", () -> new BaseReinforcedBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.OAK_PLANKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SPRUCE_PLANKS = BLOCKS.register("reinforced_spruce_planks", () -> new BaseReinforcedBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.SPRUCE_PLANKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BIRCH_PLANKS = BLOCKS.register("reinforced_birch_planks", () -> new BaseReinforcedBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.BIRCH_PLANKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_JUNGLE_PLANKS = BLOCKS.register("reinforced_jungle_planks", () -> new BaseReinforcedBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.JUNGLE_PLANKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_ACACIA_PLANKS = BLOCKS.register("reinforced_acacia_planks", () -> new BaseReinforcedBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.ACACIA_PLANKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DARK_OAK_PLANKS = BLOCKS.register("reinforced_dark_oak_planks", () -> new BaseReinforcedBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.DARK_OAK_PLANKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_MANGROVE_PLANKS = BLOCKS.register("reinforced_mangrove_planks", () -> new BaseReinforcedBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.MANGROVE_PLANKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CRIMSON_PLANKS = BLOCKS.register("reinforced_crimson_planks", () -> new BaseReinforcedBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.CRIMSON_PLANKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_WARPED_PLANKS = BLOCKS.register("reinforced_warped_planks", () -> new BaseReinforcedBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.WARPED_PLANKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SAND = BLOCKS.register("reinforced_sand", () -> new ReinforcedFallingBlock(prop(Material.SAND).sound(SoundType.SAND), Blocks.SAND));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_RED_SAND = BLOCKS.register("reinforced_red_sand", () -> new ReinforcedFallingBlock(prop(Material.SAND).sound(SoundType.SAND), Blocks.RED_SAND));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_GRAVEL = BLOCKS.register("reinforced_gravel", () -> new ReinforcedFallingBlock(prop(Material.DIRT).sound(SoundType.GRAVEL), Blocks.GRAVEL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_COAL_BLOCK = BLOCKS.register("reinforced_coal_block", () -> new BaseReinforcedBlock(prop(), Blocks.COAL_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_RAW_IRON_BLOCK = BLOCKS.register("reinforced_raw_iron_block", () -> new BaseReinforcedBlock(prop(), Blocks.RAW_IRON_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_RAW_COPPER_BLOCK = BLOCKS.register("reinforced_raw_copper_block", () -> new BaseReinforcedBlock(prop(), Blocks.RAW_COPPER_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_RAW_GOLD_BLOCK = BLOCKS.register("reinforced_raw_gold_block", () -> new BaseReinforcedBlock(prop(), Blocks.RAW_GOLD_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_AMETHYST_BLOCK = BLOCKS.register("reinforced_amethyst_block", () -> new ReinforcedAmethystBlock(prop().sound(SoundType.AMETHYST), Blocks.AMETHYST_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_IRON_BLOCK = BLOCKS.register("reinforced_iron_block", () -> new BaseReinforcedBlock(prop(Material.METAL).sound(SoundType.METAL), Blocks.IRON_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_COPPER_BLOCK = BLOCKS.register("reinforced_copper_block", () -> new BaseReinforcedBlock(prop(Material.METAL).sound(SoundType.COPPER), Blocks.COPPER_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_GOLD_BLOCK = BLOCKS.register("reinforced_gold_block", () -> new BaseReinforcedBlock(prop(Material.METAL).sound(SoundType.METAL), Blocks.GOLD_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DIAMOND_BLOCK = BLOCKS.register("reinforced_diamond_block", () -> new BaseReinforcedBlock(prop(Material.METAL).sound(SoundType.METAL), Blocks.DIAMOND_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_NETHERITE_BLOCK = BLOCKS.register("reinforced_netherite_block", () -> new BaseReinforcedBlock(prop(Material.METAL).sound(SoundType.NETHERITE_BLOCK), Blocks.NETHERITE_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_EXPOSED_COPPER = BLOCKS.register("reinforced_exposed_copper", () -> new BaseReinforcedBlock(prop(Material.METAL).sound(SoundType.COPPER), Blocks.EXPOSED_COPPER));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_WEATHERED_COPPER = BLOCKS.register("reinforced_weathered_copper", () -> new BaseReinforcedBlock(prop(Material.METAL).sound(SoundType.COPPER), Blocks.WEATHERED_COPPER));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_OXIDIZED_COPPER = BLOCKS.register("reinforced_oxidized_copper", () -> new BaseReinforcedBlock(prop(Material.METAL).sound(SoundType.COPPER), Blocks.OXIDIZED_COPPER));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CUT_COPPER = BLOCKS.register("reinforced_cut_copper", () -> new BaseReinforcedBlock(prop(Material.METAL).sound(SoundType.COPPER), Blocks.CUT_COPPER));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_EXPOSED_CUT_COPPER = BLOCKS.register("reinforced_exposed_cut_copper", () -> new BaseReinforcedBlock(prop(Material.METAL).sound(SoundType.COPPER), Blocks.EXPOSED_CUT_COPPER));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_WEATHERED_CUT_COPPER = BLOCKS.register("reinforced_weathered_cut_copper", () -> new BaseReinforcedBlock(prop(Material.METAL).sound(SoundType.COPPER), Blocks.WEATHERED_CUT_COPPER));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_OXIDIZED_CUT_COPPER = BLOCKS.register("reinforced_oxidized_cut_copper", () -> new BaseReinforcedBlock(prop(Material.METAL).sound(SoundType.COPPER), Blocks.OXIDIZED_CUT_COPPER));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CUT_COPPER_STAIRS = BLOCKS.register("reinforced_cut_copper_stairs", () -> new ReinforcedStairsBlock(prop().sound(SoundType.COPPER), Blocks.CUT_COPPER_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_EXPOSED_CUT_COPPER_STAIRS = BLOCKS.register("reinforced_exposed_cut_copper_stairs", () -> new ReinforcedStairsBlock(prop().sound(SoundType.COPPER), Blocks.EXPOSED_CUT_COPPER_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_WEATHERED_CUT_COPPER_STAIRS = BLOCKS.register("reinforced_weathered_cut_copper_stairs", () -> new ReinforcedStairsBlock(prop().sound(SoundType.COPPER), Blocks.WEATHERED_CUT_COPPER_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_OXIDIZED_CUT_COPPER_STAIRS = BLOCKS.register("reinforced_oxidized_cut_copper_stairs", () -> new ReinforcedStairsBlock(prop().sound(SoundType.COPPER), Blocks.OXIDIZED_CUT_COPPER_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CUT_COPPER_SLAB = BLOCKS.register("reinforced_cut_copper_slab", () -> new ReinforcedSlabBlock(prop().sound(SoundType.COPPER), Blocks.CUT_COPPER_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_EXPOSED_CUT_COPPER_SLAB = BLOCKS.register("reinforced_exposed_cut_copper_slab", () -> new ReinforcedSlabBlock(prop().sound(SoundType.COPPER), Blocks.EXPOSED_CUT_COPPER_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_WEATHERED_CUT_COPPER_SLAB = BLOCKS.register("reinforced_weathered_cut_copper_slab", () -> new ReinforcedSlabBlock(prop().sound(SoundType.COPPER), Blocks.WEATHERED_CUT_COPPER_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_OXIDIZED_CUT_COPPER_SLAB = BLOCKS.register("reinforced_oxidized_cut_copper_slab", () -> new ReinforcedSlabBlock(prop().sound(SoundType.COPPER), Blocks.OXIDIZED_CUT_COPPER_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_OAK_LOG = BLOCKS.register("reinforced_oak_log", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.OAK_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SPRUCE_LOG = BLOCKS.register("reinforced_spruce_log", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.SPRUCE_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BIRCH_LOG = BLOCKS.register("reinforced_birch_log", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.BIRCH_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_JUNGLE_LOG = BLOCKS.register("reinforced_jungle_log", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.JUNGLE_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_ACACIA_LOG = BLOCKS.register("reinforced_acacia_log", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.ACACIA_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DARK_OAK_LOG = BLOCKS.register("reinforced_dark_oak_log", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.DARK_OAK_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_MANGROVE_LOG = BLOCKS.register("reinforced_mangrove_log", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.MANGROVE_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CRIMSON_STEM = BLOCKS.register("reinforced_crimson_stem", () -> new ReinforcedRotatedPillarBlock(prop(Material.NETHER_WOOD).sound(SoundType.STEM), Blocks.CRIMSON_STEM));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_WARPED_STEM = BLOCKS.register("reinforced_warped_stem", () -> new ReinforcedRotatedPillarBlock(prop(Material.NETHER_WOOD).sound(SoundType.STEM), Blocks.WARPED_STEM));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STRIPPED_OAK_LOG = BLOCKS.register("reinforced_stripped_oak_log", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.STRIPPED_OAK_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STRIPPED_SPRUCE_LOG = BLOCKS.register("reinforced_stripped_spruce_log", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.STRIPPED_SPRUCE_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STRIPPED_BIRCH_LOG = BLOCKS.register("reinforced_stripped_birch_log", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.STRIPPED_BIRCH_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STRIPPED_JUNGLE_LOG = BLOCKS.register("reinforced_stripped_jungle_log", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.STRIPPED_JUNGLE_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STRIPPED_ACACIA_LOG = BLOCKS.register("reinforced_stripped_acacia_log", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.STRIPPED_ACACIA_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STRIPPED_DARK_OAK_LOG = BLOCKS.register("reinforced_stripped_dark_oak_log", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.STRIPPED_DARK_OAK_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STRIPPED_MANGROVE_LOG = BLOCKS.register("reinforced_stripped_mangrove_log", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.STRIPPED_MANGROVE_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STRIPPED_CRIMSON_STEM = BLOCKS.register("reinforced_stripped_crimson_stem", () -> new ReinforcedRotatedPillarBlock(prop(Material.NETHER_WOOD).sound(SoundType.STEM), Blocks.STRIPPED_CRIMSON_STEM));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STRIPPED_WARPED_STEM = BLOCKS.register("reinforced_stripped_warped_stem", () -> new ReinforcedRotatedPillarBlock(prop(Material.NETHER_WOOD).sound(SoundType.STEM), Blocks.STRIPPED_WARPED_STEM));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STRIPPED_OAK_WOOD = BLOCKS.register("reinforced_stripped_oak_wood", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.STRIPPED_OAK_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STRIPPED_SPRUCE_WOOD = BLOCKS.register("reinforced_stripped_spruce_wood", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.STRIPPED_SPRUCE_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STRIPPED_BIRCH_WOOD = BLOCKS.register("reinforced_stripped_birch_wood", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.STRIPPED_BIRCH_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STRIPPED_JUNGLE_WOOD = BLOCKS.register("reinforced_stripped_jungle_wood", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.STRIPPED_JUNGLE_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STRIPPED_ACACIA_WOOD = BLOCKS.register("reinforced_stripped_acacia_wood", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.STRIPPED_ACACIA_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STRIPPED_DARK_OAK_WOOD = BLOCKS.register("reinforced_stripped_dark_oak_wood", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.STRIPPED_DARK_OAK_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STRIPPED_MANGROVE_WOOD = BLOCKS.register("reinforced_stripped_mangrove_wood", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.STRIPPED_MANGROVE_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STRIPPED_CRIMSON_HYPHAE = BLOCKS.register("reinforced_stripped_crimson_hyphae", () -> new ReinforcedRotatedPillarBlock(prop(Material.NETHER_WOOD).sound(SoundType.STEM), Blocks.STRIPPED_CRIMSON_HYPHAE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STRIPPED_WARPED_HYPHAE = BLOCKS.register("reinforced_stripped_warped_hyphae", () -> new ReinforcedRotatedPillarBlock(prop(Material.NETHER_WOOD).sound(SoundType.STEM), Blocks.STRIPPED_WARPED_HYPHAE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_OAK_WOOD = BLOCKS.register("reinforced_oak_wood", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.OAK_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SPRUCE_WOOD = BLOCKS.register("reinforced_spruce_wood", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.SPRUCE_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BIRCH_WOOD = BLOCKS.register("reinforced_birch_wood", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.BIRCH_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_JUNGLE_WOOD = BLOCKS.register("reinforced_jungle_wood", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.JUNGLE_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_ACACIA_WOOD = BLOCKS.register("reinforced_acacia_wood", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.ACACIA_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DARK_OAK_WOOD = BLOCKS.register("reinforced_dark_oak_wood", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.DARK_OAK_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_MANGROVE_WOOD = BLOCKS.register("reinforced_mangrove_wood", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.MANGROVE_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CRIMSON_HYPHAE = BLOCKS.register("reinforced_crimson_hyphae", () -> new ReinforcedRotatedPillarBlock(prop(Material.NETHER_WOOD).sound(SoundType.STEM), Blocks.CRIMSON_HYPHAE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_WARPED_HYPHAE = BLOCKS.register("reinforced_warped_hyphae", () -> new ReinforcedRotatedPillarBlock(prop(Material.NETHER_WOOD).sound(SoundType.STEM), Blocks.WARPED_HYPHAE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<Block> REINFORCED_GLASS = BLOCKS.register("reinforced_glass", () -> new ReinforcedGlassBlock(glassProp(), Blocks.GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_TINTED_GLASS = BLOCKS.register("reinforced_tinted_glass", () -> new ReinforcedTintedGlassBlock(glassProp(), Blocks.TINTED_GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_LAPIS_BLOCK = BLOCKS.register("reinforced_lapis_block", () -> new BaseReinforcedBlock(prop(), Blocks.LAPIS_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SANDSTONE = BLOCKS.register("reinforced_sandstone", () -> new BaseReinforcedBlock(prop(), Blocks.SANDSTONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CHISELED_SANDSTONE = BLOCKS.register("reinforced_chiseled_sandstone", () -> new BaseReinforcedBlock(prop(), Blocks.CHISELED_SANDSTONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CUT_SANDSTONE = BLOCKS.register("reinforced_cut_sandstone", () -> new BaseReinforcedBlock(prop(), Blocks.CUT_SANDSTONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_WHITE_WOOL = BLOCKS.register("reinforced_white_wool", () -> new BaseReinforcedBlock(prop(Material.WOOL).sound(SoundType.WOOL), Blocks.WHITE_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_ORANGE_WOOL = BLOCKS.register("reinforced_orange_wool", () -> new BaseReinforcedBlock(prop(Material.WOOL).sound(SoundType.WOOL), Blocks.ORANGE_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_MAGENTA_WOOL = BLOCKS.register("reinforced_magenta_wool", () -> new BaseReinforcedBlock(prop(Material.WOOL).sound(SoundType.WOOL), Blocks.MAGENTA_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_LIGHT_BLUE_WOOL = BLOCKS.register("reinforced_light_blue_wool", () -> new BaseReinforcedBlock(prop(Material.WOOL).sound(SoundType.WOOL), Blocks.LIGHT_BLUE_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_YELLOW_WOOL = BLOCKS.register("reinforced_yellow_wool", () -> new BaseReinforcedBlock(prop(Material.WOOL).sound(SoundType.WOOL), Blocks.YELLOW_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_LIME_WOOL = BLOCKS.register("reinforced_lime_wool", () -> new BaseReinforcedBlock(prop(Material.WOOL).sound(SoundType.WOOL), Blocks.LIME_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PINK_WOOL = BLOCKS.register("reinforced_pink_wool", () -> new BaseReinforcedBlock(prop(Material.WOOL).sound(SoundType.WOOL), Blocks.PINK_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_GRAY_WOOL = BLOCKS.register("reinforced_gray_wool", () -> new BaseReinforcedBlock(prop(Material.WOOL).sound(SoundType.WOOL), Blocks.GRAY_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_LIGHT_GRAY_WOOL = BLOCKS.register("reinforced_light_gray_wool", () -> new BaseReinforcedBlock(prop(Material.WOOL).sound(SoundType.WOOL), Blocks.LIGHT_GRAY_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CYAN_WOOL = BLOCKS.register("reinforced_cyan_wool", () -> new BaseReinforcedBlock(prop(Material.WOOL).sound(SoundType.WOOL), Blocks.CYAN_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PURPLE_WOOL = BLOCKS.register("reinforced_purple_wool", () -> new BaseReinforcedBlock(prop(Material.WOOL).sound(SoundType.WOOL), Blocks.PURPLE_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BLUE_WOOL = BLOCKS.register("reinforced_blue_wool", () -> new BaseReinforcedBlock(prop(Material.WOOL).sound(SoundType.WOOL), Blocks.BLUE_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BROWN_WOOL = BLOCKS.register("reinforced_brown_wool", () -> new BaseReinforcedBlock(prop(Material.WOOL).sound(SoundType.WOOL), Blocks.BROWN_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_GREEN_WOOL = BLOCKS.register("reinforced_green_wool", () -> new BaseReinforcedBlock(prop(Material.WOOL).sound(SoundType.WOOL), Blocks.GREEN_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_RED_WOOL = BLOCKS.register("reinforced_red_wool", () -> new BaseReinforcedBlock(prop(Material.WOOL).sound(SoundType.WOOL), Blocks.RED_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BLACK_WOOL = BLOCKS.register("reinforced_black_wool", () -> new BaseReinforcedBlock(prop(Material.WOOL).sound(SoundType.WOOL), Blocks.BLACK_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_OAK_SLAB = BLOCKS.register("reinforced_oak_slab", () -> new ReinforcedSlabBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.OAK_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SPRUCE_SLAB = BLOCKS.register("reinforced_spruce_slab", () -> new ReinforcedSlabBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.SPRUCE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BIRCH_SLAB = BLOCKS.register("reinforced_birch_slab", () -> new ReinforcedSlabBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.BIRCH_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_JUNGLE_SLAB = BLOCKS.register("reinforced_jungle_slab", () -> new ReinforcedSlabBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.JUNGLE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_ACACIA_SLAB = BLOCKS.register("reinforced_acacia_slab", () -> new ReinforcedSlabBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.ACACIA_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DARK_OAK_SLAB = BLOCKS.register("reinforced_dark_oak_slab", () -> new ReinforcedSlabBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.DARK_OAK_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_MANGROVE_SLAB = BLOCKS.register("reinforced_mangrove_slab", () -> new ReinforcedSlabBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.MANGROVE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CRIMSON_SLAB = BLOCKS.register("reinforced_crimson_slab", () -> new ReinforcedSlabBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.CRIMSON_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_WARPED_SLAB = BLOCKS.register("reinforced_warped_slab", () -> new ReinforcedSlabBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.WARPED_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_NORMAL_STONE_SLAB = BLOCKS.register("reinforced_normal_stone_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.STONE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SMOOTH_STONE_SLAB = BLOCKS.register("reinforced_stone_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.SMOOTH_STONE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SANDSTONE_SLAB = BLOCKS.register("reinforced_sandstone_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.SANDSTONE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CUT_SANDSTONE_SLAB = BLOCKS.register("reinforced_cut_sandstone_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.CUT_SANDSTONE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_COBBLESTONE_SLAB = BLOCKS.register("reinforced_cobblestone_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.COBBLESTONE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BRICK_SLAB = BLOCKS.register("reinforced_brick_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.BRICK_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STONE_BRICK_SLAB = BLOCKS.register("reinforced_stone_brick_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.STONE_BRICK_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_MUD_BRICK_SLAB = BLOCKS.register("reinforced_mud_brick_slab", () -> new ReinforcedSlabBlock(prop().sound(SoundType.MUD_BRICKS), Blocks.MUD_BRICK_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_NETHER_BRICK_SLAB = BLOCKS.register("reinforced_nether_brick_slab", () -> new ReinforcedSlabBlock(prop().sound(SoundType.NETHER_BRICKS), Blocks.NETHER_BRICK_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_QUARTZ_SLAB = BLOCKS.register("reinforced_quartz_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.QUARTZ_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_RED_SANDSTONE_SLAB = BLOCKS.register("reinforced_red_sandstone_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.RED_SANDSTONE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CUT_RED_SANDSTONE_SLAB = BLOCKS.register("reinforced_cut_red_sandstone_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.CUT_RED_SANDSTONE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PURPUR_SLAB = BLOCKS.register("reinforced_purpur_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.PURPUR_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PRISMARINE_SLAB = BLOCKS.register("reinforced_prismarine_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.PRISMARINE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PRISMARINE_BRICK_SLAB = BLOCKS.register("reinforced_prismarine_brick_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.PRISMARINE_BRICK_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DARK_PRISMARINE_SLAB = BLOCKS.register("reinforced_dark_prismarine_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.DARK_PRISMARINE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SMOOTH_QUARTZ = BLOCKS.register("reinforced_smooth_quartz", () -> new BaseReinforcedBlock(prop(), Blocks.SMOOTH_QUARTZ));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SMOOTH_RED_SANDSTONE = BLOCKS.register("reinforced_smooth_red_sandstone", () -> new BaseReinforcedBlock(prop(), Blocks.SMOOTH_RED_SANDSTONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SMOOTH_SANDSTONE = BLOCKS.register("reinforced_smooth_sandstone", () -> new BaseReinforcedBlock(prop(), Blocks.SMOOTH_SANDSTONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SMOOTH_STONE = BLOCKS.register("reinforced_smooth_stone", () -> new BaseReinforcedBlock(prop(), Blocks.SMOOTH_STONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BRICKS = BLOCKS.register("reinforced_bricks", () -> new BaseReinforcedBlock(prop(), Blocks.BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BOOKSHELF = BLOCKS.register("reinforced_bookshelf", () -> new ReinforcedBookshelfBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.BOOKSHELF));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_MOSSY_COBBLESTONE = BLOCKS.register("reinforced_mossy_cobblestone", () -> new BaseReinforcedBlock(prop(), Blocks.MOSSY_COBBLESTONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_OBSIDIAN = BLOCKS.register("reinforced_obsidian", () -> new ReinforcedObsidianBlock(prop(), Blocks.OBSIDIAN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PURPUR_BLOCK = BLOCKS.register("reinforced_purpur_block", () -> new BaseReinforcedBlock(prop(), Blocks.PURPUR_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PURPUR_PILLAR = BLOCKS.register("reinforced_purpur_pillar", () -> new ReinforcedRotatedPillarBlock(prop(), Blocks.PURPUR_PILLAR));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PURPUR_STAIRS = BLOCKS.register("reinforced_purpur_stairs", () -> new ReinforcedStairsBlock(prop(), Blocks.PURPUR_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_OAK_STAIRS = BLOCKS.register("reinforced_oak_stairs", () -> new ReinforcedStairsBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.OAK_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_COBBLESTONE_STAIRS = BLOCKS.register("reinforced_cobblestone_stairs", () -> new ReinforcedStairsBlock(prop(), Blocks.COBBLESTONE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_ICE = BLOCKS.register("reinforced_ice", () -> new BaseReinforcedBlock(prop(Material.ICE).friction(0.98F).sound(SoundType.GLASS).noOcclusion().isValidSpawn((state, level, pos, type) -> type == EntityType.POLAR_BEAR), Blocks.ICE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SNOW_BLOCK = BLOCKS.register("reinforced_snow_block", () -> new BaseReinforcedBlock(prop(Material.SNOW).sound(SoundType.SNOW), Blocks.SNOW_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CLAY = BLOCKS.register("reinforced_clay", () -> new BaseReinforcedBlock(prop(Material.CLAY).sound(SoundType.GRAVEL), Blocks.CLAY));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_NETHERRACK = BLOCKS.register("reinforced_netherrack", () -> new BaseReinforcedBlock(prop().sound(SoundType.NETHERRACK), Blocks.NETHERRACK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SOUL_SOIL = BLOCKS.register("reinforced_soul_soil", () -> new BaseReinforcedBlock(prop(Material.DIRT).sound(SoundType.SOUL_SOIL), Blocks.SOUL_SOIL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BASALT = BLOCKS.register("reinforced_basalt", () -> new ReinforcedRotatedPillarBlock(prop().sound(SoundType.BASALT), Blocks.BASALT));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_BASALT = BLOCKS.register("reinforced_polished_basalt", () -> new ReinforcedRotatedPillarBlock(prop().sound(SoundType.BASALT), Blocks.POLISHED_BASALT));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SMOOTH_BASALT = BLOCKS.register("reinforced_smooth_basalt", () -> new BaseReinforcedBlock(prop().sound(SoundType.BASALT), Blocks.SMOOTH_BASALT));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_GLOWSTONE = BLOCKS.register("reinforced_glowstone", () -> new BaseReinforcedBlock(prop(Material.GLASS).sound(SoundType.GLASS).lightLevel(state -> 15), Blocks.GLOWSTONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STONE_BRICKS = BLOCKS.register("reinforced_stone_bricks", () -> new BaseReinforcedBlock(prop(), Blocks.STONE_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_MOSSY_STONE_BRICKS = BLOCKS.register("reinforced_mossy_stone_bricks", () -> new BaseReinforcedBlock(prop(), Blocks.MOSSY_STONE_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CRACKED_STONE_BRICKS = BLOCKS.register("reinforced_cracked_stone_bricks", () -> new BaseReinforcedBlock(prop(), Blocks.CRACKED_STONE_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CHISELED_STONE_BRICKS = BLOCKS.register("reinforced_chiseled_stone_bricks", () -> new BaseReinforcedBlock(prop(), Blocks.CHISELED_STONE_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PACKED_MUD = BLOCKS.register("reinforced_packed_mud", () -> new BaseReinforcedBlock(prop().sound(SoundType.PACKED_MUD), Blocks.PACKED_MUD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_MUD_BRICKS = BLOCKS.register("reinforced_mud_bricks", () -> new BaseReinforcedBlock(prop().sound(SoundType.MUD_BRICKS), Blocks.MUD_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DEEPSLATE_BRICKS = BLOCKS.register("reinforced_deepslate_bricks", () -> new BaseReinforcedBlock(prop().sound(SoundType.DEEPSLATE_BRICKS), Blocks.DEEPSLATE_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CRACKED_DEEPSLATE_BRICKS = BLOCKS.register("reinforced_cracked_deepslate_bricks", () -> new BaseReinforcedBlock(prop().sound(SoundType.DEEPSLATE_BRICKS), Blocks.CRACKED_DEEPSLATE_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DEEPSLATE_TILES = BLOCKS.register("reinforced_deepslate_tiles", () -> new BaseReinforcedBlock(prop().sound(SoundType.DEEPSLATE_TILES), Blocks.DEEPSLATE_TILES));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CRACKED_DEEPSLATE_TILES = BLOCKS.register("reinforced_cracked_deepslate_tiles", () -> new BaseReinforcedBlock(prop().sound(SoundType.DEEPSLATE_TILES), Blocks.CRACKED_DEEPSLATE_TILES));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CHISELED_DEEPSLATE = BLOCKS.register("reinforced_chiseled_deepslate", () -> new BaseReinforcedBlock(prop().sound(SoundType.DEEPSLATE_BRICKS), Blocks.CHISELED_DEEPSLATE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BRICK_STAIRS = BLOCKS.register("reinforced_brick_stairs", () -> new ReinforcedStairsBlock(prop(), Blocks.BRICK_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STONE_BRICK_STAIRS = BLOCKS.register("reinforced_stone_brick_stairs", () -> new ReinforcedStairsBlock(prop(), Blocks.STONE_BRICK_STAIRS));
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_MUD_BRICK_STAIRS = BLOCKS.register("reinforced_mud_brick_stairs", () -> new ReinforcedStairsBlock(prop().sound(SoundType.MUD_BRICKS), Blocks.MUD_BRICK_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_MYCELIUM = BLOCKS.register("reinforced_mycelium", () -> new ReinforcedSnowyDirtBlock(prop(Material.GRASS).sound(SoundType.GRASS), Blocks.MYCELIUM));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_NETHER_BRICKS = BLOCKS.register("reinforced_nether_bricks", () -> new BaseReinforcedBlock(prop().sound(SoundType.NETHER_BRICKS), Blocks.NETHER_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CRACKED_NETHER_BRICKS = BLOCKS.register("reinforced_cracked_nether_bricks", () -> new BaseReinforcedBlock(prop().sound(SoundType.NETHER_BRICKS), Blocks.CRACKED_NETHER_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CHISELED_NETHER_BRICKS = BLOCKS.register("reinforced_chiseled_nether_bricks", () -> new BaseReinforcedBlock(prop().sound(SoundType.NETHER_BRICKS), Blocks.CHISELED_NETHER_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_NETHER_BRICK_STAIRS = BLOCKS.register("reinforced_nether_brick_stairs", () -> new ReinforcedStairsBlock(prop().sound(SoundType.NETHER_BRICKS), Blocks.NETHER_BRICK_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_END_STONE = BLOCKS.register("reinforced_end_stone", () -> new BaseReinforcedBlock(prop(), Blocks.END_STONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_END_STONE_BRICKS = BLOCKS.register("reinforced_end_stone_bricks", () -> new BaseReinforcedBlock(prop(), Blocks.END_STONE_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SANDSTONE_STAIRS = BLOCKS.register("reinforced_sandstone_stairs", () -> new ReinforcedStairsBlock(prop(), Blocks.SANDSTONE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_EMERALD_BLOCK = BLOCKS.register("reinforced_emerald_block", () -> new BaseReinforcedBlock(prop(Material.METAL).sound(SoundType.METAL), Blocks.EMERALD_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SPRUCE_STAIRS = BLOCKS.register("reinforced_spruce_stairs", () -> new ReinforcedStairsBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.SPRUCE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BIRCH_STAIRS = BLOCKS.register("reinforced_birch_stairs", () -> new ReinforcedStairsBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.BIRCH_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_JUNGLE_STAIRS = BLOCKS.register("reinforced_jungle_stairs", () -> new ReinforcedStairsBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.JUNGLE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CRIMSON_STAIRS = BLOCKS.register("reinforced_crimson_stairs", () -> new ReinforcedStairsBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.CRIMSON_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_WARPED_STAIRS = BLOCKS.register("reinforced_warped_stairs", () -> new ReinforcedStairsBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.WARPED_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CHISELED_QUARTZ = BLOCKS.register("reinforced_chiseled_quartz_block", () -> new BaseReinforcedBlock(prop(), Blocks.CHISELED_QUARTZ_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_QUARTZ = BLOCKS.register("reinforced_quartz_block", () -> new BaseReinforcedBlock(prop(), Blocks.QUARTZ_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_QUARTZ_BRICKS = BLOCKS.register("reinforced_quartz_bricks", () -> new BaseReinforcedBlock(prop(), Blocks.QUARTZ_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_QUARTZ_PILLAR = BLOCKS.register("reinforced_quartz_pillar", () -> new ReinforcedRotatedPillarBlock(prop(), Blocks.QUARTZ_PILLAR));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_QUARTZ_STAIRS = BLOCKS.register("reinforced_quartz_stairs", () -> new ReinforcedStairsBlock(prop(), Blocks.QUARTZ_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_WHITE_TERRACOTTA = BLOCKS.register("reinforced_white_terracotta", () -> new BaseReinforcedBlock(prop(), Blocks.WHITE_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_ORANGE_TERRACOTTA = BLOCKS.register("reinforced_orange_terracotta", () -> new BaseReinforcedBlock(prop(), Blocks.ORANGE_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_MAGENTA_TERRACOTTA = BLOCKS.register("reinforced_magenta_terracotta", () -> new BaseReinforcedBlock(prop(), Blocks.MAGENTA_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_LIGHT_BLUE_TERRACOTTA = BLOCKS.register("reinforced_light_blue_terracotta", () -> new BaseReinforcedBlock(prop(), Blocks.LIGHT_BLUE_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_YELLOW_TERRACOTTA = BLOCKS.register("reinforced_yellow_terracotta", () -> new BaseReinforcedBlock(prop(), Blocks.YELLOW_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_LIME_TERRACOTTA = BLOCKS.register("reinforced_lime_terracotta", () -> new BaseReinforcedBlock(prop(), Blocks.LIME_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PINK_TERRACOTTA = BLOCKS.register("reinforced_pink_terracotta", () -> new BaseReinforcedBlock(prop(), Blocks.PINK_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_GRAY_TERRACOTTA = BLOCKS.register("reinforced_gray_terracotta", () -> new BaseReinforcedBlock(prop(), Blocks.GRAY_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_LIGHT_GRAY_TERRACOTTA = BLOCKS.register("reinforced_light_gray_terracotta", () -> new BaseReinforcedBlock(prop(), Blocks.LIGHT_GRAY_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CYAN_TERRACOTTA = BLOCKS.register("reinforced_cyan_terracotta", () -> new BaseReinforcedBlock(prop(), Blocks.CYAN_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PURPLE_TERRACOTTA = BLOCKS.register("reinforced_purple_terracotta", () -> new BaseReinforcedBlock(prop(), Blocks.PURPLE_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BLUE_TERRACOTTA = BLOCKS.register("reinforced_blue_terracotta", () -> new BaseReinforcedBlock(prop(), Blocks.BLUE_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BROWN_TERRACOTTA = BLOCKS.register("reinforced_brown_terracotta", () -> new BaseReinforcedBlock(prop(), Blocks.BROWN_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_GREEN_TERRACOTTA = BLOCKS.register("reinforced_green_terracotta", () -> new BaseReinforcedBlock(prop(), Blocks.GREEN_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_RED_TERRACOTTA = BLOCKS.register("reinforced_red_terracotta", () -> new BaseReinforcedBlock(prop(), Blocks.RED_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BLACK_TERRACOTTA = BLOCKS.register("reinforced_black_terracotta", () -> new BaseReinforcedBlock(prop(), Blocks.BLACK_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_TERRACOTTA = BLOCKS.register("reinforced_hardened_clay", () -> new BaseReinforcedBlock(prop(), Blocks.TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PACKED_ICE = BLOCKS.register("reinforced_packed_ice", () -> new BaseReinforcedBlock(prop(Material.ICE_SOLID).sound(SoundType.GLASS).friction(0.98F), Blocks.PACKED_ICE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_ACACIA_STAIRS = BLOCKS.register("reinforced_acacia_stairs", () -> new ReinforcedStairsBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.ACACIA_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DARK_OAK_STAIRS = BLOCKS.register("reinforced_dark_oak_stairs", () -> new ReinforcedStairsBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.DARK_OAK_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_MANGROVE_STAIRS = BLOCKS.register("reinforced_mangrove_stairs", () -> new ReinforcedStairsBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.MANGROVE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<Block> REINFORCED_WHITE_STAINED_GLASS = BLOCKS.register("reinforced_white_stained_glass", () -> new ReinforcedStainedGlassBlock(glassProp(), DyeColor.WHITE, Blocks.WHITE_STAINED_GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<Block> REINFORCED_ORANGE_STAINED_GLASS = BLOCKS.register("reinforced_orange_stained_glass", () -> new ReinforcedStainedGlassBlock(glassProp(), DyeColor.ORANGE, Blocks.ORANGE_STAINED_GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<Block> REINFORCED_MAGENTA_STAINED_GLASS = BLOCKS.register("reinforced_magenta_stained_glass", () -> new ReinforcedStainedGlassBlock(glassProp(), DyeColor.MAGENTA, Blocks.MAGENTA_STAINED_GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<Block> REINFORCED_LIGHT_BLUE_STAINED_GLASS = BLOCKS.register("reinforced_light_blue_stained_glass", () -> new ReinforcedStainedGlassBlock(glassProp(), DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_STAINED_GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<Block> REINFORCED_YELLOW_STAINED_GLASS = BLOCKS.register("reinforced_yellow_stained_glass", () -> new ReinforcedStainedGlassBlock(glassProp(), DyeColor.YELLOW, Blocks.YELLOW_STAINED_GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<Block> REINFORCED_LIME_STAINED_GLASS = BLOCKS.register("reinforced_lime_stained_glass", () -> new ReinforcedStainedGlassBlock(glassProp(), DyeColor.LIME, Blocks.LIME_STAINED_GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<Block> REINFORCED_PINK_STAINED_GLASS = BLOCKS.register("reinforced_pink_stained_glass", () -> new ReinforcedStainedGlassBlock(glassProp(), DyeColor.PINK, Blocks.PINK_STAINED_GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<Block> REINFORCED_GRAY_STAINED_GLASS = BLOCKS.register("reinforced_gray_stained_glass", () -> new ReinforcedStainedGlassBlock(glassProp(), DyeColor.GRAY, Blocks.GRAY_STAINED_GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<Block> REINFORCED_LIGHT_GRAY_STAINED_GLASS = BLOCKS.register("reinforced_light_gray_stained_glass", () -> new ReinforcedStainedGlassBlock(glassProp(), DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_STAINED_GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<Block> REINFORCED_CYAN_STAINED_GLASS = BLOCKS.register("reinforced_cyan_stained_glass", () -> new ReinforcedStainedGlassBlock(glassProp(), DyeColor.CYAN, Blocks.CYAN_STAINED_GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<Block> REINFORCED_PURPLE_STAINED_GLASS = BLOCKS.register("reinforced_purple_stained_glass", () -> new ReinforcedStainedGlassBlock(glassProp(), DyeColor.PURPLE, Blocks.PURPLE_STAINED_GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<Block> REINFORCED_BLUE_STAINED_GLASS = BLOCKS.register("reinforced_blue_stained_glass", () -> new ReinforcedStainedGlassBlock(glassProp(), DyeColor.BLUE, Blocks.BLUE_STAINED_GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<Block> REINFORCED_BROWN_STAINED_GLASS = BLOCKS.register("reinforced_brown_stained_glass", () -> new ReinforcedStainedGlassBlock(glassProp(), DyeColor.BROWN, Blocks.BROWN_STAINED_GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<Block> REINFORCED_GREEN_STAINED_GLASS = BLOCKS.register("reinforced_green_stained_glass", () -> new ReinforcedStainedGlassBlock(glassProp(), DyeColor.GREEN, Blocks.GREEN_STAINED_GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<Block> REINFORCED_RED_STAINED_GLASS = BLOCKS.register("reinforced_red_stained_glass", () -> new ReinforcedStainedGlassBlock(glassProp(), DyeColor.RED, Blocks.RED_STAINED_GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<Block> REINFORCED_BLACK_STAINED_GLASS = BLOCKS.register("reinforced_black_stained_glass", () -> new ReinforcedStainedGlassBlock(glassProp(), DyeColor.BLACK, Blocks.BLACK_STAINED_GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PRISMARINE = BLOCKS.register("reinforced_prismarine", () -> new BaseReinforcedBlock(prop(), Blocks.PRISMARINE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PRISMARINE_BRICKS = BLOCKS.register("reinforced_prismarine_bricks", () -> new BaseReinforcedBlock(prop(), Blocks.PRISMARINE_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DARK_PRISMARINE = BLOCKS.register("reinforced_dark_prismarine", () -> new BaseReinforcedBlock(prop(), Blocks.DARK_PRISMARINE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PRISMARINE_STAIRS = BLOCKS.register("reinforced_prismarine_stairs", () -> new ReinforcedStairsBlock(prop(), Blocks.PRISMARINE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PRISMARINE_BRICK_STAIRS = BLOCKS.register("reinforced_prismarine_brick_stairs", () -> new ReinforcedStairsBlock(prop(), Blocks.PRISMARINE_BRICK_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DARK_PRISMARINE_STAIRS = BLOCKS.register("reinforced_dark_prismarine_stairs", () -> new ReinforcedStairsBlock(prop(), Blocks.DARK_PRISMARINE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SEA_LANTERN = BLOCKS.register("reinforced_sea_lantern", () -> new BaseReinforcedBlock(prop(Material.GLASS).sound(SoundType.GLASS).lightLevel(state -> 15), Blocks.SEA_LANTERN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_RED_SANDSTONE = BLOCKS.register("reinforced_red_sandstone", () -> new BaseReinforcedBlock(prop(), Blocks.RED_SANDSTONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CHISELED_RED_SANDSTONE = BLOCKS.register("reinforced_chiseled_red_sandstone", () -> new BaseReinforcedBlock(prop(), Blocks.CHISELED_RED_SANDSTONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CUT_RED_SANDSTONE = BLOCKS.register("reinforced_cut_red_sandstone", () -> new BaseReinforcedBlock(prop(), Blocks.CUT_RED_SANDSTONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_RED_SANDSTONE_STAIRS = BLOCKS.register("reinforced_red_sandstone_stairs", () -> new ReinforcedStairsBlock(prop(), Blocks.RED_SANDSTONE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_NETHER_WART_BLOCK = BLOCKS.register("reinforced_nether_wart_block", () -> new BaseReinforcedBlock(prop(Material.GRASS).sound(SoundType.WART_BLOCK), Blocks.NETHER_WART_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_WARPED_WART_BLOCK = BLOCKS.register("reinforced_warped_wart_block", () -> new BaseReinforcedBlock(prop(Material.GRASS).sound(SoundType.WART_BLOCK), Blocks.WARPED_WART_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_RED_NETHER_BRICKS = BLOCKS.register("reinforced_red_nether_bricks", () -> new BaseReinforcedBlock(prop().sound(SoundType.NETHER_BRICKS), Blocks.RED_NETHER_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BONE_BLOCK = BLOCKS.register("reinforced_bone_block", () -> new ReinforcedRotatedPillarBlock(prop().sound(SoundType.BONE_BLOCK), Blocks.BONE_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_WHITE_CONCRETE = BLOCKS.register("reinforced_white_concrete", () -> new BaseReinforcedBlock(prop(), Blocks.WHITE_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_ORANGE_CONCRETE = BLOCKS.register("reinforced_orange_concrete", () -> new BaseReinforcedBlock(prop(), Blocks.ORANGE_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_MAGENTA_CONCRETE = BLOCKS.register("reinforced_magenta_concrete", () -> new BaseReinforcedBlock(prop(), Blocks.MAGENTA_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_LIGHT_BLUE_CONCRETE = BLOCKS.register("reinforced_light_blue_concrete", () -> new BaseReinforcedBlock(prop(), Blocks.LIGHT_BLUE_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_YELLOW_CONCRETE = BLOCKS.register("reinforced_yellow_concrete", () -> new BaseReinforcedBlock(prop(), Blocks.YELLOW_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_LIME_CONCRETE = BLOCKS.register("reinforced_lime_concrete", () -> new BaseReinforcedBlock(prop(), Blocks.LIME_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PINK_CONCRETE = BLOCKS.register("reinforced_pink_concrete", () -> new BaseReinforcedBlock(prop(), Blocks.PINK_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_GRAY_CONCRETE = BLOCKS.register("reinforced_gray_concrete", () -> new BaseReinforcedBlock(prop(), Blocks.GRAY_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_LIGHT_GRAY_CONCRETE = BLOCKS.register("reinforced_light_gray_concrete", () -> new BaseReinforcedBlock(prop(), Blocks.LIGHT_GRAY_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CYAN_CONCRETE = BLOCKS.register("reinforced_cyan_concrete", () -> new BaseReinforcedBlock(prop(), Blocks.CYAN_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PURPLE_CONCRETE = BLOCKS.register("reinforced_purple_concrete", () -> new BaseReinforcedBlock(prop(), Blocks.PURPLE_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BLUE_CONCRETE = BLOCKS.register("reinforced_blue_concrete", () -> new BaseReinforcedBlock(prop(), Blocks.BLUE_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BROWN_CONCRETE = BLOCKS.register("reinforced_brown_concrete", () -> new BaseReinforcedBlock(prop(), Blocks.BROWN_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_GREEN_CONCRETE = BLOCKS.register("reinforced_green_concrete", () -> new BaseReinforcedBlock(prop(), Blocks.GREEN_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_RED_CONCRETE = BLOCKS.register("reinforced_red_concrete", () -> new BaseReinforcedBlock(prop(), Blocks.RED_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BLACK_CONCRETE = BLOCKS.register("reinforced_black_concrete", () -> new BaseReinforcedBlock(prop(), Blocks.BLACK_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BLUE_ICE = BLOCKS.register("reinforced_blue_ice", () -> new BaseReinforcedBlock(prop(Material.ICE_SOLID).sound(SoundType.GLASS).friction(0.989F), Blocks.BLUE_ICE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_GRANITE_STAIRS = BLOCKS.register("reinforced_polished_granite_stairs", () -> new ReinforcedStairsBlock(prop(), Blocks.POLISHED_GRANITE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SMOOTH_RED_SANDSTONE_STAIRS = BLOCKS.register("reinforced_smooth_red_sandstone_stairs", () -> new ReinforcedStairsBlock(prop(), Blocks.SMOOTH_RED_SANDSTONE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_MOSSY_STONE_BRICK_STAIRS = BLOCKS.register("reinforced_mossy_stone_brick_stairs", () -> new ReinforcedStairsBlock(prop(), Blocks.MOSSY_STONE_BRICK_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_DIORITE_STAIRS = BLOCKS.register("reinforced_polished_diorite_stairs", () -> new ReinforcedStairsBlock(prop(), Blocks.POLISHED_DIORITE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_MOSSY_COBBLESTONE_STAIRS = BLOCKS.register("reinforced_mossy_cobblestone_stairs", () -> new ReinforcedStairsBlock(prop(), Blocks.MOSSY_COBBLESTONE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_END_STONE_BRICK_STAIRS = BLOCKS.register("reinforced_end_stone_brick_stairs", () -> new ReinforcedStairsBlock(prop(), Blocks.END_STONE_BRICK_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STONE_STAIRS = BLOCKS.register("reinforced_stone_stairs", () -> new ReinforcedStairsBlock(prop(), Blocks.STONE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SMOOTH_SANDSTONE_STAIRS = BLOCKS.register("reinforced_smooth_sandstone_stairs", () -> new ReinforcedStairsBlock(prop(), Blocks.SMOOTH_SANDSTONE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SMOOTH_QUARTZ_STAIRS = BLOCKS.register("reinforced_smooth_quartz_stairs", () -> new ReinforcedStairsBlock(prop(), Blocks.SMOOTH_QUARTZ_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_GRANITE_STAIRS = BLOCKS.register("reinforced_granite_stairs", () -> new ReinforcedStairsBlock(prop(), Blocks.GRANITE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_ANDESITE_STAIRS = BLOCKS.register("reinforced_andesite_stairs", () -> new ReinforcedStairsBlock(prop(), Blocks.ANDESITE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_RED_NETHER_BRICK_STAIRS = BLOCKS.register("reinforced_red_nether_brick_stairs", () -> new ReinforcedStairsBlock(prop().sound(SoundType.NETHER_BRICKS), Blocks.RED_NETHER_BRICK_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_ANDESITE_STAIRS = BLOCKS.register("reinforced_polished_andesite_stairs", () -> new ReinforcedStairsBlock(prop(), Blocks.POLISHED_ANDESITE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DIORITE_STAIRS = BLOCKS.register("reinforced_diorite_stairs", () -> new ReinforcedStairsBlock(prop(), Blocks.DIORITE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_COBBLED_DEEPSLATE_STAIRS = BLOCKS.register("reinforced_cobbled_deepslate_stairs", () -> new ReinforcedStairsBlock(prop().sound(SoundType.DEEPSLATE), Blocks.COBBLED_DEEPSLATE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_DEEPSLATE_STAIRS = BLOCKS.register("reinforced_polished_deepslate_stairs", () -> new ReinforcedStairsBlock(prop().sound(SoundType.POLISHED_DEEPSLATE), Blocks.POLISHED_DEEPSLATE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DEEPSLATE_BRICK_STAIRS = BLOCKS.register("reinforced_deepslate_brick_stairs", () -> new ReinforcedStairsBlock(prop().sound(SoundType.DEEPSLATE_BRICKS), Blocks.DEEPSLATE_BRICK_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DEEPSLATE_TILE_STAIRS = BLOCKS.register("reinforced_deepslate_tile_stairs", () -> new ReinforcedStairsBlock(prop().sound(SoundType.DEEPSLATE_TILES), Blocks.DEEPSLATE_TILE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_GRANITE_SLAB = BLOCKS.register("reinforced_polished_granite_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.POLISHED_GRANITE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SMOOTH_RED_SANDSTONE_SLAB = BLOCKS.register("reinforced_smooth_red_sandstone_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.SMOOTH_RED_SANDSTONE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_MOSSY_STONE_BRICK_SLAB = BLOCKS.register("reinforced_mossy_stone_brick_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.MOSSY_STONE_BRICK_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_DIORITE_SLAB = BLOCKS.register("reinforced_polished_diorite_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.POLISHED_DIORITE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_MOSSY_COBBLESTONE_SLAB = BLOCKS.register("reinforced_mossy_cobblestone_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.MOSSY_COBBLESTONE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_END_STONE_BRICK_SLAB = BLOCKS.register("reinforced_end_stone_brick_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.END_STONE_BRICK_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SMOOTH_SANDSTONE_SLAB = BLOCKS.register("reinforced_smooth_sandstone_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.SMOOTH_SANDSTONE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SMOOTH_QUARTZ_SLAB = BLOCKS.register("reinforced_smooth_quartz_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.SMOOTH_QUARTZ_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_GRANITE_SLAB = BLOCKS.register("reinforced_granite_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.GRANITE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_ANDESITE_SLAB = BLOCKS.register("reinforced_andesite_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.ANDESITE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_RED_NETHER_BRICK_SLAB = BLOCKS.register("reinforced_red_nether_brick_slab", () -> new ReinforcedSlabBlock(prop().sound(SoundType.NETHER_BRICKS), Blocks.RED_NETHER_BRICK_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_ANDESITE_SLAB = BLOCKS.register("reinforced_polished_andesite_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.POLISHED_ANDESITE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DIORITE_SLAB = BLOCKS.register("reinforced_diorite_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.DIORITE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_COBBLED_DEEPSLATE_SLAB = BLOCKS.register("reinforced_cobbled_deepslate_slab", () -> new ReinforcedSlabBlock(prop().sound(SoundType.DEEPSLATE), Blocks.COBBLED_DEEPSLATE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_DEEPSLATE_SLAB = BLOCKS.register("reinforced_polished_deepslate_slab", () -> new ReinforcedSlabBlock(prop().sound(SoundType.POLISHED_DEEPSLATE), Blocks.POLISHED_DEEPSLATE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DEEPSLATE_BRICK_SLAB = BLOCKS.register("reinforced_deepslate_brick_slab", () -> new ReinforcedSlabBlock(prop().sound(SoundType.DEEPSLATE_BRICKS), Blocks.DEEPSLATE_BRICK_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DEEPSLATE_TILE_SLAB = BLOCKS.register("reinforced_deepslate_tile_slab", () -> new ReinforcedSlabBlock(prop().sound(SoundType.DEEPSLATE_TILES), Blocks.DEEPSLATE_TILE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CRYING_OBSIDIAN = BLOCKS.register("reinforced_crying_obsidian", () -> new ReinforcedCryingObsidianBlock(prop().lightLevel(state -> 10), Blocks.CRYING_OBSIDIAN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BLACKSTONE = BLOCKS.register("reinforced_blackstone", () -> new BaseReinforcedBlock(prop(), Blocks.BLACKSTONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BLACKSTONE_SLAB = BLOCKS.register("reinforced_blackstone_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.BLACKSTONE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BLACKSTONE_STAIRS = BLOCKS.register("reinforced_blackstone_stairs", () -> new ReinforcedStairsBlock(prop(), Blocks.BLACKSTONE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_BLACKSTONE = BLOCKS.register("reinforced_polished_blackstone", () -> new BaseReinforcedBlock(prop(), Blocks.POLISHED_BLACKSTONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_BLACKSTONE_SLAB = BLOCKS.register("reinforced_polished_blackstone_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.POLISHED_BLACKSTONE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_BLACKSTONE_STAIRS = BLOCKS.register("reinforced_polished_blackstone_stairs", () -> new ReinforcedStairsBlock(prop(), Blocks.POLISHED_BLACKSTONE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CHISELED_POLISHED_BLACKSTONE = BLOCKS.register("reinforced_chiseled_polished_blackstone", () -> new BaseReinforcedBlock(prop(), Blocks.CHISELED_POLISHED_BLACKSTONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_BLACKSTONE_BRICKS = BLOCKS.register("reinforced_polished_blackstone_bricks", () -> new BaseReinforcedBlock(prop(), Blocks.POLISHED_BLACKSTONE_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_BLACKSTONE_BRICK_SLAB = BLOCKS.register("reinforced_polished_blackstone_brick_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.POLISHED_BLACKSTONE_BRICK_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_BLACKSTONE_BRICK_STAIRS = BLOCKS.register("reinforced_polished_blackstone_brick_stairs", () -> new ReinforcedStairsBlock(prop(), Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CRACKED_POLISHED_BLACKSTONE_BRICKS = BLOCKS.register("reinforced_cracked_polished_blackstone_bricks", () -> new BaseReinforcedBlock(prop(), Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS));

	//ordered by vanilla decoration blocks creative tab order
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_COBWEB = BLOCKS.register("reinforced_cobweb", () -> new ReinforcedCobwebBlock(prop(Material.CACTUS, MaterialColor.WOOL).noCollission(), Blocks.COBWEB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_MOSS_CARPET = BLOCKS.register("reinforced_moss_carpet", () -> new ReinforcedCarpetBlock(prop(Material.MOSS).sound(SoundType.MOSS), Blocks.MOSS_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_MOSS_BLOCK = BLOCKS.register("reinforced_moss_block", () -> new BaseReinforcedBlock(prop(Material.MOSS).sound(SoundType.MOSS), Blocks.MOSS_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_END_ROD = BLOCKS.register("reinforced_end_rod", () -> new ReinforcedEndRodBlock(prop(Material.DECORATION).lightLevel(state -> 14).sound(SoundType.WOOD).noOcclusion()));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<Block> REINFORCED_IRON_BARS = BLOCKS.register("reinforced_iron_bars", () -> new ReinforcedIronBarsBlock(prop(Material.METAL).sound(SoundType.METAL), Blocks.IRON_BARS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CHAIN = BLOCKS.register("reinforced_chain", () -> new ReinforcedChainBlock(prop(Material.METAL).sound(SoundType.CHAIN), Blocks.CHAIN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<Block> REINFORCED_GLASS_PANE = BLOCKS.register("reinforced_glass_pane", () -> new ReinforcedPaneBlock(prop(Material.GLASS).sound(SoundType.GLASS), Blocks.GLASS_PANE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_COBBLESTONE_WALL = BLOCKS.register("reinforced_cobblestone_wall", () -> new ReinforcedWallBlock(prop(), Blocks.COBBLESTONE_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_MOSSY_COBBLESTONE_WALL = BLOCKS.register("reinforced_mossy_cobblestone_wall", () -> new ReinforcedWallBlock(prop(), Blocks.MOSSY_COBBLESTONE_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BRICK_WALL = BLOCKS.register("reinforced_brick_wall", () -> new ReinforcedWallBlock(prop(), Blocks.BRICK_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PRISMARINE_WALL = BLOCKS.register("reinforced_prismarine_wall", () -> new ReinforcedWallBlock(prop(), Blocks.PRISMARINE_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_RED_SANDSTONE_WALL = BLOCKS.register("reinforced_red_sandstone_wall", () -> new ReinforcedWallBlock(prop(), Blocks.RED_SANDSTONE_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_MOSSY_STONE_BRICK_WALL = BLOCKS.register("reinforced_mossy_stone_brick_wall", () -> new ReinforcedWallBlock(prop(), Blocks.MOSSY_STONE_BRICK_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_GRANITE_WALL = BLOCKS.register("reinforced_granite_wall", () -> new ReinforcedWallBlock(prop(), Blocks.GRANITE_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STONE_BRICK_WALL = BLOCKS.register("reinforced_stone_brick_wall", () -> new ReinforcedWallBlock(prop(), Blocks.STONE_BRICK_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_MUD_BRICK_WALL = BLOCKS.register("reinforced_mud_brick_wall", () -> new ReinforcedWallBlock(prop().sound(SoundType.MUD_BRICKS), Blocks.MUD_BRICK_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_NETHER_BRICK_WALL = BLOCKS.register("reinforced_nether_brick_wall", () -> new ReinforcedWallBlock(prop().sound(SoundType.NETHER_BRICKS), Blocks.NETHER_BRICK_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_ANDESITE_WALL = BLOCKS.register("reinforced_andesite_wall", () -> new ReinforcedWallBlock(prop(), Blocks.ANDESITE_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_RED_NETHER_BRICK_WALL = BLOCKS.register("reinforced_red_nether_brick_wall", () -> new ReinforcedWallBlock(prop().sound(SoundType.NETHER_BRICKS), Blocks.RED_NETHER_BRICK_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SANDSTONE_WALL = BLOCKS.register("reinforced_sandstone_wall", () -> new ReinforcedWallBlock(prop(), Blocks.SANDSTONE_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_END_STONE_BRICK_WALL = BLOCKS.register("reinforced_end_stone_brick_wall", () -> new ReinforcedWallBlock(prop(), Blocks.END_STONE_BRICK_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DIORITE_WALL = BLOCKS.register("reinforced_diorite_wall", () -> new ReinforcedWallBlock(prop(), Blocks.DIORITE_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BLACKSTONE_WALL = BLOCKS.register("reinforced_blackstone_wall", () -> new ReinforcedWallBlock(prop(), Blocks.BLACKSTONE_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_BLACKSTONE_WALL = BLOCKS.register("reinforced_polished_blackstone_wall", () -> new ReinforcedWallBlock(prop(), Blocks.POLISHED_BLACKSTONE_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_BLACKSTONE_BRICK_WALL = BLOCKS.register("reinforced_polished_blackstone_brick_wall", () -> new ReinforcedWallBlock(prop(), Blocks.POLISHED_BLACKSTONE_BRICK_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_COBBLED_DEEPSLATE_WALL = BLOCKS.register("reinforced_cobbled_deepslate_wall", () -> new ReinforcedWallBlock(prop().sound(SoundType.DEEPSLATE), Blocks.COBBLED_DEEPSLATE_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_DEEPSLATE_WALL = BLOCKS.register("reinforced_polished_deepslate_wall", () -> new ReinforcedWallBlock(prop().sound(SoundType.POLISHED_DEEPSLATE), Blocks.POLISHED_DEEPSLATE_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DEEPSLATE_BRICK_WALL = BLOCKS.register("reinforced_deepslate_brick_wall", () -> new ReinforcedWallBlock(prop().sound(SoundType.DEEPSLATE_BRICKS), Blocks.DEEPSLATE_BRICK_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DEEPSLATE_TILE_WALL = BLOCKS.register("reinforced_deepslate_tile_wall", () -> new ReinforcedWallBlock(prop().sound(SoundType.DEEPSLATE_TILES), Blocks.DEEPSLATE_TILE_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_WHITE_CARPET = BLOCKS.register("reinforced_white_carpet", () -> new ReinforcedCarpetBlock(prop(Material.WOOL).sound(SoundType.WOOL), Blocks.WHITE_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_ORANGE_CARPET = BLOCKS.register("reinforced_orange_carpet", () -> new ReinforcedCarpetBlock(prop(Material.WOOL).sound(SoundType.WOOL), Blocks.ORANGE_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_MAGENTA_CARPET = BLOCKS.register("reinforced_magenta_carpet", () -> new ReinforcedCarpetBlock(prop(Material.WOOL).sound(SoundType.WOOL), Blocks.MAGENTA_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_LIGHT_BLUE_CARPET = BLOCKS.register("reinforced_light_blue_carpet", () -> new ReinforcedCarpetBlock(prop(Material.WOOL).sound(SoundType.WOOL), Blocks.LIGHT_BLUE_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_YELLOW_CARPET = BLOCKS.register("reinforced_yellow_carpet", () -> new ReinforcedCarpetBlock(prop(Material.WOOL).sound(SoundType.WOOL), Blocks.YELLOW_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_LIME_CARPET = BLOCKS.register("reinforced_lime_carpet", () -> new ReinforcedCarpetBlock(prop(Material.WOOL).sound(SoundType.WOOL), Blocks.LIME_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PINK_CARPET = BLOCKS.register("reinforced_pink_carpet", () -> new ReinforcedCarpetBlock(prop(Material.WOOL).sound(SoundType.WOOL), Blocks.PINK_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_GRAY_CARPET = BLOCKS.register("reinforced_gray_carpet", () -> new ReinforcedCarpetBlock(prop(Material.WOOL).sound(SoundType.WOOL), Blocks.GRAY_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_LIGHT_GRAY_CARPET = BLOCKS.register("reinforced_light_gray_carpet", () -> new ReinforcedCarpetBlock(prop(Material.WOOL).sound(SoundType.WOOL), Blocks.LIGHT_GRAY_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CYAN_CARPET = BLOCKS.register("reinforced_cyan_carpet", () -> new ReinforcedCarpetBlock(prop(Material.WOOL).sound(SoundType.WOOL), Blocks.CYAN_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PURPLE_CARPET = BLOCKS.register("reinforced_purple_carpet", () -> new ReinforcedCarpetBlock(prop(Material.WOOL).sound(SoundType.WOOL), Blocks.PURPLE_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BLUE_CARPET = BLOCKS.register("reinforced_blue_carpet", () -> new ReinforcedCarpetBlock(prop(Material.WOOL).sound(SoundType.WOOL), Blocks.BLUE_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BROWN_CARPET = BLOCKS.register("reinforced_brown_carpet", () -> new ReinforcedCarpetBlock(prop(Material.WOOL).sound(SoundType.WOOL), Blocks.BROWN_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_GREEN_CARPET = BLOCKS.register("reinforced_green_carpet", () -> new ReinforcedCarpetBlock(prop(Material.WOOL).sound(SoundType.WOOL), Blocks.GREEN_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_RED_CARPET = BLOCKS.register("reinforced_red_carpet", () -> new ReinforcedCarpetBlock(prop(Material.WOOL).sound(SoundType.WOOL), Blocks.RED_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BLACK_CARPET = BLOCKS.register("reinforced_black_carpet", () -> new ReinforcedCarpetBlock(prop(Material.WOOL).sound(SoundType.WOOL), Blocks.BLACK_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<Block> REINFORCED_WHITE_STAINED_GLASS_PANE = BLOCKS.register("reinforced_white_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(prop(Material.GLASS).sound(SoundType.GLASS), DyeColor.WHITE, Blocks.WHITE_STAINED_GLASS_PANE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<Block> REINFORCED_ORANGE_STAINED_GLASS_PANE = BLOCKS.register("reinforced_orange_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(prop(Material.GLASS).sound(SoundType.GLASS), DyeColor.ORANGE, Blocks.ORANGE_STAINED_GLASS_PANE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<Block> REINFORCED_MAGENTA_STAINED_GLASS_PANE = BLOCKS.register("reinforced_magenta_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(prop(Material.GLASS).sound(SoundType.GLASS), DyeColor.MAGENTA, Blocks.MAGENTA_STAINED_GLASS_PANE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<Block> REINFORCED_LIGHT_BLUE_STAINED_GLASS_PANE = BLOCKS.register("reinforced_light_blue_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(prop(Material.GLASS).sound(SoundType.GLASS), DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<Block> REINFORCED_YELLOW_STAINED_GLASS_PANE = BLOCKS.register("reinforced_yellow_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(prop(Material.GLASS).sound(SoundType.GLASS), DyeColor.YELLOW, Blocks.YELLOW_STAINED_GLASS_PANE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<Block> REINFORCED_LIME_STAINED_GLASS_PANE = BLOCKS.register("reinforced_lime_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(prop(Material.GLASS).sound(SoundType.GLASS), DyeColor.LIME, Blocks.LIME_STAINED_GLASS_PANE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<Block> REINFORCED_PINK_STAINED_GLASS_PANE = BLOCKS.register("reinforced_pink_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(prop(Material.GLASS).sound(SoundType.GLASS), DyeColor.PINK, Blocks.PINK_STAINED_GLASS_PANE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<Block> REINFORCED_GRAY_STAINED_GLASS_PANE = BLOCKS.register("reinforced_gray_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(prop(Material.GLASS).sound(SoundType.GLASS), DyeColor.GRAY, Blocks.GRAY_STAINED_GLASS_PANE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<Block> REINFORCED_LIGHT_GRAY_STAINED_GLASS_PANE = BLOCKS.register("reinforced_light_gray_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(prop(Material.GLASS).sound(SoundType.GLASS), DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<Block> REINFORCED_CYAN_STAINED_GLASS_PANE = BLOCKS.register("reinforced_cyan_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(prop(Material.GLASS).sound(SoundType.GLASS), DyeColor.CYAN, Blocks.CYAN_STAINED_GLASS_PANE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<Block> REINFORCED_PURPLE_STAINED_GLASS_PANE = BLOCKS.register("reinforced_purple_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(prop(Material.GLASS).sound(SoundType.GLASS), DyeColor.PURPLE, Blocks.PURPLE_STAINED_GLASS_PANE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<Block> REINFORCED_BLUE_STAINED_GLASS_PANE = BLOCKS.register("reinforced_blue_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(prop(Material.GLASS).sound(SoundType.GLASS), DyeColor.BLUE, Blocks.BLUE_STAINED_GLASS_PANE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<Block> REINFORCED_BROWN_STAINED_GLASS_PANE = BLOCKS.register("reinforced_brown_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(prop(Material.GLASS).sound(SoundType.GLASS), DyeColor.BROWN, Blocks.BROWN_STAINED_GLASS_PANE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<Block> REINFORCED_GREEN_STAINED_GLASS_PANE = BLOCKS.register("reinforced_green_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(prop(Material.GLASS).sound(SoundType.GLASS), DyeColor.GREEN, Blocks.GREEN_STAINED_GLASS_PANE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<Block> REINFORCED_RED_STAINED_GLASS_PANE = BLOCKS.register("reinforced_red_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(prop(Material.GLASS).sound(SoundType.GLASS), DyeColor.RED, Blocks.RED_STAINED_GLASS_PANE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<Block> REINFORCED_BLACK_STAINED_GLASS_PANE = BLOCKS.register("reinforced_black_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(prop(Material.GLASS).sound(SoundType.GLASS), DyeColor.BLACK, Blocks.BLACK_STAINED_GLASS_PANE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_WHITE_GLAZED_TERRACOTTA = BLOCKS.register("reinforced_white_glazed_terracotta", () -> new ReinforcedGlazedTerracottaBlock(prop(Material.STONE, DyeColor.WHITE.getMaterialColor()), Blocks.WHITE_GLAZED_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_ORANGE_GLAZED_TERRACOTTA = BLOCKS.register("reinforced_orange_glazed_terracotta", () -> new ReinforcedGlazedTerracottaBlock(prop(Material.STONE, DyeColor.ORANGE.getMaterialColor()), Blocks.ORANGE_GLAZED_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_MAGENTA_GLAZED_TERRACOTTA = BLOCKS.register("reinforced_magenta_glazed_terracotta", () -> new ReinforcedGlazedTerracottaBlock(prop(Material.STONE, DyeColor.MAGENTA.getMaterialColor()), Blocks.MAGENTA_GLAZED_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_LIGHT_BLUE_GLAZED_TERRACOTTA = BLOCKS.register("reinforced_light_blue_glazed_terracotta", () -> new ReinforcedGlazedTerracottaBlock(prop(Material.STONE, DyeColor.LIGHT_BLUE.getMaterialColor()), Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_YELLOW_GLAZED_TERRACOTTA = BLOCKS.register("reinforced_yellow_glazed_terracotta", () -> new ReinforcedGlazedTerracottaBlock(prop(Material.STONE, DyeColor.YELLOW.getMaterialColor()), Blocks.YELLOW_GLAZED_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_LIME_GLAZED_TERRACOTTA = BLOCKS.register("reinforced_lime_glazed_terracotta", () -> new ReinforcedGlazedTerracottaBlock(prop(Material.STONE, DyeColor.LIME.getMaterialColor()), Blocks.LIME_GLAZED_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PINK_GLAZED_TERRACOTTA = BLOCKS.register("reinforced_pink_glazed_terracotta", () -> new ReinforcedGlazedTerracottaBlock(prop(Material.STONE, DyeColor.PINK.getMaterialColor()), Blocks.PINK_GLAZED_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_GRAY_GLAZED_TERRACOTTA = BLOCKS.register("reinforced_gray_glazed_terracotta", () -> new ReinforcedGlazedTerracottaBlock(prop(Material.STONE, DyeColor.GRAY.getMaterialColor()), Blocks.GRAY_GLAZED_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_LIGHT_GRAY_GLAZED_TERRACOTTA = BLOCKS.register("reinforced_light_gray_glazed_terracotta", () -> new ReinforcedGlazedTerracottaBlock(prop(Material.STONE, DyeColor.LIGHT_GRAY.getMaterialColor()), Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CYAN_GLAZED_TERRACOTTA = BLOCKS.register("reinforced_cyan_glazed_terracotta", () -> new ReinforcedGlazedTerracottaBlock(prop(Material.STONE, DyeColor.CYAN.getMaterialColor()), Blocks.CYAN_GLAZED_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PURPLE_GLAZED_TERRACOTTA = BLOCKS.register("reinforced_purple_glazed_terracotta", () -> new ReinforcedGlazedTerracottaBlock(prop(Material.STONE, DyeColor.PURPLE.getMaterialColor()), Blocks.PURPLE_GLAZED_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BLUE_GLAZED_TERRACOTTA = BLOCKS.register("reinforced_blue_glazed_terracotta", () -> new ReinforcedGlazedTerracottaBlock(prop(Material.STONE, DyeColor.BLUE.getMaterialColor()), Blocks.BLUE_GLAZED_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BROWN_GLAZED_TERRACOTTA = BLOCKS.register("reinforced_brown_glazed_terracotta", () -> new ReinforcedGlazedTerracottaBlock(prop(Material.STONE, DyeColor.BROWN.getMaterialColor()), Blocks.BROWN_GLAZED_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_GREEN_GLAZED_TERRACOTTA = BLOCKS.register("reinforced_green_glazed_terracotta", () -> new ReinforcedGlazedTerracottaBlock(prop(Material.STONE, DyeColor.GREEN.getMaterialColor()), Blocks.GREEN_GLAZED_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_RED_GLAZED_TERRACOTTA = BLOCKS.register("reinforced_red_glazed_terracotta", () -> new ReinforcedGlazedTerracottaBlock(prop(Material.STONE, DyeColor.RED.getMaterialColor()), Blocks.RED_GLAZED_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BLACK_GLAZED_TERRACOTTA = BLOCKS.register("reinforced_black_glazed_terracotta", () -> new ReinforcedGlazedTerracottaBlock(prop(Material.STONE, DyeColor.BLACK.getMaterialColor()), Blocks.BLACK_GLAZED_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_LANTERN = BLOCKS.register("reinforced_lantern", () -> new ReinforcedLanternBlock(prop(Material.METAL).sound(SoundType.LANTERN).lightLevel(state -> 15), Blocks.LANTERN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SOUL_LANTERN = BLOCKS.register("reinforced_soul_lantern", () -> new ReinforcedLanternBlock(prop(Material.METAL).sound(SoundType.LANTERN).lightLevel(state -> 10), Blocks.SOUL_LANTERN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SHROOMLIGHT = BLOCKS.register("reinforced_shroomlight", () -> new BaseReinforcedBlock(prop(Material.GRASS).sound(SoundType.SHROOMLIGHT).lightLevel(state -> 15), Blocks.SHROOMLIGHT));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_OCHRE_FROGLIGHT = BLOCKS.register("reinforced_ochre_froglight", () -> new ReinforcedRotatedPillarBlock(prop(Material.FROGLIGHT, MaterialColor.SAND).sound(SoundType.FROGLIGHT).lightLevel(state -> 15), Blocks.OCHRE_FROGLIGHT));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_VERDANT_FROGLIGHT = BLOCKS.register("reinforced_verdant_froglight", () -> new ReinforcedRotatedPillarBlock(prop(Material.FROGLIGHT, MaterialColor.GLOW_LICHEN).sound(SoundType.FROGLIGHT).lightLevel(state -> 15), Blocks.VERDANT_FROGLIGHT));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PEARLESCENT_FROGLIGHT = BLOCKS.register("reinforced_pearlescent_froglight", () -> new ReinforcedRotatedPillarBlock(prop(Material.FROGLIGHT, MaterialColor.COLOR_PINK).sound(SoundType.FROGLIGHT).lightLevel(state -> 15), Blocks.PEARLESCENT_FROGLIGHT));

	//ordered by vanilla redstone tab order
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_REDSTONE_BLOCK = BLOCKS.register("reinforced_redstone_block", () -> new ReinforcedRedstoneBlock(prop(Material.METAL).sound(SoundType.METAL), Blocks.REDSTONE_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PISTON = BLOCKS.register("reinforced_piston", () -> new ReinforcedPistonBaseBlock(false, prop(Material.PISTON).isRedstoneConductor((s, w, p) -> false).isSuffocating((s, w, p) -> !s.getValue(PistonBaseBlock.EXTENDED)).isViewBlocking((s, w, p) -> !s.getValue(PistonBaseBlock.EXTENDED))));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STICKY_PISTON = BLOCKS.register("reinforced_sticky_piston", () -> new ReinforcedPistonBaseBlock(true, prop(Material.PISTON).isRedstoneConductor((s, w, p) -> false).isSuffocating((s, w, p) -> !s.getValue(PistonBaseBlock.EXTENDED)).isViewBlocking((s, w, p) -> !s.getValue(PistonBaseBlock.EXTENDED))));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_OBSERVER = BLOCKS.register("reinforced_observer", () -> new ReinforcedObserverBlock(prop()));
	@HasManualPage
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_HOPPER = BLOCKS.register("reinforced_hopper", () -> new ReinforcedHopperBlock(prop(Material.METAL, MaterialColor.STONE).sound(SoundType.METAL).noOcclusion()));
	@HasManualPage
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_LEVER = BLOCKS.register("reinforced_lever", () -> new ReinforcedLeverBlock(prop(Material.WOOD).noCollission().sound(SoundType.WOOD)));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_REDSTONE_LAMP = BLOCKS.register("reinforced_redstone_lamp", () -> new ReinforcedRedstoneLampBlock(prop(Material.BUILDABLE_GLASS).sound(SoundType.GLASS).lightLevel(state -> state.getValue(ReinforcedRedstoneLampBlock.LIT) ? 15 : 0), Blocks.REDSTONE_LAMP));
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STONE_BUTTON = BLOCKS.register("reinforced_stone_button", () -> new ReinforcedButtonBlock(false, prop(Material.STONE).noCollission(), Blocks.STONE_BUTTON));
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_BLACKSTONE_BUTTON = BLOCKS.register("reinforced_polished_blackstone_button", () -> new ReinforcedButtonBlock(false, prop(Material.STONE).noCollission(), Blocks.POLISHED_BLACKSTONE_BUTTON));
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_OAK_BUTTON = BLOCKS.register("reinforced_oak_button", () -> new ReinforcedButtonBlock(true, prop(Material.WOOD).noCollission().sound(SoundType.WOOD), Blocks.OAK_BUTTON));
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SPRUCE_BUTTON = BLOCKS.register("reinforced_spruce_button", () -> new ReinforcedButtonBlock(true, prop(Material.WOOD).noCollission().sound(SoundType.WOOD), Blocks.SPRUCE_BUTTON));
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BIRCH_BUTTON = BLOCKS.register("reinforced_birch_button", () -> new ReinforcedButtonBlock(true, prop(Material.WOOD).noCollission().sound(SoundType.WOOD), Blocks.BIRCH_BUTTON));
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_JUNGLE_BUTTON = BLOCKS.register("reinforced_jungle_button", () -> new ReinforcedButtonBlock(true, prop(Material.WOOD).noCollission().sound(SoundType.WOOD), Blocks.JUNGLE_BUTTON));
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_ACACIA_BUTTON = BLOCKS.register("reinforced_acacia_button", () -> new ReinforcedButtonBlock(true, prop(Material.WOOD).noCollission().sound(SoundType.WOOD), Blocks.ACACIA_BUTTON));
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DARK_OAK_BUTTON = BLOCKS.register("reinforced_dark_oak_button", () -> new ReinforcedButtonBlock(true, prop(Material.WOOD).noCollission().sound(SoundType.WOOD), Blocks.DARK_OAK_BUTTON));
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_MANGROVE_BUTTON = BLOCKS.register("reinforced_mangrove_button", () -> new ReinforcedButtonBlock(true, prop(Material.WOOD).noCollission().sound(SoundType.WOOD), Blocks.MANGROVE_BUTTON));
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CRIMSON_BUTTON = BLOCKS.register("reinforced_crimson_button", () -> new ReinforcedButtonBlock(true, prop(Material.WOOD).noCollission().sound(SoundType.WOOD), Blocks.CRIMSON_BUTTON));
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_WARPED_BUTTON = BLOCKS.register("reinforced_warped_button", () -> new ReinforcedButtonBlock(true, prop(Material.WOOD).noCollission().sound(SoundType.WOOD), Blocks.WARPED_BUTTON));
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STONE_PRESSURE_PLATE = BLOCKS.register("reinforced_stone_pressure_plate", () -> new ReinforcedPressurePlateBlock(Sensitivity.MOBS, prop().noCollission(), Blocks.STONE_PRESSURE_PLATE));
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_BLACKSTONE_PRESSURE_PLATE = BLOCKS.register("reinforced_polished_blackstone_pressure_plate", () -> new ReinforcedPressurePlateBlock(Sensitivity.MOBS, prop().noCollission(), Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE));
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_OAK_PRESSURE_PLATE = BLOCKS.register("reinforced_oak_pressure_plate", () -> new ReinforcedPressurePlateBlock(Sensitivity.EVERYTHING, prop(Material.WOOD).noCollission().sound(SoundType.WOOD), Blocks.OAK_PRESSURE_PLATE));
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SPRUCE_PRESSURE_PLATE = BLOCKS.register("reinforced_spruce_pressure_plate", () -> new ReinforcedPressurePlateBlock(Sensitivity.EVERYTHING, prop(Material.WOOD).noCollission().sound(SoundType.WOOD), Blocks.SPRUCE_PRESSURE_PLATE));
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BIRCH_PRESSURE_PLATE = BLOCKS.register("reinforced_birch_pressure_plate", () -> new ReinforcedPressurePlateBlock(Sensitivity.EVERYTHING, prop(Material.WOOD).noCollission().sound(SoundType.WOOD), Blocks.BIRCH_PRESSURE_PLATE));
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_JUNGLE_PRESSURE_PLATE = BLOCKS.register("reinforced_jungle_pressure_plate", () -> new ReinforcedPressurePlateBlock(Sensitivity.EVERYTHING, prop(Material.WOOD).noCollission().sound(SoundType.WOOD), Blocks.JUNGLE_PRESSURE_PLATE));
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_ACACIA_PRESSURE_PLATE = BLOCKS.register("reinforced_acacia_pressure_plate", () -> new ReinforcedPressurePlateBlock(Sensitivity.EVERYTHING, prop(Material.WOOD).noCollission().sound(SoundType.WOOD), Blocks.ACACIA_PRESSURE_PLATE));
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DARK_OAK_PRESSURE_PLATE = BLOCKS.register("reinforced_dark_oak_pressure_plate", () -> new ReinforcedPressurePlateBlock(Sensitivity.EVERYTHING, prop(Material.WOOD).noCollission().sound(SoundType.WOOD), Blocks.DARK_OAK_PRESSURE_PLATE));
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_MANGROVE_PRESSURE_PLATE = BLOCKS.register("reinforced_mangrove_pressure_plate", () -> new ReinforcedPressurePlateBlock(Sensitivity.EVERYTHING, prop(Material.WOOD).noCollission().sound(SoundType.WOOD), Blocks.MANGROVE_PRESSURE_PLATE));
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CRIMSON_PRESSURE_PLATE = BLOCKS.register("reinforced_crimson_pressure_plate", () -> new ReinforcedPressurePlateBlock(Sensitivity.EVERYTHING, prop(Material.WOOD).noCollission().sound(SoundType.WOOD), Blocks.CRIMSON_PRESSURE_PLATE));
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_WARPED_PRESSURE_PLATE = BLOCKS.register("reinforced_warped_pressure_plate", () -> new ReinforcedPressurePlateBlock(Sensitivity.EVERYTHING, prop(Material.WOOD).noCollission().sound(SoundType.WOOD), Blocks.WARPED_PRESSURE_PLATE));
	@HasManualPage(hasRecipeDescription = true)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<Block> REINFORCED_IRON_TRAPDOOR = BLOCKS.register("reinforced_iron_trapdoor", () -> new ReinforcedIronTrapDoorBlock(prop(Material.METAL).sound(SoundType.METAL).noOcclusion().isValidSpawn(SCContent::never)));

	//ordered by vanilla brewing tab order
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CAULDRON = BLOCKS.register("reinforced_cauldron", () -> new ReinforcedCauldronBlock(prop(Material.METAL, MaterialColor.STONE).noOcclusion(), IReinforcedCauldronInteraction.EMPTY));
	@Reinforced(registerBlockItem = false)
	public static final RegistryObject<Block> REINFORCED_WATER_CAULDRON = BLOCKS.register("reinforced_water_cauldron", () -> new ReinforcedLayeredCauldronBlock(prop(Material.METAL, MaterialColor.STONE).noOcclusion(), LayeredCauldronBlock.RAIN, IReinforcedCauldronInteraction.WATER, Blocks.WATER_CAULDRON));
	@Reinforced(registerBlockItem = false)
	public static final RegistryObject<Block> REINFORCED_LAVA_CAULDRON = BLOCKS.register("reinforced_lava_cauldron", () -> new ReinforcedLavaCauldronBlock(prop(Material.METAL, MaterialColor.STONE).noOcclusion().lightLevel(state -> 15)));
	@Reinforced(registerBlockItem = false)
	public static final RegistryObject<Block> REINFORCED_POWDER_SNOW_CAULDRON = BLOCKS.register("reinforced_powder_snow_cauldron", () -> new ReinforcedLayeredCauldronBlock(prop(Material.METAL, MaterialColor.STONE).noOcclusion(), LayeredCauldronBlock.SNOW, IReinforcedCauldronInteraction.POWDER_SNOW, Blocks.POWDER_SNOW_CAULDRON));

	//misc
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced(customTint = 0x15B3A2)
	public static final RegistryObject<Block> REINFORCED_CHISELED_CRYSTAL_QUARTZ = BLOCKS.register("reinforced_chiseled_crystal_quartz_block", () -> new BlockPocketBlock(prop(), SCContent.CHISELED_CRYSTAL_QUARTZ));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced(customTint = 0x15B3A2)
	public static final RegistryObject<Block> REINFORCED_CRYSTAL_QUARTZ = BLOCKS.register("reinforced_crystal_quartz_block", () -> new BlockPocketBlock(prop(), SCContent.CRYSTAL_QUARTZ));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced(customTint = 0x15B3A2)
	public static final RegistryObject<Block> REINFORCED_CRYSTAL_QUARTZ_PILLAR = BLOCKS.register("reinforced_crystal_quartz_pillar", () -> new ReinforcedRotatedCrystalQuartzPillar(prop(), SCContent.CRYSTAL_QUARTZ_PILLAR));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced(customTint = 0x15B3A2)
	public static final RegistryObject<Block> REINFORCED_CRYSTAL_QUARTZ_SLAB = BLOCKS.register("reinforced_crystal_quartz_slab", () -> new ReinforcedSlabBlock(prop(), SCContent.CRYSTAL_QUARTZ_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced(customTint = 0x15B3A2)
	public static final RegistryObject<Block> REINFORCED_CRYSTAL_QUARTZ_STAIRS = BLOCKS.register("reinforced_crystal_quartz_stairs", () -> new ReinforcedStairsBlock(prop(), SCContent.STAIRS_CRYSTAL_QUARTZ));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	public static final RegistryObject<Block> HORIZONTAL_REINFORCED_IRON_BARS = BLOCKS.register("horizontal_reinforced_iron_bars", () -> new HorizontalReinforcedIronBars(prop(Material.METAL).sound(SoundType.METAL), Blocks.IRON_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DIRT_PATH = BLOCKS.register("reinforced_grass_path", () -> new ReinforcedDirtPathBlock(prop(Material.DIRT).sound(SoundType.GRASS), Blocks.DIRT_PATH));
	public static final RegistryObject<Block> REINFORCED_MOVING_PISTON = BLOCKS.register("reinforced_moving_piston", () -> new ReinforcedMovingPistonBlock(prop(Material.PISTON).dynamicShape().noLootTable().noOcclusion().isRedstoneConductor((s, w, p) -> false).isSuffocating((s, w, p) -> false).isViewBlocking((s, w, p) -> false)));
	@Reinforced(registerBlockItem = false)
	public static final RegistryObject<Block> REINFORCED_PISTON_HEAD = BLOCKS.register("reinforced_piston_head", () -> new ReinforcedPistonHeadBlock(prop(Material.PISTON).noLootTable()));
	public static final RegistryObject<Block> SENTRY_DISGUISE = BLOCKS.register("sentry_disguise", () -> new SentryDisguiseBlock(propDisguisable(Material.AIR).strength(-1.0F, Float.MAX_VALUE).noLootTable()));

	//items
	@HasManualPage(hasRecipeDescription = true)
	public static final RegistryObject<Item> ADMIN_TOOL = ITEMS.register("admin_tool", () -> new AdminToolItem(itemProp(SecurityCraft.technicalTab).stacksTo(1).stacksTo(1)));
	public static final RegistryObject<Item> ANCIENT_DEBRIS_MINE_ITEM = ITEMS.register("ancient_debris_mine", () -> new BlockItem(SCContent.ANCIENT_DEBRIS_MINE.get(), itemProp(SecurityCraft.mineTab).fireResistant()));
	@HasManualPage
	public static final RegistryObject<Item> BRIEFCASE = ITEMS.register("briefcase", () -> new BriefcaseItem(itemProp(SecurityCraft.technicalTab).stacksTo(1)));
	@HasManualPage
	public static final RegistryObject<Item> CAMERA_MONITOR = ITEMS.register("camera_monitor", () -> new CameraMonitorItem(itemProp(SecurityCraft.technicalTab).stacksTo(1)));
	@HasManualPage
	public static final RegistryObject<Item> CODEBREAKER = ITEMS.register("codebreaker", () -> new CodebreakerItem(itemProp(SecurityCraft.technicalTab).stacksTo(1).defaultDurability(3)));
	@HasManualPage
	public static final RegistryObject<Item> CRYSTAL_QUARTZ_ITEM = ITEMS.register("crystal_quartz_item", () -> new Item(itemProp(SecurityCraft.decorationTab)));
	public static final RegistryObject<Item> DISPLAY_CASE_ITEM = ITEMS.register(DISPLAY_CASE_PATH, () -> new DisplayCaseItem(SCContent.DISPLAY_CASE.get(), itemProp(SecurityCraft.decorationTab), false));
	@HasManualPage(hasRecipeDescription = true)
	public static final RegistryObject<Item> FAKE_LAVA_BUCKET = ITEMS.register("bucket_f_lava", () -> new FakeLiquidBucketItem(SCContent.FAKE_LAVA, itemProp(SecurityCraft.technicalTab).stacksTo(1)));
	@HasManualPage(hasRecipeDescription = true)
	public static final RegistryObject<Item> FAKE_WATER_BUCKET = ITEMS.register("bucket_f_water", () -> new FakeLiquidBucketItem(SCContent.FAKE_WATER, itemProp(SecurityCraft.technicalTab).stacksTo(1)));
	public static final RegistryObject<Item> GLOW_DISPLAY_CASE_ITEM = ITEMS.register(GLOW_DISPLAY_CASE_PATH, () -> new DisplayCaseItem(SCContent.GLOW_DISPLAY_CASE.get(), itemProp(SecurityCraft.decorationTab), true));
	@HasManualPage(PageGroup.KEYCARDS)
	public static final RegistryObject<Item> KEYCARD_LVL_1 = ITEMS.register("keycard_lv1", () -> new KeycardItem(itemProp(SecurityCraft.technicalTab), 0));
	@HasManualPage(PageGroup.KEYCARDS)
	public static final RegistryObject<Item> KEYCARD_LVL_2 = ITEMS.register("keycard_lv2", () -> new KeycardItem(itemProp(SecurityCraft.technicalTab), 1));
	@HasManualPage(PageGroup.KEYCARDS)
	public static final RegistryObject<Item> KEYCARD_LVL_3 = ITEMS.register("keycard_lv3", () -> new KeycardItem(itemProp(SecurityCraft.technicalTab), 2));
	@HasManualPage(PageGroup.KEYCARDS)
	public static final RegistryObject<Item> KEYCARD_LVL_4 = ITEMS.register("keycard_lv4", () -> new KeycardItem(itemProp(SecurityCraft.technicalTab), 3));
	@HasManualPage(PageGroup.KEYCARDS)
	public static final RegistryObject<Item> KEYCARD_LVL_5 = ITEMS.register("keycard_lv5", () -> new KeycardItem(itemProp(SecurityCraft.technicalTab), 4));
	@HasManualPage
	public static final RegistryObject<Item> KEY_PANEL = ITEMS.register("keypad_item", () -> new KeyPanelItem(itemProp(SecurityCraft.technicalTab)));
	public static final RegistryObject<Item> KEYPAD_CHEST_ITEM = ITEMS.register(KEYPAD_CHEST_PATH, () -> new KeypadChestItem(SCContent.KEYPAD_CHEST.get(), itemProp(SecurityCraft.technicalTab)));
	@HasManualPage
	public static final RegistryObject<Item> KEYPAD_DOOR_ITEM = ITEMS.register("keypad_door_item", () -> new DoubleHighBlockItem(KEYPAD_DOOR.get(), itemProp(SecurityCraft.decorationTab)));
	@HasManualPage
	public static final RegistryObject<Item> LIMITED_USE_KEYCARD = ITEMS.register("limited_use_keycard", () -> new KeycardItem(itemProp(SecurityCraft.technicalTab), -1));
	@HasManualPage
	public static final RegistryObject<Item> PORTABLE_TUNE_PLAYER = ITEMS.register("portable_tune_player", () -> new PortableTunePlayerItem(itemProp(SecurityCraft.technicalTab)));
	@HasManualPage
	public static final RegistryObject<Item> REINFORCED_DOOR_ITEM = ITEMS.register("door_indestructible_iron_item", () -> new DoubleHighBlockItem(REINFORCED_DOOR.get(), itemProp(SecurityCraft.decorationTab)));
	@HasManualPage
	public static final RegistryObject<Item> REMOTE_ACCESS_MINE = ITEMS.register("remote_access_mine", () -> new MineRemoteAccessToolItem(itemProp(SecurityCraft.technicalTab).stacksTo(1)));
	@HasManualPage
	public static final RegistryObject<Item> REMOTE_ACCESS_SENTRY = ITEMS.register("remote_access_sentry", () -> new SentryRemoteAccessToolItem(itemProp(SecurityCraft.technicalTab).stacksTo(1)));
	@HasManualPage
	public static final RegistryObject<Item> RIFT_STABILIZER_ITEM = ITEMS.register("rift_stabilizer", () -> new DoubleHighBlockItem(RIFT_STABILIZER.get(), itemProp(SecurityCraft.technicalTab)));
	@HasManualPage
	public static final RegistryObject<Item> SCANNER_DOOR_ITEM = ITEMS.register("scanner_door_item", () -> new DoubleHighBlockItem(SCANNER_DOOR.get(), itemProp(SecurityCraft.decorationTab)));
	@HasManualPage
	public static final RegistryObject<Item> SC_MANUAL = ITEMS.register("sc_manual", () -> new SCManualItem(itemProp(SecurityCraft.technicalTab).stacksTo(1)));
	@HasManualPage(PageGroup.SECRET_SIGNS)
	public static final RegistryObject<Item> SECRET_OAK_SIGN_ITEM = ITEMS.register("secret_sign_item", () -> new SecretSignItem(itemProp(SecurityCraft.decorationTab).stacksTo(16), SCContent.SECRET_OAK_SIGN.get(), SCContent.SECRET_OAK_WALL_SIGN.get(), "item.securitycraft.secret_sign_item"));
	@HasManualPage(PageGroup.SECRET_SIGNS)
	public static final RegistryObject<Item> SECRET_SPRUCE_SIGN_ITEM = ITEMS.register("secret_spruce_sign_item", () -> new SecretSignItem(itemProp(SecurityCraft.decorationTab).stacksTo(16), SCContent.SECRET_SPRUCE_SIGN.get(), SCContent.SECRET_SPRUCE_WALL_SIGN.get(), "item.securitycraft.secret_spruce_sign_item"));
	@HasManualPage(PageGroup.SECRET_SIGNS)
	public static final RegistryObject<Item> SECRET_BIRCH_SIGN_ITEM = ITEMS.register("secret_birch_sign_item", () -> new SecretSignItem(itemProp(SecurityCraft.decorationTab).stacksTo(16), SCContent.SECRET_BIRCH_SIGN.get(), SCContent.SECRET_BIRCH_WALL_SIGN.get(), "item.securitycraft.secret_birch_sign_item"));
	@HasManualPage(PageGroup.SECRET_SIGNS)
	public static final RegistryObject<Item> SECRET_JUNGLE_SIGN_ITEM = ITEMS.register("secret_jungle_sign_item", () -> new SecretSignItem(itemProp(SecurityCraft.decorationTab).stacksTo(16), SCContent.SECRET_JUNGLE_SIGN.get(), SCContent.SECRET_JUNGLE_WALL_SIGN.get(), "item.securitycraft.secret_jungle_sign_item"));
	@HasManualPage(PageGroup.SECRET_SIGNS)
	public static final RegistryObject<Item> SECRET_ACACIA_SIGN_ITEM = ITEMS.register("secret_acacia_sign_item", () -> new SecretSignItem(itemProp(SecurityCraft.decorationTab).stacksTo(16), SCContent.SECRET_ACACIA_SIGN.get(), SCContent.SECRET_ACACIA_WALL_SIGN.get(), "item.securitycraft.secret_acacia_sign_item"));
	@HasManualPage(PageGroup.SECRET_SIGNS)
	public static final RegistryObject<Item> SECRET_DARK_OAK_SIGN_ITEM = ITEMS.register("secret_dark_oak_sign_item", () -> new SecretSignItem(itemProp(SecurityCraft.decorationTab).stacksTo(16), SCContent.SECRET_DARK_OAK_SIGN.get(), SCContent.SECRET_DARK_OAK_WALL_SIGN.get(), "item.securitycraft.secret_dark_oak_sign_item"));
	@HasManualPage(PageGroup.SECRET_SIGNS)
	public static final RegistryObject<Item> SECRET_MANGROVE_SIGN_ITEM = ITEMS.register("secret_mangrove_sign_item", () -> new SecretSignItem(itemProp(SecurityCraft.decorationTab).stacksTo(16), SCContent.SECRET_MANGROVE_SIGN.get(), SCContent.SECRET_MANGROVE_WALL_SIGN.get(), "item.securitycraft.secret_mangrove_sign_item"));
	@HasManualPage(PageGroup.SECRET_SIGNS)
	public static final RegistryObject<Item> SECRET_CRIMSON_SIGN_ITEM = ITEMS.register("secret_crimson_sign_item", () -> new SecretSignItem(itemProp(SecurityCraft.decorationTab).stacksTo(16), SCContent.SECRET_CRIMSON_SIGN.get(), SCContent.SECRET_CRIMSON_WALL_SIGN.get(), "item.securitycraft.secret_crimson_sign_item"));
	@HasManualPage(PageGroup.SECRET_SIGNS)
	public static final RegistryObject<Item> SECRET_WARPED_SIGN_ITEM = ITEMS.register("secret_warped_sign_item", () -> new SecretSignItem(itemProp(SecurityCraft.decorationTab).stacksTo(16), SCContent.SECRET_WARPED_SIGN.get(), SCContent.SECRET_WARPED_WALL_SIGN.get(), "item.securitycraft.secret_warped_sign_item"));
	@HasManualPage(designedBy = "Henzoid")
	public static final RegistryObject<Item> SENTRY = ITEMS.register("sentry", () -> new SentryItem(itemProp(SecurityCraft.technicalTab)));
	public static final RegistryObject<Item> SONIC_SECURITY_SYSTEM_ITEM = ITEMS.register("sonic_security_system", () -> new SonicSecuritySystemItem(itemProp(SecurityCraft.technicalTab).stacksTo(1)));
	@HasManualPage
	public static final RegistryObject<Item> TASER = ITEMS.register("taser", () -> new TaserItem(itemProp(SecurityCraft.technicalTab).defaultDurability(151), false));
	public static final RegistryObject<Item> TASER_POWERED = ITEMS.register("taser_powered", () -> new TaserItem(itemProp(null).defaultDurability(151), true));
	@HasManualPage
	public static final RegistryObject<Item> UNIVERSAL_BLOCK_MODIFIER = ITEMS.register("universal_block_modifier", () -> new UniversalBlockModifierItem(itemProp(SecurityCraft.technicalTab).stacksTo(1)));
	@HasManualPage(PageGroup.BLOCK_REINFORCERS)
	public static final RegistryObject<Item> UNIVERSAL_BLOCK_REINFORCER_LVL_1 = ITEMS.register("universal_block_reinforcer_lvl1", () -> new UniversalBlockReinforcerItem(itemProp(SecurityCraft.technicalTab).stacksTo(1).defaultDurability(300)));
	@HasManualPage(PageGroup.BLOCK_REINFORCERS)
	public static final RegistryObject<Item> UNIVERSAL_BLOCK_REINFORCER_LVL_2 = ITEMS.register("universal_block_reinforcer_lvl2", () -> new UniversalBlockReinforcerItem(itemProp(SecurityCraft.technicalTab).stacksTo(1).defaultDurability(2700)));
	@HasManualPage(PageGroup.BLOCK_REINFORCERS)
	public static final RegistryObject<Item> UNIVERSAL_BLOCK_REINFORCER_LVL_3 = ITEMS.register("universal_block_reinforcer_lvl3", () -> new UniversalBlockReinforcerItem(itemProp(SecurityCraft.technicalTab).stacksTo(1)));
	@HasManualPage
	public static final RegistryObject<Item> UNIVERSAL_BLOCK_REMOVER = ITEMS.register("universal_block_remover", () -> new UniversalBlockRemoverItem(itemProp(SecurityCraft.technicalTab).stacksTo(1).defaultDurability(476)));
	@HasManualPage
	public static final RegistryObject<Item> UNIVERSAL_KEY_CHANGER = ITEMS.register("universal_key_changer", () -> new UniversalKeyChangerItem(itemProp(SecurityCraft.technicalTab).stacksTo(1)));
	@HasManualPage
	public static final RegistryObject<Item> UNIVERSAL_OWNER_CHANGER = ITEMS.register("universal_owner_changer", () -> new UniversalOwnerChangerItem(itemProp(SecurityCraft.technicalTab).stacksTo(1).defaultDurability(48)));
	@HasManualPage
	public static final RegistryObject<Item> WIRE_CUTTERS = ITEMS.register("wire_cutters", () -> new Item(itemProp(SecurityCraft.technicalTab).stacksTo(1).defaultDurability(476)));

	//modules
	@HasManualPage
	public static final RegistryObject<ModuleItem> DENYLIST_MODULE = ITEMS.register("blacklist_module", () -> new ModuleItem(itemProp(SecurityCraft.technicalTab).stacksTo(1), ModuleType.DENYLIST, true, true));
	@HasManualPage
	public static final RegistryObject<ModuleItem> DISGUISE_MODULE = ITEMS.register("disguise_module", () -> new ModuleItem(itemProp(SecurityCraft.technicalTab).stacksTo(1), ModuleType.DISGUISE, false, true));
	@HasManualPage
	public static final RegistryObject<ModuleItem> HARMING_MODULE = ITEMS.register("harming_module", () -> new ModuleItem(itemProp(SecurityCraft.technicalTab).stacksTo(1), ModuleType.HARMING, false));
	@HasManualPage
	public static final RegistryObject<ModuleItem> REDSTONE_MODULE = ITEMS.register("redstone_module", () -> new ModuleItem(itemProp(SecurityCraft.technicalTab).stacksTo(1), ModuleType.REDSTONE, false));
	@HasManualPage
	public static final RegistryObject<ModuleItem> SMART_MODULE = ITEMS.register("smart_module", () -> new ModuleItem(itemProp(SecurityCraft.technicalTab).stacksTo(1), ModuleType.SMART, false));
	@HasManualPage
	public static final RegistryObject<ModuleItem> STORAGE_MODULE = ITEMS.register("storage_module", () -> new ModuleItem(itemProp(SecurityCraft.technicalTab).stacksTo(1), ModuleType.STORAGE, false));
	@HasManualPage
	public static final RegistryObject<ModuleItem> ALLOWLIST_MODULE = ITEMS.register("whitelist_module", () -> new ModuleItem(itemProp(SecurityCraft.technicalTab).stacksTo(1), ModuleType.ALLOWLIST, true, true));
	@HasManualPage
	public static final RegistryObject<ModuleItem> SPEED_MODULE = ITEMS.register("speed_module", () -> new ModuleItem(itemProp(SecurityCraft.technicalTab).stacksTo(1), ModuleType.SPEED, false));

	//block entity types
	//@formatter:off
	public static final RegistryObject<BlockEntityType<OwnableBlockEntity>> OWNABLE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("ownable", () -> {
		List<Block> beOwnableBlocks = Arrays.stream(SCContent.class.getFields())
				.filter(field -> field.isAnnotationPresent(OwnableBE.class))
				.map(field -> {
					try {
						return ((RegistryObject<Block>) field.get(null)).get();
					}
					catch(IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
						return null;
					}
				})
				.filter(Predicates.notNull())
				.toList();

		return BlockEntityType.Builder.of(OwnableBlockEntity::new, beOwnableBlocks.toArray(new Block[beOwnableBlocks.size()])).build(null);
	});
	public static final RegistryObject<BlockEntityType<NamedBlockEntity>> ABSTRACT_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("abstract", () -> BlockEntityType.Builder.of(NamedBlockEntity::new,
			SCContent.LASER_FIELD.get(),
			SCContent.INVENTORY_SCANNER_FIELD.get(),
			SCContent.IRON_FENCE.get(),
			SCContent.COBBLESTONE_MINE.get(),
			SCContent.DIAMOND_ORE_MINE.get(),
			SCContent.DIRT_MINE.get(),
			SCContent.GRAVEL_MINE.get(),
			SCContent.SAND_MINE.get(),
			SCContent.STONE_MINE.get(),
			SCContent.BOUNCING_BETTY.get(),
			SCContent.REINFORCED_FENCEGATE.get(),
			SCContent.ANCIENT_DEBRIS_MINE.get(),
			SCContent.COAL_ORE_MINE.get(),
			SCContent.EMERALD_ORE_MINE.get(),
			SCContent.GOLD_ORE_MINE.get(),
			SCContent.GILDED_BLACKSTONE_MINE.get(),
			SCContent.IRON_ORE_MINE.get(),
			SCContent.LAPIS_ORE_MINE.get(),
			SCContent.NETHER_GOLD_ORE_MINE.get(),
			SCContent.QUARTZ_ORE_MINE.get(),
			SCContent.REDSTONE_ORE_MINE.get(),
			SCContent.DEEPSLATE_COAL_ORE_MINE.get(),
			SCContent.DEEPSLATE_COPPER_ORE_MINE.get(),
			SCContent.DEEPSLATE_DIAMOND_ORE_MINE.get(),
			SCContent.DEEPSLATE_EMERALD_ORE_MINE.get(),
			SCContent.DEEPSLATE_GOLD_ORE_MINE.get(),
			SCContent.DEEPSLATE_IRON_ORE_MINE.get(),
			SCContent.DEEPSLATE_LAPIS_ORE_MINE.get(),
			SCContent.DEEPSLATE_REDSTONE_ORE_MINE.get(),
			SCContent.COPPER_ORE_MINE.get()).build(null));
	public static final RegistryObject<BlockEntityType<KeypadBlockEntity>> KEYPAD_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("keypad", () -> BlockEntityType.Builder.of(KeypadBlockEntity::new, SCContent.KEYPAD.get()).build(null));
	public static final RegistryObject<BlockEntityType<LaserBlockBlockEntity>> LASER_BLOCK_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("laser_block", () -> BlockEntityType.Builder.of(LaserBlockBlockEntity::new, SCContent.LASER_BLOCK.get()).build(null));
	public static final RegistryObject<BlockEntityType<CageTrapBlockEntity>> CAGE_TRAP_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("cage_trap", () -> BlockEntityType.Builder.of(CageTrapBlockEntity::new, SCContent.CAGE_TRAP.get()).build(null));
	public static final RegistryObject<BlockEntityType<KeycardReaderBlockEntity>> KEYCARD_READER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("keycard_reader", () -> BlockEntityType.Builder.of(KeycardReaderBlockEntity::new, SCContent.KEYCARD_READER.get()).build(null));
	public static final RegistryObject<BlockEntityType<InventoryScannerBlockEntity>> INVENTORY_SCANNER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("inventory_scanner", () -> BlockEntityType.Builder.of(InventoryScannerBlockEntity::new, SCContent.INVENTORY_SCANNER.get()).build(null));
	public static final RegistryObject<BlockEntityType<PortableRadarBlockEntity>> PORTABLE_RADAR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("portable_radar", () -> BlockEntityType.Builder.of(PortableRadarBlockEntity::new, SCContent.PORTABLE_RADAR.get()).build(null));
	public static final RegistryObject<BlockEntityType<SecurityCameraBlockEntity>> SECURITY_CAMERA_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("security_camera", () -> BlockEntityType.Builder.of(SecurityCameraBlockEntity::new, SCContent.SECURITY_CAMERA.get()).build(null));
	public static final RegistryObject<BlockEntityType<UsernameLoggerBlockEntity>> USERNAME_LOGGER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("username_logger", () -> BlockEntityType.Builder.of(UsernameLoggerBlockEntity::new, SCContent.USERNAME_LOGGER.get()).build(null));
	public static final RegistryObject<BlockEntityType<RetinalScannerBlockEntity>> RETINAL_SCANNER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("retinal_scanner", () -> BlockEntityType.Builder.of(RetinalScannerBlockEntity::new, SCContent.RETINAL_SCANNER.get()).build(null));
	public static final RegistryObject<BlockEntityType<? extends ChestBlockEntity>> KEYPAD_CHEST_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("keypad_chest", () -> BlockEntityType.Builder.of(KeypadChestBlockEntity::new, SCContent.KEYPAD_CHEST.get()).build(null));
	public static final RegistryObject<BlockEntityType<AlarmBlockEntity>> ALARM_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("alarm", () -> BlockEntityType.Builder.of(AlarmBlockEntity::new, SCContent.ALARM.get()).build(null));
	public static final RegistryObject<BlockEntityType<ClaymoreBlockEntity>> CLAYMORE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("claymore", () -> BlockEntityType.Builder.of(ClaymoreBlockEntity::new, SCContent.CLAYMORE.get()).build(null));
	public static final RegistryObject<BlockEntityType<KeypadFurnaceBlockEntity>> KEYPAD_FURNACE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("keypad_furnace", () -> BlockEntityType.Builder.of(KeypadFurnaceBlockEntity::new, SCContent.KEYPAD_FURNACE.get()).build(null));
	public static final RegistryObject<BlockEntityType<KeypadSmokerBlockEntity>> KEYPAD_SMOKER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("keypad_smoker", () -> BlockEntityType.Builder.of(KeypadSmokerBlockEntity::new, SCContent.KEYPAD_SMOKER.get()).build(null));
	public static final RegistryObject<BlockEntityType<KeypadBlastFurnaceBlockEntity>> KEYPAD_BLAST_FURNACE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("keypad_blast_furnace", () -> BlockEntityType.Builder.of(KeypadBlastFurnaceBlockEntity::new, SCContent.KEYPAD_BLAST_FURNACE.get()).build(null));
	public static final RegistryObject<BlockEntityType<IMSBlockEntity>> IMS_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("ims", () -> BlockEntityType.Builder.of(IMSBlockEntity::new, SCContent.IMS.get()).build(null));
	public static final RegistryObject<BlockEntityType<ProtectoBlockEntity>> PROTECTO_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("protecto", () -> BlockEntityType.Builder.of(ProtectoBlockEntity::new, SCContent.PROTECTO.get()).build(null));
	public static final RegistryObject<BlockEntityType<ScannerDoorBlockEntity>> SCANNER_DOOR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("scanner_door", () -> BlockEntityType.Builder.of(ScannerDoorBlockEntity::new, SCContent.SCANNER_DOOR.get()).build(null));
	public static final RegistryObject<BlockEntityType<SecretSignBlockEntity>> SECRET_SIGN_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("secret_sign", () -> BlockEntityType.Builder.of(SecretSignBlockEntity::new,
			SCContent.SECRET_OAK_SIGN.get(),
			SCContent.SECRET_OAK_WALL_SIGN.get(),
			SCContent.SECRET_SPRUCE_SIGN.get(),
			SCContent.SECRET_SPRUCE_WALL_SIGN.get(),
			SCContent.SECRET_BIRCH_SIGN.get(),
			SCContent.SECRET_BIRCH_WALL_SIGN.get(),
			SCContent.SECRET_JUNGLE_SIGN.get(),
			SCContent.SECRET_JUNGLE_WALL_SIGN.get(),
			SCContent.SECRET_ACACIA_SIGN.get(),
			SCContent.SECRET_ACACIA_WALL_SIGN.get(),
			SCContent.SECRET_DARK_OAK_SIGN.get(),
			SCContent.SECRET_DARK_OAK_WALL_SIGN.get(),
			SCContent.SECRET_MANGROVE_SIGN.get(),
			SCContent.SECRET_MANGROVE_WALL_SIGN.get(),
			SCContent.SECRET_CRIMSON_SIGN.get(),
			SCContent.SECRET_CRIMSON_WALL_SIGN.get(),
			SCContent.SECRET_WARPED_SIGN.get(),
			SCContent.SECRET_WARPED_WALL_SIGN.get()).build(null));
	public static final RegistryObject<BlockEntityType<MotionActivatedLightBlockEntity>> MOTION_LIGHT_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("motion_light", () -> BlockEntityType.Builder.of(MotionActivatedLightBlockEntity::new, SCContent.MOTION_ACTIVATED_LIGHT.get()).build(null));
	public static final RegistryObject<BlockEntityType<TrackMineBlockEntity>> TRACK_MINE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("track_mine", () -> BlockEntityType.Builder.of(TrackMineBlockEntity::new, SCContent.TRACK_MINE.get()).build(null));
	public static final RegistryObject<BlockEntityType<TrophySystemBlockEntity>> TROPHY_SYSTEM_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("trophy_system", () -> BlockEntityType.Builder.of(TrophySystemBlockEntity::new, SCContent.TROPHY_SYSTEM.get()).build(null));
	public static final RegistryObject<BlockEntityType<BlockPocketManagerBlockEntity>> BLOCK_POCKET_MANAGER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("block_pocket_manager", () -> BlockEntityType.Builder.of(BlockPocketManagerBlockEntity::new, SCContent.BLOCK_POCKET_MANAGER.get()).build(null));
	public static final RegistryObject<BlockEntityType<BlockPocketBlockEntity>> BLOCK_POCKET_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("block_pocket", () -> BlockEntityType.Builder.of(BlockPocketBlockEntity::new,
			SCContent.BLOCK_POCKET_WALL.get(),
			SCContent.REINFORCED_CRYSTAL_QUARTZ.get(),
			SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get(),
			SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get()).build(null));
	public static final RegistryObject<BlockEntityType<AllowlistOnlyBlockEntity>> ALLOWLIST_ONLY_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("reinforced_pressure_plate", () -> BlockEntityType.Builder.of(AllowlistOnlyBlockEntity::new,
			SCContent.REINFORCED_STONE_PRESSURE_PLATE.get(),
			SCContent.REINFORCED_ACACIA_PRESSURE_PLATE.get(),
			SCContent.REINFORCED_BIRCH_PRESSURE_PLATE.get(),
			SCContent.REINFORCED_CRIMSON_PRESSURE_PLATE.get(),
			SCContent.REINFORCED_DARK_OAK_PRESSURE_PLATE.get(),
			SCContent.REINFORCED_JUNGLE_PRESSURE_PLATE.get(),
			SCContent.REINFORCED_MANGROVE_PRESSURE_PLATE.get(),
			SCContent.REINFORCED_OAK_PRESSURE_PLATE.get(),
			SCContent.REINFORCED_SPRUCE_PRESSURE_PLATE.get(),
			SCContent.REINFORCED_WARPED_PRESSURE_PLATE.get(),
			SCContent.REINFORCED_POLISHED_BLACKSTONE_PRESSURE_PLATE.get(),
			SCContent.REINFORCED_STONE_BUTTON.get(),
			SCContent.REINFORCED_ACACIA_BUTTON.get(),
			SCContent.REINFORCED_BIRCH_BUTTON.get(),
			SCContent.REINFORCED_CRIMSON_BUTTON.get(),
			SCContent.REINFORCED_DARK_OAK_BUTTON.get(),
			SCContent.REINFORCED_JUNGLE_BUTTON.get(),
			SCContent.REINFORCED_MANGROVE_BUTTON.get(),
			SCContent.REINFORCED_OAK_BUTTON.get(),
			SCContent.REINFORCED_SPRUCE_BUTTON.get(),
			SCContent.REINFORCED_WARPED_BUTTON.get(),
			SCContent.REINFORCED_POLISHED_BLACKSTONE_BUTTON.get(),
			SCContent.REINFORCED_LEVER.get()).build(null));
	public static final RegistryObject<BlockEntityType<ReinforcedHopperBlockEntity>> REINFORCED_HOPPER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("reinforced_hopper", () -> BlockEntityType.Builder.of(ReinforcedHopperBlockEntity::new, SCContent.REINFORCED_HOPPER.get()).build(null));
	public static final RegistryObject<BlockEntityType<ProjectorBlockEntity>> PROJECTOR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("projector", () -> BlockEntityType.Builder.of(ProjectorBlockEntity::new, SCContent.PROJECTOR.get()).build(null));
	public static final RegistryObject<BlockEntityType<KeypadDoorBlockEntity>> KEYPAD_DOOR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("keypad_door", () -> BlockEntityType.Builder.of(KeypadDoorBlockEntity::new, SCContent.KEYPAD_DOOR.get()).build(null));
	public static final RegistryObject<BlockEntityType<ReinforcedIronBarsBlockEntity>> REINFORCED_IRON_BARS_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("reinforced_iron_bars", () -> BlockEntityType.Builder.of(ReinforcedIronBarsBlockEntity::new, SCContent.REINFORCED_IRON_BARS.get()).build(null));
	public static final RegistryObject<BlockEntityType<ReinforcedCauldronBlockEntity>> REINFORCED_CAULDRON_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("reinforced_cauldron", () -> BlockEntityType.Builder.of(ReinforcedCauldronBlockEntity::new,
			SCContent.REINFORCED_CAULDRON.get(),
			SCContent.REINFORCED_WATER_CAULDRON.get(),
			SCContent.REINFORCED_LAVA_CAULDRON.get(),
			SCContent.REINFORCED_POWDER_SNOW_CAULDRON.get()).build(null));
	public static final RegistryObject<BlockEntityType<ReinforcedPistonMovingBlockEntity>> REINFORCED_PISTON_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("reinforced_piston", () -> BlockEntityType.Builder.of(ReinforcedPistonMovingBlockEntity::new, SCContent.REINFORCED_MOVING_PISTON.get()).build(null));
	public static final RegistryObject<BlockEntityType<ValidationOwnableBlockEntity>> VALIDATION_OWNABLE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("validation_ownable", () -> BlockEntityType.Builder.of(ValidationOwnableBlockEntity::new,
			SCContent.REINFORCED_PISTON.get(),
			SCContent.REINFORCED_STICKY_PISTON.get()).build(null));
	public static final RegistryObject<BlockEntityType<KeyPanelBlockEntity>> KEY_PANEL_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("key_panel", () -> BlockEntityType.Builder.of(KeyPanelBlockEntity::new, SCContent.KEY_PANEL_BLOCK.get()).build(null));
	public static final RegistryObject<BlockEntityType<SonicSecuritySystemBlockEntity>> SONIC_SECURITY_SYSTEM_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("sonic_security_system", () -> BlockEntityType.Builder.of(SonicSecuritySystemBlockEntity::new, SCContent.SONIC_SECURITY_SYSTEM.get()).build(null));
	public static final RegistryObject<BlockEntityType<BlockChangeDetectorBlockEntity>> BLOCK_CHANGE_DETECTOR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("block_change_detector", () -> BlockEntityType.Builder.of(BlockChangeDetectorBlockEntity::new, SCContent.BLOCK_CHANGE_DETECTOR.get()).build(null));
	public static final RegistryObject<BlockEntityType<RiftStabilizerBlockEntity>> RIFT_STABILIZER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("rift_stabilizer", () -> BlockEntityType.Builder.of(RiftStabilizerBlockEntity::new, SCContent.RIFT_STABILIZER.get()).build(null));
	public static final RegistryObject<BlockEntityType<DisguisableBlockEntity>> DISGUISABLE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("disguisable", () -> BlockEntityType.Builder.of(DisguisableBlockEntity::new, SCContent.SENTRY_DISGUISE.get()).build(null));
	public static final RegistryObject<BlockEntityType<DisplayCaseBlockEntity>> DISPLAY_CASE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("display_case", () -> BlockEntityType.Builder.of(DisplayCaseBlockEntity::new, SCContent.DISPLAY_CASE.get()).build(null));
	public static final RegistryObject<BlockEntityType<GlowDisplayCaseBlockEntity>> GLOW_DISPLAY_CASE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("glow_display_case", () -> BlockEntityType.Builder.of(GlowDisplayCaseBlockEntity::new, SCContent.GLOW_DISPLAY_CASE.get()).build(null));

	//entity types
	public static final RegistryObject<EntityType<BouncingBetty>> BOUNCING_BETTY_ENTITY = ENTITY_TYPES.register("bouncingbetty",
			() -> EntityType.Builder.<BouncingBetty>of(BouncingBetty::new, MobCategory.MISC)
			.sized(0.5F, 0.2F)
			.setTrackingRange(128)
			.setUpdateInterval(1)
			.setShouldReceiveVelocityUpdates(true)
			.build(SecurityCraft.MODID + ":bouncingbetty"));
	public static final RegistryObject<EntityType<IMSBomb>> IMS_BOMB_ENTITY = ENTITY_TYPES.register("imsbomb",
			() -> EntityType.Builder.<IMSBomb>of(IMSBomb::new, MobCategory.MISC)
			.sized(0.25F, 0.3F)
			.setTrackingRange(256)
			.setUpdateInterval(1)
			.setShouldReceiveVelocityUpdates(true)
			.build(SecurityCraft.MODID + ":imsbomb"));
	public static final RegistryObject<EntityType<SecurityCamera>> SECURITY_CAMERA_ENTITY = ENTITY_TYPES.register("securitycamera",
			() -> EntityType.Builder.<SecurityCamera>of(SecurityCamera::new, MobCategory.MISC)
			.sized(0.0001F, 0.0001F)
			.setTrackingRange(256)
			.setUpdateInterval(20)
			.setShouldReceiveVelocityUpdates(true)
			.build(SecurityCraft.MODID + ":securitycamera"));
	public static final RegistryObject<EntityType<Sentry>> SENTRY_ENTITY = ENTITY_TYPES.register("sentry",
			() -> EntityType.Builder.<Sentry>of(Sentry::new, MobCategory.MISC)
			.sized(1.0F, 2.0F)
			.setTrackingRange(256)
			.setUpdateInterval(1)
			.setShouldReceiveVelocityUpdates(true)
			.build(SecurityCraft.MODID + ":sentry"));
	public static final RegistryObject<EntityType<Bullet>> BULLET_ENTITY = ENTITY_TYPES.register("bullet",
			() -> EntityType.Builder.<Bullet>of(Bullet::new, MobCategory.MISC)
			.sized(0.15F, 0.1F)
			.setTrackingRange(256)
			.setUpdateInterval(1)
			.setShouldReceiveVelocityUpdates(true)
			.build(SecurityCraft.MODID + ":bullet"));
	//@formatter:on

	//container types
	public static final RegistryObject<MenuType<BlockReinforcerMenu>> BLOCK_REINFORCER_MENU = MENU_TYPES.register("block_reinforcer", () -> IForgeMenuType.create((windowId, inv, data) -> new BlockReinforcerMenu(windowId, inv, data.readBoolean())));
	public static final RegistryObject<MenuType<BriefcaseMenu>> BRIEFCASE_INVENTORY_MENU = MENU_TYPES.register("briefcase_inventory", () -> IForgeMenuType.create((windowId, inv, data) -> new BriefcaseMenu(windowId, inv, new BriefcaseContainer(PlayerUtils.getSelectedItemStack(inv, SCContent.BRIEFCASE.get())))));
	public static final RegistryObject<MenuType<CustomizeBlockMenu>> CUSTOMIZE_BLOCK_MENU = MENU_TYPES.register("customize_block", () -> IForgeMenuType.create((windowId, inv, data) -> new CustomizeBlockMenu(windowId, inv.player.level, data.readBlockPos(), inv)));
	public static final RegistryObject<MenuType<DisguiseModuleMenu>> DISGUISE_MODULE_MENU = MENU_TYPES.register("disguise_module", () -> IForgeMenuType.create((windowId, inv, data) -> new DisguiseModuleMenu(windowId, inv, new ModuleItemContainer(PlayerUtils.getSelectedItemStack(inv, SCContent.DISGUISE_MODULE.get())))));
	public static final RegistryObject<MenuType<InventoryScannerMenu>> INVENTORY_SCANNER_MENU = MENU_TYPES.register("inventory_scanner", () -> IForgeMenuType.create((windowId, inv, data) -> new InventoryScannerMenu(windowId, inv.player.level, data.readBlockPos(), inv)));
	public static final RegistryObject<MenuType<KeypadFurnaceMenu>> KEYPAD_FURNACE_MENU = MENU_TYPES.register("keypad_furnace", () -> IForgeMenuType.create((windowId, inv, data) -> new KeypadFurnaceMenu(windowId, inv.player.level, data.readBlockPos(), inv)));
	public static final RegistryObject<MenuType<KeypadSmokerMenu>> KEYPAD_SMOKER_MENU = MENU_TYPES.register("keypad_smoker", () -> IForgeMenuType.create((windowId, inv, data) -> new KeypadSmokerMenu(windowId, inv.player.level, data.readBlockPos(), inv)));
	public static final RegistryObject<MenuType<KeypadBlastFurnaceMenu>> KEYPAD_BLAST_FURNACE_MENU = MENU_TYPES.register("keypad_blast_furnace", () -> IForgeMenuType.create((windowId, inv, data) -> new KeypadBlastFurnaceMenu(windowId, inv.player.level, data.readBlockPos(), inv)));
	public static final RegistryObject<MenuType<ProjectorMenu>> PROJECTOR_MENU = MENU_TYPES.register("projector", () -> IForgeMenuType.create((windowId, inv, data) -> new ProjectorMenu(windowId, inv.player.level, data.readBlockPos(), inv)));
	public static final RegistryObject<MenuType<KeycardReaderMenu>> KEYCARD_READER_MENU = MENU_TYPES.register("keycard_setup", () -> IForgeMenuType.create((windowId, inv, data) -> new KeycardReaderMenu(windowId, inv, inv.player.level, data.readBlockPos())));
	public static final RegistryObject<MenuType<BlockPocketManagerMenu>> BLOCK_POCKET_MANAGER_MENU = MENU_TYPES.register("block_pocket_manager", () -> IForgeMenuType.create((windowId, inv, data) -> new BlockPocketManagerMenu(windowId, inv.player.level, data.readBlockPos(), inv)));
	public static final RegistryObject<MenuType<BlockChangeDetectorMenu>> BLOCK_CHANGE_DETECTOR_MENU = MENU_TYPES.register("block_change_detector", () -> IForgeMenuType.create((windowId, inv, data) -> new BlockChangeDetectorMenu(windowId, inv.player.level, data.readBlockPos(), inv)));

	private static final Block.Properties prop() {
		return prop(Material.STONE);
	}

	private static final Block.Properties prop(Material mat) {
		return Block.Properties.of(mat).strength(-1.0F, Float.MAX_VALUE);
	}

	private static final Block.Properties prop(Material mat, float hardness) {
		return Block.Properties.of(mat).strength(hardness, Float.MAX_VALUE);
	}

	private static final Block.Properties prop(Material mat, MaterialColor color) {
		return Block.Properties.of(mat, color).strength(-1.0F, Float.MAX_VALUE);
	}

	private static final Block.Properties propDisguisable() {
		return propDisguisable(Material.STONE);
	}

	private static final Block.Properties propDisguisable(Material mat) {
		return prop(mat).noOcclusion().dynamicShape().isRedstoneConductor(DisguisableBlock::isNormalCube).isSuffocating(DisguisableBlock::isSuffocating);
	}

	private static final Block.Properties glassProp() {
		return prop(Material.GLASS).sound(SoundType.GLASS).noOcclusion().isValidSpawn(SCContent::never).isRedstoneConductor(SCContent::never).isSuffocating(SCContent::never).isViewBlocking(SCContent::never);
	}

	private static Block.Properties mineProp(Block block) {
		return Block.Properties.copy(block).explosionResistance(Float.MAX_VALUE);
	}

	private static final Item.Properties itemProp(CreativeModeTab itemGroup) {
		return new Item.Properties().tab(itemGroup);
	}

	private static boolean always(BlockState state, BlockGetter level, BlockPos pos) {
		return true;
	}

	private static boolean always(BlockState state, BlockGetter level, BlockPos pos, EntityType<?> entityType) {
		return true;
	}

	private static boolean never(BlockState state, BlockGetter level, BlockPos pos) {
		return false;
	}

	private static boolean never(BlockState state, BlockGetter level, BlockPos pos, EntityType<?> entityType) {
		return false;
	}

	private static ForgeFlowingFluid.Properties fakeWaterProperties() {
		return new ForgeFlowingFluid.Properties(ForgeMod.WATER_TYPE, FAKE_WATER, FLOWING_FAKE_WATER).block(FAKE_WATER_BLOCK).bucket(FAKE_WATER_BUCKET);
	}

	private static ForgeFlowingFluid.Properties fakeLavaProperties() {
		return new ForgeFlowingFluid.Properties(ForgeMod.LAVA_TYPE, FAKE_LAVA, FLOWING_FAKE_LAVA).block(FAKE_LAVA_BLOCK).bucket(FAKE_LAVA_BUCKET);
	}
}
