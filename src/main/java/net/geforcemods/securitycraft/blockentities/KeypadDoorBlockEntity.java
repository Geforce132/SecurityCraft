package net.geforcemods.securitycraft.blockentities;

import java.util.UUID;
import java.util.function.Consumer;

import net.geforcemods.securitycraft.api.INameSetter;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.SendAllowlistMessageOption;
import net.geforcemods.securitycraft.api.Option.SendDenylistMessageOption;
import net.geforcemods.securitycraft.api.Option.SmartModuleCooldownOption;
import net.geforcemods.securitycraft.blocks.KeypadBlock;
import net.geforcemods.securitycraft.blocks.KeypadDoorBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoor.EnumDoorHalf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class KeypadDoorBlockEntity extends SpecialDoorBlockEntity implements IPasscodeProtected {
	private BooleanOption sendAllowlistMessage = new SendAllowlistMessageOption(false);
	private BooleanOption sendDenylistMessage = new SendDenylistMessageOption(true);
	private SmartModuleCooldownOption smartModuleCooldown = new SmartModuleCooldownOption(this::getPos);
	private long cooldownEnd = 0;
	private byte[] passcode;
	private UUID saltKey;

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		if (saltKey != null)
			tag.setUniqueId("saltKey", saltKey);

		if (passcode != null)
			tag.setString("passcode", PasscodeUtils.bytesToString(passcode));

		tag.setLong("cooldownLeft", getCooldownEnd() - System.currentTimeMillis());
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		loadSaltKey(tag);
		loadPasscode(tag);
		cooldownEnd = System.currentTimeMillis() + tag.getLong("cooldownLeft");

		if (tag.hasKey("sendMessage") && !tag.getBoolean("sendMessage")) {
			sendAllowlistMessage.setValue(false);
			sendDenylistMessage.setValue(false);
		}
	}

	@Override
	public void activate(EntityPlayer player) {
		if (!world.isRemote) {
			IBlockState state = world.getBlockState(pos);

			if (state.getBlock() instanceof KeypadDoorBlock) {
				//for some reason calling BlockKeypadDoor#activate if the block is the upper half does not work, so delegate opening to the lower half
				if (state.getValue(BlockDoor.HALF) == EnumDoorHalf.UPPER) {
					pos = pos.down();
					state = world.getBlockState(pos);
				}

				((KeypadDoorBlock) state.getBlock()).activate(state, world, pos, getSignalLength());
			}
		}
	}

	@Override
	public boolean shouldAttemptCodebreak(EntityPlayer player) {
		return !world.getBlockState(pos).getValue(KeypadBlock.POWERED) && IPasscodeProtected.super.shouldAttemptCodebreak(player);
	}

	@Override
	public byte[] getPasscode() {
		return passcode == null || passcode.length == 0 ? null : passcode;
	}

	@Override
	public void setPasscode(byte[] passcode) {
		this.passcode = passcode;
	}

	@Override
	public UUID getSaltKey() {
		return saltKey;
	}

	@Override
	public void setSaltKey(UUID saltKey) {
		this.saltKey = saltKey;
	}

	@Override
	public void startCooldown() {
		long start = System.currentTimeMillis();

		startCooldown(start);
		runForOtherHalf(otherHalf -> otherHalf.startCooldown(start));
	}

	public void startCooldown(long start) {
		if (!isOnCooldown()) {
			IBlockState state = world.getBlockState(pos);

			cooldownEnd = start + smartModuleCooldown.get() * 50;
			world.notifyBlockUpdate(pos, state, state, 3);
			markDirty();
		}
	}

	@Override
	public long getCooldownEnd() {
		return cooldownEnd;
	}

	@Override
	public boolean isOnCooldown() {
		return System.currentTimeMillis() < getCooldownEnd();
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.ALLOWLIST, ModuleType.DENYLIST, ModuleType.SMART, ModuleType.HARMING, ModuleType.DISGUISE
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				sendAllowlistMessage, sendDenylistMessage, signalLength, disabled, smartModuleCooldown
		};
	}

	public boolean sendsAllowlistMessage() {
		return sendAllowlistMessage.get();
	}

	public boolean sendsDenylistMessage() {
		return sendDenylistMessage.get();
	}

	@Override
	public int defaultSignalLength() {
		return 60;
	}

	@Override
	public void setCustomName(String customName) {
		super.setCustomName(customName);

		if (world.getBlockState(pos).getValue(BlockDoor.HALF) == EnumDoorHalf.LOWER)
			((INameSetter) world.getTileEntity(pos.up())).setCustomName(customName);
	}

	public void runForOtherHalf(Consumer<KeypadDoorBlockEntity> action) {
		if (world == null) //Happens when loading the BE, in that case running the same code for the other half is unnecessary
			return;

		TileEntity te = null;
		IBlockState state = world.getBlockState(pos);

		if (state.getValue(BlockDoor.HALF) == EnumDoorHalf.LOWER)
			te = world.getTileEntity(pos.up());
		else if (state.getValue(BlockDoor.HALF) == EnumDoorHalf.UPPER)
			te = world.getTileEntity(pos.down());

		if (te instanceof KeypadDoorBlockEntity)
			action.accept((KeypadDoorBlockEntity) te);
	}
}
