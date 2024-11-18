package net.geforcemods.securitycraft.blockentities;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class KeypadChestBlockEntity extends ChestTileEntity implements IPasscodeProtected, IOwnable, IModuleInventory, ICustomizable, ILockable, ISentryBulletContainer {
	private LazyOptional<IItemHandler> insertOnlyHandler;
	private byte[] passcode;
	private UUID saltKey;
	private Owner owner = new Owner();
	private NonNullList<ItemStack> modules = NonNullList.<ItemStack>withSize(getMaxNumberOfModules(), ItemStack.EMPTY);
	private BooleanOption sendAllowlistMessage = new SendAllowlistMessageOption(false);
	private BooleanOption sendDenylistMessage = new SendDenylistMessageOption(true);
	private SmartModuleCooldownOption smartModuleCooldown = new SmartModuleCooldownOption(this::getBlockPos);
	private long cooldownEnd = 0;
	private Map<ModuleType, Boolean> moduleStates = new EnumMap<>(ModuleType.class);
	private ResourceLocation previousChest;

	public KeypadChestBlockEntity() {
		super(SCContent.KEYPAD_CHEST_BLOCK_ENTITY.get());
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		long cooldownLeft;

		super.save(tag);
		writeModuleInventory(tag);
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

		return tag;
	}

	@Override
	public void load(BlockState state, CompoundNBT tag) {
		super.load(state, tag);

		modules = readModuleInventory(tag);
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
	public CompoundNBT getUpdateTag() {
		return PasscodeUtils.filterPasscodeAndSaltFromTag(save(new CompoundNBT()));
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
	public void handleUpdateTag(BlockState state, CompoundNBT tag) {
		load(state, tag);
		DisguisableBlockEntity.onHandleUpdateTag(this);
	}

	@Override
	public ITextComponent getDefaultName() {
		return Utils.localize(SCContent.KEYPAD_CHEST.get().getDescriptionId());
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
			return BlockUtils.isAllowedToExtractFromProtectedObject(side, this) ? super.getCapability(cap, side) : getInsertOnlyHandler().cast();
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

	@Override
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
	public void openPasscodeGUI(World level, BlockPos pos, PlayerEntity player) {
		if (!level.isClientSide && !isBlocked())
			IPasscodeProtected.super.openPasscodeGUI(level, pos, player);
	}

	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module, boolean toggled) {
		IModuleInventory.super.onModuleInserted(stack, module, toggled);
		addOrRemoveModuleFromAttached(stack, false, toggled);

		if (module == ModuleType.DISGUISE)
			DisguisableBlockEntity.onDisguiseModuleInserted(this, stack, toggled);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
		addOrRemoveModuleFromAttached(stack, true, toggled);
		IModuleInventory.super.onModuleRemoved(stack, module, toggled);

		if (module == ModuleType.DISGUISE)
			DisguisableBlockEntity.onDisguiseModuleRemoved(this, stack, toggled);
	}

	@Override
	public <T> void onOptionChanged(Option<T> option) {
		KeypadChestBlockEntity otherBe = findOther();

		if (otherBe != null) {
			if (option instanceof BooleanOption) {
				BooleanOption bo = (BooleanOption) option;

				if (option == sendAllowlistMessage)
					otherBe.setSendsAllowlistMessage(bo.get());
				else if (option == sendDenylistMessage)
					otherBe.setSendsAllowlistMessage(bo.get());
				else
					throw new UnsupportedOperationException("Unhandled option synchronization in keypad chest! " + option.getName());
			}
			else if (option instanceof IntOption && option == smartModuleCooldown)
				otherBe.smartModuleCooldown.copy(option);
			else
				throw new UnsupportedOperationException("Unhandled option synchronization in keypad chest! " + option.getName());
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

		KeypadChestBlockEntity offsetBe = findOther();

		if (offsetBe != null) {
			ModuleType moduleType = ((ModuleItem) module.getItem()).getModuleType();

			if (toggled && offsetBe.isModuleEnabled(moduleType) != remove || !toggled && offsetBe.hasModule(moduleType) != remove)
				return;

			if (remove)
				offsetBe.removeModule(moduleType, toggled);
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

				if (offsetType != ChestType.SINGLE && type != offsetType && state.getValue(ChestBlock.FACING) == offsetState.getValue(ChestBlock.FACING)) {
					TileEntity offsetTe = level.getBlockEntity(offsetPos);

					if (offsetTe instanceof KeypadChestBlockEntity)
						return (KeypadChestBlockEntity) offsetTe;
				}
			}
		}

		return null;
	}

	@Override
	public void onOwnerChanged(BlockState state, World level, BlockPos pos, PlayerEntity player, Owner oldOwner, Owner newOwner) {
		KeypadChestBlockEntity otherHalf = findOther();

		if (otherHalf != null)
			otherHalf.setOwner(getOwner().getUUID(), getOwner().getName());

		IOwnable.super.onOwnerChanged(state, level, pos, player, oldOwner, newOwner);
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		DisguisableBlockEntity.onSetRemoved(this);
	}

	@Override
	public byte[] getPasscode() {
		return passcode == null || passcode.length == 0 ? null : passcode;
	}

	@Override
	public void setPasscode(byte[] passcode) {
		this.passcode = passcode;
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
	public void setPasscodeInAdjacentBlock(String codeToSet) {
		KeypadChestBlockEntity chestBe = findOther();

		if (chestBe != null && getOwner().owns(chestBe)) {
			chestBe.hashAndSetPasscode(codeToSet, getSalt());
			level.sendBlockUpdated(chestBe.worldPosition, chestBe.getBlockState(), chestBe.getBlockState(), 2);
		}
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
				ModuleType.ALLOWLIST, ModuleType.DENYLIST, ModuleType.REDSTONE, ModuleType.SMART, ModuleType.HARMING, ModuleType.DISGUISE
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

		if (shouldBeEnabled)
			onModuleInserted(getModule(module), module, true);
		else
			onModuleRemoved(getModule(module), module, true);
	}

	@Override
	public IModelData getModelData() {
		return DisguisableBlockEntity.DEFAULT_MODEL_DATA.get();
	}

	public boolean sendsAllowlistMessage() {
		return sendAllowlistMessage.get();
	}

	public void setSendsAllowlistMessage(boolean value) {
		sendAllowlistMessage.setValue(value);
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3); //sync option change to client
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
		this.previousChest = previousChest.getRegistryName();
	}

	public ResourceLocation getPreviousChest() {
		return previousChest;
	}

	@Override
	public World myLevel() {
		return level;
	}

	@Override
	public BlockPos myPos() {
		return worldPosition;
	}
}
