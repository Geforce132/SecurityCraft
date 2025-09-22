package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.SecureRedstoneInterfaceBlockEntity;
import net.geforcemods.securitycraft.blocks.SecureRedstoneInterfaceBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.models.SecureRedstoneInterfaceDishModel;
import net.geforcemods.securitycraft.renderers.state.SecureRedstoneInterfaceRenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class SecureRedstoneInterfaceRenderer implements BlockEntityRenderer<SecureRedstoneInterfaceBlockEntity, SecureRedstoneInterfaceRenderState> {
	private static final ResourceLocation TEXTURE = SecurityCraft.resLoc("textures/block/secure_redstone_interface_dish.png");
	private final SecureRedstoneInterfaceDishModel model;

	public SecureRedstoneInterfaceRenderer(BlockEntityRendererProvider.Context ctx) {
		model = new SecureRedstoneInterfaceDishModel(ctx.bakeLayer(ClientHandler.SECURE_REDSTONE_INTERFACE_DISH_LAYER_LOCATION));
	}

	@Override
	public void submit(SecureRedstoneInterfaceRenderState state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
		//TODO delegate renderer
		//ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.tryRenderDelegate(be, partialTicks, pose, buffer, combinedLight, combinedOverlay, cameraPos);

		if (!state.hasDisguiseModule && !state.isSender) {
			pose.translate(0.5D, 0.5D, 0.5D);
			pose.mulPose(state.facing.getRotation());
			pose.translate(0.0D, -0.49999D, 0.0D);
			model.rotate(state.dishRotationDegrees);
			collector.submitModel(model, null, pose, RenderType.entitySolid(TEXTURE), state.lightCoords, OverlayTexture.NO_OVERLAY, 0, state.breakProgress);
		}
	}

	@Override
	public SecureRedstoneInterfaceRenderState createRenderState() {
		return new SecureRedstoneInterfaceRenderState();
	}

	@Override
	public void extractRenderState(SecureRedstoneInterfaceBlockEntity be, SecureRedstoneInterfaceRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
		BlockEntityRenderer.super.extractRenderState(be, state, partialTick, cameraPos, crumblingOverlay);

		BlockState blockState = be.getBlockState();

		state.facing = blockState.getValue(SecureRedstoneInterfaceBlock.FACING);
		state.hasDisguiseModule = be.isModuleEnabled(ModuleType.DISGUISE);
		state.isSender = blockState.getValue(SecureRedstoneInterfaceBlock.SENDER);
		state.dishRotationDegrees = Mth.lerp(partialTick, be.getOriginalDishRotationDegrees(), be.getDishRotationDegrees());
	}
}
