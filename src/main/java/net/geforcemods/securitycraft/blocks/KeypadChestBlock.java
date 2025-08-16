package net.geforcemods.securitycraft.blocks;

import java.util.Optional;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasscodeConvertible;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.blockentities.KeypadChestBlockEntity;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.misc.SaltData;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.world.AuxiliaryLightManager;

public class KeypadChestBlock extends ChestBlock implements IOverlayDisplay, IDisguisable {
	private static final DoubleBlockCombiner.Combiner<ChestBlockEntity, Optional<MenuProvider>> CONTAINER_MERGER = new DoubleBlockCombiner.Combiner<>() {
		@Override
		public Optional<MenuProvider> acceptDouble(final ChestBlockEntity chest1, final ChestBlockEntity chest2) {
			final Container chestInventory = new CompoundContainer(chest1, chest2);
			return Optional.of(new MenuProvider() {
				@Override
				public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
					if (chest1.canOpen(player) && chest2.canOpen(player)) {
						chest1.unpackLootTable(inventory.player);
						chest2.unpackLootTable(inventory.player);
						return ChestMenu.sixRows(id, inventory, chestInventory);
					}
					else
						return null;
				}

				@Override
				public Component getDisplayName() {
					//makes sure Jade's overlay is not too wide when the chest is disguised
					//one side effect is the title in the chest's screen is also changed
					if (((KeypadChestBlockEntity) chest1).isModuleEnabled(ModuleType.DISGUISE))
						return Utils.localize(IDisguisable.getDisguisedBlockState(chest1).orElse(chest1.getBlockState()).getBlock().getDescriptionId());

					if (chest1.hasCustomName())
						return chest1.getDisplayName();
					else
						return chest2.hasCustomName() ? chest2.getDisplayName() : Utils.localize("block.securitycraft.keypad_chest_double");
				}
			});
		}

		@Override
		public Optional<MenuProvider> acceptSingle(ChestBlockEntity be) {
			return Optional.of(be);
		}

