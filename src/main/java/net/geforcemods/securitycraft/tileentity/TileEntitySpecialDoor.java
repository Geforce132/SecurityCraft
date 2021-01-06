package net.geforcemods.securitycraft.tileentity;

import java.util.ArrayList;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.EnumLinkedAction;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionBoolean;
import net.geforcemods.securitycraft.api.Option.OptionInt;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoor.EnumDoorHalf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public abstract class TileEntitySpecialDoor extends CustomizableSCTE
{
	private OptionBoolean sendMessage = new OptionBoolean("sendMessage", true);
	private OptionInt signalLength = new OptionInt(this::getPos, "signalLength", defaultSignalLength(), 0, 400, 5, true); //20 seconds max

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

			if(otherTe instanceof TileEntitySpecialDoor)
			{
				TileEntitySpecialDoor otherDoorTe = (TileEntitySpecialDoor)otherTe;

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
	public EnumModuleType[] acceptedModules()
	{
		return new EnumModuleType[]{EnumModuleType.WHITELIST};
	}

	@Override
	public Option<?>[] customOptions()
	{
		return new Option[]{ sendMessage, signalLength };
	}

	public boolean sendsMessages()
	{
		return sendMessage.get();
	}

	public int getSignalLength()
	{
		return signalLength.get();
	}

	public abstract int defaultSignalLength();
}
