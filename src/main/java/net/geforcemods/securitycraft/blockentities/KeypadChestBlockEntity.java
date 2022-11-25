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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class KeypadChestBlockEntity extends ChestTileEntity implements IPasswordProtected, IOwnable, IModuleInventory, ICustomizable, ILockable {
	private LazyOptional<IItemHandler> insertOnlyHandler;
	private String passcode;
	private Owner owner = new Owner();
	private NonNullList<ItemStack> modules = NonNullList.<ItemStack>withSize(getMaxNumberOfModules(), ItemStack.EMPTY);
	private BooleanOption sendMessage = new BooleanOption("sendMessage", true);
	private EnumMap<ModuleType, Boolean> moduleStates = new EnumMap<>(ModuleType.class);

	public KeypadChestBlockEntity() {
		super(SCContent.KEYPAD_CHEST_BLOCK_ENTITY.get());
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		super.save(tag);

		writeModuleInventory(tag);
		writeModuleStates(tag);
		writeOptions(tag);

		if (passcode != null && !passcode.isEmpty())
			tag.putString("passcode", passcode);

		if (owner != null)
			owner.write(tag, false);

		return tag;
	}

	@Override
	public void load(BlockState state, CompoundNBT tag) {
		super.load(state, tag);

		modules = readModuleInventory(tag);
		readOptions(tag);
		passcode = tag.getString("passcode");
		moduleStates = readModuleStates(tag);
		owner.read(tag);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return save(new CompoundNBT());
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(worldPosition, 1, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
		load(getBlockState(), packet.getTag());
	}

	@Override
	public ITextComponent getDefaultName() {
		return Utils.localize("block.securitycraft.keypad_chest");
	}

	@Override
	protected void signalOpenCount() {
		super.signalOpenCount();

		if (isModuleEnabled(ModuleType.REDSTONE))
			BlockUtils.updateIndirectNeighbors(level, worldPosition, SCContent.KEYPAD_CHEST.get(), Direction.DOWN);
	}

	public int getNumPlayersUsing() {
		return openCount;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
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
			return super.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP);
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
	public void activate(PlayerEntity player) {
		if (!level.isClientSide && getBlockState().getBlock() instanceof KeypadChestBlock && !isBlocked())
			((KeypadChestBlock) getBlockState().getBlock()).activate(getBlockState(), level, worldPosition, player);
	}

	@Override
	public void openPasswordGUI(World level, BlockPos pos, Owner owner, PlayerEntity player) {
		if (!level.isClientSide && !isBlocked())
			IPasswordProtected.super.openPasswordGUI(level, worldPosition, owner, player);
	}

	@Override
	public boolean shouldAttemptCodebreak(BlockState state, PlayerEntity player) {
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
	public void onOptionChanged(Option<?> option) {
		if (option instanceof BooleanOption) {
			KeypadChestBlockEntity offsetTe = findOther();

			if (offsetTe != null)
				offsetTe.setSendsMessages(((BooleanOption) option).get());
		}
	}

	@Override
	public void dropAllModules() {
		KeypadChestBlockEntity offsetBe = findOther();

		for (ItemStack module : getInventory()) {
			if (!(module.getItem() instanceof ModuleItem))
				continue;

			if (offsetBe != null)
				offsetBe.removeModule(((ModuleItem) module.getItem()).getModuleType(), false);

			Block.popResource(level, worldPosition, module);
		}

		getInventory().clear();
	}

	public void addOrRemoveModuleFromAttached(ItemStack module, boolean remove, boolean toggled) {
		if (module.isEmpty() || !(module.getItem() instanceof ModuleItem))
			return;

		KeypadChestBlockEntity offsetTe = findOther();

		if (offsetTe != null) {
			ModuleType moduleType = ((ModuleItem) module.getItem()).getModuleType();

			if (toggled && offsetTe.isModuleEnabled(moduleType) != remove)
				return;
			else if (!toggled && offsetTe.hasModule(moduleType) != remove)
				return;

			if (remove)
				offsetTe.removeModule(moduleType, toggled);
			else
				offsetTe.insertModule(module, toggled);
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
					TileEntity offsetTe = level.getBlockEntity(offsetPos);

					if (offsetTe instanceof KeypadChestBlockEntity)
						return (KeypadChestBlockEntity) offsetTe;
				}
			}
		}

		return null;
	}

	@Override
	public void onOwnerChanged(BlockState state, World level, BlockPos pos, PlayerEntity player) {
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
	}

	@Override
	public Owner getOwner() {
		return owner;
	}

	@Override
	public void setOwner(String uuid, String name) {
		owner.set(uuid, name);
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
	public TileEntity getTileEntity() {
		return this;
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
	}
}
