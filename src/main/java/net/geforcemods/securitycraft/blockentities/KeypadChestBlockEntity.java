package net.geforcemods.securitycraft.blockentities;

import java.util.EnumMap;
import java.util.stream.Collectors;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.KeypadChestBlock;
import net.geforcemods.securitycraft.entity.Sentry;
import net.geforcemods.securitycraft.inventory.InsertOnlyInvWrapper;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

public class KeypadChestBlockEntity extends ChestBlockEntity implements IPasswordProtected, IOwnable, IModuleInventory, ICustomizable, ILockable {
	private LazyOptional<IItemHandler> insertOnlyHandler;
	private String passcode;
	private Owner owner = new Owner();
	private NonNullList<ItemStack> modules = NonNullList.<ItemStack>withSize(getMaxNumberOfModules(), ItemStack.EMPTY);
	private BooleanOption sendMessage = new BooleanOption("sendMessage", true);
	private EnumMap<ModuleType, Boolean> moduleStates = new EnumMap<>(ModuleType.class);

	public KeypadChestBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.KEYPAD_CHEST_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);

		writeModuleInventory(tag);
		writeModuleStates(tag);
		writeOptions(tag);

		if (passcode != null && !passcode.isEmpty())
			tag.putString("passcode", passcode);

		if (owner != null)
			owner.save(tag, false);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);

		modules = readModuleInventory(tag);
		moduleStates = readModuleStates(tag);
		readOptions(tag);
		passcode = tag.getString("passcode");
		owner.load(tag);
	}

	@Override
	public CompoundTag getUpdateTag() {
		return saveWithoutMetadata();
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
		super.onDataPacket(net, packet);
		handleUpdateTag(packet.getTag());
	}

	@Override
	public void handleUpdateTag(CompoundTag tag) {
		load(tag);
	}

	@Override
	public Component getDefaultName() {
		return Utils.localize("block.securitycraft.keypad_chest");
	}

	@Override
	protected void signalOpenCount(Level level, BlockPos pos, BlockState state, int i, int j) {
		super.signalOpenCount(level, pos, state, i, j);

		if (isModuleEnabled(ModuleType.REDSTONE))
			BlockUtils.updateIndirectNeighbors(level, pos, state.getBlock(), Direction.DOWN);
	}

	public int getNumPlayersUsing() {
		return openersCounter.getOpenerCount();
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == ForgeCapabilities.ITEM_HANDLER)
			return BlockUtils.getProtectedCapability(side, this, () -> super.getCapability(cap, side), () -> getInsertOnlyHandler()).cast();
		else
			return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		if (insertOnlyHandler != null)
			insertOnlyHandler.invalidate();

		super.invalidateCaps();
	}

	@Override
	public void reviveCaps() {
		insertOnlyHandler = null; //recreated in getInsertOnlyHandler
		super.reviveCaps();
	}

	private LazyOptional<IItemHandler> getInsertOnlyHandler() {
		if (insertOnlyHandler == null)
			insertOnlyHandler = LazyOptional.of(() -> new InsertOnlyInvWrapper(KeypadChestBlockEntity.this));

		return insertOnlyHandler;
	}

	public LazyOptional<IItemHandler> getHandlerForSentry(Sentry entity) {
		if (entity.getOwner().owns(this))
			return super.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP);
		else
			return LazyOptional.empty();
	}

	@Override
	public boolean enableHack() {
		return true;
	}

	@Override
	public ItemStack getItem(int slot) {
		return slot >= 100 ? getModuleInSlot(slot) : super.getItem(slot);
	}

	@Override
	public void activate(Player player) {
		if (!level.isClientSide && getBlockState().getBlock() instanceof KeypadChestBlock block && !isBlocked())
			block.activate(getBlockState(), level, worldPosition, player);
	}

	@Override
	public void openPasswordGUI(Level level, BlockPos pos, Owner owner, Player player) {
		if (!level.isClientSide && !isBlocked())
			IPasswordProtected.super.openPasswordGUI(level, pos, owner, player);
	}

	@Override
	public boolean shouldAttemptCodebreak(BlockState state, Player player) {
		return true;
	}

	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module, boolean toggled) {
		IModuleInventory.super.onModuleInserted(stack, module, toggled);
		addOrRemoveModuleFromAttached(stack, false, toggled);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
		addOrRemoveModuleFromAttached(stack, true, toggled);
		IModuleInventory.super.onModuleRemoved(stack, module, toggled);
	}

	@Override
	public void onOptionChanged(Option<?> o) {
		if (o instanceof BooleanOption option) {
			KeypadChestBlockEntity offsetTe = findOther();

			if (offsetTe != null)
				offsetTe.setSendsMessages(option.get());
		}

		ICustomizable.super.onOptionChanged(o);
	}

	@Override
	public void dropAllModules() {
		KeypadChestBlockEntity offsetBe = findOther();

		for (ItemStack module : getInventory()) {
			if (!(module.getItem() instanceof ModuleItem item))
				continue;

			if (offsetBe != null)
				offsetBe.removeModule(item.getModuleType(), false);

			Block.popResource(level, worldPosition, module);
		}

		getInventory().clear();
	}

	public void addOrRemoveModuleFromAttached(ItemStack module, boolean remove, boolean toggled) {
		if (module.isEmpty() || !(module.getItem() instanceof ModuleItem moduleItem))
			return;

		KeypadChestBlockEntity offsetBe = findOther();

		if (offsetBe != null) {
			if (toggled && offsetBe.isModuleEnabled(moduleItem.getModuleType()) != remove)
				return;
			else if (!toggled && offsetBe.hasModule(moduleItem.getModuleType()) != remove)
				return;

			if (remove)
				offsetBe.removeModule(moduleItem.getModuleType(), toggled);
			else
				offsetBe.insertModule(module, toggled);
		}
	}

	public KeypadChestBlockEntity findOther() {
		BlockState state = getBlockState();
		ChestType type = state.getValue(KeypadChestBlock.TYPE);

		if (type != ChestType.SINGLE) {
			BlockPos offsetPos = worldPosition.relative(ChestBlock.getConnectedDirection(state));
			BlockState offsetState = level.getBlockState(offsetPos);

			if (state.getBlock() == offsetState.getBlock()) {
				ChestType offsetType = offsetState.getValue(KeypadChestBlock.TYPE);

				if (offsetType != ChestType.SINGLE && type != offsetType && state.getValue(KeypadChestBlock.FACING) == offsetState.getValue(KeypadChestBlock.FACING)) {
					if (level.getBlockEntity(offsetPos) instanceof KeypadChestBlockEntity be)
						return be;
				}
			}
		}

		return null;
	}

	@Override
	public void onOwnerChanged(BlockState state, Level level, BlockPos pos, Player player) {
		KeypadChestBlockEntity otherHalf = findOther();

		if (otherHalf != null)
			otherHalf.setOwner(getOwner().getUUID(), getOwner().getName());

		IOwnable.super.onOwnerChanged(state, level, pos, player);
	}

	@Override
	public String getPassword() {
		return (passcode != null && !passcode.isEmpty()) ? passcode : null;
	}

	@Override
	public void setPassword(String password) {
		passcode = password;
		setChanged();
	}

	@Override
	public Owner getOwner() {
		return owner;
	}

	@Override
	public void setOwner(String uuid, String name) {
		owner.set(uuid, name);
		setChanged();
	}

	public boolean isBlocked() {
		for (Direction dir : Direction.Plane.HORIZONTAL.stream().collect(Collectors.toList())) {
			BlockPos pos = getBlockPos().relative(dir);

			if (level.getBlockState(pos).getBlock() instanceof KeypadChestBlock && KeypadChestBlock.isBlocked(level, pos))
				return true;
		}

		return isSingleBlocked();
	}

	public boolean isSingleBlocked() {
		return KeypadChestBlock.isBlocked(getLevel(), getBlockPos());
	}

	@Override
	public NonNullList<ItemStack> getInventory() {
		return modules;
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.ALLOWLIST, ModuleType.DENYLIST, ModuleType.REDSTONE
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				sendMessage
		};
	}

	@Override
	public boolean isModuleEnabled(ModuleType module) {
		return hasModule(module) && moduleStates.get(module) == Boolean.TRUE; //prevent NPE
	}

	@Override
	public void toggleModuleState(ModuleType module, boolean shouldBeEnabled) {
		moduleStates.put(module, shouldBeEnabled);

		if (shouldBeEnabled)
			onModuleInserted(getModule(module), module, true);
		else
			onModuleRemoved(getModule(module), module, true);
	}

	public boolean sendsMessages() {
		return sendMessage.get();
	}

	public void setSendsMessages(boolean value) {
		sendMessage.setValue(value);
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3); //sync option change to client
		setChanged();
	}
}
