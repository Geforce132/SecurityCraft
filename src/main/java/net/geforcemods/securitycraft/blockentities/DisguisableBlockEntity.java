package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.models.DisguisableDynamicBakedModel;
import net.geforcemods.securitycraft.network.client.RefreshDisguisableModel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

public class DisguisableBlockEntity extends CustomizableBlockEntity {
	public DisguisableBlockEntity(BlockPos pos, BlockState state) {
		this(SCContent.DISGUISABLE_BLOCK_ENTITY.get(), pos, state);
	}

	public DisguisableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module) {
		super.onModuleInserted(stack, module);

		if (module == ModuleType.DISGUISE) {
			if (!level.isClientSide)
				SecurityCraft.channel.send(PacketDistributor.ALL.noArg(), new RefreshDisguisableModel(worldPosition, true, stack));
			else
				ClientHandler.putDisguisedBeRenderer(this, stack);
		}
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module) {
		super.onModuleRemoved(stack, module);

		if (module == ModuleType.DISGUISE) {
			if (!level.isClientSide)
				SecurityCraft.channel.send(PacketDistributor.ALL.noArg(), new RefreshDisguisableModel(worldPosition, false, stack));
			else
				ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.removeDelegateOf(this);
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
