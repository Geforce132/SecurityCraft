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
import net.geforcemods.securitycraft.inventory.InsertOnlyResourceHandler;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.SaltData;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.model.data.ModelData;
import net.neoforged.neoforge.transfer.CombinedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;

public class KeypadChestBlockEntity extends ChestBlockEntity implements IPasscodeProtected, IOwnable, IModuleInventory, ICustomizable, ILockable, ISentryBulletContainer {
	private byte[] passcode;
	private UUID saltKey;
	private boolean saveSalt = false;
	private Owner owner = new Owner();
	private NonNullList<ItemStack> modules = NonNullList.<ItemStack>withSize(getMaxNumberOfModules(), ItemStack.EMPTY);
	private BooleanOption sendAllowlistMessage = new SendAllowlistMessageOption(false);
	private BooleanOption sendDenylistMessage = new SendDenylistMessageOption(true);
	private SmartModuleCooldownOption smartModuleCooldown = new SmartModuleCooldownOption();
	private long cooldownEnd = 0;
	private Map<ModuleType, Boolean> moduleStates = new EnumMap<>(ModuleType.class);
	private ResourceLocation previousChest;
	private static final DoubleBlockCombiner.Combiner<ChestBlockEntity, ResourceHandler<ItemResource>> CHEST_COMBINER_HANDLER = new DoubleBlockCombiner.Combiner<>() {
		@Override
		public ResourceHandler<ItemResource> acceptDouble(ChestBlockEntity chest1, ChestBlockEntity chest2) {
			return new CombinedResourceHandler<>(VanillaContainerWrapper.of(chest1), VanillaContainerWrapper.of(chest2));
		}

		@Override
		public ResourceHandler<ItemResource> acceptSingle(ChestBlockEntity chest) {
			return VanillaContainerWrapper.of(chest);
		}

		@Override
		public ResourceHandler<ItemResource> acceptNone() {
			return null;
		}
	};

	public KeypadChestBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.KEYPAD_CHEST_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public void saveAdditional(ValueOutput tag) {
		long cooldownLeft;

		super.saveAdditional(tag);
		writeModuleInventory(tag);
		writeModuleStates(tag);
		writeOptions(tag);
		cooldownLeft = getCooldownEnd() - System.currentTimeMillis();
		tag.putLong("cooldownLeft", cooldownLeft <= 0 ? -1 : cooldownLeft);
		savePasscodeAndSalt(tag);

		if (owner != null)
			owner.save(tag, needsValidation());

		if (previousChest != null)
			tag.putString("previous_chest", previousChest.toString());
	}

	@Override
	public void loadAdditional(ValueInput tag) {
		super.loadAdditional(tag);

		readModuleInventory(modules, tag);
		moduleStates = readModuleStates(tag);
		readOptions(tag);
		cooldownEnd = System.currentTimeMillis() + tag.getLongOr("cooldownLeft", 0);
		loadPasscodeAndSaltKey(tag);
		owner.load(tag);

		String savedPreviousChest = tag.getStringOr("previous_chest", "");

		if (!savedPreviousChest.isBlank()) {
			ResourceLocation parsedPreviousChest = ResourceLocation.parse(savedPreviousChest);

			if (parsedPreviousChest.getPath() != null && !parsedPreviousChest.getPath().isBlank())
				previousChest = parsedPreviousChest;
		}

		if (!tag.getBooleanOr("sendMessage", true)) {
			sendAllowlistMessage.setValue(false);
			sendDenylistMessage.setValue(false);
		}
	}

	@Override
	public void preRemoveSideEffects(BlockPos pos, BlockState state) {
		SaltData.removeSalt(saltKey);
		super.preRemoveSideEffects(pos, state);
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
	public void onDataPacket(Connection net, ValueInput tag) {
		super.onDataPacket(net, tag);
		DisguisableBlockEntity.onHandleUpdateTag(this);
	}

	@Override
	public void onLoad() {
		super.onLoad();
		DisguisableBlockEntity.onHandleUpdateTag(this);
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

	public static ResourceHandler<ItemResource> getCapability(KeypadChestBlockEntity be, Direction side) {
		ResourceHandler<ItemResource> resourceHandler = ((KeypadChestBlock) be.getBlockState().getBlock()).combine(be.getBlockState(), be.getLevel(), be.getBlockPos(), true).apply(CHEST_COMBINER_HANDLER);

		return BlockUtils.isAllowedToExtractFromProtectedObject(side, be) ? resourceHandler : new InsertOnlyResourceHandler<>(resourceHandler);
	}

	@Override
	public ResourceHandler<ItemResource> getHandlerForSentry(Sentry entity) {
		if (entity.getOwner().owns(this))
			return VanillaContainerWrapper.of(this);
		else
			return null;
	}

	@Override
	public void activate(Player player) {
		if (!level.isClientSide() && getBlockState().getBlock() instanceof KeypadChestBlock block && !isBlocked())
			block.activate(getBlockState(), level, worldPosition, player);
	}

	@Override
	public void openPasscodeGUI(Level level, BlockPos pos, Player player) {
		if (!level.isClientSide() && !isBlocked())
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
		IModuleInventory.super.onModuleRemoved(stack, module, toggled);
		addOrRemoveModuleFromAttached(stack, true, toggled);

		if (module == ModuleType.DISGUISE)
			DisguisableBlockEntity.onDisguiseModuleRemoved(this, stack, toggled);
	}

	@Override
	public <T> void onOptionChanged(Option<T> option) {
		KeypadChestBlockEntity otherBe = findOther();

		if (otherBe != null) {
			switch (option) {
				case BooleanOption bo when option == sendAllowlistMessage -> otherBe.setSendsAllowlistMessage(bo.get());
				case BooleanOption bo when option == sendDenylistMessage -> otherBe.setSendsAllowlistMessage(bo.get());
				case IntOption io when option == smartModuleCooldown -> otherBe.smartModuleCooldown.copy(option);
				default ->
						throw new UnsupportedOperationException("Unhandled option synchronization in keypad chest! " + option.getName());
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
	public void onOwnerChanged(BlockState state, Level level, BlockPos pos, Player player, Owner oldOwner, Owner newOwner) {
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
	public void setSaveSalt(boolean saveSalt) {
		this.saveSalt = saveSalt;
	}

	@Override
	public boolean shouldSaveSalt() {
		return saveSalt;
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
	}

	@Override
	public ModelData getModelData() {
		return DisguisableBlockEntity.getModelData(this);
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
