package net.geforcemods.securitycraft.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockIronTrapDoor extends Block {

	/** Set this to allow trapdoors to remain free-floating */
	public static boolean disableValidation = false;

	public BlockIronTrapDoor(Material material){
		super(material);
		float f = 0.5F;
		float f1 = 1.0F;
		setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f1, 0.5F + f);
	}

	@Override
	public boolean isOpaqueCube(){
		return false;
	}

	@Override
	public boolean renderAsNormalBlock(){
		return false;
	}

	@Override
	public int getRenderType(){
		return 0;
	}

	@Override
	public boolean isPassable(IBlockAccess access, int x, int y, int z){
		return !isOpen(access.getBlockMetadata(x, y, z));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z){
		setBlockBoundsBasedOnState(world, x, y, z);
		return super.getSelectedBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z){
		setBlockBoundsBasedOnState(world, x, y, z);
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess access, int x, int y, int z){
		setBoundBasedOnMeta(access.getBlockMetadata(x, y, z));
	}

	@Override
	public void setBlockBoundsForItemRender(){
		float f = 0.1875F;
		setBlockBounds(0.0F, 0.5F - f / 2.0F, 0.0F, 1.0F, 0.5F + f / 2.0F, 1.0F);
	}

	public void setBoundBasedOnMeta(int meta){
		float f = 0.1875F;

		if ((meta & 8) != 0)
			setBlockBounds(0.0F, 1.0F - f, 0.0F, 1.0F, 1.0F, 1.0F);
		else
			setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, f, 1.0F);

		if (isOpen(meta)){
			if ((meta & 3) == 0)
				setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);

			if ((meta & 3) == 1)
				setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);

			if ((meta & 3) == 2)
				setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);

			if ((meta & 3) == 3)
				setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
		}
	}

	public void changeOpenState(World world, int x, int y, int z, boolean open){
		int meta = world.getBlockMetadata(x, y, z);
		boolean isOpen = (meta & 4) > 0;

		if (isOpen != open){
			world.setBlockMetadataWithNotify(x, y, z, meta ^ 4, 2);
			world.playAuxSFXAtEntity((EntityPlayer)null, 1003, x, y, z, 0);
		}
	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
	 * their own) Args: x, y, z, neighbor Block
	 */
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block){
		if (!world.isRemote){
			int meta = world.getBlockMetadata(x, y, z);
			int i1 = x;
			int j1 = z;

			if ((meta & 3) == 0)
				j1 = z + 1;

			if ((meta & 3) == 1)
				--j1;

			if ((meta & 3) == 2)
				i1 = x + 1;

			if ((meta & 3) == 3)
				--i1;

			if (!(canPlaceOn(world.getBlock(i1, y, j1)) || world.isSideSolid(i1, y, j1, ForgeDirection.getOrientation((meta & 3) + 2)))){
				world.setBlockToAir(x, y, z);
				this.dropBlockAsItem(world, x, y, z, meta, 0);
			}

			boolean hasActiveSCBlock = BlockUtils.hasActiveSCBlockNextTo(world, x, y, z);

			if ((hasActiveSCBlock || block.canProvidePower()) && (meta & 4) > 0 != hasActiveSCBlock)
				changeOpenState(world, x, y, z, hasActiveSCBlock);
		}
	}

	/**
	 * Ray traces through the blocks collision from start vector to end vector returning a ray trace hit. Args: world,
	 * x, y, z, startVec, endVec
	 */
	@Override
	public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 start, Vec3 end){
		setBlockBoundsBasedOnState(world, x, y, z);
		return super.collisionRayTrace(world, x, y, z, start, end);
	}

	/**
	 * Called when a block is placed using its ItemBlock. Args: World, X, Y, Z, side, hitX, hitY, hitZ, block metadata
	 */
	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta){
		int j1 = 0;

		if (side == 2)
			j1 = 0;

		if (side == 3)
			j1 = 1;

		if (side == 4)
			j1 = 2;

		if (side == 5)
			j1 = 3;

		if (side != 1 && side != 0 && hitY > 0.5F)
			j1 |= 8;

		return j1;
	}

	/**
	 * checks to see if you can place this block can be placed on that side of a block: BlockLever overrides
	 */
	@Override
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side){
		if (disableValidation) return true;

		if (side == 0)
			return false;
		else if (side == 1)
			return false;
		else{
			if (side == 2)
				++z;

			if (side == 3)
				--z;

			if (side == 4)
				++x;

			if (side == 5)
				--x;

			return canPlaceOn(world.getBlock(x, y, z)) || world.isSideSolid(x, y, z, ForgeDirection.UP);
		}
	}

	public static boolean isOpen(int meta){
		return (meta & 4) != 0;
	}

	private static boolean canPlaceOn(Block block){
		if (disableValidation) return true;
		return block.getMaterial().isOpaque() && block.renderAsNormalBlock() || block == Blocks.glowstone || block instanceof BlockSlab || block instanceof BlockStairs;
	}

}