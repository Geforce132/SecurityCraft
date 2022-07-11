package net.geforcemods.securitycraft.gui;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.ContainerDisguiseModule;
import net.geforcemods.securitycraft.containers.ContainerStateSelectorAccess;
import net.geforcemods.securitycraft.gui.components.StateSelector;
import net.geforcemods.securitycraft.inventory.ModuleItemInventory;
import net.geforcemods.securitycraft.network.server.SetStateOnDisguiseModule;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;

public class GuiDisguiseModule extends GuiContainer {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/customize1.png");
	private StateSelector stateSelector;

	public GuiDisguiseModule(InventoryPlayer inventory) {
		super(new ContainerDisguiseModule(inventory, new ModuleItemInventory(PlayerUtils.getSelectedItemStack(inventory, SCContent.disguiseModule))));
	}

	@Override
	public void initGui() {
		super.initGui();

		guiLeft += 90;
		stateSelector = new StateSelector((ContainerStateSelectorAccess) inventorySlots, guiLeft - 190, guiTop + 7, 0, 200, 15);
		stateSelector.initGui();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

		if (getSlotUnderMouse() != null && !getSlotUnderMouse().getStack().isEmpty())
			renderToolTip(getSlotUnderMouse().getStack(), mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(Utils.localize("item.securitycraft:disguiseModule.name").getFormattedText(), xSize / 2 - fontRenderer.getStringWidth(Utils.localize("item.securitycraft:disguiseModule.name").getFormattedText()) / 2, 6, 0x404040);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

		if (stateSelector != null)
			stateSelector.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
		if (stateSelector != null)
			stateSelector.mouseClicked(mouseX, mouseY, button);

		super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		if (stateSelector != null)
			stateSelector.mouseReleased(mouseX, mouseY, state);

		super.mouseReleased(mouseX, mouseY, state);
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();

		if (!inventorySlots.getSlot(0).getStack().isEmpty() && stateSelector.getState() != null) {
			ItemStack module = ((ContainerDisguiseModule) inventorySlots).getModuleInventory().getModule();
			NBTTagCompound moduleTag;
			IBlockState state = stateSelector.getState();

			if (!module.hasTagCompound())
				module.setTagCompound(new NBTTagCompound());

			moduleTag = module.getTagCompound();
			moduleTag.setTag("SavedState", NBTUtil.writeBlockState(new NBTTagCompound(), state));
			SecurityCraft.network.sendToServer(new SetStateOnDisguiseModule(state));
		}
	}

	public List<Rectangle> getExtraAreas() {
		if (stateSelector != null)
			return stateSelector.getExtraAreas();
		else
			return new ArrayList<>();
	}
}
