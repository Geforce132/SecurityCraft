package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.blocks.BlockScannerDoor;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

public class TileEntityScannerDoor extends CustomizableSCTE
{
	public boolean open = false;

	@Override
	public void entityViewed(EntityLivingBase entity)
	{
		if(!worldObj.isRemote && BlockUtils.isMetadataBetween(worldObj, xCoord, yCoord, zCoord, 8, 9)) //if it's the top part
		{
			if(!(entity instanceof EntityPlayer))
				return;

			EntityPlayer player = (EntityPlayer)entity;

			if(PlayerUtils.isPlayerMountedOnCamera(player))
				return;

			if(!getOwner().isOwner(player))
			{
				PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.scannerDoorItem.name"), StatCollector.translateToLocal("messages.retinalScanner.notOwner").replace("#", getOwner().getName()), EnumChatFormatting.RED);
				return;
			}

			if(!open)
				((BlockScannerDoor)worldObj.getBlock(xCoord, yCoord, zCoord)).func_150014_a(worldObj, xCoord, yCoord, zCoord, true);
			else
				((BlockScannerDoor)worldObj.getBlock(xCoord, yCoord, zCoord)).func_150014_a(worldObj, xCoord, yCoord, zCoord, false);

			if(!open)
				PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.scannerDoorItem.name"), StatCollector.translateToLocal("messages.retinalScanner.hello").replace("#", player.getCommandSenderName()), EnumChatFormatting.GREEN);

			open = !open;
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
