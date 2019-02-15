package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockReinforcedDoor extends BlockDoor implements ITileEntityProvider{

	public BlockReinforcedDoor(Material material) {
		super(material);
		setSoundType(SoundType.METAL);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
	{
		onNeighborChanged(world, pos, fromPos);
	}

	/**
	 * Old method, renamed because I am lazy. Called by neighborChanged
	 * @param world The world the change occured in
	 * @param pos The position of this block
	 * @param neighbor The position of the changed block
	 */
	public void onNeighborChanged(IBlockAccess access, BlockPos pos, BlockPos neighbor)
	{
		World world = (World)access;
		IBlockState state = world.getBlockState(pos);
		Block neighborBlock = world.getBlockState(neighbor).getBlock();

		if (state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER)
		{
			BlockPos blockBelow = pos.down();
			IBlockState stateBelow = world.getBlockState(blockBelow);

			if (stateBelow.getBlock() != this)
				world.setBlockToAir(pos);
			else if (neighborBlock != this)
				onNeighborChanged(world, blockBelow, neighbor);
		}
		else
		{
			boolean drop = false;
			BlockPos blockAbove = pos.up();
			IBlockState stateAbove = world.getBlockState(blockAbove);

			if (stateAbove.getBlock() != this)
			{
				world.setBlockToAir(pos);
				drop = true;
			}

			if (!world.isSideSolid(pos.down(), EnumFacing.UP))
			{
				world.setBlockToAir(pos);
				drop = true;

				if (stateAbove.getBlock() == this)
					world.setBlockToAir(blockAbove);
			}

			if (drop)
			{
				if (!world.isRemote)
					dropBlockAsItem(world, pos, state, 0);
			}
			else
			{
				boolean hasActiveSCBlock = BlockUtils.hasActiveSCBlockNextTo(world, pos) || BlockUtils.hasActiveSCBlockNextTo(world, pos.up());

				if (((hasActiveSCBlock || neighborBlock.canProvidePower(stateAbove))) && neighborBlock != this && hasActiveSCBlock != stateAbove.getValue(POWERED).booleanValue())
				{
					world.setBlockState(blockAbove, stateAbove.withProperty(POWERED, Boolean.valueOf(hasActiveSCBlock)), 2);

					if (hasActiveSCBlock != state.getValue(OPEN).booleanValue())
					{
						world.setBlockState(pos, state.withProperty(OPEN, Boolean.valueOf(hasActiveSCBlock)), 2);
						world.markBlockRangeForRenderUpdate(pos, pos);

						IBlockState secondDoorState;

						if(state.getValue(FACING) == EnumFacing.WEST)
						{
							secondDoorState = world.getBlockState(pos.north());

							if(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.getValue(OPEN).booleanValue() != hasActiveSCBlock)
							{
								world.setBlockState(pos.north(), secondDoorState.withProperty(OPEN, hasActiveSCBlock), 2);
								world.markBlockRangeForRenderUpdate(pos.north(), pos.north());
							}
							else
							{
								secondDoorState = world.getBlockState(pos.south());

								if(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.getValue(OPEN).booleanValue() != hasActiveSCBlock)
								{
									world.setBlockState(pos.south(), secondDoorState.withProperty(OPEN, hasActiveSCBlock), 2);
									world.markBlockRangeForRenderUpdate(pos.south(), pos.south());
								}
							}
						}
						else if(state.getValue(FACING) == EnumFacing.NORTH)
						{
							secondDoorState = world.getBlockState(pos.east());

							if(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.getValue(OPEN).booleanValue() != hasActiveSCBlock)
							{
								world.setBlockState(pos.east(), secondDoorState.withProperty(OPEN, hasActiveSCBlock), 2);
								world.markBlockRangeForRenderUpdate(pos.east(), pos.east());
							}
							else
							{
								secondDoorState = world.getBlockState(pos.west());

								if(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.getValue(OPEN).booleanValue() != hasActiveSCBlock)
								{
									world.setBlockState(pos.west(), secondDoorState.withProperty(OPEN, hasActiveSCBlock), 2);
									world.markBlockRangeForRenderUpdate(pos.west(), pos.west());
								}
							}
						}
						else if(state.getValue(FACING) == EnumFacing.EAST)
						{
							secondDoorState = world.getBlockState(pos.south());

							if(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.getValue(OPEN).booleanValue() != hasActiveSCBlock)
							{
								world.setBlockState(pos.south(), secondDoorState.withProperty(OPEN, hasActiveSCBlock), 2);
								world.markBlockRangeForRenderUpdate(pos.south(), pos.south());
							}
							else
							{
								secondDoorState = world.getBlockState(pos.north());

								if(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.getValue(OPEN).booleanValue() != hasActiveSCBlock)
								{
									world.setBlockState(pos.north(), secondDoorState.withProperty(OPEN, hasActiveSCBlock), 2);
									world.markBlockRangeForRenderUpdate(pos.north(), pos.north());
								}
							}
						}
						else if(state.getValue(FACING) == EnumFacing.SOUTH)
						{
							secondDoorState = world.getBlockState(pos.west());

							if(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.getValue(OPEN).booleanValue() != hasActiveSCBlock)
							{
								world.setBlockState(pos.west(), secondDoorState.withProperty(OPEN, hasActiveSCBlock), 2);
								world.markBlockRangeForRenderUpdate(pos.west(), pos.west());
							}
							else
							{
								secondDoorState = world.getBlockState(pos.east());

								if(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.getValue(OPEN).booleanValue() != hasActiveSCBlock)
								{
									world.setBlockState(pos.east(), secondDoorState.withProperty(OPEN, hasActiveSCBlock), 2);
									world.markBlockRangeForRenderUpdate(pos.east(), pos.east());
								}
							}
						}

						world.playEvent((EntityPlayer)null, hasActiveSCBlock ? 1005 : 1011, pos, 0);
					}
				}
			}
		}
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		super.breakBlock(world, pos, state);
		world.removeTileEntity(pos);
	}

	@Override
	public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int param)
	{
		super.eventReceived(state, world, pos, id, param);
		TileEntity tileentity = world.getTileEntity(pos);
		return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ItemStack getItem(World world, BlockPos pos, IBlockState state){
		return new ItemStack(SCContent.reinforcedDoorItem);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune){
		return state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER ? null : SCContent.reinforcedDoorItem;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityOwnable();
	}
}