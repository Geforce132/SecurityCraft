package net.geforcemods.securitycraft.models;

import java.util.List;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.block.model.Material.Baked;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;
import net.neoforged.neoforge.model.data.ModelProperty;

public class SecureRedstoneInterfaceBlockStateModel extends DisguisableBlockStateModel {
	public static final ModelProperty<Boolean> POWERED = new ModelProperty<>();
	private final BlockStateModel poweredModel;

	public SecureRedstoneInterfaceBlockStateModel(BlockStateModel poweredModel, BlockStateModel oldModel) {
		super(oldModel);
		this.poweredModel = poweredModel;
	}

	@Override
	public void collectOldParts(ModelData modelData, BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockModelPart> parts) {
		Boolean powered = modelData.get(POWERED);

		if (powered != null && powered)
			poweredModel.collectParts(level, pos, state, random, parts);
		else
			super.collectOldParts(modelData, level, pos, state, random, parts);
	}

	@Override
	public Baked oldParticleMaterial(ModelData modelData, BlockAndTintGetter level, BlockPos pos, BlockState state) {
		Boolean powered = modelData.get(POWERED);

		if (powered != null && powered)
			return poweredModel.particleMaterial(level, pos, state);

		return super.oldParticleMaterial(modelData, level, pos, state);
	}

	@Override
	public boolean oldHasTranslucency(ModelData modelData, BlockAndTintGetter level, BlockPos pos, BlockState state) {
		Boolean powered = modelData.get(POWERED);

		if (powered != null && powered)
			return poweredModel.hasTranslucency(level, pos, state);

		return super.oldHasTranslucency(modelData, level, pos, state);
	}
}
