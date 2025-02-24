package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasscodeConvertible;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadBlockEntity;
import net.geforcemods.securitycraft.misc.SaltData;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class KeypadBlock extends DisguisableBlock {
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public KeypadBlock(AbstractBlock.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, false).setValue(WATERLOGGED, false));
	}

	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		KeypadBlockEntity be = (KeypadBlockEntity) level.getBlockEntity(pos);

		if (state.getValue(POWERED) && be.getSignalLength() > 0)
			return ActionResultType.PASS;
		else if (!level.isClientSide) {
			if (be.isDisabled())
				player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
			else if (be.verifyPasscodeSet(level, pos, be, player)) {
				if (be.isDenied(player)) {
					if (be.sendsDenylistMessage())
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onDenylist"), TextFormatting.RED);
				}
				else if (be.isAllowed(player)) {
					if (be.sendsAllowlistMessage())
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onAllowlist"), TextFormatting.GREEN);

					activate(level, pos, be.getSignalLength());
				}
				else if (player.getItemInHand(hand).getItem() != SCContent.CODEBREAKER.get())
					be.openPasscodeGUI(level, pos, player);
			}
		}

		return ActionResultType.SUCCESS;
	}

	public void activate(World level, BlockPos pos, int signalLength) {
		level.setBlockAndUpdate(pos, level.getBlockState(pos).cycle(POWERED));
		BlockUtils.updateIndirectNeighbors(level, pos, SCContent.KEYPAD.get());

		if (signalLength > 0)
			level.getBlockTicks().scheduleTick(pos, this, signalLength);
	}

	@Override
	public void tick(BlockState state, ServerWorld level, BlockPos pos, Random random) {
		if (state.getValue(POWERED)) {
			level.setBlockAndUpdate(pos, state.setValue(POWERED, false));
			BlockUtils.updateIndirectNeighbors(level, pos, SCContent.KEYPAD.get());
		}
	}

	@Override
	public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			TileEntity be = level.getBlockEntity(pos);

			if (state.getValue(POWERED))
				BlockUtils.updateIndirectNeighbors(level, pos, this);

			if (be instanceof IPasscodeProtected)
				SaltData.removeSalt(((IPasscodeProtected) be).getSaltKey());
		}

		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	public boolean shouldCheckWeakPower(BlockState state, IWorldReader level, BlockPos pos, Direction side) {
		return false;
	}

	@Override
	public int getSignal(BlockState state, IBlockReader level, BlockPos pos, Direction side) {
		if (state.getValue(POWERED))
			return 15;
		else
			return 0;
	}

	@Override
	public int getDirectSignal(BlockState state, IBlockReader level, BlockPos pos, Direction side) {
		if (state.getValue(POWERED))
			return 15;
		else
			return 0;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(POWERED, false);
	}

	@Override
	public ItemStack getCloneItemStack(IBlockReader level, BlockPos pos, BlockState state) {
		return ItemStack.EMPTY;
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED, WATERLOGGED);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new KeypadBlockEntity();
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
		public boolean isUnprotectedBlock(BlockState state) {
			return state.is(SCContent.FRAME.get());
		}

		@Override
		public boolean isProtectedBlock(BlockState state) {
			return state.is(SCContent.KEYPAD.get());
		}

		@Override
		public boolean protect(PlayerEntity player, World level, BlockPos pos) {
			FrameBlockEntity be = (FrameBlockEntity) level.getBlockEntity(pos);

			if (be.getCurrentCamera() != null) {
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.FRAME.get().getDescriptionId()), Utils.localize("messages.securitycraft:frame.cannotConvert"), TextFormatting.RED);
				return false;
			}

			Owner owner = be.getOwner();

			be.dropAllModules();
			level.setBlockAndUpdate(pos, SCContent.KEYPAD.get().defaultBlockState().setValue(KeypadBlock.FACING, level.getBlockState(pos).getValue(FrameBlock.FACING)).setValue(KeypadBlock.POWERED, false));
			((IOwnable) level.getBlockEntity(pos)).setOwner(owner.getUUID(), owner.getName());
			return true;
		}

		@Override
		public boolean unprotect(PlayerEntity player, World level, BlockPos pos) {
			Owner owner = ((IOwnable) level.getBlockEntity(pos)).getOwner();

			((IModuleInventory) level.getBlockEntity(pos)).dropAllModules();
			level.setBlockAndUpdate(pos, SCContent.FRAME.get().defaultBlockState().setValue(FrameBlock.FACING, level.getBlockState(pos).getValue(KeypadBlock.FACING)));
			((IOwnable) level.getBlockEntity(pos)).setOwner(owner.getUUID(), owner.getName());
			return true;
		}
	}
}
