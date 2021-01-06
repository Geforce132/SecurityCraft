package net.geforcemods.securitycraft.tileentity;

import java.util.ArrayList;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.EnumLinkedAction;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionBoolean;
import net.geforcemods.securitycraft.api.Option.OptionInt;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoor.EnumDoorHalf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class TileEntityScannerDoor extends CustomizableSCTE
{
	private OptionBoolean sendMessage = new OptionBoolean("sendMessage", true);
	private OptionInt signalLength = new OptionInt(this::getPos, "signalLength", 0, 0, 400, 5, true); //20 seconds max

	@Override
	public void entityViewed(EntityLivingBase entity)
	{
		IBlockState upperState = world.getBlockState(pos);
		IBlockState lowerState = world.getBlockState(pos.down());

		if(!world.isRemote && upperState.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.UPPER && !EntityUtils.isInvisible(entity))
		{
			if(!(entity instanceof EntityPlayer))
				return;

			EntityPlayer player = (EntityPlayer)entity;

			if(PlayerUtils.isPlayerMountedOnCamera(player))
				return;

			if(!getOwner().isOwner(player) && (!hasModule(EnumModuleType.WHITELIST) || !ModuleUtils.getPlayersFromModule(getModule(EnumModuleType.WHITELIST)).contains(player.getName().toLowerCase())))
			{
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.securitycraft:scannerDoorItem.name"), ClientUtils.localize("messages.securitycraft:retinalScanner.notOwner").replace("#", getOwner().getName()), TextFormatting.RED);
				return;
			}

			boolean open = !BlockUtils.getBlockProperty(world, pos.down(), BlockDoor.OPEN);
			int length = getSignalLength();

			world.setBlockState(pos, upperState.withProperty(BlockDoor.OPEN, !upperState.getValue(BlockDoor.OPEN)), 3);
			world.setBlockState(pos.down(), lowerState.withProperty(BlockDoor.OPEN, !lowerState.getValue(BlockDoor.OPEN)), 3);
			world.markBlockRangeForRenderUpdate(pos.down(), pos);
			world.playEvent(null, open ? 1005 : 1011, pos, 0);

			if(open && length > 0)
				world.scheduleUpdate(pos, SCContent.scannerDoor, length);

			if(open && sendMessage.get())
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.securitycraft:scannerDoorItem.name"), ClientUtils.localize("messages.securitycraft:retinalScanner.hello").replace("#", player.getName()), TextFormatting.GREEN);
		}
	}

	@Override
	public void onModuleInserted(ItemStack stack, EnumModuleType module)
	{
		super.onModuleInserted(stack, module);
		handleModule(stack, module, false);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, EnumModuleType module)
	{
		super.onModuleRemoved(stack, module);
		handleModule(stack, module, true);
	}

	private void handleModule(ItemStack stack, EnumModuleType module, boolean removed)
	{
		EnumDoorHalf myHalf = world.getBlockState(pos).getValue(BlockDoor.HALF);
		BlockPos otherPos;

		if(myHalf == EnumDoorHalf.UPPER)
			otherPos = getPos().down();
		else
			otherPos = getPos().up();

		IBlockState other = world.getBlockState(otherPos);

		if(other.getValue(BlockDoor.HALF) != myHalf)
		{
			TileEntity otherTe = world.getTileEntity(otherPos);

			if(otherTe instanceof TileEntityScannerDoor)
			{
				TileEntityScannerDoor otherDoorTe = (TileEntityScannerDoor)otherTe;

				if(!removed && !otherDoorTe.hasModule(module))
					otherDoorTe.insertModule(stack);
				else if(removed && otherDoorTe.hasModule(module))
					otherDoorTe.removeModule(module);
			}
		}
	}

	@Override
	protected void onLinkedBlockAction(EnumLinkedAction action, Object[] parameters, ArrayList<CustomizableSCTE> excludedTEs)
	{
		if(action == EnumLinkedAction.OPTION_CHANGED)
		{
			Option<?> option = (Option<?>)parameters[0];

			if(option.getName().equals(sendMessage.getName()))
				sendMessage.copy(option);
			else if(option.getName().equals(signalLength.getName()))
				signalLength.copy(option);
		}
	}

	@Override
	public int getViewCooldown()
	{
		return 30;
	}

	@Override
	public EnumModuleType[] acceptedModules()
	{
		return new EnumModuleType[]{EnumModuleType.WHITELIST};
	}

	@Override
	public Option<?>[] customOptions()
	{
		return new Option[]{ sendMessage, signalLength };
	}

	public int getSignalLength()
	{
		return signalLength.get();
	}
}
