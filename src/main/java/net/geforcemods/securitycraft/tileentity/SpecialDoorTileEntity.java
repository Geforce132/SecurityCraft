package net.geforcemods.securitycraft.tileentity;

import java.util.ArrayList;

import net.geforcemods.securitycraft.api.LinkableTileEntity;
import net.geforcemods.securitycraft.api.LinkedAction;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;

public abstract class SpecialDoorTileEntity extends LinkableTileEntity
{
	private BooleanOption sendMessage = new BooleanOption("sendMessage", true);
	private IntOption signalLength = new IntOption(this::getPos, "signalLength", defaultSignalLength(), 0, 400, 5, true); //20 seconds max

	public SpecialDoorTileEntity(TileEntityType<?> type)
	{
		super(type);
	}

	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module)
	{
		super.onModuleInserted(stack, module);
		handleModule(stack, module, false);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module)
	{
		super.onModuleRemoved(stack, module);
		handleModule(stack, module, true);
	}

	private void handleModule(ItemStack stack, ModuleType module, boolean removed)
	{
		DoubleBlockHalf myHalf = getBlockState().get(DoorBlock.HALF);
		BlockPos otherPos;

		if(myHalf == DoubleBlockHalf.UPPER)
			otherPos = getPos().down();
		else
			otherPos = getPos().up();

		BlockState other = world.getBlockState(otherPos);

		if(other.get(DoorBlock.HALF) != myHalf)
		{
			TileEntity otherTe = world.getTileEntity(otherPos);

			if(otherTe instanceof SpecialDoorTileEntity)
			{
				SpecialDoorTileEntity otherDoorTe = (SpecialDoorTileEntity)otherTe;

				if(!removed && !otherDoorTe.hasModule(module))
					otherDoorTe.insertModule(stack);
				else if(removed && otherDoorTe.hasModule(module))
					otherDoorTe.removeModule(module);
			}
		}
	}

	@Override
	protected void onLinkedBlockAction(LinkedAction action, Object[] parameters, ArrayList<LinkableTileEntity> excludedTEs)
	{
		if(action == LinkedAction.OPTION_CHANGED)
		{
			Option<?> option = (Option<?>)parameters[0];

			if(option.getName().equals(sendMessage.getName()))
				sendMessage.copy(option);
			else if(option.getName().equals(signalLength.getName()))
				signalLength.copy(option);
		}
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
