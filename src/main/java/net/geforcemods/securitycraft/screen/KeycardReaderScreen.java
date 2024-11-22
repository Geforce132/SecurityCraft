package net.geforcemods.securitycraft.screen;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.KeycardReaderBlockEntity;
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
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class KeycardReaderScreen extends AbstractContainerScreen<KeycardReaderMenu> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/keycard_reader.png");
	private static final ResourceLocation BEACON_GUI = new ResourceLocation("textures/gui/container/beacon.png");
	private static final ResourceLocation RANDOM_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/random.png");
	private static final ResourceLocation RANDOM_INACTIVE_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/random_inactive.png");
	private static final ResourceLocation RESET_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/reset.png");
	private static final ResourceLocation RESET_INACTIVE_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/reset_inactive.png");
	private static final ResourceLocation RETURN_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/return.png");
	private static final ResourceLocation RETURN_INACTIVE_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/return_inactive.png");
	private static final ResourceLocation WORLD_SELECTION_ICONS = new ResourceLocation("textures/gui/world_selection.png");
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

			toggleButtons[i] = addRenderableWidget(new TogglePictureButton(leftPos + 100, topPos + 50 + (i + 1) * 17, 15, 15, BEACON_GUI, new int[]{110, 88}, new int[]{219, 219}, -1, 17, 17, 21, 22, 256, 256, 2, thisButton -> {
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
			}));
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
		reset = addRenderableWidget(new ActiveBasedTextureButton(leftPos + 82, buttonY, 12, buttonHeight, RESET_TEXTURE, RESET_INACTIVE_TEXTURE, 10, 10, 1, 2, 10, 10, 10, 10, b -> changeSignature(previousSignature)));
		plusOne = addRenderableWidget(new Button(leftPos + 96, buttonY, 12, buttonHeight, Component.literal("+"), b -> changeSignature(signature + 1), Button.DEFAULT_NARRATION));
		plusTwo = addRenderableWidget(new Button(leftPos + 110, buttonY, 18, buttonHeight, Component.literal("++"), b -> changeSignature(signature + 10), Button.DEFAULT_NARRATION));
		plusThree = addRenderableWidget(new Button(leftPos + 130, buttonY, 24, buttonHeight, Component.literal("+++"), b -> changeSignature(signature + 100), Button.DEFAULT_NARRATION));
		randomizeButton = addRenderableWidget(new ActiveBasedTextureButton(leftPos + 156, buttonY, 12, buttonHeight, RANDOM_TEXTURE, RANDOM_INACTIVE_TEXTURE, 10, 10, 1, 2, 10, 10, 10, 10, b -> changeSignature(minecraft.level.random.nextInt(MAX_SIGNATURE))));
		randomizeButton.setTooltip(Tooltip.create(Utils.localize("gui.securitycraft:keycard_reader.randomize_signature")));
		randomizeButton.active = isOwner;
		//set correct signature
		changeSignature(signature);
		//link button
		linkButton = addRenderableWidget(new Button(leftPos + 8, topPos + 126, 70, 20, linkText, b -> {
			previousSignature = signature;
			changeSignature(signature);
			SecurityCraft.CHANNEL.sendToServer(new SyncKeycardSettings(be.getBlockPos(), acceptedLevels, signature, true, usableByTextField.getValue()));

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
		setUsesButton = addRenderableWidget(new ActiveBasedTextureButton(leftPos + 62, topPos + 106, 16, 17, RETURN_TEXTURE, RETURN_INACTIVE_TEXTURE, 14, 14, 2, 2, 14, 14, 14, 14, b -> SecurityCraft.CHANNEL.sendToServer(new SetKeycardUses(be.getBlockPos(), Integer.parseInt(usesTextField.getValue())))));
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
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		font.draw(pose, title, imageWidth / 2 - font.width(title) / 2, 6, 4210752);
		font.draw(pose, signatureText, imageWidth / 2 - font.width(signatureText) / 2, 23, 4210752);
		font.draw(pose, keycardLevelsText, 170 - font.width(keycardLevelsText), 56, 4210752);

		//numbers infront of keycard levels buttons
		for (int i = 1; i <= 5; i++) {
			font.draw(pose, "" + i, 91, 55 + 17 * i, 4210752);
		}

		font.draw(pose, Utils.INVENTORY_TEXT, 8, imageHeight - 93, 4210752);
	}

	@Override
	public void containerTick() {
		super.containerTick();

		ItemStack stack = menu.keycardSlot.getItem();
		boolean isEmpty = stack.isEmpty();
		boolean wasActive = usesTextField.active;
		boolean hasTag = stack.hasTag();
		boolean enabled = !isEmpty && hasTag && stack.getTag().getBoolean("limited");
		int cardSignature = hasTag ? stack.getTag().getInt("signature") : -1;
		String usableBy = hasTag ? stack.getTag().getString("usable_by") : "";

		usesTextField.setEditable(enabled);
		usesTextField.active = enabled;

		//set the text of the text field to the amount of uses on the keycard
		if (!wasActive && enabled)
			usesTextField.setValue("" + stack.getTag().getInt("uses"));
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
			setUsesButton.active = enabled && usesTextField.getValue() != null && !usesTextField.getValue().isEmpty() && !("" + stack.getTag().getInt("uses")).equals(usesTextField.getValue());
			linkButton.active = !isEmpty && (cardSignature != signature || !usableBy.equals(usableByTextField.getValue()));
		}
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		super.render(pose, mouseX, mouseY, partialTicks);

		ItemStack stack = menu.keycardSlot.getItem();

		//if the level of the keycard currently in the slot is not enabled in the keycard reader, show a warning
		if (!stack.isEmpty() && !acceptedLevels[((KeycardItem) stack.getItem()).getLevel()]) {
			int left = leftPos + 18;
			int top = topPos + 82;

			RenderSystem._setShaderTexture(0, WORLD_SELECTION_ICONS);
			blit(pose, left + 5, top + 4, 16, 16, 70, 37, 22, 22, 256, 256);

			if (mouseX >= left && mouseX <= left + 12 && mouseY >= top && mouseY <= top + 22)
				renderComponentTooltip(pose, Arrays.asList(levelMismatchInfo), mouseX, mouseY);
		}

		if (!usesTextField.active && !stack.isEmpty() && usesHoverChecker.checkHover(mouseX, mouseY))
			renderComponentTooltip(pose, usesHoverChecker.getLines(), mouseX, mouseY);

		renderTooltip(pose, mouseX, mouseY);
		ClientUtils.renderModuleInfo(pose, ModuleType.SMART, smartModuleTooltip, hasSmartModule, leftPos + 5, topPos + 5, mouseX, mouseY);
	}

	@Override
	protected void renderBg(PoseStack pose, float partialTicks, int mouseX, int mouseY) {
		renderBackground(pose);
		RenderSystem._setShaderTexture(0, TEXTURE);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		if (isOwner && mouseX >= leftPos + signatureTextStartX && mouseY >= topPos + 23 && mouseX <= leftPos + signatureTextStartX + signatureTextLength && mouseY <= topPos + 43)
			changeSignature(signature + (int) Math.signum(delta));

		return super.mouseScrolled(mouseX, mouseY, delta);
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
			SecurityCraft.CHANNEL.sendToServer(new SyncKeycardSettings(be.getBlockPos(), acceptedLevels, signature, false, usableByTextField.getValue()));
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
}
