package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.text.TextFormatting;

public class ScannerDoorTileEntity extends SpecialDoorTileEntity
{
	public ScannerDoorTileEntity()
	{
		super(SCContent.teTypeScannerDoor);
	}

	@Override
	public void entityViewed(LivingEntity entity)
	{
		BlockState upperState = level.getBlockState(worldPosition);
		BlockState lowerState = level.getBlockState(worldPosition.below());

		if(!level.isClientSide && upperState.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER && !EntityUtils.isInvisible(entity))
		{
			if(!(entity instanceof PlayerEntity))
				return;

			PlayerEntity player = (PlayerEntity)entity;

			if(PlayerUtils.isPlayerMountedOnCamera(player))
				return;

			if(!getOwner().isOwner(player) && !ModuleUtils.isAllowed(this, player))
			{
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SCANNER_DOOR_ITEM.get().getDescriptionId()), Utils.localize("messages.securitycraft:retinalScanner.notOwner", getOwner().getName()), TextFormatting.RED);
				return;
			}

			boolean open = !lowerState.getValue(DoorBlock.OPEN);
			int length = getSignalLength();

			level.setBlock(worldPosition, upperState.setValue(DoorBlock.OPEN, !upperState.getValue(DoorBlock.OPEN)), 3);
			level.setBlock(worldPosition.below(), lowerState.setValue(DoorBlock.OPEN, !lowerState.getValue(DoorBlock.OPEN)), 3);
			level.levelEvent(null, open ? 1005 : 1011, worldPosition, 0);

			if(open && length > 0)
				level.getBlockTicks().scheduleTick(worldPosition, SCContent.SCANNER_DOOR.get(), length);

			if(open && sendsMessages())
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SCANNER_DOOR_ITEM.get().getDescriptionId()), Utils.localize("messages.securitycraft:retinalScanner.hello", player.getName()), TextFormatting.GREEN);
		}
	}

	@Override
	public int getViewCooldown()
	{
		return 30;
	}

	@Override
	public ModuleType[] acceptedModules()
	{
		return new ModuleType[]{ModuleType.ALLOWLIST};
	}

	@Override
	public int defaultSignalLength()
	{
		return 0;
	}
}
