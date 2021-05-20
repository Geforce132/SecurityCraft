package net.geforcemods.securitycraft.gui;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.ContainerKeycardReader;
import net.geforcemods.securitycraft.gui.components.ClickButton;
import net.geforcemods.securitycraft.gui.components.GuiPictureButton;
import net.geforcemods.securitycraft.gui.components.StringHoverChecker;
import net.geforcemods.securitycraft.gui.components.TogglePictureButton;
import net.geforcemods.securitycraft.items.ItemKeycard;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.server.SetKeycardUses;
import net.geforcemods.securitycraft.network.server.SyncKeycardSettings;
import net.geforcemods.securitycraft.tileentity.TileEntityKeycardReader;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.client.config.GuiUtils;

public class GuiKeycardReader extends GuiContainer
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/keycard_reader.png");
	private static final ResourceLocation CHECKMARK_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/item_bound.png");
	private static final ResourceLocation CROSS_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/item_not_bound.png");
	private static final ResourceLocation RESET_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/reset.png");
	private static final ResourceLocation RESET_INACTIVE_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/reset_inactive.png");
	private static final ResourceLocation RETURN_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/return.png");
	private static final ResourceLocation RETURN_INACTIVE_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/return_inactive.png");
	private static final ResourceLocation WORLD_SELECTION_ICONS = new ResourceLocation("textures/gui/world_selection.png");
	private static final String EQUALS = "=";
	private static final String GREATER_THAN_EQUALS = ">=";
	private final String blockName = Utils.localize(SCContent.keycardReader.getTranslationKey() + ".name").getFormattedText();
	private final String inventoryText = Utils.localize("container.inventory").getFormattedText();
	private final String keycardLevelsText = Utils.localize("gui.securitycraft:keycard_reader.keycard_levels").getFormattedText();
	private final String linkText = Utils.localize("gui.securitycraft:keycard_reader.link").getFormattedText();
	private final String noSmartModule = Utils.localize("gui.securitycraft:keycard_reader.noSmartModule").getFormattedText();
	private final String smartModule = Utils.localize("gui.securitycraft:keycard_reader.smartModule").getFormattedText();
	private final String levelMismatchInfo = Utils.localize("gui.securitycraft:keycard_reader.level_mismatch").getFormattedText();
	private final String limitedInfo = Utils.localize("tooltip.securitycraft:keycard.limited_info").getFormattedText();
	private final TileEntityKeycardReader te;
	private final boolean isSmart;
	private final boolean isOwner;
	private boolean isExactLevel = true;
	private int previousSignature;
	private int signature;
	private boolean[] acceptedLevels;
	private String signatureText;
	private int signatureTextLength;
	private int signatureTextStartX;
	private GuiButton minusThree, minusTwo, minusOne, reset, plusOne, plusTwo, plusThree;
	private TogglePictureButton[] toggleButtons = new TogglePictureButton[5];
	private GuiTextField usesTextField;
	private StringHoverChecker usesHoverChecker;
	private GuiButton setUsesButton;
	private GuiButton linkButton;
	//fixes link and set uses buttons being on for a split second when opening the container
	private boolean firstTick = true;
	private ContainerKeycardReader container;

	public GuiKeycardReader(InventoryPlayer inv, TileEntityKeycardReader tile)
	{
		super(new ContainerKeycardReader(inv, tile));

		container = (ContainerKeycardReader)inventorySlots;
		te = tile;
		previousSignature = te.getSignature();
		signature = previousSignature;
		acceptedLevels = te.getAcceptedLevels();
		isSmart = te.hasModule(EnumModuleType.SMART);
		isOwner = te.getOwner().isOwner(inv.player);
		ySize = 249;
	}

	@Override
	public void initGui()
	{
		super.initGui();

		int buttonHeight = 13;
		int buttonY = guiTop + 35;
		int activeButtons = 0;
		int firstActiveButton = -1;
		int id = 0;

		//keycard level buttons
		for(int i = 0; i < 5; i++)
		{
			toggleButtons[i] = addButton(new TogglePictureButton(id++, guiLeft + 100, guiTop + 50 + (i + 1) * 17, 15, 15, CROSS_TEXTURE, new int[]{0, 0}, new int[]{0, 0}, 1, 13, 13, 13, 13, 2, thisButton -> {
				//TogglePictureButton already implicitly handles changing the button state in the case of isSmart, so only the data needs to be updated
				if(!isSmart)
				{
					for(int otherButtonId = 0; otherButtonId < 5; otherButtonId++)
					{
						boolean active;

						if(isExactLevel)
							active = (otherButtonId == thisButton.id);
						else
							active = (otherButtonId >= thisButton.id);

						//update button state and data
						changeLevelState(otherButtonId, active);
					}
				}
				else
					acceptedLevels[thisButton.id] = !acceptedLevels[thisButton.id];
			}) {
				@Override
				public ResourceLocation getTextureLocation()
				{
					return getCurrentIndex() == 0 ? CROSS_TEXTURE : CHECKMARK_TEXTURE;
				}
			});
			toggleButtons[i].setCurrentIndex(acceptedLevels[i] ? 1 : 0); //set correct button state
			toggleButtons[i].enabled = isOwner;

			if(!isSmart)
			{
				if(acceptedLevels[i])
				{
					if(firstActiveButton == -1)
						firstActiveButton = i;

					activeButtons++;
				}
			}
		}

		minusThree = addButton(new ClickButton(id++, guiLeft + 22, buttonY, 24, buttonHeight, "---", b -> changeSignature(signature - 100)));
		minusTwo = addButton(new ClickButton(id++, guiLeft + 48, buttonY, 18, buttonHeight, "--", b -> changeSignature(signature - 10)));
		minusOne = addButton(new ClickButton(id++, guiLeft + 68, buttonY, 12, buttonHeight, "-", b -> changeSignature(signature - 1)));
		reset = addButton(new GuiPictureButton(id++, guiLeft + 82, buttonY, 12, buttonHeight, RESET_INACTIVE_TEXTURE, 10, 10, 1, 2, 10, 10, 10, 10, b -> changeSignature(previousSignature)) {
			@Override
			public ResourceLocation getTextureLocation()
			{
				return enabled ? RESET_TEXTURE : RESET_INACTIVE_TEXTURE;
			}
		});
		plusOne = addButton(new ClickButton(id++, guiLeft + 96, buttonY, 12, buttonHeight, "+", b -> changeSignature(signature + 1)));
		plusTwo = addButton(new ClickButton(id++, guiLeft + 110, buttonY, 18, buttonHeight, "++", b -> changeSignature(signature + 10)));
		plusThree = addButton(new ClickButton(id++, guiLeft + 130, buttonY, 24, buttonHeight, "+++", b -> changeSignature(signature + 100)));
		//set correct signature
		changeSignature(signature);
		//link button
		linkButton = addButton(new ClickButton(id++, guiLeft + 8, guiTop + 126, 70, 20, linkText, b -> {
			previousSignature = signature;
			changeSignature(signature);
			SecurityCraft.network.sendToServer(new SyncKeycardSettings(te.getPos(), acceptedLevels, signature, true));

			if(container.keycardSlot.getStack().getDisplayName().equalsIgnoreCase("Zelda"))
				mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SCSounds.GET_ITEM.event, 1.25F));
		}));
		linkButton.enabled = false;
		//button for saving the amount of limited uses onto the keycard
		setUsesButton = addButton(new GuiPictureButton(id++, guiLeft + 62, guiTop + 106, 16, 17, RETURN_TEXTURE, 14, 14, 2, 2, 14, 14, 14, 14, b -> SecurityCraft.network.sendToServer(new SetKeycardUses(te.getPos(), Integer.parseInt(usesTextField.getText())))) {
			@Override
			public ResourceLocation getTextureLocation()
			{
				return enabled ? RETURN_TEXTURE : RETURN_INACTIVE_TEXTURE;
			}
		});
		setUsesButton.enabled = false;
		Keyboard.enableRepeatEvents(true);
		//text field for setting amount of limited uses
		usesTextField = new GuiTextField(id++, fontRenderer, guiLeft + 28, guiTop + 107, 30, 15);
		usesTextField.setTextColor(-1);
		usesTextField.setDisabledTextColour(-1);
		usesTextField.setEnableBackgroundDrawing(true);
		usesTextField.setValidator(s -> s.matches("[0-9]*"));
		usesTextField.setMaxStringLength(3);
		//info text when hovering over text field
		usesHoverChecker = new StringHoverChecker(guiTop + 107, guiTop + 122, guiLeft + 28, guiLeft + 58, limitedInfo);

		//add =/>= button and handle it being set to the correct state, as well as changing keycard level buttons' states if a smart module was removed
		if(!isSmart)
		{
			if(activeButtons == 1)
				isExactLevel = true;
			else if(activeButtons == 0) //probably won't happen but just in case
			{
				isExactLevel = true;
				changeLevelState(0, true);
			}
			else
			{
				boolean active = false;

				isExactLevel = false;

				//set all buttons prior to the first active button to false, and >= firstActiveButton to true
				for(int i = 0; i < 5; i++)
				{
					if(i == firstActiveButton)
						active = true;

					changeLevelState(i, active);
				}
			}

			addButton(new ClickButton(id++, guiLeft + 135, guiTop + 67, 18, 18, isExactLevel ? EQUALS : GREATER_THAN_EQUALS, b -> {
				boolean change = false;

				isExactLevel = !isExactLevel;

				//change keycard level buttons' states based on the =/>= button's state
				for(int i = 0; i < 5; i++)
				{
					if(change)
						changeLevelState(i, !isExactLevel);
					else
						change = acceptedLevels[i];
				}

				b.displayString = isExactLevel ? EQUALS : GREATER_THAN_EQUALS;
			})).enabled = isOwner;
		}
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if(button instanceof ClickButton)
			((ClickButton)button).onClick();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		fontRenderer.drawString(blockName, xSize / 2 - fontRenderer.getStringWidth(blockName) / 2, 6, 4210752);
		fontRenderer.drawString(signatureText, xSize / 2 - fontRenderer.getStringWidth(signatureText) / 2, 23, 4210752);
		fontRenderer.drawString(keycardLevelsText, 170 - fontRenderer.getStringWidth(keycardLevelsText), 56, 4210752);

		//numbers infront of keycard levels buttons
		for(int i = 1; i <= 5; i++)
		{
			fontRenderer.drawString("" + i, 91, 55 + 17 * i, 4210752);
		}

		fontRenderer.drawString(inventoryText, 8, ySize - 93, 4210752);
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();

		ItemStack stack = container.keycardSlot.getStack();
		boolean isEmpty = stack.isEmpty();
		boolean wasActive = usesTextField.isEnabled;
		boolean hasTag = stack.hasTagCompound();
		boolean enabled = !isEmpty && hasTag && stack.getTagCompound().getBoolean("limited");
		int cardSignature = hasTag ? stack.getTagCompound().getInteger("signature") : -1;

		usesTextField.setEnabled(enabled);

		//set the text of the text field to the amount of uses on the keycard
		if(!wasActive && enabled)
			usesTextField.setText("" + stack.getTagCompound().getInteger("uses"));
		else if(wasActive && !enabled)
			usesTextField.setText("");

		//fixes the buttons being active for a brief moment right after opening the screen
		if(firstTick)
		{
			setUsesButton.enabled = false;
			linkButton.enabled = false;
			firstTick = false;
		}
		else
		{
			//set return button depending on whether a different amount of uses compared to the keycard in the slot can be set
			setUsesButton.enabled = enabled && usesTextField.getText() != null && !usesTextField.getText().isEmpty() && !("" + stack.getTagCompound().getInteger("uses")).equals(usesTextField.getText());
			linkButton.enabled = !isEmpty && cardSignature != signature;
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		super.drawScreen(mouseX, mouseY, partialTicks);

		usesTextField.drawTextBox();
		GlStateManager.color(1.0F, 1.0F, 1.0F);

		ItemStack stack = container.keycardSlot.getStack();

		//if the level of the keycard currently in the slot is not enabled in the keycard reader, show a warning
		if(!stack.isEmpty() && !acceptedLevels[((ItemKeycard)stack.getItem()).getLevel()])
		{
			int left = guiLeft + 40;
			int top = guiTop + 60;

			mc.getTextureManager().bindTexture(WORLD_SELECTION_ICONS);
			drawScaledCustomSizeModalRect(left, top, 70, 37, 22, 22, 22, 22, 256, 256);

			if(mouseX >= left - 7 && mouseX < left + 13 && mouseY >= top && mouseY <= top + 22)
				GuiUtils.drawHoveringText(Arrays.asList(levelMismatchInfo), mouseX, mouseY, width, height, -1, fontRenderer);
		}

		if(!usesTextField.isEnabled && !stack.isEmpty() && usesHoverChecker.checkHover(mouseX, mouseY))
			GuiUtils.drawHoveringText(usesHoverChecker.getLines(), mouseX, mouseY, width, height, -1, fontRenderer);

		renderHoveredToolTip(mouseX, mouseY);
		net.geforcemods.securitycraft.util.GuiUtils.renderSmartModuleInfo(smartModule, noSmartModule, isSmart, guiLeft, guiTop, width, height, mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		drawTexturedModalRect((width - xSize) / 2, (height - ySize) / 2, 0, 0, xSize, ySize);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException
	{
		if(keyCode != Keyboard.KEY_ESCAPE && usesTextField.isFocused())
			usesTextField.textboxKeyTyped(typedChar, keyCode);
		else
			super.keyTyped(typedChar, keyCode);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		super.mouseClicked(mouseX, mouseY, mouseButton);
		usesTextField.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void handleMouseInput() throws IOException
	{
		super.handleMouseInput();

		if(isOwner)
		{
			int mouseX = Mouse.getEventX() * width / mc.displayWidth;
			int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;

			if(mouseX >= guiLeft + signatureTextStartX && mouseY >= guiTop + 23 && mouseX <= guiLeft + signatureTextStartX + signatureTextLength && mouseY <= guiTop + 43)
				changeSignature(signature + (int)Math.signum(Mouse.getEventDWheel()));
		}
	}

	@Override
	public void onGuiClosed()
	{
		super.onGuiClosed();

		if(isOwner)
		{
			//write new data to client te and send that data to the server, which verifies and updates it on its side
			te.setAcceptedLevels(acceptedLevels);
			te.setSignature(signature);
			SecurityCraft.network.sendToServer(new SyncKeycardSettings(te.getPos(), acceptedLevels, signature, false));
		}
	}

	public void changeSignature(int newSignature)
	{
		boolean enablePlusButtons;
		boolean enableMinusButtons;

		if(isOwner)
			signature = Math.max(0, Math.min(newSignature, Short.MAX_VALUE)); //keep between 0 and 32767 (disallow negative numbers)

		signatureText = new TextComponentTranslation("gui.securitycraft:keycard_reader.signature", StringUtils.leftPad("" + signature, 5, "0")).getFormattedText();
		signatureTextLength = fontRenderer.getStringWidth(signatureText);
		signatureTextStartX = xSize / 2 - signatureTextLength / 2;

		enablePlusButtons = isOwner && signature != Short.MAX_VALUE;
		enableMinusButtons = isOwner && signature != 0;
		minusThree.enabled = enableMinusButtons;
		minusTwo.enabled = enableMinusButtons;
		minusOne.enabled = enableMinusButtons;
		reset.enabled = isOwner && signature != previousSignature;
		plusOne.enabled = enablePlusButtons;
		plusTwo.enabled = enablePlusButtons;
		plusThree.enabled = enablePlusButtons;
	}

	public void changeLevelState(int i, boolean active)
	{
		if(isOwner)
		{
			toggleButtons[i].setCurrentIndex(active ? 1 : 0);
			acceptedLevels[i] = active;
		}
	}
}
