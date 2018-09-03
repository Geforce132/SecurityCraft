package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SCContent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFakeLava extends BlockDynamicLiquid{

	/**
	 * Indicates whether the flow direction is optimal. Each array index corresponds to one of the four cardinal
	 * directions.
	 */
	private boolean[] isOptimalFlowDirection = new boolean[4];

	/**
	 * The estimated cost to flow in a given direction from the current point. Each array index corresponds to one of
	 * the four cardinal directions.
	 */
	private int[] flowCost = new int[4];

	/**
	 * Number of horizontally adjacent liquid source blocks. Diagonal doesn't count. Only source blocks of the same
	 * liquid as the block using the field are counted.
	 */
	int numAdjacentSources;

	public BlockFakeLava(Material material){
		super(material);
	}

	/**
	 * Updates the flow for the BlockFlowing object.
	 */
	private void updateFlow(World world, int x, int y, int z){
		int meta = world.getBlockMetadata(x, y, z);
		world.setBlock(x, y, z, SCContent.bogusLava, meta, 2);
	}

	@Override
	public boolean isPassable(IBlockAccess access, int x, int y, int z){
		return blockMaterial != Material.lava;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random random){
		int flowDecay = getEffectiveFlowDecay(world, x, y, z);
		byte b0 = 1;

		if (blockMaterial == Material.lava && !world.provider.isHellWorld)
			b0 = 2;

		boolean flag = true;
		int tickRate = tickRate(world);
		int j1;

		if (flowDecay > 0){
			byte b1 = -100;
			numAdjacentSources = 0;
			int minFlowDecay = getSmallestFlowDecay(world, x - 1, y, z, b1);
			minFlowDecay = getSmallestFlowDecay(world, x + 1, y, z, minFlowDecay);
			minFlowDecay = getSmallestFlowDecay(world, x, y, z - 1, minFlowDecay);
			minFlowDecay = getSmallestFlowDecay(world, x, y, z + 1, minFlowDecay);
			j1 = minFlowDecay + b0;

			if (j1 >= 8 || minFlowDecay < 0)
				j1 = -1;

			if (getEffectiveFlowDecay(world, x, y + 1, z) >= 0){
				int flowDecayAbove = getEffectiveFlowDecay(world, x, y + 1, z);

				if (flowDecayAbove >= 8)
					j1 = flowDecayAbove;
				else
					j1 = flowDecayAbove + 8;
			}

			if (numAdjacentSources >= 2 && blockMaterial == Material.water)
				if (world.getBlock(x, y - 1, z).getMaterial().isSolid())
					j1 = 0;
				else if (world.getBlock(x, y - 1, z).getMaterial() == blockMaterial && world.getBlockMetadata(x, y - 1, z) == 0)
					j1 = 0;

			if (blockMaterial == Material.lava && flowDecay < 8 && j1 < 8 && j1 > flowDecay && random.nextInt(4) != 0)
				tickRate *= 4;

			if (j1 == flowDecay){
				if (flag)
					updateFlow(world, x, y, z);
			}else{
				flowDecay = j1;

				if (j1 < 0)
					world.setBlockToAir(x, y, z);
				else{
					world.setBlockMetadataWithNotify(x, y, z, j1, 2);
					world.scheduleBlockUpdate(x, y, z, this, tickRate);
					world.notifyBlocksOfNeighborChange(x, y, z, this);
				}
			}
		}
		else
			updateFlow(world, x, y, z);

		if (liquidCanDisplaceBlock(world, x, y - 1, z)){
			if (blockMaterial == Material.lava && world.getBlock(x, y - 1, z).getMaterial() == Material.water){
				world.setBlock(x, y - 1, z, Blocks.stone);
				func_149799_m(world, x, y - 1, z);
				return;
			}

			if (flowDecay >= 8)
				flowIntoBlock(world, x, y - 1, z, flowDecay);
			else
				flowIntoBlock(world, x, y - 1, z, flowDecay + 8);
		}else if (flowDecay >= 0 && (flowDecay == 0 || blockedBy(world, x, y - 1, z))){
			boolean[] directionsTemp = getOptimalFlowDirections(world, x, y, z);
			j1 = flowDecay + b0;

			if (flowDecay >= 8)
				j1 = 1;

			if (j1 >= 8)
				return;

			if (directionsTemp[0])
				flowIntoBlock(world, x - 1, y, z, j1);

			if (directionsTemp[1])
				flowIntoBlock(world, x + 1, y, z, j1);

			if (directionsTemp[2])
				flowIntoBlock(world, x, y, z - 1, j1);

			if (directionsTemp[3])
				flowIntoBlock(world, x, y, z + 1, j1);
		}
	}

	/**
	 * flowIntoBlock(World world, int x, int y, int z, int newFlowDecay) - Flows into the block at the coordinates and
	 * changes the block type to the liquid.
	 */
	private void flowIntoBlock(World world, int x, int y, int z, int newFlowDecay){
		if (liquidCanDisplaceBlock(world, x, y, z)){
			Block block = world.getBlock(x, y, z);

			if (blockMaterial == Material.lava)
				func_149799_m(world, x, y, z);
			else
				block.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);

			world.setBlock(x, y, z, this, newFlowDecay, 3);
		}
	}

	/**
	 * calculateFlowCost(World world, int x, int y, int z, int accumulatedCost, int previousDirectionOfFlow) - Used to
	 * determine the path of least resistance, this method returns the lowest possible flow cost for the direction of
	 * flow indicated. Each necessary horizontal flow adds to the flow cost.
	 */
	private int calculateFlowCost(World world, int x, int y, int z, int accumulatedCost, int previousDirectionOfFlow){
		int cost = 1000;

		for (int direction = 0; direction < 4; ++direction)
			if ((direction != 0 || previousDirectionOfFlow != 1) && (direction != 1 || previousDirectionOfFlow != 0) && (direction != 2 || previousDirectionOfFlow != 3) && (direction != 3 || previousDirectionOfFlow != 2)){
				int newX = x;
				int newZ = z;

				if (direction == 0)
					newX = x - 1;

				if (direction == 1)
					++newX;

				if (direction == 2)
					newZ = z - 1;

				if (direction == 3)
					++newZ;

				if (!blockedBy(world, newX, y, newZ) && (world.getBlock(newX, y, newZ).getMaterial() != blockMaterial || world.getBlockMetadata(newX, y, newZ) != 0)){
					if (!blockedBy(world, newX, y - 1, newZ))
						return accumulatedCost;

					if (accumulatedCost < 4){
						int oppositeCost = calculateFlowCost(world, newX, y, newZ, accumulatedCost + 1, direction);

						if (oppositeCost < cost)
							cost = oppositeCost;
					}
				}
			}

		return cost;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity){
		if(!world.isRemote)
			if(entity instanceof EntityPlayer){
				((EntityPlayer) entity).heal(4);
				((EntityPlayer) entity).extinguish();
			}
	}

	/**
	 * Returns a boolean array indicating which flow directions are optimal based on each direction's calculated flow
	 * cost. Each array index corresponds to one of the four cardinal directions. A value of true indicates the
	 * direction is optimal.
	 */
	private boolean[] getOptimalFlowDirections(World world, int x, int y, int z){
		int direction;
		int newX;

		for (direction = 0; direction < 4; ++direction){
			flowCost[direction] = 1000;
			newX = x;
			int newZ = z;

			if (direction == 0)
				newX = x - 1;

			if (direction == 1)
				++newX;

			if (direction == 2)
				newZ = z - 1;

			if (direction == 3)
				++newZ;

			if (!blockedBy(world, newX, y, newZ) && (world.getBlock(newX, y, newZ).getMaterial() != blockMaterial || world.getBlockMetadata(newX, y, newZ) != 0))
				if (blockedBy(world, newX, y - 1, newZ))
					flowCost[direction] = calculateFlowCost(world, newX, y, newZ, 1, direction);
				else
					flowCost[direction] = 0;
		}

		direction = flowCost[0];

		for (newX = 1; newX < 4; ++newX)
			if (flowCost[newX] < direction)
				direction = flowCost[newX];

		for (newX = 0; newX < 4; ++newX)
			isOptimalFlowDirection[newX] = flowCost[newX] == direction;

		return isOptimalFlowDirection;
	}

	private boolean blockedBy(World world, int x, int y, int z){
		Block block = world.getBlock(x, y, z);
		return block != Blocks.wooden_door && block != Blocks.iron_door && block != Blocks.standing_sign && block != Blocks.ladder && block != Blocks.reeds ? (block.getMaterial() == Material.portal ? true : block.getMaterial().blocksMovement()) : true;
	}

	/**
	 * getSmallestFlowDecay(World world, intx, int y, int z, int currentSmallestFlowDecay) - Looks up the flow decay at
	 * the coordinates given and returns the smaller of this value or the provided currentSmallestFlowDecay. If one
	 * value is valid and the other isn't, the valid value will be returned. Valid values are >= 0. Flow decay is the
	 * amount that a liquid has dissipated. 0 indicates a source block.
	 */
	protected int getSmallestFlowDecay(World world, int x, int y, int z, int currentSmallestFlowDecay){
		int i1 = getEffectiveFlowDecay(world, x, y, z);

		if (i1 < 0)
			return currentSmallestFlowDecay;
		else{
			if (i1 == 0)
				++numAdjacentSources;

			if (i1 >= 8)
				i1 = 0;

			return currentSmallestFlowDecay >= 0 && i1 >= currentSmallestFlowDecay ? currentSmallestFlowDecay : i1;
		}
	}

	/**
	 * Returns true if the block at the coordinates can be displaced by the liquid.
	 */
	private boolean liquidCanDisplaceBlock(World world, int x, int y, int z){
		Material material = world.getBlock(x, y, z).getMaterial();
		return material == blockMaterial ? false : (material == Material.lava ? false : !blockedBy(world, x, y, z));
	}

	/**
	 * Called whenever the block is added into the world. Args: world, x, y, z
	 */
	@Override
	public void onBlockAdded(World world, int x, int y, int z){
		super.onBlockAdded(world, x, y, z);

		if (world.getBlock(x, y, z) == this)
			world.scheduleBlockUpdate(x, y, z, this, tickRate(world));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World world, int x, int y, int z)
	{
		return null;
	}

}
