package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;

public class TileEntityScannerDoor extends CustomizableSCTE
{
	@Override
	public void entityViewed(EntityLivingBase entity)
	{
		IBlockState upperState = worldObj.getBlockState(pos);
		IBlockState lowerState = worldObj.getBlockState(pos.down());

		if(!worldObj.isRemote && upperState.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.UPPER)
		{
			if(!(entity instanceof EntityPlayer))
				return;

			EntityPlayer player = (EntityPlayer)entity;

			if(PlayerUtils.isPlayerMountedOnCamera(player))
				return;

			if(!getOwner().isOwner(player))
			{
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.scannerDoorItem.name"), ClientUtils.localize("messages.retinalScanner.notOwner").replace("#", getOwner().getName()), TextFormatting.RED);
				return;
			}

			boolean open = !BlockUtils.getBlockPropertyAsBoolean(worldObj, pos.down(), BlockDoor.OPEN);

			worldObj.setBlockState(pos, upperState.withProperty(BlockDoor.OPEN, !upperState.getValue(BlockDoor.OPEN).booleanValue()), 3);
			worldObj.setBlockState(pos.down(), lowerState.withProperty(BlockDoor.OPEN, !lowerState.getValue(BlockDoor.OPEN).booleanValue()), 3);
			worldObj.markBlockRangeForRenderUpdate(pos.down(), pos);
			worldObj.playEvent(null, open ? 1005 : 1011, pos, 0);

			if(open)
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.scannerDoorItem.name"), ClientUtils.localize("messages.retinalScanner.hello").replace("#", player.getName()), TextFormatting.GREEN);
		}
	}

	@Override
	public int getViewCooldown()
	{
		return 30;
	}

	@Override
	public EnumCustomModules[] acceptedModules()
	{
		return new EnumCustomModules[]{};
	}

	@Override
	public Option<?>[] customOptions()
	{
		return new Option[]{};
	}
}
