package net.geforcemods.securitycraft;

import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.api.SecurityCraftTileEntity;
import net.geforcemods.securitycraft.blocks.AlarmBlock;
import net.geforcemods.securitycraft.blocks.BlockPocketBlock;
import net.geforcemods.securitycraft.blocks.BlockPocketManagerBlock;
import net.geforcemods.securitycraft.blocks.BlockPocketWallBlock;
import net.geforcemods.securitycraft.blocks.CageTrapBlock;
import net.geforcemods.securitycraft.blocks.FakeLavaBlock;
import net.geforcemods.securitycraft.blocks.FakeWaterBlock;
import net.geforcemods.securitycraft.blocks.FrameBlock;
import net.geforcemods.securitycraft.blocks.InventoryScannerBlock;
import net.geforcemods.securitycraft.blocks.InventoryScannerFieldBlock;
import net.geforcemods.securitycraft.blocks.IronFenceBlock;
import net.geforcemods.securitycraft.blocks.KeycardReaderBlock;
import net.geforcemods.securitycraft.blocks.KeypadBlock;
import net.geforcemods.securitycraft.blocks.KeypadChestBlock;
import net.geforcemods.securitycraft.blocks.KeypadFurnaceBlock;
import net.geforcemods.securitycraft.blocks.LaserBlock;
import net.geforcemods.securitycraft.blocks.LaserFieldBlock;
import net.geforcemods.securitycraft.blocks.LoggerBlock;
import net.geforcemods.securitycraft.blocks.MotionActivatedLightBlock;
import net.geforcemods.securitycraft.blocks.PanicButtonBlock;
import net.geforcemods.securitycraft.blocks.PortableRadarBlock;
import net.geforcemods.securitycraft.blocks.ProjectorBlock;
import net.geforcemods.securitycraft.blocks.ProtectoBlock;
import net.geforcemods.securitycraft.blocks.RetinalScannerBlock;
import net.geforcemods.securitycraft.blocks.ScannerDoorBlock;
import net.geforcemods.securitycraft.blocks.SecretStandingSignBlock;
import net.geforcemods.securitycraft.blocks.SecretWallSignBlock;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.blocks.TrophySystemBlock;
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
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedDoorBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedFallingBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedFenceGateBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedGlassBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedHopperBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedIronTrapDoorBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedLeverBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedObserverBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedPaneBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedPressurePlateBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedRedstoneBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedRedstoneLampBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedRotatedCrystalQuartzPillar;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedRotatedPillarBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedSlabBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedSnowyDirtBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedStainedGlassBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedStainedGlassPaneBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedStairsBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedWallBlock;
import net.geforcemods.securitycraft.containers.BlockReinforcerContainer;
import net.geforcemods.securitycraft.containers.BriefcaseContainer;
import net.geforcemods.securitycraft.containers.CustomizeBlockContainer;
import net.geforcemods.securitycraft.containers.DisguiseModuleContainer;
import net.geforcemods.securitycraft.containers.GenericContainer;
import net.geforcemods.securitycraft.containers.GenericTEContainer;
import net.geforcemods.securitycraft.containers.InventoryScannerContainer;
import net.geforcemods.securitycraft.containers.KeypadFurnaceContainer;
import net.geforcemods.securitycraft.containers.ProjectorContainer;
import net.geforcemods.securitycraft.entity.BouncingBettyEntity;
import net.geforcemods.securitycraft.entity.BulletEntity;
import net.geforcemods.securitycraft.entity.IMSBombEntity;
import net.geforcemods.securitycraft.entity.SecurityCameraEntity;
import net.geforcemods.securitycraft.entity.SentryEntity;
import net.geforcemods.securitycraft.entity.TaserBulletEntity;
import net.geforcemods.securitycraft.fluids.FakeLavaFluid;
import net.geforcemods.securitycraft.fluids.FakeWaterFluid;
import net.geforcemods.securitycraft.items.AdminToolItem;
import net.geforcemods.securitycraft.items.BaseKeycardItem;
import net.geforcemods.securitycraft.items.BriefcaseItem;
import net.geforcemods.securitycraft.items.CameraMonitorItem;
import net.geforcemods.securitycraft.items.CodebreakerItem;
import net.geforcemods.securitycraft.items.FakeLiquidBucketItem;
import net.geforcemods.securitycraft.items.KeyPanelItem;
import net.geforcemods.securitycraft.items.MineRemoteAccessToolItem;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.items.ReinforcedDoorItem;
import net.geforcemods.securitycraft.items.SCManualItem;
import net.geforcemods.securitycraft.items.ScannerDoorItem;
import net.geforcemods.securitycraft.items.SecretSignItem;
import net.geforcemods.securitycraft.items.SentryItem;
import net.geforcemods.securitycraft.items.SentryRemoteAccessToolItem;
import net.geforcemods.securitycraft.items.TaserItem;
import net.geforcemods.securitycraft.items.UniversalBlockModifierItem;
import net.geforcemods.securitycraft.items.UniversalBlockReinforcerItem;
import net.geforcemods.securitycraft.items.UniversalBlockRemoverItem;
import net.geforcemods.securitycraft.items.UniversalKeyChangerItem;
import net.geforcemods.securitycraft.items.UniversalOwnerChangerItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.renderers.ItemKeypadChestRenderer;
import net.geforcemods.securitycraft.tileentity.AlarmTileEntity;
import net.geforcemods.securitycraft.tileentity.BlockPocketManagerTileEntity;
import net.geforcemods.securitycraft.tileentity.BlockPocketTileEntity;
import net.geforcemods.securitycraft.tileentity.CageTrapTileEntity;
import net.geforcemods.securitycraft.tileentity.ClaymoreTileEntity;
import net.geforcemods.securitycraft.tileentity.IMSTileEntity;
import net.geforcemods.securitycraft.tileentity.InventoryScannerTileEntity;
import net.geforcemods.securitycraft.tileentity.KeycardReaderTileEntity;
import net.geforcemods.securitycraft.tileentity.KeypadChestTileEntity;
import net.geforcemods.securitycraft.tileentity.KeypadFurnaceTileEntity;
import net.geforcemods.securitycraft.tileentity.KeypadTileEntity;
import net.geforcemods.securitycraft.tileentity.LaserBlockTileEntity;
import net.geforcemods.securitycraft.tileentity.MotionActivatedLightTileEntity;
import net.geforcemods.securitycraft.tileentity.PortableRadarTileEntity;
import net.geforcemods.securitycraft.tileentity.ProjectorTileEntity;
import net.geforcemods.securitycraft.tileentity.ProtectoTileEntity;
import net.geforcemods.securitycraft.tileentity.ReinforcedHopperTileEntity;
import net.geforcemods.securitycraft.tileentity.RetinalScannerTileEntity;
import net.geforcemods.securitycraft.tileentity.ScannerDoorTileEntity;
import net.geforcemods.securitycraft.tileentity.SecretSignTileEntity;
import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.geforcemods.securitycraft.tileentity.TrackMineTileEntity;
import net.geforcemods.securitycraft.tileentity.TrophySystemTileEntity;
import net.geforcemods.securitycraft.tileentity.UsernameLoggerTileEntity;
import net.geforcemods.securitycraft.tileentity.WhitelistOnlyTileEntity;
import net.geforcemods.securitycraft.util.HasManualPage;
import net.geforcemods.securitycraft.util.OwnableTE;
import net.geforcemods.securitycraft.util.RegisterItemBlock;
import net.geforcemods.securitycraft.util.RegisterItemBlock.SCItemGroup;
import net.geforcemods.securitycraft.util.Reinforced;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.PressurePlateBlock.Sensitivity;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.WoodType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

