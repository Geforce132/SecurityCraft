package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class BlockReinforcedDoor extends BlockDoor implements ITileEntityProvider{

	public BlockReinforcedDoor(Material material) {
		super(Block.Properties.create(material).hardnessAndResistance(-1.0F, 6000000.0F).sound(SoundType.METAL));
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
	{
		onNeighborChanged(world, pos, fromPos);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		if(placer instanceof EntityPlayer)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (EntityPlayer)placer));
	}

	/**
	 * Old method, renamed because I am lazy. Called by neighborChanged
	 * @param world The world the change occured in
	 * @param pos The position of this block
	 * @param neighbor The position of the changed block
	 */
	public void onNeighborChanged(World world, BlockPos pos, BlockPos neighbor)
	{
		IBlockState state = world.getBlockState(pos);
		Block neighborBlock = world.getBlockState(neighbor).getBlock();
		Owner previousOwner = null;

		if(world.getTileEntity(pos) instanceof TileEntityOwnable)
			previousOwner = ((TileEntityOwnable)world.getTileEntity(pos)).getOwner();

		if (state.get(HALF) == DoubleBlockHalf.UPPER)
		{
			BlockPos blockBelow = pos.down();
			IBlockState stateBelow = world.getBlockState(blockBelow);

			if (stateBelow.getBlock() != this)
				world.removeBlock(pos);
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
				world.removeBlock(pos);
				drop = true;
			}

			if (!world.isTopSolid(pos.down()))
			{
				world.removeBlock(pos);
				drop = true;

				if (stateAbove.getBlock() == this)
					world.removeBlock(blockAbove);
			}

			if (drop)
			{
				if (!world.isRemote)
					dropBlockAsItemWithChance(state, world, pos, 1.0F, 0);
			}
			else
			{
				boolean hasActiveSCBlock = BlockUtils.hasActiveSCBlockNextTo(world, pos) || BlockUtils.hasActiveSCBlockNextTo(world, pos.up());

				if (((hasActiveSCBlock || neighborBlock.canProvidePower(stateAbove))) && neighborBlock != this && hasActiveSCBlock != stateAbove.get(POWERED).booleanValue())
				{
					world.setBlockState(blockAbove, stateAbove.with(POWERED, Boolean.valueOf(hasActiveSCBlock)), 2);

					if (hasActiveSCBlock != state.get(OPEN).booleanValue())
					{
						world.setBlockState(pos, state.with(OPEN, Boolean.valueOf(hasActiveSCBlock)), 2);
						world.markBlockRangeForRenderUpdate(pos, pos);

						IBlockState secondDoorState;

						if(state.get(FACING) == EnumFacing.WEST)
						{
							secondDoorState = world.getBlockState(pos.north());

							if(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.get(OPEN).booleanValue() != hasActiveSCBlock)
							{
								world.setBlockState(pos.north(), secondDoorState.with(OPEN, hasActiveSCBlock), 2);
								world.markBlockRangeForRenderUpdate(pos.north(), pos.north());
							}
							else
							{
								secondDoorState = world.getBlockState(pos.south());

								if(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.get(OPEN).booleanValue() != hasActiveSCBlock)
								{
									world.setBlockState(pos.south(), secondDoorState.with(OPEN, hasActiveSCBlock), 2);
									world.markBlockRangeForRenderUpdate(pos.south(), pos.south());
								}
							}
						}
						else if(state.get(FACING) == EnumFacing.NORTH)
						{
							secondDoorState = world.getBlockState(pos.east());

							if(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.get(OPEN).booleanValue() != hasActiveSCBlock)
							{
								world.setBlockState(pos.east(), secondDoorState.with(OPEN, hasActiveSCBlock), 2);
								world.markBlockRangeForRenderUpdate(pos.east(), pos.east());
							}
							else
							{
								secondDoorState = world.getBlockState(pos.west());

								if(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.get(OPEN).booleanValue() != hasActiveSCBlock)
								{
									world.setBlockState(pos.west(), secondDoorState.with(OPEN, hasActiveSCBlock), 2);
									world.markBlockRangeForRenderUpdate(pos.west(), pos.west());
								}
							}
						}
						else if(state.get(FACING) == EnumFacing.EAST)
						{
							secondDoorState = world.getBlockState(pos.south());

							if(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.get(OPEN).booleanValue() != hasActiveSCBlock)
							{
								world.setBlockState(pos.south(), secondDoorState.with(OPEN, hasActiveSCBlock), 2);
								world.markBlockRangeForRenderUpdate(pos.south(), pos.south());
							}
							else
							{
								secondDoorState = world.getBlockState(pos.north());

								if(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.get(OPEN).booleanValue() != hasActiveSCBlock)
								{
									world.setBlockState(pos.north(), secondDoorState.with(OPEN, hasActiveSCBlock), 2);
									world.markBlockRangeForRenderUpdate(pos.north(), pos.north());
								}
							}
						}
						else if(state.get(FACING) == EnumFacing.SOUTH)
						{
							secondDoorState = world.getBlockState(pos.west());

							if(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.get(OPEN).booleanValue() != hasActiveSCBlock)
							{
								world.setBlockState(pos.west(), secondDoorState.with(OPEN, hasActiveSCBlock), 2);
								world.markBlockRangeForRenderUpdate(pos.west(), pos.west());
							}
							else
							{
								secondDoorState = world.getBlockState(pos.east());

								if(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.get(OPEN).booleanValue() != hasActiveSCBlock)
								{
									world.setBlockState(pos.east(), secondDoorState.with(OPEN, hasActiveSCBlock), 2);
									world.markBlockRangeForRenderUpdate(pos.east(), pos.east());
								}
							}
						}

						world.playEvent((EntityPlayer)null, hasActiveSCBlock ? 1005 : 1011, pos, 0);
					}
				}
			}
		}

		if(previousOwner != null && world.getTileEntity(pos) instanceof TileEntityOwnable)
			((TileEntityOwnable)world.getTileEntity(pos)).getOwner().set(previousOwner);
	}

	@Override
	public void onReplaced(IBlockState state, World world, BlockPos pos, IBlockState newState, boolean isMoving)
	{
		super.onReplaced(state, world, pos, newState, isMoving);
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
	public ItemStack getItem(IBlockReader worldIn, BlockPos pos, IBlockState state){
		return new ItemStack(SCContent.reinforcedDoorItem);
	}

	@Override
	public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune){
		return state.get(HALF) == DoubleBlockHalf.UPPER ? Items.AIR : SCContent.reinforcedDoorItem;
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world) {
		return new TileEntityOwnable();
	}
}