package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SCContent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFakeLava extends BlockDynamicLiquid{

	private boolean[] field_149814_b = new boolean[4];
	private int[] field_149816_M = new int[4];

	/**
	 * Number of horizontally adjacent liquid source blocks. Diagonal doesn't count. Only source blocks of the same
	 * liquid as the block using the field are counted.
	 */
	int numAdjacentSources;

	/**
	 * Indicates whether the flow direction is optimal. Each array index corresponds to one of the four cardinal
	 * directions.
	 */
	boolean[] isOptimalFlowDirection = new boolean[4];

	/**
	 * The estimated cost to flow in a given direction from the current point. Each array index corresponds to one of
	 * the four cardinal directions.
	 */
	int[] flowCost = new int[4];

	public BlockFakeLava(Material par1Material){
		super(par1Material);
	}

	/**
	 * Updates the flow for the BlockFlowing object.
	 */
	private void updateFlow(World par1World, int par2, int par3, int par4){
		int l = par1World.getBlockMetadata(par2, par3, par4);
		par1World.setBlock(par2, par3, par4, SCContent.bogusLava, l, 2);
	}

	@Override
	public boolean isPassable(IBlockAccess par1IBlockAccess, int par2, int par3, int par4){
		return blockMaterial != Material.lava;
	}

	@Override
	public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random){
		int l = getEffectiveFlowDecay(par1World, par2, par3, par4);
		byte b0 = 1;

		if (blockMaterial == Material.lava && !par1World.provider.isHellWorld)
			b0 = 2;

		boolean flag = true;
		int i1 = tickRate(par1World);
		int j1;

		if (l > 0){
			byte b1 = -100;
			numAdjacentSources = 0;
			int k1 = getSmallestFlowDecay(par1World, par2 - 1, par3, par4, b1);
			k1 = getSmallestFlowDecay(par1World, par2 + 1, par3, par4, k1);
			k1 = getSmallestFlowDecay(par1World, par2, par3, par4 - 1, k1);
			k1 = getSmallestFlowDecay(par1World, par2, par3, par4 + 1, k1);
			j1 = k1 + b0;

			if (j1 >= 8 || k1 < 0)
				j1 = -1;

			if (getEffectiveFlowDecay(par1World, par2, par3 + 1, par4) >= 0){
				int l1 = getEffectiveFlowDecay(par1World, par2, par3 + 1, par4);

				if (l1 >= 8)
					j1 = l1;
				else
					j1 = l1 + 8;
			}

			if (numAdjacentSources >= 2 && blockMaterial == Material.water)
				if (par1World.getBlock(par2, par3 - 1, par4).getMaterial().isSolid())
					j1 = 0;
				else if (par1World.getBlock(par2, par3 - 1, par4).getMaterial() == blockMaterial && par1World.getBlockMetadata(par2, par3 - 1, par4) == 0)
					j1 = 0;

			if (blockMaterial == Material.lava && l < 8 && j1 < 8 && j1 > l && par5Random.nextInt(4) != 0)
				i1 *= 4;

			if (j1 == l){
				if (flag)
					updateFlow(par1World, par2, par3, par4);
			}else{
				l = j1;

				if (j1 < 0)
					par1World.setBlockToAir(par2, par3, par4);
				else{
					par1World.setBlockMetadataWithNotify(par2, par3, par4, j1, 2);
					par1World.scheduleBlockUpdate(par2, par3, par4, this, i1);
					par1World.notifyBlocksOfNeighborChange(par2, par3, par4, this);
				}
			}
		}
		else
			updateFlow(par1World, par2, par3, par4);

		if (liquidCanDisplaceBlock(par1World, par2, par3 - 1, par4)){
			if (blockMaterial == Material.lava && par1World.getBlock(par2, par3 - 1, par4).getMaterial() == Material.water){
				par1World.setBlock(par2, par3 - 1, par4, Blocks.stone);
				func_149799_m(par1World, par2, par3 - 1, par4);
				return;
			}

			if (l >= 8)
				func_149813_h(par1World, par2, par3 - 1, par4, l);
			else
				func_149813_h(par1World, par2, par3 - 1, par4, l + 8);
		}else if (l >= 0 && (l == 0 || func_149807_p(par1World, par2, par3 - 1, par4))){
			boolean[] aboolean = func_149808_o(par1World, par2, par3, par4);
			j1 = l + b0;

			if (l >= 8)
				j1 = 1;

			if (j1 >= 8)
				return;

			if (aboolean[0])
				func_149813_h(par1World, par2 - 1, par3, par4, j1);

			if (aboolean[1])
				func_149813_h(par1World, par2 + 1, par3, par4, j1);

			if (aboolean[2])
				func_149813_h(par1World, par2, par3, par4 - 1, j1);

			if (aboolean[3])
				func_149813_h(par1World, par2, par3, par4 + 1, j1);
		}
	}

	/**
	 * flowIntoBlock(World world, int x, int y, int z, int newFlowDecay) - Flows into the block at the coordinates and
	 * changes the block type to the liquid.
	 */
	private void func_149813_h(World par1World, int par2, int par3, int par4, int par5){
		if (func_149809_q(par1World, par2, par3, par4)){
			Block block = par1World.getBlock(par2, par3, par4);

			if (blockMaterial == Material.lava)
				func_149799_m(par1World, par2, par3, par4);
			else
				block.dropBlockAsItem(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), 0);

			par1World.setBlock(par2, par3, par4, this, par5, 3);
		}
	}

	/**
	 * calculateFlowCost(World world, int x, int y, int z, int accumulatedCost, int previousDirectionOfFlow) - Used to
	 * determine the path of least resistance, this method returns the lowest possible flow cost for the direction of
	 * flow indicated. Each necessary horizontal flow adds to the flow cost.
	 */
	private int func_149812_c(World par1World, int par2, int par3, int par4, int par5, int par6){
		int j1 = 1000;

		for (int k1 = 0; k1 < 4; ++k1)
			if ((k1 != 0 || par6 != 1) && (k1 != 1 || par6 != 0) && (k1 != 2 || par6 != 3) && (k1 != 3 || par6 != 2)){
				int l1 = par2;
				int i2 = par4;

				if (k1 == 0)
					l1 = par2 - 1;

				if (k1 == 1)
					++l1;

				if (k1 == 2)
					i2 = par4 - 1;

				if (k1 == 3)
					++i2;

				if (!func_149807_p(par1World, l1, par3, i2) && (par1World.getBlock(l1, par3, i2).getMaterial() != blockMaterial || par1World.getBlockMetadata(l1, par3, i2) != 0)){
					if (!func_149807_p(par1World, l1, par3 - 1, i2))
						return par5;

					if (par5 < 4){
						int j2 = func_149812_c(par1World, l1, par3, i2, par5 + 1, k1);

						if (j2 < j1)
							j1 = j2;
					}
				}
			}

		return j1;
	}

	/**
	 * Returns a boolean array indicating which flow directions are optimal based on each direction's calculated flow
	 * cost. Each array index corresponds to one of the four cardinal directions. A value of true indicates the
	 * direction is optimal.
	 */
	private boolean[] func_149808_o(World par1World, int par2, int par3, int par4){
		int l;
		int i1;

		for (l = 0; l < 4; ++l){
			field_149816_M[l] = 1000;
			i1 = par2;
			int j1 = par4;

			if (l == 0)
				i1 = par2 - 1;

			if (l == 1)
				++i1;

			if (l == 2)
				j1 = par4 - 1;

			if (l == 3)
				++j1;

			if (!func_149807_p(par1World, i1, par3, j1) && (par1World.getBlock(i1, par3, j1).getMaterial() != blockMaterial || par1World.getBlockMetadata(i1, par3, j1) != 0))
				if (func_149807_p(par1World, i1, par3 - 1, j1))
					field_149816_M[l] = func_149812_c(par1World, i1, par3, j1, 1, l);
				else
					field_149816_M[l] = 0;
		}

		l = field_149816_M[0];

		for (i1 = 1; i1 < 4; ++i1)
			if (field_149816_M[i1] < l)
				l = field_149816_M[i1];

		for (i1 = 0; i1 < 4; ++i1)
			field_149814_b[i1] = field_149816_M[i1] == l;

		return field_149814_b;
	}

	private boolean func_149807_p(World par1World, int par2, int par3, int par4){
		Block block = par1World.getBlock(par2, par3, par4);
		return block != Blocks.wooden_door && block != Blocks.iron_door && block != Blocks.standing_sign && block != Blocks.ladder && block != Blocks.reeds ? (block.getMaterial() == Material.portal ? true : block.getMaterial().blocksMovement()) : true;
	}

	/**
	 * getSmallestFlowDecay(World world, intx, int y, int z, int currentSmallestFlowDecay) - Looks up the flow decay at
	 * the coordinates given and returns the smaller of this value or the provided currentSmallestFlowDecay. If one
	 * value is valid and the other isn't, the valid value will be returned. Valid values are >= 0. Flow decay is the
	 * amount that a liquid has dissipated. 0 indicates a source block.
	 */
	protected int getSmallestFlowDecay(World par1World, int par2, int par3, int par4, int par5){
		int i1 = getEffectiveFlowDecay(par1World, par2, par3, par4);

		if (i1 < 0)
			return par5;
		else{
			if (i1 == 0)
				++numAdjacentSources;

			if (i1 >= 8)
				i1 = 0;

			return par5 >= 0 && i1 >= par5 ? par5 : i1;
		}
	}

	/**
	 * Returns true if the block at the coordinates can be displaced by the liquid.
	 */
	private boolean liquidCanDisplaceBlock(World par1World, int par2, int par3, int par4){
		Material material = par1World.getBlock(par2, par3, par4).getMaterial();
		return material == blockMaterial ? false : (material == Material.lava ? false : !func_149807_p(par1World, par2, par3, par4));
	}

	/**
	 * Called whenever the block is added into the world. Args: world, x, y, z
	 */
	@Override
	public void onBlockAdded(World par1World, int par2, int par3, int par4){
		super.onBlockAdded(par1World, par2, par3, par4);

		if (par1World.getBlock(par2, par3, par4) == this)
			par1World.scheduleBlockUpdate(par2, par3, par4, this, tickRate(par1World));
	}

	private boolean func_149809_q(World par1World, int par2, int par3, int par4){
		Material material = par1World.getBlock(par2, par3, par4).getMaterial();
		return material == blockMaterial ? false : (material == Material.lava ? false : !func_149807_p(par1World, par2, par3, par4));
	}

	public boolean func_82506_l(){
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_)
	{
		return null;
	}

}
