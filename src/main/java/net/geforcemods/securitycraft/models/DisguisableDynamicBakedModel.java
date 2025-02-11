package net.geforcemods.securitycraft.models;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedOverrides;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;

public class DisguisableDynamicBakedModel implements IDynamicBakedModel {
	public static final ModelProperty<BlockState> DISGUISED_STATE = new ModelProperty<>();
	private final BakedModel oldModel;

	public DisguisableDynamicBakedModel(BakedModel oldModel) {
		this.oldModel = oldModel;
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, ModelData modelData, RenderType renderType) {
		BlockState disguisedState = modelData.get(DISGUISED_STATE);

		if (disguisedState != null) {
			Block block = disguisedState.getBlock();

			if (block != Blocks.AIR) {
				BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(disguisedState);

				if (model != null && model != this)
					return model.getQuads(disguisedState, side, rand, modelData, renderType);
			}
		}

		return getOldQuads(state, side, rand, modelData, renderType);
	}

	public List<BakedQuad> getOldQuads(BlockState state, Direction side, RandomSource rand, ModelData modelData, RenderType renderType) {
		return oldModel.getQuads(state, side, rand, modelData, renderType);
	}

	@Override
	public TextureAtlasSprite getParticleIcon(ModelData modelData) {
		BlockState disguisedState = modelData.get(DISGUISED_STATE);

		if (disguisedState != null) {
			Block block = disguisedState.getBlock();

			if (block != Blocks.AIR) {
				BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(disguisedState);

				if (model != null && model != this)
					return model.getParticleIcon(modelData);
			}
		}

		return getOldParticleIcon(modelData);
	}

	public TextureAtlasSprite getOldParticleIcon(ModelData modelData) {
		return oldModel.getParticleIcon(modelData);
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return oldModel.getParticleIcon();
	}

	@Override
	public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData modelData) {
		BlockState disguisedState = modelData.get(DISGUISED_STATE);

		if (disguisedState != null) {
			Block block = disguisedState.getBlock();

			if (block != Blocks.AIR) {
				BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(disguisedState);

				if (model != null && model != this)
					return model.getRenderTypes(disguisedState, rand, ModelData.EMPTY);
			}
		}

		return getOldRenderTypes(state, rand, modelData);
	}

	public ChunkRenderTypeSet getOldRenderTypes(BlockState state, RandomSource rand, ModelData modelData) {
		return oldModel.getRenderTypes(state, rand, modelData);
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
	public BakedOverrides overrides() {
		return null;
	}

	@Override
	public boolean usesBlockLight() {
		return false;
	}
}
