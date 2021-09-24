package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadDoor;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
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
			TileEntityKeypadDoor te = (TileEntityKeypadDoor)world.getTileEntity(pos);

			if(ModuleUtils.isDenied(te, player))
			{
				if(te.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getTranslationKey() + ".name"), Utils.localize("messages.securitycraft:module.onDenylist"), TextFormatting.RED);

				return true;
			}

			if(ModuleUtils.isAllowed(te, player)){
				if(te.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getTranslationKey() + ".name"), Utils.localize("messages.securitycraft:module.onAllowlist"), TextFormatting.GREEN);

				activate(world, pos, state, te.getSignalLength());
				return true;
			}

			if(!PlayerUtils.isHoldingItem(player, SCContent.codebreaker, hand))
				te.openPasswordGUI(player);
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
