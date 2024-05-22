package net.geforcemods.securitycraft.blockentities;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.api.Option.SendAllowlistMessageOption;
import net.geforcemods.securitycraft.api.Option.SendDenylistMessageOption;
import net.geforcemods.securitycraft.api.Option.SmartModuleCooldownOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.KeypadChestBlock;
import net.geforcemods.securitycraft.entity.sentry.ISentryBulletContainer;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.inventory.InsertOnlyInvWrapper;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

public class KeypadChestBlockEntity extends ChestBlockEntity implements IPasscodeProtected, IOwnable, IModuleInventory, ICustomizable, ILockable, ISentryBulletContainer {
	private byte[] passcode;
	private UUID saltKey;
	private Owner owner = new Owner();
	private NonNullList<ItemStack> modules = NonNullList.<ItemStack>withSize(getMaxNumberOfModules(), ItemStack.EMPTY);
	private BooleanOption sendAllowlistMessage = new SendAllowlistMessageOption(false);
	private BooleanOption sendDenylistMessage = new SendDenylistMessageOption(true);
	private SmartModuleCooldownOption smartModuleCooldown = new SmartModuleCooldownOption();
	private long cooldownEnd = 0;
	private Map<ModuleType, Boolean> moduleStates = new EnumMap<>(ModuleType.class);
	private ResourceLocation previousChest;

	public KeypadChestBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.KEYPAD_CHEST_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public void saveAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		long cooldownLeft;

		super.saveAdditional(tag, lookupProvider);
		writeModuleInventory(tag, lookupProvider);
		writeModuleStates(tag);
		writeOptions(tag);
		cooldownLeft = getCooldownEnd() - System.currentTimeMillis();
		tag.putLong("cooldownLeft", cooldownLeft <= 0 ? -1 : cooldownLeft);

		if (saltKey != null)
			tag.putUUID("saltKey", saltKey);

		if (passcode != null)
			tag.putString("passcode", PasscodeUtils.bytesToString(passcode));

		if (owner != null)
			owner.save(tag, needsValidation());

