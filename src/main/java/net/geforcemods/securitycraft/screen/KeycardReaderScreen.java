package net.geforcemods.securitycraft.screen;

import java.util.Collections;

import org.apache.commons.lang3.StringUtils;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.KeycardReaderBlockEntity;
import net.geforcemods.securitycraft.inventory.KeycardReaderMenu;
import net.geforcemods.securitycraft.items.KeycardItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.server.SetKeycardUses;
import net.geforcemods.securitycraft.network.server.SyncKeycardSettings;
import net.geforcemods.securitycraft.screen.components.ActiveBasedTextureButton;
import net.geforcemods.securitycraft.screen.components.HintEditBox;
import net.geforcemods.securitycraft.screen.components.TextHoverChecker;
import net.geforcemods.securitycraft.screen.components.TogglePictureButton;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.util.InputMappings.Input;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

@OnlyIn(Dist.CLIENT)
public class KeycardReaderScreen extends ContainerScreen<KeycardReaderMenu> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/keycard_reader.png");
	private static final ResourceLocation BEACON_GUI = new ResourceLocation("textures/gui/container/beacon.png");
	private static final ResourceLocation RANDOM_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/random.png");
	private static final ResourceLocation RANDOM_INACTIVE_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/random_inactive.png");
	private static final ResourceLocation RESET_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/reset.png");
	private static final ResourceLocation RESET_INACTIVE_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/reset_inactive.png");
	private static final ResourceLocation RETURN_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/return.png");
	private static final ResourceLocation RETURN_INACTIVE_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/return_inactive.png");
	private static final ResourceLocation WORLD_SELECTION_ICONS = new ResourceLocation("textures/gui/world_selection.png");
	private static final ITextComponent EQUALS = new StringTextComponent("=");
	private static final ITextComponent GREATER_THAN_EQUALS = new StringTextComponent(">=");
	private static final int MAX_SIGNATURE = 99999;
	private final ITextComponent signatureText = Utils.localize("gui.securitycraft:keycard_reader.signature");
	private final ITextComponent keycardLevelsText = Utils.localize("gui.securitycraft:keycard_reader.keycard_levels");
	private final ITextComponent linkText = Utils.localize("gui.securitycraft:keycard_reader.link");
	private final ITextComponent levelMismatchInfo = Utils.localize("gui.securitycraft:keycard_reader.level_mismatch");
	private final ITextComponent limitedInfo = Utils.localize("tooltip.securitycraft:keycard.limited_info");
	private ITextComponent smartModuleTooltip;
	private final KeycardReaderBlockEntity be;
	private final boolean hasSmartModule;
	private final boolean isOwner;
	private boolean isExactLevel = true;
	private int previousSignature;
	private int signature;
	private boolean[] acceptedLevels;
	private int signatureTextLength;
	private int signatureTextStartX;
	private Button minusThree, minusTwo, minusOne, reset, plusOne, plusTwo, plusThree;
	private TogglePictureButton[] toggleButtons = new TogglePictureButton[5];
	private TextFieldWidget signatureTextField, usesTextField;
	private HintEditBox usableByTextField;
	private TextHoverChecker usesHoverChecker, randomizeHoverChecker, usableByHoverChecker;
	private Button setUsesButton;
	private Button linkButton;
	//fixes link and set uses buttons being on for a split second when opening the container
	private boolean firstTick = true;

	public KeycardReaderScreen(KeycardReaderMenu menu, PlayerInventory inv, ITextComponent title) {
		super(menu, inv, title);

		be = menu.be;
		previousSignature = MathHelper.clamp(be.getSignature(), 0, MAX_SIGNATURE);
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

			toggleButtons[i] = addButton(new TogglePictureButton(leftPos + 100, topPos + 50 + (i + 1) * 17, 15, 15, BEACON_GUI, new int[] {110, 88}, new int[] {219, 219}, -1, 17, 17, 21, 22, 256, 256, 2, thisButton -> {
				//TogglePictureButton already implicitly handles changing the button state in the case of isSmart, so only the data needs to be updated
				if (!hasSmartModule) {
					for (int otherButtonId = 0; otherButtonId < 5; otherButtonId++) {
						boolean active;

						if (isExactLevel)
							active = (otherButtonId == thisButtonId);
						else
							active = (otherButtonId >= thisButtonId);

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

		signatureTextLength = font.width(signatureText);
		signatureTextStartX = imageWidth / 2 - signatureTextLength + 5;
		signatureTextField = addButton(new TextFieldWidget(font, leftPos + 96, topPos + 21, 40, 12, StringTextComponent.EMPTY));
		signatureTextField.setValue(leftPaddedSignature());
		signatureTextField.setFilter(s -> s.matches("\\d*"));
		signatureTextField.setMaxLength(5);
		signatureTextField.setResponder(this::changeSignature);
		minusThree = addButton(new ExtendedButton(leftPos + 22, buttonY, 24, buttonHeight, new StringTextComponent("---"), b -> changeSignature(signature - 100)));
		minusTwo = addButton(new ExtendedButton(leftPos + 48, buttonY, 18, buttonHeight, new StringTextComponent("--"), b -> changeSignature(signature - 10)));
		minusOne = addButton(new ExtendedButton(leftPos + 68, buttonY, 12, buttonHeight, new StringTextComponent("-"), b -> changeSignature(signature - 1)));
		reset = addButton(new ActiveBasedTextureButton(leftPos + 82, buttonY, 12, buttonHeight, RESET_TEXTURE, RESET_INACTIVE_TEXTURE, 10, 10, 1, 2, 10, 10, 10, 10, b -> changeSignature(previousSignature)));
		plusOne = addButton(new ExtendedButton(leftPos + 96, buttonY, 12, buttonHeight, new StringTextComponent("+"), b -> changeSignature(signature + 1)));
		plusTwo = addButton(new ExtendedButton(leftPos + 110, buttonY, 18, buttonHeight, new StringTextComponent("++"), b -> changeSignature(signature + 10)));
		plusThree = addButton(new ExtendedButton(leftPos + 130, buttonY, 24, buttonHeight, new StringTextComponent("+++"), b -> changeSignature(signature + 100)));
		randomizeButton = addButton(new ActiveBasedTextureButton(leftPos + 156, buttonY, 12, buttonHeight, RANDOM_TEXTURE, RANDOM_INACTIVE_TEXTURE, 10, 10, 1, 2, 10, 10, 10, 10, b -> changeSignature(minecraft.level.random.nextInt(MAX_SIGNATURE))));
		randomizeButton.active = isOwner;
		//set correct signature
		changeSignature(signature);
		//link button
		linkButton = addButton(new ExtendedButton(leftPos + 8, topPos + 126, 70, 20, linkText, b -> {
			previousSignature = signature;
			changeSignature(signature);
			SecurityCraft.channel.sendToServer(new SyncKeycardSettings(be.getBlockPos(), acceptedLevels, signature, true, usableByTextField.getValue()));

			if (menu.keycardSlot.getItem().getHoverName().getString().equalsIgnoreCase("Zelda"))
				minecraft.getSoundManager().play(SimpleSound.forUI(SCSounds.GET_ITEM.event, 1.0F, 1.25F));
		}));
		linkButton.active = false;
		//text field for setting the player the keycard can be used by
		usableByTextField = addButton(new HintEditBox(font, leftPos + 8, topPos + 66, 70, 15, StringTextComponent.EMPTY));
		usableByTextField.setHint(Utils.localize("gui.securitycraft:keycard_reader.usable_by.hint"));
		usableByTextField.setMaxLength(16);
		//button for saving the amount of limited uses onto the keycard
		setUsesButton = addButton(new ActiveBasedTextureButton(leftPos + 62, topPos + 106, 16, 17, RETURN_TEXTURE, RETURN_INACTIVE_TEXTURE, 14, 14, 2, 2, 14, 14, 14, 14, b -> SecurityCraft.channel.sendToServer(new SetKeycardUses(be.getBlockPos(), Integer.parseInt(usesTextField.getValue())))));
		setUsesButton.active = false;
		//text field for setting amount of limited uses
		usesTextField = addButton(new TextFieldWidget(font, leftPos + 28, topPos + 107, 30, 15, StringTextComponent.EMPTY));
		usesTextField.setFilter(s -> s.matches("\\d*"));
		usesTextField.setMaxLength(3);
		//info text when hovering over text field
		usesHoverChecker = new TextHoverChecker(topPos + 107, topPos + 122, leftPos + 28, leftPos + 58, limitedInfo);
		randomizeHoverChecker = new TextHoverChecker(randomizeButton, Utils.localize("gui.securitycraft:keycard_reader.randomize_signature"));
		usableByHoverChecker = new TextHoverChecker(usableByTextField, Utils.localize("gui.securitycraft:keycard_reader.usable_by.tooltip"));

		//add =/>= button and handle it being set to the correct state, as well as changing keycard level buttons' states if a smart module was removed
		if (!hasSmartModule) {
			if (activeButtons == 1)
				isExactLevel = true;
			else if (activeButtons == 0) { //probably won't happen but just in case
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

			addButton(new ExtendedButton(leftPos + 135, topPos + 67, 18, 18, isExactLevel ? EQUALS : GREATER_THAN_EQUALS, b -> {
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
			})).active = isOwner;
		}
	}

	@Override
	protected void renderLabels(MatrixStack pose, int mouseX, int mouseY) {
		font.draw(pose, title, imageWidth / 2 - font.width(title) / 2, 6, 4210752);
		font.draw(pose, signatureText, signatureTextStartX, 23, 4210752);
		font.draw(pose, keycardLevelsText, 170 - font.width(keycardLevelsText), 56, 4210752);

		//numbers infront of keycard levels buttons
		for (int i = 1; i <= 5; i++) {
			font.draw(pose, "" + i, 91, 55 + 17 * i, 4210752);
		}

		font.draw(pose, Utils.INVENTORY_TEXT, 8, imageHeight - 93, 4210752);
	}

	@Override
	public void tick() {
		super.tick();

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
	public void render(MatrixStack pose, int mouseX, int mouseY, float partialTicks) {
		super.render(pose, mouseX, mouseY, partialTicks);

		ItemStack stack = menu.keycardSlot.getItem();

		//if the level of the keycard currently in the slot is not enabled in the keycard reader, show a warning
		if (!stack.isEmpty() && !acceptedLevels[((KeycardItem) stack.getItem()).getLevel()]) {
			int left = leftPos + 18;
			int top = topPos + 82;

			minecraft.getTextureManager().bind(WORLD_SELECTION_ICONS);
			blit(pose, left + 5, top + 4, 16, 16, 70, 37, 22, 22, 256, 256);

			if (mouseX >= left && mouseX <= left + 12 && mouseY >= top && mouseY <= top + 22)
				GuiUtils.drawHoveringText(pose, Collections.singletonList(levelMismatchInfo), mouseX, mouseY, width, height, -1, font);
		}

		if (!usesTextField.active && !stack.isEmpty() && usesHoverChecker.checkHover(mouseX, mouseY))
			GuiUtils.drawHoveringText(pose, usesHoverChecker.getLines(), mouseX, mouseY, width, height, -1, font);

		if (randomizeHoverChecker.checkHover(mouseX, mouseY))
			renderComponentTooltip(pose, randomizeHoverChecker.getLines(), mouseX, mouseY);

		if (usableByHoverChecker.checkHover(mouseX, mouseY))
			renderComponentTooltip(pose, usableByHoverChecker.getLines(), mouseX, mouseY);

		renderTooltip(pose, mouseX, mouseY);
		ClientUtils.renderModuleInfo(pose, ModuleType.SMART, smartModuleTooltip, hasSmartModule, leftPos + 5, topPos + 5, width, height, mouseX, mouseY);
	}

	@Override
	protected void renderBg(MatrixStack pose, float partialTicks, int mouseX, int mouseY) {
		renderBackground(pose);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(TEXTURE);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		if (isOwner && mouseX >= leftPos + signatureTextStartX && mouseY >= topPos + 23 && mouseX <= leftPos + signatureTextStartX + signatureTextLength + 42 && mouseY <= topPos + 43)
			changeSignature(signature + (int) Math.signum(delta));

		return super.mouseScrolled(mouseX, mouseY, delta);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (usableByTextField.isFocused()) {
			Input key = InputMappings.getKey(keyCode, scanCode);

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
			SecurityCraft.channel.sendToServer(new SyncKeycardSettings(be.getBlockPos(), acceptedLevels, signature, false, usableByTextField.getValue()));
		}
	}

	public void changeSignature(int newSignature) {
		changeSignature(newSignature, false);
	}

	public void changeSignature(String newSignature) {
		if (newSignature != null && !newSignature.isEmpty())
			changeSignature(Integer.parseInt(newSignature), true);
	}

	public void changeSignature(int newSignature, boolean throughTextField) {
		boolean enablePlusButtons;
		boolean enableMinusButtons;

		if (isOwner)
			signature = MathHelper.clamp(newSignature, 0, MAX_SIGNATURE); //keep between 0 and the max allowed (disallow negative numbers)

		enablePlusButtons = isOwner && signature != MAX_SIGNATURE;
		enableMinusButtons = isOwner && signature != 0;
		minusThree.active = enableMinusButtons;
		minusTwo.active = enableMinusButtons;
		minusOne.active = enableMinusButtons;
		reset.active = isOwner && signature != previousSignature;
		plusOne.active = enablePlusButtons;
		plusTwo.active = enablePlusButtons;
		plusThree.active = enablePlusButtons;

		if (!throughTextField) {
			String textFieldValue = leftPaddedSignature();

			if (!signatureTextField.getValue().equals(textFieldValue))
				signatureTextField.setValue(textFieldValue);
		}
	}

	public void changeLevelState(int i, boolean active) {
		if (isOwner) {
			toggleButtons[i].setCurrentIndex(active ? 1 : 0);
			acceptedLevels[i] = active;
		}
	}

	private String leftPaddedSignature() {
		return StringUtils.leftPad("" + signature, 5, "0");
	}
}
