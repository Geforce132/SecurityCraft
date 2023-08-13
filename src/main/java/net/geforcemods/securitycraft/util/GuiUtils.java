package net.geforcemods.securitycraft.util;

import java.util.Arrays;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
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
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GuiUtils {
	public static final ResourceLocation CAMERA_DASHBOARD = new ResourceLocation("securitycraft:textures/gui/camera/camera_dashboard.png");
	public static final ResourceLocation POTION_ICONS = new ResourceLocation("minecraft:textures/gui/container/inventory.png");
	private static final ItemStack REDSTONE = new ItemStack(Items.REDSTONE);
	//@formatter:off
	private static final ResourceLocation[] MODULE_TEXTURES = {
			new ResourceLocation(SecurityCraft.MODID, "textures/items/module_background.png"),
			new ResourceLocation(SecurityCraft.MODID, "textures/items/whitelist_module.png"),
			new ResourceLocation(SecurityCraft.MODID, "textures/items/blacklist_module.png"),
			new ResourceLocation(SecurityCraft.MODID, "textures/items/harming_module.png"),
			new ResourceLocation(SecurityCraft.MODID, "textures/items/smart_module.png"),
			new ResourceLocation(SecurityCraft.MODID, "textures/items/storage_module.png"),
			new ResourceLocation(SecurityCraft.MODID, "textures/items/disguise_module.png"),
			new ResourceLocation(SecurityCraft.MODID, "textures/items/module_background.png")
	};
	//@formatter:on
	private static final ResourceLocation REDSTONE_TEXTURE = new ResourceLocation("textures/items/redstone_dust.png");
	private static final ResourceLocation SUGAR_TEXTURE = new ResourceLocation("textures/items/sugar.png");

	private GuiUtils() {}

	public static void drawCameraOverlay(Minecraft mc, Gui gui, ScaledResolution resolution, EntityPlayer player, World world, BlockPos pos) {
		if (mc.gameSettings.showDebugInfo)
			return;

		TileEntity tile = world.getTileEntity(pos);

		if (!(tile instanceof SecurityCameraBlockEntity))
			return;

		FontRenderer font = Minecraft.getMinecraft().fontRenderer;
		SecurityCameraBlockEntity te = (SecurityCameraBlockEntity) tile;
		boolean hasRedstoneModule = te.isModuleEnabled(ModuleType.REDSTONE);
		GameSettings settings = Minecraft.getMinecraft().gameSettings;
		IBlockState state = world.getBlockState(pos);
		String lookAround = GameSettings.getKeyDisplayString(settings.keyBindForward.getKeyCode()) + GameSettings.getKeyDisplayString(settings.keyBindLeft.getKeyCode()) + GameSettings.getKeyDisplayString(settings.keyBindBack.getKeyCode()) + GameSettings.getKeyDisplayString(settings.keyBindRight.getKeyCode()) + " - " + Utils.localize("gui.securitycraft:camera.lookAround").getFormattedText();
		int timeY = 25;

		if (te.hasCustomName()) {
			String cameraName = te.getName();

			font.drawStringWithShadow(cameraName, resolution.getScaledWidth() - font.getStringWidth(cameraName) - 8, 25, 16777215);
			timeY += 10;
		}

		font.drawStringWithShadow(ClientUtils.getFormattedMinecraftTime(), resolution.getScaledWidth() - font.getStringWidth(ClientUtils.getFormattedMinecraftTime()) - 4, timeY, 16777215);
		font.drawStringWithShadow(lookAround, resolution.getScaledWidth() - font.getStringWidth(lookAround) - 8, resolution.getScaledHeight() - 80, 16777215);
		font.drawStringWithShadow(GameSettings.getKeyDisplayString(settings.keyBindSneak.getKeyCode()) + " - " + Utils.localize("gui.securitycraft:camera.exit").getFormattedText(), resolution.getScaledWidth() - font.getStringWidth(GameSettings.getKeyDisplayString(settings.keyBindSneak.getKeyCode()) + " - " + Utils.localize("gui.securitycraft:camera.exit").getFormattedText()) - 8, resolution.getScaledHeight() - 70, 16777215);
		font.drawStringWithShadow(GameSettings.getKeyDisplayString(KeyBindings.cameraZoomIn.getKeyCode()) + "/" + GameSettings.getKeyDisplayString(KeyBindings.cameraZoomOut.getKeyCode()) + " - " + Utils.localize("gui.securitycraft:camera.zoom").getFormattedText(), resolution.getScaledWidth() - font.getStringWidth(GameSettings.getKeyDisplayString(KeyBindings.cameraZoomIn.getKeyCode()) + "/" + GameSettings.getKeyDisplayString(KeyBindings.cameraZoomOut.getKeyCode()) + " - " + Utils.localize("gui.securitycraft:camera.zoom").getFormattedText()) - 8, resolution.getScaledHeight() - 60, 16777215);
		font.drawStringWithShadow(GameSettings.getKeyDisplayString(KeyBindings.cameraActivateNightVision.getKeyCode()) + " - " + Utils.localize("gui.securitycraft:camera.activateNightVision").getFormattedText(), resolution.getScaledWidth() - font.getStringWidth(GameSettings.getKeyDisplayString(KeyBindings.cameraActivateNightVision.getKeyCode()) + " - " + Utils.localize("gui.securitycraft:camera.activateNightVision").getFormattedText()) - 8, resolution.getScaledHeight() - 50, 16777215);
		font.drawStringWithShadow(GameSettings.getKeyDisplayString(KeyBindings.cameraEmitRedstone.getKeyCode()) + " - " + Utils.localize("gui.securitycraft:camera.toggleRedstone").getFormattedText(), resolution.getScaledWidth() - font.getStringWidth(GameSettings.getKeyDisplayString(KeyBindings.cameraEmitRedstone.getKeyCode()) + " - " + Utils.localize("gui.securitycraft:camera.toggleRedstone").getFormattedText()) - 8, resolution.getScaledHeight() - 40, hasRedstoneModule ? 16777215 : 16724855);
		font.drawStringWithShadow(Utils.localize("gui.securitycraft:camera.toggleRedstoneNote").getFormattedText(), resolution.getScaledWidth() - font.getStringWidth(Utils.localize("gui.securitycraft:camera.toggleRedstoneNote").getFormattedText()) - 8, resolution.getScaledHeight() - 30, hasRedstoneModule ? 16777215 : 16724855);

		mc.getTextureManager().bindTexture(CAMERA_DASHBOARD);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		gui.drawTexturedModalRect(5, 0, 0, 0, 90, 20);
		gui.drawTexturedModalRect(resolution.getScaledWidth() - 70, 5, 190, 0, 65, 30);

		if (player.getActivePotionEffect(Potion.getPotionFromResourceLocation("night_vision")) == null)
			gui.drawTexturedModalRect(28, 4, 90, 12, 16, 11);
		else {
			mc.getTextureManager().bindTexture(POTION_ICONS);
			gui.drawTexturedModalRect(25, 2, 70, 218, 19, 16);
			mc.getTextureManager().bindTexture(CAMERA_DASHBOARD);
		}

		if (state.getWeakPower(world, pos, state.getValue(SecurityCameraBlock.FACING)) == 0) {
			if (!hasRedstoneModule)
				gui.drawTexturedModalRect(12, 2, 104, 0, 12, 12);
			else
				gui.drawTexturedModalRect(12, 3, 90, 0, 12, 11);
		}
		else
			drawItemStackToGui(REDSTONE, 10, 0, false);
	}

	public static void drawItemStackToGui(ItemStack stack, int x, int y, boolean fixLighting) {
		Minecraft mc = Minecraft.getMinecraft();
		RenderItem renderItem = mc.getRenderItem();

		if (fixLighting)
			GlStateManager.enableLighting();

		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.enableRescaleNormal();
		renderItem.renderItemAndEffectIntoGUI(stack, x, y);
		renderItem.renderItemOverlays(mc.fontRenderer, stack, x, y);
		GlStateManager.disableLighting();
		GlStateManager.disableRescaleNormal();
	}

	public static void renderModuleInfo(ModuleType module, String moduleTooltip, boolean isModuleInstalled, int moduleLeft, int moduleTop, int screenWidth, int screenHeight, int mouseX, int mouseY) {
		Minecraft mc = Minecraft.getMinecraft();
		float alpha = isModuleInstalled ? 1.0F : 0.5F;
		int moduleRight = moduleLeft + 16;
		int moduleBottom = moduleTop + 16;

		GlStateManager.disableLighting();
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		mc.getTextureManager().bindTexture(MODULE_TEXTURES[module.ordinal()]);
		drawTexture(Tessellator.getInstance(), moduleLeft, moduleTop, moduleRight, moduleBottom, alpha);

		if (module == ModuleType.REDSTONE) {
			mc.getTextureManager().bindTexture(REDSTONE_TEXTURE);
			drawTexture(Tessellator.getInstance(), moduleLeft, moduleTop, moduleRight, moduleBottom, alpha);
		}
		else if (module == ModuleType.SPEED) {
			mc.getTextureManager().bindTexture(SUGAR_TEXTURE);
			drawTexture(Tessellator.getInstance(), moduleLeft, moduleTop, moduleRight, moduleBottom, alpha);
		}

		if (moduleTooltip != null && !moduleTooltip.isEmpty() && mouseX >= moduleLeft && mouseX < moduleRight && mouseY >= moduleTop && mouseY <= moduleBottom) {
			net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(Arrays.asList(moduleTooltip), mouseX, mouseY, screenWidth, screenHeight, -1, mc.fontRenderer);
			RenderHelper.disableStandardItemLighting();
		}

		GlStateManager.disableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.enableLighting();
	}

	private static void drawTexture(Tessellator tess, int moduleLeft, int moduleTop, int moduleRight, int moduleBottom, float alpha) {
		BufferBuilder bufferBuilder = tess.getBuffer();

		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
		bufferBuilder.pos(moduleLeft, moduleBottom, 0).tex(0, 1).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
		bufferBuilder.pos(moduleRight, moduleBottom, 0).tex(1, 1).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
		bufferBuilder.pos(moduleRight, moduleTop, 0).tex(1, 0).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
		bufferBuilder.pos(moduleLeft, moduleTop, 0).tex(0, 0).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
		tess.draw();
	}
}
