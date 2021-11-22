package net.geforcemods.securitycraft.blockentities;

import java.util.ArrayList;

import net.geforcemods.securitycraft.api.LinkableBlockEntity;
import net.geforcemods.securitycraft.api.LinkedAction;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public abstract class SpecialDoorBlockEntity extends LinkableBlockEntity
{
	private BooleanOption sendMessage = new BooleanOption("sendMessage", true);
	private IntOption signalLength = new IntOption(this::getBlockPos, "signalLength", defaultSignalLength(), 0, 400, 5, true); //20 seconds max

	public SpecialDoorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
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
		DoubleBlockHalf myHalf = getBlockState().getValue(DoorBlock.HALF);
		BlockPos otherPos;

		if(myHalf == DoubleBlockHalf.UPPER)
			otherPos = getBlockPos().below();
		else
			otherPos = getBlockPos().above();

		BlockState other = level.getBlockState(otherPos);

		if(other.getValue(DoorBlock.HALF) != myHalf)
		{
			BlockEntity otherTe = level.getBlockEntity(otherPos);

			if(otherTe instanceof SpecialDoorBlockEntity otherDoorTe)
			{
				if(!removed && !otherDoorTe.hasModule(module))
					otherDoorTe.insertModule(stack);
				else if(removed && otherDoorTe.hasModule(module))
					otherDoorTe.removeModule(module);
			}
		}
	}

	@Override
	protected void onLinkedBlockAction(LinkedAction action, Object[] parameters, ArrayList<LinkableBlockEntity> excludedTEs)
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
