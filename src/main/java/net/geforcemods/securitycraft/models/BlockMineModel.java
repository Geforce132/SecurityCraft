package net.geforcemods.securitycraft.models;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ForgeHooksClient;

public class BlockMineModel implements BakedModel {
	private final BakedModel defaultModel;
	private final BakedModel guiModel;

	public BlockMineModel(BakedModel defaultModel, BakedModel guiModel) {
		this.defaultModel = defaultModel;
		this.guiModel = guiModel;
	}

	@Override
	public boolean doesHandlePerspectives() {
		return true;
	}

	@Override
	public BakedModel handlePerspective(TransformType cameraTransformType, PoseStack pose) {
		if (cameraTransformType == TransformType.GUI)
			return ForgeHooksClient.handlePerspective(guiModel, cameraTransformType, pose);
		else
			return ForgeHooksClient.handlePerspective(defaultModel, cameraTransformType, pose);
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand) {
		return defaultModel == null ? new ArrayList<>() : defaultModel.getQuads(state, side, rand);
	}

	@Override
	public boolean useAmbientOcclusion() {
		return defaultModel == null ? true : defaultModel.useAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return defaultModel == null ? false : defaultModel.isGui3d();
	}

	@Override
	public boolean usesBlockLight() {
		return defaultModel.usesBlockLight();
	}

	@Override
	public boolean isCustomRenderer() {
		return defaultModel == null ? false : defaultModel.isCustomRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return defaultModel == null ? Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(MissingTextureAtlasSprite.getLocation()) : defaultModel.getParticleIcon();
	}

	@Override
	public ItemOverrides getOverrides() {
		return defaultModel == null ? ItemOverrides.EMPTY : defaultModel.getOverrides();
	}
}
