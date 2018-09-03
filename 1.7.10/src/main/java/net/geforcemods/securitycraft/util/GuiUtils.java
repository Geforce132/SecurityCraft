package net.geforcemods.securitycraft.util;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class GuiUtils{

	public static ResourceLocation cameraDashboard = new ResourceLocation("securitycraft:textures/gui/camera/cameraDashboard.png");
	public static ResourceLocation potionIcons = new ResourceLocation("minecraft:textures/gui/container/inventory.png");

	private static RenderItem itemRender = new RenderItem();

	public static void drawCameraOverlay(Minecraft mc, Gui gui, ScaledResolution resolution, EntityPlayer player, World world, int x, int y, int z, int mouseX, int mouseY) {
		Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(ClientUtils.getFormattedMinecraftTime(), resolution.getScaledWidth() / 2 - Minecraft.getMinecraft().fontRendererObj.getStringWidth(ClientUtils.getFormattedMinecraftTime()) / 2, 8, 16777215);
		Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(GameSettings.getKeyDisplayString(Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode()) + " - " + StatCollector.translateToLocal("gui.securitycraft:camera.exit"), resolution.getScaledWidth() - 98 - Minecraft.getMinecraft().fontRendererObj.getStringWidth(GameSettings.getKeyDisplayString(Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode()) + " - " + StatCollector.translateToLocal("gui.securitycraft:camera.exit")) / 2, resolution.getScaledHeight() - 70, 16777215);
		Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(GameSettings.getKeyDisplayString(KeyBindings.cameraZoomIn.getKeyCode()) + "/" + GameSettings.getKeyDisplayString(KeyBindings.cameraZoomOut.getKeyCode()) + " - " + StatCollector.translateToLocal("gui.securitycraft:camera.zoom"), resolution.getScaledWidth() - 80 - Minecraft.getMinecraft().fontRendererObj.getStringWidth(GameSettings.getKeyDisplayString(KeyBindings.cameraZoomIn.getKeyCode()) + "/" + GameSettings.getKeyDisplayString(KeyBindings.cameraZoomOut.getKeyCode()) + " - " + StatCollector.translateToLocal("gui.securitycraft:camera.zoom")) / 2, resolution.getScaledHeight() - 60, 16777215);
		Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(GameSettings.getKeyDisplayString(KeyBindings.cameraActivateNightVision.getKeyCode()) + " - " + StatCollector.translateToLocal("gui.securitycraft:camera.activateNightVision"), resolution.getScaledWidth() - 91 - Minecraft.getMinecraft().fontRendererObj.getStringWidth(GameSettings.getKeyDisplayString(KeyBindings.cameraActivateNightVision.getKeyCode()) + " - " + StatCollector.translateToLocal("gui.securitycraft:camera.activateNightVision")) / 2, resolution.getScaledHeight() - 50, 16777215);
		Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(GameSettings.getKeyDisplayString(KeyBindings.cameraEmitRedstone.getKeyCode()) + " - " + StatCollector.translateToLocal("gui.securitycraft:camera.toggleRedstone"), resolution.getScaledWidth() - 83 - Minecraft.getMinecraft().fontRendererObj.getStringWidth(GameSettings.getKeyDisplayString(KeyBindings.cameraEmitRedstone.getKeyCode()) + " - " + StatCollector.translateToLocal("gui.securitycraft:camera.toggleRedstone")) / 2, resolution.getScaledHeight() - 40, ((CustomizableSCTE)world.getTileEntity(x, y, z)).hasModule(EnumCustomModules.REDSTONE) ? 16777215 : 16724855);
		Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(StatCollector.translateToLocal("gui.securitycraft:camera.toggleRedstoneNote"), resolution.getScaledWidth() - 83 - Minecraft.getMinecraft().fontRendererObj.getStringWidth(StatCollector.translateToLocal("gui.securitycraft:camera.toggleRedstoneNote")) / 2, resolution.getScaledHeight() - 30, ((CustomizableSCTE)world.getTileEntity(x, y, z)).hasModule(EnumCustomModules.REDSTONE) ? 16777215 : 16724855);

		mc.getTextureManager().bindTexture(cameraDashboard);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		gui.drawTexturedModalRect(5, 0, 0, 0, 90, 20);
		gui.drawTexturedModalRect(resolution.getScaledWidth() - 55, 5, 205, 0, 50, 30);

		if(player.getActivePotionEffect(Potion.nightVision) == null)
			gui.drawTexturedModalRect(28, 4, 90, 12, 16, 11);
		else{
			mc.getTextureManager().bindTexture(potionIcons);
			gui.drawTexturedModalRect(25, 2, 70, 218, 19, 16);
		}

		if(world.getBlock(x, y, z).isProvidingWeakPower(world, x, y, z, 0) == 0 && !((CustomizableSCTE) world.getTileEntity(x, y, z)).hasModule(EnumCustomModules.REDSTONE))
			gui.drawTexturedModalRect(12, 2, 104, 0, 12, 12);
		else if(world.getBlock(x, y, z).isProvidingWeakPower(world, x, y, z, 0) == 0 && ((CustomizableSCTE)world.getTileEntity(x, y, z)).hasModule(EnumCustomModules.REDSTONE))
			gui.drawTexturedModalRect(12, 3, 90, 0, 12, 11);
		else
			drawItemStackToGui(mc, Items.redstone, 10, 0, false);
	}

	public static void drawItemStackToGui(Minecraft mc, Item item, int itemDamage, int x, int y, boolean fixLighting){
		if(fixLighting)
			GL11.glEnable(GL11.GL_LIGHTING);

		RenderHelper.enableGUIStandardItemLighting();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		itemRender.renderItemAndEffectIntoGUI(mc.fontRendererObj, mc.getTextureManager(), new ItemStack(item, 1, itemDamage), x, y);

		GL11.glDisable(GL11.GL_LIGHTING);

		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
	}

	public static void drawItemStackToGui(Minecraft mc, Block block, int x, int y, boolean fixLighting){
		drawItemStackToGui(mc, Item.getItemFromBlock(block), 0, x, y, fixLighting);
	}

	public static void drawItemStackToGui(Minecraft mc, Item item, int x, int y, boolean fixLighting){
		drawItemStackToGui(mc, item, 0, x, y, fixLighting);
	}
}
