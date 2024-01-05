package net.geforcemods.securitycraft.datagen;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class SCBlockModelProvider extends BlockModelProvider {
	public SCBlockModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
		super(output, SecurityCraft.MODID, existingFileHelper);
	}

	public BlockModelBuilder reinforcedCarpet(String name, ResourceLocation wool) {
		return uncheckedSingleTexture(name, modLoc(BLOCK_FOLDER + "/reinforced_carpet"), "block", wool);
	}

	public BlockModelBuilder reinforcedColumn(String name, String side, String end) {
		//@formatter:off
		return withExistingParent(name, modLoc(BLOCK_FOLDER + "/reinforced_cube_column"))
				.texture("side", mcLoc(BLOCK_FOLDER + "/" + side))
				.texture("end", mcLoc(BLOCK_FOLDER + "/" + end));
		//@formatter:on
	}

	@Override
	public BlockModelBuilder fencePost(String name, ResourceLocation texture) {
		return singleTexture(name, modLoc(BLOCK_FOLDER + "/reinforced_fence_post"), texture);
	}

	@Override
	public BlockModelBuilder fenceSide(String name, ResourceLocation texture) {
		return singleTexture(name, modLoc(BLOCK_FOLDER + "/reinforced_fence_side"), texture);
	}

	@Override
	public BlockModelBuilder fenceInventory(String name, ResourceLocation texture) {
		return singleTexture(name, modLoc(BLOCK_FOLDER + "/reinforced_fence_inventory"), texture);
	}

	@Override
	public BlockModelBuilder fenceGate(String name, ResourceLocation texture) {
		return singleTexture(name, modLoc(BLOCK_FOLDER + "/template_reinforced_fence_gate"), texture);
	}

	@Override
	public BlockModelBuilder fenceGateOpen(String name, ResourceLocation texture) {
		return singleTexture(name, modLoc(BLOCK_FOLDER + "/template_reinforced_fence_gate_open"), texture);
	}

	@Override
	public BlockModelBuilder fenceGateWall(String name, ResourceLocation texture) {
		return singleTexture(name, modLoc(BLOCK_FOLDER + "/template_reinforced_fence_gate_wall"), texture);
	}

	@Override
	public BlockModelBuilder fenceGateWallOpen(String name, ResourceLocation texture) {
		return singleTexture(name, modLoc(BLOCK_FOLDER + "/template_reinforced_fence_gate_wall_open"), texture);
	}

	public BlockModelBuilder reinforcedSlab(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
		return sideBottomTop(name, modLoc(BLOCK_FOLDER + "/reinforced_slab"), side, bottom, top);
	}

	public BlockModelBuilder reinforcedSlabTop(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
		return sideBottomTop(name, modLoc(BLOCK_FOLDER + "/reinforced_slab_top"), side, bottom, top);
	}

	public BlockModelBuilder reinforcedStairs(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
		return sideBottomTop(name, modLoc(BLOCK_FOLDER + "/reinforced_stairs"), side, bottom, top);
	}

	public BlockModelBuilder reinforcedStairsOuter(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
		return sideBottomTop(name, modLoc(BLOCK_FOLDER + "/reinforced_outer_stairs"), side, bottom, top);
	}

	public BlockModelBuilder reinforcedStairsInner(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
		return sideBottomTop(name, modLoc(BLOCK_FOLDER + "/reinforced_inner_stairs"), side, bottom, top);
	}

	public BlockModelBuilder reinforcedWallPost(String name, ResourceLocation wall) {
		return uncheckedSingleTexture(name, modLoc(BLOCK_FOLDER + "/template_reinforced_wall_post"), "wall", wall);
	}

	public BlockModelBuilder reinforcedWallSide(String name, ResourceLocation wall, boolean tall) {
		return uncheckedSingleTexture(name, modLoc(BLOCK_FOLDER + "/template_reinforced_wall_side" + (tall ? "_tall" : "")), "wall", wall);
	}

	public BlockModelBuilder reinforcedWallInventory(String name, ResourceLocation wall) {
		return uncheckedSingleTexture(name, modLoc(BLOCK_FOLDER + "/reinforced_wall_inventory"), "wall", wall);
	}

	public BlockModelBuilder uncheckedSingleTexture(String name, ResourceLocation parent, String textureKey, ResourceLocation texture) {
		return getBuilder(name).parent(new UncheckedModelFile(parent)).texture(textureKey, texture);
	}

	private BlockModelBuilder sideBottomTop(String name, ResourceLocation parent, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
		//@formatter:off
		return withExistingParent(name, parent)
				.texture("side", side)
				.texture("bottom", bottom)
				.texture("top", top);
		//@formatter:on
	}

	@Override
	protected void registerModels() {}
}
