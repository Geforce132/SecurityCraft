package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IExtractionBlock;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.TileEntityReinforcedHopper;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class BlockReinforcedHopper extends BlockHopper implements IReinforcedBlock
{
	public BlockReinforcedHopper()
	{
		setSoundType(SoundType.METAL);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		if(placer instanceof EntityPlayer)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (EntityPlayer)placer));
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(!world.isRemote)
		{
			TileEntity tileEntity = world.getTileEntity(pos);

			if(tileEntity instanceof TileEntityReinforcedHopper)
			{
				TileEntityReinforcedHopper te = (TileEntityReinforcedHopper)tileEntity;

				//only allow the owner or players on the allowlist to access a reinforced hopper
				if(te.getOwner().isOwner(player) || ModuleUtils.isAllowed(te, player))
				{
					player.displayGUIChest(te);
					player.addStat(StatList.HOPPER_INSPECTED);
				}
			}
		}

		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileEntityReinforcedHopper();
	}

	@Override
	public List<Block> getVanillaBlocks()
	{
		return Arrays.asList(Blocks.HOPPER);
	}

	@Override
	public int getAmount()
	{
		return 1;
	}

	public static class ExtractionBlock implements IExtractionBlock, Function<Object,IExtractionBlock>
	{
		@Override
		public IExtractionBlock apply(Object o)
		{
			return this;
		}

		@Override
		public boolean canExtract(IOwnable te, World world, BlockPos pos, IBlockState state)
		{
			TileEntityReinforcedHopper hopperTe = (TileEntityReinforcedHopper)world.getTileEntity(pos);

			if(!te.getOwner().owns(hopperTe))
			{
				if(te instanceof IModuleInventory)
				{
					IModuleInventory inv = (IModuleInventory)te;

					//hoppers can extract out of e.g. chests if the hopper's owner is on the chest's allowlist module
					if(ModuleUtils.isAllowed(inv, hopperTe.getOwner().getName()))
						return true;
					//hoppers can extract out of e.g. chests whose owner is on the hopper's allowlist module
					else if(ModuleUtils.isAllowed(hopperTe, te.getOwner().getName()))
						return true;
				}

				return false;
			}
			else return true;
		}

		@Override
		public Block getBlock()
		{
			return SCContent.reinforcedHopper;
		}
	}
}
