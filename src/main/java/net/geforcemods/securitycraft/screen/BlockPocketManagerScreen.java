package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.platform.GlStateManager;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.containers.GenericTEContainer;
import net.geforcemods.securitycraft.screen.components.ClickButton;
import net.geforcemods.securitycraft.tileentity.BlockPocketManagerTileEntity;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class BlockPocketManagerScreen extends ContainerScreen<GenericTEContainer>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	public BlockPocketManagerTileEntity te;
	private int size = 5;
	private Button toggleButton;
	private Button sizeButton;

	public BlockPocketManagerScreen(GenericTEContainer container, PlayerInventory inv, ITextComponent name)
	{
		super(container, inv, name);

		te = (BlockPocketManagerTileEntity)container.te;
		size = te.size;
	}

	@Override
	public void init()
	{
		super.init();

		addButton(toggleButton = new ClickButton(0, guiLeft + xSize / 2 - 45, guiTop + ySize / 2 - 10, 90, 20, ClientUtils.localize("gui.securitycraft:blockPocketManager." + (!te.enabled ? "activate" : "deactivate")), this::toggleButtonClicked));
		addButton(sizeButton = new ClickButton(1, guiLeft + xSize / 2 - 60, guiTop + ySize / 2 - 40, 120, 20, ClientUtils.localize("gui.securitycraft:blockPocketManager.size", size, size, size), this::sizeButtonClicked));

		if(!te.getOwner().isOwner(Minecraft.getInstance().player))
			sizeButton.active = toggleButton.active = false;
		else
			sizeButton.active = !te.enabled;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		String translation = ClientUtils.localize(SCContent.blockPocketManager.getTranslationKey());

		font.drawString(translation, xSize / 2 - font.getStringWidth(translation) / 2, 6, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		renderBackground();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		this.blit(startX, startY, 0, 0, xSize, ySize);
	}

	public void toggleButtonClicked(ClickButton button)
	{
		if(te.enabled)
			te.disableMultiblock();
		else
		{
			TranslationTextComponent feedback;

			te.size = size;
			feedback = te.enableMultiblock();

			if(feedback != null)
				PlayerUtils.sendMessageToPlayer(Minecraft.getInstance().player, ClientUtils.localize(SCContent.blockPocketManager.getTranslationKey()), ClientUtils.localize(feedback.getKey(), feedback.getFormatArgs()), TextFormatting.DARK_AQUA);
		}

		Minecraft.getInstance().player.closeScreen();
	}

	public void sizeButtonClicked(ClickButton button)
	{
		size += 4;

		if(size > 25)
			size = 5;

		button.setMessage(ClientUtils.localize("gui.securitycraft:blockPocketManager.size", size, size, size));
	}
}
