package net.geforcemods.securitycraft.gui;

import java.io.IOException;
import java.util.Set;

import org.lwjgl.input.Mouse;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.gui.components.SSSConnectionList;
import net.geforcemods.securitycraft.gui.components.SSSConnectionList.ConnectionAccessor;
import net.geforcemods.securitycraft.items.ItemSonicSecuritySystem;
import net.geforcemods.securitycraft.network.server.RemovePositionFromSSS;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class GuiSSSItem extends GuiScreen implements ConnectionAccessor {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private final String title = Utils.localize(SCContent.sonicSecuritySystemItem).getFormattedText();
	private final ItemStack stack;
	private final int imageWidth = 176;
	private final int imageHeight = 166;
	private int leftPos;
	private int topPos;
	private SSSConnectionList<GuiSSSItem> connectionList;

	public GuiSSSItem(ItemStack stack) {
		if (stack.getItem() instanceof ItemSonicSecuritySystem)
			this.stack = stack;
		else
			this.stack = ItemStack.EMPTY;
	}

	@Override
	public void initGui() {
		super.initGui();

		leftPos = (width - imageWidth) / 2;
		topPos = (height - imageHeight) / 2;
		connectionList = new SSSConnectionList<>(this, mc, imageWidth - 24, imageHeight - 40, topPos + 20, leftPos + 12);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		int textWidth = fontRenderer.getStringWidth(title);

		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		drawTexturedModalRect(leftPos, topPos, 0, 0, imageWidth, imageHeight);
		super.drawScreen(mouseX, mouseY, partialTicks);

		if (connectionList != null)
			connectionList.drawScreen(mouseX, mouseY, partialTicks);

		fontRenderer.drawString(title, leftPos + imageWidth / 2 - textWidth / 2, topPos + 6, 4210752);
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();

		int mouseX = Mouse.getEventX() * width / mc.displayWidth;
		int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;

		connectionList.handleMouseInput(mouseX, mouseY);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public Set<BlockPos> getPositions() {
		return ItemSonicSecuritySystem.stackTagToBlockPosSet(stack.getTagCompound());
	}

	@Override
	public void removePosition(BlockPos pos) {
		ItemSonicSecuritySystem.removeLinkedBlock(stack.getTagCompound(), pos);
		SecurityCraft.network.sendToServer(new RemovePositionFromSSS(pos));
		connectionList.refreshPositions();
	}
}
