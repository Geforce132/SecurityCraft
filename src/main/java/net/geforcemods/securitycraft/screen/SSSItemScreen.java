package net.geforcemods.securitycraft.screen;

import java.util.Set;

import com.mojang.blaze3d.platform.InputConstants;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.items.SonicSecuritySystemItem;
import net.geforcemods.securitycraft.network.server.RemovePositionFromSSS;
import net.geforcemods.securitycraft.screen.components.SSSConnectionList;
import net.geforcemods.securitycraft.screen.components.SSSConnectionList.ConnectionAccessor;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class SSSItemScreen extends Screen implements ConnectionAccessor {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private final ItemStack stack;
	private int imageWidth = 176;
	private int imageHeight = 166;
	private int leftPos;
	private int topPos;
	private SSSConnectionList<SSSItemScreen> connectionList;

	public SSSItemScreen(ItemStack stack) {
		super(Utils.localize(SCContent.SONIC_SECURITY_SYSTEM_ITEM.get().getDescriptionId()));

		if (stack.getItem() instanceof SonicSecuritySystemItem)
			this.stack = stack;
		else
			this.stack = ItemStack.EMPTY;
	}

	@Override
	protected void init() {
		super.init();

		leftPos = (width - imageWidth) / 2;
		topPos = (height - imageHeight) / 2;
		connectionList = addRenderableWidget(new SSSConnectionList<>(this, minecraft, imageWidth - 24, imageHeight - 40, topPos + 20, leftPos + 12));
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		int textWidth = font.width(title);

		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		guiGraphics.drawString(font, title, leftPos + imageWidth / 2 - textWidth / 2, topPos + 6, 4210752, false);
	}

	@Override
	public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		renderTransparentBackground(guiGraphics);
		guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (minecraft.options.keyInventory.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode))) {
			onClose();
			return true;
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public Set<BlockPos> getPositions() {
		return SonicSecuritySystemItem.stackTagToBlockPosSet(Utils.getTag(stack));
	}

	@Override
	public void removePosition(BlockPos pos) {
		SonicSecuritySystemItem.removeLinkedBlock(stack, pos);
		PacketDistributor.SERVER.noArg().send(new RemovePositionFromSSS(pos));
		connectionList.refreshPositions();
	}
}
