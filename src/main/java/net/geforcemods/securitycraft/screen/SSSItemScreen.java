package net.geforcemods.securitycraft.screen;

import java.io.IOException;
import java.util.Set;

import org.lwjgl.input.Mouse;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.items.SonicSecuritySystemItem;
import net.geforcemods.securitycraft.network.server.RemovePositionFromSSS;
import net.geforcemods.securitycraft.screen.components.SSSConnectionList;
import net.geforcemods.securitycraft.screen.components.SSSConnectionList.ConnectionAccessor;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class SSSItemScreen extends GuiScreen implements ConnectionAccessor {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private final String title;
	private final ItemStack stack;
	private final int imageWidth = 176;
	private final int imageHeight = 166;
	private int leftPos;
	private int topPos;
	private SSSConnectionList<SSSItemScreen> connectionList;

	public SSSItemScreen(ItemStack stack) {
		if (stack.getItem() instanceof SonicSecuritySystemItem) {
			this.stack = stack;
			title = stack.getDisplayName();
		}
		else {
			this.stack = ItemStack.EMPTY;
			title = Utils.localize(SCContent.sonicSecuritySystem).getFormattedText();
		}
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
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);

		if (keyCode == 1 || mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode))
			mc.player.closeScreen();
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public Set<BlockPos> getPositions() {
		return SonicSecuritySystemItem.stackTagToBlockPosSet(stack.getTagCompound());
	}

	@Override
	public void removePosition(BlockPos pos) {
		SonicSecuritySystemItem.removeLinkedBlock(stack.getTagCompound(), pos);
		SecurityCraft.network.sendToServer(new RemovePositionFromSSS(pos));
		connectionList.refreshPositions();
	}
}
