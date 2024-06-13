package net.geforcemods.securitycraft.renderers;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.SecureRedstoneInterfaceBlockEntity;
import net.geforcemods.securitycraft.blocks.SecureRedstoneInterfaceBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.models.SecureRedstoneInterfaceDishModel;
import net.geforcemods.securitycraft.util.BlockEntityRenderDelegate;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class SecureRedstoneInterfaceRenderer extends TileEntitySpecialRenderer<SecureRedstoneInterfaceBlockEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/blocks/secure_redstone_interface_dish.png");
	private final SecureRedstoneInterfaceDishModel model = new SecureRedstoneInterfaceDishModel();

	@Override
	public void render(SecureRedstoneInterfaceBlockEntity be, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		BlockEntityRenderDelegate.DISGUISED_BLOCK.tryRenderDelegate(be, x, y, z, partialTicks, destroyStage, alpha);

		if (!be.isModuleEnabled(ModuleType.DISGUISE)) {
			IBlockState state = be.getWorld().getBlockState(be.getPos());

			if (!state.getValue(SecureRedstoneInterfaceBlock.SENDER)) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
				adjustForRotation(state);
				GlStateManager.translate(0.0D, -0.4999D, 0.0D);
				Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
				model.rotate(Utils.lerp(partialTicks, be.getOriginalDishRotationDegrees(), be.getDishRotationDegrees()));
				model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
				GlStateManager.popMatrix();
			}
		}
	}

	private void adjustForRotation(IBlockState state) {
		EnumFacing facing = state.getValue(SecureRedstoneInterfaceBlock.FACING);

		switch (facing) {
			case DOWN:
				GlStateManager.rotate(180, 1, 0, 0);
				break;
			case UP:
				break;
			case NORTH:
				GlStateManager.rotate(90, 1, 0, 0);
				GlStateManager.rotate(180, 0, 0, 1);
				break;
			case SOUTH:
				GlStateManager.rotate(90, 1, 0, 0);
				break;
			case WEST:
				GlStateManager.rotate(90, 1, 0, 0);
				GlStateManager.rotate(90, 0, 0, 1);
				break;
			case EAST:
			default:
				GlStateManager.rotate(90, 1, 0, 0);
				GlStateManager.rotate(-90, 0, 0, 1);
		}
	}
}
