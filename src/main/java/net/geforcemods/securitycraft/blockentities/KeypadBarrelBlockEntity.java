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
import net.geforcemods.securitycraft.api.Option.SendAllowlistMessageOption;
import net.geforcemods.securitycraft.api.Option.SendDenylistMessageOption;
import net.geforcemods.securitycraft.api.Option.SmartModuleCooldownOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.KeypadBarrelBlock;
import net.geforcemods.securitycraft.blocks.KeypadBarrelBlock.LidFacing;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.BarrelTileEntity;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class KeypadBarrelBlockEntity extends BarrelTileEntity implements IPasscodeProtected, IOwnable, IModuleInventory, ICustomizable, ILockable, ISentryBulletContainer {
	private LazyOptional<IItemHandler> insertOnlyHandler;
	private byte[] passcode;
	private UUID saltKey;
	private boolean saveSalt = false;
	private Owner owner = new Owner();
	private NonNullList<ItemStack> modules = NonNullList.<ItemStack>withSize(getMaxNumberOfModules(), ItemStack.EMPTY);
	private BooleanOption sendAllowlistMessage = new SendAllowlistMessageOption(false);
	private BooleanOption sendDenylistMessage = new SendDenylistMessageOption(true);
	private SmartModuleCooldownOption smartModuleCooldown = new SmartModuleCooldownOption(this::getBlockPos);
	private long cooldownEnd = 0;
	private Map<ModuleType, Boolean> moduleStates = new EnumMap<>(ModuleType.class);
	private ResourceLocation previousBarrel;

	@Override
	public TileEntityType<?> getType() {
		return SCContent.KEYPAD_BARREL_BLOCK_ENTITY.get();
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
		savePasscodeAndSalt(tag);

		if (owner != null)
			owner.save(tag, needsValidation());

		if (previousBarrel != null)
			tag.putString("previous_barrel", previousBarrel.toString());

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

		if (tag.contains("previous_barrel")) {
			String savedPreviousBarrel = tag.getString("previous_barrel");

			if (!savedPreviousBarrel.isEmpty()) {
				ResourceLocation parsedPreviousBarrel = new ResourceLocation(savedPreviousBarrel);

				if (parsedPreviousBarrel.getPath() != null && !parsedPreviousBarrel.getPath().isEmpty())
					previousBarrel = parsedPreviousBarrel;
			}
		}

		if (tag.contains("sendMessage") && !tag.getBoolean("sendMessage")) {
			sendAllowlistMessage.setValue(false);
			sendDenylistMessage.setValue(false);
		}
	}

	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module, boolean toggled) {
		IModuleInventory.super.onModuleInserted(stack, module, toggled);

		if (module == ModuleType.DISGUISE)
			DisguisableBlockEntity.onDisguiseModuleInserted(this, stack, toggled);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
		IModuleInventory.super.onModuleRemoved(stack, module, toggled);

		if (module == ModuleType.DISGUISE)
			DisguisableBlockEntity.onDisguiseModuleRemoved(this, stack, toggled);
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
		super.onDataPacket(net, packet);
		DisguisableBlockEntity.onHandleUpdateTag(this);
	}

	@Override
	public void onLoad() {
		super.onLoad();
		DisguisableBlockEntity.onHandleUpdateTag(this);
	}

	@Override
	public ITextComponent getDefaultName() {
		return Utils.localize(SCContent.KEYPAD_BARREL.get().getDescriptionId());
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
			insertOnlyHandler = LazyOptional.of(() -> new InsertOnlyInvWrapper(KeypadBarrelBlockEntity.this));

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
		if (!level.isClientSide && getBlockState().getBlock() instanceof KeypadBarrelBlock)
			((KeypadBarrelBlock) getBlockState().getBlock()).activate(getBlockState(), level, worldPosition, player);
	}

	@Override
	public void dropAllModules() {
		for (ItemStack module : getInventory()) {
			if (!(module.getItem() instanceof ModuleItem))
				continue;

			Block.popResource(level, worldPosition, module);
		}

		getInventory().clear();
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
	public Owner getOwner() {
		return owner;
	}

	@Override
	public void setOwner(String uuid, String name) {
		owner.set(uuid, name);
		setChanged();
	}

	@Override
	public NonNullList<ItemStack> getInventory() {
		return modules;
	}

	@Override
	public void startCooldown() {
		startCooldown(System.currentTimeMillis());
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
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.ALLOWLIST, ModuleType.DENYLIST, ModuleType.DISGUISE, ModuleType.SMART, ModuleType.HARMING
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
	public void setRemoved() {
		super.setRemoved();
		DisguisableBlockEntity.onSetRemoved(this);
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

	@Override
	public void recheckOpen() {
		int x = worldPosition.getX();
		int y = worldPosition.getY();
		int z = worldPosition.getZ();
		BlockState state = getBlockState();

		openCount = ChestTileEntity.getOpenCount(level, this, x, y, z);

		if (openCount > 0)
			level.getBlockTicks().scheduleTick(worldPosition, state.getBlock(), 5);
		else {
			if (!state.is(SCContent.KEYPAD_BARREL.get())) {
				setRemoved();
				return;
			}

			if (state.getValue(KeypadBarrelBlock.OPEN)) {
				playSound(state, SoundEvents.BARREL_CLOSE);
				level.setBlock(worldPosition, state.setValue(KeypadBarrelBlock.OPEN, false), 3);
			}
		}
	}

	@Override
	public void playSound(BlockState state, SoundEvent sound) {
		Direction normalFacing = directionFromLidFacing(state, state.getValue(KeypadBarrelBlock.LID_FACING));
		Vector3i facingNormal = normalFacing.getNormal();
		double x = worldPosition.getX() + 0.5D + facingNormal.getX() / 2.0D;
		double y = worldPosition.getY() + 0.5D + facingNormal.getY() / 2.0D;
		double z = worldPosition.getZ() + 0.5D + facingNormal.getZ() / 2.0D;

		level.playSound(null, x, y, z, sound, SoundCategory.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
	}

	public void setPreviousBarrel(Block previousBarrel) {
		this.previousBarrel = previousBarrel.getRegistryName();
	}

	public ResourceLocation getPreviousBarrel() {
		return previousBarrel;
	}

	private Direction directionFromLidFacing(BlockState state, LidFacing lidFacing) {
		switch (lidFacing) {
			case UP:
				return Direction.UP;
			case SIDEWAYS:
				return state.getValue(KeypadBarrelBlock.HORIZONTAL_FACING);
			case DOWN:
				return Direction.DOWN;
			default:
				return Direction.NORTH;
		}
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
