package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.ConfigHandler.CommonConfig;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockFurnaceMine extends BlockExplosive implements IOverlayDisplay {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

	public BlockFurnaceMine(Material material, float baseHardness) {
		super(SoundType.STONE, material, baseHardness);
		setDefaultState(stateContainer.getBaseState().with(FACING, Direction.NORTH));
	}

	/**
	 * Called upon the block being destroyed by an explosion
	 */
	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
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
	public void onPlayerDestroy(IWorld world, BlockPos pos, BlockState state){
		if (!world.isRemote() && world instanceof World)
			explode((World)world, pos);
	}

	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
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
	public BlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		return getStateForPlacement(ctx.getWorld(), ctx.getPos(), ctx.getFace(), ctx.func_221532_j().x, ctx.func_221532_j().y, ctx.func_221532_j().z, ctx.getPlayer());
	}

	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, PlayerEntity placer)
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

		if(CommonConfig.CONFIG.smallerMineExplosion.get())
			world.createExplosion((Entity)null, pos.getX(), pos.getY(), pos.getZ(), 2.5F, true, Mode.BREAK);
		else
			world.createExplosion((Entity)null, pos.getX(), pos.getY(), pos.getZ(), 5.0F, true, Mode.BREAK);

	}

	/**
	 * Return whether this block can drop from an explosion.
	 */
	@Override
	public boolean canDropFromExplosion(Explosion explosion) {
		return false;
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder)
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
	public ItemStack getDisplayStack(World world, BlockState state, BlockPos pos) {
		return new ItemStack(Blocks.FURNACE);
	}

	@Override
	public boolean shouldShowSCInfo(World world, BlockState state, BlockPos pos) {
		return false;
	}

}
