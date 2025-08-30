package net.geforcemods.securitycraft.screen;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.SecureTradingStationBlockEntity;
import net.geforcemods.securitycraft.inventory.SecureTradingStationMenu;
import net.geforcemods.securitycraft.network.server.RequestSecureTradingStationTransactions;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class SecureTradingStationScreen extends AbstractContainerScreen<SecureTradingStationMenu> {
	private static final ResourceLocation GUI_TEXTURE = SecurityCraft.resLoc("textures/gui/container/secure_trading_station.png");
	private static final ResourceLocation GUI_TEXTURE_OWNER = SecurityCraft.resLoc("textures/gui/container/secure_trading_station_owner.png");
	private static final ResourceLocation GUI_TEXTURE_OWNER_STORAGE = SecurityCraft.resLoc("textures/gui/container/secure_trading_station_owner_storage.png");
	private static final ResourceLocation WARNING_HIGHLIGHTED_SPRITE = SecurityCraft.mcResLoc("world_list/warning_highlighted");
	private final Component paymentText = Utils.localize("gui.securitycraft:secure_trading_station.payment");
	private final Component rewardText = Utils.localize("gui.securitycraft:secure_trading_station.reward_redstone");
	private final Component storedItemsText = Utils.localize("gui.securitycraft:secure_trading_station.reward_items");
	private final Component payButtonText = Utils.localize("gui.securitycraft:secure_trading_station.pay_button");
	private final Component transactionAmountBoxTooltip = Utils.localize("gui.securitycraft:secure_trading_station.transaction_amount_tooltip");
	private final Component noRewardText = Utils.localize("gui.securitycraft:secure_trading_station.no_reward");
	private final Component confirmText = Utils.localize("gui.securitycraft:secure_trading_station.confirm");
	private final Component allowedText = Utils.localize("gui.securitycraft:secure_trading_station.allowed");
	private final SecureTradingStationBlockEntity be;
	private final boolean isOwner;
	private final boolean skipPaymentCheck;
	private final boolean storageVisible;
	private Button payButton;
	private EditBox transactionAmountBox;
	private int requestedTransactions;

	public SecureTradingStationScreen(SecureTradingStationMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);

		be = menu.be;
		isOwner = be.isOwnedBy(playerInventory.player);
		skipPaymentCheck = isOwner || be.isAllowed(playerInventory.player);
		storageVisible = be.hasRewardReferenceStacks() || menu.withStorageAccess;
		imageHeight = 249;
		inventoryLabelY = imageHeight - 94;
	}

	@Override
	protected void init() {
		super.init();

		payButton = addRenderableWidget(new Button(leftPos + 112, topPos + 43, 50, 16, payButtonText, this::sendTransactionRequest, Button.DEFAULT_NARRATION));
		payButton.active = skipPaymentCheck;

		if (storageVisible) {
			transactionAmountBox = addRenderableWidget(new EditBox(font, leftPos + 112, topPos + 93, 26, 16, Component.empty()));
			transactionAmountBox.setFilter(s -> s.matches("\\d*")); //Only allow strings of digits or empty
			transactionAmountBox.setMaxLength(3);
			transactionAmountBox.setHint(Component.literal("1").withStyle(ChatFormatting.GRAY));
			transactionAmountBox.setTooltip(Tooltip.create(transactionAmountBoxTooltip));
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

		if (be.hasRewardReferenceStacks() && getTransactionsOnConfirmation() > be.rewardLimitedTransactions) {
			guiGraphics.blitSprite(WARNING_HIGHLIGHTED_SPRITE, leftPos + 148, topPos + 89, 24, 24);

			if (mouseX >= leftPos + 148 && mouseX <= leftPos + 160 && mouseY >= topPos + 92 && mouseY <= topPos + 110) {
				Component warning;

				if (be.rewardLimitedTransactions == 0)
					warning = noRewardText;
				else
					warning = Utils.localize("gui.securitycraft:secure_trading_station.not_enough_reward", be.rewardLimitedTransactions);

				guiGraphics.renderComponentTooltip(font, List.of(warning), mouseX, mouseY);
			}
		}

		if (payButton.active && mouseX >= leftPos + 112 && mouseX <= leftPos + 161 && mouseY >= topPos + 43 && mouseY <= topPos + 58) {
			List<Component> buttonTooltip = new ArrayList<>();
			int transactions = getTransactionsOnConfirmation();

			if (transactions == 1)
				buttonTooltip.add(confirmText);
			else
				buttonTooltip.add(Utils.localize("gui.securitycraft:secure_trading_station.confirm_multiple", transactions));

			if (skipPaymentCheck)
				buttonTooltip.add(allowedText);

			guiGraphics.renderComponentTooltip(font, buttonTooltip, mouseX, mouseY);
		}

		renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		super.renderLabels(guiGraphics, mouseX, mouseY);
		guiGraphics.drawString(font, paymentText, 15, 25, 0xFF404040, false);
		guiGraphics.drawString(font, rewardText, 15, 65, 0xFF404040, false);

		if (storageVisible)
			guiGraphics.drawString(font, storedItemsText, 15, 80, 0xFF404040, false);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
		guiGraphics.blit(isOwner ? (menu.withStorageAccess ? GUI_TEXTURE_OWNER_STORAGE : GUI_TEXTURE_OWNER) : GUI_TEXTURE, leftPos, topPos, 0.0F, 0.0F, imageWidth, imageHeight, 256, 256);
	}

	private void sendTransactionRequest(Button button) {
		PacketDistributor.sendToServer(new RequestSecureTradingStationTransactions(be.getBlockPos(), getTransactionsOnConfirmation()));

		if (transactionAmountBox != null)
			transactionAmountBox.setValue("");
	}

	private int getTransactionsOnConfirmation() {
		int requestedTransactions = Math.max(1, this.requestedTransactions);

		return skipPaymentCheck ? requestedTransactions : Math.min(menu.paymentLimitedTransactions, requestedTransactions);
	}
}
