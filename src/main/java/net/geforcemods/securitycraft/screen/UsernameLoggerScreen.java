package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.GenericTEContainer;
import net.geforcemods.securitycraft.network.server.ClearLoggerServer;
import net.geforcemods.securitycraft.screen.components.ClickButton;
import net.geforcemods.securitycraft.tileentity.UsernameLoggerTileEntity;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class UsernameLoggerScreen extends ContainerScreen<GenericTEContainer>{

	private UsernameLoggerTileEntity tileEntity;
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");

	public UsernameLoggerScreen(GenericTEContainer container, PlayerInventory inv, ITextComponent name) {
		super(container, inv, name);
		tileEntity = (UsernameLoggerTileEntity)container.te;
	}

	@Override
	protected void init()
	{
		super.init();

		addButton(new ClickButton(0, guiLeft + 4, guiTop + 4, 8, 8, "x", b -> {
			tileEntity.players = new String[100];
			SecurityCraft.channel.sendToServer(new ClearLoggerServer(tileEntity.getPos()));
		})).active = tileEntity.getOwner().isOwner(minecraft.player);
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		String localized = ClientUtils.localize("gui.securitycraft:logger.logged");

		font.drawString(localized, xSize / 2 - font.getStringWidth(localized) / 2, 6, 4210752);

		for(int i = 0; i < tileEntity.players.length; i++)
			if(tileEntity.players[i] != "")
				font.drawString(tileEntity.players[i], xSize / 2 - font.getStringWidth(tileEntity.players[i]) / 2, 25 + (10 * i), 4210752);

		if(mouseX >= guiLeft + 4 && mouseY >= guiTop + 4 && mouseX < guiLeft + 4 + 8 && mouseY < guiTop + 4 + 8)
			renderTooltip(ClientUtils.localize("gui.securitycraft:editModule.clear"), mouseX - guiLeft, mouseY - guiTop);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		renderBackground();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		this.blit(startX, startY, 0, 0, xSize, ySize);
	}

}
