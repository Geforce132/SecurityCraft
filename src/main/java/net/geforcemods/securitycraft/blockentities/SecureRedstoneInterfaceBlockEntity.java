package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.blocks.SecureRedstoneInterfaceBlock;
import net.geforcemods.securitycraft.misc.BlockEntityTracker;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

// TODO: option translations
// TODO: module translations
// TODO: disabled option
public class SecureRedstoneInterfaceBlockEntity extends DisguisableBlockEntity implements ITickingBlockEntity {
	public final DisabledOption disabled = new DisabledOption(false);
	//TODO: Change this to receiver-only setting
	//private final IntOption range = new IntOption("range", 16, 1, 64, 1);
	private boolean tracked = false;
	private boolean sender = true;
	private int power = 0;

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
	public void setRemoved() {
		super.setRemoved();
		BlockEntityTracker.SECURE_REDSTONE_INTERFACE.stopTracking(this);
	}

	@Override
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		super.loadAdditional(tag, lookupProvider);
		sender = tag.getBoolean("sender");
		power = tag.getInt("power");
	}

	@Override
	public void saveAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		super.saveAdditional(tag, lookupProvider);
		tag.putBoolean("sender", sender);
		tag.putInt("power", power);
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
			tellReceiversInRangeToRefresh();
		}
	}

	public int getPower() {
		return power;
	}

	public void refreshPower() {
		if (isSender())
			setPower(level.getBestNeighborSignal(worldPosition));
		else {
			int highestPower = 0;

			for (SecureRedstoneInterfaceBlockEntity be : BlockEntityTracker.SECURE_REDSTONE_INTERFACE.getBlockEntitiesInRange(level, worldPosition)) {
				if (be.isSender()) {
					int ownPower = be.getPower();

					if (ownPower > highestPower)
						highestPower = ownPower;

					if (highestPower == 15)
						break;
				}
			}

			setPower(highestPower);
		}
	}

	public void setPower(int power) {
		if (getPower() == power)
			return;

		this.power = power;

		if (!level.isClientSide) {
			tellReceiversInRangeToRefresh();
			setChanged();
			BlockUtils.updateIndirectNeighbors(level, worldPosition, getBlockState().getBlock());
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
		}
	}

	public void tellReceiversInRangeToRefresh() {
		for (SecureRedstoneInterfaceBlockEntity be : BlockEntityTracker.SECURE_REDSTONE_INTERFACE.getBlockEntitiesInRange(level, worldPosition)) {
			if (!be.isSender())
				be.refreshPower();
		}
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
