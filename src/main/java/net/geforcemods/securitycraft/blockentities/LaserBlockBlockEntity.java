package net.geforcemods.securitycraft.blockentities;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.ILinkedAction;
import net.geforcemods.securitycraft.api.LinkableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.IgnoreOwnerOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.api.Option.SignalLengthOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.LaserBlock;
import net.geforcemods.securitycraft.blocks.LaserFieldBlock;
import net.geforcemods.securitycraft.inventory.InsertOnlyInvWrapper;
import net.geforcemods.securitycraft.inventory.LaserBlockMenu;
import net.geforcemods.securitycraft.inventory.LensContainer;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.client.UpdateLaserColors;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.network.PacketDistributor;

public class LaserBlockBlockEntity extends LinkableBlockEntity implements MenuProvider, ContainerListener {
	private DisabledOption disabled = new DisabledOption(false) {
		@Override
		public void toggle() {
			setValue(!get());
			setLasersAccordingToDisabledOption();
		}
	};
	private IgnoreOwnerOption ignoreOwner = new IgnoreOwnerOption(true);
	private IntOption signalLength = new SignalLengthOption(50);
	private Map<Direction, Boolean> sideConfig = Util.make(() -> {
		EnumMap<Direction, Boolean> map = new EnumMap<>(Direction.class);

		for (Direction dir : Direction.values()) {
			map.put(dir, true);
		}

		return map;
	});
	private LensContainer lenses = new LensContainer(6);
	private long lastToggleTime;

