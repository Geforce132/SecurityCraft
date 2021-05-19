package net.geforcemods.securitycraft.util;

import java.util.Arrays;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.network.server.UpdateNBTTagOnServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;

public class ClientUtils{
	private static final ResourceLocation SMART_MODULE_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/item/smart_module.png");

	public static void closePlayerScreen(){
		Minecraft.getInstance().player.closeScreen();
	}

	/**
	 * Returns the current Minecraft in-game time, in a 12-hour AM/PM format.
	 */
	public static String getFormattedMinecraftTime(){
		Long time = Minecraft.getInstance().world.getDayTime();

		int hours24 = (int) ((float) time.longValue() / 1000L + 6L) % 24;
		int hours = hours24 % 12;
		int minutes = (int) (time.longValue() / 16.666666F % 60.0F);

		return String.format("%02d:%02d %s", Integer.valueOf(hours < 1 ? 12 : hours), Integer.valueOf(minutes), hours24 < 12 ? "AM" : "PM");
	}

	/**
	 * Sends the client-side CompoundNBT of a player's held item to the server.
	 */
	public static void syncItemNBT(ItemStack item){
		SecurityCraft.channel.sendToServer(new UpdateNBTTagOnServer(item));
	}

	@OnlyIn(Dist.CLIENT)
	public static void renderSmartModuleInfo(MatrixStack matrix, ITextComponent moduleTooltip, ITextComponent noModuleTooltip, boolean isSmart, int guiLeft, int guiTop, int screenWidth, int screenHeight, int mouseX, int mouseY)
	{
		Minecraft mc = Minecraft.getInstance();
		float alpha = isSmart ? 1.0F : 0.5F;
		int moduleLeft = guiLeft + 5;
		int moduleRight = moduleLeft + 16;
		int moduleTop = guiTop + 5;
		int moduleBottom = moduleTop + 16;
		Matrix4f m4f = matrix.getLast().getMatrix();
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();

		RenderSystem.enableAlphaTest();
		RenderSystem.enableBlend();
		RenderSystem.defaultAlphaFunc();
		RenderSystem.defaultBlendFunc();
		mc.getTextureManager().bindTexture(SMART_MODULE_TEXTURE);
		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
		bufferBuilder.pos(m4f, moduleLeft, moduleBottom, 0).color(1.0F, 1.0F, 1.0F, alpha).tex(0, 1).endVertex();
		bufferBuilder.pos(m4f, moduleRight, moduleBottom, 0).color(1.0F, 1.0F, 1.0F, alpha).tex(1, 1).endVertex();
		bufferBuilder.pos(m4f, moduleRight, moduleTop, 0).color(1.0F, 1.0F, 1.0F, alpha).tex(1, 0).endVertex();
		bufferBuilder.pos(m4f, moduleLeft, moduleTop, 0).color(1.0F, 1.0F, 1.0F, alpha).tex(0, 0).endVertex();
		bufferBuilder.finishDrawing();
		WorldVertexBufferUploader.draw(bufferBuilder);
		RenderSystem.disableBlend();

		if(mouseX >= moduleLeft && mouseX < moduleRight && mouseY >= moduleTop && mouseY <= moduleBottom)
		{
			ITextComponent text = isSmart ? moduleTooltip : noModuleTooltip;

			if(text != null)
				GuiUtils.drawHoveringText(matrix, Arrays.asList(text), mouseX, mouseY, screenWidth, screenHeight, -1, mc.fontRenderer);
		}
	}
}