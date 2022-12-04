package net.geforcemods.securitycraft.blocks;

import java.util.stream.Stream;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.TrophySystemBlockEntity;
import net.geforcemods.securitycraft.network.client.OpenScreen;
import net.geforcemods.securitycraft.network.client.OpenScreen.DataType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

public class TrophySystemBlock extends DisguisableBlock {
	//@formatter:off
	private static final VoxelShape SHAPE = Stream.of(
			Block.box(6.5, 0, 12, 9.5, 1.5, 15),
			Block.box(5.5, 7, 5.5, 10.5, 11, 10.5),
			Block.box(7, 12, 7, 9, 13, 9),
			Block.box(6.5, 12.5, 6.5, 9.5, 15, 9.5),
			Block.box(7, 14.5, 7, 9, 15.5, 9),
			Block.box(7.25, 9, 7.25, 8.75, 12, 8.75),
			Block.box(1, 0, 6.5, 4, 1.5, 9.5),
			Block.box(12, 0, 6.5, 15, 1.5, 9.5),
			Block.box(6.5, 0, 1, 9.5, 1.5, 4)
			).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).orElse(VoxelShapes.block());
	//@formatter:on
	public TrophySystemBlock(Block.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(WATERLOGGED, false));
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
		return BlockUtils.isSideSolid(world, pos.below(), Direction.UP);
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean flag) {
		if (!canSurvive(state, world, pos))
			world.destroyBlock(pos, true);
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		TileEntity tile = world.getBlockEntity(pos);

		if (tile instanceof TrophySystemBlockEntity) {
			TrophySystemBlockEntity be = (TrophySystemBlockEntity) tile;

			if (be.isOwner(player)) {
				if (!world.isClientSide) {
					if (be.isDisabled())
						player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
					else
						SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new OpenScreen(DataType.TROPHY_SYSTEM, pos));
				}

				return ActionResultType.SUCCESS;
			}
		}

		return ActionResultType.PASS;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx) {
		BlockState disguisedState = getDisguisedStateOrDefault(state, world, pos);

		if (disguisedState.getBlock() != this)
			return disguisedState.getShape(world, pos, ctx);
		else
			return SHAPE;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TrophySystemBlockEntity();
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED);
	}
}
