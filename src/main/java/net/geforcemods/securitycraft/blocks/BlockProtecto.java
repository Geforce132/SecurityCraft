package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.tileentity.TileEntityProtecto;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockProtecto extends BlockOwnable {

	public BlockProtecto(Material material) {
		super(material);
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
		return -1;
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z){
		return world.isSideSolid(x, y - 1, z, ForgeDirection.UP);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityProtecto().attacks(EntityLivingBase.class, 10, 200);
	}

}
