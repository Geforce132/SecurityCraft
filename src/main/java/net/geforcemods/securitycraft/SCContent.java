package net.geforcemods.securitycraft;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.base.Predicates;

import net.geforcemods.securitycraft.api.NamedBlockEntity;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.geforcemods.securitycraft.blockentities.AllowlistOnlyBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockPocketBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.geforcemods.securitycraft.blockentities.BouncingBettyBlockEntity;
import net.geforcemods.securitycraft.blockentities.BrushableMineBlockEntity;
import net.geforcemods.securitycraft.blockentities.CageTrapBlockEntity;
import net.geforcemods.securitycraft.blockentities.ClaymoreBlockEntity;
import net.geforcemods.securitycraft.blockentities.DisguisableBlockEntity;
import net.geforcemods.securitycraft.blockentities.DisplayCaseBlockEntity;
import net.geforcemods.securitycraft.blockentities.ElectrifiedFenceAndGateBlockEntity;
import net.geforcemods.securitycraft.blockentities.FloorTrapBlockEntity;
import net.geforcemods.securitycraft.blockentities.GlowDisplayCaseBlockEntity;
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
import net.geforcemods.securitycraft.blockentities.MineBlockEntity;
import net.geforcemods.securitycraft.blockentities.MotionActivatedLightBlockEntity;
import net.geforcemods.securitycraft.blockentities.PortableRadarBlockEntity;
import net.geforcemods.securitycraft.blockentities.ProjectorBlockEntity;
import net.geforcemods.securitycraft.blockentities.ProtectoBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedCauldronBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedChiseledBookshelfBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedDispenserBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedDropperBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedFenceGateBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedHopperBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedIronBarsBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedLecternBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedObserverBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedPistonMovingBlockEntity;
import net.geforcemods.securitycraft.blockentities.RetinalScannerBlockEntity;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity;
import net.geforcemods.securitycraft.blockentities.ScannerDoorBlockEntity;
import net.geforcemods.securitycraft.blockentities.ScannerTrapdoorBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecretHangingSignBlockEntity;
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
import net.geforcemods.securitycraft.blocks.SecretCeilingHangingSignBlock;
import net.geforcemods.securitycraft.blocks.SecretStandingSignBlock;
import net.geforcemods.securitycraft.blocks.SecretWallHangingSignBlock;
import net.geforcemods.securitycraft.blocks.SecretWallSignBlock;
import net.geforcemods.securitycraft.blocks.SecureRedstoneInterfaceBlock;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.blocks.SometimesVisibleBlock;
import net.geforcemods.securitycraft.blocks.SonicSecuritySystemBlock;
import net.geforcemods.securitycraft.blocks.TrophySystemBlock;
import net.geforcemods.securitycraft.blocks.UsernameLoggerBlock;
import net.geforcemods.securitycraft.blocks.mines.BaseFullMineBlock;
import net.geforcemods.securitycraft.blocks.mines.BouncingBettyBlock;
import net.geforcemods.securitycraft.blocks.mines.BrushableMineBlock;
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
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedButtonBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedCarpetBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedCauldronBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedCauldronBlock.IReinforcedCauldronInteraction;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedChainBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedChiseledBookshelfBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedCobwebBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedCopperBulbBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedCopperGrateBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedCryingObsidianBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedDirtPathBlock;
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
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedHopperBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedIronBarsBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedIronTrapDoorBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedLadderBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedLanternBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedLavaCauldronBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedLayeredCauldronBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedLecternBlock;
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
import net.geforcemods.securitycraft.commands.LowercasedEnumArgument;
import net.geforcemods.securitycraft.commands.SingleGameProfileArgument;
import net.geforcemods.securitycraft.entity.BouncingBetty;
import net.geforcemods.securitycraft.entity.IMSBomb;
import net.geforcemods.securitycraft.entity.SecuritySeaBoat;
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
import net.geforcemods.securitycraft.items.DisplayCaseItem;
import net.geforcemods.securitycraft.items.FakeLiquidBucketItem;
import net.geforcemods.securitycraft.items.KeyPanelItem;
import net.geforcemods.securitycraft.items.KeycardHolderItem;
import net.geforcemods.securitycraft.items.KeycardItem;
import net.geforcemods.securitycraft.items.KeypadChestItem;
import net.geforcemods.securitycraft.items.LensItem;
import net.geforcemods.securitycraft.items.MineRemoteAccessToolItem;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.items.PortableTunePlayerItem;
import net.geforcemods.securitycraft.items.SCManualItem;
import net.geforcemods.securitycraft.items.SecuritySeaBoatItem;
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
import net.geforcemods.securitycraft.misc.BlockEntityNBTCondition;
import net.geforcemods.securitycraft.misc.ItemStackListSerializer;
import net.geforcemods.securitycraft.misc.ModuleStatesSerializer;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.OwnerDataSerializer;
import net.geforcemods.securitycraft.misc.PageGroup;
import net.geforcemods.securitycraft.particle.InterfaceHighlightParticleType;
import net.geforcemods.securitycraft.recipe.BlockReinforcingRecipe;
import net.geforcemods.securitycraft.recipe.BlockUnreinforcingRecipe;
import net.geforcemods.securitycraft.recipe.LimitedUseKeycardRecipe;
import net.geforcemods.securitycraft.util.HasManualPage;
import net.geforcemods.securitycraft.util.OwnableBE;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.RegisterItemBlock;
import net.geforcemods.securitycraft.util.Reinforced;
import net.geforcemods.securitycraft.util.SCItemGroup;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;

