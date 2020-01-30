package net.geforcemods.securitycraft.util;

import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.misc.CustomModules;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

public class GuiUtils{

	public static ResourceLocation cameraDashboard = new ResourceLocation("securitycraft:textures/gui/camera/camera_dashboard.png");
	public static ResourceLocation potionIcons = new ResourceLocation("minecraft:textures/gui/container/inventory.png");
	private static final ResourceLocation nightVis = new ResourceLocation("minecraft:night_vision");
	private static ItemRenderer itemRender = Minecraft.getInstance().getItemRenderer();

	public static void drawCameraOverlay(Minecraft mc, AbstractGui gui, MainWindow resolution, PlayerEntity player, World world, BlockPos pos) {
		Minecraft.getInstance().fontRenderer.drawStringWithShadow(ClientUtils.getFormattedMinecraftTime(), resolution.getScaledWidth() / 2 - Minecraft.getInstance().fontRenderer.getStringWidth(ClientUtils.getFormattedMinecraftTime()) / 2, 8, 16777215);
		Minecraft.getInstance().fontRenderer.drawStringWithShadow(Minecraft.getInstance().gameSettings.keyBindSneak.getLocalizedName() + " - " + ClientUtils.localize("gui.securitycraft:camera.exit"), resolution.getScaledWidth() - 98 - Minecraft.getInstance().fontRenderer.getStringWidth(Minecraft.getInstance().gameSettings.keyBindSneak.getLocalizedName() + " - " + ClientUtils.localize("gui.securitycraft:camera.exit")) / 2, resolution.getScaledHeight() - 70, 16777215);
		Minecraft.getInstance().fontRenderer.drawStringWithShadow(KeyBindings.cameraZoomIn.getLocalizedName() + "/" + KeyBindings.cameraZoomOut.getLocalizedName() + " - " + ClientUtils.localize("gui.securitycraft:camera.zoom"), resolution.getScaledWidth() - 80 - Minecraft.getInstance().fontRenderer.getStringWidth(KeyBindings.cameraZoomIn.getLocalizedName() + "/" + ClientUtils.localize(KeyBindings.cameraZoomOut.getTranslationKey()) + " - " + ClientUtils.localize("gui.securitycraft:camera.zoom")) / 2, resolution.getScaledHeight() - 60, 16777215);
		Minecraft.getInstance().fontRenderer.drawStringWithShadow(KeyBindings.cameraActivateNightVision.getLocalizedName() + " - " + ClientUtils.localize("gui.securitycraft:camera.activateNightVision"), resolution.getScaledWidth() - 91 - Minecraft.getInstance().fontRenderer.getStringWidth(KeyBindings.cameraActivateNightVision.getLocalizedName() + " - " + ClientUtils.localize("gui.securitycraft:camera.activateNightVision")) / 2, resolution.getScaledHeight() - 50, 16777215);
		Minecraft.getInstance().fontRenderer.drawStringWithShadow(KeyBindings.cameraEmitRedstone.getLocalizedName() + " - " + ClientUtils.localize("gui.securitycraft:camera.toggleRedstone"), resolution.getScaledWidth() - 82 - Minecraft.getInstance().fontRenderer.getStringWidth(KeyBindings.cameraEmitRedstone.getLocalizedName() + " - " + ClientUtils.localize("gui.securitycraft:camera.toggleRedstone")) / 2, resolution.getScaledHeight() - 40, ((CustomizableTileEntity) world.getTileEntity(pos)).hasModule(CustomModules.REDSTONE) ? 16777215 : 16724855);
		Minecraft.getInstance().fontRenderer.drawStringWithShadow(ClientUtils.localize("gui.securitycraft:camera.toggleRedstoneNote"), resolution.getScaledWidth() - 82 - Minecraft.getInstance().fontRenderer.getStringWidth(ClientUtils.localize("gui.securitycraft:camera.toggleRedstoneNote")) / 2, resolution.getScaledHeight() - 30, ((CustomizableTileEntity) world.getTileEntity(pos)).hasModule(CustomModules.REDSTONE) ? 16777215 : 16724855);

		mc.getTextureManager().bindTexture(cameraDashboard);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		gui.blit(5, 0, 0, 0, 90, 20);
		gui.blit(resolution.getScaledWidth() - 55, 5, 205, 0, 50, 30);

		if(player.getActivePotionEffect(ForgeRegistries.POTIONS.getValue(nightVis)) == null)
			gui.blit(28, 4, 90, 12, 16, 11);
		else{
			mc.getTextureManager().bindTexture(potionIcons);
			gui.blit(25, 2, 70, 218, 19, 16);
		}

		if((world.getBlockState(pos).getWeakPower(world, pos, BlockUtils.getBlockProperty(world, pos, SecurityCameraBlock.FACING)) == 0) && (!((CustomizableTileEntity) world.getTileEntity(pos)).hasModule(CustomModules.REDSTONE)))
			gui.blit(12, 2, 104, 0, 12, 12);
		else if((world.getBlockState(pos).getWeakPower(world, pos, BlockUtils.getBlockProperty(world, pos, SecurityCameraBlock.FACING)) == 0) && (((CustomizableTileEntity) world.getTileEntity(pos)).hasModule(CustomModules.REDSTONE)))
			gui.blit(12, 3, 90, 0, 12, 11);
		else
			itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.REDSTONE), 10, 0);
	}
}
