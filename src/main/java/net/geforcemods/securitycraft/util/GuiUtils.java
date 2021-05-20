package net.geforcemods.securitycraft.util;

import java.util.Arrays;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GuiUtils{

	public static ResourceLocation cameraDashboard = new ResourceLocation("securitycraft:textures/gui/camera/camera_dashboard.png");
	public static ResourceLocation potionIcons = new ResourceLocation("minecraft:textures/gui/container/inventory.png");
	private static final ResourceLocation SMART_MODULE_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/items/smart_module.png");
	private static RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();

	public static void drawCameraOverlay(Minecraft mc, Gui gui, ScaledResolution resolution, EntityPlayer player, World world, BlockPos pos) {
		TileEntitySecurityCamera te = (TileEntitySecurityCamera)world.getTileEntity(pos);
		int timeY = 25;
		GameSettings settings = Minecraft.getMinecraft().gameSettings;
		String lookAround = GameSettings.getKeyDisplayString(settings.keyBindForward.getKeyCode()) + GameSettings.getKeyDisplayString(settings.keyBindLeft.getKeyCode()) + GameSettings.getKeyDisplayString(settings.keyBindBack.getKeyCode()) + GameSettings.getKeyDisplayString(settings.keyBindRight.getKeyCode()) + " - " + Utils.localize("gui.securitycraft:camera.lookAround").getFormattedText();

		if(te.hasCustomName())
		{
			String cameraName = te.getCustomName();

			Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(cameraName, resolution.getScaledWidth() - Minecraft.getMinecraft().fontRenderer.getStringWidth(cameraName) - 8, 25, 16777215);
			timeY += 10;
		}

		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(ClientUtils.getFormattedMinecraftTime(), resolution.getScaledWidth() - Minecraft.getMinecraft().fontRenderer.getStringWidth(ClientUtils.getFormattedMinecraftTime()) - 8, timeY, 16777215);
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(lookAround, resolution.getScaledWidth() - Minecraft.getMinecraft().fontRenderer.getStringWidth(lookAround) - 8, resolution.getScaledHeight() - 80, 16777215);
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(GameSettings.getKeyDisplayString(settings.keyBindSneak.getKeyCode()) + " - " + Utils.localize("gui.securitycraft:camera.exit").getFormattedText(), resolution.getScaledWidth() - Minecraft.getMinecraft().fontRenderer.getStringWidth(GameSettings.getKeyDisplayString(settings.keyBindSneak.getKeyCode()) + " - " + Utils.localize("gui.securitycraft:camera.exit").getFormattedText()) - 8, resolution.getScaledHeight() - 70, 16777215);
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(GameSettings.getKeyDisplayString(KeyBindings.cameraZoomIn.getKeyCode()) + "/" + GameSettings.getKeyDisplayString(KeyBindings.cameraZoomOut.getKeyCode()) + " - " + Utils.localize("gui.securitycraft:camera.zoom").getFormattedText(), resolution.getScaledWidth() - Minecraft.getMinecraft().fontRenderer.getStringWidth(GameSettings.getKeyDisplayString(KeyBindings.cameraZoomIn.getKeyCode()) + "/" + GameSettings.getKeyDisplayString(KeyBindings.cameraZoomOut.getKeyCode()) + " - " + Utils.localize("gui.securitycraft:camera.zoom").getFormattedText()) - 8, resolution.getScaledHeight() - 60, 16777215);
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(GameSettings.getKeyDisplayString(KeyBindings.cameraActivateNightVision.getKeyCode()) + " - " + Utils.localize("gui.securitycraft:camera.activateNightVision").getFormattedText(), resolution.getScaledWidth() - Minecraft.getMinecraft().fontRenderer.getStringWidth(GameSettings.getKeyDisplayString(KeyBindings.cameraActivateNightVision.getKeyCode()) + " - " + Utils.localize("gui.securitycraft:camera.activateNightVision").getFormattedText()) - 8, resolution.getScaledHeight() - 50, 16777215);
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(GameSettings.getKeyDisplayString(KeyBindings.cameraEmitRedstone.getKeyCode()) + " - " + Utils.localize("gui.securitycraft:camera.toggleRedstone").getFormattedText(), resolution.getScaledWidth() - Minecraft.getMinecraft().fontRenderer.getStringWidth(GameSettings.getKeyDisplayString(KeyBindings.cameraEmitRedstone.getKeyCode()) + " - " + Utils.localize("gui.securitycraft:camera.toggleRedstone").getFormattedText()) - 8, resolution.getScaledHeight() - 40, te.hasModule(EnumModuleType.REDSTONE) ? 16777215 : 16724855);
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(Utils.localize("gui.securitycraft:camera.toggleRedstoneNote").getFormattedText(), resolution.getScaledWidth() - Minecraft.getMinecraft().fontRenderer.getStringWidth(Utils.localize("gui.securitycraft:camera.toggleRedstoneNote").getFormattedText()) - 8, resolution.getScaledHeight() - 30, te.hasModule(EnumModuleType.REDSTONE) ? 16777215 : 16724855);

		mc.getTextureManager().bindTexture(cameraDashboard);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		gui.drawTexturedModalRect(5, 0, 0, 0, 90, 20);
		gui.drawTexturedModalRect(resolution.getScaledWidth() - 55, 5, 205, 0, 50, 30);

		if(player.getActivePotionEffect(Potion.getPotionFromResourceLocation("night_vision")) == null)
			gui.drawTexturedModalRect(28, 4, 90, 12, 16, 11);
		else{
			mc.getTextureManager().bindTexture(potionIcons);
			gui.drawTexturedModalRect(25, 2, 70, 218, 19, 16);
			mc.getTextureManager().bindTexture(cameraDashboard);
		}

		if((world.getBlockState(pos).getWeakPower(world, pos, BlockUtils.getBlockProperty(world, pos, BlockSecurityCamera.FACING)) == 0) && !te.hasModule(EnumModuleType.REDSTONE))
			gui.drawTexturedModalRect(12, 2, 104, 0, 12, 12);
		else if((world.getBlockState(pos).getWeakPower(world, pos, BlockUtils.getBlockProperty(world, pos, BlockSecurityCamera.FACING)) == 0) && te.hasModule(EnumModuleType.REDSTONE))
			gui.drawTexturedModalRect(12, 3, 90, 0, 12, 11);
		else
			drawItemToGui(Items.REDSTONE, 10, 0, false);
	}

	public static void drawItemStackToGui(ItemStack stack, int x, int y, boolean fixLighting)
	{
		if(fixLighting)
			GlStateManager.enableLighting();

		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.enableRescaleNormal();
		itemRender.renderItemAndEffectIntoGUI(stack, x, y);
		itemRender.renderItemOverlays(Minecraft.getMinecraft().fontRenderer, stack, x, y);

		GlStateManager.disableLighting();
		GlStateManager.disableRescaleNormal();
	}

	public static void drawItemToGui(Item item, int itemDamage, int x, int y, boolean fixLighting){
		drawItemStackToGui(new ItemStack(item, 1, itemDamage), x, y, fixLighting);
	}

	public static void drawItemToGui(Item item, int x, int y, boolean fixLighting){
		drawItemToGui(item, 0, x, y, fixLighting);
	}

	public static void renderSmartModuleInfo(String moduleTooltip, String noModuleTooltip, boolean isSmart, int guiLeft, int guiTop, int screenWidth, int screenHeight, int mouseX, int mouseY)
	{
		Minecraft mc = Minecraft.getMinecraft();
		float alpha = isSmart ? 1.0F : 0.5F;
		int moduleLeft = guiLeft + 5;
		int moduleRight = moduleLeft + 16;
		int moduleTop = guiTop + 5;
		int moduleBottom = moduleTop + 16;
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tess.getBuffer();

		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		mc.getTextureManager().bindTexture(SMART_MODULE_TEXTURE);
		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
		bufferBuilder.pos(moduleLeft, moduleBottom, 0).tex(0, 1).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
		bufferBuilder.pos(moduleRight, moduleBottom, 0).tex(1, 1).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
		bufferBuilder.pos(moduleRight, moduleTop, 0).tex(1, 0).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
		bufferBuilder.pos(moduleLeft, moduleTop, 0).tex(0, 0).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
		tess.draw();
		GlStateManager.disableBlend();
		GlStateManager.disableAlpha();

		if(mouseX >= moduleLeft && mouseX < moduleRight && mouseY >= moduleTop && mouseY <= moduleBottom)
		{
			String text = isSmart ? moduleTooltip : noModuleTooltip;

			if(text != null && !text.isEmpty())
				net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(Arrays.asList(text), mouseX, mouseY, screenWidth, screenHeight, -1, mc.fontRenderer);
		}
	}
}
