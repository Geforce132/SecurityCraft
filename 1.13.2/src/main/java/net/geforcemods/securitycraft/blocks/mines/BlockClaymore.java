package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.ConfigHandler.CommonConfig;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.TileEntityClaymore;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class BlockClaymore extends BlockContainer implements IExplosive {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty DEACTIVATED = BooleanProperty.create("deactivated");
	private static final VoxelShape NORTH_OFF = VoxelShapes.or(Block.makeCuboidShape(4, 0, 5, 12, 4, 7), VoxelShapes.or(Block.makeCuboidShape(4, 4, 5, 12, 5, 6), VoxelShapes.or(Block.makeCuboidShape(5, 4, 4, 6, 5, 5), VoxelShapes.or(Block.makeCuboidShape(10, 4, 4, 11, 5, 5), VoxelShapes.or(Block.makeCuboidShape(4, 4, 3, 5, 5, 4), Block.makeCuboidShape(11, 4, 3, 12, 5, 4))))));
	private static final VoxelShape NORTH_ON = VoxelShapes.or(NORTH_OFF, VoxelShapes.or(Block.makeCuboidShape(3, 4, 2, 4, 5, 3), Block.makeCuboidShape(12, 4, 2, 13, 5, 3)));
	private static final VoxelShape EAST_OFF = VoxelShapes.or(Block.makeCuboidShape(9, 0, 4, 11, 4, 12), VoxelShapes.or(Block.makeCuboidShape(10, 4, 4, 11, 5, 12), VoxelShapes.or(Block.makeCuboidShape(11, 4, 5, 12, 5, 6), VoxelShapes.or(Block.makeCuboidShape(11, 4, 10, 12, 5, 11), VoxelShapes.or(Block.makeCuboidShape(12, 4, 4, 13, 5, 5), Block.makeCuboidShape(12, 4, 11, 13, 5, 12))))));
	private static final VoxelShape EAST_ON = VoxelShapes.or(EAST_OFF, VoxelShapes.or(Block.makeCuboidShape(13, 4, 3, 14, 5, 4), Block.makeCuboidShape(13, 4, 12, 14, 5, 13)));
	private static final VoxelShape SOUTH_OFF = VoxelShapes.or(Block.makeCuboidShape(4, 0, 9, 12, 4, 11), VoxelShapes.or(Block.makeCuboidShape(4, 4, 10, 12, 5, 11), VoxelShapes.or(Block.makeCuboidShape(5, 4, 11, 6, 5, 12), VoxelShapes.or(Block.makeCuboidShape(10, 4, 11, 11, 5, 12), VoxelShapes.or(Block.makeCuboidShape(4, 4, 12, 5, 5, 13), Block.makeCuboidShape(11, 4, 12, 12, 5, 13))))));
	private static final VoxelShape SOUTH_ON = VoxelShapes.or(SOUTH_OFF, VoxelShapes.or(Block.makeCuboidShape(3, 4, 13, 4, 5, 14), Block.makeCuboidShape(12, 4, 13, 13, 5, 14)));
	private static final VoxelShape WEST_OFF = VoxelShapes.or(Block.makeCuboidShape(7, 0, 4, 5, 4, 12), VoxelShapes.or(Block.makeCuboidShape(6, 4, 4, 5, 5, 12), VoxelShapes.or(Block.makeCuboidShape(5, 4, 5, 4, 5, 6), VoxelShapes.or(Block.makeCuboidShape(5, 4, 10, 4, 5, 11), VoxelShapes.or(Block.makeCuboidShape(4, 4, 4, 3, 5, 5), Block.makeCuboidShape(4, 4, 11, 3, 5, 12))))));
	private static final VoxelShape WEST_ON = VoxelShapes.or(WEST_OFF, VoxelShapes.or(Block.makeCuboidShape(3, 4, 3, 2, 5, 4), Block.makeCuboidShape(3, 4, 12, 2, 5, 13)));

	public BlockClaymore(Material material) {
		super(Block.Properties.create(material).hardnessAndResistance(1F, 6000000.0F));
		setDefaultState(stateContainer.getBaseState().with(FACING, EnumFacing.NORTH).with(DEACTIVATED, false));
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		if(placer instanceof EntityPlayer)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (EntityPlayer)placer));
	}

	@Override
	public float getBlockHardness(IBlockState blockState, IBlockReader world, BlockPos pos)
	{
		return !CommonConfig.CONFIG.ableToBreakMines.get() ? -1F : super.getBlockHardness(blockState, world, pos);
	}

	/**
	 * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
	 */
	@Override
	public boolean isNormalCube(IBlockState state)
	{
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public VoxelShape getCollisionShape(IBlockState blockState, IBlockReader access, BlockPos pos)
	{
		return VoxelShapes.empty();
	}

	@Override
	public boolean isValidPosition(IBlockState state, IWorldReaderBase world, BlockPos pos)
	{
		return world.getBlockState(pos.down()).isTopSolid();
	}

	@Override
	public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(!world.isRemote)
			if(!player.inventory.getCurrentItem().isEmpty() && player.inventory.getCurrentItem().getItem() == SCContent.wireCutters){
				world.setBlockState(pos, SCContent.claymore.getDefaultState().with(FACING, state.get(FACING)).with(DEACTIVATED, true));
				return true;
			}else if(!player.inventory.getCurrentItem().isEmpty() && player.inventory.getCurrentItem().getItem() == Items.FLINT_AND_STEEL){
				world.setBlockState(pos, SCContent.claymore.getDefaultState().with(FACING, state.get(FACING)).with(DEACTIVATED, false));
				return true;
			}

		return false;
	}

	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest, IFluidState fluid){
		if (!player.isCreative() && !world.isRemote && !world.getBlockState(pos).get(BlockClaymore.DEACTIVATED))
		{
			BlockUtils.destroyBlock(world, pos, false);
			world.createExplosion((Entity) null, (double) pos.getX() + 0.5F, (double) pos.getY() + 0.5F, (double) pos.getZ() + 0.5F, 3.5F, true);
		}

		return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
	}

	@Override
	public void onExplosionDestroy(World world, BlockPos pos, Explosion explosion)
	{
		if (!world.isRemote && BlockUtils.hasBlockProperty(world, pos, BlockClaymore.DEACTIVATED) && !world.getBlockState(pos).get(BlockClaymore.DEACTIVATED))
		{
			if(pos.equals(new BlockPos(explosion.getPosition())))
				return;

			BlockUtils.destroyBlock(world, pos, false);
			world.createExplosion((Entity) null, (double) pos.getX() + 0.5F, (double) pos.getY() + 0.5F, (double) pos.getZ() + 0.5F, 3.5F, true);
		}
	}

	@Override
	public IBlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		return getStateForPlacement(ctx.getWorld(), ctx.getPos(), ctx.getFace(), ctx.getHitX(), ctx.getHitY(), ctx.getHitZ(), ctx.getPlayer());
	}

	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, EntityPlayer placer)
	{
		return getDefaultState().with(FACING, placer.getHorizontalFacing()).with(DEACTIVATED, false);
	}

	@Override
	public void activateMine(World world, BlockPos pos) {
		if(!world.isRemote)
			BlockUtils.setBlockProperty(world, pos, DEACTIVATED, false);
	}

	@Override
	public void defuseMine(World world, BlockPos pos) {
		if(!world.isRemote)
			BlockUtils.setBlockProperty(world, pos, DEACTIVATED, true);
	}

	@Override
	public void explode(World world, BlockPos pos) {
		if(!world.isRemote){
			BlockUtils.destroyBlock(world, pos, false);
			world.createExplosion((Entity) null, pos.getX(), pos.getY(), pos.getZ(), 3.5F, true);
		}
	}

	@Override
	public VoxelShape getShape(IBlockState state, IBlockReader source, BlockPos pos)
	{
		switch(state.get(FACING))
		{
			case NORTH:
				if(state.get(DEACTIVATED))
					return NORTH_OFF;
				else
					return NORTH_ON;
			case EAST:
				if(state.get(DEACTIVATED))
					return EAST_OFF;
				else
					return EAST_ON;
			case SOUTH:
				if(state.get(DEACTIVATED))
					return SOUTH_OFF;
				else
					return SOUTH_ON;
			case WEST:
				if(state.get(DEACTIVATED))
					return WEST_OFF;
				else
					return WEST_ON;
			default: return VoxelShapes.fullCube();
		}
	}

	@Override
	protected void fillStateContainer(Builder<Block, IBlockState> builder)
	{
		builder.add(FACING);
		builder.add(DEACTIVATED);
	}

	@Override
	public boolean isActive(World world, BlockPos pos) {
		return !world.getBlockState(pos).get(DEACTIVATED);
	}

	@Override
	public boolean isDefusable() {
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world) {
		return new TileEntityClaymore();
	}
}
