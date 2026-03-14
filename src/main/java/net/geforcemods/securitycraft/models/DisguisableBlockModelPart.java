package net.geforcemods.securitycraft.models;

import java.util.List;

import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material.Baked;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public record DisguisableBlockModelPart(BlockStateModelPart disguisedModelPart, BlockState disguisedState) implements BlockStateModelPart {
	@Override
	public List<BakedQuad> getQuads(Direction direction) {
		return disguisedModelPart.getQuads(direction);
	}

	@Override
	public boolean useAmbientOcclusion() {
		return disguisedModelPart.useAmbientOcclusion();
	}

	@Override
	public Baked particleMaterial() {
		return disguisedModelPart.particleMaterial();
	}

	@Override
	public int materialFlags() {
		return disguisedModelPart.materialFlags();
	}
}
