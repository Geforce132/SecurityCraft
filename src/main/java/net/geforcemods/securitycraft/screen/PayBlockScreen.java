package net.geforcemods.securitycraft.screen;

import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.PayBlockBlockEntity;
import net.geforcemods.securitycraft.inventory.PayBlockMenu;
import net.geforcemods.securitycraft.network.server.TogglePayBlock;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class PayBlockScreen extends AbstractContainerScreen<PayBlockMenu> {
	private static final ResourceLocation GUI_TEXTURE = SecurityCraft.resLoc("textures/gui/container/pay_block.png");
	private static final ResourceLocation GUI_TEXTURE_OWNER = SecurityCraft.resLoc("textures/gui/container/pay_block_owner.png");
	private static final ResourceLocation GUI_TEXTURE_STORAGE = SecurityCraft.resLoc("textures/gui/container/pay_block_storage.png");
	private static final ResourceLocation WARNING_HIGHLIGHTED_SPRITE = SecurityCraft.mcResLoc("world_list/warning_highlighted");
	private final Component paymentText = Utils.localize("Payment: "); //TODO l10n
	private final Component rewardText = Utils.localize("Reward: Redstone Activation");
	private final Component storedItemsText = Utils.localize("+ Stored items: ");
	private final PayBlockBlockEntity be;
	private final boolean isOwner;
	private final boolean hasRewardReference;
	private Button payButton;
	private EditBox transactionAmountBox;
	private int requestedTransactions;

	public PayBlockScreen(PayBlockMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);

		be = menu.be;
		isOwner = be.isOwnedBy(playerInventory.player);
		hasRewardReference = be.hasRewardReferenceStacks();
		imageHeight = 249;
		inventoryLabelY = imageHeight - 94;
	}

	@Override
	protected void init() {
		super.init();

		payButton = addRenderableWidget(new Button(leftPos + 112, topPos + 43, 50, 16, Component.literal("Pay"), this::sendTransactionRequest, Button.DEFAULT_NARRATION));
		payButton.active = false;

		if (hasRewardReference) {
			transactionAmountBox = addRenderableWidget(new EditBox(font, leftPos + 112, topPos + 93, 26, 16, Component.empty()));
			transactionAmountBox.setValue("1");
			transactionAmountBox.setFilter(s -> s.matches("\\d*")); //Only allow numbers
			transactionAmountBox.setMaxLength(3);
			transactionAmountBox.setTooltip(Tooltip.create(Utils.localize("How many transactions should be done?")));
			transactionAmountBox.setResponder(s -> requestedTransactions = s.isEmpty() ? 1 : Integer.parseInt(s));
			requestedTransactions = 1;
		}
	}

	@Override
	protected void containerTick() {
		super.containerTick();
		payButton.active = getTransactionsOnConfirmation() > 0;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.render(guiGraphics, mouseX, mouseY, partialTick);

		if ((hasRewardReference || menu.withStorageAccess) && getTransactionsOnConfirmation() > be.rewardLimitedTransactions) {
			guiGraphics.blitSprite(WARNING_HIGHLIGHTED_SPRITE, leftPos + 148, topPos + 89, 24, 24);

			if (mouseX >= leftPos + 148 && mouseX <= leftPos + 160 && mouseY >= topPos + 92 && mouseY <= topPos + 110) { //TODO l10n and below
				Component warning;

				if (be.rewardLimitedTransactions == 0)
					warning = Utils.localize("Warning: There are not enough items left in stock to fully pay out transactions!");
				else
					warning = Utils.localize("Warning: There are only enough items in stock to fully pay out %s transactions!", be.rewardLimitedTransactions);

				guiGraphics.renderComponentTooltip(font, List.of(warning), mouseX, mouseY);
			}
		}

		if (payButton.active && mouseX >= leftPos + 112 && mouseX <= leftPos + 161 && mouseY >= topPos + 43 && mouseY <= topPos + 58) {
			Component buttonTooltip;
			int transactions = getTransactionsOnConfirmation();

			if (transactions == 1)
				buttonTooltip = Utils.localize("Confirm transaction");
			else
				buttonTooltip = Utils.localize("Confirm %s transactions", transactions);

			guiGraphics.renderComponentTooltip(font, List.of(buttonTooltip), mouseX, mouseY);
		}

		renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		super.renderLabels(guiGraphics, mouseX, mouseY);
		guiGraphics.drawString(font, paymentText, 15, 25, 0x404040, false);
		guiGraphics.drawString(font, rewardText, 15, 65, 0x404040, false);

		if (hasRewardReference || menu.withStorageAccess)
			guiGraphics.drawString(font, storedItemsText, 15, 80, 0x404040, false);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
		guiGraphics.blit(isOwner ? (menu.withStorageAccess ? GUI_TEXTURE_STORAGE : GUI_TEXTURE_OWNER) : GUI_TEXTURE, leftPos, topPos, 0.0F, 0.0F, imageWidth, imageHeight, 256, 256);
	}

	private void sendTransactionRequest(Button button) {
		PacketDistributor.sendToServer(new TogglePayBlock(be.getBlockPos(), getTransactionsOnConfirmation()));
		transactionAmountBox.setValue("1");
	}

	private int getTransactionsOnConfirmation() {
		return Math.min(menu.paymentLimitedTransactions, Math.max(1, requestedTransactions));
	}
}
