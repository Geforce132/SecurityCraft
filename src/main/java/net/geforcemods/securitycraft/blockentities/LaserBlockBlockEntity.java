package net.geforcemods.securitycraft.blockentities;

import java.util.ArrayList;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ILinkedAction;
import net.geforcemods.securitycraft.api.LinkableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.blocks.LaserBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.models.DisguisableDynamicBakedModel;
import net.geforcemods.securitycraft.network.client.RefreshDisguisableModel;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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

	public LaserBlockBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.LASER_BLOCK_BLOCK_ENTITY.get(), pos, state);
	}

	private void setLasersAccordingToDisabledOption() {
		if (isEnabled())
			((LaserBlock) getBlockState().getBlock()).setLaser(level, worldPosition);
		else
			LaserBlock.destroyAdjacentLasers(level, worldPosition);
	}

	@Override
	protected void onLinkedBlockAction(ILinkedAction action, ArrayList<LinkableBlockEntity> excludedBEs) {
		if (action instanceof ILinkedAction.OptionChanged optionChanged) {
			Option<?> option = optionChanged.option();

			disabled.copy(option);
			setLasersAccordingToDisabledOption();
		}
		else if (action instanceof ILinkedAction.ModuleInserted moduleInserted)
			insertModule(moduleInserted.stack(), moduleInserted.wasModuleToggled());
		else if (action instanceof ILinkedAction.ModuleRemoved moduleRemoved)
			removeModule(moduleRemoved.moduleType(), moduleRemoved.wasModuleToggled());
		else if (action instanceof ILinkedAction.OwnerChanged ownerChanged) {
			Owner owner = ownerChanged.newOwner();

			setOwner(owner.getUUID(), owner.getName());
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
				DisguisableBlock.getDisguisedBlockStateFromStack(level, stack).ifPresent(disguisedState -> {
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
	public ModelData getModelData() {
		BlockState disguisedState = DisguisableBlock.getDisguisedStateOrDefault(Blocks.AIR.defaultBlockState(), level, worldPosition);

		return ModelData.builder().with(DisguisableDynamicBakedModel.DISGUISED_STATE, disguisedState).build();
	}

	public boolean isEnabled() {
		return !disabled.get();
	}
}
