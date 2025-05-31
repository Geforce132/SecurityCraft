package net.geforcemods.securitycraft.blockentities;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.SendAllowlistMessageOption;
import net.geforcemods.securitycraft.api.Option.SendDenylistMessageOption;
import net.geforcemods.securitycraft.api.Option.SmartModuleCooldownOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.AbstractKeypadFurnaceBlock;
import net.geforcemods.securitycraft.inventory.AbstractKeypadFurnaceMenu;
import net.geforcemods.securitycraft.inventory.InsertOnlySidedInvWrapper;
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
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.model.data.ModelData;

public abstract class AbstractKeypadFurnaceBlockEntity extends AbstractFurnaceBlockEntity implements IPasscodeProtected, MenuProvider, IOwnable, IModuleInventory, ICustomizable, ILockable {
	private Owner owner = new Owner();
	private byte[] passcode;
	private UUID saltKey;
	private boolean saveSalt = false;
	private NonNullList<ItemStack> modules = NonNullList.<ItemStack>withSize(getMaxNumberOfModules(), ItemStack.EMPTY);
	private BooleanOption sendAllowlistMessage = new SendAllowlistMessageOption(false);
	private BooleanOption sendDenylistMessage = new SendDenylistMessageOption(true);
	private DisabledOption disabled = new DisabledOption(false);
	private SmartModuleCooldownOption smartModuleCooldown = new SmartModuleCooldownOption();
	private long cooldownEnd = 0;
	private Map<ModuleType, Boolean> moduleStates = new EnumMap<>(ModuleType.class);
	private ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
		@Override
		protected void onOpen(Level level, BlockPos pos, BlockState state) {
			if (level.isClientSide)
				return;

			level.playSound(null, pos, SoundEvents.IRON_DOOR_OPEN, SoundSource.BLOCKS, 1.0F, 1.0F);
			level.setBlockAndUpdate(pos, state.setValue(AbstractKeypadFurnaceBlock.OPEN, true));
		}

		@Override
		protected void onClose(Level level, BlockPos pos, BlockState state) {
			if (level.isClientSide)
				return;

			level.playSound(null, pos, SoundEvents.IRON_DOOR_CLOSE, SoundSource.BLOCKS, 1.0F, 1.0F);
			level.setBlockAndUpdate(pos, state.setValue(AbstractKeypadFurnaceBlock.OPEN, false));
		}

		@Override
		protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int oldCount, int newCount) {}

		@Override
		protected boolean isOwnContainer(Player player) {
			if (player.containerMenu instanceof AbstractKeypadFurnaceMenu menu) {
				Container container = menu.be;

				return container == AbstractKeypadFurnaceBlockEntity.this;
			}
			else
				return false;
		}
	};

	protected AbstractKeypadFurnaceBlockEntity(BlockEntityType<?> beType, BlockPos pos, BlockState state, RecipeType<? extends AbstractCookingRecipe> recipeType) {
		super(beType, pos, state, recipeType);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, AbstractKeypadFurnaceBlockEntity be) {
		if (!be.isDisabled())
			AbstractFurnaceBlockEntity.serverTick((ServerLevel) level, pos, state, be);
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

		if (owner != null)
			owner.save(tag, needsValidation());

		savePasscodeAndSalt(tag);
	}

	@Override
	public void loadAdditional(ValueInput tag) {
		fixBurnTimeData(tag);
		super.loadAdditional(tag);

		modules = readModuleInventory(tag);
		moduleStates = readModuleStates(tag);
		readOptions(tag);
		cooldownEnd = System.currentTimeMillis() + tag.getLongOr("cooldownLeft", 0);
		owner.load(tag);
		loadSaltKey(tag);
		loadPasscode(tag);

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
	public Owner getOwner() {
		return owner;
	}

	@Override
	public void setOwner(String uuid, String name) {
		owner.set(uuid, name);
	}

	public static IItemHandler getCapability(AbstractKeypadFurnaceBlockEntity be, Direction side) {
		if (BlockUtils.isAllowedToExtractFromProtectedObject(side, be))
			return side == null ? new InvWrapper(be) : new SidedInvWrapper(be, side);
		else
			return new InsertOnlySidedInvWrapper(be, side);
	}

	@Override
	public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {
		super.writeClientSideData(menu, buffer);
		buffer.writeBlockPos(worldPosition);
	}

	@Override
	public boolean enableHack() {
		return true;
	}

	@Override
	public ItemStack getItem(int slot) {
		return slot >= 100 ? getModuleInSlot(slot) : items.get(slot);
	}

	@Override
	public boolean shouldAttemptCodebreak(Player player) {
		if (isDisabled()) {
			player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
			return false;
		}

		return IPasscodeProtected.super.shouldAttemptCodebreak(player);
	}

	@Override
	public void activate(Player player) {
		if (!level.isClientSide && getBlockState().getBlock() instanceof AbstractKeypadFurnaceBlock block)
			block.activate(this, level, worldPosition, player);
	}

	@Override
	public void startOpen(Player player) {
		if (!remove && !player.isSpectator())
			openersCounter.incrementOpeners(player, this.getLevel(), getBlockPos(), getBlockState());
	}

	@Override
	public void stopOpen(Player player) {
		if (!remove && !player.isSpectator())
			openersCounter.decrementOpeners(player, getLevel(), getBlockPos(), getBlockState());
	}

	public void recheckOpen() {
		if (!remove)
			openersCounter.recheckOpeners(getLevel(), getBlockPos(), getBlockState());
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

	public ContainerData getFurnaceData() {
		return dataAccess;
	}

	@Override
	protected Component getDefaultName() {
		return Component.translatable(getBlockState().getBlock().getDescriptionId());
	}

	@Override
	public NonNullList<ItemStack> getInventory() {
		return modules;
	}

	@Override
	public void startCooldown() {
		if (!isOnCooldown()) {
			cooldownEnd = System.currentTimeMillis() + smartModuleCooldown.get() * 50;
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
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.ALLOWLIST, ModuleType.DENYLIST, ModuleType.DISGUISE, ModuleType.SMART, ModuleType.HARMING
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				sendAllowlistMessage, sendDenylistMessage, disabled, smartModuleCooldown
		};
	}

	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module, boolean toggled) {
		IModuleInventory.super.onModuleInserted(stack, module, toggled);

		if (module == ModuleType.DISGUISE)
			DisguisableBlockEntity.onDisguiseModuleInserted(this, stack, toggled);
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		DisguisableBlockEntity.onSetRemoved(this);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
		IModuleInventory.super.onModuleRemoved(stack, module, toggled);

		if (module == ModuleType.DISGUISE)
			DisguisableBlockEntity.onDisguiseModuleRemoved(this, stack, toggled);
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

	public boolean sendsDenylistMessage() {
		return sendDenylistMessage.get();
	}

	public boolean isDisabled() {
		return disabled.get();
	}

	@Override
	public Level myLevel() {
		return level;
	}

	@Override
	public BlockPos myPos() {
		return worldPosition;
	}

	private void fixBurnTimeData(ValueInput tag) {
		if (tag.contains("CookTime")) {//Pre-1.12.4 furnace burn time data
			int burnTime = tag.getIntOr("BurnTime", 0);

			tag.putInt("cooking_time_spent", tag.getIntOr("CookTime", 0));
			tag.putInt("cooking_total_time", tag.getIntOr("CookTimeTotal", 0));
			tag.putInt("lit_time_remaining", burnTime);
			tag.putInt("lit_total_time", burnTime);
		}
	}
}
