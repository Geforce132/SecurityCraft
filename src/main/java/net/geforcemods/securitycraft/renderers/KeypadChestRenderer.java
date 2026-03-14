package net.geforcemods.securitycraft.renderers;

import java.util.Calendar;

import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.renderers.state.KeypadChestRenderState;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.Vec3;

public class KeypadChestRenderer extends ChestRenderer<ChestBlockEntity> {
	private static final SpriteId ACTIVE = createMaterial("active");
	private static final SpriteId INACTIVE = createMaterial("inactive");
	private static final SpriteId LEFT_ACTIVE = createMaterial("left_active");
	private static final SpriteId LEFT_INACTIVE = createMaterial("left_inactive");
	private static final SpriteId RIGHT_ACTIVE = createMaterial("right_active");
	private static final SpriteId RIGHT_INACTIVE = createMaterial("right_inactive");
	private static final SpriteId CHRISTMAS = createMaterial("christmas");
	private static final SpriteId CHRISTMAS_LEFT = createMaterial("christmas_left");
	private static final SpriteId CHRISTMAS_RIGHT = createMaterial("christmas_right");
	protected boolean isChristmas;

	public KeypadChestRenderer(BlockEntityRendererProvider.Context ctx) {
		super(ctx);

		Calendar calendar = Calendar.getInstance();

		if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26)
			isChristmas = true;
	}

	@Override
	public void submit(ChestRenderState state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
		if (state instanceof KeypadChestRenderState keypadChestRenderState) {
			ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.trySubmitDelegate(keypadChestRenderState.disguiseRenderState, pose, collector, camera);

			if (!keypadChestRenderState.isDisguised)
				super.submit(state, pose, collector, camera);
		}
	}

	@Override
	public ChestRenderState createRenderState() {
		return new KeypadChestRenderState();
	}

	@Override
	public void extractRenderState(ChestBlockEntity be, ChestRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
		super.extractRenderState(be, state, partialTick, cameraPos, crumblingOverlay);

		if (state instanceof KeypadChestRenderState keypadChestRenderState) {
			keypadChestRenderState.disguiseRenderState = ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.tryExtractFromDelegate(be, partialTick, cameraPos, crumblingOverlay);
			keypadChestRenderState.isDisguised = be instanceof IModuleInventory moduleInv && moduleInv.isModuleEnabled(ModuleType.DISGUISE);
		}

		if (isChristmas)
			state.customSprite = getMaterialForType(state.type, CHRISTMAS_LEFT, CHRISTMAS_RIGHT, CHRISTMAS);
		else if (be.getOpenNess(0.0F) >= 0.9F)
			state.customSprite = getMaterialForType(state.type, LEFT_ACTIVE, RIGHT_ACTIVE, ACTIVE);
		else
			state.customSprite = getMaterialForType(state.type, LEFT_INACTIVE, RIGHT_INACTIVE, INACTIVE);
	}

	private SpriteId getMaterialForType(ChestType type, SpriteId left, SpriteId right, SpriteId single) {
		return switch (type) {
			case LEFT -> left;
			case RIGHT -> right;
			case SINGLE -> single;
		};
	}

	private static SpriteId createMaterial(String name) {
		return new SpriteId(Sheets.CHEST_SHEET, SecurityCraft.resLoc("entity/chest/" + name));
	}
}