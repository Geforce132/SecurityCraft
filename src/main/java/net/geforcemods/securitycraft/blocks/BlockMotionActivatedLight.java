package net.geforcemods.securitycraft.blocks;

import static net.minecraftforge.common.util.ForgeDirection.EAST;
import static net.minecraftforge.common.util.ForgeDirection.NORTH;
import static net.minecraftforge.common.util.ForgeDirection.SOUTH;
import static net.minecraftforge.common.util.ForgeDirection.WEST;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.compat.waila.ICustomWailaDisplay;
import net.geforcemods.securitycraft.tileentity.TileEntityMotionLight;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockMotionActivatedLight extends BlockOwnable implements ICustomWailaDisplay {

	public BlockMotionActivatedLight(Material material) {
		super(material);
	}

	@Override
	public boolean renderAsNormalBlock(){
		return false;
	}

	@Override
	public boolean isOpaqueCube(){
		return false;
	}

	@Override
	public boolean isNormalCube(){
		return false;
	}

	@Override
	public int getRenderType(){
		return -1;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess access, int x, int y, int z){
		int meta = access.getBlockMetadata(x, y, z);
		float px = 1.0F / 16.0F;

		if(meta == 3)
			setBlockBounds(px * 6, px * 3, 0F, px * 10, px * 9, px * 3);
		else if(meta == 4)
			setBlockBounds(px * 6, px * 3, .995F - (px * 3), px * 10, px * 9, .995F);
		else if(meta == 2)
			setBlockBounds(.995F - (px * 3), px * 3, px * 6, .995F, px * 9, px * 10); //1F - (px * 3)
		else if(meta == 1) {
			setBlockBounds(0F, px * 3, px * 6, px * 3, px * 9, px * 10);
		}
	}

	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta){
		int k1 = meta & 8;
		byte b0 = -1;

		if(side == 2 && world.isSideSolid(x, y, z + 1, NORTH))
			b0 = 4;

		if(side == 3 && world.isSideSolid(x, y, z - 1, SOUTH))
			b0 = 3;

		if(side == 4 && world.isSideSolid(x + 1, y, z, WEST))
			b0 = 2;

		if(side == 5 && world.isSideSolid(x - 1, y, z, EAST))
			b0 = 1;

		return b0 == 0 ? 0 : b0 + k1;
	}

	public static void toggleLight(World world, int x, int y, int z, double searchRadius, Owner owner, boolean isLit) {
		if(!world.isRemote)
		{
			if(isLit)
			{
				world.setBlock(x, y, z, SCContent.motionActivatedLightOn, world.getBlockMetadata(x, y, z), 3);

				if(((IOwnable) world.getTileEntity(x, y, z)) != null)
					((IOwnable) world.getTileEntity(x, y, z)).setOwner(owner.getUUID(), owner.getName());

				BlockUtils.updateAndNotify(world, x, y, z, SCContent.motionActivatedLightOn, 1, false);
			}
			else
			{
				world.setBlock(x, y, z, SCContent.motionActivatedLightOff, world.getBlockMetadata(x, y, z), 3);

				if(((IOwnable) world.getTileEntity(x, y, z)) != null)
					((IOwnable) world.getTileEntity(x, y, z)).setOwner(owner.getUUID(), owner.getName());

				BlockUtils.updateAndNotify(world, x, y, z, SCContent.motionActivatedLightOff, 1, false);
			}
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighborBlock) {
		if (!canPlaceBlockOnSide(world, x, y, z, getSide(world.getBlockMetadata(x, y, z)))) {
			dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			world.setBlockToAir(x, y, z);
		}
	}

	private int getSide(int blockMetadata) {
		if(blockMetadata == 1)
			return 5;
		else if(blockMetadata == 2)
			return 4;
		else if(blockMetadata == 3)
			return 3;
		else if(blockMetadata == 4)
			return 2;

		return 0;
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side)
	{
		ForgeDirection dir = ForgeDirection.getOrientation(side);
		return (dir == NORTH && world.isSideSolid(x, y, z + 1, NORTH)) ||
				(dir == SOUTH && world.isSideSolid(x, y, z - 1, SOUTH)) ||
				(dir == WEST  && world.isSideSolid(x + 1, y, z, WEST )) ||
				(dir == EAST  && world.isSideSolid(x - 1, y, z, EAST ));
	}

	@Override
	public Item getItemDropped(int meta, Random random, int fortune) {
		return Item.getItemFromBlock(SCContent.motionActivatedLightOff);
	}

	@Override
	public ItemStack getDisplayStack(World world, int x, int y, int z) {
		return new ItemStack(Item.getItemFromBlock(SCContent.motionActivatedLightOff));
	}

	@Override
	public boolean shouldShowSCInfo(World world, int x, int y, int z) {
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityMotionLight().attacks(EntityLivingBase.class, SecurityCraft.config.motionActivatedLightSearchRadius, 1);
	}

}
