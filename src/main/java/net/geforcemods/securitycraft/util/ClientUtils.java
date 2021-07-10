package net.geforcemods.securitycraft.util;

import java.util.Arrays;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.UpdateNBTTagOnServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.gui.GuiUtils;

public class ClientUtils{
	private static final ResourceLocation[] MODULE_TEXTURES = {
			new ResourceLocation(SecurityCraft.MODID, "textures/item/module_background.png"),
			new ResourceLocation(SecurityCraft.MODID, "textures/item/whitelist_module.png"),
			new ResourceLocation(SecurityCraft.MODID, "textures/item/blacklist_module.png"),
			new ResourceLocation(SecurityCraft.MODID, "textures/item/harming_module.png"),
			new ResourceLocation(SecurityCraft.MODID, "textures/item/smart_module.png"),
			new ResourceLocation(SecurityCraft.MODID, "textures/item/storage_module.png"),
			new ResourceLocation(SecurityCraft.MODID, "textures/item/disguise_module.png"),
			new ResourceLocation(SecurityCraft.MODID, "textures/item/module_background.png")
	};
	private static final ResourceLocation REDSTONE_TEXTURE = new ResourceLocation("textures/item/redstone.png");
	private static final ResourceLocation SUGAR_TEXTURE = new ResourceLocation("textures/item/sugar.png");

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

	public static void renderModuleInfo(ModuleType module, String moduleTooltip, String noModuleTooltip, boolean isModuleInstalled, int moduleLeft, int moduleTop, int screenWidth, int screenHeight, int mouseX, int mouseY)
	{
		Minecraft mc = Minecraft.getInstance();
		float alpha = isModuleInstalled ? 1.0F : 0.5F;
		int moduleRight = moduleLeft + 16;
		int moduleBottom = moduleTop + 16;
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();

		RenderSystem.enableAlphaTest();
		RenderSystem.enableBlend();
		RenderSystem.defaultAlphaFunc();
		RenderSystem.defaultBlendFunc();

		mc.getTextureManager().bindTexture(MODULE_TEXTURES[module.ordinal()]);
		drawTexture(bufferBuilder, moduleLeft, moduleTop, moduleRight, moduleBottom, alpha);

		if(module == ModuleType.REDSTONE)
		{
			mc.getTextureManager().bindTexture(REDSTONE_TEXTURE);
			drawTexture(bufferBuilder, moduleLeft, moduleTop, moduleRight, moduleBottom, alpha);
		}
		else if(module == ModuleType.SPEED)
		{
			mc.getTextureManager().bindTexture(SUGAR_TEXTURE);
			drawTexture(bufferBuilder, moduleLeft, moduleTop, moduleRight, moduleBottom, alpha);
		}

		RenderSystem.disableBlend();

		if(mouseX >= moduleLeft && mouseX < moduleRight && mouseY >= moduleTop && mouseY <= moduleBottom)
		{
			String text = isModuleInstalled ? moduleTooltip : noModuleTooltip;

			if(text != null && !text.isEmpty())
				GuiUtils.drawHoveringText(Arrays.asList(text), mouseX, mouseY, screenWidth, screenHeight, -1, mc.fontRenderer);
		}
	}

	private static void drawTexture(BufferBuilder bufferBuilder, int moduleLeft, int moduleTop, int moduleRight, int moduleBottom, float alpha)
	{
		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
		bufferBuilder.pos(moduleLeft, moduleBottom, 0).color(1.0F, 1.0F, 1.0F, alpha).tex(0, 1).endVertex();
		bufferBuilder.pos(moduleRight, moduleBottom, 0).color(1.0F, 1.0F, 1.0F, alpha).tex(1, 1).endVertex();
		bufferBuilder.pos(moduleRight, moduleTop, 0).color(1.0F, 1.0F, 1.0F, alpha).tex(1, 0).endVertex();
		bufferBuilder.pos(moduleLeft, moduleTop, 0).color(1.0F, 1.0F, 1.0F, alpha).tex(0, 0).endVertex();
		bufferBuilder.finishDrawing();
		WorldVertexBufferUploader.draw(bufferBuilder);
	}
}