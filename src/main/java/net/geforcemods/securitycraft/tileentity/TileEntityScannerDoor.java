package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.blocks.BlockScannerDoor;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

public class TileEntityScannerDoor extends CustomizableSCTE
{
	@Override
	public void entityViewed(EntityLivingBase entity)
	{
		IBlockState upperState = worldObj.getBlockState(pos);
		IBlockState lowerState = worldObj.getBlockState(pos.down());

		if(!worldObj.isRemote && upperState.getValue(BlockScannerDoor.HALF) == BlockDoor.EnumDoorHalf.UPPER)
		{
			if(!(entity instanceof EntityPlayer))
				return;

			EntityPlayer player = (EntityPlayer)entity;

			if(PlayerUtils.isPlayerMountedOnCamera(player))
				return;

			if(!getOwner().isOwner(player))
			{
				PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.securitycraft:scannerDoorItem.name"), StatCollector.translateToLocal("messages.securitycraft:retinalScanner.notOwner").replace("#", getOwner().getName()), EnumChatFormatting.RED);
				return;
			}

			boolean open = !BlockUtils.getBlockPropertyAsBoolean(worldObj, pos.down(), BlockScannerDoor.OPEN);

			worldObj.setBlockState(pos, upperState.withProperty(BlockScannerDoor.OPEN, !((Boolean)upperState.getValue(BlockScannerDoor.OPEN)).booleanValue()), 3);
			worldObj.setBlockState(pos.down(), lowerState.withProperty(BlockScannerDoor.OPEN, !((Boolean)lowerState.getValue(BlockScannerDoor.OPEN)).booleanValue()), 3);
			worldObj.markBlockRangeForRenderUpdate(pos.down(), pos);
			worldObj.playAuxSFXAtEntity(null, 1006, pos, 0);

			if(open)
				PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.securitycraft:scannerDoorItem.name"), StatCollector.translateToLocal("messages.securitycraft:retinalScanner.hello").replace("#", player.getCommandSenderName()), EnumChatFormatting.GREEN);
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

	@Override
	public String getCommandSenderName()
	{
		return "ScannerDoor";
	}
}
