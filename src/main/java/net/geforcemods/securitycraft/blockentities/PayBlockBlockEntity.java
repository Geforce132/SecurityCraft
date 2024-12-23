package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.api.Option.SignalLengthOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.PayBlock;
import net.geforcemods.securitycraft.inventory.InsertOnlySidedInvWrapper;
import net.geforcemods.securitycraft.inventory.PayBlockMenu;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.NonNullList;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;

public class PayBlockBlockEntity extends DisguisableBlockEntity implements WorldlyContainer, MenuProvider {
	private static final int[] SLOTS_FOR_UP_SIDES = new int[] {12, 13, 14, 15, 16, 17, 18, 19};
	private static final int[] SLOTS_FOR_DOWN = new int[] {4, 5, 6, 7, 8, 9, 10, 11};
	private IntOption signalLength = new SignalLengthOption(60);
	private final DisabledOption disabled = new DisabledOption(false);
	private final NonNullList<ItemStack> inventoryContents = NonNullList.<ItemStack>withSize(20, ItemStack.EMPTY); //2 for payment reference, 2 for reward reference, 8 for payment storage, 8 for reward storage

	public PayBlockBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.PAY_BLOCK_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public void loadAdditional(CompoundTag tag, Provider lookupProvider) {
		super.loadAdditional(tag, lookupProvider);

		ContainerHelper.loadAllItems(tag, inventoryContents, lookupProvider);
	}

	@Override
	public void saveAdditional(CompoundTag tag, Provider lookupProvider) {
		super.saveAdditional(tag, lookupProvider);

		ContainerHelper.saveAllItems(tag, inventoryContents, lookupProvider);
	}

	public void doTransaction(Player player, int requestedTransactionAmount) {
		if (player instanceof ServerPlayer serverPlayer && serverPlayer.containerMenu instanceof PayBlockMenu menu) {
			int signalLength = this.signalLength.get();
			boolean hasSmartModule = isModuleEnabled(ModuleType.SMART);
			int transactions = Math.min(menu.inputLimitedTransactions, requestedTransactionAmount);

			if (transactions == 0)
				return;

			for (int i = 0; i < 2; i++) {
				ItemStack inputItemStack = getItem(i);

				if (inputItemStack.isEmpty())
					continue;

				int inputItemCount = inputItemStack.getCount();
				int consumingInputItems = transactions * inputItemCount;

				BlockUtils.checkInventoryForItem(menu.paymentInput.items, inputItemStack, consumingInputItems, hasSmartModule, true, stack -> handleConsumedPaymentItem(stack, menu), menu.paymentInput::setItem);
			}

			if (menu.givesOutRewardItems) {
				for (int i = 0; i < 2; i++) {
					ItemStack rewardStack = getItem(2 + i);

					if (rewardStack.isEmpty())
						continue;

					int rewardItemCount = rewardStack.getCount();
					int totalReceivedRewardItems = transactions * rewardItemCount;

					BlockUtils.checkInventoryForItem(inventoryContents, 12, 19, rewardStack, totalReceivedRewardItems, hasSmartModule, true, stack -> DefaultDispenseItemBehavior.spawnItem(level, stack, 2, Direction.DOWN, Vec3.atCenterOf(getBlockPos()).relative(blockState.getValue(PayBlock.FACING), 0.7)), inventoryContents::set);
				}
			}

			level.setBlockAndUpdate(worldPosition, blockState.cycle(PayBlock.POWERED));
			BlockUtils.updateIndirectNeighbors(level, worldPosition, SCContent.PAY_BLOCK.get());

			if (signalLength > 0)
				level.scheduleTick(worldPosition, SCContent.PAY_BLOCK.get(), signalLength);
		}
	}

	private void handleConsumedPaymentItem(ItemStack paymentStack, PayBlockMenu menu) {
		System.out.println("paid: " + paymentStack); //TODO remove the printlns

		if (isModuleEnabled(ModuleType.STORAGE)) {
			menu.moveItemStackTo(paymentStack, 9, 17, false);
			System.out.println("left: " + paymentStack);
		}

		if (!paymentStack.isEmpty())
			DefaultDispenseItemBehavior.spawnItem(level, paymentStack, 0, Direction.DOWN, Vec3.atCenterOf(getBlockPos()).relative(blockState.getValue(PayBlock.FACING).getOpposite(), 0.7));
	}

