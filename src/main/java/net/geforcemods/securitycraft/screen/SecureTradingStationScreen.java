package net.geforcemods.securitycraft.screen;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.SecureTradingStationBlockEntity;
import net.geforcemods.securitycraft.blocks.SecureTradingStationBlock;
import net.geforcemods.securitycraft.inventory.SecureTradingStationMenu;
import net.geforcemods.securitycraft.network.server.RequestSecureTradingStationTransactions;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class SecureTradingStationScreen extends AbstractContainerScreen<SecureTradingStationMenu> {
	private static final Identifier GUI_TEXTURE = SecurityCraft.resLoc("textures/gui/container/secure_trading_station.png");
	private static final Identifier GUI_TEXTURE_OWNER = SecurityCraft.resLoc("textures/gui/container/secure_trading_station_owner.png");
	private static final Identifier GUI_TEXTURE_STORAGE = SecurityCraft.resLoc("textures/gui/container/secure_trading_station_storage.png");
	private static final Identifier GUI_TEXTURE_OWNER_STORAGE = SecurityCraft.resLoc("textures/gui/container/secure_trading_station_owner_storage.png");
	private static final Identifier WARNING_HIGHLIGHTED_SPRITE = SecurityCraft.mcResLoc("world_list/warning_highlighted");
	private final Component paymentText = Utils.localize("gui.securitycraft:secure_trading_station.payment");
	private final Component rewardText = Utils.localize("gui.securitycraft:secure_trading_station.reward");
	private final Component rewardActivateText = Utils.localize("gui.securitycraft:secure_trading_station.reward_activate_redstone");
	private final Component rewardToggleText = Utils.localize("gui.securitycraft:secure_trading_station.reward_toggle_redstone");
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
		boolean storageVisible = menu.be.hasRewardReferenceStacks() || menu.withStorageAccess;
		int imageHeight = storageVisible ? (menu.withStorageAccess ? 249 : 208) : 184;
		super(menu, playerInventory, title, 176, imageHeight);

		be = menu.be;
		isOwner = be.isOwnedBy(playerInventory.player);
		skipPaymentCheck = isOwner || be.isAllowed(playerInventory.player);
		this.storageVisible = storageVisible;
		inventoryLabelY = imageHeight - 94;
	}

	@Override
	protected void init() {
		super.init();

		payButton = addRenderableWidget(Button.builder(payButtonText, this::sendTransactionRequest).bounds(leftPos + 112, topPos + 42, 50, 16).build());

		if ((be.hasPaymentReferenceStacks() && !skipPaymentCheck) || (be.getSignalLength() != 0 && be.getBlockState().getValue(SecureTradingStationBlock.POWERED)))
			payButton.active = false; //Preliminary checks for empty container

		if (storageVisible) {
			transactionAmountBox = addRenderableWidget(new EditBox(font, leftPos + 118, topPos + 20, 26, 16, Component.empty()));
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

		if (be.getSignalLength() != 0 && be.getBlockState().getValue(SecureTradingStationBlock.POWERED))
			payButton.active = false;
		else
			payButton.active = getTransactionsOnConfirmation() > 0;
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);

		if (be.hasRewardReferenceStacks() && getTransactionsOnConfirmation() > be.rewardLimitedTransactions) {
			guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, WARNING_HIGHLIGHTED_SPRITE, leftPos + 148, topPos + 16, 24, 24);

			if (mouseX >= leftPos + 148 && mouseX <= leftPos + 160 && mouseY >= topPos + 16 && mouseY <= topPos + 40) {
				Component warning;

				if (be.rewardLimitedTransactions == 0)
					warning = noRewardText;
				else
					warning = Utils.localize("gui.securitycraft:secure_trading_station.not_enough_reward", be.rewardLimitedTransactions);

				guiGraphics.setComponentTooltipForNextFrame(font, List.of(warning), mouseX, mouseY);
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

			guiGraphics.setComponentTooltipForNextFrame(font, buttonTooltip, mouseX, mouseY);
		}

		extractTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
		super.extractLabels(guiGraphics, mouseX, mouseY);
		guiGraphics.text(font, paymentText, 12, 24, 0xFF404040, false);
		guiGraphics.text(font, rewardText, 12, 64, 0xFF404040, false);
		guiGraphics.text(font, be.getSignalLength() == 0 ? rewardToggleText : rewardActivateText, 12, 76, 0xFF404040, false);

		if (storageVisible)
			guiGraphics.text(font, storedItemsText, 12, 96, 0xFF404040, false);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.extractBackground(guiGraphics, mouseX, mouseY, partialTick);

		Identifier backgroundTexture;

		if (isOwner)
			backgroundTexture = storageVisible ? GUI_TEXTURE_OWNER_STORAGE : GUI_TEXTURE_OWNER;
		else
			backgroundTexture = storageVisible ? GUI_TEXTURE_STORAGE : GUI_TEXTURE;

		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, backgroundTexture, leftPos, topPos, 0.0F, 0.0F, imageWidth, imageHeight, 256, 256);
	}

	private void sendTransactionRequest(Button button) {
		ClientPacketDistributor.sendToServer(new RequestSecureTradingStationTransactions(be.getBlockPos(), getTransactionsOnConfirmation()));

		if (transactionAmountBox != null)
			transactionAmountBox.setValue("");
	}

	private int getTransactionsOnConfirmation() {
		int requestedTransactions = Math.max(1, this.requestedTransactions);

		return skipPaymentCheck ? requestedTransactions : Math.min(menu.paymentLimitedTransactions, requestedTransactions);
	}
}
