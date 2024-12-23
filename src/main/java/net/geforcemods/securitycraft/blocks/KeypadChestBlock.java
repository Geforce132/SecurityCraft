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
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.SoundType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.DoubleSidedInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.ChestType;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMerger;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

public class KeypadChestBlock extends ChestBlock implements IOverlayDisplay, IDisguisable {
	private static final TileEntityMerger.ICallback<ChestTileEntity, Optional<INamedContainerProvider>> CONTAINER_MERGER = new TileEntityMerger.ICallback<ChestTileEntity, Optional<INamedContainerProvider>>() {
		@Override
		public Optional<INamedContainerProvider> acceptDouble(final ChestTileEntity chest1, final ChestTileEntity chest2) {
			final IInventory chestInventory = new DoubleSidedInventory(chest1, chest2);
			return Optional.of(new INamedContainerProvider() {
				@Override
				public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
					if (chest1.canOpen(player) && chest2.canOpen(player)) {
						chest1.unpackLootTable(inventory.player);
						chest2.unpackLootTable(inventory.player);
						return ChestContainer.sixRows(id, inventory, chestInventory);
					}
					else
						return null;
				}

				@Override
				public ITextComponent getDisplayName() {
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
		public Optional<INamedContainerProvider> acceptSingle(ChestTileEntity be) {
			return Optional.of(be);
		}

		@Override
		public Optional<INamedContainerProvider> acceptNone() {
			return Optional.empty();
		}
	};

	public KeypadChestBlock(AbstractBlock.Properties properties) {
		super(properties, () -> SCContent.KEYPAD_CHEST_BLOCK_ENTITY.get());
	}

	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (!level.isClientSide && !isBlocked(level, pos)) {
			KeypadChestBlockEntity be = (KeypadChestBlockEntity) level.getBlockEntity(pos);

			if (be.verifyPasscodeSet(level, pos, be, player)) {
				if (be.isDenied(player)) {
					if (be.sendsDenylistMessage())
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onDenylist"), TextFormatting.RED);
				}
				else if (be.isAllowed(player)) {
					if (be.sendsAllowlistMessage())
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onAllowlist"), TextFormatting.GREEN);

					activate(state, level, pos, player);
				}
				else if (player.getItemInHand(hand).getItem() != SCContent.CODEBREAKER.get())
					be.openPasscodeGUI(level, pos, player);
			}
		}

		return ActionResultType.SUCCESS;
	}

	public void activate(BlockState state, World level, BlockPos pos, PlayerEntity player) {
		if (!level.isClientSide) {
			ChestBlock block = (ChestBlock) state.getBlock();
			INamedContainerProvider menuProvider = block.getMenuProvider(state, level, pos);

			if (menuProvider != null) {
				player.openMenu(menuProvider);
				player.awardStat(Stats.CUSTOM.get(Stats.OPEN_CHEST));
			}
		}
	}

	@Override
	public void setPlacedBy(World level, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
		super.setPlacedBy(level, pos, state, entity, stack);

		boolean isPlayer = entity instanceof PlayerEntity;

		if (isPlayer) {
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(level, pos, (PlayerEntity) entity));

			if (state.getValue(ChestBlock.TYPE) != ChestType.SINGLE) {
				KeypadChestBlockEntity thisBe = (KeypadChestBlockEntity) level.getBlockEntity(pos);
				TileEntity otherBe = level.getBlockEntity(pos.relative(getConnectedDirection(state)));

				if (otherBe instanceof KeypadChestBlockEntity && thisBe.getOwner().owns((KeypadChestBlockEntity) otherBe)) {
					KeypadChestBlockEntity be = (KeypadChestBlockEntity) otherBe;

					for (ModuleType type : be.getInsertedModules()) {
						thisBe.insertModule(be.getModule(type), false);
					}

					thisBe.readOptions(be.writeOptions(new CompoundNBT()));

					if (be.getSaltKey() != null)
						thisBe.setSaltKey(SaltData.copySaltToNewKey(be.getSaltKey()));

					thisBe.setPasscode(be.getPasscode());
				}
			}
		}
	}

	@Override
	public Direction candidatePartnerFacing(BlockItemUseContext ctx, Direction dir) {
		Direction returnValue = super.candidatePartnerFacing(ctx, dir);

		if (returnValue != null) {
			TileEntity te = ctx.getLevel().getBlockEntity(ctx.getClickedPos().relative(dir));

			//only connect to chests which have the same owner
			if (te instanceof IOwnable && ((IOwnable) te).isOwnedBy(ctx.getPlayer()))
				return returnValue;
		}
		return null;
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	public int getSignal(BlockState state, IBlockReader level, BlockPos pos, Direction side) {
		TileEntity te = level.getBlockEntity(pos);

		if (te instanceof KeypadChestBlockEntity)
			return ((KeypadChestBlockEntity) te).isModuleEnabled(ModuleType.REDSTONE) ? MathHelper.clamp(((KeypadChestBlockEntity) te).getNumPlayersUsing(), 0, 15) : 0;

		return 0;
	}

	@Override
	public int getDirectSignal(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		return side == Direction.UP ? state.getSignal(world, pos, side) : 0;
	}

	@Override
	public void onNeighborChange(BlockState state, IWorldReader level, BlockPos pos, BlockPos neighbor) {
		super.onNeighborChange(state, level, pos, neighbor);

		TileEntity te = level.getBlockEntity(pos);

		if (te instanceof KeypadChestBlockEntity)
			((KeypadChestBlockEntity) te).clearCache();
	}

	@Override
	public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			TileEntity be = level.getBlockEntity(pos);

			if (be instanceof IPasscodeProtected)
				SaltData.removeSalt(((IPasscodeProtected) be).getSaltKey());
		}

		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public INamedContainerProvider getMenuProvider(BlockState state, World level, BlockPos pos) {
		return combine(state, level, pos, false).apply(CONTAINER_MERGER).orElse(null);
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader level) {
		return new KeypadChestBlockEntity();
	}

	public static boolean isBlocked(World level, BlockPos pos) {
		return isBelowSolidBlock(level, pos);
	}

	private static boolean isBelowSolidBlock(World level, BlockPos pos) {
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
	public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext ctx) {
		BlockState disguisedState = IDisguisable.getDisguisedBlockState(level.getBlockEntity(pos)).orElse(state);

		if (disguisedState.getBlock() != this)
			return disguisedState.getShape(level, pos, ctx);
		else
			return super.getShape(state, level, pos, ctx);
	}

	@Override
	public int getLightValue(BlockState state, IBlockReader level, BlockPos pos) {
		BlockState disguisedState = IDisguisable.getDisguisedBlockState(level.getBlockEntity(pos)).orElse(state);

		if (disguisedState.getBlock() != this)
			return disguisedState.getLightValue(level, pos);
		else
			return super.getLightValue(state, level, pos);
	}

	@Override
	public SoundType getSoundType(BlockState state, IWorldReader level, BlockPos pos, Entity entity) {
		BlockState disguisedState = IDisguisable.getDisguisedBlockState(level.getBlockEntity(pos)).orElse(state);

		if (disguisedState.getBlock() != this)
			return disguisedState.getSoundType(level, pos, entity);
		else
			return super.getSoundType(state, level, pos, entity);
	}

	@Override
	public float getShadeBrightness(BlockState state, IBlockReader level, BlockPos pos) {
		BlockState disguisedState = IDisguisable.getDisguisedBlockState(level.getBlockEntity(pos)).orElse(state);

		if (disguisedState.getBlock() != this)
			return disguisedState.getShadeBrightness(level, pos);
		else
			return super.getShadeBrightness(state, level, pos);
	}

	@Override
	public int getLightBlock(BlockState state, IBlockReader level, BlockPos pos) {
		BlockState disguisedState = IDisguisable.getDisguisedBlockState(level.getBlockEntity(pos)).orElse(state);

		if (disguisedState.getBlock() != this)
			return disguisedState.getLightBlock(level, pos);
		else
			return super.getLightBlock(state, level, pos);
	}

	@Override
	public ItemStack getDisplayStack(World level, BlockState state, BlockPos pos) {
		return getDisguisedStack(level, pos);
	}

	@Override
	public boolean shouldShowSCInfo(World level, BlockState state, BlockPos pos) {
		return getDisguisedStack(level, pos).getItem() == asItem();
	}

	@Override
	public ItemStack getCloneItemStack(IBlockReader level, BlockPos pos, BlockState state) {
		return getDisguisedStack(level, pos);
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
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
		public boolean protect(PlayerEntity player, World level, BlockPos pos) {
			convert(player, level, pos, true);
			return true;
		}

		@Override
		public boolean unprotect(PlayerEntity player, World level, BlockPos pos) {
			convert(player, level, pos, false);
			return true;
		}

		private void convert(PlayerEntity player, World level, BlockPos pos, boolean protect) {
			BlockState state = level.getBlockState(pos);
			Direction facing = state.getValue(FACING);
			ChestType type = state.getValue(TYPE);
			ChestTileEntity chest = (ChestTileEntity) level.getBlockEntity(pos);

			if (!protect)
				((IModuleInventory) chest).dropAllModules();

			convertSingleChest(chest, player, level, pos, state, facing, type, protect);

			if (type != ChestType.SINGLE) {
				BlockPos newPos = pos.relative(getConnectedDirection(state));
				BlockState newState = level.getBlockState(newPos);

				convertSingleChest((ChestTileEntity) level.getBlockEntity(newPos), player, level, newPos, newState, facing, type.getOpposite(), protect);
			}
		}

		private void convertSingleChest(ChestTileEntity chest, PlayerEntity player, World level, BlockPos pos, BlockState oldChestState, Direction facing, ChestType type, boolean protect) {
			CompoundNBT tag;
			Block convertedBlock;

			if (protect)
				convertedBlock = SCContent.KEYPAD_CHEST.get();
			else {
				convertedBlock = ForgeRegistries.BLOCKS.getValue(((KeypadChestBlockEntity) chest).getPreviousChest());

				if (convertedBlock == Blocks.AIR)
					convertedBlock = Blocks.CHEST;
			}

			chest.unpackLootTable(player); //generate loot (if any), so items don't spill out when converting and no additional loot table is generated
			tag = chest.save(new CompoundNBT());
			chest.clearContent();
			level.setBlockAndUpdate(pos, convertedBlock.defaultBlockState().setValue(FACING, facing).setValue(TYPE, type));
			chest = (ChestTileEntity) level.getBlockEntity(pos);
			chest.load(null, tag);

			if (protect) {
				if (player != null)
					((IOwnable) chest).setOwner(player.getUUID().toString(), player.getName().getString());

				((KeypadChestBlockEntity) chest).setPreviousChest(oldChestState.getBlock());
			}
		}
	}
}
