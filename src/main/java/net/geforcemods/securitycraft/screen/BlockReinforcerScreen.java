package net.geforcemods.securitycraft.screen;

import java.io.IOException;
import java.util.Arrays;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.inventory.BlockReinforcerMenu;
import net.geforcemods.securitycraft.network.server.SyncBlockReinforcer;
import net.geforcemods.securitycraft.screen.components.CallbackCheckbox;
import net.geforcemods.securitycraft.screen.components.StringHoverChecker;
import net.geforcemods.securitycraft.util.GuiUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class BlockReinforcerScreen extends GuiContainer {
	private static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/universal_block_reinforcer.png");
	private static final ResourceLocation TEXTURE_LVL1 = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/universal_block_reinforcer_lvl1.png");
	private boolean isLvl1;
	private CallbackCheckbox unreinforceCheckbox;
	private StringHoverChecker checkboxHoverChecker;

	public BlockReinforcerScreen(Container container, boolean isLvl1) {
		super(container);
		this.isLvl1 = isLvl1;
		ySize = 186;
	}

	@Override
	public void initGui() {
		super.initGui();
		unreinforceCheckbox = addButton(new CallbackCheckbox(0, guiLeft + 24, guiTop + 69, 20, 20, "", !((BlockReinforcerMenu) inventorySlots).isReinforcing, state -> {}, 0));

		if (isLvl1)
			unreinforceCheckbox.visible = false;

		checkboxHoverChecker = new StringHoverChecker(unreinforceCheckbox, Arrays.asList(Utils.localize("gui.securitycraft:blockReinforcer.unreinforceCheckbox.not_checked").getFormattedText(), Utils.localize("gui.securitycraft:blockReinforcer.unreinforceCheckbox.checked").getFormattedText()));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);

		if (checkboxHoverChecker.checkHover(mouseX, mouseY))
			drawHoveringText(checkboxHoverChecker.getName(), mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		BlockReinforcerMenu container = (BlockReinforcerMenu) inventorySlots;
		NonNullList<ItemStack> inv = container.getInventory();
		String ubr = Minecraft.getMinecraft().player.getHeldItemMainhand().getDisplayName();

		fontRenderer.drawString(ubr, (xSize - fontRenderer.getStringWidth(ubr)) / 2, 5, 4210752);
		fontRenderer.drawString(Utils.localize("container.inventory").getFormattedText(), 8, ySize - 96 + 2, 4210752);

		if (!inv.get(36).isEmpty()) {
			fontRenderer.drawString(Utils.localize("gui.securitycraft:blockReinforcer.output").getFormattedText(), 50, 25, 4210752);
			GuiUtils.drawItemStackToGui(container.reinforcingSlot.getOutput(), 116, 20, false);

			if (mouseX >= guiLeft + 114 && mouseX < guiLeft + 134 && mouseY >= guiTop + 17 && mouseY < guiTop + 39)
				renderToolTip(container.reinforcingSlot.getOutput(), mouseX - guiLeft, mouseY - guiTop);
		}

		if (!isLvl1 && !inv.get(37).isEmpty()) {
			fontRenderer.drawString(Utils.localize("gui.securitycraft:blockReinforcer.output").getFormattedText(), 50, 50, 4210752);
			GuiUtils.drawItemStackToGui(container.unreinforcingSlot.getOutput(), 116, 46, false);

			if (mouseX >= guiLeft + 114 && mouseX < guiLeft + 134 && mouseY >= guiTop + 43 && mouseY < guiTop + 64)
				renderToolTip(container.unreinforcingSlot.getOutput(), mouseX - guiLeft, mouseY - guiTop);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(isLvl1 ? TEXTURE_LVL1 : TEXTURE);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button == unreinforceCheckbox)
			unreinforceCheckbox.onClick();
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		SecurityCraft.network.sendToServer(new SyncBlockReinforcer(!unreinforceCheckbox.selected()));
	}
}
