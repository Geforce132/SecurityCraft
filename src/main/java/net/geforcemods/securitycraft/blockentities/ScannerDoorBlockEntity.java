package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.IViewActivated;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class ScannerDoorBlockEntity extends SpecialDoorBlockEntity implements IViewActivated, ITickingBlockEntity, ILockable
{
	private int viewCooldown = 0;

	public ScannerDoorBlockEntity(BlockPos pos, BlockState state)
	{
		super(SCContent.beTypeScannerDoor, pos, state);
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state) {
		checkView(level, pos);
	}

	@Override
	public void onEntityViewed(LivingEntity entity)
	{
		BlockState upperState = level.getBlockState(worldPosition);
		BlockState lowerState = level.getBlockState(worldPosition.below());

		if(!level.isClientSide && upperState.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER && !EntityUtils.isInvisible(entity))
		{
			if(!(entity instanceof Player player))
				return;

			if (!isLocked()) {
				String name = player.getName().getString();

				if (ConfigHandler.SERVER.trickScannersWithPlayerHeads.get() && player.getItemBySlot(EquipmentSlot.HEAD).getItem() == Items.PLAYER_HEAD)
					name = PlayerUtils.getNameOfSkull(player);

				if (name == null || (!getOwner().getName().equals(name) && !ModuleUtils.isAllowed(this, name))) {
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SCANNER_DOOR_ITEM.get().getDescriptionId()), Utils.localize("messages.securitycraft:retinalScanner.notOwner", PlayerUtils.getOwnerComponent(getOwner().getName())), ChatFormatting.RED);
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
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SCANNER_DOOR_ITEM.get().getDescriptionId()), Utils.localize("messages.securitycraft:retinalScanner.hello", name), ChatFormatting.GREEN);
			}
			else if (sendsMessages()) {
				TranslatableComponent blockName = Utils.localize(SCContent.SCANNER_DOOR_ITEM.get().getDescriptionId());

				PlayerUtils.sendMessageToPlayer(player, blockName, Utils.localize("messages.securitycraft:sonic_security_system.locked", blockName), ChatFormatting.DARK_RED, false);
			}
		}
	}

	@Override
	public int getViewCooldown() {
		return viewCooldown;
	}

	@Override
	public void setViewCooldown(int viewCooldown) {
		this.viewCooldown = viewCooldown;
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
