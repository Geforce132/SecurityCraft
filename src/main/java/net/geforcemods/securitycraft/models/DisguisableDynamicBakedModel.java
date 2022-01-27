package net.geforcemods.securitycraft.models;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.annotation.Nonnull;

import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;

public class DisguisableDynamicBakedModel implements IDynamicBakedModel {
	public static final ModelProperty<BlockState> DISGUISED_STATE_RL = new ModelProperty<>();
	private final IBakedModel oldModel;

	public DisguisableDynamicBakedModel(IBakedModel oldModel) {
		this.oldModel = oldModel;
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData modelData) {
		BlockState disguisedState = modelData.getData(DISGUISED_STATE_RL);

		if (disguisedState != null) {
			Block block = disguisedState.getBlock();

			if (block != Blocks.AIR) {
				final IBakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(disguisedState);

				if (model != null && model != this)
					return model.getQuads(disguisedState, side, rand, modelData);
			}
		}

		return oldModel.getQuads(state, side, rand, modelData);
	}

	@Override
	public TextureAtlasSprite getParticleTexture(IModelData modelData) {
		BlockState state = modelData.getData(DISGUISED_STATE_RL);

		if (state != null) {
			Block block = state.getBlock();

			if (block != Blocks.AIR) {
				IBakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);

				if (model != null && model != this)
					return model.getParticleTexture(modelData);
			}
		}

		return oldModel.getParticleTexture(modelData);
	}

	@Override
	@Nonnull
	public IModelData getModelData(ILightReader world, BlockPos pos, BlockState state, IModelData tileData) {
		TileEntity te = world.getBlockEntity(pos);

		if (te != null) {
			Block block = te.getBlockState().getBlock();

			if (block instanceof DisguisableBlock) {
				Optional<BlockState> disguisedState = ((DisguisableBlock) block).getDisguisedBlockState(world, pos);

				if (disguisedState.isPresent()) {
					tileData.setData(DISGUISED_STATE_RL, disguisedState.get());
					return tileData;
				}
			}
		}

		tileData.setData(DISGUISED_STATE_RL, Blocks.AIR.defaultBlockState());
		return tileData;
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return oldModel.getParticleIcon();
	}

	@Override
	public boolean isGui3d() {
		return false;
	}

	@Override
	public boolean isCustomRenderer() {
		return false;
	}

	@Override
	public boolean useAmbientOcclusion() {
		return true;
	}

	@Override
	public ItemOverrideList getOverrides() {
		return null;
	}

	@Override
	public boolean usesBlockLight() //doesNotUseDiffuseLighting or something like that, idk
	{
		return false;
	}
}
