package net.geforcemods.securitycraft.renderers;

import org.joml.Quaternionf;

import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.blockentities.SecureTradingStationBlockEntity;
import net.geforcemods.securitycraft.blocks.SecureTradingStation;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SecureTradingStationRenderer implements BlockEntityRenderer<SecureTradingStationBlockEntity> {
	private static final Quaternionf[] ROTATIONS = {
			new Quaternionf().rotateY((float) Math.PI), //south
			new Quaternionf().rotateY((float) Math.PI / 2.0F), //west
			new Quaternionf(), //north
			new Quaternionf().rotateY((float) -Math.PI / 2.0F) //east
	};
	private final ItemRenderer itemRenderer;

	public SecureTradingStationRenderer(BlockEntityRendererProvider.Context ctx) {
		this.itemRenderer = ctx.getItemRenderer();
	}

	@Override
	public void render(SecureTradingStationBlockEntity be, float partialTicks, PoseStack pose, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.tryRenderDelegate(be, partialTicks, pose, buffer, packedLight, packedOverlay);

		if (!be.isModuleEnabled(ModuleType.DISGUISE)) {
			Level level = be.getLevel();
			Direction facing = be.blockState.getValue(SecureTradingStation.FACING);
			ItemStack paymentDisplay = be.getPaymentDisplay();
			ItemStack rewardDisplay = be.getRewardDisplay();

			if (!paymentDisplay.isEmpty())
				renderItem(level, pose, paymentDisplay, facing, 0.225F, buffer, packedLight, itemRenderer);

			if (!rewardDisplay.isEmpty())
				renderItem(level, pose, rewardDisplay, facing, -0.225F, buffer, packedLight, itemRenderer);
		}
	}

	public static void renderItem(Level level, PoseStack pose, ItemStack stack, Direction facing, float sideOffset, MultiBufferSource buffer, int packedLight, ItemRenderer itemRenderer) {
		BakedModel model = itemRenderer.getModel(stack, level, null, 0);
		float verticalSize = model.getTransforms().getTransform(ItemDisplayContext.GROUND).scale.y();

		pose.pushPose();
		pose.translate(0.5F, 0.4F + 0.3F * verticalSize, 0.5F);
		pose.mulPose(ROTATIONS[facing.get2DDataValue()]);
		pose.translate(sideOffset, 0.0F, model.isGui3d() ? 0.0F : 0.1F);
		pose.scale(0.35F, 0.35F, 0.35F);
		itemRenderer.render(stack, ItemDisplayContext.FIXED, false, pose, buffer, packedLight, OverlayTexture.NO_OVERLAY, model);
		pose.popPose();
	}
}
