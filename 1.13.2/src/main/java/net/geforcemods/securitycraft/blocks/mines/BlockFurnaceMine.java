package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.ConfigHandler.ServerConfig;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockFurnaceMine extends BlockExplosive implements IOverlayDisplay {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

	public BlockFurnaceMine(Material material) {
		super(SoundType.STONE, material);
		setDefaultState(stateContainer.getBaseState().with(FACING, EnumFacing.NORTH));
	}

	/**
	 * Called upon the block being destroyed by an explosion
	 */
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}


	@Override
	public void onExplosionDestroy(World world, BlockPos pos, Explosion explosion) {
		if (!world.isRemote)
		{
			if(pos.equals(new BlockPos(explosion.getPosition())))
				return;

			explode(world, pos);
		}
	}

	@Override
	public void onPlayerDestroy(IWorld world, BlockPos pos, IBlockState state){
		if (!world.isRemote() && world instanceof World)
			explode((World)world, pos);
	}

	@Override
	public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(world.isRemote)
			return true;
		else if(player.inventory.getCurrentItem().getItem() != SCContent.remoteAccessMine){
			explode(world, pos);
			return true;
		}
		else
			return false;
	}

	@Override
	public IBlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		return getStateForPlacement(ctx.getWorld(), ctx.getPos(), ctx.getFace(), ctx.getHitX(), ctx.getHitY(), ctx.getHitZ(), ctx.getPlayer());
	}

	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, EntityPlayer placer)
	{
		return getDefaultState().with(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public void activateMine(World world, BlockPos pos) {}

	@Override
	public void defuseMine(World world, BlockPos pos) {}

	@Override
	public void explode(World world, BlockPos pos) {
		world.destroyBlock(pos, false);

		if(ServerConfig.CONFIG.smallerMineExplosion.get())
			world.createExplosion((Entity)null, pos.getX(), pos.getY(), pos.getZ(), 2.5F, true);
		else
			world.createExplosion((Entity)null, pos.getX(), pos.getY(), pos.getZ(), 5.0F, true);

	}

	/**
	 * Return whether this block can drop from an explosion.
	 */
	@Override
	public boolean canDropFromExplosion(Explosion explosion) {
		return false;
	}

	@Override
	protected void fillStateContainer(Builder<Block, IBlockState> builder)
	{
		builder.add(FACING);
	}

	@Override
	public boolean isActive(World world, BlockPos pos) {
		return true;
	}

	@Override
	public boolean isDefusable() {
		return false;
	}

	@Override
	public ItemStack getDisplayStack(World world, IBlockState state, BlockPos pos) {
		return new ItemStack(Blocks.FURNACE);
	}

	@Override
	public boolean shouldShowSCInfo(World world, IBlockState state, BlockPos pos) {
		return false;
	}

}
