package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.tileentity.ProtectoTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class ProtectoBlock extends OwnableBlock {

	public static final BooleanProperty ACTIVATED = BlockStateProperties.ENABLED;
	public static final VoxelShape SHAPE = VoxelShapes.or(Block.makeCuboidShape(0, 0, 5, 16, 16, 11), Block.makeCuboidShape(5, 0, 0, 11, 16, 16));

	public ProtectoBlock(Material material) {
		super(SoundType.METAL, Block.Properties.create(material).hardnessAndResistance(-1.0F, 6000000.0F).lightValue(7));
		setDefaultState(stateContainer.getBaseState().with(ACTIVATED, false));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx)
	{
		return SHAPE;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos){
		return Block.hasSolidSide(world.getBlockState(pos.down()), world, pos.down(), Direction.UP);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		return getStateForPlacement(ctx.getWorld(), ctx.getPos(), ctx.getFace(), ctx.getHitVec().x, ctx.getHitVec().y, ctx.getHitVec().z, ctx.getPlayer());
	}

	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, PlayerEntity placer)
	{
		return getDefaultState().with(ACTIVATED, false);
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder)
	{
		builder.add(ACTIVATED);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world) {
		return new ProtectoTileEntity().attacks(LivingEntity.class, 10, 200);
	}

}
