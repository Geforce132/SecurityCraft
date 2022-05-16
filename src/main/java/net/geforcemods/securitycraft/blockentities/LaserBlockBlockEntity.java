package net.geforcemods.securitycraft.blockentities;

import java.util.ArrayList;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.LinkableBlockEntity;
import net.geforcemods.securitycraft.api.LinkedAction;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.blocks.LaserBlock;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.models.DisguisableDynamicBakedModel;
import net.geforcemods.securitycraft.network.client.RefreshDisguisableModel;
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
	private BooleanOption enabledOption = new BooleanOption("enabled", true) {
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
	protected void onLinkedBlockAction(LinkedAction action, Object[] parameters, ArrayList<LinkableBlockEntity> excludedTEs) {
		if (action == LinkedAction.OPTION_CHANGED) {
			Option<?> option = (Option<?>) parameters[0];
			enabledOption.copy(option);
			toggleLaser((BooleanOption) option);

			excludedTEs.add(this);
			createLinkedBlockAction(LinkedAction.OPTION_CHANGED, new Option[] {
					option
			}, excludedTEs);
		}
		else if (action == LinkedAction.MODULE_INSERTED) {
			ItemStack module = (ItemStack) parameters[0];
			boolean toggled = (boolean) parameters[2];

			insertModule(module, toggled);

			if (((ModuleItem) module.getItem()).getModuleType() == ModuleType.DISGUISE)
				onInsertDisguiseModule(module, toggled);

			excludedTEs.add(this);
			createLinkedBlockAction(action, parameters, excludedTEs);
		}
		else if (action == LinkedAction.MODULE_REMOVED) {
			ModuleType module = (ModuleType) parameters[1];
			ItemStack moduleStack = getModule(module);
			boolean toggled = (boolean) parameters[2];

			removeModule(module, toggled);

			if (module == ModuleType.DISGUISE)
				onRemoveDisguiseModule(moduleStack, toggled);

			excludedTEs.add(this);
			createLinkedBlockAction(action, parameters, excludedTEs);
		}
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
	}

	private void onInsertDisguiseModule(ItemStack stack, boolean toggled) {
		if (!level.isClientSide) {
			BlockState state = getBlockState();

			SecurityCraft.channel.send(PacketDistributor.ALL.noArg(), new RefreshDisguisableModel(worldPosition, true, stack, toggled));

			if (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED))
				level.updateNeighborsAt(worldPosition, state.getBlock());
		}
		else
			ClientHandler.putDisguisedBeRenderer(this, stack);
	}

	private void onRemoveDisguiseModule(ItemStack stack, boolean toggled) {
		if (!level.isClientSide) {
			BlockState state = getBlockState();

			SecurityCraft.channel.send(PacketDistributor.ALL.noArg(), new RefreshDisguisableModel(worldPosition, false, stack, toggled));

			if (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED))
				level.getLiquidTicks().scheduleTick(worldPosition, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}
		else
			ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.removeDelegateOf(this);
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
	public void setRemoved() {
		super.setRemoved();

		if (level.isClientSide)
			ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.removeDelegateOf(this);
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.HARMING, ModuleType.ALLOWLIST, ModuleType.DISGUISE
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				enabledOption
		};
	}

	@Override
	public IModelData getModelData() {
		return new ModelDataMap.Builder().withInitial(DisguisableDynamicBakedModel.DISGUISED_STATE_RL, Blocks.AIR.defaultBlockState()).build();
	}

	public boolean isEnabled() {
		return enabledOption.get();
	}
}
