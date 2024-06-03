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

// TODO: option translations
// TODO: module translations
public class SecureRedstoneInterfaceBlockEntity extends DisguisableBlockEntity implements ITickingBlockEntity {
	public final DisabledOption disabled = new DisabledOption(false);
	private boolean tracked = false;
	private boolean sender = true;
	private int power = 0;
	private int frequency = 0;
	private int senderRange = 24;
	private boolean sendExactPower = true;
	private boolean receiveInvertedPower = false;

	public SecureRedstoneInterfaceBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.SECURE_REDSTONE_INTERFACE_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state) {
		if (!tracked) {
			refreshPower();
			BlockEntityTracker.SECURE_REDSTONE_INTERFACE.track(this);
			tracked = true;
		}
	}

	@Override
	public void onOwnerChanged(BlockState state, Level level, BlockPos pos, Player player, Owner oldOwner, Owner newOwner) {
		super.onOwnerChanged(state, level, pos, player, oldOwner, newOwner);

		if (isSender()) {
			int currentFrequency = getFrequency();
			int range = getSenderRange();

			tellSimilarReceiversToRefresh(oldOwner, currentFrequency, range);
			tellSimilarReceiversToRefresh(newOwner, currentFrequency, range);
		}
		else
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
	public void setRemoved() {
		super.setRemoved();
		BlockEntityTracker.SECURE_REDSTONE_INTERFACE.stopTracking(this);
	}

	@Override
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		super.loadAdditional(tag, lookupProvider);
		sender = tag.getBoolean("sender");
		power = tag.getInt("power");
		frequency = tag.getInt("frequency");
		sendExactPower = tag.getBoolean("send_exact_power");
		receiveInvertedPower = tag.getBoolean("receive_inverted_power");
		senderRange = tag.getInt("sender_range");
	}

	@Override
	public void saveAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		super.saveAdditional(tag, lookupProvider);
		tag.putBoolean("sender", sender);
		tag.putInt("power", power);
		tag.putInt("frequency", frequency);
		tag.putBoolean("send_exact_power", sendExactPower);
		tag.putBoolean("receive_inverted_power", receiveInvertedPower);
		tag.putInt("sender_range", senderRange);
	}

	public boolean isSender() {
		return sender;
	}

	public void setSender(boolean sender) {
		if (isSender() == sender)
			return;

		this.sender = sender;

		if (!level.isClientSide) {
			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(SecureRedstoneInterfaceBlock.SENDER, sender));

			if (!isDisabled())
				tellSimilarReceiversToRefresh();
		}
	}

	public int getPower() {
		return power;
	}

	public void refreshPower() {
		refreshPower(getFrequency());
	}

	public void refreshPower(int frequency) {
		if (isDisabled())
			return;

		if (isSender()) {
			int bestSignal = level.getBestNeighborSignal(worldPosition);

			if (sendsExactPower())
				setPower(bestSignal);
			else
				setPower(bestSignal > 0 ? 15 : 0);
		}
		else {
			int highestPower = 0;

			for (SecureRedstoneInterfaceBlockEntity be : BlockEntityTracker.SECURE_REDSTONE_INTERFACE.getBlockEntitiesInRange(level, worldPosition)) {
				if (!be.isDisabled() && be.isSender() && be.isOwnedBy(getOwner()) && be.isSameFrequency(frequency)) {
					int ownPower = be.getPower();

					if (ownPower > highestPower)
						highestPower = ownPower;

					if (highestPower == 15)
						break;
				}
			}

			if (receivesInvertedPower())
				highestPower = 15 - highestPower;

			setPower(highestPower);
		}
	}

	public void setPower(int power) {
		if (getPower() == power)
			return;

		this.power = power;

		if (!level.isClientSide) {
			tellSimilarReceiversToRefresh(); //not restricted to sender only, as a receiver that was just changed from being a sender needs to refresh other receivers as well
			setChanged();
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
			BlockUtils.updateIndirectNeighbors(level, worldPosition, getBlockState().getBlock());
		}
	}

	public boolean sendsExactPower() {
		return sendExactPower;
	}

	public void setSendExactPower(boolean sendExactPower) {
		if (sendsExactPower() == sendExactPower)
			return;

		this.sendExactPower = sendExactPower;

		if (!level.isClientSide) {
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
		if (receivesInvertedPower() == receiveInvertedPower)
			return;

		this.receiveInvertedPower = receiveInvertedPower;

		if (!level.isClientSide) {
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
	 */
	public void tellSimilarReceiversToRefresh(Owner owner, int frequency, int range) {
		for (SecureRedstoneInterfaceBlockEntity be : BlockEntityTracker.SECURE_REDSTONE_INTERFACE.getBlockEntitiesAround(level, worldPosition, range)) {
			if (!be.isSender() && be.isOwnedBy(owner) && be.isSameFrequency(frequency))
				be.refreshPower(frequency);
		}
	}

	public void setFrequency(int frequency) {
		int oldFrequency = getFrequency();

		if (oldFrequency == frequency)
			return;

		this.frequency = frequency;

		if (!level.isClientSide) {
			Owner owner = getOwner();
			int range = getSenderRange();

			tellSimilarReceiversToRefresh(owner, oldFrequency, range);
			tellSimilarReceiversToRefresh(owner, frequency, range);
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

		if (getSenderRange() == senderRange)
			return;

		int oldRange = this.senderRange;

		this.senderRange = senderRange;

		if (!level.isClientSide && !isDisabled()) {
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
