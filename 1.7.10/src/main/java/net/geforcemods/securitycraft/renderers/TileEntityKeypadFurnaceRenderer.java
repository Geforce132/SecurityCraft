package net.geforcemods.securitycraft.renderers;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.models.ModelKeypadFurnaceActive;
import net.geforcemods.securitycraft.models.ModelKeypadFurnaceDeactivated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TileEntityKeypadFurnaceRenderer extends TileEntitySpecialRenderer {

	private ResourceLocation activeFurnaceTexture = new ResourceLocation("securitycraft:textures/blocks/keypadFurnaceActive.png");
	private ResourceLocation deactivatedFurnaceTexture = new ResourceLocation("securitycraft:textures/blocks/keypadFurnaceDeactivated.png");

	public TileEntityKeypadFurnaceRenderer(){
		activeFurnaceTexture = new ResourceLocation("securitycraft:textures/blocks/keypadFurnaceActive.png");
		deactivatedFurnaceTexture = new ResourceLocation("securitycraft:textures/blocks/keypadFurnaceDeactivated.png");
	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {

		boolean isActive = false;
		ModelKeypadFurnaceDeactivated modelDeactivated = new ModelKeypadFurnaceDeactivated();
		ModelKeypadFurnaceActive modelActive = new ModelKeypadFurnaceActive();

		if(te.hasWorldObj() && te.getBlockMetadata() >= 5)
			isActive = true;

		int meta = te.hasWorldObj() ? te.getBlockMetadata() : te.blockMetadata;
		float rotation = 0F;

		if(te.hasWorldObj()){
			Tessellator tessellator = Tessellator.instance;
			float brightness = te.getWorld().getLightBrightness(te.xCoord, te.yCoord, te.zCoord);
			int skyBrightness = te.getWorld().getLightBrightnessForSkyBlocks(te.xCoord, te.yCoord, te.zCoord, 0);
			int l1 = skyBrightness % 65536;
			int l2 = skyBrightness / 65536;
			tessellator.setColorOpaque_F(brightness, brightness, brightness);
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, l1, l2);
		}

		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);

		if(isActive)
			Minecraft.getMinecraft().renderEngine.bindTexture(activeFurnaceTexture);
		else
			Minecraft.getMinecraft().renderEngine.bindTexture(deactivatedFurnaceTexture);

		GL11.glPushMatrix();

		if(te.hasWorldObj()){
			if(meta == 1 || meta == 6)
				rotation = 0F;
			else if(meta == 2 || meta == 7)
				rotation = 1F;
			else if(meta == 3 || meta == 8)
				rotation = -10000F;
			else if(meta == 4 || meta == 9)
				rotation = -1F;
		}
		else
			rotation = -1F;

		GL11.glRotatef(180F, rotation, 0.0F, 1.0F);

		if(isActive)
			modelActive.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		else
			modelDeactivated.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

}
