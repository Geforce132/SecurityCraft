package net.geforcemods.securitycraft.tileentity;

import java.util.ArrayList;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.LinkedAction;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.misc.CustomModules;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.text.TextFormatting;

public class ScannerDoorTileEntity extends CustomizableTileEntity
{
	private BooleanOption sendMessage = new BooleanOption("sendMessage", true);

	public ScannerDoorTileEntity()
	{
		super(SCContent.teTypeScannerDoor);
	}

	@Override
	public void entityViewed(LivingEntity entity)
	{
		BlockState upperState = world.getBlockState(pos);
		BlockState lowerState = world.getBlockState(pos.down());

		if(!world.isRemote && upperState.get(DoorBlock.HALF) == DoubleBlockHalf.UPPER)
		{
			if(!(entity instanceof PlayerEntity))
				return;

			PlayerEntity player = (PlayerEntity)entity;

			if(PlayerUtils.isPlayerMountedOnCamera(player))
				return;

			if(!getOwner().isOwner(player))
			{
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.scannerDoorItem.getTranslationKey()), ClientUtils.localize("messages.securitycraft:retinalScanner.notOwner").replace("#", getOwner().getName()), TextFormatting.RED);
				return;
			}

			boolean open = !BlockUtils.getBlockPropertyAsBoolean(world, pos.down(), DoorBlock.OPEN);

			world.setBlockState(pos, upperState.with(DoorBlock.OPEN, !upperState.get(DoorBlock.OPEN).booleanValue()), 3);
			world.setBlockState(pos.down(), lowerState.with(DoorBlock.OPEN, !lowerState.get(DoorBlock.OPEN).booleanValue()), 3);
			world.playEvent(null, open ? 1005 : 1011, pos, 0);

			if(open && sendMessage.asBoolean())
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.scannerDoorItem.getTranslationKey()), ClientUtils.localize("messages.securitycraft:retinalScanner.hello").replace("#", player.getName().getFormattedText()), TextFormatting.GREEN);
		}
	}

	@Override
	protected void onLinkedBlockAction(LinkedAction action, Object[] parameters, ArrayList<CustomizableTileEntity> excludedTEs)
	{
		if(action == LinkedAction.OPTION_CHANGED)
			sendMessage.copy((Option<?>)parameters[0]);
	}

	@Override
	public int getViewCooldown()
	{
		return 30;
	}

	@Override
	public CustomModules[] acceptedModules()
	{
		return new CustomModules[]{};
	}

	@Override
	public Option<?>[] customOptions()
	{
		return new Option[]{ sendMessage };
	}
}
