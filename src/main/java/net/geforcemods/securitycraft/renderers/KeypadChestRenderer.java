package net.geforcemods.securitycraft.renderers;

import java.util.Calendar;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.blockentities.KeypadChestBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.tileentity.ChestTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.ChestType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KeypadChestRenderer extends ChestTileEntityRenderer<KeypadChestBlockEntity> {
	private static final RenderMaterial ACTIVE = createMaterial("active");
	private static final RenderMaterial INACTIVE = createMaterial("inactive");
	private static final RenderMaterial LEFT_ACTIVE = createMaterial("left_active");
	private static final RenderMaterial LEFT_INACTIVE = createMaterial("left_inactive");
	private static final RenderMaterial RIGHT_ACTIVE = createMaterial("right_active");
	private static final RenderMaterial RIGHT_INACTIVE = createMaterial("right_inactive");
	private static final RenderMaterial CHRISTMAS = createMaterial("christmas");
	private static final RenderMaterial CHRISTMAS_LEFT = createMaterial("christmas_left");
	private static final RenderMaterial CHRISTMAS_RIGHT = createMaterial("christmas_right");
	protected boolean isChristmas;

	public KeypadChestRenderer(TileEntityRendererDispatcher terd) {
		super(terd);

		Calendar calendar = Calendar.getInstance();

		if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26)
			isChristmas = true;
	}

	@Override
	public void render(KeypadChestBlockEntity be, float partialTicks, MatrixStack poseStack, IRenderTypeBuffer buffer, int packedLight, int packedOverlay) {
		ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.tryRenderDelegate(be, partialTicks, poseStack, buffer, packedLight, packedOverlay);

		if (!be.isModuleEnabled(ModuleType.DISGUISE))
			super.render(be, partialTicks, poseStack, buffer, packedLight, packedOverlay);
	}

	@Override
	protected RenderMaterial getMaterial(KeypadChestBlockEntity be, ChestType type) {
		if (isChristmas)
			return getMaterialForType(type, CHRISTMAS_LEFT, CHRISTMAS_RIGHT, CHRISTMAS);
		else if (be.getOpenNess(0.0F) >= 0.9F)
			return getMaterialForType(type, LEFT_ACTIVE, RIGHT_ACTIVE, ACTIVE);
		else
			return getMaterialForType(type, LEFT_INACTIVE, RIGHT_INACTIVE, INACTIVE);
	}

	private RenderMaterial getMaterialForType(ChestType type, RenderMaterial left, RenderMaterial right, RenderMaterial single) {
		switch (type) {
			case LEFT:
				return left;
			case RIGHT:
				return right;
			case SINGLE:
			default:
				return single;
		}
	}

	private static RenderMaterial createMaterial(String name) {
		return new RenderMaterial(Atlases.CHEST_SHEET, new ResourceLocation("securitycraft", "entity/chest/" + name));
	}
}