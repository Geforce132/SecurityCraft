package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public class TileEntityScannerDoor extends CustomizableSCTE
{
	public TileEntityScannerDoor()
	{
		super(SCContent.teTypeScannerDoor);
	}

	@Override
	public void entityViewed(LivingEntity entity)
	{
		BlockState upperState = world.getBlockState(pos);
		BlockState lowerState = world.getBlockState(pos.down());

		if(!world.isRemote && upperState.get(BlockDoor.HALF) == DoubleBlockHalf.UPPER)
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

			boolean open = !BlockUtils.getBlockPropertyAsBoolean(world, pos.down(), BlockDoor.OPEN);

			world.setBlockState(pos, upperState.with(BlockDoor.OPEN, !upperState.get(BlockDoor.OPEN).booleanValue()), 3);
			world.setBlockState(pos.down(), lowerState.with(BlockDoor.OPEN, !lowerState.get(BlockDoor.OPEN).booleanValue()), 3);
			world.markBlockRangeForRenderUpdate(pos.down(), pos);
			world.playEvent(null, open ? 1005 : 1011, pos, 0);
			((TileEntityOwnable)world.getTileEntity(pos)).getOwner().set(getOwner());
			((TileEntityOwnable)world.getTileEntity(pos.down())).getOwner().set(getOwner());

			if(open)
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.scannerDoorItem.getTranslationKey()), ClientUtils.localize("messages.securitycraft:retinalScanner.hello").replace("#", player.getName().getFormattedText()), TextFormatting.GREEN);
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
	public ITextComponent getCustomName()
	{
		return getCustomSCName();
	}

	@Override
	public boolean hasCustomName()
	{
		return hasCustomSCName();
	}
}
