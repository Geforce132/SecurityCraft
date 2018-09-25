package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.compat.waila.ICustomWailaDisplay;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFakeWaterBase extends BlockStaticLiquid implements ICustomWailaDisplay {

	public BlockFakeWaterBase(Material material){
		super(material);
		setTickRandomly(true);
		disableStats();
	}

	@Override
	public boolean isPassable(IBlockAccess access, int x, int y, int z){
		return blockMaterial != Material.lava;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block){
		if (world.getBlock(x, y, z) == this)
			setNotStationary(world, x, y, z);
	}

	/**
	 * Changes the block ID to that of an updating fluid.
	 */
	private void setNotStationary(World world, int x, int y, int z){
		int l = world.getBlockMetadata(x, y, z);
		world.setBlock(x, y, z, SCContent.bogusWaterFlowing, l, 2);
		world.scheduleBlockUpdate(x, y, z, SCContent.bogusWaterFlowing, tickRate(world));
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random random){
		if (blockMaterial == Material.lava){
			int randomValue = random.nextInt(3);
			int i1;

			for (i1 = 0; i1 < randomValue; ++i1){
				x += random.nextInt(3) - 1;
				++y;
				z += random.nextInt(3) - 1;
				Block block = world.getBlock(x, y, z);

				if (block.getMaterial() == Material.air){
					if (this.isFlammable(world, x - 1, y, z) || this.isFlammable(world, x + 1, y, z) || this.isFlammable(world, x, y, z - 1) || this.isFlammable(world, x, y, z + 1) || this.isFlammable(world, x, y - 1, z) || this.isFlammable(world, x, y + 1, z)){
						world.setBlock(x, y, z, Blocks.fire);
						return;
					}
				}else if (block.getMaterial().blocksMovement())
					return;
			}

			if (randomValue == 0){
				i1 = x;
				int k1 = z;

				for (int j1 = 0; j1 < 3; ++j1){
					x = i1 + random.nextInt(3) - 1;
					z = k1 + random.nextInt(3) - 1;

					if (world.isAirBlock(x, y + 1, z) && this.isFlammable(world, x, y, z))
						world.setBlock(x, y + 1, z, Blocks.fire);
				}
			}
		}
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity){
		if(!world.isRemote)
			if(entity instanceof EntityPlayer && !((EntityPlayer) entity).capabilities.isCreativeMode)
				((EntityPlayer) entity).attackEntityFrom(CustomDamageSources.fakeWater, 5F);
			else
				entity.attackEntityFrom(CustomDamageSources.fakeWater, 5F);
	}

	/**
	 * Checks to see if the block is flammable.
	 */
	private boolean isFlammable(World world, int x, int y, int z){
		return world.getBlock(x, y, z).getMaterial().getCanBurn();
	}

	/**
	 * Gets an item for the block being called on. Args: world, x, y, z
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World world, int x, int y, int z){
		return null;
	}

	@Override
	public ItemStack getDisplayStack(World world, int x, int y, int z)
	{
		return new ItemStack(Blocks.flowing_water);
	}

	@Override
	public boolean shouldShowSCInfo(World world, int x, int y, int z)
	{
		return false;
	}

}
