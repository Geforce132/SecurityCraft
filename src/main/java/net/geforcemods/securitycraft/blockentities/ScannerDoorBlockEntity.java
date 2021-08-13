package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class ScannerDoorBlockEntity extends SpecialDoorBlockEntity
{
	public ScannerDoorBlockEntity(BlockPos pos, BlockState state)
	{
		super(SCContent.teTypeScannerDoor, pos, state);
	}

	@Override
	public void entityViewed(LivingEntity entity)
	{
		BlockState upperState = level.getBlockState(worldPosition);
		BlockState lowerState = level.getBlockState(worldPosition.below());

		if(!level.isClientSide && upperState.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER && !EntityUtils.isInvisible(entity))
		{
			if(!(entity instanceof Player player))
				return;

			if(PlayerUtils.isPlayerMountedOnCamera(player))
				return;

			if(!getOwner().isOwner(player) && !ModuleUtils.isAllowed(this, player))
			{
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SCANNER_DOOR_ITEM.get().getDescriptionId()), Utils.localize("messages.securitycraft:retinalScanner.notOwner", getOwner().getName()), ChatFormatting.RED);
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
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SCANNER_DOOR_ITEM.get().getDescriptionId()), Utils.localize("messages.securitycraft:retinalScanner.hello", player.getName()), ChatFormatting.GREEN);
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
