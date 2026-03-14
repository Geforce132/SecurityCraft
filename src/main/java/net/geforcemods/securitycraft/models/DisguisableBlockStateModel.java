package net.geforcemods.securitycraft.models;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.sprite.Material.Baked;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;
import net.neoforged.neoforge.model.data.ModelProperty;

public class DisguisableBlockStateModel implements BlockStateModel {
	public static final ModelProperty<BlockState> DISGUISED_STATE = new ModelProperty<>();
	private final BlockStateModel oldModel;

	public DisguisableBlockStateModel(BlockStateModel oldModel) {
		this.oldModel = oldModel;
	}

	@Override
	public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts) {
		ModelData modelData = level.getModelData(pos);
		BlockState disguisedState = modelData.get(DISGUISED_STATE);

		if (disguisedState != null) {
			Block block = disguisedState.getBlock();

			if (block != Blocks.AIR) {
				BlockStateModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(disguisedState);

				if (model != this) {
					List<BlockStateModelPart> modelParts = new ArrayList<>();

					model.collectParts(level, pos, state, random, modelParts);

					for (BlockStateModelPart disguisedPart : modelParts) {
						parts.add(new DisguisableBlockModelPart(disguisedPart, disguisedState));
					}

					return;
				}
			}
		}

		collectOldParts(modelData, level, pos, state, random, parts);
	}

	public void collectOldParts(ModelData modelData, BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts) {
		oldModel.collectParts(level, pos, state, random, parts);
	}

	@Override
	public void collectParts(RandomSource random, List<BlockStateModelPart> parts) {
		oldModel.collectParts(random, parts);
	}

	@Override
	public Baked particleMaterial(BlockAndTintGetter level, BlockPos pos, BlockState state) {
		ModelData modelData = level.getModelData(pos);
		BlockState disguisedState = modelData.get(DISGUISED_STATE);

		if (disguisedState != null) {
			Block block = disguisedState.getBlock();

			if (block != Blocks.AIR) {
				BlockStateModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(disguisedState);

				if (model != this)
					return model.particleMaterial(level, pos, state);
			}
		}

		return oldParticleMaterial(modelData, level, pos, state);
	}

	public Baked oldParticleMaterial(ModelData modelData, BlockAndTintGetter level, BlockPos pos, BlockState state) {
		return oldModel.particleMaterial(level, pos, state);
	}

	@Override
	public Baked particleMaterial() {
		return oldModel.particleMaterial();
	}

	@Override
	public boolean hasTranslucency(BlockAndTintGetter level, BlockPos pos, BlockState state) {
		ModelData modelData = level.getModelData(pos);
		BlockState disguisedState = modelData.get(DISGUISED_STATE);

		if (disguisedState != null) {
			Block block = disguisedState.getBlock();

			if (block != Blocks.AIR) {
				BlockStateModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(disguisedState);

				if (model != this)
					return model.hasTranslucency(level, pos, state);
			}
		}

		return oldHasTranslucency(modelData, level, pos, state);
	}

	public boolean oldHasTranslucency(ModelData modelData, BlockAndTintGetter level, BlockPos pos, BlockState state) {
		return oldModel.hasTranslucency(level, pos, state);
	}

	@Override
	public boolean hasTranslucency() {
		return oldModel.hasTranslucency();
	}
}
