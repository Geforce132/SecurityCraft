package net.geforcemods.securitycraft.screen;

import java.util.List;

import com.mojang.blaze3d.platform.InputConstants;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.geforcemods.securitycraft.components.GlobalPositions;
import net.geforcemods.securitycraft.items.SonicSecuritySystemItem;
import net.geforcemods.securitycraft.network.server.RemovePositionFromSSS;
import net.geforcemods.securitycraft.screen.components.SSSConnectionList;
import net.geforcemods.securitycraft.screen.components.SSSConnectionList.ConnectionAccessor;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class SSSItemScreen extends Screen implements ConnectionAccessor {
	private static final ResourceLocation TEXTURE = SecurityCraft.resLoc("textures/gui/container/blank.png");
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
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0.0F, 0.0F, imageWidth, imageHeight, 256, 256);
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
	public List<GlobalPos> getPositions() {
		return stack.getOrDefault(SCContent.SSS_LINKED_BLOCKS, GlobalPositions.sized(SonicSecuritySystemBlockEntity.MAX_LINKED_BLOCKS)).positions().stream().toList();
	}

	@Override
	public void removePosition(GlobalPos globalPos) {
		GlobalPositions sssLinkedBlocks = stack.get(SCContent.SSS_LINKED_BLOCKS);

		if (sssLinkedBlocks != null)
			sssLinkedBlocks.remove(SCContent.SSS_LINKED_BLOCKS, stack, globalPos);

		PacketDistributor.sendToServer(new RemovePositionFromSSS(globalPos));
		connectionList.refreshPositions();
	}
}
