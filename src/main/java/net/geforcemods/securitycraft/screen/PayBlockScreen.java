package net.geforcemods.securitycraft.screen;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.PayBlockBlockEntity;
import net.geforcemods.securitycraft.inventory.PayBlockMenu;
import net.geforcemods.securitycraft.network.server.TogglePayBlock;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class PayBlockScreen extends AbstractContainerScreen<PayBlockMenu> {
	private static final ResourceLocation GUI_TEXTURE = SecurityCraft.resLoc("textures/gui/container/pay_block.png");
	private static final ResourceLocation GUI_TEXTURE_OWNER = SecurityCraft.resLoc("textures/gui/container/pay_block_owner.png");
	private static final ResourceLocation GUI_TEXTURE_STORAGE = SecurityCraft.resLoc("textures/gui/container/pay_block_storage.png");
	private final Component paymentText = Utils.localize("Payment: "); //TODO l10n
	private final Component rewardText = Utils.localize("Reward: Redstone Activation");
	private final Component storedItemsText = Utils.localize("+ Stored items: ");
	private final PayBlockBlockEntity be;
	private final boolean isOwner;
	private Button payButton;

	public PayBlockScreen(PayBlockMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);

		be = menu.be;
		isOwner = be.isOwnedBy(playerInventory.player);
		imageHeight = 249;
		inventoryLabelY = imageHeight - 94;
	}

	@Override
	protected void init() {
		super.init();

		payButton = addRenderableWidget(new Button(leftPos + 112, topPos + 43, 50, 16, Component.literal("Pay"), this::sendTransactionRequest, Button.DEFAULT_NARRATION));
		payButton.active = false;
	}

	@Override
	protected void containerTick() {
		super.containerTick();

		payButton.active = menu.paymentLimitedTransactions > 0;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.render(guiGraphics, mouseX, mouseY, partialTick);

		renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		super.renderLabels(guiGraphics, mouseX, mouseY);
		guiGraphics.drawString(font, paymentText, 15, 25, 0x404040, false);
		guiGraphics.drawString(font, rewardText, 15, 65, 0x404040, false);

		if (be.hasRewardReferenceStacks() || menu.withStorageAccess) {
			guiGraphics.drawString(font, storedItemsText, 15, 80, 0x404040, false);

			if (payButton.active)
				guiGraphics.drawString(font, "(" + Math.min(menu.paymentLimitedTransactions, be.rewardLimitedTransactions) + "x)", 105, 100, menu.paymentLimitedTransactions > be.rewardLimitedTransactions ? 0x900000 : 0x404040, false);
		}
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
		guiGraphics.blit(isOwner ? (menu.withStorageAccess ? GUI_TEXTURE_STORAGE : GUI_TEXTURE_OWNER) : GUI_TEXTURE, leftPos, topPos, 0.0F, 0.0F, imageWidth, imageHeight, 256, 256);
	}

	private void sendTransactionRequest(Button button) {
		PacketDistributor.sendToServer(new TogglePayBlock(be.getBlockPos(), Math.min(menu.paymentLimitedTransactions, Math.max(be.rewardLimitedTransactions, 1))));
	}
}
