package net.geforcemods.securitycraft.models;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.annotation.Nonnull;

import net.geforcemods.securitycraft.api.IDisguisable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;

public class DisguisableDynamicBakedModel implements IDynamicBakedModel {
	public static final ModelProperty<BlockState> DISGUISED_STATE = new ModelProperty<>();
	private final BakedModel oldModel;

	public DisguisableDynamicBakedModel(BakedModel oldModel) {
		this.oldModel = oldModel;
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData modelData) {
		BlockState disguisedState = modelData.getData(DISGUISED_STATE);

		if (disguisedState != null) {
			Block block = disguisedState.getBlock();

			if (block != Blocks.AIR) {
				BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(disguisedState);

				if (model != null && model != this)
					return model.getQuads(disguisedState, side, rand, modelData);
			}
		}

		return oldModel.getQuads(state, side, rand, modelData);
	}

	@Override
	public TextureAtlasSprite getParticleIcon(IModelData modelData) {
		BlockState state = modelData.getData(DISGUISED_STATE);

		if (state != null) {
			Block block = state.getBlock();

			if (block != Blocks.AIR) {
				BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);

				if (model != null && model != this)
					return model.getParticleIcon(modelData);
			}
		}

		return oldModel.getParticleIcon(modelData);
	}

	@Override
	@Nonnull
	public IModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, IModelData modelData) {
		BlockEntity blockEntity = level.getBlockEntity(pos);

		if (blockEntity != null && blockEntity.getBlockState().getBlock() instanceof IDisguisable) {
			Optional<BlockState> disguisedState = IDisguisable.getDisguisedBlockState(blockEntity);

			if (disguisedState.isPresent()) {
				modelData.setData(DISGUISED_STATE, disguisedState.get());
				return modelData;
			}
		}

		modelData.setData(DISGUISED_STATE, Blocks.AIR.defaultBlockState());
		return modelData;
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return oldModel.getParticleIcon();
	}

	@Override
	public boolean isGui3d() {
		return false;
	}

	@Override
	public boolean isCustomRenderer() {
		return false;
	}

	@Override
	public boolean useAmbientOcclusion() {
		return true;
	}

	@Override
	public ItemOverrides getOverrides() {
		return null;
	}

	@Override
	public boolean usesBlockLight() {
		return false;
	}
}
