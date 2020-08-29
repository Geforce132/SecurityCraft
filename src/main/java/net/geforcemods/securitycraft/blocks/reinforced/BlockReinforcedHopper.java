package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHopper;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockReinforcedHopper extends BlockHopper implements IReinforcedBlock
{
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

	public static boolean canExtract(IOwnable te, World world, BlockPos hopperPos)
	{
		TileEntityReinforcedHopper hopperTe = (TileEntityReinforcedHopper)world.getTileEntity(hopperPos);

		if(!te.getOwner().owns(hopperTe))
		{
			if(te instanceof IModuleInventory)
			{
				IModuleInventory inv = (IModuleInventory)te;

				if(inv.hasModule(EnumModuleType.WHITELIST) && ModuleUtils.getPlayersFromModule(inv.getModule(EnumModuleType.WHITELIST)).contains(hopperTe.getOwner().getName().toLowerCase()))
					return true;
			}

			return false;
		}
		else return true;
	}
}
