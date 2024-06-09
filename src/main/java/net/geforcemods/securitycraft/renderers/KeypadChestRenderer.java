package net.geforcemods.securitycraft.renderers;

import java.util.Calendar;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;

public class KeypadChestRenderer extends ChestRenderer<ChestBlockEntity> {
	private static final Material ACTIVE = createMaterial("active");
	private static final Material INACTIVE = createMaterial("inactive");
	private static final Material LEFT_ACTIVE = createMaterial("left_active");
	private static final Material LEFT_INACTIVE = createMaterial("left_inactive");
	private static final Material RIGHT_ACTIVE = createMaterial("right_active");
	private static final Material RIGHT_INACTIVE = createMaterial("right_inactive");
	private static final Material CHRISTMAS = createMaterial("christmas");
	private static final Material CHRISTMAS_LEFT = createMaterial("christmas_left");
	private static final Material CHRISTMAS_RIGHT = createMaterial("christmas_right");
	protected boolean isChristmas;

	public KeypadChestRenderer(BlockEntityRendererProvider.Context ctx) {
		super(ctx);

		Calendar calendar = Calendar.getInstance();

		if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26)
			isChristmas = true;
	}

	@Override
	protected Material getMaterial(ChestBlockEntity be, ChestType type) {
		if (isChristmas)
			return getMaterialForType(type, CHRISTMAS_LEFT, CHRISTMAS_RIGHT, CHRISTMAS);
		else if (be.getOpenNess(0.0F) >= 0.9F)
			return getMaterialForType(type, LEFT_ACTIVE, RIGHT_ACTIVE, ACTIVE);
		else
			return getMaterialForType(type, LEFT_INACTIVE, RIGHT_INACTIVE, INACTIVE);
	}

	private Material getMaterialForType(ChestType type, Material left, Material right, Material single) {
		return switch (type) {
			case LEFT -> left;
			case RIGHT -> right;
			case SINGLE -> single;
			default -> single;
		};
	}

	private static Material createMaterial(String name) {
		return new Material(Sheets.CHEST_SHEET, SecurityCraft.resLoc("entity/chest/" + name));
	}
}