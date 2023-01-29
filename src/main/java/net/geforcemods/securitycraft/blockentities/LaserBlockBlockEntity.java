package net.geforcemods.securitycraft.blockentities;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ILinkedAction;
import net.geforcemods.securitycraft.api.LinkableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.IgnoreOwnerOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.blocks.LaserBlock;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.models.DisguisableDynamicBakedModel;
import net.geforcemods.securitycraft.network.client.RefreshDisguisableModel;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.network.PacketDistributor;

public class LaserBlockBlockEntity extends LinkableBlockEntity {
	private DisabledOption disabled = new DisabledOption(false) {
		@Override
		public void toggle() {
			setValue(!get());
			setLasersAccordingToDisabledOption();
		}
	};
	private IgnoreOwnerOption ignoreOwner = new IgnoreOwnerOption(true);
	private EnumMap<Direction, Boolean> sideConfig = Util.make(() -> {
		EnumMap<Direction, Boolean> map = new EnumMap<>(Direction.class);

		for (Direction dir : Direction.values()) {
			map.put(dir, true);
		}

		return map;
	});
	private boolean sideConfigActive;

	public LaserBlockBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.LASER_BLOCK_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("sideConfig", saveSideConfig(sideConfig));
		tag.putBoolean("sideConfigActive", isSideConfigActive());
	}

	public static CompoundTag saveSideConfig(EnumMap<Direction, Boolean> sideConfig) {
		CompoundTag sideConfigTag = new CompoundTag();

		sideConfig.forEach((dir, enabled) -> sideConfigTag.putBoolean(dir.getName(), enabled));
		return sideConfigTag;
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		sideConfig = loadSideConfig(tag.getCompound("sideConfig"));
		sideConfigActive = tag.getBoolean("sideConfigActive");
	}

	public static EnumMap<Direction, Boolean> loadSideConfig(CompoundTag sideConfigTag) {
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
	protected void onLinkedBlockAction(ILinkedAction action, ArrayList<LinkableBlockEntity> excludedBEs) {
		if (action instanceof ILinkedAction.OptionChanged<?> optionChanged) {
			Option<?> option = optionChanged.option();

			if (option.getName().equals("disabled")) {
				disabled.copy(option);
				setLasersAccordingToDisabledOption();
			}
			else if (option.getName().equals("ignoreOwner"))
				ignoreOwner.copy(option);
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

			if (stateChanged.property() == LaserBlock.POWERED && !state.getValue(LaserBlock.POWERED)) {
				level.setBlockAndUpdate(worldPosition, state.setValue(LaserBlock.POWERED, true));
				BlockUtils.updateIndirectNeighbors(level, worldPosition, SCContent.LASER_BLOCK.get());
				level.scheduleTick(worldPosition, SCContent.LASER_BLOCK.get(), 50);
			}
		}

		excludedBEs.add(this);
		createLinkedBlockAction(action, excludedBEs);
	}

	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleInserted(stack, module, toggled);

		if (module == ModuleType.DISGUISE) {
			BlockState state = getBlockState();

			if (!level.isClientSide) {
				SecurityCraft.channel.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new RefreshDisguisableModel(worldPosition, true, stack, toggled));

				if (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)) {
					level.scheduleTick(worldPosition, Fluids.WATER, Fluids.WATER.getTickDelay(level));
					level.updateNeighborsAt(worldPosition, state.getBlock());
				}
			}
			else {
				ClientHandler.putDisguisedBeRenderer(this, stack);

				if (state.getLightEmission(level, worldPosition) > 0)
					level.getChunkSource().getLightEngine().checkBlock(worldPosition);
			}
		}
		else if (module == ModuleType.SMART)
			setSideConfigActive(true);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleRemoved(stack, module, toggled);

		if (module == ModuleType.DISGUISE) {
			if (!level.isClientSide) {
				BlockState state = getBlockState();

				SecurityCraft.channel.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new RefreshDisguisableModel(worldPosition, false, stack, toggled));

				if (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)) {
					level.scheduleTick(worldPosition, Fluids.WATER, Fluids.WATER.getTickDelay(level));
					level.updateNeighborsAt(worldPosition, state.getBlock());
				}
			}
			else {
				ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.removeDelegateOf(this);
				DisguisableBlock.getDisguisedBlockStateFromStack(stack).ifPresent(disguisedState -> {
					if (disguisedState.getLightEmission(level, worldPosition) > 0)
						level.getChunkSource().getLightEngine().checkBlock(worldPosition);
				});
			}
		}
		else if (module == ModuleType.REDSTONE) {
			if (getBlockState().getValue(LaserBlock.POWERED)) {
				level.setBlockAndUpdate(worldPosition, getBlockState().setValue(LaserBlock.POWERED, false));
				BlockUtils.updateIndirectNeighbors(level, worldPosition, SCContent.LASER_BLOCK.get());
			}
		}
		else if (module == ModuleType.SMART)
			setSideConfigActive(false);
	}

	@Override
	public void handleUpdateTag(CompoundTag tag) {
		super.handleUpdateTag(tag);

		if (level != null && level.isClientSide) {
			ItemStack stack = getModule(ModuleType.DISGUISE);

			if (!stack.isEmpty())
				ClientHandler.putDisguisedBeRenderer(this, stack);
			else
				ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.removeDelegateOf(this);
		}
	}

	@Override
	public void readOptions(CompoundTag tag) {
		if (tag.contains("enabled"))
			tag.putBoolean("disabled", !tag.getBoolean("enabled")); //legacy support

		for (Option<?> option : customOptions()) {
			option.readFromNBT(tag);
		}
	}

	@Override
	public void setRemoved() {
		super.setRemoved();

		if (level.isClientSide)
			ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.removeDelegateOf(this);
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
				disabled, ignoreOwner
		};
	}

	@Override
	public ModelData getModelData() {
		BlockState disguisedState = DisguisableBlock.getDisguisedStateOrDefault(Blocks.AIR.defaultBlockState(), level, worldPosition);

		return ModelData.builder().with(DisguisableDynamicBakedModel.DISGUISED_STATE, disguisedState).build();
	}

	public boolean isEnabled() {
		return !disabled.get();
	}

	public boolean ignoresOwner() {
		return ignoreOwner.get();
	}

	public void applySideConfig(EnumMap<Direction, Boolean> sideConfig, Player player) {
		sideConfig.forEach((direction, enabled) -> setSideEnabled(direction, enabled, player));
	}

	public void setSideEnabled(Direction direction, boolean enabled, Player player) {
		sideConfig.put(direction, enabled);

		if (isSideConfigActive())
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
		else if (!enabled)
			BlockUtils.removeInSequence(SCContent.LASER_FIELD.get(), level, worldPosition, direction);
	}

	public EnumMap<Direction, Boolean> getSideConfig() {
		return sideConfig;
	}

	public boolean isSideEnabled(Direction dir) {
		return !isSideConfigActive() || sideConfig.getOrDefault(dir, true);
	}

	public boolean isSideConfigActive() {
		return sideConfigActive;
	}

	public void setSideConfigActive(boolean sideConfigActive) {
		this.sideConfigActive = sideConfigActive;

		for (Direction direction : Direction.values()) {
			toggleLaserOnSide(direction, isSideEnabled(direction), null, false);
		}
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

				if (thatInsertedModules.contains(type) && !thisModule.areShareTagsEqual(that.getModule(type)))
					return type;

				bothInsertedModules.put(thisModule.copy(), isModuleEnabled(type));
				removeModule(type, false);
			}

			for (ModuleType type : thatInsertedModules) {
				bothInsertedModules.put(that.getModule(type).copy(), that.isModuleEnabled(type));
				that.removeModule(type, false);
				createLinkedBlockAction(new ILinkedAction.ModuleRemoved(type, false), that);
			}

			readOptions(that.writeOptions(new CompoundTag()));
			LinkableBlockEntity.link(this, that);

			for (Entry<ItemStack, Boolean> entry : bothInsertedModules.entrySet()) {
				ItemStack module = entry.getKey();
				ModuleItem item = (ModuleItem) module.getItem();
				ModuleType type = item.getModuleType();

				insertModule(entry.getKey(), false);
				createLinkedBlockAction(new ILinkedAction.ModuleInserted(module, item, false), this);
				toggleModuleState(type, entry.getValue());
				createLinkedBlockAction(new ILinkedAction.ModuleInserted(module, item, true), this);
			}
		}

		return null;
	}
}
