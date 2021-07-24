package net.geforcemods.securitycraft.util;

import java.util.Arrays;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Matrix4f;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.UpdateNBTTagOnServer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fmlclient.gui.GuiUtils;

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

	/**
	 * Returns the current Minecraft in-game time, in a 12-hour AM/PM format.
	 */
	public static String getFormattedMinecraftTime(){
		Long time = Minecraft.getInstance().level.getDayTime();

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

	public static void renderModuleInfo(PoseStack matrix, ModuleType module, Component moduleTooltip, Component noModuleTooltip, boolean isModuleInstalled, int moduleLeft, int moduleTop, int screenWidth, int screenHeight, int mouseX, int mouseY)
	{
		Minecraft mc = Minecraft.getInstance();
		float alpha = isModuleInstalled ? 1.0F : 0.5F;
		int moduleRight = moduleLeft + 16;
		int moduleBottom = moduleTop + 16;
		Matrix4f m4f = matrix.last().pose();
		BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		mc.getTextureManager().bindForSetup(MODULE_TEXTURES[module.ordinal()]);
		drawTexture(bufferBuilder, m4f, moduleLeft, moduleTop, moduleRight, moduleBottom, alpha);

		if(module == ModuleType.REDSTONE)
		{
			mc.getTextureManager().bindForSetup(REDSTONE_TEXTURE);
			drawTexture(bufferBuilder, m4f, moduleLeft, moduleTop, moduleRight, moduleBottom, alpha);
		}
		else if(module == ModuleType.SPEED)
		{
			mc.getTextureManager().bindForSetup(SUGAR_TEXTURE);
			drawTexture(bufferBuilder, m4f, moduleLeft, moduleTop, moduleRight, moduleBottom, alpha);
		}

		RenderSystem.disableBlend();

		if(mouseX >= moduleLeft && mouseX < moduleRight && mouseY >= moduleTop && mouseY <= moduleBottom)
		{
			Component text = isModuleInstalled ? moduleTooltip : noModuleTooltip;

			if(text != null)
				GuiUtils.drawHoveringText(matrix, Arrays.asList(text), mouseX, mouseY, screenWidth, screenHeight, -1, mc.font);
		}
	}

	private static void drawTexture(BufferBuilder bufferBuilder, Matrix4f m4f, int moduleLeft, int moduleTop, int moduleRight, int moduleBottom, float alpha)
	{
		bufferBuilder.begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
		bufferBuilder.vertex(m4f, moduleLeft, moduleBottom, 0).color(1.0F, 1.0F, 1.0F, alpha).uv(0, 1).endVertex();
		bufferBuilder.vertex(m4f, moduleRight, moduleBottom, 0).color(1.0F, 1.0F, 1.0F, alpha).uv(1, 1).endVertex();
		bufferBuilder.vertex(m4f, moduleRight, moduleTop, 0).color(1.0F, 1.0F, 1.0F, alpha).uv(1, 0).endVertex();
		bufferBuilder.vertex(m4f, moduleLeft, moduleTop, 0).color(1.0F, 1.0F, 1.0F, alpha).uv(0, 0).endVertex();
		bufferBuilder.end();
		BufferUploader.end(bufferBuilder);
	}
}