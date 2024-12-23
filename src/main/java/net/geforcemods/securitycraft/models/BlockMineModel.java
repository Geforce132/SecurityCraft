package net.geforcemods.securitycraft.models;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.ForgeHooksClient;

public class BlockMineModel implements IBakedModel {
	private final IBakedModel defaultModel;
	private final IBakedModel guiModel;

	public BlockMineModel(IBakedModel defaultModel, IBakedModel guiModel) {
		this.defaultModel = defaultModel;
		this.guiModel = guiModel;
	}

	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
		if (cameraTransformType == TransformType.GUI || cameraTransformType == TransformType.FIRST_PERSON_LEFT_HAND || cameraTransformType == TransformType.FIRST_PERSON_RIGHT_HAND)
			return ForgeHooksClient.handlePerspective(guiModel, cameraTransformType);
		else
			return ForgeHooksClient.handlePerspective(defaultModel, cameraTransformType);
	}

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		return defaultModel == null ? new ArrayList<>() : defaultModel.getQuads(state, side, rand);
	}

	@Override
	public boolean isAmbientOcclusion() {
		return defaultModel == null || defaultModel.isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return defaultModel != null && defaultModel.isGui3d();
	}

	@Override
	public boolean isBuiltInRenderer() {
		return defaultModel != null && defaultModel.isBuiltInRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return defaultModel == null ? Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite() : defaultModel.getParticleTexture();
	}

	@Override
	public ItemOverrideList getOverrides() {
		return defaultModel == null ? ItemOverrideList.NONE : defaultModel.getOverrides();
	}
}
