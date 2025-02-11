package net.geforcemods.securitycraft.models;

import java.util.List;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;

public class SecureRedstoneInterfaceBakedModel extends DisguisableDynamicBakedModel {
	public static final ModelProperty<Boolean> POWERED = new ModelProperty<>();
	private final BakedModel poweredModel;

	public SecureRedstoneInterfaceBakedModel(BakedModel poweredModel, BakedModel oldModel) {
		super(oldModel);
		this.poweredModel = poweredModel;
	}

	@Override
	public List<BakedQuad> getOldQuads(BlockState state, Direction side, RandomSource rand, ModelData modelData, RenderType renderType) {
		Boolean powered = modelData.get(POWERED);

		if (powered != null && powered)
			return poweredModel.getQuads(state, side, rand, modelData, renderType);

		return super.getOldQuads(state, side, rand, modelData, renderType);
	}

	@Override
	public TextureAtlasSprite getOldParticleIcon(ModelData modelData) {
		Boolean powered = modelData.get(POWERED);

		if (powered != null && powered)
			return poweredModel.getParticleIcon(modelData);

		return super.getOldParticleIcon(modelData);
	}

	@Override
	public ChunkRenderTypeSet getOldRenderTypes(BlockState state, RandomSource rand, ModelData modelData) {
		Boolean powered = modelData.get(POWERED);

		if (powered != null && powered)
			return poweredModel.getRenderTypes(state, rand, modelData);

		return super.getOldRenderTypes(state, rand, modelData);
	}
}
