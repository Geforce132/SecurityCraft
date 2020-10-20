package net.geforcemods.securitycraft.models;

import java.util.List;
import java.util.Random;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.client.ForgeHooksClient;

public class BlockMineModel implements IBakedModel
{
	private final IBakedModel defaultModel;
	private final IBakedModel guiModel;

	public BlockMineModel(IBakedModel defaultModel, IBakedModel guiModel)
	{
		this.defaultModel = defaultModel;
		this.guiModel = guiModel;
	}

	@Override
	public boolean doesHandlePerspectives()
	{
		return true;
	}

	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType)
	{
		if(cameraTransformType == TransformType.GUI)
			return ForgeHooksClient.handlePerspective(guiModel, cameraTransformType);
		else
			return ForgeHooksClient.handlePerspective(defaultModel, cameraTransformType);
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand)
	{
		return defaultModel.getQuads(state, side, rand);
	}

	@Override
	public boolean isAmbientOcclusion()
	{
		return defaultModel.isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d()
	{
		return defaultModel.isGui3d();
	}

	@Override
	public boolean isBuiltInRenderer()
	{
		return defaultModel.isBuiltInRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleTexture()
	{
		return defaultModel.getParticleTexture();
	}

	@Override
	public ItemOverrideList getOverrides()
	{
		return defaultModel.getOverrides();
	}
}
