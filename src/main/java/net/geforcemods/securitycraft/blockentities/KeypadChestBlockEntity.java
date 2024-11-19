package net.geforcemods.securitycraft.blockentities;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.INameSetter;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.SendAllowlistMessageOption;
import net.geforcemods.securitycraft.api.Option.SendDenylistMessageOption;
import net.geforcemods.securitycraft.api.Option.SmartModuleCooldownOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.KeypadChestBlock;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.inventory.InsertOnlyDoubleChestHandler;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class KeypadChestBlockEntity extends TileEntityChest implements IPasscodeProtected, IOwnable, IModuleInventory, ICustomizable, INameSetter, ILockable {
	private InsertOnlyDoubleChestHandler insertOnlyHandler;
	private byte[] passcode;
	private UUID saltKey;
	private Owner owner = new Owner();
	private NonNullList<ItemStack> modules = NonNullList.<ItemStack>withSize(getMaxNumberOfModules(), ItemStack.EMPTY);
	private BooleanOption sendAllowlistMessage = new SendAllowlistMessageOption(false);
	private BooleanOption sendDenylistMessage = new SendDenylistMessageOption(true);
	private SmartModuleCooldownOption smartModuleCooldown = new SmartModuleCooldownOption(this::getPos);
	private long cooldownEnd = 0;
	private Map<ModuleType, Boolean> moduleStates = new EnumMap<>(ModuleType.class);
	private ResourceLocation previousChest;

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		long cooldownLeft;

		super.writeToNBT(tag);
		writeModuleInventory(tag);
		writeModuleStates(tag);
		writeOptions(tag);
		cooldownLeft = getCooldownEnd() - System.currentTimeMillis();
		tag.setLong("cooldownLeft", cooldownLeft <= 0 ? -1 : cooldownLeft);

		if (saltKey != null)
			tag.setUniqueId("saltKey", saltKey);

		if (passcode != null)
			tag.setString("passcode", PasscodeUtils.bytesToString(passcode));

		if (owner != null)
			owner.save(tag, needsValidation());

		if (previousChest != null)
			tag.setString("previous_chest", previousChest.toString());

		return tag;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		modules = readModuleInventory(tag);
		moduleStates = readModuleStates(tag);
		readOptions(tag);
		cooldownEnd = System.currentTimeMillis() + tag.getLong("cooldownLeft");
		loadSaltKey(tag);
		loadPasscode(tag);
		owner.load(tag);
		previousChest = new ResourceLocation(tag.getString("previous_chest"));

		if (tag.hasKey("sendMessage") && !tag.getBoolean("sendMessage")) {
			sendAllowlistMessage.setValue(false);
			sendDenylistMessage.setValue(false);
		}
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return PasscodeUtils.filterPasscodeAndSaltFromTag(writeToNBT(new NBTTagCompound()));
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(pos, 1, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	@Override
	public void onLoad() {
		super.onLoad();
		DisguisableBlockEntity.onLoad(this);
	}

	@Override
	public void invalidate() {
		super.invalidate();
		DisguisableBlockEntity.onInvalidate(this);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return BlockUtils.isAllowedToExtractFromProtectedObject(facing, this) ? (T) super.getCapability(capability, facing) : (T) getInsertOnlyHandler();
		else
			return super.getCapability(capability, facing);
	}

	private InsertOnlyDoubleChestHandler getInsertOnlyHandler() {
		if (insertOnlyHandler == null || insertOnlyHandler.needsRefresh())
			insertOnlyHandler = InsertOnlyDoubleChestHandler.get(this);

		return insertOnlyHandler;
	}

	public IItemHandler getHandlerForSentry(Sentry entity) {
		if (entity.getOwner().owns(this))
			return super.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
		else
			return null;
	}

	@Override
	public boolean enableHack() {
		return true;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return slot >= 100 ? getModuleInSlot(slot) : super.getStackInSlot(slot);
	}

	@Override
	public void activate(EntityPlayer player) {
		if (!world.isRemote && !isBlocked())
			((KeypadChestBlock) getBlockType()).activate(world, pos, player);
	}

	@Override
	public void openPasscodeGUI(World world, BlockPos pos, EntityPlayer player) {
		if (!world.isRemote && !isBlocked())
			IPasscodeProtected.super.openPasscodeGUI(world, pos, player);
	}

	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module, boolean toggled) {
		IModuleInventory.super.onModuleInserted(stack, module, toggled);
		addOrRemoveModuleFromAttached(stack, false, toggled);

		if (module == ModuleType.DISGUISE)
			DisguisableBlockEntity.onInsertDisguiseModule(this, stack, toggled);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
		IModuleInventory.super.onModuleRemoved(stack, module, toggled);
		addOrRemoveModuleFromAttached(stack, true, toggled);

		if (module == ModuleType.DISGUISE)
			DisguisableBlockEntity.onRemoveDisguiseModule(this, stack, toggled);
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
	public <T> void onOptionChanged(Option<T> option) {
		KeypadChestBlockEntity otherTe = findOther();

		if (otherTe != null) {
			if (option instanceof BooleanOption) {
				BooleanOption bo = (BooleanOption) option;

				if (option == sendAllowlistMessage)
					otherTe.setSendsAllowlistMessage(bo.get());
				else if (option == sendDenylistMessage)
					otherTe.setSendsDenylistMessage(bo.get());
				else
					throw new UnsupportedOperationException("Unhandled option synchronization in keypad chest! " + option.getName());
			}
			else if (option == smartModuleCooldown)
				otherTe.smartModuleCooldown.copy(option);
			else
				throw new UnsupportedOperationException("Unhandled option synchronization in keypad chest! " + option.getName());
		}
	}

	@Override
	public void dropAllModules() {
		KeypadChestBlockEntity offsetTe = findOther();

		for (ItemStack module : getInventory()) {
			if (!(module.getItem() instanceof ModuleItem))
				continue;

			if (offsetTe != null)
				offsetTe.removeModule(((ModuleItem) module.getItem()).getModuleType(), false);

			Block.spawnAsEntity(world, pos, module);
		}

		getInventory().clear();
	}

	public void addOrRemoveModuleFromAttached(ItemStack module, boolean remove, boolean toggled) {
		if (module.isEmpty() || !(module.getItem() instanceof ModuleItem))
			return;

		KeypadChestBlockEntity offsetTe = findOther();

		if (offsetTe != null) {
			ModuleType moduleType = ((ModuleItem) module.getItem()).getModuleType();

			if ((toggled && offsetTe.isModuleEnabled(moduleType) != remove) || (!toggled && offsetTe.hasModule(moduleType) != remove))
				return;

			if (remove)
				offsetTe.removeModule(moduleType, toggled);
			else
				offsetTe.insertModule(module, toggled);
		}
	}

	public KeypadChestBlockEntity findOther() {
		IBlockState state = world.getBlockState(pos);

		for (EnumFacing facing : EnumFacing.HORIZONTALS) {
			BlockPos offsetPos = pos.offset(facing);
			IBlockState offsetState = world.getBlockState(offsetPos);

			if (state.getBlock() == offsetState.getBlock()) {
				TileEntity offsetTe = world.getTileEntity(offsetPos);

				if (offsetTe instanceof KeypadChestBlockEntity)
					return (KeypadChestBlockEntity) offsetTe;
			}
		}

		return null;
	}

	@Override
	public void onOwnerChanged(IBlockState state, World level, BlockPos pos, EntityPlayer player, Owner oldOwner, Owner newOwner) {
		KeypadChestBlockEntity otherHalf = findOther();

		if (otherHalf != null)
			otherHalf.setOwner(getOwner().getUUID(), getOwner().getName());

		IOwnable.super.onOwnerChanged(state, level, pos, player, oldOwner, newOwner);
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
			IBlockState state = world.getBlockState(chestBe.pos);

			chestBe.hashAndSetPasscode(codeToSet, getSalt());
			world.notifyBlockUpdate(chestBe.pos, state, state, 2);
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

	@Override
	protected TileEntityChest getAdjacentChest(EnumFacing side) {
		BlockPos blockpos = pos.offset(side);

		if (isChestAt(blockpos)) {
			TileEntity te = world.getTileEntity(blockpos);

			if (te instanceof KeypadChestBlockEntity) {
				KeypadChestBlockEntity tileentitychest = (KeypadChestBlockEntity) te;

				tileentitychest.setNeighbor(this, side.getOpposite());
				return tileentitychest;
			}
		}

		return null;
	}

	@Override
	public boolean isChestAt(BlockPos pos) {
		return world != null && world.getBlockState(pos).getBlock() instanceof KeypadChestBlock;
	}

	@Override
	public void openInventory(EntityPlayer player) {
		if (!player.isSpectator()) {
			if (numPlayersUsing < 0)
				numPlayersUsing = 0;

			++numPlayersUsing;
			world.addBlockEvent(pos, getBlockType(), 1, numPlayersUsing);
			world.notifyNeighborsOfStateChange(pos, getBlockType(), false);

			if (isModuleEnabled(ModuleType.REDSTONE))
				world.notifyNeighborsOfStateChange(pos.down(), getBlockType(), false);
		}
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		if (!player.isSpectator() && getBlockType() instanceof KeypadChestBlock) {
			--numPlayersUsing;
			world.addBlockEvent(pos, getBlockType(), 1, numPlayersUsing);
			world.notifyNeighborsOfStateChange(pos, getBlockType(), false);

			if (isModuleEnabled(ModuleType.REDSTONE))
				world.notifyNeighborsOfStateChange(pos.down(), getBlockType(), false);
		}
	}

	public boolean isBlocked() {
		for (EnumFacing facing : EnumFacing.HORIZONTALS) {
			BlockPos pos = getPos().offset(facing);

			if (world.getBlockState(pos).getBlock() instanceof KeypadChestBlock && KeypadChestBlock.isBlocked(world, pos))
				return true;
		}

		return isSingleBlocked();
	}

	public boolean isSingleBlocked() {
		return KeypadChestBlock.isBlocked(getWorld(), getPos());
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos.add(-1, 0, -1), pos.add(2, 2, 2));
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
			IBlockState state = world.getBlockState(pos);

			cooldownEnd = start + smartModuleCooldown.get() * 50;
			world.notifyBlockUpdate(pos, state, state, 3);
			markDirty();
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
		return !((KeypadChestBlock) SCContent.keypadChest).isDoubleChest(world, pos);
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

	public boolean sendsAllowlistMessage() {
		return sendAllowlistMessage.get();
	}

	public void setSendsAllowlistMessage(boolean value) {
		IBlockState state = world.getBlockState(pos);

		sendAllowlistMessage.setValue(value);
		world.notifyBlockUpdate(pos, state, state, 3); //sync option change to client
	}

	public boolean sendsDenylistMessage() {
		return sendDenylistMessage.get();
	}

	public void setSendsDenylistMessage(boolean value) {
		IBlockState state = world.getBlockState(pos);

		sendDenylistMessage.setValue(value);
		world.notifyBlockUpdate(pos, state, state, 3); //sync option change to client
	}

	@Override
	public ITextComponent getDisplayName() {
		return hasCustomName() ? new TextComponentString(customName) : getDefaultName();
	}

	@Override
	public ITextComponent getDefaultName() {
		return Utils.localize(SCContent.keypadChest);
	}

	public void setPreviousChest(Block previousChest) {
		this.previousChest = previousChest.getRegistryName();
	}

	public ResourceLocation getPreviousChest() {
		return previousChest;
	}

	@Override
	public World myLevel() {
		return world;
	}

	@Override
	public BlockPos myPos() {
		return pos;
	}
}
