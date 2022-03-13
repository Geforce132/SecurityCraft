package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.models.DisguisableDynamicBakedModel;
import net.geforcemods.securitycraft.network.client.RefreshDisguisableModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.network.PacketDistributor;

public class DisguisableBlockEntity extends CustomizableBlockEntity {
	public DisguisableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void onModuleEnabled(ItemStack stack, ModuleType module) {
		super.onModuleEnabled(stack, module);

		if (module == ModuleType.DISGUISE) {
			if (!level.isClientSide)
				SecurityCraft.channel.send(PacketDistributor.ALL.noArg(), new RefreshDisguisableModel(worldPosition, true, stack));
			else
				ClientHandler.putDisguisedBeRenderer(this, stack);
		}
	}

	@Override
	public void onModuleDisabled(ItemStack stack, ModuleType module) {
		super.onModuleDisabled(stack, module);

		if (module == ModuleType.DISGUISE) {
			if (!level.isClientSide)
				SecurityCraft.channel.send(PacketDistributor.ALL.noArg(), new RefreshDisguisableModel(worldPosition, false, stack));
			else
				ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.removeDelegateOf(this);
		}
	}

	@Override
	public void onLoad() {
		super.onLoad();

		if (level.isClientSide)
			ClientHandler.putDisguisedBeRenderer(this, getModule(ModuleType.DISGUISE));
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
				ModuleType.DISGUISE
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return null;
	}

	@Override
	public IModelData getModelData() {
		return new ModelDataMap.Builder().withInitial(DisguisableDynamicBakedModel.DISGUISED_STATE_RL, Blocks.AIR.defaultBlockState()).build();
	}
}
