package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.tileentity.TileEntityLaserBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockLaserBlock extends BlockContainer {

	public BlockLaserBlock(Material material) {
		super(material);
	}

	@Override
	public boolean isNormalCube(IBlockAccess world, int x, int y, int z){
		return true;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
	{
		return true;
	}

	/**
	 * Called whenever the block is added into the world. Args: world, x, y, z
	 */
	@Override
	public void onBlockAdded(World world, int x, int y, int z){
		super.onBlockAdded(world, x, y, z);
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stacl){
		if(!world.isRemote)
			setLaser(world, x, y, z);
	}

	public void setLaser(World world, int x, int y, int z) {
		for(int i = 1; i <= SecurityCraft.config.laserBlockRange; i++){
			Block id = world.getBlock(x + i, y, z);
			if(id != Blocks.air && id != SCContent.laserBlock)
				break;
			if(id == SCContent.laserBlock){
				CustomizableSCTE.link((CustomizableSCTE) world.getTileEntity(x, y, z), (CustomizableSCTE) world.getTileEntity(x + i, y, z));

				for(int j = 1; j < i; j++)
					if(world.getBlock(x + j, y, z) == Blocks.air)
						world.setBlock(x + j, y, z, SCContent.laserField, 3, 3);
			}
			else
				continue;
		}

		for(int i = 1; i <= SecurityCraft.config.laserBlockRange; i++){
			Block id = world.getBlock(x - i, y, z);
			if(id != Blocks.air && id != SCContent.laserBlock)
				break;
			if(id == SCContent.laserBlock){
				CustomizableSCTE.link((CustomizableSCTE) world.getTileEntity(x, y, z), (CustomizableSCTE) world.getTileEntity(x - i, y, z));
				for(int j = 1; j < i; j++)
					if(world.getBlock(x - j, y, z) == Blocks.air)
						world.setBlock(x - j, y, z, SCContent.laserField, 3, 3);
			}
			else
				continue;
		}

		for(int i = 1; i <= SecurityCraft.config.laserBlockRange; i++){
			Block id = world.getBlock(x, y, z + i);
			if(id != Blocks.air && id != SCContent.laserBlock)
				break;
			if(id == SCContent.laserBlock){
				CustomizableSCTE.link((CustomizableSCTE) world.getTileEntity(x, y, z), (CustomizableSCTE) world.getTileEntity(x, y, z + i));
				for(int j = 1; j < i; j++)
					if(world.getBlock(x, y, z + j) == Blocks.air)
						world.setBlock(x, y, z + j, SCContent.laserField, 2, 3);
			}
			else
				continue;
		}

		for(int i = 1; i <= SecurityCraft.config.laserBlockRange; i++){
			Block id = world.getBlock(x , y, z - i);
			if(id != Blocks.air && id != SCContent.laserBlock)
				break;
			if(id == SCContent.laserBlock){
				CustomizableSCTE.link((CustomizableSCTE) world.getTileEntity(x, y, z), (CustomizableSCTE) world.getTileEntity(x, y, z - i));
				for(int j = 1; j < i; j++)
					if(world.getBlock(x, y, z - j) == Blocks.air)
						world.setBlock(x, y, z - j, SCContent.laserField, 2, 3);
			}
			else
				continue;
		}

		for(int i = 1; i <= SecurityCraft.config.laserBlockRange; i++){
			Block id = world.getBlock(x, y + i, z);
			if(id != Blocks.air && id != SCContent.laserBlock)
				break;
			if(id == SCContent.laserBlock){
				CustomizableSCTE.link((CustomizableSCTE) world.getTileEntity(x, y, z), (CustomizableSCTE) world.getTileEntity(x, y + i, z));
				for(int j = 1; j < i; j++)
					if(world.getBlock(x, y + j, z) == Blocks.air)
						world.setBlock(x, y + j, z, SCContent.laserField, 1, 3);
			}
			else
				continue;
		}

		for(int i = 1; i <= SecurityCraft.config.laserBlockRange; i++){
			Block id = world.getBlock(x, y - i, z);
			if(id != Blocks.air && id != SCContent.laserBlock)
				break;
			if(id == SCContent.laserBlock){
				CustomizableSCTE.link((CustomizableSCTE) world.getTileEntity(x, y, z), (CustomizableSCTE) world.getTileEntity(x, y - i, z));
				for(int j = 1; j < i; j++)
					if(world.getBlock(x, y - j, z) == Blocks.air)
						world.setBlock(x, y - j, z, SCContent.laserField, 1, 3);
			}
			else
				continue;
		}
	}

	/**
	 * Called right before the block is destroyed by a player.  Args: world, x, y, z, metaData
	 */
	@Override
	public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int meta) {
		if(!world.isRemote)
			destroyAdjacentLasers(world, x, y, z);
	}

	public static void destroyAdjacentLasers(World world, int x, int y, int z){
		for(int i = 1; i <= SecurityCraft.config.laserBlockRange; i++){
			Block id = world.getBlock(x + i, y, z);
			if(id == SCContent.laserBlock){
				for(int j = 1; j < i; j++)
					if(world.getBlock(x + j, y, z) == SCContent.laserField && world.getBlockMetadata(x + j, y, z) == 3)
						world.breakBlock(x + j, y, z, false);
			}
			else
				continue;
		}

		for(int i = 1; i <= SecurityCraft.config.laserBlockRange; i++){
			Block id = world.getBlock(x - i, y, z);
			if(id == SCContent.laserBlock){
				for(int j = 1; j < i; j++)
					if(world.getBlock(x - j, y, z) == SCContent.laserField && world.getBlockMetadata(x - j, y, z) == 3)
						world.breakBlock(x - j, y, z, false);
			}
			else
				continue;
		}

		for(int i = 1; i <= SecurityCraft.config.laserBlockRange; i++){
			Block id = world.getBlock(x, y, z + i);
			if(id == SCContent.laserBlock){
				for(int j = 1; j < i; j++)
					if(world.getBlock(x, y, z + j) == SCContent.laserField && world.getBlockMetadata(x, y, z + j) == 2)
						world.breakBlock(x, y, z + j, false);
			}
			else
				continue;
		}

		for(int i = 1; i <= SecurityCraft.config.laserBlockRange; i++){
			Block id = world.getBlock(x, y, z - i);
			if(id == SCContent.laserBlock){
				for(int j = 1; j < i; j++)
					if(world.getBlock(x, y, z - j) == SCContent.laserField && world.getBlockMetadata(x, y, z - j) == 2)
						world.breakBlock(x, y, z - j, false);
			}
			else
				continue;
		}

		for(int i = 1; i <= SecurityCraft.config.laserBlockRange; i++){
			Block id = world.getBlock(x, y + i, z);
			if(id == SCContent.laserBlock){
				for(int j = 1; j < i; j++)
					if(world.getBlock(x, y + j, z) == SCContent.laserField && world.getBlockMetadata(x, y + j, z) == 1)
						world.breakBlock(x, y + j, z, false);
			}
			else
				continue;
		}

		for(int i = 1; i <= SecurityCraft.config.laserBlockRange; i++){
			Block id = world.getBlock(x, y - i, z);
			if(id == SCContent.laserBlock){
				for(int j = 1; j < i; j++)
					if(world.getBlock(x, y - j, z) == SCContent.laserField && world.getBlockMetadata(x, y - j, z) == 1)
						world.breakBlock(x, y - j, z, false);
			}
			else
				continue;
		}
	}

	@Override
	public boolean canProvidePower(){
		return true;
	}

	/**
	 * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
	 * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
	 * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int isProvidingWeakPower(IBlockAccess access, int x, int y, int z, int side){
		if(access.getBlockMetadata(x, y, z) == 2)
			return 15;
		else
			return 0;
	}

	/**
	 * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
	 * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int isProvidingStrongPower(IBlockAccess access, int x, int y, int z, int side){
		if(access.getBlockMetadata(x, y, z) == 2)
			return 15;
		else
			return 0;
	}

	/**
	 * Ticks the block if it's been scheduled
	 */
	@Override
	public void updateTick(World world, int x, int y, int z, Random random){
		if(!world.isRemote && world.getBlockMetadata(x, y, z) == 2)
			world.setBlockMetadataWithNotify(x, y, z, 1, 3);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random random){
		if(world.getBlockMetadata(x, y, z) == 2){
			double d0 = x + 0.5F + (random.nextFloat() - 0.5F) * 0.2D;
			double d1 = y + 0.7F + (random.nextFloat() - 0.5F) * 0.2D;
			double d2 = z + 0.5F + (random.nextFloat() - 0.5F) * 0.2D;
			double d3 = 0.2199999988079071D;
			double d4 = 0.27000001072883606D;

			world.spawnParticle("reddust", d0 - d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
			world.spawnParticle("reddust", d0 + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
			world.spawnParticle("reddust", d0, d1 + d3, d2 - d4, 0.0D, 0.0D, 0.0D);
			world.spawnParticle("reddust", d0, d1 + d3, d2 + d4, 0.0D, 0.0D, 0.0D);
			world.spawnParticle("reddust", d0, d1, d2, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityLaserBlock().linkable();
	}

}