		if (previousChest != null)
			tag.putString("previous_chest", previousChest.toString());
	}

	@Override
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		super.loadAdditional(tag, lookupProvider);

		modules = readModuleInventory(tag, lookupProvider);
		moduleStates = readModuleStates(tag);
		readOptions(tag);
		cooldownEnd = System.currentTimeMillis() + tag.getLong("cooldownLeft");
		loadSaltKey(tag);
		loadPasscode(tag);
		owner.load(tag);
		previousChest = new ResourceLocation(tag.getString("previous_chest"));

		if (tag.contains("sendMessage") && !tag.getBoolean("sendMessage")) {
			sendAllowlistMessage.setValue(false);
			sendDenylistMessage.setValue(false);
		}
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider lookupProvider) {
		return PasscodeUtils.filterPasscodeAndSaltFromTag(saveCustomOnly(lookupProvider));
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public Component getDefaultName() {
		return Utils.localize(SCContent.KEYPAD_CHEST.get().getDescriptionId());
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

	public static IItemHandler getCapability(KeypadChestBlockEntity be, Direction side) {
		if (BlockUtils.isAllowedToExtractFromProtectedBlock(side, be))
			return new InvWrapper(ChestBlock.getContainer((ChestBlock) be.getBlockState().getBlock(), be.getBlockState(), be.getLevel(), be.getBlockPos(), true));
		else
			return new InsertOnlyInvWrapper(be);
	}

	@Override
	public IItemHandler getHandlerForSentry(Sentry entity) {
		if (entity.getOwner().owns(this))
			return new InvWrapper(this);
		else
			return null;
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
	public void openPasscodeGUI(Level level, BlockPos pos, Player player) {
		if (!level.isClientSide && !isBlocked())
			IPasscodeProtected.super.openPasscodeGUI(level, pos, player);
	}

	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module, boolean toggled) {
		IModuleInventory.super.onModuleInserted(stack, module, toggled);
		addOrRemoveModuleFromAttached(stack, false, toggled);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
		IModuleInventory.super.onModuleRemoved(stack, module, toggled);
		addOrRemoveModuleFromAttached(stack, true, toggled);
	}

	@Override
	public <T> void onOptionChanged(Option<T> option) {
		KeypadChestBlockEntity otherBe = findOther();

		if (otherBe != null) {
			switch (option) {
				case BooleanOption bo when option == sendAllowlistMessage -> otherBe.setSendsAllowlistMessage(bo.get());
				case BooleanOption bo when option == sendDenylistMessage -> otherBe.setSendsAllowlistMessage(bo.get());
				case IntOption io when option == smartModuleCooldown -> otherBe.smartModuleCooldown.copy(option);
				default -> throw new UnsupportedOperationException("Unhandled option synchronization in keypad chest! " + option.getName());
			}
		}

		ICustomizable.super.onOptionChanged(option);
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
			if (toggled ? offsetBe.isModuleEnabled(moduleItem.getModuleType()) != remove : offsetBe.hasModule(moduleItem.getModuleType()) != remove)
				return;

			if (remove)
				offsetBe.removeModule(moduleItem.getModuleType(), toggled);
			else
				offsetBe.insertModule(module, toggled);
		}
	}

	public KeypadChestBlockEntity findOther() {
		BlockState state = getBlockState();
		ChestType type = state.getValue(ChestBlock.TYPE);

		if (type != ChestType.SINGLE) {
			BlockPos offsetPos = worldPosition.relative(ChestBlock.getConnectedDirection(state));
			BlockState offsetState = level.getBlockState(offsetPos);

			if (state.getBlock() == offsetState.getBlock()) {
				ChestType offsetType = offsetState.getValue(ChestBlock.TYPE);

				if (offsetType != ChestType.SINGLE && type != offsetType && state.getValue(ChestBlock.FACING) == offsetState.getValue(ChestBlock.FACING) && level.getBlockEntity(offsetPos) instanceof KeypadChestBlockEntity be)
					return be;
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
	public byte[] getPasscode() {
		return passcode == null || passcode.length == 0 ? null : passcode;
	}

	@Override
	public void setPasscode(byte[] passcode) {
		this.passcode = passcode;
		setChanged();
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
	public Owner getOwner() {
		return owner;
	}

	@Override
	public void setOwner(String uuid, String name) {
		owner.set(uuid, name);
		setChanged();
	}

	public boolean isBlocked() {
		for (Direction dir : Direction.Plane.HORIZONTAL) {
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
	public void startCooldown() {
		KeypadChestBlockEntity otherHalf = findOther();
		long start = System.currentTimeMillis();

		startCooldown(start);

		if (otherHalf != null)
			otherHalf.startCooldown(start);
	}

	public void startCooldown(long start) {
		if (!isOnCooldown()) {
			cooldownEnd = start + smartModuleCooldown.get() * 50;
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
			setChanged();
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
	public boolean shouldDropModules() {
		return getBlockState().getValue(ChestBlock.TYPE) == ChestType.SINGLE;
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.ALLOWLIST, ModuleType.DENYLIST, ModuleType.REDSTONE, ModuleType.SMART, ModuleType.HARMING
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				sendAllowlistMessage, sendDenylistMessage, smartModuleCooldown
		};
	}

	@Override
	public boolean isModuleEnabled(ModuleType module) {
		return hasModule(module) && moduleStates.get(module) == Boolean.TRUE; //prevent NPE
	}

	@Override
	public void toggleModuleState(ModuleType module, boolean shouldBeEnabled) {
		moduleStates.put(module, shouldBeEnabled);
	}

	public boolean sendsAllowlistMessage() {
		return sendAllowlistMessage.get();
	}

	public void setSendsAllowlistMessage(boolean value) {
		sendAllowlistMessage.setValue(value);
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3); //sync option change to client
		setChanged();
	}

	public boolean sendsDenylistMessage() {
		return sendDenylistMessage.get();
	}

	public void setSendsDenylistMessage(boolean value) {
		sendDenylistMessage.setValue(value);
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3); //sync option change to client
		setChanged();
	}

	public void setPreviousChest(Block previousChest) {
		this.previousChest = Utils.getRegistryName(previousChest);
	}

	public ResourceLocation getPreviousChest() {
		return previousChest;
	}

	@Override
	public Level myLevel() {
		return level;
	}

	@Override
	public BlockPos myPos() {
		return worldPosition;
	}
}
