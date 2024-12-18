package net.geforcemods.securitycraft.datagen;

import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.resources.ResourceLocation;

public class DataGenConstants {
	private DataGenConstants() {}

	public class SCTextureSlots {
		public static final TextureSlot BLOCK = TextureSlot.create("block");

		private SCTextureSlots() {}
	}

	public class SCModelTemplates {
		public static final ModelTemplate CUSTOM_REINFORCED_FENCE_GATE_CLOSED = ModelTemplates.create("securitycraft:template_reinforced_custom_fence_gate", TextureSlot.TEXTURE, TextureSlot.PARTICLE);
		public static final ModelTemplate CUSTOM_REINFORCED_FENCE_GATE_OPEN = ModelTemplates.create("securitycraft:template_reinforced_custom_fence_gate_open", "_open", TextureSlot.TEXTURE, TextureSlot.PARTICLE);
		public static final ModelTemplate CUSTOM_REINFORCED_FENCE_GATE_WALL_CLOSED = ModelTemplates.create("securitycraft:template_reinforced_custom_fence_gate_wall", "_wall", TextureSlot.TEXTURE, TextureSlot.PARTICLE);
		public static final ModelTemplate CUSTOM_REINFORCED_FENCE_GATE_WALL_OPEN = ModelTemplates.create("securitycraft:template_reinforced_custom_fence_gate_wall_open", "_wall_open", TextureSlot.TEXTURE, TextureSlot.PARTICLE);
		public static final ModelTemplate CUSTOM_REINFORCED_FENCE_INVENTORY = ModelTemplates.create("securitycraft:custom_reinforced_fence_inventory", "_inventory", TextureSlot.TEXTURE);
		public static final ModelTemplate CUSTOM_REINFORCED_FENCE_POST = ModelTemplates.create("securitycraft:custom_reinforced_fence_post", "_post", TextureSlot.TEXTURE, TextureSlot.PARTICLE);
		public static final ModelTemplate CUSTOM_REINFORCED_FENCE_SIDE_EAST = ModelTemplates.create("securitycraft:custom_reinforced_fence_side_east", "_side_east", TextureSlot.TEXTURE);
		public static final ModelTemplate CUSTOM_REINFORCED_FENCE_SIDE_NORTH = ModelTemplates.create("securitycraft:custom_reinforced_fence_side_north", "_side_north", TextureSlot.TEXTURE);
		public static final ModelTemplate CUSTOM_REINFORCED_FENCE_SIDE_SOUTH = ModelTemplates.create("securitycraft:custom_reinforced_fence_side_south", "_side_south", TextureSlot.TEXTURE);
		public static final ModelTemplate CUSTOM_REINFORCED_FENCE_SIDE_WEST = ModelTemplates.create("securitycraft:custom_reinforced_fence_side_west", "_side_west", TextureSlot.TEXTURE);
		public static final ModelTemplate REINFORCED_BUTTON = ModelTemplates.create("securitycraft:reinforced_button", TextureSlot.TEXTURE);
		public static final ModelTemplate REINFORCED_BUTTON_INVENTORY = ModelTemplates.create("securitycraft:reinforced_button_inventory", "_inventory", TextureSlot.TEXTURE);
		public static final ModelTemplate REINFORCED_BUTTON_PRESSED = ModelTemplates.create("securitycraft:reinforced_button_pressed", "_pressed", TextureSlot.TEXTURE);
		public static final ModelTemplate REINFORCED_CARPET = ModelTemplates.create("securitycraft:reinforced_carpet", SCTextureSlots.BLOCK);
		public static final ModelTemplate REINFORCED_CUBE_ALL = ModelTemplates.create("securitycraft:reinforced_cube_all", TextureSlot.ALL);
		public static final ModelTemplate REINFORCED_CUBE_BOTTOM_TOP = ModelTemplates.create("securitycraft:reinforced_cube_bottom_top", TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.SIDE);
		public static final ModelTemplate REINFORCED_CUBE_COLUMN = ModelTemplates.create("securitycraft:reinforced_cube_column", TextureSlot.END, TextureSlot.SIDE);
		public static final ModelTemplate REINFORCED_CUBE_COLUMN_MIRRORED = ModelTemplates.create("securitycraft:reinforced_cube_column_mirrored", "_mirrored", TextureSlot.END, TextureSlot.SIDE);
		public static final ModelTemplate REINFORCED_CUBE_MIRRORED_ALL = ModelTemplates.create("securitycraft:reinforced_cube_mirrored_all", "_mirrored", TextureSlot.ALL);
		public static final ModelTemplate REINFORCED_CUBE_NORTH_WEST_MIRRORED_ALL = ModelTemplates.create("securitycraft:reinforced_cube_north_west_mirrored_all", "_north_west_mirrored", TextureSlot.ALL);
		public static final ModelTemplate REINFORCED_FENCE_GATE_CLOSED = ModelTemplates.create("securitycraft:template_reinforced_fence_gate", TextureSlot.TEXTURE);
		public static final ModelTemplate REINFORCED_FENCE_GATE_OPEN = ModelTemplates.create("securitycraft:template_reinforced_fence_gate_open", "_open", TextureSlot.TEXTURE);
		public static final ModelTemplate REINFORCED_FENCE_GATE_WALL_CLOSED = ModelTemplates.create("securitycraft:template_reinforced_fence_gate_wall", "_wall", TextureSlot.TEXTURE);
		public static final ModelTemplate REINFORCED_FENCE_GATE_WALL_OPEN = ModelTemplates.create("securitycraft:template_reinforced_fence_gate_wall_open", "_wall_open", TextureSlot.TEXTURE);
		public static final ModelTemplate REINFORCED_FENCE_INVENTORY = ModelTemplates.create("securitycraft:reinforced_fence_inventory", "_inventory", TextureSlot.TEXTURE);
		public static final ModelTemplate REINFORCED_FENCE_POST = ModelTemplates.create("securitycraft:reinforced_fence_post", "_post", TextureSlot.TEXTURE);
		public static final ModelTemplate REINFORCED_FENCE_SIDE = ModelTemplates.create("securitycraft:reinforced_fence_side", "_side", TextureSlot.TEXTURE);
		public static final ModelTemplate REINFORCED_PRESSURE_PLATE_DOWN = ModelTemplates.create("securitycraft:reinforced_pressure_plate_down", "_down", TextureSlot.TEXTURE);
		public static final ModelTemplate REINFORCED_PRESSURE_PLATE_UP = ModelTemplates.create("securitycraft:reinforced_pressure_plate_up", TextureSlot.TEXTURE);
		public static final ModelTemplate REINFORCED_SLAB_BOTTOM = ModelTemplates.create("securitycraft:reinforced_slab", TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
		public static final ModelTemplate REINFORCED_SLAB_TOP = ModelTemplates.create("securitycraft:reinforced_slab_top", "_top", TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
		public static final ModelTemplate REINFORCED_STAIRS_INNER = ModelTemplates.create("securitycraft:reinforced_inner_stairs", "_inner", TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
		public static final ModelTemplate REINFORCED_STAIRS_OUTER = ModelTemplates.create("securitycraft:reinforced_outer_stairs", "_outer", TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
		public static final ModelTemplate REINFORCED_STAIRS_STRAIGHT = ModelTemplates.create("securitycraft:reinforced_stairs", TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
		public static final ModelTemplate REINFORCED_WALL_INVENTORY = ModelTemplates.create("securitycraft:reinforced_wall_inventory", "_inventory", TextureSlot.WALL);
		public static final ModelTemplate REINFORCED_WALL_LOW_SIDE = ModelTemplates.create("securitycraft:template_reinforced_wall_side", "_side", TextureSlot.WALL);
		public static final ModelTemplate REINFORCED_WALL_POST = ModelTemplates.create("securitycraft:template_reinforced_wall_post", "_post", TextureSlot.WALL);
		public static final ModelTemplate REINFORCED_WALL_TALL_SIDE = ModelTemplates.create("securitycraft:template_reinforced_wall_side_tall", "_side_tall", TextureSlot.WALL);

		private SCModelTemplates() {}
	}

	public class SCTexturedModels {
		public static final TexturedModel.Provider REINFORCED_CARPET = TexturedModel.createDefault(block -> TextureMapping.singleSlot(SCTextureSlots.BLOCK, TextureMapping.getBlockTexture(block)), SCModelTemplates.REINFORCED_CARPET);
		public static final TexturedModel.Provider REINFORCED_COLUMN = TexturedModel.createDefault(TextureMapping::column, SCModelTemplates.REINFORCED_CUBE_COLUMN);
		public static final TexturedModel.Provider REINFORCED_COLUMN_WITH_WALL = TexturedModel.createDefault(TextureMapping::columnWithWall, SCModelTemplates.REINFORCED_CUBE_COLUMN);
		public static final TexturedModel.Provider REINFORCED_CUBE = TexturedModel.createDefault(TextureMapping::cube, SCModelTemplates.REINFORCED_CUBE_ALL);
		public static final TexturedModel.Provider REINFORCED_TOP_BOTTOM_WITH_WALL = TexturedModel.createDefault(TextureMapping::cubeBottomTopWithWall, SCModelTemplates.REINFORCED_CUBE_BOTTOM_TOP);

		public static TexturedModel createAllSame(ResourceLocation location) {
			return new TexturedModel(TextureMapping.cube(location), SCModelTemplates.REINFORCED_CUBE_ALL);
		}

		private SCTexturedModels() {}
	}
}