public class SCContent {
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SecurityCraft.MODID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SecurityCraft.MODID);
	public static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, SecurityCraft.MODID);
	public static final DeferredRegister<EntityDataSerializer<?>> DATA_SERIALIZERS = DeferredRegister.create(Keys.ENTITY_DATA_SERIALIZERS, SecurityCraft.MODID);
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, SecurityCraft.MODID);
	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, SecurityCraft.MODID);
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SecurityCraft.MODID);
	public static final DeferredRegister<LootItemConditionType> LOOT_ITEM_CONDITION_TYPES = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, SecurityCraft.MODID);
	public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, SecurityCraft.MODID);
	public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, SecurityCraft.MODID);
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, SecurityCraft.MODID);
	public static final String KEYPAD_CHEST_PATH = "keypad_chest";
	public static final String DISPLAY_CASE_PATH = "display_case";
	public static final String GLOW_DISPLAY_CASE_PATH = "glow_display_case";

	//command argument types
	public static final DeferredHolder<ArgumentTypeInfo<?, ?>, SingletonArgumentInfo<SingleGameProfileArgument>> SINGLE_GAME_PROFILE_COMMAND_ARGUMENT_TYPE = COMMAND_ARGUMENT_TYPES.register("single_game_profile", () -> ArgumentTypeInfos.registerByClass(SingleGameProfileArgument.class, SingletonArgumentInfo.contextFree(SingleGameProfileArgument::singleGameProfile)));
	@SuppressWarnings("rawtypes")
	public static final Holder<ArgumentTypeInfo<?, ?>> LOWERCASED_ENUM_COMMAND_ARGUMENT_TYPE = COMMAND_ARGUMENT_TYPES.register("lowercased_enum", () -> ArgumentTypeInfos.registerByClass(LowercasedEnumArgument.class, new LowercasedEnumArgument.Info()));

	//loot item condition types
	public static final DeferredHolder<LootItemConditionType, LootItemConditionType> BLOCK_ENTITY_NBT = LOOT_ITEM_CONDITION_TYPES.register("tile_entity_nbt", () -> new LootItemConditionType(BlockEntityNBTCondition.CODEC));

	//recipe serializers
	public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<BlockReinforcingRecipe>> BLOCK_REINFORCING_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("block_reinforcing_recipe", () -> new SimpleCraftingRecipeSerializer<>(BlockReinforcingRecipe::new));
	public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<BlockUnreinforcingRecipe>> BLOCK_UNREINFORCING_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("block_unreinforcing_recipe", () -> new SimpleCraftingRecipeSerializer<>(BlockUnreinforcingRecipe::new));
	public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<LimitedUseKeycardRecipe>> LIMITED_USE_KEYCARD_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("limited_use_keycard_recipe", () -> new SimpleCraftingRecipeSerializer<>(LimitedUseKeycardRecipe::new));

	//data serializer entries
	public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<Owner>> OWNER_SERIALIZER = DATA_SERIALIZERS.register("owner", () -> new OwnerDataSerializer());
	public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<Map<ModuleType, Boolean>>> MODULE_STATES_SERIALIZER = DATA_SERIALIZERS.register("module_states", () -> new ModuleStatesSerializer());
	public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<NonNullList<ItemStack>>> ITEM_STACK_LIST_SERIALIZER = DATA_SERIALIZERS.register("item_stack_list", () -> new ItemStackListSerializer());

	//particle types
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FLOOR_TRAP_CLOUD = PARTICLE_TYPES.register("floor_trap_cloud", () -> new SimpleParticleType(false));
	public static final DeferredHolder<ParticleType<?>, InterfaceHighlightParticleType> INTERFACE_HIGHLIGHT = PARTICLE_TYPES.register("interface_highlight", () -> new InterfaceHighlightParticleType(false));

	//fluids
	public static final DeferredHolder<Fluid, FakeWaterFluid.Flowing> FLOWING_FAKE_WATER = FLUIDS.register("flowing_fake_water", () -> new FakeWaterFluid.Flowing(fakeWaterProperties()));
	public static final DeferredHolder<Fluid, FakeWaterFluid.Source> FAKE_WATER = FLUIDS.register("fake_water", () -> new FakeWaterFluid.Source(fakeWaterProperties()));
	public static final DeferredHolder<Fluid, FakeLavaFluid.Flowing> FLOWING_FAKE_LAVA = FLUIDS.register("flowing_fake_lava", () -> new FakeLavaFluid.Flowing(fakeLavaProperties()));
	public static final DeferredHolder<Fluid, FakeLavaFluid.Source> FAKE_LAVA = FLUIDS.register("fake_lava", () -> new FakeLavaFluid.Source(fakeLavaProperties()));

	//blocks
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<AlarmBlock> ALARM = BLOCKS.register("alarm", () -> new AlarmBlock(prop(MapColor.METAL).lightLevel(state -> state.getValue(AlarmBlock.LIT) ? 15 : 0)));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<BlockChangeDetectorBlock> BLOCK_CHANGE_DETECTOR = BLOCKS.register("block_change_detector", () -> new BlockChangeDetectorBlock(propDisguisable()));
	@HasManualPage(designedBy = "Henzoid")
	@RegisterItemBlock
	public static final DeferredBlock<BlockPocketManagerBlock> BLOCK_POCKET_MANAGER = BLOCKS.register("block_pocket_manager", () -> new BlockPocketManagerBlock(prop(MapColor.COLOR_CYAN)));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<BlockPocketWallBlock> BLOCK_POCKET_WALL = BLOCKS.register("block_pocket_wall", () -> new BlockPocketWallBlock(prop(MapColor.COLOR_CYAN).noCollission().isRedstoneConductor(SCContent::never).isSuffocating(BlockPocketWallBlock::causesSuffocation).isViewBlocking(BlockPocketWallBlock::causesSuffocation)));
	@HasManualPage
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BouncingBettyBlock> BOUNCING_BETTY = BLOCKS.register("bouncing_betty", () -> new BouncingBettyBlock(prop(MapColor.METAL, 1.0F).forceSolidOn().pushReaction(PushReaction.NORMAL)));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<CageTrapBlock> CAGE_TRAP = BLOCKS.register("cage_trap", () -> new CageTrapBlock(propDisguisable(MapColor.METAL).sound(SoundType.METAL).noCollission()));
	@HasManualPage
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<ClaymoreBlock> CLAYMORE = BLOCKS.register("claymore", () -> new ClaymoreBlock(prop(MapColor.METAL).forceSolidOn().pushReaction(PushReaction.NORMAL)));
	@HasManualPage(PageGroup.DISPLAY_CASES)
	public static final DeferredBlock<DisplayCaseBlock> DISPLAY_CASE = BLOCKS.register(DISPLAY_CASE_PATH, () -> new DisplayCaseBlock(prop(MapColor.METAL).sound(SoundType.METAL), false));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<FloorTrapBlock> FLOOR_TRAP = BLOCKS.register("floor_trap", () -> new FloorTrapBlock(propDisguisable(MapColor.METAL).sound(SoundType.METAL)));
	@HasManualPage
	@OwnableBE
	@RegisterItemBlock
	public static final DeferredBlock<FrameBlock> FRAME = BLOCKS.register("keypad_frame", () -> new FrameBlock(prop().sound(SoundType.METAL)));
	@HasManualPage(PageGroup.DISPLAY_CASES)
	public static final DeferredBlock<DisplayCaseBlock> GLOW_DISPLAY_CASE = BLOCKS.register(GLOW_DISPLAY_CASE_PATH, () -> new DisplayCaseBlock(prop(MapColor.METAL).sound(SoundType.METAL), true));
	@HasManualPage
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<IMSBlock> IMS = BLOCKS.register("ims", () -> new IMSBlock(prop(MapColor.METAL, 0.7F).sound(SoundType.METAL)));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<InventoryScannerBlock> INVENTORY_SCANNER = BLOCKS.register("inventory_scanner", () -> new InventoryScannerBlock(propDisguisable()));
	public static final DeferredBlock<InventoryScannerFieldBlock> INVENTORY_SCANNER_FIELD = BLOCKS.register("inventory_scanner_field", () -> new InventoryScannerFieldBlock(prop(MapColor.NONE).noLootTable()));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<ElectrifiedIronFenceBlock> ELECTRIFIED_IRON_FENCE = BLOCKS.register("electrified_iron_fence", () -> new ElectrifiedIronFenceBlock(prop(MapColor.METAL).sound(SoundType.METAL)));
	public static final DeferredBlock<KeyPanelBlock> KEY_PANEL_BLOCK = BLOCKS.register("key_panel", () -> new KeyPanelBlock(prop(MapColor.METAL).sound(SoundType.METAL)));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<KeycardLockBlock> KEYCARD_LOCK = BLOCKS.register("keycard_lock", () -> new KeycardLockBlock(prop()));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<KeycardReaderBlock> KEYCARD_READER = BLOCKS.register("keycard_reader", () -> new KeycardReaderBlock(propDisguisable()));
	@HasManualPage(hasRecipeDescription = true)
	@RegisterItemBlock
	public static final DeferredBlock<KeypadBlock> KEYPAD = BLOCKS.register("keypad", () -> new KeypadBlock(propDisguisable(MapColor.METAL).sound(SoundType.METAL)));
	@HasManualPage(hasRecipeDescription = true)
	@RegisterItemBlock
	public static final DeferredBlock<KeypadBarrelBlock> KEYPAD_BARREL = BLOCKS.register("keypad_barrel", () -> new KeypadBarrelBlock(propDisguisable(MapColor.METAL).sound(SoundType.METAL)));
	@HasManualPage(hasRecipeDescription = true)
	public static final DeferredBlock<KeypadChestBlock> KEYPAD_CHEST = BLOCKS.register(KEYPAD_CHEST_PATH, () -> new KeypadChestBlock(propDisguisable(MapColor.METAL).sound(SoundType.METAL)));
	public static final DeferredBlock<KeypadDoorBlock> KEYPAD_DOOR = BLOCKS.register("keypad_door", () -> new KeypadDoorBlock(propDisguisable(MapColor.METAL).sound(SoundType.METAL).pushReaction(PushReaction.BLOCK), BlockSetType.IRON));
	@HasManualPage(hasRecipeDescription = true)
	@RegisterItemBlock
	public static final DeferredBlock<KeypadTrapDoorBlock> KEYPAD_TRAPDOOR = BLOCKS.register("keypad_trapdoor", () -> new KeypadTrapDoorBlock(propDisguisable(MapColor.METAL).sound(SoundType.METAL).isValidSpawn(SCContent::never), BlockSetType.IRON));
	@HasManualPage(hasRecipeDescription = true)
	@RegisterItemBlock
	public static final DeferredBlock<KeypadFurnaceBlock> KEYPAD_FURNACE = BLOCKS.register("keypad_furnace", () -> new KeypadFurnaceBlock(prop(MapColor.METAL).sound(SoundType.METAL).lightLevel(state -> state.getValue(AbstractKeypadFurnaceBlock.LIT) ? 13 : 0)));
	@HasManualPage(hasRecipeDescription = true)
	@RegisterItemBlock
	public static final DeferredBlock<KeypadSmokerBlock> KEYPAD_SMOKER = BLOCKS.register("keypad_smoker", () -> new KeypadSmokerBlock(prop(MapColor.METAL).sound(SoundType.METAL).lightLevel(state -> state.getValue(AbstractKeypadFurnaceBlock.LIT) ? 13 : 0)));
	@HasManualPage(hasRecipeDescription = true)
	@RegisterItemBlock
	public static final DeferredBlock<KeypadBlastFurnaceBlock> KEYPAD_BLAST_FURNACE = BLOCKS.register("keypad_blast_furnace", () -> new KeypadBlastFurnaceBlock(prop(MapColor.METAL).sound(SoundType.METAL).lightLevel(state -> state.getValue(AbstractKeypadFurnaceBlock.LIT) ? 13 : 0)));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<LaserBlock> LASER_BLOCK = BLOCKS.register("laser_block", () -> new LaserBlock(propDisguisable()));
	public static final DeferredBlock<LaserFieldBlock> LASER_FIELD = BLOCKS.register("laser", () -> new LaserFieldBlock(prop(MapColor.NONE).noLootTable()));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<MotionActivatedLightBlock> MOTION_ACTIVATED_LIGHT = BLOCKS.register("motion_activated_light", () -> new MotionActivatedLightBlock(prop(MapColor.NONE).sound(SoundType.GLASS).lightLevel(state -> state.getValue(MotionActivatedLightBlock.LIT) ? 15 : 0)));
	@HasManualPage
	@OwnableBE
	@RegisterItemBlock
	public static final DeferredBlock<PanicButtonBlock> PANIC_BUTTON = BLOCKS.register("panic_button", () -> new PanicButtonBlock(prop().lightLevel(state -> state.getValue(ButtonBlock.POWERED) ? 4 : 0), BlockSetType.STONE, -1));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<PortableRadarBlock> PORTABLE_RADAR = BLOCKS.register("portable_radar", () -> new PortableRadarBlock(prop(MapColor.METAL)));
	@HasManualPage
	@OwnableBE
	@RegisterItemBlock
	public static final DeferredBlock<ProjectorBlock> PROJECTOR = BLOCKS.register("projector", () -> new ProjectorBlock(propDisguisable(MapColor.METAL).sound(SoundType.METAL)));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<ProtectoBlock> PROTECTO = BLOCKS.register("protecto", () -> new ProtectoBlock(propDisguisable(MapColor.METAL).sound(SoundType.METAL).lightLevel(state -> 7)));
	@OwnableBE
	public static final DeferredBlock<ReinforcedDoorBlock> REINFORCED_DOOR = BLOCKS.register("iron_door_reinforced", () -> new ReinforcedDoorBlock(prop(MapColor.METAL).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.BLOCK)));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<ElectrifiedIronFenceGateBlock> ELECTRIFIED_IRON_FENCE_GATE = BLOCKS.register("reinforced_fence_gate", () -> new ElectrifiedIronFenceGateBlock(prop(MapColor.METAL).sound(SoundType.METAL).forceSolidOn()));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<RetinalScannerBlock> RETINAL_SCANNER = BLOCKS.register("retinal_scanner", () -> new RetinalScannerBlock(propDisguisable()));
	public static final DeferredBlock<RiftStabilizerBlock> RIFT_STABILIZER = BLOCKS.register("rift_stabilizer", () -> new RiftStabilizerBlock(propDisguisable(MapColor.METAL).sound(SoundType.METAL)));
	public static final DeferredBlock<ScannerDoorBlock> SCANNER_DOOR = BLOCKS.register("scanner_door", () -> new ScannerDoorBlock(propDisguisable(MapColor.METAL).sound(SoundType.METAL).pushReaction(PushReaction.BLOCK), BlockSetType.IRON));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<ScannerTrapDoorBlock> SCANNER_TRAPDOOR = BLOCKS.register("scanner_trapdoor", () -> new ScannerTrapDoorBlock(propDisguisable(MapColor.METAL).sound(SoundType.METAL).isValidSpawn(SCContent::never), BlockSetType.IRON));
	public static final DeferredBlock<SecretStandingSignBlock> SECRET_OAK_SIGN = BLOCKS.register("secret_sign_standing", () -> new SecretStandingSignBlock(prop(MapColor.WOOD).sound(SoundType.WOOD).noCollission().forceSolidOn(), WoodType.OAK));
	public static final DeferredBlock<SecretWallSignBlock> SECRET_OAK_WALL_SIGN = BLOCKS.register("secret_sign_wall", () -> new SecretWallSignBlock(prop(MapColor.WOOD).sound(SoundType.WOOD).noCollission().forceSolidOn(), WoodType.OAK));
	public static final DeferredBlock<SecretStandingSignBlock> SECRET_SPRUCE_SIGN = BLOCKS.register("secret_spruce_sign_standing", () -> new SecretStandingSignBlock(prop(MapColor.COLOR_BROWN).sound(SoundType.WOOD).noCollission().forceSolidOn(), WoodType.SPRUCE));
	public static final DeferredBlock<SecretWallSignBlock> SECRET_SPRUCE_WALL_SIGN = BLOCKS.register("secret_spruce_sign_wall", () -> new SecretWallSignBlock(prop(MapColor.COLOR_BROWN).sound(SoundType.WOOD).noCollission().forceSolidOn(), WoodType.SPRUCE));
	public static final DeferredBlock<SecretStandingSignBlock> SECRET_BIRCH_SIGN = BLOCKS.register("secret_birch_sign_standing", () -> new SecretStandingSignBlock(prop(MapColor.SAND).sound(SoundType.WOOD).noCollission().forceSolidOn(), WoodType.BIRCH));
	public static final DeferredBlock<SecretWallSignBlock> SECRET_BIRCH_WALL_SIGN = BLOCKS.register("secret_birch_sign_wall", () -> new SecretWallSignBlock(prop(MapColor.SAND).sound(SoundType.WOOD).noCollission().forceSolidOn(), WoodType.BIRCH));
	public static final DeferredBlock<SecretStandingSignBlock> SECRET_JUNGLE_SIGN = BLOCKS.register("secret_jungle_sign_standing", () -> new SecretStandingSignBlock(prop(MapColor.PODZOL).sound(SoundType.WOOD).noCollission().forceSolidOn(), WoodType.JUNGLE));
	public static final DeferredBlock<SecretWallSignBlock> SECRET_JUNGLE_WALL_SIGN = BLOCKS.register("secret_jungle_sign_wall", () -> new SecretWallSignBlock(prop(MapColor.PODZOL).sound(SoundType.WOOD).noCollission().forceSolidOn(), WoodType.JUNGLE));
	public static final DeferredBlock<SecretStandingSignBlock> SECRET_ACACIA_SIGN = BLOCKS.register("secret_acacia_sign_standing", () -> new SecretStandingSignBlock(prop(MapColor.COLOR_ORANGE).sound(SoundType.WOOD).noCollission().forceSolidOn(), WoodType.ACACIA));
	public static final DeferredBlock<SecretWallSignBlock> SECRET_ACACIA_WALL_SIGN = BLOCKS.register("secret_acacia_sign_wall", () -> new SecretWallSignBlock(prop(MapColor.COLOR_ORANGE).sound(SoundType.WOOD).noCollission().forceSolidOn(), WoodType.ACACIA));
	public static final DeferredBlock<SecretStandingSignBlock> SECRET_DARK_OAK_SIGN = BLOCKS.register("secret_dark_oak_sign_standing", () -> new SecretStandingSignBlock(prop(MapColor.COLOR_BROWN).sound(SoundType.WOOD).noCollission().forceSolidOn(), WoodType.DARK_OAK));
	public static final DeferredBlock<SecretWallSignBlock> SECRET_DARK_OAK_WALL_SIGN = BLOCKS.register("secret_dark_oak_sign_wall", () -> new SecretWallSignBlock(prop(MapColor.COLOR_BROWN).sound(SoundType.WOOD).noCollission().forceSolidOn(), WoodType.DARK_OAK));
	public static final DeferredBlock<SecretStandingSignBlock> SECRET_MANGROVE_SIGN = BLOCKS.register("secret_mangrove_sign_standing", () -> new SecretStandingSignBlock(prop(MapColor.PODZOL).sound(SoundType.WOOD).noCollission().forceSolidOn(), WoodType.MANGROVE));
	public static final DeferredBlock<SecretWallSignBlock> SECRET_MANGROVE_WALL_SIGN = BLOCKS.register("secret_mangrove_sign_wall", () -> new SecretWallSignBlock(prop(MapColor.PODZOL).sound(SoundType.WOOD).noCollission().forceSolidOn(), WoodType.MANGROVE));
	public static final DeferredBlock<SecretStandingSignBlock> SECRET_CRIMSON_SIGN = BLOCKS.register("secret_crimson_sign_standing", () -> new SecretStandingSignBlock(prop(MapColor.CRIMSON_STEM).sound(SoundType.NETHER_WOOD).noCollission().forceSolidOn(), WoodType.CRIMSON));
	public static final DeferredBlock<SecretWallSignBlock> SECRET_CRIMSON_WALL_SIGN = BLOCKS.register("secret_crimson_sign_wall", () -> new SecretWallSignBlock(prop(MapColor.CRIMSON_STEM).sound(SoundType.NETHER_WOOD).noCollission().forceSolidOn(), WoodType.CRIMSON));
	public static final DeferredBlock<SecretStandingSignBlock> SECRET_WARPED_SIGN = BLOCKS.register("secret_warped_sign_standing", () -> new SecretStandingSignBlock(prop(MapColor.WARPED_STEM).sound(SoundType.NETHER_WOOD).noCollission().forceSolidOn(), WoodType.WARPED));
	public static final DeferredBlock<SecretWallSignBlock> SECRET_WARPED_WALL_SIGN = BLOCKS.register("secret_warped_sign_wall", () -> new SecretWallSignBlock(prop(MapColor.WARPED_STEM).sound(SoundType.NETHER_WOOD).noCollission().forceSolidOn(), WoodType.WARPED));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<SecureRedstoneInterfaceBlock> SECURE_REDSTONE_INTERFACE = BLOCKS.register("secure_redstone_interface", () -> new SecureRedstoneInterfaceBlock(propDisguisable()));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<SecurityCameraBlock> SECURITY_CAMERA = BLOCKS.register("security_camera", () -> new SecurityCameraBlock(propDisguisable(MapColor.METAL).noCollission()));
	@HasManualPage
	public static final DeferredBlock<SonicSecuritySystemBlock> SONIC_SECURITY_SYSTEM = BLOCKS.register("sonic_security_system", () -> new SonicSecuritySystemBlock(propDisguisable(MapColor.METAL).sound(SoundType.METAL).noCollission()));
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<TrackMineBlock> TRACK_MINE = BLOCKS.register("track_mine", () -> new TrackMineBlock(prop(MapColor.METAL, 0.7F).noCollission().sound(SoundType.METAL)));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<TrophySystemBlock> TROPHY_SYSTEM = BLOCKS.register("trophy_system", () -> new TrophySystemBlock(propDisguisable(MapColor.METAL).sound(SoundType.METAL)));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<UsernameLoggerBlock> USERNAME_LOGGER = BLOCKS.register("username_logger", () -> new UsernameLoggerBlock(propDisguisable()));
	@HasManualPage
	@OwnableBE
	@RegisterItemBlock
	public static final DeferredBlock<MineBlock> MINE = BLOCKS.register("mine", () -> new MineBlock(prop(MapColor.METAL, 1.0F).forceSolidOn().pushReaction(PushReaction.NORMAL)));
	public static final DeferredBlock<FakeWaterBlock> FAKE_WATER_BLOCK = BLOCKS.register("fake_water_block", () -> new FakeWaterBlock(prop(MapColor.WATER).replaceable().noLootTable().liquid().sound(SoundType.EMPTY).pushReaction(PushReaction.DESTROY).noCollission(), FAKE_WATER));
	public static final DeferredBlock<FakeLavaBlock> FAKE_LAVA_BLOCK = BLOCKS.register("fake_lava_block", () -> new FakeLavaBlock(prop(MapColor.FIRE).replaceable().noLootTable().liquid().sound(SoundType.EMPTY).pushReaction(PushReaction.DESTROY).noCollission().randomTicks().lightLevel(state -> 15), FAKE_LAVA));

	//block mines
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> STONE_MINE = BLOCKS.register("stone_mine", () -> new BaseFullMineBlock(mineProp(Blocks.STONE), Blocks.STONE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<DeepslateMineBlock> DEEPSLATE_MINE = BLOCKS.register("deepslate_mine", () -> new DeepslateMineBlock(mineProp(Blocks.DEEPSLATE), Blocks.DEEPSLATE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> COBBLED_DEEPSLATE_MINE = BLOCKS.register("cobbled_deepslate_mine", () -> new BaseFullMineBlock(mineProp(Blocks.COBBLED_DEEPSLATE), Blocks.COBBLED_DEEPSLATE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> DIRT_MINE = BLOCKS.register("dirt_mine", () -> new BaseFullMineBlock(mineProp(Blocks.DIRT), Blocks.DIRT));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> COBBLESTONE_MINE = BLOCKS.register("cobblestone_mine", () -> new BaseFullMineBlock(mineProp(Blocks.COBBLESTONE), Blocks.COBBLESTONE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<FallingBlockMineBlock> SAND_MINE = BLOCKS.register("sand_mine", () -> new FallingBlockMineBlock(mineProp(Blocks.SAND), Blocks.SAND));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<FallingBlockMineBlock> GRAVEL_MINE = BLOCKS.register("gravel_mine", () -> new FallingBlockMineBlock(mineProp(Blocks.GRAVEL), Blocks.GRAVEL));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> NETHERRACK_MINE = BLOCKS.register("netherrack_mine", () -> new BaseFullMineBlock(mineProp(Blocks.NETHERRACK), Blocks.NETHERRACK));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> END_STONE_MINE = BLOCKS.register("end_stone_mine", () -> new BaseFullMineBlock(mineProp(Blocks.END_STONE), Blocks.END_STONE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> COAL_ORE_MINE = BLOCKS.register("coal_mine", () -> new BaseFullMineBlock(mineProp(Blocks.COAL_ORE), Blocks.COAL_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> DEEPSLATE_COAL_ORE_MINE = BLOCKS.register("deepslate_coal_mine", () -> new BaseFullMineBlock(mineProp(Blocks.DEEPSLATE_COAL_ORE), Blocks.DEEPSLATE_COAL_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> IRON_ORE_MINE = BLOCKS.register("iron_mine", () -> new BaseFullMineBlock(mineProp(Blocks.IRON_ORE), Blocks.IRON_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> DEEPSLATE_IRON_ORE_MINE = BLOCKS.register("deepslate_iron_mine", () -> new BaseFullMineBlock(mineProp(Blocks.DEEPSLATE_IRON_ORE), Blocks.DEEPSLATE_IRON_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> GOLD_ORE_MINE = BLOCKS.register("gold_mine", () -> new BaseFullMineBlock(mineProp(Blocks.GOLD_ORE), Blocks.GOLD_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> DEEPSLATE_GOLD_ORE_MINE = BLOCKS.register("deepslate_gold_mine", () -> new BaseFullMineBlock(mineProp(Blocks.DEEPSLATE_GOLD_ORE), Blocks.DEEPSLATE_GOLD_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> COPPER_ORE_MINE = BLOCKS.register("copper_mine", () -> new BaseFullMineBlock(mineProp(Blocks.COPPER_ORE), Blocks.COPPER_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> DEEPSLATE_COPPER_ORE_MINE = BLOCKS.register("deepslate_copper_mine", () -> new BaseFullMineBlock(mineProp(Blocks.DEEPSLATE_COPPER_ORE), Blocks.DEEPSLATE_COPPER_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<RedstoneOreMineBlock> REDSTONE_ORE_MINE = BLOCKS.register("redstone_mine", () -> new RedstoneOreMineBlock(mineProp(Blocks.REDSTONE_ORE), Blocks.REDSTONE_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<RedstoneOreMineBlock> DEEPSLATE_REDSTONE_ORE_MINE = BLOCKS.register("deepslate_redstone_mine", () -> new RedstoneOreMineBlock(mineProp(Blocks.DEEPSLATE_REDSTONE_ORE), Blocks.DEEPSLATE_REDSTONE_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> EMERALD_ORE_MINE = BLOCKS.register("emerald_mine", () -> new BaseFullMineBlock(mineProp(Blocks.EMERALD_ORE), Blocks.EMERALD_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> DEEPSLATE_EMERALD_ORE_MINE = BLOCKS.register("deepslate_emerald_mine", () -> new BaseFullMineBlock(mineProp(Blocks.DEEPSLATE_EMERALD_ORE), Blocks.DEEPSLATE_EMERALD_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> LAPIS_ORE_MINE = BLOCKS.register("lapis_mine", () -> new BaseFullMineBlock(mineProp(Blocks.LAPIS_ORE), Blocks.LAPIS_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> DEEPSLATE_LAPIS_ORE_MINE = BLOCKS.register("deepslate_lapis_mine", () -> new BaseFullMineBlock(mineProp(Blocks.DEEPSLATE_LAPIS_ORE), Blocks.DEEPSLATE_LAPIS_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> DIAMOND_ORE_MINE = BLOCKS.register("diamond_mine", () -> new BaseFullMineBlock(mineProp(Blocks.DIAMOND_ORE), Blocks.DIAMOND_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> DEEPSLATE_DIAMOND_ORE_MINE = BLOCKS.register("deepslate_diamond_mine", () -> new BaseFullMineBlock(mineProp(Blocks.DEEPSLATE_DIAMOND_ORE), Blocks.DEEPSLATE_DIAMOND_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> NETHER_GOLD_ORE_MINE = BLOCKS.register("nether_gold_mine", () -> new BaseFullMineBlock(mineProp(Blocks.NETHER_GOLD_ORE), Blocks.NETHER_GOLD_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> QUARTZ_ORE_MINE = BLOCKS.register("quartz_mine", () -> new BaseFullMineBlock(mineProp(Blocks.NETHER_QUARTZ_ORE), Blocks.NETHER_QUARTZ_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	public static final DeferredBlock<BaseFullMineBlock> ANCIENT_DEBRIS_MINE = BLOCKS.register("ancient_debris_mine", () -> new BaseFullMineBlock(mineProp(Blocks.ANCIENT_DEBRIS), Blocks.ANCIENT_DEBRIS));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> GILDED_BLACKSTONE_MINE = BLOCKS.register("gilded_blackstone_mine", () -> new BaseFullMineBlock(mineProp(Blocks.GILDED_BLACKSTONE), Blocks.GILDED_BLACKSTONE));
	@HasManualPage(PageGroup.FURNACE_MINES)
	@OwnableBE
	@RegisterItemBlock
	public static final DeferredBlock<FurnaceMineBlock> FURNACE_MINE = BLOCKS.register("furnace_mine", () -> new FurnaceMineBlock(prop(MapColor.STONE, 3.5F).requiresCorrectToolForDrops(), Blocks.FURNACE));
	@HasManualPage(PageGroup.FURNACE_MINES)
	@OwnableBE
	@RegisterItemBlock
	public static final DeferredBlock<FurnaceMineBlock> SMOKER_MINE = BLOCKS.register("smoker_mine", () -> new FurnaceMineBlock(prop(MapColor.STONE, 3.5F).requiresCorrectToolForDrops(), Blocks.SMOKER));
	@HasManualPage(PageGroup.FURNACE_MINES)
	@OwnableBE
	@RegisterItemBlock
	public static final DeferredBlock<FurnaceMineBlock> BLAST_FURNACE_MINE = BLOCKS.register("blast_furnace_mine", () -> new FurnaceMineBlock(prop(MapColor.STONE, 3.5F).requiresCorrectToolForDrops(), Blocks.BLAST_FURNACE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@OwnableBE
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BrushableMineBlock> SUSPICIOUS_SAND_MINE = BLOCKS.register("suspicious_sand_mine", () -> new BrushableMineBlock(mineProp(Blocks.SUSPICIOUS_SAND).pushReaction(PushReaction.DESTROY), Blocks.SUSPICIOUS_SAND));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@OwnableBE
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BrushableMineBlock> SUSPICIOUS_GRAVEL_MINE = BLOCKS.register("suspicious_gravel_mine", () -> new BrushableMineBlock(mineProp(Blocks.SUSPICIOUS_GRAVEL), Blocks.SUSPICIOUS_GRAVEL));

	//reinforced blocks (ordered by vanilla <1.19.3 building blocks creative tab order)
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_STONE = BLOCKS.register("reinforced_stone", () -> new BaseReinforcedBlock(prop(), Blocks.STONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_GRANITE = BLOCKS.register("reinforced_granite", () -> new BaseReinforcedBlock(prop(MapColor.DIRT), Blocks.GRANITE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_POLISHED_GRANITE = BLOCKS.register("reinforced_polished_granite", () -> new BaseReinforcedBlock(prop(MapColor.DIRT), Blocks.POLISHED_GRANITE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_DIORITE = BLOCKS.register("reinforced_diorite", () -> new BaseReinforcedBlock(prop(MapColor.QUARTZ), Blocks.DIORITE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_POLISHED_DIORITE = BLOCKS.register("reinforced_polished_diorite", () -> new BaseReinforcedBlock(prop(MapColor.QUARTZ), Blocks.POLISHED_DIORITE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_ANDESITE = BLOCKS.register("reinforced_andesite", () -> new BaseReinforcedBlock(prop(), Blocks.ANDESITE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_POLISHED_ANDESITE = BLOCKS.register("reinforced_polished_andesite", () -> new BaseReinforcedBlock(prop(), Blocks.POLISHED_ANDESITE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_DEEPSLATE = BLOCKS.register("reinforced_deepslate", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.DEEPSLATE).sound(SoundType.DEEPSLATE), Blocks.DEEPSLATE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_COBBLED_DEEPSLATE = BLOCKS.register("reinforced_cobbled_deepslate", () -> new BaseReinforcedBlock(prop(MapColor.DEEPSLATE).sound(SoundType.DEEPSLATE), Blocks.COBBLED_DEEPSLATE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_POLISHED_DEEPSLATE = BLOCKS.register("reinforced_polished_deepslate", () -> new BaseReinforcedBlock(prop(MapColor.DEEPSLATE).sound(SoundType.POLISHED_DEEPSLATE), Blocks.POLISHED_DEEPSLATE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CALCITE = BLOCKS.register("reinforced_calcite", () -> new BaseReinforcedBlock(prop(MapColor.TERRACOTTA_WHITE).sound(SoundType.CALCITE), Blocks.CALCITE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_TUFF = BLOCKS.register("reinforced_tuff", () -> new BaseReinforcedBlock(prop(MapColor.TERRACOTTA_GRAY).sound(SoundType.TUFF), Blocks.TUFF));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_DRIPSTONE_BLOCK = BLOCKS.register("reinforced_dripstone_block", () -> new BaseReinforcedBlock(prop(MapColor.TERRACOTTA_BROWN).sound(SoundType.DRIPSTONE_BLOCK), Blocks.DRIPSTONE_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGrassBlock> REINFORCED_GRASS_BLOCK = BLOCKS.register("reinforced_grass_block", () -> new ReinforcedGrassBlock(prop(MapColor.GRASS).sound(SoundType.GRASS)));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_DIRT = BLOCKS.register("reinforced_dirt", () -> new BaseReinforcedBlock(prop(MapColor.DIRT).sound(SoundType.GRAVEL), Blocks.DIRT));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_COARSE_DIRT = BLOCKS.register("reinforced_coarse_dirt", () -> new BaseReinforcedBlock(prop(MapColor.DIRT).sound(SoundType.GRAVEL), Blocks.COARSE_DIRT));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSnowyDirtBlock> REINFORCED_PODZOL = BLOCKS.register("reinforced_podzol", () -> new ReinforcedSnowyDirtBlock(prop(MapColor.PODZOL).sound(SoundType.GRAVEL), Blocks.PODZOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedMud> REINFORCED_MUD = BLOCKS.register("reinforced_mud", () -> new ReinforcedMud(prop(MapColor.TERRACOTTA_CYAN).isValidSpawn(SCContent::always).isRedstoneConductor(SCContent::always).isViewBlocking(SCContent::always).isSuffocating(SCContent::always).sound(SoundType.MUD), Blocks.MUD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedNyliumBlock> REINFORCED_CRIMSON_NYLIUM = BLOCKS.register("reinforced_crimson_nylium", () -> new ReinforcedNyliumBlock(prop(MapColor.CRIMSON_NYLIUM).sound(SoundType.NYLIUM), Blocks.CRIMSON_NYLIUM));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedNyliumBlock> REINFORCED_WARPED_NYLIUM = BLOCKS.register("reinforced_warped_nylium", () -> new ReinforcedNyliumBlock(prop(MapColor.WARPED_NYLIUM).sound(SoundType.NYLIUM), Blocks.WARPED_NYLIUM));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRootedDirtBlock> REINFORCED_ROOTED_DIRT = BLOCKS.register("reinforced_rooted_dirt", () -> new ReinforcedRootedDirtBlock(prop(MapColor.DIRT).sound(SoundType.ROOTED_DIRT), Blocks.ROOTED_DIRT));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_COBBLESTONE = BLOCKS.register("reinforced_cobblestone", () -> new BaseReinforcedBlock(prop(), Blocks.COBBLESTONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_OAK_PLANKS = BLOCKS.register("reinforced_oak_planks", () -> new BaseReinforcedBlock(prop(MapColor.WOOD).sound(SoundType.WOOD), Blocks.OAK_PLANKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_SPRUCE_PLANKS = BLOCKS.register("reinforced_spruce_planks", () -> new BaseReinforcedBlock(prop(MapColor.PODZOL).sound(SoundType.WOOD), Blocks.SPRUCE_PLANKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BIRCH_PLANKS = BLOCKS.register("reinforced_birch_planks", () -> new BaseReinforcedBlock(prop(MapColor.SAND).sound(SoundType.WOOD), Blocks.BIRCH_PLANKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_JUNGLE_PLANKS = BLOCKS.register("reinforced_jungle_planks", () -> new BaseReinforcedBlock(prop(MapColor.DIRT).sound(SoundType.WOOD), Blocks.JUNGLE_PLANKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_ACACIA_PLANKS = BLOCKS.register("reinforced_acacia_planks", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_ORANGE).sound(SoundType.WOOD), Blocks.ACACIA_PLANKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_DARK_OAK_PLANKS = BLOCKS.register("reinforced_dark_oak_planks", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_BROWN).sound(SoundType.WOOD), Blocks.DARK_OAK_PLANKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_MANGROVE_PLANKS = BLOCKS.register("reinforced_mangrove_planks", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_RED).sound(SoundType.WOOD), Blocks.MANGROVE_PLANKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CRIMSON_PLANKS = BLOCKS.register("reinforced_crimson_planks", () -> new BaseReinforcedBlock(prop(MapColor.CRIMSON_STEM).sound(SoundType.NETHER_WOOD), Blocks.CRIMSON_PLANKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_WARPED_PLANKS = BLOCKS.register("reinforced_warped_planks", () -> new BaseReinforcedBlock(prop(MapColor.WARPED_STEM).sound(SoundType.NETHER_WOOD), Blocks.WARPED_PLANKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFallingBlock> REINFORCED_SAND = BLOCKS.register("reinforced_sand", () -> new ReinforcedFallingBlock(prop(MapColor.SAND).sound(SoundType.SAND), Blocks.SAND));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFallingBlock> REINFORCED_RED_SAND = BLOCKS.register("reinforced_red_sand", () -> new ReinforcedFallingBlock(prop(MapColor.COLOR_ORANGE).sound(SoundType.SAND), Blocks.RED_SAND));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFallingBlock> REINFORCED_GRAVEL = BLOCKS.register("reinforced_gravel", () -> new ReinforcedFallingBlock(prop().sound(SoundType.GRAVEL), Blocks.GRAVEL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_COAL_BLOCK = BLOCKS.register("reinforced_coal_block", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_BLACK), Blocks.COAL_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_RAW_IRON_BLOCK = BLOCKS.register("reinforced_raw_iron_block", () -> new BaseReinforcedBlock(prop(MapColor.RAW_IRON), Blocks.RAW_IRON_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_RAW_COPPER_BLOCK = BLOCKS.register("reinforced_raw_copper_block", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_ORANGE), Blocks.RAW_COPPER_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_RAW_GOLD_BLOCK = BLOCKS.register("reinforced_raw_gold_block", () -> new BaseReinforcedBlock(prop(MapColor.GOLD), Blocks.RAW_GOLD_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedAmethystBlock> REINFORCED_AMETHYST_BLOCK = BLOCKS.register("reinforced_amethyst_block", () -> new ReinforcedAmethystBlock(prop(MapColor.COLOR_PURPLE).sound(SoundType.AMETHYST), Blocks.AMETHYST_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_IRON_BLOCK = BLOCKS.register("reinforced_iron_block", () -> new BaseReinforcedBlock(prop(MapColor.METAL).sound(SoundType.METAL), Blocks.IRON_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_COPPER_BLOCK = BLOCKS.register("reinforced_copper_block", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_ORANGE).sound(SoundType.COPPER), Blocks.COPPER_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_GOLD_BLOCK = BLOCKS.register("reinforced_gold_block", () -> new BaseReinforcedBlock(prop(MapColor.GOLD).sound(SoundType.METAL), Blocks.GOLD_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_DIAMOND_BLOCK = BLOCKS.register("reinforced_diamond_block", () -> new BaseReinforcedBlock(prop(MapColor.DIAMOND).sound(SoundType.METAL), Blocks.DIAMOND_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_NETHERITE_BLOCK = BLOCKS.register("reinforced_netherite_block", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_BLACK).sound(SoundType.NETHERITE_BLOCK), Blocks.NETHERITE_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_EXPOSED_COPPER = BLOCKS.register("reinforced_exposed_copper", () -> new BaseReinforcedBlock(prop(MapColor.TERRACOTTA_LIGHT_GRAY).sound(SoundType.COPPER), Blocks.EXPOSED_COPPER));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_WEATHERED_COPPER = BLOCKS.register("reinforced_weathered_copper", () -> new BaseReinforcedBlock(prop(MapColor.WARPED_STEM).sound(SoundType.COPPER), Blocks.WEATHERED_COPPER));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_OXIDIZED_COPPER = BLOCKS.register("reinforced_oxidized_copper", () -> new BaseReinforcedBlock(prop(MapColor.WARPED_NYLIUM).sound(SoundType.COPPER), Blocks.OXIDIZED_COPPER));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CUT_COPPER = BLOCKS.register("reinforced_cut_copper", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_ORANGE).sound(SoundType.COPPER), Blocks.CUT_COPPER));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_EXPOSED_CUT_COPPER = BLOCKS.register("reinforced_exposed_cut_copper", () -> new BaseReinforcedBlock(prop(MapColor.TERRACOTTA_LIGHT_GRAY).sound(SoundType.COPPER), Blocks.EXPOSED_CUT_COPPER));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_WEATHERED_CUT_COPPER = BLOCKS.register("reinforced_weathered_cut_copper", () -> new BaseReinforcedBlock(prop(MapColor.WARPED_STEM).sound(SoundType.COPPER), Blocks.WEATHERED_CUT_COPPER));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_OXIDIZED_CUT_COPPER = BLOCKS.register("reinforced_oxidized_cut_copper", () -> new BaseReinforcedBlock(prop(MapColor.WARPED_NYLIUM).sound(SoundType.COPPER), Blocks.OXIDIZED_CUT_COPPER));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_CUT_COPPER_STAIRS = BLOCKS.register("reinforced_cut_copper_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.COLOR_ORANGE).sound(SoundType.COPPER), Blocks.CUT_COPPER_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_EXPOSED_CUT_COPPER_STAIRS = BLOCKS.register("reinforced_exposed_cut_copper_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.TERRACOTTA_LIGHT_GRAY).sound(SoundType.COPPER), Blocks.EXPOSED_CUT_COPPER_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_WEATHERED_CUT_COPPER_STAIRS = BLOCKS.register("reinforced_weathered_cut_copper_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.WARPED_STEM).sound(SoundType.COPPER), Blocks.WEATHERED_CUT_COPPER_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_OXIDIZED_CUT_COPPER_STAIRS = BLOCKS.register("reinforced_oxidized_cut_copper_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.WARPED_NYLIUM).sound(SoundType.COPPER), Blocks.OXIDIZED_CUT_COPPER_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_CUT_COPPER_SLAB = BLOCKS.register("reinforced_cut_copper_slab", () -> new ReinforcedSlabBlock(prop(MapColor.COLOR_ORANGE).sound(SoundType.COPPER), Blocks.CUT_COPPER_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_EXPOSED_CUT_COPPER_SLAB = BLOCKS.register("reinforced_exposed_cut_copper_slab", () -> new ReinforcedSlabBlock(prop(MapColor.TERRACOTTA_LIGHT_GRAY).sound(SoundType.COPPER), Blocks.EXPOSED_CUT_COPPER_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_WEATHERED_CUT_COPPER_SLAB = BLOCKS.register("reinforced_weathered_cut_copper_slab", () -> new ReinforcedSlabBlock(prop(MapColor.WARPED_STEM).sound(SoundType.COPPER), Blocks.WEATHERED_CUT_COPPER_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_OXIDIZED_CUT_COPPER_SLAB = BLOCKS.register("reinforced_oxidized_cut_copper_slab", () -> new ReinforcedSlabBlock(prop(MapColor.WARPED_NYLIUM).sound(SoundType.COPPER), Blocks.OXIDIZED_CUT_COPPER_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_OAK_LOG = BLOCKS.register("reinforced_oak_log", () -> new ReinforcedRotatedPillarBlock(logProp(MapColor.WOOD, MapColor.PODZOL).sound(SoundType.WOOD), Blocks.OAK_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_SPRUCE_LOG = BLOCKS.register("reinforced_spruce_log", () -> new ReinforcedRotatedPillarBlock(logProp(MapColor.PODZOL, MapColor.COLOR_BROWN).sound(SoundType.WOOD), Blocks.SPRUCE_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_BIRCH_LOG = BLOCKS.register("reinforced_birch_log", () -> new ReinforcedRotatedPillarBlock(logProp(MapColor.SAND, MapColor.QUARTZ).sound(SoundType.WOOD), Blocks.BIRCH_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_JUNGLE_LOG = BLOCKS.register("reinforced_jungle_log", () -> new ReinforcedRotatedPillarBlock(logProp(MapColor.DIRT, MapColor.PODZOL).sound(SoundType.WOOD), Blocks.JUNGLE_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_ACACIA_LOG = BLOCKS.register("reinforced_acacia_log", () -> new ReinforcedRotatedPillarBlock(logProp(MapColor.COLOR_ORANGE, MapColor.STONE).sound(SoundType.WOOD), Blocks.ACACIA_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_DARK_OAK_LOG = BLOCKS.register("reinforced_dark_oak_log", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.COLOR_BROWN).sound(SoundType.WOOD), Blocks.DARK_OAK_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_MANGROVE_LOG = BLOCKS.register("reinforced_mangrove_log", () -> new ReinforcedRotatedPillarBlock(logProp(MapColor.COLOR_RED, MapColor.PODZOL).sound(SoundType.WOOD), Blocks.MANGROVE_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_CRIMSON_STEM = BLOCKS.register("reinforced_crimson_stem", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.CRIMSON_STEM).sound(SoundType.STEM), Blocks.CRIMSON_STEM));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_WARPED_STEM = BLOCKS.register("reinforced_warped_stem", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.WARPED_STEM).sound(SoundType.STEM), Blocks.WARPED_STEM));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_OAK_LOG = BLOCKS.register("reinforced_stripped_oak_log", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.WOOD).sound(SoundType.WOOD), Blocks.STRIPPED_OAK_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_SPRUCE_LOG = BLOCKS.register("reinforced_stripped_spruce_log", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.PODZOL).sound(SoundType.WOOD), Blocks.STRIPPED_SPRUCE_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_BIRCH_LOG = BLOCKS.register("reinforced_stripped_birch_log", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.SAND).sound(SoundType.WOOD), Blocks.STRIPPED_BIRCH_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_JUNGLE_LOG = BLOCKS.register("reinforced_stripped_jungle_log", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.DIRT).sound(SoundType.WOOD), Blocks.STRIPPED_JUNGLE_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_ACACIA_LOG = BLOCKS.register("reinforced_stripped_acacia_log", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.COLOR_ORANGE).sound(SoundType.WOOD), Blocks.STRIPPED_ACACIA_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_DARK_OAK_LOG = BLOCKS.register("reinforced_stripped_dark_oak_log", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.COLOR_BROWN).sound(SoundType.WOOD), Blocks.STRIPPED_DARK_OAK_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_MANGROVE_LOG = BLOCKS.register("reinforced_stripped_mangrove_log", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.COLOR_RED).sound(SoundType.WOOD), Blocks.STRIPPED_MANGROVE_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_CRIMSON_STEM = BLOCKS.register("reinforced_stripped_crimson_stem", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.CRIMSON_STEM).sound(SoundType.STEM), Blocks.STRIPPED_CRIMSON_STEM));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_WARPED_STEM = BLOCKS.register("reinforced_stripped_warped_stem", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.WARPED_STEM).sound(SoundType.STEM), Blocks.STRIPPED_WARPED_STEM));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_OAK_WOOD = BLOCKS.register("reinforced_stripped_oak_wood", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.WOOD).sound(SoundType.WOOD), Blocks.STRIPPED_OAK_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_SPRUCE_WOOD = BLOCKS.register("reinforced_stripped_spruce_wood", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.PODZOL).sound(SoundType.WOOD), Blocks.STRIPPED_SPRUCE_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_BIRCH_WOOD = BLOCKS.register("reinforced_stripped_birch_wood", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.SAND).sound(SoundType.WOOD), Blocks.STRIPPED_BIRCH_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_JUNGLE_WOOD = BLOCKS.register("reinforced_stripped_jungle_wood", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.DIRT).sound(SoundType.WOOD), Blocks.STRIPPED_JUNGLE_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_ACACIA_WOOD = BLOCKS.register("reinforced_stripped_acacia_wood", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.COLOR_ORANGE).sound(SoundType.WOOD), Blocks.STRIPPED_ACACIA_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_DARK_OAK_WOOD = BLOCKS.register("reinforced_stripped_dark_oak_wood", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.COLOR_BROWN).sound(SoundType.WOOD), Blocks.STRIPPED_DARK_OAK_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_MANGROVE_WOOD = BLOCKS.register("reinforced_stripped_mangrove_wood", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.COLOR_RED).sound(SoundType.WOOD), Blocks.STRIPPED_MANGROVE_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_CRIMSON_HYPHAE = BLOCKS.register("reinforced_stripped_crimson_hyphae", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.CRIMSON_HYPHAE).sound(SoundType.STEM), Blocks.STRIPPED_CRIMSON_HYPHAE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_WARPED_HYPHAE = BLOCKS.register("reinforced_stripped_warped_hyphae", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.WARPED_HYPHAE).sound(SoundType.STEM), Blocks.STRIPPED_WARPED_HYPHAE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_OAK_WOOD = BLOCKS.register("reinforced_oak_wood", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.WOOD).sound(SoundType.WOOD), Blocks.OAK_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_SPRUCE_WOOD = BLOCKS.register("reinforced_spruce_wood", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.PODZOL).sound(SoundType.WOOD), Blocks.SPRUCE_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_BIRCH_WOOD = BLOCKS.register("reinforced_birch_wood", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.SAND).sound(SoundType.WOOD), Blocks.BIRCH_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_JUNGLE_WOOD = BLOCKS.register("reinforced_jungle_wood", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.DIRT).sound(SoundType.WOOD), Blocks.JUNGLE_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_ACACIA_WOOD = BLOCKS.register("reinforced_acacia_wood", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.COLOR_GRAY).sound(SoundType.WOOD), Blocks.ACACIA_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_DARK_OAK_WOOD = BLOCKS.register("reinforced_dark_oak_wood", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.COLOR_BROWN).sound(SoundType.WOOD), Blocks.DARK_OAK_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_MANGROVE_WOOD = BLOCKS.register("reinforced_mangrove_wood", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.COLOR_RED).sound(SoundType.WOOD), Blocks.MANGROVE_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_CRIMSON_HYPHAE = BLOCKS.register("reinforced_crimson_hyphae", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.CRIMSON_HYPHAE).sound(SoundType.STEM), Blocks.CRIMSON_HYPHAE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_WARPED_HYPHAE = BLOCKS.register("reinforced_warped_hyphae", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.WARPED_HYPHAE).sound(SoundType.STEM), Blocks.WARPED_HYPHAE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedGlassBlock> REINFORCED_GLASS = BLOCKS.register("reinforced_glass", () -> new ReinforcedGlassBlock(glassProp(MapColor.NONE), Blocks.GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedTintedGlassBlock> REINFORCED_TINTED_GLASS = BLOCKS.register("reinforced_tinted_glass", () -> new ReinforcedTintedGlassBlock(glassProp(MapColor.COLOR_GRAY), Blocks.TINTED_GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_LAPIS_BLOCK = BLOCKS.register("reinforced_lapis_block", () -> new BaseReinforcedBlock(prop(MapColor.LAPIS), Blocks.LAPIS_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_SANDSTONE = BLOCKS.register("reinforced_sandstone", () -> new BaseReinforcedBlock(prop(MapColor.SAND), Blocks.SANDSTONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CHISELED_SANDSTONE = BLOCKS.register("reinforced_chiseled_sandstone", () -> new BaseReinforcedBlock(prop(MapColor.SAND), Blocks.CHISELED_SANDSTONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CUT_SANDSTONE = BLOCKS.register("reinforced_cut_sandstone", () -> new BaseReinforcedBlock(prop(MapColor.SAND), Blocks.CUT_SANDSTONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_WHITE_WOOL = BLOCKS.register("reinforced_white_wool", () -> new BaseReinforcedBlock(prop(MapColor.SNOW).sound(SoundType.WOOL), Blocks.WHITE_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_ORANGE_WOOL = BLOCKS.register("reinforced_orange_wool", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_ORANGE).sound(SoundType.WOOL), Blocks.ORANGE_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_MAGENTA_WOOL = BLOCKS.register("reinforced_magenta_wool", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_MAGENTA).sound(SoundType.WOOL), Blocks.MAGENTA_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_LIGHT_BLUE_WOOL = BLOCKS.register("reinforced_light_blue_wool", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_LIGHT_BLUE).sound(SoundType.WOOL), Blocks.LIGHT_BLUE_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_YELLOW_WOOL = BLOCKS.register("reinforced_yellow_wool", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_YELLOW).sound(SoundType.WOOL), Blocks.YELLOW_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_LIME_WOOL = BLOCKS.register("reinforced_lime_wool", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_LIGHT_GREEN).sound(SoundType.WOOL), Blocks.LIME_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_PINK_WOOL = BLOCKS.register("reinforced_pink_wool", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_PINK).sound(SoundType.WOOL), Blocks.PINK_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_GRAY_WOOL = BLOCKS.register("reinforced_gray_wool", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_GRAY).sound(SoundType.WOOL), Blocks.GRAY_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_LIGHT_GRAY_WOOL = BLOCKS.register("reinforced_light_gray_wool", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_LIGHT_GRAY).sound(SoundType.WOOL), Blocks.LIGHT_GRAY_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CYAN_WOOL = BLOCKS.register("reinforced_cyan_wool", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_CYAN).sound(SoundType.WOOL), Blocks.CYAN_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_PURPLE_WOOL = BLOCKS.register("reinforced_purple_wool", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_PURPLE).sound(SoundType.WOOL), Blocks.PURPLE_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BLUE_WOOL = BLOCKS.register("reinforced_blue_wool", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_BLUE).sound(SoundType.WOOL), Blocks.BLUE_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BROWN_WOOL = BLOCKS.register("reinforced_brown_wool", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_BROWN).sound(SoundType.WOOL), Blocks.BROWN_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_GREEN_WOOL = BLOCKS.register("reinforced_green_wool", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_GREEN).sound(SoundType.WOOL), Blocks.GREEN_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_RED_WOOL = BLOCKS.register("reinforced_red_wool", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_RED).sound(SoundType.WOOL), Blocks.RED_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BLACK_WOOL = BLOCKS.register("reinforced_black_wool", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_BLACK).sound(SoundType.WOOL), Blocks.BLACK_WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_OAK_SLAB = BLOCKS.register("reinforced_oak_slab", () -> new ReinforcedSlabBlock(prop(MapColor.WOOD).sound(SoundType.WOOD), Blocks.OAK_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_SPRUCE_SLAB = BLOCKS.register("reinforced_spruce_slab", () -> new ReinforcedSlabBlock(prop(MapColor.PODZOL).sound(SoundType.WOOD), Blocks.SPRUCE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_BIRCH_SLAB = BLOCKS.register("reinforced_birch_slab", () -> new ReinforcedSlabBlock(prop(MapColor.SAND).sound(SoundType.WOOD), Blocks.BIRCH_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_JUNGLE_SLAB = BLOCKS.register("reinforced_jungle_slab", () -> new ReinforcedSlabBlock(prop(MapColor.DIRT).sound(SoundType.WOOD), Blocks.JUNGLE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_ACACIA_SLAB = BLOCKS.register("reinforced_acacia_slab", () -> new ReinforcedSlabBlock(prop(MapColor.COLOR_ORANGE).sound(SoundType.WOOD), Blocks.ACACIA_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_DARK_OAK_SLAB = BLOCKS.register("reinforced_dark_oak_slab", () -> new ReinforcedSlabBlock(prop(MapColor.COLOR_BROWN).sound(SoundType.WOOD), Blocks.DARK_OAK_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_MANGROVE_SLAB = BLOCKS.register("reinforced_mangrove_slab", () -> new ReinforcedSlabBlock(prop(MapColor.COLOR_RED).sound(SoundType.WOOD), Blocks.MANGROVE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_CRIMSON_SLAB = BLOCKS.register("reinforced_crimson_slab", () -> new ReinforcedSlabBlock(prop(MapColor.CRIMSON_STEM).sound(SoundType.NETHER_WOOD), Blocks.CRIMSON_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_WARPED_SLAB = BLOCKS.register("reinforced_warped_slab", () -> new ReinforcedSlabBlock(prop(MapColor.WARPED_STEM).sound(SoundType.NETHER_WOOD), Blocks.WARPED_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_NORMAL_STONE_SLAB = BLOCKS.register("reinforced_normal_stone_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.STONE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_SMOOTH_STONE_SLAB = BLOCKS.register("reinforced_stone_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.SMOOTH_STONE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_SANDSTONE_SLAB = BLOCKS.register("reinforced_sandstone_slab", () -> new ReinforcedSlabBlock(prop(MapColor.SAND), Blocks.SANDSTONE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_CUT_SANDSTONE_SLAB = BLOCKS.register("reinforced_cut_sandstone_slab", () -> new ReinforcedSlabBlock(prop(MapColor.SAND), Blocks.CUT_SANDSTONE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_COBBLESTONE_SLAB = BLOCKS.register("reinforced_cobblestone_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.COBBLESTONE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_BRICK_SLAB = BLOCKS.register("reinforced_brick_slab", () -> new ReinforcedSlabBlock(prop(MapColor.COLOR_RED), Blocks.BRICK_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_STONE_BRICK_SLAB = BLOCKS.register("reinforced_stone_brick_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.STONE_BRICK_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_MUD_BRICK_SLAB = BLOCKS.register("reinforced_mud_brick_slab", () -> new ReinforcedSlabBlock(prop(MapColor.TERRACOTTA_LIGHT_GRAY).sound(SoundType.MUD_BRICKS), Blocks.MUD_BRICK_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_NETHER_BRICK_SLAB = BLOCKS.register("reinforced_nether_brick_slab", () -> new ReinforcedSlabBlock(prop(MapColor.NETHER).sound(SoundType.NETHER_BRICKS), Blocks.NETHER_BRICK_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_QUARTZ_SLAB = BLOCKS.register("reinforced_quartz_slab", () -> new ReinforcedSlabBlock(prop(MapColor.QUARTZ), Blocks.QUARTZ_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_RED_SANDSTONE_SLAB = BLOCKS.register("reinforced_red_sandstone_slab", () -> new ReinforcedSlabBlock(prop(MapColor.COLOR_ORANGE), Blocks.RED_SANDSTONE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_CUT_RED_SANDSTONE_SLAB = BLOCKS.register("reinforced_cut_red_sandstone_slab", () -> new ReinforcedSlabBlock(prop(MapColor.COLOR_ORANGE), Blocks.CUT_RED_SANDSTONE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_PURPUR_SLAB = BLOCKS.register("reinforced_purpur_slab", () -> new ReinforcedSlabBlock(prop(MapColor.COLOR_MAGENTA), Blocks.PURPUR_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_PRISMARINE_SLAB = BLOCKS.register("reinforced_prismarine_slab", () -> new ReinforcedSlabBlock(prop(MapColor.COLOR_CYAN), Blocks.PRISMARINE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_PRISMARINE_BRICK_SLAB = BLOCKS.register("reinforced_prismarine_brick_slab", () -> new ReinforcedSlabBlock(prop(MapColor.DIAMOND), Blocks.PRISMARINE_BRICK_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_DARK_PRISMARINE_SLAB = BLOCKS.register("reinforced_dark_prismarine_slab", () -> new ReinforcedSlabBlock(prop(MapColor.DIAMOND), Blocks.DARK_PRISMARINE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_SMOOTH_QUARTZ = BLOCKS.register("reinforced_smooth_quartz", () -> new BaseReinforcedBlock(prop(MapColor.QUARTZ), Blocks.SMOOTH_QUARTZ));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_SMOOTH_RED_SANDSTONE = BLOCKS.register("reinforced_smooth_red_sandstone", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_ORANGE), Blocks.SMOOTH_RED_SANDSTONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_SMOOTH_SANDSTONE = BLOCKS.register("reinforced_smooth_sandstone", () -> new BaseReinforcedBlock(prop(MapColor.SAND), Blocks.SMOOTH_SANDSTONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_SMOOTH_STONE = BLOCKS.register("reinforced_smooth_stone", () -> new BaseReinforcedBlock(prop(), Blocks.SMOOTH_STONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BRICKS = BLOCKS.register("reinforced_bricks", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_RED), Blocks.BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BOOKSHELF = BLOCKS.register("reinforced_bookshelf", () -> new BaseReinforcedBlock(prop(MapColor.WOOD).sound(SoundType.WOOD), Blocks.BOOKSHELF));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_MOSSY_COBBLESTONE = BLOCKS.register("reinforced_mossy_cobblestone", () -> new BaseReinforcedBlock(prop(), Blocks.MOSSY_COBBLESTONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedObsidianBlock> REINFORCED_OBSIDIAN = BLOCKS.register("reinforced_obsidian", () -> new ReinforcedObsidianBlock(prop(MapColor.COLOR_BLACK), Blocks.OBSIDIAN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_PURPUR_BLOCK = BLOCKS.register("reinforced_purpur_block", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_MAGENTA), Blocks.PURPUR_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_PURPUR_PILLAR = BLOCKS.register("reinforced_purpur_pillar", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.COLOR_MAGENTA), Blocks.PURPUR_PILLAR));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_PURPUR_STAIRS = BLOCKS.register("reinforced_purpur_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.COLOR_MAGENTA), Blocks.PURPUR_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_OAK_STAIRS = BLOCKS.register("reinforced_oak_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.WOOD).sound(SoundType.WOOD), Blocks.OAK_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_COBBLESTONE_STAIRS = BLOCKS.register("reinforced_cobblestone_stairs", () -> new ReinforcedStairsBlock(prop(), Blocks.COBBLESTONE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_ICE = BLOCKS.register("reinforced_ice", () -> new BaseReinforcedBlock(prop(MapColor.ICE).friction(0.98F).sound(SoundType.GLASS).noOcclusion().isRedstoneConductor(SCContent::never).isValidSpawn((state, level, pos, type) -> type == EntityType.POLAR_BEAR), Blocks.ICE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_SNOW_BLOCK = BLOCKS.register("reinforced_snow_block", () -> new BaseReinforcedBlock(prop(MapColor.SNOW).sound(SoundType.SNOW), Blocks.SNOW_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CLAY = BLOCKS.register("reinforced_clay", () -> new BaseReinforcedBlock(prop(MapColor.CLAY).sound(SoundType.GRAVEL), Blocks.CLAY));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_NETHERRACK = BLOCKS.register("reinforced_netherrack", () -> new BaseReinforcedBlock(prop(MapColor.NETHER).sound(SoundType.NETHERRACK), Blocks.NETHERRACK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_SOUL_SOIL = BLOCKS.register("reinforced_soul_soil", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_BROWN).sound(SoundType.SOUL_SOIL), Blocks.SOUL_SOIL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_BASALT = BLOCKS.register("reinforced_basalt", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.COLOR_BLACK).sound(SoundType.BASALT), Blocks.BASALT));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_POLISHED_BASALT = BLOCKS.register("reinforced_polished_basalt", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.COLOR_BLACK).sound(SoundType.BASALT), Blocks.POLISHED_BASALT));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_SMOOTH_BASALT = BLOCKS.register("reinforced_smooth_basalt", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_BLACK).sound(SoundType.BASALT), Blocks.SMOOTH_BASALT));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_GLOWSTONE = BLOCKS.register("reinforced_glowstone", () -> new BaseReinforcedBlock(prop(MapColor.SAND).sound(SoundType.GLASS).lightLevel(state -> 15).isRedstoneConductor(SCContent::never), Blocks.GLOWSTONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_STONE_BRICKS = BLOCKS.register("reinforced_stone_bricks", () -> new BaseReinforcedBlock(prop(), Blocks.STONE_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_MOSSY_STONE_BRICKS = BLOCKS.register("reinforced_mossy_stone_bricks", () -> new BaseReinforcedBlock(prop(), Blocks.MOSSY_STONE_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CRACKED_STONE_BRICKS = BLOCKS.register("reinforced_cracked_stone_bricks", () -> new BaseReinforcedBlock(prop(), Blocks.CRACKED_STONE_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CHISELED_STONE_BRICKS = BLOCKS.register("reinforced_chiseled_stone_bricks", () -> new BaseReinforcedBlock(prop(), Blocks.CHISELED_STONE_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_PACKED_MUD = BLOCKS.register("reinforced_packed_mud", () -> new BaseReinforcedBlock(prop(MapColor.DIRT).sound(SoundType.PACKED_MUD), Blocks.PACKED_MUD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_MUD_BRICKS = BLOCKS.register("reinforced_mud_bricks", () -> new BaseReinforcedBlock(prop(MapColor.TERRACOTTA_LIGHT_GRAY).sound(SoundType.MUD_BRICKS), Blocks.MUD_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_DEEPSLATE_BRICKS = BLOCKS.register("reinforced_deepslate_bricks", () -> new BaseReinforcedBlock(prop(MapColor.DEEPSLATE).sound(SoundType.DEEPSLATE_BRICKS), Blocks.DEEPSLATE_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CRACKED_DEEPSLATE_BRICKS = BLOCKS.register("reinforced_cracked_deepslate_bricks", () -> new BaseReinforcedBlock(prop(MapColor.DEEPSLATE).sound(SoundType.DEEPSLATE_BRICKS), Blocks.CRACKED_DEEPSLATE_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_DEEPSLATE_TILES = BLOCKS.register("reinforced_deepslate_tiles", () -> new BaseReinforcedBlock(prop(MapColor.DEEPSLATE).sound(SoundType.DEEPSLATE_TILES), Blocks.DEEPSLATE_TILES));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CRACKED_DEEPSLATE_TILES = BLOCKS.register("reinforced_cracked_deepslate_tiles", () -> new BaseReinforcedBlock(prop(MapColor.DEEPSLATE).sound(SoundType.DEEPSLATE_TILES), Blocks.CRACKED_DEEPSLATE_TILES));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CHISELED_DEEPSLATE = BLOCKS.register("reinforced_chiseled_deepslate", () -> new BaseReinforcedBlock(prop(MapColor.DEEPSLATE).sound(SoundType.DEEPSLATE_BRICKS), Blocks.CHISELED_DEEPSLATE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_BRICK_STAIRS = BLOCKS.register("reinforced_brick_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.COLOR_RED), Blocks.BRICK_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_STONE_BRICK_STAIRS = BLOCKS.register("reinforced_stone_brick_stairs", () -> new ReinforcedStairsBlock(prop(), Blocks.STONE_BRICK_STAIRS));
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_MUD_BRICK_STAIRS = BLOCKS.register("reinforced_mud_brick_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.TERRACOTTA_LIGHT_GRAY).sound(SoundType.MUD_BRICKS), Blocks.MUD_BRICK_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSnowyDirtBlock> REINFORCED_MYCELIUM = BLOCKS.register("reinforced_mycelium", () -> new ReinforcedSnowyDirtBlock(prop(MapColor.COLOR_PURPLE).sound(SoundType.GRASS), Blocks.MYCELIUM));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_NETHER_BRICKS = BLOCKS.register("reinforced_nether_bricks", () -> new BaseReinforcedBlock(prop(MapColor.NETHER).sound(SoundType.NETHER_BRICKS), Blocks.NETHER_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CRACKED_NETHER_BRICKS = BLOCKS.register("reinforced_cracked_nether_bricks", () -> new BaseReinforcedBlock(prop(MapColor.NETHER).sound(SoundType.NETHER_BRICKS), Blocks.CRACKED_NETHER_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CHISELED_NETHER_BRICKS = BLOCKS.register("reinforced_chiseled_nether_bricks", () -> new BaseReinforcedBlock(prop(MapColor.NETHER).sound(SoundType.NETHER_BRICKS), Blocks.CHISELED_NETHER_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_NETHER_BRICK_STAIRS = BLOCKS.register("reinforced_nether_brick_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.NETHER).sound(SoundType.NETHER_BRICKS), Blocks.NETHER_BRICK_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_END_STONE = BLOCKS.register("reinforced_end_stone", () -> new BaseReinforcedBlock(prop(MapColor.SAND), Blocks.END_STONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_END_STONE_BRICKS = BLOCKS.register("reinforced_end_stone_bricks", () -> new BaseReinforcedBlock(prop(MapColor.SAND), Blocks.END_STONE_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_SANDSTONE_STAIRS = BLOCKS.register("reinforced_sandstone_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.SAND), Blocks.SANDSTONE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_EMERALD_BLOCK = BLOCKS.register("reinforced_emerald_block", () -> new BaseReinforcedBlock(prop(MapColor.EMERALD).sound(SoundType.METAL), Blocks.EMERALD_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_SPRUCE_STAIRS = BLOCKS.register("reinforced_spruce_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.PODZOL).sound(SoundType.WOOD), Blocks.SPRUCE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_BIRCH_STAIRS = BLOCKS.register("reinforced_birch_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.SAND).sound(SoundType.WOOD), Blocks.BIRCH_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_JUNGLE_STAIRS = BLOCKS.register("reinforced_jungle_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.DIRT).sound(SoundType.WOOD), Blocks.JUNGLE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_CRIMSON_STAIRS = BLOCKS.register("reinforced_crimson_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.CRIMSON_STEM).sound(SoundType.NETHER_WOOD), Blocks.CRIMSON_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_WARPED_STAIRS = BLOCKS.register("reinforced_warped_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.WARPED_STEM).sound(SoundType.NETHER_WOOD), Blocks.WARPED_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CHISELED_QUARTZ = BLOCKS.register("reinforced_chiseled_quartz_block", () -> new BaseReinforcedBlock(prop(MapColor.QUARTZ), Blocks.CHISELED_QUARTZ_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_QUARTZ_BLOCK = BLOCKS.register("reinforced_quartz_block", () -> new BaseReinforcedBlock(prop(MapColor.QUARTZ), Blocks.QUARTZ_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_QUARTZ_BRICKS = BLOCKS.register("reinforced_quartz_bricks", () -> new BaseReinforcedBlock(prop(MapColor.QUARTZ), Blocks.QUARTZ_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_QUARTZ_PILLAR = BLOCKS.register("reinforced_quartz_pillar", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.QUARTZ), Blocks.QUARTZ_PILLAR));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_QUARTZ_STAIRS = BLOCKS.register("reinforced_quartz_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.QUARTZ), Blocks.QUARTZ_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_WHITE_TERRACOTTA = BLOCKS.register("reinforced_white_terracotta", () -> new BaseReinforcedBlock(prop(MapColor.TERRACOTTA_WHITE), Blocks.WHITE_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_ORANGE_TERRACOTTA = BLOCKS.register("reinforced_orange_terracotta", () -> new BaseReinforcedBlock(prop(MapColor.TERRACOTTA_ORANGE), Blocks.ORANGE_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_MAGENTA_TERRACOTTA = BLOCKS.register("reinforced_magenta_terracotta", () -> new BaseReinforcedBlock(prop(MapColor.TERRACOTTA_MAGENTA), Blocks.MAGENTA_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_LIGHT_BLUE_TERRACOTTA = BLOCKS.register("reinforced_light_blue_terracotta", () -> new BaseReinforcedBlock(prop(MapColor.TERRACOTTA_LIGHT_BLUE), Blocks.LIGHT_BLUE_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_YELLOW_TERRACOTTA = BLOCKS.register("reinforced_yellow_terracotta", () -> new BaseReinforcedBlock(prop(MapColor.TERRACOTTA_YELLOW), Blocks.YELLOW_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_LIME_TERRACOTTA = BLOCKS.register("reinforced_lime_terracotta", () -> new BaseReinforcedBlock(prop(MapColor.TERRACOTTA_LIGHT_GREEN), Blocks.LIME_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_PINK_TERRACOTTA = BLOCKS.register("reinforced_pink_terracotta", () -> new BaseReinforcedBlock(prop(MapColor.TERRACOTTA_PINK), Blocks.PINK_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_GRAY_TERRACOTTA = BLOCKS.register("reinforced_gray_terracotta", () -> new BaseReinforcedBlock(prop(MapColor.TERRACOTTA_GRAY), Blocks.GRAY_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_LIGHT_GRAY_TERRACOTTA = BLOCKS.register("reinforced_light_gray_terracotta", () -> new BaseReinforcedBlock(prop(MapColor.TERRACOTTA_LIGHT_GRAY), Blocks.LIGHT_GRAY_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CYAN_TERRACOTTA = BLOCKS.register("reinforced_cyan_terracotta", () -> new BaseReinforcedBlock(prop(MapColor.TERRACOTTA_CYAN), Blocks.CYAN_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_PURPLE_TERRACOTTA = BLOCKS.register("reinforced_purple_terracotta", () -> new BaseReinforcedBlock(prop(MapColor.TERRACOTTA_PURPLE), Blocks.PURPLE_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BLUE_TERRACOTTA = BLOCKS.register("reinforced_blue_terracotta", () -> new BaseReinforcedBlock(prop(MapColor.TERRACOTTA_BLUE), Blocks.BLUE_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BROWN_TERRACOTTA = BLOCKS.register("reinforced_brown_terracotta", () -> new BaseReinforcedBlock(prop(MapColor.TERRACOTTA_BROWN), Blocks.BROWN_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_GREEN_TERRACOTTA = BLOCKS.register("reinforced_green_terracotta", () -> new BaseReinforcedBlock(prop(MapColor.TERRACOTTA_GREEN), Blocks.GREEN_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_RED_TERRACOTTA = BLOCKS.register("reinforced_red_terracotta", () -> new BaseReinforcedBlock(prop(MapColor.TERRACOTTA_RED), Blocks.RED_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BLACK_TERRACOTTA = BLOCKS.register("reinforced_black_terracotta", () -> new BaseReinforcedBlock(prop(MapColor.TERRACOTTA_BLACK), Blocks.BLACK_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_TERRACOTTA = BLOCKS.register("reinforced_hardened_clay", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_ORANGE), Blocks.TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_PACKED_ICE = BLOCKS.register("reinforced_packed_ice", () -> new BaseReinforcedBlock(prop(MapColor.ICE).sound(SoundType.GLASS).friction(0.98F), Blocks.PACKED_ICE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_ACACIA_STAIRS = BLOCKS.register("reinforced_acacia_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.COLOR_ORANGE).sound(SoundType.WOOD), Blocks.ACACIA_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_DARK_OAK_STAIRS = BLOCKS.register("reinforced_dark_oak_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.COLOR_BROWN).sound(SoundType.WOOD), Blocks.DARK_OAK_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_MANGROVE_STAIRS = BLOCKS.register("reinforced_mangrove_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.COLOR_RED).sound(SoundType.WOOD), Blocks.MANGROVE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_WHITE_STAINED_GLASS = BLOCKS.register("reinforced_white_stained_glass", () -> new ReinforcedStainedGlassBlock(glassProp(MapColor.SNOW), DyeColor.WHITE, Blocks.WHITE_STAINED_GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_ORANGE_STAINED_GLASS = BLOCKS.register("reinforced_orange_stained_glass", () -> new ReinforcedStainedGlassBlock(glassProp(MapColor.COLOR_ORANGE), DyeColor.ORANGE, Blocks.ORANGE_STAINED_GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_MAGENTA_STAINED_GLASS = BLOCKS.register("reinforced_magenta_stained_glass", () -> new ReinforcedStainedGlassBlock(glassProp(MapColor.COLOR_MAGENTA), DyeColor.MAGENTA, Blocks.MAGENTA_STAINED_GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_LIGHT_BLUE_STAINED_GLASS = BLOCKS.register("reinforced_light_blue_stained_glass", () -> new ReinforcedStainedGlassBlock(glassProp(MapColor.COLOR_LIGHT_BLUE), DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_STAINED_GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_YELLOW_STAINED_GLASS = BLOCKS.register("reinforced_yellow_stained_glass", () -> new ReinforcedStainedGlassBlock(glassProp(MapColor.COLOR_YELLOW), DyeColor.YELLOW, Blocks.YELLOW_STAINED_GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_LIME_STAINED_GLASS = BLOCKS.register("reinforced_lime_stained_glass", () -> new ReinforcedStainedGlassBlock(glassProp(MapColor.COLOR_LIGHT_GREEN), DyeColor.LIME, Blocks.LIME_STAINED_GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_PINK_STAINED_GLASS = BLOCKS.register("reinforced_pink_stained_glass", () -> new ReinforcedStainedGlassBlock(glassProp(MapColor.COLOR_PINK), DyeColor.PINK, Blocks.PINK_STAINED_GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_GRAY_STAINED_GLASS = BLOCKS.register("reinforced_gray_stained_glass", () -> new ReinforcedStainedGlassBlock(glassProp(MapColor.COLOR_GRAY), DyeColor.GRAY, Blocks.GRAY_STAINED_GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_LIGHT_GRAY_STAINED_GLASS = BLOCKS.register("reinforced_light_gray_stained_glass", () -> new ReinforcedStainedGlassBlock(glassProp(MapColor.COLOR_LIGHT_GRAY), DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_STAINED_GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_CYAN_STAINED_GLASS = BLOCKS.register("reinforced_cyan_stained_glass", () -> new ReinforcedStainedGlassBlock(glassProp(MapColor.COLOR_CYAN), DyeColor.CYAN, Blocks.CYAN_STAINED_GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_PURPLE_STAINED_GLASS = BLOCKS.register("reinforced_purple_stained_glass", () -> new ReinforcedStainedGlassBlock(glassProp(MapColor.COLOR_PURPLE), DyeColor.PURPLE, Blocks.PURPLE_STAINED_GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_BLUE_STAINED_GLASS = BLOCKS.register("reinforced_blue_stained_glass", () -> new ReinforcedStainedGlassBlock(glassProp(MapColor.COLOR_BLUE), DyeColor.BLUE, Blocks.BLUE_STAINED_GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_BROWN_STAINED_GLASS = BLOCKS.register("reinforced_brown_stained_glass", () -> new ReinforcedStainedGlassBlock(glassProp(MapColor.COLOR_BROWN), DyeColor.BROWN, Blocks.BROWN_STAINED_GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_GREEN_STAINED_GLASS = BLOCKS.register("reinforced_green_stained_glass", () -> new ReinforcedStainedGlassBlock(glassProp(MapColor.COLOR_GREEN), DyeColor.GREEN, Blocks.GREEN_STAINED_GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_RED_STAINED_GLASS = BLOCKS.register("reinforced_red_stained_glass", () -> new ReinforcedStainedGlassBlock(glassProp(MapColor.COLOR_RED), DyeColor.RED, Blocks.RED_STAINED_GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_BLACK_STAINED_GLASS = BLOCKS.register("reinforced_black_stained_glass", () -> new ReinforcedStainedGlassBlock(glassProp(MapColor.COLOR_BLACK), DyeColor.BLACK, Blocks.BLACK_STAINED_GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_PRISMARINE = BLOCKS.register("reinforced_prismarine", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_CYAN), Blocks.PRISMARINE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_PRISMARINE_BRICKS = BLOCKS.register("reinforced_prismarine_bricks", () -> new BaseReinforcedBlock(prop(MapColor.DIAMOND), Blocks.PRISMARINE_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_DARK_PRISMARINE = BLOCKS.register("reinforced_dark_prismarine", () -> new BaseReinforcedBlock(prop(MapColor.DIAMOND), Blocks.DARK_PRISMARINE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_PRISMARINE_STAIRS = BLOCKS.register("reinforced_prismarine_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.COLOR_CYAN), Blocks.PRISMARINE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_PRISMARINE_BRICK_STAIRS = BLOCKS.register("reinforced_prismarine_brick_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.DIAMOND), Blocks.PRISMARINE_BRICK_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_DARK_PRISMARINE_STAIRS = BLOCKS.register("reinforced_dark_prismarine_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.DIAMOND), Blocks.DARK_PRISMARINE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_SEA_LANTERN = BLOCKS.register("reinforced_sea_lantern", () -> new BaseReinforcedBlock(prop(MapColor.QUARTZ).sound(SoundType.GLASS).lightLevel(state -> 15).isRedstoneConductor(SCContent::never), Blocks.SEA_LANTERN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_RED_SANDSTONE = BLOCKS.register("reinforced_red_sandstone", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_ORANGE), Blocks.RED_SANDSTONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CHISELED_RED_SANDSTONE = BLOCKS.register("reinforced_chiseled_red_sandstone", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_ORANGE), Blocks.CHISELED_RED_SANDSTONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CUT_RED_SANDSTONE = BLOCKS.register("reinforced_cut_red_sandstone", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_ORANGE), Blocks.CUT_RED_SANDSTONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_RED_SANDSTONE_STAIRS = BLOCKS.register("reinforced_red_sandstone_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.COLOR_ORANGE), Blocks.RED_SANDSTONE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_NETHER_WART_BLOCK = BLOCKS.register("reinforced_nether_wart_block", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_RED).sound(SoundType.WART_BLOCK), Blocks.NETHER_WART_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_WARPED_WART_BLOCK = BLOCKS.register("reinforced_warped_wart_block", () -> new BaseReinforcedBlock(prop(MapColor.WARPED_WART_BLOCK).sound(SoundType.WART_BLOCK), Blocks.WARPED_WART_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_RED_NETHER_BRICKS = BLOCKS.register("reinforced_red_nether_bricks", () -> new BaseReinforcedBlock(prop(MapColor.NETHER).sound(SoundType.NETHER_BRICKS), Blocks.RED_NETHER_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_BONE_BLOCK = BLOCKS.register("reinforced_bone_block", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.SAND).sound(SoundType.BONE_BLOCK), Blocks.BONE_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_WHITE_CONCRETE = BLOCKS.register("reinforced_white_concrete", () -> new BaseReinforcedBlock(prop(MapColor.SNOW), Blocks.WHITE_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_ORANGE_CONCRETE = BLOCKS.register("reinforced_orange_concrete", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_ORANGE), Blocks.ORANGE_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_MAGENTA_CONCRETE = BLOCKS.register("reinforced_magenta_concrete", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_MAGENTA), Blocks.MAGENTA_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_LIGHT_BLUE_CONCRETE = BLOCKS.register("reinforced_light_blue_concrete", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_LIGHT_BLUE), Blocks.LIGHT_BLUE_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_YELLOW_CONCRETE = BLOCKS.register("reinforced_yellow_concrete", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_YELLOW), Blocks.YELLOW_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_LIME_CONCRETE = BLOCKS.register("reinforced_lime_concrete", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_LIGHT_GREEN), Blocks.LIME_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_PINK_CONCRETE = BLOCKS.register("reinforced_pink_concrete", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_PINK), Blocks.PINK_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_GRAY_CONCRETE = BLOCKS.register("reinforced_gray_concrete", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_GRAY), Blocks.GRAY_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_LIGHT_GRAY_CONCRETE = BLOCKS.register("reinforced_light_gray_concrete", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_LIGHT_GRAY), Blocks.LIGHT_GRAY_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CYAN_CONCRETE = BLOCKS.register("reinforced_cyan_concrete", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_CYAN), Blocks.CYAN_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_PURPLE_CONCRETE = BLOCKS.register("reinforced_purple_concrete", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_PURPLE), Blocks.PURPLE_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BLUE_CONCRETE = BLOCKS.register("reinforced_blue_concrete", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_BLUE), Blocks.BLUE_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BROWN_CONCRETE = BLOCKS.register("reinforced_brown_concrete", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_BROWN), Blocks.BROWN_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_GREEN_CONCRETE = BLOCKS.register("reinforced_green_concrete", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_GREEN), Blocks.GREEN_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_RED_CONCRETE = BLOCKS.register("reinforced_red_concrete", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_RED), Blocks.RED_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BLACK_CONCRETE = BLOCKS.register("reinforced_black_concrete", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_BLACK), Blocks.BLACK_CONCRETE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BLUE_ICE = BLOCKS.register("reinforced_blue_ice", () -> new BaseReinforcedBlock(prop(MapColor.ICE).sound(SoundType.GLASS).friction(0.989F), Blocks.BLUE_ICE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_POLISHED_GRANITE_STAIRS = BLOCKS.register("reinforced_polished_granite_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.DIRT), Blocks.POLISHED_GRANITE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_SMOOTH_RED_SANDSTONE_STAIRS = BLOCKS.register("reinforced_smooth_red_sandstone_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.COLOR_ORANGE), Blocks.SMOOTH_RED_SANDSTONE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_MOSSY_STONE_BRICK_STAIRS = BLOCKS.register("reinforced_mossy_stone_brick_stairs", () -> new ReinforcedStairsBlock(prop(), Blocks.MOSSY_STONE_BRICK_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_POLISHED_DIORITE_STAIRS = BLOCKS.register("reinforced_polished_diorite_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.QUARTZ), Blocks.POLISHED_DIORITE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_MOSSY_COBBLESTONE_STAIRS = BLOCKS.register("reinforced_mossy_cobblestone_stairs", () -> new ReinforcedStairsBlock(prop(), Blocks.MOSSY_COBBLESTONE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_END_STONE_BRICK_STAIRS = BLOCKS.register("reinforced_end_stone_brick_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.SAND), Blocks.END_STONE_BRICK_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_STONE_STAIRS = BLOCKS.register("reinforced_stone_stairs", () -> new ReinforcedStairsBlock(prop(), Blocks.STONE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_SMOOTH_SANDSTONE_STAIRS = BLOCKS.register("reinforced_smooth_sandstone_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.SAND), Blocks.SMOOTH_SANDSTONE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_SMOOTH_QUARTZ_STAIRS = BLOCKS.register("reinforced_smooth_quartz_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.QUARTZ), Blocks.SMOOTH_QUARTZ_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_GRANITE_STAIRS = BLOCKS.register("reinforced_granite_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.DIRT), Blocks.GRANITE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_ANDESITE_STAIRS = BLOCKS.register("reinforced_andesite_stairs", () -> new ReinforcedStairsBlock(prop(), Blocks.ANDESITE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_RED_NETHER_BRICK_STAIRS = BLOCKS.register("reinforced_red_nether_brick_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.NETHER).sound(SoundType.NETHER_BRICKS), Blocks.RED_NETHER_BRICK_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_POLISHED_ANDESITE_STAIRS = BLOCKS.register("reinforced_polished_andesite_stairs", () -> new ReinforcedStairsBlock(prop(), Blocks.POLISHED_ANDESITE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_DIORITE_STAIRS = BLOCKS.register("reinforced_diorite_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.QUARTZ), Blocks.DIORITE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_COBBLED_DEEPSLATE_STAIRS = BLOCKS.register("reinforced_cobbled_deepslate_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.DEEPSLATE).sound(SoundType.DEEPSLATE), Blocks.COBBLED_DEEPSLATE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_POLISHED_DEEPSLATE_STAIRS = BLOCKS.register("reinforced_polished_deepslate_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.DEEPSLATE).sound(SoundType.POLISHED_DEEPSLATE), Blocks.POLISHED_DEEPSLATE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_DEEPSLATE_BRICK_STAIRS = BLOCKS.register("reinforced_deepslate_brick_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.DEEPSLATE).sound(SoundType.DEEPSLATE_BRICKS), Blocks.DEEPSLATE_BRICK_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_DEEPSLATE_TILE_STAIRS = BLOCKS.register("reinforced_deepslate_tile_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.DEEPSLATE).sound(SoundType.DEEPSLATE_TILES), Blocks.DEEPSLATE_TILE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_POLISHED_GRANITE_SLAB = BLOCKS.register("reinforced_polished_granite_slab", () -> new ReinforcedSlabBlock(prop(MapColor.DIRT), Blocks.POLISHED_GRANITE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_SMOOTH_RED_SANDSTONE_SLAB = BLOCKS.register("reinforced_smooth_red_sandstone_slab", () -> new ReinforcedSlabBlock(prop(MapColor.COLOR_ORANGE), Blocks.SMOOTH_RED_SANDSTONE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_MOSSY_STONE_BRICK_SLAB = BLOCKS.register("reinforced_mossy_stone_brick_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.MOSSY_STONE_BRICK_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_POLISHED_DIORITE_SLAB = BLOCKS.register("reinforced_polished_diorite_slab", () -> new ReinforcedSlabBlock(prop(MapColor.QUARTZ), Blocks.POLISHED_DIORITE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_MOSSY_COBBLESTONE_SLAB = BLOCKS.register("reinforced_mossy_cobblestone_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.MOSSY_COBBLESTONE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_END_STONE_BRICK_SLAB = BLOCKS.register("reinforced_end_stone_brick_slab", () -> new ReinforcedSlabBlock(prop(MapColor.SAND), Blocks.END_STONE_BRICK_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_SMOOTH_SANDSTONE_SLAB = BLOCKS.register("reinforced_smooth_sandstone_slab", () -> new ReinforcedSlabBlock(prop(MapColor.SAND), Blocks.SMOOTH_SANDSTONE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_SMOOTH_QUARTZ_SLAB = BLOCKS.register("reinforced_smooth_quartz_slab", () -> new ReinforcedSlabBlock(prop(MapColor.QUARTZ), Blocks.SMOOTH_QUARTZ_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_GRANITE_SLAB = BLOCKS.register("reinforced_granite_slab", () -> new ReinforcedSlabBlock(prop(MapColor.DIRT), Blocks.GRANITE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_ANDESITE_SLAB = BLOCKS.register("reinforced_andesite_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.ANDESITE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_RED_NETHER_BRICK_SLAB = BLOCKS.register("reinforced_red_nether_brick_slab", () -> new ReinforcedSlabBlock(prop(MapColor.NETHER).sound(SoundType.NETHER_BRICKS), Blocks.RED_NETHER_BRICK_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_POLISHED_ANDESITE_SLAB = BLOCKS.register("reinforced_polished_andesite_slab", () -> new ReinforcedSlabBlock(prop(), Blocks.POLISHED_ANDESITE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_DIORITE_SLAB = BLOCKS.register("reinforced_diorite_slab", () -> new ReinforcedSlabBlock(prop(MapColor.QUARTZ), Blocks.DIORITE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_COBBLED_DEEPSLATE_SLAB = BLOCKS.register("reinforced_cobbled_deepslate_slab", () -> new ReinforcedSlabBlock(prop(MapColor.DEEPSLATE).sound(SoundType.DEEPSLATE), Blocks.COBBLED_DEEPSLATE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_POLISHED_DEEPSLATE_SLAB = BLOCKS.register("reinforced_polished_deepslate_slab", () -> new ReinforcedSlabBlock(prop(MapColor.DEEPSLATE).sound(SoundType.POLISHED_DEEPSLATE), Blocks.POLISHED_DEEPSLATE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_DEEPSLATE_BRICK_SLAB = BLOCKS.register("reinforced_deepslate_brick_slab", () -> new ReinforcedSlabBlock(prop(MapColor.DEEPSLATE).sound(SoundType.DEEPSLATE_BRICKS), Blocks.DEEPSLATE_BRICK_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_DEEPSLATE_TILE_SLAB = BLOCKS.register("reinforced_deepslate_tile_slab", () -> new ReinforcedSlabBlock(prop(MapColor.DEEPSLATE).sound(SoundType.DEEPSLATE_TILES), Blocks.DEEPSLATE_TILE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCryingObsidianBlock> REINFORCED_CRYING_OBSIDIAN = BLOCKS.register("reinforced_crying_obsidian", () -> new ReinforcedCryingObsidianBlock(prop(MapColor.COLOR_BLACK).lightLevel(state -> 10), Blocks.CRYING_OBSIDIAN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BLACKSTONE = BLOCKS.register("reinforced_blackstone", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_BLACK), Blocks.BLACKSTONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_BLACKSTONE_SLAB = BLOCKS.register("reinforced_blackstone_slab", () -> new ReinforcedSlabBlock(prop(MapColor.COLOR_BLACK), Blocks.BLACKSTONE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_BLACKSTONE_STAIRS = BLOCKS.register("reinforced_blackstone_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.COLOR_BLACK), Blocks.BLACKSTONE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_POLISHED_BLACKSTONE = BLOCKS.register("reinforced_polished_blackstone", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_BLACK), Blocks.POLISHED_BLACKSTONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_POLISHED_BLACKSTONE_SLAB = BLOCKS.register("reinforced_polished_blackstone_slab", () -> new ReinforcedSlabBlock(prop(MapColor.COLOR_BLACK), Blocks.POLISHED_BLACKSTONE_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_POLISHED_BLACKSTONE_STAIRS = BLOCKS.register("reinforced_polished_blackstone_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.COLOR_BLACK), Blocks.POLISHED_BLACKSTONE_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CHISELED_POLISHED_BLACKSTONE = BLOCKS.register("reinforced_chiseled_polished_blackstone", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_BLACK), Blocks.CHISELED_POLISHED_BLACKSTONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_POLISHED_BLACKSTONE_BRICKS = BLOCKS.register("reinforced_polished_blackstone_bricks", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_BLACK), Blocks.POLISHED_BLACKSTONE_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_POLISHED_BLACKSTONE_BRICK_SLAB = BLOCKS.register("reinforced_polished_blackstone_brick_slab", () -> new ReinforcedSlabBlock(prop(MapColor.COLOR_BLACK), Blocks.POLISHED_BLACKSTONE_BRICK_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_POLISHED_BLACKSTONE_BRICK_STAIRS = BLOCKS.register("reinforced_polished_blackstone_brick_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.COLOR_BLACK), Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CRACKED_POLISHED_BLACKSTONE_BRICKS = BLOCKS.register("reinforced_cracked_polished_blackstone_bricks", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_BLACK), Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS));

	//ordered by vanilla <1.19.3 decoration blocks creative tab order
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCobwebBlock> REINFORCED_COBWEB = BLOCKS.register("reinforced_cobweb", () -> new ReinforcedCobwebBlock(prop(MapColor.WOOL).forceSolidOn().noCollission(), Blocks.COBWEB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_MOSS_CARPET = BLOCKS.register("reinforced_moss_carpet", () -> new ReinforcedCarpetBlock(prop(MapColor.COLOR_GREEN).sound(SoundType.MOSS_CARPET).forceSolidOn(), Blocks.MOSS_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_MOSS_BLOCK = BLOCKS.register("reinforced_moss_block", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_GREEN).sound(SoundType.MOSS), Blocks.MOSS_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedEndRodBlock> REINFORCED_END_ROD = BLOCKS.register("reinforced_end_rod", () -> new ReinforcedEndRodBlock(prop(MapColor.NONE).lightLevel(state -> 14).sound(SoundType.WOOD).noOcclusion()));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedIronBarsBlock> REINFORCED_IRON_BARS = BLOCKS.register("reinforced_iron_bars", () -> new ReinforcedIronBarsBlock(prop(MapColor.NONE).sound(SoundType.METAL), Blocks.IRON_BARS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedLadderBlock> REINFORCED_LADDER = BLOCKS.register("reinforced_ladder", () -> new ReinforcedLadderBlock(prop().forceSolidOff().sound(SoundType.LADDER).noOcclusion().pushReaction(PushReaction.DESTROY)));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedChainBlock> REINFORCED_CHAIN = BLOCKS.register("reinforced_chain", () -> new ReinforcedChainBlock(prop(MapColor.NONE).sound(SoundType.CHAIN), Blocks.CHAIN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedPaneBlock> REINFORCED_GLASS_PANE = BLOCKS.register("reinforced_glass_pane", () -> new ReinforcedPaneBlock(prop(MapColor.NONE).sound(SoundType.GLASS), Blocks.GLASS_PANE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_COBBLESTONE_WALL = BLOCKS.register("reinforced_cobblestone_wall", () -> new ReinforcedWallBlock(prop(), Blocks.COBBLESTONE_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_MOSSY_COBBLESTONE_WALL = BLOCKS.register("reinforced_mossy_cobblestone_wall", () -> new ReinforcedWallBlock(prop(), Blocks.MOSSY_COBBLESTONE_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_BRICK_WALL = BLOCKS.register("reinforced_brick_wall", () -> new ReinforcedWallBlock(prop(MapColor.COLOR_RED), Blocks.BRICK_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_PRISMARINE_WALL = BLOCKS.register("reinforced_prismarine_wall", () -> new ReinforcedWallBlock(prop(MapColor.COLOR_CYAN), Blocks.PRISMARINE_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_RED_SANDSTONE_WALL = BLOCKS.register("reinforced_red_sandstone_wall", () -> new ReinforcedWallBlock(prop(MapColor.COLOR_ORANGE), Blocks.RED_SANDSTONE_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_MOSSY_STONE_BRICK_WALL = BLOCKS.register("reinforced_mossy_stone_brick_wall", () -> new ReinforcedWallBlock(prop(), Blocks.MOSSY_STONE_BRICK_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_GRANITE_WALL = BLOCKS.register("reinforced_granite_wall", () -> new ReinforcedWallBlock(prop(MapColor.DIRT), Blocks.GRANITE_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_STONE_BRICK_WALL = BLOCKS.register("reinforced_stone_brick_wall", () -> new ReinforcedWallBlock(prop(), Blocks.STONE_BRICK_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_MUD_BRICK_WALL = BLOCKS.register("reinforced_mud_brick_wall", () -> new ReinforcedWallBlock(prop(MapColor.TERRACOTTA_LIGHT_GRAY).sound(SoundType.MUD_BRICKS), Blocks.MUD_BRICK_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_NETHER_BRICK_WALL = BLOCKS.register("reinforced_nether_brick_wall", () -> new ReinforcedWallBlock(prop(MapColor.NETHER).sound(SoundType.NETHER_BRICKS), Blocks.NETHER_BRICK_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_ANDESITE_WALL = BLOCKS.register("reinforced_andesite_wall", () -> new ReinforcedWallBlock(prop(), Blocks.ANDESITE_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_RED_NETHER_BRICK_WALL = BLOCKS.register("reinforced_red_nether_brick_wall", () -> new ReinforcedWallBlock(prop(MapColor.NETHER).sound(SoundType.NETHER_BRICKS), Blocks.RED_NETHER_BRICK_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_SANDSTONE_WALL = BLOCKS.register("reinforced_sandstone_wall", () -> new ReinforcedWallBlock(prop(MapColor.SAND), Blocks.SANDSTONE_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_END_STONE_BRICK_WALL = BLOCKS.register("reinforced_end_stone_brick_wall", () -> new ReinforcedWallBlock(prop(MapColor.SAND), Blocks.END_STONE_BRICK_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_DIORITE_WALL = BLOCKS.register("reinforced_diorite_wall", () -> new ReinforcedWallBlock(prop(MapColor.QUARTZ), Blocks.DIORITE_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_BLACKSTONE_WALL = BLOCKS.register("reinforced_blackstone_wall", () -> new ReinforcedWallBlock(prop(MapColor.COLOR_BLACK), Blocks.BLACKSTONE_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_POLISHED_BLACKSTONE_WALL = BLOCKS.register("reinforced_polished_blackstone_wall", () -> new ReinforcedWallBlock(prop(MapColor.COLOR_BLACK), Blocks.POLISHED_BLACKSTONE_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_POLISHED_BLACKSTONE_BRICK_WALL = BLOCKS.register("reinforced_polished_blackstone_brick_wall", () -> new ReinforcedWallBlock(prop(MapColor.COLOR_BLACK), Blocks.POLISHED_BLACKSTONE_BRICK_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_COBBLED_DEEPSLATE_WALL = BLOCKS.register("reinforced_cobbled_deepslate_wall", () -> new ReinforcedWallBlock(prop(MapColor.DEEPSLATE).sound(SoundType.DEEPSLATE), Blocks.COBBLED_DEEPSLATE_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_POLISHED_DEEPSLATE_WALL = BLOCKS.register("reinforced_polished_deepslate_wall", () -> new ReinforcedWallBlock(prop(MapColor.DEEPSLATE).sound(SoundType.POLISHED_DEEPSLATE), Blocks.POLISHED_DEEPSLATE_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_DEEPSLATE_BRICK_WALL = BLOCKS.register("reinforced_deepslate_brick_wall", () -> new ReinforcedWallBlock(prop(MapColor.DEEPSLATE).sound(SoundType.DEEPSLATE_BRICKS), Blocks.DEEPSLATE_BRICK_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_DEEPSLATE_TILE_WALL = BLOCKS.register("reinforced_deepslate_tile_wall", () -> new ReinforcedWallBlock(prop(MapColor.DEEPSLATE).sound(SoundType.DEEPSLATE_TILES), Blocks.DEEPSLATE_TILE_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_WHITE_CARPET = BLOCKS.register("reinforced_white_carpet", () -> new ReinforcedCarpetBlock(prop(MapColor.SNOW).sound(SoundType.WOOL).forceSolidOn(), Blocks.WHITE_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_ORANGE_CARPET = BLOCKS.register("reinforced_orange_carpet", () -> new ReinforcedCarpetBlock(prop(MapColor.COLOR_ORANGE).sound(SoundType.WOOL).forceSolidOn(), Blocks.ORANGE_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_MAGENTA_CARPET = BLOCKS.register("reinforced_magenta_carpet", () -> new ReinforcedCarpetBlock(prop(MapColor.COLOR_MAGENTA).sound(SoundType.WOOL).forceSolidOn(), Blocks.MAGENTA_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_LIGHT_BLUE_CARPET = BLOCKS.register("reinforced_light_blue_carpet", () -> new ReinforcedCarpetBlock(prop(MapColor.COLOR_LIGHT_BLUE).sound(SoundType.WOOL).forceSolidOn(), Blocks.LIGHT_BLUE_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_YELLOW_CARPET = BLOCKS.register("reinforced_yellow_carpet", () -> new ReinforcedCarpetBlock(prop(MapColor.COLOR_YELLOW).sound(SoundType.WOOL).forceSolidOn(), Blocks.YELLOW_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_LIME_CARPET = BLOCKS.register("reinforced_lime_carpet", () -> new ReinforcedCarpetBlock(prop(MapColor.COLOR_LIGHT_GREEN).sound(SoundType.WOOL).forceSolidOn(), Blocks.LIME_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_PINK_CARPET = BLOCKS.register("reinforced_pink_carpet", () -> new ReinforcedCarpetBlock(prop(MapColor.COLOR_PINK).sound(SoundType.WOOL).forceSolidOn(), Blocks.PINK_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_GRAY_CARPET = BLOCKS.register("reinforced_gray_carpet", () -> new ReinforcedCarpetBlock(prop(MapColor.COLOR_GRAY).sound(SoundType.WOOL).forceSolidOn(), Blocks.GRAY_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_LIGHT_GRAY_CARPET = BLOCKS.register("reinforced_light_gray_carpet", () -> new ReinforcedCarpetBlock(prop(MapColor.COLOR_LIGHT_GRAY).sound(SoundType.WOOL).forceSolidOn(), Blocks.LIGHT_GRAY_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_CYAN_CARPET = BLOCKS.register("reinforced_cyan_carpet", () -> new ReinforcedCarpetBlock(prop(MapColor.COLOR_CYAN).sound(SoundType.WOOL).forceSolidOn(), Blocks.CYAN_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_PURPLE_CARPET = BLOCKS.register("reinforced_purple_carpet", () -> new ReinforcedCarpetBlock(prop(MapColor.COLOR_PURPLE).sound(SoundType.WOOL).forceSolidOn(), Blocks.PURPLE_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_BLUE_CARPET = BLOCKS.register("reinforced_blue_carpet", () -> new ReinforcedCarpetBlock(prop(MapColor.COLOR_BLUE).sound(SoundType.WOOL).forceSolidOn(), Blocks.BLUE_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_BROWN_CARPET = BLOCKS.register("reinforced_brown_carpet", () -> new ReinforcedCarpetBlock(prop(MapColor.COLOR_BROWN).sound(SoundType.WOOL).forceSolidOn(), Blocks.BROWN_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_GREEN_CARPET = BLOCKS.register("reinforced_green_carpet", () -> new ReinforcedCarpetBlock(prop(MapColor.COLOR_GREEN).sound(SoundType.WOOL).forceSolidOn(), Blocks.GREEN_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_RED_CARPET = BLOCKS.register("reinforced_red_carpet", () -> new ReinforcedCarpetBlock(prop(MapColor.COLOR_RED).sound(SoundType.WOOL).forceSolidOn(), Blocks.RED_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_BLACK_CARPET = BLOCKS.register("reinforced_black_carpet", () -> new ReinforcedCarpetBlock(prop(MapColor.COLOR_BLACK).sound(SoundType.WOOL).forceSolidOn(), Blocks.BLACK_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_WHITE_STAINED_GLASS_PANE = BLOCKS.register("reinforced_white_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(prop(MapColor.NONE).sound(SoundType.GLASS), DyeColor.WHITE, Blocks.WHITE_STAINED_GLASS_PANE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_ORANGE_STAINED_GLASS_PANE = BLOCKS.register("reinforced_orange_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(prop(MapColor.NONE).sound(SoundType.GLASS), DyeColor.ORANGE, Blocks.ORANGE_STAINED_GLASS_PANE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_MAGENTA_STAINED_GLASS_PANE = BLOCKS.register("reinforced_magenta_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(prop(MapColor.NONE).sound(SoundType.GLASS), DyeColor.MAGENTA, Blocks.MAGENTA_STAINED_GLASS_PANE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_LIGHT_BLUE_STAINED_GLASS_PANE = BLOCKS.register("reinforced_light_blue_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(prop(MapColor.NONE).sound(SoundType.GLASS), DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_YELLOW_STAINED_GLASS_PANE = BLOCKS.register("reinforced_yellow_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(prop(MapColor.NONE).sound(SoundType.GLASS), DyeColor.YELLOW, Blocks.YELLOW_STAINED_GLASS_PANE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_LIME_STAINED_GLASS_PANE = BLOCKS.register("reinforced_lime_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(prop(MapColor.NONE).sound(SoundType.GLASS), DyeColor.LIME, Blocks.LIME_STAINED_GLASS_PANE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_PINK_STAINED_GLASS_PANE = BLOCKS.register("reinforced_pink_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(prop(MapColor.NONE).sound(SoundType.GLASS), DyeColor.PINK, Blocks.PINK_STAINED_GLASS_PANE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_GRAY_STAINED_GLASS_PANE = BLOCKS.register("reinforced_gray_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(prop(MapColor.NONE).sound(SoundType.GLASS), DyeColor.GRAY, Blocks.GRAY_STAINED_GLASS_PANE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_LIGHT_GRAY_STAINED_GLASS_PANE = BLOCKS.register("reinforced_light_gray_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(prop(MapColor.NONE).sound(SoundType.GLASS), DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_CYAN_STAINED_GLASS_PANE = BLOCKS.register("reinforced_cyan_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(prop(MapColor.NONE).sound(SoundType.GLASS), DyeColor.CYAN, Blocks.CYAN_STAINED_GLASS_PANE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_PURPLE_STAINED_GLASS_PANE = BLOCKS.register("reinforced_purple_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(prop(MapColor.NONE).sound(SoundType.GLASS), DyeColor.PURPLE, Blocks.PURPLE_STAINED_GLASS_PANE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_BLUE_STAINED_GLASS_PANE = BLOCKS.register("reinforced_blue_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(prop(MapColor.NONE).sound(SoundType.GLASS), DyeColor.BLUE, Blocks.BLUE_STAINED_GLASS_PANE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_BROWN_STAINED_GLASS_PANE = BLOCKS.register("reinforced_brown_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(prop(MapColor.NONE).sound(SoundType.GLASS), DyeColor.BROWN, Blocks.BROWN_STAINED_GLASS_PANE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_GREEN_STAINED_GLASS_PANE = BLOCKS.register("reinforced_green_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(prop(MapColor.NONE).sound(SoundType.GLASS), DyeColor.GREEN, Blocks.GREEN_STAINED_GLASS_PANE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_RED_STAINED_GLASS_PANE = BLOCKS.register("reinforced_red_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(prop(MapColor.NONE).sound(SoundType.GLASS), DyeColor.RED, Blocks.RED_STAINED_GLASS_PANE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_BLACK_STAINED_GLASS_PANE = BLOCKS.register("reinforced_black_stained_glass_pane", () -> new ReinforcedStainedGlassPaneBlock(prop(MapColor.NONE).sound(SoundType.GLASS), DyeColor.BLACK, Blocks.BLACK_STAINED_GLASS_PANE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_WHITE_GLAZED_TERRACOTTA = BLOCKS.register("reinforced_white_glazed_terracotta", () -> new ReinforcedGlazedTerracottaBlock(prop(DyeColor.WHITE.getMapColor()), Blocks.WHITE_GLAZED_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_ORANGE_GLAZED_TERRACOTTA = BLOCKS.register("reinforced_orange_glazed_terracotta", () -> new ReinforcedGlazedTerracottaBlock(prop(DyeColor.ORANGE.getMapColor()), Blocks.ORANGE_GLAZED_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_MAGENTA_GLAZED_TERRACOTTA = BLOCKS.register("reinforced_magenta_glazed_terracotta", () -> new ReinforcedGlazedTerracottaBlock(prop(DyeColor.MAGENTA.getMapColor()), Blocks.MAGENTA_GLAZED_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_LIGHT_BLUE_GLAZED_TERRACOTTA = BLOCKS.register("reinforced_light_blue_glazed_terracotta", () -> new ReinforcedGlazedTerracottaBlock(prop(DyeColor.LIGHT_BLUE.getMapColor()), Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_YELLOW_GLAZED_TERRACOTTA = BLOCKS.register("reinforced_yellow_glazed_terracotta", () -> new ReinforcedGlazedTerracottaBlock(prop(DyeColor.YELLOW.getMapColor()), Blocks.YELLOW_GLAZED_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_LIME_GLAZED_TERRACOTTA = BLOCKS.register("reinforced_lime_glazed_terracotta", () -> new ReinforcedGlazedTerracottaBlock(prop(DyeColor.LIME.getMapColor()), Blocks.LIME_GLAZED_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_PINK_GLAZED_TERRACOTTA = BLOCKS.register("reinforced_pink_glazed_terracotta", () -> new ReinforcedGlazedTerracottaBlock(prop(DyeColor.PINK.getMapColor()), Blocks.PINK_GLAZED_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_GRAY_GLAZED_TERRACOTTA = BLOCKS.register("reinforced_gray_glazed_terracotta", () -> new ReinforcedGlazedTerracottaBlock(prop(DyeColor.GRAY.getMapColor()), Blocks.GRAY_GLAZED_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_LIGHT_GRAY_GLAZED_TERRACOTTA = BLOCKS.register("reinforced_light_gray_glazed_terracotta", () -> new ReinforcedGlazedTerracottaBlock(prop(DyeColor.LIGHT_GRAY.getMapColor()), Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_CYAN_GLAZED_TERRACOTTA = BLOCKS.register("reinforced_cyan_glazed_terracotta", () -> new ReinforcedGlazedTerracottaBlock(prop(DyeColor.CYAN.getMapColor()), Blocks.CYAN_GLAZED_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_PURPLE_GLAZED_TERRACOTTA = BLOCKS.register("reinforced_purple_glazed_terracotta", () -> new ReinforcedGlazedTerracottaBlock(prop(DyeColor.PURPLE.getMapColor()), Blocks.PURPLE_GLAZED_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_BLUE_GLAZED_TERRACOTTA = BLOCKS.register("reinforced_blue_glazed_terracotta", () -> new ReinforcedGlazedTerracottaBlock(prop(DyeColor.BLUE.getMapColor()), Blocks.BLUE_GLAZED_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_BROWN_GLAZED_TERRACOTTA = BLOCKS.register("reinforced_brown_glazed_terracotta", () -> new ReinforcedGlazedTerracottaBlock(prop(DyeColor.BROWN.getMapColor()), Blocks.BROWN_GLAZED_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_GREEN_GLAZED_TERRACOTTA = BLOCKS.register("reinforced_green_glazed_terracotta", () -> new ReinforcedGlazedTerracottaBlock(prop(DyeColor.GREEN.getMapColor()), Blocks.GREEN_GLAZED_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_RED_GLAZED_TERRACOTTA = BLOCKS.register("reinforced_red_glazed_terracotta", () -> new ReinforcedGlazedTerracottaBlock(prop(DyeColor.RED.getMapColor()), Blocks.RED_GLAZED_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_BLACK_GLAZED_TERRACOTTA = BLOCKS.register("reinforced_black_glazed_terracotta", () -> new ReinforcedGlazedTerracottaBlock(prop(DyeColor.BLACK.getMapColor()), Blocks.BLACK_GLAZED_TERRACOTTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedLanternBlock> REINFORCED_LANTERN = BLOCKS.register("reinforced_lantern", () -> new ReinforcedLanternBlock(prop(MapColor.METAL).sound(SoundType.LANTERN).lightLevel(state -> 15).pushReaction(PushReaction.BLOCK).forceSolidOn(), Blocks.LANTERN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedLanternBlock> REINFORCED_SOUL_LANTERN = BLOCKS.register("reinforced_soul_lantern", () -> new ReinforcedLanternBlock(prop(MapColor.METAL).sound(SoundType.LANTERN).lightLevel(state -> 10).pushReaction(PushReaction.BLOCK).forceSolidOn(), Blocks.SOUL_LANTERN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_SHROOMLIGHT = BLOCKS.register("reinforced_shroomlight", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_RED).sound(SoundType.SHROOMLIGHT).lightLevel(state -> 15), Blocks.SHROOMLIGHT));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_OCHRE_FROGLIGHT = BLOCKS.register("reinforced_ochre_froglight", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.SAND).sound(SoundType.FROGLIGHT).lightLevel(state -> 15), Blocks.OCHRE_FROGLIGHT));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_VERDANT_FROGLIGHT = BLOCKS.register("reinforced_verdant_froglight", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.GLOW_LICHEN).sound(SoundType.FROGLIGHT).lightLevel(state -> 15), Blocks.VERDANT_FROGLIGHT));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_PEARLESCENT_FROGLIGHT = BLOCKS.register("reinforced_pearlescent_froglight", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.COLOR_PINK).sound(SoundType.FROGLIGHT).lightLevel(state -> 15), Blocks.PEARLESCENT_FROGLIGHT));

	//ordered by vanilla <1.19.3 redstone tab order
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRedstoneBlock> REINFORCED_REDSTONE_BLOCK = BLOCKS.register("reinforced_redstone_block", () -> new ReinforcedRedstoneBlock(prop(MapColor.FIRE).sound(SoundType.METAL).isRedstoneConductor(SCContent::never), Blocks.REDSTONE_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final DeferredBlock<ReinforcedPistonBaseBlock> REINFORCED_PISTON = BLOCKS.register("reinforced_piston", () -> new ReinforcedPistonBaseBlock(false, prop().isRedstoneConductor(SCContent::never).isSuffocating((s, w, p) -> !s.getValue(PistonBaseBlock.EXTENDED)).isViewBlocking((s, w, p) -> !s.getValue(PistonBaseBlock.EXTENDED))));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final DeferredBlock<ReinforcedPistonBaseBlock> REINFORCED_STICKY_PISTON = BLOCKS.register("reinforced_sticky_piston", () -> new ReinforcedPistonBaseBlock(true, prop().isRedstoneConductor(SCContent::never).isSuffocating((s, w, p) -> !s.getValue(PistonBaseBlock.EXTENDED)).isViewBlocking((s, w, p) -> !s.getValue(PistonBaseBlock.EXTENDED))));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedObserverBlock> REINFORCED_OBSERVER = BLOCKS.register("reinforced_observer", () -> new ReinforcedObserverBlock(propDisguisable().isRedstoneConductor(SCContent::never)));
	@HasManualPage
	@Reinforced
	public static final DeferredBlock<ReinforcedHopperBlock> REINFORCED_HOPPER = BLOCKS.register("reinforced_hopper", () -> new ReinforcedHopperBlock(propDisguisable().sound(SoundType.METAL)));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final DeferredBlock<ReinforcedDispenserBlock> REINFORCED_DISPENSER = BLOCKS.register("reinforced_dispenser", () -> new ReinforcedDispenserBlock(propDisguisable()));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final DeferredBlock<ReinforcedDropperBlock> REINFORCED_DROPPER = BLOCKS.register("reinforced_dropper", () -> new ReinforcedDropperBlock(propDisguisable()));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final DeferredBlock<ReinforcedLecternBlock> REINFORCED_LECTERN = BLOCKS.register("reinforced_lectern", () -> new ReinforcedLecternBlock(prop(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).sound(SoundType.WOOD)));
	@HasManualPage
	@Reinforced
	public static final DeferredBlock<ReinforcedLeverBlock> REINFORCED_LEVER = BLOCKS.register("reinforced_lever", () -> new ReinforcedLeverBlock(prop(MapColor.NONE).noCollission().sound(SoundType.STONE).pushReaction(PushReaction.BLOCK).forceSolidOn()));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRedstoneLampBlock> REINFORCED_REDSTONE_LAMP = BLOCKS.register("reinforced_redstone_lamp", () -> new ReinforcedRedstoneLampBlock(prop(MapColor.NONE).sound(SoundType.GLASS).lightLevel(state -> state.getValue(ReinforcedRedstoneLampBlock.LIT) ? 15 : 0), Blocks.REDSTONE_LAMP));
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final DeferredBlock<ReinforcedButtonBlock> REINFORCED_STONE_BUTTON = BLOCKS.register("reinforced_stone_button", () -> stoneButton(Blocks.STONE_BUTTON, BlockSetType.STONE));
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final DeferredBlock<ReinforcedButtonBlock> REINFORCED_POLISHED_BLACKSTONE_BUTTON = BLOCKS.register("reinforced_polished_blackstone_button", () -> stoneButton(Blocks.POLISHED_BLACKSTONE_BUTTON, BlockSetType.POLISHED_BLACKSTONE));
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final DeferredBlock<ReinforcedButtonBlock> REINFORCED_OAK_BUTTON = BLOCKS.register("reinforced_oak_button", () -> woodenButton(Blocks.OAK_BUTTON, BlockSetType.OAK));
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final DeferredBlock<ReinforcedButtonBlock> REINFORCED_SPRUCE_BUTTON = BLOCKS.register("reinforced_spruce_button", () -> woodenButton(Blocks.SPRUCE_BUTTON, BlockSetType.SPRUCE));
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final DeferredBlock<ReinforcedButtonBlock> REINFORCED_BIRCH_BUTTON = BLOCKS.register("reinforced_birch_button", () -> woodenButton(Blocks.BIRCH_BUTTON, BlockSetType.BIRCH));
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final DeferredBlock<ReinforcedButtonBlock> REINFORCED_JUNGLE_BUTTON = BLOCKS.register("reinforced_jungle_button", () -> woodenButton(Blocks.JUNGLE_BUTTON, BlockSetType.JUNGLE));
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final DeferredBlock<ReinforcedButtonBlock> REINFORCED_ACACIA_BUTTON = BLOCKS.register("reinforced_acacia_button", () -> woodenButton(Blocks.ACACIA_BUTTON, BlockSetType.ACACIA));
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final DeferredBlock<ReinforcedButtonBlock> REINFORCED_DARK_OAK_BUTTON = BLOCKS.register("reinforced_dark_oak_button", () -> woodenButton(Blocks.DARK_OAK_BUTTON, BlockSetType.DARK_OAK));
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final DeferredBlock<ReinforcedButtonBlock> REINFORCED_MANGROVE_BUTTON = BLOCKS.register("reinforced_mangrove_button", () -> woodenButton(Blocks.MANGROVE_BUTTON, BlockSetType.MANGROVE));
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final DeferredBlock<ReinforcedButtonBlock> REINFORCED_CRIMSON_BUTTON = BLOCKS.register("reinforced_crimson_button", () -> woodenButton(Blocks.CRIMSON_BUTTON, BlockSetType.CRIMSON));
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final DeferredBlock<ReinforcedButtonBlock> REINFORCED_WARPED_BUTTON = BLOCKS.register("reinforced_warped_button", () -> woodenButton(Blocks.WARPED_BUTTON, BlockSetType.WARPED));
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedPressurePlateBlock> REINFORCED_STONE_PRESSURE_PLATE = BLOCKS.register("reinforced_stone_pressure_plate", () -> stonePressurePlate(Blocks.STONE_PRESSURE_PLATE, BlockSetType.STONE));
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedPressurePlateBlock> REINFORCED_POLISHED_BLACKSTONE_PRESSURE_PLATE = BLOCKS.register("reinforced_polished_blackstone_pressure_plate", () -> stonePressurePlate(Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE, BlockSetType.POLISHED_BLACKSTONE));
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedPressurePlateBlock> REINFORCED_OAK_PRESSURE_PLATE = BLOCKS.register("reinforced_oak_pressure_plate", () -> woodenPressurePlate(Blocks.OAK_PRESSURE_PLATE, BlockSetType.OAK));
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedPressurePlateBlock> REINFORCED_SPRUCE_PRESSURE_PLATE = BLOCKS.register("reinforced_spruce_pressure_plate", () -> woodenPressurePlate(Blocks.SPRUCE_PRESSURE_PLATE, BlockSetType.SPRUCE));
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedPressurePlateBlock> REINFORCED_BIRCH_PRESSURE_PLATE = BLOCKS.register("reinforced_birch_pressure_plate", () -> woodenPressurePlate(Blocks.BIRCH_PRESSURE_PLATE, BlockSetType.BIRCH));
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedPressurePlateBlock> REINFORCED_JUNGLE_PRESSURE_PLATE = BLOCKS.register("reinforced_jungle_pressure_plate", () -> woodenPressurePlate(Blocks.JUNGLE_PRESSURE_PLATE, BlockSetType.JUNGLE));
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedPressurePlateBlock> REINFORCED_ACACIA_PRESSURE_PLATE = BLOCKS.register("reinforced_acacia_pressure_plate", () -> woodenPressurePlate(Blocks.ACACIA_PRESSURE_PLATE, BlockSetType.ACACIA));
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedPressurePlateBlock> REINFORCED_DARK_OAK_PRESSURE_PLATE = BLOCKS.register("reinforced_dark_oak_pressure_plate", () -> woodenPressurePlate(Blocks.DARK_OAK_PRESSURE_PLATE, BlockSetType.DARK_OAK));
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedPressurePlateBlock> REINFORCED_MANGROVE_PRESSURE_PLATE = BLOCKS.register("reinforced_mangrove_pressure_plate", () -> woodenPressurePlate(Blocks.MANGROVE_PRESSURE_PLATE, BlockSetType.MANGROVE));
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedPressurePlateBlock> REINFORCED_CRIMSON_PRESSURE_PLATE = BLOCKS.register("reinforced_crimson_pressure_plate", () -> woodenPressurePlate(Blocks.CRIMSON_PRESSURE_PLATE, BlockSetType.CRIMSON));
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedPressurePlateBlock> REINFORCED_WARPED_PRESSURE_PLATE = BLOCKS.register("reinforced_warped_pressure_plate", () -> woodenPressurePlate(Blocks.WARPED_PRESSURE_PLATE, BlockSetType.WARPED));
	@HasManualPage(hasRecipeDescription = true)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false, itemGroup = SCItemGroup.MANUAL)
	public static final DeferredBlock<ReinforcedIronTrapDoorBlock> REINFORCED_IRON_TRAPDOOR = BLOCKS.register("reinforced_iron_trapdoor", () -> new ReinforcedIronTrapDoorBlock(prop(MapColor.METAL).sound(SoundType.METAL).noOcclusion().isValidSpawn(SCContent::never), BlockSetType.IRON));

	//ordered by vanilla <1.19.3 brewing tab order
	@Reinforced
	public static final DeferredBlock<ReinforcedCauldronBlock> REINFORCED_CAULDRON = BLOCKS.register("reinforced_cauldron", () -> new ReinforcedCauldronBlock(prop(MapColor.STONE).noOcclusion(), IReinforcedCauldronInteraction.EMPTY));
	@Reinforced(registerBlockItem = false)
	public static final DeferredBlock<ReinforcedLayeredCauldronBlock> REINFORCED_WATER_CAULDRON = BLOCKS.register("reinforced_water_cauldron", () -> new ReinforcedLayeredCauldronBlock(Biome.Precipitation.RAIN, IReinforcedCauldronInteraction.WATER, prop(MapColor.STONE).noOcclusion(), Blocks.WATER_CAULDRON));
	@Reinforced(registerBlockItem = false)
	public static final DeferredBlock<ReinforcedLavaCauldronBlock> REINFORCED_LAVA_CAULDRON = BLOCKS.register("reinforced_lava_cauldron", () -> new ReinforcedLavaCauldronBlock(prop(MapColor.STONE).noOcclusion().lightLevel(state -> 15)));
	@Reinforced(registerBlockItem = false)
	public static final DeferredBlock<ReinforcedLayeredCauldronBlock> REINFORCED_POWDER_SNOW_CAULDRON = BLOCKS.register("reinforced_powder_snow_cauldron", () -> new ReinforcedLayeredCauldronBlock(Biome.Precipitation.SNOW, IReinforcedCauldronInteraction.POWDER_SNOW, prop(MapColor.STONE).noOcclusion(), Blocks.POWDER_SNOW_CAULDRON));

	//1.19.3+ content
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_BAMBOO_BLOCK = BLOCKS.register("reinforced_bamboo_block", () -> new ReinforcedRotatedPillarBlock(logProp(MapColor.COLOR_YELLOW, MapColor.PLANT).sound(SoundType.BAMBOO_WOOD), Blocks.BAMBOO_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_BAMBOO_BLOCK = BLOCKS.register("reinforced_stripped_bamboo_block", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.COLOR_YELLOW).sound(SoundType.BAMBOO_WOOD), Blocks.STRIPPED_BAMBOO_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BAMBOO_PLANKS = BLOCKS.register("reinforced_bamboo_planks", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_YELLOW).sound(SoundType.BAMBOO_WOOD), Blocks.BAMBOO_PLANKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BAMBOO_MOSAIC = BLOCKS.register("reinforced_bamboo_mosaic", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_YELLOW).sound(SoundType.BAMBOO_WOOD), Blocks.BAMBOO_MOSAIC));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_BAMBOO_STAIRS = BLOCKS.register("reinforced_bamboo_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.COLOR_YELLOW).sound(SoundType.BAMBOO_WOOD), Blocks.BAMBOO_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_BAMBOO_MOSAIC_STAIRS = BLOCKS.register("reinforced_bamboo_mosaic_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.COLOR_YELLOW).sound(SoundType.BAMBOO_WOOD), Blocks.BAMBOO_MOSAIC_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_BAMBOO_SLAB = BLOCKS.register("reinforced_bamboo_slab", () -> new ReinforcedSlabBlock(prop(MapColor.COLOR_YELLOW).sound(SoundType.BAMBOO_WOOD), Blocks.BAMBOO_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_BAMBOO_MOSAIC_SLAB = BLOCKS.register("reinforced_bamboo_mosaic_slab", () -> new ReinforcedSlabBlock(prop(MapColor.COLOR_YELLOW).sound(SoundType.BAMBOO_WOOD), Blocks.BAMBOO_MOSAIC_SLAB));
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedPressurePlateBlock> REINFORCED_BAMBOO_PRESSURE_PLATE = BLOCKS.register("reinforced_bamboo_pressure_plate", () -> woodenPressurePlate(Blocks.BAMBOO_PRESSURE_PLATE, BlockSetType.BAMBOO));
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final DeferredBlock<ReinforcedButtonBlock> REINFORCED_BAMBOO_BUTTON = BLOCKS.register("reinforced_bamboo_button", () -> woodenButton(Blocks.BAMBOO_BUTTON, BlockSetType.BAMBOO));
	public static final DeferredBlock<SecretStandingSignBlock> SECRET_CHERRY_SIGN = BLOCKS.register("secret_cherry_sign_standing", () -> new SecretStandingSignBlock(prop(MapColor.TERRACOTTA_WHITE).noCollission().forceSolidOn(), WoodType.CHERRY));
	public static final DeferredBlock<SecretWallSignBlock> SECRET_CHERRY_WALL_SIGN = BLOCKS.register("secret_cherry_sign_wall", () -> new SecretWallSignBlock(prop(MapColor.TERRACOTTA_WHITE).noCollission().forceSolidOn(), WoodType.CHERRY));
	public static final DeferredBlock<SecretStandingSignBlock> SECRET_BAMBOO_SIGN = BLOCKS.register("secret_bamboo_sign_standing", () -> new SecretStandingSignBlock(prop(MapColor.COLOR_YELLOW).noCollission().forceSolidOn(), WoodType.BAMBOO));
	public static final DeferredBlock<SecretWallSignBlock> SECRET_BAMBOO_WALL_SIGN = BLOCKS.register("secret_bamboo_sign_wall", () -> new SecretWallSignBlock(prop(MapColor.COLOR_YELLOW).noCollission().forceSolidOn(), WoodType.BAMBOO));
	//hanging signs
	public static final DeferredBlock<SecretCeilingHangingSignBlock> SECRET_OAK_HANGING_SIGN = BLOCKS.register("secret_oak_hanging_sign", () -> new SecretCeilingHangingSignBlock(prop(MapColor.WOOD).noCollission().forceSolidOn(), WoodType.OAK));
	public static final DeferredBlock<SecretWallHangingSignBlock> SECRET_OAK_WALL_HANGING_SIGN = BLOCKS.register("secret_oak_wall_hanging_sign", () -> new SecretWallHangingSignBlock(prop(MapColor.WOOD).noCollission().forceSolidOn(), WoodType.OAK));
	public static final DeferredBlock<SecretCeilingHangingSignBlock> SECRET_SPRUCE_HANGING_SIGN = BLOCKS.register("secret_spruce_hanging_sign", () -> new SecretCeilingHangingSignBlock(prop(MapColor.PODZOL).noCollission().forceSolidOn(), WoodType.SPRUCE));
	public static final DeferredBlock<SecretWallHangingSignBlock> SECRET_SPRUCE_WALL_HANGING_SIGN = BLOCKS.register("secret_spruce_wall_hanging_sign", () -> new SecretWallHangingSignBlock(prop(MapColor.PODZOL).noCollission().forceSolidOn(), WoodType.SPRUCE));
	public static final DeferredBlock<SecretCeilingHangingSignBlock> SECRET_BIRCH_HANGING_SIGN = BLOCKS.register("secret_birch_hanging_sign", () -> new SecretCeilingHangingSignBlock(prop(MapColor.SAND).noCollission().forceSolidOn(), WoodType.BIRCH));
	public static final DeferredBlock<SecretWallHangingSignBlock> SECRET_BIRCH_WALL_HANGING_SIGN = BLOCKS.register("secret_birch_wall_hanging_sign", () -> new SecretWallHangingSignBlock(prop(MapColor.SAND).noCollission().forceSolidOn(), WoodType.BIRCH));
	public static final DeferredBlock<SecretCeilingHangingSignBlock> SECRET_JUNGLE_HANGING_SIGN = BLOCKS.register("secret_jungle_hanging_sign", () -> new SecretCeilingHangingSignBlock(prop(MapColor.DIRT).noCollission().forceSolidOn(), WoodType.JUNGLE));
	public static final DeferredBlock<SecretWallHangingSignBlock> SECRET_JUNGLE_WALL_HANGING_SIGN = BLOCKS.register("secret_jungle_wall_hanging_sign", () -> new SecretWallHangingSignBlock(prop(MapColor.DIRT).noCollission().forceSolidOn(), WoodType.JUNGLE));
	public static final DeferredBlock<SecretCeilingHangingSignBlock> SECRET_ACACIA_HANGING_SIGN = BLOCKS.register("secret_acacia_hanging_sign", () -> new SecretCeilingHangingSignBlock(prop(MapColor.COLOR_ORANGE).noCollission().forceSolidOn(), WoodType.ACACIA));
	public static final DeferredBlock<SecretWallHangingSignBlock> SECRET_ACACIA_WALL_HANGING_SIGN = BLOCKS.register("secret_acacia_wall_hanging_sign", () -> new SecretWallHangingSignBlock(prop(MapColor.COLOR_ORANGE).noCollission().forceSolidOn(), WoodType.ACACIA));
	public static final DeferredBlock<SecretCeilingHangingSignBlock> SECRET_DARK_OAK_HANGING_SIGN = BLOCKS.register("secret_dark_oak_hanging_sign", () -> new SecretCeilingHangingSignBlock(prop(MapColor.COLOR_BROWN).noCollission().forceSolidOn(), WoodType.DARK_OAK));
	public static final DeferredBlock<SecretWallHangingSignBlock> SECRET_DARK_OAK_WALL_HANGING_SIGN = BLOCKS.register("secret_dark_oak_wall_hanging_sign", () -> new SecretWallHangingSignBlock(prop(MapColor.COLOR_BROWN).noCollission().forceSolidOn(), WoodType.DARK_OAK));
	public static final DeferredBlock<SecretCeilingHangingSignBlock> SECRET_MANGROVE_HANGING_SIGN = BLOCKS.register("secret_mangrove_hanging_sign", () -> new SecretCeilingHangingSignBlock(prop(MapColor.COLOR_RED).noCollission().forceSolidOn(), WoodType.MANGROVE));
	public static final DeferredBlock<SecretWallHangingSignBlock> SECRET_MANGROVE_WALL_HANGING_SIGN = BLOCKS.register("secret_mangrove_wall_hanging_sign", () -> new SecretWallHangingSignBlock(prop(MapColor.COLOR_RED).noCollission().forceSolidOn(), WoodType.MANGROVE));
	public static final DeferredBlock<SecretCeilingHangingSignBlock> SECRET_CHERRY_HANGING_SIGN = BLOCKS.register("secret_cherry_hanging_sign", () -> new SecretCeilingHangingSignBlock(prop(MapColor.TERRACOTTA_PINK).noCollission().forceSolidOn(), WoodType.CHERRY));
	public static final DeferredBlock<SecretWallHangingSignBlock> SECRET_CHERRY_WALL_HANGING_SIGN = BLOCKS.register("secret_cherry_wall_hanging_sign", () -> new SecretWallHangingSignBlock(prop(MapColor.TERRACOTTA_PINK).noCollission().forceSolidOn(), WoodType.CHERRY));
	public static final DeferredBlock<SecretCeilingHangingSignBlock> SECRET_BAMBOO_HANGING_SIGN = BLOCKS.register("secret_bamboo_hanging_sign", () -> new SecretCeilingHangingSignBlock(prop(MapColor.COLOR_YELLOW).noCollission().forceSolidOn(), WoodType.BAMBOO));
	public static final DeferredBlock<SecretWallHangingSignBlock> SECRET_BAMBOO_WALL_HANGING_SIGN = BLOCKS.register("secret_bamboo_wall_hanging_sign", () -> new SecretWallHangingSignBlock(prop(MapColor.COLOR_YELLOW).noCollission().forceSolidOn(), WoodType.BAMBOO));
	public static final DeferredBlock<SecretCeilingHangingSignBlock> SECRET_CRIMSON_HANGING_SIGN = BLOCKS.register("secret_crimson_hanging_sign", () -> new SecretCeilingHangingSignBlock(prop(MapColor.CRIMSON_STEM).noCollission().forceSolidOn(), WoodType.CRIMSON));
	public static final DeferredBlock<SecretWallHangingSignBlock> SECRET_CRIMSON_WALL_HANGING_SIGN = BLOCKS.register("secret_crimson_wall_hanging_sign", () -> new SecretWallHangingSignBlock(prop(MapColor.CRIMSON_STEM).noCollission().forceSolidOn(), WoodType.CRIMSON));
	public static final DeferredBlock<SecretCeilingHangingSignBlock> SECRET_WARPED_HANGING_SIGN = BLOCKS.register("secret_warped_hanging_sign", () -> new SecretCeilingHangingSignBlock(prop(MapColor.WARPED_STEM).noCollission().forceSolidOn(), WoodType.WARPED));
	public static final DeferredBlock<SecretWallHangingSignBlock> SECRET_WARPED_WALL_HANGING_SIGN = BLOCKS.register("secret_warped_wall_hanging_sign", () -> new SecretWallHangingSignBlock(prop(MapColor.WARPED_STEM).noCollission().forceSolidOn(), WoodType.WARPED));
	//end hanging signs
	@HasManualPage
	@Reinforced
	public static final DeferredBlock<ReinforcedChiseledBookshelfBlock> REINFORCED_CHISELED_BOOKSHELF = BLOCKS.register("reinforced_chiseled_bookshelf", () -> new ReinforcedChiseledBookshelfBlock(prop(MapColor.WOOD).sound(SoundType.CHISELED_BOOKSHELF)));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_CHERRY_LOG = BLOCKS.register("reinforced_cherry_log", () -> new ReinforcedRotatedPillarBlock(logProp(MapColor.TERRACOTTA_WHITE, MapColor.TERRACOTTA_GRAY).sound(SoundType.CHERRY_WOOD), Blocks.CHERRY_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_CHERRY_WOOD = BLOCKS.register("reinforced_cherry_wood", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.TERRACOTTA_GRAY).sound(SoundType.CHERRY_WOOD), Blocks.CHERRY_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_CHERRY_LOG = BLOCKS.register("reinforced_stripped_cherry_log", () -> new ReinforcedRotatedPillarBlock(logProp(MapColor.TERRACOTTA_WHITE, MapColor.TERRACOTTA_PINK).sound(SoundType.CHERRY_WOOD), Blocks.STRIPPED_CHERRY_LOG));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_CHERRY_WOOD = BLOCKS.register("reinforced_stripped_cherry_wood", () -> new ReinforcedRotatedPillarBlock(prop(MapColor.TERRACOTTA_PINK).sound(SoundType.CHERRY_WOOD), Blocks.STRIPPED_CHERRY_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CHERRY_PLANKS = BLOCKS.register("reinforced_cherry_planks", () -> new BaseReinforcedBlock(prop(MapColor.TERRACOTTA_WHITE).sound(SoundType.CHERRY_WOOD), Blocks.CHERRY_PLANKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_CHERRY_STAIRS = BLOCKS.register("reinforced_cherry_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.TERRACOTTA_WHITE).sound(SoundType.CHERRY_WOOD), Blocks.CHERRY_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_CHERRY_SLAB = BLOCKS.register("reinforced_cherry_slab", () -> new ReinforcedSlabBlock(prop(MapColor.TERRACOTTA_WHITE).sound(SoundType.CHERRY_WOOD), Blocks.CHERRY_SLAB));
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedPressurePlateBlock> REINFORCED_CHERRY_PRESSURE_PLATE = BLOCKS.register("reinforced_cherry_pressure_plate", () -> woodenPressurePlate(Blocks.CHERRY_PRESSURE_PLATE, BlockSetType.CHERRY));
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final DeferredBlock<ReinforcedButtonBlock> REINFORCED_CHERRY_BUTTON = BLOCKS.register("reinforced_cherry_button", () -> woodenButton(Blocks.CHERRY_BUTTON, BlockSetType.CHERRY));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceBlock> REINFORCED_OAK_FENCE = BLOCKS.register("reinforced_oak_fence", () -> new ReinforcedFenceBlock(prop(Blocks.OAK_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).sound(SoundType.WOOD), Blocks.OAK_FENCE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceBlock> REINFORCED_SPRUCE_FENCE = BLOCKS.register("reinforced_spruce_fence", () -> new ReinforcedFenceBlock(prop(Blocks.SPRUCE_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).sound(SoundType.WOOD), Blocks.SPRUCE_FENCE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceBlock> REINFORCED_BIRCH_FENCE = BLOCKS.register("reinforced_birch_fence", () -> new ReinforcedFenceBlock(prop(Blocks.BIRCH_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).sound(SoundType.WOOD), Blocks.BIRCH_FENCE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceBlock> REINFORCED_JUNGLE_FENCE = BLOCKS.register("reinforced_jungle_fence", () -> new ReinforcedFenceBlock(prop(Blocks.JUNGLE_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).sound(SoundType.WOOD), Blocks.JUNGLE_FENCE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceBlock> REINFORCED_ACACIA_FENCE = BLOCKS.register("reinforced_acacia_fence", () -> new ReinforcedFenceBlock(prop(Blocks.ACACIA_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).sound(SoundType.WOOD), Blocks.ACACIA_FENCE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceBlock> REINFORCED_DARK_OAK_FENCE = BLOCKS.register("reinforced_dark_oak_fence", () -> new ReinforcedFenceBlock(prop(Blocks.DARK_OAK_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).sound(SoundType.WOOD), Blocks.DARK_OAK_FENCE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceBlock> REINFORCED_MANGROVE_FENCE = BLOCKS.register("reinforced_mangrove_fence", () -> new ReinforcedFenceBlock(prop(Blocks.MANGROVE_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).sound(SoundType.WOOD), Blocks.MANGROVE_FENCE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceBlock> REINFORCED_CHERRY_FENCE = BLOCKS.register("reinforced_cherry_fence", () -> new ReinforcedFenceBlock(prop(Blocks.CHERRY_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).sound(SoundType.CHERRY_WOOD), Blocks.CHERRY_FENCE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceBlock> REINFORCED_BAMBOO_FENCE = BLOCKS.register("reinforced_bamboo_fence", () -> new ReinforcedFenceBlock(prop(Blocks.BAMBOO_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).sound(SoundType.BAMBOO_WOOD), Blocks.BAMBOO_FENCE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceBlock> REINFORCED_CRIMSON_FENCE = BLOCKS.register("reinforced_crimson_fence", () -> new ReinforcedFenceBlock(prop(Blocks.CRIMSON_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).sound(SoundType.NETHER_WOOD), Blocks.CRIMSON_FENCE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceBlock> REINFORCED_WARPED_FENCE = BLOCKS.register("reinforced_warped_fence", () -> new ReinforcedFenceBlock(prop(Blocks.WARPED_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).sound(SoundType.NETHER_WOOD), Blocks.WARPED_FENCE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceBlock> REINFORCED_NETHER_BRICK_FENCE = BLOCKS.register("reinforced_nether_brick_fence", () -> new ReinforcedFenceBlock(prop(MapColor.NETHER).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHER_BRICKS), Blocks.NETHER_BRICK_FENCE));
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceGateBlock> REINFORCED_OAK_FENCE_GATE = BLOCKS.register("reinforced_oak_fence_gate", () -> new ReinforcedFenceGateBlock(prop(Blocks.OAK_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).forceSolidOn(), WoodType.OAK, Blocks.OAK_FENCE_GATE));
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceGateBlock> REINFORCED_SPRUCE_FENCE_GATE = BLOCKS.register("reinforced_spruce_fence_gate", () -> new ReinforcedFenceGateBlock(prop(Blocks.SPRUCE_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).forceSolidOn(), WoodType.SPRUCE, Blocks.SPRUCE_FENCE_GATE));
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceGateBlock> REINFORCED_BIRCH_FENCE_GATE = BLOCKS.register("reinforced_birch_fence_gate", () -> new ReinforcedFenceGateBlock(prop(Blocks.BIRCH_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).forceSolidOn(), WoodType.BIRCH, Blocks.BIRCH_FENCE_GATE));
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceGateBlock> REINFORCED_JUNGLE_FENCE_GATE = BLOCKS.register("reinforced_jungle_fence_gate", () -> new ReinforcedFenceGateBlock(prop(Blocks.JUNGLE_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).forceSolidOn(), WoodType.JUNGLE, Blocks.JUNGLE_FENCE_GATE));
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceGateBlock> REINFORCED_ACACIA_FENCE_GATE = BLOCKS.register("reinforced_acacia_fence_gate", () -> new ReinforcedFenceGateBlock(prop(Blocks.ACACIA_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).forceSolidOn(), WoodType.ACACIA, Blocks.ACACIA_FENCE_GATE));
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceGateBlock> REINFORCED_DARK_OAK_FENCE_GATE = BLOCKS.register("reinforced_dark_oak_fence_gate", () -> new ReinforcedFenceGateBlock(prop(Blocks.DARK_OAK_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).forceSolidOn(), WoodType.DARK_OAK, Blocks.DARK_OAK_FENCE_GATE));
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceGateBlock> REINFORCED_MANGROVE_FENCE_GATE = BLOCKS.register("reinforced_mangrove_fence_gate", () -> new ReinforcedFenceGateBlock(prop(Blocks.MANGROVE_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).forceSolidOn(), WoodType.MANGROVE, Blocks.MANGROVE_FENCE_GATE));
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceGateBlock> REINFORCED_CHERRY_FENCE_GATE = BLOCKS.register("reinforced_cherry_fence_gate", () -> new ReinforcedFenceGateBlock(prop(Blocks.CHERRY_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).forceSolidOn(), WoodType.CHERRY, Blocks.CHERRY_FENCE_GATE));
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceGateBlock> REINFORCED_BAMBOO_FENCE_GATE = BLOCKS.register("reinforced_bamboo_fence_gate", () -> new ReinforcedFenceGateBlock(prop(Blocks.BAMBOO_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).forceSolidOn(), WoodType.BAMBOO, Blocks.BAMBOO_FENCE_GATE));
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceGateBlock> REINFORCED_CRIMSON_FENCE_GATE = BLOCKS.register("reinforced_crimson_fence_gate", () -> new ReinforcedFenceGateBlock(prop(Blocks.CRIMSON_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).forceSolidOn(), WoodType.CRIMSON, Blocks.CRIMSON_FENCE_GATE));
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceGateBlock> REINFORCED_WARPED_FENCE_GATE = BLOCKS.register("reinforced_warped_fence_gate", () -> new ReinforcedFenceGateBlock(prop(Blocks.WARPED_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).forceSolidOn(), WoodType.WARPED, Blocks.WARPED_FENCE_GATE));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CHISELED_COPPER = BLOCKS.register("reinforced_chiseled_copper", () -> new BaseReinforcedBlock(BlockBehaviour.Properties.ofFullCopy(REINFORCED_COPPER_BLOCK.get()), Blocks.CHISELED_COPPER));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_EXPOSED_CHISELED_COPPER = BLOCKS.register("reinforced_exposed_chiseled_copper", () -> new BaseReinforcedBlock(BlockBehaviour.Properties.ofFullCopy(REINFORCED_EXPOSED_COPPER.get()), Blocks.EXPOSED_CHISELED_COPPER));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_WEATHERED_CHISELED_COPPER = BLOCKS.register("reinforced_weathered_chiseled_copper", () -> new BaseReinforcedBlock(BlockBehaviour.Properties.ofFullCopy(REINFORCED_WEATHERED_COPPER.get()), Blocks.WEATHERED_CHISELED_COPPER));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_OXIDIZED_CHISELED_COPPER = BLOCKS.register("reinforced_oxidized_chiseled_copper", () -> new BaseReinforcedBlock(BlockBehaviour.Properties.ofFullCopy(REINFORCED_OXIDIZED_COPPER.get()), Blocks.OXIDIZED_CHISELED_COPPER));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final DeferredBlock<ReinforcedCopperGrateBlock> REINFORCED_COPPER_GRATE = BLOCKS.register("reinforced_copper_grate", () -> new ReinforcedCopperGrateBlock(Blocks.COPPER_GRATE));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final DeferredBlock<ReinforcedCopperGrateBlock> REINFORCED_EXPOSED_COPPER_GRATE = BLOCKS.register("reinforced_exposed_copper_grate", () -> new ReinforcedCopperGrateBlock(Blocks.EXPOSED_COPPER_GRATE));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final DeferredBlock<ReinforcedCopperGrateBlock> REINFORCED_WEATHERED_COPPER_GRATE = BLOCKS.register("reinforced_weathered_copper_grate", () -> new ReinforcedCopperGrateBlock(Blocks.WEATHERED_COPPER_GRATE));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final DeferredBlock<ReinforcedCopperGrateBlock> REINFORCED_OXIDIZED_COPPER_GRATE = BLOCKS.register("reinforced_oxidized_copper_grate", () -> new ReinforcedCopperGrateBlock(Blocks.OXIDIZED_COPPER_GRATE));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final DeferredBlock<ReinforcedCopperBulbBlock> REINFORCED_COPPER_BULB = BLOCKS.register("reinforced_copper_bulb", () -> new ReinforcedCopperBulbBlock(Blocks.COPPER_BULB));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final DeferredBlock<ReinforcedCopperBulbBlock> REINFORCED_EXPOSED_COPPER_BULB = BLOCKS.register("reinforced_exposed_copper_bulb", () -> new ReinforcedCopperBulbBlock(Blocks.EXPOSED_COPPER_BULB));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final DeferredBlock<ReinforcedCopperBulbBlock> REINFORCED_WEATHERED_COPPER_BULB = BLOCKS.register("reinforced_weathered_copper_bulb", () -> new ReinforcedCopperBulbBlock(Blocks.WEATHERED_COPPER_BULB));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final DeferredBlock<ReinforcedCopperBulbBlock> REINFORCED_OXIDIZED_COPPER_BULB = BLOCKS.register("reinforced_oxidized_copper_bulb", () -> new ReinforcedCopperBulbBlock(Blocks.OXIDIZED_COPPER_BULB));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CHISELED_TUFF = BLOCKS.register("reinforced_chiseled_tuff", () -> new BaseReinforcedBlock(Blocks.CHISELED_TUFF));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_TUFF_STAIRS = BLOCKS.register("reinforced_tuff_stairs", () -> new ReinforcedStairsBlock(Blocks.TUFF_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_TUFF_SLAB = BLOCKS.register("reinforced_tuff_slab", () -> new ReinforcedSlabBlock(Blocks.TUFF_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_TUFF_WALL = BLOCKS.register("reinforced_tuff_wall", () -> new ReinforcedWallBlock(Blocks.TUFF_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_POLISHED_TUFF = BLOCKS.register("reinforced_polished_tuff", () -> new BaseReinforcedBlock(Blocks.POLISHED_TUFF));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_POLISHED_TUFF_STAIRS = BLOCKS.register("reinforced_polished_tuff_stairs", () -> new ReinforcedStairsBlock(Blocks.POLISHED_TUFF_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_POLISHED_TUFF_SLAB = BLOCKS.register("reinforced_polished_tuff_slab", () -> new ReinforcedSlabBlock(Blocks.POLISHED_TUFF_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_POLISHED_TUFF_WALL = BLOCKS.register("reinforced_polished_tuff_wall", () -> new ReinforcedWallBlock(Blocks.POLISHED_TUFF_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_TUFF_BRICKS = BLOCKS.register("reinforced_tuff_bricks", () -> new BaseReinforcedBlock(Blocks.TUFF_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_TUFF_BRICK_STAIRS = BLOCKS.register("reinforced_tuff_brick_stairs", () -> new ReinforcedStairsBlock(Blocks.TUFF_BRICK_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_TUFF_BRICK_SLAB = BLOCKS.register("reinforced_tuff_brick_slab", () -> new ReinforcedSlabBlock(Blocks.TUFF_BRICK_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_TUFF_BRICK_WALL = BLOCKS.register("reinforced_tuff_brick_wall", () -> new ReinforcedWallBlock(Blocks.TUFF_BRICK_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CHISELED_TUFF_BRICKS = BLOCKS.register("reinforced_chiseled_tuff_bricks", () -> new BaseReinforcedBlock(Blocks.CHISELED_TUFF_BRICKS));

	//misc
	@RegisterItemBlock
	public static final DeferredBlock<SlabBlock> CRYSTAL_QUARTZ_SLAB = BLOCKS.register("crystal_quartz_slab", () -> new SlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).strength(2.0F, 6.0F).requiresCorrectToolForDrops()));
	@RegisterItemBlock
	public static final DeferredBlock<Block> SMOOTH_CRYSTAL_QUARTZ = BLOCKS.register("smooth_crystal_quartz", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).strength(2.0F, 6.0F).requiresCorrectToolForDrops()));
	@RegisterItemBlock
	public static final DeferredBlock<Block> CHISELED_CRYSTAL_QUARTZ = BLOCKS.register("chiseled_crystal_quartz", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).strength(0.8F).requiresCorrectToolForDrops()));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<Block> CRYSTAL_QUARTZ_BLOCK = BLOCKS.register("crystal_quartz", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).strength(0.8F).requiresCorrectToolForDrops()));
	@RegisterItemBlock
	public static final DeferredBlock<Block> CRYSTAL_QUARTZ_BRICKS = BLOCKS.register("crystal_quartz_bricks", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).strength(0.8F).requiresCorrectToolForDrops()));
	@RegisterItemBlock
	public static final DeferredBlock<RotatedPillarBlock> CRYSTAL_QUARTZ_PILLAR = BLOCKS.register("crystal_quartz_pillar", () -> new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).strength(0.8F).requiresCorrectToolForDrops()));
	@RegisterItemBlock
	public static final DeferredBlock<StairBlock> CRYSTAL_QUARTZ_STAIRS = BLOCKS.register("crystal_quartz_stairs", () -> new StairBlock(() -> CRYSTAL_QUARTZ_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.ofLegacyCopy(CRYSTAL_QUARTZ_BLOCK.get())));
	@RegisterItemBlock
	public static final DeferredBlock<StairBlock> SMOOTH_CRYSTAL_QUARTZ_STAIRS = BLOCKS.register("smooth_crystal_quartz_stairs", () -> new StairBlock(() -> SMOOTH_CRYSTAL_QUARTZ.get().defaultBlockState(), BlockBehaviour.Properties.ofLegacyCopy(SMOOTH_CRYSTAL_QUARTZ.get())));
	@RegisterItemBlock
	public static final DeferredBlock<SlabBlock> SMOOTH_CRYSTAL_QUARTZ_SLAB = BLOCKS.register("smooth_crystal_quartz_slab", () -> new SlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).strength(2.0F, 6.0F).requiresCorrectToolForDrops()));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced(customTint = 0x15B3A2, itemGroup = SCItemGroup.MANUAL)
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_CRYSTAL_QUARTZ_SLAB = BLOCKS.register("reinforced_crystal_quartz_slab", () -> new ReinforcedSlabBlock(prop(MapColor.COLOR_CYAN), SCContent.CRYSTAL_QUARTZ_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced(customTint = 0x15B3A2, itemGroup = SCItemGroup.MANUAL)
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_SMOOTH_CRYSTAL_QUARTZ = BLOCKS.register("reinforced_smooth_crystal_quartz", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_CYAN), SCContent.SMOOTH_CRYSTAL_QUARTZ));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced(customTint = 0x15B3A2, itemGroup = SCItemGroup.MANUAL)
	public static final DeferredBlock<BlockPocketBlock> REINFORCED_CHISELED_CRYSTAL_QUARTZ = BLOCKS.register("reinforced_chiseled_crystal_quartz_block", () -> new BlockPocketBlock(prop(MapColor.COLOR_CYAN), SCContent.CHISELED_CRYSTAL_QUARTZ));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced(customTint = 0x15B3A2, itemGroup = SCItemGroup.MANUAL)
	public static final DeferredBlock<BlockPocketBlock> REINFORCED_CRYSTAL_QUARTZ_BLOCK = BLOCKS.register("reinforced_crystal_quartz_block", () -> new BlockPocketBlock(prop(MapColor.COLOR_CYAN), SCContent.CRYSTAL_QUARTZ_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced(customTint = 0x15B3A2, itemGroup = SCItemGroup.MANUAL)
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CRYSTAL_QUARTZ_BRICKS = BLOCKS.register("reinforced_crystal_quartz_bricks", () -> new BaseReinforcedBlock(prop(MapColor.COLOR_CYAN), SCContent.CRYSTAL_QUARTZ_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced(customTint = 0x15B3A2, itemGroup = SCItemGroup.MANUAL)
	public static final DeferredBlock<ReinforcedRotatedCrystalQuartzPillar> REINFORCED_CRYSTAL_QUARTZ_PILLAR = BLOCKS.register("reinforced_crystal_quartz_pillar", () -> new ReinforcedRotatedCrystalQuartzPillar(prop(MapColor.COLOR_CYAN), SCContent.CRYSTAL_QUARTZ_PILLAR));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced(customTint = 0x15B3A2, itemGroup = SCItemGroup.MANUAL)
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_CRYSTAL_QUARTZ_STAIRS = BLOCKS.register("reinforced_crystal_quartz_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.COLOR_CYAN), SCContent.CRYSTAL_QUARTZ_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced(customTint = 0x15B3A2, itemGroup = SCItemGroup.MANUAL)
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_SMOOTH_CRYSTAL_QUARTZ_STAIRS = BLOCKS.register("reinforced_smooth_crystal_quartz_stairs", () -> new ReinforcedStairsBlock(prop(MapColor.COLOR_CYAN), SCContent.SMOOTH_CRYSTAL_QUARTZ_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced(customTint = 0x15B3A2, itemGroup = SCItemGroup.MANUAL)
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_SMOOTH_CRYSTAL_QUARTZ_SLAB = BLOCKS.register("reinforced_smooth_crystal_quartz_slab", () -> new ReinforcedSlabBlock(prop(MapColor.COLOR_CYAN), SCContent.SMOOTH_CRYSTAL_QUARTZ_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	public static final DeferredBlock<HorizontalReinforcedIronBars> HORIZONTAL_REINFORCED_IRON_BARS = BLOCKS.register("horizontal_reinforced_iron_bars", () -> new HorizontalReinforcedIronBars(prop(MapColor.METAL).sound(SoundType.METAL).noLootTable(), Blocks.IRON_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedDirtPathBlock> REINFORCED_DIRT_PATH = BLOCKS.register("reinforced_grass_path", () -> new ReinforcedDirtPathBlock(prop(MapColor.DIRT).sound(SoundType.GRASS), Blocks.DIRT_PATH));
	public static final DeferredBlock<ReinforcedMovingPistonBlock> REINFORCED_MOVING_PISTON = BLOCKS.register("reinforced_moving_piston", () -> new ReinforcedMovingPistonBlock(prop().dynamicShape().noLootTable().noOcclusion().isRedstoneConductor(SCContent::never).isSuffocating(SCContent::never).isViewBlocking(SCContent::never)));
	@Reinforced(registerBlockItem = false)
	public static final DeferredBlock<ReinforcedPistonHeadBlock> REINFORCED_PISTON_HEAD = BLOCKS.register("reinforced_piston_head", () -> new ReinforcedPistonHeadBlock(prop().noLootTable().pushReaction(PushReaction.BLOCK)));
	public static final DeferredBlock<SometimesVisibleBlock> SENTRY_DISGUISE = BLOCKS.register("sentry_disguise", () -> new SometimesVisibleBlock(propDisguisable().noLootTable().pushReaction(PushReaction.BLOCK)));

	//items
	@HasManualPage(hasRecipeDescription = true)
	public static final DeferredItem<AdminToolItem> ADMIN_TOOL = ITEMS.register("admin_tool", () -> new AdminToolItem(itemProp(1)));
	public static final DeferredItem<BlockItem> ANCIENT_DEBRIS_MINE_ITEM = ITEMS.registerSimpleBlockItem(SCContent.ANCIENT_DEBRIS_MINE, itemProp().fireResistant());
	@HasManualPage
	public static final DeferredItem<BriefcaseItem> BRIEFCASE = ITEMS.register("briefcase", () -> new BriefcaseItem(itemProp(1)));
	@HasManualPage
	public static final DeferredItem<CameraMonitorItem> CAMERA_MONITOR = ITEMS.register("camera_monitor", () -> new CameraMonitorItem(itemProp(1)));
	@HasManualPage
	public static final DeferredItem<CodebreakerItem> CODEBREAKER = ITEMS.register("codebreaker", () -> new CodebreakerItem(itemProp().defaultDurability(5)));
	@HasManualPage
	public static final DeferredItem<Item> CRYSTAL_QUARTZ_ITEM = ITEMS.registerSimpleItem("crystal_quartz_item");
	public static final DeferredItem<DisplayCaseItem> DISPLAY_CASE_ITEM = ITEMS.register(DISPLAY_CASE_PATH, () -> new DisplayCaseItem(SCContent.DISPLAY_CASE.get(), itemProp(), false));
	@HasManualPage(hasRecipeDescription = true)
	public static final DeferredItem<FakeLiquidBucketItem> FAKE_LAVA_BUCKET = ITEMS.register("bucket_f_lava", () -> new FakeLiquidBucketItem(SCContent.FAKE_LAVA, itemProp(1)));
	@HasManualPage(hasRecipeDescription = true)
	public static final DeferredItem<FakeLiquidBucketItem> FAKE_WATER_BUCKET = ITEMS.register("bucket_f_water", () -> new FakeLiquidBucketItem(SCContent.FAKE_WATER, itemProp(1)));
	public static final DeferredItem<DisplayCaseItem> GLOW_DISPLAY_CASE_ITEM = ITEMS.register(GLOW_DISPLAY_CASE_PATH, () -> new DisplayCaseItem(SCContent.GLOW_DISPLAY_CASE.get(), itemProp(), true));
	@HasManualPage
	public static final DeferredItem<KeycardHolderItem> KEYCARD_HOLDER = ITEMS.register("keycard_holder", () -> new KeycardHolderItem(itemProp(1)));
	@HasManualPage(PageGroup.KEYCARDS)
	public static final DeferredItem<KeycardItem> KEYCARD_LVL_1 = ITEMS.register("keycard_lv1", () -> new KeycardItem(itemProp(), 0));
	@HasManualPage(PageGroup.KEYCARDS)
	public static final DeferredItem<KeycardItem> KEYCARD_LVL_2 = ITEMS.register("keycard_lv2", () -> new KeycardItem(itemProp(), 1));
	@HasManualPage(PageGroup.KEYCARDS)
	public static final DeferredItem<KeycardItem> KEYCARD_LVL_3 = ITEMS.register("keycard_lv3", () -> new KeycardItem(itemProp(), 2));
	@HasManualPage(PageGroup.KEYCARDS)
	public static final DeferredItem<KeycardItem> KEYCARD_LVL_4 = ITEMS.register("keycard_lv4", () -> new KeycardItem(itemProp(), 3));
	@HasManualPage(PageGroup.KEYCARDS)
	public static final DeferredItem<KeycardItem> KEYCARD_LVL_5 = ITEMS.register("keycard_lv5", () -> new KeycardItem(itemProp(), 4));
	@HasManualPage
	public static final DeferredItem<KeyPanelItem> KEY_PANEL = ITEMS.register("keypad_item", () -> new KeyPanelItem(itemProp()));
	public static final DeferredItem<KeypadChestItem> KEYPAD_CHEST_ITEM = ITEMS.register(KEYPAD_CHEST_PATH, () -> new KeypadChestItem(SCContent.KEYPAD_CHEST.get(), itemProp()));
	@HasManualPage
	public static final DeferredItem<DoubleHighBlockItem> KEYPAD_DOOR_ITEM = ITEMS.register("keypad_door_item", () -> new DoubleHighBlockItem(KEYPAD_DOOR.get(), itemProp()));
	@HasManualPage
	public static final DeferredItem<LensItem> LENS = ITEMS.register("lens", () -> new LensItem(itemProp()));
	@HasManualPage
	public static final DeferredItem<KeycardItem> LIMITED_USE_KEYCARD = ITEMS.register("limited_use_keycard", () -> new KeycardItem(itemProp(), -1));
	@HasManualPage
	public static final DeferredItem<PortableTunePlayerItem> PORTABLE_TUNE_PLAYER = ITEMS.register("portable_tune_player", () -> new PortableTunePlayerItem(itemProp()));
	@HasManualPage
	public static final DeferredItem<DoubleHighBlockItem> REINFORCED_DOOR_ITEM = ITEMS.register("door_indestructible_iron_item", () -> new DoubleHighBlockItem(REINFORCED_DOOR.get(), itemProp()));
	@HasManualPage
	public static final DeferredItem<MineRemoteAccessToolItem> MINE_REMOTE_ACCESS_TOOL = ITEMS.register("remote_access_mine", () -> new MineRemoteAccessToolItem(itemProp(1)));
	@HasManualPage
	public static final DeferredItem<SentryRemoteAccessToolItem> SENTRY_REMOTE_ACCESS_TOOL = ITEMS.register("remote_access_sentry", () -> new SentryRemoteAccessToolItem(itemProp(1)));
	@HasManualPage
	public static final DeferredItem<DoubleHighBlockItem> RIFT_STABILIZER_ITEM = ITEMS.register("rift_stabilizer", () -> new DoubleHighBlockItem(RIFT_STABILIZER.get(), itemProp()));
	@HasManualPage
	public static final DeferredItem<DoubleHighBlockItem> SCANNER_DOOR_ITEM = ITEMS.register("scanner_door_item", () -> new DoubleHighBlockItem(SCANNER_DOOR.get(), itemProp()));
	@HasManualPage
	public static final DeferredItem<SCManualItem> SC_MANUAL = ITEMS.register("sc_manual", () -> new SCManualItem(itemProp(1)));
	@HasManualPage(PageGroup.SECRET_SIGNS)
	public static final DeferredItem<SignItem> SECRET_OAK_SIGN_ITEM = ITEMS.register("secret_sign_item", () -> new SignItem(itemProp(16), SCContent.SECRET_OAK_SIGN.get(), SCContent.SECRET_OAK_WALL_SIGN.get()));
	@HasManualPage(PageGroup.SECRET_SIGNS)
	public static final DeferredItem<SignItem> SECRET_SPRUCE_SIGN_ITEM = ITEMS.register("secret_spruce_sign_item", () -> new SignItem(itemProp(16), SCContent.SECRET_SPRUCE_SIGN.get(), SCContent.SECRET_SPRUCE_WALL_SIGN.get()));
	@HasManualPage(PageGroup.SECRET_SIGNS)
	public static final DeferredItem<SignItem> SECRET_BIRCH_SIGN_ITEM = ITEMS.register("secret_birch_sign_item", () -> new SignItem(itemProp(16), SCContent.SECRET_BIRCH_SIGN.get(), SCContent.SECRET_BIRCH_WALL_SIGN.get()));
	@HasManualPage(PageGroup.SECRET_SIGNS)
	public static final DeferredItem<SignItem> SECRET_JUNGLE_SIGN_ITEM = ITEMS.register("secret_jungle_sign_item", () -> new SignItem(itemProp(16), SCContent.SECRET_JUNGLE_SIGN.get(), SCContent.SECRET_JUNGLE_WALL_SIGN.get()));
	@HasManualPage(PageGroup.SECRET_SIGNS)
	public static final DeferredItem<SignItem> SECRET_ACACIA_SIGN_ITEM = ITEMS.register("secret_acacia_sign_item", () -> new SignItem(itemProp(16), SCContent.SECRET_ACACIA_SIGN.get(), SCContent.SECRET_ACACIA_WALL_SIGN.get()));
	@HasManualPage(PageGroup.SECRET_SIGNS)
	public static final DeferredItem<SignItem> SECRET_DARK_OAK_SIGN_ITEM = ITEMS.register("secret_dark_oak_sign_item", () -> new SignItem(itemProp(16), SCContent.SECRET_DARK_OAK_SIGN.get(), SCContent.SECRET_DARK_OAK_WALL_SIGN.get()));
	@HasManualPage(PageGroup.SECRET_SIGNS)
	public static final DeferredItem<SignItem> SECRET_MANGROVE_SIGN_ITEM = ITEMS.register("secret_mangrove_sign_item", () -> new SignItem(itemProp(16), SCContent.SECRET_MANGROVE_SIGN.get(), SCContent.SECRET_MANGROVE_WALL_SIGN.get()));
	@HasManualPage(PageGroup.SECRET_SIGNS)
	public static final DeferredItem<SignItem> SECRET_CHERRY_SIGN_ITEM = ITEMS.register("secret_cherry_sign_item", () -> new SignItem(itemProp(16), SCContent.SECRET_CHERRY_SIGN.get(), SCContent.SECRET_CHERRY_WALL_SIGN.get()));
	@HasManualPage(PageGroup.SECRET_SIGNS)
	public static final DeferredItem<SignItem> SECRET_BAMBOO_SIGN_ITEM = ITEMS.register("secret_bamboo_sign_item", () -> new SignItem(itemProp(16), SCContent.SECRET_BAMBOO_SIGN.get(), SCContent.SECRET_BAMBOO_WALL_SIGN.get()));
	@HasManualPage(PageGroup.SECRET_SIGNS)
	public static final DeferredItem<SignItem> SECRET_CRIMSON_SIGN_ITEM = ITEMS.register("secret_crimson_sign_item", () -> new SignItem(itemProp(16), SCContent.SECRET_CRIMSON_SIGN.get(), SCContent.SECRET_CRIMSON_WALL_SIGN.get()));
	@HasManualPage(PageGroup.SECRET_SIGNS)
	public static final DeferredItem<SignItem> SECRET_WARPED_SIGN_ITEM = ITEMS.register("secret_warped_sign_item", () -> new SignItem(itemProp(16), SCContent.SECRET_WARPED_SIGN.get(), SCContent.SECRET_WARPED_WALL_SIGN.get()));
	@HasManualPage(PageGroup.SECRET_HANGING_SIGNS)
	public static final DeferredItem<HangingSignItem> SECRET_OAK_HANGING_SIGN_ITEM = ITEMS.register("secret_oak_hanging_sign", () -> new HangingSignItem(SCContent.SECRET_OAK_HANGING_SIGN.get(), SCContent.SECRET_OAK_WALL_HANGING_SIGN.get(), itemProp(16)));
	@HasManualPage(PageGroup.SECRET_HANGING_SIGNS)
	public static final DeferredItem<HangingSignItem> SECRET_SPRUCE_HANGING_SIGN_ITEM = ITEMS.register("secret_spruce_hanging_sign", () -> new HangingSignItem(SCContent.SECRET_SPRUCE_HANGING_SIGN.get(), SCContent.SECRET_SPRUCE_WALL_HANGING_SIGN.get(), itemProp(16)));
	@HasManualPage(PageGroup.SECRET_HANGING_SIGNS)
	public static final DeferredItem<HangingSignItem> SECRET_BIRCH_HANGING_SIGN_ITEM = ITEMS.register("secret_birch_hanging_sign", () -> new HangingSignItem(SCContent.SECRET_BIRCH_HANGING_SIGN.get(), SCContent.SECRET_BIRCH_WALL_HANGING_SIGN.get(), itemProp(16)));
	@HasManualPage(PageGroup.SECRET_HANGING_SIGNS)
	public static final DeferredItem<HangingSignItem> SECRET_JUNGLE_HANGING_SIGN_ITEM = ITEMS.register("secret_jungle_hanging_sign", () -> new HangingSignItem(SCContent.SECRET_JUNGLE_HANGING_SIGN.get(), SCContent.SECRET_JUNGLE_WALL_HANGING_SIGN.get(), itemProp(16)));
	@HasManualPage(PageGroup.SECRET_HANGING_SIGNS)
	public static final DeferredItem<HangingSignItem> SECRET_ACACIA_HANGING_SIGN_ITEM = ITEMS.register("secret_acacia_hanging_sign", () -> new HangingSignItem(SCContent.SECRET_ACACIA_HANGING_SIGN.get(), SCContent.SECRET_ACACIA_WALL_HANGING_SIGN.get(), itemProp(16)));
	@HasManualPage(PageGroup.SECRET_HANGING_SIGNS)
	public static final DeferredItem<HangingSignItem> SECRET_DARK_OAK_HANGING_SIGN_ITEM = ITEMS.register("secret_dark_oak_hanging_sign", () -> new HangingSignItem(SCContent.SECRET_DARK_OAK_HANGING_SIGN.get(), SCContent.SECRET_DARK_OAK_WALL_HANGING_SIGN.get(), itemProp(16)));
	@HasManualPage(PageGroup.SECRET_HANGING_SIGNS)
	public static final DeferredItem<HangingSignItem> SECRET_MANGROVE_HANGING_SIGN_ITEM = ITEMS.register("secret_mangrove_hanging_sign", () -> new HangingSignItem(SCContent.SECRET_MANGROVE_HANGING_SIGN.get(), SCContent.SECRET_MANGROVE_WALL_HANGING_SIGN.get(), itemProp(16)));
	@HasManualPage(PageGroup.SECRET_HANGING_SIGNS)
	public static final DeferredItem<HangingSignItem> SECRET_CHERRY_HANGING_SIGN_ITEM = ITEMS.register("secret_cherry_hanging_sign", () -> new HangingSignItem(SCContent.SECRET_CHERRY_HANGING_SIGN.get(), SCContent.SECRET_CHERRY_WALL_HANGING_SIGN.get(), itemProp(16)));
	@HasManualPage(PageGroup.SECRET_HANGING_SIGNS)
	public static final DeferredItem<HangingSignItem> SECRET_BAMBOO_HANGING_SIGN_ITEM = ITEMS.register("secret_bamboo_hanging_sign", () -> new HangingSignItem(SCContent.SECRET_BAMBOO_HANGING_SIGN.get(), SCContent.SECRET_BAMBOO_WALL_HANGING_SIGN.get(), itemProp(16)));
	@HasManualPage(PageGroup.SECRET_HANGING_SIGNS)
	public static final DeferredItem<HangingSignItem> SECRET_CRIMSON_HANGING_SIGN_ITEM = ITEMS.register("secret_crimson_hanging_sign", () -> new HangingSignItem(SCContent.SECRET_CRIMSON_HANGING_SIGN.get(), SCContent.SECRET_CRIMSON_WALL_HANGING_SIGN.get(), itemProp(16)));
	@HasManualPage(PageGroup.SECRET_HANGING_SIGNS)
	public static final DeferredItem<HangingSignItem> SECRET_WARPED_HANGING_SIGN_ITEM = ITEMS.register("secret_warped_hanging_sign", () -> new HangingSignItem(SCContent.SECRET_WARPED_HANGING_SIGN.get(), SCContent.SECRET_WARPED_WALL_HANGING_SIGN.get(), itemProp(16)));
	@HasManualPage(PageGroup.SECURITY_SEA_BOATS)
	public static final DeferredItem<SecuritySeaBoatItem> OAK_SECURITY_SEA_BOAT = ITEMS.register("oak_security_sea_boat", () -> new SecuritySeaBoatItem(Boat.Type.OAK, itemProp(1).fireResistant()));
	@HasManualPage(PageGroup.SECURITY_SEA_BOATS)
	public static final DeferredItem<SecuritySeaBoatItem> SPRUCE_SECURITY_SEA_BOAT = ITEMS.register("spruce_security_sea_boat", () -> new SecuritySeaBoatItem(Boat.Type.SPRUCE, itemProp(1).fireResistant()));
	@HasManualPage(PageGroup.SECURITY_SEA_BOATS)
	public static final DeferredItem<SecuritySeaBoatItem> BIRCH_SECURITY_SEA_BOAT = ITEMS.register("birch_security_sea_boat", () -> new SecuritySeaBoatItem(Boat.Type.BIRCH, itemProp(1).fireResistant()));
	@HasManualPage(PageGroup.SECURITY_SEA_BOATS)
	public static final DeferredItem<SecuritySeaBoatItem> JUNGLE_SECURITY_SEA_BOAT = ITEMS.register("jungle_security_sea_boat", () -> new SecuritySeaBoatItem(Boat.Type.JUNGLE, itemProp(1).fireResistant()));
	@HasManualPage(PageGroup.SECURITY_SEA_BOATS)
	public static final DeferredItem<SecuritySeaBoatItem> ACACIA_SECURITY_SEA_BOAT = ITEMS.register("acacia_security_sea_boat", () -> new SecuritySeaBoatItem(Boat.Type.ACACIA, itemProp(1).fireResistant()));
	@HasManualPage(PageGroup.SECURITY_SEA_BOATS)
	public static final DeferredItem<SecuritySeaBoatItem> DARK_OAK_SECURITY_SEA_BOAT = ITEMS.register("dark_oak_security_sea_boat", () -> new SecuritySeaBoatItem(Boat.Type.DARK_OAK, itemProp(1).fireResistant()));
	@HasManualPage(PageGroup.SECURITY_SEA_BOATS)
	public static final DeferredItem<SecuritySeaBoatItem> MANGROVE_SECURITY_SEA_BOAT = ITEMS.register("mangrove_security_sea_boat", () -> new SecuritySeaBoatItem(Boat.Type.MANGROVE, itemProp(1).fireResistant()));
	@HasManualPage(PageGroup.SECURITY_SEA_BOATS)
	public static final DeferredItem<SecuritySeaBoatItem> CHERRY_SECURITY_SEA_BOAT = ITEMS.register("cherry_security_sea_boat", () -> new SecuritySeaBoatItem(Boat.Type.CHERRY, itemProp(1).fireResistant()));
	@HasManualPage(PageGroup.SECURITY_SEA_BOATS)
	public static final DeferredItem<SecuritySeaBoatItem> BAMBOO_SECURITY_SEA_RAFT = ITEMS.register("bamboo_security_sea_raft", () -> new SecuritySeaBoatItem(Boat.Type.BAMBOO, itemProp(1).fireResistant()));
	@HasManualPage(designedBy = "Henzoid")
	public static final DeferredItem<SentryItem> SENTRY = ITEMS.register("sentry", () -> new SentryItem(itemProp()));
	public static final DeferredItem<SonicSecuritySystemItem> SONIC_SECURITY_SYSTEM_ITEM = ITEMS.register("sonic_security_system", () -> new SonicSecuritySystemItem(itemProp(1)));
	@HasManualPage
	public static final DeferredItem<TaserItem> TASER = ITEMS.register("taser", () -> new TaserItem(itemProp().defaultDurability(151), false));
	public static final DeferredItem<TaserItem> TASER_POWERED = ITEMS.register("taser_powered", () -> new TaserItem(itemProp().defaultDurability(151), true));
	@HasManualPage
	public static final DeferredItem<UniversalBlockModifierItem> UNIVERSAL_BLOCK_MODIFIER = ITEMS.register("universal_block_modifier", () -> new UniversalBlockModifierItem(itemProp(1)));
	@HasManualPage(PageGroup.BLOCK_REINFORCERS)
	public static final DeferredItem<UniversalBlockReinforcerItem> UNIVERSAL_BLOCK_REINFORCER_LVL_1 = ITEMS.register("universal_block_reinforcer_lvl1", () -> new UniversalBlockReinforcerItem(itemProp().defaultDurability(300)));
	@HasManualPage(PageGroup.BLOCK_REINFORCERS)
	public static final DeferredItem<UniversalBlockReinforcerItem> UNIVERSAL_BLOCK_REINFORCER_LVL_2 = ITEMS.register("universal_block_reinforcer_lvl2", () -> new UniversalBlockReinforcerItem(itemProp().defaultDurability(2700)));
	@HasManualPage(PageGroup.BLOCK_REINFORCERS)
	public static final DeferredItem<UniversalBlockReinforcerItem> UNIVERSAL_BLOCK_REINFORCER_LVL_3 = ITEMS.register("universal_block_reinforcer_lvl3", () -> new UniversalBlockReinforcerItem(itemProp(1)));
	@HasManualPage
	public static final DeferredItem<UniversalBlockRemoverItem> UNIVERSAL_BLOCK_REMOVER = ITEMS.register("universal_block_remover", () -> new UniversalBlockRemoverItem(itemProp().defaultDurability(476)));
	@HasManualPage
	public static final DeferredItem<UniversalKeyChangerItem> UNIVERSAL_KEY_CHANGER = ITEMS.register("universal_key_changer", () -> new UniversalKeyChangerItem(itemProp(1)));
	@HasManualPage
	public static final DeferredItem<UniversalOwnerChangerItem> UNIVERSAL_OWNER_CHANGER = ITEMS.register("universal_owner_changer", () -> new UniversalOwnerChangerItem(itemProp().defaultDurability(48)));
	@HasManualPage
	public static final DeferredItem<Item> WIRE_CUTTERS = ITEMS.register("wire_cutters", () -> new WireCuttersItem(itemProp().defaultDurability(476)));

	//modules
	@HasManualPage
	public static final DeferredItem<ModuleItem> DENYLIST_MODULE = ITEMS.register("blacklist_module", () -> new ModuleItem(itemProp(1), ModuleType.DENYLIST, true, true));
	@HasManualPage
	public static final DeferredItem<ModuleItem> DISGUISE_MODULE = ITEMS.register("disguise_module", () -> new ModuleItem(itemProp(1), ModuleType.DISGUISE, false, true));
	@HasManualPage
	public static final DeferredItem<ModuleItem> HARMING_MODULE = ITEMS.register("harming_module", () -> new ModuleItem(itemProp(1), ModuleType.HARMING, false));
	@HasManualPage
	public static final DeferredItem<ModuleItem> REDSTONE_MODULE = ITEMS.register("redstone_module", () -> new ModuleItem(itemProp(1), ModuleType.REDSTONE, false));
	@HasManualPage
	public static final DeferredItem<ModuleItem> SMART_MODULE = ITEMS.register("smart_module", () -> new ModuleItem(itemProp(1), ModuleType.SMART, false));
	@HasManualPage
	public static final DeferredItem<ModuleItem> STORAGE_MODULE = ITEMS.register("storage_module", () -> new ModuleItem(itemProp(1), ModuleType.STORAGE, false));
	@HasManualPage
	public static final DeferredItem<ModuleItem> ALLOWLIST_MODULE = ITEMS.register("whitelist_module", () -> new ModuleItem(itemProp(1), ModuleType.ALLOWLIST, true, true));
	@HasManualPage
	public static final DeferredItem<ModuleItem> SPEED_MODULE = ITEMS.register("speed_module", () -> new ModuleItem(itemProp(1), ModuleType.SPEED, false));

	//block entity types
	//@formatter:off
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<OwnableBlockEntity>> OWNABLE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("ownable", () -> {
		List<Block> beOwnableBlocks = Arrays.stream(SCContent.class.getFields())
				.filter(field -> field.isAnnotationPresent(OwnableBE.class))
				.map(field -> {
					//@formatter:on
					try {
						return ((DeferredBlock<Block>) field.get(null)).get();
					}
					catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
						return null;
					}
				}) //@formatter:off
				.filter(Predicates.notNull())
				.toList();

		//@formatter:on
		return BlockEntityType.Builder.of((pos, state) -> {
			if (state.is(REINFORCED_OBSERVER))
				return new ReinforcedObserverBlockEntity(pos, state);
			else if (state.is(MINE))
				return new MineBlockEntity(pos, state);
			else
				return new OwnableBlockEntity(pos, state);
		}, beOwnableBlocks.toArray(new Block[beOwnableBlocks.size()])).build(null);
	});
	//@formatter:off
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<NamedBlockEntity>> ABSTRACT_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("abstract", () -> BlockEntityType.Builder.of((pos, state) -> {
		if (state.is(ELECTRIFIED_IRON_FENCE) || state.is(ELECTRIFIED_IRON_FENCE_GATE))
			return new ElectrifiedFenceAndGateBlockEntity(pos, state);
		else if (state.is(BOUNCING_BETTY))
			return new BouncingBettyBlockEntity(pos, state);
		else
			return new NamedBlockEntity(pos, state);
	},
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
			SCContent.DEEPSLATE_COAL_ORE_MINE.get(),
			SCContent.DEEPSLATE_COPPER_ORE_MINE.get(),
			SCContent.DEEPSLATE_DIAMOND_ORE_MINE.get(),
			SCContent.DEEPSLATE_EMERALD_ORE_MINE.get(),
			SCContent.DEEPSLATE_GOLD_ORE_MINE.get(),
			SCContent.DEEPSLATE_IRON_ORE_MINE.get(),
			SCContent.DEEPSLATE_LAPIS_ORE_MINE.get(),
			SCContent.DEEPSLATE_REDSTONE_ORE_MINE.get(),
			SCContent.COPPER_ORE_MINE.get(),
			SCContent.NETHERRACK_MINE.get(),
			SCContent.END_STONE_MINE.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KeypadBlockEntity>> KEYPAD_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("keypad", () -> BlockEntityType.Builder.of(KeypadBlockEntity::new, SCContent.KEYPAD.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LaserBlockBlockEntity>> LASER_BLOCK_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("laser_block", () -> BlockEntityType.Builder.of(LaserBlockBlockEntity::new, SCContent.LASER_BLOCK.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CageTrapBlockEntity>> CAGE_TRAP_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("cage_trap", () -> BlockEntityType.Builder.of(CageTrapBlockEntity::new, SCContent.CAGE_TRAP.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KeycardReaderBlockEntity>> KEYCARD_READER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("keycard_reader", () -> BlockEntityType.Builder.of(KeycardReaderBlockEntity::new, SCContent.KEYCARD_READER.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<InventoryScannerBlockEntity>> INVENTORY_SCANNER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("inventory_scanner", () -> BlockEntityType.Builder.of(InventoryScannerBlockEntity::new, SCContent.INVENTORY_SCANNER.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PortableRadarBlockEntity>> PORTABLE_RADAR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("portable_radar", () -> BlockEntityType.Builder.of(PortableRadarBlockEntity::new, SCContent.PORTABLE_RADAR.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SecurityCameraBlockEntity>> SECURITY_CAMERA_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("security_camera", () -> BlockEntityType.Builder.of(SecurityCameraBlockEntity::new, SCContent.SECURITY_CAMERA.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<UsernameLoggerBlockEntity>> USERNAME_LOGGER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("username_logger", () -> BlockEntityType.Builder.of(UsernameLoggerBlockEntity::new, SCContent.USERNAME_LOGGER.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RetinalScannerBlockEntity>> RETINAL_SCANNER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("retinal_scanner", () -> BlockEntityType.Builder.of(RetinalScannerBlockEntity::new, SCContent.RETINAL_SCANNER.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<? extends ChestBlockEntity>> KEYPAD_CHEST_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(KEYPAD_CHEST_PATH, () -> BlockEntityType.Builder.of(KeypadChestBlockEntity::new, SCContent.KEYPAD_CHEST.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AlarmBlockEntity>> ALARM_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("alarm", () -> BlockEntityType.Builder.of(AlarmBlockEntity::new, SCContent.ALARM.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ClaymoreBlockEntity>> CLAYMORE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("claymore", () -> BlockEntityType.Builder.of(ClaymoreBlockEntity::new, SCContent.CLAYMORE.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KeypadFurnaceBlockEntity>> KEYPAD_FURNACE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("keypad_furnace", () -> BlockEntityType.Builder.of(KeypadFurnaceBlockEntity::new, SCContent.KEYPAD_FURNACE.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KeypadSmokerBlockEntity>> KEYPAD_SMOKER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("keypad_smoker", () -> BlockEntityType.Builder.of(KeypadSmokerBlockEntity::new, SCContent.KEYPAD_SMOKER.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KeypadBlastFurnaceBlockEntity>> KEYPAD_BLAST_FURNACE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("keypad_blast_furnace", () -> BlockEntityType.Builder.of(KeypadBlastFurnaceBlockEntity::new, SCContent.KEYPAD_BLAST_FURNACE.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<IMSBlockEntity>> IMS_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("ims", () -> BlockEntityType.Builder.of(IMSBlockEntity::new, SCContent.IMS.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ProtectoBlockEntity>> PROTECTO_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("protecto", () -> BlockEntityType.Builder.of(ProtectoBlockEntity::new, SCContent.PROTECTO.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ScannerDoorBlockEntity>> SCANNER_DOOR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("scanner_door", () -> BlockEntityType.Builder.of(ScannerDoorBlockEntity::new, SCContent.SCANNER_DOOR.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SecretSignBlockEntity>> SECRET_SIGN_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("secret_sign", () -> BlockEntityType.Builder.of(SecretSignBlockEntity::new,
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
			SCContent.SECRET_CHERRY_SIGN.get(),
			SCContent.SECRET_CHERRY_WALL_SIGN.get(),
			SCContent.SECRET_BAMBOO_SIGN.get(),
			SCContent.SECRET_BAMBOO_WALL_SIGN.get(),
			SCContent.SECRET_CRIMSON_SIGN.get(),
			SCContent.SECRET_CRIMSON_WALL_SIGN.get(),
			SCContent.SECRET_WARPED_SIGN.get(),
			SCContent.SECRET_WARPED_WALL_SIGN.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SecretHangingSignBlockEntity>> SECRET_HANGING_SIGN_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("secret_hanging_sign", () -> BlockEntityType.Builder.of(SecretHangingSignBlockEntity::new,
			SCContent.SECRET_OAK_HANGING_SIGN.get(),
			SCContent.SECRET_OAK_WALL_HANGING_SIGN.get(),
			SCContent.SECRET_SPRUCE_HANGING_SIGN.get(),
			SCContent.SECRET_SPRUCE_WALL_HANGING_SIGN.get(),
			SCContent.SECRET_BIRCH_HANGING_SIGN.get(),
			SCContent.SECRET_BIRCH_WALL_HANGING_SIGN.get(),
			SCContent.SECRET_JUNGLE_HANGING_SIGN.get(),
			SCContent.SECRET_JUNGLE_WALL_HANGING_SIGN.get(),
			SCContent.SECRET_ACACIA_HANGING_SIGN.get(),
			SCContent.SECRET_ACACIA_WALL_HANGING_SIGN.get(),
			SCContent.SECRET_DARK_OAK_HANGING_SIGN.get(),
			SCContent.SECRET_DARK_OAK_WALL_HANGING_SIGN.get(),
			SCContent.SECRET_MANGROVE_HANGING_SIGN.get(),
			SCContent.SECRET_MANGROVE_WALL_HANGING_SIGN.get(),
			SCContent.SECRET_CHERRY_HANGING_SIGN.get(),
			SCContent.SECRET_CHERRY_WALL_HANGING_SIGN.get(),
			SCContent.SECRET_BAMBOO_HANGING_SIGN.get(),
			SCContent.SECRET_BAMBOO_WALL_HANGING_SIGN.get(),
			SCContent.SECRET_CRIMSON_HANGING_SIGN.get(),
			SCContent.SECRET_CRIMSON_WALL_HANGING_SIGN.get(),
			SCContent.SECRET_WARPED_HANGING_SIGN.get(),
			SCContent.SECRET_WARPED_WALL_HANGING_SIGN.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MotionActivatedLightBlockEntity>> MOTION_LIGHT_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("motion_light", () -> BlockEntityType.Builder.of(MotionActivatedLightBlockEntity::new, SCContent.MOTION_ACTIVATED_LIGHT.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TrackMineBlockEntity>> TRACK_MINE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("track_mine", () -> BlockEntityType.Builder.of(TrackMineBlockEntity::new, SCContent.TRACK_MINE.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TrophySystemBlockEntity>> TROPHY_SYSTEM_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("trophy_system", () -> BlockEntityType.Builder.of(TrophySystemBlockEntity::new, SCContent.TROPHY_SYSTEM.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockPocketManagerBlockEntity>> BLOCK_POCKET_MANAGER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("block_pocket_manager", () -> BlockEntityType.Builder.of(BlockPocketManagerBlockEntity::new, SCContent.BLOCK_POCKET_MANAGER.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockPocketBlockEntity>> BLOCK_POCKET_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("block_pocket", () -> BlockEntityType.Builder.of(BlockPocketBlockEntity::new,
			SCContent.BLOCK_POCKET_WALL.get(),
			SCContent.REINFORCED_CRYSTAL_QUARTZ_BLOCK.get(),
			SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get(),
			SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AllowlistOnlyBlockEntity>> ALLOWLIST_ONLY_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("reinforced_pressure_plate", () -> BlockEntityType.Builder.of(AllowlistOnlyBlockEntity::new,
			SCContent.REINFORCED_STONE_PRESSURE_PLATE.get(),
			SCContent.REINFORCED_ACACIA_PRESSURE_PLATE.get(),
			SCContent.REINFORCED_BAMBOO_PRESSURE_PLATE.get(),
			SCContent.REINFORCED_BIRCH_PRESSURE_PLATE.get(),
			SCContent.REINFORCED_CHERRY_PRESSURE_PLATE.get(),
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
			SCContent.REINFORCED_BAMBOO_BUTTON.get(),
			SCContent.REINFORCED_BIRCH_BUTTON.get(),
			SCContent.REINFORCED_CHERRY_BUTTON.get(),
			SCContent.REINFORCED_CRIMSON_BUTTON.get(),
			SCContent.REINFORCED_DARK_OAK_BUTTON.get(),
			SCContent.REINFORCED_JUNGLE_BUTTON.get(),
			SCContent.REINFORCED_MANGROVE_BUTTON.get(),
			SCContent.REINFORCED_OAK_BUTTON.get(),
			SCContent.REINFORCED_SPRUCE_BUTTON.get(),
			SCContent.REINFORCED_WARPED_BUTTON.get(),
			SCContent.REINFORCED_POLISHED_BLACKSTONE_BUTTON.get(),
			SCContent.REINFORCED_LEVER.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ReinforcedHopperBlockEntity>> REINFORCED_HOPPER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("reinforced_hopper", () -> BlockEntityType.Builder.of(ReinforcedHopperBlockEntity::new, SCContent.REINFORCED_HOPPER.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ProjectorBlockEntity>> PROJECTOR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("projector", () -> BlockEntityType.Builder.of(ProjectorBlockEntity::new, SCContent.PROJECTOR.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KeypadDoorBlockEntity>> KEYPAD_DOOR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("keypad_door", () -> BlockEntityType.Builder.of(KeypadDoorBlockEntity::new, SCContent.KEYPAD_DOOR.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ReinforcedIronBarsBlockEntity>> REINFORCED_IRON_BARS_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("reinforced_iron_bars", () -> BlockEntityType.Builder.of(ReinforcedIronBarsBlockEntity::new, SCContent.REINFORCED_IRON_BARS.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ReinforcedCauldronBlockEntity>> REINFORCED_CAULDRON_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("reinforced_cauldron", () -> BlockEntityType.Builder.of(ReinforcedCauldronBlockEntity::new,
			SCContent.REINFORCED_CAULDRON.get(),
			SCContent.REINFORCED_WATER_CAULDRON.get(),
			SCContent.REINFORCED_LAVA_CAULDRON.get(),
			SCContent.REINFORCED_POWDER_SNOW_CAULDRON.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ReinforcedPistonMovingBlockEntity>> REINFORCED_PISTON_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("reinforced_piston", () -> BlockEntityType.Builder.of(ReinforcedPistonMovingBlockEntity::new, SCContent.REINFORCED_MOVING_PISTON.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ValidationOwnableBlockEntity>> VALIDATION_OWNABLE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("validation_ownable", () -> BlockEntityType.Builder.of(ValidationOwnableBlockEntity::new,
			SCContent.REINFORCED_PISTON.get(),
			SCContent.REINFORCED_STICKY_PISTON.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KeyPanelBlockEntity>> KEY_PANEL_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("key_panel", () -> BlockEntityType.Builder.of(KeyPanelBlockEntity::new, SCContent.KEY_PANEL_BLOCK.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SonicSecuritySystemBlockEntity>> SONIC_SECURITY_SYSTEM_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("sonic_security_system", () -> BlockEntityType.Builder.of(SonicSecuritySystemBlockEntity::new, SCContent.SONIC_SECURITY_SYSTEM.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockChangeDetectorBlockEntity>> BLOCK_CHANGE_DETECTOR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("block_change_detector", () -> BlockEntityType.Builder.of(BlockChangeDetectorBlockEntity::new, SCContent.BLOCK_CHANGE_DETECTOR.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RiftStabilizerBlockEntity>> RIFT_STABILIZER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("rift_stabilizer", () -> BlockEntityType.Builder.of(RiftStabilizerBlockEntity::new, SCContent.RIFT_STABILIZER.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DisguisableBlockEntity>> DISGUISABLE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("disguisable", () -> BlockEntityType.Builder.of(DisguisableBlockEntity::new, SCContent.SENTRY_DISGUISE.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DisplayCaseBlockEntity>> DISPLAY_CASE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(DISPLAY_CASE_PATH, () -> BlockEntityType.Builder.of(DisplayCaseBlockEntity::new, SCContent.DISPLAY_CASE.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GlowDisplayCaseBlockEntity>> GLOW_DISPLAY_CASE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(GLOW_DISPLAY_CASE_PATH, () -> BlockEntityType.Builder.of(GlowDisplayCaseBlockEntity::new, SCContent.GLOW_DISPLAY_CASE.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KeypadBarrelBlockEntity>> KEYPAD_BARREL_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("keypad_barrel", () -> BlockEntityType.Builder.of(KeypadBarrelBlockEntity::new, SCContent.KEYPAD_BARREL.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BrushableMineBlockEntity>> BRUSHABLE_MINE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("brushable_mine", () -> BlockEntityType.Builder.of(BrushableMineBlockEntity::new, SCContent.SUSPICIOUS_SAND_MINE.get(), SCContent.SUSPICIOUS_GRAVEL_MINE.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ReinforcedChiseledBookshelfBlockEntity>> REINFORCED_CHISELED_BOOKSHELF_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("reinforced_chiseled_bookshelf", () -> BlockEntityType.Builder.of(ReinforcedChiseledBookshelfBlockEntity::new, SCContent.REINFORCED_CHISELED_BOOKSHELF.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KeypadTrapdoorBlockEntity>> KEYPAD_TRAPDOOR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("keypad_trapdoor", () -> BlockEntityType.Builder.of(KeypadTrapdoorBlockEntity::new, SCContent.KEYPAD_TRAPDOOR.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FloorTrapBlockEntity>> FLOOR_TRAP_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("floor_trap", () -> BlockEntityType.Builder.of(FloorTrapBlockEntity::new, SCContent.FLOOR_TRAP.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KeycardLockBlockEntity>> KEYCARD_LOCK_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("keycard_lock", () -> BlockEntityType.Builder.of(KeycardLockBlockEntity::new, SCContent.KEYCARD_LOCK.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ScannerTrapdoorBlockEntity>> SCANNER_TRAPDOOR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("scanner_trapdoor", () -> BlockEntityType.Builder.of(ScannerTrapdoorBlockEntity::new, SCContent.SCANNER_TRAPDOOR.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ReinforcedDispenserBlockEntity>> REINFORCED_DISPENSER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("reinforced_dispenser", () -> BlockEntityType.Builder.of(ReinforcedDispenserBlockEntity::new, SCContent.REINFORCED_DISPENSER.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ReinforcedDropperBlockEntity>> REINFORCED_DROPPER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("reinforced_dropper", () -> BlockEntityType.Builder.of(ReinforcedDropperBlockEntity::new, SCContent.REINFORCED_DROPPER.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ReinforcedFenceGateBlockEntity>> REINFORCED_FENCE_GATE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("reinforced_fence_gate", () -> BlockEntityType.Builder.of(ReinforcedFenceGateBlockEntity::new,
			SCContent.REINFORCED_OAK_FENCE_GATE.get(),
			SCContent.REINFORCED_SPRUCE_FENCE_GATE.get(),
			SCContent.REINFORCED_BIRCH_FENCE_GATE.get(),
			SCContent.REINFORCED_JUNGLE_FENCE_GATE.get(),
			SCContent.REINFORCED_ACACIA_FENCE_GATE.get(),
			SCContent.REINFORCED_DARK_OAK_FENCE_GATE.get(),
			SCContent.REINFORCED_MANGROVE_FENCE_GATE.get(),
			SCContent.REINFORCED_CHERRY_FENCE_GATE.get(),
			SCContent.REINFORCED_BAMBOO_FENCE_GATE.get(),
			SCContent.REINFORCED_CRIMSON_FENCE_GATE.get(),
			SCContent.REINFORCED_WARPED_FENCE_GATE.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ReinforcedLecternBlockEntity>> REINFORCED_LECTERN_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("reinforced_lectern", () -> BlockEntityType.Builder.of(ReinforcedLecternBlockEntity::new, SCContent.REINFORCED_LECTERN.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SecureRedstoneInterfaceBlockEntity>> SECURE_REDSTONE_INTERFACE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("secure_redstone_interface", () -> BlockEntityType.Builder.of(SecureRedstoneInterfaceBlockEntity::new, SCContent.SECURE_REDSTONE_INTERFACE.get()).build(null));

	//entity types
	public static final DeferredHolder<EntityType<?>, EntityType<BouncingBetty>> BOUNCING_BETTY_ENTITY = ENTITY_TYPES.register("bouncingbetty",
			() -> EntityType.Builder.<BouncingBetty>of(BouncingBetty::new, MobCategory.MISC)
			.sized(0.5F, 0.2F)
			.setTrackingRange(128)
			.setUpdateInterval(1)
			.setShouldReceiveVelocityUpdates(true)
			.build(SecurityCraft.MODID + ":bouncingbetty"));
	public static final DeferredHolder<EntityType<?>, EntityType<IMSBomb>> IMS_BOMB_ENTITY = ENTITY_TYPES.register("imsbomb",
			() -> EntityType.Builder.<IMSBomb>of(IMSBomb::new, MobCategory.MISC)
			.sized(0.25F, 0.3F)
			.setTrackingRange(256)
			.setUpdateInterval(1)
			.setShouldReceiveVelocityUpdates(true)
			.build(SecurityCraft.MODID + ":imsbomb"));
	public static final DeferredHolder<EntityType<?>, EntityType<SecurityCamera>> SECURITY_CAMERA_ENTITY = ENTITY_TYPES.register("securitycamera",
			() -> EntityType.Builder.<SecurityCamera>of(SecurityCamera::new, MobCategory.MISC)
			.sized(0.0001F, 0.0001F)
			.setTrackingRange(256)
			.setUpdateInterval(20)
			.setShouldReceiveVelocityUpdates(true)
			.build(SecurityCraft.MODID + ":securitycamera"));
	public static final DeferredHolder<EntityType<?>, EntityType<Sentry>> SENTRY_ENTITY = ENTITY_TYPES.register("sentry",
			() -> EntityType.Builder.<Sentry>of(Sentry::new, MobCategory.MISC)
			.sized(1.0F, 1.01F)
			.setTrackingRange(256)
			.setUpdateInterval(1)
			.setShouldReceiveVelocityUpdates(true)
			.build(SecurityCraft.MODID + ":sentry"));
	public static final DeferredHolder<EntityType<?>, EntityType<Bullet>> BULLET_ENTITY = ENTITY_TYPES.register("bullet",
			() -> EntityType.Builder.<Bullet>of(Bullet::new, MobCategory.MISC)
			.sized(0.15F, 0.1F)
			.setTrackingRange(256)
			.setUpdateInterval(1)
			.setShouldReceiveVelocityUpdates(true)
			.build(SecurityCraft.MODID + ":bullet"));
	public static final DeferredHolder<EntityType<?>, EntityType<SecuritySeaBoat>> SECURITY_SEA_BOAT_ENTITY = ENTITY_TYPES.register("security_sea_boat",
			() -> EntityType.Builder.<SecuritySeaBoat>of(SecuritySeaBoat::new, MobCategory.MISC)
					.sized(1.375F, 0.5625F)
					.clientTrackingRange(10)
					.fireImmune()
					.build(SecurityCraft.MODID + ":security_sea_boat"));
	//@formatter:on

	//container types
	public static final DeferredHolder<MenuType<?>, MenuType<BlockReinforcerMenu>> BLOCK_REINFORCER_MENU = MENU_TYPES.register("block_reinforcer", () -> IMenuTypeExtension.create((windowId, inv, data) -> new BlockReinforcerMenu(windowId, inv, data.readBoolean())));
	public static final DeferredHolder<MenuType<?>, MenuType<BriefcaseMenu>> BRIEFCASE_INVENTORY_MENU = MENU_TYPES.register("briefcase_inventory", () -> IMenuTypeExtension.create((windowId, inv, data) -> new BriefcaseMenu(windowId, inv, ItemContainer.briefcase(PlayerUtils.getItemStackFromAnyHand(inv.player, SCContent.BRIEFCASE.get())))));
	public static final DeferredHolder<MenuType<?>, MenuType<CustomizeBlockMenu>> CUSTOMIZE_BLOCK_MENU = MENU_TYPES.register("customize_block", () -> IMenuTypeExtension.create((windowId, inv, data) -> new CustomizeBlockMenu(windowId, inv.player.level(), data.readBlockPos(), inv)));
	public static final DeferredHolder<MenuType<?>, MenuType<CustomizeBlockMenu>> CUSTOMIZE_ENTITY_MENU = MENU_TYPES.register("customize_entity", () -> IMenuTypeExtension.create((windowId, inv, data) -> new CustomizeBlockMenu(windowId, inv.player.level(), data.readBlockPos(), data.readVarInt(), inv)));
	public static final DeferredHolder<MenuType<?>, MenuType<DisguiseModuleMenu>> DISGUISE_MODULE_MENU = MENU_TYPES.register("disguise_module", () -> IMenuTypeExtension.create((windowId, inv, data) -> new DisguiseModuleMenu(windowId, inv, new ModuleItemContainer(PlayerUtils.getItemStackFromAnyHand(inv.player, SCContent.DISGUISE_MODULE.get())))));
	public static final DeferredHolder<MenuType<?>, MenuType<InventoryScannerMenu>> INVENTORY_SCANNER_MENU = MENU_TYPES.register("inventory_scanner", () -> IMenuTypeExtension.create((windowId, inv, data) -> new InventoryScannerMenu(windowId, inv.player.level(), data.readBlockPos(), inv)));
	public static final DeferredHolder<MenuType<?>, MenuType<KeypadFurnaceMenu>> KEYPAD_FURNACE_MENU = MENU_TYPES.register("keypad_furnace", () -> IMenuTypeExtension.create((windowId, inv, data) -> new KeypadFurnaceMenu(windowId, inv.player.level(), data.readBlockPos(), inv)));
	public static final DeferredHolder<MenuType<?>, MenuType<KeypadSmokerMenu>> KEYPAD_SMOKER_MENU = MENU_TYPES.register("keypad_smoker", () -> IMenuTypeExtension.create((windowId, inv, data) -> new KeypadSmokerMenu(windowId, inv.player.level(), data.readBlockPos(), inv)));
	public static final DeferredHolder<MenuType<?>, MenuType<KeypadBlastFurnaceMenu>> KEYPAD_BLAST_FURNACE_MENU = MENU_TYPES.register("keypad_blast_furnace", () -> IMenuTypeExtension.create((windowId, inv, data) -> new KeypadBlastFurnaceMenu(windowId, inv.player.level(), data.readBlockPos(), inv)));
	public static final DeferredHolder<MenuType<?>, MenuType<ProjectorMenu>> PROJECTOR_MENU = MENU_TYPES.register("projector", () -> IMenuTypeExtension.create((windowId, inv, data) -> new ProjectorMenu(windowId, inv.player.level(), data.readBlockPos(), inv)));
	public static final DeferredHolder<MenuType<?>, MenuType<KeycardReaderMenu>> KEYCARD_READER_MENU = MENU_TYPES.register("keycard_setup", () -> IMenuTypeExtension.create((windowId, inv, data) -> new KeycardReaderMenu(windowId, inv, inv.player.level(), data.readBlockPos())));
	public static final DeferredHolder<MenuType<?>, MenuType<BlockPocketManagerMenu>> BLOCK_POCKET_MANAGER_MENU = MENU_TYPES.register("block_pocket_manager", () -> IMenuTypeExtension.create((windowId, inv, data) -> new BlockPocketManagerMenu(windowId, inv.player.level(), data.readBlockPos(), inv)));
	public static final DeferredHolder<MenuType<?>, MenuType<BlockChangeDetectorMenu>> BLOCK_CHANGE_DETECTOR_MENU = MENU_TYPES.register("block_change_detector", () -> IMenuTypeExtension.create((windowId, inv, data) -> new BlockChangeDetectorMenu(windowId, inv.player.level(), data.readBlockPos(), inv)));
	public static final DeferredHolder<MenuType<?>, MenuType<KeycardHolderMenu>> KEYCARD_HOLDER_MENU = MENU_TYPES.register("keycard_holder", () -> IMenuTypeExtension.create((windowId, inv, data) -> new KeycardHolderMenu(windowId, inv, ItemContainer.keycardHolder(PlayerUtils.getItemStackFromAnyHand(inv.player, SCContent.KEYCARD_HOLDER.get())))));
	public static final DeferredHolder<MenuType<?>, MenuType<TrophySystemMenu>> TROPHY_SYSTEM_MENU = MENU_TYPES.register("trophy_system", () -> IMenuTypeExtension.create((windowId, inv, data) -> new TrophySystemMenu(windowId, inv.player.level(), data.readBlockPos(), inv)));
	public static final DeferredHolder<MenuType<?>, MenuType<SingleLensMenu>> SINGLE_LENS_MENU = MENU_TYPES.register("single_lens", () -> IMenuTypeExtension.create((windowId, inv, data) -> new SingleLensMenu(windowId, inv.player.level(), data.readBlockPos(), inv)));
	public static final DeferredHolder<MenuType<?>, MenuType<LaserBlockMenu>> LASER_BLOCK_MENU = MENU_TYPES.register("laser_block", () -> IMenuTypeExtension.create((windowId, inv, data) -> new LaserBlockMenu(windowId, inv.player.level(), data.readBlockPos(), LaserBlockBlockEntity.loadSideConfig(data.readNbt()), inv)));
	public static final DeferredHolder<MenuType<?>, MenuType<ReinforcedLecternMenu>> REINFORCED_LECTERN_MENU = MENU_TYPES.register("reinforced_lectern", () -> IMenuTypeExtension.create((windowId, inv, data) -> new ReinforcedLecternMenu(windowId, inv.player.level(), data.readBlockPos())));

	private static final BlockBehaviour.Properties prop() {
		return prop(MapColor.STONE);
	}

	@SuppressWarnings("unused")
	private static final BlockBehaviour.Properties prop(FeatureFlag... requiredFeatures) {
		return prop().requiredFeatures(requiredFeatures);
	}

	private static final BlockBehaviour.Properties prop(MapColor color, float hardness) {
		return BlockBehaviour.Properties.of().mapColor(color).strength(hardness, Float.MAX_VALUE);
	}

	private static final BlockBehaviour.Properties prop(MapColor color) {
		return BlockBehaviour.Properties.of().mapColor(color).strength(-1.0F, Float.MAX_VALUE);
	}

	public static final BlockBehaviour.Properties reinforcedCopy(Block block) {
		return BlockBehaviour.Properties.ofFullCopy(block).strength(-1.0F, Float.MAX_VALUE);
	}

	@SuppressWarnings("unused")
	private static final BlockBehaviour.Properties prop(MapColor color, FeatureFlag... requiredFeatures) {
		return prop(color).requiredFeatures(requiredFeatures);
	}

	private static final BlockBehaviour.Properties logProp(MapColor topColor, MapColor sideColor) {
		return BlockBehaviour.Properties.of().mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? topColor : sideColor).strength(-1.0F, Float.MAX_VALUE);
	}

	private static final BlockBehaviour.Properties propDisguisable() {
		return propDisguisable(MapColor.STONE);
	}

	private static final BlockBehaviour.Properties propDisguisable(MapColor color) {
		return prop(color).noOcclusion().dynamicShape().isRedstoneConductor(DisguisableBlock::isNormalCube).isSuffocating(DisguisableBlock::isSuffocating);
	}

	private static final BlockBehaviour.Properties glassProp(MapColor color) {
		return prop().mapColor(color).sound(SoundType.GLASS).noOcclusion().isValidSpawn(SCContent::never).isRedstoneConductor(SCContent::never).isSuffocating(SCContent::never).isViewBlocking(SCContent::never);
	}

	private static BlockBehaviour.Properties mineProp(Block block) {
		return BlockBehaviour.Properties.ofLegacyCopy(block).explosionResistance(Float.MAX_VALUE).pushReaction(PushReaction.NORMAL);
	}

	private static final Item.Properties itemProp() {
		return new Item.Properties();
	}

	private static final Item.Properties itemProp(int stackSize) {
		return itemProp().stacksTo(stackSize);
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

	private static BaseFlowingFluid.Properties fakeWaterProperties() {
		return new BaseFlowingFluid.Properties(() -> NeoForgeMod.WATER_TYPE.value(), FAKE_WATER, FLOWING_FAKE_WATER).block(FAKE_WATER_BLOCK).bucket(FAKE_WATER_BUCKET);
	}

	private static BaseFlowingFluid.Properties fakeLavaProperties() {
		return new BaseFlowingFluid.Properties(() -> NeoForgeMod.LAVA_TYPE.value(), FAKE_LAVA, FLOWING_FAKE_LAVA).block(FAKE_LAVA_BLOCK).bucket(FAKE_LAVA_BUCKET);
	}

	private static ReinforcedButtonBlock woodenButton(Block vanillaBlock, BlockSetType blockSetType) {
		return new ReinforcedButtonBlock(prop().mapColor(MapColor.NONE).noCollission().pushReaction(PushReaction.BLOCK).forceSolidOn(), vanillaBlock, blockSetType, 30);
	}

	@SuppressWarnings("unused")
	private static ReinforcedButtonBlock woodenButton(Block vanillaBlock, BlockSetType blockSetType, FeatureFlag... requiredFeatures) {
		return new ReinforcedButtonBlock(prop().mapColor(MapColor.NONE).noCollission().pushReaction(PushReaction.BLOCK).forceSolidOn().requiredFeatures(requiredFeatures), vanillaBlock, blockSetType, 30);
	}

	private static ReinforcedButtonBlock stoneButton(Block vanillaBlock, BlockSetType blockSetType) {
		return new ReinforcedButtonBlock(prop().mapColor(MapColor.NONE).noCollission().pushReaction(PushReaction.BLOCK).forceSolidOn(), vanillaBlock, blockSetType, 20);
	}

	private static ReinforcedPressurePlateBlock woodenPressurePlate(Block vanillaBlock, BlockSetType blockSetType) {
		return new ReinforcedPressurePlateBlock(prop().mapColor(vanillaBlock.defaultMapColor()).noCollission().pushReaction(PushReaction.BLOCK).forceSolidOn(), vanillaBlock, blockSetType);
	}

	@SuppressWarnings("unused")
	private static ReinforcedPressurePlateBlock woodenPressurePlate(Block vanillaBlock, BlockSetType blockSetType, FeatureFlag... requiredFeatures) {
		return new ReinforcedPressurePlateBlock(prop().mapColor(vanillaBlock.defaultMapColor()).noCollission().requiredFeatures(requiredFeatures).pushReaction(PushReaction.BLOCK).forceSolidOn(), vanillaBlock, blockSetType);
	}

	private static ReinforcedPressurePlateBlock stonePressurePlate(Block vanillaBlock, BlockSetType blockSetType) {
		return new ReinforcedPressurePlateBlock(prop().mapColor(vanillaBlock.defaultMapColor()).noCollission().pushReaction(PushReaction.BLOCK).forceSolidOn(), vanillaBlock, blockSetType);
	}

	private SCContent() {}
}
