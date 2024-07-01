package net.geforcemods.securitycraft;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedBookshelfBlock;
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
import net.geforcemods.securitycraft.items.KeyPanelItem;
import net.geforcemods.securitycraft.items.KeycardHolderItem;
import net.geforcemods.securitycraft.items.KeycardItem;
import net.geforcemods.securitycraft.items.LensItem;
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
import net.geforcemods.securitycraft.items.WireCuttersItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.PageGroup;
import net.geforcemods.securitycraft.particle.InterfaceHighlightParticleType;
import net.geforcemods.securitycraft.renderers.DisplayCaseItemRenderer;
import net.geforcemods.securitycraft.renderers.KeypadChestItemRenderer;
import net.geforcemods.securitycraft.util.HasManualPage;
import net.geforcemods.securitycraft.util.OwnableBE;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.RegisterItemBlock;
import net.geforcemods.securitycraft.util.RegisterItemBlock.SCItemGroup;
import net.geforcemods.securitycraft.util.Reinforced;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
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
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.TallBlockItem;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
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
	public static final String KEYPAD_CHEST_PATH = "keypad_chest";
	public static final String DISPLAY_CASE_PATH = "display_case";

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
	public static final RegistryObject<Block> ALARM = BLOCKS.register("alarm", () -> new AlarmBlock(prop(Material.METAL).lightLevel(state -> state.getValue(AlarmBlock.LIT) ? 15 : 0)));
	@HasManualPage
	@RegisterItemBlock
	public static final RegistryObject<Block> BLOCK_CHANGE_DETECTOR = BLOCKS.register("block_change_detector", () -> new BlockChangeDetectorBlock(propDisguisable()));
	@HasManualPage(designedBy = "Henzoid")
	@RegisterItemBlock
	public static final RegistryObject<Block> BLOCK_POCKET_MANAGER = BLOCKS.register("block_pocket_manager", () -> new BlockPocketManagerBlock(prop(MaterialColor.COLOR_CYAN)));
	@HasManualPage
	@RegisterItemBlock(SCItemGroup.DECORATION)
	public static final RegistryObject<Block> BLOCK_POCKET_WALL = BLOCKS.register("block_pocket_wall", () -> new BlockPocketWallBlock(prop().noCollission().isRedstoneConductor(SCContent::never).isSuffocating(BlockPocketWallBlock::causesSuffocation).isViewBlocking(BlockPocketWallBlock::causesSuffocation)));
	@HasManualPage
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> BOUNCING_BETTY = BLOCKS.register("bouncing_betty", () -> new BouncingBettyBlock(prop(Material.METAL, 1.0F)));
	@HasManualPage
	@RegisterItemBlock
	public static final RegistryObject<Block> CAGE_TRAP = BLOCKS.register("cage_trap", () -> new CageTrapBlock(propDisguisable(Material.METAL).sound(SoundType.METAL).noCollission()));
	@HasManualPage
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> CLAYMORE = BLOCKS.register("claymore", () -> new ClaymoreBlock(prop(Material.METAL)));
	@HasManualPage
	public static final RegistryObject<Block> DISPLAY_CASE = BLOCKS.register(DISPLAY_CASE_PATH, () -> new DisplayCaseBlock(prop(Material.METAL).sound(SoundType.METAL)));
	@HasManualPage
	@RegisterItemBlock
	public static final RegistryObject<Block> FLOOR_TRAP = BLOCKS.register("floor_trap", () -> new FloorTrapBlock(propDisguisable(Material.METAL).sound(SoundType.METAL)));
	@HasManualPage
	@OwnableBE
	@RegisterItemBlock
	public static final RegistryObject<Block> FRAME = BLOCKS.register("keypad_frame", () -> new FrameBlock(prop().sound(SoundType.METAL)));
	@HasManualPage
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> IMS = BLOCKS.register("ims", () -> new IMSBlock(prop(Material.METAL, 0.7F).sound(SoundType.METAL)));
	@HasManualPage
	@RegisterItemBlock
	public static final RegistryObject<Block> INVENTORY_SCANNER = BLOCKS.register("inventory_scanner", () -> new InventoryScannerBlock(propDisguisable()));
	public static final RegistryObject<Block> INVENTORY_SCANNER_FIELD = BLOCKS.register("inventory_scanner_field", () -> new InventoryScannerFieldBlock(prop(Material.GLASS)));
	@HasManualPage
	@RegisterItemBlock(SCItemGroup.DECORATION)
	public static final RegistryObject<Block> ELECTRIFIED_IRON_FENCE = BLOCKS.register("electrified_iron_fence", () -> new ElectrifiedIronFenceBlock(prop(Material.METAL, MaterialColor.METAL).sound(SoundType.METAL)));
	public static final RegistryObject<Block> KEY_PANEL_BLOCK = BLOCKS.register("key_panel", () -> new KeyPanelBlock(prop(Material.METAL).sound(SoundType.METAL)));
	@HasManualPage
	@RegisterItemBlock
	public static final RegistryObject<Block> KEYCARD_LOCK = BLOCKS.register("keycard_lock", () -> new KeycardLockBlock(prop()));
	@HasManualPage
	@RegisterItemBlock
	public static final RegistryObject<Block> KEYCARD_READER = BLOCKS.register("keycard_reader", () -> new KeycardReaderBlock(propDisguisable()));
	@HasManualPage(hasRecipeDescription = true)
	@RegisterItemBlock
	public static final RegistryObject<Block> KEYPAD = BLOCKS.register("keypad", () -> new KeypadBlock(propDisguisable(Material.METAL).sound(SoundType.METAL)));
	@HasManualPage(hasRecipeDescription = true)
	@RegisterItemBlock
	public static final RegistryObject<Block> KEYPAD_BARREL = BLOCKS.register("keypad_barrel", () -> new KeypadBarrelBlock(propDisguisable(Material.METAL).sound(SoundType.METAL)));
	@HasManualPage(hasRecipeDescription = true)
	public static final RegistryObject<Block> KEYPAD_CHEST = BLOCKS.register(KEYPAD_CHEST_PATH, () -> new KeypadChestBlock(propDisguisable(Material.METAL).sound(SoundType.METAL)));
	public static final RegistryObject<Block> KEYPAD_DOOR = BLOCKS.register("keypad_door", () -> new KeypadDoorBlock(propDisguisable(Material.METAL).sound(SoundType.METAL)));
	@HasManualPage(hasRecipeDescription = true)
	@RegisterItemBlock
	public static final RegistryObject<KeypadTrapDoorBlock> KEYPAD_TRAPDOOR = BLOCKS.register("keypad_trapdoor", () -> new KeypadTrapDoorBlock(propDisguisable(Material.METAL).sound(SoundType.METAL).isValidSpawn(SCContent::never)));
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
	public static final RegistryObject<Block> LASER_BLOCK = BLOCKS.register("laser_block", () -> new LaserBlock(propDisguisable()));
	public static final RegistryObject<LaserFieldBlock> LASER_FIELD = BLOCKS.register("laser", () -> new LaserFieldBlock(prop()));
	@HasManualPage
	@RegisterItemBlock
	public static final RegistryObject<Block> MOTION_ACTIVATED_LIGHT = BLOCKS.register("motion_activated_light", () -> new MotionActivatedLightBlock(prop(Material.GLASS).sound(SoundType.GLASS).lightLevel(state -> state.getValue(MotionActivatedLightBlock.LIT) ? 15 : 0)));
	@HasManualPage
	@OwnableBE
	@RegisterItemBlock
	public static final RegistryObject<Block> PANIC_BUTTON = BLOCKS.register("panic_button", () -> new PanicButtonBlock(false, prop().lightLevel(state -> state.getValue(AbstractButtonBlock.POWERED) ? 4 : 0)));
	@HasManualPage
	@RegisterItemBlock
	public static final RegistryObject<Block> PORTABLE_RADAR = BLOCKS.register("portable_radar", () -> new PortableRadarBlock(prop(Material.METAL)));
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
	@RegisterItemBlock
	public static final RegistryObject<Block> ELECTRIFIED_IRON_FENCE_GATE = BLOCKS.register("reinforced_fence_gate", () -> new ElectrifiedIronFenceGateBlock(prop(Material.METAL).sound(SoundType.METAL)));
	@HasManualPage
	@RegisterItemBlock
	public static final RegistryObject<Block> RETINAL_SCANNER = BLOCKS.register("retinal_scanner", () -> new RetinalScannerBlock(propDisguisable()));
	public static final RegistryObject<Block> RIFT_STABILIZER = BLOCKS.register("rift_stabilizer", () -> new RiftStabilizerBlock(propDisguisable(Material.METAL).sound(SoundType.METAL)));
	public static final RegistryObject<Block> SCANNER_DOOR = BLOCKS.register("scanner_door", () -> new ScannerDoorBlock(propDisguisable(Material.METAL).sound(SoundType.METAL)));
	@HasManualPage
	@RegisterItemBlock
	public static final RegistryObject<ScannerTrapDoorBlock> SCANNER_TRAPDOOR = BLOCKS.register("scanner_trapdoor", () -> new ScannerTrapDoorBlock(propDisguisable(Material.METAL).sound(SoundType.METAL).isValidSpawn(SCContent::never)));
	public static final RegistryObject<Block> SECRET_OAK_SIGN = BLOCKS.register("secret_sign_standing", () -> new SecretStandingSignBlock(prop(Material.WOOD).sound(SoundType.WOOD), WoodType.OAK));
	public static final RegistryObject<Block> SECRET_OAK_WALL_SIGN = BLOCKS.register("secret_sign_wall", () -> new SecretWallSignBlock(prop(Material.WOOD).sound(SoundType.WOOD), WoodType.OAK));
	public static final RegistryObject<Block> SECRET_SPRUCE_SIGN = BLOCKS.register("secret_spruce_sign_standing", () -> new SecretStandingSignBlock(prop(Material.WOOD, MaterialColor.PODZOL).sound(SoundType.WOOD), WoodType.SPRUCE));
	public static final RegistryObject<Block> SECRET_SPRUCE_WALL_SIGN = BLOCKS.register("secret_spruce_sign_wall", () -> new SecretWallSignBlock(prop(Material.WOOD, MaterialColor.PODZOL).sound(SoundType.WOOD), WoodType.SPRUCE));
	public static final RegistryObject<Block> SECRET_BIRCH_SIGN = BLOCKS.register("secret_birch_sign_standing", () -> new SecretStandingSignBlock(prop(Material.WOOD, MaterialColor.SAND).sound(SoundType.WOOD), WoodType.BIRCH));
	public static final RegistryObject<Block> SECRET_BIRCH_WALL_SIGN = BLOCKS.register("secret_birch_sign_wall", () -> new SecretWallSignBlock(prop(Material.WOOD, MaterialColor.SAND).sound(SoundType.WOOD), WoodType.BIRCH));
	public static final RegistryObject<Block> SECRET_JUNGLE_SIGN = BLOCKS.register("secret_jungle_sign_standing", () -> new SecretStandingSignBlock(prop(Material.WOOD, MaterialColor.DIRT).sound(SoundType.WOOD), WoodType.JUNGLE));
	public static final RegistryObject<Block> SECRET_JUNGLE_WALL_SIGN = BLOCKS.register("secret_jungle_sign_wall", () -> new SecretWallSignBlock(prop(Material.WOOD, MaterialColor.DIRT).sound(SoundType.WOOD), WoodType.JUNGLE));
	public static final RegistryObject<Block> SECRET_ACACIA_SIGN = BLOCKS.register("secret_acacia_sign_standing", () -> new SecretStandingSignBlock(prop(Material.WOOD, MaterialColor.COLOR_ORANGE).sound(SoundType.WOOD), WoodType.ACACIA));
	public static final RegistryObject<Block> SECRET_ACACIA_WALL_SIGN = BLOCKS.register("secret_acacia_sign_wall", () -> new SecretWallSignBlock(prop(Material.WOOD, MaterialColor.COLOR_ORANGE).sound(SoundType.WOOD), WoodType.ACACIA));
	public static final RegistryObject<Block> SECRET_DARK_OAK_SIGN = BLOCKS.register("secret_dark_oak_sign_standing", () -> new SecretStandingSignBlock(prop(Material.WOOD, MaterialColor.COLOR_BROWN).sound(SoundType.WOOD), WoodType.DARK_OAK));
	public static final RegistryObject<Block> SECRET_DARK_OAK_WALL_SIGN = BLOCKS.register("secret_dark_oak_sign_wall", () -> new SecretWallSignBlock(prop(Material.WOOD, MaterialColor.COLOR_BROWN).sound(SoundType.WOOD), WoodType.DARK_OAK));
	public static final RegistryObject<Block> SECRET_CRIMSON_SIGN = BLOCKS.register("secret_crimson_sign_standing", () -> new SecretStandingSignBlock(prop(Material.WOOD, MaterialColor.CRIMSON_STEM).sound(SoundType.WOOD), WoodType.CRIMSON));
	public static final RegistryObject<Block> SECRET_CRIMSON_WALL_SIGN = BLOCKS.register("secret_crimson_sign_wall", () -> new SecretWallSignBlock(prop(Material.WOOD, MaterialColor.CRIMSON_STEM).sound(SoundType.WOOD), WoodType.CRIMSON));
	public static final RegistryObject<Block> SECRET_WARPED_SIGN = BLOCKS.register("secret_warped_sign_standing", () -> new SecretStandingSignBlock(prop(Material.WOOD, MaterialColor.WARPED_STEM).sound(SoundType.WOOD), WoodType.WARPED));
	public static final RegistryObject<Block> SECRET_WARPED_WALL_SIGN = BLOCKS.register("secret_warped_sign_wall", () -> new SecretWallSignBlock(prop(Material.WOOD, MaterialColor.WARPED_STEM).sound(SoundType.WOOD), WoodType.WARPED));
	@HasManualPage
	@RegisterItemBlock
	public static final RegistryObject<Block> SECURE_REDSTONE_INTERFACE = BLOCKS.register("secure_redstone_interface", () -> new SecureRedstoneInterfaceBlock(propDisguisable()));
	@HasManualPage
	@RegisterItemBlock
	public static final RegistryObject<Block> SECURITY_CAMERA = BLOCKS.register("security_camera", () -> new SecurityCameraBlock(propDisguisable(Material.METAL)));
	@HasManualPage
	public static final RegistryObject<Block> SONIC_SECURITY_SYSTEM = BLOCKS.register("sonic_security_system", () -> new SonicSecuritySystemBlock(propDisguisable(Material.METAL).sound(SoundType.METAL).noCollission()));
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
	public static final RegistryObject<Block> MINE = BLOCKS.register("mine", () -> new MineBlock(prop(Material.METAL, 1.0F)));
	public static final RegistryObject<Block> FAKE_WATER_BLOCK = BLOCKS.register("fake_water_block", () -> new FakeWaterBlock(prop(Material.WATER).noCollission(), FAKE_WATER));
	public static final RegistryObject<Block> FAKE_LAVA_BLOCK = BLOCKS.register("fake_lava_block", () -> new FakeLavaBlock(prop(Material.LAVA).noCollission().randomTicks().lightLevel(state -> 15), FAKE_LAVA));

	//block mines
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> STONE_MINE = BLOCKS.register("stone_mine", () -> new BaseFullMineBlock(prop(Material.STONE, 1.5F).requiresCorrectToolForDrops(), Blocks.STONE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> DIRT_MINE = BLOCKS.register("dirt_mine", () -> new BaseFullMineBlock(prop(Material.DIRT, 0.5F).harvestTool(ToolType.SHOVEL).sound(SoundType.GRAVEL), Blocks.DIRT));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> COBBLESTONE_MINE = BLOCKS.register("cobblestone_mine", () -> new BaseFullMineBlock(prop(Material.STONE, 2.0F).requiresCorrectToolForDrops(), Blocks.COBBLESTONE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> SAND_MINE = BLOCKS.register("sand_mine", () -> new FallingBlockMineBlock(prop(Material.SAND, 0.5F).harvestTool(ToolType.SHOVEL).sound(SoundType.SAND), Blocks.SAND));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> GRAVEL_MINE = BLOCKS.register("gravel_mine", () -> new FallingBlockMineBlock(prop(Material.DIRT, 0.6F).harvestTool(ToolType.SHOVEL).sound(SoundType.GRAVEL), Blocks.GRAVEL));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> GOLD_ORE_MINE = BLOCKS.register("gold_mine", () -> new BaseFullMineBlock(prop(Material.STONE, 3.0F).requiresCorrectToolForDrops().harvestLevel(2).harvestTool(ToolType.PICKAXE), Blocks.GOLD_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> IRON_ORE_MINE = BLOCKS.register("iron_mine", () -> new BaseFullMineBlock(prop(Material.STONE, 3.0F).requiresCorrectToolForDrops().harvestLevel(1).harvestTool(ToolType.PICKAXE), Blocks.IRON_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> NETHERRACK_MINE = BLOCKS.register("netherrack_mine", () -> new BaseFullMineBlock(prop(Material.STONE, 0.4F).requiresCorrectToolForDrops().sound(SoundType.NETHERRACK).harvestTool(ToolType.PICKAXE), Blocks.NETHERRACK));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> END_STONE_MINE = BLOCKS.register("end_stone_mine", () -> new BaseFullMineBlock(prop(Material.STONE, 3.0F).harvestTool(ToolType.PICKAXE), Blocks.END_STONE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> COAL_ORE_MINE = BLOCKS.register("coal_mine", () -> new BaseFullMineBlock(prop(Material.STONE, 3.0F).requiresCorrectToolForDrops(), Blocks.COAL_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> NETHER_GOLD_ORE_MINE = BLOCKS.register("nether_gold_mine", () -> new BaseFullMineBlock(prop(Material.STONE, 3.0F).requiresCorrectToolForDrops().sound(SoundType.NETHER_GOLD_ORE), Blocks.NETHER_GOLD_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> LAPIS_ORE_MINE = BLOCKS.register("lapis_mine", () -> new BaseFullMineBlock(prop(Material.STONE, 3.0F).requiresCorrectToolForDrops().harvestLevel(1).harvestTool(ToolType.PICKAXE), Blocks.LAPIS_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> DIAMOND_ORE_MINE = BLOCKS.register("diamond_mine", () -> new BaseFullMineBlock(prop(Material.STONE, 3.0F).requiresCorrectToolForDrops().harvestLevel(2).harvestTool(ToolType.PICKAXE), Blocks.DIAMOND_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> REDSTONE_ORE_MINE = BLOCKS.register("redstone_mine", () -> new RedstoneOreMineBlock(prop(Material.STONE, 3.0F).requiresCorrectToolForDrops().harvestLevel(2).harvestTool(ToolType.PICKAXE).randomTicks().lightLevel(state -> state.getValue(RedstoneOreMineBlock.LIT) ? 9 : 0), Blocks.REDSTONE_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> EMERALD_ORE_MINE = BLOCKS.register("emerald_mine", () -> new BaseFullMineBlock(prop(Material.STONE, 3.0F).requiresCorrectToolForDrops().harvestLevel(2).harvestTool(ToolType.PICKAXE), Blocks.EMERALD_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> QUARTZ_ORE_MINE = BLOCKS.register("quartz_mine", () -> new BaseFullMineBlock(prop(Material.STONE, 3.0F).requiresCorrectToolForDrops().sound(SoundType.NETHER_ORE), Blocks.NETHER_QUARTZ_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	public static final RegistryObject<Block> ANCIENT_DEBRIS_MINE = BLOCKS.register("ancient_debris_mine", () -> new BaseFullMineBlock(prop(Material.METAL, 30.0F).requiresCorrectToolForDrops().harvestLevel(3).harvestTool(ToolType.PICKAXE).sound(SoundType.ANCIENT_DEBRIS), Blocks.ANCIENT_DEBRIS));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final RegistryObject<Block> GILDED_BLACKSTONE_MINE = BLOCKS.register("gilded_blackstone_mine", () -> new BaseFullMineBlock(prop(Material.STONE, 1.5F).requiresCorrectToolForDrops().sound(SoundType.GILDED_BLACKSTONE), Blocks.GILDED_BLACKSTONE));
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
	public static final RegistryObject<Block> REINFORCED_GRANITE = BLOCKS.register("reinforced_granite", () -> new BaseReinforcedBlock(prop(MaterialColor.DIRT), Blocks.GRANITE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_GRANITE = BLOCKS.register("reinforced_polished_granite", () -> new BaseReinforcedBlock(prop(MaterialColor.DIRT), Blocks.POLISHED_GRANITE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DIORITE = BLOCKS.register("reinforced_diorite", () -> new BaseReinforcedBlock(prop(MaterialColor.QUARTZ), Blocks.DIORITE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_DIORITE = BLOCKS.register("reinforced_polished_diorite", () -> new BaseReinforcedBlock(prop(MaterialColor.QUARTZ), Blocks.POLISHED_DIORITE));
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
	public static final RegistryObject<Block> REINFORCED_GRASS_BLOCK = BLOCKS.register("reinforced_grass_block", () -> new ReinforcedGrassBlock(prop(Material.GRASS).sound(SoundType.GRASS)));
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
	public static final RegistryObject<Block> REINFORCED_PODZOL = BLOCKS.register("reinforced_podzol", () -> new ReinforcedSnowyDirtBlock(prop(Material.DIRT, MaterialColor.PODZOL).sound(SoundType.GRAVEL), Blocks.PODZOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CRIMSON_NYLIUM = BLOCKS.register("reinforced_crimson_nylium", () -> new ReinforcedNyliumBlock(prop(MaterialColor.CRIMSON_NYLIUM).sound(SoundType.NYLIUM), Blocks.CRIMSON_NYLIUM));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_WARPED_NYLIUM = BLOCKS.register("reinforced_warped_nylium", () -> new ReinforcedNyliumBlock(prop(MaterialColor.WARPED_NYLIUM).sound(SoundType.NYLIUM), Blocks.WARPED_NYLIUM));
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
	public static final RegistryObject<Block> REINFORCED_SPRUCE_PLANKS = BLOCKS.register("reinforced_spruce_planks", () -> new BaseReinforcedBlock(prop(Material.WOOD, MaterialColor.PODZOL).sound(SoundType.WOOD), Blocks.SPRUCE_PLANKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BIRCH_PLANKS = BLOCKS.register("reinforced_birch_planks", () -> new BaseReinforcedBlock(prop(Material.WOOD, MaterialColor.SAND).sound(SoundType.WOOD), Blocks.BIRCH_PLANKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_JUNGLE_PLANKS = BLOCKS.register("reinforced_jungle_planks", () -> new BaseReinforcedBlock(prop(Material.WOOD, MaterialColor.DIRT).sound(SoundType.WOOD), Blocks.JUNGLE_PLANKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_ACACIA_PLANKS = BLOCKS.register("reinforced_acacia_planks", () -> new BaseReinforcedBlock(prop(Material.WOOD, MaterialColor.COLOR_ORANGE).sound(SoundType.WOOD), Blocks.ACACIA_PLANKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DARK_OAK_PLANKS = BLOCKS.register("reinforced_dark_oak_planks", () -> new BaseReinforcedBlock(prop(Material.WOOD, MaterialColor.COLOR_BROWN).sound(SoundType.WOOD), Blocks.DARK_OAK_PLANKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CRIMSON_PLANKS = BLOCKS.register("reinforced_crimson_planks", () -> new BaseReinforcedBlock(prop(Material.WOOD, MaterialColor.CRIMSON_STEM).sound(SoundType.WOOD), Blocks.CRIMSON_PLANKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_WARPED_PLANKS = BLOCKS.register("reinforced_warped_planks", () -> new BaseReinforcedBlock(prop(Material.WOOD, MaterialColor.WARPED_STEM).sound(SoundType.WOOD), Blocks.WARPED_PLANKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SAND = BLOCKS.register("reinforced_sand", () -> new ReinforcedFallingBlock(prop(Material.SAND).sound(SoundType.SAND), Blocks.SAND));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_RED_SAND = BLOCKS.register("reinforced_red_sand", () -> new ReinforcedFallingBlock(prop(Material.SAND, MaterialColor.COLOR_ORANGE).sound(SoundType.SAND), Blocks.RED_SAND));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_GRAVEL = BLOCKS.register("reinforced_gravel", () -> new ReinforcedFallingBlock(prop(Material.DIRT, MaterialColor.STONE).sound(SoundType.GRAVEL), Blocks.GRAVEL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_OAK_LOG = BLOCKS.register("reinforced_oak_log", () -> new ReinforcedRotatedPillarBlock(logProp(MaterialColor.WOOD, MaterialColor.PODZOL).sound(SoundType.WOOD), Blocks.OAK_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SPRUCE_LOG = BLOCKS.register("reinforced_spruce_log", () -> new ReinforcedRotatedPillarBlock(logProp(MaterialColor.PODZOL, MaterialColor.COLOR_BROWN).sound(SoundType.WOOD), Blocks.SPRUCE_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BIRCH_LOG = BLOCKS.register("reinforced_birch_log", () -> new ReinforcedRotatedPillarBlock(logProp(MaterialColor.SAND, MaterialColor.QUARTZ).sound(SoundType.WOOD), Blocks.BIRCH_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_JUNGLE_LOG = BLOCKS.register("reinforced_jungle_log", () -> new ReinforcedRotatedPillarBlock(logProp(MaterialColor.DIRT, MaterialColor.PODZOL).sound(SoundType.WOOD), Blocks.JUNGLE_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_ACACIA_LOG = BLOCKS.register("reinforced_acacia_log", () -> new ReinforcedRotatedPillarBlock(logProp(MaterialColor.COLOR_ORANGE, MaterialColor.STONE).sound(SoundType.WOOD), Blocks.ACACIA_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DARK_OAK_LOG = BLOCKS.register("reinforced_dark_oak_log", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD, MaterialColor.COLOR_BROWN).sound(SoundType.WOOD), Blocks.DARK_OAK_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CRIMSON_STEM = BLOCKS.register("reinforced_crimson_stem", () -> new ReinforcedRotatedPillarBlock(prop(Material.NETHER_WOOD, MaterialColor.CRIMSON_STEM).sound(SoundType.STEM), Blocks.CRIMSON_STEM));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_WARPED_STEM = BLOCKS.register("reinforced_warped_stem", () -> new ReinforcedRotatedPillarBlock(prop(Material.NETHER_WOOD, MaterialColor.WARPED_STEM).sound(SoundType.STEM), Blocks.WARPED_STEM));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STRIPPED_OAK_LOG = BLOCKS.register("reinforced_stripped_oak_log", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.STRIPPED_OAK_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STRIPPED_SPRUCE_LOG = BLOCKS.register("reinforced_stripped_spruce_log", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD, MaterialColor.PODZOL).sound(SoundType.WOOD), Blocks.STRIPPED_SPRUCE_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STRIPPED_BIRCH_LOG = BLOCKS.register("reinforced_stripped_birch_log", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD, MaterialColor.SAND).sound(SoundType.WOOD), Blocks.STRIPPED_BIRCH_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STRIPPED_JUNGLE_LOG = BLOCKS.register("reinforced_stripped_jungle_log", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD, MaterialColor.DIRT).sound(SoundType.WOOD), Blocks.STRIPPED_JUNGLE_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STRIPPED_ACACIA_LOG = BLOCKS.register("reinforced_stripped_acacia_log", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD, MaterialColor.COLOR_ORANGE).sound(SoundType.WOOD), Blocks.STRIPPED_ACACIA_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STRIPPED_DARK_OAK_LOG = BLOCKS.register("reinforced_stripped_dark_oak_log", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD, MaterialColor.COLOR_BROWN).sound(SoundType.WOOD), Blocks.STRIPPED_DARK_OAK_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STRIPPED_CRIMSON_STEM = BLOCKS.register("reinforced_stripped_crimson_stem", () -> new ReinforcedRotatedPillarBlock(prop(Material.NETHER_WOOD, MaterialColor.CRIMSON_STEM).sound(SoundType.STEM), Blocks.STRIPPED_CRIMSON_STEM));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STRIPPED_WARPED_STEM = BLOCKS.register("reinforced_stripped_warped_stem", () -> new ReinforcedRotatedPillarBlock(prop(Material.NETHER_WOOD, MaterialColor.WARPED_STEM).sound(SoundType.STEM), Blocks.STRIPPED_WARPED_STEM));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STRIPPED_OAK_WOOD = BLOCKS.register("reinforced_stripped_oak_wood", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.STRIPPED_OAK_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STRIPPED_SPRUCE_WOOD = BLOCKS.register("reinforced_stripped_spruce_wood", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD, MaterialColor.PODZOL).sound(SoundType.WOOD), Blocks.STRIPPED_SPRUCE_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STRIPPED_BIRCH_WOOD = BLOCKS.register("reinforced_stripped_birch_wood", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD, MaterialColor.SAND).sound(SoundType.WOOD), Blocks.STRIPPED_BIRCH_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STRIPPED_JUNGLE_WOOD = BLOCKS.register("reinforced_stripped_jungle_wood", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD, MaterialColor.DIRT).sound(SoundType.WOOD), Blocks.STRIPPED_JUNGLE_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STRIPPED_ACACIA_WOOD = BLOCKS.register("reinforced_stripped_acacia_wood", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD, MaterialColor.COLOR_ORANGE).sound(SoundType.WOOD), Blocks.STRIPPED_ACACIA_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STRIPPED_DARK_OAK_WOOD = BLOCKS.register("reinforced_stripped_dark_oak_wood", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD, MaterialColor.COLOR_BROWN).sound(SoundType.WOOD), Blocks.STRIPPED_DARK_OAK_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STRIPPED_CRIMSON_HYPHAE = BLOCKS.register("reinforced_stripped_crimson_hyphae", () -> new ReinforcedRotatedPillarBlock(prop(Material.NETHER_WOOD, MaterialColor.CRIMSON_HYPHAE).sound(SoundType.STEM), Blocks.STRIPPED_CRIMSON_HYPHAE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STRIPPED_WARPED_HYPHAE = BLOCKS.register("reinforced_stripped_warped_hyphae", () -> new ReinforcedRotatedPillarBlock(prop(Material.NETHER_WOOD, MaterialColor.WARPED_HYPHAE).sound(SoundType.STEM), Blocks.STRIPPED_WARPED_HYPHAE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_OAK_WOOD = BLOCKS.register("reinforced_oak_wood", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.OAK_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SPRUCE_WOOD = BLOCKS.register("reinforced_spruce_wood", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD, MaterialColor.PODZOL).sound(SoundType.WOOD), Blocks.SPRUCE_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BIRCH_WOOD = BLOCKS.register("reinforced_birch_wood", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD, MaterialColor.SAND).sound(SoundType.WOOD), Blocks.BIRCH_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_JUNGLE_WOOD = BLOCKS.register("reinforced_jungle_wood", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD, MaterialColor.DIRT).sound(SoundType.WOOD), Blocks.JUNGLE_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_ACACIA_WOOD = BLOCKS.register("reinforced_acacia_wood", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD, MaterialColor.COLOR_GRAY).sound(SoundType.WOOD), Blocks.ACACIA_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DARK_OAK_WOOD = BLOCKS.register("reinforced_dark_oak_wood", () -> new ReinforcedRotatedPillarBlock(prop(Material.WOOD, MaterialColor.COLOR_BROWN).sound(SoundType.WOOD), Blocks.DARK_OAK_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CRIMSON_HYPHAE = BLOCKS.register("reinforced_crimson_hyphae", () -> new ReinforcedRotatedPillarBlock(prop(Material.NETHER_WOOD, MaterialColor.CRIMSON_HYPHAE).sound(SoundType.STEM), Blocks.CRIMSON_HYPHAE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_WARPED_HYPHAE = BLOCKS.register("reinforced_warped_hyphae", () -> new ReinforcedRotatedPillarBlock(prop(Material.NETHER_WOOD, MaterialColor.WARPED_HYPHAE).sound(SoundType.STEM), Blocks.WARPED_HYPHAE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<Block> REINFORCED_GLASS = BLOCKS.register("reinforced_glass", () -> new ReinforcedGlassBlock(glassProp(), Blocks.GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_LAPIS_BLOCK = BLOCKS.register("reinforced_lapis_block", () -> new BaseReinforcedBlock(prop(MaterialColor.LAPIS), Blocks.LAPIS_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SANDSTONE = BLOCKS.register("reinforced_sandstone", () -> new BaseReinforcedBlock(prop(MaterialColor.SAND), Blocks.SANDSTONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CHISELED_SANDSTONE = BLOCKS.register("reinforced_chiseled_sandstone", () -> new BaseReinforcedBlock(prop(MaterialColor.SAND), Blocks.CHISELED_SANDSTONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CUT_SANDSTONE = BLOCKS.register("reinforced_cut_sandstone", () -> new BaseReinforcedBlock(prop(MaterialColor.SAND), Blocks.CUT_SANDSTONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_WHITE_WOOL = BLOCKS.register("reinforced_white_wool", () -> new BaseReinforcedBlock(prop(Material.WOOL, MaterialColor.SNOW).sound(SoundType.WOOL), Blocks.WHITE_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_ORANGE_WOOL = BLOCKS.register("reinforced_orange_wool", () -> new BaseReinforcedBlock(prop(Material.WOOL, MaterialColor.COLOR_ORANGE).sound(SoundType.WOOL), Blocks.ORANGE_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_MAGENTA_WOOL = BLOCKS.register("reinforced_magenta_wool", () -> new BaseReinforcedBlock(prop(Material.WOOL, MaterialColor.COLOR_MAGENTA).sound(SoundType.WOOL), Blocks.MAGENTA_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_LIGHT_BLUE_WOOL = BLOCKS.register("reinforced_light_blue_wool", () -> new BaseReinforcedBlock(prop(Material.WOOL, MaterialColor.COLOR_LIGHT_BLUE).sound(SoundType.WOOL), Blocks.LIGHT_BLUE_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_YELLOW_WOOL = BLOCKS.register("reinforced_yellow_wool", () -> new BaseReinforcedBlock(prop(Material.WOOL, MaterialColor.COLOR_YELLOW).sound(SoundType.WOOL), Blocks.YELLOW_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_LIME_WOOL = BLOCKS.register("reinforced_lime_wool", () -> new BaseReinforcedBlock(prop(Material.WOOL, MaterialColor.COLOR_LIGHT_GREEN).sound(SoundType.WOOL), Blocks.LIME_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PINK_WOOL = BLOCKS.register("reinforced_pink_wool", () -> new BaseReinforcedBlock(prop(Material.WOOL, MaterialColor.COLOR_PINK).sound(SoundType.WOOL), Blocks.PINK_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_GRAY_WOOL = BLOCKS.register("reinforced_gray_wool", () -> new BaseReinforcedBlock(prop(Material.WOOL, MaterialColor.COLOR_GRAY).sound(SoundType.WOOL), Blocks.GRAY_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_LIGHT_GRAY_WOOL = BLOCKS.register("reinforced_light_gray_wool", () -> new BaseReinforcedBlock(prop(Material.WOOL, MaterialColor.COLOR_LIGHT_GRAY).sound(SoundType.WOOL), Blocks.LIGHT_GRAY_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CYAN_WOOL = BLOCKS.register("reinforced_cyan_wool", () -> new BaseReinforcedBlock(prop(Material.WOOL, MaterialColor.COLOR_CYAN).sound(SoundType.WOOL), Blocks.CYAN_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PURPLE_WOOL = BLOCKS.register("reinforced_purple_wool", () -> new BaseReinforcedBlock(prop(Material.WOOL, MaterialColor.COLOR_PURPLE).sound(SoundType.WOOL), Blocks.PURPLE_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BLUE_WOOL = BLOCKS.register("reinforced_blue_wool", () -> new BaseReinforcedBlock(prop(Material.WOOL, MaterialColor.COLOR_BLUE).sound(SoundType.WOOL), Blocks.BLUE_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BROWN_WOOL = BLOCKS.register("reinforced_brown_wool", () -> new BaseReinforcedBlock(prop(Material.WOOL, MaterialColor.COLOR_BROWN).sound(SoundType.WOOL), Blocks.BROWN_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_GREEN_WOOL = BLOCKS.register("reinforced_green_wool", () -> new BaseReinforcedBlock(prop(Material.WOOL, MaterialColor.COLOR_GREEN).sound(SoundType.WOOL), Blocks.GREEN_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_RED_WOOL = BLOCKS.register("reinforced_red_wool", () -> new BaseReinforcedBlock(prop(Material.WOOL, MaterialColor.COLOR_RED).sound(SoundType.WOOL), Blocks.RED_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BLACK_WOOL = BLOCKS.register("reinforced_black_wool", () -> new BaseReinforcedBlock(prop(Material.WOOL, MaterialColor.COLOR_BLACK).sound(SoundType.WOOL), Blocks.BLACK_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_GOLD_BLOCK = BLOCKS.register("reinforced_gold_block", () -> new BaseReinforcedBlock(prop(Material.METAL, MaterialColor.GOLD).sound(SoundType.METAL), Blocks.GOLD_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_IRON_BLOCK = BLOCKS.register("reinforced_iron_block", () -> new BaseReinforcedBlock(prop(Material.METAL).sound(SoundType.METAL), Blocks.IRON_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_OAK_SLAB = BLOCKS.register("reinforced_oak_slab", () -> new ReinforcedSlabBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.OAK_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SPRUCE_SLAB = BLOCKS.register("reinforced_spruce_slab", () -> new ReinforcedSlabBlock(prop(Material.WOOD, MaterialColor.PODZOL).sound(SoundType.WOOD), Blocks.SPRUCE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BIRCH_SLAB = BLOCKS.register("reinforced_birch_slab", () -> new ReinforcedSlabBlock(prop(Material.WOOD, MaterialColor.SAND).sound(SoundType.WOOD), Blocks.BIRCH_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_JUNGLE_SLAB = BLOCKS.register("reinforced_jungle_slab", () -> new ReinforcedSlabBlock(prop(Material.WOOD, MaterialColor.DIRT).sound(SoundType.WOOD), Blocks.JUNGLE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_ACACIA_SLAB = BLOCKS.register("reinforced_acacia_slab", () -> new ReinforcedSlabBlock(prop(Material.WOOD, MaterialColor.COLOR_ORANGE).sound(SoundType.WOOD), Blocks.ACACIA_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DARK_OAK_SLAB = BLOCKS.register("reinforced_dark_oak_slab", () -> new ReinforcedSlabBlock(prop(Material.WOOD, MaterialColor.COLOR_BROWN).sound(SoundType.WOOD), Blocks.DARK_OAK_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CRIMSON_SLAB = BLOCKS.register("reinforced_crimson_slab", () -> new ReinforcedSlabBlock(prop(Material.WOOD, MaterialColor.CRIMSON_STEM).sound(SoundType.WOOD), Blocks.CRIMSON_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_WARPED_SLAB = BLOCKS.register("reinforced_warped_slab", () -> new ReinforcedSlabBlock(prop(Material.WOOD, MaterialColor.WARPED_STEM).sound(SoundType.WOOD), Blocks.WARPED_SLAB));
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
	public static final RegistryObject<Block> REINFORCED_SANDSTONE_SLAB = BLOCKS.register("reinforced_sandstone_slab", () -> new ReinforcedSlabBlock(prop(MaterialColor.SAND), Blocks.SANDSTONE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CUT_SANDSTONE_SLAB = BLOCKS.register("reinforced_cut_sandstone_slab", () -> new ReinforcedSlabBlock(prop(MaterialColor.SAND), Blocks.CUT_SANDSTONE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_COBBLESTONE_SLAB = BLOCKS.register("reinforced_cobblestone_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.COBBLESTONE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BRICK_SLAB = BLOCKS.register("reinforced_brick_slab", () -> new ReinforcedSlabBlock(prop(MaterialColor.COLOR_RED), Blocks.BRICK_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STONE_BRICK_SLAB = BLOCKS.register("reinforced_stone_brick_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.STONE_BRICK_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_NETHER_BRICK_SLAB = BLOCKS.register("reinforced_nether_brick_slab", () -> new ReinforcedSlabBlock(prop(MaterialColor.NETHER).sound(SoundType.NETHER_BRICKS), Blocks.NETHER_BRICK_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_QUARTZ_SLAB = BLOCKS.register("reinforced_quartz_slab", () -> new ReinforcedSlabBlock(prop(MaterialColor.QUARTZ), Blocks.QUARTZ_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_RED_SANDSTONE_SLAB = BLOCKS.register("reinforced_red_sandstone_slab", () -> new ReinforcedSlabBlock(prop(MaterialColor.COLOR_ORANGE), Blocks.RED_SANDSTONE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CUT_RED_SANDSTONE_SLAB = BLOCKS.register("reinforced_cut_red_sandstone_slab", () -> new ReinforcedSlabBlock(prop(MaterialColor.COLOR_ORANGE), Blocks.CUT_RED_SANDSTONE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PURPUR_SLAB = BLOCKS.register("reinforced_purpur_slab", () -> new ReinforcedSlabBlock(prop(MaterialColor.COLOR_MAGENTA), Blocks.PURPUR_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PRISMARINE_SLAB = BLOCKS.register("reinforced_prismarine_slab", () -> new ReinforcedSlabBlock(prop(MaterialColor.COLOR_CYAN), Blocks.PRISMARINE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PRISMARINE_BRICK_SLAB = BLOCKS.register("reinforced_prismarine_brick_slab", () -> new ReinforcedSlabBlock(prop(MaterialColor.DIAMOND), Blocks.PRISMARINE_BRICK_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DARK_PRISMARINE_SLAB = BLOCKS.register("reinforced_dark_prismarine_slab", () -> new ReinforcedSlabBlock(prop(MaterialColor.DIAMOND), Blocks.DARK_PRISMARINE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SMOOTH_QUARTZ = BLOCKS.register("reinforced_smooth_quartz", () -> new BaseReinforcedBlock(prop(MaterialColor.QUARTZ), Blocks.SMOOTH_QUARTZ));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SMOOTH_RED_SANDSTONE = BLOCKS.register("reinforced_smooth_red_sandstone", () -> new BaseReinforcedBlock(prop(MaterialColor.COLOR_ORANGE), Blocks.SMOOTH_RED_SANDSTONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SMOOTH_SANDSTONE = BLOCKS.register("reinforced_smooth_sandstone", () -> new BaseReinforcedBlock(prop(MaterialColor.SAND), Blocks.SMOOTH_SANDSTONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SMOOTH_STONE = BLOCKS.register("reinforced_smooth_stone", () -> new BaseReinforcedBlock(prop(), Blocks.SMOOTH_STONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BRICKS = BLOCKS.register("reinforced_bricks", () -> new BaseReinforcedBlock(prop(MaterialColor.COLOR_RED), Blocks.BRICKS));
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
	public static final RegistryObject<Block> REINFORCED_OBSIDIAN = BLOCKS.register("reinforced_obsidian", () -> new ReinforcedObsidianBlock(prop(MaterialColor.COLOR_BLACK), Blocks.OBSIDIAN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PURPUR_BLOCK = BLOCKS.register("reinforced_purpur_block", () -> new BaseReinforcedBlock(prop(MaterialColor.COLOR_MAGENTA), Blocks.PURPUR_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PURPUR_PILLAR = BLOCKS.register("reinforced_purpur_pillar", () -> new ReinforcedRotatedPillarBlock(prop(MaterialColor.COLOR_MAGENTA), Blocks.PURPUR_PILLAR));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PURPUR_STAIRS = BLOCKS.register("reinforced_purpur_stairs", () -> new ReinforcedStairsBlock(prop(MaterialColor.COLOR_MAGENTA), Blocks.PURPUR_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_OAK_STAIRS = BLOCKS.register("reinforced_oak_stairs", () -> new ReinforcedStairsBlock(prop(Material.WOOD).sound(SoundType.WOOD), Blocks.OAK_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DIAMOND_BLOCK = BLOCKS.register("reinforced_diamond_block", () -> new BaseReinforcedBlock(prop(Material.METAL, MaterialColor.DIAMOND).sound(SoundType.METAL), Blocks.DIAMOND_BLOCK));
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
	public static final RegistryObject<Block> REINFORCED_NETHERRACK = BLOCKS.register("reinforced_netherrack", () -> new BaseReinforcedBlock(prop(MaterialColor.NETHER).sound(SoundType.NETHERRACK), Blocks.NETHERRACK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedSoulSandBlock> REINFORCED_SOUL_SAND = BLOCKS.register("reinforced_soul_sand", () -> new ReinforcedSoulSandBlock(Blocks.SOUL_SAND));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SOUL_SOIL = BLOCKS.register("reinforced_soul_soil", () -> new BaseReinforcedBlock(prop(Material.DIRT, MaterialColor.COLOR_BROWN).sound(SoundType.SOUL_SOIL), Blocks.SOUL_SOIL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BASALT = BLOCKS.register("reinforced_basalt", () -> new ReinforcedRotatedPillarBlock(prop().sound(SoundType.BASALT), Blocks.BASALT));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_BASALT = BLOCKS.register("reinforced_polished_basalt", () -> new ReinforcedRotatedPillarBlock(prop(MaterialColor.COLOR_BLACK).sound(SoundType.BASALT), Blocks.POLISHED_BASALT));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_GLOWSTONE = BLOCKS.register("reinforced_glowstone", () -> new BaseReinforcedBlock(prop(Material.GLASS, MaterialColor.SAND).sound(SoundType.GLASS).lightLevel(state -> 15), Blocks.GLOWSTONE));
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
	public static final RegistryObject<Block> REINFORCED_BRICK_STAIRS = BLOCKS.register("reinforced_brick_stairs", () -> new ReinforcedStairsBlock(prop(MaterialColor.COLOR_RED), Blocks.BRICK_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STONE_BRICK_STAIRS = BLOCKS.register("reinforced_stone_brick_stairs", () -> new ReinforcedStairsBlock(prop(), Blocks.STONE_BRICK_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_MYCELIUM = BLOCKS.register("reinforced_mycelium", () -> new ReinforcedSnowyDirtBlock(prop(Material.GRASS, MaterialColor.COLOR_PURPLE).sound(SoundType.GRASS), Blocks.MYCELIUM));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_NETHER_BRICKS = BLOCKS.register("reinforced_nether_bricks", () -> new BaseReinforcedBlock(prop(MaterialColor.NETHER).sound(SoundType.NETHER_BRICKS), Blocks.NETHER_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CRACKED_NETHER_BRICKS = BLOCKS.register("reinforced_cracked_nether_bricks", () -> new BaseReinforcedBlock(prop(MaterialColor.NETHER).sound(SoundType.NETHER_BRICKS), Blocks.CRACKED_NETHER_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CHISELED_NETHER_BRICKS = BLOCKS.register("reinforced_chiseled_nether_bricks", () -> new BaseReinforcedBlock(prop(MaterialColor.NETHER).sound(SoundType.NETHER_BRICKS), Blocks.CHISELED_NETHER_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_NETHER_BRICK_STAIRS = BLOCKS.register("reinforced_nether_brick_stairs", () -> new ReinforcedStairsBlock(prop(MaterialColor.NETHER).sound(SoundType.NETHER_BRICKS), Blocks.NETHER_BRICK_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_END_STONE = BLOCKS.register("reinforced_end_stone", () -> new BaseReinforcedBlock(prop(MaterialColor.SAND), Blocks.END_STONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_END_STONE_BRICKS = BLOCKS.register("reinforced_end_stone_bricks", () -> new BaseReinforcedBlock(prop(MaterialColor.SAND), Blocks.END_STONE_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SANDSTONE_STAIRS = BLOCKS.register("reinforced_sandstone_stairs", () -> new ReinforcedStairsBlock(prop(MaterialColor.SAND), Blocks.SANDSTONE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_EMERALD_BLOCK = BLOCKS.register("reinforced_emerald_block", () -> new BaseReinforcedBlock(prop(Material.METAL, MaterialColor.EMERALD).sound(SoundType.METAL), Blocks.EMERALD_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SPRUCE_STAIRS = BLOCKS.register("reinforced_spruce_stairs", () -> new ReinforcedStairsBlock(prop(Material.WOOD, MaterialColor.PODZOL).sound(SoundType.WOOD), Blocks.SPRUCE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BIRCH_STAIRS = BLOCKS.register("reinforced_birch_stairs", () -> new ReinforcedStairsBlock(prop(Material.WOOD, MaterialColor.SAND).sound(SoundType.WOOD), Blocks.BIRCH_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_JUNGLE_STAIRS = BLOCKS.register("reinforced_jungle_stairs", () -> new ReinforcedStairsBlock(prop(Material.WOOD, MaterialColor.DIRT).sound(SoundType.WOOD), Blocks.JUNGLE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CRIMSON_STAIRS = BLOCKS.register("reinforced_crimson_stairs", () -> new ReinforcedStairsBlock(prop(Material.WOOD, MaterialColor.CRIMSON_STEM).sound(SoundType.WOOD), Blocks.CRIMSON_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_WARPED_STAIRS = BLOCKS.register("reinforced_warped_stairs", () -> new ReinforcedStairsBlock(prop(Material.WOOD, MaterialColor.WARPED_STEM).sound(SoundType.WOOD), Blocks.WARPED_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CHISELED_QUARTZ = BLOCKS.register("reinforced_chiseled_quartz_block", () -> new BaseReinforcedBlock(prop(MaterialColor.QUARTZ), Blocks.CHISELED_QUARTZ_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_QUARTZ_BLOCK = BLOCKS.register("reinforced_quartz_block", () -> new BaseReinforcedBlock(prop(MaterialColor.QUARTZ), Blocks.QUARTZ_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_QUARTZ_BRICKS = BLOCKS.register("reinforced_quartz_bricks", () -> new BaseReinforcedBlock(prop(MaterialColor.QUARTZ), Blocks.QUARTZ_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_QUARTZ_PILLAR = BLOCKS.register("reinforced_quartz_pillar", () -> new ReinforcedRotatedPillarBlock(prop(MaterialColor.QUARTZ), Blocks.QUARTZ_PILLAR));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_QUARTZ_STAIRS = BLOCKS.register("reinforced_quartz_stairs", () -> new ReinforcedStairsBlock(prop(MaterialColor.QUARTZ), Blocks.QUARTZ_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_WHITE_TERRACOTTA = BLOCKS.register("reinforced_white_terracotta", () -> new BaseReinforcedBlock(prop(MaterialColor.TERRACOTTA_WHITE), Blocks.WHITE_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_ORANGE_TERRACOTTA = BLOCKS.register("reinforced_orange_terracotta", () -> new BaseReinforcedBlock(prop(MaterialColor.TERRACOTTA_ORANGE), Blocks.ORANGE_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_MAGENTA_TERRACOTTA = BLOCKS.register("reinforced_magenta_terracotta", () -> new BaseReinforcedBlock(prop(MaterialColor.TERRACOTTA_MAGENTA), Blocks.MAGENTA_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_LIGHT_BLUE_TERRACOTTA = BLOCKS.register("reinforced_light_blue_terracotta", () -> new BaseReinforcedBlock(prop(MaterialColor.TERRACOTTA_LIGHT_BLUE), Blocks.LIGHT_BLUE_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_YELLOW_TERRACOTTA = BLOCKS.register("reinforced_yellow_terracotta", () -> new BaseReinforcedBlock(prop(MaterialColor.TERRACOTTA_YELLOW), Blocks.YELLOW_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_LIME_TERRACOTTA = BLOCKS.register("reinforced_lime_terracotta", () -> new BaseReinforcedBlock(prop(MaterialColor.TERRACOTTA_LIGHT_GREEN), Blocks.LIME_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PINK_TERRACOTTA = BLOCKS.register("reinforced_pink_terracotta", () -> new BaseReinforcedBlock(prop(MaterialColor.TERRACOTTA_PINK), Blocks.PINK_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_GRAY_TERRACOTTA = BLOCKS.register("reinforced_gray_terracotta", () -> new BaseReinforcedBlock(prop(MaterialColor.TERRACOTTA_GRAY), Blocks.GRAY_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_LIGHT_GRAY_TERRACOTTA = BLOCKS.register("reinforced_light_gray_terracotta", () -> new BaseReinforcedBlock(prop(MaterialColor.TERRACOTTA_LIGHT_GRAY), Blocks.LIGHT_GRAY_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CYAN_TERRACOTTA = BLOCKS.register("reinforced_cyan_terracotta", () -> new BaseReinforcedBlock(prop(MaterialColor.TERRACOTTA_CYAN), Blocks.CYAN_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PURPLE_TERRACOTTA = BLOCKS.register("reinforced_purple_terracotta", () -> new BaseReinforcedBlock(prop(MaterialColor.TERRACOTTA_PURPLE), Blocks.PURPLE_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BLUE_TERRACOTTA = BLOCKS.register("reinforced_blue_terracotta", () -> new BaseReinforcedBlock(prop(MaterialColor.TERRACOTTA_BLUE), Blocks.BLUE_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BROWN_TERRACOTTA = BLOCKS.register("reinforced_brown_terracotta", () -> new BaseReinforcedBlock(prop(MaterialColor.TERRACOTTA_BROWN), Blocks.BROWN_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_GREEN_TERRACOTTA = BLOCKS.register("reinforced_green_terracotta", () -> new BaseReinforcedBlock(prop(MaterialColor.TERRACOTTA_GREEN), Blocks.GREEN_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_RED_TERRACOTTA = BLOCKS.register("reinforced_red_terracotta", () -> new BaseReinforcedBlock(prop(MaterialColor.TERRACOTTA_RED), Blocks.RED_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BLACK_TERRACOTTA = BLOCKS.register("reinforced_black_terracotta", () -> new BaseReinforcedBlock(prop(MaterialColor.TERRACOTTA_BLACK), Blocks.BLACK_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_TERRACOTTA = BLOCKS.register("reinforced_hardened_clay", () -> new BaseReinforcedBlock(prop(MaterialColor.COLOR_ORANGE), Blocks.TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_COAL_BLOCK = BLOCKS.register("reinforced_coal_block", () -> new BaseReinforcedBlock(prop(MaterialColor.COLOR_BLACK), Blocks.COAL_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PACKED_ICE = BLOCKS.register("reinforced_packed_ice", () -> new BaseReinforcedBlock(prop(Material.ICE_SOLID).sound(SoundType.GLASS).friction(0.98F), Blocks.PACKED_ICE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_ACACIA_STAIRS = BLOCKS.register("reinforced_acacia_stairs", () -> new ReinforcedStairsBlock(prop(Material.WOOD, MaterialColor.COLOR_ORANGE).sound(SoundType.WOOD), Blocks.ACACIA_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DARK_OAK_STAIRS = BLOCKS.register("reinforced_dark_oak_stairs", () -> new ReinforcedStairsBlock(prop(Material.WOOD, MaterialColor.COLOR_BROWN).sound(SoundType.WOOD), Blocks.DARK_OAK_STAIRS));
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
	public static final RegistryObject<Block> REINFORCED_PRISMARINE = BLOCKS.register("reinforced_prismarine", () -> new BaseReinforcedBlock(prop(MaterialColor.COLOR_CYAN), Blocks.PRISMARINE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PRISMARINE_BRICKS = BLOCKS.register("reinforced_prismarine_bricks", () -> new BaseReinforcedBlock(prop(MaterialColor.DIAMOND), Blocks.PRISMARINE_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DARK_PRISMARINE = BLOCKS.register("reinforced_dark_prismarine", () -> new BaseReinforcedBlock(prop(MaterialColor.DIAMOND), Blocks.DARK_PRISMARINE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PRISMARINE_STAIRS = BLOCKS.register("reinforced_prismarine_stairs", () -> new ReinforcedStairsBlock(prop(MaterialColor.COLOR_CYAN), Blocks.PRISMARINE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PRISMARINE_BRICK_STAIRS = BLOCKS.register("reinforced_prismarine_brick_stairs", () -> new ReinforcedStairsBlock(prop(MaterialColor.DIAMOND), Blocks.PRISMARINE_BRICK_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DARK_PRISMARINE_STAIRS = BLOCKS.register("reinforced_dark_prismarine_stairs", () -> new ReinforcedStairsBlock(prop(MaterialColor.DIAMOND), Blocks.DARK_PRISMARINE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SEA_LANTERN = BLOCKS.register("reinforced_sea_lantern", () -> new BaseReinforcedBlock(prop(Material.GLASS, MaterialColor.QUARTZ).sound(SoundType.GLASS).lightLevel(state -> 15), Blocks.SEA_LANTERN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_RED_SANDSTONE = BLOCKS.register("reinforced_red_sandstone", () -> new BaseReinforcedBlock(prop(MaterialColor.COLOR_ORANGE), Blocks.RED_SANDSTONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CHISELED_RED_SANDSTONE = BLOCKS.register("reinforced_chiseled_red_sandstone", () -> new BaseReinforcedBlock(prop(MaterialColor.COLOR_ORANGE), Blocks.CHISELED_RED_SANDSTONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CUT_RED_SANDSTONE = BLOCKS.register("reinforced_cut_red_sandstone", () -> new BaseReinforcedBlock(prop(MaterialColor.COLOR_ORANGE), Blocks.CUT_RED_SANDSTONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_RED_SANDSTONE_STAIRS = BLOCKS.register("reinforced_red_sandstone_stairs", () -> new ReinforcedStairsBlock(prop(MaterialColor.COLOR_ORANGE), Blocks.RED_SANDSTONE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<ReinforcedMagmaBlock> REINFORCED_MAGMA_BLOCK = BLOCKS.register("reinforced_magma_block", () -> new ReinforcedMagmaBlock(Blocks.MAGMA_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_NETHER_WART_BLOCK = BLOCKS.register("reinforced_nether_wart_block", () -> new BaseReinforcedBlock(prop(Material.GRASS, MaterialColor.COLOR_RED).sound(SoundType.WART_BLOCK), Blocks.NETHER_WART_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_WARPED_WART_BLOCK = BLOCKS.register("reinforced_warped_wart_block", () -> new BaseReinforcedBlock(prop(Material.GRASS, MaterialColor.WARPED_WART_BLOCK).sound(SoundType.WART_BLOCK), Blocks.WARPED_WART_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_RED_NETHER_BRICKS = BLOCKS.register("reinforced_red_nether_bricks", () -> new BaseReinforcedBlock(prop(MaterialColor.NETHER).sound(SoundType.NETHER_BRICKS), Blocks.RED_NETHER_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BONE_BLOCK = BLOCKS.register("reinforced_bone_block", () -> new ReinforcedRotatedPillarBlock(prop(MaterialColor.SAND).sound(SoundType.BONE_BLOCK), Blocks.BONE_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_WHITE_CONCRETE = BLOCKS.register("reinforced_white_concrete", () -> new BaseReinforcedBlock(prop(MaterialColor.SNOW), Blocks.WHITE_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_ORANGE_CONCRETE = BLOCKS.register("reinforced_orange_concrete", () -> new BaseReinforcedBlock(prop(MaterialColor.COLOR_ORANGE), Blocks.ORANGE_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_MAGENTA_CONCRETE = BLOCKS.register("reinforced_magenta_concrete", () -> new BaseReinforcedBlock(prop(MaterialColor.COLOR_MAGENTA), Blocks.MAGENTA_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_LIGHT_BLUE_CONCRETE = BLOCKS.register("reinforced_light_blue_concrete", () -> new BaseReinforcedBlock(prop(MaterialColor.COLOR_LIGHT_BLUE), Blocks.LIGHT_BLUE_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_YELLOW_CONCRETE = BLOCKS.register("reinforced_yellow_concrete", () -> new BaseReinforcedBlock(prop(MaterialColor.COLOR_YELLOW), Blocks.YELLOW_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_LIME_CONCRETE = BLOCKS.register("reinforced_lime_concrete", () -> new BaseReinforcedBlock(prop(MaterialColor.COLOR_LIGHT_GREEN), Blocks.LIME_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PINK_CONCRETE = BLOCKS.register("reinforced_pink_concrete", () -> new BaseReinforcedBlock(prop(MaterialColor.COLOR_PINK), Blocks.PINK_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_GRAY_CONCRETE = BLOCKS.register("reinforced_gray_concrete", () -> new BaseReinforcedBlock(prop(MaterialColor.COLOR_GRAY), Blocks.GRAY_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_LIGHT_GRAY_CONCRETE = BLOCKS.register("reinforced_light_gray_concrete", () -> new BaseReinforcedBlock(prop(MaterialColor.COLOR_LIGHT_GRAY), Blocks.LIGHT_GRAY_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CYAN_CONCRETE = BLOCKS.register("reinforced_cyan_concrete", () -> new BaseReinforcedBlock(prop(MaterialColor.COLOR_CYAN), Blocks.CYAN_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PURPLE_CONCRETE = BLOCKS.register("reinforced_purple_concrete", () -> new BaseReinforcedBlock(prop(MaterialColor.COLOR_PURPLE), Blocks.PURPLE_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BLUE_CONCRETE = BLOCKS.register("reinforced_blue_concrete", () -> new BaseReinforcedBlock(prop(MaterialColor.COLOR_BLUE), Blocks.BLUE_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BROWN_CONCRETE = BLOCKS.register("reinforced_brown_concrete", () -> new BaseReinforcedBlock(prop(MaterialColor.COLOR_BROWN), Blocks.BROWN_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_GREEN_CONCRETE = BLOCKS.register("reinforced_green_concrete", () -> new BaseReinforcedBlock(prop(MaterialColor.COLOR_GREEN), Blocks.GREEN_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_RED_CONCRETE = BLOCKS.register("reinforced_red_concrete", () -> new BaseReinforcedBlock(prop(MaterialColor.COLOR_RED), Blocks.RED_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BLACK_CONCRETE = BLOCKS.register("reinforced_black_concrete", () -> new BaseReinforcedBlock(prop(MaterialColor.COLOR_BLACK), Blocks.BLACK_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BLUE_ICE = BLOCKS.register("reinforced_blue_ice", () -> new BaseReinforcedBlock(prop(Material.ICE_SOLID).sound(SoundType.GLASS).friction(0.989F), Blocks.BLUE_ICE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_GRANITE_STAIRS = BLOCKS.register("reinforced_polished_granite_stairs", () -> new ReinforcedStairsBlock(prop(MaterialColor.DIRT), Blocks.POLISHED_GRANITE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SMOOTH_RED_SANDSTONE_STAIRS = BLOCKS.register("reinforced_smooth_red_sandstone_stairs", () -> new ReinforcedStairsBlock(prop(MaterialColor.COLOR_ORANGE), Blocks.SMOOTH_RED_SANDSTONE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_MOSSY_STONE_BRICK_STAIRS = BLOCKS.register("reinforced_mossy_stone_brick_stairs", () -> new ReinforcedStairsBlock(prop(), Blocks.MOSSY_STONE_BRICK_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_DIORITE_STAIRS = BLOCKS.register("reinforced_polished_diorite_stairs", () -> new ReinforcedStairsBlock(prop(MaterialColor.QUARTZ), Blocks.POLISHED_DIORITE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_MOSSY_COBBLESTONE_STAIRS = BLOCKS.register("reinforced_mossy_cobblestone_stairs", () -> new ReinforcedStairsBlock(prop(), Blocks.MOSSY_COBBLESTONE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_END_STONE_BRICK_STAIRS = BLOCKS.register("reinforced_end_stone_brick_stairs", () -> new ReinforcedStairsBlock(prop(MaterialColor.SAND), Blocks.END_STONE_BRICK_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STONE_STAIRS = BLOCKS.register("reinforced_stone_stairs", () -> new ReinforcedStairsBlock(prop(), Blocks.STONE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SMOOTH_SANDSTONE_STAIRS = BLOCKS.register("reinforced_smooth_sandstone_stairs", () -> new ReinforcedStairsBlock(prop(MaterialColor.SAND), Blocks.SMOOTH_SANDSTONE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SMOOTH_QUARTZ_STAIRS = BLOCKS.register("reinforced_smooth_quartz_stairs", () -> new ReinforcedStairsBlock(prop(MaterialColor.QUARTZ), Blocks.SMOOTH_QUARTZ_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_GRANITE_STAIRS = BLOCKS.register("reinforced_granite_stairs", () -> new ReinforcedStairsBlock(prop(MaterialColor.DIRT), Blocks.GRANITE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_ANDESITE_STAIRS = BLOCKS.register("reinforced_andesite_stairs", () -> new ReinforcedStairsBlock(prop(), Blocks.ANDESITE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_RED_NETHER_BRICK_STAIRS = BLOCKS.register("reinforced_red_nether_brick_stairs", () -> new ReinforcedStairsBlock(prop(MaterialColor.NETHER).sound(SoundType.NETHER_BRICKS), Blocks.RED_NETHER_BRICK_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_ANDESITE_STAIRS = BLOCKS.register("reinforced_polished_andesite_stairs", () -> new ReinforcedStairsBlock(prop(), Blocks.POLISHED_ANDESITE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DIORITE_STAIRS = BLOCKS.register("reinforced_diorite_stairs", () -> new ReinforcedStairsBlock(prop(MaterialColor.QUARTZ), Blocks.DIORITE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_GRANITE_SLAB = BLOCKS.register("reinforced_polished_granite_slab", () -> new ReinforcedSlabBlock(prop(MaterialColor.DIRT), Blocks.POLISHED_GRANITE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SMOOTH_RED_SANDSTONE_SLAB = BLOCKS.register("reinforced_smooth_red_sandstone_slab", () -> new ReinforcedSlabBlock(prop(MaterialColor.COLOR_ORANGE), Blocks.SMOOTH_RED_SANDSTONE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_MOSSY_STONE_BRICK_SLAB = BLOCKS.register("reinforced_mossy_stone_brick_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.MOSSY_STONE_BRICK_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_DIORITE_SLAB = BLOCKS.register("reinforced_polished_diorite_slab", () -> new ReinforcedSlabBlock(prop(MaterialColor.QUARTZ), Blocks.POLISHED_DIORITE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_MOSSY_COBBLESTONE_SLAB = BLOCKS.register("reinforced_mossy_cobblestone_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.MOSSY_COBBLESTONE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_END_STONE_BRICK_SLAB = BLOCKS.register("reinforced_end_stone_brick_slab", () -> new ReinforcedSlabBlock(prop(MaterialColor.SAND), Blocks.END_STONE_BRICK_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SMOOTH_SANDSTONE_SLAB = BLOCKS.register("reinforced_smooth_sandstone_slab", () -> new ReinforcedSlabBlock(prop(MaterialColor.SAND), Blocks.SMOOTH_SANDSTONE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SMOOTH_QUARTZ_SLAB = BLOCKS.register("reinforced_smooth_quartz_slab", () -> new ReinforcedSlabBlock(prop(MaterialColor.QUARTZ), Blocks.SMOOTH_QUARTZ_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_GRANITE_SLAB = BLOCKS.register("reinforced_granite_slab", () -> new ReinforcedSlabBlock(prop(MaterialColor.DIRT), Blocks.GRANITE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_ANDESITE_SLAB = BLOCKS.register("reinforced_andesite_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.ANDESITE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_RED_NETHER_BRICK_SLAB = BLOCKS.register("reinforced_red_nether_brick_slab", () -> new ReinforcedSlabBlock(prop(MaterialColor.NETHER).sound(SoundType.NETHER_BRICKS), Blocks.RED_NETHER_BRICK_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_ANDESITE_SLAB = BLOCKS.register("reinforced_polished_andesite_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.POLISHED_ANDESITE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DIORITE_SLAB = BLOCKS.register("reinforced_diorite_slab", () -> new ReinforcedSlabBlock(prop(MaterialColor.QUARTZ), Blocks.DIORITE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_NETHERITE_BLOCK = BLOCKS.register("reinforced_netherite_block", () -> new BaseReinforcedBlock(prop(Material.METAL, MaterialColor.COLOR_BLACK).sound(SoundType.NETHERITE_BLOCK), Blocks.NETHERITE_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CRYING_OBSIDIAN = BLOCKS.register("reinforced_crying_obsidian", () -> new ReinforcedCryingObsidianBlock(prop(MaterialColor.COLOR_BLACK).lightLevel(state -> 10), Blocks.CRYING_OBSIDIAN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BLACKSTONE = BLOCKS.register("reinforced_blackstone", () -> new BaseReinforcedBlock(prop(MaterialColor.COLOR_BLACK), Blocks.BLACKSTONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BLACKSTONE_SLAB = BLOCKS.register("reinforced_blackstone_slab", () -> new ReinforcedSlabBlock(prop(MaterialColor.COLOR_BLACK), Blocks.BLACKSTONE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BLACKSTONE_STAIRS = BLOCKS.register("reinforced_blackstone_stairs", () -> new ReinforcedStairsBlock(prop(MaterialColor.COLOR_BLACK), Blocks.BLACKSTONE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_BLACKSTONE = BLOCKS.register("reinforced_polished_blackstone", () -> new BaseReinforcedBlock(prop(MaterialColor.COLOR_BLACK), Blocks.POLISHED_BLACKSTONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_BLACKSTONE_SLAB = BLOCKS.register("reinforced_polished_blackstone_slab", () -> new ReinforcedSlabBlock(prop(MaterialColor.COLOR_BLACK), Blocks.POLISHED_BLACKSTONE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_BLACKSTONE_STAIRS = BLOCKS.register("reinforced_polished_blackstone_stairs", () -> new ReinforcedStairsBlock(prop(MaterialColor.COLOR_BLACK), Blocks.POLISHED_BLACKSTONE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CHISELED_POLISHED_BLACKSTONE = BLOCKS.register("reinforced_chiseled_polished_blackstone", () -> new BaseReinforcedBlock(prop(MaterialColor.COLOR_BLACK), Blocks.CHISELED_POLISHED_BLACKSTONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_BLACKSTONE_BRICKS = BLOCKS.register("reinforced_polished_blackstone_bricks", () -> new BaseReinforcedBlock(prop(MaterialColor.COLOR_BLACK), Blocks.POLISHED_BLACKSTONE_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_BLACKSTONE_BRICK_SLAB = BLOCKS.register("reinforced_polished_blackstone_brick_slab", () -> new ReinforcedSlabBlock(prop(MaterialColor.COLOR_BLACK), Blocks.POLISHED_BLACKSTONE_BRICK_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_BLACKSTONE_BRICK_STAIRS = BLOCKS.register("reinforced_polished_blackstone_brick_stairs", () -> new ReinforcedStairsBlock(prop(MaterialColor.COLOR_BLACK), Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CRACKED_POLISHED_BLACKSTONE_BRICKS = BLOCKS.register("reinforced_cracked_polished_blackstone_bricks", () -> new BaseReinforcedBlock(prop(MaterialColor.COLOR_BLACK), Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS));

	//ordered by vanilla decoration blocks creative tab order
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_COBWEB = BLOCKS.register("reinforced_cobweb", () -> new ReinforcedCobwebBlock(prop(Material.GLASS, MaterialColor.WOOL).noCollission(), Blocks.COBWEB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_END_ROD = BLOCKS.register("reinforced_end_rod", () -> new ReinforcedEndRodBlock(prop(MaterialColor.NONE).lightLevel(state -> 14).sound(SoundType.WOOD).noOcclusion()));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_LADDER = BLOCKS.register("reinforced_ladder", () -> new ReinforcedLadderBlock(prop(Material.DECORATION).sound(SoundType.LADDER).noOcclusion()));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_OAK_FENCE = BLOCKS.register("reinforced_oak_fence", () -> new ReinforcedFenceBlock(prop(Blocks.OAK_PLANKS.defaultMaterialColor()).sound(SoundType.WOOD), Blocks.OAK_FENCE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SPRUCE_FENCE = BLOCKS.register("reinforced_spruce_fence", () -> new ReinforcedFenceBlock(prop(Blocks.SPRUCE_PLANKS.defaultMaterialColor()).sound(SoundType.WOOD), Blocks.SPRUCE_FENCE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BIRCH_FENCE = BLOCKS.register("reinforced_birch_fence", () -> new ReinforcedFenceBlock(prop(Blocks.BIRCH_PLANKS.defaultMaterialColor()).sound(SoundType.WOOD), Blocks.BIRCH_FENCE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_JUNGLE_FENCE = BLOCKS.register("reinforced_jungle_fence", () -> new ReinforcedFenceBlock(prop(Blocks.JUNGLE_PLANKS.defaultMaterialColor()).sound(SoundType.WOOD), Blocks.JUNGLE_FENCE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_ACACIA_FENCE = BLOCKS.register("reinforced_acacia_fence", () -> new ReinforcedFenceBlock(prop(Blocks.ACACIA_PLANKS.defaultMaterialColor()).sound(SoundType.WOOD), Blocks.ACACIA_FENCE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DARK_OAK_FENCE = BLOCKS.register("reinforced_dark_oak_fence", () -> new ReinforcedFenceBlock(prop(Blocks.DARK_OAK_PLANKS.defaultMaterialColor()).sound(SoundType.WOOD), Blocks.DARK_OAK_FENCE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CRIMSON_FENCE = BLOCKS.register("reinforced_crimson_fence", () -> new ReinforcedFenceBlock(prop(Blocks.CRIMSON_PLANKS.defaultMaterialColor()).sound(SoundType.WOOD), Blocks.CRIMSON_FENCE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_WARPED_FENCE = BLOCKS.register("reinforced_warped_fence", () -> new ReinforcedFenceBlock(prop(Blocks.WARPED_PLANKS.defaultMaterialColor()).sound(SoundType.WOOD), Blocks.WARPED_FENCE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<Block> REINFORCED_IRON_BARS = BLOCKS.register("reinforced_iron_bars", () -> new ReinforcedIronBarsBlock(prop(Material.METAL, MaterialColor.NONE).sound(SoundType.METAL), Blocks.IRON_BARS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CHAIN = BLOCKS.register("reinforced_chain", () -> new ReinforcedChainBlock(prop(Material.METAL, MaterialColor.NONE).sound(SoundType.CHAIN), Blocks.CHAIN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<Block> REINFORCED_GLASS_PANE = BLOCKS.register("reinforced_glass_pane", () -> new ReinforcedPaneBlock(prop(Material.GLASS).sound(SoundType.GLASS), Blocks.GLASS_PANE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_NETHER_BRICK_FENCE = BLOCKS.register("reinforced_nether_brick_fence", () -> new ReinforcedFenceBlock(prop(MaterialColor.NETHER).sound(SoundType.NETHER_BRICKS), Blocks.NETHER_BRICK_FENCE));
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
	public static final RegistryObject<Block> REINFORCED_BRICK_WALL = BLOCKS.register("reinforced_brick_wall", () -> new ReinforcedWallBlock(prop(MaterialColor.COLOR_RED), Blocks.BRICK_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PRISMARINE_WALL = BLOCKS.register("reinforced_prismarine_wall", () -> new ReinforcedWallBlock(prop(MaterialColor.COLOR_CYAN), Blocks.PRISMARINE_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_RED_SANDSTONE_WALL = BLOCKS.register("reinforced_red_sandstone_wall", () -> new ReinforcedWallBlock(prop(MaterialColor.COLOR_ORANGE), Blocks.RED_SANDSTONE_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_MOSSY_STONE_BRICK_WALL = BLOCKS.register("reinforced_mossy_stone_brick_wall", () -> new ReinforcedWallBlock(prop(), Blocks.MOSSY_STONE_BRICK_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_GRANITE_WALL = BLOCKS.register("reinforced_granite_wall", () -> new ReinforcedWallBlock(prop(MaterialColor.DIRT), Blocks.GRANITE_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STONE_BRICK_WALL = BLOCKS.register("reinforced_stone_brick_wall", () -> new ReinforcedWallBlock(prop(), Blocks.STONE_BRICK_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_NETHER_BRICK_WALL = BLOCKS.register("reinforced_nether_brick_wall", () -> new ReinforcedWallBlock(prop(MaterialColor.NETHER).sound(SoundType.NETHER_BRICKS), Blocks.NETHER_BRICK_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_ANDESITE_WALL = BLOCKS.register("reinforced_andesite_wall", () -> new ReinforcedWallBlock(prop(), Blocks.ANDESITE_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_RED_NETHER_BRICK_WALL = BLOCKS.register("reinforced_red_nether_brick_wall", () -> new ReinforcedWallBlock(prop(MaterialColor.NETHER).sound(SoundType.NETHER_BRICKS), Blocks.RED_NETHER_BRICK_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SANDSTONE_WALL = BLOCKS.register("reinforced_sandstone_wall", () -> new ReinforcedWallBlock(prop(MaterialColor.SAND), Blocks.SANDSTONE_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_END_STONE_BRICK_WALL = BLOCKS.register("reinforced_end_stone_brick_wall", () -> new ReinforcedWallBlock(prop(MaterialColor.SAND), Blocks.END_STONE_BRICK_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DIORITE_WALL = BLOCKS.register("reinforced_diorite_wall", () -> new ReinforcedWallBlock(prop(MaterialColor.QUARTZ), Blocks.DIORITE_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BLACKSTONE_WALL = BLOCKS.register("reinforced_blackstone_wall", () -> new ReinforcedWallBlock(prop(MaterialColor.COLOR_BLACK), Blocks.BLACKSTONE_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_BLACKSTONE_WALL = BLOCKS.register("reinforced_polished_blackstone_wall", () -> new ReinforcedWallBlock(prop(MaterialColor.COLOR_BLACK), Blocks.POLISHED_BLACKSTONE_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_BLACKSTONE_BRICK_WALL = BLOCKS.register("reinforced_polished_blackstone_brick_wall", () -> new ReinforcedWallBlock(prop(MaterialColor.COLOR_BLACK), Blocks.POLISHED_BLACKSTONE_BRICK_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_WHITE_CARPET = BLOCKS.register("reinforced_white_carpet", () -> new ReinforcedCarpetBlock(prop(Material.WOOL, MaterialColor.SNOW).sound(SoundType.WOOL), Blocks.WHITE_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_ORANGE_CARPET = BLOCKS.register("reinforced_orange_carpet", () -> new ReinforcedCarpetBlock(prop(Material.WOOL, MaterialColor.COLOR_ORANGE).sound(SoundType.WOOL), Blocks.ORANGE_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_MAGENTA_CARPET = BLOCKS.register("reinforced_magenta_carpet", () -> new ReinforcedCarpetBlock(prop(Material.WOOL, MaterialColor.COLOR_MAGENTA).sound(SoundType.WOOL), Blocks.MAGENTA_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_LIGHT_BLUE_CARPET = BLOCKS.register("reinforced_light_blue_carpet", () -> new ReinforcedCarpetBlock(prop(Material.WOOL, MaterialColor.COLOR_LIGHT_BLUE).sound(SoundType.WOOL), Blocks.LIGHT_BLUE_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_YELLOW_CARPET = BLOCKS.register("reinforced_yellow_carpet", () -> new ReinforcedCarpetBlock(prop(Material.WOOL, MaterialColor.COLOR_YELLOW).sound(SoundType.WOOL), Blocks.YELLOW_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_LIME_CARPET = BLOCKS.register("reinforced_lime_carpet", () -> new ReinforcedCarpetBlock(prop(Material.WOOL, MaterialColor.COLOR_LIGHT_GREEN).sound(SoundType.WOOL), Blocks.LIME_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PINK_CARPET = BLOCKS.register("reinforced_pink_carpet", () -> new ReinforcedCarpetBlock(prop(Material.WOOL, MaterialColor.COLOR_PINK).sound(SoundType.WOOL), Blocks.PINK_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_GRAY_CARPET = BLOCKS.register("reinforced_gray_carpet", () -> new ReinforcedCarpetBlock(prop(Material.WOOL, MaterialColor.COLOR_GRAY).sound(SoundType.WOOL), Blocks.GRAY_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_LIGHT_GRAY_CARPET = BLOCKS.register("reinforced_light_gray_carpet", () -> new ReinforcedCarpetBlock(prop(Material.WOOL, MaterialColor.COLOR_LIGHT_GRAY).sound(SoundType.WOOL), Blocks.LIGHT_GRAY_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CYAN_CARPET = BLOCKS.register("reinforced_cyan_carpet", () -> new ReinforcedCarpetBlock(prop(Material.WOOL, MaterialColor.COLOR_CYAN).sound(SoundType.WOOL), Blocks.CYAN_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PURPLE_CARPET = BLOCKS.register("reinforced_purple_carpet", () -> new ReinforcedCarpetBlock(prop(Material.WOOL, MaterialColor.COLOR_PURPLE).sound(SoundType.WOOL), Blocks.PURPLE_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BLUE_CARPET = BLOCKS.register("reinforced_blue_carpet", () -> new ReinforcedCarpetBlock(prop(Material.WOOL, MaterialColor.COLOR_BLUE).sound(SoundType.WOOL), Blocks.BLUE_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BROWN_CARPET = BLOCKS.register("reinforced_brown_carpet", () -> new ReinforcedCarpetBlock(prop(Material.WOOL, MaterialColor.COLOR_BROWN).sound(SoundType.WOOL), Blocks.BROWN_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_GREEN_CARPET = BLOCKS.register("reinforced_green_carpet", () -> new ReinforcedCarpetBlock(prop(Material.WOOL, MaterialColor.COLOR_GREEN).sound(SoundType.WOOL), Blocks.GREEN_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_RED_CARPET = BLOCKS.register("reinforced_red_carpet", () -> new ReinforcedCarpetBlock(prop(Material.WOOL, MaterialColor.COLOR_RED).sound(SoundType.WOOL), Blocks.RED_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BLACK_CARPET = BLOCKS.register("reinforced_black_carpet", () -> new ReinforcedCarpetBlock(prop(Material.WOOL, MaterialColor.COLOR_RED).sound(SoundType.WOOL), Blocks.BLACK_CARPET));
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
	public static final RegistryObject<Block> REINFORCED_SHROOMLIGHT = BLOCKS.register("reinforced_shroomlight", () -> new BaseReinforcedBlock(prop(Material.GRASS, MaterialColor.COLOR_RED).sound(SoundType.SHROOMLIGHT).lightLevel(state -> 15), Blocks.SHROOMLIGHT));

	//ordered by vanilla redstone tab order
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DISPENSER = BLOCKS.register("reinforced_dispenser", () -> new ReinforcedDispenserBlock(prop(Material.STONE)));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STICKY_PISTON = BLOCKS.register("reinforced_sticky_piston", () -> new ReinforcedPistonBlock(true, prop(Material.PISTON).isRedstoneConductor(SCContent::never).isSuffocating((s, w, p) -> !s.getValue(PistonBlock.EXTENDED)).isViewBlocking((s, w, p) -> !s.getValue(PistonBlock.EXTENDED))));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_PISTON = BLOCKS.register("reinforced_piston", () -> new ReinforcedPistonBlock(false, prop(Material.PISTON).isRedstoneConductor(SCContent::never).isSuffocating((s, w, p) -> !s.getValue(PistonBlock.EXTENDED)).isViewBlocking((s, w, p) -> !s.getValue(PistonBlock.EXTENDED))));
	@HasManualPage
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_LEVER = BLOCKS.register("reinforced_lever", () -> new ReinforcedLeverBlock(prop().noCollission().sound(SoundType.WOOD)));
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STONE_PRESSURE_PLATE = BLOCKS.register("reinforced_stone_pressure_plate", () -> stonePressurePlate(Blocks.STONE_PRESSURE_PLATE));
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_OAK_PRESSURE_PLATE = BLOCKS.register("reinforced_oak_pressure_plate", () -> woodenPressurePlate(Blocks.OAK_PRESSURE_PLATE));
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SPRUCE_PRESSURE_PLATE = BLOCKS.register("reinforced_spruce_pressure_plate", () -> woodenPressurePlate(Blocks.SPRUCE_PRESSURE_PLATE));
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BIRCH_PRESSURE_PLATE = BLOCKS.register("reinforced_birch_pressure_plate", () -> woodenPressurePlate(Blocks.BIRCH_PRESSURE_PLATE));
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_JUNGLE_PRESSURE_PLATE = BLOCKS.register("reinforced_jungle_pressure_plate", () -> woodenPressurePlate(Blocks.JUNGLE_PRESSURE_PLATE));
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_ACACIA_PRESSURE_PLATE = BLOCKS.register("reinforced_acacia_pressure_plate", () -> woodenPressurePlate(Blocks.ACACIA_PRESSURE_PLATE));
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DARK_OAK_PRESSURE_PLATE = BLOCKS.register("reinforced_dark_oak_pressure_plate", () -> woodenPressurePlate(Blocks.DARK_OAK_PRESSURE_PLATE));
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CRIMSON_PRESSURE_PLATE = BLOCKS.register("reinforced_crimson_pressure_plate", () -> woodenPressurePlate(Blocks.CRIMSON_PRESSURE_PLATE));
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_WARPED_PRESSURE_PLATE = BLOCKS.register("reinforced_warped_pressure_plate", () -> woodenPressurePlate(Blocks.WARPED_PRESSURE_PLATE));
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_BLACKSTONE_PRESSURE_PLATE = BLOCKS.register("reinforced_polished_blackstone_pressure_plate", () -> stonePressurePlate(Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_REDSTONE_LAMP = BLOCKS.register("reinforced_redstone_lamp", () -> new ReinforcedRedstoneLampBlock(prop(Material.BUILDABLE_GLASS).sound(SoundType.GLASS).lightLevel(state -> state.getValue(ReinforcedRedstoneLampBlock.LIT) ? 15 : 0), Blocks.REDSTONE_LAMP));
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_STONE_BUTTON = BLOCKS.register("reinforced_stone_button", () -> new ReinforcedButtonBlock(false, prop(Material.STONE, MaterialColor.NONE).noCollission(), Blocks.STONE_BUTTON));
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_OAK_BUTTON = BLOCKS.register("reinforced_oak_button", () -> new ReinforcedButtonBlock(true, prop(Material.WOOD, MaterialColor.NONE).noCollission().sound(SoundType.WOOD), Blocks.OAK_BUTTON));
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SPRUCE_BUTTON = BLOCKS.register("reinforced_spruce_button", () -> new ReinforcedButtonBlock(true, prop(Material.WOOD, MaterialColor.NONE).noCollission().sound(SoundType.WOOD), Blocks.SPRUCE_BUTTON));
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BIRCH_BUTTON = BLOCKS.register("reinforced_birch_button", () -> new ReinforcedButtonBlock(true, prop(Material.WOOD, MaterialColor.NONE).noCollission().sound(SoundType.WOOD), Blocks.BIRCH_BUTTON));
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_JUNGLE_BUTTON = BLOCKS.register("reinforced_jungle_button", () -> new ReinforcedButtonBlock(true, prop(Material.WOOD, MaterialColor.NONE).noCollission().sound(SoundType.WOOD), Blocks.JUNGLE_BUTTON));
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_ACACIA_BUTTON = BLOCKS.register("reinforced_acacia_button", () -> new ReinforcedButtonBlock(true, prop(Material.WOOD, MaterialColor.NONE).noCollission().sound(SoundType.WOOD), Blocks.ACACIA_BUTTON));
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DARK_OAK_BUTTON = BLOCKS.register("reinforced_dark_oak_button", () -> new ReinforcedButtonBlock(true, prop(Material.WOOD, MaterialColor.NONE).noCollission().sound(SoundType.WOOD), Blocks.DARK_OAK_BUTTON));
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CRIMSON_BUTTON = BLOCKS.register("reinforced_crimson_button", () -> new ReinforcedButtonBlock(true, prop(Material.WOOD, MaterialColor.NONE).noCollission().sound(SoundType.WOOD), Blocks.CRIMSON_BUTTON));
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_WARPED_BUTTON = BLOCKS.register("reinforced_warped_button", () -> new ReinforcedButtonBlock(true, prop(Material.WOOD, MaterialColor.NONE).noCollission().sound(SoundType.WOOD), Blocks.WARPED_BUTTON));
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_POLISHED_BLACKSTONE_BUTTON = BLOCKS.register("reinforced_polished_blackstone_button", () -> new ReinforcedButtonBlock(false, prop(Material.STONE, MaterialColor.NONE).noCollission(), Blocks.POLISHED_BLACKSTONE_BUTTON));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_REDSTONE_BLOCK = BLOCKS.register("reinforced_redstone_block", () -> new ReinforcedRedstoneBlock(prop(Material.METAL, MaterialColor.FIRE).sound(SoundType.METAL).isRedstoneConductor(SCContent::never), Blocks.REDSTONE_BLOCK));
	@HasManualPage
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_HOPPER = BLOCKS.register("reinforced_hopper", () -> new ReinforcedHopperBlock(prop(Material.METAL, MaterialColor.STONE).sound(SoundType.METAL).noOcclusion()));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DROPPER = BLOCKS.register("reinforced_dropper", () -> new ReinforcedDropperBlock(prop(Material.STONE)));
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_OAK_FENCE_GATE = BLOCKS.register("reinforced_oak_fence_gate", () -> new ReinforcedFenceGateBlock(prop(Blocks.OAK_PLANKS.defaultMaterialColor()).sound(SoundType.WOOD), Blocks.OAK_FENCE_GATE));
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_SPRUCE_FENCE_GATE = BLOCKS.register("reinforced_spruce_fence_gate", () -> new ReinforcedFenceGateBlock(prop(Blocks.SPRUCE_PLANKS.defaultMaterialColor()).sound(SoundType.WOOD), Blocks.SPRUCE_FENCE_GATE));
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_BIRCH_FENCE_GATE = BLOCKS.register("reinforced_birch_fence_gate", () -> new ReinforcedFenceGateBlock(prop(Blocks.BIRCH_PLANKS.defaultMaterialColor()).sound(SoundType.WOOD), Blocks.BIRCH_FENCE_GATE));
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_JUNGLE_FENCE_GATE = BLOCKS.register("reinforced_jungle_fence_gate", () -> new ReinforcedFenceGateBlock(prop(Blocks.JUNGLE_PLANKS.defaultMaterialColor()).sound(SoundType.WOOD), Blocks.JUNGLE_FENCE_GATE));
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_ACACIA_FENCE_GATE = BLOCKS.register("reinforced_acacia_fence_gate", () -> new ReinforcedFenceGateBlock(prop(Blocks.ACACIA_PLANKS.defaultMaterialColor()).sound(SoundType.WOOD), Blocks.ACACIA_FENCE_GATE));
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_DARK_OAK_FENCE_GATE = BLOCKS.register("reinforced_dark_oak_fence_gate", () -> new ReinforcedFenceGateBlock(prop(Blocks.DARK_OAK_PLANKS.defaultMaterialColor()).sound(SoundType.WOOD), Blocks.DARK_OAK_FENCE_GATE));
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CRIMSON_FENCE_GATE = BLOCKS.register("reinforced_crimson_fence_gate", () -> new ReinforcedFenceGateBlock(prop(Blocks.CRIMSON_PLANKS.defaultMaterialColor()).sound(SoundType.WOOD), Blocks.CRIMSON_FENCE_GATE));
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_WARPED_FENCE_GATE = BLOCKS.register("reinforced_warped_fence_gate", () -> new ReinforcedFenceGateBlock(prop(Blocks.WARPED_PLANKS.defaultMaterialColor()).sound(SoundType.WOOD), Blocks.WARPED_FENCE_GATE));
	@HasManualPage(hasRecipeDescription = true)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final RegistryObject<Block> REINFORCED_IRON_TRAPDOOR = BLOCKS.register("reinforced_iron_trapdoor", () -> new ReinforcedIronTrapDoorBlock(prop(Material.METAL).sound(SoundType.METAL).noOcclusion().isValidSpawn(SCContent::never)));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_OBSERVER = BLOCKS.register("reinforced_observer", () -> new ReinforcedObserverBlock(prop().isRedstoneConductor(SCContent::never)));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_LECTERN = BLOCKS.register("reinforced_lectern", () -> new ReinforcedLecternBlock(prop(Material.WOOD).sound(SoundType.WOOD)));

	//ordered by vanilla brewing tab order
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_CAULDRON = BLOCKS.register("reinforced_cauldron", () -> new ReinforcedCauldronBlock(prop(Material.METAL, MaterialColor.STONE)));

	//misc
	@RegisterItemBlock(SCItemGroup.DECORATION)
	public static final RegistryObject<Block> CRYSTAL_QUARTZ_SLAB = BLOCKS.register("crystal_quartz_slab", () -> new SlabBlock(AbstractBlock.Properties.of(Material.STONE, MaterialColor.COLOR_CYAN).strength(2.0F, 6.0F).requiresCorrectToolForDrops()));
	@RegisterItemBlock(SCItemGroup.DECORATION)
	public static final RegistryObject<Block> SMOOTH_CRYSTAL_QUARTZ = BLOCKS.register("smooth_crystal_quartz", () -> new Block(AbstractBlock.Properties.of(Material.STONE, MaterialColor.COLOR_CYAN).strength(2.0F, 6.0F).requiresCorrectToolForDrops()));
	@RegisterItemBlock(SCItemGroup.DECORATION)
	public static final RegistryObject<Block> CHISELED_CRYSTAL_QUARTZ = BLOCKS.register("chiseled_crystal_quartz", () -> new Block(AbstractBlock.Properties.of(Material.STONE, MaterialColor.COLOR_CYAN).strength(0.8F).requiresCorrectToolForDrops()));
	@HasManualPage
	@RegisterItemBlock(SCItemGroup.DECORATION)
	public static final RegistryObject<Block> CRYSTAL_QUARTZ_BLOCK = BLOCKS.register("crystal_quartz", () -> new Block(AbstractBlock.Properties.of(Material.STONE, MaterialColor.COLOR_CYAN).strength(0.8F).requiresCorrectToolForDrops()));
	@RegisterItemBlock(SCItemGroup.DECORATION)
	public static final RegistryObject<Block> CRYSTAL_QUARTZ_BRICKS = BLOCKS.register("crystal_quartz_bricks", () -> new Block(AbstractBlock.Properties.of(Material.STONE, MaterialColor.COLOR_CYAN).strength(0.8F).requiresCorrectToolForDrops()));
	@RegisterItemBlock(SCItemGroup.DECORATION)
	public static final RegistryObject<Block> CRYSTAL_QUARTZ_PILLAR = BLOCKS.register("crystal_quartz_pillar", () -> new RotatedPillarBlock(AbstractBlock.Properties.of(Material.STONE, MaterialColor.COLOR_CYAN).strength(0.8F).requiresCorrectToolForDrops()));
	@RegisterItemBlock(SCItemGroup.DECORATION)
	public static final RegistryObject<Block> CRYSTAL_QUARTZ_STAIRS = BLOCKS.register("crystal_quartz_stairs", () -> new StairsBlock(() -> CRYSTAL_QUARTZ_BLOCK.get().defaultBlockState(), AbstractBlock.Properties.copy(CRYSTAL_QUARTZ_BLOCK.get())));
	@RegisterItemBlock(SCItemGroup.DECORATION)
	public static final RegistryObject<Block> SMOOTH_CRYSTAL_QUARTZ_STAIRS = BLOCKS.register("smooth_crystal_quartz_stairs", () -> new StairsBlock(() -> SMOOTH_CRYSTAL_QUARTZ.get().defaultBlockState(), AbstractBlock.Properties.copy(SMOOTH_CRYSTAL_QUARTZ.get())));
	@RegisterItemBlock(SCItemGroup.DECORATION)
	public static final RegistryObject<Block> SMOOTH_CRYSTAL_QUARTZ_SLAB = BLOCKS.register("smooth_crystal_quartz_slab", () -> new SlabBlock(AbstractBlock.Properties.of(Material.STONE, MaterialColor.COLOR_CYAN).strength(2.0F, 6.0F).requiresCorrectToolForDrops()));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced(customTint = 0x15B3A2)
	public static final RegistryObject<Block> REINFORCED_CRYSTAL_QUARTZ_SLAB = BLOCKS.register("reinforced_crystal_quartz_slab", () -> new ReinforcedSlabBlock(prop(MaterialColor.COLOR_CYAN), SCContent.CRYSTAL_QUARTZ_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced(customTint = 0x15B3A2)
	public static final RegistryObject<Block> REINFORCED_SMOOTH_CRYSTAL_QUARTZ = BLOCKS.register("reinforced_smooth_crystal_quartz", () -> new BaseReinforcedBlock(prop(MaterialColor.COLOR_CYAN), SCContent.SMOOTH_CRYSTAL_QUARTZ));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced(customTint = 0x15B3A2)
	public static final RegistryObject<Block> REINFORCED_CHISELED_CRYSTAL_QUARTZ = BLOCKS.register("reinforced_chiseled_crystal_quartz_block", () -> new BlockPocketBlock(prop(MaterialColor.COLOR_CYAN), SCContent.CHISELED_CRYSTAL_QUARTZ));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced(customTint = 0x15B3A2)
	public static final RegistryObject<Block> REINFORCED_CRYSTAL_QUARTZ_BLOCK = BLOCKS.register("reinforced_crystal_quartz_block", () -> new BlockPocketBlock(prop(MaterialColor.COLOR_CYAN), SCContent.CRYSTAL_QUARTZ_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced(customTint = 0x15B3A2)
	public static final RegistryObject<Block> REINFORCED_CRYSTAL_QUARTZ_BRICKS = BLOCKS.register("reinforced_crystal_quartz_bricks", () -> new BaseReinforcedBlock(prop(MaterialColor.COLOR_CYAN), SCContent.CRYSTAL_QUARTZ_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced(customTint = 0x15B3A2)
	public static final RegistryObject<Block> REINFORCED_CRYSTAL_QUARTZ_PILLAR = BLOCKS.register("reinforced_crystal_quartz_pillar", () -> new ReinforcedRotatedCrystalQuartzPillar(prop(MaterialColor.COLOR_CYAN), SCContent.CRYSTAL_QUARTZ_PILLAR));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced(customTint = 0x15B3A2)
	public static final RegistryObject<Block> REINFORCED_CRYSTAL_QUARTZ_STAIRS = BLOCKS.register("reinforced_crystal_quartz_stairs", () -> new ReinforcedStairsBlock(prop(MaterialColor.COLOR_CYAN), SCContent.CRYSTAL_QUARTZ_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced(customTint = 0x15B3A2)
	public static final RegistryObject<Block> REINFORCED_SMOOTH_CRYSTAL_QUARTZ_STAIRS = BLOCKS.register("reinforced_smooth_crystal_quartz_stairs", () -> new ReinforcedStairsBlock(prop(MaterialColor.COLOR_CYAN), SCContent.SMOOTH_CRYSTAL_QUARTZ_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced(customTint = 0x15B3A2)
	public static final RegistryObject<Block> REINFORCED_SMOOTH_CRYSTAL_QUARTZ_SLAB = BLOCKS.register("reinforced_smooth_crystal_quartz_slab", () -> new ReinforcedSlabBlock(prop(MaterialColor.COLOR_CYAN), SCContent.SMOOTH_CRYSTAL_QUARTZ_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	public static final RegistryObject<Block> HORIZONTAL_REINFORCED_IRON_BARS = BLOCKS.register("horizontal_reinforced_iron_bars", () -> new HorizontalReinforcedIronBars(prop(Material.METAL).sound(SoundType.METAL), Blocks.IRON_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final RegistryObject<Block> REINFORCED_GRASS_PATH = BLOCKS.register("reinforced_grass_path", () -> new ReinforcedGrassPathBlock(prop(Material.DIRT).sound(SoundType.GRASS), Blocks.GRASS_PATH));
	public static final RegistryObject<Block> REINFORCED_MOVING_PISTON = BLOCKS.register("reinforced_moving_piston", () -> new ReinforcedMovingPistonBlock(prop(Material.PISTON).dynamicShape().noDrops().noOcclusion().isRedstoneConductor(SCContent::never).isSuffocating(SCContent::never).isViewBlocking(SCContent::never)));
	@Reinforced(registerBlockItem = false)
	public static final RegistryObject<Block> REINFORCED_PISTON_HEAD = BLOCKS.register("reinforced_piston_head", () -> new ReinforcedPistonHeadBlock(prop(Material.PISTON).noDrops()));
	public static final RegistryObject<Block> SENTRY_DISGUISE = BLOCKS.register("sentry_disguise", () -> new SometimesVisibleBlock(propDisguisable(Material.AIR).noDrops()));

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

	private static final AbstractBlock.Properties prop() {
		return prop(Material.STONE);
	}

	private static final AbstractBlock.Properties prop(Material mat) {
		return AbstractBlock.Properties.of(mat).strength(-1.0F, Float.MAX_VALUE);
	}

	private static final AbstractBlock.Properties prop(MaterialColor color) {
		return prop(Material.STONE, color);
	}

	public static final AbstractBlock.Properties reinforcedCopy(Block block) {
		return AbstractBlock.Properties.copy(block).strength(-1.0F, Float.MAX_VALUE);
	}

	private static final AbstractBlock.Properties prop(Material mat, float hardness) {
		return AbstractBlock.Properties.of(mat).strength(hardness, Float.MAX_VALUE);
	}

	private static final AbstractBlock.Properties prop(Material mat, MaterialColor color) {
		return AbstractBlock.Properties.of(mat, color).strength(-1.0F, Float.MAX_VALUE);
	}

	private static final AbstractBlock.Properties logProp(MaterialColor topColor, MaterialColor sideColor) {
		return AbstractBlock.Properties.of(Material.WOOD, state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? topColor : sideColor).strength(-1.0F, Float.MAX_VALUE);
	}

	private static final AbstractBlock.Properties propDisguisable() {
		return propDisguisable(Material.STONE);
	}

	private static final AbstractBlock.Properties propDisguisable(Material mat) {
		return prop(mat).noOcclusion().dynamicShape().isRedstoneConductor(DisguisableBlock::isNormalCube).isSuffocating(DisguisableBlock::isSuffocating);
	}

	private static final AbstractBlock.Properties glassProp() {
		return prop(Material.GLASS).sound(SoundType.GLASS).noOcclusion();
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

	private static ReinforcedPressurePlateBlock woodenPressurePlate(Block vanillaBlock) {
		return new ReinforcedPressurePlateBlock(Sensitivity.EVERYTHING, prop(Material.WOOD, vanillaBlock.defaultMaterialColor()).noCollission().sound(SoundType.WOOD), vanillaBlock);
	}

	private static ReinforcedPressurePlateBlock stonePressurePlate(Block vanillaBlock) {
		return new ReinforcedPressurePlateBlock(Sensitivity.MOBS, prop(vanillaBlock.defaultMaterialColor()).noCollission(), vanillaBlock);
	}

	private SCContent() {}
}
