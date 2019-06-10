package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.tileentity.TileEntityProtecto;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockProtecto extends BlockOwnable {

	public static final BooleanProperty ACTIVATED = BlockStateProperties.ENABLED;
	public static final VoxelShape SHAPE = VoxelShapes.or(Block.makeCuboidShape(0, 0, 5, 16, 16, 11), Block.makeCuboidShape(5, 0, 0, 11, 16, 16));

	public BlockProtecto(Material material) {
		super(SoundType.METAL, Block.Properties.create(material).hardnessAndResistance(-1.0F, 6000000.0F).lightValue(7));
		setDefaultState(stateContainer.getBaseState().with(ACTIVATED, false));
	}

	@Override
	public VoxelShape getShape(IBlockState state, IBlockReader world, BlockPos pos)
	{
		return SHAPE;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean isValidPosition(IBlockState state, IWorldReaderBase world, BlockPos pos){
		return world.getBlockState(pos.down()).isTopSolid();
	}

	@Override
	public IBlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		return getStateForPlacement(ctx.getWorld(), ctx.getPos(), ctx.getFace(), ctx.getHitX(), ctx.getHitY(), ctx.getHitZ(), ctx.getPlayer());
	}

	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, EntityPlayer placer)
	{
		return getDefaultState().with(ACTIVATED, false);
	}

	@Override
	protected void fillStateContainer(Builder<Block, IBlockState> builder)
	{
		builder.add(ACTIVATED);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world) {
		return new TileEntityProtecto().attacks(EntityLivingBase.class, 10, 200);
	}

}
