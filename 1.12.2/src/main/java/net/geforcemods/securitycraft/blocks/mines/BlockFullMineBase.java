package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.IIntersectable;
import net.geforcemods.securitycraft.compat.waila.ICustomWailaDisplay;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFullMineBase extends BlockExplosive implements IIntersectable, ICustomWailaDisplay {

	private final Block blockDisguisedAs;

	public BlockFullMineBase(Material material, Block disguisedBlock) {
		super(material);
		blockDisguisedAs = disguisedBlock;

		if(material == Material.SAND)
			setSoundType(SoundType.SAND);
		else if(material == Material.GROUND)
			setSoundType(SoundType.GROUND);
		else
			setSoundType(SoundType.STONE);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess access, BlockPos pos){
		return null;
	}

	@Override
	public void onEntityIntersected(World world, BlockPos pos, Entity entity){
		if(entity instanceof EntityItem)
			return;
		else if(entity instanceof EntityLivingBase && !PlayerUtils.isPlayerMountedOnCamera((EntityLivingBase)entity))
			explode(world, pos);
	}

	/**
	 * Called upon the block being destroyed by an explosion
	 */
	@Override
	public void onExplosionDestroy(World world, BlockPos pos, Explosion explosion){
		if (!world.isRemote)
		{
			if(pos.equals(new BlockPos(explosion.getPosition())))
				return;

			explode(world, pos);
		}
	}

	@Override
	public void onPlayerDestroy(World world, BlockPos pos, IBlockState state){
		if (!world.isRemote)
			explode(world, pos);
	}

	@Override
	public void activateMine(World world, BlockPos pos) {}

	@Override
	public void defuseMine(World world, BlockPos pos) {}

	@Override
	public void explode(World world, BlockPos pos) {
		world.destroyBlock(pos, false);

		if(ConfigHandler.smallerMineExplosion)
			world.createExplosion((Entity)null, pos.getX(), pos.getY() + 0.5D, pos.getZ(), 2.5F, true);
		else
			world.createExplosion((Entity)null, pos.getX(), pos.getY() + 0.5D, pos.getZ(), 5.0F, true);
	}

	/**
	 * Return whether this block can drop from an explosion.
	 */
	@Override
	public boolean canDropFromExplosion(Explosion explosion){
		return false;
	}

	@Override
	public boolean isActive(World world, BlockPos pos) {
		return true;
	}

	@Override
	public boolean explodesWhenInteractedWith() {
		return false;
	}

	@Override
	public boolean isDefusable() {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityOwnable().intersectsEntities();
	}

	@Override
	public ItemStack getDisplayStack(World world, IBlockState state, BlockPos pos) {
		return new ItemStack(blockDisguisedAs);
	}

	@Override
	public boolean shouldShowSCInfo(World world, IBlockState state, BlockPos pos) {
		return false;
	}

}