package net.geforcemods.securitycraft.blockentity;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.IViewActivated;
import net.geforcemods.securitycraft.blocks.ScannerDoorBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Items;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class ScannerDoorBlockEntity extends SpecialDoorBlockEntity implements IViewActivated, ILockable {
	private int viewCooldown = 0;

	public ScannerDoorBlockEntity() {
		super(SCContent.beTypeScannerDoor);
	}

	@Override
	public void tick() {
		super.tick();
		checkView(level, worldPosition);
	}

	@Override
	public boolean onEntityViewed(LivingEntity entity, BlockRayTraceResult rayTraceResult) {
		BlockState upperState = level.getBlockState(worldPosition);
		BlockState lowerState = level.getBlockState(worldPosition.below());
		Direction.Axis facingAxis = ScannerDoorBlock.getFacingAxis(upperState);

		if (upperState.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER && !EntityUtils.isInvisible(entity)) {
			if (!(entity instanceof PlayerEntity) || facingAxis != rayTraceResult.getDirection().getAxis())
				return false;

			PlayerEntity player = (PlayerEntity) entity;

			if (!isLocked()) {
				String name = entity.getName().getString();

				if (ConfigHandler.SERVER.trickScannersWithPlayerHeads.get() && player.getItemBySlot(EquipmentSlotType.HEAD).getItem() == Items.PLAYER_HEAD)
					name = PlayerUtils.getNameOfSkull(player);

				if (name == null || (!getOwner().getName().equals(name) && !ModuleUtils.isAllowed(this, name))) {
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.RETINAL_SCANNER.get().getDescriptionId()), Utils.localize("messages.securitycraft:retinalScanner.notOwner", PlayerUtils.getOwnerComponent(getOwner().getName())), TextFormatting.RED);
					return true;
				}

				boolean open = !lowerState.getValue(DoorBlock.OPEN);
				int length = getSignalLength();

				level.setBlock(worldPosition, upperState.setValue(DoorBlock.OPEN, !upperState.getValue(DoorBlock.OPEN)), 3);
				level.setBlock(worldPosition.below(), lowerState.setValue(DoorBlock.OPEN, !lowerState.getValue(DoorBlock.OPEN)), 3);
				level.levelEvent(null, open ? 1005 : 1011, worldPosition, 0);

				if (open && length > 0)
					level.getBlockTicks().scheduleTick(worldPosition, SCContent.SCANNER_DOOR.get(), length);

				if (open && sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SCANNER_DOOR_ITEM.get().getDescriptionId()), Utils.localize("messages.securitycraft:retinalScanner.hello", name), TextFormatting.GREEN);

				return true;
			}
			else if (sendsMessages()) {
				TranslationTextComponent blockName = Utils.localize(SCContent.SCANNER_DOOR_ITEM.get().getDescriptionId());

				PlayerUtils.sendMessageToPlayer(player, blockName, Utils.localize("messages.securitycraft:sonic_security_system.locked", blockName), TextFormatting.DARK_RED, false);
				return true;
			}
		}

		return false;
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
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.ALLOWLIST
		};
	}

	@Override
	public int defaultSignalLength() {
		return 0;
	}
}
