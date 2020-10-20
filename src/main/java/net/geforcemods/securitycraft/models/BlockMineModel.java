package net.geforcemods.securitycraft.models;

import java.util.List;
import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;

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
	public IBakedModel handlePerspective(TransformType cameraTransformType, MatrixStack matrix)
	{
		if(cameraTransformType == TransformType.GUI)
			return ForgeHooksClient.handlePerspective(guiModel, cameraTransformType, matrix);
		else
			return ForgeHooksClient.handlePerspective(defaultModel, cameraTransformType, matrix);
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
	public boolean isSideLit()
	{
		return defaultModel.isSideLit();
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
