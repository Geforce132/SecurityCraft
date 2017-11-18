package net.geforcemods.securitycraft.renderers;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.models.ModelProtecto;
import net.geforcemods.securitycraft.tileentity.TileEntityProtecto;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TileEntityProtectoRenderer extends TileEntitySpecialRenderer {

	private ModelProtecto protectoModel;
	private ResourceLocation activeTexture = new ResourceLocation("securitycraft:textures/blocks/protectoActive.png");
	private ResourceLocation deactivatedTexture = new ResourceLocation("securitycraft:textures/blocks/protectoDeactivated.png");

	public TileEntityProtectoRenderer() {
		protectoModel = new ModelProtecto();
	}

	@Override
	public void renderTileEntityAt(TileEntity par1TileEntity, double x, double y, double z, float par5) {
		if(par1TileEntity.hasWorldObj()){
			Tessellator tessellator = Tessellator.instance;
			float f = par1TileEntity.getWorldObj().getLightBrightness(par1TileEntity.xCoord, par1TileEntity.yCoord, par1TileEntity.zCoord);
			int l = par1TileEntity.getWorldObj().getLightBrightnessForSkyBlocks(par1TileEntity.xCoord, par1TileEntity.yCoord, par1TileEntity.zCoord, 0);
			int l1 = l % 65536;
			int l2 = l / 65536;
			tessellator.setColorOpaque_F(f, f, f);
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, l1, l2);
		}

		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);

		if(par1TileEntity.hasWorldObj() && ((TileEntityProtecto) par1TileEntity).canAttack())
			Minecraft.getMinecraft().renderEngine.bindTexture(activeTexture);
		else
			Minecraft.getMinecraft().renderEngine.bindTexture(deactivatedTexture);

		GL11.glPushMatrix();

		GL11.glRotatef(180F, 0, 0.0F, 1.0F);

		protectoModel.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

}
