package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.SecureRedstoneInterfaceBlockEntity;
import net.geforcemods.securitycraft.blocks.SecureRedstoneInterfaceBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.models.SecureRedstoneInterfaceDishModel;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class SecureRedstoneInterfaceRenderer extends TileEntityRenderer<SecureRedstoneInterfaceBlockEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/block/secure_redstone_interface_dish.png");
	private final SecureRedstoneInterfaceDishModel model = new SecureRedstoneInterfaceDishModel();

	public SecureRedstoneInterfaceRenderer(TileEntityRendererDispatcher terd) {
		super(terd);
	}

	@Override
	public void render(SecureRedstoneInterfaceBlockEntity be, float partialTicks, MatrixStack pose, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.tryRenderDelegate(be, partialTicks, pose, buffer, combinedLight, combinedOverlay);

		if (!be.isModuleEnabled(ModuleType.DISGUISE) && !be.getBlockState().getValue(SecureRedstoneInterfaceBlock.SENDER)) {
			pose.translate(0.5D, 0.5D, 0.5D);
			pose.mulPose(be.getBlockState().getValue(SecureRedstoneInterfaceBlock.FACING).getRotation());
			pose.translate(0.0D, -0.49999D, 0.0D);
			model.rotate(MathHelper.lerp(partialTicks, be.getOriginalDishRotationDegrees(), be.getDishRotationDegrees()));
			model.renderToBuffer(pose, buffer.getBuffer(RenderType.entitySolid(TEXTURE)), combinedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
		}
	}
}
