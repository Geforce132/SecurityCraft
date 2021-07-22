package net.geforcemods.securitycraft.datagen;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class SCBlockModelProvider extends BlockModelProvider
{
	public SCBlockModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper)
	{
		super(generator, SecurityCraft.MODID, existingFileHelper);
	}

	public BlockModelBuilder reinforcedCarpet(String name, ResourceLocation wool)
	{
		return uncheckedSingleTexture(name, modLoc(BLOCK_FOLDER + "/reinforced_carpet"), "wool", wool);
	}

	public BlockModelBuilder reinforcedColumn(String name, String side, String end)
	{
		return withExistingParent(name, modLoc(BLOCK_FOLDER + "/reinforced_cube_column"))
				.texture("side", mcLoc(BLOCK_FOLDER + "/" + side))
				.texture("end", mcLoc(BLOCK_FOLDER + "/" + end));
	}

	public BlockModelBuilder reinforcedSlab(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top)
	{
		return sideBottomTop(name, modLoc(BLOCK_FOLDER + "/reinforced_slab"), side, bottom, top);
	}

	public BlockModelBuilder reinforcedSlabTop(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top)
	{
		return sideBottomTop(name, modLoc(BLOCK_FOLDER + "/reinforced_slab_top"), side, bottom, top);
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
		return uncheckedSingleTexture(name, modLoc(BLOCK_FOLDER + "/template_reinforced_wall_post"), "wall", wall);
	}

	public BlockModelBuilder reinforcedWallSide(String name, ResourceLocation wall, boolean tall)
	{
		return uncheckedSingleTexture(name, modLoc(BLOCK_FOLDER + "/template_reinforced_wall_side" + (tall ? "_tall" : "")), "wall", wall);
	}

	public BlockModelBuilder reinforcedWallInventory(String name, ResourceLocation wall)
	{
		return uncheckedSingleTexture(name, modLoc(BLOCK_FOLDER + "/reinforced_wall_inventory"), "wall", wall);
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