	public NonNullList<ItemStack> getContents() {
		return inventoryContents;
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.ALLOWLIST, ModuleType.DENYLIST, ModuleType.STORAGE, ModuleType.SMART
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				signalLength, disabled
		};
	}

	public void setSignalLength(int signalLength) {
		if (getSignalLength() != signalLength) {
			this.signalLength.setValue(signalLength);
			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(BlockStateProperties.POWERED, false));
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3); //sync option change to client
			setChanged();
		}
	}

	public int getSignalLength() {
		return signalLength.get();
	}

	public void setDisabled(boolean disabled) {
		if (isDisabled() != disabled) {
			this.disabled.setValue(disabled);
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3); //sync option change to client
			setChanged();
		}
	}

	public boolean isDisabled() {
		return disabled.get();
	}

	@Override
	public int getContainerSize() {
		return inventoryContents.size();
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		if (!inventoryContents.get(index).isEmpty()) {
			ItemStack stack;

			if (inventoryContents.get(index).getCount() <= count) {
				stack = inventoryContents.get(index);
				inventoryContents.set(index, ItemStack.EMPTY);
				setChanged();
				return stack;
			}
			else {
				stack = inventoryContents.get(index).split(count);

				if (inventoryContents.get(index).getCount() == 0)
					inventoryContents.set(index, ItemStack.EMPTY);

				setChanged();
				return stack;
			}
		}
		else
			return ItemStack.EMPTY;
	}

	@Override
	public boolean enableHack() {
		return true;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return slot >= 100 ? getModuleInSlot(slot) : inventoryContents.get(slot);
	}

	@Override
	public ItemStack getItem(int slot) {
		return getStackInSlot(slot);
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		inventoryContents.set(index, stack);

		if (!stack.isEmpty() && stack.getCount() > getMaxStackSize())
			stack.setCount(getMaxStackSize());

		setChanged();
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		return true;
	}

	public static IItemHandler getCapability(PayBlockBlockEntity be, Direction side) {
		return BlockUtils.isAllowedToExtractFromProtectedObject(side, be) ? new SidedInvWrapper(be, side) : new InsertOnlySidedInvWrapper(be, side);
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return switch (side) {
			case null -> new int[0];
			case UP, NORTH, SOUTH, WEST, EAST -> SLOTS_FOR_UP_SIDES;
			case DOWN -> SLOTS_FOR_DOWN;
		};
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, Direction direction) {
		return true;
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		return true;
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleRemoved(stack, module, toggled);

		if (module == ModuleType.STORAGE) {
			dropContents();
			inventoryContents.set(2, ItemStack.EMPTY);
			inventoryContents.set(3, ItemStack.EMPTY);
		}
	}

	@Override
	public void onOwnerChanged(BlockState state, Level level, BlockPos pos, Player player, Owner oldOwner, Owner newOwner) {
		super.onOwnerChanged(state, level, pos, player, oldOwner, newOwner);

		if (state.getValue(PayBlock.POWERED)) {
			level.setBlockAndUpdate(pos, state.setValue(PayBlock.POWERED, false));
			BlockUtils.updateIndirectNeighbors(level, pos, state.getBlock());
		}

		dropContents();
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new PayBlockMenu(windowId, level, worldPosition, inv);
	}

	public void dropContents() {
		//first 4 slots (0-3) are the payment & reward slots
		for (int i = 4; i < getContainerSize(); i++) {
			Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), getContents().get(i));
		}
	}

	@Override
	public Component getDisplayName() {
		return super.getDisplayName();
	}

	@Override
	public void clearContent() {
		inventoryContents.clear();
	}

	@Override
	public boolean isEmpty() {
		return inventoryContents.isEmpty();
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		return inventoryContents.remove(index);
	}
}
