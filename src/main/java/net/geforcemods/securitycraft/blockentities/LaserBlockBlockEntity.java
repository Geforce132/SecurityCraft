package net.geforcemods.securitycraft.blockentities;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ILinkedAction;
import net.geforcemods.securitycraft.api.LinkableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.IgnoreOwnerOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.api.Option.SignalLengthOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.blocks.LaserBlock;
import net.geforcemods.securitycraft.blocks.LaserFieldBlock;
import net.geforcemods.securitycraft.inventory.InsertOnlyInvWrapper;
import net.geforcemods.securitycraft.inventory.LensContainer;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.client.RefreshDiguisedModel;
import net.geforcemods.securitycraft.network.client.UpdateLaserColors;
import net.geforcemods.securitycraft.util.BlockEntityRenderDelegate;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class LaserBlockBlockEntity extends LinkableBlockEntity implements IInventoryChangedListener {
	private DisabledOption disabled = new DisabledOption(false) {
		@Override
		public void toggle() {
			setValue(!get());

			setLasersAccordingToDisabledOption();
		}
	};
	private IgnoreOwnerOption ignoreOwner = new IgnoreOwnerOption(true);
	private IntOption signalLength = new SignalLengthOption(this::getPos, 50);
	private Map<EnumFacing, Boolean> sideConfig;
	private IItemHandler insertOnlyHandler, lensHandler;
	private LensContainer lenses = new LensContainer(6);
	private long lastToggleTime;

	public LaserBlockBlockEntity() {
		lenses.addInventoryChangeListener(this);
		sideConfig = new EnumMap<>(EnumFacing.class);

		for (EnumFacing dir : EnumFacing.values()) {
			sideConfig.put(dir, true);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setTag("sideConfig", saveSideConfig(sideConfig));

		for (int i = 0; i < lenses.getSizeInventory(); i++) {
			tag.setTag("lens" + i, lenses.getStackInSlot(i).writeToNBT(new NBTTagCompound()));
		}

		return tag;
	}

	public static NBTTagCompound saveSideConfig(Map<EnumFacing, Boolean> sideConfig) {
		NBTTagCompound sideConfigTag = new NBTTagCompound();

		sideConfig.forEach((dir, enabled) -> sideConfigTag.setBoolean(dir.getName(), enabled));
		return sideConfigTag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		sideConfig = loadSideConfig(tag.getCompoundTag("sideConfig"));

		for (int i = 0; i < lenses.getSizeInventory(); i++) {
			lenses.setItemExclusively(i, new ItemStack(tag.getCompoundTag("lens" + i)));
		}

		lenses.markDirty();
	}

	public static Map<EnumFacing, Boolean> loadSideConfig(NBTTagCompound sideConfigTag) {
		Map<EnumFacing, Boolean> sideConfig = new EnumMap<>(EnumFacing.class);

		for (EnumFacing dir : EnumFacing.values()) {
			if (sideConfigTag.hasKey(dir.getName(), Constants.NBT.TAG_BYTE))
				sideConfig.put(dir, sideConfigTag.getBoolean(dir.getName()));
			else
				sideConfig.put(dir, true);
		}

		return sideConfig;
	}

	@Override
	protected void onLinkedBlockAction(ILinkedAction action, List<LinkableBlockEntity> excludedTEs) {
		if (action instanceof ILinkedAction.OptionChanged) {
			Option<?> option = ((ILinkedAction.OptionChanged<?>) action).option;

			if (option.getName().equals("disabled")) {
				disabled.copy(option);
				setLasersAccordingToDisabledOption();
			}
			else if (option.getName().equals("ignoreOwner"))
				ignoreOwner.copy(option);
			else if (option.getName().equals("signalLength")) {
				signalLength.copy(option);
				onRemoveRedstoneModule();
			}
		}
		else if (action instanceof ILinkedAction.ModuleInserted) {
			ILinkedAction.ModuleInserted moduleInserted = (ILinkedAction.ModuleInserted) action;
			ItemStack module = moduleInserted.stack;
			boolean toggled = moduleInserted.wasModuleToggled;

			insertModule(module, toggled);
		}
		else if (action instanceof ILinkedAction.ModuleRemoved) {
			ILinkedAction.ModuleRemoved moduleRemoved = (ILinkedAction.ModuleRemoved) action;
			ModuleType module = moduleRemoved.moduleType;
			boolean toggled = moduleRemoved.wasModuleToggled;

			removeModule(module, toggled);
		}
		else if (action instanceof ILinkedAction.OwnerChanged) {
			Owner owner = ((ILinkedAction.OwnerChanged) action).newOwner;

			setOwner(owner.getUUID(), owner.getName());
		}
		else if (action instanceof ILinkedAction.StateChanged<?>) {
			IBlockState state = world.getBlockState(pos);

			if (((ILinkedAction.StateChanged<?>) action).property == LaserBlock.POWERED) {
				int signalLength = getSignalLength();

				world.setBlockState(pos, state.cycleProperty(LaserBlock.POWERED));
				BlockUtils.updateIndirectNeighbors(world, pos, SCContent.laserBlock);

				if (signalLength > 0)
					world.scheduleUpdate(pos, SCContent.laserBlock, signalLength);
			}
		}

		excludedTEs.add(this);
		propagate(action, excludedTEs);
	}

	@Override
	public <T> void onOptionChanged(Option<T> option) {
		if (option == signalLength)
			onRemoveRedstoneModule();

		super.onOptionChanged(option);
	}

	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleInserted(stack, module, toggled);

		if (module == ModuleType.DISGUISE)
			onInsertDisguiseModule(stack, toggled);
		else if (module == ModuleType.SMART)
			applyExistingSideConfig();
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleRemoved(stack, module, toggled);

		if (module == ModuleType.DISGUISE)
			onRemoveDisguiseModule(stack, toggled);
		else if (module == ModuleType.REDSTONE)
			onRemoveRedstoneModule();
		else if (module == ModuleType.SMART)
			applyExistingSideConfig();
	}

	private void onInsertDisguiseModule(ItemStack stack, boolean toggled) {
		if (!world.isRemote)
			SecurityCraft.network.sendToAllTracking(new RefreshDiguisedModel(pos, true, stack, toggled), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 0));
		else {
			IBlockState state = world.getBlockState(pos);

			BlockEntityRenderDelegate.putDisguisedTeRenderer(this, stack);

			if (state.getLightValue(world, pos) > 0)
				world.checkLight(pos);
		}
	}

	private void onRemoveDisguiseModule(ItemStack stack, boolean toggled) {
		if (!world.isRemote)
			SecurityCraft.network.sendToAllTracking(new RefreshDiguisedModel(pos, false, stack, toggled), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 0));
		else {
			IBlockState disguisedState = ((DisguisableBlock) blockType).getDisguisedBlockStateFromStack(null, null, stack);

			BlockEntityRenderDelegate.DISGUISED_BLOCK.removeDelegateOf(this);

			if (disguisedState != null && disguisedState.getLightValue(world, pos) > 0)
				world.checkLight(pos);
		}
	}

	private void onRemoveRedstoneModule() {
		IBlockState state = world.getBlockState(pos);

		if (state.getValue(LaserBlock.POWERED)) {
			world.setBlockState(pos, state.withProperty(LaserBlock.POWERED, false));
			BlockUtils.updateIndirectNeighbors(world, pos, SCContent.laserBlock);
		}
	}

	@Override
	public void onInventoryChanged(IInventory container) {
		if (world == null)
			return;

		for (EnumFacing direction : EnumFacing.values()) {
			int i = 1;
			BlockPos pos = getPos();
			BlockPos modifiedPos = pos.offset(direction, i);
			IBlockState stateAtModifiedPos = world.getBlockState(modifiedPos);
			List<BlockPos> positionsToUpdate = new ArrayList<>();

			while (i < ConfigHandler.laserBlockRange && stateAtModifiedPos.getBlock() != SCContent.laserBlock) {
				modifiedPos = pos.offset(direction, ++i);
				stateAtModifiedPos = world.getBlockState(modifiedPos);
				positionsToUpdate.add(modifiedPos);
			}

			TileEntity be = world.getTileEntity(modifiedPos);

			if (be instanceof LaserBlockBlockEntity) {
				LaserBlockBlockEntity otherLaser = (LaserBlockBlockEntity) be;

				otherLaser.getLensContainer().setItemExclusively(direction.getOpposite().ordinal(), lenses.getStackInSlot(direction.ordinal()));

				if (!world.isRemote)
					SecurityCraft.network.sendToDimension(new UpdateLaserColors(positionsToUpdate), world.provider.getDimension());

				world.notifyBlockUpdate(modifiedPos, stateAtModifiedPos, stateAtModifiedPos, 2);
			}
		}

		markDirty();
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return BlockUtils.isAllowedToExtractFromProtectedBlock(facing, this) ? (T) getNormalHandler() : (T) getInsertOnlyHandler();
		else
			return super.getCapability(capability, facing);
	}

	private IItemHandler getInsertOnlyHandler() {
		if (insertOnlyHandler == null)
			insertOnlyHandler = new InsertOnlyInvWrapper(lenses);

		return insertOnlyHandler;
	}

	private IItemHandler getNormalHandler() {
		if (lensHandler == null)
			lensHandler = new InvWrapper(lenses);

		return lensHandler;
	}

	public LensContainer getLensContainer() {
		return lenses;
	}

	@Override
	public void onLoad() {
		super.onLoad();

		if (world.isRemote)
			BlockEntityRenderDelegate.putDisguisedTeRenderer(this, getModule(ModuleType.DISGUISE));
	}

	@Override
	public void readOptions(NBTTagCompound tag) {
		if (tag.hasKey("enabled"))
			tag.setBoolean("disabled", !tag.getBoolean("enabled")); //legacy support

		for (Option<?> option : customOptions()) {
			option.load(tag);
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();

		if (world.isRemote)
			BlockEntityRenderDelegate.DISGUISED_BLOCK.removeDelegateOf(this);
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.HARMING, ModuleType.ALLOWLIST, ModuleType.DISGUISE, ModuleType.REDSTONE, ModuleType.SMART
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				disabled, ignoreOwner, signalLength
		};
	}

	public boolean isEnabled() {
		return !disabled.get();
	}

	@Override
	public boolean ignoresOwner() {
		return ignoreOwner.get();
	}

	public int getSignalLength() {
		return signalLength.get();
	}

	public void setLastToggleTime(long lastToggleTime) {
		this.lastToggleTime = lastToggleTime;
	}

	public long getLastToggleTime() {
		return lastToggleTime;
	}

	public long timeSinceLastToggle() {
		return System.currentTimeMillis() - getLastToggleTime();
	}

	public void applyNewSideConfig(Map<EnumFacing, Boolean> sideConfig, EntityPlayer player) {
		sideConfig.forEach((direction, enabled) -> setSideEnabled(direction, enabled, player));
	}

	public void applyExistingSideConfig() {
		for (EnumFacing direction : EnumFacing.values()) {
			toggleLaserOnSide(direction, isSideEnabled(direction), null, false);
		}
	}

	public void setSideEnabled(EnumFacing direction, boolean enabled, EntityPlayer player) {
		sideConfig.put(direction, enabled);

		if (isModuleEnabled(ModuleType.SMART))
			toggleLaserOnSide(direction, enabled, player, true);
	}

	public void toggleLaserOnSide(EnumFacing direction, boolean enabled, EntityPlayer player, boolean modifyOtherLaser) {
		int i = 1;
		BlockPos pos = getPos();
		BlockPos modifiedPos = pos.offset(direction, i);
		IBlockState ownState = world.getBlockState(pos);
		IBlockState stateAtModifiedPos = world.getBlockState(modifiedPos);

		while (i < ConfigHandler.laserBlockRange && stateAtModifiedPos.getBlock() != SCContent.laserBlock) {
			modifiedPos = pos.offset(direction, ++i);
			stateAtModifiedPos = world.getBlockState(modifiedPos);
		}

		if (modifyOtherLaser) {
			TileEntity te = world.getTileEntity(modifiedPos);

			if (te instanceof LaserBlockBlockEntity)
				((LaserBlockBlockEntity) te).sideConfig.put(direction.getOpposite(), enabled);
		}

		if (enabled) {
			Block block = ownState.getBlock();

			if (block instanceof LaserBlock)
				((LaserBlock) block).setLaser(world, pos, direction, player);
		}
		else {
			int boundType = LaserFieldBlock.getBoundType(direction);

			BlockUtils.removeInSequence((directionToCheck, stateToCheck) -> stateToCheck.getBlock() == SCContent.laserField && stateToCheck.getValue(LaserFieldBlock.BOUNDTYPE) == boundType, world, pos, direction);
		}
	}

	public Map<EnumFacing, Boolean> getSideConfig() {
		return sideConfig;
	}

	public boolean isSideEnabled(EnumFacing dir) {
		return !isModuleEnabled(ModuleType.SMART) || sideConfig.getOrDefault(dir, true);
	}

	private void setLasersAccordingToDisabledOption() {
		Block block = world.getBlockState(pos).getBlock();

		if (block != SCContent.laserBlock)
			return;

		if (isEnabled())
			((LaserBlock) block).setLaser(world, pos, null);
		else
			LaserBlock.destroyAdjacentLasers(world, pos);
	}

	public ModuleType synchronizeWith(LaserBlockBlockEntity that) {
		if (!LinkableBlockEntity.isLinkedWith(this, that)) {
			Map<ItemStack, Boolean> bothInsertedModules = new Object2BooleanArrayMap<>();
			List<ModuleType> thisInsertedModules = getInsertedModules();
			List<ModuleType> thatInsertedModules = that.getInsertedModules();

			for (ModuleType type : thisInsertedModules) {
				ItemStack thisModule = getModule(type);

				if (thatInsertedModules.contains(type) && !ItemStack.areItemStackShareTagsEqual(thisModule, that.getModule(type)))
					return type;

				bothInsertedModules.put(thisModule.copy(), isModuleEnabled(type));
				removeModule(type, false);
			}

			for (ModuleType type : thatInsertedModules) {
				bothInsertedModules.put(that.getModule(type).copy(), that.isModuleEnabled(type));
				that.removeModule(type, false);
				propagate(new ILinkedAction.ModuleRemoved(type, false), that);
			}

			readOptions(that.writeOptions(new NBTTagCompound()));
			LinkableBlockEntity.link(this, that);

			for (Entry<ItemStack, Boolean> entry : bothInsertedModules.entrySet()) {
				ItemStack module = entry.getKey();
				ModuleItem item = (ModuleItem) module.getItem();
				ModuleType type = item.getModuleType();

				insertModule(entry.getKey(), false);
				propagate(new ILinkedAction.ModuleInserted(module, item, false), this);
				toggleModuleState(type, entry.getValue());
				propagate(new ILinkedAction.ModuleInserted(module, item, true), this);
			}
		}

		return null;
	}
}
