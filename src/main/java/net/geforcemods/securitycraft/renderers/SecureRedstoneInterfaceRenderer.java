package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.SecureRedstoneInterfaceBlockEntity;
import net.geforcemods.securitycraft.blocks.SecureRedstoneInterfaceBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SecureRedstoneInterfaceRenderer implements BlockEntityRenderer<SecureRedstoneInterfaceBlockEntity> {
	private static final ResourceLocation TEXTURE = SecurityCraft.resLoc("textures/block/secure_redstone_interface_dish.png");
	private final SecureRedstoneInterfaceDishModel model;

	public SecureRedstoneInterfaceRenderer(BlockEntityRendererProvider.Context ctx) {
		model = new SecureRedstoneInterfaceDishModel(ctx.bakeLayer(ClientHandler.SECURE_REDSTONE_INTERFACE_DISH_LAYER_LOCATION));
	}

	@Override
	public void render(SecureRedstoneInterfaceBlockEntity be, float partialTicks, PoseStack pose, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.tryRenderDelegate(be, partialTicks, pose, buffer, combinedLight, combinedOverlay);

		if (!be.isModuleEnabled(ModuleType.DISGUISE) && !be.isDisabled() && !be.getBlockState().getValue(SecureRedstoneInterfaceBlock.SENDER)) {
			pose.translate(0.5D, 0.5D, 0.5D);
			pose.mulPose(be.getBlockState().getValue(SecureRedstoneInterfaceBlock.FACING).getRotation());
			pose.translate(0.0D, -0.49999D, 0.0D);
			model.rotate(Mth.lerp(partialTicks, be.getOriginalDishRotationDegrees(), be.getDishRotationDegrees()));
			model.renderToBuffer(pose, buffer.getBuffer(RenderType.entitySolid(TEXTURE)), combinedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
		}
	}
}
