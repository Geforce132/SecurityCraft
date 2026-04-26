package net.geforcemods.securitycraft.renderers;

import org.joml.Quaternionf;

import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.blockentities.SecureTradingStationBlockEntity;
import net.geforcemods.securitycraft.blocks.SecureTradingStationBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.renderers.state.SecureTradingStationRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ItemClusterRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SecureTradingStationRenderer implements BlockEntityRenderer<SecureTradingStationBlockEntity, SecureTradingStationRenderState> {
	private static final Quaternionf[] ROTATIONS = {
			new Quaternionf().rotateY((float) Math.PI), //south
			new Quaternionf().rotateY((float) Math.PI / 2.0F), //west
			new Quaternionf(), //north
			new Quaternionf().rotateY((float) -Math.PI / 2.0F) //east
	};
	private final ItemModelResolver itemModelResolver;

	public SecureTradingStationRenderer(BlockEntityRendererProvider.Context ctx) {
		itemModelResolver = ctx.itemModelResolver();
	}

	@Override
	public void submit(SecureTradingStationRenderState state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
		ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.trySubmitDelegate(state.disguiseRenderState, pose, collector, camera);

		if (!state.isDisguised) {
			if (!state.payment.isEmpty())
				submitItem(state, pose, state.payment, 0.225F, collector, camera);

			if (!state.reward.isEmpty())
				submitItem(state, pose, state.reward, -0.225F, collector, camera);
		}
	}

	private void submitItem(SecureTradingStationRenderState state, PoseStack pose, ItemStackRenderState stack, float sideOffset, SubmitNodeCollector collector, CameraRenderState camera) {
		pose.pushPose();
		pose.translate(0.5F, 0.5F, 0.5F);
		pose.mulPose(state.rotation);
		pose.translate(sideOffset, 0.0F, 0.0F);
		pose.scale(0.35F, 0.35F, 0.35F);
		stack.submit(pose, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
		pose.popPose();
	}

	@Override
	public SecureTradingStationRenderState createRenderState() {
		return new SecureTradingStationRenderState();
	}

	@Override
	public void extractRenderState(SecureTradingStationBlockEntity be, SecureTradingStationRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
		Level level = be.getLevel();
		Direction facing = be.getBlockState().getValue(SecureTradingStationBlock.FACING);

		BlockEntityRenderer.super.extractRenderState(be, state, partialTick, cameraPos, crumblingOverlay);
		state.disguiseRenderState = ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.tryExtractFromDelegate(be, partialTick, cameraPos, crumblingOverlay);
		extractItem(level, state.payment, be.getPaymentDisplay());
		extractItem(level, state.reward, be.getRewardDisplay());
		state.rotation = ROTATIONS[facing.get2DDataValue()];
		state.isDisguised = be.isModuleEnabled(ModuleType.DISGUISE);
	}

	private void extractItem(Level level, ItemStackRenderState state, ItemStack stack) {
		int seed = ItemClusterRenderState.getSeedForItemStack(stack);

		itemModelResolver.updateForTopItem(state, stack, ItemDisplayContext.FIXED, level, null, seed);
	}
}