public class SCContent
{
	public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, SecurityCraft.MODID);
	public static final DeferredRegister<Fluid> FLUIDS = new DeferredRegister<>(ForgeRegistries.FLUIDS, SecurityCraft.MODID);
	public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, SecurityCraft.MODID);
	public static final String KEYPAD_CHEST_PATH = "keypad_chest";
	//blocks
	@HasManualPage @RegisterItemBlock public static final RegistryObject<Block> ALARM = BLOCKS.register("alarm", () -> new AlarmBlock());
	@HasManualPage(designedBy="Henzoid") @RegisterItemBlock public static final RegistryObject<Block> BLOCK_POCKET_MANAGER = BLOCKS.register("block_pocket_manager", () -> new BlockPocketManagerBlock());
	@HasManualPage @RegisterItemBlock(SCItemGroup.DECORATION) public static final RegistryObject<Block> BLOCK_POCKET_WALL = BLOCKS.register("block_pocket_wall", () -> new BlockPocketWallBlock());
	@HasManualPage @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static final RegistryObject<Block> BOUNCING_BETTY = BLOCKS.register("bouncing_betty", () -> new BouncingBettyBlock(Material.MISCELLANEOUS, 1F));
	@HasManualPage @RegisterItemBlock public static final RegistryObject<Block> CAGE_TRAP = BLOCKS.register("cage_trap", () -> new CageTrapBlock(Material.IRON));
	@RegisterItemBlock(SCItemGroup.DECORATION) public static final RegistryObject<Block> CHISELED_CRYSTAL_QUARTZ = BLOCKS.register("chiseled_crystal_quartz", () -> new Block(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.8F)));
	@HasManualPage @RegisterItemBlock(SCItemGroup.DECORATION) public static final RegistryObject<Block> CRYSTAL_QUARTZ = BLOCKS.register("crystal_quartz", () -> new Block(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.8F)));
	@RegisterItemBlock(SCItemGroup.DECORATION) public static final RegistryObject<Block> CRYSTAL_QUARTZ_PILLAR = BLOCKS.register("crystal_quartz_pillar", () -> new RotatedPillarBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.8F)));
	@RegisterItemBlock(SCItemGroup.DECORATION) public static final RegistryObject<Block> CRYSTAL_QUARTZ_SLAB = BLOCKS.register("crystal_quartz_slab", () -> new SlabBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance(2.0F, 6.0F)));
	@HasManualPage @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static final RegistryObject<Block> CLAYMORE = BLOCKS.register("claymore", () -> new ClaymoreBlock(Material.MISCELLANEOUS));
	@HasManualPage @OwnableTE @RegisterItemBlock public static final RegistryObject<Block> FRAME = BLOCKS.register("keypad_frame", () -> new FrameBlock(Material.ROCK));
	@HasManualPage @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static final RegistryObject<Block> IMS = BLOCKS.register("ims", () -> new IMSBlock(Material.IRON));
	@HasManualPage @RegisterItemBlock public static final RegistryObject<Block> INVENTORY_SCANNER = BLOCKS.register("inventory_scanner", () -> new InventoryScannerBlock(Material.ROCK));
	public static final RegistryObject<Block> INVENTORY_SCANNER_FIELD = BLOCKS.register("inventory_scanner_field", () -> new InventoryScannerFieldBlock(Material.GLASS));
	@HasManualPage @RegisterItemBlock(SCItemGroup.DECORATION) public static final RegistryObject<Block> IRON_FENCE = BLOCKS.register("electrified_iron_fence", () -> new IronFenceBlock(Material.IRON));
	@HasManualPage @RegisterItemBlock public static final RegistryObject<Block> KEYCARD_READER = BLOCKS.register("keycard_reader", () -> new KeycardReaderBlock(Material.IRON));
	@HasManualPage @RegisterItemBlock public static final RegistryObject<Block> KEYPAD = BLOCKS.register("keypad", () -> new KeypadBlock(Material.IRON));
	@HasManualPage public static final RegistryObject<Block> KEYPAD_CHEST = BLOCKS.register(KEYPAD_CHEST_PATH, () -> new KeypadChestBlock());
	@HasManualPage @RegisterItemBlock public static final RegistryObject<Block> KEYPAD_FURNACE = BLOCKS.register("keypad_furnace", () -> new KeypadFurnaceBlock(Material.IRON));
	@HasManualPage @RegisterItemBlock public static final RegistryObject<Block> LASER_BLOCK = BLOCKS.register("laser_block", () -> new LaserBlock(Material.IRON));
	public static final RegistryObject<Block> LASER_FIELD = BLOCKS.register("laser", () -> new LaserFieldBlock(Material.ROCK));
	@HasManualPage @RegisterItemBlock public static final RegistryObject<Block> MOTION_ACTIVATED_LIGHT = BLOCKS.register("motion_activated_light", () -> new MotionActivatedLightBlock(Material.GLASS));
	@HasManualPage @OwnableTE @RegisterItemBlock public static final RegistryObject<Block> PANIC_BUTTON = BLOCKS.register("panic_button", () -> new PanicButtonBlock());
	@HasManualPage @RegisterItemBlock public static final RegistryObject<Block> PORTABLE_RADAR = BLOCKS.register("portable_radar", () -> new PortableRadarBlock(Material.MISCELLANEOUS));
	@HasManualPage @RegisterItemBlock public static final RegistryObject<Block> PROTECTO = BLOCKS.register("protecto", () -> new ProtectoBlock(Material.IRON));
	@OwnableTE public static final RegistryObject<Block> REINFORCED_DOOR = BLOCKS.register("iron_door_reinforced", () -> new ReinforcedDoorBlock(Material.IRON));
	@HasManualPage @RegisterItemBlock(SCItemGroup.DECORATION) public static final RegistryObject<Block> REINFORCED_FENCEGATE = BLOCKS.register("reinforced_fence_gate", () -> new ReinforcedFenceGateBlock());
	@HasManualPage @RegisterItemBlock public static final RegistryObject<Block> RETINAL_SCANNER = BLOCKS.register("retinal_scanner", () -> new RetinalScannerBlock(Material.IRON));
	public static final RegistryObject<Block> SCANNER_DOOR = BLOCKS.register("scanner_door", () -> new ScannerDoorBlock(Material.IRON));
	public static final RegistryObject<Block> SECRET_OAK_SIGN = BLOCKS.register("secret_sign_standing", () -> new SecretStandingSignBlock(WoodType.OAK));
	public static final RegistryObject<Block> SECRET_OAK_WALL_SIGN = BLOCKS.register("secret_sign_wall", () -> new SecretWallSignBlock(WoodType.OAK));
	public static final RegistryObject<Block> SECRET_SPRUCE_SIGN = BLOCKS.register("secret_spruce_sign_standing", () -> new SecretStandingSignBlock(WoodType.SPRUCE));
	public static final RegistryObject<Block> SECRET_SPRUCE_WALL_SIGN = BLOCKS.register("secret_spruce_sign_wall", () -> new SecretWallSignBlock(WoodType.SPRUCE));
	public static final RegistryObject<Block> SECRET_BIRCH_SIGN = BLOCKS.register("secret_birch_sign_standing", () -> new SecretStandingSignBlock(WoodType.BIRCH));
	public static final RegistryObject<Block> SECRET_BIRCH_WALL_SIGN = BLOCKS.register("secret_birch_sign_wall", () -> new SecretWallSignBlock(WoodType.BIRCH));
	public static final RegistryObject<Block> SECRET_JUNGLE_SIGN = BLOCKS.register("secret_jungle_sign_standing", () -> new SecretStandingSignBlock(WoodType.JUNGLE));
	public static final RegistryObject<Block> SECRET_JUNGLE_WALL_SIGN = BLOCKS.register("secret_jungle_sign_wall", () -> new SecretWallSignBlock(WoodType.JUNGLE));
	public static final RegistryObject<Block> SECRET_ACACIA_SIGN = BLOCKS.register("secret_acacia_sign_standing", () -> new SecretStandingSignBlock(WoodType.ACACIA));
	public static final RegistryObject<Block> SECRET_ACACIA_WALL_SIGN = BLOCKS.register("secret_acacia_sign_wall", () -> new SecretWallSignBlock(WoodType.ACACIA));
	public static final RegistryObject<Block> SECRET_DARK_OAK_SIGN = BLOCKS.register("secret_dark_oak_sign_standing", () -> new SecretStandingSignBlock(WoodType.DARK_OAK));
	public static final RegistryObject<Block> SECRET_DARK_OAK_WALL_SIGN = BLOCKS.register("secret_dark_oak_sign_wall", () -> new SecretWallSignBlock(WoodType.DARK_OAK));
	@HasManualPage @RegisterItemBlock public static final RegistryObject<Block> SECURITY_CAMERA = BLOCKS.register("security_camera", () -> new SecurityCameraBlock(Material.IRON));
	@RegisterItemBlock(SCItemGroup.DECORATION) public static final RegistryObject<Block> STAIRS_CRYSTAL_QUARTZ = BLOCKS.register("crystal_quartz_stairs", () -> new StairsBlock(() -> CRYSTAL_QUARTZ.get().getDefaultState(), Block.Properties.from(CRYSTAL_QUARTZ.get())));
	@OwnableTE @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static final RegistryObject<Block> TRACK_MINE = BLOCKS.register("track_mine", () -> new TrackMineBlock());
	@HasManualPage @OwnableTE @RegisterItemBlock(SCItemGroup.TECHNICAL) public static final RegistryObject<Block> TROPHY_SYSTEM = BLOCKS.register("trophy_system", () -> new TrophySystemBlock(Material.IRON));
	@HasManualPage @RegisterItemBlock public static final RegistryObject<Block> USERNAME_LOGGER = BLOCKS.register("username_logger", () -> new LoggerBlock(Material.ROCK));
	@HasManualPage @OwnableTE @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static final RegistryObject<Block> MINE = BLOCKS.register("mine", () -> new MineBlock(Material.MISCELLANEOUS, 1F));
	public static final RegistryObject<Block> FAKE_WATER_BLOCK = BLOCKS.register("fake_water_block", () -> new FakeWaterBlock());
	public static final RegistryObject<Block> FAKE_LAVA_BLOCK = BLOCKS.register("fake_lava_block", () -> new FakeLavaBlock());
	//	@HasManualPage @OwnableTE @RegisterItemBlock public static final RegistryObject<Block> PROJECTOR = BLOCKS.register("projector", () -> new ProjectorBlock(Block.Properties.create(Material.IRON)));

	//block mines
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static final RegistryObject<Block> STONE_MINE = BLOCKS.register("stone_mine", () -> new BaseFullMineBlock(Material.ROCK, SoundType.STONE, Blocks.STONE, 1.5F));
	@HasManualPage @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static final RegistryObject<Block> DIRT_MINE = BLOCKS.register("dirt_mine", () -> new BaseFullMineBlock(Material.EARTH, SoundType.GROUND, Blocks.DIRT, 0.5F));
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static final RegistryObject<Block> COBBLESTONE_MINE = BLOCKS.register("cobblestone_mine", () -> new BaseFullMineBlock(Material.ROCK, SoundType.STONE, Blocks.COBBLESTONE, 2.0F));
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static final RegistryObject<Block> SAND_MINE = BLOCKS.register("sand_mine", () -> new FallingBlockMineBlock(Material.SAND, SoundType.SAND, Blocks.SAND, 0.5F));
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static final RegistryObject<Block> GRAVEL_MINE = BLOCKS.register("gravel_mine", () -> new FallingBlockMineBlock(Material.EARTH, SoundType.GROUND, Blocks.GRAVEL, 0.6F));
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static final RegistryObject<Block> GOLD_ORE_MINE = BLOCKS.register("gold_mine", () -> new BaseFullMineBlock(Material.ROCK, SoundType.STONE, Blocks.GOLD_ORE, 3.0F));
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static final RegistryObject<Block> IRON_ORE_MINE = BLOCKS.register("iron_mine", () -> new BaseFullMineBlock(Material.ROCK, SoundType.STONE, Blocks.IRON_ORE, 3.0F));
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static final RegistryObject<Block> COAL_ORE_MINE = BLOCKS.register("coal_mine", () -> new BaseFullMineBlock(Material.ROCK, SoundType.STONE, Blocks.COAL_ORE, 3.0F));
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static final RegistryObject<Block> LAPIS_ORE_MINE = BLOCKS.register("lapis_mine", () -> new BaseFullMineBlock(Material.ROCK, SoundType.STONE, Blocks.LAPIS_ORE, 3.0F));
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static final RegistryObject<Block> DIAMOND_ORE_MINE = BLOCKS.register("diamond_mine", () -> new BaseFullMineBlock(Material.ROCK, SoundType.STONE, Blocks.DIAMOND_ORE, 3.0F));
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static final RegistryObject<Block> REDSTONE_ORE_MINE = BLOCKS.register("redstone_mine", () -> new RedstoneOreMineBlock());
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static final RegistryObject<Block> EMERALD_ORE_MINE = BLOCKS.register("emerald_mine", () -> new BaseFullMineBlock(Material.ROCK, SoundType.STONE, Blocks.EMERALD_ORE, 3.0F));
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static final RegistryObject<Block> QUARTZ_ORE_MINE = BLOCKS.register("quartz_mine", () -> new BaseFullMineBlock(Material.ROCK, SoundType.STONE, Blocks.NETHER_QUARTZ_ORE, 3.0F));
	@HasManualPage @OwnableTE @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static final RegistryObject<Block> FURNACE_MINE = BLOCKS.register("furnace_mine", () -> new FurnaceMineBlock(Material.ROCK, 3.5F));

	//reinforced blocks (ordered by vanilla building blocks creative tab order)
	@HasManualPage(specialInfoKey="help.securitycraft:reinforced.info") @OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_STONE = BLOCKS.register("reinforced_stone", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.STONE));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_GRANITE = BLOCKS.register("reinforced_granite", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.GRANITE));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_POLISHED_GRANITE = BLOCKS.register("reinforced_polished_granite", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.POLISHED_GRANITE));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_DIORITE = BLOCKS.register("reinforced_diorite", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.DIORITE));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_POLISHED_DIORITE = BLOCKS.register("reinforced_polished_diorite", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.POLISHED_DIORITE));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_ANDESITE = BLOCKS.register("reinforced_andesite", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.ANDESITE));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_POLISHED_ANDESITE = BLOCKS.register("reinforced_polished_andesite", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.POLISHED_ANDESITE));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_GRASS_BLOCK = BLOCKS.register("reinforced_grass_block", () -> new ReinforcedSnowyDirtBlock(Material.ORGANIC, SoundType.PLANT, Blocks.GRASS_BLOCK));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_DIRT = BLOCKS.register("reinforced_dirt", () -> new BaseReinforcedBlock(SoundType.GROUND, Material.EARTH, Blocks.DIRT));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_COARSE_DIRT = BLOCKS.register("reinforced_coarse_dirt", () -> new BaseReinforcedBlock(SoundType.GROUND, Material.EARTH, Blocks.COARSE_DIRT));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_PODZOL = BLOCKS.register("reinforced_podzol", () -> new ReinforcedSnowyDirtBlock( Material.EARTH, SoundType.GROUND, Blocks.PODZOL));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_COBBLESTONE = BLOCKS.register("reinforced_cobblestone", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.COBBLESTONE));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_OAK_PLANKS = BLOCKS.register("reinforced_oak_planks", () -> new BaseReinforcedBlock(SoundType.WOOD, Material.WOOD, Blocks.OAK_PLANKS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_SPRUCE_PLANKS = BLOCKS.register("reinforced_spruce_planks", () -> new BaseReinforcedBlock(SoundType.WOOD, Material.WOOD, Blocks.SPRUCE_PLANKS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_BIRCH_PLANKS = BLOCKS.register("reinforced_birch_planks", () -> new BaseReinforcedBlock(SoundType.WOOD, Material.WOOD, Blocks.BIRCH_PLANKS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_JUNGLE_PLANKS = BLOCKS.register("reinforced_jungle_planks", () -> new BaseReinforcedBlock(SoundType.WOOD, Material.WOOD, Blocks.JUNGLE_PLANKS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_ACACIA_PLANKS = BLOCKS.register("reinforced_acacia_planks", () -> new BaseReinforcedBlock(SoundType.WOOD, Material.WOOD, Blocks.ACACIA_PLANKS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_DARK_OAK_PLANKS = BLOCKS.register("reinforced_dark_oak_planks", () -> new BaseReinforcedBlock(SoundType.WOOD, Material.WOOD, Blocks.DARK_OAK_PLANKS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_SAND = BLOCKS.register("reinforced_sand", () -> new ReinforcedFallingBlock(SoundType.SAND, Material.SAND, Blocks.SAND));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_RED_SAND = BLOCKS.register("reinforced_red_sand", () -> new ReinforcedFallingBlock(SoundType.SAND, Material.SAND, Blocks.RED_SAND));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_GRAVEL = BLOCKS.register("reinforced_gravel", () -> new ReinforcedFallingBlock(SoundType.GROUND, Material.EARTH, Blocks.GRAVEL));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_OAK_LOG = BLOCKS.register("reinforced_oak_log", () -> new ReinforcedRotatedPillarBlock(SoundType.WOOD, Material.WOOD, Blocks.OAK_LOG));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_SPRUCE_LOG = BLOCKS.register("reinforced_spruce_log", () -> new ReinforcedRotatedPillarBlock(SoundType.WOOD, Material.WOOD, Blocks.SPRUCE_LOG));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_BIRCH_LOG = BLOCKS.register("reinforced_birch_log", () -> new ReinforcedRotatedPillarBlock(SoundType.WOOD, Material.WOOD, Blocks.BIRCH_LOG));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_JUNGLE_LOG = BLOCKS.register("reinforced_jungle_log", () -> new ReinforcedRotatedPillarBlock(SoundType.WOOD, Material.WOOD, Blocks.JUNGLE_LOG));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_ACACIA_LOG = BLOCKS.register("reinforced_acacia_log", () -> new ReinforcedRotatedPillarBlock(SoundType.WOOD, Material.WOOD, Blocks.ACACIA_LOG));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_DARK_OAK_LOG = BLOCKS.register("reinforced_dark_oak_log", () -> new ReinforcedRotatedPillarBlock(SoundType.WOOD, Material.WOOD, Blocks.DARK_OAK_LOG));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_STRIPPED_OAK_LOG = BLOCKS.register("reinforced_stripped_oak_log", () -> new ReinforcedRotatedPillarBlock(SoundType.WOOD, Material.WOOD, Blocks.STRIPPED_OAK_LOG));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_STRIPPED_SPRUCE_LOG = BLOCKS.register("reinforced_stripped_spruce_log", () -> new ReinforcedRotatedPillarBlock(SoundType.WOOD, Material.WOOD, Blocks.STRIPPED_SPRUCE_LOG));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_STRIPPED_BIRCH_LOG = BLOCKS.register("reinforced_stripped_birch_log", () -> new ReinforcedRotatedPillarBlock(SoundType.WOOD, Material.WOOD, Blocks.STRIPPED_BIRCH_LOG));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_STRIPPED_JUNGLE_LOG = BLOCKS.register("reinforced_stripped_jungle_log", () -> new ReinforcedRotatedPillarBlock(SoundType.WOOD, Material.WOOD, Blocks.STRIPPED_JUNGLE_LOG));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_STRIPPED_ACACIA_LOG = BLOCKS.register("reinforced_stripped_acacia_log", () -> new ReinforcedRotatedPillarBlock(SoundType.WOOD, Material.WOOD, Blocks.STRIPPED_ACACIA_LOG));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_STRIPPED_DARK_OAK_LOG = BLOCKS.register("reinforced_stripped_dark_oak_log", () -> new ReinforcedRotatedPillarBlock(SoundType.WOOD, Material.WOOD, Blocks.STRIPPED_DARK_OAK_LOG));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_STRIPPED_OAK_WOOD = BLOCKS.register("reinforced_stripped_oak_wood", () -> new ReinforcedRotatedPillarBlock(SoundType.WOOD, Material.WOOD, Blocks.STRIPPED_OAK_WOOD));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_STRIPPED_SPRUCE_WOOD = BLOCKS.register("reinforced_stripped_spruce_wood", () -> new ReinforcedRotatedPillarBlock(SoundType.WOOD, Material.WOOD, Blocks.STRIPPED_SPRUCE_WOOD));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_STRIPPED_BIRCH_WOOD = BLOCKS.register("reinforced_stripped_birch_wood", () -> new ReinforcedRotatedPillarBlock(SoundType.WOOD, Material.WOOD, Blocks.STRIPPED_BIRCH_WOOD));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_STRIPPED_JUNGLE_WOOD = BLOCKS.register("reinforced_stripped_jungle_wood", () -> new ReinforcedRotatedPillarBlock(SoundType.WOOD, Material.WOOD, Blocks.STRIPPED_JUNGLE_WOOD));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_STRIPPED_ACACIA_WOOD = BLOCKS.register("reinforced_stripped_acacia_wood", () -> new ReinforcedRotatedPillarBlock(SoundType.WOOD, Material.WOOD, Blocks.STRIPPED_ACACIA_WOOD));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_STRIPPED_DARK_OAK_WOOD = BLOCKS.register("reinforced_stripped_dark_oak_wood", () -> new ReinforcedRotatedPillarBlock(SoundType.WOOD, Material.WOOD, Blocks.STRIPPED_DARK_OAK_WOOD));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_OAK_WOOD = BLOCKS.register("reinforced_oak_wood", () -> new ReinforcedRotatedPillarBlock(SoundType.WOOD, Material.WOOD, Blocks.OAK_WOOD));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_SPRUCE_WOOD = BLOCKS.register("reinforced_spruce_wood", () -> new ReinforcedRotatedPillarBlock(SoundType.WOOD, Material.WOOD, Blocks.SPRUCE_WOOD));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_BIRCH_WOOD = BLOCKS.register("reinforced_birch_wood", () -> new ReinforcedRotatedPillarBlock(SoundType.WOOD, Material.WOOD, Blocks.BIRCH_WOOD));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_JUNGLE_WOOD = BLOCKS.register("reinforced_jungle_wood", () -> new ReinforcedRotatedPillarBlock(SoundType.WOOD, Material.WOOD, Blocks.JUNGLE_WOOD));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_ACACIA_WOOD = BLOCKS.register("reinforced_acacia_wood", () -> new ReinforcedRotatedPillarBlock(SoundType.WOOD, Material.WOOD, Blocks.ACACIA_WOOD));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_DARK_OAK_WOOD = BLOCKS.register("reinforced_dark_oak_wood", () -> new ReinforcedRotatedPillarBlock(SoundType.WOOD, Material.WOOD, Blocks.DARK_OAK_WOOD));
	@OwnableTE @Reinforced(hasTint=false) public static final RegistryObject<Block> REINFORCED_GLASS = BLOCKS.register("reinforced_glass", () -> new ReinforcedGlassBlock(Blocks.GLASS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_LAPIS_BLOCK = BLOCKS.register("reinforced_lapis_block", () ->  new BaseReinforcedBlock(Material.ROCK, Blocks.LAPIS_BLOCK));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_SANDSTONE = BLOCKS.register("reinforced_sandstone", () ->  new BaseReinforcedBlock(Material.ROCK, Blocks.SANDSTONE));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_CHISELED_SANDSTONE = BLOCKS.register("reinforced_chiseled_sandstone", () ->  new BaseReinforcedBlock(Material.ROCK, Blocks.CHISELED_SANDSTONE));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_CUT_SANDSTONE = BLOCKS.register("reinforced_cut_sandstone", () ->  new BaseReinforcedBlock(Material.ROCK, Blocks.CUT_SANDSTONE));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_WHITE_WOOL = BLOCKS.register("reinforced_white_wool", () -> new BaseReinforcedBlock(SoundType.CLOTH, Material.WOOL, Blocks.WHITE_WOOL));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_ORANGE_WOOL = BLOCKS.register("reinforced_orange_wool", () -> new BaseReinforcedBlock(SoundType.CLOTH, Material.WOOL, Blocks.ORANGE_WOOL));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_MAGENTA_WOOL = BLOCKS.register("reinforced_magenta_wool", () -> new BaseReinforcedBlock(SoundType.CLOTH, Material.WOOL, Blocks.MAGENTA_WOOL));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_LIGHT_BLUE_WOOL = BLOCKS.register("reinforced_light_blue_wool", () -> new BaseReinforcedBlock(SoundType.CLOTH, Material.WOOL, Blocks.LIGHT_BLUE_WOOL));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_YELLOW_WOOL = BLOCKS.register("reinforced_yellow_wool", () -> new BaseReinforcedBlock(SoundType.CLOTH, Material.WOOL, Blocks.YELLOW_WOOL));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_LIME_WOOL = BLOCKS.register("reinforced_lime_wool", () -> new BaseReinforcedBlock(SoundType.CLOTH, Material.WOOL, Blocks.LIME_WOOL));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_PINK_WOOL = BLOCKS.register("reinforced_pink_wool", () -> new BaseReinforcedBlock(SoundType.CLOTH, Material.WOOL, Blocks.PINK_WOOL));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_GRAY_WOOL = BLOCKS.register("reinforced_gray_wool", () -> new BaseReinforcedBlock(SoundType.CLOTH, Material.WOOL, Blocks.GRAY_WOOL));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_LIGHT_GRAY_WOOL = BLOCKS.register("reinforced_light_gray_wool", () -> new BaseReinforcedBlock(SoundType.CLOTH, Material.WOOL, Blocks.LIGHT_GRAY_WOOL));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_CYAN_WOOL = BLOCKS.register("reinforced_cyan_wool", () -> new BaseReinforcedBlock(SoundType.CLOTH, Material.WOOL, Blocks.CYAN_WOOL));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_PURPLE_WOOL = BLOCKS.register("reinforced_purple_wool", () -> new BaseReinforcedBlock(SoundType.CLOTH, Material.WOOL, Blocks.PURPLE_WOOL));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_BLUE_WOOL = BLOCKS.register("reinforced_blue_wool", () -> new BaseReinforcedBlock(SoundType.CLOTH, Material.WOOL, Blocks.BLUE_WOOL));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_BROWN_WOOL = BLOCKS.register("reinforced_brown_wool", () -> new BaseReinforcedBlock(SoundType.CLOTH, Material.WOOL, Blocks.BROWN_WOOL));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_GREEN_WOOL = BLOCKS.register("reinforced_green_wool", () -> new BaseReinforcedBlock(SoundType.CLOTH, Material.WOOL, Blocks.GREEN_WOOL));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_RED_WOOL = BLOCKS.register("reinforced_red_wool", () -> new BaseReinforcedBlock(SoundType.CLOTH, Material.WOOL, Blocks.RED_WOOL));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_BLACK_WOOL = BLOCKS.register("reinforced_black_wool", () -> new BaseReinforcedBlock(SoundType.CLOTH, Material.WOOL, Blocks.BLACK_WOOL));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_GOLD_BLOCK = BLOCKS.register("reinforced_gold_block", () -> new BaseReinforcedBlock(SoundType.METAL, Material.IRON, Blocks.GOLD_BLOCK));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_IRON_BLOCK = BLOCKS.register("reinforced_iron_block", () -> new BaseReinforcedBlock(SoundType.METAL, Material.IRON, Blocks.IRON_BLOCK));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_OAK_SLAB = BLOCKS.register("reinforced_oak_slab", () -> new ReinforcedSlabBlock(SoundType.WOOD, Material.WOOD, Blocks.OAK_SLAB));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_SPRUCE_SLAB = BLOCKS.register("reinforced_spruce_slab", () -> new ReinforcedSlabBlock(SoundType.WOOD, Material.WOOD, Blocks.SPRUCE_SLAB));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_BIRCH_SLAB = BLOCKS.register("reinforced_birch_slab", () -> new ReinforcedSlabBlock(SoundType.WOOD, Material.WOOD, Blocks.BIRCH_SLAB));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_JUNGLE_SLAB = BLOCKS.register("reinforced_jungle_slab", () -> new ReinforcedSlabBlock(SoundType.WOOD, Material.WOOD, Blocks.JUNGLE_SLAB));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_ACACIA_SLAB = BLOCKS.register("reinforced_acacia_slab", () -> new ReinforcedSlabBlock(SoundType.WOOD, Material.WOOD, Blocks.ACACIA_SLAB));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_DARK_OAK_SLAB = BLOCKS.register("reinforced_dark_oak_slab", () -> new ReinforcedSlabBlock(SoundType.WOOD, Material.WOOD, Blocks.DARK_OAK_SLAB));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_NORMAL_STONE_SLAB = BLOCKS.register("reinforced_normal_stone_slab", () -> new ReinforcedSlabBlock(SoundType.STONE, Material.ROCK, Blocks.STONE_SLAB));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_SMOOTH_STONE_SLAB = BLOCKS.register("reinforced_stone_slab", () -> new ReinforcedSlabBlock(SoundType.STONE, Material.ROCK, Blocks.SMOOTH_STONE_SLAB));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_SANDSTONE_SLAB = BLOCKS.register("reinforced_sandstone_slab", () -> new ReinforcedSlabBlock(SoundType.STONE, Material.ROCK, Blocks.SANDSTONE_SLAB));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_CUT_SANDSTONE_SLAB = BLOCKS.register("reinforced_cut_sandstone_slab", () -> new ReinforcedSlabBlock(SoundType.STONE, Material.ROCK, Blocks.CUT_SANDSTONE_SLAB));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_COBBLESTONE_SLAB = BLOCKS.register("reinforced_cobblestone_slab", () -> new ReinforcedSlabBlock(SoundType.STONE, Material.ROCK, Blocks.COBBLESTONE_SLAB));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_BRICK_SLAB = BLOCKS.register("reinforced_brick_slab", () -> new ReinforcedSlabBlock(SoundType.STONE, Material.ROCK, Blocks.BRICK_SLAB));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_STONE_BRICK_SLAB = BLOCKS.register("reinforced_stone_brick_slab", () -> new ReinforcedSlabBlock(SoundType.STONE, Material.ROCK, Blocks.STONE_BRICK_SLAB));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_NETHER_BRICK_SLAB = BLOCKS.register("reinforced_nether_brick_slab", () -> new ReinforcedSlabBlock(SoundType.STONE, Material.ROCK, Blocks.NETHER_BRICK_SLAB));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_QUARTZ_SLAB = BLOCKS.register("reinforced_quartz_slab", () -> new ReinforcedSlabBlock(SoundType.STONE, Material.ROCK, Blocks.QUARTZ_SLAB));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_RED_SANDSTONE_SLAB = BLOCKS.register("reinforced_red_sandstone_slab", () -> new ReinforcedSlabBlock(SoundType.STONE, Material.ROCK, Blocks.RED_SANDSTONE_SLAB));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_CUT_RED_SANDSTONE_SLAB = BLOCKS.register("reinforced_cut_red_sandstone_slab", () -> new ReinforcedSlabBlock(SoundType.STONE, Material.ROCK, Blocks.CUT_RED_SANDSTONE_SLAB));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_PURPUR_SLAB = BLOCKS.register("reinforced_purpur_slab", () -> new ReinforcedSlabBlock(SoundType.STONE, Material.ROCK, Blocks.PURPUR_SLAB));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_PRISMARINE_SLAB = BLOCKS.register("reinforced_prismarine_slab", () -> new ReinforcedSlabBlock(SoundType.STONE, Material.ROCK, Blocks.PRISMARINE_SLAB));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_PRISMARINE_BRICK_SLAB = BLOCKS.register("reinforced_prismarine_brick_slab", () -> new ReinforcedSlabBlock(SoundType.STONE, Material.ROCK, Blocks.PRISMARINE_BRICK_SLAB));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_DARK_PRISMARINE_SLAB = BLOCKS.register("reinforced_dark_prismarine_slab", () -> new ReinforcedSlabBlock(SoundType.STONE, Material.ROCK, Blocks.DARK_PRISMARINE_SLAB));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_SMOOTH_QUARTZ = BLOCKS.register("reinforced_smooth_quartz", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.SMOOTH_QUARTZ));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_SMOOTH_RED_SANDSTONE = BLOCKS.register("reinforced_smooth_red_sandstone", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.SMOOTH_RED_SANDSTONE));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_SMOOTH_SANDSTONE = BLOCKS.register("reinforced_smooth_sandstone", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.SMOOTH_SANDSTONE));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_SMOOTH_STONE = BLOCKS.register("reinforced_smooth_stone", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.SMOOTH_STONE));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_BRICKS = BLOCKS.register("reinforced_bricks", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.BRICKS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_BOOKSHELF = BLOCKS.register("reinforced_bookshelf", () -> new BaseReinforcedBlock(SoundType.WOOD, Material.WOOD, Blocks.BOOKSHELF));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_MOSSY_COBBLESTONE = BLOCKS.register("reinforced_mossy_cobblestone", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.MOSSY_COBBLESTONE));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_OBSIDIAN = BLOCKS.register("reinforced_obsidian", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.OBSIDIAN));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_PURPUR_BLOCK = BLOCKS.register("reinforced_purpur_block", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.PURPUR_BLOCK));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_PURPUR_PILLAR = BLOCKS.register("reinforced_purpur_pillar", () -> new ReinforcedRotatedPillarBlock(Material.ROCK, Blocks.PURPUR_PILLAR));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_PURPUR_STAIRS = BLOCKS.register("reinforced_purpur_stairs", () -> new ReinforcedStairsBlock(SoundType.STONE, Material.ROCK, Blocks.PURPUR_STAIRS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_OAK_STAIRS = BLOCKS.register("reinforced_oak_stairs", () -> new ReinforcedStairsBlock(SoundType.WOOD, Material.WOOD, Blocks.OAK_STAIRS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_DIAMOND_BLOCK = BLOCKS.register("reinforced_diamond_block", () -> new BaseReinforcedBlock(SoundType.METAL, Material.IRON, Blocks.DIAMOND_BLOCK));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_COBBLESTONE_STAIRS = BLOCKS.register("reinforced_cobblestone_stairs", () -> new ReinforcedStairsBlock(SoundType.STONE, Material.ROCK, Blocks.COBBLESTONE_STAIRS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_ICE = BLOCKS.register("reinforced_ice", () -> new BaseReinforcedBlock(Block.Properties.create(Material.ICE).hardnessAndResistance(-1.0F, 6000000.0F).slipperiness(0.98F), SoundType.GLASS, Blocks.ICE));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_SNOW_BLOCK = BLOCKS.register("reinforced_snow_block", () -> new BaseReinforcedBlock(SoundType.SNOW, Material.SNOW_BLOCK, Blocks.SNOW_BLOCK));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_CLAY = BLOCKS.register("reinforced_clay", () -> new BaseReinforcedBlock(SoundType.GROUND, Material.CLAY, Blocks.CLAY));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_NETHERRACK = BLOCKS.register("reinforced_netherrack", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.NETHERRACK));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_GLOWSTONE = BLOCKS.register("reinforced_glowstone", () -> new BaseReinforcedBlock(SoundType.GLASS, Material.GLASS, Blocks.GLOWSTONE, 15));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_STONE_BRICKS = BLOCKS.register("reinforced_stone_bricks", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.STONE_BRICKS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_MOSSY_STONE_BRICKS = BLOCKS.register("reinforced_mossy_stone_bricks", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.MOSSY_STONE_BRICKS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_CRACKED_STONE_BRICKS = BLOCKS.register("reinforced_cracked_stone_bricks", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.CRACKED_STONE_BRICKS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_CHISELED_STONE_BRICKS = BLOCKS.register("reinforced_chiseled_stone_bricks", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.CHISELED_STONE_BRICKS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_BRICK_STAIRS = BLOCKS.register("reinforced_brick_stairs", () -> new ReinforcedStairsBlock(SoundType.STONE, Material.ROCK, Blocks.BRICK_STAIRS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_STONE_BRICK_STAIRS = BLOCKS.register("reinforced_stone_brick_stairs", () -> new ReinforcedStairsBlock(SoundType.STONE, Material.ROCK, Blocks.STONE_BRICK_STAIRS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_MYCELIUM = BLOCKS.register("reinforced_mycelium", () -> new ReinforcedSnowyDirtBlock(Material.ORGANIC, SoundType.PLANT, Blocks.MYCELIUM));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_NETHER_BRICKS = BLOCKS.register("reinforced_nether_bricks", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.NETHER_BRICKS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_NETHER_BRICK_STAIRS = BLOCKS.register("reinforced_nether_brick_stairs", () -> new ReinforcedStairsBlock(SoundType.STONE, Material.ROCK, Blocks.NETHER_BRICK_STAIRS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_END_STONE = BLOCKS.register("reinforced_end_stone", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.END_STONE));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_END_STONE_BRICKS = BLOCKS.register("reinforced_end_stone_bricks", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.END_STONE_BRICKS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_SANDSTONE_STAIRS = BLOCKS.register("reinforced_sandstone_stairs", () -> new ReinforcedStairsBlock(SoundType.STONE, Material.ROCK, Blocks.SANDSTONE_STAIRS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_EMERALD_BLOCK = BLOCKS.register("reinforced_emerald_block", () -> new BaseReinforcedBlock(SoundType.METAL, Material.IRON, Blocks.EMERALD_BLOCK));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_SPRUCE_STAIRS = BLOCKS.register("reinforced_spruce_stairs", () -> new ReinforcedStairsBlock(SoundType.WOOD, Material.WOOD, Blocks.SPRUCE_STAIRS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_BIRCH_STAIRS = BLOCKS.register("reinforced_birch_stairs", () -> new ReinforcedStairsBlock(SoundType.WOOD, Material.WOOD, Blocks.BIRCH_STAIRS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_JUNGLE_STAIRS = BLOCKS.register("reinforced_jungle_stairs", () -> new ReinforcedStairsBlock(SoundType.WOOD, Material.WOOD, Blocks.JUNGLE_STAIRS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_CHISELED_QUARTZ = BLOCKS.register("reinforced_chiseled_quartz_block", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.CHISELED_QUARTZ_BLOCK));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_QUARTZ = BLOCKS.register("reinforced_quartz_block", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.QUARTZ_BLOCK));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_QUARTZ_PILLAR = BLOCKS.register("reinforced_quartz_pillar", () -> new ReinforcedRotatedPillarBlock(Material.ROCK, Blocks.QUARTZ_PILLAR));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_QUARTZ_STAIRS = BLOCKS.register("reinforced_quartz_stairs", () -> new ReinforcedStairsBlock(SoundType.STONE, Material.ROCK, Blocks.QUARTZ_STAIRS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_WHITE_TERRACOTTA = BLOCKS.register("reinforced_white_terracotta", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.WHITE_TERRACOTTA));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_ORANGE_TERRACOTTA = BLOCKS.register("reinforced_orange_terracotta", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.ORANGE_TERRACOTTA));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_MAGENTA_TERRACOTTA = BLOCKS.register("reinforced_magenta_terracotta", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.MAGENTA_TERRACOTTA));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_LIGHT_BLUE_TERRACOTTA = BLOCKS.register("reinforced_light_blue_terracotta", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.LIGHT_BLUE_TERRACOTTA));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_YELLOW_TERRACOTTA = BLOCKS.register("reinforced_yellow_terracotta", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.YELLOW_TERRACOTTA));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_LIME_TERRACOTTA = BLOCKS.register("reinforced_lime_terracotta", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.LIME_TERRACOTTA));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_PINK_TERRACOTTA = BLOCKS.register("reinforced_pink_terracotta", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.PINK_TERRACOTTA));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_GRAY_TERRACOTTA = BLOCKS.register("reinforced_gray_terracotta", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.GRAY_TERRACOTTA));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_LIGHT_GRAY_TERRACOTTA = BLOCKS.register("reinforced_light_gray_terracotta", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.LIGHT_GRAY_TERRACOTTA));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_CYAN_TERRACOTTA = BLOCKS.register("reinforced_cyan_terracotta", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.CYAN_TERRACOTTA));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_PURPLE_TERRACOTTA = BLOCKS.register("reinforced_purple_terracotta", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.PURPLE_TERRACOTTA));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_BLUE_TERRACOTTA = BLOCKS.register("reinforced_blue_terracotta", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.BLUE_TERRACOTTA));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_BROWN_TERRACOTTA = BLOCKS.register("reinforced_brown_terracotta", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.BROWN_TERRACOTTA));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_GREEN_TERRACOTTA = BLOCKS.register("reinforced_green_terracotta", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.GREEN_TERRACOTTA));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_RED_TERRACOTTA = BLOCKS.register("reinforced_red_terracotta", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.RED_TERRACOTTA));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_BLACK_TERRACOTTA = BLOCKS.register("reinforced_black_terracotta", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.BLACK_TERRACOTTA));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_TERRACOTTA = BLOCKS.register("reinforced_hardened_clay", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.TERRACOTTA));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_COAL_BLOCK = BLOCKS.register("reinforced_coal_block", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.COAL_BLOCK));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_PACKED_ICE = BLOCKS.register("reinforced_packed_ice", () -> new BaseReinforcedBlock(SoundType.GLASS, Material.PACKED_ICE, Blocks.PACKED_ICE, 0.98F));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_ACACIA_STAIRS = BLOCKS.register("reinforced_acacia_stairs", () -> new ReinforcedStairsBlock(SoundType.WOOD, Material.WOOD, Blocks.ACACIA_STAIRS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_DARK_OAK_STAIRS = BLOCKS.register("reinforced_dark_oak_stairs", () -> new ReinforcedStairsBlock(SoundType.WOOD, Material.WOOD, Blocks.DARK_OAK_STAIRS));
	@OwnableTE @Reinforced(hasTint=false) public static final RegistryObject<Block> REINFORCED_WHITE_STAINED_GLASS = BLOCKS.register("reinforced_white_stained_glass", () -> new ReinforcedStainedGlassBlock(DyeColor.WHITE, Blocks.WHITE_STAINED_GLASS));
	@OwnableTE @Reinforced(hasTint=false) public static final RegistryObject<Block> REINFORCED_ORANGE_STAINED_GLASS = BLOCKS.register("reinforced_orange_stained_glass", () -> new ReinforcedStainedGlassBlock(DyeColor.ORANGE, Blocks.ORANGE_STAINED_GLASS));
	@OwnableTE @Reinforced(hasTint=false) public static final RegistryObject<Block> REINFORCED_MAGENTA_STAINED_GLASS = BLOCKS.register("reinforced_magenta_stained_glass", () -> new ReinforcedStainedGlassBlock(DyeColor.MAGENTA, Blocks.MAGENTA_STAINED_GLASS));
	@OwnableTE @Reinforced(hasTint=false) public static final RegistryObject<Block> REINFORCED_LIGHT_BLUE_STAINED_GLASS = BLOCKS.register("reinforced_light_blue_stained_glass", () -> new ReinforcedStainedGlassBlock(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_STAINED_GLASS));
	@OwnableTE @Reinforced(hasTint=false) public static final RegistryObject<Block> REINFORCED_YELLOW_STAINED_GLASS = BLOCKS.register("reinforced_yellow_stained_glass", () -> new ReinforcedStainedGlassBlock(DyeColor.YELLOW, Blocks.YELLOW_STAINED_GLASS));
	@OwnableTE @Reinforced(hasTint=false) public static final RegistryObject<Block> REINFORCED_LIME_STAINED_GLASS = BLOCKS.register("reinforced_lime_stained_glass", () -> new ReinforcedStainedGlassBlock(DyeColor.LIME, Blocks.LIME_STAINED_GLASS));
	@OwnableTE @Reinforced(hasTint=false) public static final RegistryObject<Block> REINFORCED_PINK_STAINED_GLASS = BLOCKS.register("reinforced_pink_stained_glass", () -> new ReinforcedStainedGlassBlock(DyeColor.PINK, Blocks.PINK_STAINED_GLASS));
	@OwnableTE @Reinforced(hasTint=false) public static final RegistryObject<Block> REINFORCED_GRAY_STAINED_GLASS = BLOCKS.register("reinforced_gray_stained_glass", () -> new ReinforcedStainedGlassBlock(DyeColor.GRAY, Blocks.GRAY_STAINED_GLASS));
	@OwnableTE @Reinforced(hasTint=false) public static final RegistryObject<Block> REINFORCED_LIGHT_GRAY_STAINED_GLASS = BLOCKS.register("reinforced_light_gray_stained_glass", () -> new ReinforcedStainedGlassBlock(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_STAINED_GLASS));
	@OwnableTE @Reinforced(hasTint=false) public static final RegistryObject<Block> REINFORCED_CYAN_STAINED_GLASS = BLOCKS.register("reinforced_cyan_stained_glass", () -> new ReinforcedStainedGlassBlock(DyeColor.CYAN, Blocks.CYAN_STAINED_GLASS));
	@OwnableTE @Reinforced(hasTint=false) public static final RegistryObject<Block> REINFORCED_PURPLE_STAINED_GLASS = BLOCKS.register("reinforced_purple_stained_glass", () -> new ReinforcedStainedGlassBlock(DyeColor.PURPLE, Blocks.PURPLE_STAINED_GLASS));
	@OwnableTE @Reinforced(hasTint=false) public static final RegistryObject<Block> REINFORCED_BLUE_STAINED_GLASS = BLOCKS.register("reinforced_blue_stained_glass", () -> new ReinforcedStainedGlassBlock(DyeColor.BLUE, Blocks.BLUE_STAINED_GLASS));
	@OwnableTE @Reinforced(hasTint=false) public static final RegistryObject<Block> REINFORCED_BROWN_STAINED_GLASS = BLOCKS.register("reinforced_brown_stained_glass", () -> new ReinforcedStainedGlassBlock(DyeColor.BROWN, Blocks.BROWN_STAINED_GLASS));
	@OwnableTE @Reinforced(hasTint=false) public static final RegistryObject<Block> REINFORCED_GREEN_STAINED_GLASS = BLOCKS.register("reinforced_green_stained_glass", () -> new ReinforcedStainedGlassBlock(DyeColor.GREEN, Blocks.GREEN_STAINED_GLASS));
	@OwnableTE @Reinforced(hasTint=false) public static final RegistryObject<Block> REINFORCED_RED_STAINED_GLASS = BLOCKS.register("reinforced_red_stained_glass", () -> new ReinforcedStainedGlassBlock(DyeColor.RED, Blocks.RED_STAINED_GLASS));
	@OwnableTE @Reinforced(hasTint=false) public static final RegistryObject<Block> REINFORCED_BLACK_STAINED_GLASS = BLOCKS.register("reinforced_black_stained_glass", () -> new ReinforcedStainedGlassBlock(DyeColor.BLACK, Blocks.BLACK_STAINED_GLASS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_PRISMARINE = BLOCKS.register("reinforced_prismarine", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.PRISMARINE));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_PRISMARINE_BRICKS = BLOCKS.register("reinforced_prismarine_bricks", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.PRISMARINE_BRICKS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_DARK_PRISMARINE = BLOCKS.register("reinforced_dark_prismarine", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.DARK_PRISMARINE));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_PRISMARINE_STAIRS = BLOCKS.register("reinforced_prismarine_stairs", () -> new ReinforcedStairsBlock(SoundType.STONE, Material.ROCK, Blocks.PRISMARINE_STAIRS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_PRISMARINE_BRICK_STAIRS = BLOCKS.register("reinforced_prismarine_brick_stairs", () -> new ReinforcedStairsBlock(SoundType.STONE, Material.ROCK, Blocks.PRISMARINE_BRICK_STAIRS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_DARK_PRISMARINE_STAIRS = BLOCKS.register("reinforced_dark_prismarine_stairs", () -> new ReinforcedStairsBlock(SoundType.STONE, Material.ROCK, Blocks.DARK_PRISMARINE_STAIRS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_SEA_LANTERN = BLOCKS.register("reinforced_sea_lantern", () -> new BaseReinforcedBlock(SoundType.GLASS, Material.GLASS, Blocks.SEA_LANTERN, 15));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_RED_SANDSTONE = BLOCKS.register("reinforced_red_sandstone", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.RED_SANDSTONE));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_CHISELED_RED_SANDSTONE = BLOCKS.register("reinforced_chiseled_red_sandstone", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.CHISELED_RED_SANDSTONE));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_CUT_RED_SANDSTONE = BLOCKS.register("reinforced_cut_red_sandstone", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.CUT_RED_SANDSTONE));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_RED_SANDSTONE_STAIRS = BLOCKS.register("reinforced_red_sandstone_stairs", () -> new ReinforcedStairsBlock(SoundType.STONE, Material.ROCK, Blocks.RED_SANDSTONE_STAIRS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_NETHER_WART_BLOCK = BLOCKS.register("reinforced_nether_wart_block", () -> new BaseReinforcedBlock(SoundType.WOOD, Material.ORGANIC, Blocks.NETHER_WART_BLOCK));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_RED_NETHER_BRICKS = BLOCKS.register("reinforced_red_nether_bricks", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.RED_NETHER_BRICKS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_BONE_BLOCK = BLOCKS.register("reinforced_bone_block", () -> new ReinforcedRotatedPillarBlock(Material.ROCK, Blocks.BONE_BLOCK));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_WHITE_CONCRETE = BLOCKS.register("reinforced_white_concrete", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.WHITE_CONCRETE));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_ORANGE_CONCRETE = BLOCKS.register("reinforced_orange_concrete", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.ORANGE_CONCRETE));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_MAGENTA_CONCRETE = BLOCKS.register("reinforced_magenta_concrete", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.MAGENTA_CONCRETE));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_LIGHT_BLUE_CONCRETE = BLOCKS.register("reinforced_light_blue_concrete", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.LIGHT_BLUE_CONCRETE));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_YELLOW_CONCRETE = BLOCKS.register("reinforced_yellow_concrete", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.YELLOW_CONCRETE));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_LIME_CONCRETE = BLOCKS.register("reinforced_lime_concrete", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.LIME_CONCRETE));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_PINK_CONCRETE = BLOCKS.register("reinforced_pink_concrete", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.PINK_CONCRETE));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_GRAY_CONCRETE = BLOCKS.register("reinforced_gray_concrete", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.GRAY_CONCRETE));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_LIGHT_GRAY_CONCRETE = BLOCKS.register("reinforced_light_gray_concrete", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.LIGHT_GRAY_CONCRETE));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_CYAN_CONCRETE = BLOCKS.register("reinforced_cyan_concrete", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.CYAN_CONCRETE));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_PURPLE_CONCRETE = BLOCKS.register("reinforced_purple_concrete", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.PURPLE_CONCRETE));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_BLUE_CONCRETE = BLOCKS.register("reinforced_blue_concrete", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.BLUE_CONCRETE));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_BROWN_CONCRETE = BLOCKS.register("reinforced_brown_concrete", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.BROWN_CONCRETE));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_GREEN_CONCRETE = BLOCKS.register("reinforced_green_concrete", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.GREEN_CONCRETE));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_RED_CONCRETE = BLOCKS.register("reinforced_red_concrete", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.RED_CONCRETE));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_BLACK_CONCRETE = BLOCKS.register("reinforced_black_concrete", () -> new BaseReinforcedBlock(Material.ROCK, Blocks.BLACK_CONCRETE));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_BLUE_ICE = BLOCKS.register("reinforced_blue_ice", () -> new BaseReinforcedBlock(SoundType.GLASS, Material.PACKED_ICE, Blocks.BLUE_ICE, 0.989F));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_POLISHED_GRANITE_STAIRS = BLOCKS.register("reinforced_polished_granite_stairs", () -> new ReinforcedStairsBlock(SoundType.STONE, Material.ROCK, Blocks.POLISHED_GRANITE_STAIRS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_SMOOTH_RED_SANDSTONE_STAIRS = BLOCKS.register("reinforced_smooth_red_sandstone_stairs", () -> new ReinforcedStairsBlock(SoundType.STONE, Material.ROCK, Blocks.SMOOTH_RED_SANDSTONE_STAIRS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_MOSSY_STONE_BRICK_STAIRS = BLOCKS.register("reinforced_mossy_stone_brick_stairs", () -> new ReinforcedStairsBlock(SoundType.STONE, Material.ROCK, Blocks.MOSSY_STONE_BRICK_STAIRS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_POLISHED_DIORITE_STAIRS = BLOCKS.register("reinforced_polished_diorite_stairs", () -> new ReinforcedStairsBlock(SoundType.STONE, Material.ROCK, Blocks.POLISHED_DIORITE_STAIRS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_MOSSY_COBBLESTONE_STAIRS = BLOCKS.register("reinforced_mossy_cobblestone_stairs", () -> new ReinforcedStairsBlock(SoundType.STONE, Material.ROCK, Blocks.MOSSY_COBBLESTONE_STAIRS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_END_STONE_BRICK_STAIRS = BLOCKS.register("reinforced_end_stone_brick_stairs", () -> new ReinforcedStairsBlock(SoundType.STONE, Material.ROCK, Blocks.END_STONE_BRICK_STAIRS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_STONE_STAIRS = BLOCKS.register("reinforced_stone_stairs", () -> new ReinforcedStairsBlock(SoundType.STONE, Material.ROCK, Blocks.STONE_STAIRS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_SMOOTH_SANDSTONE_STAIRS = BLOCKS.register("reinforced_smooth_sandstone_stairs", () -> new ReinforcedStairsBlock(SoundType.STONE, Material.ROCK, Blocks.SMOOTH_SANDSTONE_STAIRS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_SMOOTH_QUARTZ_STAIRS = BLOCKS.register("reinforced_smooth_quartz_stairs", () -> new ReinforcedStairsBlock(SoundType.STONE, Material.ROCK, Blocks.SMOOTH_QUARTZ_STAIRS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_GRANITE_STAIRS = BLOCKS.register("reinforced_granite_stairs", () -> new ReinforcedStairsBlock(SoundType.STONE, Material.ROCK, Blocks.GRANITE_STAIRS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_ANDESITE_STAIRS = BLOCKS.register("reinforced_andesite_stairs", () -> new ReinforcedStairsBlock(SoundType.STONE, Material.ROCK, Blocks.ANDESITE_STAIRS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_RED_NETHER_BRICK_STAIRS = BLOCKS.register("reinforced_red_nether_brick_stairs", () -> new ReinforcedStairsBlock(SoundType.STONE, Material.ROCK, Blocks.RED_NETHER_BRICK_STAIRS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_POLISHED_ANDESITE_STAIRS = BLOCKS.register("reinforced_polished_andesite_stairs", () -> new ReinforcedStairsBlock(SoundType.STONE, Material.ROCK, Blocks.POLISHED_ANDESITE_STAIRS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_DIORITE_STAIRS = BLOCKS.register("reinforced_diorite_stairs", () -> new ReinforcedStairsBlock(SoundType.STONE, Material.ROCK, Blocks.DIORITE_STAIRS));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_POLISHED_GRANITE_SLAB = BLOCKS.register("reinforced_polished_granite_slab", () -> new ReinforcedSlabBlock(SoundType.STONE, Material.ROCK, Blocks.POLISHED_GRANITE_SLAB));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_SMOOTH_RED_SANDSTONE_SLAB = BLOCKS.register("reinforced_smooth_red_sandstone_slab", () -> new ReinforcedSlabBlock(SoundType.STONE, Material.ROCK, Blocks.SMOOTH_RED_SANDSTONE_SLAB));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_MOSSY_STONE_BRICK_SLAB = BLOCKS.register("reinforced_mossy_stone_brick_slab", () -> new ReinforcedSlabBlock(SoundType.STONE, Material.ROCK, Blocks.MOSSY_STONE_BRICK_SLAB));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_POLISHED_DIORITE_SLAB = BLOCKS.register("reinforced_polished_diorite_slab", () -> new ReinforcedSlabBlock(SoundType.STONE, Material.ROCK, Blocks.POLISHED_DIORITE_SLAB));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_MOSSY_COBBLESTONE_SLAB = BLOCKS.register("reinforced_mossy_cobblestone_slab", () -> new ReinforcedSlabBlock(SoundType.STONE, Material.ROCK, Blocks.MOSSY_COBBLESTONE_SLAB));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_END_STONE_BRICK_SLAB = BLOCKS.register("reinforced_end_stone_brick_slab", () -> new ReinforcedSlabBlock(SoundType.STONE, Material.ROCK, Blocks.END_STONE_BRICK_SLAB));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_SMOOTH_SANDSTONE_SLAB = BLOCKS.register("reinforced_smooth_sandstone_slab", () -> new ReinforcedSlabBlock(SoundType.STONE, Material.ROCK, Blocks.SMOOTH_SANDSTONE_SLAB));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_SMOOTH_QUARTZ_SLAB = BLOCKS.register("reinforced_smooth_quartz_slab", () -> new ReinforcedSlabBlock(SoundType.STONE, Material.ROCK, Blocks.SMOOTH_QUARTZ_SLAB));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_GRANITE_SLAB = BLOCKS.register("reinforced_granite_slab", () -> new ReinforcedSlabBlock(SoundType.STONE, Material.ROCK, Blocks.GRANITE_SLAB));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_ANDESITE_SLAB = BLOCKS.register("reinforced_andesite_slab", () -> new ReinforcedSlabBlock(SoundType.STONE, Material.ROCK, Blocks.ANDESITE_SLAB));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_RED_NETHER_BRICK_SLAB = BLOCKS.register("reinforced_red_nether_brick_slab", () -> new ReinforcedSlabBlock(SoundType.STONE, Material.ROCK, Blocks.RED_NETHER_BRICK_SLAB));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_POLISHED_ANDESITE_SLAB = BLOCKS.register("reinforced_polished_andesite_slab", () -> new ReinforcedSlabBlock(SoundType.STONE, Material.ROCK, Blocks.POLISHED_ANDESITE_SLAB));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_DIORITE_SLAB = BLOCKS.register("reinforced_diorite_slab", () -> new ReinforcedSlabBlock(SoundType.STONE, Material.ROCK, Blocks.DIORITE_SLAB));
	//ordered by vanilla decoration blocks creative tab order
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_COBWEB = BLOCKS.register("reinforced_cobweb", () -> new BaseReinforcedBlock(Block.Properties.create(Material.WEB).doesNotBlockMovement(), Blocks.COBWEB));
	@OwnableTE @Reinforced(hasTint=false) public static final RegistryObject<Block> REINFORCED_IRON_BARS = BLOCKS.register("reinforced_iron_bars", () -> new ReinforcedPaneBlock(SoundType.METAL, Material.IRON, Blocks.IRON_BARS));
	@OwnableTE @Reinforced(hasTint=false) public static final RegistryObject<Block> REINFORCED_GLASS_PANE = BLOCKS.register("reinforced_glass_pane", () -> new ReinforcedPaneBlock(SoundType.GLASS, Material.GLASS, Blocks.GLASS_PANE));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_COBBLESTONE_WALL = BLOCKS.register("reinforced_cobblestone_wall", () -> new ReinforcedWallBlock(Blocks.COBBLESTONE_WALL));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_MOSSY_COBBLESTONE_WALL = BLOCKS.register("reinforced_mossy_cobblestone_wall", () -> new ReinforcedWallBlock(Blocks.MOSSY_COBBLESTONE_WALL));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_BRICK_WALL = BLOCKS.register("reinforced_brick_wall", () -> new ReinforcedWallBlock(Blocks.BRICK_WALL));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_PRISMARINE_WALL = BLOCKS.register("reinforced_prismarine_wall", () -> new ReinforcedWallBlock(Blocks.PRISMARINE_WALL));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_RED_SANDSTONE_WALL = BLOCKS.register("reinforced_red_sandstone_wall", () -> new ReinforcedWallBlock(Blocks.RED_SANDSTONE_WALL));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_MOSSY_STONE_BRICK_WALL = BLOCKS.register("reinforced_mossy_stone_brick_wall", () -> new ReinforcedWallBlock(Blocks.MOSSY_STONE_BRICK_WALL));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_GRANITE_WALL = BLOCKS.register("reinforced_granite_wall", () -> new ReinforcedWallBlock(Blocks.GRANITE_WALL));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_STONE_BRICK_WALL = BLOCKS.register("reinforced_stone_brick_wall", () -> new ReinforcedWallBlock(Blocks.STONE_BRICK_WALL));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_NETHER_BRICK_WALL = BLOCKS.register("reinforced_nether_brick_wall", () -> new ReinforcedWallBlock(Blocks.NETHER_BRICK_WALL));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_ANDESITE_WALL = BLOCKS.register("reinforced_andesite_wall", () -> new ReinforcedWallBlock(Blocks.ANDESITE_WALL));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_RED_NETHER_BRICK_WALL = BLOCKS.register("reinforced_red_nether_brick_wall", () -> new ReinforcedWallBlock(Blocks.RED_NETHER_BRICK_WALL));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_SANDSTONE_WALL = BLOCKS.register("reinforced_sandstone_wall", () -> new ReinforcedWallBlock(Blocks.SANDSTONE_WALL));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_END_STONE_BRICK_WALL = BLOCKS.register("reinforced_end_stone_brick_wall", () -> new ReinforcedWallBlock(Blocks.END_STONE_BRICK_WALL));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_DIORITE_WALL = BLOCKS.register("reinforced_diorite_wall", () -> new ReinforcedWallBlock(Blocks.DIORITE_WALL));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_WHITE_CARPET = BLOCKS.register("reinforced_white_carpet", () -> new ReinforcedCarpetBlock(Blocks.WHITE_CARPET));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_ORANGE_CARPET = BLOCKS.register("reinforced_orange_carpet", () -> new ReinforcedCarpetBlock(Blocks.ORANGE_CARPET));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_MAGENTA_CARPET = BLOCKS.register("reinforced_magenta_carpet", () -> new ReinforcedCarpetBlock(Blocks.MAGENTA_CARPET));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_LIGHT_BLUE_CARPET = BLOCKS.register("reinforced_light_blue_carpet", () -> new ReinforcedCarpetBlock(Blocks.LIGHT_BLUE_CARPET));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_YELLOW_CARPET = BLOCKS.register("reinforced_yellow_carpet", () -> new ReinforcedCarpetBlock(Blocks.YELLOW_CARPET));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_LIME_CARPET = BLOCKS.register("reinforced_lime_carpet", () -> new ReinforcedCarpetBlock(Blocks.LIME_CARPET));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_PINK_CARPET = BLOCKS.register("reinforced_pink_carpet", () -> new ReinforcedCarpetBlock(Blocks.PINK_CARPET));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_GRAY_CARPET = BLOCKS.register("reinforced_gray_carpet", () -> new ReinforcedCarpetBlock(Blocks.GRAY_CARPET));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_LIGHT_GRAY_CARPET = BLOCKS.register("reinforced_light_gray_carpet", () -> new ReinforcedCarpetBlock(Blocks.LIGHT_GRAY_CARPET));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_CYAN_CARPET = BLOCKS.register("reinforced_cyan_carpet", () -> new ReinforcedCarpetBlock(Blocks.CYAN_CARPET));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_PURPLE_CARPET = BLOCKS.register("reinforced_purple_carpet", () -> new ReinforcedCarpetBlock(Blocks.PURPLE_CARPET));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_BLUE_CARPET = BLOCKS.register("reinforced_blue_carpet", () -> new ReinforcedCarpetBlock(Blocks.BLUE_CARPET));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_BROWN_CARPET = BLOCKS.register("reinforced_brown_carpet", () -> new ReinforcedCarpetBlock(Blocks.BROWN_CARPET));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_GREEN_CARPET = BLOCKS.register("reinforced_green_carpet", () -> new ReinforcedCarpetBlock(Blocks.GREEN_CARPET));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_RED_CARPET = BLOCKS.register("reinforced_red_carpet", () -> new ReinforcedCarpetBlock(Blocks.RED_CARPET));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_BLACK_CARPET = BLOCKS.register("reinforced_black_carpet", () -> new ReinforcedCarpetBlock(Blocks.BLACK_CARPET));
	@OwnableTE @Reinforced(hasTint=false) public static final RegistryObject<Block> REINFORCED_WHITE_STAINED_GLASS_PANE = BLOCKS.register("reinforced_white_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(DyeColor.WHITE, Blocks.WHITE_STAINED_GLASS_PANE));
	@OwnableTE @Reinforced(hasTint=false) public static final RegistryObject<Block> REINFORCED_ORANGE_STAINED_GLASS_PANE = BLOCKS.register("reinforced_orange_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(DyeColor.ORANGE, Blocks.ORANGE_STAINED_GLASS_PANE));
	@OwnableTE @Reinforced(hasTint=false) public static final RegistryObject<Block> REINFORCED_MAGENTA_STAINED_GLASS_PANE = BLOCKS.register("reinforced_magenta_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(DyeColor.MAGENTA, Blocks.MAGENTA_STAINED_GLASS_PANE));
	@OwnableTE @Reinforced(hasTint=false) public static final RegistryObject<Block> REINFORCED_LIGHT_BLUE_STAINED_GLASS_PANE = BLOCKS.register("reinforced_light_blue_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE));
	@OwnableTE @Reinforced(hasTint=false) public static final RegistryObject<Block> REINFORCED_YELLOW_STAINED_GLASS_PANE = BLOCKS.register("reinforced_yellow_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(DyeColor.YELLOW, Blocks.YELLOW_STAINED_GLASS_PANE));
	@OwnableTE @Reinforced(hasTint=false) public static final RegistryObject<Block> REINFORCED_LIME_STAINED_GLASS_PANE = BLOCKS.register("reinforced_lime_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(DyeColor.LIME, Blocks.LIME_STAINED_GLASS_PANE));
	@OwnableTE @Reinforced(hasTint=false) public static final RegistryObject<Block> REINFORCED_PINK_STAINED_GLASS_PANE = BLOCKS.register("reinforced_pink_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(DyeColor.PINK, Blocks.PINK_STAINED_GLASS_PANE));
	@OwnableTE @Reinforced(hasTint=false) public static final RegistryObject<Block> REINFORCED_GRAY_STAINED_GLASS_PANE = BLOCKS.register("reinforced_gray_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(DyeColor.GRAY, Blocks.GRAY_STAINED_GLASS_PANE));
	@OwnableTE @Reinforced(hasTint=false) public static final RegistryObject<Block> REINFORCED_LIGHT_GRAY_STAINED_GLASS_PANE = BLOCKS.register("reinforced_light_gray_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE));
	@OwnableTE @Reinforced(hasTint=false) public static final RegistryObject<Block> REINFORCED_CYAN_STAINED_GLASS_PANE = BLOCKS.register("reinforced_cyan_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(DyeColor.CYAN, Blocks.CYAN_STAINED_GLASS_PANE));
	@OwnableTE @Reinforced(hasTint=false) public static final RegistryObject<Block> REINFORCED_PURPLE_STAINED_GLASS_PANE = BLOCKS.register("reinforced_purple_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(DyeColor.PURPLE, Blocks.PURPLE_STAINED_GLASS_PANE));
	@OwnableTE @Reinforced(hasTint=false) public static final RegistryObject<Block> REINFORCED_BLUE_STAINED_GLASS_PANE = BLOCKS.register("reinforced_blue_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(DyeColor.BLUE, Blocks.BLUE_STAINED_GLASS_PANE));
	@OwnableTE @Reinforced(hasTint=false) public static final RegistryObject<Block> REINFORCED_BROWN_STAINED_GLASS_PANE = BLOCKS.register("reinforced_brown_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(DyeColor.BROWN, Blocks.BROWN_STAINED_GLASS_PANE));
	@OwnableTE @Reinforced(hasTint=false) public static final RegistryObject<Block> REINFORCED_GREEN_STAINED_GLASS_PANE = BLOCKS.register("reinforced_green_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(DyeColor.GREEN, Blocks.GREEN_STAINED_GLASS_PANE));
	@OwnableTE @Reinforced(hasTint=false) public static final RegistryObject<Block> REINFORCED_RED_STAINED_GLASS_PANE = BLOCKS.register("reinforced_red_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(DyeColor.RED, Blocks.RED_STAINED_GLASS_PANE));
	@OwnableTE @Reinforced(hasTint=false) public static final RegistryObject<Block> REINFORCED_BLACK_STAINED_GLASS_PANE = BLOCKS.register("reinforced_black_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(DyeColor.BLACK, Blocks.BLACK_STAINED_GLASS_PANE));
	//ordered by vanilla redstone tab order
	@HasManualPage @Reinforced public static final RegistryObject<Block> REINFORCED_LEVER = BLOCKS.register("reinforced_lever", () -> new ReinforcedLeverBlock(Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(-1.0F, 6000000.0F).sound(SoundType.WOOD)));
	@HasManualPage @Reinforced public static final RegistryObject<Block> REINFORCED_STONE_PRESSURE_PLATE = BLOCKS.register("reinforced_stone_pressure_plate", () -> new ReinforcedPressurePlateBlock(Sensitivity.MOBS, ReinforcedPressurePlateBlock.STONE_PROPERTIES, Blocks.STONE_PRESSURE_PLATE));
	@Reinforced public static final RegistryObject<Block> REINFORCED_OAK_PRESSURE_PLATE = BLOCKS.register("reinforced_oak_pressure_plate", () -> new ReinforcedPressurePlateBlock(Sensitivity.EVERYTHING, ReinforcedPressurePlateBlock.WOOD_PROPERTIES, Blocks.OAK_PRESSURE_PLATE));
	@Reinforced public static final RegistryObject<Block> REINFORCED_SPRUCE_PRESSURE_PLATE = BLOCKS.register("reinforced_spruce_pressure_plate", () -> new ReinforcedPressurePlateBlock(Sensitivity.EVERYTHING, ReinforcedPressurePlateBlock.WOOD_PROPERTIES, Blocks.SPRUCE_PRESSURE_PLATE));
	@Reinforced public static final RegistryObject<Block> REINFORCED_BIRCH_PRESSURE_PLATE = BLOCKS.register("reinforced_birch_pressure_plate", () -> new ReinforcedPressurePlateBlock(Sensitivity.EVERYTHING, ReinforcedPressurePlateBlock.WOOD_PROPERTIES, Blocks.BIRCH_PRESSURE_PLATE));
	@Reinforced public static final RegistryObject<Block> REINFORCED_JUNGLE_PRESSURE_PLATE = BLOCKS.register("reinforced_jungle_pressure_plate", () -> new ReinforcedPressurePlateBlock(Sensitivity.EVERYTHING, ReinforcedPressurePlateBlock.WOOD_PROPERTIES, Blocks.JUNGLE_PRESSURE_PLATE));
	@Reinforced public static final RegistryObject<Block> REINFORCED_ACACIA_PRESSURE_PLATE = BLOCKS.register("reinforced_acacia_pressure_plate", () -> new ReinforcedPressurePlateBlock(Sensitivity.EVERYTHING, ReinforcedPressurePlateBlock.WOOD_PROPERTIES, Blocks.ACACIA_PRESSURE_PLATE));
	@Reinforced public static final RegistryObject<Block> REINFORCED_DARK_OAK_PRESSURE_PLATE = BLOCKS.register("reinforced_dark_oak_pressure_plate", () -> new ReinforcedPressurePlateBlock(Sensitivity.EVERYTHING, ReinforcedPressurePlateBlock.WOOD_PROPERTIES, Blocks.DARK_OAK_PRESSURE_PLATE));
	@HasManualPage @Reinforced public static final RegistryObject<Block> REINFORCED_STONE_BUTTON = BLOCKS.register("reinforced_stone_button", () -> new ReinforcedButtonBlock(false, ReinforcedButtonBlock.STONE_PROPERTIES, Blocks.STONE_BUTTON));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_REDSTONE_LAMP = BLOCKS.register("reinforced_redstone_lamp", () -> new ReinforcedRedstoneLampBlock());
	@Reinforced public static final RegistryObject<Block> REINFORCED_OAK_BUTTON = BLOCKS.register("reinforced_oak_button", () -> new ReinforcedButtonBlock(true, ReinforcedButtonBlock.WOOD_PROPERTIES, Blocks.OAK_BUTTON));
	@Reinforced public static final RegistryObject<Block> REINFORCED_SPRUCE_BUTTON = BLOCKS.register("reinforced_spruce_button", () -> new ReinforcedButtonBlock(true, ReinforcedButtonBlock.WOOD_PROPERTIES, Blocks.SPRUCE_BUTTON));
	@Reinforced public static final RegistryObject<Block> REINFORCED_BIRCH_BUTTON = BLOCKS.register("reinforced_birch_button", () -> new ReinforcedButtonBlock(true, ReinforcedButtonBlock.WOOD_PROPERTIES, Blocks.BIRCH_BUTTON));
	@Reinforced public static final RegistryObject<Block> REINFORCED_JUNGLE_BUTTON = BLOCKS.register("reinforced_jungle_button", () -> new ReinforcedButtonBlock(true, ReinforcedButtonBlock.WOOD_PROPERTIES, Blocks.JUNGLE_BUTTON));
	@Reinforced public static final RegistryObject<Block> REINFORCED_ACACIA_BUTTON = BLOCKS.register("reinforced_acacia_button", () -> new ReinforcedButtonBlock(true, ReinforcedButtonBlock.WOOD_PROPERTIES, Blocks.ACACIA_BUTTON));
	@Reinforced public static final RegistryObject<Block> REINFORCED_DARK_OAK_BUTTON = BLOCKS.register("reinforced_dark_oak_button", () -> new ReinforcedButtonBlock(true, ReinforcedButtonBlock.WOOD_PROPERTIES, Blocks.DARK_OAK_BUTTON));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_REDSTONE_BLOCK = BLOCKS.register("reinforced_redstone_block", () -> new ReinforcedRedstoneBlock());
	@HasManualPage @Reinforced public static final RegistryObject<Block> REINFORCED_HOPPER = BLOCKS.register("reinforced_hopper", () -> new ReinforcedHopperBlock());
	@HasManualPage @OwnableTE @Reinforced(hasTint=false) public static final RegistryObject<Block> REINFORCED_IRON_TRAPDOOR = BLOCKS.register("reinforced_iron_trapdoor", () -> new ReinforcedIronTrapDoorBlock(Material.IRON));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_OBSERVER = BLOCKS.register("reinforced_observer", () -> new ReinforcedObserverBlock());
	//misc
	@Reinforced(tint=0x0E7063) public static final RegistryObject<Block> REINFORCED_CHISELED_CRYSTAL_QUARTZ = BLOCKS.register("reinforced_chiseled_crystal_quartz_block", () -> new BlockPocketBlock(Material.ROCK, SCContent.CHISELED_CRYSTAL_QUARTZ));
	@Reinforced(tint=0x0E7063) public static final RegistryObject<Block> REINFORCED_CRYSTAL_QUARTZ = BLOCKS.register("reinforced_crystal_quartz_block", () -> new BlockPocketBlock(Material.ROCK, SCContent.CRYSTAL_QUARTZ));
	@Reinforced(tint=0x0E7063) public static final RegistryObject<Block> REINFORCED_CRYSTAL_QUARTZ_PILLAR = BLOCKS.register("reinforced_crystal_quartz_pillar", () -> new ReinforcedRotatedCrystalQuartzPillar(Material.ROCK, SCContent.CRYSTAL_QUARTZ_PILLAR));
	@Reinforced(tint=0x0E7063) public static final RegistryObject<Block> REINFORCED_CRYSTAL_QUARTZ_SLAB = BLOCKS.register("reinforced_crystal_quartz_slab", () -> new ReinforcedSlabBlock(SoundType.STONE, Material.ROCK, SCContent.CRYSTAL_QUARTZ_SLAB));
	@Reinforced(tint=0x0E7063) public static final RegistryObject<Block> REINFORCED_CRYSTAL_QUARTZ_STAIRS = BLOCKS.register("reinforced_crystal_quartz_stairs", () -> new ReinforcedStairsBlock(SoundType.STONE, Material.ROCK, SCContent.STAIRS_CRYSTAL_QUARTZ));
	@OwnableTE public static final RegistryObject<Block> HORIZONTAL_REINFORCED_IRON_BARS = BLOCKS.register("horizontal_reinforced_iron_bars", () -> new HorizontalReinforcedIronBars(SoundType.METAL, Material.IRON, Blocks.IRON_BLOCK));
	@OwnableTE @Reinforced public static final RegistryObject<Block> REINFORCED_GRASS_PATH = BLOCKS.register("reinforced_grass_path", () -> new BaseReinforcedBlock(SoundType.PLANT, Material.EARTH, Blocks.GRASS_PATH));

	//fluids
	public static final RegistryObject<FlowingFluid> FLOWING_FAKE_WATER = FLUIDS.register("flowing_fake_water", () -> new FakeWaterFluid.Flowing());
	public static final RegistryObject<FlowingFluid> FAKE_WATER = FLUIDS.register("fake_water", () -> new FakeWaterFluid.Source());
	public static final RegistryObject<FlowingFluid> FLOWING_FAKE_LAVA = FLUIDS.register("flowing_fake_lava", () -> new FakeLavaFluid.Flowing());
	public static final RegistryObject<FlowingFluid> FAKE_LAVA = FLUIDS.register("fake_lava", () -> new FakeLavaFluid.Source());

	//items
	@HasManualPage public static final RegistryObject<Item> ADMIN_TOOL = ITEMS.register("admin_tool", () -> new AdminToolItem());
	@HasManualPage public static final RegistryObject<Item> BRIEFCASE = ITEMS.register("briefcase", () -> new BriefcaseItem());
	@HasManualPage public static final RegistryObject<Item> CAMERA_MONITOR = ITEMS.register("camera_monitor", () -> new CameraMonitorItem());
	@HasManualPage public static final RegistryObject<Item> CODEBREAKER = ITEMS.register("codebreaker", () -> new CodebreakerItem());
	@HasManualPage public static final RegistryObject<Item> CRYSTAL_QUARTZ_ITEM = ITEMS.register("crystal_quartz_item", () -> new Item(new Item.Properties().group(SecurityCraft.groupSCDecoration)));
	@HasManualPage public static final RegistryObject<Item> FAKE_LAVA_BUCKET = ITEMS.register("bucket_f_lava", () -> new FakeLiquidBucketItem(SCContent.FAKE_LAVA, new Item.Properties().group(SecurityCraft.groupSCTechnical).maxStackSize(1)));
	@HasManualPage public static final RegistryObject<Item> FAKE_WATER_BUCKET = ITEMS.register("bucket_f_water", () -> new FakeLiquidBucketItem(SCContent.FAKE_WATER, new Item.Properties().group(SecurityCraft.groupSCTechnical).maxStackSize(1)));
	@HasManualPage public static final RegistryObject<Item> KEYCARD_LVL_1 = ITEMS.register("keycard_lv1", () -> new BaseKeycardItem(0));
	@HasManualPage public static final RegistryObject<Item> KEYCARD_LVL_2 = ITEMS.register("keycard_lv2", () -> new BaseKeycardItem(1));
	@HasManualPage public static final RegistryObject<Item> KEYCARD_LVL_3 = ITEMS.register("keycard_lv3", () -> new BaseKeycardItem(2));
	@HasManualPage public static final RegistryObject<Item> KEYCARD_LVL_4 = ITEMS.register("keycard_lv4", () -> new BaseKeycardItem(4));
	@HasManualPage public static final RegistryObject<Item> KEYCARD_LVL_5 = ITEMS.register("keycard_lv5", () -> new BaseKeycardItem(5));
	@HasManualPage public static final RegistryObject<Item> KEY_PANEL = ITEMS.register("keypad_item", () -> new KeyPanelItem());
	public static final RegistryObject<Item> KEYPAD_CHEST_ITEM = ITEMS.register(KEYPAD_CHEST_PATH, () -> new BlockItem(SCContent.KEYPAD_CHEST.get(), new Item.Properties().group(SecurityCraft.groupSCTechnical).setISTER(() -> ItemKeypadChestRenderer::new))); //keep this as a method reference or else the server will crash
	@HasManualPage public static final RegistryObject<Item> LIMITED_USE_KEYCARD = ITEMS.register("limited_use_keycard", () -> new BaseKeycardItem(3));
	@HasManualPage public static final RegistryObject<Item> REINFORCED_DOOR_ITEM = ITEMS.register("door_indestructible_iron_item", () -> new ReinforcedDoorItem());
	@HasManualPage public static final RegistryObject<Item> REMOTE_ACCESS_MINE = ITEMS.register("remote_access_mine", () -> new MineRemoteAccessToolItem());
	@HasManualPage public static final RegistryObject<Item> REMOTE_ACCESS_SENTRY = ITEMS.register("remote_access_sentry", () -> new SentryRemoteAccessToolItem());
	@HasManualPage public static final RegistryObject<Item> SCANNER_DOOR_ITEM = ITEMS.register("scanner_door_item", () -> new ScannerDoorItem());
	@HasManualPage public static final RegistryObject<Item> SC_MANUAL = ITEMS.register("sc_manual", () -> new SCManualItem());
	@HasManualPage public static final RegistryObject<Item> SECRET_OAK_SIGN_ITEM = ITEMS.register("secret_sign_item", () -> new SecretSignItem(SCContent.SECRET_OAK_SIGN.get(), SCContent.SECRET_OAK_WALL_SIGN.get(), "item.securitycraft.secret_sign_item"));
	public static final RegistryObject<Item> SECRET_SPRUCE_SIGN_ITEM = ITEMS.register("secret_spruce_sign_item", () -> new SecretSignItem(SCContent.SECRET_SPRUCE_SIGN.get(), SCContent.SECRET_SPRUCE_WALL_SIGN.get(), "item.securitycraft.secret_spruce_sign_item"));
	public static final RegistryObject<Item> SECRET_BIRCH_SIGN_ITEM = ITEMS.register("secret_birch_sign_item", () -> new SecretSignItem(SCContent.SECRET_BIRCH_SIGN.get(), SCContent.SECRET_BIRCH_WALL_SIGN.get(), "item.securitycraft.secret_birch_sign_item"));
	public static final RegistryObject<Item> SECRET_JUNGLE_SIGN_ITEM = ITEMS.register("secret_jungle_sign_item", () -> new SecretSignItem(SCContent.SECRET_JUNGLE_SIGN.get(), SCContent.SECRET_JUNGLE_WALL_SIGN.get(), "item.securitycraft.secret_jungle_sign_item"));
	public static final RegistryObject<Item> SECRET_ACACIA_SIGN_ITEM = ITEMS.register("secret_acacia_sign_item", () -> new SecretSignItem(SCContent.SECRET_ACACIA_SIGN.get(), SCContent.SECRET_ACACIA_WALL_SIGN.get(), "item.securitycraft.secret_acacia_sign_item"));
	public static final RegistryObject<Item> SECRET_DARK_OAK_SIGN_ITEM = ITEMS.register("secret_dark_oak_sign_item", () -> new SecretSignItem(SCContent.SECRET_DARK_OAK_SIGN.get(), SCContent.SECRET_DARK_OAK_WALL_SIGN.get(), "item.securitycraft.secret_dark_oak_sign_item"));
	@HasManualPage(designedBy="Henzoid") public static final RegistryObject<Item> SENTRY = ITEMS.register("sentry", () -> new SentryItem());
	@HasManualPage public static final RegistryObject<Item> TASER = ITEMS.register("taser", () -> new TaserItem(false));
	public static final RegistryObject<Item> TASER_POWERED = ITEMS.register("taser_powered", () -> new TaserItem(true));
	@HasManualPage public static final RegistryObject<Item> UNIVERSAL_BLOCK_MODIFIER = ITEMS.register("universal_block_modifier", () -> new UniversalBlockModifierItem(new Item.Properties().maxStackSize(1).group(SecurityCraft.groupSCTechnical)));
	@HasManualPage public static final RegistryObject<Item> UNIVERSAL_BLOCK_REINFORCER_LVL_1 = ITEMS.register("universal_block_reinforcer_lvl1", () -> new UniversalBlockReinforcerItem(300));
	@HasManualPage public static final RegistryObject<Item> UNIVERSAL_BLOCK_REINFORCER_LVL_2 = ITEMS.register("universal_block_reinforcer_lvl2", () -> new UniversalBlockReinforcerItem(2700));
	@HasManualPage public static final RegistryObject<Item> UNIVERSAL_BLOCK_REINFORCER_LVL_3 = ITEMS.register("universal_block_reinforcer_lvl3", () -> new UniversalBlockReinforcerItem(0));
	@HasManualPage public static final RegistryObject<Item> UNIVERSAL_BLOCK_REMOVER = ITEMS.register("universal_block_remover", () -> new UniversalBlockRemoverItem(new Item.Properties().maxStackSize(1).defaultMaxDamage(476).group(SecurityCraft.groupSCTechnical)));
	@HasManualPage public static final RegistryObject<Item> UNIVERSAL_KEY_CHANGER = ITEMS.register("universal_key_changer", () -> new UniversalKeyChangerItem());
	@HasManualPage public static final RegistryObject<Item> UNIVERSAL_OWNER_CHANGER = ITEMS.register("universal_owner_changer", () -> new UniversalOwnerChangerItem());
	@HasManualPage public static final RegistryObject<Item> WIRE_CUTTERS = ITEMS.register("wire_cutters", () -> new Item(new Item.Properties().maxStackSize(1).defaultMaxDamage(476).group(SecurityCraft.groupSCTechnical)));

	//modules
	@HasManualPage public static final RegistryObject<ModuleItem> BLACKLIST_MODULE = ITEMS.register("blacklist_module", () -> new ModuleItem(ModuleType.BLACKLIST, true, true));
	@HasManualPage public static final RegistryObject<ModuleItem> DISGUISE_MODULE = ITEMS.register("disguise_module", () -> new ModuleItem(ModuleType.DISGUISE, false, true, 0, 1));
	@HasManualPage public static final RegistryObject<ModuleItem> HARMING_MODULE = ITEMS.register("harming_module", () -> new ModuleItem(ModuleType.HARMING, false));
	@HasManualPage public static final RegistryObject<ModuleItem> REDSTONE_MODULE = ITEMS.register("redstone_module", () -> new ModuleItem(ModuleType.REDSTONE, false));
	@HasManualPage public static final RegistryObject<ModuleItem> SMART_MODULE = ITEMS.register("smart_module", () -> new ModuleItem(ModuleType.SMART, false));
	@HasManualPage public static final RegistryObject<ModuleItem> STORAGE_MODULE = ITEMS.register("storage_module", () -> new ModuleItem(ModuleType.STORAGE, false));
	@HasManualPage public static final RegistryObject<ModuleItem> WHITELIST_MODULE = ITEMS.register("whitelist_module", () -> new ModuleItem(ModuleType.WHITELIST, true, true));

	//tile entity types
	@ObjectHolder(SecurityCraft.MODID + ":ownable")
	public static TileEntityType<OwnableTileEntity> teTypeOwnable;
	@ObjectHolder(SecurityCraft.MODID + ":abstract")
	public static TileEntityType<SecurityCraftTileEntity> teTypeAbstract;
	@ObjectHolder(SecurityCraft.MODID + ":keypad")
	public static TileEntityType<KeypadTileEntity> teTypeKeypad;
	@ObjectHolder(SecurityCraft.MODID + ":laser_block")
	public static TileEntityType<LaserBlockTileEntity> teTypeLaserBlock;
	@ObjectHolder(SecurityCraft.MODID + ":cage_trap")
	public static TileEntityType<CageTrapTileEntity> teTypeCageTrap;
	@ObjectHolder(SecurityCraft.MODID + ":keycard_reader")
	public static TileEntityType<KeycardReaderTileEntity> teTypeKeycardReader;
	@ObjectHolder(SecurityCraft.MODID + ":inventory_scanner")
	public static TileEntityType<InventoryScannerTileEntity> teTypeInventoryScanner;
	@ObjectHolder(SecurityCraft.MODID + ":portable_radar")
	public static TileEntityType<PortableRadarTileEntity> teTypePortableRadar;
	@ObjectHolder(SecurityCraft.MODID + ":security_camera")
	public static TileEntityType<SecurityCameraTileEntity> teTypeSecurityCamera;
	@ObjectHolder(SecurityCraft.MODID + ":username_logger")
	public static TileEntityType<UsernameLoggerTileEntity> teTypeUsernameLogger;
	@ObjectHolder(SecurityCraft.MODID + ":retinal_scanner")
	public static TileEntityType<RetinalScannerTileEntity> teTypeRetinalScanner;
	@ObjectHolder(SecurityCraft.MODID + ":keypad_chest")
	public static TileEntityType<KeypadChestTileEntity> teTypeKeypadChest;
	@ObjectHolder(SecurityCraft.MODID + ":alarm")
	public static TileEntityType<AlarmTileEntity> teTypeAlarm;
	@ObjectHolder(SecurityCraft.MODID + ":claymore")
	public static TileEntityType<ClaymoreTileEntity> teTypeClaymore;
	@ObjectHolder(SecurityCraft.MODID + ":keypad_furnace")
	public static TileEntityType<KeypadFurnaceTileEntity> teTypeKeypadFurnace;
	@ObjectHolder(SecurityCraft.MODID + ":ims")
	public static TileEntityType<IMSTileEntity> teTypeIms;
	@ObjectHolder(SecurityCraft.MODID + ":protecto")
	public static TileEntityType<ProtectoTileEntity> teTypeProtecto;
	@ObjectHolder(SecurityCraft.MODID + ":scanner_door")
	public static TileEntityType<ScannerDoorTileEntity> teTypeScannerDoor;
	@ObjectHolder(SecurityCraft.MODID + ":secret_sign")
	public static TileEntityType<SecretSignTileEntity> teTypeSecretSign;
	@ObjectHolder(SecurityCraft.MODID + ":motion_light")
	public static TileEntityType<MotionActivatedLightTileEntity> teTypeMotionLight;
	@ObjectHolder(SecurityCraft.MODID + ":track_mine")
	public static TileEntityType<TrackMineTileEntity> teTypeTrackMine;
	@ObjectHolder(SecurityCraft.MODID + ":trophy_system")
	public static TileEntityType<TrophySystemTileEntity> teTypeTrophySystem;
	@ObjectHolder(SecurityCraft.MODID + ":block_pocket_manager")
	public static TileEntityType<BlockPocketManagerTileEntity> teTypeBlockPocketManager;
	@ObjectHolder(SecurityCraft.MODID + ":block_pocket")
	public static TileEntityType<BlockPocketTileEntity> teTypeBlockPocket;
	@ObjectHolder(SecurityCraft.MODID + ":reinforced_pressure_plate")
	public static TileEntityType<WhitelistOnlyTileEntity> teTypeWhitelistOnly;
	@ObjectHolder(SecurityCraft.MODID + ":reinforced_hopper")
	public static TileEntityType<ReinforcedHopperTileEntity> teTypeReinforcedHopper;
	@ObjectHolder(SecurityCraft.MODID + ":projector")
	public static TileEntityType<ProjectorTileEntity> teTypeProjector;

	//entity types
	@ObjectHolder(SecurityCraft.MODID + ":bouncingbetty")
	public static EntityType<BouncingBettyEntity> eTypeBouncingBetty;
	@ObjectHolder(SecurityCraft.MODID + ":taserbullet")
	public static EntityType<TaserBulletEntity> eTypeTaserBullet;
	@ObjectHolder(SecurityCraft.MODID + ":imsbomb")
	public static EntityType<IMSBombEntity> eTypeImsBomb;
	@ObjectHolder(SecurityCraft.MODID + ":securitycamera")
	public static EntityType<SecurityCameraEntity> eTypeSecurityCamera;
	@ObjectHolder(SecurityCraft.MODID + ":sentry")
	public static EntityType<SentryEntity> eTypeSentry;
	@ObjectHolder(SecurityCraft.MODID + ":bullet")
	public static EntityType<BulletEntity> eTypeBullet;

	//container types
	@ObjectHolder(SecurityCraft.MODID + ":block_reinforcer")
	public static ContainerType<BlockReinforcerContainer> cTypeBlockReinforcer;
	@ObjectHolder(SecurityCraft.MODID + ":briefcase")
	public static ContainerType<GenericContainer> cTypeBriefcase;
	@ObjectHolder(SecurityCraft.MODID + ":briefcase_inventory")
	public static ContainerType<BriefcaseContainer> cTypeBriefcaseInventory;
	@ObjectHolder(SecurityCraft.MODID + ":briefcase_setup")
	public static ContainerType<GenericContainer> cTypeBriefcaseSetup;
	@ObjectHolder(SecurityCraft.MODID + ":customize_block")
	public static ContainerType<CustomizeBlockContainer> cTypeCustomizeBlock;
	@ObjectHolder(SecurityCraft.MODID + ":disguise_module")
	public static ContainerType<DisguiseModuleContainer> cTypeDisguiseModule;
	@ObjectHolder(SecurityCraft.MODID + ":inventory_scanner")
	public static ContainerType<InventoryScannerContainer> cTypeInventoryScanner;
	@ObjectHolder(SecurityCraft.MODID + ":keypad_furnace")
	public static ContainerType<KeypadFurnaceContainer> cTypeKeypadFurnace;
	@ObjectHolder(SecurityCraft.MODID + ":projector")
	public static ContainerType<ProjectorContainer> cTypeProjector;
	@ObjectHolder(SecurityCraft.MODID + ":check_password")
	public static ContainerType<GenericTEContainer> cTypeCheckPassword;
	@ObjectHolder(SecurityCraft.MODID + ":set_password")
	public static ContainerType<GenericTEContainer> cTypeSetPassword;
	@ObjectHolder(SecurityCraft.MODID + ":username_logger")
	public static ContainerType<GenericTEContainer> cTypeUsernameLogger;
	@ObjectHolder(SecurityCraft.MODID + ":ims")
	public static ContainerType<GenericTEContainer> cTypeIMS;
	@ObjectHolder(SecurityCraft.MODID + ":keycard_setup")
	public static ContainerType<GenericTEContainer> cTypeKeycardSetup;
	@ObjectHolder(SecurityCraft.MODID + ":key_changer")
	public static ContainerType<GenericTEContainer> cTypeKeyChanger;
	@ObjectHolder(SecurityCraft.MODID + ":block_pocket_manager")
	public static ContainerType<GenericTEContainer> cTypeBlockPocketManager;
}