	public LaserBlockBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.LASER_BLOCK_BLOCK_ENTITY.get(), pos, state);
		lenses.addListener(LaserBlockBlockEntity.this);
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("sideConfig", saveSideConfig(sideConfig));

		for (int i = 0; i < lenses.getContainerSize(); i++) {
			tag.put("lens" + i, lenses.getItem(i).save(new CompoundTag()));
		}
	}

	public static CompoundTag saveSideConfig(Map<Direction, Boolean> sideConfig) {
		CompoundTag sideConfigTag = new CompoundTag();

		sideConfig.forEach((dir, enabled) -> sideConfigTag.putBoolean(dir.getName(), enabled));
		return sideConfigTag;
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		sideConfig = loadSideConfig(tag.getCompound("sideConfig"));

		for (int i = 0; i < lenses.getContainerSize(); i++) {
			lenses.setItemExclusively(i, ItemStack.of(tag.getCompound("lens" + i)));
		}

		lenses.setChanged();
	}

	public static Map<Direction, Boolean> loadSideConfig(CompoundTag sideConfigTag) {
		EnumMap<Direction, Boolean> sideConfig = new EnumMap<>(Direction.class);

		for (Direction dir : Direction.values()) {
			if (sideConfigTag.contains(dir.getName(), Tag.TAG_BYTE))
				sideConfig.put(dir, sideConfigTag.getBoolean(dir.getName()));
			else
				sideConfig.put(dir, true);
		}

		return sideConfig;
	}

	@Override
	protected void onLinkedBlockAction(ILinkedAction action, List<LinkableBlockEntity> excludedBEs) {
		if (action instanceof ILinkedAction.OptionChanged<?> optionChanged) {
			Option<?> option = optionChanged.option();

			if (option.getName().equals("disabled")) {
				disabled.copy(option);
				setLasersAccordingToDisabledOption();
			}
			else if (option.getName().equals("ignoreOwner"))
				ignoreOwner.copy(option);
			else if (option.getName().equals("signalLength")) {
				signalLength.copy(option);
				turnOffRedstoneOutput();
			}
		}
		else if (action instanceof ILinkedAction.ModuleInserted moduleInserted)
			insertModule(moduleInserted.stack(), moduleInserted.wasModuleToggled());
		else if (action instanceof ILinkedAction.ModuleRemoved moduleRemoved)
			removeModule(moduleRemoved.moduleType(), moduleRemoved.wasModuleToggled());
		else if (action instanceof ILinkedAction.OwnerChanged ownerChanged) {
			Owner owner = ownerChanged.newOwner();

			setOwner(owner.getUUID(), owner.getName());
		}
		else if (action instanceof ILinkedAction.StateChanged<?> stateChanged) {
			BlockState state = getBlockState();

			if (stateChanged.property() == LaserBlock.POWERED) {
				int signalLength = getSignalLength();

				level.setBlockAndUpdate(worldPosition, state.cycle(LaserBlock.POWERED));
				BlockUtils.updateIndirectNeighbors(level, worldPosition, SCContent.LASER_BLOCK.get());

				if (signalLength > 0)
					level.scheduleTick(worldPosition, SCContent.LASER_BLOCK.get(), signalLength);
			}
		}

		excludedBEs.add(this);
		propagate(action, excludedBEs);
	}

	@Override
	public <T> void onOptionChanged(Option<T> option) {
		if (option == signalLength)
			turnOffRedstoneOutput();

		super.onOptionChanged(option);
	}

	private void turnOffRedstoneOutput() {
		level.setBlockAndUpdate(worldPosition, getBlockState().setValue(LaserBlock.POWERED, false));
		BlockUtils.updateIndirectNeighbors(level, worldPosition, getBlockState().getBlock());
	}

	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleInserted(stack, module, toggled);

		if (module == ModuleType.DISGUISE)
			DisguisableBlockEntity.onDisguiseModuleInserted(this, stack, toggled);
		else if (module == ModuleType.SMART)
			applyExistingSideConfig();
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleRemoved(stack, module, toggled);

		if (module == ModuleType.DISGUISE)
			DisguisableBlockEntity.onDisguiseModuleRemoved(this, stack, toggled);
		else if (module == ModuleType.REDSTONE) {
			if (getBlockState().getValue(LaserBlock.POWERED))
				turnOffRedstoneOutput();
		}
		else if (module == ModuleType.SMART)
			applyExistingSideConfig();
	}

	@Override
	public void containerChanged(Container container) {
		if (level == null)
			return;

		for (Direction direction : Direction.values()) {
			int i = 1;
			BlockPos pos = getBlockPos();
			BlockPos modifiedPos = pos.relative(direction, i);
			BlockState stateAtModifiedPos = level.getBlockState(modifiedPos);
			List<BlockPos> positionsToUpdate = new ArrayList<>();

			while (i < ConfigHandler.SERVER.laserBlockRange.get() && stateAtModifiedPos.getBlock() != SCContent.LASER_BLOCK.get()) {
				modifiedPos = pos.relative(direction, ++i);
				stateAtModifiedPos = level.getBlockState(modifiedPos);
				positionsToUpdate.add(modifiedPos);
			}

			if (level.getBlockEntity(modifiedPos) instanceof LaserBlockBlockEntity otherLaser) {
				otherLaser.getLensContainer().setItemExclusively(direction.getOpposite().ordinal(), lenses.getItem(direction.ordinal()));

				if (!level.isClientSide)
					PacketDistributor.DIMENSION.with(level.dimension()).send(new UpdateLaserColors(positionsToUpdate));

				level.sendBlockUpdated(modifiedPos, stateAtModifiedPos, stateAtModifiedPos, 2);
			}
		}

		setChanged();
	}

	@Override
	public void handleUpdateTag(CompoundTag tag) {
		super.handleUpdateTag(tag);
		DisguisableBlockEntity.onHandleUpdateTag(this);
	}

	@Override
	public void readOptions(CompoundTag tag) {
		if (tag.contains("enabled"))
			tag.putBoolean("disabled", !tag.getBoolean("enabled")); //legacy support

		for (Option<?> option : customOptions()) {
			option.load(tag);
		}
	}

	public static IItemHandler getCapability(LaserBlockBlockEntity be, Direction side) {
		return BlockUtils.isAllowedToExtractFromProtectedBlock(side, be) ? new InvWrapper(be.lenses) : new InsertOnlyInvWrapper(be.lenses);
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
		return new LaserBlockMenu(id, level, worldPosition, sideConfig, inventory);
	}

	@Override
	public Component getDisplayName() {
		return super.getDisplayName();
	}

	public LensContainer getLensContainer() {
		return lenses;
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		DisguisableBlockEntity.onSetRemoved(this);
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

	@Override
	public ModelData getModelData() {
		return DisguisableBlockEntity.getModelData(this);
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

	public void applyNewSideConfig(Map<Direction, Boolean> sideConfig, Player player) {
		sideConfig.forEach((direction, enabled) -> setSideEnabled(direction, enabled, player));
	}

	public void applyExistingSideConfig() {
		for (Direction direction : Direction.values()) {
			toggleLaserOnSide(direction, isSideEnabled(direction), null, false);
		}
	}

	public void setSideEnabled(Direction direction, boolean enabled, Player player) {
		sideConfig.put(direction, enabled);

		if (isModuleEnabled(ModuleType.SMART))
			toggleLaserOnSide(direction, enabled, player, true);
	}

	public void toggleLaserOnSide(Direction direction, boolean enabled, Player player, boolean modifyOtherLaser) {
		int i = 1;
		BlockPos pos = getBlockPos();
		BlockPos modifiedPos = pos.relative(direction, i);
		BlockState stateAtModifiedPos = level.getBlockState(modifiedPos);

		while (i < ConfigHandler.SERVER.laserBlockRange.get() && stateAtModifiedPos.getBlock() != SCContent.LASER_BLOCK.get()) {
			modifiedPos = pos.relative(direction, ++i);
			stateAtModifiedPos = level.getBlockState(modifiedPos);
		}

		if (modifyOtherLaser && level.getBlockEntity(modifiedPos) instanceof LaserBlockBlockEntity otherLaser)
			otherLaser.sideConfig.put(direction.getOpposite(), enabled);

		if (enabled && getBlockState().getBlock() instanceof LaserBlock block)
			block.setLaser(level, pos, direction, player);
		else if (!enabled) {
			int boundType = LaserFieldBlock.getBoundType(direction);

			BlockUtils.removeInSequence((directionToCheck, stateToCheck) -> stateToCheck.is(SCContent.LASER_FIELD.get()) && stateToCheck.getValue(LaserFieldBlock.BOUNDTYPE) == boundType, level, worldPosition, direction);
		}
	}

	public Map<Direction, Boolean> getSideConfig() {
		return sideConfig;
	}

	public boolean isSideEnabled(Direction dir) {
		return !isModuleEnabled(ModuleType.SMART) || sideConfig.getOrDefault(dir, true);
	}

	private void setLasersAccordingToDisabledOption() {
		if (isEnabled())
			((LaserBlock) getBlockState().getBlock()).setLaser(level, worldPosition, null);
		else
			LaserBlock.destroyAdjacentLasers(level, worldPosition);
	}

	public ModuleType synchronizeWith(LaserBlockBlockEntity that) {
		if (!LinkableBlockEntity.isLinkedWith(this, that)) {
			Map<ItemStack, Boolean> bothInsertedModules = new Object2BooleanArrayMap<>();
			List<ModuleType> thisInsertedModules = getInsertedModules();
			List<ModuleType> thatInsertedModules = that.getInsertedModules();

			for (ModuleType type : thisInsertedModules) {
				ItemStack thisModule = getModule(type);

				if (thatInsertedModules.contains(type) && !Objects.equals(thisModule.getTag(), that.getModule(type).getTag()))
					return type;

				bothInsertedModules.put(thisModule.copy(), isModuleEnabled(type));
				removeModule(type, false);
			}

			for (ModuleType type : thatInsertedModules) {
				bothInsertedModules.put(that.getModule(type).copy(), that.isModuleEnabled(type));
				that.removeModule(type, false);
				propagate(new ILinkedAction.ModuleRemoved(type, false), that);
			}

			readOptions(that.writeOptions(new CompoundTag()));
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
