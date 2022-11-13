package net.geforcemods.securitycraft.blockentities;

import java.util.ArrayList;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ILinkedAction;
import net.geforcemods.securitycraft.api.LinkableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.blocks.LaserBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.models.DisguisableDynamicBakedModel;
import net.geforcemods.securitycraft.network.client.RefreshDisguisableModel;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.fml.network.PacketDistributor;

public class LaserBlockBlockEntity extends LinkableBlockEntity {
	private DisabledOption disabled = new DisabledOption(false) {
		@Override
		public void toggle() {
			setValue(!get());

			toggleLaser(this);
		}
	};

	public LaserBlockBlockEntity() {
		super(SCContent.LASER_BLOCK_BLOCK_ENTITY.get());
	}

	private void toggleLaser(BooleanOption option) {
		if (option.get())
			((LaserBlock) getBlockState().getBlock()).setLaser(level, worldPosition);
		else
			LaserBlock.destroyAdjacentLasers(level, worldPosition);
	}

	@Override
	protected void onLinkedBlockAction(ILinkedAction action, ArrayList<LinkableBlockEntity> excludedTEs) {
		if (action instanceof ILinkedAction.OptionChanged) {
			Option<?> option = ((ILinkedAction.OptionChanged) action).option;

			disabled.copy(option);
			toggleLaser((BooleanOption) option);
		}
		else if (action instanceof ILinkedAction.ModuleInserted) {
			ILinkedAction.ModuleInserted moduleInserted = (ILinkedAction.ModuleInserted) action;
			ItemStack module = moduleInserted.stack;
			boolean toggled = moduleInserted.wasModuleToggled;

			insertModule(module, toggled);

			if (moduleInserted.module.getModuleType() == ModuleType.DISGUISE)
				onInsertDisguiseModule(module, toggled);
		}
		else if (action instanceof ILinkedAction.ModuleRemoved) {
			ILinkedAction.ModuleRemoved moduleRemoved = (ILinkedAction.ModuleRemoved) action;
			ModuleType module = moduleRemoved.moduleType;
			ItemStack moduleStack = getModule(module);
			boolean toggled = moduleRemoved.wasModuleToggled;

			removeModule(module, toggled);

			if (module == ModuleType.DISGUISE)
				onRemoveDisguiseModule(moduleStack, toggled);
			else if (module == ModuleType.REDSTONE)
				onRemoveRedstoneModule();
		}
		else if (action instanceof ILinkedAction.OwnerChanged) {
			Owner owner = ((ILinkedAction.OwnerChanged) action).newOwner;

			setOwner(owner.getUUID(), owner.getName());
		}

		excludedTEs.add(this);
		createLinkedBlockAction(action, excludedTEs);
	}

	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleInserted(stack, module, toggled);

		if (module == ModuleType.DISGUISE)
			onInsertDisguiseModule(stack, toggled);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleRemoved(stack, module, toggled);

		if (module == ModuleType.DISGUISE)
			onRemoveDisguiseModule(stack, toggled);
		else if (module == ModuleType.REDSTONE)
			onRemoveRedstoneModule();
	}

	private void onInsertDisguiseModule(ItemStack stack, boolean toggled) {
		BlockState state = getBlockState();

		if (!level.isClientSide) {
			SecurityCraft.channel.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new RefreshDisguisableModel(worldPosition, true, stack, toggled));

			if (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)) {
				level.getLiquidTicks().scheduleTick(worldPosition, Fluids.WATER, Fluids.WATER.getTickDelay(level));
				level.updateNeighborsAt(worldPosition, state.getBlock());
			}
		}
		else {
			ClientHandler.putDisguisedBeRenderer(this, stack);

			if (state.getLightValue(level, worldPosition) > 0)
				level.getChunkSource().getLightEngine().checkBlock(worldPosition);
		}
	}

	private void onRemoveDisguiseModule(ItemStack stack, boolean toggled) {
		if (!level.isClientSide) {
			BlockState state = getBlockState();

			SecurityCraft.channel.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new RefreshDisguisableModel(worldPosition, false, stack, toggled));

			if (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)) {
				level.getLiquidTicks().scheduleTick(worldPosition, Fluids.WATER, Fluids.WATER.getTickDelay(level));
				level.updateNeighborsAt(worldPosition, state.getBlock());
			}
		}
		else {
			ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.removeDelegateOf(this);
			DisguisableBlock.getDisguisedBlockStateFromStack(stack).ifPresent(disguisedState -> {
				if (disguisedState.getLightValue(level, worldPosition) > 0)
					level.getChunkSource().getLightEngine().checkBlock(worldPosition);
			});
		}
	}

	private void onRemoveRedstoneModule() {
		if (getBlockState().getValue(LaserBlock.POWERED)) {
			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(LaserBlock.POWERED, false));
			BlockUtils.updateIndirectNeighbors(level, worldPosition, SCContent.LASER_BLOCK.get());
		}
	}

	@Override
	public void handleUpdateTag(BlockState state, CompoundNBT tag) {
		super.handleUpdateTag(state, tag);

		if (level != null && level.isClientSide) {
			ItemStack stack = getModule(ModuleType.DISGUISE);

			if (!stack.isEmpty())
				ClientHandler.putDisguisedBeRenderer(this, stack);
			else
				ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.removeDelegateOf(this);
		}
	}

	@Override
	public void readOptions(CompoundNBT tag) {
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
				ModuleType.HARMING, ModuleType.ALLOWLIST, ModuleType.DISGUISE, ModuleType.REDSTONE
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				disabled
		};
	}

	@Override
	public IModelData getModelData() {
		return new ModelDataMap.Builder().withInitial(DisguisableDynamicBakedModel.DISGUISED_STATE, Blocks.AIR.defaultBlockState()).build();
	}

	public boolean isEnabled() {
		return !disabled.get();
	}
}
