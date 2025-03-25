package net.geforcemods.securitycraft.models;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
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
	public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockModelPart> parts) {
		ModelData modelData = level.getModelData(pos);
		BlockState disguisedState = modelData.get(DISGUISED_STATE);

		if (disguisedState != null) {
			Block block = disguisedState.getBlock();

			if (block != Blocks.AIR) {
				BlockStateModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(disguisedState);

				if (model != this) {
					for (BlockModelPart disguisedPart : model.collectParts(level, pos, state, random)) {
						parts.add(new DisguisableBlockModelPart(disguisedPart, disguisedState));
					}

					return;
				}
			}
		}

		parts.addAll(oldModel.collectParts(level, pos, state, random));
	}

	@Override
	public void collectParts(RandomSource random, List<BlockModelPart> modelList) {
		modelList.addAll(oldModel.collectParts(random));
	}

	@Override
	public TextureAtlasSprite particleIcon(BlockAndTintGetter level, BlockPos pos, BlockState state) {
		ModelData modelData = level.getModelData(pos);
		BlockState disguisedState = modelData.get(DISGUISED_STATE);

		if (disguisedState != null) {
			Block block = disguisedState.getBlock();

			if (block != Blocks.AIR) {
				BlockStateModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(disguisedState);

				if (model != this)
					return model.particleIcon(level, pos, state);
			}
		}

		return oldModel.particleIcon(level, pos, state);
	}

	@Override
	public TextureAtlasSprite particleIcon() {
		return oldModel.particleIcon();
	}
}
