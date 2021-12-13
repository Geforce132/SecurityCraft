package net.geforcemods.securitycraft.renderers;

import net.geforcemods.securitycraft.models.ModelSonicSecuritySystem;
import net.geforcemods.securitycraft.tileentity.TileEntitySonicSecuritySystem;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntitySonicSecuritySystemRenderer extends TileEntitySpecialRenderer<TileEntitySonicSecuritySystem> {

	private static final ModelSonicSecuritySystem MODEL = new ModelSonicSecuritySystem();
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/blocks/sonic_security_system.png");
	private static final TextComponentTranslation RECORDING_TEXT = Utils.localize("gui.securitycraft:sonic_security_system.recording");
	private static final TextComponentTranslation LISTENING_TEXT = Utils.localize("gui.securitycraft:sonic_security_system.listening");

	@Override
	public void render(TileEntitySonicSecuritySystem te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
	{
		boolean recording = te.isRecording();

		if(recording || te.isListening())
		{
			EntityPlayer player = Minecraft.getMinecraft().player;

			setLightmapDisabled(true);
			EntityRenderer.drawNameplate(getFontRenderer(), (recording ? RECORDING_TEXT : LISTENING_TEXT).getFormattedText(), (float)x + 0.5F, (float)y + 1.5F, (float)z + 0.5F, 0, player.rotationYaw, player.rotationPitch, false, false);
			setLightmapDisabled(false);
		}

		GlStateManager.translate(x + 0.5D, y + 1.5D, z + 0.5D);
		GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
		Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
		MODEL.setRadarRotation(te.radarRotationDegrees);
		MODEL.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
	}
}
