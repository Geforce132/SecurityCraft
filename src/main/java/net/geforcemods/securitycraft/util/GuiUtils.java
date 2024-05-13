package net.geforcemods.securitycraft.util;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.misc.CameraRedstoneModuleState;
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
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class GuiUtils {
	public static final ResourceLocation CAMERA_DASHBOARD = new ResourceLocation("securitycraft:textures/gui/camera/camera_dashboard.png");
	public static final ResourceLocation POTION_ICONS = new ResourceLocation("minecraft:textures/gui/container/inventory.png");
	public static final ItemStack REDSTONE = new ItemStack(Items.REDSTONE);
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
	private static final TextComponentTranslation REDSTONE_NOTE = Utils.localize("gui.securitycraft:camera.toggleRedstoneNote");
	private static final TextComponentTranslation SMART_MODULE_NOTE = Utils.localize("gui.securitycraft:camera.smartModuleNote");
	//@formatter:off
	private static final CameraKeyInfoEntry[] CAMERA_KEY_INFO_LIST = {
			new CameraKeyInfoEntry(() -> true, options -> Utils.localize("gui.securitycraft:camera.lookAround", options.keyBindForward.getDisplayName(), options.keyBindLeft.getDisplayName(), options.keyBindBack.getDisplayName(), options.keyBindRight.getDisplayName()), $ -> true),
			new CameraKeyInfoEntry(() -> true, options -> Utils.localize("gui.securitycraft:camera.exit", options.keyBindSneak.getDisplayName()), $ -> true),
			new CameraKeyInfoEntry(() -> true, $ -> Utils.localize("gui.securitycraft:camera.zoom", KeyBindings.cameraZoomIn.getDisplayName(), KeyBindings.cameraZoomOut.getDisplayName()), $ -> true),
			new CameraKeyInfoEntry(() -> ConfigHandler.allowCameraNightVision, $ -> Utils.localize("gui.securitycraft:camera.activateNightVision", KeyBindings.cameraActivateNightVision.getDisplayName()), $ -> true),
			new CameraKeyInfoEntry(() -> true, $ -> Utils.localize("gui.securitycraft:camera.toggleRedstone", KeyBindings.cameraEmitRedstone.getDisplayName()), be -> be.isModuleEnabled(ModuleType.REDSTONE)),
			new CameraKeyInfoEntry(() -> true, $ -> REDSTONE_NOTE, be -> be.isModuleEnabled(ModuleType.REDSTONE)),
			new CameraKeyInfoEntry(() -> true, $ -> Utils.localize("gui.securitycraft:camera.setDefaultViewingDirection", KeyBindings.setDefaultViewingDirection.getDisplayName()), be -> be.isModuleEnabled(ModuleType.SMART)),
			new CameraKeyInfoEntry(() -> true, $ -> SMART_MODULE_NOTE, be -> be.isModuleEnabled(ModuleType.SMART))
	};
	//@formatter:on

	private GuiUtils() {}

	public static void drawCameraOverlay(Minecraft mc, Gui gui, ScaledResolution resolution, EntityPlayer player, World world, BlockPos pos) {
		if (mc.gameSettings.showDebugInfo)
			return;

		TileEntity tile = world.getTileEntity(pos);

		if (!(tile instanceof SecurityCameraBlockEntity))
			return;

		int scaledWidth = resolution.getScaledWidth();
		int scaledHeight = resolution.getScaledHeight();
		FontRenderer font = Minecraft.getMinecraft().fontRenderer;
		SecurityCameraBlockEntity te = (SecurityCameraBlockEntity) tile;
		GameSettings settings = Minecraft.getMinecraft().gameSettings;
		IBlockState state = world.getBlockState(pos);
		long worldTime = Minecraft.getMinecraft().world.provider.getWorldTime();
		int hours24 = (int) ((float) worldTime / 1000L + 6L) % 24;
		int hours = hours24 % 12;
		int minutes = (int) (worldTime / 16.666666F % 60.0F);
		String time = String.format("%02d:%02d %s", Integer.valueOf(hours < 1 ? 12 : hours), Integer.valueOf(minutes), hours24 < 12 ? "AM" : "PM");
		int timeY = 25;

		if (te.hasCustomName()) {
			String cameraName = te.getName();

			font.drawStringWithShadow(cameraName, scaledWidth - font.getStringWidth(cameraName) - 8, 25, 16777215);
			timeY += 10;
		}

		font.drawStringWithShadow(time, scaledWidth - font.getStringWidth(time) - 4, timeY, 16777215);

		int heightOffset = 10;

		for (int i = CAMERA_KEY_INFO_LIST.length - 1; i >= 0; i--) {
			CameraKeyInfoEntry entry = CAMERA_KEY_INFO_LIST[i];

			if (entry.enabled().get()) {
				entry.drawString(settings, font, scaledWidth, scaledHeight, heightOffset, te);
				heightOffset += 10;
			}
		}

		mc.getTextureManager().bindTexture(CAMERA_DASHBOARD);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		gui.drawTexturedModalRect(5, 0, 0, 0, 90, 20);
		gui.drawTexturedModalRect(scaledWidth - 70, 5, 190, 0, 65, 30);

		if (player.getActivePotionEffect(Potion.getPotionFromResourceLocation("night_vision")) == null)
			gui.drawTexturedModalRect(28, 4, 90, 12, 16, 11);
		else {
			mc.getTextureManager().bindTexture(POTION_ICONS);
			gui.drawTexturedModalRect(25, 2, 70, 218, 19, 16);
		}

		if (state.getWeakPower(world, pos, state.getValue(SecurityCameraBlock.FACING)) == 0) {
			if (!te.isModuleEnabled(ModuleType.REDSTONE))
				CameraRedstoneModuleState.NOT_INSTALLED.render(gui, 12, 2);
			else
				CameraRedstoneModuleState.DEACTIVATED.render(gui, 12, 2);
		}
		else
			CameraRedstoneModuleState.ACTIVATED.render(gui, 12, 2);
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

	public static final class CameraKeyInfoEntry {
		private final Supplier<Boolean> enabled;
		private final Function<GameSettings, TextComponentTranslation> text;
		private final Predicate<SecurityCameraBlockEntity> whiteText;

		public CameraKeyInfoEntry(Supplier<Boolean> enabled, Function<GameSettings, TextComponentTranslation> text, Predicate<SecurityCameraBlockEntity> whiteText) {
			this.enabled = enabled;
			this.text = text;
			this.whiteText = whiteText;
		}

		public void drawString(GameSettings options, FontRenderer font, int scaledWidth, int scaledHeight, int heightOffset, SecurityCameraBlockEntity be) {
			String formattedText = text().apply(options).getFormattedText();
			boolean shouldTextBeWhite = whiteText().test(be);

			font.drawStringWithShadow(formattedText, scaledWidth - font.getStringWidth(formattedText) - 8, scaledHeight - heightOffset, shouldTextBeWhite ? 0xFFFFFF : 0xFF3377);
		}

		public Supplier<Boolean> enabled() {
			return enabled;
		}

		public Function<GameSettings, TextComponentTranslation> text() {
			return text;
		}

		public Predicate<SecurityCameraBlockEntity> whiteText() {
			return whiteText;
		}
	}
}
