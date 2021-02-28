package net.geforcemods.securitycraft.datagen;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;

public class SCBlockModelProvider extends BlockModelProvider
{
	public SCBlockModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper)
	{
		super(generator, SecurityCraft.MODID, existingFileHelper);
	}

	public BlockModelBuilder reinforcedStairs(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top)
	{
		return sideBottomTop(name, modLoc(BLOCK_FOLDER + "/reinforced_stairs"), side, bottom, top);
	}

	public BlockModelBuilder reinforcedStairsOuter(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top)
	{
		return sideBottomTop(name, modLoc(BLOCK_FOLDER + "/reinforced_outer_stairs"), side, bottom, top);
	}

	public BlockModelBuilder reinforcedStairsInner(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top)
	{
		return sideBottomTop(name, modLoc(BLOCK_FOLDER + "/reinforced_inner_stairs"), side, bottom, top);
	}

	public BlockModelBuilder reinforcedWallPost(String name, ResourceLocation wall)
	{
		return uncheckedSingleTexture(name, modLoc(ModelProvider.BLOCK_FOLDER + "/reinforced_wall_post"), "wall", wall);
	}

	public BlockModelBuilder reinforcedWallSide(String name, ResourceLocation wall)
	{
		return uncheckedSingleTexture(name, modLoc(ModelProvider.BLOCK_FOLDER + "/reinforced_wall_side"), "wall", wall);
	}

	public BlockModelBuilder reinforcedWallInventory(String name, ResourceLocation wall)
	{
		return uncheckedSingleTexture(name, modLoc(ModelProvider.BLOCK_FOLDER + "/reinforced_wall_inventory"), "wall", wall);
	}

	public BlockModelBuilder uncheckedSingleTexture(String name, ResourceLocation parent, String textureKey, ResourceLocation texture)
	{
		return getBuilder(name).parent(new UncheckedModelFile(parent)).texture(textureKey, texture);
	}

	private BlockModelBuilder sideBottomTop(String name, ResourceLocation parent, ResourceLocation side, ResourceLocation bottom, ResourceLocation top)
	{
		return withExistingParent(name, parent)
				.texture("side", side)
				.texture("bottom", bottom)
				.texture("top", top);
	}

	@Override
	protected void registerModels() {}
}
