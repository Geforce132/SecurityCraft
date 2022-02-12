package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.inventory.GenericBEMenu;
import net.geforcemods.securitycraft.misc.BlockEntityTracker;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BlockChangeDetectorBlockEntity extends DisguisableBlockEntity implements MenuProvider, ILockable, ITickingBlockEntity {
	private int range = 0;
	private DetectionMode mode = DetectionMode.BREAK;
	private boolean tracked = false;

	public BlockChangeDetectorBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.beTypeBlockChangeDetector, pos, state);
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state) {
		if (!tracked) {
			BlockEntityTracker.BLOCK_CHANGE_DETECTOR.track(this);
			tracked = true;
		}
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		BlockEntityTracker.BLOCK_CHANGE_DETECTOR.stopTracking(this);
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
		return new GenericBEMenu(SCContent.mTypeBlockChangeDetector, id, level, worldPosition);
	}

	@Override
	public Component getDisplayName() {
		return super.getDisplayName();
	}

	public void changeMode() {
		if (mode == DetectionMode.BREAK)
			mode = DetectionMode.PLACE;
		else
			mode = DetectionMode.BREAK;
	}

	public DetectionMode getMode() {
		return mode;
	}

	public void changeRange() {
		if (++range > 15)
			range = 0;
	}

	public int getRange() {
		return range;
	}

	public static enum DetectionMode {
		BREAK,
		PLACE;
	}
}
