package net.geforcemods.securitycraft.models;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.BlockKeypad;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypad;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.registries.ForgeRegistries;

public class ModelDynamicBakedKeypad implements IDynamicBakedModel
{
	public static final ModelProperty<ResourceLocation> DISGUISED_BLOCK_RL = new ModelProperty<>();
	public static final ResourceLocation DEFAULT_STATE_RL = SCContent.keypad.getRegistryName();
	private IBakedModel oldModel;
	private TextureAtlasSprite particleTexture;

	public ModelDynamicBakedKeypad(IBakedModel oldModel)
	{
		this.oldModel = oldModel;
		particleTexture = oldModel.getParticleTexture();
	}

	@Override
	public boolean isGui3d()
	{
		return false;
	}

	@Override
	public boolean isBuiltInRenderer()
	{
		return false;
	}

	@Override
	public boolean isAmbientOcclusion()
	{
		return true;
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, Random rand, IModelData modelData)
	{
		ResourceLocation rl = modelData.getData(DISGUISED_BLOCK_RL);

		if(rl != DEFAULT_STATE_RL)
		{
			Block block = ForgeRegistries.BLOCKS.getValue(rl);

			if(block != null)
			{
				final IBakedModel model = Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(block.getDefaultState());

				if(model != null)
				{
					particleTexture = model.getParticleTexture();
					return model.getQuads(state, side, rand, modelData);
				}
			}
		}

		particleTexture = oldModel.getParticleTexture();
		return oldModel.getQuads(state, side, rand, modelData);
	}

	@Override
	public TextureAtlasSprite getParticleTexture()
	{
		return particleTexture;
	}

	@Override
	public ItemOverrideList getOverrides()
	{
		return null;
	}

	@Override
	@Nonnull
	public IModelData getModelData(@Nonnull IWorldReader world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull IModelData tileData)
	{
		TileEntity te = world.getTileEntity(pos);

		if(te instanceof TileEntityKeypad)
		{
			Block block = ((TileEntityKeypad)te).getBlockState().getBlock();

			if(block instanceof BlockKeypad)
			{
				IBlockState disguisedState = ((BlockKeypad)block).getDisguisedBlockState(world, pos);

				if(disguisedState != null)
				{
					tileData.setData(DISGUISED_BLOCK_RL, disguisedState.getBlock().getRegistryName());
					return tileData;
				}
			}
		}

		tileData.setData(DISGUISED_BLOCK_RL, DEFAULT_STATE_RL);
		return tileData;
	}
}
