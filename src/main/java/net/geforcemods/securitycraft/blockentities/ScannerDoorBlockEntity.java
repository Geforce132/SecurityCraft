package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IViewActivated;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.DoubleOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.ScannerDoorBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
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
import net.minecraftforge.common.util.Constants;

public class ScannerDoorBlockEntity extends SpecialDoorBlockEntity implements IViewActivated {
	private BooleanOption sendMessage = new BooleanOption("sendMessage", true);
	private DoubleOption maximumDistance = new DoubleOption(this::getBlockPos, "maximumDistance", 5.0D, 0.1D, 25.0D, 0.1D) {
		@Override
		public String getKey(String denotation) {
			return "option.generic.viewActivated.maximumDistance";
		}
	};
	private int viewCooldown = 0;

	public ScannerDoorBlockEntity() {
		super(SCContent.SCANNER_DOOR_BLOCK_ENTITY.get());
	}

	@Override
	public void tick() {
		super.tick();
		checkView(level, worldPosition);
	}

	@Override
	public boolean onEntityViewed(LivingEntity entity, BlockRayTraceResult hitResult) {
		BlockState upperState = level.getBlockState(worldPosition);
		BlockState lowerState = level.getBlockState(worldPosition.below());
		Direction.Axis facingAxis = ScannerDoorBlock.getFacingAxis(upperState);

		if (upperState.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER && !Utils.isEntityInvisible(entity)) {
			if (!(entity instanceof PlayerEntity) || facingAxis != hitResult.getDirection().getAxis())
				return false;

			PlayerEntity player = (PlayerEntity) entity;

			if (!isLocked() && !isDisabled()) {
				Owner viewingPlayer = new Owner(player);

				if (ConfigHandler.SERVER.trickScannersWithPlayerHeads.get() && player.getItemBySlot(EquipmentSlotType.HEAD).getItem() == Items.PLAYER_HEAD)
					viewingPlayer = PlayerUtils.getSkullOwner(player);

				if (!isOwnedBy(viewingPlayer) && !isAllowed(viewingPlayer.getName())) {
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.RETINAL_SCANNER.get().getDescriptionId()), Utils.localize("messages.securitycraft:retinalScanner.notOwner", PlayerUtils.getOwnerComponent(getOwner())), TextFormatting.RED);
					return true;
				}

				boolean open = !lowerState.getValue(DoorBlock.OPEN);
				int length = getSignalLength();

				level.setBlock(worldPosition, upperState.setValue(DoorBlock.OPEN, !upperState.getValue(DoorBlock.OPEN)), 3);
				level.setBlock(worldPosition.below(), lowerState.setValue(DoorBlock.OPEN, !lowerState.getValue(DoorBlock.OPEN)), 3);
				level.levelEvent(null, open ? Constants.WorldEvents.IRON_DOOR_OPEN_SOUND : Constants.WorldEvents.IRON_DOOR_CLOSE_SOUND, worldPosition, 0);

				if (open && length > 0)
					level.getBlockTicks().scheduleTick(worldPosition, SCContent.SCANNER_DOOR.get(), length);

				if (open && sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SCANNER_DOOR_ITEM.get().getDescriptionId()), Utils.localize("messages.securitycraft:retinalScanner.hello", viewingPlayer.getName()), TextFormatting.GREEN);
			}
			else {
				if (isLocked() && sendsMessages()) {
					TranslationTextComponent blockName = Utils.localize(SCContent.SCANNER_DOOR_ITEM.get().getDescriptionId());

					PlayerUtils.sendMessageToPlayer(player, blockName, Utils.localize("messages.securitycraft:sonic_security_system.locked", blockName), TextFormatting.DARK_RED, false);
				}
				else if (isDisabled())
					player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
			}

			return true;
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
	public Option<?>[] customOptions() {
		return new Option[] {
				sendMessage, signalLength, disabled, maximumDistance
		};
	}

	public boolean sendsMessages() {
		return sendMessage.get();
	}

	@Override
	public int defaultSignalLength() {
		return 0;
	}

	@Override
	public double getMaximumDistance() {
		return maximumDistance.get();
	}
}
