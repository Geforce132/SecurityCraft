package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.ConfigHandler.CommonConfig;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.tileentity.TileEntityMotionLight;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BlockMotionActivatedLight extends BlockOwnable {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	private static final VoxelShape SHAPE_NORTH = VoxelShapes.or(Block.makeCuboidShape(6, 3, 13, 10, 4, 14), VoxelShapes.or(Block.makeCuboidShape(6, 6, 13, 10, 9, 14), VoxelShapes.combine(Block.makeCuboidShape(7, 3, 14, 9, 8, 16), Block.makeCuboidShape(7, 4, 15, 9, 7, 14), IBooleanFunction.ONLY_FIRST)));
	private static final VoxelShape SHAPE_EAST = VoxelShapes.or(Block.makeCuboidShape(3, 3, 6, 2, 4, 10), VoxelShapes.or(Block.makeCuboidShape(3, 6, 6, 2, 9, 10), VoxelShapes.combine(Block.makeCuboidShape(2, 3, 7, 0, 8, 9), Block.makeCuboidShape(1, 4, 7, 2, 7, 9), IBooleanFunction.ONLY_FIRST)));
	private static final VoxelShape SHAPE_SOUTH = VoxelShapes.or(Block.makeCuboidShape(6, 3, 2, 10, 4, 3), VoxelShapes.or(Block.makeCuboidShape(6, 6, 2, 10, 9, 3), VoxelShapes.combine(Block.makeCuboidShape(7, 3, 0, 9, 8, 2), Block.makeCuboidShape(7, 4, 1, 9, 7, 2), IBooleanFunction.ONLY_FIRST)));
	private static final VoxelShape SHAPE_WEST = VoxelShapes.or(Block.makeCuboidShape(13, 3, 6, 14, 4, 10), VoxelShapes.or(Block.makeCuboidShape(13, 6, 6, 14, 9, 10), VoxelShapes.combine(Block.makeCuboidShape(14, 3, 7, 16, 8, 9), Block.makeCuboidShape(15, 4, 7, 14, 7, 9), IBooleanFunction.ONLY_FIRST)));

	public BlockMotionActivatedLight(Material material) {
		super(SoundType.GLASS, Block.Properties.create(material).hardnessAndResistance(-1.0F, 6000000.0F));
		setDefaultState(stateContainer.getBaseState().with(FACING, Direction.NORTH).with(LIT, false));
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx){
		switch(state.get(FACING))
		{
			case NORTH: return SHAPE_NORTH;
			case EAST: return SHAPE_EAST;
			case SOUTH: return SHAPE_SOUTH;
			case WEST: return SHAPE_WEST;
			default: return VoxelShapes.fullCube();
		}
	}

	@Override
	public int getLightValue(BlockState state) {
		return state.get(LIT) ? 15 : 0;
	}

	public static void toggleLight(World world, BlockPos pos, double searchRadius, Owner owner, boolean isLit) {
		if(!world.isRemote)
		{
			if(isLit)
			{
				BlockUtils.setBlockProperty(world, pos, LIT, true);

				if(((IOwnable) world.getTileEntity(pos)) != null)
					((IOwnable) world.getTileEntity(pos)).setOwner(owner.getUUID(), owner.getName());

				BlockUtils.updateAndNotify(world, pos, SCContent.motionActivatedLight, 1, false);
			}
			else
			{
				BlockUtils.setBlockProperty(world, pos, LIT, false);

				if(((IOwnable) world.getTileEntity(pos)) != null)
					((IOwnable) world.getTileEntity(pos)).setOwner(owner.getUUID(), owner.getName());

				BlockUtils.updateAndNotify(world, pos, SCContent.motionActivatedLight, 1, false);
			}
		}
	}

	@Override
	public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos){
		Direction side = state.get(FACING);

		return side != Direction.UP && side != Direction.DOWN && BlockUtils.isSideSolid(world, pos.offset(side.getOpposite()), side);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		return getStateForPlacement(ctx.getWorld(), ctx.getPos(), ctx.getFace(), ctx.getHitVec().x, ctx.getHitVec().y, ctx.getHitVec().z, ctx.getPlayer());
	}

	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, PlayerEntity placer)
	{
		return facing != Direction.UP && facing != Direction.DOWN && BlockUtils.isSideSolid(world, pos.offset(facing.getOpposite()), facing) ? getDefaultState().with(FACING, facing) : null;
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean flag) {
		if (!isValidPosition(state, world, pos))
			world.destroyBlock(pos, true);
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder)
	{
		builder.add(FACING);
		builder.add(LIT);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world) {
		return new TileEntityMotionLight().attacks(LivingEntity.class, CommonConfig.CONFIG.motionActivatedLightSearchRadius.get(), 1);
	}

}
