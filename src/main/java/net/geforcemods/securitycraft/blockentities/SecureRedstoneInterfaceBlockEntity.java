package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.SecureRedstoneInterfaceBlock;
import net.geforcemods.securitycraft.misc.BlockEntityTracker;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class SecureRedstoneInterfaceBlockEntity extends DisguisableBlockEntity implements ITickingBlockEntity {
	public final DisabledOption disabled = new DisabledOption(false);
	private boolean tracked = false, refreshed = false;
	private boolean sender = true;
	private int power = 0;
	private int frequency = 0;
	private int senderRange = 24;
	private boolean protectedSignal = false;
	private boolean sendExactPower = true;
	private boolean receiveInvertedPower = false;

	public SecureRedstoneInterfaceBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.SECURE_REDSTONE_INTERFACE_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state) {
		if (!tracked) {
			if (isSender())
				refreshPower();

			BlockEntityTracker.SECURE_REDSTONE_INTERFACE.track(this);
			tracked = true;
		}
		else if (!refreshed) {
			refreshed = true;

			if (!isSender())
				refreshPower();
		}
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		BlockEntityTracker.SECURE_REDSTONE_INTERFACE.stopTracking(this);
	}

	@Override
	public void onOwnerChanged(BlockState state, Level level, BlockPos pos, Player player, Owner oldOwner, Owner newOwner) {
		super.onOwnerChanged(state, level, pos, player, oldOwner, newOwner);

		if (isSender()) {
			int currentFrequency = getFrequency();
			int range = getSenderRange();

			tellSimilarReceiversToRefresh(oldOwner, currentFrequency, range);
		}
		else
			setPower(0);
	}

	@Override
	public boolean needsValidation() {
		return true;
	}

	@Override
	public void onValidate() {
		refreshPower();
	}

	@Override
	public <T> void onOptionChanged(Option<T> option) {
		if (option == disabled) {
			if (isDisabled())
				setPower(0);
			else
				refreshPower();
		}
	}

	@Override
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		super.loadAdditional(tag, lookupProvider);
		sender = tag.getBoolean("sender");
		power = tag.getInt("power");
		frequency = tag.getInt("frequency");
		senderRange = tag.getInt("sender_range");
		protectedSignal = tag.getBoolean("protected_signal");
		sendExactPower = tag.getBoolean("send_exact_power");
		receiveInvertedPower = tag.getBoolean("receive_inverted_power");
	}

	@Override
	public void saveAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		super.saveAdditional(tag, lookupProvider);
		tag.putBoolean("sender", sender);
		tag.putInt("power", power);
		tag.putInt("frequency", frequency);
		tag.putInt("sender_range", senderRange);
		tag.putBoolean("protected_signal", protectedSignal);
		tag.putBoolean("send_exact_power", sendExactPower);
		tag.putBoolean("receive_inverted_power", receiveInvertedPower);
	}

	public boolean isSender() {
		return sender;
	}

	public void setSender(boolean sender) {
		if (isSender() == sender || !getOwner().isValidated())
			return;

		this.sender = sender;

		if (!level.isClientSide) {
			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(SecureRedstoneInterfaceBlock.SENDER, sender));
			setChanged();
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);

			if (!isDisabled())
				tellSimilarReceiversToRefresh();

			BlockUtils.updateIndirectNeighbors(level, worldPosition, getBlockState().getBlock());
		}
	}

	public int getPower() {
		return power;
	}

	public void refreshPower() {
		refreshPower(getFrequency());
	}

	public void refreshPower(int frequency) {
		if (isDisabled() || !getOwner().isValidated())
			return;

		if (isSender()) {
			int bestSignal;

			if (isProtectedSignal())
				bestSignal = BlockUtils.hasActiveSCBlockNextTo(level, worldPosition) ? 15 : 0;
			else
				bestSignal = level.getBestNeighborSignal(worldPosition);

			if (sendsExactPower())
				setPower(bestSignal);
			else
				setPower(bestSignal > 0 ? 15 : 0);
		}
		else {
			int highestPower = 0;
			boolean protectedSignal = true;

			for (SecureRedstoneInterfaceBlockEntity be : BlockEntityTracker.SECURE_REDSTONE_INTERFACE.getBlockEntitiesInRange(level, worldPosition)) {
				if (!be.isDisabled() && be.isSender() && be.isOwnedBy(getOwner()) && be.isSameFrequency(frequency)) {
					int ownPower = be.getPower();

					if (ownPower > highestPower)
						highestPower = ownPower;

					if (!be.isProtectedSignal())
						protectedSignal = false;

					if (highestPower == 15 && !protectedSignal)
						break;
				}
			}

			if (receivesInvertedPower())
				highestPower = 15 - highestPower;

			setProtectedSignal(protectedSignal);
			setPower(highestPower);
		}
	}

	public void setPower(int power) {
		if (getPower() == power || !getOwner().isValidated())
			return;

		this.power = power;

		if (!level.isClientSide) {
			tellSimilarReceiversToRefresh(); //not restricted to sender only, as a receiver that was just changed from being a sender needs to refresh other receivers as well
			setChanged();
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
			BlockUtils.updateIndirectNeighbors(level, worldPosition, getBlockState().getBlock());
		}
	}

	public boolean isProtectedSignal() {
		return protectedSignal;
	}

	public void setProtectedSignal(boolean protectedSignal) {
		if (isProtectedSignal() == protectedSignal || !getOwner().isValidated())
			return;

		this.protectedSignal = protectedSignal;

		if (!level.isClientSide) {
			refreshPower();

			if (isSender())
				tellSimilarReceiversToRefresh();
			else
				BlockUtils.updateIndirectNeighbors(level, worldPosition, getBlockState().getBlock());

			setChanged();
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
		}
	}

	public boolean sendsExactPower() {
		return sendExactPower;
	}

	public void setSendExactPower(boolean sendExactPower) {
		if (sendsExactPower() == sendExactPower || !getOwner().isValidated())
			return;

		this.sendExactPower = sendExactPower;

		if (!level.isClientSide && isSender()) {
			refreshPower();
			setChanged();
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
			BlockUtils.updateIndirectNeighbors(level, worldPosition, getBlockState().getBlock());
		}
	}

	public boolean receivesInvertedPower() {
		return receiveInvertedPower;
	}

	public void setReceiveInvertedPower(boolean receiveInvertedPower) {
		if (receivesInvertedPower() == receiveInvertedPower || !getOwner().isValidated())
			return;

		this.receiveInvertedPower = receiveInvertedPower;

		if (!level.isClientSide && !isSender()) {
			refreshPower();
			setChanged();
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
			BlockUtils.updateIndirectNeighbors(level, worldPosition, getBlockState().getBlock());
		}
	}

	/**
	 * Tells receivers in range with the same owner and frequency of this one to refresh their power
	 */
	public void tellSimilarReceiversToRefresh() {
		tellSimilarReceiversToRefresh(getOwner(), getFrequency(), getSenderRange());
	}

	/**
	 * Tells receivers in range that have the same owner and frequency given to refresh their power
	 *
	 * @param owner The owner the receivers that should be refreshed belong to
	 * @param frequency The frequency that the receivers that should be refreshed need to have
	 * @param range The range around this block entity's position in which to update the receivers
	 */
	public void tellSimilarReceiversToRefresh(Owner owner, int frequency, int range) {
		if (getOwner().isValidated()) {
			for (SecureRedstoneInterfaceBlockEntity be : BlockEntityTracker.SECURE_REDSTONE_INTERFACE.getBlockEntitiesAround(level, worldPosition, range)) {
				if (!be.isSender() && be.isOwnedBy(owner) && be.isSameFrequency(frequency))
					be.refreshPower(frequency);
			}
		}
	}

	public void setFrequency(int frequency) {
		int oldFrequency = getFrequency();

		if (oldFrequency == frequency || !getOwner().isValidated())
			return;

		this.frequency = frequency;

		if (!level.isClientSide) {
			Owner owner = getOwner();
			int range = getSenderRange();

			if (isSender()) {
				tellSimilarReceiversToRefresh(owner, oldFrequency, range);
				tellSimilarReceiversToRefresh(owner, frequency, range);
			}
			else
				refreshPower();

			setChanged();
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
		}
	}

	public int getFrequency() {
		return frequency;
	}

	public boolean isSameFrequency(int frequency) {
		return getFrequency() == frequency;
	}

	public void setSenderRange(int senderRange) {
		senderRange = Math.clamp(senderRange, 1, 64);

		if (getSenderRange() == senderRange || !getOwner().isValidated())
			return;

		int oldRange = this.senderRange;

		this.senderRange = senderRange;

		if (!level.isClientSide && !isDisabled() && isSender()) {
			Owner owner = getOwner();
			int frequency = getFrequency();

			tellSimilarReceiversToRefresh(owner, frequency, Math.max(oldRange, senderRange));
			setChanged();
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
		}
	}

	public int getSenderRange() {
		return senderRange;
	}

	public boolean isDisabled() {
		return disabled.get();
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				disabled
		};
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.DISGUISE
		};
	}
}
