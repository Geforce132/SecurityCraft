package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.systems.RenderSystem;

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
import net.minecraft.item.ItemStack;
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
	private Button assembleButton;
	private Button outlineButton;
	private static final ItemStack BLOCK_POCKET_WALL = new ItemStack(SCContent.BLOCK_POCKET_WALL.get());
	private static final ItemStack REINFORCED_CHISELED_CRYSTAL_QUARTZ = new ItemStack(SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get());
	private static final ItemStack REINFORCED_CRYSTAL_QUARTZ_PILLAR = new ItemStack(SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get());

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

		addButton(toggleButton = new ClickButton(0, guiLeft + xSize / 2 - 45, guiTop + ySize / 2 - 30, 90, 20, ClientUtils.localize("gui.securitycraft:blockPocketManager." + (!te.enabled ? "activate" : "deactivate")), this::toggleButtonClicked));
		addButton(sizeButton = new ClickButton(1, guiLeft + xSize / 2 - 60, guiTop + ySize / 2 - 60, 120, 20, ClientUtils.localize("gui.securitycraft:blockPocketManager.size", size, size, size), this::sizeButtonClicked));
		addButton(assembleButton = new ClickButton(0, guiLeft + xSize / 2 - 45, guiTop + ySize / 2 + 33, 90, 20, ClientUtils.localize("gui.securitycraft:blockPocketManager.assemble"), this::assembleButtonClicked));
		addButton(outlineButton = new ClickButton(0, guiLeft + xSize / 2 - 60, guiTop + ySize / 2 + 57, 120, 20, ClientUtils.localize("gui.securitycraft:blockPocketManager.outline." + (!te.showOutline ? "show" : "hide")), this::outlineButtonClicked));

		if(!te.getOwner().isOwner(Minecraft.getInstance().player))
			sizeButton.active = toggleButton.active = assembleButton.active = outlineButton.active = false;
		else
			sizeButton.active = assembleButton.active = !te.enabled;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		String translation = ClientUtils.localize(SCContent.BLOCK_POCKET_MANAGER.get().getTranslationKey());

		font.drawString(translation, xSize / 2 - font.getStringWidth(translation) / 2, 6, 4210752);

		if (!te.enabled)
		{
			font.drawString(ClientUtils.localize("gui.securitycraft:blockPocketManager.youNeed"), xSize / 2 - font.getStringWidth(ClientUtils.localize("gui.securitycraft:blockPocketManager.youNeed")) / 2, 83, 4210752);

			font.drawString((size - 2) * (size - 2) * 6 + "", 42, 100, 4210752);
			minecraft.getItemRenderer().renderItemAndEffectIntoGUI(BLOCK_POCKET_WALL, 25, 96);

			font.drawString((size - 2) * 12 - 1 + "", 94, 100, 4210752);
			minecraft.getItemRenderer().renderItemAndEffectIntoGUI(REINFORCED_CRYSTAL_QUARTZ_PILLAR, 77, 96);

			font.drawString("8", 147, 100, 4210752);
			minecraft.getItemRenderer().renderItemAndEffectIntoGUI(REINFORCED_CHISELED_CRYSTAL_QUARTZ, 130, 96);

			if(mouseX >= guiLeft + 23 && mouseX < guiLeft + 48 && mouseY >= guiTop + 93 && mouseY < guiTop + 115)
				renderTooltip(BLOCK_POCKET_WALL, mouseX - guiLeft, mouseY - guiTop);

			if(mouseX >= guiLeft + 75 && mouseX < guiLeft + 100 && mouseY >= guiTop + 93 && mouseY < guiTop + 115)
				renderTooltip(REINFORCED_CRYSTAL_QUARTZ_PILLAR, mouseX - guiLeft, mouseY - guiTop);

			if(mouseX >= guiLeft + 128 && mouseX < guiLeft + 153 && mouseY >= guiTop + 93 && mouseY < guiTop + 115)
				renderTooltip(REINFORCED_CHISELED_CRYSTAL_QUARTZ, mouseX - guiLeft, mouseY - guiTop);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		renderBackground();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
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
				PlayerUtils.sendMessageToPlayer(Minecraft.getInstance().player, ClientUtils.localize(SCContent.BLOCK_POCKET_MANAGER.get().getTranslationKey()), ClientUtils.localize(feedback.getKey(), feedback.getFormatArgs()), TextFormatting.DARK_AQUA);
		}

		Minecraft.getInstance().player.closeScreen();
	}

	public void sizeButtonClicked(ClickButton button)
	{
		size += 4;

		if(size > 25)
			size = 5;

		te.size = size;
		button.setMessage(ClientUtils.localize("gui.securitycraft:blockPocketManager.size", size, size, size));
	}

	public void assembleButtonClicked(ClickButton button)
	{
		TranslationTextComponent feedback;

		te.size = size;
		feedback = te.autoAssembleMultiblock(Minecraft.getInstance().player);

		if(feedback != null)
			PlayerUtils.sendMessageToPlayer(Minecraft.getInstance().player, ClientUtils.localize(SCContent.BLOCK_POCKET_MANAGER.get().getTranslationKey()), ClientUtils.localize(feedback.getKey(), feedback.getFormatArgs()), TextFormatting.DARK_AQUA);

		Minecraft.getInstance().player.closeScreen();
	}

	public void outlineButtonClicked(ClickButton button)
	{
		te.toggleOutline();
		outlineButton.setMessage(ClientUtils.localize("gui.securitycraft:blockPocketManager.outline."+ (!te.showOutline ? "show" : "hide")));
	}
}
