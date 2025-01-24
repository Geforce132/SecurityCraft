package net.geforcemods.securitycraft;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.base.Predicates;
import com.mojang.serialization.Codec;

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
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
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
import net.geforcemods.securitycraft.blockentities.PanicButtonBlockEntity;
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
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedMagmaBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedMossyCarpetBlock;
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
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedScaffoldingBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedSlabBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedSnowyDirtBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedSoulSandBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedStainedGlassBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedStainedGlassPaneBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedStairsBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedTintedGlassBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedWallBlock;
import net.geforcemods.securitycraft.commands.LowercasedEnumArgument;
import net.geforcemods.securitycraft.commands.SingleGameProfileArgument;
import net.geforcemods.securitycraft.components.CodebreakerData;
import net.geforcemods.securitycraft.components.GlobalPositions;
import net.geforcemods.securitycraft.components.KeycardData;
import net.geforcemods.securitycraft.components.ListModuleData;
import net.geforcemods.securitycraft.components.NamedPositions;
import net.geforcemods.securitycraft.components.Notes;
import net.geforcemods.securitycraft.components.OwnerData;
import net.geforcemods.securitycraft.components.PasscodeData;
import net.geforcemods.securitycraft.components.SavedBlockState;
import net.geforcemods.securitycraft.entity.BouncingBetty;
import net.geforcemods.securitycraft.entity.IMSBomb;
import net.geforcemods.securitycraft.entity.SecuritySeaBoat;
import net.geforcemods.securitycraft.entity.SecuritySeaRaft;
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
import net.geforcemods.securitycraft.items.ReinforcedScaffoldingBlockItem;
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
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
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
	public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, SecurityCraft.MODID);
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
	public static final int CRYSTAL_QUARTZ_TINT = 0xFF15B3A2;

	//command argument types
	public static final DeferredHolder<ArgumentTypeInfo<?, ?>, SingletonArgumentInfo<SingleGameProfileArgument>> SINGLE_GAME_PROFILE_COMMAND_ARGUMENT_TYPE = COMMAND_ARGUMENT_TYPES.register("single_game_profile", () -> ArgumentTypeInfos.registerByClass(SingleGameProfileArgument.class, SingletonArgumentInfo.contextFree(SingleGameProfileArgument::singleGameProfile)));
	@SuppressWarnings("rawtypes")
	public static final Holder<ArgumentTypeInfo<?, ?>> LOWERCASED_ENUM_COMMAND_ARGUMENT_TYPE = COMMAND_ARGUMENT_TYPES.register("lowercased_enum", () -> ArgumentTypeInfos.registerByClass(LowercasedEnumArgument.class, new LowercasedEnumArgument.Info()));

	//loot item condition types
	public static final DeferredHolder<LootItemConditionType, LootItemConditionType> BLOCK_ENTITY_NBT = LOOT_ITEM_CONDITION_TYPES.register("tile_entity_nbt", () -> new LootItemConditionType(BlockEntityNBTCondition.CODEC));

	//data components
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<KeycardData>> KEYCARD_DATA = DATA_COMPONENTS.registerComponentType("keycard_data", builder -> builder.persistent(KeycardData.CODEC).networkSynchronized(KeycardData.STREAM_CODEC).cacheEncoding());
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<OwnerData>> OWNER_DATA = DATA_COMPONENTS.registerComponentType("owner", builder -> builder.persistent(OwnerData.CODEC).networkSynchronized(OwnerData.STREAM_CODEC).cacheEncoding());
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<PasscodeData>> PASSCODE_DATA = DATA_COMPONENTS.registerComponentType("passcode_data", builder -> builder.persistent(PasscodeData.CODEC).cacheEncoding());
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<CodebreakerData>> CODEBREAKER_DATA = DATA_COMPONENTS.registerComponentType("codebreaker_data", builder -> builder.persistent(CodebreakerData.CODEC).networkSynchronized(CodebreakerData.STREAM_CODEC).cacheEncoding());
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<NamedPositions>> BOUND_CAMERAS = DATA_COMPONENTS.registerComponentType("bound_cameras", builder -> builder.persistent(NamedPositions.codec(CameraMonitorItem.MAX_CAMERAS)).networkSynchronized(NamedPositions.streamCodec(CameraMonitorItem.MAX_CAMERAS)).cacheEncoding());
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<GlobalPositions>> BOUND_MINES = DATA_COMPONENTS.registerComponentType("bound_mines", builder -> builder.persistent(GlobalPositions.codec(MineRemoteAccessToolItem.MAX_MINES)).networkSynchronized(GlobalPositions.streamCodec(MineRemoteAccessToolItem.MAX_MINES)).cacheEncoding());
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<GlobalPositions>> SSS_LINKED_BLOCKS = DATA_COMPONENTS.registerComponentType("sss_linked_blocks", builder -> builder.persistent(GlobalPositions.codec(SonicSecuritySystemBlockEntity.MAX_LINKED_BLOCKS)).networkSynchronized(GlobalPositions.streamCodec(SonicSecuritySystemBlockEntity.MAX_LINKED_BLOCKS)).cacheEncoding());
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<NamedPositions>> BOUND_SENTRIES = DATA_COMPONENTS.registerComponentType("bound_sentries", builder -> builder.persistent(NamedPositions.codec(SentryRemoteAccessToolItem.MAX_SENTRIES)).networkSynchronized(NamedPositions.streamCodec(SentryRemoteAccessToolItem.MAX_SENTRIES)).cacheEncoding());
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Notes>> NOTES = DATA_COMPONENTS.registerComponentType("notes", builder -> builder.persistent(Notes.CODEC).networkSynchronized(Notes.STREAM_CODEC).cacheEncoding());
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> UNREINFORCING = DATA_COMPONENTS.registerComponentType("unreinforcing", builder -> builder.persistent(Codec.unit(Unit.INSTANCE)));
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<ListModuleData>> LIST_MODULE_DATA = DATA_COMPONENTS.registerComponentType("list_module_data", builder -> builder.persistent(ListModuleData.CODEC).networkSynchronized(ListModuleData.STREAM_CODEC).cacheEncoding());
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<SavedBlockState>> SAVED_BLOCK_STATE = DATA_COMPONENTS.registerComponentType("saved_block_state", builder -> builder.persistent(SavedBlockState.CODEC).networkSynchronized(SavedBlockState.STREAM_CODEC).cacheEncoding());
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Double>> SUCCESS_CHANCE = DATA_COMPONENTS.registerComponentType("success_chance", builder -> builder.persistent(Codec.doubleRange(-1.0D, 1.0D)).networkSynchronized(ByteBufCodecs.DOUBLE).cacheEncoding());

	//recipe serializers
	public static final DeferredHolder<RecipeSerializer<?>, CustomRecipe.Serializer<BlockReinforcingRecipe>> BLOCK_REINFORCING_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("block_reinforcing_recipe", () -> new CustomRecipe.Serializer<>(BlockReinforcingRecipe::new));
	public static final DeferredHolder<RecipeSerializer<?>, CustomRecipe.Serializer<BlockUnreinforcingRecipe>> BLOCK_UNREINFORCING_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("block_unreinforcing_recipe", () -> new CustomRecipe.Serializer<>(BlockUnreinforcingRecipe::new));
	public static final DeferredHolder<RecipeSerializer<?>, CustomRecipe.Serializer<LimitedUseKeycardRecipe>> LIMITED_USE_KEYCARD_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("limited_use_keycard_recipe", () -> new CustomRecipe.Serializer<>(LimitedUseKeycardRecipe::new));

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
	public static final DeferredBlock<AlarmBlock> ALARM = BLOCKS.registerBlock("alarm", AlarmBlock::new, prop(MapColor.METAL).lightLevel(state -> state.getValue(AlarmBlock.LIT) ? 15 : 0));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<BlockChangeDetectorBlock> BLOCK_CHANGE_DETECTOR = BLOCKS.registerBlock("block_change_detector", BlockChangeDetectorBlock::new, propDisguisable());
	@HasManualPage(designedBy = "Henzoid")
	@RegisterItemBlock
	public static final DeferredBlock<BlockPocketManagerBlock> BLOCK_POCKET_MANAGER = BLOCKS.registerBlock("block_pocket_manager", BlockPocketManagerBlock::new, prop(MapColor.COLOR_CYAN));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<BlockPocketWallBlock> BLOCK_POCKET_WALL = BLOCKS.registerBlock("block_pocket_wall", BlockPocketWallBlock::new, prop(MapColor.COLOR_CYAN).noCollission().isRedstoneConductor(SCContent::never).isSuffocating(BlockPocketWallBlock::causesSuffocation).isViewBlocking(BlockPocketWallBlock::causesSuffocation).isValidSpawn(SCContent::never));
	@HasManualPage
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BouncingBettyBlock> BOUNCING_BETTY = BLOCKS.registerBlock("bouncing_betty", BouncingBettyBlock::new, prop(MapColor.METAL, 1.0F).forceSolidOn().pushReaction(PushReaction.NORMAL));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<CageTrapBlock> CAGE_TRAP = BLOCKS.registerBlock("cage_trap", CageTrapBlock::new, propDisguisable(MapColor.METAL).sound(SoundType.METAL).noCollission());
	@HasManualPage
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<ClaymoreBlock> CLAYMORE = BLOCKS.registerBlock("claymore", ClaymoreBlock::new, prop(MapColor.METAL).forceSolidOn().pushReaction(PushReaction.NORMAL));
	@HasManualPage(PageGroup.DISPLAY_CASES)
	public static final DeferredBlock<DisplayCaseBlock> DISPLAY_CASE = BLOCKS.registerBlock(DISPLAY_CASE_PATH, p -> new DisplayCaseBlock(p, false), prop(MapColor.METAL).sound(SoundType.METAL));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<FloorTrapBlock> FLOOR_TRAP = BLOCKS.registerBlock("floor_trap", FloorTrapBlock::new, propDisguisable(MapColor.METAL).sound(SoundType.METAL));
	@HasManualPage
	@OwnableBE
	@RegisterItemBlock
	public static final DeferredBlock<FrameBlock> FRAME = BLOCKS.registerBlock("keypad_frame", FrameBlock::new, prop().sound(SoundType.METAL));
	@HasManualPage(PageGroup.DISPLAY_CASES)
	public static final DeferredBlock<DisplayCaseBlock> GLOW_DISPLAY_CASE = BLOCKS.registerBlock(GLOW_DISPLAY_CASE_PATH, p -> new DisplayCaseBlock(p, true), prop(MapColor.METAL).sound(SoundType.METAL));
	@HasManualPage
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<IMSBlock> IMS = BLOCKS.registerBlock("ims", IMSBlock::new, prop(MapColor.METAL, 0.7F).sound(SoundType.METAL));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<InventoryScannerBlock> INVENTORY_SCANNER = BLOCKS.registerBlock("inventory_scanner", InventoryScannerBlock::new, propDisguisable());
	public static final DeferredBlock<InventoryScannerFieldBlock> INVENTORY_SCANNER_FIELD = BLOCKS.registerBlock("inventory_scanner_field", InventoryScannerFieldBlock::new, prop(MapColor.NONE).noLootTable());
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<ElectrifiedIronFenceBlock> ELECTRIFIED_IRON_FENCE = BLOCKS.registerBlock("electrified_iron_fence", ElectrifiedIronFenceBlock::new, prop(MapColor.METAL).sound(SoundType.METAL));
	public static final DeferredBlock<KeyPanelBlock> KEY_PANEL_BLOCK = BLOCKS.registerBlock("key_panel", KeyPanelBlock::new, prop(MapColor.METAL).sound(SoundType.METAL));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<KeycardLockBlock> KEYCARD_LOCK = BLOCKS.registerBlock("keycard_lock", KeycardLockBlock::new, prop());
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<KeycardReaderBlock> KEYCARD_READER = BLOCKS.registerBlock("keycard_reader", KeycardReaderBlock::new, propDisguisable());
	@HasManualPage(hasRecipeDescription = true)
	@RegisterItemBlock
	public static final DeferredBlock<KeypadBlock> KEYPAD = BLOCKS.registerBlock("keypad", KeypadBlock::new, propDisguisable(MapColor.METAL).sound(SoundType.METAL));
	@HasManualPage(hasRecipeDescription = true)
	@RegisterItemBlock
	public static final DeferredBlock<KeypadBarrelBlock> KEYPAD_BARREL = BLOCKS.registerBlock("keypad_barrel", KeypadBarrelBlock::new, propDisguisable(MapColor.METAL).sound(SoundType.METAL));
	@HasManualPage(hasRecipeDescription = true)
	public static final DeferredBlock<KeypadChestBlock> KEYPAD_CHEST = BLOCKS.registerBlock(KEYPAD_CHEST_PATH, KeypadChestBlock::new, propDisguisable(MapColor.METAL).sound(SoundType.METAL));
	public static final DeferredBlock<KeypadDoorBlock> KEYPAD_DOOR = BLOCKS.registerBlock("keypad_door", p -> new KeypadDoorBlock(p, BlockSetType.IRON), propDisguisable(MapColor.METAL).sound(SoundType.METAL).pushReaction(PushReaction.BLOCK));
	@HasManualPage(hasRecipeDescription = true)
	@RegisterItemBlock
	public static final DeferredBlock<KeypadTrapDoorBlock> KEYPAD_TRAPDOOR = BLOCKS.registerBlock("keypad_trapdoor", p -> new KeypadTrapDoorBlock(p, BlockSetType.IRON), propDisguisable(MapColor.METAL).sound(SoundType.METAL).isValidSpawn(SCContent::never));
	@HasManualPage(hasRecipeDescription = true)
	@RegisterItemBlock
	public static final DeferredBlock<KeypadFurnaceBlock> KEYPAD_FURNACE = BLOCKS.registerBlock("keypad_furnace", KeypadFurnaceBlock::new, prop(MapColor.METAL).sound(SoundType.METAL).lightLevel(state -> state.getValue(AbstractKeypadFurnaceBlock.LIT) ? 13 : 0));
	@HasManualPage(hasRecipeDescription = true)
	@RegisterItemBlock
	public static final DeferredBlock<KeypadSmokerBlock> KEYPAD_SMOKER = BLOCKS.registerBlock("keypad_smoker", KeypadSmokerBlock::new, prop(MapColor.METAL).sound(SoundType.METAL).lightLevel(state -> state.getValue(AbstractKeypadFurnaceBlock.LIT) ? 13 : 0));
	@HasManualPage(hasRecipeDescription = true)
	@RegisterItemBlock
	public static final DeferredBlock<KeypadBlastFurnaceBlock> KEYPAD_BLAST_FURNACE = BLOCKS.registerBlock("keypad_blast_furnace", KeypadBlastFurnaceBlock::new, prop(MapColor.METAL).sound(SoundType.METAL).lightLevel(state -> state.getValue(AbstractKeypadFurnaceBlock.LIT) ? 13 : 0));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<LaserBlock> LASER_BLOCK = BLOCKS.registerBlock("laser_block", LaserBlock::new, propDisguisable());
	public static final DeferredBlock<LaserFieldBlock> LASER_FIELD = BLOCKS.registerBlock("laser", LaserFieldBlock::new, prop(MapColor.NONE).noLootTable());
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<MotionActivatedLightBlock> MOTION_ACTIVATED_LIGHT = BLOCKS.registerBlock("motion_activated_light", MotionActivatedLightBlock::new, prop(MapColor.NONE).sound(SoundType.GLASS).lightLevel(state -> state.getValue(MotionActivatedLightBlock.LIT) ? 15 : 0));
	@HasManualPage
	@OwnableBE
	@RegisterItemBlock
	public static final DeferredBlock<PanicButtonBlock> PANIC_BUTTON = BLOCKS.registerBlock("panic_button", p -> new PanicButtonBlock(p, BlockSetType.STONE, -1), prop().lightLevel(state -> state.getValue(ButtonBlock.POWERED) ? 4 : 0));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<PortableRadarBlock> PORTABLE_RADAR = BLOCKS.registerBlock("portable_radar", PortableRadarBlock::new, prop(MapColor.METAL));
	@HasManualPage
	@OwnableBE
	@RegisterItemBlock
	public static final DeferredBlock<ProjectorBlock> PROJECTOR = BLOCKS.registerBlock("projector", ProjectorBlock::new, propDisguisable(MapColor.METAL).sound(SoundType.METAL));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<ProtectoBlock> PROTECTO = BLOCKS.registerBlock("protecto", ProtectoBlock::new, propDisguisable(MapColor.METAL).sound(SoundType.METAL).lightLevel(state -> 7));
	@OwnableBE
	public static final DeferredBlock<ReinforcedDoorBlock> REINFORCED_DOOR = BLOCKS.registerBlock("iron_door_reinforced", ReinforcedDoorBlock::new, prop(MapColor.METAL).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.BLOCK));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<ElectrifiedIronFenceGateBlock> ELECTRIFIED_IRON_FENCE_GATE = BLOCKS.registerBlock("reinforced_fence_gate", ElectrifiedIronFenceGateBlock::new, prop(MapColor.METAL).sound(SoundType.METAL).forceSolidOn());
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<RetinalScannerBlock> RETINAL_SCANNER = BLOCKS.registerBlock("retinal_scanner", RetinalScannerBlock::new, propDisguisable());
	public static final DeferredBlock<RiftStabilizerBlock> RIFT_STABILIZER = BLOCKS.registerBlock("rift_stabilizer", RiftStabilizerBlock::new, propDisguisable(MapColor.METAL).sound(SoundType.METAL));
	public static final DeferredBlock<ScannerDoorBlock> SCANNER_DOOR = BLOCKS.registerBlock("scanner_door", p -> new ScannerDoorBlock(p, BlockSetType.IRON), propDisguisable(MapColor.METAL).sound(SoundType.METAL).pushReaction(PushReaction.BLOCK));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<ScannerTrapDoorBlock> SCANNER_TRAPDOOR = BLOCKS.registerBlock("scanner_trapdoor", p -> new ScannerTrapDoorBlock(p, BlockSetType.IRON), propDisguisable(MapColor.METAL).sound(SoundType.METAL).isValidSpawn(SCContent::never));
	public static final DeferredBlock<SecretStandingSignBlock> SECRET_OAK_SIGN = BLOCKS.registerBlock("secret_sign_standing", p -> new SecretStandingSignBlock(p, WoodType.OAK), prop(MapColor.WOOD).sound(SoundType.WOOD).noCollission().forceSolidOn().overrideDescription("block.securitycraft.secret_sign"));
	public static final DeferredBlock<SecretWallSignBlock> SECRET_OAK_WALL_SIGN = BLOCKS.registerBlock("secret_sign_wall", p -> new SecretWallSignBlock(p, WoodType.OAK), prop(MapColor.WOOD).sound(SoundType.WOOD).noCollission().forceSolidOn().overrideDescription("block.securitycraft.secret_sign"));
	public static final DeferredBlock<SecretStandingSignBlock> SECRET_SPRUCE_SIGN = BLOCKS.registerBlock("secret_spruce_sign_standing", p -> new SecretStandingSignBlock(p, WoodType.SPRUCE), prop(MapColor.COLOR_BROWN).sound(SoundType.WOOD).noCollission().forceSolidOn().overrideDescription("block.securitycraft.secret_spruce_sign"));
	public static final DeferredBlock<SecretWallSignBlock> SECRET_SPRUCE_WALL_SIGN = BLOCKS.registerBlock("secret_spruce_sign_wall", p -> new SecretWallSignBlock(p, WoodType.SPRUCE), prop(MapColor.COLOR_BROWN).sound(SoundType.WOOD).noCollission().forceSolidOn().overrideDescription("block.securitycraft.secret_spruce_sign"));
	public static final DeferredBlock<SecretStandingSignBlock> SECRET_BIRCH_SIGN = BLOCKS.registerBlock("secret_birch_sign_standing", p -> new SecretStandingSignBlock(p, WoodType.BIRCH), prop(MapColor.SAND).sound(SoundType.WOOD).noCollission().forceSolidOn().overrideDescription("block.securitycraft.secret_birch_sign"));
	public static final DeferredBlock<SecretWallSignBlock> SECRET_BIRCH_WALL_SIGN = BLOCKS.registerBlock("secret_birch_sign_wall", p -> new SecretWallSignBlock(p, WoodType.BIRCH), prop(MapColor.SAND).sound(SoundType.WOOD).noCollission().forceSolidOn().overrideDescription("block.securitycraft.secret_birch_sign"));
	public static final DeferredBlock<SecretStandingSignBlock> SECRET_JUNGLE_SIGN = BLOCKS.registerBlock("secret_jungle_sign_standing", p -> new SecretStandingSignBlock(p, WoodType.JUNGLE), prop(MapColor.PODZOL).sound(SoundType.WOOD).noCollission().forceSolidOn().overrideDescription("block.securitycraft.secret_jungle_sign"));
	public static final DeferredBlock<SecretWallSignBlock> SECRET_JUNGLE_WALL_SIGN = BLOCKS.registerBlock("secret_jungle_sign_wall", p -> new SecretWallSignBlock(p, WoodType.JUNGLE), prop(MapColor.PODZOL).sound(SoundType.WOOD).noCollission().forceSolidOn().overrideDescription("block.securitycraft.secret_jungle_sign"));
	public static final DeferredBlock<SecretStandingSignBlock> SECRET_ACACIA_SIGN = BLOCKS.registerBlock("secret_acacia_sign_standing", p -> new SecretStandingSignBlock(p, WoodType.ACACIA), prop(MapColor.COLOR_ORANGE).sound(SoundType.WOOD).noCollission().forceSolidOn().overrideDescription("block.securitycraft.secret_acacia_sign"));
	public static final DeferredBlock<SecretWallSignBlock> SECRET_ACACIA_WALL_SIGN = BLOCKS.registerBlock("secret_acacia_sign_wall", p -> new SecretWallSignBlock(p, WoodType.ACACIA), prop(MapColor.COLOR_ORANGE).sound(SoundType.WOOD).noCollission().forceSolidOn().overrideDescription("block.securitycraft.secret_acacia_sign"));
	public static final DeferredBlock<SecretStandingSignBlock> SECRET_DARK_OAK_SIGN = BLOCKS.registerBlock("secret_dark_oak_sign_standing", p -> new SecretStandingSignBlock(p, WoodType.DARK_OAK), prop(MapColor.COLOR_BROWN).sound(SoundType.WOOD).noCollission().forceSolidOn().overrideDescription("block.securitycraft.secret_dark_oak_sign"));
	public static final DeferredBlock<SecretWallSignBlock> SECRET_DARK_OAK_WALL_SIGN = BLOCKS.registerBlock("secret_dark_oak_sign_wall", p -> new SecretWallSignBlock(p, WoodType.DARK_OAK), prop(MapColor.COLOR_BROWN).sound(SoundType.WOOD).noCollission().forceSolidOn().overrideDescription("block.securitycraft.secret_dark_oak_sign"));
	public static final DeferredBlock<SecretStandingSignBlock> SECRET_MANGROVE_SIGN = BLOCKS.registerBlock("secret_mangrove_sign_standing", p -> new SecretStandingSignBlock(p, WoodType.MANGROVE), prop(MapColor.PODZOL).sound(SoundType.WOOD).noCollission().forceSolidOn().overrideDescription("block.securitycraft.secret_mangrove_sign"));
	public static final DeferredBlock<SecretWallSignBlock> SECRET_MANGROVE_WALL_SIGN = BLOCKS.registerBlock("secret_mangrove_sign_wall", p -> new SecretWallSignBlock(p, WoodType.MANGROVE), prop(MapColor.PODZOL).sound(SoundType.WOOD).noCollission().forceSolidOn().overrideDescription("block.securitycraft.secret_mangrove_sign"));
	public static final DeferredBlock<SecretStandingSignBlock> SECRET_CRIMSON_SIGN = BLOCKS.registerBlock("secret_crimson_sign_standing", p -> new SecretStandingSignBlock(p, WoodType.CRIMSON), prop(MapColor.CRIMSON_STEM).sound(SoundType.NETHER_WOOD).noCollission().forceSolidOn().overrideDescription("block.securitycraft.secret_crimson_sign"));
	public static final DeferredBlock<SecretWallSignBlock> SECRET_CRIMSON_WALL_SIGN = BLOCKS.registerBlock("secret_crimson_sign_wall", p -> new SecretWallSignBlock(p, WoodType.CRIMSON), prop(MapColor.CRIMSON_STEM).sound(SoundType.NETHER_WOOD).noCollission().forceSolidOn().overrideDescription("block.securitycraft.secret_crimson_sign"));
	public static final DeferredBlock<SecretStandingSignBlock> SECRET_WARPED_SIGN = BLOCKS.registerBlock("secret_warped_sign_standing", p -> new SecretStandingSignBlock(p, WoodType.WARPED), prop(MapColor.WARPED_STEM).sound(SoundType.NETHER_WOOD).noCollission().forceSolidOn().overrideDescription("block.securitycraft.secret_warped_sign"));
	public static final DeferredBlock<SecretWallSignBlock> SECRET_WARPED_WALL_SIGN = BLOCKS.registerBlock("secret_warped_sign_wall", p -> new SecretWallSignBlock(p, WoodType.WARPED), prop(MapColor.WARPED_STEM).sound(SoundType.NETHER_WOOD).noCollission().forceSolidOn().overrideDescription("block.securitycraft.secret_warped_sign"));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<SecureRedstoneInterfaceBlock> SECURE_REDSTONE_INTERFACE = BLOCKS.registerBlock("secure_redstone_interface", SecureRedstoneInterfaceBlock::new, propDisguisable());
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<SecurityCameraBlock> SECURITY_CAMERA = BLOCKS.registerBlock("security_camera", SecurityCameraBlock::new, propDisguisable(MapColor.METAL, false).noCollission());
	@HasManualPage
	public static final DeferredBlock<SonicSecuritySystemBlock> SONIC_SECURITY_SYSTEM = BLOCKS.registerBlock("sonic_security_system", SonicSecuritySystemBlock::new, propDisguisable(MapColor.METAL, false).sound(SoundType.METAL).noCollission());
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<TrackMineBlock> TRACK_MINE = BLOCKS.registerBlock("track_mine", TrackMineBlock::new, prop(MapColor.METAL, 0.7F).noCollission().sound(SoundType.METAL));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<TrophySystemBlock> TROPHY_SYSTEM = BLOCKS.registerBlock("trophy_system", TrophySystemBlock::new, propDisguisable(MapColor.METAL, false).sound(SoundType.METAL));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<UsernameLoggerBlock> USERNAME_LOGGER = BLOCKS.registerBlock("username_logger", UsernameLoggerBlock::new, propDisguisable());
	@HasManualPage
	@OwnableBE
	@RegisterItemBlock
	public static final DeferredBlock<MineBlock> MINE = BLOCKS.registerBlock("mine", MineBlock::new, prop(MapColor.METAL, 1.0F).forceSolidOn().pushReaction(PushReaction.NORMAL));
	public static final DeferredBlock<FakeWaterBlock> FAKE_WATER_BLOCK = BLOCKS.registerBlock("fake_water_block", p -> new FakeWaterBlock(p, FAKE_WATER.get()), prop(MapColor.WATER).replaceable().noLootTable().liquid().sound(SoundType.EMPTY).pushReaction(PushReaction.DESTROY).noCollission());
	public static final DeferredBlock<FakeLavaBlock> FAKE_LAVA_BLOCK = BLOCKS.registerBlock("fake_lava_block", p -> new FakeLavaBlock(p, FAKE_LAVA.get()), prop(MapColor.FIRE).replaceable().noLootTable().liquid().sound(SoundType.EMPTY).pushReaction(PushReaction.DESTROY).noCollission().randomTicks().lightLevel(state -> 15));

	//block mines
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> STONE_MINE = BLOCKS.registerBlock("stone_mine", p -> new BaseFullMineBlock(p, Blocks.STONE), mineProp(Blocks.STONE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<DeepslateMineBlock> DEEPSLATE_MINE = BLOCKS.registerBlock("deepslate_mine", p -> new DeepslateMineBlock(p, Blocks.DEEPSLATE), mineProp(Blocks.DEEPSLATE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> COBBLED_DEEPSLATE_MINE = BLOCKS.registerBlock("cobbled_deepslate_mine", p -> new BaseFullMineBlock(p, Blocks.COBBLED_DEEPSLATE), mineProp(Blocks.COBBLED_DEEPSLATE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> DIRT_MINE = BLOCKS.registerBlock("dirt_mine", p -> new BaseFullMineBlock(p, Blocks.DIRT), mineProp(Blocks.DIRT));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> COBBLESTONE_MINE = BLOCKS.registerBlock("cobblestone_mine", p -> new BaseFullMineBlock(p, Blocks.COBBLESTONE), mineProp(Blocks.COBBLESTONE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<FallingBlockMineBlock> SAND_MINE = BLOCKS.registerBlock("sand_mine", p -> new FallingBlockMineBlock(p, Blocks.SAND), mineProp(Blocks.SAND));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<FallingBlockMineBlock> GRAVEL_MINE = BLOCKS.registerBlock("gravel_mine", p -> new FallingBlockMineBlock(p, Blocks.GRAVEL), mineProp(Blocks.GRAVEL));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> NETHERRACK_MINE = BLOCKS.registerBlock("netherrack_mine", p -> new BaseFullMineBlock(p, Blocks.NETHERRACK), mineProp(Blocks.NETHERRACK));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> END_STONE_MINE = BLOCKS.registerBlock("end_stone_mine", p -> new BaseFullMineBlock(p, Blocks.END_STONE), mineProp(Blocks.END_STONE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> COAL_ORE_MINE = BLOCKS.registerBlock("coal_mine", p -> new BaseFullMineBlock(p, Blocks.COAL_ORE), mineProp(Blocks.COAL_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> DEEPSLATE_COAL_ORE_MINE = BLOCKS.registerBlock("deepslate_coal_mine", p -> new BaseFullMineBlock(p, Blocks.DEEPSLATE_COAL_ORE), mineProp(Blocks.DEEPSLATE_COAL_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> IRON_ORE_MINE = BLOCKS.registerBlock("iron_mine", p -> new BaseFullMineBlock(p, Blocks.IRON_ORE), mineProp(Blocks.IRON_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> DEEPSLATE_IRON_ORE_MINE = BLOCKS.registerBlock("deepslate_iron_mine", p -> new BaseFullMineBlock(p, Blocks.DEEPSLATE_IRON_ORE), mineProp(Blocks.DEEPSLATE_IRON_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> GOLD_ORE_MINE = BLOCKS.registerBlock("gold_mine", p -> new BaseFullMineBlock(p, Blocks.GOLD_ORE), mineProp(Blocks.GOLD_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> DEEPSLATE_GOLD_ORE_MINE = BLOCKS.registerBlock("deepslate_gold_mine", p -> new BaseFullMineBlock(p, Blocks.DEEPSLATE_GOLD_ORE), mineProp(Blocks.DEEPSLATE_GOLD_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> COPPER_ORE_MINE = BLOCKS.registerBlock("copper_mine", p -> new BaseFullMineBlock(p, Blocks.COPPER_ORE), mineProp(Blocks.COPPER_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> DEEPSLATE_COPPER_ORE_MINE = BLOCKS.registerBlock("deepslate_copper_mine", p -> new BaseFullMineBlock(p, Blocks.DEEPSLATE_COPPER_ORE), mineProp(Blocks.DEEPSLATE_COPPER_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<RedstoneOreMineBlock> REDSTONE_ORE_MINE = BLOCKS.registerBlock("redstone_mine", p -> new RedstoneOreMineBlock(p, Blocks.REDSTONE_ORE), mineProp(Blocks.REDSTONE_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<RedstoneOreMineBlock> DEEPSLATE_REDSTONE_ORE_MINE = BLOCKS.registerBlock("deepslate_redstone_mine", p -> new RedstoneOreMineBlock(p, Blocks.DEEPSLATE_REDSTONE_ORE), mineProp(Blocks.DEEPSLATE_REDSTONE_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> EMERALD_ORE_MINE = BLOCKS.registerBlock("emerald_mine", p -> new BaseFullMineBlock(p, Blocks.EMERALD_ORE), mineProp(Blocks.EMERALD_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> DEEPSLATE_EMERALD_ORE_MINE = BLOCKS.registerBlock("deepslate_emerald_mine", p -> new BaseFullMineBlock(p, Blocks.DEEPSLATE_EMERALD_ORE), mineProp(Blocks.DEEPSLATE_EMERALD_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> LAPIS_ORE_MINE = BLOCKS.registerBlock("lapis_mine", p -> new BaseFullMineBlock(p, Blocks.LAPIS_ORE), mineProp(Blocks.LAPIS_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> DEEPSLATE_LAPIS_ORE_MINE = BLOCKS.registerBlock("deepslate_lapis_mine", p -> new BaseFullMineBlock(p, Blocks.DEEPSLATE_LAPIS_ORE), mineProp(Blocks.DEEPSLATE_LAPIS_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> DIAMOND_ORE_MINE = BLOCKS.registerBlock("diamond_mine", p -> new BaseFullMineBlock(p, Blocks.DIAMOND_ORE), mineProp(Blocks.DIAMOND_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> DEEPSLATE_DIAMOND_ORE_MINE = BLOCKS.registerBlock("deepslate_diamond_mine", p -> new BaseFullMineBlock(p, Blocks.DEEPSLATE_DIAMOND_ORE), mineProp(Blocks.DEEPSLATE_DIAMOND_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> NETHER_GOLD_ORE_MINE = BLOCKS.registerBlock("nether_gold_mine", p -> new BaseFullMineBlock(p, Blocks.NETHER_GOLD_ORE), mineProp(Blocks.NETHER_GOLD_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> QUARTZ_ORE_MINE = BLOCKS.registerBlock("quartz_mine", p -> new BaseFullMineBlock(p, Blocks.NETHER_QUARTZ_ORE), mineProp(Blocks.NETHER_QUARTZ_ORE));
	@HasManualPage(PageGroup.BLOCK_MINES)
	public static final DeferredBlock<BaseFullMineBlock> ANCIENT_DEBRIS_MINE = BLOCKS.registerBlock("ancient_debris_mine", p -> new BaseFullMineBlock(p, Blocks.ANCIENT_DEBRIS), mineProp(Blocks.ANCIENT_DEBRIS));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> GILDED_BLACKSTONE_MINE = BLOCKS.registerBlock("gilded_blackstone_mine", p -> new BaseFullMineBlock(p, Blocks.GILDED_BLACKSTONE), mineProp(Blocks.GILDED_BLACKSTONE));
	@HasManualPage(PageGroup.FURNACE_MINES)
	@OwnableBE
	@RegisterItemBlock
	public static final DeferredBlock<FurnaceMineBlock> FURNACE_MINE = BLOCKS.registerBlock("furnace_mine", p -> new FurnaceMineBlock(p, Blocks.FURNACE), prop(MapColor.STONE, 3.5F).requiresCorrectToolForDrops());
	@HasManualPage(PageGroup.FURNACE_MINES)
	@OwnableBE
	@RegisterItemBlock
	public static final DeferredBlock<FurnaceMineBlock> SMOKER_MINE = BLOCKS.registerBlock("smoker_mine", p -> new FurnaceMineBlock(p, Blocks.SMOKER), prop(MapColor.STONE, 3.5F).requiresCorrectToolForDrops());
	@HasManualPage(PageGroup.FURNACE_MINES)
	@OwnableBE
	@RegisterItemBlock
	public static final DeferredBlock<FurnaceMineBlock> BLAST_FURNACE_MINE = BLOCKS.registerBlock("blast_furnace_mine", p -> new FurnaceMineBlock(p, Blocks.BLAST_FURNACE), prop(MapColor.STONE, 3.5F).requiresCorrectToolForDrops());
	@HasManualPage(PageGroup.BLOCK_MINES)
	@OwnableBE
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BrushableMineBlock> SUSPICIOUS_SAND_MINE = BLOCKS.registerBlock("suspicious_sand_mine", p -> new BrushableMineBlock(p, Blocks.SUSPICIOUS_SAND), mineProp(Blocks.SUSPICIOUS_SAND).pushReaction(PushReaction.DESTROY));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@OwnableBE
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BrushableMineBlock> SUSPICIOUS_GRAVEL_MINE = BLOCKS.registerBlock("suspicious_gravel_mine", p -> new BrushableMineBlock(p, Blocks.SUSPICIOUS_GRAVEL), mineProp(Blocks.SUSPICIOUS_GRAVEL));

	//reinforced blocks (ordered by vanilla <1.19.3 building blocks creative tab order)
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_STONE = BLOCKS.registerBlock("reinforced_stone", p -> new BaseReinforcedBlock(p, Blocks.STONE), prop());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_GRANITE = BLOCKS.registerBlock("reinforced_granite", p -> new BaseReinforcedBlock(p, Blocks.GRANITE), prop(MapColor.DIRT));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_POLISHED_GRANITE = BLOCKS.registerBlock("reinforced_polished_granite", p -> new BaseReinforcedBlock(p, Blocks.POLISHED_GRANITE), prop(MapColor.DIRT));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_DIORITE = BLOCKS.registerBlock("reinforced_diorite", p -> new BaseReinforcedBlock(p, Blocks.DIORITE), prop(MapColor.QUARTZ));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_POLISHED_DIORITE = BLOCKS.registerBlock("reinforced_polished_diorite", p -> new BaseReinforcedBlock(p, Blocks.POLISHED_DIORITE), prop(MapColor.QUARTZ));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_ANDESITE = BLOCKS.registerBlock("reinforced_andesite", p -> new BaseReinforcedBlock(p, Blocks.ANDESITE), prop());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_POLISHED_ANDESITE = BLOCKS.registerBlock("reinforced_polished_andesite", p -> new BaseReinforcedBlock(p, Blocks.POLISHED_ANDESITE), prop());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_DEEPSLATE = BLOCKS.registerBlock("reinforced_deepslate", p -> new ReinforcedRotatedPillarBlock(p, Blocks.DEEPSLATE), prop(MapColor.DEEPSLATE).sound(SoundType.DEEPSLATE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_COBBLED_DEEPSLATE = BLOCKS.registerBlock("reinforced_cobbled_deepslate", p -> new BaseReinforcedBlock(p, Blocks.COBBLED_DEEPSLATE), prop(MapColor.DEEPSLATE).sound(SoundType.DEEPSLATE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_POLISHED_DEEPSLATE = BLOCKS.registerBlock("reinforced_polished_deepslate", p -> new BaseReinforcedBlock(p, Blocks.POLISHED_DEEPSLATE), prop(MapColor.DEEPSLATE).sound(SoundType.POLISHED_DEEPSLATE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CALCITE = BLOCKS.registerBlock("reinforced_calcite", p -> new BaseReinforcedBlock(p, Blocks.CALCITE), prop(MapColor.TERRACOTTA_WHITE).sound(SoundType.CALCITE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_TUFF = BLOCKS.registerBlock("reinforced_tuff", p -> new BaseReinforcedBlock(p, Blocks.TUFF), prop(MapColor.TERRACOTTA_GRAY).sound(SoundType.TUFF));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_DRIPSTONE_BLOCK = BLOCKS.registerBlock("reinforced_dripstone_block", p -> new BaseReinforcedBlock(p, Blocks.DRIPSTONE_BLOCK), prop(MapColor.TERRACOTTA_BROWN).sound(SoundType.DRIPSTONE_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGrassBlock> REINFORCED_GRASS_BLOCK = BLOCKS.registerBlock("reinforced_grass_block", ReinforcedGrassBlock::new, prop(MapColor.GRASS).sound(SoundType.GRASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_DIRT = BLOCKS.registerBlock("reinforced_dirt", p -> new BaseReinforcedBlock(p, Blocks.DIRT), prop(MapColor.DIRT).sound(SoundType.GRAVEL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_COARSE_DIRT = BLOCKS.registerBlock("reinforced_coarse_dirt", p -> new BaseReinforcedBlock(p, Blocks.COARSE_DIRT), prop(MapColor.DIRT).sound(SoundType.GRAVEL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSnowyDirtBlock> REINFORCED_PODZOL = BLOCKS.registerBlock("reinforced_podzol", p -> new ReinforcedSnowyDirtBlock(p, Blocks.PODZOL), prop(MapColor.PODZOL).sound(SoundType.GRAVEL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedMud> REINFORCED_MUD = BLOCKS.registerBlock("reinforced_mud", p -> new ReinforcedMud(p, Blocks.MUD), prop(MapColor.TERRACOTTA_CYAN).isValidSpawn(SCContent::always).isRedstoneConductor(SCContent::always).isViewBlocking(SCContent::always).isSuffocating(SCContent::always).sound(SoundType.MUD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedNyliumBlock> REINFORCED_CRIMSON_NYLIUM = BLOCKS.registerBlock("reinforced_crimson_nylium", p -> new ReinforcedNyliumBlock(p, Blocks.CRIMSON_NYLIUM), prop(MapColor.CRIMSON_NYLIUM).sound(SoundType.NYLIUM));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedNyliumBlock> REINFORCED_WARPED_NYLIUM = BLOCKS.registerBlock("reinforced_warped_nylium", p -> new ReinforcedNyliumBlock(p, Blocks.WARPED_NYLIUM), prop(MapColor.WARPED_NYLIUM).sound(SoundType.NYLIUM));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRootedDirtBlock> REINFORCED_ROOTED_DIRT = BLOCKS.registerBlock("reinforced_rooted_dirt", p -> new ReinforcedRootedDirtBlock(p, Blocks.ROOTED_DIRT), prop(MapColor.DIRT).sound(SoundType.ROOTED_DIRT));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_COBBLESTONE = BLOCKS.registerBlock("reinforced_cobblestone", p -> new BaseReinforcedBlock(p, Blocks.COBBLESTONE), prop());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_OAK_PLANKS = BLOCKS.registerBlock("reinforced_oak_planks", p -> new BaseReinforcedBlock(p, Blocks.OAK_PLANKS), prop(MapColor.WOOD).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_SPRUCE_PLANKS = BLOCKS.registerBlock("reinforced_spruce_planks", p -> new BaseReinforcedBlock(p, Blocks.SPRUCE_PLANKS), prop(MapColor.PODZOL).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BIRCH_PLANKS = BLOCKS.registerBlock("reinforced_birch_planks", p -> new BaseReinforcedBlock(p, Blocks.BIRCH_PLANKS), prop(MapColor.SAND).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_JUNGLE_PLANKS = BLOCKS.registerBlock("reinforced_jungle_planks", p -> new BaseReinforcedBlock(p, Blocks.JUNGLE_PLANKS), prop(MapColor.DIRT).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_ACACIA_PLANKS = BLOCKS.registerBlock("reinforced_acacia_planks", p -> new BaseReinforcedBlock(p, Blocks.ACACIA_PLANKS), prop(MapColor.COLOR_ORANGE).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_DARK_OAK_PLANKS = BLOCKS.registerBlock("reinforced_dark_oak_planks", p -> new BaseReinforcedBlock(p, Blocks.DARK_OAK_PLANKS), prop(MapColor.COLOR_BROWN).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_MANGROVE_PLANKS = BLOCKS.registerBlock("reinforced_mangrove_planks", p -> new BaseReinforcedBlock(p, Blocks.MANGROVE_PLANKS), prop(MapColor.COLOR_RED).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CRIMSON_PLANKS = BLOCKS.registerBlock("reinforced_crimson_planks", p -> new BaseReinforcedBlock(p, Blocks.CRIMSON_PLANKS), prop(MapColor.CRIMSON_STEM).sound(SoundType.NETHER_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_WARPED_PLANKS = BLOCKS.registerBlock("reinforced_warped_planks", p -> new BaseReinforcedBlock(p, Blocks.WARPED_PLANKS), prop(MapColor.WARPED_STEM).sound(SoundType.NETHER_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFallingBlock> REINFORCED_SAND = BLOCKS.registerBlock("reinforced_sand", p -> new ReinforcedFallingBlock(p, Blocks.SAND), prop(MapColor.SAND).sound(SoundType.SAND));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFallingBlock> REINFORCED_RED_SAND = BLOCKS.registerBlock("reinforced_red_sand", p -> new ReinforcedFallingBlock(p, Blocks.RED_SAND), prop(MapColor.COLOR_ORANGE).sound(SoundType.SAND));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFallingBlock> REINFORCED_GRAVEL = BLOCKS.registerBlock("reinforced_gravel", p -> new ReinforcedFallingBlock(p, Blocks.GRAVEL), prop().sound(SoundType.GRAVEL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_COAL_BLOCK = BLOCKS.registerBlock("reinforced_coal_block", p -> new BaseReinforcedBlock(p, Blocks.COAL_BLOCK), prop(MapColor.COLOR_BLACK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_RAW_IRON_BLOCK = BLOCKS.registerBlock("reinforced_raw_iron_block", p -> new BaseReinforcedBlock(p, Blocks.RAW_IRON_BLOCK), prop(MapColor.RAW_IRON));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_RAW_COPPER_BLOCK = BLOCKS.registerBlock("reinforced_raw_copper_block", p -> new BaseReinforcedBlock(p, Blocks.RAW_COPPER_BLOCK), prop(MapColor.COLOR_ORANGE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_RAW_GOLD_BLOCK = BLOCKS.registerBlock("reinforced_raw_gold_block", p -> new BaseReinforcedBlock(p, Blocks.RAW_GOLD_BLOCK), prop(MapColor.GOLD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedAmethystBlock> REINFORCED_AMETHYST_BLOCK = BLOCKS.registerBlock("reinforced_amethyst_block", p -> new ReinforcedAmethystBlock(p, Blocks.AMETHYST_BLOCK), prop(MapColor.COLOR_PURPLE).sound(SoundType.AMETHYST));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_IRON_BLOCK = BLOCKS.registerBlock("reinforced_iron_block", p -> new BaseReinforcedBlock(p, Blocks.IRON_BLOCK), prop(MapColor.METAL).sound(SoundType.METAL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_COPPER_BLOCK = BLOCKS.registerBlock("reinforced_copper_block", p -> new BaseReinforcedBlock(p, Blocks.COPPER_BLOCK), prop(MapColor.COLOR_ORANGE).sound(SoundType.COPPER));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_GOLD_BLOCK = BLOCKS.registerBlock("reinforced_gold_block", p -> new BaseReinforcedBlock(p, Blocks.GOLD_BLOCK), prop(MapColor.GOLD).sound(SoundType.METAL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_DIAMOND_BLOCK = BLOCKS.registerBlock("reinforced_diamond_block", p -> new BaseReinforcedBlock(p, Blocks.DIAMOND_BLOCK), prop(MapColor.DIAMOND).sound(SoundType.METAL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_NETHERITE_BLOCK = BLOCKS.registerBlock("reinforced_netherite_block", p -> new BaseReinforcedBlock(p, Blocks.NETHERITE_BLOCK), prop(MapColor.COLOR_BLACK).sound(SoundType.NETHERITE_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_EXPOSED_COPPER = BLOCKS.registerBlock("reinforced_exposed_copper", p -> new BaseReinforcedBlock(p, Blocks.EXPOSED_COPPER), prop(MapColor.TERRACOTTA_LIGHT_GRAY).sound(SoundType.COPPER));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_WEATHERED_COPPER = BLOCKS.registerBlock("reinforced_weathered_copper", p -> new BaseReinforcedBlock(p, Blocks.WEATHERED_COPPER), prop(MapColor.WARPED_STEM).sound(SoundType.COPPER));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_OXIDIZED_COPPER = BLOCKS.registerBlock("reinforced_oxidized_copper", p -> new BaseReinforcedBlock(p, Blocks.OXIDIZED_COPPER), prop(MapColor.WARPED_NYLIUM).sound(SoundType.COPPER));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CUT_COPPER = BLOCKS.registerBlock("reinforced_cut_copper", p -> new BaseReinforcedBlock(p, Blocks.CUT_COPPER), prop(MapColor.COLOR_ORANGE).sound(SoundType.COPPER));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_EXPOSED_CUT_COPPER = BLOCKS.registerBlock("reinforced_exposed_cut_copper", p -> new BaseReinforcedBlock(p, Blocks.EXPOSED_CUT_COPPER), prop(MapColor.TERRACOTTA_LIGHT_GRAY).sound(SoundType.COPPER));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_WEATHERED_CUT_COPPER = BLOCKS.registerBlock("reinforced_weathered_cut_copper", p -> new BaseReinforcedBlock(p, Blocks.WEATHERED_CUT_COPPER), prop(MapColor.WARPED_STEM).sound(SoundType.COPPER));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_OXIDIZED_CUT_COPPER = BLOCKS.registerBlock("reinforced_oxidized_cut_copper", p -> new BaseReinforcedBlock(p, Blocks.OXIDIZED_CUT_COPPER), prop(MapColor.WARPED_NYLIUM).sound(SoundType.COPPER));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_CUT_COPPER_STAIRS = BLOCKS.registerBlock("reinforced_cut_copper_stairs", p -> new ReinforcedStairsBlock(p, Blocks.CUT_COPPER_STAIRS), prop(MapColor.COLOR_ORANGE).sound(SoundType.COPPER));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_EXPOSED_CUT_COPPER_STAIRS = BLOCKS.registerBlock("reinforced_exposed_cut_copper_stairs", p -> new ReinforcedStairsBlock(p, Blocks.EXPOSED_CUT_COPPER_STAIRS), prop(MapColor.TERRACOTTA_LIGHT_GRAY).sound(SoundType.COPPER));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_WEATHERED_CUT_COPPER_STAIRS = BLOCKS.registerBlock("reinforced_weathered_cut_copper_stairs", p -> new ReinforcedStairsBlock(p, Blocks.WEATHERED_CUT_COPPER_STAIRS), prop(MapColor.WARPED_STEM).sound(SoundType.COPPER));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_OXIDIZED_CUT_COPPER_STAIRS = BLOCKS.registerBlock("reinforced_oxidized_cut_copper_stairs", p -> new ReinforcedStairsBlock(p, Blocks.OXIDIZED_CUT_COPPER_STAIRS), prop(MapColor.WARPED_NYLIUM).sound(SoundType.COPPER));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_CUT_COPPER_SLAB = BLOCKS.registerBlock("reinforced_cut_copper_slab", p -> new ReinforcedSlabBlock(p, Blocks.CUT_COPPER_SLAB), prop(MapColor.COLOR_ORANGE).sound(SoundType.COPPER));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_EXPOSED_CUT_COPPER_SLAB = BLOCKS.registerBlock("reinforced_exposed_cut_copper_slab", p -> new ReinforcedSlabBlock(p, Blocks.EXPOSED_CUT_COPPER_SLAB), prop(MapColor.TERRACOTTA_LIGHT_GRAY).sound(SoundType.COPPER));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_WEATHERED_CUT_COPPER_SLAB = BLOCKS.registerBlock("reinforced_weathered_cut_copper_slab", p -> new ReinforcedSlabBlock(p, Blocks.WEATHERED_CUT_COPPER_SLAB), prop(MapColor.WARPED_STEM).sound(SoundType.COPPER));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_OXIDIZED_CUT_COPPER_SLAB = BLOCKS.registerBlock("reinforced_oxidized_cut_copper_slab", p -> new ReinforcedSlabBlock(p, Blocks.OXIDIZED_CUT_COPPER_SLAB), prop(MapColor.WARPED_NYLIUM).sound(SoundType.COPPER));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_OAK_LOG = BLOCKS.registerBlock("reinforced_oak_log", p -> new ReinforcedRotatedPillarBlock(p, Blocks.OAK_LOG), logProp(MapColor.WOOD, MapColor.PODZOL).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_SPRUCE_LOG = BLOCKS.registerBlock("reinforced_spruce_log", p -> new ReinforcedRotatedPillarBlock(p, Blocks.SPRUCE_LOG), logProp(MapColor.PODZOL, MapColor.COLOR_BROWN).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_BIRCH_LOG = BLOCKS.registerBlock("reinforced_birch_log", p -> new ReinforcedRotatedPillarBlock(p, Blocks.BIRCH_LOG), logProp(MapColor.SAND, MapColor.QUARTZ).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_JUNGLE_LOG = BLOCKS.registerBlock("reinforced_jungle_log", p -> new ReinforcedRotatedPillarBlock(p, Blocks.JUNGLE_LOG), logProp(MapColor.DIRT, MapColor.PODZOL).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_ACACIA_LOG = BLOCKS.registerBlock("reinforced_acacia_log", p -> new ReinforcedRotatedPillarBlock(p, Blocks.ACACIA_LOG), logProp(MapColor.COLOR_ORANGE, MapColor.STONE).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_DARK_OAK_LOG = BLOCKS.registerBlock("reinforced_dark_oak_log", p -> new ReinforcedRotatedPillarBlock(p, Blocks.DARK_OAK_LOG), prop(MapColor.COLOR_BROWN).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_MANGROVE_LOG = BLOCKS.registerBlock("reinforced_mangrove_log", p -> new ReinforcedRotatedPillarBlock(p, Blocks.MANGROVE_LOG), logProp(MapColor.COLOR_RED, MapColor.PODZOL).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_CRIMSON_STEM = BLOCKS.registerBlock("reinforced_crimson_stem", p -> new ReinforcedRotatedPillarBlock(p, Blocks.CRIMSON_STEM), prop(MapColor.CRIMSON_STEM).sound(SoundType.STEM));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_WARPED_STEM = BLOCKS.registerBlock("reinforced_warped_stem", p -> new ReinforcedRotatedPillarBlock(p, Blocks.WARPED_STEM), prop(MapColor.WARPED_STEM).sound(SoundType.STEM));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_OAK_LOG = BLOCKS.registerBlock("reinforced_stripped_oak_log", p -> new ReinforcedRotatedPillarBlock(p, Blocks.STRIPPED_OAK_LOG), prop(MapColor.WOOD).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_SPRUCE_LOG = BLOCKS.registerBlock("reinforced_stripped_spruce_log", p -> new ReinforcedRotatedPillarBlock(p, Blocks.STRIPPED_SPRUCE_LOG), prop(MapColor.PODZOL).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_BIRCH_LOG = BLOCKS.registerBlock("reinforced_stripped_birch_log", p -> new ReinforcedRotatedPillarBlock(p, Blocks.STRIPPED_BIRCH_LOG), prop(MapColor.SAND).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_JUNGLE_LOG = BLOCKS.registerBlock("reinforced_stripped_jungle_log", p -> new ReinforcedRotatedPillarBlock(p, Blocks.STRIPPED_JUNGLE_LOG), prop(MapColor.DIRT).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_ACACIA_LOG = BLOCKS.registerBlock("reinforced_stripped_acacia_log", p -> new ReinforcedRotatedPillarBlock(p, Blocks.STRIPPED_ACACIA_LOG), prop(MapColor.COLOR_ORANGE).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_DARK_OAK_LOG = BLOCKS.registerBlock("reinforced_stripped_dark_oak_log", p -> new ReinforcedRotatedPillarBlock(p, Blocks.STRIPPED_DARK_OAK_LOG), prop(MapColor.COLOR_BROWN).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_MANGROVE_LOG = BLOCKS.registerBlock("reinforced_stripped_mangrove_log", p -> new ReinforcedRotatedPillarBlock(p, Blocks.STRIPPED_MANGROVE_LOG), prop(MapColor.COLOR_RED).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_CRIMSON_STEM = BLOCKS.registerBlock("reinforced_stripped_crimson_stem", p -> new ReinforcedRotatedPillarBlock(p, Blocks.STRIPPED_CRIMSON_STEM), prop(MapColor.CRIMSON_STEM).sound(SoundType.STEM));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_WARPED_STEM = BLOCKS.registerBlock("reinforced_stripped_warped_stem", p -> new ReinforcedRotatedPillarBlock(p, Blocks.STRIPPED_WARPED_STEM), prop(MapColor.WARPED_STEM).sound(SoundType.STEM));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_OAK_WOOD = BLOCKS.registerBlock("reinforced_stripped_oak_wood", p -> new ReinforcedRotatedPillarBlock(p, Blocks.STRIPPED_OAK_WOOD), prop(MapColor.WOOD).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_SPRUCE_WOOD = BLOCKS.registerBlock("reinforced_stripped_spruce_wood", p -> new ReinforcedRotatedPillarBlock(p, Blocks.STRIPPED_SPRUCE_WOOD), prop(MapColor.PODZOL).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_BIRCH_WOOD = BLOCKS.registerBlock("reinforced_stripped_birch_wood", p -> new ReinforcedRotatedPillarBlock(p, Blocks.STRIPPED_BIRCH_WOOD), prop(MapColor.SAND).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_JUNGLE_WOOD = BLOCKS.registerBlock("reinforced_stripped_jungle_wood", p -> new ReinforcedRotatedPillarBlock(p, Blocks.STRIPPED_JUNGLE_WOOD), prop(MapColor.DIRT).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_ACACIA_WOOD = BLOCKS.registerBlock("reinforced_stripped_acacia_wood", p -> new ReinforcedRotatedPillarBlock(p, Blocks.STRIPPED_ACACIA_WOOD), prop(MapColor.COLOR_ORANGE).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_DARK_OAK_WOOD = BLOCKS.registerBlock("reinforced_stripped_dark_oak_wood", p -> new ReinforcedRotatedPillarBlock(p, Blocks.STRIPPED_DARK_OAK_WOOD), prop(MapColor.COLOR_BROWN).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_MANGROVE_WOOD = BLOCKS.registerBlock("reinforced_stripped_mangrove_wood", p -> new ReinforcedRotatedPillarBlock(p, Blocks.STRIPPED_MANGROVE_WOOD), prop(MapColor.COLOR_RED).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_CRIMSON_HYPHAE = BLOCKS.registerBlock("reinforced_stripped_crimson_hyphae", p -> new ReinforcedRotatedPillarBlock(p, Blocks.STRIPPED_CRIMSON_HYPHAE), prop(MapColor.CRIMSON_HYPHAE).sound(SoundType.STEM));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_WARPED_HYPHAE = BLOCKS.registerBlock("reinforced_stripped_warped_hyphae", p -> new ReinforcedRotatedPillarBlock(p, Blocks.STRIPPED_WARPED_HYPHAE), prop(MapColor.WARPED_HYPHAE).sound(SoundType.STEM));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_OAK_WOOD = BLOCKS.registerBlock("reinforced_oak_wood", p -> new ReinforcedRotatedPillarBlock(p, Blocks.OAK_WOOD), prop(MapColor.WOOD).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_SPRUCE_WOOD = BLOCKS.registerBlock("reinforced_spruce_wood", p -> new ReinforcedRotatedPillarBlock(p, Blocks.SPRUCE_WOOD), prop(MapColor.PODZOL).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_BIRCH_WOOD = BLOCKS.registerBlock("reinforced_birch_wood", p -> new ReinforcedRotatedPillarBlock(p, Blocks.BIRCH_WOOD), prop(MapColor.SAND).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_JUNGLE_WOOD = BLOCKS.registerBlock("reinforced_jungle_wood", p -> new ReinforcedRotatedPillarBlock(p, Blocks.JUNGLE_WOOD), prop(MapColor.DIRT).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_ACACIA_WOOD = BLOCKS.registerBlock("reinforced_acacia_wood", p -> new ReinforcedRotatedPillarBlock(p, Blocks.ACACIA_WOOD), prop(MapColor.COLOR_GRAY).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_DARK_OAK_WOOD = BLOCKS.registerBlock("reinforced_dark_oak_wood", p -> new ReinforcedRotatedPillarBlock(p, Blocks.DARK_OAK_WOOD), prop(MapColor.COLOR_BROWN).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_MANGROVE_WOOD = BLOCKS.registerBlock("reinforced_mangrove_wood", p -> new ReinforcedRotatedPillarBlock(p, Blocks.MANGROVE_WOOD), prop(MapColor.COLOR_RED).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_CRIMSON_HYPHAE = BLOCKS.registerBlock("reinforced_crimson_hyphae", p -> new ReinforcedRotatedPillarBlock(p, Blocks.CRIMSON_HYPHAE), prop(MapColor.CRIMSON_HYPHAE).sound(SoundType.STEM));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_WARPED_HYPHAE = BLOCKS.registerBlock("reinforced_warped_hyphae", p -> new ReinforcedRotatedPillarBlock(p, Blocks.WARPED_HYPHAE), prop(MapColor.WARPED_HYPHAE).sound(SoundType.STEM));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedGlassBlock> REINFORCED_GLASS = BLOCKS.registerBlock("reinforced_glass", p -> new ReinforcedGlassBlock(p, Blocks.GLASS), glassProp(MapColor.NONE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedTintedGlassBlock> REINFORCED_TINTED_GLASS = BLOCKS.registerBlock("reinforced_tinted_glass", p -> new ReinforcedTintedGlassBlock(p, Blocks.TINTED_GLASS), glassProp(MapColor.COLOR_GRAY));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_LAPIS_BLOCK = BLOCKS.registerBlock("reinforced_lapis_block", p -> new BaseReinforcedBlock(p, Blocks.LAPIS_BLOCK), prop(MapColor.LAPIS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_SANDSTONE = BLOCKS.registerBlock("reinforced_sandstone", p -> new BaseReinforcedBlock(p, Blocks.SANDSTONE), prop(MapColor.SAND));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CHISELED_SANDSTONE = BLOCKS.registerBlock("reinforced_chiseled_sandstone", p -> new BaseReinforcedBlock(p, Blocks.CHISELED_SANDSTONE), prop(MapColor.SAND));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CUT_SANDSTONE = BLOCKS.registerBlock("reinforced_cut_sandstone", p -> new BaseReinforcedBlock(p, Blocks.CUT_SANDSTONE), prop(MapColor.SAND));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_WHITE_WOOL = BLOCKS.registerBlock("reinforced_white_wool", p -> new BaseReinforcedBlock(p, Blocks.WHITE_WOOL), prop(MapColor.SNOW).sound(SoundType.WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_ORANGE_WOOL = BLOCKS.registerBlock("reinforced_orange_wool", p -> new BaseReinforcedBlock(p, Blocks.ORANGE_WOOL), prop(MapColor.COLOR_ORANGE).sound(SoundType.WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_MAGENTA_WOOL = BLOCKS.registerBlock("reinforced_magenta_wool", p -> new BaseReinforcedBlock(p, Blocks.MAGENTA_WOOL), prop(MapColor.COLOR_MAGENTA).sound(SoundType.WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_LIGHT_BLUE_WOOL = BLOCKS.registerBlock("reinforced_light_blue_wool", p -> new BaseReinforcedBlock(p, Blocks.LIGHT_BLUE_WOOL), prop(MapColor.COLOR_LIGHT_BLUE).sound(SoundType.WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_YELLOW_WOOL = BLOCKS.registerBlock("reinforced_yellow_wool", p -> new BaseReinforcedBlock(p, Blocks.YELLOW_WOOL), prop(MapColor.COLOR_YELLOW).sound(SoundType.WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_LIME_WOOL = BLOCKS.registerBlock("reinforced_lime_wool", p -> new BaseReinforcedBlock(p, Blocks.LIME_WOOL), prop(MapColor.COLOR_LIGHT_GREEN).sound(SoundType.WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_PINK_WOOL = BLOCKS.registerBlock("reinforced_pink_wool", p -> new BaseReinforcedBlock(p, Blocks.PINK_WOOL), prop(MapColor.COLOR_PINK).sound(SoundType.WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_GRAY_WOOL = BLOCKS.registerBlock("reinforced_gray_wool", p -> new BaseReinforcedBlock(p, Blocks.GRAY_WOOL), prop(MapColor.COLOR_GRAY).sound(SoundType.WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_LIGHT_GRAY_WOOL = BLOCKS.registerBlock("reinforced_light_gray_wool", p -> new BaseReinforcedBlock(p, Blocks.LIGHT_GRAY_WOOL), prop(MapColor.COLOR_LIGHT_GRAY).sound(SoundType.WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CYAN_WOOL = BLOCKS.registerBlock("reinforced_cyan_wool", p -> new BaseReinforcedBlock(p, Blocks.CYAN_WOOL), prop(MapColor.COLOR_CYAN).sound(SoundType.WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_PURPLE_WOOL = BLOCKS.registerBlock("reinforced_purple_wool", p -> new BaseReinforcedBlock(p, Blocks.PURPLE_WOOL), prop(MapColor.COLOR_PURPLE).sound(SoundType.WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BLUE_WOOL = BLOCKS.registerBlock("reinforced_blue_wool", p -> new BaseReinforcedBlock(p, Blocks.BLUE_WOOL), prop(MapColor.COLOR_BLUE).sound(SoundType.WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BROWN_WOOL = BLOCKS.registerBlock("reinforced_brown_wool", p -> new BaseReinforcedBlock(p, Blocks.BROWN_WOOL), prop(MapColor.COLOR_BROWN).sound(SoundType.WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_GREEN_WOOL = BLOCKS.registerBlock("reinforced_green_wool", p -> new BaseReinforcedBlock(p, Blocks.GREEN_WOOL), prop(MapColor.COLOR_GREEN).sound(SoundType.WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_RED_WOOL = BLOCKS.registerBlock("reinforced_red_wool", p -> new BaseReinforcedBlock(p, Blocks.RED_WOOL), prop(MapColor.COLOR_RED).sound(SoundType.WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BLACK_WOOL = BLOCKS.registerBlock("reinforced_black_wool", p -> new BaseReinforcedBlock(p, Blocks.BLACK_WOOL), prop(MapColor.COLOR_BLACK).sound(SoundType.WOOL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_OAK_SLAB = BLOCKS.registerBlock("reinforced_oak_slab", p -> new ReinforcedSlabBlock(p, Blocks.OAK_SLAB), prop(MapColor.WOOD).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_SPRUCE_SLAB = BLOCKS.registerBlock("reinforced_spruce_slab", p -> new ReinforcedSlabBlock(p, Blocks.SPRUCE_SLAB), prop(MapColor.PODZOL).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_BIRCH_SLAB = BLOCKS.registerBlock("reinforced_birch_slab", p -> new ReinforcedSlabBlock(p, Blocks.BIRCH_SLAB), prop(MapColor.SAND).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_JUNGLE_SLAB = BLOCKS.registerBlock("reinforced_jungle_slab", p -> new ReinforcedSlabBlock(p, Blocks.JUNGLE_SLAB), prop(MapColor.DIRT).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_ACACIA_SLAB = BLOCKS.registerBlock("reinforced_acacia_slab", p -> new ReinforcedSlabBlock(p, Blocks.ACACIA_SLAB), prop(MapColor.COLOR_ORANGE).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_DARK_OAK_SLAB = BLOCKS.registerBlock("reinforced_dark_oak_slab", p -> new ReinforcedSlabBlock(p, Blocks.DARK_OAK_SLAB), prop(MapColor.COLOR_BROWN).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_MANGROVE_SLAB = BLOCKS.registerBlock("reinforced_mangrove_slab", p -> new ReinforcedSlabBlock(p, Blocks.MANGROVE_SLAB), prop(MapColor.COLOR_RED).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_CRIMSON_SLAB = BLOCKS.registerBlock("reinforced_crimson_slab", p -> new ReinforcedSlabBlock(p, Blocks.CRIMSON_SLAB), prop(MapColor.CRIMSON_STEM).sound(SoundType.NETHER_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_WARPED_SLAB = BLOCKS.registerBlock("reinforced_warped_slab", p -> new ReinforcedSlabBlock(p, Blocks.WARPED_SLAB), prop(MapColor.WARPED_STEM).sound(SoundType.NETHER_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_NORMAL_STONE_SLAB = BLOCKS.registerBlock("reinforced_normal_stone_slab", p -> new ReinforcedSlabBlock(p, Blocks.STONE_SLAB), prop());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_SMOOTH_STONE_SLAB = BLOCKS.registerBlock("reinforced_stone_slab", p -> new ReinforcedSlabBlock(p, Blocks.SMOOTH_STONE_SLAB), prop());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_SANDSTONE_SLAB = BLOCKS.registerBlock("reinforced_sandstone_slab", p -> new ReinforcedSlabBlock(p, Blocks.SANDSTONE_SLAB), prop(MapColor.SAND));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_CUT_SANDSTONE_SLAB = BLOCKS.registerBlock("reinforced_cut_sandstone_slab", p -> new ReinforcedSlabBlock(p, Blocks.CUT_SANDSTONE_SLAB), prop(MapColor.SAND));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_COBBLESTONE_SLAB = BLOCKS.registerBlock("reinforced_cobblestone_slab", p -> new ReinforcedSlabBlock(p, Blocks.COBBLESTONE_SLAB), prop());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_BRICK_SLAB = BLOCKS.registerBlock("reinforced_brick_slab", p -> new ReinforcedSlabBlock(p, Blocks.BRICK_SLAB), prop(MapColor.COLOR_RED));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_STONE_BRICK_SLAB = BLOCKS.registerBlock("reinforced_stone_brick_slab", p -> new ReinforcedSlabBlock(p, Blocks.STONE_BRICK_SLAB), prop());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_MUD_BRICK_SLAB = BLOCKS.registerBlock("reinforced_mud_brick_slab", p -> new ReinforcedSlabBlock(p, Blocks.MUD_BRICK_SLAB), prop(MapColor.TERRACOTTA_LIGHT_GRAY).sound(SoundType.MUD_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_NETHER_BRICK_SLAB = BLOCKS.registerBlock("reinforced_nether_brick_slab", p -> new ReinforcedSlabBlock(p, Blocks.NETHER_BRICK_SLAB), prop(MapColor.NETHER).sound(SoundType.NETHER_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_QUARTZ_SLAB = BLOCKS.registerBlock("reinforced_quartz_slab", p -> new ReinforcedSlabBlock(p, Blocks.QUARTZ_SLAB), prop(MapColor.QUARTZ));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_RED_SANDSTONE_SLAB = BLOCKS.registerBlock("reinforced_red_sandstone_slab", p -> new ReinforcedSlabBlock(p, Blocks.RED_SANDSTONE_SLAB), prop(MapColor.COLOR_ORANGE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_CUT_RED_SANDSTONE_SLAB = BLOCKS.registerBlock("reinforced_cut_red_sandstone_slab", p -> new ReinforcedSlabBlock(p, Blocks.CUT_RED_SANDSTONE_SLAB), prop(MapColor.COLOR_ORANGE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_PURPUR_SLAB = BLOCKS.registerBlock("reinforced_purpur_slab", p -> new ReinforcedSlabBlock(p, Blocks.PURPUR_SLAB), prop(MapColor.COLOR_MAGENTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_PRISMARINE_SLAB = BLOCKS.registerBlock("reinforced_prismarine_slab", p -> new ReinforcedSlabBlock(p, Blocks.PRISMARINE_SLAB), prop(MapColor.COLOR_CYAN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_PRISMARINE_BRICK_SLAB = BLOCKS.registerBlock("reinforced_prismarine_brick_slab", p -> new ReinforcedSlabBlock(p, Blocks.PRISMARINE_BRICK_SLAB), prop(MapColor.DIAMOND));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_DARK_PRISMARINE_SLAB = BLOCKS.registerBlock("reinforced_dark_prismarine_slab", p -> new ReinforcedSlabBlock(p, Blocks.DARK_PRISMARINE_SLAB), prop(MapColor.DIAMOND));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_SMOOTH_QUARTZ = BLOCKS.registerBlock("reinforced_smooth_quartz", p -> new BaseReinforcedBlock(p, Blocks.SMOOTH_QUARTZ), prop(MapColor.QUARTZ));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_SMOOTH_RED_SANDSTONE = BLOCKS.registerBlock("reinforced_smooth_red_sandstone", p -> new BaseReinforcedBlock(p, Blocks.SMOOTH_RED_SANDSTONE), prop(MapColor.COLOR_ORANGE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_SMOOTH_SANDSTONE = BLOCKS.registerBlock("reinforced_smooth_sandstone", p -> new BaseReinforcedBlock(p, Blocks.SMOOTH_SANDSTONE), prop(MapColor.SAND));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_SMOOTH_STONE = BLOCKS.registerBlock("reinforced_smooth_stone", p -> new BaseReinforcedBlock(p, Blocks.SMOOTH_STONE), prop());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BRICKS = BLOCKS.registerBlock("reinforced_bricks", p -> new BaseReinforcedBlock(p, Blocks.BRICKS), prop(MapColor.COLOR_RED));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BOOKSHELF = BLOCKS.registerBlock("reinforced_bookshelf", p -> new BaseReinforcedBlock(p, Blocks.BOOKSHELF), prop(MapColor.WOOD).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_MOSSY_COBBLESTONE = BLOCKS.registerBlock("reinforced_mossy_cobblestone", p -> new BaseReinforcedBlock(p, Blocks.MOSSY_COBBLESTONE), prop());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedObsidianBlock> REINFORCED_OBSIDIAN = BLOCKS.registerBlock("reinforced_obsidian", p -> new ReinforcedObsidianBlock(p, Blocks.OBSIDIAN), prop(MapColor.COLOR_BLACK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_PURPUR_BLOCK = BLOCKS.registerBlock("reinforced_purpur_block", p -> new BaseReinforcedBlock(p, Blocks.PURPUR_BLOCK), prop(MapColor.COLOR_MAGENTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_PURPUR_PILLAR = BLOCKS.registerBlock("reinforced_purpur_pillar", p -> new ReinforcedRotatedPillarBlock(p, Blocks.PURPUR_PILLAR), prop(MapColor.COLOR_MAGENTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_PURPUR_STAIRS = BLOCKS.registerBlock("reinforced_purpur_stairs", p -> new ReinforcedStairsBlock(p, Blocks.PURPUR_STAIRS), prop(MapColor.COLOR_MAGENTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_OAK_STAIRS = BLOCKS.registerBlock("reinforced_oak_stairs", p -> new ReinforcedStairsBlock(p, Blocks.OAK_STAIRS), prop(MapColor.WOOD).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_COBBLESTONE_STAIRS = BLOCKS.registerBlock("reinforced_cobblestone_stairs", p -> new ReinforcedStairsBlock(p, Blocks.COBBLESTONE_STAIRS), prop());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_ICE = BLOCKS.registerBlock("reinforced_ice", p -> new BaseReinforcedBlock(p, Blocks.ICE), prop(MapColor.ICE).friction(0.98F).sound(SoundType.GLASS).noOcclusion().isRedstoneConductor(SCContent::never).isValidSpawn((state, level, pos, type) -> type == EntityType.POLAR_BEAR));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_SNOW_BLOCK = BLOCKS.registerBlock("reinforced_snow_block", p -> new BaseReinforcedBlock(p, Blocks.SNOW_BLOCK), prop(MapColor.SNOW).sound(SoundType.SNOW));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CLAY = BLOCKS.registerBlock("reinforced_clay", p -> new BaseReinforcedBlock(p, Blocks.CLAY), prop(MapColor.CLAY).sound(SoundType.GRAVEL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_NETHERRACK = BLOCKS.registerBlock("reinforced_netherrack", p -> new BaseReinforcedBlock(p, Blocks.NETHERRACK), prop(MapColor.NETHER).sound(SoundType.NETHERRACK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSoulSandBlock> REINFORCED_SOUL_SAND = BLOCKS.registerBlock("reinforced_soul_sand", p -> new ReinforcedSoulSandBlock(p, Blocks.SOUL_SAND), reinforcedCopy(Blocks.SOUL_SAND));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_SOUL_SOIL = BLOCKS.registerBlock("reinforced_soul_soil", p -> new BaseReinforcedBlock(p, Blocks.SOUL_SOIL), prop(MapColor.COLOR_BROWN).sound(SoundType.SOUL_SOIL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_BASALT = BLOCKS.registerBlock("reinforced_basalt", p -> new ReinforcedRotatedPillarBlock(p, Blocks.BASALT), prop(MapColor.COLOR_BLACK).sound(SoundType.BASALT));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_POLISHED_BASALT = BLOCKS.registerBlock("reinforced_polished_basalt", p -> new ReinforcedRotatedPillarBlock(p, Blocks.POLISHED_BASALT), prop(MapColor.COLOR_BLACK).sound(SoundType.BASALT));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_SMOOTH_BASALT = BLOCKS.registerBlock("reinforced_smooth_basalt", p -> new BaseReinforcedBlock(p, Blocks.SMOOTH_BASALT), prop(MapColor.COLOR_BLACK).sound(SoundType.BASALT));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_GLOWSTONE = BLOCKS.registerBlock("reinforced_glowstone", p -> new BaseReinforcedBlock(p, Blocks.GLOWSTONE), prop(MapColor.SAND).sound(SoundType.GLASS).lightLevel(state -> 15).isRedstoneConductor(SCContent::never));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_STONE_BRICKS = BLOCKS.registerBlock("reinforced_stone_bricks", p -> new BaseReinforcedBlock(p, Blocks.STONE_BRICKS), prop());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_MOSSY_STONE_BRICKS = BLOCKS.registerBlock("reinforced_mossy_stone_bricks", p -> new BaseReinforcedBlock(p, Blocks.MOSSY_STONE_BRICKS), prop());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CRACKED_STONE_BRICKS = BLOCKS.registerBlock("reinforced_cracked_stone_bricks", p -> new BaseReinforcedBlock(p, Blocks.CRACKED_STONE_BRICKS), prop());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CHISELED_STONE_BRICKS = BLOCKS.registerBlock("reinforced_chiseled_stone_bricks", p -> new BaseReinforcedBlock(p, Blocks.CHISELED_STONE_BRICKS), prop());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_PACKED_MUD = BLOCKS.registerBlock("reinforced_packed_mud", p -> new BaseReinforcedBlock(p, Blocks.PACKED_MUD), prop(MapColor.DIRT).sound(SoundType.PACKED_MUD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_MUD_BRICKS = BLOCKS.registerBlock("reinforced_mud_bricks", p -> new BaseReinforcedBlock(p, Blocks.MUD_BRICKS), prop(MapColor.TERRACOTTA_LIGHT_GRAY).sound(SoundType.MUD_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_DEEPSLATE_BRICKS = BLOCKS.registerBlock("reinforced_deepslate_bricks", p -> new BaseReinforcedBlock(p, Blocks.DEEPSLATE_BRICKS), prop(MapColor.DEEPSLATE).sound(SoundType.DEEPSLATE_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CRACKED_DEEPSLATE_BRICKS = BLOCKS.registerBlock("reinforced_cracked_deepslate_bricks", p -> new BaseReinforcedBlock(p, Blocks.CRACKED_DEEPSLATE_BRICKS), prop(MapColor.DEEPSLATE).sound(SoundType.DEEPSLATE_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_DEEPSLATE_TILES = BLOCKS.registerBlock("reinforced_deepslate_tiles", p -> new BaseReinforcedBlock(p, Blocks.DEEPSLATE_TILES), prop(MapColor.DEEPSLATE).sound(SoundType.DEEPSLATE_TILES));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CRACKED_DEEPSLATE_TILES = BLOCKS.registerBlock("reinforced_cracked_deepslate_tiles", p -> new BaseReinforcedBlock(p, Blocks.CRACKED_DEEPSLATE_TILES), prop(MapColor.DEEPSLATE).sound(SoundType.DEEPSLATE_TILES));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CHISELED_DEEPSLATE = BLOCKS.registerBlock("reinforced_chiseled_deepslate", p -> new BaseReinforcedBlock(p, Blocks.CHISELED_DEEPSLATE), prop(MapColor.DEEPSLATE).sound(SoundType.DEEPSLATE_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_BRICK_STAIRS = BLOCKS.registerBlock("reinforced_brick_stairs", p -> new ReinforcedStairsBlock(p, Blocks.BRICK_STAIRS), prop(MapColor.COLOR_RED));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_STONE_BRICK_STAIRS = BLOCKS.registerBlock("reinforced_stone_brick_stairs", p -> new ReinforcedStairsBlock(p, Blocks.STONE_BRICK_STAIRS), prop());
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_MUD_BRICK_STAIRS = BLOCKS.registerBlock("reinforced_mud_brick_stairs", p -> new ReinforcedStairsBlock(p, Blocks.MUD_BRICK_STAIRS), prop(MapColor.TERRACOTTA_LIGHT_GRAY).sound(SoundType.MUD_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSnowyDirtBlock> REINFORCED_MYCELIUM = BLOCKS.registerBlock("reinforced_mycelium", p -> new ReinforcedSnowyDirtBlock(p, Blocks.MYCELIUM), prop(MapColor.COLOR_PURPLE).sound(SoundType.GRASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_NETHER_BRICKS = BLOCKS.registerBlock("reinforced_nether_bricks", p -> new BaseReinforcedBlock(p, Blocks.NETHER_BRICKS), prop(MapColor.NETHER).sound(SoundType.NETHER_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CRACKED_NETHER_BRICKS = BLOCKS.registerBlock("reinforced_cracked_nether_bricks", p -> new BaseReinforcedBlock(p, Blocks.CRACKED_NETHER_BRICKS), prop(MapColor.NETHER).sound(SoundType.NETHER_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CHISELED_NETHER_BRICKS = BLOCKS.registerBlock("reinforced_chiseled_nether_bricks", p -> new BaseReinforcedBlock(p, Blocks.CHISELED_NETHER_BRICKS), prop(MapColor.NETHER).sound(SoundType.NETHER_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_NETHER_BRICK_STAIRS = BLOCKS.registerBlock("reinforced_nether_brick_stairs", p -> new ReinforcedStairsBlock(p, Blocks.NETHER_BRICK_STAIRS), prop(MapColor.NETHER).sound(SoundType.NETHER_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_END_STONE = BLOCKS.registerBlock("reinforced_end_stone", p -> new BaseReinforcedBlock(p, Blocks.END_STONE), prop(MapColor.SAND));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_END_STONE_BRICKS = BLOCKS.registerBlock("reinforced_end_stone_bricks", p -> new BaseReinforcedBlock(p, Blocks.END_STONE_BRICKS), prop(MapColor.SAND));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_SANDSTONE_STAIRS = BLOCKS.registerBlock("reinforced_sandstone_stairs", p -> new ReinforcedStairsBlock(p, Blocks.SANDSTONE_STAIRS), prop(MapColor.SAND));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_EMERALD_BLOCK = BLOCKS.registerBlock("reinforced_emerald_block", p -> new BaseReinforcedBlock(p, Blocks.EMERALD_BLOCK), prop(MapColor.EMERALD).sound(SoundType.METAL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_SPRUCE_STAIRS = BLOCKS.registerBlock("reinforced_spruce_stairs", p -> new ReinforcedStairsBlock(p, Blocks.SPRUCE_STAIRS), prop(MapColor.PODZOL).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_BIRCH_STAIRS = BLOCKS.registerBlock("reinforced_birch_stairs", p -> new ReinforcedStairsBlock(p, Blocks.BIRCH_STAIRS), prop(MapColor.SAND).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_JUNGLE_STAIRS = BLOCKS.registerBlock("reinforced_jungle_stairs", p -> new ReinforcedStairsBlock(p, Blocks.JUNGLE_STAIRS), prop(MapColor.DIRT).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_CRIMSON_STAIRS = BLOCKS.registerBlock("reinforced_crimson_stairs", p -> new ReinforcedStairsBlock(p, Blocks.CRIMSON_STAIRS), prop(MapColor.CRIMSON_STEM).sound(SoundType.NETHER_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_WARPED_STAIRS = BLOCKS.registerBlock("reinforced_warped_stairs", p -> new ReinforcedStairsBlock(p, Blocks.WARPED_STAIRS), prop(MapColor.WARPED_STEM).sound(SoundType.NETHER_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CHISELED_QUARTZ = BLOCKS.registerBlock("reinforced_chiseled_quartz_block", p -> new BaseReinforcedBlock(p, Blocks.CHISELED_QUARTZ_BLOCK), prop(MapColor.QUARTZ));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_QUARTZ_BLOCK = BLOCKS.registerBlock("reinforced_quartz_block", p -> new BaseReinforcedBlock(p, Blocks.QUARTZ_BLOCK), prop(MapColor.QUARTZ));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_QUARTZ_BRICKS = BLOCKS.registerBlock("reinforced_quartz_bricks", p -> new BaseReinforcedBlock(p, Blocks.QUARTZ_BRICKS), prop(MapColor.QUARTZ));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_QUARTZ_PILLAR = BLOCKS.registerBlock("reinforced_quartz_pillar", p -> new ReinforcedRotatedPillarBlock(p, Blocks.QUARTZ_PILLAR), prop(MapColor.QUARTZ));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_QUARTZ_STAIRS = BLOCKS.registerBlock("reinforced_quartz_stairs", p -> new ReinforcedStairsBlock(p, Blocks.QUARTZ_STAIRS), prop(MapColor.QUARTZ));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_WHITE_TERRACOTTA = BLOCKS.registerBlock("reinforced_white_terracotta", p -> new BaseReinforcedBlock(p, Blocks.WHITE_TERRACOTTA), prop(MapColor.TERRACOTTA_WHITE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_ORANGE_TERRACOTTA = BLOCKS.registerBlock("reinforced_orange_terracotta", p -> new BaseReinforcedBlock(p, Blocks.ORANGE_TERRACOTTA), prop(MapColor.TERRACOTTA_ORANGE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_MAGENTA_TERRACOTTA = BLOCKS.registerBlock("reinforced_magenta_terracotta", p -> new BaseReinforcedBlock(p, Blocks.MAGENTA_TERRACOTTA), prop(MapColor.TERRACOTTA_MAGENTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_LIGHT_BLUE_TERRACOTTA = BLOCKS.registerBlock("reinforced_light_blue_terracotta", p -> new BaseReinforcedBlock(p, Blocks.LIGHT_BLUE_TERRACOTTA), prop(MapColor.TERRACOTTA_LIGHT_BLUE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_YELLOW_TERRACOTTA = BLOCKS.registerBlock("reinforced_yellow_terracotta", p -> new BaseReinforcedBlock(p, Blocks.YELLOW_TERRACOTTA), prop(MapColor.TERRACOTTA_YELLOW));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_LIME_TERRACOTTA = BLOCKS.registerBlock("reinforced_lime_terracotta", p -> new BaseReinforcedBlock(p, Blocks.LIME_TERRACOTTA), prop(MapColor.TERRACOTTA_LIGHT_GREEN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_PINK_TERRACOTTA = BLOCKS.registerBlock("reinforced_pink_terracotta", p -> new BaseReinforcedBlock(p, Blocks.PINK_TERRACOTTA), prop(MapColor.TERRACOTTA_PINK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_GRAY_TERRACOTTA = BLOCKS.registerBlock("reinforced_gray_terracotta", p -> new BaseReinforcedBlock(p, Blocks.GRAY_TERRACOTTA), prop(MapColor.TERRACOTTA_GRAY));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_LIGHT_GRAY_TERRACOTTA = BLOCKS.registerBlock("reinforced_light_gray_terracotta", p -> new BaseReinforcedBlock(p, Blocks.LIGHT_GRAY_TERRACOTTA), prop(MapColor.TERRACOTTA_LIGHT_GRAY));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CYAN_TERRACOTTA = BLOCKS.registerBlock("reinforced_cyan_terracotta", p -> new BaseReinforcedBlock(p, Blocks.CYAN_TERRACOTTA), prop(MapColor.TERRACOTTA_CYAN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_PURPLE_TERRACOTTA = BLOCKS.registerBlock("reinforced_purple_terracotta", p -> new BaseReinforcedBlock(p, Blocks.PURPLE_TERRACOTTA), prop(MapColor.TERRACOTTA_PURPLE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BLUE_TERRACOTTA = BLOCKS.registerBlock("reinforced_blue_terracotta", p -> new BaseReinforcedBlock(p, Blocks.BLUE_TERRACOTTA), prop(MapColor.TERRACOTTA_BLUE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BROWN_TERRACOTTA = BLOCKS.registerBlock("reinforced_brown_terracotta", p -> new BaseReinforcedBlock(p, Blocks.BROWN_TERRACOTTA), prop(MapColor.TERRACOTTA_BROWN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_GREEN_TERRACOTTA = BLOCKS.registerBlock("reinforced_green_terracotta", p -> new BaseReinforcedBlock(p, Blocks.GREEN_TERRACOTTA), prop(MapColor.TERRACOTTA_GREEN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_RED_TERRACOTTA = BLOCKS.registerBlock("reinforced_red_terracotta", p -> new BaseReinforcedBlock(p, Blocks.RED_TERRACOTTA), prop(MapColor.TERRACOTTA_RED));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BLACK_TERRACOTTA = BLOCKS.registerBlock("reinforced_black_terracotta", p -> new BaseReinforcedBlock(p, Blocks.BLACK_TERRACOTTA), prop(MapColor.TERRACOTTA_BLACK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_TERRACOTTA = BLOCKS.registerBlock("reinforced_hardened_clay", p -> new BaseReinforcedBlock(p, Blocks.TERRACOTTA), prop(MapColor.COLOR_ORANGE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_PACKED_ICE = BLOCKS.registerBlock("reinforced_packed_ice", p -> new BaseReinforcedBlock(p, Blocks.PACKED_ICE), prop(MapColor.ICE).sound(SoundType.GLASS).friction(0.98F));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_ACACIA_STAIRS = BLOCKS.registerBlock("reinforced_acacia_stairs", p -> new ReinforcedStairsBlock(p, Blocks.ACACIA_STAIRS), prop(MapColor.COLOR_ORANGE).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_DARK_OAK_STAIRS = BLOCKS.registerBlock("reinforced_dark_oak_stairs", p -> new ReinforcedStairsBlock(p, Blocks.DARK_OAK_STAIRS), prop(MapColor.COLOR_BROWN).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_MANGROVE_STAIRS = BLOCKS.registerBlock("reinforced_mangrove_stairs", p -> new ReinforcedStairsBlock(p, Blocks.MANGROVE_STAIRS), prop(MapColor.COLOR_RED).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_WHITE_STAINED_GLASS = BLOCKS.registerBlock("reinforced_white_stained_glass", p -> new ReinforcedStainedGlassBlock(p, DyeColor.WHITE, Blocks.WHITE_STAINED_GLASS), glassProp(MapColor.SNOW));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_ORANGE_STAINED_GLASS = BLOCKS.registerBlock("reinforced_orange_stained_glass", p -> new ReinforcedStainedGlassBlock(p, DyeColor.ORANGE, Blocks.ORANGE_STAINED_GLASS), glassProp(MapColor.COLOR_ORANGE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_MAGENTA_STAINED_GLASS = BLOCKS.registerBlock("reinforced_magenta_stained_glass", p -> new ReinforcedStainedGlassBlock(p, DyeColor.MAGENTA, Blocks.MAGENTA_STAINED_GLASS), glassProp(MapColor.COLOR_MAGENTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_LIGHT_BLUE_STAINED_GLASS = BLOCKS.registerBlock("reinforced_light_blue_stained_glass", p -> new ReinforcedStainedGlassBlock(p, DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_STAINED_GLASS), glassProp(MapColor.COLOR_LIGHT_BLUE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_YELLOW_STAINED_GLASS = BLOCKS.registerBlock("reinforced_yellow_stained_glass", p -> new ReinforcedStainedGlassBlock(p, DyeColor.YELLOW, Blocks.YELLOW_STAINED_GLASS), glassProp(MapColor.COLOR_YELLOW));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_LIME_STAINED_GLASS = BLOCKS.registerBlock("reinforced_lime_stained_glass", p -> new ReinforcedStainedGlassBlock(p, DyeColor.LIME, Blocks.LIME_STAINED_GLASS), glassProp(MapColor.COLOR_LIGHT_GREEN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_PINK_STAINED_GLASS = BLOCKS.registerBlock("reinforced_pink_stained_glass", p -> new ReinforcedStainedGlassBlock(p, DyeColor.PINK, Blocks.PINK_STAINED_GLASS), glassProp(MapColor.COLOR_PINK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_GRAY_STAINED_GLASS = BLOCKS.registerBlock("reinforced_gray_stained_glass", p -> new ReinforcedStainedGlassBlock(p, DyeColor.GRAY, Blocks.GRAY_STAINED_GLASS), glassProp(MapColor.COLOR_GRAY));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_LIGHT_GRAY_STAINED_GLASS = BLOCKS.registerBlock("reinforced_light_gray_stained_glass", p -> new ReinforcedStainedGlassBlock(p, DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_STAINED_GLASS), glassProp(MapColor.COLOR_LIGHT_GRAY));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_CYAN_STAINED_GLASS = BLOCKS.registerBlock("reinforced_cyan_stained_glass", p -> new ReinforcedStainedGlassBlock(p, DyeColor.CYAN, Blocks.CYAN_STAINED_GLASS), glassProp(MapColor.COLOR_CYAN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_PURPLE_STAINED_GLASS = BLOCKS.registerBlock("reinforced_purple_stained_glass", p -> new ReinforcedStainedGlassBlock(p, DyeColor.PURPLE, Blocks.PURPLE_STAINED_GLASS), glassProp(MapColor.COLOR_PURPLE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_BLUE_STAINED_GLASS = BLOCKS.registerBlock("reinforced_blue_stained_glass", p -> new ReinforcedStainedGlassBlock(p, DyeColor.BLUE, Blocks.BLUE_STAINED_GLASS), glassProp(MapColor.COLOR_BLUE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_BROWN_STAINED_GLASS = BLOCKS.registerBlock("reinforced_brown_stained_glass", p -> new ReinforcedStainedGlassBlock(p, DyeColor.BROWN, Blocks.BROWN_STAINED_GLASS), glassProp(MapColor.COLOR_BROWN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_GREEN_STAINED_GLASS = BLOCKS.registerBlock("reinforced_green_stained_glass", p -> new ReinforcedStainedGlassBlock(p, DyeColor.GREEN, Blocks.GREEN_STAINED_GLASS), glassProp(MapColor.COLOR_GREEN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_RED_STAINED_GLASS = BLOCKS.registerBlock("reinforced_red_stained_glass", p -> new ReinforcedStainedGlassBlock(p, DyeColor.RED, Blocks.RED_STAINED_GLASS), glassProp(MapColor.COLOR_RED));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_BLACK_STAINED_GLASS = BLOCKS.registerBlock("reinforced_black_stained_glass", p -> new ReinforcedStainedGlassBlock(p, DyeColor.BLACK, Blocks.BLACK_STAINED_GLASS), glassProp(MapColor.COLOR_BLACK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_PRISMARINE = BLOCKS.registerBlock("reinforced_prismarine", p -> new BaseReinforcedBlock(p, Blocks.PRISMARINE), prop(MapColor.COLOR_CYAN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_PRISMARINE_BRICKS = BLOCKS.registerBlock("reinforced_prismarine_bricks", p -> new BaseReinforcedBlock(p, Blocks.PRISMARINE_BRICKS), prop(MapColor.DIAMOND));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_DARK_PRISMARINE = BLOCKS.registerBlock("reinforced_dark_prismarine", p -> new BaseReinforcedBlock(p, Blocks.DARK_PRISMARINE), prop(MapColor.DIAMOND));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_PRISMARINE_STAIRS = BLOCKS.registerBlock("reinforced_prismarine_stairs", p -> new ReinforcedStairsBlock(p, Blocks.PRISMARINE_STAIRS), prop(MapColor.COLOR_CYAN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_PRISMARINE_BRICK_STAIRS = BLOCKS.registerBlock("reinforced_prismarine_brick_stairs", p -> new ReinforcedStairsBlock(p, Blocks.PRISMARINE_BRICK_STAIRS), prop(MapColor.DIAMOND));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_DARK_PRISMARINE_STAIRS = BLOCKS.registerBlock("reinforced_dark_prismarine_stairs", p -> new ReinforcedStairsBlock(p, Blocks.DARK_PRISMARINE_STAIRS), prop(MapColor.DIAMOND));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_SEA_LANTERN = BLOCKS.registerBlock("reinforced_sea_lantern", p -> new BaseReinforcedBlock(p, Blocks.SEA_LANTERN), prop(MapColor.QUARTZ).sound(SoundType.GLASS).lightLevel(state -> 15).isRedstoneConductor(SCContent::never));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_RED_SANDSTONE = BLOCKS.registerBlock("reinforced_red_sandstone", p -> new BaseReinforcedBlock(p, Blocks.RED_SANDSTONE), prop(MapColor.COLOR_ORANGE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CHISELED_RED_SANDSTONE = BLOCKS.registerBlock("reinforced_chiseled_red_sandstone", p -> new BaseReinforcedBlock(p, Blocks.CHISELED_RED_SANDSTONE), prop(MapColor.COLOR_ORANGE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CUT_RED_SANDSTONE = BLOCKS.registerBlock("reinforced_cut_red_sandstone", p -> new BaseReinforcedBlock(p, Blocks.CUT_RED_SANDSTONE), prop(MapColor.COLOR_ORANGE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_RED_SANDSTONE_STAIRS = BLOCKS.registerBlock("reinforced_red_sandstone_stairs", p -> new ReinforcedStairsBlock(p, Blocks.RED_SANDSTONE_STAIRS), prop(MapColor.COLOR_ORANGE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedMagmaBlock> REINFORCED_MAGMA_BLOCK = BLOCKS.registerBlock("reinforced_magma_block", p -> new ReinforcedMagmaBlock(p, Blocks.MAGMA_BLOCK), reinforcedCopy(Blocks.MAGMA_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_NETHER_WART_BLOCK = BLOCKS.registerBlock("reinforced_nether_wart_block", p -> new BaseReinforcedBlock(p, Blocks.NETHER_WART_BLOCK), prop(MapColor.COLOR_RED).sound(SoundType.WART_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_WARPED_WART_BLOCK = BLOCKS.registerBlock("reinforced_warped_wart_block", p -> new BaseReinforcedBlock(p, Blocks.WARPED_WART_BLOCK), prop(MapColor.WARPED_WART_BLOCK).sound(SoundType.WART_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_RED_NETHER_BRICKS = BLOCKS.registerBlock("reinforced_red_nether_bricks", p -> new BaseReinforcedBlock(p, Blocks.RED_NETHER_BRICKS), prop(MapColor.NETHER).sound(SoundType.NETHER_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_BONE_BLOCK = BLOCKS.registerBlock("reinforced_bone_block", p -> new ReinforcedRotatedPillarBlock(p, Blocks.BONE_BLOCK), prop(MapColor.SAND).sound(SoundType.BONE_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_WHITE_CONCRETE = BLOCKS.registerBlock("reinforced_white_concrete", p -> new BaseReinforcedBlock(p, Blocks.WHITE_CONCRETE), prop(MapColor.SNOW));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_ORANGE_CONCRETE = BLOCKS.registerBlock("reinforced_orange_concrete", p -> new BaseReinforcedBlock(p, Blocks.ORANGE_CONCRETE), prop(MapColor.COLOR_ORANGE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_MAGENTA_CONCRETE = BLOCKS.registerBlock("reinforced_magenta_concrete", p -> new BaseReinforcedBlock(p, Blocks.MAGENTA_CONCRETE), prop(MapColor.COLOR_MAGENTA));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_LIGHT_BLUE_CONCRETE = BLOCKS.registerBlock("reinforced_light_blue_concrete", p -> new BaseReinforcedBlock(p, Blocks.LIGHT_BLUE_CONCRETE), prop(MapColor.COLOR_LIGHT_BLUE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_YELLOW_CONCRETE = BLOCKS.registerBlock("reinforced_yellow_concrete", p -> new BaseReinforcedBlock(p, Blocks.YELLOW_CONCRETE), prop(MapColor.COLOR_YELLOW));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_LIME_CONCRETE = BLOCKS.registerBlock("reinforced_lime_concrete", p -> new BaseReinforcedBlock(p, Blocks.LIME_CONCRETE), prop(MapColor.COLOR_LIGHT_GREEN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_PINK_CONCRETE = BLOCKS.registerBlock("reinforced_pink_concrete", p -> new BaseReinforcedBlock(p, Blocks.PINK_CONCRETE), prop(MapColor.COLOR_PINK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_GRAY_CONCRETE = BLOCKS.registerBlock("reinforced_gray_concrete", p -> new BaseReinforcedBlock(p, Blocks.GRAY_CONCRETE), prop(MapColor.COLOR_GRAY));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_LIGHT_GRAY_CONCRETE = BLOCKS.registerBlock("reinforced_light_gray_concrete", p -> new BaseReinforcedBlock(p, Blocks.LIGHT_GRAY_CONCRETE), prop(MapColor.COLOR_LIGHT_GRAY));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CYAN_CONCRETE = BLOCKS.registerBlock("reinforced_cyan_concrete", p -> new BaseReinforcedBlock(p, Blocks.CYAN_CONCRETE), prop(MapColor.COLOR_CYAN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_PURPLE_CONCRETE = BLOCKS.registerBlock("reinforced_purple_concrete", p -> new BaseReinforcedBlock(p, Blocks.PURPLE_CONCRETE), prop(MapColor.COLOR_PURPLE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BLUE_CONCRETE = BLOCKS.registerBlock("reinforced_blue_concrete", p -> new BaseReinforcedBlock(p, Blocks.BLUE_CONCRETE), prop(MapColor.COLOR_BLUE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BROWN_CONCRETE = BLOCKS.registerBlock("reinforced_brown_concrete", p -> new BaseReinforcedBlock(p, Blocks.BROWN_CONCRETE), prop(MapColor.COLOR_BROWN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_GREEN_CONCRETE = BLOCKS.registerBlock("reinforced_green_concrete", p -> new BaseReinforcedBlock(p, Blocks.GREEN_CONCRETE), prop(MapColor.COLOR_GREEN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_RED_CONCRETE = BLOCKS.registerBlock("reinforced_red_concrete", p -> new BaseReinforcedBlock(p, Blocks.RED_CONCRETE), prop(MapColor.COLOR_RED));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BLACK_CONCRETE = BLOCKS.registerBlock("reinforced_black_concrete", p -> new BaseReinforcedBlock(p, Blocks.BLACK_CONCRETE), prop(MapColor.COLOR_BLACK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BLUE_ICE = BLOCKS.registerBlock("reinforced_blue_ice", p -> new BaseReinforcedBlock(p, Blocks.BLUE_ICE), prop(MapColor.ICE).sound(SoundType.GLASS).friction(0.989F));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_POLISHED_GRANITE_STAIRS = BLOCKS.registerBlock("reinforced_polished_granite_stairs", p -> new ReinforcedStairsBlock(p, Blocks.POLISHED_GRANITE_STAIRS), prop(MapColor.DIRT));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_SMOOTH_RED_SANDSTONE_STAIRS = BLOCKS.registerBlock("reinforced_smooth_red_sandstone_stairs", p -> new ReinforcedStairsBlock(p, Blocks.SMOOTH_RED_SANDSTONE_STAIRS), prop(MapColor.COLOR_ORANGE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_MOSSY_STONE_BRICK_STAIRS = BLOCKS.registerBlock("reinforced_mossy_stone_brick_stairs", p -> new ReinforcedStairsBlock(p, Blocks.MOSSY_STONE_BRICK_STAIRS), prop());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_POLISHED_DIORITE_STAIRS = BLOCKS.registerBlock("reinforced_polished_diorite_stairs", p -> new ReinforcedStairsBlock(p, Blocks.POLISHED_DIORITE_STAIRS), prop(MapColor.QUARTZ));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_MOSSY_COBBLESTONE_STAIRS = BLOCKS.registerBlock("reinforced_mossy_cobblestone_stairs", p -> new ReinforcedStairsBlock(p, Blocks.MOSSY_COBBLESTONE_STAIRS), prop());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_END_STONE_BRICK_STAIRS = BLOCKS.registerBlock("reinforced_end_stone_brick_stairs", p -> new ReinforcedStairsBlock(p, Blocks.END_STONE_BRICK_STAIRS), prop(MapColor.SAND));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_STONE_STAIRS = BLOCKS.registerBlock("reinforced_stone_stairs", p -> new ReinforcedStairsBlock(p, Blocks.STONE_STAIRS), prop());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_SMOOTH_SANDSTONE_STAIRS = BLOCKS.registerBlock("reinforced_smooth_sandstone_stairs", p -> new ReinforcedStairsBlock(p, Blocks.SMOOTH_SANDSTONE_STAIRS), prop(MapColor.SAND));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_SMOOTH_QUARTZ_STAIRS = BLOCKS.registerBlock("reinforced_smooth_quartz_stairs", p -> new ReinforcedStairsBlock(p, Blocks.SMOOTH_QUARTZ_STAIRS), prop(MapColor.QUARTZ));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_GRANITE_STAIRS = BLOCKS.registerBlock("reinforced_granite_stairs", p -> new ReinforcedStairsBlock(p, Blocks.GRANITE_STAIRS), prop(MapColor.DIRT));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_ANDESITE_STAIRS = BLOCKS.registerBlock("reinforced_andesite_stairs", p -> new ReinforcedStairsBlock(p, Blocks.ANDESITE_STAIRS), prop());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_RED_NETHER_BRICK_STAIRS = BLOCKS.registerBlock("reinforced_red_nether_brick_stairs", p -> new ReinforcedStairsBlock(p, Blocks.RED_NETHER_BRICK_STAIRS), prop(MapColor.NETHER).sound(SoundType.NETHER_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_POLISHED_ANDESITE_STAIRS = BLOCKS.registerBlock("reinforced_polished_andesite_stairs", p -> new ReinforcedStairsBlock(p, Blocks.POLISHED_ANDESITE_STAIRS), prop());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_DIORITE_STAIRS = BLOCKS.registerBlock("reinforced_diorite_stairs", p -> new ReinforcedStairsBlock(p, Blocks.DIORITE_STAIRS), prop(MapColor.QUARTZ));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_COBBLED_DEEPSLATE_STAIRS = BLOCKS.registerBlock("reinforced_cobbled_deepslate_stairs", p -> new ReinforcedStairsBlock(p, Blocks.COBBLED_DEEPSLATE_STAIRS), prop(MapColor.DEEPSLATE).sound(SoundType.DEEPSLATE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_POLISHED_DEEPSLATE_STAIRS = BLOCKS.registerBlock("reinforced_polished_deepslate_stairs", p -> new ReinforcedStairsBlock(p, Blocks.POLISHED_DEEPSLATE_STAIRS), prop(MapColor.DEEPSLATE).sound(SoundType.POLISHED_DEEPSLATE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_DEEPSLATE_BRICK_STAIRS = BLOCKS.registerBlock("reinforced_deepslate_brick_stairs", p -> new ReinforcedStairsBlock(p, Blocks.DEEPSLATE_BRICK_STAIRS), prop(MapColor.DEEPSLATE).sound(SoundType.DEEPSLATE_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_DEEPSLATE_TILE_STAIRS = BLOCKS.registerBlock("reinforced_deepslate_tile_stairs", p -> new ReinforcedStairsBlock(p, Blocks.DEEPSLATE_TILE_STAIRS), prop(MapColor.DEEPSLATE).sound(SoundType.DEEPSLATE_TILES));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_POLISHED_GRANITE_SLAB = BLOCKS.registerBlock("reinforced_polished_granite_slab", p -> new ReinforcedSlabBlock(p, Blocks.POLISHED_GRANITE_SLAB), prop(MapColor.DIRT));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_SMOOTH_RED_SANDSTONE_SLAB = BLOCKS.registerBlock("reinforced_smooth_red_sandstone_slab", p -> new ReinforcedSlabBlock(p, Blocks.SMOOTH_RED_SANDSTONE_SLAB), prop(MapColor.COLOR_ORANGE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_MOSSY_STONE_BRICK_SLAB = BLOCKS.registerBlock("reinforced_mossy_stone_brick_slab", p -> new ReinforcedSlabBlock(p, Blocks.MOSSY_STONE_BRICK_SLAB), prop());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_POLISHED_DIORITE_SLAB = BLOCKS.registerBlock("reinforced_polished_diorite_slab", p -> new ReinforcedSlabBlock(p, Blocks.POLISHED_DIORITE_SLAB), prop(MapColor.QUARTZ));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_MOSSY_COBBLESTONE_SLAB = BLOCKS.registerBlock("reinforced_mossy_cobblestone_slab", p -> new ReinforcedSlabBlock(p, Blocks.MOSSY_COBBLESTONE_SLAB), prop());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_END_STONE_BRICK_SLAB = BLOCKS.registerBlock("reinforced_end_stone_brick_slab", p -> new ReinforcedSlabBlock(p, Blocks.END_STONE_BRICK_SLAB), prop(MapColor.SAND));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_SMOOTH_SANDSTONE_SLAB = BLOCKS.registerBlock("reinforced_smooth_sandstone_slab", p -> new ReinforcedSlabBlock(p, Blocks.SMOOTH_SANDSTONE_SLAB), prop(MapColor.SAND));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_SMOOTH_QUARTZ_SLAB = BLOCKS.registerBlock("reinforced_smooth_quartz_slab", p -> new ReinforcedSlabBlock(p, Blocks.SMOOTH_QUARTZ_SLAB), prop(MapColor.QUARTZ));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_GRANITE_SLAB = BLOCKS.registerBlock("reinforced_granite_slab", p -> new ReinforcedSlabBlock(p, Blocks.GRANITE_SLAB), prop(MapColor.DIRT));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_ANDESITE_SLAB = BLOCKS.registerBlock("reinforced_andesite_slab", p -> new ReinforcedSlabBlock(p, Blocks.ANDESITE_SLAB), prop());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_RED_NETHER_BRICK_SLAB = BLOCKS.registerBlock("reinforced_red_nether_brick_slab", p -> new ReinforcedSlabBlock(p, Blocks.RED_NETHER_BRICK_SLAB), prop(MapColor.NETHER).sound(SoundType.NETHER_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_POLISHED_ANDESITE_SLAB = BLOCKS.registerBlock("reinforced_polished_andesite_slab", p -> new ReinforcedSlabBlock(p, Blocks.POLISHED_ANDESITE_SLAB), prop());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_DIORITE_SLAB = BLOCKS.registerBlock("reinforced_diorite_slab", p -> new ReinforcedSlabBlock(p, Blocks.DIORITE_SLAB), prop(MapColor.QUARTZ));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_COBBLED_DEEPSLATE_SLAB = BLOCKS.registerBlock("reinforced_cobbled_deepslate_slab", p -> new ReinforcedSlabBlock(p, Blocks.COBBLED_DEEPSLATE_SLAB), prop(MapColor.DEEPSLATE).sound(SoundType.DEEPSLATE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_POLISHED_DEEPSLATE_SLAB = BLOCKS.registerBlock("reinforced_polished_deepslate_slab", p -> new ReinforcedSlabBlock(p, Blocks.POLISHED_DEEPSLATE_SLAB), prop(MapColor.DEEPSLATE).sound(SoundType.POLISHED_DEEPSLATE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_DEEPSLATE_BRICK_SLAB = BLOCKS.registerBlock("reinforced_deepslate_brick_slab", p -> new ReinforcedSlabBlock(p, Blocks.DEEPSLATE_BRICK_SLAB), prop(MapColor.DEEPSLATE).sound(SoundType.DEEPSLATE_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_DEEPSLATE_TILE_SLAB = BLOCKS.registerBlock("reinforced_deepslate_tile_slab", p -> new ReinforcedSlabBlock(p, Blocks.DEEPSLATE_TILE_SLAB), prop(MapColor.DEEPSLATE).sound(SoundType.DEEPSLATE_TILES));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCryingObsidianBlock> REINFORCED_CRYING_OBSIDIAN = BLOCKS.registerBlock("reinforced_crying_obsidian", p -> new ReinforcedCryingObsidianBlock(p, Blocks.CRYING_OBSIDIAN), prop(MapColor.COLOR_BLACK).lightLevel(state -> 10));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BLACKSTONE = BLOCKS.registerBlock("reinforced_blackstone", p -> new BaseReinforcedBlock(p, Blocks.BLACKSTONE), prop(MapColor.COLOR_BLACK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_BLACKSTONE_SLAB = BLOCKS.registerBlock("reinforced_blackstone_slab", p -> new ReinforcedSlabBlock(p, Blocks.BLACKSTONE_SLAB), prop(MapColor.COLOR_BLACK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_BLACKSTONE_STAIRS = BLOCKS.registerBlock("reinforced_blackstone_stairs", p -> new ReinforcedStairsBlock(p, Blocks.BLACKSTONE_STAIRS), prop(MapColor.COLOR_BLACK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_POLISHED_BLACKSTONE = BLOCKS.registerBlock("reinforced_polished_blackstone", p -> new BaseReinforcedBlock(p, Blocks.POLISHED_BLACKSTONE), prop(MapColor.COLOR_BLACK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_POLISHED_BLACKSTONE_SLAB = BLOCKS.registerBlock("reinforced_polished_blackstone_slab", p -> new ReinforcedSlabBlock(p, Blocks.POLISHED_BLACKSTONE_SLAB), prop(MapColor.COLOR_BLACK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_POLISHED_BLACKSTONE_STAIRS = BLOCKS.registerBlock("reinforced_polished_blackstone_stairs", p -> new ReinforcedStairsBlock(p, Blocks.POLISHED_BLACKSTONE_STAIRS), prop(MapColor.COLOR_BLACK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CHISELED_POLISHED_BLACKSTONE = BLOCKS.registerBlock("reinforced_chiseled_polished_blackstone", p -> new BaseReinforcedBlock(p, Blocks.CHISELED_POLISHED_BLACKSTONE), prop(MapColor.COLOR_BLACK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_POLISHED_BLACKSTONE_BRICKS = BLOCKS.registerBlock("reinforced_polished_blackstone_bricks", p -> new BaseReinforcedBlock(p, Blocks.POLISHED_BLACKSTONE_BRICKS), prop(MapColor.COLOR_BLACK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_POLISHED_BLACKSTONE_BRICK_SLAB = BLOCKS.registerBlock("reinforced_polished_blackstone_brick_slab", p -> new ReinforcedSlabBlock(p, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB), prop(MapColor.COLOR_BLACK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_POLISHED_BLACKSTONE_BRICK_STAIRS = BLOCKS.registerBlock("reinforced_polished_blackstone_brick_stairs", p -> new ReinforcedStairsBlock(p, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS), prop(MapColor.COLOR_BLACK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CRACKED_POLISHED_BLACKSTONE_BRICKS = BLOCKS.registerBlock("reinforced_cracked_polished_blackstone_bricks", p -> new BaseReinforcedBlock(p, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS), prop(MapColor.COLOR_BLACK));

	//ordered by vanilla <1.19.3 decoration blocks creative tab order
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCobwebBlock> REINFORCED_COBWEB = BLOCKS.registerBlock("reinforced_cobweb", p -> new ReinforcedCobwebBlock(p, Blocks.COBWEB), prop(MapColor.WOOL).sound(SoundType.COBWEB).forceSolidOn().noCollission());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_MOSS_CARPET = BLOCKS.registerBlock("reinforced_moss_carpet", p -> new ReinforcedCarpetBlock(p, Blocks.MOSS_CARPET), prop(MapColor.COLOR_GREEN).sound(SoundType.MOSS_CARPET).forceSolidOn());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_MOSS_BLOCK = BLOCKS.registerBlock("reinforced_moss_block", p -> new BaseReinforcedBlock(p, Blocks.MOSS_BLOCK), prop(MapColor.COLOR_GREEN).sound(SoundType.MOSS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedEndRodBlock> REINFORCED_END_ROD = BLOCKS.registerBlock("reinforced_end_rod", ReinforcedEndRodBlock::new, prop(MapColor.NONE).lightLevel(state -> 14).sound(SoundType.WOOD).noOcclusion());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedIronBarsBlock> REINFORCED_IRON_BARS = BLOCKS.registerBlock("reinforced_iron_bars", p -> new ReinforcedIronBarsBlock(p, Blocks.IRON_BARS), prop(MapColor.NONE).sound(SoundType.METAL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedLadderBlock> REINFORCED_LADDER = BLOCKS.registerBlock("reinforced_ladder", ReinforcedLadderBlock::new, prop().forceSolidOff().sound(SoundType.LADDER).noOcclusion().pushReaction(PushReaction.DESTROY));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedChainBlock> REINFORCED_CHAIN = BLOCKS.registerBlock("reinforced_chain", p -> new ReinforcedChainBlock(p, Blocks.CHAIN), prop(MapColor.NONE).sound(SoundType.CHAIN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedPaneBlock> REINFORCED_GLASS_PANE = BLOCKS.registerBlock("reinforced_glass_pane", p -> new ReinforcedPaneBlock(p, Blocks.GLASS_PANE), prop(MapColor.NONE).sound(SoundType.GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_COBBLESTONE_WALL = BLOCKS.registerBlock("reinforced_cobblestone_wall", p -> new ReinforcedWallBlock(p, Blocks.COBBLESTONE_WALL), prop());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_MOSSY_COBBLESTONE_WALL = BLOCKS.registerBlock("reinforced_mossy_cobblestone_wall", p -> new ReinforcedWallBlock(p, Blocks.MOSSY_COBBLESTONE_WALL), prop());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_BRICK_WALL = BLOCKS.registerBlock("reinforced_brick_wall", p -> new ReinforcedWallBlock(p, Blocks.BRICK_WALL), prop(MapColor.COLOR_RED));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_PRISMARINE_WALL = BLOCKS.registerBlock("reinforced_prismarine_wall", p -> new ReinforcedWallBlock(p, Blocks.PRISMARINE_WALL), prop(MapColor.COLOR_CYAN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_RED_SANDSTONE_WALL = BLOCKS.registerBlock("reinforced_red_sandstone_wall", p -> new ReinforcedWallBlock(p, Blocks.RED_SANDSTONE_WALL), prop(MapColor.COLOR_ORANGE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_MOSSY_STONE_BRICK_WALL = BLOCKS.registerBlock("reinforced_mossy_stone_brick_wall", p -> new ReinforcedWallBlock(p, Blocks.MOSSY_STONE_BRICK_WALL), prop());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_GRANITE_WALL = BLOCKS.registerBlock("reinforced_granite_wall", p -> new ReinforcedWallBlock(p, Blocks.GRANITE_WALL), prop(MapColor.DIRT));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_STONE_BRICK_WALL = BLOCKS.registerBlock("reinforced_stone_brick_wall", p -> new ReinforcedWallBlock(p, Blocks.STONE_BRICK_WALL), prop());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_MUD_BRICK_WALL = BLOCKS.registerBlock("reinforced_mud_brick_wall", p -> new ReinforcedWallBlock(p, Blocks.MUD_BRICK_WALL), prop(MapColor.TERRACOTTA_LIGHT_GRAY).sound(SoundType.MUD_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_NETHER_BRICK_WALL = BLOCKS.registerBlock("reinforced_nether_brick_wall", p -> new ReinforcedWallBlock(p, Blocks.NETHER_BRICK_WALL), prop(MapColor.NETHER).sound(SoundType.NETHER_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_ANDESITE_WALL = BLOCKS.registerBlock("reinforced_andesite_wall", p -> new ReinforcedWallBlock(p, Blocks.ANDESITE_WALL), prop());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_RED_NETHER_BRICK_WALL = BLOCKS.registerBlock("reinforced_red_nether_brick_wall", p -> new ReinforcedWallBlock(p, Blocks.RED_NETHER_BRICK_WALL), prop(MapColor.NETHER).sound(SoundType.NETHER_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_SANDSTONE_WALL = BLOCKS.registerBlock("reinforced_sandstone_wall", p -> new ReinforcedWallBlock(p, Blocks.SANDSTONE_WALL), prop(MapColor.SAND));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_END_STONE_BRICK_WALL = BLOCKS.registerBlock("reinforced_end_stone_brick_wall", p -> new ReinforcedWallBlock(p, Blocks.END_STONE_BRICK_WALL), prop(MapColor.SAND));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_DIORITE_WALL = BLOCKS.registerBlock("reinforced_diorite_wall", p -> new ReinforcedWallBlock(p, Blocks.DIORITE_WALL), prop(MapColor.QUARTZ));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_BLACKSTONE_WALL = BLOCKS.registerBlock("reinforced_blackstone_wall", p -> new ReinforcedWallBlock(p, Blocks.BLACKSTONE_WALL), prop(MapColor.COLOR_BLACK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_POLISHED_BLACKSTONE_WALL = BLOCKS.registerBlock("reinforced_polished_blackstone_wall", p -> new ReinforcedWallBlock(p, Blocks.POLISHED_BLACKSTONE_WALL), prop(MapColor.COLOR_BLACK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_POLISHED_BLACKSTONE_BRICK_WALL = BLOCKS.registerBlock("reinforced_polished_blackstone_brick_wall", p -> new ReinforcedWallBlock(p, Blocks.POLISHED_BLACKSTONE_BRICK_WALL), prop(MapColor.COLOR_BLACK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_COBBLED_DEEPSLATE_WALL = BLOCKS.registerBlock("reinforced_cobbled_deepslate_wall", p -> new ReinforcedWallBlock(p, Blocks.COBBLED_DEEPSLATE_WALL), prop(MapColor.DEEPSLATE).sound(SoundType.DEEPSLATE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_POLISHED_DEEPSLATE_WALL = BLOCKS.registerBlock("reinforced_polished_deepslate_wall", p -> new ReinforcedWallBlock(p, Blocks.POLISHED_DEEPSLATE_WALL), prop(MapColor.DEEPSLATE).sound(SoundType.POLISHED_DEEPSLATE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_DEEPSLATE_BRICK_WALL = BLOCKS.registerBlock("reinforced_deepslate_brick_wall", p -> new ReinforcedWallBlock(p, Blocks.DEEPSLATE_BRICK_WALL), prop(MapColor.DEEPSLATE).sound(SoundType.DEEPSLATE_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_DEEPSLATE_TILE_WALL = BLOCKS.registerBlock("reinforced_deepslate_tile_wall", p -> new ReinforcedWallBlock(p, Blocks.DEEPSLATE_TILE_WALL), prop(MapColor.DEEPSLATE).sound(SoundType.DEEPSLATE_TILES));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_WHITE_CARPET = BLOCKS.registerBlock("reinforced_white_carpet", p -> new ReinforcedCarpetBlock(p, Blocks.WHITE_CARPET), prop(MapColor.SNOW).sound(SoundType.WOOL).forceSolidOn());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_ORANGE_CARPET = BLOCKS.registerBlock("reinforced_orange_carpet", p -> new ReinforcedCarpetBlock(p, Blocks.ORANGE_CARPET), prop(MapColor.COLOR_ORANGE).sound(SoundType.WOOL).forceSolidOn());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_MAGENTA_CARPET = BLOCKS.registerBlock("reinforced_magenta_carpet", p -> new ReinforcedCarpetBlock(p, Blocks.MAGENTA_CARPET), prop(MapColor.COLOR_MAGENTA).sound(SoundType.WOOL).forceSolidOn());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_LIGHT_BLUE_CARPET = BLOCKS.registerBlock("reinforced_light_blue_carpet", p -> new ReinforcedCarpetBlock(p, Blocks.LIGHT_BLUE_CARPET), prop(MapColor.COLOR_LIGHT_BLUE).sound(SoundType.WOOL).forceSolidOn());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_YELLOW_CARPET = BLOCKS.registerBlock("reinforced_yellow_carpet", p -> new ReinforcedCarpetBlock(p, Blocks.YELLOW_CARPET), prop(MapColor.COLOR_YELLOW).sound(SoundType.WOOL).forceSolidOn());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_LIME_CARPET = BLOCKS.registerBlock("reinforced_lime_carpet", p -> new ReinforcedCarpetBlock(p, Blocks.LIME_CARPET), prop(MapColor.COLOR_LIGHT_GREEN).sound(SoundType.WOOL).forceSolidOn());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_PINK_CARPET = BLOCKS.registerBlock("reinforced_pink_carpet", p -> new ReinforcedCarpetBlock(p, Blocks.PINK_CARPET), prop(MapColor.COLOR_PINK).sound(SoundType.WOOL).forceSolidOn());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_GRAY_CARPET = BLOCKS.registerBlock("reinforced_gray_carpet", p -> new ReinforcedCarpetBlock(p, Blocks.GRAY_CARPET), prop(MapColor.COLOR_GRAY).sound(SoundType.WOOL).forceSolidOn());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_LIGHT_GRAY_CARPET = BLOCKS.registerBlock("reinforced_light_gray_carpet", p -> new ReinforcedCarpetBlock(p, Blocks.LIGHT_GRAY_CARPET), prop(MapColor.COLOR_LIGHT_GRAY).sound(SoundType.WOOL).forceSolidOn());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_CYAN_CARPET = BLOCKS.registerBlock("reinforced_cyan_carpet", p -> new ReinforcedCarpetBlock(p, Blocks.CYAN_CARPET), prop(MapColor.COLOR_CYAN).sound(SoundType.WOOL).forceSolidOn());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_PURPLE_CARPET = BLOCKS.registerBlock("reinforced_purple_carpet", p -> new ReinforcedCarpetBlock(p, Blocks.PURPLE_CARPET), prop(MapColor.COLOR_PURPLE).sound(SoundType.WOOL).forceSolidOn());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_BLUE_CARPET = BLOCKS.registerBlock("reinforced_blue_carpet", p -> new ReinforcedCarpetBlock(p, Blocks.BLUE_CARPET), prop(MapColor.COLOR_BLUE).sound(SoundType.WOOL).forceSolidOn());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_BROWN_CARPET = BLOCKS.registerBlock("reinforced_brown_carpet", p -> new ReinforcedCarpetBlock(p, Blocks.BROWN_CARPET), prop(MapColor.COLOR_BROWN).sound(SoundType.WOOL).forceSolidOn());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_GREEN_CARPET = BLOCKS.registerBlock("reinforced_green_carpet", p -> new ReinforcedCarpetBlock(p, Blocks.GREEN_CARPET), prop(MapColor.COLOR_GREEN).sound(SoundType.WOOL).forceSolidOn());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_RED_CARPET = BLOCKS.registerBlock("reinforced_red_carpet", p -> new ReinforcedCarpetBlock(p, Blocks.RED_CARPET), prop(MapColor.COLOR_RED).sound(SoundType.WOOL).forceSolidOn());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_BLACK_CARPET = BLOCKS.registerBlock("reinforced_black_carpet", p -> new ReinforcedCarpetBlock(p, Blocks.BLACK_CARPET), prop(MapColor.COLOR_BLACK).sound(SoundType.WOOL).forceSolidOn());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_WHITE_STAINED_GLASS_PANE = BLOCKS.registerBlock("reinforced_white_stained_glass_pane", p -> new ReinforcedStainedGlassPaneBlock(p, DyeColor.WHITE, Blocks.WHITE_STAINED_GLASS_PANE), prop(MapColor.NONE).sound(SoundType.GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_ORANGE_STAINED_GLASS_PANE = BLOCKS.registerBlock("reinforced_orange_stained_glass_pane", p -> new ReinforcedStainedGlassPaneBlock(p, DyeColor.ORANGE, Blocks.ORANGE_STAINED_GLASS_PANE), prop(MapColor.NONE).sound(SoundType.GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_MAGENTA_STAINED_GLASS_PANE = BLOCKS.registerBlock("reinforced_magenta_stained_glass_pane", p -> new ReinforcedStainedGlassPaneBlock(p, DyeColor.MAGENTA, Blocks.MAGENTA_STAINED_GLASS_PANE), prop(MapColor.NONE).sound(SoundType.GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_LIGHT_BLUE_STAINED_GLASS_PANE = BLOCKS.registerBlock("reinforced_light_blue_stained_glass_pane", p -> new ReinforcedStainedGlassPaneBlock(p, DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE), prop(MapColor.NONE).sound(SoundType.GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_YELLOW_STAINED_GLASS_PANE = BLOCKS.registerBlock("reinforced_yellow_stained_glass_pane", p -> new ReinforcedStainedGlassPaneBlock(p, DyeColor.YELLOW, Blocks.YELLOW_STAINED_GLASS_PANE), prop(MapColor.NONE).sound(SoundType.GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_LIME_STAINED_GLASS_PANE = BLOCKS.registerBlock("reinforced_lime_stained_glass_pane", p -> new ReinforcedStainedGlassPaneBlock(p, DyeColor.LIME, Blocks.LIME_STAINED_GLASS_PANE), prop(MapColor.NONE).sound(SoundType.GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_PINK_STAINED_GLASS_PANE = BLOCKS.registerBlock("reinforced_pink_stained_glass_pane", p -> new ReinforcedStainedGlassPaneBlock(p, DyeColor.PINK, Blocks.PINK_STAINED_GLASS_PANE), prop(MapColor.NONE).sound(SoundType.GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_GRAY_STAINED_GLASS_PANE = BLOCKS.registerBlock("reinforced_gray_stained_glass_pane", p -> new ReinforcedStainedGlassPaneBlock(p, DyeColor.GRAY, Blocks.GRAY_STAINED_GLASS_PANE), prop(MapColor.NONE).sound(SoundType.GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_LIGHT_GRAY_STAINED_GLASS_PANE = BLOCKS.registerBlock("reinforced_light_gray_stained_glass_pane", p -> new ReinforcedStainedGlassPaneBlock(p, DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE), prop(MapColor.NONE).sound(SoundType.GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_CYAN_STAINED_GLASS_PANE = BLOCKS.registerBlock("reinforced_cyan_stained_glass_pane", p -> new ReinforcedStainedGlassPaneBlock(p, DyeColor.CYAN, Blocks.CYAN_STAINED_GLASS_PANE), prop(MapColor.NONE).sound(SoundType.GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_PURPLE_STAINED_GLASS_PANE = BLOCKS.registerBlock("reinforced_purple_stained_glass_pane", p -> new ReinforcedStainedGlassPaneBlock(p, DyeColor.PURPLE, Blocks.PURPLE_STAINED_GLASS_PANE), prop(MapColor.NONE).sound(SoundType.GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_BLUE_STAINED_GLASS_PANE = BLOCKS.registerBlock("reinforced_blue_stained_glass_pane", p -> new ReinforcedStainedGlassPaneBlock(p, DyeColor.BLUE, Blocks.BLUE_STAINED_GLASS_PANE), prop(MapColor.NONE).sound(SoundType.GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_BROWN_STAINED_GLASS_PANE = BLOCKS.registerBlock("reinforced_brown_stained_glass_pane", p -> new ReinforcedStainedGlassPaneBlock(p, DyeColor.BROWN, Blocks.BROWN_STAINED_GLASS_PANE), prop(MapColor.NONE).sound(SoundType.GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_GREEN_STAINED_GLASS_PANE = BLOCKS.registerBlock("reinforced_green_stained_glass_pane", p -> new ReinforcedStainedGlassPaneBlock(p, DyeColor.GREEN, Blocks.GREEN_STAINED_GLASS_PANE), prop(MapColor.NONE).sound(SoundType.GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_RED_STAINED_GLASS_PANE = BLOCKS.registerBlock("reinforced_red_stained_glass_pane", p -> new ReinforcedStainedGlassPaneBlock(p, DyeColor.RED, Blocks.RED_STAINED_GLASS_PANE), prop(MapColor.NONE).sound(SoundType.GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_BLACK_STAINED_GLASS_PANE = BLOCKS.registerBlock("reinforced_black_stained_glass_pane", p -> new ReinforcedStainedGlassPaneBlock(p, DyeColor.BLACK, Blocks.BLACK_STAINED_GLASS_PANE), prop(MapColor.NONE).sound(SoundType.GLASS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_WHITE_GLAZED_TERRACOTTA = BLOCKS.registerBlock("reinforced_white_glazed_terracotta", p -> new ReinforcedGlazedTerracottaBlock(p, Blocks.WHITE_GLAZED_TERRACOTTA), prop(DyeColor.WHITE.getMapColor()));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_ORANGE_GLAZED_TERRACOTTA = BLOCKS.registerBlock("reinforced_orange_glazed_terracotta", p -> new ReinforcedGlazedTerracottaBlock(p, Blocks.ORANGE_GLAZED_TERRACOTTA), prop(DyeColor.ORANGE.getMapColor()));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_MAGENTA_GLAZED_TERRACOTTA = BLOCKS.registerBlock("reinforced_magenta_glazed_terracotta", p -> new ReinforcedGlazedTerracottaBlock(p, Blocks.MAGENTA_GLAZED_TERRACOTTA), prop(DyeColor.MAGENTA.getMapColor()));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_LIGHT_BLUE_GLAZED_TERRACOTTA = BLOCKS.registerBlock("reinforced_light_blue_glazed_terracotta", p -> new ReinforcedGlazedTerracottaBlock(p, Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA), prop(DyeColor.LIGHT_BLUE.getMapColor()));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_YELLOW_GLAZED_TERRACOTTA = BLOCKS.registerBlock("reinforced_yellow_glazed_terracotta", p -> new ReinforcedGlazedTerracottaBlock(p, Blocks.YELLOW_GLAZED_TERRACOTTA), prop(DyeColor.YELLOW.getMapColor()));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_LIME_GLAZED_TERRACOTTA = BLOCKS.registerBlock("reinforced_lime_glazed_terracotta", p -> new ReinforcedGlazedTerracottaBlock(p, Blocks.LIME_GLAZED_TERRACOTTA), prop(DyeColor.LIME.getMapColor()));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_PINK_GLAZED_TERRACOTTA = BLOCKS.registerBlock("reinforced_pink_glazed_terracotta", p -> new ReinforcedGlazedTerracottaBlock(p, Blocks.PINK_GLAZED_TERRACOTTA), prop(DyeColor.PINK.getMapColor()));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_GRAY_GLAZED_TERRACOTTA = BLOCKS.registerBlock("reinforced_gray_glazed_terracotta", p -> new ReinforcedGlazedTerracottaBlock(p, Blocks.GRAY_GLAZED_TERRACOTTA), prop(DyeColor.GRAY.getMapColor()));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_LIGHT_GRAY_GLAZED_TERRACOTTA = BLOCKS.registerBlock("reinforced_light_gray_glazed_terracotta", p -> new ReinforcedGlazedTerracottaBlock(p, Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA), prop(DyeColor.LIGHT_GRAY.getMapColor()));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_CYAN_GLAZED_TERRACOTTA = BLOCKS.registerBlock("reinforced_cyan_glazed_terracotta", p -> new ReinforcedGlazedTerracottaBlock(p, Blocks.CYAN_GLAZED_TERRACOTTA), prop(DyeColor.CYAN.getMapColor()));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_PURPLE_GLAZED_TERRACOTTA = BLOCKS.registerBlock("reinforced_purple_glazed_terracotta", p -> new ReinforcedGlazedTerracottaBlock(p, Blocks.PURPLE_GLAZED_TERRACOTTA), prop(DyeColor.PURPLE.getMapColor()));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_BLUE_GLAZED_TERRACOTTA = BLOCKS.registerBlock("reinforced_blue_glazed_terracotta", p -> new ReinforcedGlazedTerracottaBlock(p, Blocks.BLUE_GLAZED_TERRACOTTA), prop(DyeColor.BLUE.getMapColor()));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_BROWN_GLAZED_TERRACOTTA = BLOCKS.registerBlock("reinforced_brown_glazed_terracotta", p -> new ReinforcedGlazedTerracottaBlock(p, Blocks.BROWN_GLAZED_TERRACOTTA), prop(DyeColor.BROWN.getMapColor()));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_GREEN_GLAZED_TERRACOTTA = BLOCKS.registerBlock("reinforced_green_glazed_terracotta", p -> new ReinforcedGlazedTerracottaBlock(p, Blocks.GREEN_GLAZED_TERRACOTTA), prop(DyeColor.GREEN.getMapColor()));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_RED_GLAZED_TERRACOTTA = BLOCKS.registerBlock("reinforced_red_glazed_terracotta", p -> new ReinforcedGlazedTerracottaBlock(p, Blocks.RED_GLAZED_TERRACOTTA), prop(DyeColor.RED.getMapColor()));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_BLACK_GLAZED_TERRACOTTA = BLOCKS.registerBlock("reinforced_black_glazed_terracotta", p -> new ReinforcedGlazedTerracottaBlock(p, Blocks.BLACK_GLAZED_TERRACOTTA), prop(DyeColor.BLACK.getMapColor()));
	@OwnableBE
	@Reinforced(registerBlockItem = false)
	public static final DeferredBlock<ReinforcedScaffoldingBlock> REINFORCED_SCAFFOLDING = BLOCKS.registerBlock("reinforced_scaffolding", ReinforcedScaffoldingBlock::new, reinforcedCopy(Blocks.SCAFFOLDING));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedLanternBlock> REINFORCED_LANTERN = BLOCKS.registerBlock("reinforced_lantern", p -> new ReinforcedLanternBlock(p, Blocks.LANTERN), prop(MapColor.METAL).sound(SoundType.LANTERN).lightLevel(state -> 15).pushReaction(PushReaction.BLOCK).forceSolidOn());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedLanternBlock> REINFORCED_SOUL_LANTERN = BLOCKS.registerBlock("reinforced_soul_lantern", p -> new ReinforcedLanternBlock(p, Blocks.SOUL_LANTERN), prop(MapColor.METAL).sound(SoundType.LANTERN).lightLevel(state -> 10).pushReaction(PushReaction.BLOCK).forceSolidOn());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_SHROOMLIGHT = BLOCKS.registerBlock("reinforced_shroomlight", p -> new BaseReinforcedBlock(p, Blocks.SHROOMLIGHT), prop(MapColor.COLOR_RED).sound(SoundType.SHROOMLIGHT).lightLevel(state -> 15));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_OCHRE_FROGLIGHT = BLOCKS.registerBlock("reinforced_ochre_froglight", p -> new ReinforcedRotatedPillarBlock(p, Blocks.OCHRE_FROGLIGHT), prop(MapColor.SAND).sound(SoundType.FROGLIGHT).lightLevel(state -> 15));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_VERDANT_FROGLIGHT = BLOCKS.registerBlock("reinforced_verdant_froglight", p -> new ReinforcedRotatedPillarBlock(p, Blocks.VERDANT_FROGLIGHT), prop(MapColor.GLOW_LICHEN).sound(SoundType.FROGLIGHT).lightLevel(state -> 15));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_PEARLESCENT_FROGLIGHT = BLOCKS.registerBlock("reinforced_pearlescent_froglight", p -> new ReinforcedRotatedPillarBlock(p, Blocks.PEARLESCENT_FROGLIGHT), prop(MapColor.COLOR_PINK).sound(SoundType.FROGLIGHT).lightLevel(state -> 15));

	//ordered by vanilla <1.19.3 redstone tab order
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRedstoneBlock> REINFORCED_REDSTONE_BLOCK = BLOCKS.registerBlock("reinforced_redstone_block", p -> new ReinforcedRedstoneBlock(p, Blocks.REDSTONE_BLOCK), prop(MapColor.FIRE).sound(SoundType.METAL).isRedstoneConductor(SCContent::never));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final DeferredBlock<ReinforcedPistonBaseBlock> REINFORCED_PISTON = BLOCKS.registerBlock("reinforced_piston", p -> new ReinforcedPistonBaseBlock(false, p), prop().isRedstoneConductor(SCContent::never).isSuffocating((s, w, p) -> !s.getValue(PistonBaseBlock.EXTENDED)).isViewBlocking((s, w, p) -> !s.getValue(PistonBaseBlock.EXTENDED)));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final DeferredBlock<ReinforcedPistonBaseBlock> REINFORCED_STICKY_PISTON = BLOCKS.registerBlock("reinforced_sticky_piston", p -> new ReinforcedPistonBaseBlock(true, p), prop().isRedstoneConductor(SCContent::never).isSuffocating((s, w, p) -> !s.getValue(PistonBaseBlock.EXTENDED)).isViewBlocking((s, w, p) -> !s.getValue(PistonBaseBlock.EXTENDED)));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedObserverBlock> REINFORCED_OBSERVER = BLOCKS.registerBlock("reinforced_observer", ReinforcedObserverBlock::new, propDisguisable().isRedstoneConductor(SCContent::never));
	@HasManualPage
	@Reinforced
	public static final DeferredBlock<ReinforcedHopperBlock> REINFORCED_HOPPER = BLOCKS.registerBlock("reinforced_hopper", ReinforcedHopperBlock::new, propDisguisable().sound(SoundType.METAL));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final DeferredBlock<ReinforcedDispenserBlock> REINFORCED_DISPENSER = BLOCKS.registerBlock("reinforced_dispenser", ReinforcedDispenserBlock::new, propDisguisable());
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final DeferredBlock<ReinforcedDropperBlock> REINFORCED_DROPPER = BLOCKS.registerBlock("reinforced_dropper", ReinforcedDropperBlock::new, propDisguisable());
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final DeferredBlock<ReinforcedLecternBlock> REINFORCED_LECTERN = BLOCKS.registerBlock("reinforced_lectern", ReinforcedLecternBlock::new, prop(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).sound(SoundType.WOOD));
	@HasManualPage
	@Reinforced
	public static final DeferredBlock<ReinforcedLeverBlock> REINFORCED_LEVER = BLOCKS.registerBlock("reinforced_lever", ReinforcedLeverBlock::new, prop(MapColor.NONE).noCollission().sound(SoundType.STONE).pushReaction(PushReaction.BLOCK).forceSolidOn());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRedstoneLampBlock> REINFORCED_REDSTONE_LAMP = BLOCKS.registerBlock("reinforced_redstone_lamp", p -> new ReinforcedRedstoneLampBlock(p, Blocks.REDSTONE_LAMP), prop(MapColor.NONE).sound(SoundType.GLASS).lightLevel(state -> state.getValue(ReinforcedRedstoneLampBlock.LIT) ? 15 : 0));
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final DeferredBlock<ReinforcedButtonBlock> REINFORCED_STONE_BUTTON = stoneButton("reinforced_stone_button", Blocks.STONE_BUTTON, BlockSetType.STONE);
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final DeferredBlock<ReinforcedButtonBlock> REINFORCED_POLISHED_BLACKSTONE_BUTTON = stoneButton("reinforced_polished_blackstone_button", Blocks.POLISHED_BLACKSTONE_BUTTON, BlockSetType.POLISHED_BLACKSTONE);
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final DeferredBlock<ReinforcedButtonBlock> REINFORCED_OAK_BUTTON = woodenButton("reinforced_oak_button", Blocks.OAK_BUTTON, BlockSetType.OAK);
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final DeferredBlock<ReinforcedButtonBlock> REINFORCED_SPRUCE_BUTTON = woodenButton("reinforced_spruce_button", Blocks.SPRUCE_BUTTON, BlockSetType.SPRUCE);
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final DeferredBlock<ReinforcedButtonBlock> REINFORCED_BIRCH_BUTTON = woodenButton("reinforced_birch_button", Blocks.BIRCH_BUTTON, BlockSetType.BIRCH);
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final DeferredBlock<ReinforcedButtonBlock> REINFORCED_JUNGLE_BUTTON = woodenButton("reinforced_jungle_button", Blocks.JUNGLE_BUTTON, BlockSetType.JUNGLE);
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final DeferredBlock<ReinforcedButtonBlock> REINFORCED_ACACIA_BUTTON = woodenButton("reinforced_acacia_button", Blocks.ACACIA_BUTTON, BlockSetType.ACACIA);
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final DeferredBlock<ReinforcedButtonBlock> REINFORCED_DARK_OAK_BUTTON = woodenButton("reinforced_dark_oak_button", Blocks.DARK_OAK_BUTTON, BlockSetType.DARK_OAK);
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final DeferredBlock<ReinforcedButtonBlock> REINFORCED_MANGROVE_BUTTON = woodenButton("reinforced_mangrove_button", Blocks.MANGROVE_BUTTON, BlockSetType.MANGROVE);
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final DeferredBlock<ReinforcedButtonBlock> REINFORCED_CRIMSON_BUTTON = woodenButton("reinforced_crimson_button", Blocks.CRIMSON_BUTTON, BlockSetType.CRIMSON);
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final DeferredBlock<ReinforcedButtonBlock> REINFORCED_WARPED_BUTTON = woodenButton("reinforced_warped_button", Blocks.WARPED_BUTTON, BlockSetType.WARPED);
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedPressurePlateBlock> REINFORCED_STONE_PRESSURE_PLATE = stonePressurePlate("reinforced_stone_pressure_plate", Blocks.STONE_PRESSURE_PLATE, BlockSetType.STONE);
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedPressurePlateBlock> REINFORCED_POLISHED_BLACKSTONE_PRESSURE_PLATE = stonePressurePlate("reinforced_polished_blackstone_pressure_plate", Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE, BlockSetType.POLISHED_BLACKSTONE);
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedPressurePlateBlock> REINFORCED_OAK_PRESSURE_PLATE = woodenPressurePlate("reinforced_oak_pressure_plate", Blocks.OAK_PRESSURE_PLATE, BlockSetType.OAK);
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedPressurePlateBlock> REINFORCED_SPRUCE_PRESSURE_PLATE = woodenPressurePlate("reinforced_spruce_pressure_plate", Blocks.SPRUCE_PRESSURE_PLATE, BlockSetType.SPRUCE);
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedPressurePlateBlock> REINFORCED_BIRCH_PRESSURE_PLATE = woodenPressurePlate("reinforced_birch_pressure_plate", Blocks.BIRCH_PRESSURE_PLATE, BlockSetType.BIRCH);
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedPressurePlateBlock> REINFORCED_JUNGLE_PRESSURE_PLATE = woodenPressurePlate("reinforced_jungle_pressure_plate", Blocks.JUNGLE_PRESSURE_PLATE, BlockSetType.JUNGLE);
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedPressurePlateBlock> REINFORCED_ACACIA_PRESSURE_PLATE = woodenPressurePlate("reinforced_acacia_pressure_plate", Blocks.ACACIA_PRESSURE_PLATE, BlockSetType.ACACIA);
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedPressurePlateBlock> REINFORCED_DARK_OAK_PRESSURE_PLATE = woodenPressurePlate("reinforced_dark_oak_pressure_plate", Blocks.DARK_OAK_PRESSURE_PLATE, BlockSetType.DARK_OAK);
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedPressurePlateBlock> REINFORCED_MANGROVE_PRESSURE_PLATE = woodenPressurePlate("reinforced_mangrove_pressure_plate", Blocks.MANGROVE_PRESSURE_PLATE, BlockSetType.MANGROVE);
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedPressurePlateBlock> REINFORCED_CRIMSON_PRESSURE_PLATE = woodenPressurePlate("reinforced_crimson_pressure_plate", Blocks.CRIMSON_PRESSURE_PLATE, BlockSetType.CRIMSON);
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedPressurePlateBlock> REINFORCED_WARPED_PRESSURE_PLATE = woodenPressurePlate("reinforced_warped_pressure_plate", Blocks.WARPED_PRESSURE_PLATE, BlockSetType.WARPED);
	@HasManualPage(hasRecipeDescription = true)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false, itemGroup = SCItemGroup.MANUAL)
	public static final DeferredBlock<ReinforcedIronTrapDoorBlock> REINFORCED_IRON_TRAPDOOR = BLOCKS.registerBlock("reinforced_iron_trapdoor", p -> new ReinforcedIronTrapDoorBlock(p, BlockSetType.IRON), prop(MapColor.METAL).sound(SoundType.METAL).noOcclusion().isValidSpawn(SCContent::never));

	//ordered by vanilla <1.19.3 brewing tab order
	@Reinforced
	public static final DeferredBlock<ReinforcedCauldronBlock> REINFORCED_CAULDRON = BLOCKS.registerBlock("reinforced_cauldron", p -> new ReinforcedCauldronBlock(p, IReinforcedCauldronInteraction.EMPTY), prop(MapColor.STONE).noOcclusion());
	@Reinforced(registerBlockItem = false)
	public static final DeferredBlock<ReinforcedLayeredCauldronBlock> REINFORCED_WATER_CAULDRON = BLOCKS.registerBlock("reinforced_water_cauldron", p -> new ReinforcedLayeredCauldronBlock(Biome.Precipitation.RAIN, IReinforcedCauldronInteraction.WATER, p, Blocks.WATER_CAULDRON), prop(MapColor.STONE).noOcclusion());
	@Reinforced(registerBlockItem = false)
	public static final DeferredBlock<ReinforcedLavaCauldronBlock> REINFORCED_LAVA_CAULDRON = BLOCKS.registerBlock("reinforced_lava_cauldron", ReinforcedLavaCauldronBlock::new, prop(MapColor.STONE).noOcclusion().lightLevel(state -> 15));
	@Reinforced(registerBlockItem = false)
	public static final DeferredBlock<ReinforcedLayeredCauldronBlock> REINFORCED_POWDER_SNOW_CAULDRON = BLOCKS.registerBlock("reinforced_powder_snow_cauldron", p -> new ReinforcedLayeredCauldronBlock(Biome.Precipitation.SNOW, IReinforcedCauldronInteraction.POWDER_SNOW, p, Blocks.POWDER_SNOW_CAULDRON), prop(MapColor.STONE).noOcclusion());

	//1.19.3+ content
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_BAMBOO_BLOCK = BLOCKS.registerBlock("reinforced_bamboo_block", p -> new ReinforcedRotatedPillarBlock(p, Blocks.BAMBOO_BLOCK), logProp(MapColor.COLOR_YELLOW, MapColor.PLANT).sound(SoundType.BAMBOO_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_BAMBOO_BLOCK = BLOCKS.registerBlock("reinforced_stripped_bamboo_block", p -> new ReinforcedRotatedPillarBlock(p, Blocks.STRIPPED_BAMBOO_BLOCK), prop(MapColor.COLOR_YELLOW).sound(SoundType.BAMBOO_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BAMBOO_PLANKS = BLOCKS.registerBlock("reinforced_bamboo_planks", p -> new BaseReinforcedBlock(p, Blocks.BAMBOO_PLANKS), prop(MapColor.COLOR_YELLOW).sound(SoundType.BAMBOO_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BAMBOO_MOSAIC = BLOCKS.registerBlock("reinforced_bamboo_mosaic", p -> new BaseReinforcedBlock(p, Blocks.BAMBOO_MOSAIC), prop(MapColor.COLOR_YELLOW).sound(SoundType.BAMBOO_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_BAMBOO_STAIRS = BLOCKS.registerBlock("reinforced_bamboo_stairs", p -> new ReinforcedStairsBlock(p, Blocks.BAMBOO_STAIRS), prop(MapColor.COLOR_YELLOW).sound(SoundType.BAMBOO_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_BAMBOO_MOSAIC_STAIRS = BLOCKS.registerBlock("reinforced_bamboo_mosaic_stairs", p -> new ReinforcedStairsBlock(p, Blocks.BAMBOO_MOSAIC_STAIRS), prop(MapColor.COLOR_YELLOW).sound(SoundType.BAMBOO_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_BAMBOO_SLAB = BLOCKS.registerBlock("reinforced_bamboo_slab", p -> new ReinforcedSlabBlock(p, Blocks.BAMBOO_SLAB), prop(MapColor.COLOR_YELLOW).sound(SoundType.BAMBOO_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_BAMBOO_MOSAIC_SLAB = BLOCKS.registerBlock("reinforced_bamboo_mosaic_slab", p -> new ReinforcedSlabBlock(p, Blocks.BAMBOO_MOSAIC_SLAB), prop(MapColor.COLOR_YELLOW).sound(SoundType.BAMBOO_WOOD));
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedPressurePlateBlock> REINFORCED_BAMBOO_PRESSURE_PLATE = woodenPressurePlate("reinforced_bamboo_pressure_plate", Blocks.BAMBOO_PRESSURE_PLATE, BlockSetType.BAMBOO);
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final DeferredBlock<ReinforcedButtonBlock> REINFORCED_BAMBOO_BUTTON = woodenButton("reinforced_bamboo_button", Blocks.BAMBOO_BUTTON, BlockSetType.BAMBOO);
	public static final DeferredBlock<SecretStandingSignBlock> SECRET_CHERRY_SIGN = BLOCKS.registerBlock("secret_cherry_sign_standing", p -> new SecretStandingSignBlock(p, WoodType.CHERRY), prop(MapColor.TERRACOTTA_WHITE).noCollission().forceSolidOn().overrideDescription("block.securitycraft.secret_cherry_sign"));
	public static final DeferredBlock<SecretWallSignBlock> SECRET_CHERRY_WALL_SIGN = BLOCKS.registerBlock("secret_cherry_sign_wall", p -> new SecretWallSignBlock(p, WoodType.CHERRY), prop(MapColor.TERRACOTTA_WHITE).noCollission().forceSolidOn().overrideDescription("block.securitycraft.secret_cherry_sign"));
	public static final DeferredBlock<SecretStandingSignBlock> SECRET_BAMBOO_SIGN = BLOCKS.registerBlock("secret_bamboo_sign_standing", p -> new SecretStandingSignBlock(p, WoodType.BAMBOO), prop(MapColor.COLOR_YELLOW).noCollission().forceSolidOn().overrideDescription("block.securitycraft.secret_bamboo_sign"));
	public static final DeferredBlock<SecretWallSignBlock> SECRET_BAMBOO_WALL_SIGN = BLOCKS.registerBlock("secret_bamboo_sign_wall", p -> new SecretWallSignBlock(p, WoodType.BAMBOO), prop(MapColor.COLOR_YELLOW).noCollission().forceSolidOn().overrideDescription("block.securitycraft.secret_bamboo_sign"));
	//hanging signs
	public static final DeferredBlock<SecretCeilingHangingSignBlock> SECRET_OAK_HANGING_SIGN = BLOCKS.registerBlock("secret_oak_hanging_sign", p -> new SecretCeilingHangingSignBlock(p, WoodType.OAK), prop(MapColor.WOOD).noCollission().forceSolidOn());
	public static final DeferredBlock<SecretWallHangingSignBlock> SECRET_OAK_WALL_HANGING_SIGN = BLOCKS.registerBlock("secret_oak_wall_hanging_sign", p -> new SecretWallHangingSignBlock(p, WoodType.OAK), prop(MapColor.WOOD).noCollission().forceSolidOn());
	public static final DeferredBlock<SecretCeilingHangingSignBlock> SECRET_SPRUCE_HANGING_SIGN = BLOCKS.registerBlock("secret_spruce_hanging_sign", p -> new SecretCeilingHangingSignBlock(p, WoodType.SPRUCE), prop(MapColor.PODZOL).noCollission().forceSolidOn());
	public static final DeferredBlock<SecretWallHangingSignBlock> SECRET_SPRUCE_WALL_HANGING_SIGN = BLOCKS.registerBlock("secret_spruce_wall_hanging_sign", p -> new SecretWallHangingSignBlock(p, WoodType.SPRUCE), prop(MapColor.PODZOL).noCollission().forceSolidOn());
	public static final DeferredBlock<SecretCeilingHangingSignBlock> SECRET_BIRCH_HANGING_SIGN = BLOCKS.registerBlock("secret_birch_hanging_sign", p -> new SecretCeilingHangingSignBlock(p, WoodType.BIRCH), prop(MapColor.SAND).noCollission().forceSolidOn());
	public static final DeferredBlock<SecretWallHangingSignBlock> SECRET_BIRCH_WALL_HANGING_SIGN = BLOCKS.registerBlock("secret_birch_wall_hanging_sign", p -> new SecretWallHangingSignBlock(p, WoodType.BIRCH), prop(MapColor.SAND).noCollission().forceSolidOn());
	public static final DeferredBlock<SecretCeilingHangingSignBlock> SECRET_JUNGLE_HANGING_SIGN = BLOCKS.registerBlock("secret_jungle_hanging_sign", p -> new SecretCeilingHangingSignBlock(p, WoodType.JUNGLE), prop(MapColor.DIRT).noCollission().forceSolidOn());
	public static final DeferredBlock<SecretWallHangingSignBlock> SECRET_JUNGLE_WALL_HANGING_SIGN = BLOCKS.registerBlock("secret_jungle_wall_hanging_sign", p -> new SecretWallHangingSignBlock(p, WoodType.JUNGLE), prop(MapColor.DIRT).noCollission().forceSolidOn());
	public static final DeferredBlock<SecretCeilingHangingSignBlock> SECRET_ACACIA_HANGING_SIGN = BLOCKS.registerBlock("secret_acacia_hanging_sign", p -> new SecretCeilingHangingSignBlock(p, WoodType.ACACIA), prop(MapColor.COLOR_ORANGE).noCollission().forceSolidOn());
	public static final DeferredBlock<SecretWallHangingSignBlock> SECRET_ACACIA_WALL_HANGING_SIGN = BLOCKS.registerBlock("secret_acacia_wall_hanging_sign", p -> new SecretWallHangingSignBlock(p, WoodType.ACACIA), prop(MapColor.COLOR_ORANGE).noCollission().forceSolidOn());
	public static final DeferredBlock<SecretCeilingHangingSignBlock> SECRET_DARK_OAK_HANGING_SIGN = BLOCKS.registerBlock("secret_dark_oak_hanging_sign", p -> new SecretCeilingHangingSignBlock(p, WoodType.DARK_OAK), prop(MapColor.COLOR_BROWN).noCollission().forceSolidOn());
	public static final DeferredBlock<SecretWallHangingSignBlock> SECRET_DARK_OAK_WALL_HANGING_SIGN = BLOCKS.registerBlock("secret_dark_oak_wall_hanging_sign", p -> new SecretWallHangingSignBlock(p, WoodType.DARK_OAK), prop(MapColor.COLOR_BROWN).noCollission().forceSolidOn());
	public static final DeferredBlock<SecretCeilingHangingSignBlock> SECRET_MANGROVE_HANGING_SIGN = BLOCKS.registerBlock("secret_mangrove_hanging_sign", p -> new SecretCeilingHangingSignBlock(p, WoodType.MANGROVE), prop(MapColor.COLOR_RED).noCollission().forceSolidOn());
	public static final DeferredBlock<SecretWallHangingSignBlock> SECRET_MANGROVE_WALL_HANGING_SIGN = BLOCKS.registerBlock("secret_mangrove_wall_hanging_sign", p -> new SecretWallHangingSignBlock(p, WoodType.MANGROVE), prop(MapColor.COLOR_RED).noCollission().forceSolidOn());
	public static final DeferredBlock<SecretCeilingHangingSignBlock> SECRET_CHERRY_HANGING_SIGN = BLOCKS.registerBlock("secret_cherry_hanging_sign", p -> new SecretCeilingHangingSignBlock(p, WoodType.CHERRY), prop(MapColor.TERRACOTTA_PINK).noCollission().forceSolidOn());
	public static final DeferredBlock<SecretWallHangingSignBlock> SECRET_CHERRY_WALL_HANGING_SIGN = BLOCKS.registerBlock("secret_cherry_wall_hanging_sign", p -> new SecretWallHangingSignBlock(p, WoodType.CHERRY), prop(MapColor.TERRACOTTA_PINK).noCollission().forceSolidOn());
	public static final DeferredBlock<SecretCeilingHangingSignBlock> SECRET_BAMBOO_HANGING_SIGN = BLOCKS.registerBlock("secret_bamboo_hanging_sign", p -> new SecretCeilingHangingSignBlock(p, WoodType.BAMBOO), prop(MapColor.COLOR_YELLOW).noCollission().forceSolidOn());
	public static final DeferredBlock<SecretWallHangingSignBlock> SECRET_BAMBOO_WALL_HANGING_SIGN = BLOCKS.registerBlock("secret_bamboo_wall_hanging_sign", p -> new SecretWallHangingSignBlock(p, WoodType.BAMBOO), prop(MapColor.COLOR_YELLOW).noCollission().forceSolidOn());
	public static final DeferredBlock<SecretCeilingHangingSignBlock> SECRET_PALE_OAK_HANGING_SIGN = BLOCKS.registerBlock("secret_pale_oak_hanging_sign", p -> new SecretCeilingHangingSignBlock(p, WoodType.PALE_OAK), prop(MapColor.QUARTZ).noCollission().forceSolidOn());
	public static final DeferredBlock<SecretWallHangingSignBlock> SECRET_PALE_OAK_WALL_HANGING_SIGN = BLOCKS.registerBlock("secret_pale_oak_wall_hanging_sign", p -> new SecretWallHangingSignBlock(p, WoodType.PALE_OAK), prop(MapColor.QUARTZ).noCollission().forceSolidOn());
	public static final DeferredBlock<SecretCeilingHangingSignBlock> SECRET_CRIMSON_HANGING_SIGN = BLOCKS.registerBlock("secret_crimson_hanging_sign", p -> new SecretCeilingHangingSignBlock(p, WoodType.CRIMSON), prop(MapColor.CRIMSON_STEM).noCollission().forceSolidOn());
	public static final DeferredBlock<SecretWallHangingSignBlock> SECRET_CRIMSON_WALL_HANGING_SIGN = BLOCKS.registerBlock("secret_crimson_wall_hanging_sign", p -> new SecretWallHangingSignBlock(p, WoodType.CRIMSON), prop(MapColor.CRIMSON_STEM).noCollission().forceSolidOn());
	public static final DeferredBlock<SecretCeilingHangingSignBlock> SECRET_WARPED_HANGING_SIGN = BLOCKS.registerBlock("secret_warped_hanging_sign", p -> new SecretCeilingHangingSignBlock(p, WoodType.WARPED), prop(MapColor.WARPED_STEM).noCollission().forceSolidOn());
	public static final DeferredBlock<SecretWallHangingSignBlock> SECRET_WARPED_WALL_HANGING_SIGN = BLOCKS.registerBlock("secret_warped_wall_hanging_sign", p -> new SecretWallHangingSignBlock(p, WoodType.WARPED), prop(MapColor.WARPED_STEM).noCollission().forceSolidOn());
	//end hanging signs
	@HasManualPage
	@Reinforced
	public static final DeferredBlock<ReinforcedChiseledBookshelfBlock> REINFORCED_CHISELED_BOOKSHELF = BLOCKS.registerBlock("reinforced_chiseled_bookshelf", ReinforcedChiseledBookshelfBlock::new, prop(MapColor.WOOD).sound(SoundType.CHISELED_BOOKSHELF));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_CHERRY_LOG = BLOCKS.registerBlock("reinforced_cherry_log", p -> new ReinforcedRotatedPillarBlock(p, Blocks.CHERRY_LOG), logProp(MapColor.TERRACOTTA_WHITE, MapColor.TERRACOTTA_GRAY).sound(SoundType.CHERRY_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_CHERRY_WOOD = BLOCKS.registerBlock("reinforced_cherry_wood", p -> new ReinforcedRotatedPillarBlock(p, Blocks.CHERRY_WOOD), prop(MapColor.TERRACOTTA_GRAY).sound(SoundType.CHERRY_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_CHERRY_LOG = BLOCKS.registerBlock("reinforced_stripped_cherry_log", p -> new ReinforcedRotatedPillarBlock(p, Blocks.STRIPPED_CHERRY_LOG), logProp(MapColor.TERRACOTTA_WHITE, MapColor.TERRACOTTA_PINK).sound(SoundType.CHERRY_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_CHERRY_WOOD = BLOCKS.registerBlock("reinforced_stripped_cherry_wood", p -> new ReinforcedRotatedPillarBlock(p, Blocks.STRIPPED_CHERRY_WOOD), prop(MapColor.TERRACOTTA_PINK).sound(SoundType.CHERRY_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CHERRY_PLANKS = BLOCKS.registerBlock("reinforced_cherry_planks", p -> new BaseReinforcedBlock(p, Blocks.CHERRY_PLANKS), prop(MapColor.TERRACOTTA_WHITE).sound(SoundType.CHERRY_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_CHERRY_STAIRS = BLOCKS.registerBlock("reinforced_cherry_stairs", p -> new ReinforcedStairsBlock(p, Blocks.CHERRY_STAIRS), prop(MapColor.TERRACOTTA_WHITE).sound(SoundType.CHERRY_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_CHERRY_SLAB = BLOCKS.registerBlock("reinforced_cherry_slab", p -> new ReinforcedSlabBlock(p, Blocks.CHERRY_SLAB), prop(MapColor.TERRACOTTA_WHITE).sound(SoundType.CHERRY_WOOD));
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedPressurePlateBlock> REINFORCED_CHERRY_PRESSURE_PLATE = woodenPressurePlate("reinforced_cherry_pressure_plate", Blocks.CHERRY_PRESSURE_PLATE, BlockSetType.CHERRY);
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final DeferredBlock<ReinforcedButtonBlock> REINFORCED_CHERRY_BUTTON = woodenButton("reinforced_cherry_button", Blocks.CHERRY_BUTTON, BlockSetType.CHERRY);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceBlock> REINFORCED_OAK_FENCE = BLOCKS.registerBlock("reinforced_oak_fence", p -> new ReinforcedFenceBlock(p, Blocks.OAK_FENCE), prop(Blocks.OAK_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceBlock> REINFORCED_SPRUCE_FENCE = BLOCKS.registerBlock("reinforced_spruce_fence", p -> new ReinforcedFenceBlock(p, Blocks.SPRUCE_FENCE), prop(Blocks.SPRUCE_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceBlock> REINFORCED_BIRCH_FENCE = BLOCKS.registerBlock("reinforced_birch_fence", p -> new ReinforcedFenceBlock(p, Blocks.BIRCH_FENCE), prop(Blocks.BIRCH_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceBlock> REINFORCED_JUNGLE_FENCE = BLOCKS.registerBlock("reinforced_jungle_fence", p -> new ReinforcedFenceBlock(p, Blocks.JUNGLE_FENCE), prop(Blocks.JUNGLE_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceBlock> REINFORCED_ACACIA_FENCE = BLOCKS.registerBlock("reinforced_acacia_fence", p -> new ReinforcedFenceBlock(p, Blocks.ACACIA_FENCE), prop(Blocks.ACACIA_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceBlock> REINFORCED_DARK_OAK_FENCE = BLOCKS.registerBlock("reinforced_dark_oak_fence", p -> new ReinforcedFenceBlock(p, Blocks.DARK_OAK_FENCE), prop(Blocks.DARK_OAK_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceBlock> REINFORCED_MANGROVE_FENCE = BLOCKS.registerBlock("reinforced_mangrove_fence", p -> new ReinforcedFenceBlock(p, Blocks.MANGROVE_FENCE), prop(Blocks.MANGROVE_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceBlock> REINFORCED_CHERRY_FENCE = BLOCKS.registerBlock("reinforced_cherry_fence", p -> new ReinforcedFenceBlock(p, Blocks.CHERRY_FENCE), prop(Blocks.CHERRY_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).sound(SoundType.CHERRY_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceBlock> REINFORCED_PALE_OAK_FENCE = BLOCKS.registerBlock("reinforced_pale_oak_fence", p -> new ReinforcedFenceBlock(p, Blocks.PALE_OAK_FENCE), prop(Blocks.PALE_OAK_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceBlock> REINFORCED_BAMBOO_FENCE = BLOCKS.registerBlock("reinforced_bamboo_fence", p -> new ReinforcedFenceBlock(p, Blocks.BAMBOO_FENCE), prop(Blocks.BAMBOO_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).sound(SoundType.BAMBOO_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceBlock> REINFORCED_CRIMSON_FENCE = BLOCKS.registerBlock("reinforced_crimson_fence", p -> new ReinforcedFenceBlock(p, Blocks.CRIMSON_FENCE), prop(Blocks.CRIMSON_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).sound(SoundType.NETHER_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceBlock> REINFORCED_WARPED_FENCE = BLOCKS.registerBlock("reinforced_warped_fence", p -> new ReinforcedFenceBlock(p, Blocks.WARPED_FENCE), prop(Blocks.WARPED_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).sound(SoundType.NETHER_WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceBlock> REINFORCED_NETHER_BRICK_FENCE = BLOCKS.registerBlock("reinforced_nether_brick_fence", p -> new ReinforcedFenceBlock(p, Blocks.NETHER_BRICK_FENCE), prop(MapColor.NETHER).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHER_BRICKS));
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceGateBlock> REINFORCED_OAK_FENCE_GATE = BLOCKS.registerBlock("reinforced_oak_fence_gate", p -> new ReinforcedFenceGateBlock(p, WoodType.OAK, Blocks.OAK_FENCE_GATE), prop(Blocks.OAK_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).forceSolidOn());
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceGateBlock> REINFORCED_SPRUCE_FENCE_GATE = BLOCKS.registerBlock("reinforced_spruce_fence_gate", p -> new ReinforcedFenceGateBlock(p, WoodType.SPRUCE, Blocks.SPRUCE_FENCE_GATE), prop(Blocks.SPRUCE_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).forceSolidOn());
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceGateBlock> REINFORCED_BIRCH_FENCE_GATE = BLOCKS.registerBlock("reinforced_birch_fence_gate", p -> new ReinforcedFenceGateBlock(p, WoodType.BIRCH, Blocks.BIRCH_FENCE_GATE), prop(Blocks.BIRCH_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).forceSolidOn());
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceGateBlock> REINFORCED_JUNGLE_FENCE_GATE = BLOCKS.registerBlock("reinforced_jungle_fence_gate", p -> new ReinforcedFenceGateBlock(p, WoodType.JUNGLE, Blocks.JUNGLE_FENCE_GATE), prop(Blocks.JUNGLE_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).forceSolidOn());
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceGateBlock> REINFORCED_ACACIA_FENCE_GATE = BLOCKS.registerBlock("reinforced_acacia_fence_gate", p -> new ReinforcedFenceGateBlock(p, WoodType.ACACIA, Blocks.ACACIA_FENCE_GATE), prop(Blocks.ACACIA_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).forceSolidOn());
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceGateBlock> REINFORCED_DARK_OAK_FENCE_GATE = BLOCKS.registerBlock("reinforced_dark_oak_fence_gate", p -> new ReinforcedFenceGateBlock(p, WoodType.DARK_OAK, Blocks.DARK_OAK_FENCE_GATE), prop(Blocks.DARK_OAK_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).forceSolidOn());
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceGateBlock> REINFORCED_MANGROVE_FENCE_GATE = BLOCKS.registerBlock("reinforced_mangrove_fence_gate", p -> new ReinforcedFenceGateBlock(p, WoodType.MANGROVE, Blocks.MANGROVE_FENCE_GATE), prop(Blocks.MANGROVE_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).forceSolidOn());
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceGateBlock> REINFORCED_CHERRY_FENCE_GATE = BLOCKS.registerBlock("reinforced_cherry_fence_gate", p -> new ReinforcedFenceGateBlock(p, WoodType.CHERRY, Blocks.CHERRY_FENCE_GATE), prop(Blocks.CHERRY_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).forceSolidOn());
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceGateBlock> REINFORCED_PALE_OAK_FENCE_GATE = BLOCKS.registerBlock("reinforced_pale_oak_fence_gate", p -> new ReinforcedFenceGateBlock(p, WoodType.PALE_OAK, Blocks.PALE_OAK_FENCE_GATE), prop(Blocks.PALE_OAK_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).forceSolidOn());
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceGateBlock> REINFORCED_BAMBOO_FENCE_GATE = BLOCKS.registerBlock("reinforced_bamboo_fence_gate", p -> new ReinforcedFenceGateBlock(p, WoodType.BAMBOO, Blocks.BAMBOO_FENCE_GATE), prop(Blocks.BAMBOO_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).forceSolidOn());
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceGateBlock> REINFORCED_CRIMSON_FENCE_GATE = BLOCKS.registerBlock("reinforced_crimson_fence_gate", p -> new ReinforcedFenceGateBlock(p, WoodType.CRIMSON, Blocks.CRIMSON_FENCE_GATE), prop(Blocks.CRIMSON_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).forceSolidOn());
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceGateBlock> REINFORCED_WARPED_FENCE_GATE = BLOCKS.registerBlock("reinforced_warped_fence_gate", p -> new ReinforcedFenceGateBlock(p, WoodType.WARPED, Blocks.WARPED_FENCE_GATE), prop(Blocks.WARPED_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).forceSolidOn());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CHISELED_COPPER = BLOCKS.registerBlock("reinforced_chiseled_copper", p -> new BaseReinforcedBlock(p, Blocks.CHISELED_COPPER), prop(MapColor.COLOR_ORANGE).sound(SoundType.COPPER));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_EXPOSED_CHISELED_COPPER = BLOCKS.registerBlock("reinforced_exposed_chiseled_copper", p -> new BaseReinforcedBlock(p, Blocks.EXPOSED_CHISELED_COPPER), prop(MapColor.TERRACOTTA_LIGHT_GRAY).sound(SoundType.COPPER));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_WEATHERED_CHISELED_COPPER = BLOCKS.registerBlock("reinforced_weathered_chiseled_copper", p -> new BaseReinforcedBlock(p, Blocks.WEATHERED_CHISELED_COPPER), prop(MapColor.WARPED_STEM).sound(SoundType.COPPER));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_OXIDIZED_CHISELED_COPPER = BLOCKS.registerBlock("reinforced_oxidized_chiseled_copper", p -> new BaseReinforcedBlock(p, Blocks.OXIDIZED_CHISELED_COPPER), prop(MapColor.WARPED_NYLIUM).sound(SoundType.COPPER));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCopperGrateBlock> REINFORCED_COPPER_GRATE = BLOCKS.registerBlock("reinforced_copper_grate", p -> new ReinforcedCopperGrateBlock(p, Blocks.COPPER_GRATE), reinforcedCopy(Blocks.COPPER_GRATE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCopperGrateBlock> REINFORCED_EXPOSED_COPPER_GRATE = BLOCKS.registerBlock("reinforced_exposed_copper_grate", p -> new ReinforcedCopperGrateBlock(p, Blocks.EXPOSED_COPPER_GRATE), reinforcedCopy(Blocks.EXPOSED_COPPER_GRATE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCopperGrateBlock> REINFORCED_WEATHERED_COPPER_GRATE = BLOCKS.registerBlock("reinforced_weathered_copper_grate", p -> new ReinforcedCopperGrateBlock(p, Blocks.WEATHERED_COPPER_GRATE), reinforcedCopy(Blocks.WEATHERED_COPPER_GRATE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCopperGrateBlock> REINFORCED_OXIDIZED_COPPER_GRATE = BLOCKS.registerBlock("reinforced_oxidized_copper_grate", p -> new ReinforcedCopperGrateBlock(p, Blocks.OXIDIZED_COPPER_GRATE), reinforcedCopy(Blocks.OXIDIZED_COPPER_GRATE));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCopperBulbBlock> REINFORCED_COPPER_BULB = BLOCKS.registerBlock("reinforced_copper_bulb", p -> new ReinforcedCopperBulbBlock(p, Blocks.COPPER_BULB), reinforcedCopy(Blocks.COPPER_BULB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCopperBulbBlock> REINFORCED_EXPOSED_COPPER_BULB = BLOCKS.registerBlock("reinforced_exposed_copper_bulb", p -> new ReinforcedCopperBulbBlock(p, Blocks.EXPOSED_COPPER_BULB), reinforcedCopy(Blocks.EXPOSED_COPPER_BULB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCopperBulbBlock> REINFORCED_WEATHERED_COPPER_BULB = BLOCKS.registerBlock("reinforced_weathered_copper_bulb", p -> new ReinforcedCopperBulbBlock(p, Blocks.WEATHERED_COPPER_BULB), reinforcedCopy(Blocks.WEATHERED_COPPER_BULB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCopperBulbBlock> REINFORCED_OXIDIZED_COPPER_BULB = BLOCKS.registerBlock("reinforced_oxidized_copper_bulb", p -> new ReinforcedCopperBulbBlock(p, Blocks.OXIDIZED_COPPER_BULB), reinforcedCopy(Blocks.OXIDIZED_COPPER_BULB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CHISELED_TUFF = BLOCKS.registerBlock("reinforced_chiseled_tuff", p -> new BaseReinforcedBlock(p, Blocks.CHISELED_TUFF), reinforcedCopy(Blocks.CHISELED_TUFF));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_TUFF_STAIRS = BLOCKS.registerBlock("reinforced_tuff_stairs", p -> new ReinforcedStairsBlock(p, Blocks.TUFF_STAIRS), reinforcedCopy(Blocks.TUFF_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_TUFF_SLAB = BLOCKS.registerBlock("reinforced_tuff_slab", p -> new ReinforcedSlabBlock(p, Blocks.TUFF_SLAB), reinforcedCopy(Blocks.TUFF_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_TUFF_WALL = BLOCKS.registerBlock("reinforced_tuff_wall", p -> new ReinforcedWallBlock(p, Blocks.TUFF_WALL), reinforcedCopy(Blocks.TUFF_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_POLISHED_TUFF = BLOCKS.registerBlock("reinforced_polished_tuff", p -> new BaseReinforcedBlock(p, Blocks.POLISHED_TUFF), reinforcedCopy(Blocks.POLISHED_TUFF));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_POLISHED_TUFF_STAIRS = BLOCKS.registerBlock("reinforced_polished_tuff_stairs", p -> new ReinforcedStairsBlock(p, Blocks.POLISHED_TUFF_STAIRS), reinforcedCopy(Blocks.POLISHED_TUFF_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_POLISHED_TUFF_SLAB = BLOCKS.registerBlock("reinforced_polished_tuff_slab", p -> new ReinforcedSlabBlock(p, Blocks.POLISHED_TUFF_SLAB), reinforcedCopy(Blocks.POLISHED_TUFF_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_POLISHED_TUFF_WALL = BLOCKS.registerBlock("reinforced_polished_tuff_wall", p -> new ReinforcedWallBlock(p, Blocks.POLISHED_TUFF_WALL), reinforcedCopy(Blocks.POLISHED_TUFF_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_TUFF_BRICKS = BLOCKS.registerBlock("reinforced_tuff_bricks", p -> new BaseReinforcedBlock(p, Blocks.TUFF_BRICKS), reinforcedCopy(Blocks.TUFF_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_TUFF_BRICK_STAIRS = BLOCKS.registerBlock("reinforced_tuff_brick_stairs", p -> new ReinforcedStairsBlock(p, Blocks.TUFF_BRICK_STAIRS), reinforcedCopy(Blocks.TUFF_BRICK_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_TUFF_BRICK_SLAB = BLOCKS.registerBlock("reinforced_tuff_brick_slab", p -> new ReinforcedSlabBlock(p, Blocks.TUFF_BRICK_SLAB), reinforcedCopy(Blocks.TUFF_BRICK_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_TUFF_BRICK_WALL = BLOCKS.registerBlock("reinforced_tuff_brick_wall", p -> new ReinforcedWallBlock(p, Blocks.TUFF_BRICK_WALL), reinforcedCopy(Blocks.TUFF_BRICK_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CHISELED_TUFF_BRICKS = BLOCKS.registerBlock("reinforced_chiseled_tuff_bricks", p -> new BaseReinforcedBlock(p, Blocks.CHISELED_TUFF_BRICKS), reinforcedCopy(Blocks.CHISELED_TUFF_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_PALE_OAK_LOG = BLOCKS.registerBlock("reinforced_pale_oak_log", p -> new ReinforcedRotatedPillarBlock(p, Blocks.PALE_OAK_LOG), logProp(MapColor.QUARTZ, MapColor.STONE).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_PALE_OAK_LOG = BLOCKS.registerBlock("reinforced_stripped_pale_oak_log", p -> new ReinforcedRotatedPillarBlock(p, Blocks.STRIPPED_PALE_OAK_LOG), prop(MapColor.QUARTZ).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_PALE_OAK_WOOD = BLOCKS.registerBlock("reinforced_stripped_pale_oak_wood", p -> new ReinforcedRotatedPillarBlock(p, Blocks.STRIPPED_PALE_OAK_WOOD), prop(MapColor.QUARTZ).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_PALE_OAK_WOOD = BLOCKS.registerBlock("reinforced_pale_oak_wood", p -> new ReinforcedRotatedPillarBlock(p, Blocks.PALE_OAK_WOOD), prop(MapColor.STONE).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_PALE_OAK_PLANKS = BLOCKS.registerBlock("reinforced_pale_oak_planks", p -> new BaseReinforcedBlock(p, Blocks.PALE_OAK_PLANKS), prop(MapColor.QUARTZ).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_PALE_OAK_STAIRS = BLOCKS.registerBlock("reinforced_pale_oak_stairs", p -> new ReinforcedStairsBlock(p, Blocks.PALE_OAK_STAIRS), prop(MapColor.QUARTZ).sound(SoundType.WOOD));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_PALE_OAK_SLAB = BLOCKS.registerBlock("reinforced_pale_oak_slab", p -> new ReinforcedSlabBlock(p, Blocks.PALE_OAK_SLAB), prop(MapColor.QUARTZ).sound(SoundType.WOOD));
	public static final DeferredBlock<SecretStandingSignBlock> SECRET_PALE_OAK_SIGN = BLOCKS.registerBlock("secret_pale_oak_sign_standing", p -> new SecretStandingSignBlock(p, WoodType.PALE_OAK), prop(MapColor.QUARTZ).sound(SoundType.WOOD).noCollission().forceSolidOn().overrideDescription("block.securitycraft.secret_pale_oak_sign"));
	public static final DeferredBlock<SecretWallSignBlock> SECRET_PALE_OAK_WALL_SIGN = BLOCKS.registerBlock("secret_pale_oak_sign_wall", p -> new SecretWallSignBlock(p, WoodType.PALE_OAK), prop(MapColor.QUARTZ).sound(SoundType.WOOD).noCollission().forceSolidOn().overrideDescription("block.securitycraft.secret_pale_oak_sign"));
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final DeferredBlock<ReinforcedButtonBlock> REINFORCED_PALE_OAK_BUTTON = woodenButton("reinforced_pale_oak_button", Blocks.PALE_OAK_BUTTON, BlockSetType.PALE_OAK);
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedPressurePlateBlock> REINFORCED_PALE_OAK_PRESSURE_PLATE = woodenPressurePlate("reinforced_pale_oak_pressure_plate", Blocks.PALE_OAK_PRESSURE_PLATE, BlockSetType.PALE_OAK);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_RESIN_BLOCK = BLOCKS.registerBlock("reinforced_resin_block", p -> new BaseReinforcedBlock(p, Blocks.RESIN_BLOCK), reinforcedCopy(Blocks.RESIN_BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_RESIN_BRICKS = BLOCKS.registerBlock("reinforced_resin_bricks", p -> new BaseReinforcedBlock(p, Blocks.RESIN_BRICKS), reinforcedCopy(Blocks.RESIN_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CHISELED_RESIN_BRICKS = BLOCKS.registerBlock("reinforced_chiseled_resin_bricks", p -> new BaseReinforcedBlock(p, Blocks.CHISELED_RESIN_BRICKS), reinforcedCopy(Blocks.CHISELED_RESIN_BRICKS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_RESIN_BRICK_STAIRS = BLOCKS.registerBlock("reinforced_resin_brick_stairs", p -> new ReinforcedStairsBlock(p, Blocks.RESIN_BRICK_STAIRS), reinforcedCopy(Blocks.RESIN_BRICK_STAIRS));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_RESIN_BRICK_SLAB = BLOCKS.registerBlock("reinforced_resin_brick_slab", p -> new ReinforcedSlabBlock(p, Blocks.RESIN_BRICK_SLAB), reinforcedCopy(Blocks.RESIN_BRICK_SLAB));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_RESIN_BRICK_WALL = BLOCKS.registerBlock("reinforced_resin_brick_wall", p -> new ReinforcedWallBlock(p, Blocks.RESIN_BRICK_WALL), reinforcedCopy(Blocks.RESIN_BRICK_WALL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedMossyCarpetBlock> REINFORCED_PALE_MOSS_CARPET = BLOCKS.registerBlock("reinforced_pale_moss_carpet", p -> new ReinforcedMossyCarpetBlock(p, Blocks.PALE_MOSS_CARPET), reinforcedCopy(Blocks.PALE_MOSS_CARPET));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_PALE_MOSS_BLOCK = BLOCKS.registerBlock("reinforced_pale_moss_block", p -> new BaseReinforcedBlock(p, Blocks.PALE_MOSS_BLOCK), reinforcedCopy(Blocks.PALE_MOSS_BLOCK));

	//misc
	@RegisterItemBlock
	public static final DeferredBlock<SlabBlock> CRYSTAL_QUARTZ_SLAB = BLOCKS.registerBlock("crystal_quartz_slab", SlabBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).strength(2.0F, 6.0F).requiresCorrectToolForDrops());
	@RegisterItemBlock
	public static final DeferredBlock<Block> SMOOTH_CRYSTAL_QUARTZ = BLOCKS.registerSimpleBlock("smooth_crystal_quartz", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).strength(2.0F, 6.0F).requiresCorrectToolForDrops());
	@RegisterItemBlock
	public static final DeferredBlock<Block> CHISELED_CRYSTAL_QUARTZ = BLOCKS.registerSimpleBlock("chiseled_crystal_quartz", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).strength(0.8F).requiresCorrectToolForDrops());
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<Block> CRYSTAL_QUARTZ_BLOCK = BLOCKS.registerSimpleBlock("crystal_quartz", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).strength(0.8F).requiresCorrectToolForDrops());
	@RegisterItemBlock
	public static final DeferredBlock<Block> CRYSTAL_QUARTZ_BRICKS = BLOCKS.registerSimpleBlock("crystal_quartz_bricks", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).strength(0.8F).requiresCorrectToolForDrops());
	@RegisterItemBlock
	public static final DeferredBlock<RotatedPillarBlock> CRYSTAL_QUARTZ_PILLAR = BLOCKS.registerBlock("crystal_quartz_pillar", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).strength(0.8F).requiresCorrectToolForDrops());
	@RegisterItemBlock
	public static final DeferredBlock<StairBlock> CRYSTAL_QUARTZ_STAIRS = BLOCKS.registerBlock("crystal_quartz_stairs", p -> new StairBlock(CRYSTAL_QUARTZ_BLOCK.get().defaultBlockState(), p), BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).strength(0.8F).requiresCorrectToolForDrops());
	@RegisterItemBlock
	public static final DeferredBlock<StairBlock> SMOOTH_CRYSTAL_QUARTZ_STAIRS = BLOCKS.registerBlock("smooth_crystal_quartz_stairs", p -> new StairBlock(SMOOTH_CRYSTAL_QUARTZ.get().defaultBlockState(), p), BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).strength(2.0F, 6.0F).requiresCorrectToolForDrops());
	@RegisterItemBlock
	public static final DeferredBlock<SlabBlock> SMOOTH_CRYSTAL_QUARTZ_SLAB = BLOCKS.registerBlock("smooth_crystal_quartz_slab", SlabBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).strength(2.0F, 6.0F).requiresCorrectToolForDrops());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(customTint = CRYSTAL_QUARTZ_TINT, itemGroup = SCItemGroup.MANUAL)
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_CRYSTAL_QUARTZ_SLAB = BLOCKS.registerBlock("reinforced_crystal_quartz_slab", p -> new ReinforcedSlabBlock(p, SCContent.CRYSTAL_QUARTZ_SLAB), prop(MapColor.COLOR_CYAN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(customTint = CRYSTAL_QUARTZ_TINT, itemGroup = SCItemGroup.MANUAL)
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_SMOOTH_CRYSTAL_QUARTZ = BLOCKS.registerBlock("reinforced_smooth_crystal_quartz", p -> new BaseReinforcedBlock(p, SCContent.SMOOTH_CRYSTAL_QUARTZ), prop(MapColor.COLOR_CYAN));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced(customTint = CRYSTAL_QUARTZ_TINT, itemGroup = SCItemGroup.MANUAL)
	public static final DeferredBlock<BlockPocketBlock> REINFORCED_CHISELED_CRYSTAL_QUARTZ = BLOCKS.registerBlock("reinforced_chiseled_crystal_quartz_block", p -> new BlockPocketBlock(p, SCContent.CHISELED_CRYSTAL_QUARTZ), prop(MapColor.COLOR_CYAN));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced(customTint = CRYSTAL_QUARTZ_TINT, itemGroup = SCItemGroup.MANUAL)
	public static final DeferredBlock<BlockPocketBlock> REINFORCED_CRYSTAL_QUARTZ_BLOCK = BLOCKS.registerBlock("reinforced_crystal_quartz_block", p -> new BlockPocketBlock(p, SCContent.CRYSTAL_QUARTZ_BLOCK), prop(MapColor.COLOR_CYAN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(customTint = CRYSTAL_QUARTZ_TINT, itemGroup = SCItemGroup.MANUAL)
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CRYSTAL_QUARTZ_BRICKS = BLOCKS.registerBlock("reinforced_crystal_quartz_bricks", p -> new BaseReinforcedBlock(p, SCContent.CRYSTAL_QUARTZ_BRICKS), prop(MapColor.COLOR_CYAN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(customTint = CRYSTAL_QUARTZ_TINT, itemGroup = SCItemGroup.MANUAL)
	public static final DeferredBlock<ReinforcedRotatedCrystalQuartzPillar> REINFORCED_CRYSTAL_QUARTZ_PILLAR = BLOCKS.registerBlock("reinforced_crystal_quartz_pillar", p -> new ReinforcedRotatedCrystalQuartzPillar(p, SCContent.CRYSTAL_QUARTZ_PILLAR), prop(MapColor.COLOR_CYAN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(customTint = CRYSTAL_QUARTZ_TINT, itemGroup = SCItemGroup.MANUAL)
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_CRYSTAL_QUARTZ_STAIRS = BLOCKS.registerBlock("reinforced_crystal_quartz_stairs", p -> new ReinforcedStairsBlock(p, SCContent.CRYSTAL_QUARTZ_STAIRS), prop(MapColor.COLOR_CYAN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(customTint = CRYSTAL_QUARTZ_TINT, itemGroup = SCItemGroup.MANUAL)
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_SMOOTH_CRYSTAL_QUARTZ_STAIRS = BLOCKS.registerBlock("reinforced_smooth_crystal_quartz_stairs", p -> new ReinforcedStairsBlock(p, SCContent.SMOOTH_CRYSTAL_QUARTZ_STAIRS), prop(MapColor.COLOR_CYAN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(customTint = CRYSTAL_QUARTZ_TINT, itemGroup = SCItemGroup.MANUAL)
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_SMOOTH_CRYSTAL_QUARTZ_SLAB = BLOCKS.registerBlock("reinforced_smooth_crystal_quartz_slab", p -> new ReinforcedSlabBlock(p, SCContent.SMOOTH_CRYSTAL_QUARTZ_SLAB), prop(MapColor.COLOR_CYAN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	public static final DeferredBlock<HorizontalReinforcedIronBars> HORIZONTAL_REINFORCED_IRON_BARS = BLOCKS.registerBlock("horizontal_reinforced_iron_bars", p -> new HorizontalReinforcedIronBars(p, Blocks.IRON_BLOCK), prop(MapColor.METAL).sound(SoundType.METAL).noLootTable());
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedDirtPathBlock> REINFORCED_DIRT_PATH = BLOCKS.registerBlock("reinforced_grass_path", p -> new ReinforcedDirtPathBlock(p, Blocks.DIRT_PATH), prop(MapColor.DIRT).sound(SoundType.GRASS));
	@OwnableBE
	public static final DeferredBlock<ReinforcedMovingPistonBlock> REINFORCED_MOVING_PISTON = BLOCKS.registerBlock("reinforced_moving_piston", ReinforcedMovingPistonBlock::new, prop().dynamicShape().noLootTable().noOcclusion().isRedstoneConductor(SCContent::never).isSuffocating(SCContent::never).isViewBlocking(SCContent::never));
	@OwnableBE
	@Reinforced(registerBlockItem = false)
	public static final DeferredBlock<ReinforcedPistonHeadBlock> REINFORCED_PISTON_HEAD = BLOCKS.registerBlock("reinforced_piston_head", ReinforcedPistonHeadBlock::new, prop().noLootTable().pushReaction(PushReaction.BLOCK));
	public static final DeferredBlock<SometimesVisibleBlock> SENTRY_DISGUISE = BLOCKS.registerBlock("sentry_disguise", SometimesVisibleBlock::new, propDisguisable().noLootTable().pushReaction(PushReaction.BLOCK));

	//items
	@HasManualPage(hasRecipeDescription = true)
	public static final DeferredItem<AdminToolItem> ADMIN_TOOL = ITEMS.registerItem("admin_tool", AdminToolItem::new, itemProp(1).rarity(Rarity.EPIC));
	public static final DeferredItem<BlockItem> ANCIENT_DEBRIS_MINE_ITEM = ITEMS.registerSimpleBlockItem(SCContent.ANCIENT_DEBRIS_MINE, itemProp().fireResistant());
	@HasManualPage
	public static final DeferredItem<BriefcaseItem> BRIEFCASE = ITEMS.registerItem("briefcase", BriefcaseItem::new, itemProp(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
	@HasManualPage
	public static final DeferredItem<CameraMonitorItem> CAMERA_MONITOR = registerItem("camera_monitor", CameraMonitorItem::new, () -> itemProp(1).component(BOUND_CAMERAS, CameraMonitorItem.DEFAULT_NAMED_POSITIONS));
	@HasManualPage
	public static final DeferredItem<CodebreakerItem> CODEBREAKER = registerItem("codebreaker", CodebreakerItem::new, () -> itemProp().durability(5).rarity(Rarity.RARE).component(CODEBREAKER_DATA, CodebreakerData.DEFAULT).component(SUCCESS_CHANCE, 0.33D).component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true));
	@HasManualPage
	public static final DeferredItem<Item> CRYSTAL_QUARTZ_ITEM = ITEMS.registerSimpleItem("crystal_quartz_item");
	public static final DeferredItem<BlockItem> DISPLAY_CASE_ITEM = ITEMS.registerSimpleBlockItem(DISPLAY_CASE_PATH, SCContent.DISPLAY_CASE, itemProp());
	@HasManualPage(hasRecipeDescription = true)
	public static final DeferredItem<FakeLiquidBucketItem> FAKE_LAVA_BUCKET = ITEMS.registerItem("bucket_f_lava", p -> new FakeLiquidBucketItem(SCContent.FAKE_LAVA.get(), p), itemProp(1));
	@HasManualPage(hasRecipeDescription = true)
	public static final DeferredItem<FakeLiquidBucketItem> FAKE_WATER_BUCKET = ITEMS.registerItem("bucket_f_water", p -> new FakeLiquidBucketItem(SCContent.FAKE_WATER.get(), p), itemProp(1));
	public static final DeferredItem<BlockItem> GLOW_DISPLAY_CASE_ITEM = ITEMS.registerSimpleBlockItem(GLOW_DISPLAY_CASE_PATH, SCContent.GLOW_DISPLAY_CASE, itemProp());
	@HasManualPage
	public static final DeferredItem<KeycardHolderItem> KEYCARD_HOLDER = ITEMS.registerItem("keycard_holder", KeycardHolderItem::new, itemProp(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
	@HasManualPage(PageGroup.KEYCARDS)
	public static final DeferredItem<KeycardItem> KEYCARD_LVL_1 = ITEMS.registerItem("keycard_lv1", p -> new KeycardItem(p, 0), itemProp());
	@HasManualPage(PageGroup.KEYCARDS)
	public static final DeferredItem<KeycardItem> KEYCARD_LVL_2 = ITEMS.registerItem("keycard_lv2", p -> new KeycardItem(p, 1), itemProp());
	@HasManualPage(PageGroup.KEYCARDS)
	public static final DeferredItem<KeycardItem> KEYCARD_LVL_3 = ITEMS.registerItem("keycard_lv3", p -> new KeycardItem(p, 2), itemProp());
	@HasManualPage(PageGroup.KEYCARDS)
	public static final DeferredItem<KeycardItem> KEYCARD_LVL_4 = ITEMS.registerItem("keycard_lv4", p -> new KeycardItem(p, 3), itemProp());
	@HasManualPage(PageGroup.KEYCARDS)
	public static final DeferredItem<KeycardItem> KEYCARD_LVL_5 = ITEMS.registerItem("keycard_lv5", p -> new KeycardItem(p, 4), itemProp());
	@HasManualPage
	public static final DeferredItem<KeyPanelItem> KEY_PANEL = ITEMS.registerItem("keypad_item", KeyPanelItem::new, itemProp());
	public static final DeferredItem<BlockItem> KEYPAD_CHEST_ITEM = ITEMS.registerSimpleBlockItem(KEYPAD_CHEST_PATH, SCContent.KEYPAD_CHEST, itemProp());
	@HasManualPage
	public static final DeferredItem<DoubleHighBlockItem> KEYPAD_DOOR_ITEM = ITEMS.registerItem("keypad_door_item", p -> new DoubleHighBlockItem(KEYPAD_DOOR.get(), p), itemProp());
	@HasManualPage
	public static final DeferredItem<LensItem> LENS = ITEMS.registerItem("lens", LensItem::new, itemProp());
	@HasManualPage
	public static final DeferredItem<KeycardItem> LIMITED_USE_KEYCARD = ITEMS.registerItem("limited_use_keycard", p -> new KeycardItem(p, -1), itemProp());
	@HasManualPage
	public static final DeferredItem<PortableTunePlayerItem> PORTABLE_TUNE_PLAYER = ITEMS.registerItem("portable_tune_player", PortableTunePlayerItem::new, itemProp());
	@HasManualPage
	public static final DeferredItem<DoubleHighBlockItem> REINFORCED_DOOR_ITEM = ITEMS.registerItem("door_indestructible_iron_item", p -> new DoubleHighBlockItem(REINFORCED_DOOR.get(), p), itemProp());
	@HasManualPage
	public static final DeferredItem<MineRemoteAccessToolItem> MINE_REMOTE_ACCESS_TOOL = registerItem("remote_access_mine", MineRemoteAccessToolItem::new, () -> itemProp(1).component(BOUND_MINES, GlobalPositions.sized(MineRemoteAccessToolItem.MAX_MINES)));
	@HasManualPage
	public static final DeferredItem<SentryRemoteAccessToolItem> SENTRY_REMOTE_ACCESS_TOOL = registerItem("remote_access_sentry", SentryRemoteAccessToolItem::new, () -> itemProp(1).component(BOUND_SENTRIES, SentryRemoteAccessToolItem.DEFAULT_NAMED_POSITIONS));
	@HasManualPage
	public static final DeferredItem<DoubleHighBlockItem> RIFT_STABILIZER_ITEM = ITEMS.registerItem("rift_stabilizer", p -> new DoubleHighBlockItem(RIFT_STABILIZER.get(), p), itemProp().useBlockDescriptionPrefix());
	@HasManualPage
	public static final DeferredItem<DoubleHighBlockItem> SCANNER_DOOR_ITEM = ITEMS.registerItem("scanner_door_item", p -> new DoubleHighBlockItem(SCANNER_DOOR.get(), p), itemProp());
	@HasManualPage
	public static final DeferredItem<SCManualItem> SC_MANUAL = ITEMS.registerItem("sc_manual", SCManualItem::new, itemProp(1));
	@HasManualPage(PageGroup.REINFORCED)
	public static final DeferredItem<ReinforcedScaffoldingBlockItem> REINFORCED_SCAFFOLDING_ITEM = ITEMS.registerItem("reinforced_scaffolding", ReinforcedScaffoldingBlockItem::new, itemProp().useBlockDescriptionPrefix());
	@HasManualPage(PageGroup.SECRET_SIGNS)
	public static final DeferredItem<SignItem> SECRET_OAK_SIGN_ITEM = ITEMS.registerItem("secret_sign_item", p -> new SignItem(SCContent.SECRET_OAK_SIGN.get(), SCContent.SECRET_OAK_WALL_SIGN.get(), p), itemProp(16).overrideDescription("block.securitycraft.secret_sign"));
	@HasManualPage(PageGroup.SECRET_SIGNS)
	public static final DeferredItem<SignItem> SECRET_SPRUCE_SIGN_ITEM = ITEMS.registerItem("secret_spruce_sign_item", p -> new SignItem(SCContent.SECRET_SPRUCE_SIGN.get(), SCContent.SECRET_SPRUCE_WALL_SIGN.get(), p), itemProp(16).overrideDescription("block.securitycraft.secret_spruce_sign"));
	@HasManualPage(PageGroup.SECRET_SIGNS)
	public static final DeferredItem<SignItem> SECRET_BIRCH_SIGN_ITEM = ITEMS.registerItem("secret_birch_sign_item", p -> new SignItem(SCContent.SECRET_BIRCH_SIGN.get(), SCContent.SECRET_BIRCH_WALL_SIGN.get(), p), itemProp(16).overrideDescription("block.securitycraft.secret_birch_sign"));
	@HasManualPage(PageGroup.SECRET_SIGNS)
	public static final DeferredItem<SignItem> SECRET_JUNGLE_SIGN_ITEM = ITEMS.registerItem("secret_jungle_sign_item", p -> new SignItem(SCContent.SECRET_JUNGLE_SIGN.get(), SCContent.SECRET_JUNGLE_WALL_SIGN.get(), p), itemProp(16).overrideDescription("block.securitycraft.secret_jungle_sign"));
	@HasManualPage(PageGroup.SECRET_SIGNS)
	public static final DeferredItem<SignItem> SECRET_ACACIA_SIGN_ITEM = ITEMS.registerItem("secret_acacia_sign_item", p -> new SignItem(SCContent.SECRET_ACACIA_SIGN.get(), SCContent.SECRET_ACACIA_WALL_SIGN.get(), p), itemProp(16).overrideDescription("block.securitycraft.secret_acacia_sign"));
	@HasManualPage(PageGroup.SECRET_SIGNS)
	public static final DeferredItem<SignItem> SECRET_DARK_OAK_SIGN_ITEM = ITEMS.registerItem("secret_dark_oak_sign_item", p -> new SignItem(SCContent.SECRET_DARK_OAK_SIGN.get(), SCContent.SECRET_DARK_OAK_WALL_SIGN.get(), p), itemProp(16).overrideDescription("block.securitycraft.secret_dark_oak_sign"));
	@HasManualPage(PageGroup.SECRET_SIGNS)
	public static final DeferredItem<SignItem> SECRET_MANGROVE_SIGN_ITEM = ITEMS.registerItem("secret_mangrove_sign_item", p -> new SignItem(SCContent.SECRET_MANGROVE_SIGN.get(), SCContent.SECRET_MANGROVE_WALL_SIGN.get(), p), itemProp(16).overrideDescription("block.securitycraft.secret_mangrove_sign"));
	@HasManualPage(PageGroup.SECRET_SIGNS)
	public static final DeferredItem<SignItem> SECRET_CHERRY_SIGN_ITEM = ITEMS.registerItem("secret_cherry_sign_item", p -> new SignItem(SCContent.SECRET_CHERRY_SIGN.get(), SCContent.SECRET_CHERRY_WALL_SIGN.get(), p), itemProp(16).overrideDescription("block.securitycraft.secret_cherry_sign"));
	@HasManualPage(PageGroup.SECRET_SIGNS)
	public static final DeferredItem<SignItem> SECRET_PALE_OAK_SIGN_ITEM = ITEMS.registerItem("secret_pale_oak_sign_item", p -> new SignItem(SCContent.SECRET_PALE_OAK_SIGN.get(), SCContent.SECRET_PALE_OAK_WALL_SIGN.get(), p), itemProp(16).overrideDescription("block.securitycraft.secret_pale_oak_sign"));
	@HasManualPage(PageGroup.SECRET_SIGNS)
	public static final DeferredItem<SignItem> SECRET_BAMBOO_SIGN_ITEM = ITEMS.registerItem("secret_bamboo_sign_item", p -> new SignItem(SCContent.SECRET_BAMBOO_SIGN.get(), SCContent.SECRET_BAMBOO_WALL_SIGN.get(), p), itemProp(16).overrideDescription("block.securitycraft.secret_bamboo_sign"));
	@HasManualPage(PageGroup.SECRET_SIGNS)
	public static final DeferredItem<SignItem> SECRET_CRIMSON_SIGN_ITEM = ITEMS.registerItem("secret_crimson_sign_item", p -> new SignItem(SCContent.SECRET_CRIMSON_SIGN.get(), SCContent.SECRET_CRIMSON_WALL_SIGN.get(), p), itemProp(16).overrideDescription("block.securitycraft.secret_crimson_sign"));
	@HasManualPage(PageGroup.SECRET_SIGNS)
	public static final DeferredItem<SignItem> SECRET_WARPED_SIGN_ITEM = ITEMS.registerItem("secret_warped_sign_item", p -> new SignItem(SCContent.SECRET_WARPED_SIGN.get(), SCContent.SECRET_WARPED_WALL_SIGN.get(), p), itemProp(16).overrideDescription("block.securitycraft.secret_warped_sign"));
	@HasManualPage(PageGroup.SECRET_HANGING_SIGNS)
	public static final DeferredItem<HangingSignItem> SECRET_OAK_HANGING_SIGN_ITEM = ITEMS.registerItem("secret_oak_hanging_sign", p -> new HangingSignItem(SCContent.SECRET_OAK_HANGING_SIGN.get(), SCContent.SECRET_OAK_WALL_HANGING_SIGN.get(), p), itemProp(16).useBlockDescriptionPrefix());
	@HasManualPage(PageGroup.SECRET_HANGING_SIGNS)
	public static final DeferredItem<HangingSignItem> SECRET_SPRUCE_HANGING_SIGN_ITEM = ITEMS.registerItem("secret_spruce_hanging_sign", p -> new HangingSignItem(SCContent.SECRET_SPRUCE_HANGING_SIGN.get(), SCContent.SECRET_SPRUCE_WALL_HANGING_SIGN.get(), p), itemProp(16).useBlockDescriptionPrefix());
	@HasManualPage(PageGroup.SECRET_HANGING_SIGNS)
	public static final DeferredItem<HangingSignItem> SECRET_BIRCH_HANGING_SIGN_ITEM = ITEMS.registerItem("secret_birch_hanging_sign", p -> new HangingSignItem(SCContent.SECRET_BIRCH_HANGING_SIGN.get(), SCContent.SECRET_BIRCH_WALL_HANGING_SIGN.get(), p), itemProp(16).useBlockDescriptionPrefix());
	@HasManualPage(PageGroup.SECRET_HANGING_SIGNS)
	public static final DeferredItem<HangingSignItem> SECRET_JUNGLE_HANGING_SIGN_ITEM = ITEMS.registerItem("secret_jungle_hanging_sign", p -> new HangingSignItem(SCContent.SECRET_JUNGLE_HANGING_SIGN.get(), SCContent.SECRET_JUNGLE_WALL_HANGING_SIGN.get(), p), itemProp(16).useBlockDescriptionPrefix());
	@HasManualPage(PageGroup.SECRET_HANGING_SIGNS)
	public static final DeferredItem<HangingSignItem> SECRET_ACACIA_HANGING_SIGN_ITEM = ITEMS.registerItem("secret_acacia_hanging_sign", p -> new HangingSignItem(SCContent.SECRET_ACACIA_HANGING_SIGN.get(), SCContent.SECRET_ACACIA_WALL_HANGING_SIGN.get(), p), itemProp(16).useBlockDescriptionPrefix());
	@HasManualPage(PageGroup.SECRET_HANGING_SIGNS)
	public static final DeferredItem<HangingSignItem> SECRET_DARK_OAK_HANGING_SIGN_ITEM = ITEMS.registerItem("secret_dark_oak_hanging_sign", p -> new HangingSignItem(SCContent.SECRET_DARK_OAK_HANGING_SIGN.get(), SCContent.SECRET_DARK_OAK_WALL_HANGING_SIGN.get(), p), itemProp(16).useBlockDescriptionPrefix());
	@HasManualPage(PageGroup.SECRET_HANGING_SIGNS)
	public static final DeferredItem<HangingSignItem> SECRET_MANGROVE_HANGING_SIGN_ITEM = ITEMS.registerItem("secret_mangrove_hanging_sign", p -> new HangingSignItem(SCContent.SECRET_MANGROVE_HANGING_SIGN.get(), SCContent.SECRET_MANGROVE_WALL_HANGING_SIGN.get(), p), itemProp(16).useBlockDescriptionPrefix());
	@HasManualPage(PageGroup.SECRET_HANGING_SIGNS)
	public static final DeferredItem<HangingSignItem> SECRET_CHERRY_HANGING_SIGN_ITEM = ITEMS.registerItem("secret_cherry_hanging_sign", p -> new HangingSignItem(SCContent.SECRET_CHERRY_HANGING_SIGN.get(), SCContent.SECRET_CHERRY_WALL_HANGING_SIGN.get(), p), itemProp(16).useBlockDescriptionPrefix());
	@HasManualPage(PageGroup.SECRET_HANGING_SIGNS)
	public static final DeferredItem<HangingSignItem> SECRET_PALE_OAK_HANGING_SIGN_ITEM = ITEMS.registerItem("secret_pale_oak_hanging_sign", p -> new HangingSignItem(SCContent.SECRET_PALE_OAK_HANGING_SIGN.get(), SCContent.SECRET_PALE_OAK_WALL_HANGING_SIGN.get(), p), itemProp(16).useBlockDescriptionPrefix());
	@HasManualPage(PageGroup.SECRET_HANGING_SIGNS)
	public static final DeferredItem<HangingSignItem> SECRET_BAMBOO_HANGING_SIGN_ITEM = ITEMS.registerItem("secret_bamboo_hanging_sign", p -> new HangingSignItem(SCContent.SECRET_BAMBOO_HANGING_SIGN.get(), SCContent.SECRET_BAMBOO_WALL_HANGING_SIGN.get(), p), itemProp(16).useBlockDescriptionPrefix());
	@HasManualPage(PageGroup.SECRET_HANGING_SIGNS)
	public static final DeferredItem<HangingSignItem> SECRET_CRIMSON_HANGING_SIGN_ITEM = ITEMS.registerItem("secret_crimson_hanging_sign", p -> new HangingSignItem(SCContent.SECRET_CRIMSON_HANGING_SIGN.get(), SCContent.SECRET_CRIMSON_WALL_HANGING_SIGN.get(), p), itemProp(16).useBlockDescriptionPrefix());
	@HasManualPage(PageGroup.SECRET_HANGING_SIGNS)
	public static final DeferredItem<HangingSignItem> SECRET_WARPED_HANGING_SIGN_ITEM = ITEMS.registerItem("secret_warped_hanging_sign", p -> new HangingSignItem(SCContent.SECRET_WARPED_HANGING_SIGN.get(), SCContent.SECRET_WARPED_WALL_HANGING_SIGN.get(), p), itemProp(16).useBlockDescriptionPrefix());
	@HasManualPage(PageGroup.SECURITY_SEA_BOATS)
	public static final DeferredItem<SecuritySeaBoatItem> OAK_SECURITY_SEA_BOAT = ITEMS.registerItem("oak_security_sea_boat", p -> new SecuritySeaBoatItem(getOakSecuritySeaBoat(), p), itemProp(1).fireResistant());
	@HasManualPage(PageGroup.SECURITY_SEA_BOATS)
	public static final DeferredItem<SecuritySeaBoatItem> SPRUCE_SECURITY_SEA_BOAT = ITEMS.registerItem("spruce_security_sea_boat", p -> new SecuritySeaBoatItem(getSpruceSecuritySeaBoat(), p), itemProp(1).fireResistant());
	@HasManualPage(PageGroup.SECURITY_SEA_BOATS)
	public static final DeferredItem<SecuritySeaBoatItem> BIRCH_SECURITY_SEA_BOAT = ITEMS.registerItem("birch_security_sea_boat", p -> new SecuritySeaBoatItem(getBirchSecuritySeaBoat(), p), itemProp(1).fireResistant());
	@HasManualPage(PageGroup.SECURITY_SEA_BOATS)
	public static final DeferredItem<SecuritySeaBoatItem> JUNGLE_SECURITY_SEA_BOAT = ITEMS.registerItem("jungle_security_sea_boat", p -> new SecuritySeaBoatItem(getJungleSecuritySeaBoat(), p), itemProp(1).fireResistant());
	@HasManualPage(PageGroup.SECURITY_SEA_BOATS)
	public static final DeferredItem<SecuritySeaBoatItem> ACACIA_SECURITY_SEA_BOAT = ITEMS.registerItem("acacia_security_sea_boat", p -> new SecuritySeaBoatItem(getAcaciaSecuritySeaBoat(), p), itemProp(1).fireResistant());
	@HasManualPage(PageGroup.SECURITY_SEA_BOATS)
	public static final DeferredItem<SecuritySeaBoatItem> DARK_OAK_SECURITY_SEA_BOAT = ITEMS.registerItem("dark_oak_security_sea_boat", p -> new SecuritySeaBoatItem(getDarkOakSecuritySeaBoat(), p), itemProp(1).fireResistant());
	@HasManualPage(PageGroup.SECURITY_SEA_BOATS)
	public static final DeferredItem<SecuritySeaBoatItem> MANGROVE_SECURITY_SEA_BOAT = ITEMS.registerItem("mangrove_security_sea_boat", p -> new SecuritySeaBoatItem(getMangroveSecuritySeaBoat(), p), itemProp(1).fireResistant());
	@HasManualPage(PageGroup.SECURITY_SEA_BOATS)
	public static final DeferredItem<SecuritySeaBoatItem> CHERRY_SECURITY_SEA_BOAT = ITEMS.registerItem("cherry_security_sea_boat", p -> new SecuritySeaBoatItem(getCherrySecuritySeaBoat(), p), itemProp(1).fireResistant());
	@HasManualPage(PageGroup.SECURITY_SEA_BOATS)
	public static final DeferredItem<SecuritySeaBoatItem> PALE_OAK_SECURITY_SEA_BOAT = ITEMS.registerItem("pale_oak_security_sea_boat", p -> new SecuritySeaBoatItem(getPaleOakSecuritySeaBoat(), p), itemProp(1).fireResistant());
	@HasManualPage(PageGroup.SECURITY_SEA_BOATS)
	public static final DeferredItem<SecuritySeaBoatItem> BAMBOO_SECURITY_SEA_RAFT = ITEMS.registerItem("bamboo_security_sea_raft", p -> new SecuritySeaBoatItem(getBambooSecuritySeaRaft(), p), itemProp(1).fireResistant());
	@HasManualPage(designedBy = "Henzoid")
	public static final DeferredItem<SentryItem> SENTRY = ITEMS.registerItem("sentry", SentryItem::new, itemProp());
	public static final DeferredItem<SonicSecuritySystemItem> SONIC_SECURITY_SYSTEM_ITEM = registerItem("sonic_security_system", SonicSecuritySystemItem::new, () -> itemProp(1).component(SSS_LINKED_BLOCKS, GlobalPositions.sized(SonicSecuritySystemBlockEntity.MAX_LINKED_BLOCKS)).useBlockDescriptionPrefix());
	@HasManualPage
	public static final DeferredItem<TaserItem> TASER = ITEMS.registerItem("taser", p -> new TaserItem(p, false), itemProp().durability(151).component(DataComponents.POTION_CONTENTS, TaserItem.getDefaultEffects()));
	public static final DeferredItem<TaserItem> TASER_POWERED = ITEMS.registerItem("taser_powered", p -> new TaserItem(p, true), itemProp().durability(151).component(DataComponents.POTION_CONTENTS, TaserItem.getDefaultPoweredEffects()));
	@HasManualPage
	public static final DeferredItem<UniversalBlockModifierItem> UNIVERSAL_BLOCK_MODIFIER = ITEMS.registerItem("universal_block_modifier", UniversalBlockModifierItem::new, itemProp(1));
	@HasManualPage(PageGroup.BLOCK_REINFORCERS)
	public static final DeferredItem<UniversalBlockReinforcerItem> UNIVERSAL_BLOCK_REINFORCER_LVL_1 = ITEMS.registerItem("universal_block_reinforcer_lvl1", UniversalBlockReinforcerItem::new, itemProp().durability(300));
	@HasManualPage(PageGroup.BLOCK_REINFORCERS)
	public static final DeferredItem<UniversalBlockReinforcerItem> UNIVERSAL_BLOCK_REINFORCER_LVL_2 = ITEMS.registerItem("universal_block_reinforcer_lvl2", UniversalBlockReinforcerItem::new, itemProp().durability(2700));
	@HasManualPage(PageGroup.BLOCK_REINFORCERS)
	public static final DeferredItem<UniversalBlockReinforcerItem> UNIVERSAL_BLOCK_REINFORCER_LVL_3 = ITEMS.registerItem("universal_block_reinforcer_lvl3", UniversalBlockReinforcerItem::new, itemProp(1).rarity(Rarity.RARE));
	@HasManualPage
	public static final DeferredItem<UniversalBlockRemoverItem> UNIVERSAL_BLOCK_REMOVER = ITEMS.registerItem("universal_block_remover", UniversalBlockRemoverItem::new, itemProp().durability(476));
	@HasManualPage
	public static final DeferredItem<UniversalKeyChangerItem> UNIVERSAL_KEY_CHANGER = ITEMS.registerItem("universal_key_changer", UniversalKeyChangerItem::new, itemProp(1));
	@HasManualPage
	public static final DeferredItem<UniversalOwnerChangerItem> UNIVERSAL_OWNER_CHANGER = ITEMS.registerItem("universal_owner_changer", UniversalOwnerChangerItem::new, itemProp().durability(48));
	@HasManualPage
	public static final DeferredItem<Item> WIRE_CUTTERS = ITEMS.registerItem("wire_cutters", WireCuttersItem::new, itemProp().durability(476));

	//modules
	@HasManualPage
	public static final DeferredItem<ModuleItem> DENYLIST_MODULE = registerItem("blacklist_module", p -> new ModuleItem(p, ModuleType.DENYLIST, true, true), () -> itemProp(1).component(LIST_MODULE_DATA, ListModuleData.EMPTY));
	@HasManualPage
	public static final DeferredItem<ModuleItem> DISGUISE_MODULE = registerItem("disguise_module", p -> new ModuleItem(p, ModuleType.DISGUISE, false, true), () -> itemProp(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY).component(SAVED_BLOCK_STATE, SavedBlockState.EMPTY));
	@HasManualPage
	public static final DeferredItem<ModuleItem> HARMING_MODULE = ITEMS.registerItem("harming_module", p -> new ModuleItem(p, ModuleType.HARMING, false), itemProp(1));
	@HasManualPage
	public static final DeferredItem<ModuleItem> REDSTONE_MODULE = ITEMS.registerItem("redstone_module", p -> new ModuleItem(p, ModuleType.REDSTONE, false), itemProp(1));
	@HasManualPage
	public static final DeferredItem<ModuleItem> SMART_MODULE = ITEMS.registerItem("smart_module", p -> new ModuleItem(p, ModuleType.SMART, false), itemProp(1));
	@HasManualPage
	public static final DeferredItem<ModuleItem> STORAGE_MODULE = ITEMS.registerItem("storage_module", p -> new ModuleItem(p, ModuleType.STORAGE, false), itemProp(1));
	@HasManualPage
	public static final DeferredItem<ModuleItem> ALLOWLIST_MODULE = registerItem("whitelist_module", p -> new ModuleItem(p, ModuleType.ALLOWLIST, true, true), () -> itemProp(1).component(LIST_MODULE_DATA, ListModuleData.EMPTY));
	@HasManualPage
	public static final DeferredItem<ModuleItem> SPEED_MODULE = ITEMS.registerItem("speed_module", p -> new ModuleItem(p, ModuleType.SPEED, false), itemProp(1));

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
		return new BlockEntityType<>((pos, state) -> {
			if (state.is(REINFORCED_OBSERVER))
				return new ReinforcedObserverBlockEntity(pos, state);
			else if (state.is(MINE))
				return new MineBlockEntity(pos, state);
			else if (state.is(PANIC_BUTTON))
				return new PanicButtonBlockEntity(pos, state);
			else
				return new OwnableBlockEntity(pos, state);
		}, beOwnableBlocks.toArray(new Block[beOwnableBlocks.size()]));
	});
	//@formatter:off
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<NamedBlockEntity>> ABSTRACT_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("abstract", () -> new BlockEntityType<>((pos, state) -> {
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
			SCContent.COBBLED_DEEPSLATE_MINE.get(),
			SCContent.COBBLESTONE_MINE.get(),
			SCContent.DEEPSLATE_MINE.get(),
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
			SCContent.END_STONE_MINE.get(),
			SCContent.REINFORCED_MOVING_PISTON.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KeypadBlockEntity>> KEYPAD_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("keypad", () -> new BlockEntityType<>(KeypadBlockEntity::new, SCContent.KEYPAD.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LaserBlockBlockEntity>> LASER_BLOCK_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("laser_block", () -> new BlockEntityType<>(LaserBlockBlockEntity::new, SCContent.LASER_BLOCK.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CageTrapBlockEntity>> CAGE_TRAP_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("cage_trap", () -> new BlockEntityType<>(CageTrapBlockEntity::new, SCContent.CAGE_TRAP.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KeycardReaderBlockEntity>> KEYCARD_READER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("keycard_reader", () -> new BlockEntityType<>(KeycardReaderBlockEntity::new, SCContent.KEYCARD_READER.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<InventoryScannerBlockEntity>> INVENTORY_SCANNER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("inventory_scanner", () -> new BlockEntityType<>(InventoryScannerBlockEntity::new, SCContent.INVENTORY_SCANNER.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PortableRadarBlockEntity>> PORTABLE_RADAR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("portable_radar", () -> new BlockEntityType<>(PortableRadarBlockEntity::new, SCContent.PORTABLE_RADAR.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SecurityCameraBlockEntity>> SECURITY_CAMERA_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("security_camera", () -> new BlockEntityType<>(SecurityCameraBlockEntity::new, SCContent.SECURITY_CAMERA.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<UsernameLoggerBlockEntity>> USERNAME_LOGGER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("username_logger", () -> new BlockEntityType<>(UsernameLoggerBlockEntity::new, SCContent.USERNAME_LOGGER.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RetinalScannerBlockEntity>> RETINAL_SCANNER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("retinal_scanner", () -> new BlockEntityType<>(RetinalScannerBlockEntity::new, SCContent.RETINAL_SCANNER.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<? extends ChestBlockEntity>> KEYPAD_CHEST_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(KEYPAD_CHEST_PATH, () -> new BlockEntityType<>(KeypadChestBlockEntity::new, SCContent.KEYPAD_CHEST.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AlarmBlockEntity>> ALARM_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("alarm", () -> new BlockEntityType<>(AlarmBlockEntity::new, SCContent.ALARM.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ClaymoreBlockEntity>> CLAYMORE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("claymore", () -> new BlockEntityType<>(ClaymoreBlockEntity::new, SCContent.CLAYMORE.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KeypadFurnaceBlockEntity>> KEYPAD_FURNACE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("keypad_furnace", () -> new BlockEntityType<>(KeypadFurnaceBlockEntity::new, SCContent.KEYPAD_FURNACE.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KeypadSmokerBlockEntity>> KEYPAD_SMOKER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("keypad_smoker", () -> new BlockEntityType<>(KeypadSmokerBlockEntity::new, SCContent.KEYPAD_SMOKER.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KeypadBlastFurnaceBlockEntity>> KEYPAD_BLAST_FURNACE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("keypad_blast_furnace", () -> new BlockEntityType<>(KeypadBlastFurnaceBlockEntity::new, SCContent.KEYPAD_BLAST_FURNACE.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<IMSBlockEntity>> IMS_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("ims", () -> new BlockEntityType<>(IMSBlockEntity::new, SCContent.IMS.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ProtectoBlockEntity>> PROTECTO_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("protecto", () -> new BlockEntityType<>(ProtectoBlockEntity::new, SCContent.PROTECTO.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ScannerDoorBlockEntity>> SCANNER_DOOR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("scanner_door", () -> new BlockEntityType<>(ScannerDoorBlockEntity::new, SCContent.SCANNER_DOOR.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SecretSignBlockEntity>> SECRET_SIGN_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("secret_sign", () -> new BlockEntityType<>(SecretSignBlockEntity::new,
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
			SCContent.SECRET_PALE_OAK_SIGN.get(),
			SCContent.SECRET_PALE_OAK_WALL_SIGN.get(),
			SCContent.SECRET_BAMBOO_SIGN.get(),
			SCContent.SECRET_BAMBOO_WALL_SIGN.get(),
			SCContent.SECRET_CRIMSON_SIGN.get(),
			SCContent.SECRET_CRIMSON_WALL_SIGN.get(),
			SCContent.SECRET_WARPED_SIGN.get(),
			SCContent.SECRET_WARPED_WALL_SIGN.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SecretHangingSignBlockEntity>> SECRET_HANGING_SIGN_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("secret_hanging_sign", () -> new BlockEntityType<>(SecretHangingSignBlockEntity::new,
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
			SCContent.SECRET_PALE_OAK_HANGING_SIGN.get(),
			SCContent.SECRET_PALE_OAK_WALL_HANGING_SIGN.get(),
			SCContent.SECRET_BAMBOO_HANGING_SIGN.get(),
			SCContent.SECRET_BAMBOO_WALL_HANGING_SIGN.get(),
			SCContent.SECRET_CRIMSON_HANGING_SIGN.get(),
			SCContent.SECRET_CRIMSON_WALL_HANGING_SIGN.get(),
			SCContent.SECRET_WARPED_HANGING_SIGN.get(),
			SCContent.SECRET_WARPED_WALL_HANGING_SIGN.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MotionActivatedLightBlockEntity>> MOTION_LIGHT_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("motion_light", () -> new BlockEntityType<>(MotionActivatedLightBlockEntity::new, SCContent.MOTION_ACTIVATED_LIGHT.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TrackMineBlockEntity>> TRACK_MINE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("track_mine", () -> new BlockEntityType<>(TrackMineBlockEntity::new, SCContent.TRACK_MINE.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TrophySystemBlockEntity>> TROPHY_SYSTEM_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("trophy_system", () -> new BlockEntityType<>(TrophySystemBlockEntity::new, SCContent.TROPHY_SYSTEM.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockPocketManagerBlockEntity>> BLOCK_POCKET_MANAGER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("block_pocket_manager", () -> new BlockEntityType<>(BlockPocketManagerBlockEntity::new, SCContent.BLOCK_POCKET_MANAGER.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockPocketBlockEntity>> BLOCK_POCKET_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("block_pocket", () -> new BlockEntityType<>(BlockPocketBlockEntity::new,
			SCContent.BLOCK_POCKET_WALL.get(),
			SCContent.REINFORCED_CRYSTAL_QUARTZ_BLOCK.get(),
			SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get(),
			SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get(),
			SCContent.REINFORCED_MOVING_PISTON.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AllowlistOnlyBlockEntity>> ALLOWLIST_ONLY_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("reinforced_pressure_plate", () -> new BlockEntityType<>(AllowlistOnlyBlockEntity::new,
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
			SCContent.REINFORCED_PALE_OAK_PRESSURE_PLATE.get(),
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
			SCContent.REINFORCED_PALE_OAK_BUTTON.get(),
			SCContent.REINFORCED_SPRUCE_BUTTON.get(),
			SCContent.REINFORCED_WARPED_BUTTON.get(),
			SCContent.REINFORCED_POLISHED_BLACKSTONE_BUTTON.get(),
			SCContent.REINFORCED_LEVER.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ReinforcedHopperBlockEntity>> REINFORCED_HOPPER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("reinforced_hopper", () -> new BlockEntityType<>(ReinforcedHopperBlockEntity::new,
			SCContent.REINFORCED_HOPPER.get(),
			SCContent.REINFORCED_MOVING_PISTON.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ProjectorBlockEntity>> PROJECTOR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("projector", () -> new BlockEntityType<>(ProjectorBlockEntity::new, SCContent.PROJECTOR.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KeypadDoorBlockEntity>> KEYPAD_DOOR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("keypad_door", () -> new BlockEntityType<>(KeypadDoorBlockEntity::new, SCContent.KEYPAD_DOOR.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ReinforcedIronBarsBlockEntity>> REINFORCED_IRON_BARS_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("reinforced_iron_bars", () -> new BlockEntityType<>(ReinforcedIronBarsBlockEntity::new,
			SCContent.REINFORCED_IRON_BARS.get(),
			SCContent.REINFORCED_MOVING_PISTON.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ReinforcedCauldronBlockEntity>> REINFORCED_CAULDRON_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("reinforced_cauldron", () -> new BlockEntityType<>(ReinforcedCauldronBlockEntity::new,
			SCContent.REINFORCED_CAULDRON.get(),
			SCContent.REINFORCED_WATER_CAULDRON.get(),
			SCContent.REINFORCED_LAVA_CAULDRON.get(),
			SCContent.REINFORCED_POWDER_SNOW_CAULDRON.get(),
			SCContent.REINFORCED_MOVING_PISTON.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ReinforcedPistonMovingBlockEntity>> REINFORCED_PISTON_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("reinforced_piston", () -> new BlockEntityType<>(ReinforcedPistonMovingBlockEntity::new, SCContent.REINFORCED_MOVING_PISTON.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ValidationOwnableBlockEntity>> VALIDATION_OWNABLE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("validation_ownable", () -> new BlockEntityType<>(ValidationOwnableBlockEntity::new,
			SCContent.REINFORCED_PISTON.get(),
			SCContent.REINFORCED_STICKY_PISTON.get(),
			SCContent.REINFORCED_MOVING_PISTON.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KeyPanelBlockEntity>> KEY_PANEL_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("key_panel", () -> new BlockEntityType<>(KeyPanelBlockEntity::new, SCContent.KEY_PANEL_BLOCK.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SonicSecuritySystemBlockEntity>> SONIC_SECURITY_SYSTEM_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("sonic_security_system", () -> new BlockEntityType<>(SonicSecuritySystemBlockEntity::new, SCContent.SONIC_SECURITY_SYSTEM.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockChangeDetectorBlockEntity>> BLOCK_CHANGE_DETECTOR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("block_change_detector", () -> new BlockEntityType<>(BlockChangeDetectorBlockEntity::new, SCContent.BLOCK_CHANGE_DETECTOR.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RiftStabilizerBlockEntity>> RIFT_STABILIZER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("rift_stabilizer", () -> new BlockEntityType<>(RiftStabilizerBlockEntity::new, SCContent.RIFT_STABILIZER.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DisguisableBlockEntity>> DISGUISABLE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("disguisable", () -> new BlockEntityType<>(DisguisableBlockEntity::new, SCContent.SENTRY_DISGUISE.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DisplayCaseBlockEntity>> DISPLAY_CASE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(DISPLAY_CASE_PATH, () -> new BlockEntityType<>(DisplayCaseBlockEntity::new, SCContent.DISPLAY_CASE.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GlowDisplayCaseBlockEntity>> GLOW_DISPLAY_CASE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(GLOW_DISPLAY_CASE_PATH, () -> new BlockEntityType<>(GlowDisplayCaseBlockEntity::new, SCContent.GLOW_DISPLAY_CASE.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KeypadBarrelBlockEntity>> KEYPAD_BARREL_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("keypad_barrel", () -> new BlockEntityType<>(KeypadBarrelBlockEntity::new, SCContent.KEYPAD_BARREL.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BrushableMineBlockEntity>> BRUSHABLE_MINE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("brushable_mine", () -> new BlockEntityType<>(BrushableMineBlockEntity::new, SCContent.SUSPICIOUS_SAND_MINE.get(), SCContent.SUSPICIOUS_GRAVEL_MINE.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ReinforcedChiseledBookshelfBlockEntity>> REINFORCED_CHISELED_BOOKSHELF_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("reinforced_chiseled_bookshelf", () -> new BlockEntityType<>(ReinforcedChiseledBookshelfBlockEntity::new,
			SCContent.REINFORCED_CHISELED_BOOKSHELF.get(),
			SCContent.REINFORCED_MOVING_PISTON.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KeypadTrapdoorBlockEntity>> KEYPAD_TRAPDOOR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("keypad_trapdoor", () -> new BlockEntityType<>(KeypadTrapdoorBlockEntity::new, SCContent.KEYPAD_TRAPDOOR.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FloorTrapBlockEntity>> FLOOR_TRAP_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("floor_trap", () -> new BlockEntityType<>(FloorTrapBlockEntity::new, SCContent.FLOOR_TRAP.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KeycardLockBlockEntity>> KEYCARD_LOCK_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("keycard_lock", () -> new BlockEntityType<>(KeycardLockBlockEntity::new, SCContent.KEYCARD_LOCK.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ScannerTrapdoorBlockEntity>> SCANNER_TRAPDOOR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("scanner_trapdoor", () -> new BlockEntityType<>(ScannerTrapdoorBlockEntity::new, SCContent.SCANNER_TRAPDOOR.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ReinforcedDispenserBlockEntity>> REINFORCED_DISPENSER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("reinforced_dispenser", () -> new BlockEntityType<>(ReinforcedDispenserBlockEntity::new,
			SCContent.REINFORCED_DISPENSER.get(),
			SCContent.REINFORCED_MOVING_PISTON.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ReinforcedDropperBlockEntity>> REINFORCED_DROPPER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("reinforced_dropper", () -> new BlockEntityType<>(ReinforcedDropperBlockEntity::new,
			SCContent.REINFORCED_DROPPER.get(),
			SCContent.REINFORCED_MOVING_PISTON.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ReinforcedFenceGateBlockEntity>> REINFORCED_FENCE_GATE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("reinforced_fence_gate", () -> new BlockEntityType<>(ReinforcedFenceGateBlockEntity::new,
			SCContent.REINFORCED_OAK_FENCE_GATE.get(),
			SCContent.REINFORCED_SPRUCE_FENCE_GATE.get(),
			SCContent.REINFORCED_BIRCH_FENCE_GATE.get(),
			SCContent.REINFORCED_JUNGLE_FENCE_GATE.get(),
			SCContent.REINFORCED_ACACIA_FENCE_GATE.get(),
			SCContent.REINFORCED_DARK_OAK_FENCE_GATE.get(),
			SCContent.REINFORCED_MANGROVE_FENCE_GATE.get(),
			SCContent.REINFORCED_CHERRY_FENCE_GATE.get(),
			SCContent.REINFORCED_PALE_OAK_FENCE_GATE.get(),
			SCContent.REINFORCED_BAMBOO_FENCE_GATE.get(),
			SCContent.REINFORCED_CRIMSON_FENCE_GATE.get(),
			SCContent.REINFORCED_WARPED_FENCE_GATE.get(),
			SCContent.REINFORCED_MOVING_PISTON.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ReinforcedLecternBlockEntity>> REINFORCED_LECTERN_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("reinforced_lectern", () -> new BlockEntityType<>(ReinforcedLecternBlockEntity::new,
			SCContent.REINFORCED_LECTERN.get(),
			SCContent.REINFORCED_MOVING_PISTON.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SecureRedstoneInterfaceBlockEntity>> SECURE_REDSTONE_INTERFACE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("secure_redstone_interface", () -> new BlockEntityType<>(SecureRedstoneInterfaceBlockEntity::new, SCContent.SECURE_REDSTONE_INTERFACE.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FrameBlockEntity>> FRAME_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("frame", () -> new BlockEntityType<>(FrameBlockEntity::new, SCContent.FRAME.get()));

	//entity types
	public static final DeferredHolder<EntityType<?>, EntityType<BouncingBetty>> BOUNCING_BETTY_ENTITY = ENTITY_TYPES.register("bouncingbetty",
			() -> EntityType.Builder.<BouncingBetty>of(BouncingBetty::new, MobCategory.MISC)
			.sized(0.5F, 0.2F)
			.setTrackingRange(128)
			.setUpdateInterval(1)
			.setShouldReceiveVelocityUpdates(true)
			.build(scEntityId("bouncingbetty")));
	public static final DeferredHolder<EntityType<?>, EntityType<IMSBomb>> IMS_BOMB_ENTITY = ENTITY_TYPES.register("imsbomb",
			() -> EntityType.Builder.<IMSBomb>of(IMSBomb::new, MobCategory.MISC)
			.sized(0.25F, 0.3F)
			.setTrackingRange(256)
			.setUpdateInterval(1)
			.setShouldReceiveVelocityUpdates(true)
			.build(scEntityId("imsbomb")));
	public static final DeferredHolder<EntityType<?>, EntityType<SecurityCamera>> SECURITY_CAMERA_ENTITY = ENTITY_TYPES.register("securitycamera",
			() -> EntityType.Builder.<SecurityCamera>of(SecurityCamera::new, MobCategory.MISC)
			.sized(0.0001F, 0.0001F)
			.setTrackingRange(256)
			.setUpdateInterval(20)
			.setShouldReceiveVelocityUpdates(true)
			.build(scEntityId("securitycamera")));
	public static final DeferredHolder<EntityType<?>, EntityType<Sentry>> SENTRY_ENTITY = ENTITY_TYPES.register("sentry",
			() -> EntityType.Builder.<Sentry>of(Sentry::new, MobCategory.MISC)
			.sized(1.0F, 1.01F)
			.setTrackingRange(256)
			.setUpdateInterval(1)
			.setShouldReceiveVelocityUpdates(true)
			.eyeHeight(1.6F)
			.build(scEntityId("sentry")));
	public static final DeferredHolder<EntityType<?>, EntityType<Bullet>> BULLET_ENTITY = ENTITY_TYPES.register("bullet",
			() -> EntityType.Builder.<Bullet>of(Bullet::new, MobCategory.MISC)
			.sized(0.15F, 0.1F)
			.setTrackingRange(256)
			.setUpdateInterval(1)
			.setShouldReceiveVelocityUpdates(true)
			.build(scEntityId("bullet")));
	//@formatter:on
	public static final DeferredHolder<EntityType<?>, EntityType<SecuritySeaBoat>> OAK_SECURITY_SEA_BOAT_ENTITY = securitySeaBoat("oak_security_sea_boat", OAK_SECURITY_SEA_BOAT::asItem);
	public static final DeferredHolder<EntityType<?>, EntityType<SecuritySeaBoat>> SPRUCE_SECURITY_SEA_BOAT_ENTITY = securitySeaBoat("spruce_security_sea_boat", SPRUCE_SECURITY_SEA_BOAT::asItem);
	public static final DeferredHolder<EntityType<?>, EntityType<SecuritySeaBoat>> BIRCH_SECURITY_SEA_BOAT_ENTITY = securitySeaBoat("birch_security_sea_boat", BIRCH_SECURITY_SEA_BOAT::asItem);
	public static final DeferredHolder<EntityType<?>, EntityType<SecuritySeaBoat>> JUNGLE_SECURITY_SEA_BOAT_ENTITY = securitySeaBoat("jungle_security_sea_boat", JUNGLE_SECURITY_SEA_BOAT::asItem);
	public static final DeferredHolder<EntityType<?>, EntityType<SecuritySeaBoat>> ACACIA_SECURITY_SEA_BOAT_ENTITY = securitySeaBoat("acacia_security_sea_boat", ACACIA_SECURITY_SEA_BOAT::asItem);
	public static final DeferredHolder<EntityType<?>, EntityType<SecuritySeaBoat>> DARK_OAK_SECURITY_SEA_BOAT_ENTITY = securitySeaBoat("dark_oak_security_sea_boat", DARK_OAK_SECURITY_SEA_BOAT::asItem);
	public static final DeferredHolder<EntityType<?>, EntityType<SecuritySeaBoat>> MANGROVE_SECURITY_SEA_BOAT_ENTITY = securitySeaBoat("mangrove_security_sea_boat", MANGROVE_SECURITY_SEA_BOAT::asItem);
	public static final DeferredHolder<EntityType<?>, EntityType<SecuritySeaBoat>> CHERRY_SECURITY_SEA_BOAT_ENTITY = securitySeaBoat("cherry_security_sea_boat", CHERRY_SECURITY_SEA_BOAT::asItem);
	public static final DeferredHolder<EntityType<?>, EntityType<SecuritySeaBoat>> PALE_OAK_SECURITY_SEA_BOAT_ENTITY = securitySeaBoat("pale_oak_security_sea_boat", PALE_OAK_SECURITY_SEA_BOAT::asItem);
	public static final DeferredHolder<EntityType<?>, EntityType<SecuritySeaRaft>> BAMBOO_SECURITY_SEA_RAFT_ENTITY = securitySeaRaft("bamboo_security_sea_raft", BAMBOO_SECURITY_SEA_RAFT::asItem);

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
		return propDisguisable(MapColor.STONE, true);
	}

	private static final BlockBehaviour.Properties propDisguisable(MapColor color) {
		return propDisguisable(color, true);
	}

	private static final BlockBehaviour.Properties propDisguisable(MapColor color, boolean forceSolidOn) {
		BlockBehaviour.Properties props = prop(color).noOcclusion().dynamicShape().isRedstoneConductor(DisguisableBlock::isNormalCube).isSuffocating(DisguisableBlock::isSuffocating);

		if (forceSolidOn)
			props = props.forceSolidOn();

		return props;
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

	private static DeferredBlock<ReinforcedButtonBlock> woodenButton(String id, Block vanillaBlock, BlockSetType blockSetType) {
		return BLOCKS.registerBlock(id, p -> new ReinforcedButtonBlock(p, vanillaBlock, blockSetType, 30), setId(id, prop().mapColor(MapColor.NONE).noCollission().pushReaction(PushReaction.BLOCK).forceSolidOn()));
	}

	@SuppressWarnings("unused")
	private static DeferredBlock<ReinforcedButtonBlock> woodenButton(String id, Block vanillaBlock, BlockSetType blockSetType, FeatureFlag... requiredFeatures) {
		return BLOCKS.registerBlock(id, p -> new ReinforcedButtonBlock(p, vanillaBlock, blockSetType, 30), setId(id, prop().mapColor(MapColor.NONE).noCollission().pushReaction(PushReaction.BLOCK).forceSolidOn().requiredFeatures(requiredFeatures)));
	}

	private static DeferredBlock<ReinforcedButtonBlock> stoneButton(String id, Block vanillaBlock, BlockSetType blockSetType) {
		return BLOCKS.registerBlock(id, p -> new ReinforcedButtonBlock(p, vanillaBlock, blockSetType, 20), setId(id, prop().mapColor(MapColor.NONE).noCollission().pushReaction(PushReaction.BLOCK).forceSolidOn()));
	}

	private static DeferredBlock<ReinforcedPressurePlateBlock> woodenPressurePlate(String id, Block vanillaBlock, BlockSetType blockSetType) {
		return BLOCKS.registerBlock(id, p -> new ReinforcedPressurePlateBlock(p, vanillaBlock, blockSetType), setId(id, prop().mapColor(vanillaBlock.defaultMapColor()).noCollission().pushReaction(PushReaction.BLOCK).forceSolidOn()));
	}

	@SuppressWarnings("unused")
	private static DeferredBlock<ReinforcedPressurePlateBlock> woodenPressurePlate(String id, Block vanillaBlock, BlockSetType blockSetType, FeatureFlag... requiredFeatures) {
		return BLOCKS.registerBlock(id, p -> new ReinforcedPressurePlateBlock(p, vanillaBlock, blockSetType), setId(id, prop().mapColor(vanillaBlock.defaultMapColor()).noCollission().requiredFeatures(requiredFeatures).pushReaction(PushReaction.BLOCK).forceSolidOn()));
	}

	private static DeferredBlock<ReinforcedPressurePlateBlock> stonePressurePlate(String id, Block vanillaBlock, BlockSetType blockSetType) {
		return BLOCKS.registerBlock(id, p -> new ReinforcedPressurePlateBlock(p, vanillaBlock, blockSetType), setId(id, prop().mapColor(vanillaBlock.defaultMapColor()).noCollission().pushReaction(PushReaction.BLOCK).forceSolidOn()));
	}

	private static <I extends Item> DeferredItem<I> registerItem(String name, Function<Item.Properties, ? extends I> func, Supplier<Item.Properties> props) {
		return ITEMS.register(name, key -> func.apply(props.get().setId(ResourceKey.create(Registries.ITEM, key))));
	}

	public static BlockBehaviour.Properties setId(String id, BlockBehaviour.Properties properties) {
		return properties.setId(ResourceKey.create(Registries.BLOCK, SecurityCraft.resLoc(id)));
	}

	public static Item.Properties setId(String id, Item.Properties properties) {
		return setId(id, properties, false);
	}

	public static Item.Properties setId(String id, Item.Properties properties, boolean useBlockDescriptionPrefix) {
		properties.setId(ResourceKey.create(Registries.ITEM, SecurityCraft.resLoc(id)));

		if (useBlockDescriptionPrefix)
			properties.useBlockDescriptionPrefix();

		return properties;
	}

	private static DeferredHolder<EntityType<?>, EntityType<SecuritySeaBoat>> securitySeaBoat(String name, Supplier<Item> dropItem) {
		//@formatter:off
		return ENTITY_TYPES.register(name,
			() -> EntityType.Builder.<SecuritySeaBoat>of((type, level) -> new SecuritySeaBoat(type, level, dropItem), MobCategory.MISC)
		    .noLootTable()
			.sized(1.375F, 0.5625F)
		    .eyeHeight(0.5625F)
			.clientTrackingRange(10)
			.fireImmune()
			.build(scEntityId(name)));
		//@formatter:on
	}

	private static DeferredHolder<EntityType<?>, EntityType<SecuritySeaRaft>> securitySeaRaft(String name, Supplier<Item> dropItem) {
		//@formatter:off
		return ENTITY_TYPES.register(name,
			() -> EntityType.Builder.<SecuritySeaRaft>of((type, level) -> new SecuritySeaRaft(type, level, dropItem), MobCategory.MISC)
		    .noLootTable()
			.sized(1.375F, 0.5625F)
		    .eyeHeight(0.5625F)
			.clientTrackingRange(10)
			.fireImmune()
			.build(scEntityId(name)));
		//@formatter:on
	}

	private static ResourceKey<EntityType<?>> scEntityId(String path) {
		return ResourceKey.create(Registries.ENTITY_TYPE, SecurityCraft.resLoc(path));
	}

	public static EntityType<SecuritySeaBoat> getOakSecuritySeaBoat() {
		return OAK_SECURITY_SEA_BOAT_ENTITY.get();
	}

	public static EntityType<SecuritySeaBoat> getSpruceSecuritySeaBoat() {
		return SPRUCE_SECURITY_SEA_BOAT_ENTITY.get();
	}

	public static EntityType<SecuritySeaBoat> getBirchSecuritySeaBoat() {
		return BIRCH_SECURITY_SEA_BOAT_ENTITY.get();
	}

	public static EntityType<SecuritySeaBoat> getJungleSecuritySeaBoat() {
		return JUNGLE_SECURITY_SEA_BOAT_ENTITY.get();
	}

	public static EntityType<SecuritySeaBoat> getAcaciaSecuritySeaBoat() {
		return ACACIA_SECURITY_SEA_BOAT_ENTITY.get();
	}

	public static EntityType<SecuritySeaBoat> getDarkOakSecuritySeaBoat() {
		return DARK_OAK_SECURITY_SEA_BOAT_ENTITY.get();
	}

	public static EntityType<SecuritySeaBoat> getMangroveSecuritySeaBoat() {
		return MANGROVE_SECURITY_SEA_BOAT_ENTITY.get();
	}

	public static EntityType<SecuritySeaBoat> getCherrySecuritySeaBoat() {
		return CHERRY_SECURITY_SEA_BOAT_ENTITY.get();
	}

	public static EntityType<SecuritySeaRaft> getBambooSecuritySeaRaft() {
		return BAMBOO_SECURITY_SEA_RAFT_ENTITY.get();
	}

	public static EntityType<SecuritySeaBoat> getPaleOakSecuritySeaBoat() {
		return PALE_OAK_SECURITY_SEA_BOAT_ENTITY.get();
	}

	private SCContent() {}
}
