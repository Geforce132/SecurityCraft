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
		BlockState upperState = world.getBlockState(pos);
		BlockState lowerState = world.getBlockState(pos.down());

		if(!world.isRemote && upperState.get(DoorBlock.HALF) == DoubleBlockHalf.UPPER && !EntityUtils.isInvisible(entity))
		{
			if(!(entity instanceof PlayerEntity))
				return;

			PlayerEntity player = (PlayerEntity)entity;

			if(PlayerUtils.isPlayerMountedOnCamera(player))
				return;

			if(!getOwner().isOwner(player) && (!hasModule(ModuleType.WHITELIST) || !ModuleUtils.getPlayersFromModule(getModule(ModuleType.WHITELIST)).contains(player.getName().getFormattedText().toLowerCase())))
			{
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SCANNER_DOOR_ITEM.get().getTranslationKey()), Utils.localize("messages.securitycraft:retinalScanner.notOwner", getOwner().getName()), TextFormatting.RED);
				return;
			}

			boolean open = !lowerState.get(DoorBlock.OPEN);
			int length = getSignalLength();

			world.setBlockState(pos, upperState.with(DoorBlock.OPEN, !upperState.get(DoorBlock.OPEN)), 3);
			world.setBlockState(pos.down(), lowerState.with(DoorBlock.OPEN, !lowerState.get(DoorBlock.OPEN)), 3);
			world.playEvent(null, open ? 1005 : 1011, pos, 0);

			if(open && length > 0)
				world.getPendingBlockTicks().scheduleTick(pos, SCContent.SCANNER_DOOR.get(), length);

			if(open && sendsMessages())
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SCANNER_DOOR_ITEM.get().getTranslationKey()), Utils.localize("messages.securitycraft:retinalScanner.hello", player.getName()), TextFormatting.GREEN);
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
		return new ModuleType[]{ModuleType.WHITELIST};
	}

	@Override
	public int defaultSignalLength()
	{
		return 0;
	}
}
