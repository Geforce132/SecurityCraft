package net.geforcemods.securitycraft.util;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.minecraft.block.Block;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

public class GuiUtils{

	public static ResourceLocation cameraDashboard = new ResourceLocation("securitycraft:textures/gui/camera/camera_dashboard.png");
	public static ResourceLocation potionIcons = new ResourceLocation("minecraft:textures/gui/container/inventory.png");
	private static final ResourceLocation nightVis = new ResourceLocation("minecraft:night_vision");
	private static ItemRenderer itemRender = Minecraft.getInstance().getItemRenderer();

	public static void drawCameraOverlay(Minecraft mc, Gui gui, MainWindow resolution, EntityPlayer player, World world, BlockPos pos) {
		Minecraft.getInstance().fontRenderer.drawStringWithShadow(ClientUtils.getFormattedMinecraftTime(), resolution.getScaledWidth() / 2 - Minecraft.getInstance().fontRenderer.getStringWidth(ClientUtils.getFormattedMinecraftTime()) / 2, 8, 16777215);
		Minecraft.getInstance().fontRenderer.drawStringWithShadow(Minecraft.getInstance().gameSettings.keyBindSneak.getKey().getName() + " - " + ClientUtils.localize("gui.securitycraft:camera.exit"), resolution.getScaledWidth() - 98 - Minecraft.getInstance().fontRenderer.getStringWidth(Minecraft.getInstance().gameSettings.keyBindSneak.getKey().getName() + " - " + ClientUtils.localize("gui.securitycraft:camera.exit")) / 2, resolution.getScaledHeight() - 70, 16777215);
		Minecraft.getInstance().fontRenderer.drawStringWithShadow(KeyBindings.cameraZoomIn.getKey().getName() + "/" + KeyBindings.cameraZoomOut.getKey().getName() + " - " + ClientUtils.localize("gui.securitycraft:camera.zoom"), resolution.getScaledWidth() - 80 - Minecraft.getInstance().fontRenderer.getStringWidth(KeyBindings.cameraZoomIn.getKey().getName() + "/" + KeyBindings.cameraZoomOut.getKey().getName() + " - " + ClientUtils.localize("gui.securitycraft:camera.zoom")) / 2, resolution.getScaledHeight() - 60, 16777215);
		Minecraft.getInstance().fontRenderer.drawStringWithShadow(KeyBindings.cameraActivateNightVision.getKey().getName() + " - " + ClientUtils.localize("gui.securitycraft:camera.activateNightVision"), resolution.getScaledWidth() - 91 - Minecraft.getInstance().fontRenderer.getStringWidth(KeyBindings.cameraActivateNightVision.getKey().getName() + " - " + ClientUtils.localize("gui.securitycraft:camera.activateNightVision")) / 2, resolution.getScaledHeight() - 50, 16777215);
		Minecraft.getInstance().fontRenderer.drawStringWithShadow(KeyBindings.cameraEmitRedstone.getKey().getName() + " - " + ClientUtils.localize("gui.securitycraft:camera.toggleRedstone"), resolution.getScaledWidth() - 82 - Minecraft.getInstance().fontRenderer.getStringWidth(KeyBindings.cameraEmitRedstone.getKey().getName() + " - " + ClientUtils.localize("gui.securitycraft:camera.toggleRedstone")) / 2, resolution.getScaledHeight() - 40, ((CustomizableSCTE) world.getTileEntity(pos)).hasModule(EnumCustomModules.REDSTONE) ? 16777215 : 16724855);
		Minecraft.getInstance().fontRenderer.drawStringWithShadow(ClientUtils.localize("gui.securitycraft:camera.toggleRedstoneNote"), resolution.getScaledWidth() - 82 - Minecraft.getInstance().fontRenderer.getStringWidth(ClientUtils.localize("gui.securitycraft:camera.toggleRedstoneNote")) / 2, resolution.getScaledHeight() - 30, ((CustomizableSCTE) world.getTileEntity(pos)).hasModule(EnumCustomModules.REDSTONE) ? 16777215 : 16724855);

		mc.getTextureManager().bindTexture(cameraDashboard);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		gui.drawTexturedModalRect(5, 0, 0, 0, 90, 20);
		gui.drawTexturedModalRect(resolution.getScaledWidth() - 55, 5, 205, 0, 50, 30);

		if(player.getActivePotionEffect(ForgeRegistries.POTIONS.getValue(nightVis)) == null)
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

	public static void drawItemStackToGui(Minecraft mc, Item item, int x, int y, boolean fixLighting){
		if(fixLighting)
			GlStateManager.enableLighting();

		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.enableRescaleNormal();
		itemRender.renderItemAndEffectIntoGUI(new ItemStack(item), x, y);

		GlStateManager.disableLighting();
		GlStateManager.disableRescaleNormal();
	}

	public static void drawItemStackToGui(Minecraft mc, Block block, int x, int y, boolean fixLighting){
		drawItemStackToGui(mc, block.asItem(), x, y, fixLighting);
	}
}