		@Override
		public Optional<MenuProvider> acceptNone() {
			return Optional.empty();
		}
	};
	private final float destroyTimeForOwner;

	public KeypadChestBlock(BlockBehaviour.Properties properties) {
		super(SCContent.KEYPAD_CHEST_BLOCK_ENTITY, OwnableBlock.withReinforcedDestroyTime(properties));
		destroyTimeForOwner = OwnableBlock.getStoredDestroyTime();
	}

	@Override
	public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
		return BlockUtils.getDestroyProgress(super::getDestroyProgress, destroyTimeForOwner, state, player, level, pos);
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (!level.isClientSide && !isBlocked(level, pos)) {
			KeypadChestBlockEntity be = (KeypadChestBlockEntity) level.getBlockEntity(pos);

			if (be.verifyPasscodeSet(level, pos, be, player)) {
				if (be.isDenied(player)) {
					if (be.sendsDenylistMessage())
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onDenylist"), ChatFormatting.RED);
				}
				else if (be.isAllowed(player)) {
					if (be.sendsAllowlistMessage())
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onAllowlist"), ChatFormatting.GREEN);

					activate(state, level, pos, player);
				}
				else
					be.openPasscodeGUI(level, pos, player);
			}
		}

		return InteractionResult.SUCCESS;
	}

	public void activate(BlockState state, Level level, BlockPos pos, Player player) {
		if (!level.isClientSide) {
			KeypadChestBlock block = (KeypadChestBlock) state.getBlock();
			MenuProvider menuProvider = block.getMenuProvider(state, level, pos);

			if (menuProvider != null) {
				player.openMenu(menuProvider);
				player.awardStat(Stats.CUSTOM.get(Stats.OPEN_CHEST));
			}
		}
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
		super.setPlacedBy(level, pos, state, entity, stack);

		if (entity instanceof Player player) {
			NeoForge.EVENT_BUS.post(new OwnershipEvent(level, pos, player));

			if (state.getValue(TYPE) != ChestType.SINGLE) {
				KeypadChestBlockEntity thisBe = (KeypadChestBlockEntity) level.getBlockEntity(pos);
				BlockEntity otherBe = level.getBlockEntity(pos.relative(getConnectedDirection(state)));

				if (otherBe instanceof KeypadChestBlockEntity be && thisBe.getOwner().owns(be)) {
					for (ModuleType type : be.getInsertedModules()) {
						thisBe.insertModule(be.getModule(type), false);
					}

					thisBe.readOptions(be.writeOptions(new CompoundTag()));

					if (be.getSaltKey() != null)
						thisBe.setSaltKey(SaltData.copySaltToNewKey(be.getSaltKey()));

					thisBe.setPasscode(be.getPasscode());
				}
			}
		}
	}

	@Override
	public Direction candidatePartnerFacing(BlockPlaceContext ctx, Direction dir) {
		Direction returnValue = super.candidatePartnerFacing(ctx, dir);

		//only connect to chests which have the same owner
		if (returnValue != null && ctx.getLevel().getBlockEntity(ctx.getClickedPos().relative(dir)) instanceof IOwnable ownable && ownable.isOwnedBy(ctx.getPlayer()))
			return returnValue;

		return null;
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
		if (level.getBlockEntity(pos) instanceof KeypadChestBlockEntity be)
			return be.isModuleEnabled(ModuleType.REDSTONE) ? Mth.clamp(be.getNumPlayersUsing(), 0, 15) : 0;
		else
			return 0;
	}

	@Override
	public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
		return side == Direction.UP ? state.getSignal(level, pos, side) : 0;
	}

	@Override
	public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
		super.onNeighborChange(state, level, pos, neighbor);

		if (level.getBlockEntity(pos) instanceof KeypadChestBlockEntity be)
			be.setBlockState(state);
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof IPasscodeProtected be)
			SaltData.removeSalt(be.getSaltKey());

		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
		return combine(state, level, pos, false).apply(CONTAINER_MERGER).orElse(null);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new KeypadChestBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return level.isClientSide ? createTickerHelper(type, SCContent.KEYPAD_CHEST_BLOCK_ENTITY.get(), KeypadChestBlockEntity::lidAnimateTick) : null;
	}

	public static boolean isBlocked(Level level, BlockPos pos) {
		return isBelowSolidBlock(level, pos);
	}

	private static boolean isBelowSolidBlock(Level level, BlockPos pos) {
		return level.getBlockState(pos.above()).isRedstoneConductor(level, pos.above());
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		BlockState disguisedState = IDisguisable.getDisguisedBlockState(level.getBlockEntity(pos)).orElse(state);

		if (disguisedState.getBlock() != this)
			return disguisedState.getShape(level, pos, ctx);
		else
			return super.getShape(state, level, pos, ctx);
	}

	@Override
	public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
		AuxiliaryLightManager lightManager = level.getAuxLightManager(pos);
		int lightValue = 0;

		if (lightManager != null)
			lightValue = lightManager.getLightAt(pos);

		return lightValue > 0 ? lightValue : super.getLightEmission(state, level, pos);
	}

	@Override
	public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, Entity entity) {
		BlockState disguisedState = IDisguisable.getDisguisedBlockState(level.getBlockEntity(pos)).orElse(state);

		if (disguisedState.getBlock() != this)
			return disguisedState.getSoundType(level, pos, entity);
		else
			return super.getSoundType(state, level, pos, entity);
	}

	@Override
	public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
		BlockState disguisedState = IDisguisable.getDisguisedBlockState(level.getBlockEntity(pos)).orElse(state);

		if (disguisedState.getBlock() != this)
			return disguisedState.getShadeBrightness(level, pos);
		else
			return super.getShadeBrightness(state, level, pos);
	}

	@Override
	public BlockState getAppearance(BlockState state, BlockAndTintGetter level, BlockPos pos, Direction side, BlockState queryState, BlockPos queryPos) {
		return IDisguisable.getDisguisedBlockState(level.getBlockEntity(pos)).orElse(state);
	}

	@Override
	public ItemStack getDisplayStack(Level level, BlockState state, BlockPos pos) {
		return getDisguisedStack(level, pos);
	}

	@Override
	public boolean shouldShowSCInfo(Level level, BlockState state, BlockPos pos) {
		return getDisguisedStack(level, pos).getItem() == asItem();
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
		if (IDisguisable.shouldPickBlockDisguise(level, pos, player))
			return getDisguisedStack(level, pos);

		return super.getCloneItemStack(state, target, level, pos, player);
	}

	@Override
	protected RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public boolean hasDynamicLightEmission(BlockState state) {
		return true;
	}

	public static class Convertible implements IPasscodeConvertible {
		@Override
		public boolean isUnprotectedBlock(BlockState state) {
			return state.is(Tags.Blocks.CHESTS_WOODEN);
		}

		@Override
		public boolean isProtectedBlock(BlockState state) {
			return state.is(SCContent.KEYPAD_CHEST.get());
		}

		@Override
		public boolean protect(Player player, Level level, BlockPos pos) {
			convert(player, level, pos, true);
			return true;
		}

		@Override
		public boolean unprotect(Player player, Level level, BlockPos pos) {
			convert(player, level, pos, false);
			return true;
		}

		private void convert(Player player, Level level, BlockPos pos, boolean protect) {
			BlockState state = level.getBlockState(pos);
			Direction facing = state.getValue(FACING);
			ChestType type = state.getValue(TYPE);
			ChestBlockEntity chest = (ChestBlockEntity) level.getBlockEntity(pos);

			if (!protect)
				((IModuleInventory) chest).dropAllModules();

			convertSingleChest(chest, player, level, pos, state, facing, type, protect);

			if (type != ChestType.SINGLE) {
				BlockPos newPos = pos.relative(getConnectedDirection(state));
				BlockState newState = level.getBlockState(newPos);

				convertSingleChest((ChestBlockEntity) level.getBlockEntity(newPos), player, level, newPos, newState, facing, type.getOpposite(), protect);
			}
		}

		private void convertSingleChest(ChestBlockEntity chest, Player player, Level level, BlockPos pos, BlockState oldChestState, Direction facing, ChestType type, boolean protect) {
			CompoundTag tag;
			Block convertedBlock;

			if (protect)
				convertedBlock = SCContent.KEYPAD_CHEST.get();
			else
				convertedBlock = BuiltInRegistries.BLOCK.get(((KeypadChestBlockEntity) chest).getPreviousChest()).map(Reference::value).orElse(Blocks.CHEST);

			chest.unpackLootTable(player); //generate loot (if any), so items don't spill out when converting and no additional loot table is generated
			tag = chest.saveWithFullMetadata(level.registryAccess());
			chest.clearContent();
			level.setBlockAndUpdate(pos, convertedBlock.defaultBlockState().setValue(FACING, facing).setValue(TYPE, type));
			chest = (ChestBlockEntity) level.getBlockEntity(pos);
			chest.loadWithComponents(tag, level.registryAccess());

			if (protect) {
				if (player != null)
					((IOwnable) chest).setOwner(player.getUUID().toString(), player.getName().getString());

				((KeypadChestBlockEntity) chest).setPreviousChest(oldChestState.getBlock());
			}
		}
	}
}
