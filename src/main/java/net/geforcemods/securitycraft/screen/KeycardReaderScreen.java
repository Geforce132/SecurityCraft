package net.geforcemods.securitycraft.screen;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.KeycardReaderBlockEntity;
import net.geforcemods.securitycraft.components.KeycardData;
import net.geforcemods.securitycraft.inventory.KeycardReaderMenu;
import net.geforcemods.securitycraft.items.KeycardItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.server.SetKeycardUses;
import net.geforcemods.securitycraft.network.server.SyncKeycardSettings;
import net.geforcemods.securitycraft.screen.components.ActiveBasedTextureButton;
import net.geforcemods.securitycraft.screen.components.TextHoverChecker;
import net.geforcemods.securitycraft.screen.components.TogglePictureButton;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class KeycardReaderScreen extends AbstractContainerScreen<KeycardReaderMenu> {
	private static final ResourceLocation TEXTURE = SecurityCraft.resLoc("textures/gui/container/keycard_reader.png");
	private static final ResourceLocation CONFIRM_SPRITE = SecurityCraft.mcResLoc("container/beacon/confirm");
	private static final ResourceLocation CANCEL_SPRITE = SecurityCraft.mcResLoc("container/beacon/cancel");
	private static final ResourceLocation RANDOM_SPRITE = SecurityCraft.resLoc("widget/random");
	private static final ResourceLocation RANDOM_INACTIVE_SPRITE = SecurityCraft.resLoc("widget/random_inactive");
	private static final ResourceLocation RESET_SPRITE = SecurityCraft.resLoc("widget/reset");
	private static final ResourceLocation RESET_INACTIVE_SPRITE = SecurityCraft.resLoc("widget/reset_inactive");
	private static final ResourceLocation RETURN_SPRITE = SecurityCraft.resLoc("widget/return");
	private static final ResourceLocation RETURN_INACTIVE_SPRITE = SecurityCraft.resLoc("widget/return_inactive");
	private static final ResourceLocation WARNING_HIGHLIGHTED_SPRITE = SecurityCraft.mcResLoc("world_list/warning_highlighted");
	private static final Component EQUALS = Component.literal("=");
	private static final Component GREATER_THAN_EQUALS = Component.literal(">=");
	private static final int MAX_SIGNATURE = 99999;
	private final Component keycardLevelsText = Utils.localize("gui.securitycraft:keycard_reader.keycard_levels");
	private final Component linkText = Utils.localize("gui.securitycraft:keycard_reader.link");
	private final Component levelMismatchInfo = Utils.localize("gui.securitycraft:keycard_reader.level_mismatch");
	private final Component limitedInfo = Utils.localize("tooltip.securitycraft:keycard.limited_info");
	private Component smartModuleTooltip;
	private final KeycardReaderBlockEntity be;
	private final boolean hasSmartModule;
	private final boolean isOwner;
	private boolean isExactLevel = true;
	private int previousSignature;
	private int signature;
	private boolean[] acceptedLevels;
	private Component signatureText;
	private int signatureTextLength;
	private int signatureTextStartX;
	private Button minusThree, minusTwo, minusOne, reset, plusOne, plusTwo, plusThree;
	private TogglePictureButton[] toggleButtons = new TogglePictureButton[5];
	private EditBox usesTextField, usableByTextField;
	private TextHoverChecker usesHoverChecker;
	private Button setUsesButton;
	private Button linkButton;
	//fixes link and set uses buttons being on for a split second when opening the container
	private boolean firstTick = true;

	public KeycardReaderScreen(KeycardReaderMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);

		be = menu.be;
		previousSignature = Mth.clamp(be.getSignature(), 0, MAX_SIGNATURE);
		signature = previousSignature;
		acceptedLevels = be.getAcceptedLevels();
		hasSmartModule = be.isModuleEnabled(ModuleType.SMART);
		isOwner = be.isOwnedBy(inv.player);
		imageHeight = 249;

		if (hasSmartModule)
			smartModuleTooltip = Utils.localize("gui.securitycraft:keycard_reader.smartModule");
		else
			smartModuleTooltip = Utils.localize("gui.securitycraft:keycard_reader.noSmartModule");
	}

	@Override
	public void init() {
		super.init();

		int buttonHeight = 13;
		int buttonY = topPos + 35;
		int activeButtons = 0;
		int firstActiveButton = -1;
		Button randomizeButton;

		//keycard level buttons
		for (int i = 0; i < 5; i++) {
			final int thisButtonId = i;

			toggleButtons[i] = addRenderableWidget(new TogglePictureButton(leftPos + 100, topPos + 50 + (i + 1) * 17, 15, 15, 0, 15, 15, 2, thisButton -> {
				//TogglePictureButton already implicitly handles changing the button state in the case of isSmart, so only the data needs to be updated
				if (!hasSmartModule) {
					for (int otherButtonId = 0; otherButtonId < 5; otherButtonId++) {
						boolean active;

						if (isExactLevel)
							active = otherButtonId == thisButtonId;
						else
							active = otherButtonId >= thisButtonId;

						//update button state and data
						changeLevelState(otherButtonId, active);
					}
				}
				else
					acceptedLevels[thisButtonId] = !acceptedLevels[thisButtonId];
			}, CANCEL_SPRITE, CONFIRM_SPRITE));
			toggleButtons[i].setCurrentIndex(acceptedLevels[i] ? 1 : 0); //set correct button state
			toggleButtons[i].active = isOwner;

			if (!hasSmartModule && acceptedLevels[i]) {
				if (firstActiveButton == -1)
					firstActiveButton = i;

				activeButtons++;
			}
		}

		minusThree = addRenderableWidget(new Button(leftPos + 22, buttonY, 24, buttonHeight, Component.literal("---"), b -> changeSignature(signature - 100), Button.DEFAULT_NARRATION));
		minusTwo = addRenderableWidget(new Button(leftPos + 48, buttonY, 18, buttonHeight, Component.literal("--"), b -> changeSignature(signature - 10), Button.DEFAULT_NARRATION));
		minusOne = addRenderableWidget(new Button(leftPos + 68, buttonY, 12, buttonHeight, Component.literal("-"), b -> changeSignature(signature - 1), Button.DEFAULT_NARRATION));
		reset = addRenderableWidget(new ActiveBasedTextureButton(leftPos + 82, buttonY, 12, buttonHeight, RESET_SPRITE, RESET_INACTIVE_SPRITE, 1, 2, 10, 10, b -> changeSignature(previousSignature)));
		plusOne = addRenderableWidget(new Button(leftPos + 96, buttonY, 12, buttonHeight, Component.literal("+"), b -> changeSignature(signature + 1), Button.DEFAULT_NARRATION));
		plusTwo = addRenderableWidget(new Button(leftPos + 110, buttonY, 18, buttonHeight, Component.literal("++"), b -> changeSignature(signature + 10), Button.DEFAULT_NARRATION));
		plusThree = addRenderableWidget(new Button(leftPos + 130, buttonY, 24, buttonHeight, Component.literal("+++"), b -> changeSignature(signature + 100), Button.DEFAULT_NARRATION));
		randomizeButton = addRenderableWidget(new ActiveBasedTextureButton(leftPos + 156, buttonY, 12, buttonHeight, RANDOM_SPRITE, RANDOM_INACTIVE_SPRITE, 1, 2, 10, 10, b -> changeSignature(SecurityCraft.RANDOM.nextInt(MAX_SIGNATURE))));
		randomizeButton.setTooltip(Tooltip.create(Utils.localize("gui.securitycraft:keycard_reader.randomize_signature")));
		randomizeButton.active = isOwner;
		//set correct signature
		changeSignature(signature);
		//link button
		linkButton = addRenderableWidget(new Button(leftPos + 8, topPos + 126, 70, 20, linkText, b -> {
			previousSignature = signature;
			changeSignature(signature);
			PacketDistributor.sendToServer(new SyncKeycardSettings(be.getBlockPos(), acceptedLevels, signature, true, getUsableBy()));

			if (menu.keycardSlot.getItem().getHoverName().getString().equalsIgnoreCase("Zelda"))
				minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SCSounds.GET_ITEM.event, 1.0F, 1.25F));
		}, Button.DEFAULT_NARRATION));
		linkButton.active = false;
		//text field for setting the player the keycard can be used by
		usableByTextField = addRenderableWidget(new EditBox(font, leftPos + 8, topPos + 66, 70, 15, Component.empty()));
		usableByTextField.setTooltip(Tooltip.create(Utils.localize("gui.securitycraft:keycard_reader.usable_by.tooltip")));
		usableByTextField.setHint(Utils.localize("gui.securitycraft:keycard_reader.usable_by.hint"));
		usableByTextField.setMaxLength(16);
		//button for saving the amount of limited uses onto the keycard
		setUsesButton = addRenderableWidget(new ActiveBasedTextureButton(leftPos + 62, topPos + 106, 16, 17, RETURN_SPRITE, RETURN_INACTIVE_SPRITE, 2, 2, 14, 14, b -> PacketDistributor.sendToServer(new SetKeycardUses(be.getBlockPos(), Integer.parseInt(usesTextField.getValue())))));
		setUsesButton.active = false;
		//text field for setting amount of limited uses
		usesTextField = addRenderableWidget(new EditBox(font, leftPos + 28, topPos + 107, 30, 15, Component.empty()));
		usesTextField.setFilter(s -> s.matches("\\d*"));
		usesTextField.setMaxLength(3);
		//info text when hovering over text field
		usesHoverChecker = new TextHoverChecker(topPos + 107, topPos + 122, leftPos + 28, leftPos + 58, limitedInfo);

		//add =/>= button and handle it being set to the correct state, as well as changing keycard level buttons' states if a smart module was removed
		if (!hasSmartModule) {
			if (activeButtons == 1)
				isExactLevel = true;
			else if (activeButtons == 0) {//probably won't happen but just in case
				isExactLevel = true;
				changeLevelState(0, true);
			}
			else {
				boolean active = false;

				isExactLevel = false;

				//set all buttons prior to the first active button to false, and >= firstActiveButton to true
				for (int i = 0; i < 5; i++) {
					if (i == firstActiveButton)
						active = true;

					changeLevelState(i, active);
				}
			}

			addRenderableWidget(new Button(leftPos + 135, topPos + 67, 18, 18, isExactLevel ? EQUALS : GREATER_THAN_EQUALS, b -> {
				boolean change = false;

				isExactLevel = !isExactLevel;

				//change keycard level buttons' states based on the =/>= button's state
				for (int i = 0; i < 5; i++) {
					if (change)
						changeLevelState(i, !isExactLevel);
					else
						change = acceptedLevels[i];
				}

				b.setMessage(isExactLevel ? EQUALS : GREATER_THAN_EQUALS);
			}, Button.DEFAULT_NARRATION)).active = isOwner;
		}
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(font, title, imageWidth / 2 - font.width(title) / 2, 6, 4210752, false);
		guiGraphics.drawString(font, signatureText, imageWidth / 2 - font.width(signatureText) / 2, 23, 4210752, false);
		guiGraphics.drawString(font, keycardLevelsText, 170 - font.width(keycardLevelsText), 56, 4210752, false);

		//numbers infront of keycard levels buttons
		for (int i = 1; i <= 5; i++) {
			guiGraphics.drawString(font, "" + i, 91, 55 + 17 * i, 4210752, false);
		}

		guiGraphics.drawString(font, Utils.INVENTORY_TEXT, 8, imageHeight - 93, 4210752, false);
	}

	@Override
	public void containerTick() {
		super.containerTick();

		ItemStack stack = menu.keycardSlot.getItem();
		boolean isEmpty = stack.isEmpty();
		boolean wasActive = usesTextField.active;
		KeycardData keycardData = stack.get(SCContent.KEYCARD_DATA);
		boolean hasData = keycardData != null;
		boolean enabled = !isEmpty && hasData && keycardData.limited();
		int cardSignature = hasData ? keycardData.signature() : -1;

		usesTextField.setEditable(enabled);
		usesTextField.active = enabled;

		//set the text of the text field to the amount of uses on the keycard
		if (!wasActive && enabled)
			usesTextField.setValue("" + keycardData.usesLeft());
		else if (wasActive && !enabled)
			usesTextField.setValue("");

		//fixes the buttons being active for a brief moment right after opening the screen
		if (firstTick) {
			setUsesButton.active = false;
			linkButton.active = false;
			firstTick = false;
		}
		else {
			//set return button depending on whether a different amount of uses compared to the keycard in the slot can be set
			setUsesButton.active = enabled && usesTextField.getValue() != null && !usesTextField.getValue().isEmpty() && !("" + keycardData.usesLeft()).equals(usesTextField.getValue());
			linkButton.active = !isEmpty && (cardSignature != signature || !keycardData.usableBy().orElse("").equals(usableByTextField.getValue()));
		}
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);

		ItemStack stack = menu.keycardSlot.getItem();

		//if the level of the keycard currently in the slot is not enabled in the keycard reader, show a warning
		if (!stack.isEmpty() && !acceptedLevels[((KeycardItem) stack.getItem()).getLevel()]) {
			int left = leftPos + 18;
			int top = topPos + 82;

			guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, WARNING_HIGHLIGHTED_SPRITE, left, top, 24, 24);

			if (mouseX >= left && mouseX <= left + 12 && mouseY >= top && mouseY <= top + 22)
				guiGraphics.setTooltipForNextFrame(font, levelMismatchInfo, mouseX, mouseY); //TODO: works?
		}

		if (!usesTextField.active && !stack.isEmpty() && usesHoverChecker.checkHover(mouseX, mouseY))
			guiGraphics.setComponentTooltipForNextFrame(font, usesHoverChecker.getLines(), mouseX, mouseY); //TODO: works?

		renderTooltip(guiGraphics, mouseX, mouseY);
		ClientUtils.renderModuleInfo(guiGraphics, font, ModuleType.SMART, smartModuleTooltip, hasSmartModule, leftPos + 5, topPos + 5, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0.0F, 0.0F, imageWidth, imageHeight, 256, 256);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (minecraft.player.isSpectator())
			return false;

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		if (isOwner && mouseX >= leftPos + signatureTextStartX && mouseY >= topPos + 23 && mouseX <= leftPos + signatureTextStartX + signatureTextLength && mouseY <= topPos + 43)
			changeSignature(signature + (int) Math.signum(scrollY));

		return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (usableByTextField.isFocused()) {
			Key key = InputConstants.getKey(keyCode, scanCode);

			if (minecraft.options.keyInventory.isActiveAndMatches(key) || minecraft.options.keySwapOffhand.isActiveAndMatches(key) || minecraft.options.keyPickItem.isActiveAndMatches(key))
				return false;
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void removed() {
		super.removed();

		if (isOwner) {
			//write new data to client te and send that data to the server, which verifies and updates it on its side
			be.setAcceptedLevels(acceptedLevels);
			be.setSignature(signature);
			PacketDistributor.sendToServer(new SyncKeycardSettings(be.getBlockPos(), acceptedLevels, signature, false, getUsableBy()));
		}
	}

	public void changeSignature(int newSignature) {
		boolean enablePlusButtons;
		boolean enableMinusButtons;

		if (isOwner)
			signature = Mth.clamp(newSignature, 0, MAX_SIGNATURE); //keep between 0 and the max allowed (disallow negative numbers)

		signatureText = Component.translatable("gui.securitycraft:keycard_reader.signature", StringUtils.leftPad("" + signature, 5, "0"));
		signatureTextLength = font.width(signatureText);
		signatureTextStartX = imageWidth / 2 - signatureTextLength / 2;
		enablePlusButtons = isOwner && signature != MAX_SIGNATURE;
		enableMinusButtons = isOwner && signature != 0;
		minusThree.active = enableMinusButtons;
		minusTwo.active = enableMinusButtons;
		minusOne.active = enableMinusButtons;
		reset.active = isOwner && signature != previousSignature;
		plusOne.active = enablePlusButtons;
		plusTwo.active = enablePlusButtons;
		plusThree.active = enablePlusButtons;
	}

	public void changeLevelState(int i, boolean active) {
		if (isOwner)
			toggleButtons[i].setCurrentIndex(active ? 1 : 0);

		acceptedLevels[i] = active;
	}

	public Optional<String> getUsableBy() {
		String message = usableByTextField.getValue();

		if (message == null || message.isBlank())
			return Optional.empty();
		else
			return Optional.of(message);
	}
}
