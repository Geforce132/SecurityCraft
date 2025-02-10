package net.geforcemods.securitycraft;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

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
import net.geforcemods.securitycraft.blockentities.CreakingHeartMineBlockEntity;
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
import net.geforcemods.securitycraft.blocks.mines.CreakingHeartMineBlock;
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
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedMudBlock;
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
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DoubleHighBlockItem;
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
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
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
	public static final DeferredBlock<AlarmBlock> ALARM = BLOCKS.registerBlock("alarm", AlarmBlock::new, prop(MapColor.COLOR_RED, 3.5F).sound(SoundType.METAL).lightLevel(state -> state.getValue(AlarmBlock.LIT) ? 15 : 0));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<BlockChangeDetectorBlock> BLOCK_CHANGE_DETECTOR = BLOCKS.registerBlock("block_change_detector", BlockChangeDetectorBlock::new, propDisguisable(3.5F));
	@HasManualPage(designedBy = "Henzoid")
	@RegisterItemBlock
	public static final DeferredBlock<BlockPocketManagerBlock> BLOCK_POCKET_MANAGER = BLOCKS.registerBlock("block_pocket_manager", BlockPocketManagerBlock::new, prop(MapColor.COLOR_CYAN, 3.5F));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<BlockPocketWallBlock> BLOCK_POCKET_WALL = BLOCKS.registerBlock("block_pocket_wall", BlockPocketWallBlock::new, prop(MapColor.COLOR_CYAN, 0.8F).noCollission().isRedstoneConductor(SCContent::never).isSuffocating(BlockPocketWallBlock::causesSuffocation).isViewBlocking(BlockPocketWallBlock::causesSuffocation).isValidSpawn(SCContent::never));
	@HasManualPage
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BouncingBettyBlock> BOUNCING_BETTY = BLOCKS.registerBlock("bouncing_betty", BouncingBettyBlock::new, prop(MapColor.METAL, 3.5F).sound(SoundType.METAL).forceSolidOn().pushReaction(PushReaction.NORMAL));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<CageTrapBlock> CAGE_TRAP = BLOCKS.registerBlock("cage_trap", CageTrapBlock::new, propDisguisable(MapColor.METAL, 5.0F).sound(SoundType.METAL).noCollission());
	@HasManualPage
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<ClaymoreBlock> CLAYMORE = BLOCKS.registerBlock("claymore", ClaymoreBlock::new, prop(MapColor.TERRACOTTA_GREEN, 3.5F).sound(SoundType.METAL).forceSolidOn().pushReaction(PushReaction.NORMAL));
	@HasManualPage(PageGroup.DISPLAY_CASES)
	public static final DeferredBlock<DisplayCaseBlock> DISPLAY_CASE = BLOCKS.registerBlock(DISPLAY_CASE_PATH, p -> new DisplayCaseBlock(p, false), prop(MapColor.METAL, 5.0F).sound(SoundType.METAL));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<FloorTrapBlock> FLOOR_TRAP = BLOCKS.registerBlock("floor_trap", FloorTrapBlock::new, propDisguisable(MapColor.METAL, 5.0F).sound(SoundType.METAL));
	@HasManualPage
	@OwnableBE
	@RegisterItemBlock
	public static final DeferredBlock<FrameBlock> FRAME = BLOCKS.registerBlock("keypad_frame", FrameBlock::new, prop(MapColor.METAL, 5.0F).sound(SoundType.METAL));
	@HasManualPage(PageGroup.DISPLAY_CASES)
	public static final DeferredBlock<DisplayCaseBlock> GLOW_DISPLAY_CASE = BLOCKS.registerBlock(GLOW_DISPLAY_CASE_PATH, p -> new DisplayCaseBlock(p, true), prop(MapColor.METAL, 5.0F).sound(SoundType.METAL));
	@HasManualPage
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<IMSBlock> IMS = BLOCKS.registerBlock("ims", IMSBlock::new, prop(MapColor.TERRACOTTA_GREEN, 3.5F).sound(SoundType.METAL));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<InventoryScannerBlock> INVENTORY_SCANNER = BLOCKS.registerBlock("inventory_scanner", InventoryScannerBlock::new, propDisguisable(3.5F));
	public static final DeferredBlock<InventoryScannerFieldBlock> INVENTORY_SCANNER_FIELD = BLOCKS.registerBlock("inventory_scanner_field", InventoryScannerFieldBlock::new, prop(MapColor.NONE, -1.0F).noLootTable());
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<ElectrifiedIronFenceBlock> ELECTRIFIED_IRON_FENCE = BLOCKS.registerBlock("electrified_iron_fence", ElectrifiedIronFenceBlock::new, prop(MapColor.METAL, 5.0F).sound(SoundType.METAL));
	public static final DeferredBlock<KeyPanelBlock> KEY_PANEL_BLOCK = BLOCKS.registerBlock("key_panel", KeyPanelBlock::new, prop(MapColor.METAL, 3.5F).sound(SoundType.METAL));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<KeycardLockBlock> KEYCARD_LOCK = BLOCKS.registerBlock("keycard_lock", KeycardLockBlock::new, prop(2.0F));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<KeycardReaderBlock> KEYCARD_READER = BLOCKS.registerBlock("keycard_reader", KeycardReaderBlock::new, propDisguisable(3.5F));
	@HasManualPage(hasRecipeDescription = true)
	@RegisterItemBlock
	public static final DeferredBlock<KeypadBlock> KEYPAD = BLOCKS.registerBlock("keypad", KeypadBlock::new, propDisguisable(MapColor.METAL, 5.0F).sound(SoundType.METAL));
	@HasManualPage(hasRecipeDescription = true)
	@RegisterItemBlock
	public static final DeferredBlock<KeypadBarrelBlock> KEYPAD_BARREL = BLOCKS.registerBlock("keypad_barrel", KeypadBarrelBlock::new, propDisguisable(MapColor.METAL, 5.0F).sound(SoundType.METAL));
	@HasManualPage(hasRecipeDescription = true)
	public static final DeferredBlock<KeypadChestBlock> KEYPAD_CHEST = BLOCKS.registerBlock(KEYPAD_CHEST_PATH, KeypadChestBlock::new, propDisguisable(MapColor.METAL, 5.0F).sound(SoundType.METAL));
	public static final DeferredBlock<KeypadDoorBlock> KEYPAD_DOOR = BLOCKS.registerBlock("keypad_door", p -> new KeypadDoorBlock(p, BlockSetType.IRON), propDisguisable(MapColor.METAL, 5.0F).sound(SoundType.METAL).pushReaction(PushReaction.BLOCK));
	@HasManualPage(hasRecipeDescription = true)
	@RegisterItemBlock
	public static final DeferredBlock<KeypadTrapDoorBlock> KEYPAD_TRAPDOOR = BLOCKS.registerBlock("keypad_trapdoor", p -> new KeypadTrapDoorBlock(p, BlockSetType.IRON), propDisguisable(MapColor.METAL, 5.0F).sound(SoundType.METAL).isValidSpawn(SCContent::never));
	@HasManualPage(hasRecipeDescription = true)
	@RegisterItemBlock
	public static final DeferredBlock<KeypadFurnaceBlock> KEYPAD_FURNACE = BLOCKS.registerBlock("keypad_furnace", KeypadFurnaceBlock::new, prop(MapColor.METAL, 5.0F).sound(SoundType.METAL).lightLevel(state -> state.getValue(AbstractKeypadFurnaceBlock.LIT) ? 13 : 0));
	@HasManualPage(hasRecipeDescription = true)
	@RegisterItemBlock
	public static final DeferredBlock<KeypadSmokerBlock> KEYPAD_SMOKER = BLOCKS.registerBlock("keypad_smoker", KeypadSmokerBlock::new, prop(MapColor.METAL, 5.0F).sound(SoundType.METAL).lightLevel(state -> state.getValue(AbstractKeypadFurnaceBlock.LIT) ? 13 : 0));
	@HasManualPage(hasRecipeDescription = true)
	@RegisterItemBlock
	public static final DeferredBlock<KeypadBlastFurnaceBlock> KEYPAD_BLAST_FURNACE = BLOCKS.registerBlock("keypad_blast_furnace", KeypadBlastFurnaceBlock::new, prop(MapColor.METAL, 5.0F).sound(SoundType.METAL).lightLevel(state -> state.getValue(AbstractKeypadFurnaceBlock.LIT) ? 13 : 0));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<LaserBlock> LASER_BLOCK = BLOCKS.registerBlock("laser_block", LaserBlock::new, propDisguisable(3.5F));
	public static final DeferredBlock<LaserFieldBlock> LASER_FIELD = BLOCKS.registerBlock("laser", LaserFieldBlock::new, prop(MapColor.NONE, -1.0F).noLootTable());
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<MotionActivatedLightBlock> MOTION_ACTIVATED_LIGHT = BLOCKS.registerBlock("motion_activated_light", MotionActivatedLightBlock::new, prop(MapColor.NONE, 5.0F).sound(SoundType.GLASS).lightLevel(state -> state.getValue(MotionActivatedLightBlock.LIT) ? 15 : 0));
	@HasManualPage
	@OwnableBE
	@RegisterItemBlock
	public static final DeferredBlock<PanicButtonBlock> PANIC_BUTTON = BLOCKS.registerBlock("panic_button", p -> new PanicButtonBlock(p, BlockSetType.STONE, -1), prop(3.5F).lightLevel(state -> state.getValue(ButtonBlock.POWERED) ? 4 : 0));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<PortableRadarBlock> PORTABLE_RADAR = BLOCKS.registerBlock("portable_radar", PortableRadarBlock::new, prop(MapColor.COLOR_BLACK, 5.0F));
	@HasManualPage
	@OwnableBE
	@RegisterItemBlock
	public static final DeferredBlock<ProjectorBlock> PROJECTOR = BLOCKS.registerBlock("projector", ProjectorBlock::new, propDisguisable(MapColor.METAL, 5.0F).sound(SoundType.METAL));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<ProtectoBlock> PROTECTO = BLOCKS.registerBlock("protecto", ProtectoBlock::new, propDisguisable(MapColor.METAL, 10.0F).sound(SoundType.METAL).lightLevel(state -> 7));
	@OwnableBE
	public static final DeferredBlock<ReinforcedDoorBlock> REINFORCED_DOOR = BLOCKS.registerBlock("iron_door_reinforced", ReinforcedDoorBlock::new, prop(MapColor.METAL, 5.0F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.BLOCK));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<ElectrifiedIronFenceGateBlock> ELECTRIFIED_IRON_FENCE_GATE = BLOCKS.registerBlock("reinforced_fence_gate", ElectrifiedIronFenceGateBlock::new, prop(MapColor.METAL, 5.0F).sound(SoundType.METAL).forceSolidOn());
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<RetinalScannerBlock> RETINAL_SCANNER = BLOCKS.registerBlock("retinal_scanner", RetinalScannerBlock::new, propDisguisable(3.5F));
	public static final DeferredBlock<RiftStabilizerBlock> RIFT_STABILIZER = BLOCKS.registerBlock("rift_stabilizer", RiftStabilizerBlock::new, propDisguisable(MapColor.METAL, 5.0F).sound(SoundType.METAL));
	public static final DeferredBlock<ScannerDoorBlock> SCANNER_DOOR = BLOCKS.registerBlock("scanner_door", p -> new ScannerDoorBlock(p, BlockSetType.IRON), propDisguisable(MapColor.METAL, 5.0F).sound(SoundType.METAL).pushReaction(PushReaction.BLOCK));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<ScannerTrapDoorBlock> SCANNER_TRAPDOOR = BLOCKS.registerBlock("scanner_trapdoor", p -> new ScannerTrapDoorBlock(p, BlockSetType.IRON), propDisguisable(MapColor.METAL, 5.0F).sound(SoundType.METAL).isValidSpawn(SCContent::never));
	public static final DeferredBlock<SecretStandingSignBlock> SECRET_OAK_SIGN = secretStandingSign("secret_sign_standing", Blocks.OAK_SIGN, p -> p.overrideDescription("block.securitycraft.secret_sign"));
	public static final DeferredBlock<SecretWallSignBlock> SECRET_OAK_WALL_SIGN = secretWallSign("secret_sign_wall", Blocks.OAK_SIGN, p -> p.overrideDescription("block.securitycraft.secret_sign"));
	public static final DeferredBlock<SecretStandingSignBlock> SECRET_SPRUCE_SIGN = secretStandingSign("secret_spruce_sign_standing", Blocks.SPRUCE_SIGN, p -> p.overrideDescription("block.securitycraft.secret_spruce_sign"));
	public static final DeferredBlock<SecretWallSignBlock> SECRET_SPRUCE_WALL_SIGN = secretWallSign("secret_spruce_sign_wall", Blocks.SPRUCE_SIGN, p -> p.overrideDescription("block.securitycraft.secret_spruce_sign"));
	public static final DeferredBlock<SecretStandingSignBlock> SECRET_BIRCH_SIGN = secretStandingSign("secret_birch_sign_standing", Blocks.BIRCH_SIGN, p -> p.overrideDescription("block.securitycraft.secret_birch_sign"));
	public static final DeferredBlock<SecretWallSignBlock> SECRET_BIRCH_WALL_SIGN = secretWallSign("secret_birch_sign_wall", Blocks.BIRCH_SIGN, p -> p.overrideDescription("block.securitycraft.secret_birch_sign"));
	public static final DeferredBlock<SecretStandingSignBlock> SECRET_JUNGLE_SIGN = secretStandingSign("secret_jungle_sign_standing", Blocks.JUNGLE_SIGN, p -> p.overrideDescription("block.securitycraft.secret_jungle_sign"));
	public static final DeferredBlock<SecretWallSignBlock> SECRET_JUNGLE_WALL_SIGN = secretWallSign("secret_jungle_sign_wall", Blocks.JUNGLE_SIGN, p -> p.overrideDescription("block.securitycraft.secret_jungle_sign"));
	public static final DeferredBlock<SecretStandingSignBlock> SECRET_ACACIA_SIGN = secretStandingSign("secret_acacia_sign_standing", Blocks.ACACIA_SIGN, p -> p.overrideDescription("block.securitycraft.secret_acacia_sign"));
	public static final DeferredBlock<SecretWallSignBlock> SECRET_ACACIA_WALL_SIGN = secretWallSign("secret_acacia_sign_wall", Blocks.ACACIA_SIGN, p -> p.overrideDescription("block.securitycraft.secret_acacia_sign"));
	public static final DeferredBlock<SecretStandingSignBlock> SECRET_DARK_OAK_SIGN = secretStandingSign("secret_dark_oak_sign_standing", Blocks.DARK_OAK_SIGN, p -> p.overrideDescription("block.securitycraft.secret_dark_oak_sign"));
	public static final DeferredBlock<SecretWallSignBlock> SECRET_DARK_OAK_WALL_SIGN = secretWallSign("secret_dark_oak_sign_wall", Blocks.DARK_OAK_SIGN, p -> p.overrideDescription("block.securitycraft.secret_dark_oak_sign"));
	public static final DeferredBlock<SecretStandingSignBlock> SECRET_MANGROVE_SIGN = secretStandingSign("secret_mangrove_sign_standing", Blocks.MANGROVE_SIGN, p -> p.overrideDescription("block.securitycraft.secret_mangrove_sign"));
	public static final DeferredBlock<SecretWallSignBlock> SECRET_MANGROVE_WALL_SIGN = secretWallSign("secret_mangrove_sign_wall", Blocks.MANGROVE_SIGN, p -> p.overrideDescription("block.securitycraft.secret_mangrove_sign"));
	public static final DeferredBlock<SecretStandingSignBlock> SECRET_CRIMSON_SIGN = secretStandingSign("secret_crimson_sign_standing", Blocks.CRIMSON_SIGN, p -> p.overrideDescription("block.securitycraft.secret_crimson_sign"));
	public static final DeferredBlock<SecretWallSignBlock> SECRET_CRIMSON_WALL_SIGN = secretWallSign("secret_crimson_sign_wall", Blocks.CRIMSON_SIGN, p -> p.overrideDescription("block.securitycraft.secret_crimson_sign"));
	public static final DeferredBlock<SecretStandingSignBlock> SECRET_WARPED_SIGN = secretStandingSign("secret_warped_sign_standing", Blocks.WARPED_SIGN, p -> p.overrideDescription("block.securitycraft.secret_warped_sign"));
	public static final DeferredBlock<SecretWallSignBlock> SECRET_WARPED_WALL_SIGN = secretWallSign("secret_warped_sign_wall", Blocks.WARPED_SIGN, p -> p.overrideDescription("block.securitycraft.secret_warped_sign"));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<SecureRedstoneInterfaceBlock> SECURE_REDSTONE_INTERFACE = BLOCKS.registerBlock("secure_redstone_interface", SecureRedstoneInterfaceBlock::new, propDisguisable(3.5F));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<SecurityCameraBlock> SECURITY_CAMERA = BLOCKS.registerBlock("security_camera", SecurityCameraBlock::new, propDisguisable(MapColor.METAL, 5.0F, false).noCollission());
	@HasManualPage
	public static final DeferredBlock<SonicSecuritySystemBlock> SONIC_SECURITY_SYSTEM = BLOCKS.registerBlock("sonic_security_system", SonicSecuritySystemBlock::new, propDisguisable(MapColor.METAL, 5.0F, false).sound(SoundType.METAL).noCollission());
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<TrackMineBlock> TRACK_MINE = BLOCKS.registerBlock("track_mine", TrackMineBlock::new, prop(MapColor.METAL, 0.7F).noCollission().sound(SoundType.METAL));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<TrophySystemBlock> TROPHY_SYSTEM = BLOCKS.registerBlock("trophy_system", TrophySystemBlock::new, propDisguisable(MapColor.METAL, 5.0F, false).sound(SoundType.METAL));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<UsernameLoggerBlock> USERNAME_LOGGER = BLOCKS.registerBlock("username_logger", UsernameLoggerBlock::new, propDisguisable(3.5F));
	@HasManualPage
	@OwnableBE
	@RegisterItemBlock
	public static final DeferredBlock<MineBlock> MINE = BLOCKS.registerBlock("mine", MineBlock::new, prop(MapColor.METAL, 3.5F).sound(SoundType.METAL).forceSolidOn().pushReaction(PushReaction.NORMAL));
	public static final DeferredBlock<FakeWaterBlock> FAKE_WATER_BLOCK = BLOCKS.registerBlock("fake_water_block", p -> new FakeWaterBlock(p, FAKE_WATER.get()), reinforcedCopy(Blocks.WATER));
	public static final DeferredBlock<FakeLavaBlock> FAKE_LAVA_BLOCK = BLOCKS.registerBlock("fake_lava_block", p -> new FakeLavaBlock(p, FAKE_LAVA.get()), reinforcedCopy(Blocks.LAVA));

	//block mines
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> STONE_MINE = blockMine("stone_mine", Blocks.STONE);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<DeepslateMineBlock> DEEPSLATE_MINE = reinforcedBlock("deepslate_mine", Blocks.DEEPSLATE, DeepslateMineBlock::new);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> COBBLED_DEEPSLATE_MINE = blockMine("cobbled_deepslate_mine", Blocks.COBBLED_DEEPSLATE);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> DIRT_MINE = blockMine("dirt_mine", Blocks.DIRT);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> COBBLESTONE_MINE = blockMine("cobblestone_mine", Blocks.COBBLESTONE);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<FallingBlockMineBlock> SAND_MINE = reinforcedBlock("sand_mine", Blocks.SAND, FallingBlockMineBlock::new);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<FallingBlockMineBlock> GRAVEL_MINE = reinforcedBlock("gravel_mine", Blocks.GRAVEL, FallingBlockMineBlock::new);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> NETHERRACK_MINE = blockMine("netherrack_mine", Blocks.NETHERRACK);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> END_STONE_MINE = blockMine("end_stone_mine", Blocks.END_STONE);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> COAL_ORE_MINE = blockMine("coal_mine", Blocks.COAL_ORE);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> DEEPSLATE_COAL_ORE_MINE = blockMine("deepslate_coal_mine", Blocks.DEEPSLATE_COAL_ORE);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> IRON_ORE_MINE = blockMine("iron_mine", Blocks.IRON_ORE);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> DEEPSLATE_IRON_ORE_MINE = blockMine("deepslate_iron_mine", Blocks.DEEPSLATE_IRON_ORE);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> GOLD_ORE_MINE = blockMine("gold_mine", Blocks.GOLD_ORE);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> DEEPSLATE_GOLD_ORE_MINE = blockMine("deepslate_gold_mine", Blocks.DEEPSLATE_GOLD_ORE);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> COPPER_ORE_MINE = blockMine("copper_mine", Blocks.COPPER_ORE);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> DEEPSLATE_COPPER_ORE_MINE = blockMine("deepslate_copper_mine", Blocks.DEEPSLATE_COPPER_ORE);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<RedstoneOreMineBlock> REDSTONE_ORE_MINE = reinforcedBlock("redstone_mine", Blocks.REDSTONE_ORE, RedstoneOreMineBlock::new);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<RedstoneOreMineBlock> DEEPSLATE_REDSTONE_ORE_MINE = reinforcedBlock("deepslate_redstone_mine", Blocks.DEEPSLATE_REDSTONE_ORE, RedstoneOreMineBlock::new);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> EMERALD_ORE_MINE = blockMine("emerald_mine", Blocks.EMERALD_ORE);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> DEEPSLATE_EMERALD_ORE_MINE = blockMine("deepslate_emerald_mine", Blocks.DEEPSLATE_EMERALD_ORE);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> LAPIS_ORE_MINE = blockMine("lapis_mine", Blocks.LAPIS_ORE);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> DEEPSLATE_LAPIS_ORE_MINE = blockMine("deepslate_lapis_mine", Blocks.DEEPSLATE_LAPIS_ORE);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> DIAMOND_ORE_MINE = blockMine("diamond_mine", Blocks.DIAMOND_ORE);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> DEEPSLATE_DIAMOND_ORE_MINE = blockMine("deepslate_diamond_mine", Blocks.DEEPSLATE_DIAMOND_ORE);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> NETHER_GOLD_ORE_MINE = blockMine("nether_gold_mine", Blocks.NETHER_GOLD_ORE);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> QUARTZ_ORE_MINE = blockMine("quartz_mine", Blocks.NETHER_QUARTZ_ORE);
	@HasManualPage(PageGroup.BLOCK_MINES)
	public static final DeferredBlock<BaseFullMineBlock> ANCIENT_DEBRIS_MINE = blockMine("ancient_debris_mine", Blocks.ANCIENT_DEBRIS);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BaseFullMineBlock> GILDED_BLACKSTONE_MINE = blockMine("gilded_blackstone_mine", Blocks.GILDED_BLACKSTONE);
	@HasManualPage(PageGroup.FURNACE_MINES)
	@OwnableBE
	@RegisterItemBlock
	public static final DeferredBlock<FurnaceMineBlock> FURNACE_MINE = reinforcedBlock("furnace_mine", Blocks.FURNACE, FurnaceMineBlock::new, p -> p.lightLevel(state -> 0));
	@HasManualPage(PageGroup.FURNACE_MINES)
	@OwnableBE
	@RegisterItemBlock
	public static final DeferredBlock<FurnaceMineBlock> SMOKER_MINE = reinforcedBlock("smoker_mine", Blocks.SMOKER, FurnaceMineBlock::new, p -> p.lightLevel(state -> 0));
	@HasManualPage(PageGroup.FURNACE_MINES)
	@OwnableBE
	@RegisterItemBlock
	public static final DeferredBlock<FurnaceMineBlock> BLAST_FURNACE_MINE = reinforcedBlock("blast_furnace_mine", Blocks.BLAST_FURNACE, FurnaceMineBlock::new, p -> p.lightLevel(state -> 0));
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BrushableMineBlock> SUSPICIOUS_SAND_MINE = reinforcedBlock("suspicious_sand_mine", Blocks.SUSPICIOUS_SAND, BrushableMineBlock::new);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<BrushableMineBlock> SUSPICIOUS_GRAVEL_MINE = reinforcedBlock("suspicious_gravel_mine", Blocks.SUSPICIOUS_GRAVEL, BrushableMineBlock::new);
	@HasManualPage(PageGroup.BLOCK_MINES)
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES)
	public static final DeferredBlock<CreakingHeartMineBlock> CREAKING_HEART_MINE = reinforcedBlock("creaking_heart_mine", Blocks.CREAKING_HEART, CreakingHeartMineBlock::new);

	//reinforced blocks (ordered by vanilla <1.19.3 building blocks creative tab order)
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_STONE = reinforcedBlock("reinforced_stone", Blocks.STONE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_GRANITE = reinforcedBlock("reinforced_granite", Blocks.GRANITE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_POLISHED_GRANITE = reinforcedBlock("reinforced_polished_granite", Blocks.POLISHED_GRANITE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_DIORITE = reinforcedBlock("reinforced_diorite", Blocks.DIORITE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_POLISHED_DIORITE = reinforcedBlock("reinforced_polished_diorite", Blocks.POLISHED_DIORITE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_ANDESITE = reinforcedBlock("reinforced_andesite", Blocks.ANDESITE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_POLISHED_ANDESITE = reinforcedBlock("reinforced_polished_andesite", Blocks.POLISHED_ANDESITE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_DEEPSLATE = reinforcedBlock("reinforced_deepslate", Blocks.DEEPSLATE, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_COBBLED_DEEPSLATE = reinforcedBlock("reinforced_cobbled_deepslate", Blocks.COBBLED_DEEPSLATE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_POLISHED_DEEPSLATE = reinforcedBlock("reinforced_polished_deepslate", Blocks.POLISHED_DEEPSLATE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CALCITE = reinforcedBlock("reinforced_calcite", Blocks.CALCITE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_TUFF = reinforcedBlock("reinforced_tuff", Blocks.TUFF);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_DRIPSTONE_BLOCK = reinforcedBlock("reinforced_dripstone_block", Blocks.DRIPSTONE_BLOCK);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGrassBlock> REINFORCED_GRASS_BLOCK = reinforcedBlock("reinforced_grass_block", Blocks.GRASS_BLOCK, (p, b) -> new ReinforcedGrassBlock(p));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_DIRT = reinforcedBlock("reinforced_dirt", Blocks.DIRT);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_COARSE_DIRT = reinforcedBlock("reinforced_coarse_dirt", Blocks.COARSE_DIRT);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSnowyDirtBlock> REINFORCED_PODZOL = reinforcedBlock("reinforced_podzol", Blocks.PODZOL, ReinforcedSnowyDirtBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedMudBlock> REINFORCED_MUD = reinforcedBlock("reinforced_mud", Blocks.MUD, ReinforcedMudBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedNyliumBlock> REINFORCED_CRIMSON_NYLIUM = reinforcedBlock("reinforced_crimson_nylium", Blocks.CRIMSON_NYLIUM, ReinforcedNyliumBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedNyliumBlock> REINFORCED_WARPED_NYLIUM = reinforcedBlock("reinforced_warped_nylium", Blocks.WARPED_NYLIUM, ReinforcedNyliumBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRootedDirtBlock> REINFORCED_ROOTED_DIRT = reinforcedBlock("reinforced_rooted_dirt", Blocks.ROOTED_DIRT, ReinforcedRootedDirtBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_COBBLESTONE = reinforcedBlock("reinforced_cobblestone", Blocks.COBBLESTONE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_OAK_PLANKS = reinforcedBlock("reinforced_oak_planks", Blocks.OAK_PLANKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_SPRUCE_PLANKS = reinforcedBlock("reinforced_spruce_planks", Blocks.SPRUCE_PLANKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BIRCH_PLANKS = reinforcedBlock("reinforced_birch_planks", Blocks.BIRCH_PLANKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_JUNGLE_PLANKS = reinforcedBlock("reinforced_jungle_planks", Blocks.JUNGLE_PLANKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_ACACIA_PLANKS = reinforcedBlock("reinforced_acacia_planks", Blocks.ACACIA_PLANKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_DARK_OAK_PLANKS = reinforcedBlock("reinforced_dark_oak_planks", Blocks.DARK_OAK_PLANKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_MANGROVE_PLANKS = reinforcedBlock("reinforced_mangrove_planks", Blocks.MANGROVE_PLANKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CRIMSON_PLANKS = reinforcedBlock("reinforced_crimson_planks", Blocks.CRIMSON_PLANKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_WARPED_PLANKS = reinforcedBlock("reinforced_warped_planks", Blocks.WARPED_PLANKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFallingBlock> REINFORCED_SAND = reinforcedBlock("reinforced_sand", Blocks.SAND, ReinforcedFallingBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFallingBlock> REINFORCED_RED_SAND = reinforcedBlock("reinforced_red_sand", Blocks.RED_SAND, ReinforcedFallingBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFallingBlock> REINFORCED_GRAVEL = reinforcedBlock("reinforced_gravel", Blocks.GRAVEL, ReinforcedFallingBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_COAL_BLOCK = reinforcedBlock("reinforced_coal_block", Blocks.COAL_BLOCK);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_RAW_IRON_BLOCK = reinforcedBlock("reinforced_raw_iron_block", Blocks.RAW_IRON_BLOCK);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_RAW_COPPER_BLOCK = reinforcedBlock("reinforced_raw_copper_block", Blocks.RAW_COPPER_BLOCK);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_RAW_GOLD_BLOCK = reinforcedBlock("reinforced_raw_gold_block", Blocks.RAW_GOLD_BLOCK);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedAmethystBlock> REINFORCED_AMETHYST_BLOCK = reinforcedBlock("reinforced_amethyst_block", Blocks.AMETHYST_BLOCK, ReinforcedAmethystBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_IRON_BLOCK = reinforcedBlock("reinforced_iron_block", Blocks.IRON_BLOCK);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_COPPER_BLOCK = reinforcedBlock("reinforced_copper_block", Blocks.COPPER_BLOCK);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_GOLD_BLOCK = reinforcedBlock("reinforced_gold_block", Blocks.GOLD_BLOCK);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_DIAMOND_BLOCK = reinforcedBlock("reinforced_diamond_block", Blocks.DIAMOND_BLOCK);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_NETHERITE_BLOCK = reinforcedBlock("reinforced_netherite_block", Blocks.NETHERITE_BLOCK);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_EXPOSED_COPPER = reinforcedBlock("reinforced_exposed_copper", Blocks.EXPOSED_COPPER);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_WEATHERED_COPPER = reinforcedBlock("reinforced_weathered_copper", Blocks.WEATHERED_COPPER);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_OXIDIZED_COPPER = reinforcedBlock("reinforced_oxidized_copper", Blocks.OXIDIZED_COPPER);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CUT_COPPER = reinforcedBlock("reinforced_cut_copper", Blocks.CUT_COPPER);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_EXPOSED_CUT_COPPER = reinforcedBlock("reinforced_exposed_cut_copper", Blocks.EXPOSED_CUT_COPPER);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_WEATHERED_CUT_COPPER = reinforcedBlock("reinforced_weathered_cut_copper", Blocks.WEATHERED_CUT_COPPER);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_OXIDIZED_CUT_COPPER = reinforcedBlock("reinforced_oxidized_cut_copper", Blocks.OXIDIZED_CUT_COPPER);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_CUT_COPPER_STAIRS = reinforcedBlock("reinforced_cut_copper_stairs", Blocks.CUT_COPPER_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_EXPOSED_CUT_COPPER_STAIRS = reinforcedBlock("reinforced_exposed_cut_copper_stairs", Blocks.EXPOSED_CUT_COPPER_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_WEATHERED_CUT_COPPER_STAIRS = reinforcedBlock("reinforced_weathered_cut_copper_stairs", Blocks.WEATHERED_CUT_COPPER_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_OXIDIZED_CUT_COPPER_STAIRS = reinforcedBlock("reinforced_oxidized_cut_copper_stairs", Blocks.OXIDIZED_CUT_COPPER_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_CUT_COPPER_SLAB = reinforcedBlock("reinforced_cut_copper_slab", Blocks.CUT_COPPER_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_EXPOSED_CUT_COPPER_SLAB = reinforcedBlock("reinforced_exposed_cut_copper_slab", Blocks.EXPOSED_CUT_COPPER_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_WEATHERED_CUT_COPPER_SLAB = reinforcedBlock("reinforced_weathered_cut_copper_slab", Blocks.WEATHERED_CUT_COPPER_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_OXIDIZED_CUT_COPPER_SLAB = reinforcedBlock("reinforced_oxidized_cut_copper_slab", Blocks.OXIDIZED_CUT_COPPER_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_OAK_LOG = reinforcedBlock("reinforced_oak_log", Blocks.OAK_LOG, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_SPRUCE_LOG = reinforcedBlock("reinforced_spruce_log", Blocks.SPRUCE_LOG, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_BIRCH_LOG = reinforcedBlock("reinforced_birch_log", Blocks.BIRCH_LOG, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_JUNGLE_LOG = reinforcedBlock("reinforced_jungle_log", Blocks.JUNGLE_LOG, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_ACACIA_LOG = reinforcedBlock("reinforced_acacia_log", Blocks.ACACIA_LOG, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_DARK_OAK_LOG = reinforcedBlock("reinforced_dark_oak_log", Blocks.DARK_OAK_LOG, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_MANGROVE_LOG = reinforcedBlock("reinforced_mangrove_log", Blocks.MANGROVE_LOG, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_CRIMSON_STEM = reinforcedBlock("reinforced_crimson_stem", Blocks.CRIMSON_STEM, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_WARPED_STEM = reinforcedBlock("reinforced_warped_stem", Blocks.WARPED_STEM, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_OAK_LOG = reinforcedBlock("reinforced_stripped_oak_log", Blocks.STRIPPED_OAK_LOG, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_SPRUCE_LOG = reinforcedBlock("reinforced_stripped_spruce_log", Blocks.STRIPPED_SPRUCE_LOG, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_BIRCH_LOG = reinforcedBlock("reinforced_stripped_birch_log", Blocks.STRIPPED_BIRCH_LOG, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_JUNGLE_LOG = reinforcedBlock("reinforced_stripped_jungle_log", Blocks.STRIPPED_JUNGLE_LOG, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_ACACIA_LOG = reinforcedBlock("reinforced_stripped_acacia_log", Blocks.STRIPPED_ACACIA_LOG, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_DARK_OAK_LOG = reinforcedBlock("reinforced_stripped_dark_oak_log", Blocks.STRIPPED_DARK_OAK_LOG, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_MANGROVE_LOG = reinforcedBlock("reinforced_stripped_mangrove_log", Blocks.STRIPPED_MANGROVE_LOG, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_CRIMSON_STEM = reinforcedBlock("reinforced_stripped_crimson_stem", Blocks.STRIPPED_CRIMSON_STEM, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_WARPED_STEM = reinforcedBlock("reinforced_stripped_warped_stem", Blocks.STRIPPED_WARPED_STEM, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_OAK_WOOD = reinforcedBlock("reinforced_stripped_oak_wood", Blocks.STRIPPED_OAK_WOOD, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_SPRUCE_WOOD = reinforcedBlock("reinforced_stripped_spruce_wood", Blocks.STRIPPED_SPRUCE_WOOD, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_BIRCH_WOOD = reinforcedBlock("reinforced_stripped_birch_wood", Blocks.STRIPPED_BIRCH_WOOD, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_JUNGLE_WOOD = reinforcedBlock("reinforced_stripped_jungle_wood", Blocks.STRIPPED_JUNGLE_WOOD, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_ACACIA_WOOD = reinforcedBlock("reinforced_stripped_acacia_wood", Blocks.STRIPPED_ACACIA_WOOD, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_DARK_OAK_WOOD = reinforcedBlock("reinforced_stripped_dark_oak_wood", Blocks.STRIPPED_DARK_OAK_WOOD, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_MANGROVE_WOOD = reinforcedBlock("reinforced_stripped_mangrove_wood", Blocks.STRIPPED_MANGROVE_WOOD, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_CRIMSON_HYPHAE = reinforcedBlock("reinforced_stripped_crimson_hyphae", Blocks.STRIPPED_CRIMSON_HYPHAE, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_WARPED_HYPHAE = reinforcedBlock("reinforced_stripped_warped_hyphae", Blocks.STRIPPED_WARPED_HYPHAE, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_OAK_WOOD = reinforcedBlock("reinforced_oak_wood", Blocks.OAK_WOOD, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_SPRUCE_WOOD = reinforcedBlock("reinforced_spruce_wood", Blocks.SPRUCE_WOOD, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_BIRCH_WOOD = reinforcedBlock("reinforced_birch_wood", Blocks.BIRCH_WOOD, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_JUNGLE_WOOD = reinforcedBlock("reinforced_jungle_wood", Blocks.JUNGLE_WOOD, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_ACACIA_WOOD = reinforcedBlock("reinforced_acacia_wood", Blocks.ACACIA_WOOD, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_DARK_OAK_WOOD = reinforcedBlock("reinforced_dark_oak_wood", Blocks.DARK_OAK_WOOD, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_MANGROVE_WOOD = reinforcedBlock("reinforced_mangrove_wood", Blocks.MANGROVE_WOOD, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_CRIMSON_HYPHAE = reinforcedBlock("reinforced_crimson_hyphae", Blocks.CRIMSON_HYPHAE, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_WARPED_HYPHAE = reinforcedBlock("reinforced_warped_hyphae", Blocks.WARPED_HYPHAE, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedGlassBlock> REINFORCED_GLASS = reinforcedBlock("reinforced_glass", Blocks.GLASS, ReinforcedGlassBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedTintedGlassBlock> REINFORCED_TINTED_GLASS = reinforcedBlock("reinforced_tinted_glass", Blocks.TINTED_GLASS, ReinforcedTintedGlassBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_LAPIS_BLOCK = reinforcedBlock("reinforced_lapis_block", Blocks.LAPIS_BLOCK);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_SANDSTONE = reinforcedBlock("reinforced_sandstone", Blocks.SANDSTONE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CHISELED_SANDSTONE = reinforcedBlock("reinforced_chiseled_sandstone", Blocks.CHISELED_SANDSTONE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CUT_SANDSTONE = reinforcedBlock("reinforced_cut_sandstone", Blocks.CUT_SANDSTONE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_WHITE_WOOL = reinforcedBlock("reinforced_white_wool", Blocks.WHITE_WOOL);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_ORANGE_WOOL = reinforcedBlock("reinforced_orange_wool", Blocks.ORANGE_WOOL);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_MAGENTA_WOOL = reinforcedBlock("reinforced_magenta_wool", Blocks.MAGENTA_WOOL);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_LIGHT_BLUE_WOOL = reinforcedBlock("reinforced_light_blue_wool", Blocks.LIGHT_BLUE_WOOL);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_YELLOW_WOOL = reinforcedBlock("reinforced_yellow_wool", Blocks.YELLOW_WOOL);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_LIME_WOOL = reinforcedBlock("reinforced_lime_wool", Blocks.LIME_WOOL);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_PINK_WOOL = reinforcedBlock("reinforced_pink_wool", Blocks.PINK_WOOL);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_GRAY_WOOL = reinforcedBlock("reinforced_gray_wool", Blocks.GRAY_WOOL);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_LIGHT_GRAY_WOOL = reinforcedBlock("reinforced_light_gray_wool", Blocks.LIGHT_GRAY_WOOL);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CYAN_WOOL = reinforcedBlock("reinforced_cyan_wool", Blocks.CYAN_WOOL);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_PURPLE_WOOL = reinforcedBlock("reinforced_purple_wool", Blocks.PURPLE_WOOL);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BLUE_WOOL = reinforcedBlock("reinforced_blue_wool", Blocks.BLUE_WOOL);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BROWN_WOOL = reinforcedBlock("reinforced_brown_wool", Blocks.BROWN_WOOL);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_GREEN_WOOL = reinforcedBlock("reinforced_green_wool", Blocks.GREEN_WOOL);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_RED_WOOL = reinforcedBlock("reinforced_red_wool", Blocks.RED_WOOL);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BLACK_WOOL = reinforcedBlock("reinforced_black_wool", Blocks.BLACK_WOOL);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_OAK_SLAB = reinforcedBlock("reinforced_oak_slab", Blocks.OAK_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_SPRUCE_SLAB = reinforcedBlock("reinforced_spruce_slab", Blocks.SPRUCE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_BIRCH_SLAB = reinforcedBlock("reinforced_birch_slab", Blocks.BIRCH_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_JUNGLE_SLAB = reinforcedBlock("reinforced_jungle_slab", Blocks.JUNGLE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_ACACIA_SLAB = reinforcedBlock("reinforced_acacia_slab", Blocks.ACACIA_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_DARK_OAK_SLAB = reinforcedBlock("reinforced_dark_oak_slab", Blocks.DARK_OAK_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_MANGROVE_SLAB = reinforcedBlock("reinforced_mangrove_slab", Blocks.MANGROVE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_CRIMSON_SLAB = reinforcedBlock("reinforced_crimson_slab", Blocks.CRIMSON_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_WARPED_SLAB = reinforcedBlock("reinforced_warped_slab", Blocks.WARPED_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_NORMAL_STONE_SLAB = reinforcedBlock("reinforced_normal_stone_slab", Blocks.STONE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_SMOOTH_STONE_SLAB = reinforcedBlock("reinforced_stone_slab", Blocks.SMOOTH_STONE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_SANDSTONE_SLAB = reinforcedBlock("reinforced_sandstone_slab", Blocks.SANDSTONE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_CUT_SANDSTONE_SLAB = reinforcedBlock("reinforced_cut_sandstone_slab", Blocks.CUT_SANDSTONE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_COBBLESTONE_SLAB = reinforcedBlock("reinforced_cobblestone_slab", Blocks.COBBLESTONE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_BRICK_SLAB = reinforcedBlock("reinforced_brick_slab", Blocks.BRICK_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_STONE_BRICK_SLAB = reinforcedBlock("reinforced_stone_brick_slab", Blocks.STONE_BRICK_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_MUD_BRICK_SLAB = reinforcedBlock("reinforced_mud_brick_slab", Blocks.MUD_BRICK_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_NETHER_BRICK_SLAB = reinforcedBlock("reinforced_nether_brick_slab", Blocks.NETHER_BRICK_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_QUARTZ_SLAB = reinforcedBlock("reinforced_quartz_slab", Blocks.QUARTZ_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_RED_SANDSTONE_SLAB = reinforcedBlock("reinforced_red_sandstone_slab", Blocks.RED_SANDSTONE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_CUT_RED_SANDSTONE_SLAB = reinforcedBlock("reinforced_cut_red_sandstone_slab", Blocks.CUT_RED_SANDSTONE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_PURPUR_SLAB = reinforcedBlock("reinforced_purpur_slab", Blocks.PURPUR_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_PRISMARINE_SLAB = reinforcedBlock("reinforced_prismarine_slab", Blocks.PRISMARINE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_PRISMARINE_BRICK_SLAB = reinforcedBlock("reinforced_prismarine_brick_slab", Blocks.PRISMARINE_BRICK_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_DARK_PRISMARINE_SLAB = reinforcedBlock("reinforced_dark_prismarine_slab", Blocks.DARK_PRISMARINE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_SMOOTH_QUARTZ = reinforcedBlock("reinforced_smooth_quartz", Blocks.SMOOTH_QUARTZ);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_SMOOTH_RED_SANDSTONE = reinforcedBlock("reinforced_smooth_red_sandstone", Blocks.SMOOTH_RED_SANDSTONE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_SMOOTH_SANDSTONE = reinforcedBlock("reinforced_smooth_sandstone", Blocks.SMOOTH_SANDSTONE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_SMOOTH_STONE = reinforcedBlock("reinforced_smooth_stone", Blocks.SMOOTH_STONE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BRICKS = reinforcedBlock("reinforced_bricks", Blocks.BRICKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BOOKSHELF = reinforcedBlock("reinforced_bookshelf", Blocks.BOOKSHELF);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_MOSSY_COBBLESTONE = reinforcedBlock("reinforced_mossy_cobblestone", Blocks.MOSSY_COBBLESTONE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedObsidianBlock> REINFORCED_OBSIDIAN = reinforcedBlock("reinforced_obsidian", Blocks.OBSIDIAN, ReinforcedObsidianBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_PURPUR_BLOCK = reinforcedBlock("reinforced_purpur_block", Blocks.PURPUR_BLOCK);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_PURPUR_PILLAR = reinforcedBlock("reinforced_purpur_pillar", Blocks.PURPUR_PILLAR, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_PURPUR_STAIRS = reinforcedBlock("reinforced_purpur_stairs", Blocks.PURPUR_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_OAK_STAIRS = reinforcedBlock("reinforced_oak_stairs", Blocks.OAK_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_COBBLESTONE_STAIRS = reinforcedBlock("reinforced_cobblestone_stairs", Blocks.COBBLESTONE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_ICE = reinforcedBlock("reinforced_ice", Blocks.ICE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_SNOW_BLOCK = reinforcedBlock("reinforced_snow_block", Blocks.SNOW_BLOCK);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CLAY = reinforcedBlock("reinforced_clay", Blocks.CLAY);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_NETHERRACK = reinforcedBlock("reinforced_netherrack", Blocks.NETHERRACK);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSoulSandBlock> REINFORCED_SOUL_SAND = reinforcedBlock("reinforced_soul_sand", Blocks.SOUL_SAND, ReinforcedSoulSandBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_SOUL_SOIL = reinforcedBlock("reinforced_soul_soil", Blocks.SOUL_SOIL);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_BASALT = reinforcedBlock("reinforced_basalt", Blocks.BASALT, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_POLISHED_BASALT = reinforcedBlock("reinforced_polished_basalt", Blocks.POLISHED_BASALT, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_SMOOTH_BASALT = reinforcedBlock("reinforced_smooth_basalt", Blocks.SMOOTH_BASALT);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_GLOWSTONE = reinforcedBlock("reinforced_glowstone", Blocks.GLOWSTONE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_STONE_BRICKS = reinforcedBlock("reinforced_stone_bricks", Blocks.STONE_BRICKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_MOSSY_STONE_BRICKS = reinforcedBlock("reinforced_mossy_stone_bricks", Blocks.MOSSY_STONE_BRICKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CRACKED_STONE_BRICKS = reinforcedBlock("reinforced_cracked_stone_bricks", Blocks.CRACKED_STONE_BRICKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CHISELED_STONE_BRICKS = reinforcedBlock("reinforced_chiseled_stone_bricks", Blocks.CHISELED_STONE_BRICKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_PACKED_MUD = reinforcedBlock("reinforced_packed_mud", Blocks.PACKED_MUD);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_MUD_BRICKS = reinforcedBlock("reinforced_mud_bricks", Blocks.MUD_BRICKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_DEEPSLATE_BRICKS = reinforcedBlock("reinforced_deepslate_bricks", Blocks.DEEPSLATE_BRICKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CRACKED_DEEPSLATE_BRICKS = reinforcedBlock("reinforced_cracked_deepslate_bricks", Blocks.CRACKED_DEEPSLATE_BRICKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_DEEPSLATE_TILES = reinforcedBlock("reinforced_deepslate_tiles", Blocks.DEEPSLATE_TILES);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CRACKED_DEEPSLATE_TILES = reinforcedBlock("reinforced_cracked_deepslate_tiles", Blocks.CRACKED_DEEPSLATE_TILES);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CHISELED_DEEPSLATE = reinforcedBlock("reinforced_chiseled_deepslate", Blocks.CHISELED_DEEPSLATE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_BRICK_STAIRS = reinforcedBlock("reinforced_brick_stairs", Blocks.BRICK_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_STONE_BRICK_STAIRS = reinforcedBlock("reinforced_stone_brick_stairs", Blocks.STONE_BRICK_STAIRS, ReinforcedStairsBlock::new);
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_MUD_BRICK_STAIRS = reinforcedBlock("reinforced_mud_brick_stairs", Blocks.MUD_BRICK_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSnowyDirtBlock> REINFORCED_MYCELIUM = reinforcedBlock("reinforced_mycelium", Blocks.MYCELIUM, ReinforcedSnowyDirtBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_NETHER_BRICKS = reinforcedBlock("reinforced_nether_bricks", Blocks.NETHER_BRICKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CRACKED_NETHER_BRICKS = reinforcedBlock("reinforced_cracked_nether_bricks", Blocks.CRACKED_NETHER_BRICKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CHISELED_NETHER_BRICKS = reinforcedBlock("reinforced_chiseled_nether_bricks", Blocks.CHISELED_NETHER_BRICKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_NETHER_BRICK_STAIRS = reinforcedBlock("reinforced_nether_brick_stairs", Blocks.NETHER_BRICK_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_END_STONE = reinforcedBlock("reinforced_end_stone", Blocks.END_STONE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_END_STONE_BRICKS = reinforcedBlock("reinforced_end_stone_bricks", Blocks.END_STONE_BRICKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_SANDSTONE_STAIRS = reinforcedBlock("reinforced_sandstone_stairs", Blocks.SANDSTONE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_EMERALD_BLOCK = reinforcedBlock("reinforced_emerald_block", Blocks.EMERALD_BLOCK);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_SPRUCE_STAIRS = reinforcedBlock("reinforced_spruce_stairs", Blocks.SPRUCE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_BIRCH_STAIRS = reinforcedBlock("reinforced_birch_stairs", Blocks.BIRCH_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_JUNGLE_STAIRS = reinforcedBlock("reinforced_jungle_stairs", Blocks.JUNGLE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_CRIMSON_STAIRS = reinforcedBlock("reinforced_crimson_stairs", Blocks.CRIMSON_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_WARPED_STAIRS = reinforcedBlock("reinforced_warped_stairs", Blocks.WARPED_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CHISELED_QUARTZ = reinforcedBlock("reinforced_chiseled_quartz_block", Blocks.CHISELED_QUARTZ_BLOCK);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_QUARTZ_BLOCK = reinforcedBlock("reinforced_quartz_block", Blocks.QUARTZ_BLOCK);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_QUARTZ_BRICKS = reinforcedBlock("reinforced_quartz_bricks", Blocks.QUARTZ_BRICKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_QUARTZ_PILLAR = reinforcedBlock("reinforced_quartz_pillar", Blocks.QUARTZ_PILLAR, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_QUARTZ_STAIRS = reinforcedBlock("reinforced_quartz_stairs", Blocks.QUARTZ_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_WHITE_TERRACOTTA = reinforcedBlock("reinforced_white_terracotta", Blocks.WHITE_TERRACOTTA);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_ORANGE_TERRACOTTA = reinforcedBlock("reinforced_orange_terracotta", Blocks.ORANGE_TERRACOTTA);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_MAGENTA_TERRACOTTA = reinforcedBlock("reinforced_magenta_terracotta", Blocks.MAGENTA_TERRACOTTA);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_LIGHT_BLUE_TERRACOTTA = reinforcedBlock("reinforced_light_blue_terracotta", Blocks.LIGHT_BLUE_TERRACOTTA);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_YELLOW_TERRACOTTA = reinforcedBlock("reinforced_yellow_terracotta", Blocks.YELLOW_TERRACOTTA);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_LIME_TERRACOTTA = reinforcedBlock("reinforced_lime_terracotta", Blocks.LIME_TERRACOTTA);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_PINK_TERRACOTTA = reinforcedBlock("reinforced_pink_terracotta", Blocks.PINK_TERRACOTTA);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_GRAY_TERRACOTTA = reinforcedBlock("reinforced_gray_terracotta", Blocks.GRAY_TERRACOTTA);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_LIGHT_GRAY_TERRACOTTA = reinforcedBlock("reinforced_light_gray_terracotta", Blocks.LIGHT_GRAY_TERRACOTTA);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CYAN_TERRACOTTA = reinforcedBlock("reinforced_cyan_terracotta", Blocks.CYAN_TERRACOTTA);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_PURPLE_TERRACOTTA = reinforcedBlock("reinforced_purple_terracotta", Blocks.PURPLE_TERRACOTTA);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BLUE_TERRACOTTA = reinforcedBlock("reinforced_blue_terracotta", Blocks.BLUE_TERRACOTTA);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BROWN_TERRACOTTA = reinforcedBlock("reinforced_brown_terracotta", Blocks.BROWN_TERRACOTTA);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_GREEN_TERRACOTTA = reinforcedBlock("reinforced_green_terracotta", Blocks.GREEN_TERRACOTTA);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_RED_TERRACOTTA = reinforcedBlock("reinforced_red_terracotta", Blocks.RED_TERRACOTTA);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BLACK_TERRACOTTA = reinforcedBlock("reinforced_black_terracotta", Blocks.BLACK_TERRACOTTA);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_TERRACOTTA = reinforcedBlock("reinforced_hardened_clay", Blocks.TERRACOTTA);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_PACKED_ICE = reinforcedBlock("reinforced_packed_ice", Blocks.PACKED_ICE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_ACACIA_STAIRS = reinforcedBlock("reinforced_acacia_stairs", Blocks.ACACIA_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_DARK_OAK_STAIRS = reinforcedBlock("reinforced_dark_oak_stairs", Blocks.DARK_OAK_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_MANGROVE_STAIRS = reinforcedBlock("reinforced_mangrove_stairs", Blocks.MANGROVE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_WHITE_STAINED_GLASS = reinforcedBlock("reinforced_white_stained_glass", Blocks.WHITE_STAINED_GLASS, ReinforcedStainedGlassBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_ORANGE_STAINED_GLASS = reinforcedBlock("reinforced_orange_stained_glass", Blocks.ORANGE_STAINED_GLASS, ReinforcedStainedGlassBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_MAGENTA_STAINED_GLASS = reinforcedBlock("reinforced_magenta_stained_glass", Blocks.MAGENTA_STAINED_GLASS, ReinforcedStainedGlassBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_LIGHT_BLUE_STAINED_GLASS = reinforcedBlock("reinforced_light_blue_stained_glass", Blocks.LIGHT_BLUE_STAINED_GLASS, ReinforcedStainedGlassBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_YELLOW_STAINED_GLASS = reinforcedBlock("reinforced_yellow_stained_glass", Blocks.YELLOW_STAINED_GLASS, ReinforcedStainedGlassBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_LIME_STAINED_GLASS = reinforcedBlock("reinforced_lime_stained_glass", Blocks.LIME_STAINED_GLASS, ReinforcedStainedGlassBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_PINK_STAINED_GLASS = reinforcedBlock("reinforced_pink_stained_glass", Blocks.PINK_STAINED_GLASS, ReinforcedStainedGlassBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_GRAY_STAINED_GLASS = reinforcedBlock("reinforced_gray_stained_glass", Blocks.GRAY_STAINED_GLASS, ReinforcedStainedGlassBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_LIGHT_GRAY_STAINED_GLASS = reinforcedBlock("reinforced_light_gray_stained_glass", Blocks.LIGHT_GRAY_STAINED_GLASS, ReinforcedStainedGlassBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_CYAN_STAINED_GLASS = reinforcedBlock("reinforced_cyan_stained_glass", Blocks.CYAN_STAINED_GLASS, ReinforcedStainedGlassBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_PURPLE_STAINED_GLASS = reinforcedBlock("reinforced_purple_stained_glass", Blocks.PURPLE_STAINED_GLASS, ReinforcedStainedGlassBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_BLUE_STAINED_GLASS = reinforcedBlock("reinforced_blue_stained_glass", Blocks.BLUE_STAINED_GLASS, ReinforcedStainedGlassBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_BROWN_STAINED_GLASS = reinforcedBlock("reinforced_brown_stained_glass", Blocks.BROWN_STAINED_GLASS, ReinforcedStainedGlassBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_GREEN_STAINED_GLASS = reinforcedBlock("reinforced_green_stained_glass", Blocks.GREEN_STAINED_GLASS, ReinforcedStainedGlassBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_RED_STAINED_GLASS = reinforcedBlock("reinforced_red_stained_glass", Blocks.RED_STAINED_GLASS, ReinforcedStainedGlassBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassBlock> REINFORCED_BLACK_STAINED_GLASS = reinforcedBlock("reinforced_black_stained_glass", Blocks.BLACK_STAINED_GLASS, ReinforcedStainedGlassBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_PRISMARINE = reinforcedBlock("reinforced_prismarine", Blocks.PRISMARINE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_PRISMARINE_BRICKS = reinforcedBlock("reinforced_prismarine_bricks", Blocks.PRISMARINE_BRICKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_DARK_PRISMARINE = reinforcedBlock("reinforced_dark_prismarine", Blocks.DARK_PRISMARINE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_PRISMARINE_STAIRS = reinforcedBlock("reinforced_prismarine_stairs", Blocks.PRISMARINE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_PRISMARINE_BRICK_STAIRS = reinforcedBlock("reinforced_prismarine_brick_stairs", Blocks.PRISMARINE_BRICK_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_DARK_PRISMARINE_STAIRS = reinforcedBlock("reinforced_dark_prismarine_stairs", Blocks.DARK_PRISMARINE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_SEA_LANTERN = reinforcedBlock("reinforced_sea_lantern", Blocks.SEA_LANTERN);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_RED_SANDSTONE = reinforcedBlock("reinforced_red_sandstone", Blocks.RED_SANDSTONE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CHISELED_RED_SANDSTONE = reinforcedBlock("reinforced_chiseled_red_sandstone", Blocks.CHISELED_RED_SANDSTONE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CUT_RED_SANDSTONE = reinforcedBlock("reinforced_cut_red_sandstone", Blocks.CUT_RED_SANDSTONE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_RED_SANDSTONE_STAIRS = reinforcedBlock("reinforced_red_sandstone_stairs", Blocks.RED_SANDSTONE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedMagmaBlock> REINFORCED_MAGMA_BLOCK = reinforcedBlock("reinforced_magma_block", Blocks.MAGMA_BLOCK, ReinforcedMagmaBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_NETHER_WART_BLOCK = reinforcedBlock("reinforced_nether_wart_block", Blocks.NETHER_WART_BLOCK);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_WARPED_WART_BLOCK = reinforcedBlock("reinforced_warped_wart_block", Blocks.WARPED_WART_BLOCK);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_RED_NETHER_BRICKS = reinforcedBlock("reinforced_red_nether_bricks", Blocks.RED_NETHER_BRICKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_BONE_BLOCK = reinforcedBlock("reinforced_bone_block", Blocks.BONE_BLOCK, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_WHITE_CONCRETE = reinforcedBlock("reinforced_white_concrete", Blocks.WHITE_CONCRETE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_ORANGE_CONCRETE = reinforcedBlock("reinforced_orange_concrete", Blocks.ORANGE_CONCRETE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_MAGENTA_CONCRETE = reinforcedBlock("reinforced_magenta_concrete", Blocks.MAGENTA_CONCRETE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_LIGHT_BLUE_CONCRETE = reinforcedBlock("reinforced_light_blue_concrete", Blocks.LIGHT_BLUE_CONCRETE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_YELLOW_CONCRETE = reinforcedBlock("reinforced_yellow_concrete", Blocks.YELLOW_CONCRETE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_LIME_CONCRETE = reinforcedBlock("reinforced_lime_concrete", Blocks.LIME_CONCRETE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_PINK_CONCRETE = reinforcedBlock("reinforced_pink_concrete", Blocks.PINK_CONCRETE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_GRAY_CONCRETE = reinforcedBlock("reinforced_gray_concrete", Blocks.GRAY_CONCRETE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_LIGHT_GRAY_CONCRETE = reinforcedBlock("reinforced_light_gray_concrete", Blocks.LIGHT_GRAY_CONCRETE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CYAN_CONCRETE = reinforcedBlock("reinforced_cyan_concrete", Blocks.CYAN_CONCRETE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_PURPLE_CONCRETE = reinforcedBlock("reinforced_purple_concrete", Blocks.PURPLE_CONCRETE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BLUE_CONCRETE = reinforcedBlock("reinforced_blue_concrete", Blocks.BLUE_CONCRETE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BROWN_CONCRETE = reinforcedBlock("reinforced_brown_concrete", Blocks.BROWN_CONCRETE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_GREEN_CONCRETE = reinforcedBlock("reinforced_green_concrete", Blocks.GREEN_CONCRETE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_RED_CONCRETE = reinforcedBlock("reinforced_red_concrete", Blocks.RED_CONCRETE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BLACK_CONCRETE = reinforcedBlock("reinforced_black_concrete", Blocks.BLACK_CONCRETE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BLUE_ICE = reinforcedBlock("reinforced_blue_ice", Blocks.BLUE_ICE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_POLISHED_GRANITE_STAIRS = reinforcedBlock("reinforced_polished_granite_stairs", Blocks.POLISHED_GRANITE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_SMOOTH_RED_SANDSTONE_STAIRS = reinforcedBlock("reinforced_smooth_red_sandstone_stairs", Blocks.SMOOTH_RED_SANDSTONE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_MOSSY_STONE_BRICK_STAIRS = reinforcedBlock("reinforced_mossy_stone_brick_stairs", Blocks.MOSSY_STONE_BRICK_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_POLISHED_DIORITE_STAIRS = reinforcedBlock("reinforced_polished_diorite_stairs", Blocks.POLISHED_DIORITE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_MOSSY_COBBLESTONE_STAIRS = reinforcedBlock("reinforced_mossy_cobblestone_stairs", Blocks.MOSSY_COBBLESTONE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_END_STONE_BRICK_STAIRS = reinforcedBlock("reinforced_end_stone_brick_stairs", Blocks.END_STONE_BRICK_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_STONE_STAIRS = reinforcedBlock("reinforced_stone_stairs", Blocks.STONE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_SMOOTH_SANDSTONE_STAIRS = reinforcedBlock("reinforced_smooth_sandstone_stairs", Blocks.SMOOTH_SANDSTONE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_SMOOTH_QUARTZ_STAIRS = reinforcedBlock("reinforced_smooth_quartz_stairs", Blocks.SMOOTH_QUARTZ_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_GRANITE_STAIRS = reinforcedBlock("reinforced_granite_stairs", Blocks.GRANITE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_ANDESITE_STAIRS = reinforcedBlock("reinforced_andesite_stairs", Blocks.ANDESITE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_RED_NETHER_BRICK_STAIRS = reinforcedBlock("reinforced_red_nether_brick_stairs", Blocks.RED_NETHER_BRICK_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_POLISHED_ANDESITE_STAIRS = reinforcedBlock("reinforced_polished_andesite_stairs", Blocks.POLISHED_ANDESITE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_DIORITE_STAIRS = reinforcedBlock("reinforced_diorite_stairs", Blocks.DIORITE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_COBBLED_DEEPSLATE_STAIRS = reinforcedBlock("reinforced_cobbled_deepslate_stairs", Blocks.COBBLED_DEEPSLATE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_POLISHED_DEEPSLATE_STAIRS = reinforcedBlock("reinforced_polished_deepslate_stairs", Blocks.POLISHED_DEEPSLATE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_DEEPSLATE_BRICK_STAIRS = reinforcedBlock("reinforced_deepslate_brick_stairs", Blocks.DEEPSLATE_BRICK_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_DEEPSLATE_TILE_STAIRS = reinforcedBlock("reinforced_deepslate_tile_stairs", Blocks.DEEPSLATE_TILE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_POLISHED_GRANITE_SLAB = reinforcedBlock("reinforced_polished_granite_slab", Blocks.POLISHED_GRANITE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_SMOOTH_RED_SANDSTONE_SLAB = reinforcedBlock("reinforced_smooth_red_sandstone_slab", Blocks.SMOOTH_RED_SANDSTONE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_MOSSY_STONE_BRICK_SLAB = reinforcedBlock("reinforced_mossy_stone_brick_slab", Blocks.MOSSY_STONE_BRICK_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_POLISHED_DIORITE_SLAB = reinforcedBlock("reinforced_polished_diorite_slab", Blocks.POLISHED_DIORITE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_MOSSY_COBBLESTONE_SLAB = reinforcedBlock("reinforced_mossy_cobblestone_slab", Blocks.MOSSY_COBBLESTONE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_END_STONE_BRICK_SLAB = reinforcedBlock("reinforced_end_stone_brick_slab", Blocks.END_STONE_BRICK_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_SMOOTH_SANDSTONE_SLAB = reinforcedBlock("reinforced_smooth_sandstone_slab", Blocks.SMOOTH_SANDSTONE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_SMOOTH_QUARTZ_SLAB = reinforcedBlock("reinforced_smooth_quartz_slab", Blocks.SMOOTH_QUARTZ_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_GRANITE_SLAB = reinforcedBlock("reinforced_granite_slab", Blocks.GRANITE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_ANDESITE_SLAB = reinforcedBlock("reinforced_andesite_slab", Blocks.ANDESITE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_RED_NETHER_BRICK_SLAB = reinforcedBlock("reinforced_red_nether_brick_slab", Blocks.RED_NETHER_BRICK_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_POLISHED_ANDESITE_SLAB = reinforcedBlock("reinforced_polished_andesite_slab", Blocks.POLISHED_ANDESITE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_DIORITE_SLAB = reinforcedBlock("reinforced_diorite_slab", Blocks.DIORITE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_COBBLED_DEEPSLATE_SLAB = reinforcedBlock("reinforced_cobbled_deepslate_slab", Blocks.COBBLED_DEEPSLATE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_POLISHED_DEEPSLATE_SLAB = reinforcedBlock("reinforced_polished_deepslate_slab", Blocks.POLISHED_DEEPSLATE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_DEEPSLATE_BRICK_SLAB = reinforcedBlock("reinforced_deepslate_brick_slab", Blocks.DEEPSLATE_BRICK_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_DEEPSLATE_TILE_SLAB = reinforcedBlock("reinforced_deepslate_tile_slab", Blocks.DEEPSLATE_TILE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCryingObsidianBlock> REINFORCED_CRYING_OBSIDIAN = reinforcedBlock("reinforced_crying_obsidian", Blocks.CRYING_OBSIDIAN, ReinforcedCryingObsidianBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BLACKSTONE = reinforcedBlock("reinforced_blackstone", Blocks.BLACKSTONE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_BLACKSTONE_SLAB = reinforcedBlock("reinforced_blackstone_slab", Blocks.BLACKSTONE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_BLACKSTONE_STAIRS = reinforcedBlock("reinforced_blackstone_stairs", Blocks.BLACKSTONE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_POLISHED_BLACKSTONE = reinforcedBlock("reinforced_polished_blackstone", Blocks.POLISHED_BLACKSTONE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_POLISHED_BLACKSTONE_SLAB = reinforcedBlock("reinforced_polished_blackstone_slab", Blocks.POLISHED_BLACKSTONE_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_POLISHED_BLACKSTONE_STAIRS = reinforcedBlock("reinforced_polished_blackstone_stairs", Blocks.POLISHED_BLACKSTONE_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CHISELED_POLISHED_BLACKSTONE = reinforcedBlock("reinforced_chiseled_polished_blackstone", Blocks.CHISELED_POLISHED_BLACKSTONE);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_POLISHED_BLACKSTONE_BRICKS = reinforcedBlock("reinforced_polished_blackstone_bricks", Blocks.POLISHED_BLACKSTONE_BRICKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_POLISHED_BLACKSTONE_BRICK_SLAB = reinforcedBlock("reinforced_polished_blackstone_brick_slab", Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_POLISHED_BLACKSTONE_BRICK_STAIRS = reinforcedBlock("reinforced_polished_blackstone_brick_stairs", Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CRACKED_POLISHED_BLACKSTONE_BRICKS = reinforcedBlock("reinforced_cracked_polished_blackstone_bricks", Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS);

	//ordered by vanilla <1.19.3 decoration blocks creative tab order
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCobwebBlock> REINFORCED_COBWEB = reinforcedBlock("reinforced_cobweb", Blocks.COBWEB, ReinforcedCobwebBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_MOSS_CARPET = reinforcedBlock("reinforced_moss_carpet", Blocks.MOSS_CARPET, ReinforcedCarpetBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_MOSS_BLOCK = reinforcedBlock("reinforced_moss_block", Blocks.MOSS_BLOCK, p -> p.pushReaction(PushReaction.NORMAL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedEndRodBlock> REINFORCED_END_ROD = reinforcedBlock("reinforced_end_rod", Blocks.END_ROD, (p, b) -> new ReinforcedEndRodBlock(p));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedIronBarsBlock> REINFORCED_IRON_BARS = reinforcedBlock("reinforced_iron_bars", Blocks.IRON_BARS, ReinforcedIronBarsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedLadderBlock> REINFORCED_LADDER = reinforcedBlock("reinforced_ladder", Blocks.LADDER, (p, b) -> new ReinforcedLadderBlock(p.pushReaction(PushReaction.BLOCK)));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedChainBlock> REINFORCED_CHAIN = reinforcedBlock("reinforced_chain", Blocks.CHAIN, ReinforcedChainBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedPaneBlock> REINFORCED_GLASS_PANE = reinforcedBlock("reinforced_glass_pane", Blocks.GLASS_PANE, ReinforcedPaneBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_COBBLESTONE_WALL = reinforcedBlock("reinforced_cobblestone_wall", Blocks.COBBLESTONE_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_MOSSY_COBBLESTONE_WALL = reinforcedBlock("reinforced_mossy_cobblestone_wall", Blocks.MOSSY_COBBLESTONE_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_BRICK_WALL = reinforcedBlock("reinforced_brick_wall", Blocks.BRICK_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_PRISMARINE_WALL = reinforcedBlock("reinforced_prismarine_wall", Blocks.PRISMARINE_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_RED_SANDSTONE_WALL = reinforcedBlock("reinforced_red_sandstone_wall", Blocks.RED_SANDSTONE_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_MOSSY_STONE_BRICK_WALL = reinforcedBlock("reinforced_mossy_stone_brick_wall", Blocks.MOSSY_STONE_BRICK_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_GRANITE_WALL = reinforcedBlock("reinforced_granite_wall", Blocks.GRANITE_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_STONE_BRICK_WALL = reinforcedBlock("reinforced_stone_brick_wall", Blocks.STONE_BRICK_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_MUD_BRICK_WALL = reinforcedBlock("reinforced_mud_brick_wall", Blocks.MUD_BRICK_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_NETHER_BRICK_WALL = reinforcedBlock("reinforced_nether_brick_wall", Blocks.NETHER_BRICK_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_ANDESITE_WALL = reinforcedBlock("reinforced_andesite_wall", Blocks.ANDESITE_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_RED_NETHER_BRICK_WALL = reinforcedBlock("reinforced_red_nether_brick_wall", Blocks.RED_NETHER_BRICK_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_SANDSTONE_WALL = reinforcedBlock("reinforced_sandstone_wall", Blocks.SANDSTONE_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_END_STONE_BRICK_WALL = reinforcedBlock("reinforced_end_stone_brick_wall", Blocks.END_STONE_BRICK_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_DIORITE_WALL = reinforcedBlock("reinforced_diorite_wall", Blocks.DIORITE_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_BLACKSTONE_WALL = reinforcedBlock("reinforced_blackstone_wall", Blocks.BLACKSTONE_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_POLISHED_BLACKSTONE_WALL = reinforcedBlock("reinforced_polished_blackstone_wall", Blocks.POLISHED_BLACKSTONE_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_POLISHED_BLACKSTONE_BRICK_WALL = reinforcedBlock("reinforced_polished_blackstone_brick_wall", Blocks.POLISHED_BLACKSTONE_BRICK_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_COBBLED_DEEPSLATE_WALL = reinforcedBlock("reinforced_cobbled_deepslate_wall", Blocks.COBBLED_DEEPSLATE_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_POLISHED_DEEPSLATE_WALL = reinforcedBlock("reinforced_polished_deepslate_wall", Blocks.POLISHED_DEEPSLATE_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_DEEPSLATE_BRICK_WALL = reinforcedBlock("reinforced_deepslate_brick_wall", Blocks.DEEPSLATE_BRICK_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_DEEPSLATE_TILE_WALL = reinforcedBlock("reinforced_deepslate_tile_wall", Blocks.DEEPSLATE_TILE_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_WHITE_CARPET = reinforcedBlock("reinforced_white_carpet", Blocks.WHITE_CARPET, ReinforcedCarpetBlock::new, BlockBehaviour.Properties::forceSolidOn);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_ORANGE_CARPET = reinforcedBlock("reinforced_orange_carpet", Blocks.ORANGE_CARPET, ReinforcedCarpetBlock::new, BlockBehaviour.Properties::forceSolidOn);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_MAGENTA_CARPET = reinforcedBlock("reinforced_magenta_carpet", Blocks.MAGENTA_CARPET, ReinforcedCarpetBlock::new, BlockBehaviour.Properties::forceSolidOn);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_LIGHT_BLUE_CARPET = reinforcedBlock("reinforced_light_blue_carpet", Blocks.LIGHT_BLUE_CARPET, ReinforcedCarpetBlock::new, BlockBehaviour.Properties::forceSolidOn);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_YELLOW_CARPET = reinforcedBlock("reinforced_yellow_carpet", Blocks.YELLOW_CARPET, ReinforcedCarpetBlock::new, BlockBehaviour.Properties::forceSolidOn);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_LIME_CARPET = reinforcedBlock("reinforced_lime_carpet", Blocks.LIME_CARPET, ReinforcedCarpetBlock::new, BlockBehaviour.Properties::forceSolidOn);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_PINK_CARPET = reinforcedBlock("reinforced_pink_carpet", Blocks.PINK_CARPET, ReinforcedCarpetBlock::new, BlockBehaviour.Properties::forceSolidOn);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_GRAY_CARPET = reinforcedBlock("reinforced_gray_carpet", Blocks.GRAY_CARPET, ReinforcedCarpetBlock::new, BlockBehaviour.Properties::forceSolidOn);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_LIGHT_GRAY_CARPET = reinforcedBlock("reinforced_light_gray_carpet", Blocks.LIGHT_GRAY_CARPET, ReinforcedCarpetBlock::new, BlockBehaviour.Properties::forceSolidOn);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_CYAN_CARPET = reinforcedBlock("reinforced_cyan_carpet", Blocks.CYAN_CARPET, ReinforcedCarpetBlock::new, BlockBehaviour.Properties::forceSolidOn);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_PURPLE_CARPET = reinforcedBlock("reinforced_purple_carpet", Blocks.PURPLE_CARPET, ReinforcedCarpetBlock::new, BlockBehaviour.Properties::forceSolidOn);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_BLUE_CARPET = reinforcedBlock("reinforced_blue_carpet", Blocks.BLUE_CARPET, ReinforcedCarpetBlock::new, BlockBehaviour.Properties::forceSolidOn);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_BROWN_CARPET = reinforcedBlock("reinforced_brown_carpet", Blocks.BROWN_CARPET, ReinforcedCarpetBlock::new, BlockBehaviour.Properties::forceSolidOn);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_GREEN_CARPET = reinforcedBlock("reinforced_green_carpet", Blocks.GREEN_CARPET, ReinforcedCarpetBlock::new, BlockBehaviour.Properties::forceSolidOn);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_RED_CARPET = reinforcedBlock("reinforced_red_carpet", Blocks.RED_CARPET, ReinforcedCarpetBlock::new, BlockBehaviour.Properties::forceSolidOn);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCarpetBlock> REINFORCED_BLACK_CARPET = reinforcedBlock("reinforced_black_carpet", Blocks.BLACK_CARPET, ReinforcedCarpetBlock::new, BlockBehaviour.Properties::forceSolidOn);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_WHITE_STAINED_GLASS_PANE = reinforcedBlock("reinforced_white_stained_glass_pane", Blocks.WHITE_STAINED_GLASS_PANE, ReinforcedStainedGlassPaneBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_ORANGE_STAINED_GLASS_PANE = reinforcedBlock("reinforced_orange_stained_glass_pane", Blocks.ORANGE_STAINED_GLASS_PANE, ReinforcedStainedGlassPaneBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_MAGENTA_STAINED_GLASS_PANE = reinforcedBlock("reinforced_magenta_stained_glass_pane", Blocks.MAGENTA_STAINED_GLASS_PANE, ReinforcedStainedGlassPaneBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_LIGHT_BLUE_STAINED_GLASS_PANE = reinforcedBlock("reinforced_light_blue_stained_glass_pane", Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, ReinforcedStainedGlassPaneBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_YELLOW_STAINED_GLASS_PANE = reinforcedBlock("reinforced_yellow_stained_glass_pane", Blocks.YELLOW_STAINED_GLASS_PANE, ReinforcedStainedGlassPaneBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_LIME_STAINED_GLASS_PANE = reinforcedBlock("reinforced_lime_stained_glass_pane", Blocks.LIME_STAINED_GLASS_PANE, ReinforcedStainedGlassPaneBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_PINK_STAINED_GLASS_PANE = reinforcedBlock("reinforced_pink_stained_glass_pane", Blocks.PINK_STAINED_GLASS_PANE, ReinforcedStainedGlassPaneBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_GRAY_STAINED_GLASS_PANE = reinforcedBlock("reinforced_gray_stained_glass_pane", Blocks.GRAY_STAINED_GLASS_PANE, ReinforcedStainedGlassPaneBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_LIGHT_GRAY_STAINED_GLASS_PANE = reinforcedBlock("reinforced_light_gray_stained_glass_pane", Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, ReinforcedStainedGlassPaneBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_CYAN_STAINED_GLASS_PANE = reinforcedBlock("reinforced_cyan_stained_glass_pane", Blocks.CYAN_STAINED_GLASS_PANE, ReinforcedStainedGlassPaneBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_PURPLE_STAINED_GLASS_PANE = reinforcedBlock("reinforced_purple_stained_glass_pane", Blocks.PURPLE_STAINED_GLASS_PANE, ReinforcedStainedGlassPaneBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_BLUE_STAINED_GLASS_PANE = reinforcedBlock("reinforced_blue_stained_glass_pane", Blocks.BLUE_STAINED_GLASS_PANE, ReinforcedStainedGlassPaneBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_BROWN_STAINED_GLASS_PANE = reinforcedBlock("reinforced_brown_stained_glass_pane", Blocks.BROWN_STAINED_GLASS_PANE, ReinforcedStainedGlassPaneBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_GREEN_STAINED_GLASS_PANE = reinforcedBlock("reinforced_green_stained_glass_pane", Blocks.GREEN_STAINED_GLASS_PANE, ReinforcedStainedGlassPaneBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_RED_STAINED_GLASS_PANE = reinforcedBlock("reinforced_red_stained_glass_pane", Blocks.RED_STAINED_GLASS_PANE, ReinforcedStainedGlassPaneBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(hasReinforcedTint = false)
	public static final DeferredBlock<ReinforcedStainedGlassPaneBlock> REINFORCED_BLACK_STAINED_GLASS_PANE = reinforcedBlock("reinforced_black_stained_glass_pane", Blocks.BLACK_STAINED_GLASS_PANE, ReinforcedStainedGlassPaneBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_WHITE_GLAZED_TERRACOTTA = reinforcedBlock("reinforced_white_glazed_terracotta", Blocks.WHITE_GLAZED_TERRACOTTA, ReinforcedGlazedTerracottaBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_ORANGE_GLAZED_TERRACOTTA = reinforcedBlock("reinforced_orange_glazed_terracotta", Blocks.ORANGE_GLAZED_TERRACOTTA, ReinforcedGlazedTerracottaBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_MAGENTA_GLAZED_TERRACOTTA = reinforcedBlock("reinforced_magenta_glazed_terracotta", Blocks.MAGENTA_GLAZED_TERRACOTTA, ReinforcedGlazedTerracottaBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_LIGHT_BLUE_GLAZED_TERRACOTTA = reinforcedBlock("reinforced_light_blue_glazed_terracotta", Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA, ReinforcedGlazedTerracottaBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_YELLOW_GLAZED_TERRACOTTA = reinforcedBlock("reinforced_yellow_glazed_terracotta", Blocks.YELLOW_GLAZED_TERRACOTTA, ReinforcedGlazedTerracottaBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_LIME_GLAZED_TERRACOTTA = reinforcedBlock("reinforced_lime_glazed_terracotta", Blocks.LIME_GLAZED_TERRACOTTA, ReinforcedGlazedTerracottaBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_PINK_GLAZED_TERRACOTTA = reinforcedBlock("reinforced_pink_glazed_terracotta", Blocks.PINK_GLAZED_TERRACOTTA, ReinforcedGlazedTerracottaBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_GRAY_GLAZED_TERRACOTTA = reinforcedBlock("reinforced_gray_glazed_terracotta", Blocks.GRAY_GLAZED_TERRACOTTA, ReinforcedGlazedTerracottaBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_LIGHT_GRAY_GLAZED_TERRACOTTA = reinforcedBlock("reinforced_light_gray_glazed_terracotta", Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA, ReinforcedGlazedTerracottaBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_CYAN_GLAZED_TERRACOTTA = reinforcedBlock("reinforced_cyan_glazed_terracotta", Blocks.CYAN_GLAZED_TERRACOTTA, ReinforcedGlazedTerracottaBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_PURPLE_GLAZED_TERRACOTTA = reinforcedBlock("reinforced_purple_glazed_terracotta", Blocks.PURPLE_GLAZED_TERRACOTTA, ReinforcedGlazedTerracottaBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_BLUE_GLAZED_TERRACOTTA = reinforcedBlock("reinforced_blue_glazed_terracotta", Blocks.BLUE_GLAZED_TERRACOTTA, ReinforcedGlazedTerracottaBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_BROWN_GLAZED_TERRACOTTA = reinforcedBlock("reinforced_brown_glazed_terracotta", Blocks.BROWN_GLAZED_TERRACOTTA, ReinforcedGlazedTerracottaBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_GREEN_GLAZED_TERRACOTTA = reinforcedBlock("reinforced_green_glazed_terracotta", Blocks.GREEN_GLAZED_TERRACOTTA, ReinforcedGlazedTerracottaBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_RED_GLAZED_TERRACOTTA = reinforcedBlock("reinforced_red_glazed_terracotta", Blocks.RED_GLAZED_TERRACOTTA, ReinforcedGlazedTerracottaBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedGlazedTerracottaBlock> REINFORCED_BLACK_GLAZED_TERRACOTTA = reinforcedBlock("reinforced_black_glazed_terracotta", Blocks.BLACK_GLAZED_TERRACOTTA, ReinforcedGlazedTerracottaBlock::new);
	@OwnableBE
	@Reinforced(registerBlockItem = false)
	public static final DeferredBlock<ReinforcedScaffoldingBlock> REINFORCED_SCAFFOLDING = reinforcedBlock("reinforced_scaffolding", Blocks.SCAFFOLDING, (p, b) -> new ReinforcedScaffoldingBlock(p.pushReaction(PushReaction.NORMAL)));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedLanternBlock> REINFORCED_LANTERN = reinforcedBlock("reinforced_lantern", Blocks.LANTERN, ReinforcedLanternBlock::new, p -> p.pushReaction(PushReaction.BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedLanternBlock> REINFORCED_SOUL_LANTERN = reinforcedBlock("reinforced_soul_lantern", Blocks.SOUL_LANTERN, ReinforcedLanternBlock::new, p -> p.pushReaction(PushReaction.BLOCK));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_SHROOMLIGHT = reinforcedBlock("reinforced_shroomlight", Blocks.SHROOMLIGHT);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_OCHRE_FROGLIGHT = reinforcedBlock("reinforced_ochre_froglight", Blocks.OCHRE_FROGLIGHT, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_VERDANT_FROGLIGHT = reinforcedBlock("reinforced_verdant_froglight", Blocks.VERDANT_FROGLIGHT, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_PEARLESCENT_FROGLIGHT = reinforcedBlock("reinforced_pearlescent_froglight", Blocks.PEARLESCENT_FROGLIGHT, ReinforcedRotatedPillarBlock::new);

	//ordered by vanilla <1.19.3 redstone tab order
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRedstoneBlock> REINFORCED_REDSTONE_BLOCK = reinforcedBlock("reinforced_redstone_block", Blocks.REDSTONE_BLOCK, ReinforcedRedstoneBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final DeferredBlock<ReinforcedPistonBaseBlock> REINFORCED_PISTON = reinforcedBlock("reinforced_piston", Blocks.PISTON, (p, b) -> new ReinforcedPistonBaseBlock(false, p));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final DeferredBlock<ReinforcedPistonBaseBlock> REINFORCED_STICKY_PISTON = reinforcedBlock("reinforced_sticky_piston", Blocks.STICKY_PISTON, (p, b) -> new ReinforcedPistonBaseBlock(true, p));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedObserverBlock> REINFORCED_OBSERVER = reinforcedBlock("reinforced_observer", Blocks.OBSERVER, (p, b) -> new ReinforcedObserverBlock(propDisguisable(p)));
	@HasManualPage
	@Reinforced
	public static final DeferredBlock<ReinforcedHopperBlock> REINFORCED_HOPPER = reinforcedBlock("reinforced_hopper", Blocks.HOPPER, (p, b) -> new ReinforcedHopperBlock(propDisguisable(p)));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final DeferredBlock<ReinforcedDispenserBlock> REINFORCED_DISPENSER = reinforcedBlock("reinforced_dispenser", Blocks.DISPENSER, (p, b) -> new ReinforcedDispenserBlock(propDisguisable(p)));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final DeferredBlock<ReinforcedDropperBlock> REINFORCED_DROPPER = reinforcedBlock("reinforced_dropper", Blocks.DROPPER, (p, b) -> new ReinforcedDropperBlock(propDisguisable(p)));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced
	public static final DeferredBlock<ReinforcedLecternBlock> REINFORCED_LECTERN = reinforcedBlock("reinforced_lectern", Blocks.LECTERN, (p, b) -> new ReinforcedLecternBlock(p));
	@HasManualPage
	@Reinforced
	public static final DeferredBlock<ReinforcedLeverBlock> REINFORCED_LEVER = reinforcedBlock("reinforced_lever", Blocks.LEVER, (p, b) -> new ReinforcedLeverBlock(p.pushReaction(PushReaction.BLOCK).forceSolidOn()));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRedstoneLampBlock> REINFORCED_REDSTONE_LAMP = reinforcedBlock("reinforced_redstone_lamp", Blocks.REDSTONE_LAMP, ReinforcedRedstoneLampBlock::new);
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
	public static final DeferredBlock<ReinforcedIronTrapDoorBlock> REINFORCED_IRON_TRAPDOOR = reinforcedBlock("reinforced_iron_trapdoor", Blocks.IRON_TRAPDOOR, (p, b) -> new ReinforcedIronTrapDoorBlock(p, BlockSetType.IRON));

	//ordered by vanilla <1.19.3 brewing tab order
	@Reinforced
	public static final DeferredBlock<ReinforcedCauldronBlock> REINFORCED_CAULDRON = reinforcedBlock("reinforced_cauldron", Blocks.CAULDRON, (p, b) -> new ReinforcedCauldronBlock(p, IReinforcedCauldronInteraction.EMPTY));
	@Reinforced(registerBlockItem = false)
	public static final DeferredBlock<ReinforcedLayeredCauldronBlock> REINFORCED_WATER_CAULDRON = reinforcedBlock("reinforced_water_cauldron", Blocks.WATER_CAULDRON, (p, b) -> new ReinforcedLayeredCauldronBlock(Biome.Precipitation.RAIN, IReinforcedCauldronInteraction.WATER, p, b));
	@Reinforced(registerBlockItem = false)
	public static final DeferredBlock<ReinforcedLavaCauldronBlock> REINFORCED_LAVA_CAULDRON = reinforcedBlock("reinforced_lava_cauldron", Blocks.LAVA_CAULDRON, (p, b) -> new ReinforcedLavaCauldronBlock(p));
	@Reinforced(registerBlockItem = false)
	public static final DeferredBlock<ReinforcedLayeredCauldronBlock> REINFORCED_POWDER_SNOW_CAULDRON = reinforcedBlock("reinforced_powder_snow_cauldron", Blocks.POWDER_SNOW_CAULDRON, (p, b) -> new ReinforcedLayeredCauldronBlock(Biome.Precipitation.SNOW, IReinforcedCauldronInteraction.POWDER_SNOW, p, b));

	//1.19.3+ content
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_BAMBOO_BLOCK = reinforcedBlock("reinforced_bamboo_block", Blocks.BAMBOO_BLOCK, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_BAMBOO_BLOCK = reinforcedBlock("reinforced_stripped_bamboo_block", Blocks.STRIPPED_BAMBOO_BLOCK, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BAMBOO_PLANKS = reinforcedBlock("reinforced_bamboo_planks", Blocks.BAMBOO_PLANKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_BAMBOO_MOSAIC = reinforcedBlock("reinforced_bamboo_mosaic", Blocks.BAMBOO_MOSAIC);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_BAMBOO_STAIRS = reinforcedBlock("reinforced_bamboo_stairs", Blocks.BAMBOO_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_BAMBOO_MOSAIC_STAIRS = reinforcedBlock("reinforced_bamboo_mosaic_stairs", Blocks.BAMBOO_MOSAIC_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_BAMBOO_SLAB = reinforcedBlock("reinforced_bamboo_slab", Blocks.BAMBOO_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_BAMBOO_MOSAIC_SLAB = reinforcedBlock("reinforced_bamboo_mosaic_slab", Blocks.BAMBOO_MOSAIC_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedPressurePlateBlock> REINFORCED_BAMBOO_PRESSURE_PLATE = woodenPressurePlate("reinforced_bamboo_pressure_plate", Blocks.BAMBOO_PRESSURE_PLATE, BlockSetType.BAMBOO);
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final DeferredBlock<ReinforcedButtonBlock> REINFORCED_BAMBOO_BUTTON = woodenButton("reinforced_bamboo_button", Blocks.BAMBOO_BUTTON, BlockSetType.BAMBOO);
	public static final DeferredBlock<SecretStandingSignBlock> SECRET_CHERRY_SIGN = secretStandingSign("secret_cherry_sign_standing", Blocks.CHERRY_SIGN, p -> p.overrideDescription("block.securitycraft.secret_cherry_sign"));
	public static final DeferredBlock<SecretWallSignBlock> SECRET_CHERRY_WALL_SIGN = secretWallSign("secret_cherry_sign_wall", Blocks.CHERRY_SIGN, p -> p.overrideDescription("block.securitycraft.secret_cherry_sign"));
	public static final DeferredBlock<SecretStandingSignBlock> SECRET_BAMBOO_SIGN = secretStandingSign("secret_bamboo_sign_standing", Blocks.BAMBOO_SIGN, p -> p.overrideDescription("block.securitycraft.secret_bamboo_sign"));
	public static final DeferredBlock<SecretWallSignBlock> SECRET_BAMBOO_WALL_SIGN = secretWallSign("secret_bamboo_sign_wall", Blocks.BAMBOO_SIGN, p -> p.overrideDescription("block.securitycraft.secret_bamboo_sign"));
	//hanging signs
	public static final DeferredBlock<SecretCeilingHangingSignBlock> SECRET_OAK_HANGING_SIGN = secretCeilingHangingSign("secret_oak_hanging_sign", Blocks.OAK_HANGING_SIGN);
	public static final DeferredBlock<SecretWallHangingSignBlock> SECRET_OAK_WALL_HANGING_SIGN = secretWallHangingSign("secret_oak_wall_hanging_sign", Blocks.OAK_HANGING_SIGN, p -> p.overrideDescription("block.securitycraft.secret_oak_hanging_sign"));
	public static final DeferredBlock<SecretCeilingHangingSignBlock> SECRET_SPRUCE_HANGING_SIGN = secretCeilingHangingSign("secret_spruce_hanging_sign", Blocks.SPRUCE_HANGING_SIGN);
	public static final DeferredBlock<SecretWallHangingSignBlock> SECRET_SPRUCE_WALL_HANGING_SIGN = secretWallHangingSign("secret_spruce_wall_hanging_sign", Blocks.SPRUCE_HANGING_SIGN, p -> p.overrideDescription("block.securitycraft.secret_spruce_hanging_sign"));
	public static final DeferredBlock<SecretCeilingHangingSignBlock> SECRET_BIRCH_HANGING_SIGN = secretCeilingHangingSign("secret_birch_hanging_sign", Blocks.BIRCH_HANGING_SIGN);
	public static final DeferredBlock<SecretWallHangingSignBlock> SECRET_BIRCH_WALL_HANGING_SIGN = secretWallHangingSign("secret_birch_wall_hanging_sign", Blocks.BIRCH_HANGING_SIGN, p -> p.overrideDescription("block.securitycraft.secret_birch_hanging_sign"));
	public static final DeferredBlock<SecretCeilingHangingSignBlock> SECRET_JUNGLE_HANGING_SIGN = secretCeilingHangingSign("secret_jungle_hanging_sign", Blocks.JUNGLE_HANGING_SIGN);
	public static final DeferredBlock<SecretWallHangingSignBlock> SECRET_JUNGLE_WALL_HANGING_SIGN = secretWallHangingSign("secret_jungle_wall_hanging_sign", Blocks.JUNGLE_HANGING_SIGN, p -> p.overrideDescription("block.securitycraft.secret_jungle_hanging_sign"));
	public static final DeferredBlock<SecretCeilingHangingSignBlock> SECRET_ACACIA_HANGING_SIGN = secretCeilingHangingSign("secret_acacia_hanging_sign", Blocks.ACACIA_HANGING_SIGN);
	public static final DeferredBlock<SecretWallHangingSignBlock> SECRET_ACACIA_WALL_HANGING_SIGN = secretWallHangingSign("secret_acacia_wall_hanging_sign", Blocks.ACACIA_HANGING_SIGN, p -> p.overrideDescription("block.securitycraft.secret_acacia_hanging_sign"));
	public static final DeferredBlock<SecretCeilingHangingSignBlock> SECRET_DARK_OAK_HANGING_SIGN = secretCeilingHangingSign("secret_dark_oak_hanging_sign", Blocks.DARK_OAK_HANGING_SIGN);
	public static final DeferredBlock<SecretWallHangingSignBlock> SECRET_DARK_OAK_WALL_HANGING_SIGN = secretWallHangingSign("secret_dark_oak_wall_hanging_sign", Blocks.DARK_OAK_HANGING_SIGN, p -> p.overrideDescription("block.securitycraft.secret_dark_oak_hanging_sign"));
	public static final DeferredBlock<SecretCeilingHangingSignBlock> SECRET_MANGROVE_HANGING_SIGN = secretCeilingHangingSign("secret_mangrove_hanging_sign", Blocks.MANGROVE_HANGING_SIGN);
	public static final DeferredBlock<SecretWallHangingSignBlock> SECRET_MANGROVE_WALL_HANGING_SIGN = secretWallHangingSign("secret_mangrove_wall_hanging_sign", Blocks.MANGROVE_HANGING_SIGN, p -> p.overrideDescription("block.securitycraft.secret_mangrove_hanging_sign"));
	public static final DeferredBlock<SecretCeilingHangingSignBlock> SECRET_CHERRY_HANGING_SIGN = secretCeilingHangingSign("secret_cherry_hanging_sign", Blocks.CHERRY_HANGING_SIGN);
	public static final DeferredBlock<SecretWallHangingSignBlock> SECRET_CHERRY_WALL_HANGING_SIGN = secretWallHangingSign("secret_cherry_wall_hanging_sign", Blocks.CHERRY_HANGING_SIGN, p -> p.overrideDescription("block.securitycraft.secret_cherry_hanging_sign"));
	public static final DeferredBlock<SecretCeilingHangingSignBlock> SECRET_PALE_OAK_HANGING_SIGN = secretCeilingHangingSign("secret_pale_oak_hanging_sign", Blocks.PALE_OAK_HANGING_SIGN);
	public static final DeferredBlock<SecretWallHangingSignBlock> SECRET_PALE_OAK_WALL_HANGING_SIGN = secretWallHangingSign("secret_pale_oak_wall_hanging_sign", Blocks.PALE_OAK_HANGING_SIGN, p -> p.overrideDescription("block.securitycraft.secret_pale_oak_hanging_sign"));
	public static final DeferredBlock<SecretCeilingHangingSignBlock> SECRET_BAMBOO_HANGING_SIGN = secretCeilingHangingSign("secret_bamboo_hanging_sign", Blocks.BAMBOO_HANGING_SIGN);
	public static final DeferredBlock<SecretWallHangingSignBlock> SECRET_BAMBOO_WALL_HANGING_SIGN = secretWallHangingSign("secret_bamboo_wall_hanging_sign", Blocks.BAMBOO_HANGING_SIGN, p -> p.overrideDescription("block.securitycraft.secret_bamboo_hanging_sign"));
	public static final DeferredBlock<SecretCeilingHangingSignBlock> SECRET_CRIMSON_HANGING_SIGN = secretCeilingHangingSign("secret_crimson_hanging_sign", Blocks.CRIMSON_HANGING_SIGN);
	public static final DeferredBlock<SecretWallHangingSignBlock> SECRET_CRIMSON_WALL_HANGING_SIGN = secretWallHangingSign("secret_crimson_wall_hanging_sign", Blocks.CRIMSON_HANGING_SIGN, p -> p.overrideDescription("block.securitycraft.secret_crimson_hanging_sign"));
	public static final DeferredBlock<SecretCeilingHangingSignBlock> SECRET_WARPED_HANGING_SIGN = secretCeilingHangingSign("secret_warped_hanging_sign", Blocks.WARPED_HANGING_SIGN);
	public static final DeferredBlock<SecretWallHangingSignBlock> SECRET_WARPED_WALL_HANGING_SIGN = secretWallHangingSign("secret_warped_wall_hanging_sign", Blocks.WARPED_HANGING_SIGN, p -> p.overrideDescription("block.securitycraft.secret_warped_hanging_sign"));
	//end hanging signs
	@HasManualPage
	@Reinforced
	public static final DeferredBlock<ReinforcedChiseledBookshelfBlock> REINFORCED_CHISELED_BOOKSHELF = reinforcedBlock("reinforced_chiseled_bookshelf", Blocks.CHISELED_BOOKSHELF, (p, b) -> new ReinforcedChiseledBookshelfBlock(p));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_CHERRY_LOG = reinforcedBlock("reinforced_cherry_log", Blocks.CHERRY_LOG, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_CHERRY_WOOD = reinforcedBlock("reinforced_cherry_wood", Blocks.CHERRY_WOOD, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_CHERRY_LOG = reinforcedBlock("reinforced_stripped_cherry_log", Blocks.STRIPPED_CHERRY_LOG, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_CHERRY_WOOD = reinforcedBlock("reinforced_stripped_cherry_wood", Blocks.STRIPPED_CHERRY_WOOD, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CHERRY_PLANKS = reinforcedBlock("reinforced_cherry_planks", Blocks.CHERRY_PLANKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_CHERRY_STAIRS = reinforcedBlock("reinforced_cherry_stairs", Blocks.CHERRY_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_CHERRY_SLAB = reinforcedBlock("reinforced_cherry_slab", Blocks.CHERRY_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedPressurePlateBlock> REINFORCED_CHERRY_PRESSURE_PLATE = woodenPressurePlate("reinforced_cherry_pressure_plate", Blocks.CHERRY_PRESSURE_PLATE, BlockSetType.CHERRY);
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final DeferredBlock<ReinforcedButtonBlock> REINFORCED_CHERRY_BUTTON = woodenButton("reinforced_cherry_button", Blocks.CHERRY_BUTTON, BlockSetType.CHERRY);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceBlock> REINFORCED_OAK_FENCE = reinforcedBlock("reinforced_oak_fence", Blocks.OAK_FENCE, ReinforcedFenceBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceBlock> REINFORCED_SPRUCE_FENCE = reinforcedBlock("reinforced_spruce_fence", Blocks.SPRUCE_FENCE, ReinforcedFenceBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceBlock> REINFORCED_BIRCH_FENCE = reinforcedBlock("reinforced_birch_fence", Blocks.BIRCH_FENCE, ReinforcedFenceBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceBlock> REINFORCED_JUNGLE_FENCE = reinforcedBlock("reinforced_jungle_fence", Blocks.JUNGLE_FENCE, ReinforcedFenceBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceBlock> REINFORCED_ACACIA_FENCE = reinforcedBlock("reinforced_acacia_fence", Blocks.ACACIA_FENCE, ReinforcedFenceBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceBlock> REINFORCED_DARK_OAK_FENCE = reinforcedBlock("reinforced_dark_oak_fence", Blocks.DARK_OAK_FENCE, ReinforcedFenceBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceBlock> REINFORCED_MANGROVE_FENCE = reinforcedBlock("reinforced_mangrove_fence", Blocks.MANGROVE_FENCE, ReinforcedFenceBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceBlock> REINFORCED_CHERRY_FENCE = reinforcedBlock("reinforced_cherry_fence", Blocks.CHERRY_FENCE, ReinforcedFenceBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceBlock> REINFORCED_PALE_OAK_FENCE = reinforcedBlock("reinforced_pale_oak_fence", Blocks.PALE_OAK_FENCE, ReinforcedFenceBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceBlock> REINFORCED_BAMBOO_FENCE = reinforcedBlock("reinforced_bamboo_fence", Blocks.BAMBOO_FENCE, ReinforcedFenceBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceBlock> REINFORCED_CRIMSON_FENCE = reinforcedBlock("reinforced_crimson_fence", Blocks.CRIMSON_FENCE, ReinforcedFenceBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceBlock> REINFORCED_WARPED_FENCE = reinforcedBlock("reinforced_warped_fence", Blocks.WARPED_FENCE, ReinforcedFenceBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceBlock> REINFORCED_NETHER_BRICK_FENCE = reinforcedBlock("reinforced_nether_brick_fence", Blocks.NETHER_BRICK_FENCE, ReinforcedFenceBlock::new);
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceGateBlock> REINFORCED_OAK_FENCE_GATE = reinforcedFenceGateBlock("reinforced_oak_fence_gate", Blocks.OAK_FENCE_GATE, WoodType.OAK);
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceGateBlock> REINFORCED_SPRUCE_FENCE_GATE = reinforcedFenceGateBlock("reinforced_spruce_fence_gate", Blocks.SPRUCE_FENCE_GATE, WoodType.SPRUCE);
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceGateBlock> REINFORCED_BIRCH_FENCE_GATE = reinforcedFenceGateBlock("reinforced_birch_fence_gate", Blocks.BIRCH_FENCE_GATE, WoodType.BIRCH);
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceGateBlock> REINFORCED_JUNGLE_FENCE_GATE = reinforcedFenceGateBlock("reinforced_jungle_fence_gate", Blocks.JUNGLE_FENCE_GATE, WoodType.JUNGLE);
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceGateBlock> REINFORCED_ACACIA_FENCE_GATE = reinforcedFenceGateBlock("reinforced_acacia_fence_gate", Blocks.ACACIA_FENCE_GATE, WoodType.ACACIA);
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceGateBlock> REINFORCED_DARK_OAK_FENCE_GATE = reinforcedFenceGateBlock("reinforced_dark_oak_fence_gate", Blocks.DARK_OAK_FENCE_GATE, WoodType.DARK_OAK);
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceGateBlock> REINFORCED_MANGROVE_FENCE_GATE = reinforcedFenceGateBlock("reinforced_mangrove_fence_gate", Blocks.MANGROVE_FENCE_GATE, WoodType.MANGROVE);
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceGateBlock> REINFORCED_CHERRY_FENCE_GATE = reinforcedFenceGateBlock("reinforced_cherry_fence_gate", Blocks.CHERRY_FENCE_GATE, WoodType.CHERRY);
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceGateBlock> REINFORCED_PALE_OAK_FENCE_GATE = reinforcedFenceGateBlock("reinforced_pale_oak_fence_gate", Blocks.PALE_OAK_FENCE_GATE, WoodType.PALE_OAK);
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceGateBlock> REINFORCED_BAMBOO_FENCE_GATE = reinforcedFenceGateBlock("reinforced_bamboo_fence_gate", Blocks.BAMBOO_FENCE_GATE, WoodType.BAMBOO);
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceGateBlock> REINFORCED_CRIMSON_FENCE_GATE = reinforcedFenceGateBlock("reinforced_crimson_fence_gate", Blocks.CRIMSON_FENCE_GATE, WoodType.CRIMSON);
	@HasManualPage(PageGroup.FENCE_GATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedFenceGateBlock> REINFORCED_WARPED_FENCE_GATE = reinforcedFenceGateBlock("reinforced_warped_fence_gate", Blocks.WARPED_FENCE_GATE, WoodType.WARPED);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CHISELED_COPPER = reinforcedBlock("reinforced_chiseled_copper", Blocks.CHISELED_COPPER);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_EXPOSED_CHISELED_COPPER = reinforcedBlock("reinforced_exposed_chiseled_copper", Blocks.EXPOSED_CHISELED_COPPER);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_WEATHERED_CHISELED_COPPER = reinforcedBlock("reinforced_weathered_chiseled_copper", Blocks.WEATHERED_CHISELED_COPPER);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_OXIDIZED_CHISELED_COPPER = reinforcedBlock("reinforced_oxidized_chiseled_copper", Blocks.OXIDIZED_CHISELED_COPPER);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCopperGrateBlock> REINFORCED_COPPER_GRATE = reinforcedBlock("reinforced_copper_grate", Blocks.COPPER_GRATE, ReinforcedCopperGrateBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCopperGrateBlock> REINFORCED_EXPOSED_COPPER_GRATE = reinforcedBlock("reinforced_exposed_copper_grate", Blocks.EXPOSED_COPPER_GRATE, ReinforcedCopperGrateBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCopperGrateBlock> REINFORCED_WEATHERED_COPPER_GRATE = reinforcedBlock("reinforced_weathered_copper_grate", Blocks.WEATHERED_COPPER_GRATE, ReinforcedCopperGrateBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCopperGrateBlock> REINFORCED_OXIDIZED_COPPER_GRATE = reinforcedBlock("reinforced_oxidized_copper_grate", Blocks.OXIDIZED_COPPER_GRATE, ReinforcedCopperGrateBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCopperBulbBlock> REINFORCED_COPPER_BULB = reinforcedBlock("reinforced_copper_bulb", Blocks.COPPER_BULB, ReinforcedCopperBulbBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCopperBulbBlock> REINFORCED_EXPOSED_COPPER_BULB = reinforcedBlock("reinforced_exposed_copper_bulb", Blocks.EXPOSED_COPPER_BULB, ReinforcedCopperBulbBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCopperBulbBlock> REINFORCED_WEATHERED_COPPER_BULB = reinforcedBlock("reinforced_weathered_copper_bulb", Blocks.WEATHERED_COPPER_BULB, ReinforcedCopperBulbBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedCopperBulbBlock> REINFORCED_OXIDIZED_COPPER_BULB = reinforcedBlock("reinforced_oxidized_copper_bulb", Blocks.OXIDIZED_COPPER_BULB, ReinforcedCopperBulbBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CHISELED_TUFF = reinforcedBlock("reinforced_chiseled_tuff", Blocks.CHISELED_TUFF);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_TUFF_STAIRS = reinforcedBlock("reinforced_tuff_stairs", Blocks.TUFF_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_TUFF_SLAB = reinforcedBlock("reinforced_tuff_slab", Blocks.TUFF_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_TUFF_WALL = reinforcedBlock("reinforced_tuff_wall", Blocks.TUFF_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_POLISHED_TUFF = reinforcedBlock("reinforced_polished_tuff", Blocks.POLISHED_TUFF);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_POLISHED_TUFF_STAIRS = reinforcedBlock("reinforced_polished_tuff_stairs", Blocks.POLISHED_TUFF_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_POLISHED_TUFF_SLAB = reinforcedBlock("reinforced_polished_tuff_slab", Blocks.POLISHED_TUFF_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_POLISHED_TUFF_WALL = reinforcedBlock("reinforced_polished_tuff_wall", Blocks.POLISHED_TUFF_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_TUFF_BRICKS = reinforcedBlock("reinforced_tuff_bricks", Blocks.TUFF_BRICKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_TUFF_BRICK_STAIRS = reinforcedBlock("reinforced_tuff_brick_stairs", Blocks.TUFF_BRICK_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_TUFF_BRICK_SLAB = reinforcedBlock("reinforced_tuff_brick_slab", Blocks.TUFF_BRICK_SLAB, ReinforcedSlabBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedWallBlock> REINFORCED_TUFF_BRICK_WALL = reinforcedBlock("reinforced_tuff_brick_wall", Blocks.TUFF_BRICK_WALL, ReinforcedWallBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CHISELED_TUFF_BRICKS = reinforcedBlock("reinforced_chiseled_tuff_bricks", Blocks.CHISELED_TUFF_BRICKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_PALE_OAK_LOG = reinforcedBlock("reinforced_pale_oak_log", Blocks.PALE_OAK_LOG, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_PALE_OAK_LOG = reinforcedBlock("reinforced_stripped_pale_oak_log", Blocks.STRIPPED_PALE_OAK_LOG, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_STRIPPED_PALE_OAK_WOOD = reinforcedBlock("reinforced_stripped_pale_oak_wood", Blocks.STRIPPED_PALE_OAK_WOOD, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedRotatedPillarBlock> REINFORCED_PALE_OAK_WOOD = reinforcedBlock("reinforced_pale_oak_wood", Blocks.PALE_OAK_WOOD, ReinforcedRotatedPillarBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_PALE_OAK_PLANKS = reinforcedBlock("reinforced_pale_oak_planks", Blocks.PALE_OAK_PLANKS);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_PALE_OAK_STAIRS = reinforcedBlock("reinforced_pale_oak_stairs", Blocks.PALE_OAK_STAIRS, ReinforcedStairsBlock::new);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_PALE_OAK_SLAB = reinforcedBlock("reinforced_pale_oak_slab", Blocks.PALE_OAK_SLAB, ReinforcedSlabBlock::new);
	public static final DeferredBlock<SecretStandingSignBlock> SECRET_PALE_OAK_SIGN = secretStandingSign("secret_pale_oak_sign_standing", Blocks.PALE_OAK_SIGN, p -> p.overrideDescription("block.securitycraft.secret_pale_oak_sign"));
	public static final DeferredBlock<SecretWallSignBlock> SECRET_PALE_OAK_WALL_SIGN = secretWallSign("secret_pale_oak_sign_wall", Blocks.PALE_OAK_SIGN, p -> p.overrideDescription("block.securitycraft.secret_pale_oak_sign"));
	@HasManualPage(PageGroup.BUTTONS)
	@Reinforced
	public static final DeferredBlock<ReinforcedButtonBlock> REINFORCED_PALE_OAK_BUTTON = woodenButton("reinforced_pale_oak_button", Blocks.PALE_OAK_BUTTON, BlockSetType.PALE_OAK);
	@HasManualPage(PageGroup.PRESSURE_PLATES)
	@Reinforced
	public static final DeferredBlock<ReinforcedPressurePlateBlock> REINFORCED_PALE_OAK_PRESSURE_PLATE = woodenPressurePlate("reinforced_pale_oak_pressure_plate", Blocks.PALE_OAK_PRESSURE_PLATE, BlockSetType.PALE_OAK);
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedMossyCarpetBlock> REINFORCED_PALE_MOSS_CARPET = reinforcedBlock("reinforced_pale_moss_carpet", Blocks.PALE_MOSS_CARPET, ReinforcedMossyCarpetBlock::new, p -> p.pushReaction(PushReaction.NORMAL));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_PALE_MOSS_BLOCK = reinforcedBlock("reinforced_pale_moss_block", Blocks.PALE_MOSS_BLOCK, p -> p.pushReaction(PushReaction.NORMAL));

	//misc
	@RegisterItemBlock
	public static final DeferredBlock<SlabBlock> CRYSTAL_QUARTZ_SLAB = BLOCKS.registerBlock("crystal_quartz_slab", SlabBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.QUARTZ_SLAB).mapColor(MapColor.COLOR_CYAN));
	@RegisterItemBlock
	public static final DeferredBlock<Block> SMOOTH_CRYSTAL_QUARTZ = BLOCKS.registerSimpleBlock("smooth_crystal_quartz", BlockBehaviour.Properties.ofFullCopy(Blocks.SMOOTH_QUARTZ).mapColor(MapColor.COLOR_CYAN));
	@RegisterItemBlock
	public static final DeferredBlock<Block> CHISELED_CRYSTAL_QUARTZ = BLOCKS.registerSimpleBlock("chiseled_crystal_quartz", BlockBehaviour.Properties.ofFullCopy(Blocks.CHISELED_QUARTZ_BLOCK).mapColor(MapColor.COLOR_CYAN));
	@HasManualPage
	@RegisterItemBlock
	public static final DeferredBlock<Block> CRYSTAL_QUARTZ_BLOCK = BLOCKS.registerSimpleBlock("crystal_quartz", BlockBehaviour.Properties.ofFullCopy(Blocks.QUARTZ_BLOCK).mapColor(MapColor.COLOR_CYAN));
	@RegisterItemBlock
	public static final DeferredBlock<Block> CRYSTAL_QUARTZ_BRICKS = BLOCKS.registerSimpleBlock("crystal_quartz_bricks", BlockBehaviour.Properties.ofFullCopy(Blocks.QUARTZ_BRICKS).mapColor(MapColor.COLOR_CYAN));
	@RegisterItemBlock
	public static final DeferredBlock<RotatedPillarBlock> CRYSTAL_QUARTZ_PILLAR = BLOCKS.registerBlock("crystal_quartz_pillar", RotatedPillarBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.QUARTZ_PILLAR).mapColor(MapColor.COLOR_CYAN));
	@RegisterItemBlock
	public static final DeferredBlock<StairBlock> CRYSTAL_QUARTZ_STAIRS = BLOCKS.registerBlock("crystal_quartz_stairs", p -> new StairBlock(CRYSTAL_QUARTZ_BLOCK.get().defaultBlockState(), p), BlockBehaviour.Properties.ofFullCopy(Blocks.QUARTZ_STAIRS).mapColor(MapColor.COLOR_CYAN));
	@RegisterItemBlock
	public static final DeferredBlock<StairBlock> SMOOTH_CRYSTAL_QUARTZ_STAIRS = BLOCKS.registerBlock("smooth_crystal_quartz_stairs", p -> new StairBlock(SMOOTH_CRYSTAL_QUARTZ.get().defaultBlockState(), p), BlockBehaviour.Properties.ofFullCopy(Blocks.SMOOTH_QUARTZ_STAIRS).mapColor(MapColor.COLOR_CYAN));
	@RegisterItemBlock
	public static final DeferredBlock<SlabBlock> SMOOTH_CRYSTAL_QUARTZ_SLAB = BLOCKS.registerBlock("smooth_crystal_quartz_slab", SlabBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.SMOOTH_QUARTZ_SLAB).mapColor(MapColor.COLOR_CYAN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(customTint = CRYSTAL_QUARTZ_TINT, itemGroup = SCItemGroup.MANUAL)
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_CRYSTAL_QUARTZ_SLAB = BLOCKS.registerBlock("reinforced_crystal_quartz_slab", p -> new ReinforcedSlabBlock(p, SCContent.CRYSTAL_QUARTZ_SLAB), reinforcedCopy(Blocks.QUARTZ_SLAB).mapColor(MapColor.COLOR_CYAN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(customTint = CRYSTAL_QUARTZ_TINT, itemGroup = SCItemGroup.MANUAL)
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_SMOOTH_CRYSTAL_QUARTZ = BLOCKS.registerBlock("reinforced_smooth_crystal_quartz", p -> new BaseReinforcedBlock(p, SCContent.SMOOTH_CRYSTAL_QUARTZ), reinforcedCopy(Blocks.SMOOTH_QUARTZ).mapColor(MapColor.COLOR_CYAN));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced(customTint = CRYSTAL_QUARTZ_TINT, itemGroup = SCItemGroup.MANUAL)
	public static final DeferredBlock<BlockPocketBlock> REINFORCED_CHISELED_CRYSTAL_QUARTZ = BLOCKS.registerBlock("reinforced_chiseled_crystal_quartz_block", p -> new BlockPocketBlock(p, SCContent.CHISELED_CRYSTAL_QUARTZ), reinforcedCopy(Blocks.CHISELED_QUARTZ_BLOCK).mapColor(MapColor.COLOR_CYAN));
	@HasManualPage(PageGroup.REINFORCED)
	@Reinforced(customTint = CRYSTAL_QUARTZ_TINT, itemGroup = SCItemGroup.MANUAL)
	public static final DeferredBlock<BlockPocketBlock> REINFORCED_CRYSTAL_QUARTZ_BLOCK = BLOCKS.registerBlock("reinforced_crystal_quartz_block", p -> new BlockPocketBlock(p, SCContent.CRYSTAL_QUARTZ_BLOCK), reinforcedCopy(Blocks.QUARTZ_BLOCK).mapColor(MapColor.COLOR_CYAN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(customTint = CRYSTAL_QUARTZ_TINT, itemGroup = SCItemGroup.MANUAL)
	public static final DeferredBlock<BaseReinforcedBlock> REINFORCED_CRYSTAL_QUARTZ_BRICKS = BLOCKS.registerBlock("reinforced_crystal_quartz_bricks", p -> new BaseReinforcedBlock(p, SCContent.CRYSTAL_QUARTZ_BRICKS), reinforcedCopy(Blocks.QUARTZ_BRICKS).mapColor(MapColor.COLOR_CYAN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(customTint = CRYSTAL_QUARTZ_TINT, itemGroup = SCItemGroup.MANUAL)
	public static final DeferredBlock<ReinforcedRotatedCrystalQuartzPillar> REINFORCED_CRYSTAL_QUARTZ_PILLAR = BLOCKS.registerBlock("reinforced_crystal_quartz_pillar", p -> new ReinforcedRotatedCrystalQuartzPillar(p, SCContent.CRYSTAL_QUARTZ_PILLAR), reinforcedCopy(Blocks.QUARTZ_PILLAR).mapColor(MapColor.COLOR_CYAN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(customTint = CRYSTAL_QUARTZ_TINT, itemGroup = SCItemGroup.MANUAL)
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_CRYSTAL_QUARTZ_STAIRS = BLOCKS.registerBlock("reinforced_crystal_quartz_stairs", p -> new ReinforcedStairsBlock(p, SCContent.CRYSTAL_QUARTZ_STAIRS), reinforcedCopy(Blocks.QUARTZ_STAIRS).mapColor(MapColor.COLOR_CYAN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(customTint = CRYSTAL_QUARTZ_TINT, itemGroup = SCItemGroup.MANUAL)
	public static final DeferredBlock<ReinforcedStairsBlock> REINFORCED_SMOOTH_CRYSTAL_QUARTZ_STAIRS = BLOCKS.registerBlock("reinforced_smooth_crystal_quartz_stairs", p -> new ReinforcedStairsBlock(p, SCContent.SMOOTH_CRYSTAL_QUARTZ_STAIRS), reinforcedCopy(Blocks.SMOOTH_QUARTZ_STAIRS).mapColor(MapColor.COLOR_CYAN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced(customTint = CRYSTAL_QUARTZ_TINT, itemGroup = SCItemGroup.MANUAL)
	public static final DeferredBlock<ReinforcedSlabBlock> REINFORCED_SMOOTH_CRYSTAL_QUARTZ_SLAB = BLOCKS.registerBlock("reinforced_smooth_crystal_quartz_slab", p -> new ReinforcedSlabBlock(p, SCContent.SMOOTH_CRYSTAL_QUARTZ_SLAB), reinforcedCopy(Blocks.SMOOTH_QUARTZ_SLAB).mapColor(MapColor.COLOR_CYAN));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	public static final DeferredBlock<HorizontalReinforcedIronBars> HORIZONTAL_REINFORCED_IRON_BARS = BLOCKS.registerBlock("horizontal_reinforced_iron_bars", HorizontalReinforcedIronBars::new, reinforcedCopy(Blocks.IRON_BARS).noLootTable().overrideDescription("block.securitycraft.reinforced_iron_bars"));
	@HasManualPage(PageGroup.REINFORCED)
	@OwnableBE
	@Reinforced
	public static final DeferredBlock<ReinforcedDirtPathBlock> REINFORCED_DIRT_PATH = reinforcedBlock("reinforced_grass_path", Blocks.DIRT_PATH, ReinforcedDirtPathBlock::new);
	@OwnableBE
	public static final DeferredBlock<ReinforcedMovingPistonBlock> REINFORCED_MOVING_PISTON = reinforcedBlock("reinforced_moving_piston", Blocks.MOVING_PISTON, (p, b) -> new ReinforcedMovingPistonBlock(p));
	@OwnableBE
	@Reinforced(registerBlockItem = false)
	public static final DeferredBlock<ReinforcedPistonHeadBlock> REINFORCED_PISTON_HEAD = reinforcedBlock("reinforced_piston_head", Blocks.PISTON_HEAD, (p, b) -> new ReinforcedPistonHeadBlock(p));
	public static final DeferredBlock<SometimesVisibleBlock> SENTRY_DISGUISE = BLOCKS.registerBlock("sentry_disguise", SometimesVisibleBlock::new, propDisguisable(-1.0F).noLootTable().pushReaction(PushReaction.BLOCK));

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
	public static final DeferredItem<SecuritySeaBoatItem> PALE_OAK_SECURITY_SEA_BOAT = ITEMS.registerItem("pale_oak_security_sea_boat", p -> new SecuritySeaBoatItem(getPaleOakSecuritySeaBoat(), p), itemProp(1).fireResistant().requiredFeatures(FeatureFlags.WINTER_DROP));
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
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CreakingHeartMineBlockEntity>> CREAKING_HEART_MINE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("creaking_heart_mine", () -> new BlockEntityType<>(CreakingHeartMineBlockEntity::new, SCContent.CREAKING_HEART_MINE.get()));

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

	public static final BlockBehaviour.Properties reinforcedCopy(Block block) {
		return reinforcedCopy(block, UnaryOperator.identity());
	}

	public static final BlockBehaviour.Properties reinforcedCopy(Block block, UnaryOperator<BlockBehaviour.Properties> propertyEditor) {
		return propertyEditor.apply(BlockBehaviour.Properties.ofFullCopy(block).explosionResistance(Float.MAX_VALUE));
	}

	private static final BlockBehaviour.Properties prop(float hardness) {
		return prop(MapColor.STONE, hardness);
	}

	private static final BlockBehaviour.Properties prop(MapColor color, float hardness) {
		return BlockBehaviour.Properties.of().mapColor(color).strength(hardness, Float.MAX_VALUE).requiresCorrectToolForDrops();
	}

	private static final BlockBehaviour.Properties propDisguisable(float hardness) {
		return propDisguisable(MapColor.STONE, hardness, true);
	}

	private static final BlockBehaviour.Properties propDisguisable(MapColor color, float hardness) {
		return propDisguisable(color, hardness, true);
	}

	private static final BlockBehaviour.Properties propDisguisable(MapColor color, float hardness, boolean forceSolidOn) {
		return propDisguisable(prop(color, hardness), forceSolidOn);
	}

	private static final BlockBehaviour.Properties propDisguisable(BlockBehaviour.Properties properties) {
		return propDisguisable(properties, true);
	}

	private static final BlockBehaviour.Properties propDisguisable(BlockBehaviour.Properties properties, boolean forceSolidOn) {
		if (forceSolidOn)
			properties.forceSolidOn();

		return properties.noOcclusion().dynamicShape().isRedstoneConductor(DisguisableBlock::isNormalCube).isSuffocating(DisguisableBlock::isSuffocating);
	}

	private static final Item.Properties itemProp() {
		return new Item.Properties();
	}

	private static final Item.Properties itemProp(int stackSize) {
		return itemProp().stacksTo(stackSize);
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

	private static DeferredBlock<BaseReinforcedBlock> reinforcedBlock(String name, Block vanillaBlock) {
		return reinforcedBlock(name, vanillaBlock, UnaryOperator.identity());
	}

	private static DeferredBlock<BaseReinforcedBlock> reinforcedBlock(String name, Block vanillaBlock, UnaryOperator<BlockBehaviour.Properties> propertyEditor) {
		return BLOCKS.registerBlock(name, p -> new BaseReinforcedBlock(p, vanillaBlock), reinforcedCopy(vanillaBlock, propertyEditor));
	}

	private static <B extends Block> DeferredBlock<B> reinforcedBlock(String name, Block vanillaBlock, BiFunction<BlockBehaviour.Properties, Block, B> constructor) {
		return reinforcedBlock(name, vanillaBlock, constructor, UnaryOperator.identity());
	}

	private static <B extends Block> DeferredBlock<B> reinforcedBlock(String name, Block vanillaBlock, BiFunction<BlockBehaviour.Properties, Block, B> constructor, UnaryOperator<BlockBehaviour.Properties> propertyEditor) {
		return BLOCKS.registerBlock(name, p -> constructor.apply(p, vanillaBlock), reinforcedCopy(vanillaBlock, propertyEditor));
	}

	private static DeferredBlock<BaseFullMineBlock> blockMine(String name, Block vanillaBlock) {
		return reinforcedBlock(name, vanillaBlock, BaseFullMineBlock::new);
	}

	private static DeferredBlock<ReinforcedButtonBlock> woodenButton(String id, Block vanillaBlock, BlockSetType blockSetType) {
		return reinforcedBlock(id, vanillaBlock, (p, _vanillaBlock) -> new ReinforcedButtonBlock(p, vanillaBlock, blockSetType, 30), p -> p.pushReaction(PushReaction.BLOCK).forceSolidOn());
	}

	private static DeferredBlock<ReinforcedButtonBlock> stoneButton(String id, Block vanillaBlock, BlockSetType blockSetType) {
		return reinforcedBlock(id, vanillaBlock, (p, _vanillaBlock) -> new ReinforcedButtonBlock(p, vanillaBlock, blockSetType, 20), p -> p.pushReaction(PushReaction.BLOCK).forceSolidOn());
	}

	private static DeferredBlock<ReinforcedPressurePlateBlock> woodenPressurePlate(String id, Block vanillaBlock, BlockSetType blockSetType) {
		return reinforcedBlock(id, vanillaBlock, (p, _vanillaBlock) -> new ReinforcedPressurePlateBlock(p, vanillaBlock, blockSetType), p -> p.pushReaction(PushReaction.BLOCK).forceSolidOn());
	}

	private static DeferredBlock<ReinforcedPressurePlateBlock> stonePressurePlate(String id, Block vanillaBlock, BlockSetType blockSetType) {
		return reinforcedBlock(id, vanillaBlock, (p, _vanillaBlock) -> new ReinforcedPressurePlateBlock(p, vanillaBlock, blockSetType), p -> p.pushReaction(PushReaction.BLOCK).forceSolidOn());
	}

	private static DeferredBlock<ReinforcedFenceGateBlock> reinforcedFenceGateBlock(String id, Block vanillaBlock, WoodType woodType) {
		return reinforcedBlock(id, vanillaBlock, (p, _vanillaBlock) -> new ReinforcedFenceGateBlock(p, woodType, vanillaBlock));
	}

	private static DeferredBlock<SecretStandingSignBlock> secretStandingSign(String id, Block standingSign, UnaryOperator<BlockBehaviour.Properties> propertyEditor) {
		return secretSign(id, SecretStandingSignBlock::new, standingSign, propertyEditor);
	}

	private static DeferredBlock<SecretWallSignBlock> secretWallSign(String id, Block standingSign, UnaryOperator<BlockBehaviour.Properties> propertyEditor) {
		return secretSign(id, SecretWallSignBlock::new, standingSign, propertyEditor);
	}

	private static DeferredBlock<SecretCeilingHangingSignBlock> secretCeilingHangingSign(String id, Block ceilingSign) {
		return secretSign(id, SecretCeilingHangingSignBlock::new, ceilingSign, UnaryOperator.identity());
	}

	private static DeferredBlock<SecretWallHangingSignBlock> secretWallHangingSign(String id, Block ceilingSign, UnaryOperator<BlockBehaviour.Properties> propertyEditor) {
		return secretSign(id, SecretWallHangingSignBlock::new, ceilingSign, propertyEditor);
	}

	private static <T extends SignBlock> DeferredBlock<T> secretSign(String id, BiFunction<BlockBehaviour.Properties, WoodType, T> signConstructor, Block baseSign, UnaryOperator<BlockBehaviour.Properties> propertyEditor) {
		return BLOCKS.registerBlock(id, p -> signConstructor.apply(p, SignBlock.getWoodType(baseSign)), propertyEditor.apply(reinforcedCopy(baseSign)));
	}

	private static <I extends Item> DeferredItem<I> registerItem(String name, Function<Item.Properties, ? extends I> func, Supplier<Item.Properties> props) {
		return ITEMS.register(name, key -> func.apply(props.get().setId(ResourceKey.create(Registries.ITEM, key))));
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
