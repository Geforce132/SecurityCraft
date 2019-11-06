package net.geforcemods.securitycraft.screen;

import java.util.Random;

import com.mojang.blaze3d.platform.GlStateManager;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.KeypadFurnaceContainer;
import net.geforcemods.securitycraft.network.server.CloseFurnace;
import net.geforcemods.securitycraft.tileentity.KeypadFurnaceTileEntity;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KeypadFurnaceScreen extends ContainerScreen<KeypadFurnaceContainer>{

	private static final ResourceLocation furnaceGuiTextures = new ResourceLocation("textures/gui/container/furnace.png");
	private KeypadFurnaceTileEntity tileFurnace;
	private boolean gurnace = false;

	public KeypadFurnaceScreen(KeypadFurnaceContainer container, PlayerInventory inv, ITextComponent name){
		super(container, inv, name);
		tileFurnace = container.te;

		if(new Random().nextInt(100) < 5)
			gurnace = true;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks)
	{
		super.render(mouseX, mouseY, partialTicks);

		if(getSlotUnderMouse() != null && !getSlotUnderMouse().getStack().isEmpty())
			renderTooltip(getSlotUnderMouse().getStack(), mouseX, mouseY);
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		String s = gurnace ? "Keypad Gurnace" : (tileFurnace.hasCustomSCName() ? tileFurnace.getCustomSCName().getFormattedText() : ClientUtils.localize("gui.securitycraft:protectedFurnace.name"));
		font.drawString(s, xSize / 2 - font.getStringWidth(s) / 2, 6, 4210752);
		font.drawString(ClientUtils.localize("container.inventory"), 8, ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		renderBackground();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(furnaceGuiTextures);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		this.blit(startX, startY, 0, 0, xSize, ySize);

		if (tileFurnace.isBurning())
		{
			int burnTime = tileFurnace.getBurnTimeRemainingScaled(13);
			this.blit(startX + 56, startY + 36 + 12 - burnTime, 176, 12 - burnTime, 14, burnTime + 1);
			burnTime = tileFurnace.getCookProgressScaled(24);
			this.blit(startX + 79, startY + 34, 176, 14, burnTime + 1, 16);
		}
	}

	@Override
	public void onClose(){
		super.onClose();
		SecurityCraft.channel.sendToServer(new CloseFurnace(tileFurnace.getPos()));
	}

}