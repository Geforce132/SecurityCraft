package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.blocks.BlockScannerDoor;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

public class TileEntityScannerDoor extends CustomizableSCTE
{
	public void entityViewed(EntityLivingBase entity)
	{
		IBlockState upperState = worldObj.getBlockState(pos);
		IBlockState lowerState = worldObj.getBlockState(pos.down());
		
		if(!worldObj.isRemote && upperState.getValue(BlockScannerDoor.HALF) == BlockDoor.EnumDoorHalf.UPPER)
		{
			if(!(entity instanceof EntityPlayer))
				return;
			else
			{
				if(!getOwner().isOwner((EntityPlayer) entity))
				{
					PlayerUtils.sendMessageToPlayer((EntityPlayer) entity, StatCollector.translateToLocal("item.scannerDoorItem.name"), StatCollector.translateToLocal("messages.retinalScanner.notOwner").replace("#", getOwner().getName()), EnumChatFormatting.RED);
					return;
				}
				
				worldObj.setBlockState(pos, upperState.withProperty(BlockScannerDoor.OPEN, !upperState.getValue(BlockScannerDoor.OPEN).booleanValue()), 3);
				worldObj.setBlockState(pos.down(), lowerState.withProperty(BlockScannerDoor.OPEN, !lowerState.getValue(BlockScannerDoor.OPEN).booleanValue()), 3);
				worldObj.markBlockRangeForRenderUpdate(pos.down(), pos);
                worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1006, pos, 0);
				PlayerUtils.sendMessageToPlayer((EntityPlayer) entity, StatCollector.translateToLocal("item.scannerDoorItem.name"), StatCollector.translateToLocal("messages.retinalScanner.hello").replace("#", entity.getCommandSenderName()), EnumChatFormatting.GREEN);
			}
		}
	}

	public int getViewCooldown()
	{
		return 30;
	}

	public EnumCustomModules[] acceptedModules()
	{
		return new EnumCustomModules[]{};
	}

	public Option<?>[] customOptions()
	{
		return new Option[]{};
	}
}
