package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.ProjectorBlockEntity;
import net.geforcemods.securitycraft.network.server.SyncProjector;
import net.geforcemods.securitycraft.util.StandingOrWallType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;

public class ProjectorMenu extends StateSelectorAccessMenu {
	public static final int SIZE = 1;
	public final ProjectorBlockEntity be;
	private ContainerLevelAccess worldPosCallable;
	private Slot projectedBlockSlot;

	public ProjectorMenu(int windowId, Level level, BlockPos pos, Inventory inventory) {
		super(SCContent.PROJECTOR_MENU.get(), windowId);

		be = (ProjectorBlockEntity) level.getBlockEntity(pos);
		worldPosCallable = ContainerLevelAccess.create(level, pos);

		// A custom slot that prevents non-Block items from being inserted into the projector
		projectedBlockSlot = addSlot(new Slot(new BlockEntityInventoryWrapper<>(be, this), 36, 80, 23) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return stack.getItem() instanceof BlockItem;
			}

			@Override
			public int getMaxStackSize() {
				return 1;
			}
		});

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; ++x) {
				addSlot(new Slot(inventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18 + 69));
			}
		}

		for (int x = 0; x < 9; x++) {
			addSlot(new Slot(inventory, x, 8 + x * 18, 142 + 69));
		}
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot slot = slots.get(index);

		if (slot.hasItem()) {
			ItemStack slotStack = slot.getItem();

			slotStackCopy = slotStack.copy();

			if (index < 1) {
				if (!moveItemStackTo(slotStack, 1, 37, true))
					return ItemStack.EMPTY;

				slot.onQuickCraft(slotStack, slotStackCopy);
			}
			else if (!moveItemStackTo(slotStack, 0, 1, false))
				return ItemStack.EMPTY;

			if (slotStack.getCount() == 0)
				slot.set(ItemStack.EMPTY);
			else
				slot.setChanged();

			if (slotStack.getCount() == slotStackCopy.getCount())
				return ItemStack.EMPTY;

			slot.onTake(player, slotStack);
			broadcastChanges();
		}

		return slotStackCopy;
	}

	@Override
	public void onStateChange(BlockState state) {
		be.setProjectedState(state);
		broadcastChanges();

		if (be.getLevel().isClientSide)
			PacketDistributor.sendToServer(new SyncProjector(be.getBlockPos(), state));
	}

	@Override
	public boolean stillValid(Player player) {
		return stillValid(worldPosCallable, player, SCContent.PROJECTOR.get());
	}

	@Override
	public ItemStack getStateStack() {
		return projectedBlockSlot.getItem();
	}

	@Override
	public BlockState getSavedState() {
		return be.getProjectedState();
	}

	@Override
	public StandingOrWallType getStandingOrWallType() {
		return be.getStandingOrWallType();
	}
}
