package net.geforcemods.securitycraft;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import net.geforcemods.securitycraft.api.NamedBlockEntity;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.geforcemods.securitycraft.blockentities.AllowlistOnlyBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockPocketBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.geforcemods.securitycraft.blockentities.CageTrapBlockEntity;
import net.geforcemods.securitycraft.blockentities.ClaymoreBlockEntity;
import net.geforcemods.securitycraft.blockentities.DisguisableBlockEntity;
import net.geforcemods.securitycraft.blockentities.DisplayCaseBlockEntity;
import net.geforcemods.securitycraft.blockentities.FloorTrapBlockEntity;
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.geforcemods.securitycraft.blockentities.IMSBlockEntity;
import net.geforcemods.securitycraft.blockentities.InventoryScannerBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeyPanelBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeycardLockBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeycardReaderBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadBarrelBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadBlastFurnaceBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadChestBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadDoorBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadFurnaceBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadSmokerBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadTrapdoorBlockEntity;
import net.geforcemods.securitycraft.blockentities.LaserBlockBlockEntity;
import net.geforcemods.securitycraft.blockentities.MotionActivatedLightBlockEntity;
import net.geforcemods.securitycraft.blockentities.PortableRadarBlockEntity;
import net.geforcemods.securitycraft.blockentities.ProjectorBlockEntity;
import net.geforcemods.securitycraft.blockentities.ProtectoBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedCauldronBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedDispenserBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedDropperBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedFenceGateBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedHopperBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedIronBarsBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedLecternBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedPistonBlockEntity;
import net.geforcemods.securitycraft.blockentities.RetinalScannerBlockEntity;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity;
import net.geforcemods.securitycraft.blockentities.ScannerDoorBlockEntity;
import net.geforcemods.securitycraft.blockentities.ScannerTrapdoorBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecretSignBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecureRedstoneInterfaceBlockEntity;
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
import net.geforcemods.securitycraft.blocks.ElectrifiedIronFenceBlock;
import net.geforcemods.securitycraft.blocks.ElectrifiedIronFenceGateBlock;
import net.geforcemods.securitycraft.blocks.FakeLavaBlock;
import net.geforcemods.securitycraft.blocks.FakeWaterBlock;
import net.geforcemods.securitycraft.blocks.FloorTrapBlock;
import net.geforcemods.securitycraft.blocks.FrameBlock;
import net.geforcemods.securitycraft.blocks.InventoryScannerBlock;
import net.geforcemods.securitycraft.blocks.InventoryScannerFieldBlock;
import net.geforcemods.securitycraft.blocks.KeyPanelBlock;
import net.geforcemods.securitycraft.blocks.KeycardLockBlock;
import net.geforcemods.securitycraft.blocks.KeycardReaderBlock;
import net.geforcemods.securitycraft.blocks.KeypadBarrelBlock;
import net.geforcemods.securitycraft.blocks.KeypadBlastFurnaceBlock;
import net.geforcemods.securitycraft.blocks.KeypadBlock;
import net.geforcemods.securitycraft.blocks.KeypadChestBlock;
import net.geforcemods.securitycraft.blocks.KeypadDoorBlock;
import net.geforcemods.securitycraft.blocks.KeypadFurnaceBlock;
import net.geforcemods.securitycraft.blocks.KeypadSmokerBlock;
import net.geforcemods.securitycraft.blocks.KeypadTrapDoorBlock;
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
import net.geforcemods.securitycraft.blocks.ScannerTrapDoorBlock;
import net.geforcemods.securitycraft.blocks.SecretStandingSignBlock;
import net.geforcemods.securitycraft.blocks.SecretWallSignBlock;
import net.geforcemods.securitycraft.blocks.SecureRedstoneInterfaceBlock;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.blocks.SometimesVisibleBlock;
import net.geforcemods.securitycraft.blocks.SonicSecuritySystemBlock;
import net.geforcemods.securitycraft.blocks.TrophySystemBlock;
import net.geforcemods.securitycraft.blocks.UsernameLoggerBlock;
import net.geforcemods.securitycraft.blocks.mines.BaseFullMineBlock;
import net.geforcemods.securitycraft.blocks.mines.BouncingBettyBlock;
import net.geforcemods.securitycraft.blocks.mines.ClaymoreBlock;
import net.geforcemods.securitycraft.blocks.mines.FallingBlockMineBlock;
import net.geforcemods.securitycraft.blocks.mines.FurnaceMineBlock;
import net.geforcemods.securitycraft.blocks.mines.IMSBlock;
import net.geforcemods.securitycraft.blocks.mines.MineBlock;
import net.geforcemods.securitycraft.blocks.mines.RedstoneOreMineBlock;
import net.geforcemods.securitycraft.blocks.mines.TrackMineBlock;
import net.geforcemods.securitycraft.blocks.reinforced.BaseReinforcedBlock;
import net.geforcemods.securitycraft.blocks.reinforced.HorizontalReinforcedIronBars;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedButtonBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedCarpetBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedCauldronBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedChainBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedCobwebBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedCryingObsidianBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedDispenserBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedDoorBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedDropperBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedEndRodBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedFallingBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedFenceBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedFenceGateBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedGlassBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedGlazedTerracottaBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedGrassBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedGrassPathBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedHopperBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedIronBarsBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedIronTrapDoorBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedLadderBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedLanternBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedLecternBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedLeverBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedMagmaBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedMovingPistonBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedNyliumBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedObserverBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedObsidianBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedPaneBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedPistonBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedPistonHeadBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedPressurePlateBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedRedstoneBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedRedstoneLampBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedRotatedCrystalQuartzPillar;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedRotatedPillarBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedScaffoldingBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedSlabBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedSnowyDirtBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedSoulSandBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedStainedGlassBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedStainedGlassPaneBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedStairsBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedWallBlock;
import net.geforcemods.securitycraft.entity.BouncingBetty;
import net.geforcemods.securitycraft.entity.IMSBomb;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.entity.sentry.Bullet;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.fluids.FakeLavaFluid;
import net.geforcemods.securitycraft.fluids.FakeWaterFluid;
import net.geforcemods.securitycraft.inventory.BlockChangeDetectorMenu;
import net.geforcemods.securitycraft.inventory.BlockPocketManagerMenu;
import net.geforcemods.securitycraft.inventory.BlockReinforcerMenu;
import net.geforcemods.securitycraft.inventory.BriefcaseMenu;
import net.geforcemods.securitycraft.inventory.CustomizeBlockMenu;
import net.geforcemods.securitycraft.inventory.DisguiseModuleMenu;
import net.geforcemods.securitycraft.inventory.InventoryScannerMenu;
import net.geforcemods.securitycraft.inventory.ItemContainer;
import net.geforcemods.securitycraft.inventory.KeycardHolderMenu;
import net.geforcemods.securitycraft.inventory.KeycardReaderMenu;
import net.geforcemods.securitycraft.inventory.KeypadBlastFurnaceMenu;
import net.geforcemods.securitycraft.inventory.KeypadFurnaceMenu;
import net.geforcemods.securitycraft.inventory.KeypadSmokerMenu;
import net.geforcemods.securitycraft.inventory.LaserBlockMenu;
import net.geforcemods.securitycraft.inventory.ModuleItemContainer;
import net.geforcemods.securitycraft.inventory.ProjectorMenu;
import net.geforcemods.securitycraft.inventory.ReinforcedLecternMenu;
import net.geforcemods.securitycraft.inventory.SingleLensMenu;
import net.geforcemods.securitycraft.inventory.TrophySystemMenu;
import net.geforcemods.securitycraft.items.AdminToolItem;
import net.geforcemods.securitycraft.items.BriefcaseItem;
import net.geforcemods.securitycraft.items.CameraMonitorItem;
import net.geforcemods.securitycraft.items.CodebreakerItem;
import net.geforcemods.securitycraft.items.FakeLiquidBucketItem;
import net.geforcemods.securitycraft.items.IncognitoMaskItem;
import net.geforcemods.securitycraft.items.KeyPanelItem;
import net.geforcemods.securitycraft.items.KeycardHolderItem;
import net.geforcemods.securitycraft.items.KeycardItem;
import net.geforcemods.securitycraft.items.LensItem;
import net.geforcemods.securitycraft.items.MineRemoteAccessToolItem;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.items.PortableTunePlayerItem;
import net.geforcemods.securitycraft.items.ReinforcedScaffoldingBlockItem;
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
import net.geforcemods.securitycraft.items.WireCuttersItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.PageGroup;
import net.geforcemods.securitycraft.particle.InterfaceHighlightParticleType;
import net.geforcemods.securitycraft.renderers.DisplayCaseItemRenderer;
import net.geforcemods.securitycraft.renderers.KeypadChestItemRenderer;
import net.geforcemods.securitycraft.renderers.SecurityCameraItemRenderer;
import net.geforcemods.securitycraft.util.HasManualPage;
import net.geforcemods.securitycraft.util.OwnableBE;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.RegisterItemBlock;
import net.geforcemods.securitycraft.util.RegisterItemBlock.SCItemGroup;
import net.geforcemods.securitycraft.util.Reinforced;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PressurePlateBlock.Sensitivity;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.WoodType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemTier;
import net.minecraft.item.TallBlockItem;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class SCContent {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, SecurityCraft.MODID);
	public static final DeferredRegister<TileEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, SecurityCraft.MODID);
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, SecurityCraft.MODID);
	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, SecurityCraft.MODID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SecurityCraft.MODID);
	public static final DeferredRegister<ContainerType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, SecurityCraft.MODID);
	public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, SecurityCraft.MODID);
	public static final String DISPLAY_CASE_PATH = "display_case";
	public static final String KEYPAD_CHEST_PATH = "keypad_chest";
	public static final String SECURITY_CAMERA_PATH = "security_camera";

	//particle types
	public static final RegistryObject<BasicParticleType> FLOOR_TRAP_CLOUD = PARTICLE_TYPES.register("floor_trap_cloud", () -> new BasicParticleType(false));
	public static final RegistryObject<InterfaceHighlightParticleType> INTERFACE_HIGHLIGHT = PARTICLE_TYPES.register("interface_highlight", () -> new InterfaceHighlightParticleType(false));

	//fluids
	public static final RegistryObject<FlowingFluid> FLOWING_FAKE_WATER = FLUIDS.register("flowing_fake_water", () -> new FakeWaterFluid.Flowing());
	public static final RegistryObject<FlowingFluid> FAKE_WATER = FLUIDS.register("fake_water", () -> new FakeWaterFluid.Source());
	public static final RegistryObject<FlowingFluid> FLOWING_FAKE_LAVA = FLUIDS.register("flowing_fake_lava", () -> new FakeLavaFluid.Flowing());
	public static final RegistryObject<FlowingFluid> FAKE_LAVA = FLUIDS.register("fake_lava", () -> new FakeLavaFluid.Source());

	//blocks
	@HasManualPage
	@RegisterItemBlock
	public static final RegistryObject<AlarmBlock> ALARM = registerBlock("alarm", AlarmBlock::new, prop(Material.METAL, MaterialColor.COLOR_RED, 3.5F).sound(SoundType.METAL).lightLevel(state -> state.getValue(AlarmBlock.LIT) ? 15 : 0).harvestTool(ToolType.PICKAXE));
	@HasManualPage
	@RegisterItemBlock
	public static final RegistryObject<BlockChangeDetectorBlock> BLOCK_CHANGE_DETECTOR = registerBlock("block_change_detector", BlockChangeDetectorBlock::new, propDisguisable(3.5F).harvestTool(ToolType.PICKAXE));
	@HasManualPage(designedBy = "Henzoid")
	@RegisterItemBlock
	public static final RegistryObject<BlockPocketManagerBlock> BLOCK_POCKET_MANAGER = registerBlock("block_pocket_manager", BlockPocketManagerBlock::new, prop(Material.STONE, MaterialColor.COLOR_CYAN, 3.5F).harvestTool(ToolType.PICKAXE));
	@HasManualPage
	@RegisterItemBlock(SCItemGroup.DECORATION)
	public static final RegistryObject<BlockPocketWallBlock> BLOCK_POCKET_WALL = registerBlock("block_pocket_wall", BlockPocketWallBlock::new, prop(Material.STONE, MaterialColor.COLOR_CYAN, 0.8F).noCollission().isRedstoneConductor(SCContent::never).isSuffocating(BlockPocketWallBlock::causesSuffocation).isViewBlocking(BlockPocketWallBlock::causesSuffocation).isValidSpawn(SCContent::never).harvestTool(ToolType.PICKAXE));
	@HasManualPage
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<BouncingBettyBlock> BOUNCING_BETTY = registerBlock("bouncing_betty", BouncingBettyBlock::new, prop(Material.METAL, 3.5F).sound(SoundType.METAL).harvestTool(ToolType.PICKAXE).harvestLevel(ItemTier.STONE.getLevel()));
	@HasManualPage
	@RegisterItemBlock
	public static final RegistryObject<CageTrapBlock> CAGE_TRAP = registerBlock("cage_trap", CageTrapBlock::new, propDisguisable(Material.METAL, 5.0F).sound(SoundType.METAL).noCollission().harvestTool(ToolType.PICKAXE).harvestLevel(ItemTier.STONE.getLevel()));
	@HasManualPage
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<ClaymoreBlock> CLAYMORE = registerBlock("claymore", ClaymoreBlock::new, prop(Material.METAL, MaterialColor.TERRACOTTA_GREEN, 3.5F).sound(SoundType.METAL).harvestTool(ToolType.PICKAXE).harvestLevel(ItemTier.STONE.getLevel()));
	@HasManualPage
	public static final RegistryObject<DisplayCaseBlock> DISPLAY_CASE = registerBlock(DISPLAY_CASE_PATH, DisplayCaseBlock::new, prop(Material.METAL, 5.0F).sound(SoundType.METAL).harvestTool(ToolType.PICKAXE).harvestLevel(ItemTier.STONE.getLevel()));
	@HasManualPage
	@RegisterItemBlock
	public static final RegistryObject<FloorTrapBlock> FLOOR_TRAP = registerBlock("floor_trap", FloorTrapBlock::new, propDisguisable(Material.METAL, 5.0F).sound(SoundType.METAL).harvestTool(ToolType.PICKAXE).harvestLevel(ItemTier.STONE.getLevel()));
	@HasManualPage
	@OwnableBE
	@RegisterItemBlock
	public static final RegistryObject<FrameBlock> FRAME = registerBlock("keypad_frame", FrameBlock::new, prop(Material.METAL, 5.0F).sound(SoundType.METAL).harvestTool(ToolType.PICKAXE).harvestLevel(ItemTier.STONE.getLevel()));
	@HasManualPage
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<IMSBlock> IMS = registerBlock("ims", IMSBlock::new, prop(Material.METAL, MaterialColor.TERRACOTTA_GREEN, 3.5F).sound(SoundType.METAL).harvestTool(ToolType.PICKAXE).harvestLevel(ItemTier.STONE.getLevel()));
	@HasManualPage
	@RegisterItemBlock
	public static final RegistryObject<InventoryScannerBlock> INVENTORY_SCANNER = registerBlock("inventory_scanner", InventoryScannerBlock::new, propDisguisable(3.5F).harvestTool(ToolType.PICKAXE));
	public static final RegistryObject<InventoryScannerFieldBlock> INVENTORY_SCANNER_FIELD = registerBlock("inventory_scanner_field", InventoryScannerFieldBlock::new, prop(Material.GLASS, -1.0F));
	@HasManualPage
	@RegisterItemBlock(SCItemGroup.DECORATION)
	public static final RegistryObject<ElectrifiedIronFenceBlock> ELECTRIFIED_IRON_FENCE = registerBlock("electrified_iron_fence", ElectrifiedIronFenceBlock::new, prop(Material.METAL, 5.0F).sound(SoundType.METAL).harvestTool(ToolType.PICKAXE));
	public static final RegistryObject<KeyPanelBlock> KEY_PANEL_BLOCK = registerBlock("key_panel", KeyPanelBlock::new, prop(Material.METAL, 3.5F).sound(SoundType.METAL).harvestTool(ToolType.PICKAXE).harvestLevel(ItemTier.STONE.getLevel()));
	@HasManualPage
	@RegisterItemBlock
	public static final RegistryObject<KeycardLockBlock> KEYCARD_LOCK = registerBlock("keycard_lock", KeycardLockBlock::new, prop(2.0F).harvestTool(ToolType.PICKAXE));
	@HasManualPage
	@RegisterItemBlock
	public static final RegistryObject<KeycardReaderBlock> KEYCARD_READER = registerBlock("keycard_reader", KeycardReaderBlock::new, propDisguisable(3.5F).harvestTool(ToolType.PICKAXE));
	@HasManualPage(hasRecipeDescription = true)
	@RegisterItemBlock
	public static final RegistryObject<KeypadBlock> KEYPAD = registerBlock("keypad", KeypadBlock::new, propDisguisable(Material.METAL, 5.0F).sound(SoundType.METAL).harvestTool(ToolType.PICKAXE).harvestLevel(ItemTier.STONE.getLevel()));
	@HasManualPage(hasRecipeDescription = true)
	@RegisterItemBlock
	public static final RegistryObject<KeypadBarrelBlock> KEYPAD_BARREL = registerBlock("keypad_barrel", KeypadBarrelBlock::new, propDisguisable(Material.METAL, 5.0F).sound(SoundType.METAL).harvestTool(ToolType.PICKAXE).harvestLevel(ItemTier.STONE.getLevel()));
	@HasManualPage(hasRecipeDescription = true)
	public static final RegistryObject<KeypadChestBlock> KEYPAD_CHEST = registerBlock(KEYPAD_CHEST_PATH, KeypadChestBlock::new, propDisguisable(Material.METAL, 5.0F).sound(SoundType.METAL).harvestTool(ToolType.PICKAXE).harvestLevel(ItemTier.STONE.getLevel()));
	public static final RegistryObject<KeypadDoorBlock> KEYPAD_DOOR = registerBlock("keypad_door", p -> new KeypadDoorBlock(p), propDisguisable(Material.METAL, 5.0F).sound(SoundType.METAL).harvestTool(ToolType.PICKAXE));
	@HasManualPage(hasRecipeDescription = true)
	@RegisterItemBlock
	public static final RegistryObject<KeypadTrapDoorBlock> KEYPAD_TRAPDOOR = registerBlock("keypad_trapdoor", KeypadTrapDoorBlock::new, propDisguisable(Material.METAL, 5.0F).sound(SoundType.METAL).isValidSpawn(SCContent::never).harvestTool(ToolType.PICKAXE));
	@HasManualPage(hasRecipeDescription = true)
	@RegisterItemBlock
	public static final RegistryObject<KeypadFurnaceBlock> KEYPAD_FURNACE = registerBlock("keypad_furnace", KeypadFurnaceBlock::new, prop(Material.METAL, 5.0F).sound(SoundType.METAL).lightLevel(state -> state.getValue(AbstractKeypadFurnaceBlock.LIT) ? 13 : 0).harvestTool(ToolType.PICKAXE).harvestLevel(ItemTier.STONE.getLevel()));
	@HasManualPage(hasRecipeDescription = true)
	@RegisterItemBlock
	public static final RegistryObject<KeypadSmokerBlock> KEYPAD_SMOKER = registerBlock("keypad_smoker", KeypadSmokerBlock::new, prop(Material.METAL, 5.0F).sound(SoundType.METAL).lightLevel(state -> state.getValue(AbstractKeypadFurnaceBlock.LIT) ? 13 : 0).harvestTool(ToolType.PICKAXE).harvestLevel(ItemTier.STONE.getLevel()));
	@HasManualPage(hasRecipeDescription = true)
	@RegisterItemBlock
	public static final RegistryObject<KeypadBlastFurnaceBlock> KEYPAD_BLAST_FURNACE = registerBlock("keypad_blast_furnace", KeypadBlastFurnaceBlock::new, prop(Material.METAL, 5.0F).sound(SoundType.METAL).lightLevel(state -> state.getValue(AbstractKeypadFurnaceBlock.LIT) ? 13 : 0).harvestTool(ToolType.PICKAXE).harvestLevel(ItemTier.STONE.getLevel()));
	@HasManualPage
	@RegisterItemBlock
	public static final RegistryObject<LaserBlock> LASER_BLOCK = registerBlock("laser_block", LaserBlock::new, propDisguisable(3.5F).harvestTool(ToolType.PICKAXE));
	public static final RegistryObject<LaserFieldBlock> LASER_FIELD = registerBlock("laser", LaserFieldBlock::new, prop(Material.GLASS, -1.0F));
	@HasManualPage
	@RegisterItemBlock
	public static final RegistryObject<MotionActivatedLightBlock> MOTION_ACTIVATED_LIGHT = registerBlock("motion_activated_light", MotionActivatedLightBlock::new, prop(Material.GLASS, 5.0F).sound(SoundType.GLASS).lightLevel(state -> state.getValue(MotionActivatedLightBlock.LIT) ? 15 : 0).harvestTool(ToolType.PICKAXE).harvestLevel(ItemTier.STONE.getLevel()));
	@HasManualPage
	@OwnableBE
	@RegisterItemBlock
	public static final RegistryObject<PanicButtonBlock> PANIC_BUTTON = registerBlock("panic_button", p -> new PanicButtonBlock(false, p), prop(3.5F).lightLevel(state -> state.getValue(AbstractButtonBlock.POWERED) ? 4 : 0).harvestTool(ToolType.PICKAXE));
	@HasManualPage
	@RegisterItemBlock
	public static final RegistryObject<PortableRadarBlock> PORTABLE_RADAR = registerBlock("portable_radar", PortableRadarBlock::new, prop(Material.METAL, MaterialColor.COLOR_BLACK, 5.0F).harvestTool(ToolType.PICKAXE).harvestLevel(ItemTier.STONE.getLevel()));
	@HasManualPage
	@OwnableBE
	@RegisterItemBlock
	public static final RegistryObject<ProjectorBlock> PROJECTOR = registerBlock("projector", ProjectorBlock::new, propDisguisable(Material.METAL, 5.0F).sound(SoundType.METAL).harvestTool(ToolType.PICKAXE).harvestLevel(ItemTier.STONE.getLevel()));
	@HasManualPage
	@RegisterItemBlock
	public static final RegistryObject<ProtectoBlock> PROTECTO = registerBlock("protecto", ProtectoBlock::new, propDisguisable(Material.METAL, 10.0F).sound(SoundType.METAL).lightLevel(state -> 7).harvestTool(ToolType.PICKAXE).harvestLevel(ItemTier.DIAMOND.getLevel()));
	@OwnableBE
	public static final RegistryObject<ReinforcedDoorBlock> REINFORCED_DOOR = registerBlock("iron_door_reinforced", ReinforcedDoorBlock::new, prop(Material.METAL, 5.0F).sound(SoundType.METAL).noOcclusion().harvestTool(ToolType.PICKAXE));
	@HasManualPage
	@RegisterItemBlock
	public static final RegistryObject<ElectrifiedIronFenceGateBlock> ELECTRIFIED_IRON_FENCE_GATE = registerBlock("reinforced_fence_gate", ElectrifiedIronFenceGateBlock::new, prop(Material.METAL, 5.0F).sound(SoundType.METAL).harvestTool(ToolType.PICKAXE));
	@HasManualPage
	@RegisterItemBlock
	public static final RegistryObject<RetinalScannerBlock> RETINAL_SCANNER = registerBlock("retinal_scanner", RetinalScannerBlock::new, propDisguisable(3.5F).harvestTool(ToolType.PICKAXE));
	public static final RegistryObject<RiftStabilizerBlock> RIFT_STABILIZER = registerBlock("rift_stabilizer", RiftStabilizerBlock::new, propDisguisable(Material.METAL, 5.0F).sound(SoundType.METAL).harvestTool(ToolType.PICKAXE).harvestLevel(ItemTier.STONE.getLevel()));
	public static final RegistryObject<ScannerDoorBlock> SCANNER_DOOR = registerBlock("scanner_door", ScannerDoorBlock::new, propDisguisable(Material.METAL, 5.0F).sound(SoundType.METAL).harvestTool(ToolType.PICKAXE));
	@HasManualPage
	@RegisterItemBlock
	public static final RegistryObject<ScannerTrapDoorBlock> SCANNER_TRAPDOOR = registerBlock("scanner_trapdoor", ScannerTrapDoorBlock::new, propDisguisable(Material.METAL, 5.0F).sound(SoundType.METAL).isValidSpawn(SCContent::never).harvestTool(ToolType.PICKAXE));
	public static final RegistryObject<SecretStandingSignBlock> SECRET_OAK_SIGN = secretStandingSign("secret_sign_standing", Blocks.OAK_SIGN);
	public static final RegistryObject<SecretWallSignBlock> SECRET_OAK_WALL_SIGN = secretWallSign("secret_sign_wall", Blocks.OAK_SIGN);
	public static final RegistryObject<SecretStandingSignBlock> SECRET_SPRUCE_SIGN = secretStandingSign("secret_spruce_sign_standing", Blocks.SPRUCE_SIGN);
	public static final RegistryObject<SecretWallSignBlock> SECRET_SPRUCE_WALL_SIGN = secretWallSign("secret_spruce_sign_wall", Blocks.SPRUCE_SIGN);
	public static final RegistryObject<SecretStandingSignBlock> SECRET_BIRCH_SIGN = secretStandingSign("secret_birch_sign_standing", Blocks.BIRCH_SIGN);
	public static final RegistryObject<SecretWallSignBlock> SECRET_BIRCH_WALL_SIGN = secretWallSign("secret_birch_sign_wall", Blocks.BIRCH_SIGN);
	public static final RegistryObject<SecretStandingSignBlock> SECRET_JUNGLE_SIGN = secretStandingSign("secret_jungle_sign_standing", Blocks.JUNGLE_SIGN);
	public static final RegistryObject<SecretWallSignBlock> SECRET_JUNGLE_WALL_SIGN = secretWallSign("secret_jungle_sign_wall", Blocks.JUNGLE_SIGN);
	public static final RegistryObject<SecretStandingSignBlock> SECRET_ACACIA_SIGN = secretStandingSign("secret_acacia_sign_standing", Blocks.ACACIA_SIGN);
	public static final RegistryObject<SecretWallSignBlock> SECRET_ACACIA_WALL_SIGN = secretWallSign("secret_acacia_sign_wall", Blocks.ACACIA_SIGN);
	public static final RegistryObject<SecretStandingSignBlock> SECRET_DARK_OAK_SIGN = secretStandingSign("secret_dark_oak_sign_standing", Blocks.DARK_OAK_SIGN);
	public static final RegistryObject<SecretWallSignBlock> SECRET_DARK_OAK_WALL_SIGN = secretWallSign("secret_dark_oak_sign_wall", Blocks.DARK_OAK_SIGN);
	public static final RegistryObject<SecretStandingSignBlock> SECRET_CRIMSON_SIGN = secretStandingSign("secret_crimson_sign_standing", Blocks.CRIMSON_SIGN);
	public static final RegistryObject<SecretWallSignBlock> SECRET_CRIMSON_WALL_SIGN = secretWallSign("secret_crimson_sign_wall", Blocks.CRIMSON_SIGN);
	public static final RegistryObject<SecretStandingSignBlock> SECRET_WARPED_SIGN = secretStandingSign("secret_warped_sign_standing", Blocks.WARPED_SIGN);
	public static final RegistryObject<SecretWallSignBlock> SECRET_WARPED_WALL_SIGN = secretWallSign("secret_warped_sign_wall", Blocks.WARPED_SIGN);
	@HasManualPage
	@RegisterItemBlock
	public static final RegistryObject<SecureRedstoneInterfaceBlock> SECURE_REDSTONE_INTERFACE = registerBlock("secure_redstone_interface", SecureRedstoneInterfaceBlock::new, propDisguisable(3.5F).harvestTool(ToolType.PICKAXE));
	@HasManualPage
	public static final RegistryObject<SecurityCameraBlock> SECURITY_CAMERA = registerBlock(SECURITY_CAMERA_PATH, SecurityCameraBlock::new, propDisguisable(Material.METAL, 5.0F).noCollission().harvestTool(ToolType.PICKAXE).harvestLevel(ItemTier.STONE.getLevel()));
	@HasManualPage
	public static final RegistryObject<SonicSecuritySystemBlock> SONIC_SECURITY_SYSTEM = registerBlock("sonic_security_system", SonicSecuritySystemBlock::new, propDisguisable(Material.METAL, 5.0F).sound(SoundType.METAL).noCollission().harvestTool(ToolType.PICKAXE).harvestLevel(ItemTier.STONE.getLevel()));
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> TRACK_MINE = BLOCKS.register("track_mine", () -> new TrackMineBlock(prop(Material.METAL, 0.7F).noCollission().sound(SoundType.METAL).harvestTool(ToolType.PICKAXE)));
	@HasManualPage
	@RegisterItemBlock(SCItemGroup.TECHNICAL)
	public static final RegistryObject<TrophySystemBlock> TROPHY_SYSTEM = registerBlock("trophy_system", TrophySystemBlock::new, propDisguisable(Material.METAL, 5.0F).sound(SoundType.METAL).harvestTool(ToolType.PICKAXE).harvestLevel(ItemTier.STONE.getLevel()));
	@HasManualPage
	@RegisterItemBlock
	public static final RegistryObject<UsernameLoggerBlock> USERNAME_LOGGER = registerBlock("username_logger", UsernameLoggerBlock::new, propDisguisable(3.5F).harvestTool(ToolType.PICKAXE));
	@HasManualPage
	@OwnableBE
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<MineBlock> MINE = registerBlock("mine", MineBlock::new, prop(Material.METAL, 3.5F).sound(SoundType.METAL).harvestTool(ToolType.PICKAXE).harvestLevel(ItemTier.STONE.getLevel()));
	public static final RegistryObject<FakeWaterBlock> FAKE_WATER_BLOCK = registerBlock("fake_water_block", p -> new FakeWaterBlock(p, FAKE_WATER), reinforcedCopy(Blocks.WATER));
	public static final RegistryObject<FakeLavaBlock> FAKE_LAVA_BLOCK = registerBlock("fake_lava_block", p -> new FakeLavaBlock(p, FAKE_LAVA), reinforcedCopy(Blocks.LAVA));

	//block mines
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<BaseFullMineBlock> STONE_MINE = blockMine("stone_mine", Blocks.STONE);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<BaseFullMineBlock> DIRT_MINE = blockMine("dirt_mine", Blocks.DIRT);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<BaseFullMineBlock> COBBLESTONE_MINE = blockMine("cobblestone_mine", Blocks.COBBLESTONE);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<FallingBlockMineBlock> SAND_MINE = reinforcedBlock("sand_mine", Blocks.SAND, FallingBlockMineBlock::new);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<FallingBlockMineBlock> GRAVEL_MINE = reinforcedBlock("gravel_mine", Blocks.GRAVEL, FallingBlockMineBlock::new);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<BaseFullMineBlock> GOLD_ORE_MINE = blockMine("gold_mine", Blocks.GOLD_ORE);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<BaseFullMineBlock> IRON_ORE_MINE = blockMine("iron_mine", Blocks.IRON_ORE);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<BaseFullMineBlock> NETHERRACK_MINE = blockMine("netherrack_mine", Blocks.NETHERRACK);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<BaseFullMineBlock> END_STONE_MINE = blockMine("end_stone_mine", Blocks.END_STONE);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<BaseFullMineBlock> COAL_ORE_MINE = blockMine("coal_mine", Blocks.COAL_ORE);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<BaseFullMineBlock> NETHER_GOLD_ORE_MINE = blockMine("nether_gold_mine", Blocks.NETHER_GOLD_ORE);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<BaseFullMineBlock> LAPIS_ORE_MINE = blockMine("lapis_mine", Blocks.LAPIS_ORE);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<BaseFullMineBlock> DIAMOND_ORE_MINE = blockMine("diamond_mine", Blocks.DIAMOND_ORE);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<RedstoneOreMineBlock> REDSTONE_ORE_MINE = reinforcedBlock("redstone_mine", Blocks.REDSTONE_ORE, RedstoneOreMineBlock::new);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<BaseFullMineBlock> EMERALD_ORE_MINE = blockMine("emerald_mine", Blocks.EMERALD_ORE);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<BaseFullMineBlock> QUARTZ_ORE_MINE = blockMine("quartz_mine", Blocks.NETHER_QUARTZ_ORE);
	@HasManualPage(PageGroup.BLOCK_MINES)
	public static final RegistryObject<BaseFullMineBlock> ANCIENT_DEBRIS_MINE = blockMine("ancient_debris_mine", Blocks.ANCIENT_DEBRIS);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<BaseFullMineBlock> GILDED_BLACKSTONE_MINE = blockMine("gilded_blackstone_mine", Blocks.GILDED_BLACKSTONE);
	@HasManualPage(PageGroup.FURNACE_MINES)
	@OwnableBE
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<FurnaceMineBlock> FURNACE_MINE = reinforcedBlock("furnace_mine", Blocks.FURNACE, FurnaceMineBlock::new, p -> p.lightLevel(state -> 0));
	@HasManualPage(PageGroup.FURNACE_MINES)
	@OwnableBE
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<FurnaceMineBlock> SMOKER_MINE = reinforcedBlock("smoker_mine", Blocks.SMOKER, FurnaceMineBlock::new, p -> p.lightLevel(state -> 0));
	@HasManualPage(PageGroup.FURNACE_MINES)
	@OwnableBE
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<FurnaceMineBlock> BLAST_FURNACE_MINE = reinforcedBlock("blast_furnace_mine", Blocks.BLAST_FURNACE, FurnaceMineBlock::new, p -> p.lightLevel(state -> 0));

	//reinforced blocks (ordered by vanilla building blocks creative tab order)
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_STONE = reinforcedBlock("reinforced_stone", Blocks.STONE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_GRANITE = reinforcedBlock("reinforced_granite", Blocks.GRANITE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_POLISHED_GRANITE = reinforcedBlock("reinforced_polished_granite", Blocks.POLISHED_GRANITE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_DIORITE = reinforcedBlock("reinforced_diorite", Blocks.DIORITE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_POLISHED_DIORITE = reinforcedBlock("reinforced_polished_diorite", Blocks.POLISHED_DIORITE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_ANDESITE = reinforcedBlock("reinforced_andesite", Blocks.ANDESITE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_POLISHED_ANDESITE = reinforcedBlock("reinforced_polished_andesite", Blocks.POLISHED_ANDESITE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedGrassBlock> REINFORCED_GRASS_BLOCK = reinforcedBlock("reinforced_grass_block", Blocks.GRASS_BLOCK, (p, b) -> new ReinforcedGrassBlock(p));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_DIRT = reinforcedBlock("reinforced_dirt", Blocks.DIRT);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_COARSE_DIRT = reinforcedBlock("reinforced_coarse_dirt", Blocks.COARSE_DIRT);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSnowyDirtBlock> REINFORCED_PODZOL = reinforcedBlock("reinforced_podzol", Blocks.PODZOL, ReinforcedSnowyDirtBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedNyliumBlock> REINFORCED_CRIMSON_NYLIUM = reinforcedBlock("reinforced_crimson_nylium", Blocks.CRIMSON_NYLIUM, ReinforcedNyliumBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedNyliumBlock> REINFORCED_WARPED_NYLIUM = reinforcedBlock("reinforced_warped_nylium", Blocks.WARPED_NYLIUM, ReinforcedNyliumBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_COBBLESTONE = reinforcedBlock("reinforced_cobblestone", Blocks.COBBLESTONE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_OAK_PLANKS = reinforcedBlock("reinforced_oak_planks", Blocks.OAK_PLANKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_SPRUCE_PLANKS = reinforcedBlock("reinforced_spruce_planks", Blocks.SPRUCE_PLANKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_BIRCH_PLANKS = reinforcedBlock("reinforced_birch_planks", Blocks.BIRCH_PLANKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_JUNGLE_PLANKS = reinforcedBlock("reinforced_jungle_planks", Blocks.JUNGLE_PLANKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_ACACIA_PLANKS = reinforcedBlock("reinforced_acacia_planks", Blocks.ACACIA_PLANKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_DARK_OAK_PLANKS = reinforcedBlock("reinforced_dark_oak_planks", Blocks.DARK_OAK_PLANKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_CRIMSON_PLANKS = reinforcedBlock("reinforced_crimson_planks", Blocks.CRIMSON_PLANKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_WARPED_PLANKS = reinforcedBlock("reinforced_warped_planks", Blocks.WARPED_PLANKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedFallingBlock> REINFORCED_SAND = reinforcedBlock("reinforced_sand", Blocks.SAND, ReinforcedFallingBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedFallingBlock> REINFORCED_RED_SAND = reinforcedBlock("reinforced_red_sand", Blocks.RED_SAND, ReinforcedFallingBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedFallingBlock> REINFORCED_GRAVEL = reinforcedBlock("reinforced_gravel", Blocks.GRAVEL, ReinforcedFallingBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedRotatedPillarBlock> REINFORCED_OAK_LOG = reinforcedBlock("reinforced_oak_log", Blocks.OAK_LOG, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedRotatedPillarBlock> REINFORCED_SPRUCE_LOG = reinforcedBlock("reinforced_spruce_log", Blocks.SPRUCE_LOG, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedRotatedPillarBlock> REINFORCED_BIRCH_LOG = reinforcedBlock("reinforced_birch_log", Blocks.BIRCH_LOG, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedRotatedPillarBlock> REINFORCED_JUNGLE_LOG = reinforcedBlock("reinforced_jungle_log", Blocks.JUNGLE_LOG, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedRotatedPillarBlock> REINFORCED_ACACIA_LOG = reinforcedBlock("reinforced_acacia_log", Blocks.ACACIA_LOG, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedRotatedPillarBlock> REINFORCED_DARK_OAK_LOG = reinforcedBlock("reinforced_dark_oak_log", Blocks.DARK_OAK_LOG, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedRotatedPillarBlock> REINFORCED_CRIMSON_STEM = reinforcedBlock("reinforced_crimson_stem", Blocks.CRIMSON_STEM, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedRotatedPillarBlock> REINFORCED_WARPED_STEM = reinforcedBlock("reinforced_warped_stem", Blocks.WARPED_STEM, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_OAK_LOG = reinforcedBlock("reinforced_stripped_oak_log", Blocks.STRIPPED_OAK_LOG, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_SPRUCE_LOG = reinforcedBlock("reinforced_stripped_spruce_log", Blocks.STRIPPED_SPRUCE_LOG, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_BIRCH_LOG = reinforcedBlock("reinforced_stripped_birch_log", Blocks.STRIPPED_BIRCH_LOG, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_JUNGLE_LOG = reinforcedBlock("reinforced_stripped_jungle_log", Blocks.STRIPPED_JUNGLE_LOG, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_ACACIA_LOG = reinforcedBlock("reinforced_stripped_acacia_log", Blocks.STRIPPED_ACACIA_LOG, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_DARK_OAK_LOG = reinforcedBlock("reinforced_stripped_dark_oak_log", Blocks.STRIPPED_DARK_OAK_LOG, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_CRIMSON_STEM = reinforcedBlock("reinforced_stripped_crimson_stem", Blocks.STRIPPED_CRIMSON_STEM, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_WARPED_STEM = reinforcedBlock("reinforced_stripped_warped_stem", Blocks.STRIPPED_WARPED_STEM, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_OAK_WOOD = reinforcedBlock("reinforced_stripped_oak_wood", Blocks.STRIPPED_OAK_WOOD, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_SPRUCE_WOOD = reinforcedBlock("reinforced_stripped_spruce_wood", Blocks.STRIPPED_SPRUCE_WOOD, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_BIRCH_WOOD = reinforcedBlock("reinforced_stripped_birch_wood", Blocks.STRIPPED_BIRCH_WOOD, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_JUNGLE_WOOD = reinforcedBlock("reinforced_stripped_jungle_wood", Blocks.STRIPPED_JUNGLE_WOOD, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_ACACIA_WOOD = reinforcedBlock("reinforced_stripped_acacia_wood", Blocks.STRIPPED_ACACIA_WOOD, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_DARK_OAK_WOOD = reinforcedBlock("reinforced_stripped_dark_oak_wood", Blocks.STRIPPED_DARK_OAK_WOOD, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_CRIMSON_HYPHAE = reinforcedBlock("reinforced_stripped_crimson_hyphae", Blocks.STRIPPED_CRIMSON_HYPHAE, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_WARPED_HYPHAE = reinforcedBlock("reinforced_stripped_warped_hyphae", Blocks.STRIPPED_WARPED_HYPHAE, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedRotatedPillarBlock> REINFORCED_OAK_WOOD = reinforcedBlock("reinforced_oak_wood", Blocks.OAK_WOOD, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedRotatedPillarBlock> REINFORCED_SPRUCE_WOOD = reinforcedBlock("reinforced_spruce_wood", Blocks.SPRUCE_WOOD, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedRotatedPillarBlock> REINFORCED_BIRCH_WOOD = reinforcedBlock("reinforced_birch_wood", Blocks.BIRCH_WOOD, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedRotatedPillarBlock> REINFORCED_JUNGLE_WOOD = reinforcedBlock("reinforced_jungle_wood", Blocks.JUNGLE_WOOD, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedRotatedPillarBlock> REINFORCED_ACACIA_WOOD = reinforcedBlock("reinforced_acacia_wood", Blocks.ACACIA_WOOD, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedRotatedPillarBlock> REINFORCED_DARK_OAK_WOOD = reinforcedBlock("reinforced_dark_oak_wood", Blocks.DARK_OAK_WOOD, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedRotatedPillarBlock> REINFORCED_CRIMSON_HYPHAE = reinforcedBlock("reinforced_crimson_hyphae", Blocks.CRIMSON_HYPHAE, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedRotatedPillarBlock> REINFORCED_WARPED_HYPHAE = reinforcedBlock("reinforced_warped_hyphae", Blocks.WARPED_HYPHAE, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<ReinforcedGlassBlock> REINFORCED_GLASS = reinforcedBlock("reinforced_glass", Blocks.GLASS, ReinforcedGlassBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_LAPIS_BLOCK = reinforcedBlock("reinforced_lapis_block", Blocks.LAPIS_BLOCK);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_SANDSTONE = reinforcedBlock("reinforced_sandstone", Blocks.SANDSTONE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_CHISELED_SANDSTONE = reinforcedBlock("reinforced_chiseled_sandstone", Blocks.CHISELED_SANDSTONE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_CUT_SANDSTONE = reinforcedBlock("reinforced_cut_sandstone", Blocks.CUT_SANDSTONE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_WHITE_WOOL = reinforcedBlock("reinforced_white_wool", Blocks.WHITE_WOOL);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_ORANGE_WOOL = reinforcedBlock("reinforced_orange_wool", Blocks.ORANGE_WOOL);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_MAGENTA_WOOL = reinforcedBlock("reinforced_magenta_wool", Blocks.MAGENTA_WOOL);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_LIGHT_BLUE_WOOL = reinforcedBlock("reinforced_light_blue_wool", Blocks.LIGHT_BLUE_WOOL);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_YELLOW_WOOL = reinforcedBlock("reinforced_yellow_wool", Blocks.YELLOW_WOOL);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_LIME_WOOL = reinforcedBlock("reinforced_lime_wool", Blocks.LIME_WOOL);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_PINK_WOOL = reinforcedBlock("reinforced_pink_wool", Blocks.PINK_WOOL);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_GRAY_WOOL = reinforcedBlock("reinforced_gray_wool", Blocks.GRAY_WOOL);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_LIGHT_GRAY_WOOL = reinforcedBlock("reinforced_light_gray_wool", Blocks.LIGHT_GRAY_WOOL);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_CYAN_WOOL = reinforcedBlock("reinforced_cyan_wool", Blocks.CYAN_WOOL);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_PURPLE_WOOL = reinforcedBlock("reinforced_purple_wool", Blocks.PURPLE_WOOL);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_BLUE_WOOL = reinforcedBlock("reinforced_blue_wool", Blocks.BLUE_WOOL);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_BROWN_WOOL = reinforcedBlock("reinforced_brown_wool", Blocks.BROWN_WOOL);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_GREEN_WOOL = reinforcedBlock("reinforced_green_wool", Blocks.GREEN_WOOL);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_RED_WOOL = reinforcedBlock("reinforced_red_wool", Blocks.RED_WOOL);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_BLACK_WOOL = reinforcedBlock("reinforced_black_wool", Blocks.BLACK_WOOL);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_GOLD_BLOCK = reinforcedBlock("reinforced_gold_block", Blocks.GOLD_BLOCK);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_IRON_BLOCK = reinforcedBlock("reinforced_iron_block", Blocks.IRON_BLOCK);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSlabBlock> REINFORCED_OAK_SLAB = reinforcedBlock("reinforced_oak_slab", Blocks.OAK_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSlabBlock> REINFORCED_SPRUCE_SLAB = reinforcedBlock("reinforced_spruce_slab", Blocks.SPRUCE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSlabBlock> REINFORCED_BIRCH_SLAB = reinforcedBlock("reinforced_birch_slab", Blocks.BIRCH_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSlabBlock> REINFORCED_JUNGLE_SLAB = reinforcedBlock("reinforced_jungle_slab", Blocks.JUNGLE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSlabBlock> REINFORCED_ACACIA_SLAB = reinforcedBlock("reinforced_acacia_slab", Blocks.ACACIA_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSlabBlock> REINFORCED_DARK_OAK_SLAB = reinforcedBlock("reinforced_dark_oak_slab", Blocks.DARK_OAK_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSlabBlock> REINFORCED_CRIMSON_SLAB = reinforcedBlock("reinforced_crimson_slab", Blocks.CRIMSON_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSlabBlock> REINFORCED_WARPED_SLAB = reinforcedBlock("reinforced_warped_slab", Blocks.WARPED_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSlabBlock> REINFORCED_NORMAL_STONE_SLAB = reinforcedBlock("reinforced_normal_stone_slab", Blocks.STONE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSlabBlock> REINFORCED_SMOOTH_STONE_SLAB = reinforcedBlock("reinforced_stone_slab", Blocks.SMOOTH_STONE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSlabBlock> REINFORCED_SANDSTONE_SLAB = reinforcedBlock("reinforced_sandstone_slab", Blocks.SANDSTONE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSlabBlock> REINFORCED_CUT_SANDSTONE_SLAB = reinforcedBlock("reinforced_cut_sandstone_slab", Blocks.CUT_SANDSTONE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSlabBlock> REINFORCED_COBBLESTONE_SLAB = reinforcedBlock("reinforced_cobblestone_slab", Blocks.COBBLESTONE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSlabBlock> REINFORCED_BRICK_SLAB = reinforcedBlock("reinforced_brick_slab", Blocks.BRICK_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSlabBlock> REINFORCED_STONE_BRICK_SLAB = reinforcedBlock("reinforced_stone_brick_slab", Blocks.STONE_BRICK_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSlabBlock> REINFORCED_NETHER_BRICK_SLAB = reinforcedBlock("reinforced_nether_brick_slab", Blocks.NETHER_BRICK_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSlabBlock> REINFORCED_QUARTZ_SLAB = reinforcedBlock("reinforced_quartz_slab", Blocks.QUARTZ_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSlabBlock> REINFORCED_RED_SANDSTONE_SLAB = reinforcedBlock("reinforced_red_sandstone_slab", Blocks.RED_SANDSTONE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSlabBlock> REINFORCED_CUT_RED_SANDSTONE_SLAB = reinforcedBlock("reinforced_cut_red_sandstone_slab", Blocks.CUT_RED_SANDSTONE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSlabBlock> REINFORCED_PURPUR_SLAB = reinforcedBlock("reinforced_purpur_slab", Blocks.PURPUR_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSlabBlock> REINFORCED_PRISMARINE_SLAB = reinforcedBlock("reinforced_prismarine_slab", Blocks.PRISMARINE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSlabBlock> REINFORCED_PRISMARINE_BRICK_SLAB = reinforcedBlock("reinforced_prismarine_brick_slab", Blocks.PRISMARINE_BRICK_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSlabBlock> REINFORCED_DARK_PRISMARINE_SLAB = reinforcedBlock("reinforced_dark_prismarine_slab", Blocks.DARK_PRISMARINE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_SMOOTH_QUARTZ = reinforcedBlock("reinforced_smooth_quartz", Blocks.SMOOTH_QUARTZ);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_SMOOTH_RED_SANDSTONE = reinforcedBlock("reinforced_smooth_red_sandstone", Blocks.SMOOTH_RED_SANDSTONE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_SMOOTH_SANDSTONE = reinforcedBlock("reinforced_smooth_sandstone", Blocks.SMOOTH_SANDSTONE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_SMOOTH_STONE = reinforcedBlock("reinforced_smooth_stone", Blocks.SMOOTH_STONE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_BRICKS = reinforcedBlock("reinforced_bricks", Blocks.BRICKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_BOOKSHELF = reinforcedBlock("reinforced_bookshelf", Blocks.BOOKSHELF);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_MOSSY_COBBLESTONE = reinforcedBlock("reinforced_mossy_cobblestone", Blocks.MOSSY_COBBLESTONE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedObsidianBlock> REINFORCED_OBSIDIAN = reinforcedBlock("reinforced_obsidian", Blocks.OBSIDIAN, ReinforcedObsidianBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_PURPUR_BLOCK = reinforcedBlock("reinforced_purpur_block", Blocks.PURPUR_BLOCK);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedRotatedPillarBlock> REINFORCED_PURPUR_PILLAR = reinforcedBlock("reinforced_purpur_pillar", Blocks.PURPUR_PILLAR, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedStairsBlock> REINFORCED_PURPUR_STAIRS = reinforcedBlock("reinforced_purpur_stairs", Blocks.PURPUR_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedStairsBlock> REINFORCED_OAK_STAIRS = reinforcedBlock("reinforced_oak_stairs", Blocks.OAK_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_DIAMOND_BLOCK = reinforcedBlock("reinforced_diamond_block", Blocks.DIAMOND_BLOCK);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedStairsBlock> REINFORCED_COBBLESTONE_STAIRS = reinforcedBlock("reinforced_cobblestone_stairs", Blocks.COBBLESTONE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_ICE = reinforcedBlock("reinforced_ice", Blocks.ICE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_SNOW_BLOCK = reinforcedBlock("reinforced_snow_block", Blocks.SNOW_BLOCK);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_CLAY = reinforcedBlock("reinforced_clay", Blocks.CLAY);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_NETHERRACK = reinforcedBlock("reinforced_netherrack", Blocks.NETHERRACK);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSoulSandBlock> REINFORCED_SOUL_SAND = reinforcedBlock("reinforced_soul_sand", Blocks.SOUL_SAND, ReinforcedSoulSandBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_SOUL_SOIL = reinforcedBlock("reinforced_soul_soil", Blocks.SOUL_SOIL);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedRotatedPillarBlock> REINFORCED_BASALT = reinforcedBlock("reinforced_basalt", Blocks.BASALT, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedRotatedPillarBlock> REINFORCED_POLISHED_BASALT = reinforcedBlock("reinforced_polished_basalt", Blocks.POLISHED_BASALT, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_GLOWSTONE = reinforcedBlock("reinforced_glowstone", Blocks.GLOWSTONE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_STONE_BRICKS = reinforcedBlock("reinforced_stone_bricks", Blocks.STONE_BRICKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_MOSSY_STONE_BRICKS = reinforcedBlock("reinforced_mossy_stone_bricks", Blocks.MOSSY_STONE_BRICKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_CRACKED_STONE_BRICKS = reinforcedBlock("reinforced_cracked_stone_bricks", Blocks.CRACKED_STONE_BRICKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_CHISELED_STONE_BRICKS = reinforcedBlock("reinforced_chiseled_stone_bricks", Blocks.CHISELED_STONE_BRICKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedStairsBlock> REINFORCED_BRICK_STAIRS = reinforcedBlock("reinforced_brick_stairs", Blocks.BRICK_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedStairsBlock> REINFORCED_STONE_BRICK_STAIRS = reinforcedBlock("reinforced_stone_brick_stairs", Blocks.STONE_BRICK_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSnowyDirtBlock> REINFORCED_MYCELIUM = reinforcedBlock("reinforced_mycelium", Blocks.MYCELIUM, ReinforcedSnowyDirtBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_NETHER_BRICKS = reinforcedBlock("reinforced_nether_bricks", Blocks.NETHER_BRICKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_CRACKED_NETHER_BRICKS = reinforcedBlock("reinforced_cracked_nether_bricks", Blocks.CRACKED_NETHER_BRICKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_CHISELED_NETHER_BRICKS = reinforcedBlock("reinforced_chiseled_nether_bricks", Blocks.CHISELED_NETHER_BRICKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedStairsBlock> REINFORCED_NETHER_BRICK_STAIRS = reinforcedBlock("reinforced_nether_brick_stairs", Blocks.NETHER_BRICK_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_END_STONE = reinforcedBlock("reinforced_end_stone", Blocks.END_STONE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_END_STONE_BRICKS = reinforcedBlock("reinforced_end_stone_bricks", Blocks.END_STONE_BRICKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedStairsBlock> REINFORCED_SANDSTONE_STAIRS = reinforcedBlock("reinforced_sandstone_stairs", Blocks.SANDSTONE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_EMERALD_BLOCK = reinforcedBlock("reinforced_emerald_block", Blocks.EMERALD_BLOCK);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedStairsBlock> REINFORCED_SPRUCE_STAIRS = reinforcedBlock("reinforced_spruce_stairs", Blocks.SPRUCE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedStairsBlock> REINFORCED_BIRCH_STAIRS = reinforcedBlock("reinforced_birch_stairs", Blocks.BIRCH_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedStairsBlock> REINFORCED_JUNGLE_STAIRS = reinforcedBlock("reinforced_jungle_stairs", Blocks.JUNGLE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedStairsBlock> REINFORCED_CRIMSON_STAIRS = reinforcedBlock("reinforced_crimson_stairs", Blocks.CRIMSON_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedStairsBlock> REINFORCED_WARPED_STAIRS = reinforcedBlock("reinforced_warped_stairs", Blocks.WARPED_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_CHISELED_QUARTZ = reinforcedBlock("reinforced_chiseled_quartz_block", Blocks.CHISELED_QUARTZ_BLOCK);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_QUARTZ_BLOCK = reinforcedBlock("reinforced_quartz_block", Blocks.QUARTZ_BLOCK);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_QUARTZ_BRICKS = reinforcedBlock("reinforced_quartz_bricks", Blocks.QUARTZ_BRICKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedRotatedPillarBlock> REINFORCED_QUARTZ_PILLAR = reinforcedBlock("reinforced_quartz_pillar", Blocks.QUARTZ_PILLAR, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedStairsBlock> REINFORCED_QUARTZ_STAIRS = reinforcedBlock("reinforced_quartz_stairs", Blocks.QUARTZ_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_WHITE_TERRACOTTA = reinforcedBlock("reinforced_white_terracotta", Blocks.WHITE_TERRACOTTA);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_ORANGE_TERRACOTTA = reinforcedBlock("reinforced_orange_terracotta", Blocks.ORANGE_TERRACOTTA);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_MAGENTA_TERRACOTTA = reinforcedBlock("reinforced_magenta_terracotta", Blocks.MAGENTA_TERRACOTTA);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_LIGHT_BLUE_TERRACOTTA = reinforcedBlock("reinforced_light_blue_terracotta", Blocks.LIGHT_BLUE_TERRACOTTA);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_YELLOW_TERRACOTTA = reinforcedBlock("reinforced_yellow_terracotta", Blocks.YELLOW_TERRACOTTA);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_LIME_TERRACOTTA = reinforcedBlock("reinforced_lime_terracotta", Blocks.LIME_TERRACOTTA);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_PINK_TERRACOTTA = reinforcedBlock("reinforced_pink_terracotta", Blocks.PINK_TERRACOTTA);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_GRAY_TERRACOTTA = reinforcedBlock("reinforced_gray_terracotta", Blocks.GRAY_TERRACOTTA);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_LIGHT_GRAY_TERRACOTTA = reinforcedBlock("reinforced_light_gray_terracotta", Blocks.LIGHT_GRAY_TERRACOTTA);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_CYAN_TERRACOTTA = reinforcedBlock("reinforced_cyan_terracotta", Blocks.CYAN_TERRACOTTA);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_PURPLE_TERRACOTTA = reinforcedBlock("reinforced_purple_terracotta", Blocks.PURPLE_TERRACOTTA);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_BLUE_TERRACOTTA = reinforcedBlock("reinforced_blue_terracotta", Blocks.BLUE_TERRACOTTA);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_BROWN_TERRACOTTA = reinforcedBlock("reinforced_brown_terracotta", Blocks.BROWN_TERRACOTTA);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_GREEN_TERRACOTTA = reinforcedBlock("reinforced_green_terracotta", Blocks.GREEN_TERRACOTTA);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_RED_TERRACOTTA = reinforcedBlock("reinforced_red_terracotta", Blocks.RED_TERRACOTTA);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_BLACK_TERRACOTTA = reinforcedBlock("reinforced_black_terracotta", Blocks.BLACK_TERRACOTTA);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_TERRACOTTA = reinforcedBlock("reinforced_hardened_clay", Blocks.TERRACOTTA);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_COAL_BLOCK = reinforcedBlock("reinforced_coal_block", Blocks.COAL_BLOCK);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_PACKED_ICE = reinforcedBlock("reinforced_packed_ice", Blocks.PACKED_ICE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedStairsBlock> REINFORCED_ACACIA_STAIRS = reinforcedBlock("reinforced_acacia_stairs", Blocks.ACACIA_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedStairsBlock> REINFORCED_DARK_OAK_STAIRS = reinforcedBlock("reinforced_dark_oak_stairs", Blocks.DARK_OAK_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<ReinforcedStainedGlassBlock> REINFORCED_WHITE_STAINED_GLASS = reinforcedBlock("reinforced_white_stained_glass", Blocks.WHITE_STAINED_GLASS, ReinforcedStainedGlassBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<ReinforcedStainedGlassBlock> REINFORCED_ORANGE_STAINED_GLASS = reinforcedBlock("reinforced_orange_stained_glass", Blocks.ORANGE_STAINED_GLASS, ReinforcedStainedGlassBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<ReinforcedStainedGlassBlock> REINFORCED_MAGENTA_STAINED_GLASS = reinforcedBlock("reinforced_magenta_stained_glass", Blocks.MAGENTA_STAINED_GLASS, ReinforcedStainedGlassBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<ReinforcedStainedGlassBlock> REINFORCED_LIGHT_BLUE_STAINED_GLASS = reinforcedBlock("reinforced_light_blue_stained_glass", Blocks.LIGHT_BLUE_STAINED_GLASS, ReinforcedStainedGlassBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<ReinforcedStainedGlassBlock> REINFORCED_YELLOW_STAINED_GLASS = reinforcedBlock("reinforced_yellow_stained_glass", Blocks.YELLOW_STAINED_GLASS, ReinforcedStainedGlassBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<ReinforcedStainedGlassBlock> REINFORCED_LIME_STAINED_GLASS = reinforcedBlock("reinforced_lime_stained_glass", Blocks.LIME_STAINED_GLASS, ReinforcedStainedGlassBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<ReinforcedStainedGlassBlock> REINFORCED_PINK_STAINED_GLASS = reinforcedBlock("reinforced_pink_stained_glass", Blocks.PINK_STAINED_GLASS, ReinforcedStainedGlassBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<ReinforcedStainedGlassBlock> REINFORCED_GRAY_STAINED_GLASS = reinforcedBlock("reinforced_gray_stained_glass", Blocks.GRAY_STAINED_GLASS, ReinforcedStainedGlassBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<ReinforcedStainedGlassBlock> REINFORCED_LIGHT_GRAY_STAINED_GLASS = reinforcedBlock("reinforced_light_gray_stained_glass", Blocks.LIGHT_GRAY_STAINED_GLASS, ReinforcedStainedGlassBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<ReinforcedStainedGlassBlock> REINFORCED_CYAN_STAINED_GLASS = reinforcedBlock("reinforced_cyan_stained_glass", Blocks.CYAN_STAINED_GLASS, ReinforcedStainedGlassBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<ReinforcedStainedGlassBlock> REINFORCED_PURPLE_STAINED_GLASS = reinforcedBlock("reinforced_purple_stained_glass", Blocks.PURPLE_STAINED_GLASS, ReinforcedStainedGlassBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<ReinforcedStainedGlassBlock> REINFORCED_BLUE_STAINED_GLASS = reinforcedBlock("reinforced_blue_stained_glass", Blocks.BLUE_STAINED_GLASS, ReinforcedStainedGlassBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<ReinforcedStainedGlassBlock> REINFORCED_BROWN_STAINED_GLASS = reinforcedBlock("reinforced_brown_stained_glass", Blocks.BROWN_STAINED_GLASS, ReinforcedStainedGlassBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<ReinforcedStainedGlassBlock> REINFORCED_GREEN_STAINED_GLASS = reinforcedBlock("reinforced_green_stained_glass", Blocks.GREEN_STAINED_GLASS, ReinforcedStainedGlassBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<ReinforcedStainedGlassBlock> REINFORCED_RED_STAINED_GLASS = reinforcedBlock("reinforced_red_stained_glass", Blocks.RED_STAINED_GLASS, ReinforcedStainedGlassBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<ReinforcedStainedGlassBlock> REINFORCED_BLACK_STAINED_GLASS = reinforcedBlock("reinforced_black_stained_glass", Blocks.BLACK_STAINED_GLASS, ReinforcedStainedGlassBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_PRISMARINE = reinforcedBlock("reinforced_prismarine", Blocks.PRISMARINE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_PRISMARINE_BRICKS = reinforcedBlock("reinforced_prismarine_bricks", Blocks.PRISMARINE_BRICKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_DARK_PRISMARINE = reinforcedBlock("reinforced_dark_prismarine", Blocks.DARK_PRISMARINE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedStairsBlock> REINFORCED_PRISMARINE_STAIRS = reinforcedBlock("reinforced_prismarine_stairs", Blocks.PRISMARINE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedStairsBlock> REINFORCED_PRISMARINE_BRICK_STAIRS = reinforcedBlock("reinforced_prismarine_brick_stairs", Blocks.PRISMARINE_BRICK_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedStairsBlock> REINFORCED_DARK_PRISMARINE_STAIRS = reinforcedBlock("reinforced_dark_prismarine_stairs", Blocks.DARK_PRISMARINE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_SEA_LANTERN = reinforcedBlock("reinforced_sea_lantern", Blocks.SEA_LANTERN);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_RED_SANDSTONE = reinforcedBlock("reinforced_red_sandstone", Blocks.RED_SANDSTONE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_CHISELED_RED_SANDSTONE = reinforcedBlock("reinforced_chiseled_red_sandstone", Blocks.CHISELED_RED_SANDSTONE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_CUT_RED_SANDSTONE = reinforcedBlock("reinforced_cut_red_sandstone", Blocks.CUT_RED_SANDSTONE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedStairsBlock> REINFORCED_RED_SANDSTONE_STAIRS = reinforcedBlock("reinforced_red_sandstone_stairs", Blocks.RED_SANDSTONE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedMagmaBlock> REINFORCED_MAGMA_BLOCK = reinforcedBlock("reinforced_magma_block", Blocks.MAGMA_BLOCK, ReinforcedMagmaBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_NETHER_WART_BLOCK = reinforcedBlock("reinforced_nether_wart_block", Blocks.NETHER_WART_BLOCK);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_WARPED_WART_BLOCK = reinforcedBlock("reinforced_warped_wart_block", Blocks.WARPED_WART_BLOCK);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_RED_NETHER_BRICKS = reinforcedBlock("reinforced_red_nether_bricks", Blocks.RED_NETHER_BRICKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedRotatedPillarBlock> REINFORCED_BONE_BLOCK = reinforcedBlock("reinforced_bone_block", Blocks.BONE_BLOCK, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_WHITE_CONCRETE = reinforcedBlock("reinforced_white_concrete", Blocks.WHITE_CONCRETE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_ORANGE_CONCRETE = reinforcedBlock("reinforced_orange_concrete", Blocks.ORANGE_CONCRETE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_MAGENTA_CONCRETE = reinforcedBlock("reinforced_magenta_concrete", Blocks.MAGENTA_CONCRETE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_LIGHT_BLUE_CONCRETE = reinforcedBlock("reinforced_light_blue_concrete", Blocks.LIGHT_BLUE_CONCRETE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_YELLOW_CONCRETE = reinforcedBlock("reinforced_yellow_concrete", Blocks.YELLOW_CONCRETE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_LIME_CONCRETE = reinforcedBlock("reinforced_lime_concrete", Blocks.LIME_CONCRETE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_PINK_CONCRETE = reinforcedBlock("reinforced_pink_concrete", Blocks.PINK_CONCRETE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_GRAY_CONCRETE = reinforcedBlock("reinforced_gray_concrete", Blocks.GRAY_CONCRETE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_LIGHT_GRAY_CONCRETE = reinforcedBlock("reinforced_light_gray_concrete", Blocks.LIGHT_GRAY_CONCRETE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_CYAN_CONCRETE = reinforcedBlock("reinforced_cyan_concrete", Blocks.CYAN_CONCRETE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_PURPLE_CONCRETE = reinforcedBlock("reinforced_purple_concrete", Blocks.PURPLE_CONCRETE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_BLUE_CONCRETE = reinforcedBlock("reinforced_blue_concrete", Blocks.BLUE_CONCRETE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_BROWN_CONCRETE = reinforcedBlock("reinforced_brown_concrete", Blocks.BROWN_CONCRETE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_GREEN_CONCRETE = reinforcedBlock("reinforced_green_concrete", Blocks.GREEN_CONCRETE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_RED_CONCRETE = reinforcedBlock("reinforced_red_concrete", Blocks.RED_CONCRETE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_BLACK_CONCRETE = reinforcedBlock("reinforced_black_concrete", Blocks.BLACK_CONCRETE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_BLUE_ICE = reinforcedBlock("reinforced_blue_ice", Blocks.BLUE_ICE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedStairsBlock> REINFORCED_POLISHED_GRANITE_STAIRS = reinforcedBlock("reinforced_polished_granite_stairs", Blocks.POLISHED_GRANITE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedStairsBlock> REINFORCED_SMOOTH_RED_SANDSTONE_STAIRS = reinforcedBlock("reinforced_smooth_red_sandstone_stairs", Blocks.SMOOTH_RED_SANDSTONE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedStairsBlock> REINFORCED_MOSSY_STONE_BRICK_STAIRS = reinforcedBlock("reinforced_mossy_stone_brick_stairs", Blocks.MOSSY_STONE_BRICK_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedStairsBlock> REINFORCED_POLISHED_DIORITE_STAIRS = reinforcedBlock("reinforced_polished_diorite_stairs", Blocks.POLISHED_DIORITE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedStairsBlock> REINFORCED_MOSSY_COBBLESTONE_STAIRS = reinforcedBlock("reinforced_mossy_cobblestone_stairs", Blocks.MOSSY_COBBLESTONE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedStairsBlock> REINFORCED_END_STONE_BRICK_STAIRS = reinforcedBlock("reinforced_end_stone_brick_stairs", Blocks.END_STONE_BRICK_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedStairsBlock> REINFORCED_STONE_STAIRS = reinforcedBlock("reinforced_stone_stairs", Blocks.STONE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedStairsBlock> REINFORCED_SMOOTH_SANDSTONE_STAIRS = reinforcedBlock("reinforced_smooth_sandstone_stairs", Blocks.SMOOTH_SANDSTONE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedStairsBlock> REINFORCED_SMOOTH_QUARTZ_STAIRS = reinforcedBlock("reinforced_smooth_quartz_stairs", Blocks.SMOOTH_QUARTZ_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedStairsBlock> REINFORCED_GRANITE_STAIRS = reinforcedBlock("reinforced_granite_stairs", Blocks.GRANITE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedStairsBlock> REINFORCED_ANDESITE_STAIRS = reinforcedBlock("reinforced_andesite_stairs", Blocks.ANDESITE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedStairsBlock> REINFORCED_RED_NETHER_BRICK_STAIRS = reinforcedBlock("reinforced_red_nether_brick_stairs", Blocks.RED_NETHER_BRICK_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedStairsBlock> REINFORCED_POLISHED_ANDESITE_STAIRS = reinforcedBlock("reinforced_polished_andesite_stairs", Blocks.POLISHED_ANDESITE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedStairsBlock> REINFORCED_DIORITE_STAIRS = reinforcedBlock("reinforced_diorite_stairs", Blocks.DIORITE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSlabBlock> REINFORCED_POLISHED_GRANITE_SLAB = reinforcedBlock("reinforced_polished_granite_slab", Blocks.POLISHED_GRANITE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSlabBlock> REINFORCED_SMOOTH_RED_SANDSTONE_SLAB = reinforcedBlock("reinforced_smooth_red_sandstone_slab", Blocks.SMOOTH_RED_SANDSTONE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSlabBlock> REINFORCED_MOSSY_STONE_BRICK_SLAB = reinforcedBlock("reinforced_mossy_stone_brick_slab", Blocks.MOSSY_STONE_BRICK_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSlabBlock> REINFORCED_POLISHED_DIORITE_SLAB = reinforcedBlock("reinforced_polished_diorite_slab", Blocks.POLISHED_DIORITE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSlabBlock> REINFORCED_MOSSY_COBBLESTONE_SLAB = reinforcedBlock("reinforced_mossy_cobblestone_slab", Blocks.MOSSY_COBBLESTONE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSlabBlock> REINFORCED_END_STONE_BRICK_SLAB = reinforcedBlock("reinforced_end_stone_brick_slab", Blocks.END_STONE_BRICK_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSlabBlock> REINFORCED_SMOOTH_SANDSTONE_SLAB = reinforcedBlock("reinforced_smooth_sandstone_slab", Blocks.SMOOTH_SANDSTONE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSlabBlock> REINFORCED_SMOOTH_QUARTZ_SLAB = reinforcedBlock("reinforced_smooth_quartz_slab", Blocks.SMOOTH_QUARTZ_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSlabBlock> REINFORCED_GRANITE_SLAB = reinforcedBlock("reinforced_granite_slab", Blocks.GRANITE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSlabBlock> REINFORCED_ANDESITE_SLAB = reinforcedBlock("reinforced_andesite_slab", Blocks.ANDESITE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSlabBlock> REINFORCED_RED_NETHER_BRICK_SLAB = reinforcedBlock("reinforced_red_nether_brick_slab", Blocks.RED_NETHER_BRICK_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSlabBlock> REINFORCED_POLISHED_ANDESITE_SLAB = reinforcedBlock("reinforced_polished_andesite_slab", Blocks.POLISHED_ANDESITE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSlabBlock> REINFORCED_DIORITE_SLAB = reinforcedBlock("reinforced_diorite_slab", Blocks.DIORITE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_NETHERITE_BLOCK = reinforcedBlock("reinforced_netherite_block", Blocks.NETHERITE_BLOCK);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedCryingObsidianBlock> REINFORCED_CRYING_OBSIDIAN = reinforcedBlock("reinforced_crying_obsidian", Blocks.CRYING_OBSIDIAN, ReinforcedCryingObsidianBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_BLACKSTONE = reinforcedBlock("reinforced_blackstone", Blocks.BLACKSTONE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSlabBlock> REINFORCED_BLACKSTONE_SLAB = reinforcedBlock("reinforced_blackstone_slab", Blocks.BLACKSTONE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedStairsBlock> REINFORCED_BLACKSTONE_STAIRS = reinforcedBlock("reinforced_blackstone_stairs", Blocks.BLACKSTONE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_POLISHED_BLACKSTONE = reinforcedBlock("reinforced_polished_blackstone", Blocks.POLISHED_BLACKSTONE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSlabBlock> REINFORCED_POLISHED_BLACKSTONE_SLAB = reinforcedBlock("reinforced_polished_blackstone_slab", Blocks.POLISHED_BLACKSTONE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedStairsBlock> REINFORCED_POLISHED_BLACKSTONE_STAIRS = reinforcedBlock("reinforced_polished_blackstone_stairs", Blocks.POLISHED_BLACKSTONE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_CHISELED_POLISHED_BLACKSTONE = reinforcedBlock("reinforced_chiseled_polished_blackstone", Blocks.CHISELED_POLISHED_BLACKSTONE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_POLISHED_BLACKSTONE_BRICKS = reinforcedBlock("reinforced_polished_blackstone_bricks", Blocks.POLISHED_BLACKSTONE_BRICKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSlabBlock> REINFORCED_POLISHED_BLACKSTONE_BRICK_SLAB = reinforcedBlock("reinforced_polished_blackstone_brick_slab", Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedStairsBlock> REINFORCED_POLISHED_BLACKSTONE_BRICK_STAIRS = reinforcedBlock("reinforced_polished_blackstone_brick_stairs", Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_CRACKED_POLISHED_BLACKSTONE_BRICKS = reinforcedBlock("reinforced_cracked_polished_blackstone_bricks", Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS);

	//ordered by vanilla decoration blocks creative tab order
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedCobwebBlock> REINFORCED_COBWEB = reinforcedBlock("reinforced_cobweb", Blocks.COBWEB, ReinforcedCobwebBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedEndRodBlock> REINFORCED_END_ROD = reinforcedBlock("reinforced_end_rod", Blocks.END_ROD, (p, b) -> new ReinforcedEndRodBlock(p));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedLadderBlock> REINFORCED_LADDER = reinforcedBlock("reinforced_ladder", Blocks.LADDER, (p, b) -> new ReinforcedLadderBlock(p));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedFenceBlock> REINFORCED_OAK_FENCE = reinforcedBlock("reinforced_oak_fence", Blocks.OAK_FENCE, ReinforcedFenceBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedFenceBlock> REINFORCED_SPRUCE_FENCE = reinforcedBlock("reinforced_spruce_fence", Blocks.SPRUCE_FENCE, ReinforcedFenceBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedFenceBlock> REINFORCED_BIRCH_FENCE = reinforcedBlock("reinforced_birch_fence", Blocks.BIRCH_FENCE, ReinforcedFenceBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedFenceBlock> REINFORCED_JUNGLE_FENCE = reinforcedBlock("reinforced_jungle_fence", Blocks.JUNGLE_FENCE, ReinforcedFenceBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedFenceBlock> REINFORCED_ACACIA_FENCE = reinforcedBlock("reinforced_acacia_fence", Blocks.ACACIA_FENCE, ReinforcedFenceBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedFenceBlock> REINFORCED_DARK_OAK_FENCE = reinforcedBlock("reinforced_dark_oak_fence", Blocks.DARK_OAK_FENCE, ReinforcedFenceBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedFenceBlock> REINFORCED_CRIMSON_FENCE = reinforcedBlock("reinforced_crimson_fence", Blocks.CRIMSON_FENCE, ReinforcedFenceBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedFenceBlock> REINFORCED_WARPED_FENCE = reinforcedBlock("reinforced_warped_fence", Blocks.WARPED_FENCE, ReinforcedFenceBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<ReinforcedIronBarsBlock> REINFORCED_IRON_BARS = reinforcedBlock("reinforced_iron_bars", Blocks.IRON_BARS, ReinforcedIronBarsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedChainBlock> REINFORCED_CHAIN = reinforcedBlock("reinforced_chain", Blocks.CHAIN, ReinforcedChainBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<ReinforcedPaneBlock> REINFORCED_GLASS_PANE = reinforcedBlock("reinforced_glass_pane", Blocks.GLASS_PANE, ReinforcedPaneBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedFenceBlock> REINFORCED_NETHER_BRICK_FENCE = reinforcedBlock("reinforced_nether_brick_fence", Blocks.NETHER_BRICK_FENCE, ReinforcedFenceBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedWallBlock> REINFORCED_COBBLESTONE_WALL = reinforcedBlock("reinforced_cobblestone_wall", Blocks.COBBLESTONE_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedWallBlock> REINFORCED_MOSSY_COBBLESTONE_WALL = reinforcedBlock("reinforced_mossy_cobblestone_wall", Blocks.MOSSY_COBBLESTONE_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedWallBlock> REINFORCED_BRICK_WALL = reinforcedBlock("reinforced_brick_wall", Blocks.BRICK_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedWallBlock> REINFORCED_PRISMARINE_WALL = reinforcedBlock("reinforced_prismarine_wall", Blocks.PRISMARINE_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedWallBlock> REINFORCED_RED_SANDSTONE_WALL = reinforcedBlock("reinforced_red_sandstone_wall", Blocks.RED_SANDSTONE_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedWallBlock> REINFORCED_MOSSY_STONE_BRICK_WALL = reinforcedBlock("reinforced_mossy_stone_brick_wall", Blocks.MOSSY_STONE_BRICK_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedWallBlock> REINFORCED_GRANITE_WALL = reinforcedBlock("reinforced_granite_wall", Blocks.GRANITE_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedWallBlock> REINFORCED_STONE_BRICK_WALL = reinforcedBlock("reinforced_stone_brick_wall", Blocks.STONE_BRICK_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedWallBlock> REINFORCED_NETHER_BRICK_WALL = reinforcedBlock("reinforced_nether_brick_wall", Blocks.NETHER_BRICK_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedWallBlock> REINFORCED_ANDESITE_WALL = reinforcedBlock("reinforced_andesite_wall", Blocks.ANDESITE_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedWallBlock> REINFORCED_RED_NETHER_BRICK_WALL = reinforcedBlock("reinforced_red_nether_brick_wall", Blocks.RED_NETHER_BRICK_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedWallBlock> REINFORCED_SANDSTONE_WALL = reinforcedBlock("reinforced_sandstone_wall", Blocks.SANDSTONE_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedWallBlock> REINFORCED_END_STONE_BRICK_WALL = reinforcedBlock("reinforced_end_stone_brick_wall", Blocks.END_STONE_BRICK_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedWallBlock> REINFORCED_DIORITE_WALL = reinforcedBlock("reinforced_diorite_wall", Blocks.DIORITE_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedWallBlock> REINFORCED_BLACKSTONE_WALL = reinforcedBlock("reinforced_blackstone_wall", Blocks.BLACKSTONE_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedWallBlock> REINFORCED_POLISHED_BLACKSTONE_WALL = reinforcedBlock("reinforced_polished_blackstone_wall", Blocks.POLISHED_BLACKSTONE_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedWallBlock> REINFORCED_POLISHED_BLACKSTONE_BRICK_WALL = reinforcedBlock("reinforced_polished_blackstone_brick_wall", Blocks.POLISHED_BLACKSTONE_BRICK_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedCarpetBlock> REINFORCED_WHITE_CARPET = reinforcedBlock("reinforced_white_carpet", Blocks.WHITE_CARPET, ReinforcedCarpetBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedCarpetBlock> REINFORCED_ORANGE_CARPET = reinforcedBlock("reinforced_orange_carpet", Blocks.ORANGE_CARPET, ReinforcedCarpetBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedCarpetBlock> REINFORCED_MAGENTA_CARPET = reinforcedBlock("reinforced_magenta_carpet", Blocks.MAGENTA_CARPET, ReinforcedCarpetBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedCarpetBlock> REINFORCED_LIGHT_BLUE_CARPET = reinforcedBlock("reinforced_light_blue_carpet", Blocks.LIGHT_BLUE_CARPET, ReinforcedCarpetBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedCarpetBlock> REINFORCED_YELLOW_CARPET = reinforcedBlock("reinforced_yellow_carpet", Blocks.YELLOW_CARPET, ReinforcedCarpetBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedCarpetBlock> REINFORCED_LIME_CARPET = reinforcedBlock("reinforced_lime_carpet", Blocks.LIME_CARPET, ReinforcedCarpetBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedCarpetBlock> REINFORCED_PINK_CARPET = reinforcedBlock("reinforced_pink_carpet", Blocks.PINK_CARPET, ReinforcedCarpetBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedCarpetBlock> REINFORCED_GRAY_CARPET = reinforcedBlock("reinforced_gray_carpet", Blocks.GRAY_CARPET, ReinforcedCarpetBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedCarpetBlock> REINFORCED_LIGHT_GRAY_CARPET = reinforcedBlock("reinforced_light_gray_carpet", Blocks.LIGHT_GRAY_CARPET, ReinforcedCarpetBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedCarpetBlock> REINFORCED_CYAN_CARPET = reinforcedBlock("reinforced_cyan_carpet", Blocks.CYAN_CARPET, ReinforcedCarpetBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedCarpetBlock> REINFORCED_PURPLE_CARPET = reinforcedBlock("reinforced_purple_carpet", Blocks.PURPLE_CARPET, ReinforcedCarpetBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedCarpetBlock> REINFORCED_BLUE_CARPET = reinforcedBlock("reinforced_blue_carpet", Blocks.BLUE_CARPET, ReinforcedCarpetBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedCarpetBlock> REINFORCED_BROWN_CARPET = reinforcedBlock("reinforced_brown_carpet", Blocks.BROWN_CARPET, ReinforcedCarpetBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedCarpetBlock> REINFORCED_GREEN_CARPET = reinforcedBlock("reinforced_green_carpet", Blocks.GREEN_CARPET, ReinforcedCarpetBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedCarpetBlock> REINFORCED_RED_CARPET = reinforcedBlock("reinforced_red_carpet", Blocks.RED_CARPET, ReinforcedCarpetBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedCarpetBlock> REINFORCED_BLACK_CARPET = reinforcedBlock("reinforced_black_carpet", Blocks.BLACK_CARPET, ReinforcedCarpetBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<ReinforcedStainedGlassPaneBlock> REINFORCED_WHITE_STAINED_GLASS_PANE = reinforcedBlock("reinforced_white_stained_glass_pane", Blocks.WHITE_STAINED_GLASS_PANE, ReinforcedStainedGlassPaneBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<ReinforcedStainedGlassPaneBlock> REINFORCED_ORANGE_STAINED_GLASS_PANE = reinforcedBlock("reinforced_orange_stained_glass_pane", Blocks.ORANGE_STAINED_GLASS_PANE, ReinforcedStainedGlassPaneBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<ReinforcedStainedGlassPaneBlock> REINFORCED_MAGENTA_STAINED_GLASS_PANE = reinforcedBlock("reinforced_magenta_stained_glass_pane", Blocks.MAGENTA_STAINED_GLASS_PANE, ReinforcedStainedGlassPaneBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<ReinforcedStainedGlassPaneBlock> REINFORCED_LIGHT_BLUE_STAINED_GLASS_PANE = reinforcedBlock("reinforced_light_blue_stained_glass_pane", Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, ReinforcedStainedGlassPaneBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<ReinforcedStainedGlassPaneBlock> REINFORCED_YELLOW_STAINED_GLASS_PANE = reinforcedBlock("reinforced_yellow_stained_glass_pane", Blocks.YELLOW_STAINED_GLASS_PANE, ReinforcedStainedGlassPaneBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<ReinforcedStainedGlassPaneBlock> REINFORCED_LIME_STAINED_GLASS_PANE = reinforcedBlock("reinforced_lime_stained_glass_pane", Blocks.LIME_STAINED_GLASS_PANE, ReinforcedStainedGlassPaneBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<ReinforcedStainedGlassPaneBlock> REINFORCED_PINK_STAINED_GLASS_PANE = reinforcedBlock("reinforced_pink_stained_glass_pane", Blocks.PINK_STAINED_GLASS_PANE, ReinforcedStainedGlassPaneBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<ReinforcedStainedGlassPaneBlock> REINFORCED_GRAY_STAINED_GLASS_PANE = reinforcedBlock("reinforced_gray_stained_glass_pane", Blocks.GRAY_STAINED_GLASS_PANE, ReinforcedStainedGlassPaneBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<ReinforcedStainedGlassPaneBlock> REINFORCED_LIGHT_GRAY_STAINED_GLASS_PANE = reinforcedBlock("reinforced_light_gray_stained_glass_pane", Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, ReinforcedStainedGlassPaneBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<ReinforcedStainedGlassPaneBlock> REINFORCED_CYAN_STAINED_GLASS_PANE = reinforcedBlock("reinforced_cyan_stained_glass_pane", Blocks.CYAN_STAINED_GLASS_PANE, ReinforcedStainedGlassPaneBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<ReinforcedStainedGlassPaneBlock> REINFORCED_PURPLE_STAINED_GLASS_PANE = reinforcedBlock("reinforced_purple_stained_glass_pane", Blocks.PURPLE_STAINED_GLASS_PANE, ReinforcedStainedGlassPaneBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<ReinforcedStainedGlassPaneBlock> REINFORCED_BLUE_STAINED_GLASS_PANE = reinforcedBlock("reinforced_blue_stained_glass_pane", Blocks.BLUE_STAINED_GLASS_PANE, ReinforcedStainedGlassPaneBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<ReinforcedStainedGlassPaneBlock> REINFORCED_BROWN_STAINED_GLASS_PANE = reinforcedBlock("reinforced_brown_stained_glass_pane", Blocks.BROWN_STAINED_GLASS_PANE, ReinforcedStainedGlassPaneBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<ReinforcedStainedGlassPaneBlock> REINFORCED_GREEN_STAINED_GLASS_PANE = reinforcedBlock("reinforced_green_stained_glass_pane", Blocks.GREEN_STAINED_GLASS_PANE, ReinforcedStainedGlassPaneBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<ReinforcedStainedGlassPaneBlock> REINFORCED_RED_STAINED_GLASS_PANE = reinforcedBlock("reinforced_red_stained_glass_pane", Blocks.RED_STAINED_GLASS_PANE, ReinforcedStainedGlassPaneBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<ReinforcedStainedGlassPaneBlock> REINFORCED_BLACK_STAINED_GLASS_PANE = reinforcedBlock("reinforced_black_stained_glass_pane", Blocks.BLACK_STAINED_GLASS_PANE, ReinforcedStainedGlassPaneBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedGlazedTerracottaBlock> REINFORCED_WHITE_GLAZED_TERRACOTTA = reinforcedBlock("reinforced_white_glazed_terracotta", Blocks.WHITE_GLAZED_TERRACOTTA, ReinforcedGlazedTerracottaBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedGlazedTerracottaBlock> REINFORCED_ORANGE_GLAZED_TERRACOTTA = reinforcedBlock("reinforced_orange_glazed_terracotta", Blocks.ORANGE_GLAZED_TERRACOTTA, ReinforcedGlazedTerracottaBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedGlazedTerracottaBlock> REINFORCED_MAGENTA_GLAZED_TERRACOTTA = reinforcedBlock("reinforced_magenta_glazed_terracotta", Blocks.MAGENTA_GLAZED_TERRACOTTA, ReinforcedGlazedTerracottaBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedGlazedTerracottaBlock> REINFORCED_LIGHT_BLUE_GLAZED_TERRACOTTA = reinforcedBlock("reinforced_light_blue_glazed_terracotta", Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA, ReinforcedGlazedTerracottaBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedGlazedTerracottaBlock> REINFORCED_YELLOW_GLAZED_TERRACOTTA = reinforcedBlock("reinforced_yellow_glazed_terracotta", Blocks.YELLOW_GLAZED_TERRACOTTA, ReinforcedGlazedTerracottaBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedGlazedTerracottaBlock> REINFORCED_LIME_GLAZED_TERRACOTTA = reinforcedBlock("reinforced_lime_glazed_terracotta", Blocks.LIME_GLAZED_TERRACOTTA, ReinforcedGlazedTerracottaBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedGlazedTerracottaBlock> REINFORCED_PINK_GLAZED_TERRACOTTA = reinforcedBlock("reinforced_pink_glazed_terracotta", Blocks.PINK_GLAZED_TERRACOTTA, ReinforcedGlazedTerracottaBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedGlazedTerracottaBlock> REINFORCED_GRAY_GLAZED_TERRACOTTA = reinforcedBlock("reinforced_gray_glazed_terracotta", Blocks.GRAY_GLAZED_TERRACOTTA, ReinforcedGlazedTerracottaBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedGlazedTerracottaBlock> REINFORCED_LIGHT_GRAY_GLAZED_TERRACOTTA = reinforcedBlock("reinforced_light_gray_glazed_terracotta", Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA, ReinforcedGlazedTerracottaBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedGlazedTerracottaBlock> REINFORCED_CYAN_GLAZED_TERRACOTTA = reinforcedBlock("reinforced_cyan_glazed_terracotta", Blocks.CYAN_GLAZED_TERRACOTTA, ReinforcedGlazedTerracottaBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedGlazedTerracottaBlock> REINFORCED_PURPLE_GLAZED_TERRACOTTA = reinforcedBlock("reinforced_purple_glazed_terracotta", Blocks.PURPLE_GLAZED_TERRACOTTA, ReinforcedGlazedTerracottaBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedGlazedTerracottaBlock> REINFORCED_BLUE_GLAZED_TERRACOTTA = reinforcedBlock("reinforced_blue_glazed_terracotta", Blocks.BLUE_GLAZED_TERRACOTTA, ReinforcedGlazedTerracottaBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedGlazedTerracottaBlock> REINFORCED_BROWN_GLAZED_TERRACOTTA = reinforcedBlock("reinforced_brown_glazed_terracotta", Blocks.BROWN_GLAZED_TERRACOTTA, ReinforcedGlazedTerracottaBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedGlazedTerracottaBlock> REINFORCED_GREEN_GLAZED_TERRACOTTA = reinforcedBlock("reinforced_green_glazed_terracotta", Blocks.GREEN_GLAZED_TERRACOTTA, ReinforcedGlazedTerracottaBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedGlazedTerracottaBlock> REINFORCED_RED_GLAZED_TERRACOTTA = reinforcedBlock("reinforced_red_glazed_terracotta", Blocks.RED_GLAZED_TERRACOTTA, ReinforcedGlazedTerracottaBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedGlazedTerracottaBlock> REINFORCED_BLACK_GLAZED_TERRACOTTA = reinforcedBlock("reinforced_black_glazed_terracotta", Blocks.BLACK_GLAZED_TERRACOTTA, ReinforcedGlazedTerracottaBlock::new);
	@OwnableBE
	@Reinforced(registerBlockItem = false)
	public static final RegistryObject<ReinforcedScaffoldingBlock> REINFORCED_SCAFFOLDING = reinforcedBlock("reinforced_scaffolding", Blocks.SCAFFOLDING, (p, b) -> new ReinforcedScaffoldingBlock(p));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedLanternBlock> REINFORCED_LANTERN = reinforcedBlock("reinforced_lantern", Blocks.LANTERN, ReinforcedLanternBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedLanternBlock> REINFORCED_SOUL_LANTERN = reinforcedBlock("reinforced_soul_lantern", Blocks.SOUL_LANTERN, ReinforcedLanternBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_SHROOMLIGHT = reinforcedBlock("reinforced_shroomlight", Blocks.SHROOMLIGHT);

	//ordered by vanilla redstone tab order
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final RegistryObject<ReinforcedDispenserBlock> REINFORCED_DISPENSER = reinforcedBlock("reinforced_dispenser", Blocks.DISPENSER, (p, b) -> new ReinforcedDispenserBlock(propDisguisable(p)));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final RegistryObject<ReinforcedPistonBlock> REINFORCED_STICKY_PISTON = reinforcedBlock("reinforced_sticky_piston", Blocks.STICKY_PISTON, (p, b) -> new ReinforcedPistonBlock(true, p));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final RegistryObject<ReinforcedPistonBlock> REINFORCED_PISTON = reinforcedBlock("reinforced_piston", Blocks.PISTON, (p, b) -> new ReinforcedPistonBlock(false, p));
	@HasManualPage
	@Reinforced
	public static final RegistryObject<ReinforcedLeverBlock> REINFORCED_LEVER = reinforcedBlock("reinforced_lever", Blocks.LEVER, (p, b) -> new ReinforcedLeverBlock(p));
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final RegistryObject<ReinforcedPressurePlateBlock> REINFORCED_STONE_PRESSURE_PLATE = stonePressurePlate("reinforced_stone_pressure_plate", Blocks.STONE_PRESSURE_PLATE);
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final RegistryObject<ReinforcedPressurePlateBlock> REINFORCED_OAK_PRESSURE_PLATE = woodenPressurePlate("reinforced_oak_pressure_plate", Blocks.OAK_PRESSURE_PLATE);
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final RegistryObject<ReinforcedPressurePlateBlock> REINFORCED_SPRUCE_PRESSURE_PLATE = woodenPressurePlate("reinforced_spruce_pressure_plate", Blocks.SPRUCE_PRESSURE_PLATE);
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final RegistryObject<ReinforcedPressurePlateBlock> REINFORCED_BIRCH_PRESSURE_PLATE = woodenPressurePlate("reinforced_birch_pressure_plate", Blocks.BIRCH_PRESSURE_PLATE);
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final RegistryObject<ReinforcedPressurePlateBlock> REINFORCED_JUNGLE_PRESSURE_PLATE = woodenPressurePlate("reinforced_jungle_pressure_plate", Blocks.JUNGLE_PRESSURE_PLATE);
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final RegistryObject<ReinforcedPressurePlateBlock> REINFORCED_ACACIA_PRESSURE_PLATE = woodenPressurePlate("reinforced_acacia_pressure_plate", Blocks.ACACIA_PRESSURE_PLATE);
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final RegistryObject<ReinforcedPressurePlateBlock> REINFORCED_DARK_OAK_PRESSURE_PLATE = woodenPressurePlate("reinforced_dark_oak_pressure_plate", Blocks.DARK_OAK_PRESSURE_PLATE);
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final RegistryObject<ReinforcedPressurePlateBlock> REINFORCED_CRIMSON_PRESSURE_PLATE = woodenPressurePlate("reinforced_crimson_pressure_plate", Blocks.CRIMSON_PRESSURE_PLATE);
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final RegistryObject<ReinforcedPressurePlateBlock> REINFORCED_WARPED_PRESSURE_PLATE = woodenPressurePlate("reinforced_warped_pressure_plate", Blocks.WARPED_PRESSURE_PLATE);
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final RegistryObject<ReinforcedPressurePlateBlock> REINFORCED_POLISHED_BLACKSTONE_PRESSURE_PLATE = stonePressurePlate("reinforced_polished_blackstone_pressure_plate", Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedRedstoneLampBlock> REINFORCED_REDSTONE_LAMP = reinforcedBlock("reinforced_redstone_lamp", Blocks.REDSTONE_LAMP, ReinforcedRedstoneLampBlock::new);
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final RegistryObject<ReinforcedButtonBlock> REINFORCED_STONE_BUTTON = stoneButton("reinforced_stone_button", Blocks.STONE_BUTTON);
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final RegistryObject<ReinforcedButtonBlock> REINFORCED_OAK_BUTTON = woodenButton("reinforced_oak_button", Blocks.OAK_BUTTON);
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final RegistryObject<ReinforcedButtonBlock> REINFORCED_SPRUCE_BUTTON = woodenButton("reinforced_spruce_button", Blocks.SPRUCE_BUTTON);
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final RegistryObject<ReinforcedButtonBlock> REINFORCED_BIRCH_BUTTON = woodenButton("reinforced_birch_button", Blocks.BIRCH_BUTTON);
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final RegistryObject<ReinforcedButtonBlock> REINFORCED_JUNGLE_BUTTON = woodenButton("reinforced_jungle_button", Blocks.JUNGLE_BUTTON);
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final RegistryObject<ReinforcedButtonBlock> REINFORCED_ACACIA_BUTTON = woodenButton("reinforced_acacia_button", Blocks.ACACIA_BUTTON);
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final RegistryObject<ReinforcedButtonBlock> REINFORCED_DARK_OAK_BUTTON = woodenButton("reinforced_dark_oak_button", Blocks.DARK_OAK_BUTTON);
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final RegistryObject<ReinforcedButtonBlock> REINFORCED_CRIMSON_BUTTON = woodenButton("reinforced_crimson_button", Blocks.CRIMSON_BUTTON);
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final RegistryObject<ReinforcedButtonBlock> REINFORCED_WARPED_BUTTON = woodenButton("reinforced_warped_button", Blocks.WARPED_BUTTON);
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final RegistryObject<ReinforcedButtonBlock> REINFORCED_POLISHED_BLACKSTONE_BUTTON = stoneButton("reinforced_polished_blackstone_button", Blocks.POLISHED_BLACKSTONE_BUTTON);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedRedstoneBlock> REINFORCED_REDSTONE_BLOCK = reinforcedBlock("reinforced_redstone_block", Blocks.REDSTONE_BLOCK, ReinforcedRedstoneBlock::new);
	@HasManualPage
	@Reinforced
	public static final RegistryObject<ReinforcedHopperBlock> REINFORCED_HOPPER = reinforcedBlock("reinforced_hopper", Blocks.HOPPER, (p, b) -> new ReinforcedHopperBlock(propDisguisable(p)));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final RegistryObject<ReinforcedDropperBlock> REINFORCED_DROPPER = reinforcedBlock("reinforced_dropper", Blocks.DROPPER, (p, b) -> new ReinforcedDropperBlock(propDisguisable(p)));
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final RegistryObject<ReinforcedFenceGateBlock> REINFORCED_OAK_FENCE_GATE = reinforcedFenceGateBlock("reinforced_oak_fence_gate", Blocks.OAK_FENCE_GATE);
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final RegistryObject<ReinforcedFenceGateBlock> REINFORCED_SPRUCE_FENCE_GATE = reinforcedFenceGateBlock("reinforced_spruce_fence_gate", Blocks.SPRUCE_FENCE_GATE);
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final RegistryObject<ReinforcedFenceGateBlock> REINFORCED_BIRCH_FENCE_GATE = reinforcedFenceGateBlock("reinforced_birch_fence_gate", Blocks.BIRCH_FENCE_GATE);
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final RegistryObject<ReinforcedFenceGateBlock> REINFORCED_JUNGLE_FENCE_GATE = reinforcedFenceGateBlock("reinforced_jungle_fence_gate", Blocks.JUNGLE_FENCE_GATE);
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final RegistryObject<ReinforcedFenceGateBlock> REINFORCED_ACACIA_FENCE_GATE = reinforcedFenceGateBlock("reinforced_acacia_fence_gate", Blocks.ACACIA_FENCE_GATE);
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final RegistryObject<ReinforcedFenceGateBlock> REINFORCED_DARK_OAK_FENCE_GATE = reinforcedFenceGateBlock("reinforced_dark_oak_fence_gate", Blocks.DARK_OAK_FENCE_GATE);
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final RegistryObject<ReinforcedFenceGateBlock> REINFORCED_CRIMSON_FENCE_GATE = reinforcedFenceGateBlock("reinforced_crimson_fence_gate", Blocks.CRIMSON_FENCE_GATE);
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final RegistryObject<ReinforcedFenceGateBlock> REINFORCED_WARPED_FENCE_GATE = reinforcedFenceGateBlock("reinforced_warped_fence_gate", Blocks.WARPED_FENCE_GATE);
	@HasManualPage(hasRecipeDescription = true)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<ReinforcedIronTrapDoorBlock> REINFORCED_IRON_TRAPDOOR = reinforcedBlock("reinforced_iron_trapdoor", Blocks.IRON_TRAPDOOR, (p, b) -> new ReinforcedIronTrapDoorBlock(p));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedObserverBlock> REINFORCED_OBSERVER = reinforcedBlock("reinforced_observer", Blocks.OBSERVER, (p, b) -> new ReinforcedObserverBlock(propDisguisable(p)));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final RegistryObject<ReinforcedLecternBlock> REINFORCED_LECTERN = reinforcedBlock("reinforced_lectern", Blocks.LECTERN, (p, b) -> new ReinforcedLecternBlock(p));

	//ordered by vanilla brewing tab order
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final RegistryObject<ReinforcedCauldronBlock> REINFORCED_CAULDRON = reinforcedBlock("reinforced_cauldron", Blocks.CAULDRON, (p, b) -> new ReinforcedCauldronBlock(p));

	//misc
	@RegisterItemBlock(SCItemGroup.DECORATION)
	public static final RegistryObject<SlabBlock> CRYSTAL_QUARTZ_SLAB = registerBlock("crystal_quartz_slab", SlabBlock::new, ofFullCopy(Blocks.QUARTZ_SLAB), p -> color(p, MaterialColor.COLOR_CYAN).harvestTool(ToolType.PICKAXE));
	@RegisterItemBlock(SCItemGroup.DECORATION)
	public static final RegistryObject<Block> SMOOTH_CRYSTAL_QUARTZ = registerBlock("smooth_crystal_quartz", Block::new, ofFullCopy(Blocks.SMOOTH_QUARTZ), p -> color(p, MaterialColor.COLOR_CYAN).harvestTool(ToolType.PICKAXE));
	@RegisterItemBlock(SCItemGroup.DECORATION)
	public static final RegistryObject<Block> CHISELED_CRYSTAL_QUARTZ = registerBlock("chiseled_crystal_quartz", Block::new, ofFullCopy(Blocks.CHISELED_QUARTZ_BLOCK), p -> color(p, MaterialColor.COLOR_CYAN).harvestTool(ToolType.PICKAXE));
	@HasManualPage
	@RegisterItemBlock(SCItemGroup.DECORATION)
	public static final RegistryObject<Block> CRYSTAL_QUARTZ_BLOCK = registerBlock("crystal_quartz", Block::new, ofFullCopy(Blocks.QUARTZ_BLOCK), p -> color(p, MaterialColor.COLOR_CYAN).harvestTool(ToolType.PICKAXE));
	@RegisterItemBlock(SCItemGroup.DECORATION)
	public static final RegistryObject<Block> CRYSTAL_QUARTZ_BRICKS = registerBlock("crystal_quartz_bricks", Block::new, ofFullCopy(Blocks.QUARTZ_BRICKS), p -> color(p, MaterialColor.COLOR_CYAN).harvestTool(ToolType.PICKAXE));
	@RegisterItemBlock(SCItemGroup.DECORATION)
	public static final RegistryObject<RotatedPillarBlock> CRYSTAL_QUARTZ_PILLAR = registerBlock("crystal_quartz_pillar", RotatedPillarBlock::new, ofFullCopy(Blocks.QUARTZ_PILLAR), p -> color(p, MaterialColor.COLOR_CYAN).harvestTool(ToolType.PICKAXE));
	@RegisterItemBlock(SCItemGroup.DECORATION)
	public static final RegistryObject<StairsBlock> CRYSTAL_QUARTZ_STAIRS = registerBlock("crystal_quartz_stairs", p -> new StairsBlock(() -> CRYSTAL_QUARTZ_BLOCK.get().defaultBlockState(), p), ofFullCopy(Blocks.QUARTZ_STAIRS), p -> color(p, MaterialColor.COLOR_CYAN).harvestTool(ToolType.PICKAXE));
	@RegisterItemBlock(SCItemGroup.DECORATION)
	public static final RegistryObject<StairsBlock> SMOOTH_CRYSTAL_QUARTZ_STAIRS = registerBlock("smooth_crystal_quartz_stairs", p -> new StairsBlock(() -> SMOOTH_CRYSTAL_QUARTZ.get().defaultBlockState(), p), ofFullCopy(Blocks.SMOOTH_QUARTZ_STAIRS), p -> color(p, MaterialColor.COLOR_CYAN).harvestTool(ToolType.PICKAXE));
	@RegisterItemBlock(SCItemGroup.DECORATION)
	public static final RegistryObject<SlabBlock> SMOOTH_CRYSTAL_QUARTZ_SLAB = registerBlock("smooth_crystal_quartz_slab", SlabBlock::new, ofFullCopy(Blocks.SMOOTH_QUARTZ_SLAB), p -> color(p, MaterialColor.COLOR_CYAN).harvestTool(ToolType.PICKAXE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(customTint = 0x15B3A2)
	public static final RegistryObject<ReinforcedSlabBlock> REINFORCED_CRYSTAL_QUARTZ_SLAB = registerBlock("reinforced_crystal_quartz_slab", p -> new ReinforcedSlabBlock(p, SCContent.CRYSTAL_QUARTZ_SLAB::get), reinforcedCopy(Blocks.QUARTZ_SLAB), p -> color(p, MaterialColor.COLOR_CYAN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(customTint = 0x15B3A2)
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_SMOOTH_CRYSTAL_QUARTZ = registerBlock("reinforced_smooth_crystal_quartz", p -> new BaseReinforcedBlock(p, SCContent.SMOOTH_CRYSTAL_QUARTZ), reinforcedCopy(Blocks.SMOOTH_QUARTZ), p -> color(p, MaterialColor.COLOR_CYAN));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced(customTint = 0x15B3A2)
	public static final RegistryObject<BlockPocketBlock> REINFORCED_CHISELED_CRYSTAL_QUARTZ = registerBlock("reinforced_chiseled_crystal_quartz_block", p -> new BlockPocketBlock(p, SCContent.CHISELED_CRYSTAL_QUARTZ), reinforcedCopy(Blocks.CHISELED_QUARTZ_BLOCK), p -> color(p, MaterialColor.COLOR_CYAN));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced(customTint = 0x15B3A2)
	public static final RegistryObject<BlockPocketBlock> REINFORCED_CRYSTAL_QUARTZ_BLOCK = registerBlock("reinforced_crystal_quartz_block", p -> new BlockPocketBlock(p, SCContent.CRYSTAL_QUARTZ_BLOCK), reinforcedCopy(Blocks.QUARTZ_BLOCK), p -> color(p, MaterialColor.COLOR_CYAN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(customTint = 0x15B3A2)
	public static final RegistryObject<BaseReinforcedBlock> REINFORCED_CRYSTAL_QUARTZ_BRICKS = registerBlock("reinforced_crystal_quartz_bricks", p -> new BaseReinforcedBlock(p, SCContent.CRYSTAL_QUARTZ_BRICKS), reinforcedCopy(Blocks.QUARTZ_BRICKS), p -> color(p, MaterialColor.COLOR_CYAN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(customTint = 0x15B3A2)
	public static final RegistryObject<ReinforcedRotatedCrystalQuartzPillar> REINFORCED_CRYSTAL_QUARTZ_PILLAR = registerBlock("reinforced_crystal_quartz_pillar", p -> new ReinforcedRotatedCrystalQuartzPillar(p, SCContent.CRYSTAL_QUARTZ_PILLAR::get), reinforcedCopy(Blocks.QUARTZ_PILLAR), p -> color(p, MaterialColor.COLOR_CYAN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(customTint = 0x15B3A2)
	public static final RegistryObject<ReinforcedStairsBlock> REINFORCED_CRYSTAL_QUARTZ_STAIRS = registerBlock("reinforced_crystal_quartz_stairs", p -> new ReinforcedStairsBlock(p, SCContent.CRYSTAL_QUARTZ_STAIRS::get), reinforcedCopy(Blocks.QUARTZ_STAIRS), p -> color(p, MaterialColor.COLOR_CYAN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(customTint = 0x15B3A2)
	public static final RegistryObject<ReinforcedStairsBlock> REINFORCED_SMOOTH_CRYSTAL_QUARTZ_STAIRS = registerBlock("reinforced_smooth_crystal_quartz_stairs", p -> new ReinforcedStairsBlock(p, SCContent.SMOOTH_CRYSTAL_QUARTZ_STAIRS::get), reinforcedCopy(Blocks.SMOOTH_QUARTZ_STAIRS), p -> color(p, MaterialColor.COLOR_CYAN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(customTint = 0x15B3A2)
	public static final RegistryObject<ReinforcedSlabBlock> REINFORCED_SMOOTH_CRYSTAL_QUARTZ_SLAB = registerBlock("reinforced_smooth_crystal_quartz_slab", p -> new ReinforcedSlabBlock(p, SCContent.SMOOTH_CRYSTAL_QUARTZ_SLAB::get), reinforcedCopy(Blocks.SMOOTH_QUARTZ_SLAB), p -> color(p, MaterialColor.COLOR_CYAN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	public static final RegistryObject<HorizontalReinforcedIronBars> HORIZONTAL_REINFORCED_IRON_BARS = registerBlock("horizontal_reinforced_iron_bars", HorizontalReinforcedIronBars::new, reinforcedCopy(Blocks.IRON_BARS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedGrassPathBlock> REINFORCED_GRASS_PATH = reinforcedBlock("reinforced_grass_path", Blocks.GRASS_PATH, ReinforcedGrassPathBlock::new);
	@OwnableBE
	public static final RegistryObject<ReinforcedMovingPistonBlock> REINFORCED_MOVING_PISTON = reinforcedBlock("reinforced_moving_piston", Blocks.MOVING_PISTON, (p, b) -> new ReinforcedMovingPistonBlock(p));
	@OwnableBE
	@Reinforced(registerBlockItem = false)
	public static final RegistryObject<ReinforcedPistonHeadBlock> REINFORCED_PISTON_HEAD = reinforcedBlock("reinforced_piston_head", Blocks.PISTON_HEAD, (p, b) -> new ReinforcedPistonHeadBlock(p));
	public static final RegistryObject<SometimesVisibleBlock> SENTRY_DISGUISE = registerBlock("sentry_disguise", SometimesVisibleBlock::new, propDisguisable(-1.0F));

	//items
	@HasManualPage(hasRecipeDescription = true)
	public static final RegistryObject<Item> ADMIN_TOOL = ITEMS.register("admin_tool", () -> new AdminToolItem(itemProp(SecurityCraft.TECHNICAL_TAB, 1)));
	public static final RegistryObject<Item> ANCIENT_DEBRIS_MINE_ITEM = ITEMS.register("ancient_debris_mine", () -> new BlockItem(SCContent.ANCIENT_DEBRIS_MINE.get(), itemProp(SecurityCraft.MINE_TAB).fireResistant()));
	@HasManualPage
	public static final RegistryObject<Item> BRIEFCASE = ITEMS.register("briefcase", () -> new BriefcaseItem(itemProp(SecurityCraft.TECHNICAL_TAB, 1)));
	@HasManualPage
	public static final RegistryObject<Item> CAMERA_MONITOR = ITEMS.register("camera_monitor", () -> new CameraMonitorItem(itemProp(SecurityCraft.TECHNICAL_TAB, 1)));
	@HasManualPage
	public static final RegistryObject<Item> CODEBREAKER = ITEMS.register("codebreaker", () -> new CodebreakerItem(itemProp(SecurityCraft.TECHNICAL_TAB).defaultDurability(5)));
	@HasManualPage
	public static final RegistryObject<Item> CRYSTAL_QUARTZ_ITEM = ITEMS.register("crystal_quartz_item", () -> new Item(itemProp(SecurityCraft.DECORATION_TAB)));
	public static final RegistryObject<Item> DISPLAY_CASE_ITEM = ITEMS.register(DISPLAY_CASE_PATH, () -> new BlockItem(SCContent.DISPLAY_CASE.get(), itemProp(SecurityCraft.DECORATION_TAB).setISTER(() -> DisplayCaseItemRenderer::new))); //keep this as a method reference or else the server will crash
	@HasManualPage(hasRecipeDescription = true)
	public static final RegistryObject<Item> FAKE_LAVA_BUCKET = ITEMS.register("bucket_f_lava", () -> new FakeLiquidBucketItem(SCContent.FAKE_LAVA, itemProp(SecurityCraft.TECHNICAL_TAB, 1)));
	@HasManualPage(hasRecipeDescription = true)
	public static final RegistryObject<Item> FAKE_WATER_BUCKET = ITEMS.register("bucket_f_water", () -> new FakeLiquidBucketItem(SCContent.FAKE_WATER, itemProp(SecurityCraft.TECHNICAL_TAB, 1)));
	@HasManualPage
	public static final RegistryObject<Item> INCOGNITO_MASK = ITEMS.register("incognito_mask", () -> new IncognitoMaskItem(itemProp(SecurityCraft.TECHNICAL_TAB, 1)));
	@HasManualPage
	public static final RegistryObject<Item> KEYCARD_HOLDER = ITEMS.register("keycard_holder", () -> new KeycardHolderItem(itemProp(SecurityCraft.TECHNICAL_TAB, 1)));
	@HasManualPage(PageGroup.KEYCARDS)
	public static final RegistryObject<Item> KEYCARD_LVL_1 = ITEMS.register("keycard_lv1", () -> new KeycardItem(itemProp(SecurityCraft.TECHNICAL_TAB), 0));
	@HasManualPage(PageGroup.KEYCARDS)
	public static final RegistryObject<Item> KEYCARD_LVL_2 = ITEMS.register("keycard_lv2", () -> new KeycardItem(itemProp(SecurityCraft.TECHNICAL_TAB), 1));
	@HasManualPage(PageGroup.KEYCARDS)
	public static final RegistryObject<Item> KEYCARD_LVL_3 = ITEMS.register("keycard_lv3", () -> new KeycardItem(itemProp(SecurityCraft.TECHNICAL_TAB), 2));
	@HasManualPage(PageGroup.KEYCARDS)
	public static final RegistryObject<Item> KEYCARD_LVL_4 = ITEMS.register("keycard_lv4", () -> new KeycardItem(itemProp(SecurityCraft.TECHNICAL_TAB), 3));
	@HasManualPage(PageGroup.KEYCARDS)
	public static final RegistryObject<Item> KEYCARD_LVL_5 = ITEMS.register("keycard_lv5", () -> new KeycardItem(itemProp(SecurityCraft.TECHNICAL_TAB), 4));
	@HasManualPage
	public static final RegistryObject<Item> KEY_PANEL = ITEMS.register("keypad_item", () -> new KeyPanelItem(itemProp(SecurityCraft.TECHNICAL_TAB)));
	public static final RegistryObject<Item> KEYPAD_CHEST_ITEM = ITEMS.register(KEYPAD_CHEST_PATH, () -> new BlockItem(SCContent.KEYPAD_CHEST.get(), itemProp(SecurityCraft.TECHNICAL_TAB).setISTER(() -> KeypadChestItemRenderer::new))); //keep this as a method reference or else the server will crash
	@HasManualPage
	public static final RegistryObject<Item> KEYPAD_DOOR_ITEM = ITEMS.register("keypad_door_item", () -> new TallBlockItem(KEYPAD_DOOR.get(), itemProp(SecurityCraft.DECORATION_TAB)));
	@HasManualPage
	public static final RegistryObject<LensItem> LENS = ITEMS.register("lens", () -> new LensItem(itemProp(SecurityCraft.TECHNICAL_TAB)));
	@HasManualPage
	public static final RegistryObject<Item> LIMITED_USE_KEYCARD = ITEMS.register("limited_use_keycard", () -> new KeycardItem(itemProp(SecurityCraft.TECHNICAL_TAB), -1));
	@HasManualPage
	public static final RegistryObject<Item> PORTABLE_TUNE_PLAYER = ITEMS.register("portable_tune_player", () -> new PortableTunePlayerItem(itemProp(SecurityCraft.TECHNICAL_TAB)));
	@HasManualPage
	public static final RegistryObject<Item> REINFORCED_DOOR_ITEM = ITEMS.register("door_indestructible_iron_item", () -> new TallBlockItem(REINFORCED_DOOR.get(), itemProp(SecurityCraft.DECORATION_TAB)));
	@HasManualPage
	public static final RegistryObject<Item> MINE_REMOTE_ACCESS_TOOL = ITEMS.register("remote_access_mine", () -> new MineRemoteAccessToolItem(itemProp(SecurityCraft.TECHNICAL_TAB, 1)));
	@HasManualPage
	public static final RegistryObject<Item> SENTRY_REMOTE_ACCESS_TOOL = ITEMS.register("remote_access_sentry", () -> new SentryRemoteAccessToolItem(itemProp(SecurityCraft.TECHNICAL_TAB, 1)));
	@HasManualPage
	public static final RegistryObject<Item> RIFT_STABILIZER_ITEM = ITEMS.register("rift_stabilizer", () -> new TallBlockItem(RIFT_STABILIZER.get(), itemProp(SecurityCraft.TECHNICAL_TAB)));
	@HasManualPage
	public static final RegistryObject<Item> SCANNER_DOOR_ITEM = ITEMS.register("scanner_door_item", () -> new TallBlockItem(SCANNER_DOOR.get(), itemProp(SecurityCraft.DECORATION_TAB)));
	@HasManualPage
	public static final RegistryObject<Item> SC_MANUAL = ITEMS.register("sc_manual", () -> new SCManualItem(itemProp(SecurityCraft.TECHNICAL_TAB, 1)));
	@HasManualPage(PageGroup.REINFORCED)
	public static final RegistryObject<Item> REINFORCED_SCAFFOLDING_ITEM = ITEMS.register("reinforced_scaffolding", () -> new ReinforcedScaffoldingBlockItem(itemProp(SecurityCraft.DECORATION_TAB)));
	@HasManualPage(PageGroup.SECRET_SIGNS)
	public static final RegistryObject<Item> SECRET_OAK_SIGN_ITEM = ITEMS.register("secret_sign_item", () -> new SecretSignItem(itemProp(SecurityCraft.DECORATION_TAB, 16), SCContent.SECRET_OAK_SIGN.get(), SCContent.SECRET_OAK_WALL_SIGN.get(), "item.securitycraft.secret_sign_item"));
	@HasManualPage(PageGroup.SECRET_SIGNS)
	public static final RegistryObject<Item> SECRET_SPRUCE_SIGN_ITEM = ITEMS.register("secret_spruce_sign_item", () -> new SecretSignItem(itemProp(SecurityCraft.DECORATION_TAB, 16), SCContent.SECRET_SPRUCE_SIGN.get(), SCContent.SECRET_SPRUCE_WALL_SIGN.get(), "item.securitycraft.secret_spruce_sign_item"));
	@HasManualPage(PageGroup.SECRET_SIGNS)
	public static final RegistryObject<Item> SECRET_BIRCH_SIGN_ITEM = ITEMS.register("secret_birch_sign_item", () -> new SecretSignItem(itemProp(SecurityCraft.DECORATION_TAB, 16), SCContent.SECRET_BIRCH_SIGN.get(), SCContent.SECRET_BIRCH_WALL_SIGN.get(), "item.securitycraft.secret_birch_sign_item"));
	@HasManualPage(PageGroup.SECRET_SIGNS)
	public static final RegistryObject<Item> SECRET_JUNGLE_SIGN_ITEM = ITEMS.register("secret_jungle_sign_item", () -> new SecretSignItem(itemProp(SecurityCraft.DECORATION_TAB, 16), SCContent.SECRET_JUNGLE_SIGN.get(), SCContent.SECRET_JUNGLE_WALL_SIGN.get(), "item.securitycraft.secret_jungle_sign_item"));
	@HasManualPage(PageGroup.SECRET_SIGNS)
	public static final RegistryObject<Item> SECRET_ACACIA_SIGN_ITEM = ITEMS.register("secret_acacia_sign_item", () -> new SecretSignItem(itemProp(SecurityCraft.DECORATION_TAB, 16), SCContent.SECRET_ACACIA_SIGN.get(), SCContent.SECRET_ACACIA_WALL_SIGN.get(), "item.securitycraft.secret_acacia_sign_item"));
	@HasManualPage(PageGroup.SECRET_SIGNS)
	public static final RegistryObject<Item> SECRET_DARK_OAK_SIGN_ITEM = ITEMS.register("secret_dark_oak_sign_item", () -> new SecretSignItem(itemProp(SecurityCraft.DECORATION_TAB, 16), SCContent.SECRET_DARK_OAK_SIGN.get(), SCContent.SECRET_DARK_OAK_WALL_SIGN.get(), "item.securitycraft.secret_dark_oak_sign_item"));
	@HasManualPage(PageGroup.SECRET_SIGNS)
	public static final RegistryObject<Item> SECRET_CRIMSON_SIGN_ITEM = ITEMS.register("secret_crimson_sign_item", () -> new SecretSignItem(itemProp(SecurityCraft.DECORATION_TAB, 16), SCContent.SECRET_CRIMSON_SIGN.get(), SCContent.SECRET_CRIMSON_WALL_SIGN.get(), "item.securitycraft.secret_crimson_sign_item"));
	@HasManualPage(PageGroup.SECRET_SIGNS)
	public static final RegistryObject<Item> SECRET_WARPED_SIGN_ITEM = ITEMS.register("secret_warped_sign_item", () -> new SecretSignItem(itemProp(SecurityCraft.DECORATION_TAB, 16), SCContent.SECRET_WARPED_SIGN.get(), SCContent.SECRET_WARPED_WALL_SIGN.get(), "item.securitycraft.secret_warped_sign_item"));
	public static final RegistryObject<Item> SECURITY_CAMERA_ITEM = ITEMS.register(SECURITY_CAMERA_PATH, () -> new BlockItem(SCContent.SECURITY_CAMERA.get(), itemProp(SecurityCraft.TECHNICAL_TAB).setISTER(() -> SecurityCameraItemRenderer::new))); //keep this as a method reference or else the server will crash
	@HasManualPage(designedBy = "Henzoid")
	public static final RegistryObject<Item> SENTRY = ITEMS.register("sentry", () -> new SentryItem(itemProp(SecurityCraft.TECHNICAL_TAB)));
	public static final RegistryObject<Item> SONIC_SECURITY_SYSTEM_ITEM = ITEMS.register("sonic_security_system", () -> new SonicSecuritySystemItem(itemProp(SecurityCraft.TECHNICAL_TAB, 1)));
	@HasManualPage
	public static final RegistryObject<Item> TASER = ITEMS.register("taser", () -> new TaserItem(itemProp(SecurityCraft.TECHNICAL_TAB).defaultDurability(151), false));
	public static final RegistryObject<Item> TASER_POWERED = ITEMS.register("taser_powered", () -> new TaserItem(itemProp(null).defaultDurability(151), true));
	@HasManualPage
	public static final RegistryObject<Item> UNIVERSAL_BLOCK_MODIFIER = ITEMS.register("universal_block_modifier", () -> new UniversalBlockModifierItem(itemProp(SecurityCraft.TECHNICAL_TAB, 1)));
	@HasManualPage(PageGroup.BLOCK_REINFORCERS)
	public static final RegistryObject<Item> UNIVERSAL_BLOCK_REINFORCER_LVL_1 = ITEMS.register("universal_block_reinforcer_lvl1", () -> new UniversalBlockReinforcerItem(itemProp(SecurityCraft.TECHNICAL_TAB).defaultDurability(300)));
	@HasManualPage(PageGroup.BLOCK_REINFORCERS)
	public static final RegistryObject<Item> UNIVERSAL_BLOCK_REINFORCER_LVL_2 = ITEMS.register("universal_block_reinforcer_lvl2", () -> new UniversalBlockReinforcerItem(itemProp(SecurityCraft.TECHNICAL_TAB).defaultDurability(2700)));
	@HasManualPage(PageGroup.BLOCK_REINFORCERS)
	public static final RegistryObject<Item> UNIVERSAL_BLOCK_REINFORCER_LVL_3 = ITEMS.register("universal_block_reinforcer_lvl3", () -> new UniversalBlockReinforcerItem(itemProp(SecurityCraft.TECHNICAL_TAB, 1)));
	@HasManualPage
	public static final RegistryObject<Item> UNIVERSAL_BLOCK_REMOVER = ITEMS.register("universal_block_remover", () -> new UniversalBlockRemoverItem(itemProp(SecurityCraft.TECHNICAL_TAB).defaultDurability(476)));
	@HasManualPage
	public static final RegistryObject<Item> UNIVERSAL_KEY_CHANGER = ITEMS.register("universal_key_changer", () -> new UniversalKeyChangerItem(itemProp(SecurityCraft.TECHNICAL_TAB, 1)));
	@HasManualPage
	public static final RegistryObject<Item> UNIVERSAL_OWNER_CHANGER = ITEMS.register("universal_owner_changer", () -> new UniversalOwnerChangerItem(itemProp(SecurityCraft.TECHNICAL_TAB).defaultDurability(48)));
	@HasManualPage
	public static final RegistryObject<Item> WIRE_CUTTERS = ITEMS.register("wire_cutters", () -> new WireCuttersItem(itemProp(SecurityCraft.TECHNICAL_TAB).defaultDurability(476)));

	//modules
	@HasManualPage
	public static final RegistryObject<ModuleItem> DENYLIST_MODULE = ITEMS.register("blacklist_module", () -> new ModuleItem(itemProp(SecurityCraft.TECHNICAL_TAB, 1), ModuleType.DENYLIST, true, true));
	@HasManualPage
	public static final RegistryObject<ModuleItem> DISGUISE_MODULE = ITEMS.register("disguise_module", () -> new ModuleItem(itemProp(SecurityCraft.TECHNICAL_TAB, 1), ModuleType.DISGUISE, false, true));
	@HasManualPage
	public static final RegistryObject<ModuleItem> HARMING_MODULE = ITEMS.register("harming_module", () -> new ModuleItem(itemProp(SecurityCraft.TECHNICAL_TAB, 1), ModuleType.HARMING, false));
	@HasManualPage
	public static final RegistryObject<ModuleItem> REDSTONE_MODULE = ITEMS.register("redstone_module", () -> new ModuleItem(itemProp(SecurityCraft.TECHNICAL_TAB, 1), ModuleType.REDSTONE, false));
	@HasManualPage
	public static final RegistryObject<ModuleItem> SMART_MODULE = ITEMS.register("smart_module", () -> new ModuleItem(itemProp(SecurityCraft.TECHNICAL_TAB, 1), ModuleType.SMART, false));
	@HasManualPage
	public static final RegistryObject<ModuleItem> STORAGE_MODULE = ITEMS.register("storage_module", () -> new ModuleItem(itemProp(SecurityCraft.TECHNICAL_TAB, 1), ModuleType.STORAGE, false));
	@HasManualPage
	public static final RegistryObject<ModuleItem> ALLOWLIST_MODULE = ITEMS.register("whitelist_module", () -> new ModuleItem(itemProp(SecurityCraft.TECHNICAL_TAB, 1), ModuleType.ALLOWLIST, true, true));
	@HasManualPage
	public static final RegistryObject<ModuleItem> SPEED_MODULE = ITEMS.register("speed_module", () -> new ModuleItem(itemProp(SecurityCraft.TECHNICAL_TAB, 1), ModuleType.SPEED, false));

	//tile entity types
	//@formatter:off
	public static final RegistryObject<TileEntityType<OwnableBlockEntity>> OWNABLE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("ownable", () -> {
		List<Block> teOwnableBlocks = new ArrayList<>();

		//find all blocks whose tile entity is TileEntityOwnable
		for (Field field : SCContent.class.getFields()) {
			try {
				if (field.isAnnotationPresent(OwnableBE.class))
					teOwnableBlocks.add(((RegistryObject<Block>) field.get(null)).get());
			}
			catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		return TileEntityType.Builder.of(OwnableBlockEntity::new, teOwnableBlocks.toArray(new Block[teOwnableBlocks.size()])).build(null);
	});
	public static final RegistryObject<TileEntityType<NamedBlockEntity>> ABSTRACT_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("abstract", () -> TileEntityType.Builder.of(NamedBlockEntity::new,
			SCContent.LASER_FIELD.get(),
			SCContent.INVENTORY_SCANNER_FIELD.get(),
			SCContent.ELECTRIFIED_IRON_FENCE.get(),
			SCContent.COBBLESTONE_MINE.get(),
			SCContent.DIAMOND_ORE_MINE.get(),
			SCContent.DIRT_MINE.get(),
			SCContent.GRAVEL_MINE.get(),
			SCContent.SAND_MINE.get(),
			SCContent.STONE_MINE.get(),
			SCContent.BOUNCING_BETTY.get(),
			SCContent.ELECTRIFIED_IRON_FENCE_GATE.get(),
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
			SCContent.NETHERRACK_MINE.get(),
			SCContent.END_STONE_MINE.get()).build(null));
	public static final RegistryObject<TileEntityType<KeypadBlockEntity>> KEYPAD_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("keypad", () -> TileEntityType.Builder.of(KeypadBlockEntity::new, SCContent.KEYPAD.get()).build(null));
	public static final RegistryObject<TileEntityType<LaserBlockBlockEntity>> LASER_BLOCK_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("laser_block", () -> TileEntityType.Builder.of(LaserBlockBlockEntity::new, SCContent.LASER_BLOCK.get()).build(null));
	public static final RegistryObject<TileEntityType<CageTrapBlockEntity>> CAGE_TRAP_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("cage_trap", () -> TileEntityType.Builder.of(CageTrapBlockEntity::new, SCContent.CAGE_TRAP.get()).build(null));
	public static final RegistryObject<TileEntityType<KeycardReaderBlockEntity>> KEYCARD_READER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("keycard_reader", () -> TileEntityType.Builder.of(KeycardReaderBlockEntity::new, SCContent.KEYCARD_READER.get()).build(null));
	public static final RegistryObject<TileEntityType<InventoryScannerBlockEntity>> INVENTORY_SCANNER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("inventory_scanner", () -> TileEntityType.Builder.of(InventoryScannerBlockEntity::new, SCContent.INVENTORY_SCANNER.get()).build(null));
	public static final RegistryObject<TileEntityType<PortableRadarBlockEntity>> PORTABLE_RADAR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("portable_radar", () -> TileEntityType.Builder.of(PortableRadarBlockEntity::new, SCContent.PORTABLE_RADAR.get()).build(null));
	public static final RegistryObject<TileEntityType<SecurityCameraBlockEntity>> SECURITY_CAMERA_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("security_camera", () -> TileEntityType.Builder.of(SecurityCameraBlockEntity::new, SCContent.SECURITY_CAMERA.get()).build(null));
	public static final RegistryObject<TileEntityType<UsernameLoggerBlockEntity>> USERNAME_LOGGER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("username_logger", () -> TileEntityType.Builder.of(UsernameLoggerBlockEntity::new, SCContent.USERNAME_LOGGER.get()).build(null));
	public static final RegistryObject<TileEntityType<RetinalScannerBlockEntity>> RETINAL_SCANNER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("retinal_scanner", () -> TileEntityType.Builder.of(RetinalScannerBlockEntity::new, SCContent.RETINAL_SCANNER.get()).build(null));
	public static final RegistryObject<TileEntityType<KeypadChestBlockEntity>> KEYPAD_CHEST_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(KEYPAD_CHEST_PATH, () -> TileEntityType.Builder.of(KeypadChestBlockEntity::new, SCContent.KEYPAD_CHEST.get()).build(null));
	public static final RegistryObject<TileEntityType<AlarmBlockEntity>> ALARM_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("alarm", () -> TileEntityType.Builder.of(AlarmBlockEntity::new, SCContent.ALARM.get()).build(null));
	public static final RegistryObject<TileEntityType<ClaymoreBlockEntity>> CLAYMORE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("claymore", () -> TileEntityType.Builder.of(ClaymoreBlockEntity::new, SCContent.CLAYMORE.get()).build(null));
	public static final RegistryObject<TileEntityType<KeypadFurnaceBlockEntity>> KEYPAD_FURNACE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("keypad_furnace", () -> TileEntityType.Builder.of(KeypadFurnaceBlockEntity::new, SCContent.KEYPAD_FURNACE.get()).build(null));
	public static final RegistryObject<TileEntityType<KeypadSmokerBlockEntity>> KEYPAD_SMOKER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("keypad_smoker", () -> TileEntityType.Builder.of(KeypadSmokerBlockEntity::new, SCContent.KEYPAD_SMOKER.get()).build(null));
	public static final RegistryObject<TileEntityType<KeypadBlastFurnaceBlockEntity>> KEYPAD_BLAST_FURNACE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("keypad_blast_furnace", () -> TileEntityType.Builder.of(KeypadBlastFurnaceBlockEntity::new, SCContent.KEYPAD_BLAST_FURNACE.get()).build(null));
	public static final RegistryObject<TileEntityType<IMSBlockEntity>> IMS_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("ims", () -> TileEntityType.Builder.of(IMSBlockEntity::new, SCContent.IMS.get()).build(null));
	public static final RegistryObject<TileEntityType<ProtectoBlockEntity>> PROTECTO_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("protecto", () -> TileEntityType.Builder.of(ProtectoBlockEntity::new, SCContent.PROTECTO.get()).build(null));
	public static final RegistryObject<TileEntityType<ScannerDoorBlockEntity>> SCANNER_DOOR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("scanner_door", () -> TileEntityType.Builder.of(ScannerDoorBlockEntity::new, SCContent.SCANNER_DOOR.get()).build(null));
	public static final RegistryObject<TileEntityType<SecretSignBlockEntity>> SECRET_SIGN_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("secret_sign", () -> TileEntityType.Builder.of(SecretSignBlockEntity::new,
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
			SCContent.SECRET_CRIMSON_SIGN.get(),
			SCContent.SECRET_CRIMSON_WALL_SIGN.get(),
			SCContent.SECRET_WARPED_SIGN.get(),
			SCContent.SECRET_WARPED_WALL_SIGN.get()).build(null));
	public static final RegistryObject<TileEntityType<MotionActivatedLightBlockEntity>> MOTION_LIGHT_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("motion_light", () -> TileEntityType.Builder.of(MotionActivatedLightBlockEntity::new, SCContent.MOTION_ACTIVATED_LIGHT.get()).build(null));
	public static final RegistryObject<TileEntityType<TrackMineBlockEntity>> TRACK_MINE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("track_mine", () -> TileEntityType.Builder.of(TrackMineBlockEntity::new, SCContent.TRACK_MINE.get()).build(null));
	public static final RegistryObject<TileEntityType<TrophySystemBlockEntity>> TROPHY_SYSTEM_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("trophy_system", () -> TileEntityType.Builder.of(TrophySystemBlockEntity::new, SCContent.TROPHY_SYSTEM.get()).build(null));
	public static final RegistryObject<TileEntityType<BlockPocketManagerBlockEntity>> BLOCK_POCKET_MANAGER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("block_pocket_manager", () -> TileEntityType.Builder.of(BlockPocketManagerBlockEntity::new, SCContent.BLOCK_POCKET_MANAGER.get()).build(null));
	public static final RegistryObject<TileEntityType<BlockPocketBlockEntity>> BLOCK_POCKET_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("block_pocket", () -> TileEntityType.Builder.of(BlockPocketBlockEntity::new,
			SCContent.BLOCK_POCKET_WALL.get(),
			SCContent.REINFORCED_CRYSTAL_QUARTZ_BLOCK.get(),
			SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get(),
			SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get()).build(null));
	public static final RegistryObject<TileEntityType<AllowlistOnlyBlockEntity>> ALLOWLIST_ONLY_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("reinforced_pressure_plate", () -> TileEntityType.Builder.of(AllowlistOnlyBlockEntity::new,
			SCContent.REINFORCED_STONE_PRESSURE_PLATE.get(),
			SCContent.REINFORCED_ACACIA_PRESSURE_PLATE.get(),
			SCContent.REINFORCED_BIRCH_PRESSURE_PLATE.get(),
			SCContent.REINFORCED_CRIMSON_PRESSURE_PLATE.get(),
			SCContent.REINFORCED_DARK_OAK_PRESSURE_PLATE.get(),
			SCContent.REINFORCED_JUNGLE_PRESSURE_PLATE.get(),
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
			SCContent.REINFORCED_OAK_BUTTON.get(),
			SCContent.REINFORCED_SPRUCE_BUTTON.get(),
			SCContent.REINFORCED_WARPED_BUTTON.get(),
			SCContent.REINFORCED_POLISHED_BLACKSTONE_BUTTON.get(),
			SCContent.REINFORCED_LEVER.get()).build(null));
	public static final RegistryObject<TileEntityType<ReinforcedHopperBlockEntity>> REINFORCED_HOPPER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("reinforced_hopper", () -> TileEntityType.Builder.of(ReinforcedHopperBlockEntity::new, SCContent.REINFORCED_HOPPER.get()).build(null));
	public static final RegistryObject<TileEntityType<ProjectorBlockEntity>> PROJECTOR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("projector", () -> TileEntityType.Builder.of(ProjectorBlockEntity::new, SCContent.PROJECTOR.get()).build(null));
	public static final RegistryObject<TileEntityType<KeypadDoorBlockEntity>> KEYPAD_DOOR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("keypad_door", () -> TileEntityType.Builder.of(KeypadDoorBlockEntity::new, SCContent.KEYPAD_DOOR.get()).build(null));
	public static final RegistryObject<TileEntityType<ReinforcedIronBarsBlockEntity>> REINFORCED_IRON_BARS_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("reinforced_iron_bars", () -> TileEntityType.Builder.of(ReinforcedIronBarsBlockEntity::new, SCContent.REINFORCED_IRON_BARS.get()).build(null));
	public static final RegistryObject<TileEntityType<ReinforcedCauldronBlockEntity>> REINFORCED_CAULDRON_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("reinforced_cauldron", () -> TileEntityType.Builder.of(ReinforcedCauldronBlockEntity::new, SCContent.REINFORCED_CAULDRON.get()).build(null));
	public static final RegistryObject<TileEntityType<ReinforcedPistonBlockEntity>> REINFORCED_PISTON_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("reinforced_piston", () -> TileEntityType.Builder.of(ReinforcedPistonBlockEntity::new, SCContent.REINFORCED_MOVING_PISTON.get()).build(null));
	public static final RegistryObject<TileEntityType<ValidationOwnableBlockEntity>> VALIDATION_OWNABLE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("validation_ownable", () -> TileEntityType.Builder.of(ValidationOwnableBlockEntity::new,
			SCContent.REINFORCED_PISTON.get(),
			SCContent.REINFORCED_STICKY_PISTON.get()).build(null));
	public static final RegistryObject<TileEntityType<KeyPanelBlockEntity>> KEY_PANEL_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("key_panel", () -> TileEntityType.Builder.of(KeyPanelBlockEntity::new, SCContent.KEY_PANEL_BLOCK.get()).build(null));
	public static final RegistryObject<TileEntityType<SonicSecuritySystemBlockEntity>> SONIC_SECURITY_SYSTEM_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("sonic_security_system", () -> TileEntityType.Builder.of(SonicSecuritySystemBlockEntity::new, SCContent.SONIC_SECURITY_SYSTEM.get()).build(null));
	public static final RegistryObject<TileEntityType<BlockChangeDetectorBlockEntity>> BLOCK_CHANGE_DETECTOR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("block_change_detector", () -> TileEntityType.Builder.of(BlockChangeDetectorBlockEntity::new, SCContent.BLOCK_CHANGE_DETECTOR.get()).build(null));
	public static final RegistryObject<TileEntityType<RiftStabilizerBlockEntity>> RIFT_STABILIZER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("rift_stabilizer", () -> TileEntityType.Builder.of(RiftStabilizerBlockEntity::new, SCContent.RIFT_STABILIZER.get()).build(null));
	public static final RegistryObject<TileEntityType<DisguisableBlockEntity>> DISGUISABLE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("disguisable", () -> TileEntityType.Builder.of(DisguisableBlockEntity::new, SCContent.SENTRY_DISGUISE.get()).build(null));
	public static final RegistryObject<TileEntityType<DisplayCaseBlockEntity>> DISPLAY_CASE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(DISPLAY_CASE_PATH, () -> TileEntityType.Builder.of(DisplayCaseBlockEntity::new, SCContent.DISPLAY_CASE.get()).build(null));
	public static final RegistryObject<TileEntityType<KeypadBarrelBlockEntity>> KEYPAD_BARREL_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("keypad_barrel", () -> TileEntityType.Builder.of(KeypadBarrelBlockEntity::new, SCContent.KEYPAD_BARREL.get()).build(null));
	public static final RegistryObject<TileEntityType<KeypadTrapdoorBlockEntity>> KEYPAD_TRAPDOOR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("keypad_trapdoor", () -> TileEntityType.Builder.of(KeypadTrapdoorBlockEntity::new, SCContent.KEYPAD_TRAPDOOR.get()).build(null));
	public static final RegistryObject<TileEntityType<FloorTrapBlockEntity>> FLOOR_TRAP_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("floor_trap", () -> TileEntityType.Builder.of(FloorTrapBlockEntity::new, SCContent.FLOOR_TRAP.get()).build(null));
	public static final RegistryObject<TileEntityType<KeycardLockBlockEntity>> KEYCARD_LOCK_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("keycard_lock", () -> TileEntityType.Builder.of(KeycardLockBlockEntity::new, SCContent.KEYCARD_LOCK.get()).build(null));
	public static final RegistryObject<TileEntityType<ScannerTrapdoorBlockEntity>> SCANNER_TRAPDOOR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("scanner_trapdoor", () -> TileEntityType.Builder.of(ScannerTrapdoorBlockEntity::new, SCContent.SCANNER_TRAPDOOR.get()).build(null));
	public static final RegistryObject<TileEntityType<ReinforcedDispenserBlockEntity>> REINFORCED_DISPENSER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("reinforced_dispenser", () -> TileEntityType.Builder.of(ReinforcedDispenserBlockEntity::new, SCContent.REINFORCED_DISPENSER.get()).build(null));
	public static final RegistryObject<TileEntityType<ReinforcedDropperBlockEntity>> REINFORCED_DROPPER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("reinforced_dropper", () -> TileEntityType.Builder.of(ReinforcedDropperBlockEntity::new, SCContent.REINFORCED_DROPPER.get()).build(null));
	public static final RegistryObject<TileEntityType<ReinforcedFenceGateBlockEntity>> REINFORCED_FENCE_GATE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("reinforced_fence_gate", () -> TileEntityType.Builder.of(ReinforcedFenceGateBlockEntity::new,
			SCContent.REINFORCED_OAK_FENCE_GATE.get(),
			SCContent.REINFORCED_SPRUCE_FENCE_GATE.get(),
			SCContent.REINFORCED_BIRCH_FENCE_GATE.get(),
			SCContent.REINFORCED_JUNGLE_FENCE_GATE.get(),
			SCContent.REINFORCED_ACACIA_FENCE_GATE.get(),
			SCContent.REINFORCED_DARK_OAK_FENCE_GATE.get(),
			SCContent.REINFORCED_CRIMSON_FENCE_GATE.get(),
			SCContent.REINFORCED_WARPED_FENCE_GATE.get()).build(null));
	public static final RegistryObject<TileEntityType<ReinforcedLecternBlockEntity>> REINFORCED_LECTERN_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("reinforced_lectern", () -> TileEntityType.Builder.of(ReinforcedLecternBlockEntity::new, SCContent.REINFORCED_LECTERN.get()).build(null));
	public static final RegistryObject<TileEntityType<SecureRedstoneInterfaceBlockEntity>> SECURE_REDSTONE_INTERFACE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("secure_redstone_interface", () -> TileEntityType.Builder.of(SecureRedstoneInterfaceBlockEntity::new, SCContent.SECURE_REDSTONE_INTERFACE.get()).build(null));
	public static final RegistryObject<TileEntityType<FrameBlockEntity>> FRAME_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("frame", () -> TileEntityType.Builder.of(FrameBlockEntity::new, SCContent.FRAME.get()).build(null));

	//entity types
	public static final RegistryObject<EntityType<BouncingBetty>> BOUNCING_BETTY_ENTITY = ENTITY_TYPES.register("bouncingbetty",
			() -> EntityType.Builder.<BouncingBetty>of(BouncingBetty::new, EntityClassification.MISC)
			.sized(0.5F, 0.2F)
			.setTrackingRange(128)
			.setUpdateInterval(1)
			.setShouldReceiveVelocityUpdates(true)
			.setCustomClientFactory((spawnEntity, world) -> new BouncingBetty(world))
			.build(SecurityCraft.MODID + ":bouncingbetty"));
	public static final RegistryObject<EntityType<IMSBomb>> IMS_BOMB_ENTITY = ENTITY_TYPES.register("imsbomb",
			() -> EntityType.Builder.<IMSBomb>of(IMSBomb::new, EntityClassification.MISC)
			.sized(0.25F, 0.3F)
			.setTrackingRange(256)
			.setUpdateInterval(1)
			.setShouldReceiveVelocityUpdates(true)
			.setCustomClientFactory((spawnEntity, world) -> new IMSBomb(world))
			.build(SecurityCraft.MODID + ":imsbomb"));
	public static final RegistryObject<EntityType<SecurityCamera>> SECURITY_CAMERA_ENTITY = ENTITY_TYPES.register("securitycamera",
			() -> EntityType.Builder.<SecurityCamera>of(SecurityCamera::new, EntityClassification.MISC)
			.sized(0.0001F, 0.0001F)
			.setTrackingRange(256)
			.setUpdateInterval(20)
			.setShouldReceiveVelocityUpdates(true)
			.setCustomClientFactory((spawnEntity, world) -> new SecurityCamera(world))
			.build(SecurityCraft.MODID + ":securitycamera"));
	public static final RegistryObject<EntityType<Sentry>> SENTRY_ENTITY = ENTITY_TYPES.register("sentry",
			() -> EntityType.Builder.<Sentry>of(Sentry::new, EntityClassification.MISC)
			.sized(1.0F, 1.01F)
			.setTrackingRange(256)
			.setUpdateInterval(1)
			.setShouldReceiveVelocityUpdates(true)
			.setCustomClientFactory((spawnEntity, world) -> new Sentry(world))
			.build(SecurityCraft.MODID + ":sentry"));
	public static final RegistryObject<EntityType<Bullet>> BULLET_ENTITY = ENTITY_TYPES.register("bullet",
			() -> EntityType.Builder.<Bullet>of(Bullet::new, EntityClassification.MISC)
			.sized(0.15F, 0.1F)
			.setTrackingRange(256)
			.setUpdateInterval(1)
			.setShouldReceiveVelocityUpdates(true)
			.setCustomClientFactory((spawnEntity, world) -> new Bullet(world))
			.build(SecurityCraft.MODID + ":bullet"));
	//@formatter:off

	//container types
	public static final RegistryObject<ContainerType<BlockReinforcerMenu>> BLOCK_REINFORCER_MENU = MENU_TYPES.register("block_reinforcer", () -> IForgeContainerType.create((windowId, inv, data) -> new BlockReinforcerMenu(windowId, inv, data.readBoolean())));
	public static final RegistryObject<ContainerType<BriefcaseMenu>> BRIEFCASE_INVENTORY_MENU = MENU_TYPES.register("briefcase_inventory", () -> IForgeContainerType.create((windowId, inv, data) -> new BriefcaseMenu(windowId, inv, ItemContainer.briefcase(PlayerUtils.getItemStackFromAnyHand(inv.player, SCContent.BRIEFCASE.get())))));
	public static final RegistryObject<ContainerType<CustomizeBlockMenu>> CUSTOMIZE_BLOCK_MENU = MENU_TYPES.register("customize_block", () -> IForgeContainerType.create((windowId, inv, data) -> new CustomizeBlockMenu(windowId, inv.player.level, data.readBlockPos(), inv)));
	public static final RegistryObject<ContainerType<DisguiseModuleMenu>> DISGUISE_MODULE_MENU = MENU_TYPES.register("disguise_module", () -> IForgeContainerType.create((windowId, inv, data) -> new DisguiseModuleMenu(windowId, inv, new ModuleItemContainer(PlayerUtils.getItemStackFromAnyHand(inv.player, SCContent.DISGUISE_MODULE.get())))));
	public static final RegistryObject<ContainerType<InventoryScannerMenu>> INVENTORY_SCANNER_MENU = MENU_TYPES.register("inventory_scanner", () -> IForgeContainerType.create((windowId, inv, data) -> new InventoryScannerMenu(windowId, inv.player.level, data.readBlockPos(), inv)));
	public static final RegistryObject<ContainerType<KeypadFurnaceMenu>> KEYPAD_FURNACE_MENU = MENU_TYPES.register("keypad_furnace", () -> IForgeContainerType.create((windowId, inv, data) -> new KeypadFurnaceMenu(windowId, inv.player.level, data.readBlockPos(), inv)));
	public static final RegistryObject<ContainerType<KeypadSmokerMenu>> KEYPAD_SMOKER_MENU = MENU_TYPES.register("keypad_smoker", () -> IForgeContainerType.create((windowId, inv, data) -> new KeypadSmokerMenu(windowId, inv.player.level, data.readBlockPos(), inv)));
	public static final RegistryObject<ContainerType<KeypadBlastFurnaceMenu>> KEYPAD_BLAST_FURNACE_MENU = MENU_TYPES.register("keypad_blast_furnace", () -> IForgeContainerType.create((windowId, inv, data) -> new KeypadBlastFurnaceMenu(windowId, inv.player.level, data.readBlockPos(), inv)));
	public static final RegistryObject<ContainerType<ProjectorMenu>> PROJECTOR_MENU = MENU_TYPES.register("projector", () -> IForgeContainerType.create((windowId, inv, data) -> new ProjectorMenu(windowId, inv.player.level, data.readBlockPos(), inv)));
	public static final RegistryObject<ContainerType<KeycardReaderMenu>> KEYCARD_READER_MENU = MENU_TYPES.register("keycard_setup", () -> IForgeContainerType.create((windowId, inv, data) -> new KeycardReaderMenu(windowId, inv, inv.player.level, data.readBlockPos())));
	public static final RegistryObject<ContainerType<BlockPocketManagerMenu>> BLOCK_POCKET_MANAGER_MENU = MENU_TYPES.register("block_pocket_manager", () -> IForgeContainerType.create((windowId, inv, data) -> new BlockPocketManagerMenu(windowId, inv.player.level, data.readBlockPos(), inv)));
	public static final RegistryObject<ContainerType<BlockChangeDetectorMenu>> BLOCK_CHANGE_DETECTOR_MENU = MENU_TYPES.register("block_change_detector", () -> IForgeContainerType.create((windowId, inv, data) -> new BlockChangeDetectorMenu(windowId, inv.player.level, data.readBlockPos(), inv)));
	public static final RegistryObject<ContainerType<KeycardHolderMenu>> KEYCARD_HOLDER_MENU = MENU_TYPES.register("keycard_holder", () -> IForgeContainerType.create((windowId, inv, data) -> new KeycardHolderMenu(windowId, inv, ItemContainer.keycardHolder(PlayerUtils.getItemStackFromAnyHand(inv.player, SCContent.KEYCARD_HOLDER.get())))));
	public static final RegistryObject<ContainerType<TrophySystemMenu>> TROPHY_SYSTEM_MENU = MENU_TYPES.register("trophy_system", () -> IForgeContainerType.create((windowId, inv, data) -> new TrophySystemMenu(windowId, inv.player.level, data.readBlockPos(), inv)));
	public static final RegistryObject<ContainerType<SingleLensMenu>> SINGLE_LENS_MENU = MENU_TYPES.register("single_lens", () -> IForgeContainerType.create((windowId, inv, data) -> new SingleLensMenu(windowId, inv.player.level, data.readBlockPos(), inv)));
	public static final RegistryObject<ContainerType<LaserBlockMenu>> LASER_BLOCK_MENU = MENU_TYPES.register("laser_block", () -> IForgeContainerType.create((windowId, inv, data) -> new LaserBlockMenu(windowId, inv.player.level, data.readBlockPos(), LaserBlockBlockEntity.loadSideConfig(data.readNbt()), inv)));
	public static final RegistryObject<ContainerType<ReinforcedLecternMenu>> REINFORCED_LECTERN_MENU = MENU_TYPES.register("reinforced_lectern", () -> IForgeContainerType.create((windowId, inv, data) -> new ReinforcedLecternMenu(windowId, inv.player.level, data.readBlockPos())));

	public static final AbstractBlock.Properties reinforcedCopy(Block block) {
		return reinforcedCopy(block, UnaryOperator.identity());
	}

	public static final AbstractBlock.Properties reinforcedCopy(Block block, UnaryOperator<AbstractBlock.Properties> propertyEditor) {
		AbstractBlock.Properties properties = ofFullCopy(block);

		properties.explosionResistance = Float.MAX_VALUE;
		return propertyEditor.apply(properties);
	}

	private static final AbstractBlock.Properties prop(float hardness) {
		return prop(Material.STONE, hardness);
	}

	private static final AbstractBlock.Properties prop(Material material, float hardness) {
		return prop(material, material.getColor(), hardness);
	}

	private static final AbstractBlock.Properties prop(Material material, MaterialColor color, float hardness) {
		return AbstractBlock.Properties.of(material, color).strength(hardness, Float.MAX_VALUE).requiresCorrectToolForDrops();
	}

	private static final AbstractBlock.Properties propDisguisable(float hardness) {
		return propDisguisable(Material.STONE, hardness);
	}

	private static final AbstractBlock.Properties propDisguisable(Material material, float hardness) {
		return propDisguisable(material, material.getColor(), hardness);
	}

	private static final AbstractBlock.Properties propDisguisable(Material material, MaterialColor color, float hardness) {
		return propDisguisable(prop(material, color, hardness));
	}

	private static final AbstractBlock.Properties propDisguisable(AbstractBlock.Properties properties) {
		return properties.noOcclusion().dynamicShape().isRedstoneConductor(DisguisableBlock::isNormalCube).isSuffocating(DisguisableBlock::isSuffocating);
	}

	private static final Item.Properties itemProp(ItemGroup tab) {
		return new Item.Properties().tab(tab);
	}

	private static final Item.Properties itemProp(ItemGroup tab, int stackSize) {
		return itemProp(tab).stacksTo(stackSize);
	}

	private static boolean never(BlockState state, IBlockReader level, BlockPos pos) {
		return false;
	}

	private static boolean never(BlockState state, IBlockReader level, BlockPos pos, EntityType<?> entityType) {
		return false;
	}

	private static RegistryObject<BaseReinforcedBlock> reinforcedBlock(String name, Block vanillaBlock) {
		return reinforcedBlock(name, vanillaBlock, UnaryOperator.identity());
	}

	private static RegistryObject<BaseReinforcedBlock> reinforcedBlock(String name, Block vanillaBlock, UnaryOperator<AbstractBlock.Properties> propertyEditor) {
		return registerBlock(name, p -> new BaseReinforcedBlock(p, vanillaBlock), reinforcedCopy(vanillaBlock, propertyEditor));
	}

	private static <B extends Block> RegistryObject<B> reinforcedBlock(String name, Block vanillaBlock, BiFunction<AbstractBlock.Properties, Block, B> constructor) {
		return reinforcedBlock(name, vanillaBlock, constructor, UnaryOperator.identity());
	}

	private static <B extends Block> RegistryObject<B> reinforcedBlock(String name, Block vanillaBlock, BiFunction<AbstractBlock.Properties, Block, B> constructor, UnaryOperator<AbstractBlock.Properties> propertyEditor) {
		return registerBlock(name, p -> constructor.apply(p, vanillaBlock), reinforcedCopy(vanillaBlock, propertyEditor));
	}

	private static RegistryObject<BaseFullMineBlock> blockMine(String name, Block vanillaBlock) {
		return reinforcedBlock(name, vanillaBlock, BaseFullMineBlock::new);
	}

	private static RegistryObject<ReinforcedButtonBlock> woodenButton(String id, Block vanillaBlock) {
		return reinforcedBlock(id, vanillaBlock, (p, _vanillaBlock) -> new ReinforcedButtonBlock(true, p, vanillaBlock));
	}

	private static RegistryObject<ReinforcedButtonBlock> stoneButton(String id, Block vanillaBlock) {
		return reinforcedBlock(id, vanillaBlock, (p, _vanillaBlock) -> new ReinforcedButtonBlock(false, p, vanillaBlock));
	}

	private static RegistryObject<ReinforcedPressurePlateBlock> woodenPressurePlate(String id, Block vanillaBlock) {
		return reinforcedBlock(id, vanillaBlock, (p, _vanillaBlock) -> new ReinforcedPressurePlateBlock(Sensitivity.EVERYTHING, p, vanillaBlock));
	}

	private static RegistryObject<ReinforcedPressurePlateBlock> stonePressurePlate(String id, Block vanillaBlock) {
		return reinforcedBlock(id, vanillaBlock, (p, _vanillaBlock) -> new ReinforcedPressurePlateBlock(Sensitivity.MOBS, p, vanillaBlock));
	}

	private static RegistryObject<ReinforcedFenceGateBlock> reinforcedFenceGateBlock(String id, Block vanillaBlock) {
		return reinforcedBlock(id, vanillaBlock, (p, _vanillaBlock) -> new ReinforcedFenceGateBlock(p, vanillaBlock));
	}

	private static RegistryObject<SecretStandingSignBlock> secretStandingSign(String id, Block standingSign) {
		return secretSign(id, SecretStandingSignBlock::new, standingSign);
	}

	private static RegistryObject<SecretWallSignBlock> secretWallSign(String id, Block standingSign) {
		return secretSign(id, SecretWallSignBlock::new, standingSign);
	}

	private static <T extends AbstractSignBlock> RegistryObject<T> secretSign(String id, BiFunction<AbstractBlock.Properties, WoodType, T> signConstructor, Block baseSign) {
		return registerBlock(id, p -> signConstructor.apply(p, ((AbstractSignBlock) baseSign).type), reinforcedCopy(baseSign));
	}

	private static <B extends Block> RegistryObject<B> registerBlock(String name, Function<AbstractBlock.Properties, ? extends B> constructor, AbstractBlock.Properties properties) {
		return registerBlock(name, constructor, properties, UnaryOperator.identity());
	}

	private static <B extends Block> RegistryObject<B> registerBlock(String name, Function<AbstractBlock.Properties, ? extends B> constructor, AbstractBlock.Properties properties, UnaryOperator<AbstractBlock.Properties> propertyEditor) {
		return BLOCKS.register(name, () -> constructor.apply(propertyEditor.apply(properties)));
	}

	private static AbstractBlock.Properties color(AbstractBlock.Properties properties, MaterialColor color) {
		properties.materialColor = state -> color;
		return properties;
	}


	private static AbstractBlock.Properties ofFullCopy(AbstractBlock blockBehaviour) {
		AbstractBlock.Properties copy = AbstractBlock.Properties.copy(blockBehaviour);
		AbstractBlock.Properties original = blockBehaviour.properties;

		copy.jumpFactor = original.jumpFactor;
		copy.isRedstoneConductor = original.isRedstoneConductor;
		copy.isValidSpawn = original.isValidSpawn;
		copy.hasPostProcess = original.hasPostProcess;
		copy.isSuffocating = original.isSuffocating;
		copy.isViewBlocking = original.isViewBlocking;
		copy.drops = original.drops;
		return copy;
	}

	private SCContent() {}
}
