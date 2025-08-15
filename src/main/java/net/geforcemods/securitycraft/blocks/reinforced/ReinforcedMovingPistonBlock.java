package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Collections;
import java.util.List;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.blockentities.ReinforcedPistonBlockEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.MovingPistonBlock;
import net.minecraft.block.PistonBlock;
import net.minecraft.entity.player.PlayerEntity;
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
	public ReinforcedMovingPistonBlock(AbstractBlock.Properties properties) {
		super(properties);
	}

	@Override
	public boolean canHarvestBlock(BlockState state, IBlockReader level, BlockPos pos, PlayerEntity player) {
		return ConfigHandler.SERVER.alwaysDrop.get() || super.canHarvestBlock(state, level, pos, player);
	}

	public static TileEntity createTilePiston(BlockState state, CompoundNBT tag, Direction direction, boolean extending, boolean shouldHeadBeRendered) {
		return new ReinforcedPistonBlockEntity(state, tag, direction, extending, shouldHeadBeRendered);
	}

	@Override
	public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			TileEntity te = level.getBlockEntity(pos);

			if (te instanceof ReinforcedPistonBlockEntity)
				((ReinforcedPistonBlockEntity) te).finalTick();
		}
	}

	@Override
	public void destroy(IWorld level, BlockPos pos, BlockState state) {
		BlockPos oppositePos = pos.relative(state.getValue(FACING).getOpposite());
		BlockState oppositeState = level.getBlockState(oppositePos);

		if (oppositeState.getBlock() instanceof ReinforcedPistonBlock && oppositeState.getValue(PistonBlock.EXTENDED))
			level.removeBlock(oppositePos, false);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		ReinforcedPistonBlockEntity reinforcedPistonTileEntity = getTileEntity(builder.getLevel(), new BlockPos(builder.getParameter(LootParameters.ORIGIN)));
		return reinforcedPistonTileEntity == null ? Collections.emptyList() : reinforcedPistonTileEntity.getPistonState().getDrops(builder);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
		ReinforcedPistonBlockEntity reinforcedPistonTileEntity = getTileEntity(level, pos);
		return reinforcedPistonTileEntity != null ? reinforcedPistonTileEntity.getCollisionShape(level, pos) : VoxelShapes.empty();
	}

	private ReinforcedPistonBlockEntity getTileEntity(IBlockReader level, BlockPos pos) {
		TileEntity te = level.getBlockEntity(pos);
		return te instanceof ReinforcedPistonBlockEntity ? (ReinforcedPistonBlockEntity) te : null;
	}
}
