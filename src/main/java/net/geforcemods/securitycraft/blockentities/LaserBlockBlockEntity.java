package net.geforcemods.securitycraft.blockentities;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.ILinkedAction;
import net.geforcemods.securitycraft.api.LinkableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.IgnoreOwnerOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.api.Option.RespectInvisibilityOption;
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
import net.minecraft.network.Connection;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.model.data.ModelData;
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
	private RespectInvisibilityOption respectInvisibility = new RespectInvisibilityOption();
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
	public void saveAdditional(ValueOutput tag) {
		super.saveAdditional(tag);

		//TODO: does side config saving and loading work with and the same as old data?
		saveSideConfig(tag.child("sideConfig"), sideConfig);

		for (int i = 0; i < lenses.getContainerSize(); i++) {
			ItemStack lens = lenses.getItem(i);

			if (!lens.isEmpty())
				tag.store("lens" + i, ItemStack.CODEC, lens);
		}
	}

	public static void saveSideConfig(ValueOutput sideConfigTag, Map<Direction, Boolean> sideConfig) {
		sideConfig.forEach((dir, enabled) -> sideConfigTag.putBoolean(dir.getName(), enabled));
	}

	public static CompoundTag saveSideConfigToTag(Map<Direction, Boolean> sideConfig) {
		CompoundTag sideConfigTag = new CompoundTag();

		sideConfig.forEach((dir, enabled) -> sideConfigTag.putBoolean(dir.getName(), enabled));
		return sideConfigTag;
	}

	@Override
	public void loadAdditional(ValueInput tag) {
		super.loadAdditional(tag);
		sideConfig = loadSideConfig(tag.childOrEmpty("sideConfig"));

		for (int i = 0; i < lenses.getContainerSize(); i++) {
			lenses.setItemExclusively(i, tag.read("lens" + i, ItemStack.CODEC).orElse(ItemStack.EMPTY)); //TODO: test if 1.21.5 lenses stay in 1.21.6 laser blocks
		}

		lenses.setChanged();
	}

	public static Map<Direction, Boolean> loadSideConfig(ValueInput sideConfigTag) {
		EnumMap<Direction, Boolean> sideConfig = new EnumMap<>(Direction.class);

		for (Direction dir : Direction.values()) {
			sideConfig.put(dir, sideConfigTag.getBooleanOr(dir.getName(), true));
		}

		return sideConfig;
	}

	public static Map<Direction, Boolean> loadSideConfigFromTag(CompoundTag sideConfigTag) {
		EnumMap<Direction, Boolean> sideConfig = new EnumMap<>(Direction.class);

		for (Direction dir : Direction.values()) {
			if (sideConfigTag.contains(dir.getName()))
				sideConfig.put(dir, sideConfigTag.getBooleanOr(dir.getName(), false));
			else
				sideConfig.put(dir, true);
		}

		return sideConfig;
	}

	@Override
	public void preRemoveSideEffects(BlockPos pos, BlockState state) {
		if (level != null) {
			Containers.dropContents(level, pos, lenses);
			lenses.clearContent();
		}

		super.preRemoveSideEffects(pos, state);
	}

	@Override
	public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {
		MenuProvider.super.writeClientSideData(menu, buffer);
		buffer.writeBlockPos(worldPosition);
		buffer.writeNbt(saveSideConfigToTag(sideConfig));
	}

	@Override
	protected void onLinkedBlockAction(ILinkedAction action, List<LinkableBlockEntity> excludedBEs) {
		switch (action) {
			case ILinkedAction.OptionChanged(BooleanOption option) when option.getName().equals(disabled.getName()) -> {
				disabled.copy(option);
				setLasersAccordingToDisabledOption();
			}
			case ILinkedAction.OptionChanged(BooleanOption option) when option.getName().equals(ignoreOwner.getName()) ->
					ignoreOwner.copy(option);
			case ILinkedAction.OptionChanged(BooleanOption option) when option.getName().equals(respectInvisibility.getName()) ->
					respectInvisibility.copy(option);
			case ILinkedAction.OptionChanged(IntOption option) when option.getName().equals(signalLength.getName()) -> {
				signalLength.copy(option);
				turnOffRedstoneOutput();
			}
			case ILinkedAction.OptionChanged(Option<?> option) ->
					throw new UnsupportedOperationException("Unhandled option synchronization in laser block! " + option.getName());
			case ILinkedAction.ModuleInserted(ItemStack stack, ModuleItem module, boolean wasModuleToggled) ->
					insertModule(stack, wasModuleToggled);
			case ILinkedAction.ModuleRemoved(ModuleType moduleType, boolean wasModuleToggled) ->
					removeModule(moduleType, wasModuleToggled);
			case ILinkedAction.OwnerChanged(Owner newOwner) -> setOwner(newOwner.getUUID(), newOwner.getName());
			case ILinkedAction.StateChanged(BooleanProperty property, Boolean oldValue, Boolean newValue) when property == LaserBlock.POWERED -> {
				if (timeSinceLastToggle() < 500)
					setLastToggleTime(System.currentTimeMillis());
				else {
					BlockState state = getBlockState();
					int signalLength = getSignalLength();

					setLastToggleTime(System.currentTimeMillis());
					level.setBlockAndUpdate(worldPosition, state.cycle(LaserBlock.POWERED));
					BlockUtils.updateIndirectNeighbors(level, worldPosition, SCContent.LASER_BLOCK.get());

					if (signalLength > 0)
						level.scheduleTick(worldPosition, SCContent.LASER_BLOCK.get(), signalLength);
				}
			}
			default -> {
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
					PacketDistributor.sendToPlayersInDimension((ServerLevel) level, new UpdateLaserColors(positionsToUpdate));

				level.sendBlockUpdated(modifiedPos, stateAtModifiedPos, stateAtModifiedPos, 2);
			}
		}

		setChanged();
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
	public void readOptions(ValueInput tag) {
		for (Option<?> option : customOptions()) {
			option.load(tag);
		}
	}

	public static IItemHandler getCapability(LaserBlockBlockEntity be, Direction side) {
		return BlockUtils.isAllowedToExtractFromProtectedObject(side, be) ? new InvWrapper(be.lenses) : new InsertOnlyInvWrapper(be.lenses);
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
				disabled, ignoreOwner, signalLength, respectInvisibility
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

	public boolean isConsideredInvisible(LivingEntity entity) {
		return respectInvisibility.isConsideredInvisible(entity);
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

	@SuppressWarnings({
			"rawtypes", "unchecked"
	})
	public ModuleType synchronizeWith(LaserBlockBlockEntity that) {
		if (!LinkableBlockEntity.isLinkedWith(this, that)) {
			Map<ItemStack, Boolean> bothInsertedModules = new Object2BooleanArrayMap<>();
			List<ModuleType> thisInsertedModules = getInsertedModules();
			List<ModuleType> thatInsertedModules = that.getInsertedModules();

			for (ModuleType type : thisInsertedModules) {
				ItemStack thisModule = getModule(type);

				if (thatInsertedModules.contains(type) && !ItemStack.isSameItemSameComponents(thisModule, that.getModule(type)))
					return type;

				bothInsertedModules.put(thisModule.copy(), isModuleEnabled(type));
				removeModule(type, false);
			}

			for (ModuleType type : thatInsertedModules) {
				bothInsertedModules.put(that.getModule(type).copy(), that.isModuleEnabled(type));
				that.removeModule(type, false);
				propagate(new ILinkedAction.ModuleRemoved(type, false), that);
			}

			//safe, because both blocks are the same and thus have the same options
			//TODO: test anyway
			Option[] options = that.customOptions();
			Option[] thisOptions = customOptions();

			for (int i = 0; i < options.length; i++) {
				thisOptions[i].setValue(options[i].get());
			}

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
