package net.geforcemods.securitycraft.blockentities;

import java.util.function.Predicate;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.blocks.RiftStabilizerBlock;
import net.geforcemods.securitycraft.misc.BlockEntityTracker;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class RiftStabilizerBlockEntity extends DisguisableBlockEntity implements ITickingBlockEntity {
	private final IntOption signalLength = new IntOption(this::getBlockPos, "signalLength", 60, 5, 400, 5, true); //20 seconds max
	private final IntOption range = new IntOption(this::getBlockPos, "range", 5, 1, 15, 1, true);
	private final DisabledOption disabled = new DisabledOption(false);
	private boolean tracked = false;

	public RiftStabilizerBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.RIFT_STABILIZER_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state) {
		if (!tracked) {
			BlockEntityTracker.RIFT_STABILIZER.track(this);
			tracked = true;
		}
	}

	@Override
	public void setRemoved() {
		super.setRemoved();

		// Stop tracking Rift Stabilizers when they are removed from the world
		BlockEntityTracker.RIFT_STABILIZER.stopTracking(this);
	}

	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleInserted(stack, module, toggled);

		RiftStabilizerBlockEntity connectedBlockEntity = RiftStabilizerBlock.getConnectedBlockEntity(level, worldPosition);
		Predicate<ModuleType> test = toggled ? connectedBlockEntity::isModuleEnabled : connectedBlockEntity::hasModule;

		if (connectedBlockEntity != null && !test.test(module))
			connectedBlockEntity.insertModule(stack, toggled);

		if (module == ModuleType.DISGUISE) {
			onInsertDisguiseModule(this, stack);

			if (connectedBlockEntity != null)
				onInsertDisguiseModule(connectedBlockEntity, stack);
		}
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleRemoved(stack, module, toggled);

		RiftStabilizerBlockEntity connectedBlockEntity = RiftStabilizerBlock.getConnectedBlockEntity(level, worldPosition);
		Predicate<ModuleType> test = toggled ? connectedBlockEntity::isModuleEnabled : connectedBlockEntity::hasModule;

		if (connectedBlockEntity != null && test.test(module))
			connectedBlockEntity.removeModule(module, toggled);

		else if (module == ModuleType.DISGUISE) {
			onRemoveDisguiseModule(this, stack);

			if (connectedBlockEntity != null)
				onRemoveDisguiseModule(connectedBlockEntity, stack);
		}
	}

	private void onInsertDisguiseModule(BlockEntity be, ItemStack stack) {
		if (!be.getLevel().isClientSide)
			be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), 3);
		else
			ClientHandler.putDisguisedBeRenderer(be, stack);
	}

	private void onRemoveDisguiseModule(BlockEntity be, ItemStack stack) {
		if (!be.getLevel().isClientSide)
			be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), 3);
		else
			ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.removeDelegateOf(be);
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.ALLOWLIST, ModuleType.DISGUISE, ModuleType.REDSTONE, ModuleType.HARMING
		};
	}

	@Override
	public void onOptionChanged(Option<?> option) {
		RiftStabilizerBlockEntity connectedScanner = RiftStabilizerBlock.getConnectedBlockEntity(level, worldPosition);

		if (option.getName().equals("signalLength")) {
			IntOption intOption = (IntOption) option;

			if (connectedScanner != null)
				connectedScanner.setSignalLength(intOption.get());
		}
		else if (option.getName().equals("range")) {
			IntOption intOption = (IntOption) option;

			if (connectedScanner != null)
				connectedScanner.setRange(intOption.get());
		}
		else if (option.getName().equals("disabled")) {
			BooleanOption bo = (BooleanOption) option;

			if (connectedScanner != null)
				connectedScanner.setDisabled(bo.get());
		}

		super.onOptionChanged(option);
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				signalLength, range, disabled
		};
	}

	@Override
	public void onOwnerChanged(BlockState state, Level level, BlockPos pos, Player player) {
		RiftStabilizerBlockEntity be = RiftStabilizerBlock.getConnectedBlockEntity(level, pos);

		if (be != null) {
			be.setOwner(getOwner().getUUID(), getOwner().getName());

			if (!level.isClientSide)
				level.getServer().getPlayerList().broadcastAll(be.getUpdatePacket());
		}

		super.onOwnerChanged(state, level, pos, player);
	}

	public void setSignalLength(int signalLength) {
		if (getSignalLength() != signalLength) {
			this.signalLength.setValue(signalLength);
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3); //sync option change to client
			setChanged();
		}
	}

	public int getSignalLength() {
		return signalLength.get();
	}

	public void setRange(int range) {
		if (getRange() != range) {
			this.range.setValue(range);
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3); //sync option change to client
			setChanged();
		}
	}

	public int getRange() {
		return range.get();
	}

	public void setDisabled(boolean disabled) {
		if (this.isDisabled() != disabled) {
			this.disabled.setValue(disabled);
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3); //sync option change to client
			setChanged();
		}
	}

	public boolean isDisabled() {
		return disabled.get();
	}
}
