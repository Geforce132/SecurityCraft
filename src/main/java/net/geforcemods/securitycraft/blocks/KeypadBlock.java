package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordConvertible;
import net.geforcemods.securitycraft.blockentities.KeypadBlockEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
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

	public KeypadBlock(Block.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, false));
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (state.getValue(POWERED))
			return ActionResultType.PASS;
		else {
			KeypadBlockEntity te = (KeypadBlockEntity) world.getBlockEntity(pos);

			if (ModuleUtils.isDenied(te, player)) {
				if (te.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onDenylist"), TextFormatting.RED);
			}
			else if (ModuleUtils.isAllowed(te, player)) {
				if (te.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onAllowlist"), TextFormatting.GREEN);

				activate(state, world, pos, te.getSignalLength());
			}
			else if (!PlayerUtils.isHoldingItem(player, SCContent.CODEBREAKER, hand))
				te.openPasswordGUI(player);
		}

		return ActionResultType.SUCCESS;
	}

	public void activate(BlockState state, World world, BlockPos pos, int signalLength) {
		world.setBlockAndUpdate(pos, world.getBlockState(pos).setValue(POWERED, true));
		BlockUtils.updateIndirectNeighbors(world, pos, SCContent.KEYPAD.get());
		world.getBlockTicks().scheduleTick(pos, this, signalLength);
	}

	@Override
	public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		world.setBlockAndUpdate(pos, state.setValue(POWERED, false));
		BlockUtils.updateIndirectNeighbors(world, pos, SCContent.KEYPAD.get());
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	public boolean shouldCheckWeakPower(BlockState state, IWorldReader world, BlockPos pos, Direction side) {
		return false;
	}

	@Override
	public int getSignal(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		if (blockState.getValue(POWERED))
			return 15;
		else
			return 0;
	}

	@Override
	public int getDirectSignal(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		if (blockState.getValue(POWERED))
			return 15;
		else
			return 0;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		return getStateForPlacement(ctx.getLevel(), ctx.getClickedPos(), ctx.getClickedFace(), ctx.getClickLocation().x, ctx.getClickLocation().y, ctx.getClickLocation().z, ctx.getPlayer());
	}

	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, PlayerEntity placer) {
		return defaultBlockState().setValue(FACING, placer.getDirection().getOpposite()).setValue(POWERED, false);
	}

	@Override
	public ItemStack getCloneItemStack(IBlockReader worldIn, BlockPos pos, BlockState state) {
		return ItemStack.EMPTY;
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
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

	public static class Convertible implements IPasswordConvertible {
		@Override
		public Block getOriginalBlock() {
			return SCContent.FRAME.get();
		}

		@Override
		public boolean convert(PlayerEntity player, World world, BlockPos pos) {
			world.setBlockAndUpdate(pos, SCContent.KEYPAD.get().defaultBlockState().setValue(KeypadBlock.FACING, world.getBlockState(pos).getValue(FrameBlock.FACING)).setValue(KeypadBlock.POWERED, false));
			((IOwnable) world.getBlockEntity(pos)).setOwner(player.getUUID().toString(), player.getName().getString());
			return true;
		}
	}
}
