package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypad;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadDoor;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockKeypadDoor extends BlockSpecialDoor
{
	public BlockKeypadDoor(Material material)
	{
		super(material);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(state.getValue(POWERED))
			return false;
		else if(!world.isRemote) {
			if(ModuleUtils.checkForModule(world, pos, player, EnumModuleType.BLACKLIST))
				return false;

			if(ModuleUtils.checkForModule(world, pos, player, EnumModuleType.WHITELIST)){
				activate(world, pos, state, ((TileEntityKeypad)world.getTileEntity(pos)).getSignalLength());
				return true;
			}

			if(!PlayerUtils.isHoldingItem(player, SCContent.codebreaker) && !PlayerUtils.isHoldingItem(player, SCContent.keyPanel))
				((IPasswordProtected) world.getTileEntity(pos)).openPasswordGUI(player);
		}

		return true;
	}

	public static void activate(World world, BlockPos pos, IBlockState state, int signalLength){
		boolean open = !state.getValue(OPEN);

		world.playEvent(null, open ? 1005 : 1011, pos, 0);
		world.setBlockState(pos, state.withProperty(OPEN, open));
		world.markBlockRangeForRenderUpdate(pos, pos);
		world.notifyNeighborsOfStateChange(pos, SCContent.keypadDoor, false);

		if(open && signalLength > 0)
			world.scheduleUpdate(pos, SCContent.keypadDoor, signalLength);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileEntityKeypadDoor().linkable();
	}

	@Override
	public Item getDoorItem()
	{
		return SCContent.keypadDoorItem;
	}
}
