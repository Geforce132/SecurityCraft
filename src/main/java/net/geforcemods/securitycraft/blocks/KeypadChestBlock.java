package net.geforcemods.securitycraft.blocks;

import java.util.Optional;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasscodeConvertible;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.blockentities.KeypadChestBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.misc.SaltData;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;

public class KeypadChestBlock extends ChestBlock {
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
					if (chest1.hasCustomName())
						return chest1.getDisplayName();
					else
						return chest2.hasCustomName() ? chest2.getDisplayName() : Utils.localize("block.securitycraft.keypad_chest_double");
				}
			});
		}

		@Override
		public Optional<INamedContainerProvider> acceptSingle(ChestTileEntity te) {
			return Optional.of(te);
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
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (!world.isClientSide && !isBlocked(world, pos)) {
			KeypadChestBlockEntity te = (KeypadChestBlockEntity) world.getBlockEntity(pos);

			if (te.verifyPasscodeSet(world, pos, te, player)) {
				if (te.isDenied(player)) {
					if (te.sendsMessages())
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onDenylist"), TextFormatting.RED);
				}
				else if (te.isAllowed(player)) {
					if (te.sendsMessages())
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onAllowlist"), TextFormatting.GREEN);

					activate(state, world, pos, player);
				}
				else if (!PlayerUtils.isHoldingItem(player, SCContent.CODEBREAKER, hand))
					te.openPasscodeGUI(world, pos, player);
			}
		}

		return ActionResultType.SUCCESS;
	}

	public void activate(BlockState state, World world, BlockPos pos, PlayerEntity player) {
		if (!world.isClientSide) {
			ChestBlock block = (ChestBlock) state.getBlock();
			INamedContainerProvider containerProvider = block.getMenuProvider(state, world, pos);

			if (containerProvider != null) {
				player.openMenu(containerProvider);
				player.awardStat(Stats.CUSTOM.get(Stats.OPEN_CHEST));
			}
		}
	}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
		super.setPlacedBy(world, pos, state, entity, stack);

		boolean isPlayer = entity instanceof PlayerEntity;

		if (isPlayer) {
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (PlayerEntity) entity));

			if (state.getValue(ChestBlock.TYPE) != ChestType.SINGLE) {
				KeypadChestBlockEntity thisTe = (KeypadChestBlockEntity) world.getBlockEntity(pos);
				TileEntity otherTe = world.getBlockEntity(pos.relative(getConnectedDirection(state)));

				if (otherTe instanceof KeypadChestBlockEntity && thisTe.getOwner().owns((KeypadChestBlockEntity) otherTe)) {
					KeypadChestBlockEntity te = (KeypadChestBlockEntity) otherTe;

					for (ModuleType type : te.getInsertedModules()) {
						thisTe.insertModule(te.getModule(type), false);
					}

					thisTe.readOptions(te.writeOptions(new CompoundNBT()));

					if (te.getSaltKey() != null)
						thisTe.setSaltKey(SaltData.putSalt(te.getSalt()));

					thisTe.setPasscode(te.getPasscode());
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
	public int getSignal(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		TileEntity te = world.getBlockEntity(pos);

		if (te instanceof KeypadChestBlockEntity)
			return ((KeypadChestBlockEntity) te).isModuleEnabled(ModuleType.REDSTONE) ? MathHelper.clamp(((KeypadChestBlockEntity) te).getNumPlayersUsing(), 0, 15) : 0;

		return 0;
	}

	@Override
	public int getDirectSignal(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		return side == Direction.UP ? state.getSignal(world, pos, side) : 0;
	}

	@Override
	public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
		super.onNeighborChange(state, world, pos, neighbor);

		TileEntity tileEntity = world.getBlockEntity(pos);

		if (tileEntity instanceof KeypadChestBlockEntity)
			((KeypadChestBlockEntity) tileEntity).clearCache();
	}

	@Override
	public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			TileEntity be = level.getBlockEntity(pos);

			if (be instanceof IPasscodeProtected)
				SaltData.removeSalt(((IPasscodeProtected) be).getSaltKey());

			super.onRemove(state, level, pos, newState, isMoving);
		}
	}

	@Override
	public INamedContainerProvider getMenuProvider(BlockState state, World world, BlockPos pos) {
		return combine(state, world, pos, false).apply(CONTAINER_MERGER).orElse(null);
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader reader) {
		return new KeypadChestBlockEntity();
	}

	public static boolean isBlocked(World world, BlockPos pos) {
		return isBelowSolidBlock(world, pos);
	}

	private static boolean isBelowSolidBlock(World world, BlockPos pos) {
		return world.getBlockState(pos.above()).isRedstoneConductor(world, pos.above());
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	public static class Convertible implements IPasscodeConvertible {
		@Override
		public boolean isValidStateForConversion(BlockState state) {
			return state.is(Tags.Blocks.CHESTS_WOODEN);
		}

		@Override
		public boolean convert(PlayerEntity player, World world, BlockPos pos) {
			BlockState state = world.getBlockState(pos);
			Direction facing = state.getValue(FACING);
			ChestType type = state.getValue(TYPE);

			convertChest(player, world, pos, facing, type);

			if (type != ChestType.SINGLE) {
				BlockPos newPos = pos.relative(getConnectedDirection(state));
				BlockState newState = world.getBlockState(newPos);
				Direction newFacing = newState.getValue(FACING);
				ChestType newType = newState.getValue(TYPE);

				convertChest(player, world, newPos, newFacing, newType);
			}

			return true;
		}

		private void convertChest(PlayerEntity player, World world, BlockPos pos, Direction facing, ChestType type) {
			ChestTileEntity chest = (ChestTileEntity) world.getBlockEntity(pos);
			CompoundNBT tag;

			chest.unpackLootTable(player); //generate loot (if any), so items don't spill out when converting and no additional loot table is generated
			tag = chest.save(new CompoundNBT());
			chest.clearContent();
			world.setBlockAndUpdate(pos, SCContent.KEYPAD_CHEST.get().defaultBlockState().setValue(FACING, facing).setValue(TYPE, type));
			chest = ((ChestTileEntity) world.getBlockEntity(pos));
			chest.load(world.getBlockState(pos), tag);
			((IOwnable) chest).setOwner(player.getUUID().toString(), player.getName().getString());
		}
	}
}
