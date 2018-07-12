package net.geforcemods.securitycraft.util;

import java.util.Iterator;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
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

	private static RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();

	public static void drawCameraOverlay(Minecraft mc, Gui gui, ScaledResolution resolution, EntityPlayer player, World world, BlockPos pos) {
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(ClientUtils.getFormattedMinecraftTime(), resolution.getScaledWidth() / 2 - Minecraft.getMinecraft().fontRenderer.getStringWidth(ClientUtils.getFormattedMinecraftTime()) / 2, 8, 16777215);
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(GameSettings.getKeyDisplayString(Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode()) + " - " + ClientUtils.localize("gui.securitycraft:camera.exit"), resolution.getScaledWidth() - 98 - Minecraft.getMinecraft().fontRenderer.getStringWidth(GameSettings.getKeyDisplayString(Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode()) + " - " + ClientUtils.localize("gui.securitycraft:camera.exit")) / 2, resolution.getScaledHeight() - 70, 16777215);
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(GameSettings.getKeyDisplayString(KeyBindings.cameraZoomIn.getKeyCode()) + "/" + GameSettings.getKeyDisplayString(KeyBindings.cameraZoomOut.getKeyCode()) + " - " + ClientUtils.localize("gui.securitycraft:camera.zoom"), resolution.getScaledWidth() - 80 - Minecraft.getMinecraft().fontRenderer.getStringWidth(GameSettings.getKeyDisplayString(KeyBindings.cameraZoomIn.getKeyCode()) + "/" + GameSettings.getKeyDisplayString(KeyBindings.cameraZoomOut.getKeyCode()) + " - " + ClientUtils.localize("gui.securitycraft:camera.zoom")) / 2, resolution.getScaledHeight() - 60, 16777215);
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(GameSettings.getKeyDisplayString(KeyBindings.cameraActivateNightVision.getKeyCode()) + " - " + ClientUtils.localize("gui.securitycraft:camera.activateNightVision"), resolution.getScaledWidth() - 91 - Minecraft.getMinecraft().fontRenderer.getStringWidth(GameSettings.getKeyDisplayString(KeyBindings.cameraActivateNightVision.getKeyCode()) + " - " + ClientUtils.localize("gui.securitycraft:camera.activateNightVision")) / 2, resolution.getScaledHeight() - 50, 16777215);
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(GameSettings.getKeyDisplayString(KeyBindings.cameraEmitRedstone.getKeyCode()) + " - " + ClientUtils.localize("gui.securitycraft:camera.toggleRedstone"), resolution.getScaledWidth() - 82 - Minecraft.getMinecraft().fontRenderer.getStringWidth(GameSettings.getKeyDisplayString(KeyBindings.cameraEmitRedstone.getKeyCode()) + " - " + ClientUtils.localize("gui.securitycraft:camera.toggleRedstone")) / 2, resolution.getScaledHeight() - 40, ((CustomizableSCTE) world.getTileEntity(pos)).hasModule(EnumCustomModules.REDSTONE) ? 16777215 : 16724855);
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(ClientUtils.localize("gui.securitycraft:camera.toggleRedstoneNote"), resolution.getScaledWidth() - 82 - Minecraft.getMinecraft().fontRenderer.getStringWidth(ClientUtils.localize("gui.securitycraft:camera.toggleRedstoneNote")) / 2, resolution.getScaledHeight() - 30, ((CustomizableSCTE) world.getTileEntity(pos)).hasModule(EnumCustomModules.REDSTONE) ? 16777215 : 16724855);

		mc.getTextureManager().bindTexture(cameraDashboard);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		gui.drawTexturedModalRect(5, 0, 0, 0, 90, 20);
		gui.drawTexturedModalRect(resolution.getScaledWidth() - 55, 5, 205, 0, 50, 30);

		if(player.getActivePotionEffect(Potion.getPotionFromResourceLocation("night_vision")) == null)
			gui.drawTexturedModalRect(28, 4, 90, 12, 16, 11);
		else{
			mc.getTextureManager().bindTexture(potionIcons);
			gui.drawTexturedModalRect(25, 2, 70, 218, 19, 16);
		}

		if((world.getBlockState(pos).getWeakPower(world, pos, BlockUtils.getBlockPropertyAsEnum(world, pos, BlockSecurityCamera.FACING)) == 0) && (!((CustomizableSCTE) world.getTileEntity(pos)).hasModule(EnumCustomModules.REDSTONE)))
			gui.drawTexturedModalRect(12, 2, 104, 0, 12, 12);
		else if((world.getBlockState(pos).getWeakPower(world, pos, BlockUtils.getBlockPropertyAsEnum(world, pos, BlockSecurityCamera.FACING)) == 0) && (((CustomizableSCTE) world.getTileEntity(pos)).hasModule(EnumCustomModules.REDSTONE)))
			gui.drawTexturedModalRect(12, 3, 90, 0, 12, 11);
		else
			drawItemStackToGui(mc, Items.REDSTONE, 10, 0, false);
	}

	public static void drawTooltip(List<?> list, int x, int y, FontRenderer fontRenderer){
		if (!list.isEmpty()){
			GlStateManager.disableRescaleNormal();
			RenderHelper.disableStandardItemLighting();
			GlStateManager.disableLighting();
			GlStateManager.disableDepth();
			int k = 0;
			Iterator<?> iterator = list.iterator();

			while (iterator.hasNext())
			{
				String s = (String)iterator.next();
				int l = fontRenderer.getStringWidth(s);

				if (l > k)
					k = l;
			}

			int j2 = x + 12;
			int k2 = y - 12;
			int i1 = 8;

			if (list.size() > 1)
				i1 += 2 + (list.size() - 1) * 10;

			if (j2 + k > Minecraft.getMinecraft().displayWidth)
				j2 -= 28 + k;

			if (k2 + i1 + 6 > Minecraft.getMinecraft().displayHeight)
				k2 = Minecraft.getMinecraft().displayHeight - i1 - 6; //h

			itemRender.zLevel = 300.0F;
			int j1 = -267386864;
			drawGradientRect(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j1, j1, 300.0F);
			drawGradientRect(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j1, j1, 300.0F);
			drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j1, j1, 300.0F);
			drawGradientRect(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j1, j1, 300.0F);
			drawGradientRect(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j1, j1, 300.0F);
			int k1 = 1347420415;
			int l1 = (k1 & 16711422) >> 1 | k1 & -16777216;
			drawGradientRect(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1, 300.0F);
			drawGradientRect(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1, 300.0F);
			drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1, 300.0F);
			drawGradientRect(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1, 300.0F);

			for (int i2 = 0; i2 < list.size(); ++i2)
			{
				String s1 = (String)list.get(i2);
				fontRenderer.drawStringWithShadow(s1, j2, k2, -1);

				if (i2 == 0)
					k2 += 2;

				k2 += 10;
			}

			itemRender.zLevel = 0.0F;
			//GlStateManager.glEnable(GlStateManager.GL_LIGHTING);
			GlStateManager.enableDepth();
			//RenderHelper.enableStandardItemLighting();
			GlStateManager.enableRescaleNormal();
		}
	}

	public static void drawItemStackToGui(Minecraft mc, Item item, int itemDamage, int x, int y, boolean fixLighting){
		if(fixLighting)
			GlStateManager.enableLighting();

		GlStateManager.enableRescaleNormal();
		itemRender.renderItemAndEffectIntoGUI(new ItemStack(item, 1, itemDamage), x, y);

		GlStateManager.disableLighting();
		GlStateManager.disableRescaleNormal();
	}

	public static void drawItemStackToGui(Minecraft mc, Block block, int x, int y, boolean fixLighting){
		if(fixLighting)
			GlStateManager.enableLighting();

		GlStateManager.enableRescaleNormal();
		itemRender.renderItemAndEffectIntoGUI(new ItemStack(Item.getItemFromBlock(block), 1, 0), x, y);

		GlStateManager.disableLighting();
		GlStateManager.disableRescaleNormal();
	}

	public static void drawItemStackToGui(Minecraft mc, Item item, int x, int y, boolean fixLighting){
		drawItemStackToGui(mc, item, 0, x, y, fixLighting);
	}

	private static void drawGradientRect(int p_73733_1_, int p_73733_2_, int p_73733_3_, int p_73733_4_, int p_73733_5_, int p_73733_6_, float zLevel){
		float f = (p_73733_5_ >> 24 & 255) / 255.0F;
		float f1 = (p_73733_5_ >> 16 & 255) / 255.0F;
		float f2 = (p_73733_5_ >> 8 & 255) / 255.0F;
		float f3 = (p_73733_5_ & 255) / 255.0F;
		float f4 = (p_73733_6_ >> 24 & 255) / 255.0F;
		float f5 = (p_73733_6_ >> 16 & 255) / 255.0F;
		float f6 = (p_73733_6_ >> 8 & 255) / 255.0F;
		float f7 = (p_73733_6_ & 255) / 255.0F;
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexBuffer = tessellator.getBuffer();
		vertexBuffer.putColorRGBA(0, (int)f1, (int)f2, (int)f3, (int)f);
		vertexBuffer.putPosition(p_73733_3_, p_73733_2_, zLevel);
		vertexBuffer.putPosition(p_73733_1_, p_73733_2_, zLevel);
		vertexBuffer.putColorRGBA(0, (int)f5, (int)f6, (int)f7, (int)f4);
		vertexBuffer.putPosition(p_73733_1_, p_73733_4_, zLevel);
		vertexBuffer.putPosition(p_73733_3_, p_73733_4_, zLevel);
		tessellator.draw();
		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
	}
}
