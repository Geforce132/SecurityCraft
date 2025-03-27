package net.geforcemods.securitycraft.blockentities;

import java.util.Arrays;
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
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants.WorldEvents;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public abstract class AbstractKeypadFurnaceBlockEntity extends AbstractFurnaceTileEntity implements IPasscodeProtected, INamedContainerProvider, IOwnable, IModuleInventory, ICustomizable, ILockable {
	private LazyOptional<IItemHandlerModifiable>[] insertOnlyHandlers;
	private Owner owner = new Owner();
	private byte[] passcode;
	private UUID saltKey;
	private boolean saveSalt = false;
	private NonNullList<ItemStack> modules = NonNullList.<ItemStack>withSize(getMaxNumberOfModules(), ItemStack.EMPTY);
	private BooleanOption sendAllowlistMessage = new SendAllowlistMessageOption(false);
	private BooleanOption sendDenylistMessage = new SendDenylistMessageOption(true);
	private DisabledOption disabled = new DisabledOption(false);
	private SmartModuleCooldownOption smartModuleCooldown = new SmartModuleCooldownOption(this::getBlockPos);
	private long cooldownEnd = 0;
	private Map<ModuleType, Boolean> moduleStates = new EnumMap<>(ModuleType.class);
	private int openCount;

	protected AbstractKeypadFurnaceBlockEntity(TileEntityType<?> beType, IRecipeType<? extends AbstractCookingRecipe> recipeType) {
		super(beType, recipeType);
	}

	@Override
	public void tick() {
		if (!isDisabled())
			super.tick();
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

		if (owner != null)
			owner.save(tag, needsValidation());

		savePasscodeAndSalt(tag);
		return tag;
	}

	@Override
	public void load(BlockState state, CompoundNBT tag) {
		super.load(state, tag);

		modules = readModuleInventory(tag);
		moduleStates = readModuleStates(tag);
		readOptions(tag);
		cooldownEnd = System.currentTimeMillis() + tag.getLong("cooldownLeft");
		owner.load(tag);
		loadSaltKey(tag);
		loadPasscode(tag);

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
		super.handleUpdateTag(state, tag);
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

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return BlockUtils.isAllowedToExtractFromProtectedObject(side, this) ? super.getCapability(cap, side) : getInsertOnlyHandler(side).cast();
		else
			return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		if (insertOnlyHandlers != null)
			Arrays.stream(insertOnlyHandlers).forEach(LazyOptional::invalidate);

		super.invalidateCaps();
	}

	@Override
	public void reviveCaps() {
		insertOnlyHandlers = null; //recreated in getInsertOnlyHandler
		super.reviveCaps();
	}

	private LazyOptional<IItemHandlerModifiable> getInsertOnlyHandler(Direction side) {
		if (insertOnlyHandlers == null)
			insertOnlyHandlers = InsertOnlySidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);

		if (side == Direction.UP)
			return insertOnlyHandlers[0];
		else if (side == Direction.DOWN)
			return insertOnlyHandlers[1];
		else
			return insertOnlyHandlers[2];
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
	public boolean shouldAttemptCodebreak(PlayerEntity player) {
		if (isDisabled()) {
			player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
			return false;
		}

		return IPasscodeProtected.super.shouldAttemptCodebreak(player);
	}

	@Override
	public void activate(PlayerEntity player) {
		if (!level.isClientSide && getBlockState().getBlock() instanceof AbstractKeypadFurnaceBlock)
			((AbstractKeypadFurnaceBlock) getBlockState().getBlock()).activate(level, worldPosition, player);
	}

	@Override
	public void startOpen(PlayerEntity player) {
		if (!player.isSpectator()) {
			if (openCount < 0)
				openCount = 0;

			++openCount;

			BlockState state = getBlockState();
			boolean isOpen = state.getValue(AbstractKeypadFurnaceBlock.OPEN);

			if (!isOpen) {
				level.levelEvent(null, WorldEvents.IRON_DOOR_OPEN_SOUND, worldPosition, 0);
				level.setBlockAndUpdate(worldPosition, state.setValue(AbstractKeypadFurnaceBlock.OPEN, true));
			}

			scheduleRecheck();
		}
	}

	private void scheduleRecheck() {
		level.getBlockTicks().scheduleTick(getBlockPos(), getBlockState().getBlock(), 5);
	}

	@Override
	public void stopOpen(PlayerEntity player) {
		if (!player.isSpectator())
			--openCount;
	}

	public void recheckOpen() {
		int x = worldPosition.getX();
		int y = worldPosition.getY();
		int z = worldPosition.getZ();

		openCount = getOpenCount(level, this, x, y, z);

		if (openCount > 0)
			scheduleRecheck();
		else {
			BlockState state = getBlockState();

			if (!(state.getBlock() instanceof AbstractKeypadFurnaceBlock)) {
				setRemoved();
				return;
			}

			boolean isOpen = state.getValue(AbstractKeypadFurnaceBlock.OPEN);

			if (isOpen) {
				level.levelEvent(null, WorldEvents.IRON_DOOR_CLOSE_SOUND, worldPosition, 0);
				level.setBlockAndUpdate(worldPosition, state.setValue(AbstractKeypadFurnaceBlock.OPEN, false));
			}
		}
	}

	public static int getOpenCount(World world, LockableTileEntity be, int x, int y, int z) {
		int returnValue = 0;

		for (PlayerEntity player : world.getEntitiesOfClass(PlayerEntity.class, new AxisAlignedBB(x - 5.0F, y - 5.0F, z - 5.0F, x + 1 + 5.0F, y + 1 + 5.0F, z + 1 + 5.0F))) {
			if (player.containerMenu instanceof AbstractKeypadFurnaceMenu && ((AbstractKeypadFurnaceMenu) player.containerMenu).be == be)
				++returnValue;
		}

		return returnValue;
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
	public void setSaveSalt(boolean saveSalt) {
		this.saveSalt = saveSalt;
	}

	@Override
	public boolean shouldSaveSalt() {
		return saveSalt;
	}

	public IIntArray getFurnaceData() {
		return dataAccess;
	}

	@Override
	protected ITextComponent getDefaultName() {
		return new TranslationTextComponent(getBlockState().getBlock().getDescriptionId());
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

	public boolean sendsDenylistMessage() {
		return sendDenylistMessage.get();
	}

	public boolean isDisabled() {
		return disabled.get();
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
