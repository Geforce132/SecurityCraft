package net.geforcemods.securitycraft.models;

import java.util.List;
import java.util.Random;

import net.geforcemods.securitycraft.blockentities.SecureRedstoneInterfaceBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;

public class SecureRedstoneInterfaceBakedModel extends DisguisableDynamicBakedModel {
	public static final ModelProperty<Boolean> POWERED = new ModelProperty<>();
	private final IBakedModel poweredModel;

	public SecureRedstoneInterfaceBakedModel(IBakedModel poweredModel, IBakedModel oldModel) {
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
	public TextureAtlasSprite getOldParticleTexture(IModelData modelData) {
		Boolean powered = modelData.getData(POWERED);

		if (powered != null && powered)
			return poweredModel.getParticleTexture(modelData);

		return super.getOldParticleTexture(modelData);
	}

	@Override
	public IModelData getModelData(IBlockDisplayReader level, BlockPos pos, BlockState state, IModelData modelData) {
		TileEntity be = level.getBlockEntity(pos);

		if (be instanceof SecureRedstoneInterfaceBlockEntity)
			modelData.setData(POWERED, ((SecureRedstoneInterfaceBlockEntity) be).getPower() > 0);

		return super.getModelData(level, pos, state, modelData);
	}
}
