package net.geforcemods.securitycraft.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.Direction;
import net.minecraftforge.client.ForgeHooksClient;

public class BlockMineModel implements IBakedModel {
	private final IBakedModel defaultModel;
	private final IBakedModel guiModel;

	public BlockMineModel(IBakedModel defaultModel, IBakedModel guiModel) {
		this.defaultModel = defaultModel;
		this.guiModel = guiModel;
	}

	@Override
	public boolean doesHandlePerspectives() {
		return true;
	}

	@Override
	public IBakedModel handlePerspective(TransformType cameraTransformType, MatrixStack pose) {
		if (cameraTransformType == TransformType.GUI || cameraTransformType == TransformType.FIRST_PERSON_LEFT_HAND || cameraTransformType == TransformType.FIRST_PERSON_RIGHT_HAND)
			return ForgeHooksClient.handlePerspective(guiModel, cameraTransformType, pose);
		else
			return ForgeHooksClient.handlePerspective(defaultModel, cameraTransformType, pose);
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
		return defaultModel == null ? new ArrayList<>() : defaultModel.getQuads(state, side, rand);
	}

	@Override
	public boolean useAmbientOcclusion() {
		return defaultModel == null || defaultModel.useAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return defaultModel != null && defaultModel.isGui3d();
	}

	@Override
	public boolean usesBlockLight() {
		return defaultModel.usesBlockLight();
	}

	@Override
	public boolean isCustomRenderer() {
		return defaultModel != null && defaultModel.isCustomRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return defaultModel == null ? Minecraft.getInstance().getTextureAtlas(PlayerContainer.BLOCK_ATLAS).apply(MissingTextureSprite.getLocation()) : defaultModel.getParticleIcon();
	}

	@Override
	public ItemOverrideList getOverrides() {
		return defaultModel == null ? ItemOverrideList.EMPTY : defaultModel.getOverrides();
	}
}
