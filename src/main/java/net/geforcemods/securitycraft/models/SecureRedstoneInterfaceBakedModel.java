package net.geforcemods.securitycraft.models;

import java.util.List;
import java.util.Random;

import net.geforcemods.securitycraft.blockentities.SecureRedstoneInterfaceBlockEntity;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;

public class SecureRedstoneInterfaceBakedModel extends DisguisableDynamicBakedModel {
	public static final ModelProperty<Boolean> POWERED = new ModelProperty<>();
	private final BakedModel poweredModel;

	public SecureRedstoneInterfaceBakedModel(BakedModel poweredModel, BakedModel oldModel) {
		super(oldModel);
		this.poweredModel = poweredModel;
	}

	@Override
	public List<BakedQuad> getOldQuads(BlockState state, Direction side, Random rand, IModelData modelData) {
		Boolean powered = modelData.getData(POWERED);

		if (powered != null && powered)
			return poweredModel.getQuads(state, side, rand, modelData);

		return super.getOldQuads(state, side, rand, modelData);
	}

	@Override
	public TextureAtlasSprite getOldParticleIcon(IModelData modelData) {
		Boolean powered = modelData.getData(POWERED);

		if (powered != null && powered)
			return poweredModel.getParticleIcon(modelData);

		return super.getOldParticleIcon(modelData);
	}

	@Override
	public IModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, IModelData modelData) {
		if (level.getBlockEntity(pos) instanceof SecureRedstoneInterfaceBlockEntity be)
			modelData.setData(POWERED, be.getPower() > 0);

		return super.getModelData(level, pos, state, modelData);
	}
}
