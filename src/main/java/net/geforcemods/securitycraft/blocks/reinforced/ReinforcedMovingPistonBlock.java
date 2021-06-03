package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Collections;
import java.util.List;

import net.geforcemods.securitycraft.tileentity.ReinforcedPistonTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MovingPistonBlock;
import net.minecraft.block.PistonBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ReinforcedMovingPistonBlock extends MovingPistonBlock {

	public ReinforcedMovingPistonBlock(Block.Properties properties) {
		super(properties);
	}

	public static TileEntity createTilePiston(BlockState state, CompoundNBT tag, Direction direction, boolean extending, boolean shouldHeadBeRendered) {
		return new ReinforcedPistonTileEntity(state, tag, direction, extending, shouldHeadBeRendered);
	}

	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			TileEntity te = worldIn.getTileEntity(pos);

			if (te instanceof ReinforcedPistonTileEntity) {
				((ReinforcedPistonTileEntity)te).clearPistonTileEntity();
			}

		}
	}

	/**
	 * Called after a player destroys this Block - the posiiton pos may no longer hold the state indicated.
	 */
	@Override
	public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state) {
		BlockPos blockpos = pos.offset(state.get(FACING).getOpposite());
		BlockState blockstate = worldIn.getBlockState(blockpos);
		if (blockstate.getBlock() instanceof ReinforcedPistonBlock && blockstate.get(PistonBlock.EXTENDED)) {
			worldIn.removeBlock(blockpos, false);
		}

	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		ReinforcedPistonTileEntity reinforcedPistonTileEntity = this.getTileEntity(builder.getWorld(), new BlockPos(builder.assertPresent(LootParameters.ORIGIN)));
		return reinforcedPistonTileEntity == null ? Collections.emptyList() : reinforcedPistonTileEntity.getPistonState().getDrops(builder);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		ReinforcedPistonTileEntity reinforcedPistonTileEntity = this.getTileEntity(worldIn, pos);
		return reinforcedPistonTileEntity != null ? reinforcedPistonTileEntity.getCollisionShape(worldIn, pos) : VoxelShapes.empty();
	}

	private ReinforcedPistonTileEntity getTileEntity(IBlockReader world, BlockPos pos) {
		TileEntity tileentity = world.getTileEntity(pos);
		return tileentity instanceof ReinforcedPistonTileEntity ? (ReinforcedPistonTileEntity)tileentity : null;
	}
}
