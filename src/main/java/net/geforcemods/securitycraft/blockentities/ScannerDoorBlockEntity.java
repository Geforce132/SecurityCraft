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
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.Constants;

public class ScannerDoorBlockEntity extends SpecialDoorBlockEntity implements IViewActivated {
	private BooleanOption sendMessage = new BooleanOption("sendMessage", true);
	private DoubleOption maximumDistance = new DoubleOption(this::getPos, "maximumDistance", 5.0D, 0.1D, 25.0D, 0.1D) {
		@Override
		public String getKey(String denotation) {
			return "option.generic.viewActivated.maximumDistance";
		}
	};
	private int viewCooldown = 0;

	@Override
	public void update() {
		super.update();
		checkView(world, pos);
	}

	@Override
	public boolean onEntityViewed(EntityLivingBase entity, RayTraceResult rayTraceResult) {
		IBlockState upperState = world.getBlockState(pos);
		IBlockState lowerState = world.getBlockState(pos.down());

		if (upperState.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.UPPER && !Utils.isEntityInvisible(entity)) {
			EnumFacing.Axis facingAxis = ScannerDoorBlock.getFacingAxis(lowerState);

			if (!(entity instanceof EntityPlayer) || facingAxis != rayTraceResult.sideHit.getAxis())
				return false;

			EntityPlayer player = (EntityPlayer) entity;

			if (!isLocked() && !isDisabled()) {
				Owner viewingPlayer = new Owner(player);

				if (ConfigHandler.trickScannersWithPlayerHeads && player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == Items.SKULL)
					viewingPlayer = PlayerUtils.getSkullOwner(player);

				if (!isOwnedBy(viewingPlayer) && !isAllowed(viewingPlayer.getName())) {
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.retinalScanner), Utils.localize("messages.securitycraft:retinalScanner.notOwner", PlayerUtils.getOwnerComponent(getOwner())), TextFormatting.RED);
					return true;
				}

				boolean open = !lowerState.getValue(BlockDoor.OPEN);
				int length = getSignalLength();

				world.setBlockState(pos, upperState.withProperty(BlockDoor.OPEN, open), 3);
				world.setBlockState(pos.down(), lowerState.withProperty(BlockDoor.OPEN, open), 3);
				world.markBlockRangeForRenderUpdate(pos.down(), pos);
				world.playEvent(null, open ? Constants.WorldEvents.IRON_DOOR_OPEN_SOUND : Constants.WorldEvents.IRON_DOOR_CLOSE_SOUND, pos, 0);

				if (open && length > 0)
					world.scheduleUpdate(pos, SCContent.scannerDoor, length);

				if (open && sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:scannerDoorItem.name"), Utils.localize("messages.securitycraft:retinalScanner.hello", viewingPlayer.getName()), TextFormatting.GREEN);
			}
			else {
				if (isLocked() && sendsMessages())
					PlayerUtils.sendMessageToPlayer((EntityPlayer) entity, Utils.localize(SCContent.scannerDoor), Utils.localize("messages.securitycraft:sonic_security_system.locked", Utils.localize(SCContent.scannerDoor)), TextFormatting.DARK_RED, false);
				else if (isDisabled())
					player.sendStatusMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
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
