package net.geforcemods.securitycraft.models;

import java.util.List;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public record DisguisableBlockModelPart(BlockModelPart disguisedModelPart, BlockState disguisedState) implements BlockModelPart {
	@Override
	public List<BakedQuad> getQuads(Direction direction) {
		return disguisedModelPart.getQuads(direction);
	}

	@Override
	public boolean useAmbientOcclusion() {
		return disguisedModelPart.useAmbientOcclusion();
	}

	@Override
	public TextureAtlasSprite particleIcon() {
		return disguisedModelPart.particleIcon();
	}

	@Override
	public ChunkSectionLayer getRenderType(BlockState state) {
		return disguisedModelPart.getRenderType(disguisedState);
	}
}
