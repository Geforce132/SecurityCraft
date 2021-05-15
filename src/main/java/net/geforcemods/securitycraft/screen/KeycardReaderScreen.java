package net.geforcemods.securitycraft.screen;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.KeycardReaderContainer;
import net.geforcemods.securitycraft.items.KeycardItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.server.SetKeycardUses;
import net.geforcemods.securitycraft.network.server.SyncKeycardSettings;
import net.geforcemods.securitycraft.screen.components.PictureButton;
import net.geforcemods.securitycraft.screen.components.TextHoverChecker;
import net.geforcemods.securitycraft.screen.components.TogglePictureButton;
import net.geforcemods.securitycraft.tileentity.KeycardReaderTileEntity;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

@OnlyIn(Dist.CLIENT)
public class KeycardReaderScreen extends ContainerScreen<KeycardReaderContainer>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/keycard_reader.png");
	private static final ResourceLocation CHECKMARK_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/item_bound.png");
	private static final ResourceLocation CROSS_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/item_not_bound.png");
	private static final ResourceLocation RESET_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/reset.png");
	private static final ResourceLocation RESET_INACTIVE_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/reset_inactive.png");
	private static final ResourceLocation RETURN_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/return.png");
	private static final ResourceLocation RETURN_INACTIVE_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/return_inactive.png");
	private static final ResourceLocation WORLD_SELECTION_ICONS = new ResourceLocation("textures/gui/world_selection.png");
	private static final ITextComponent EQUALS = new StringTextComponent("=");
	private static final ITextComponent GREATER_THAN_EQUALS = new StringTextComponent(">=");
	private final ITextComponent blockName = ClientUtils.localize(SCContent.KEYCARD_READER.get().getTranslationKey());
	private final ITextComponent inventoryText = ClientUtils.localize("container.inventory");
	private final ITextComponent keycardLevelsText = ClientUtils.localize("gui.securitycraft:keycard_reader.keycard_levels");
	private final ITextComponent linkText = ClientUtils.localize("gui.securitycraft:keycard_reader.link");
	private final ITextComponent noSmartModule = ClientUtils.localize("gui.securitycraft:keycard_reader.noSmartModule");
	private final ITextComponent smartModule = ClientUtils.localize("gui.securitycraft:keycard_reader.smartModule");
	private final ITextComponent levelMismatchInfo = ClientUtils.localize("gui.securitycraft:keycard_reader.level_mismatch");
	private final ITextComponent limitedInfo = new TranslationTextComponent("tooltip.securitycraft:keycard.limited_info");
	private final KeycardReaderTileEntity te;
	private final boolean isSmart;
	private final boolean isOwner;
	private boolean isExactLevel = true;
	private int previousSignature;
	private int signature;
	private boolean[] acceptedLevels;
	private TranslationTextComponent signatureText;
	private int signatureTextLength;
	private int signatureTextStartX;
	private Button minusThree, minusTwo, minusOne, reset, plusOne, plusTwo, plusThree;
	private TogglePictureButton[] toggleButtons = new TogglePictureButton[5];
	private TextFieldWidget usesTextField;
	private TextHoverChecker usesHoverChecker;
	private Button setUsesButton;

	public KeycardReaderScreen(KeycardReaderContainer container, PlayerInventory inv, ITextComponent name)
	{
		super(container, inv, name);

		te = container.te;
		previousSignature = te.getSignature();
		signature = previousSignature;
		acceptedLevels = te.getAcceptedLevels();
		isSmart = te.hasModule(ModuleType.SMART);
		isOwner = te.getOwner().isOwner(inv.player);
		ySize = 249;
	}

	@Override
	public void init()
	{
		super.init();

		int buttonHeight = 13;
		int buttonY = guiTop + 35;
		int activeButtons = 0;
		int firstActiveButton = -1;

		//keycard level buttons
		for(int i = 0; i < 5; i++)
		{
			toggleButtons[i] = addButton(new TogglePictureButton(i, guiLeft + 100, guiTop + 50 + (i + 1) * 17, 15, 15, CROSS_TEXTURE, new int[]{0, 0}, new int[]{0, 0}, 1, 13, 13, 13, 13, 2, thisButton -> {
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
			toggleButtons[i].active = isOwner;

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

		minusThree = addButton(new ExtendedButton(guiLeft + 22, buttonY, 24, buttonHeight, new StringTextComponent("---"), b -> changeSignature(signature - 100)));
		minusTwo = addButton(new ExtendedButton(guiLeft + 48, buttonY, 18, buttonHeight, new StringTextComponent("--"), b -> changeSignature(signature - 10)));
		minusOne = addButton(new ExtendedButton(guiLeft + 68, buttonY, 12, buttonHeight, new StringTextComponent("-"), b -> changeSignature(signature - 1)));
		reset = addButton(new PictureButton(-1, guiLeft + 82, buttonY, 12, buttonHeight, RESET_INACTIVE_TEXTURE, 10, 10, 1, 2, 10, 10, 10, 10, b -> changeSignature(previousSignature)) {
			@Override
			public ResourceLocation getTextureLocation()
			{
				return active ? RESET_TEXTURE : RESET_INACTIVE_TEXTURE;
			}
		});
		plusOne = addButton(new ExtendedButton(guiLeft + 96, buttonY, 12, buttonHeight, new StringTextComponent("+"), b -> changeSignature(signature + 1)));
		plusTwo = addButton(new ExtendedButton(guiLeft + 110, buttonY, 18, buttonHeight, new StringTextComponent("++"), b -> changeSignature(signature + 10)));
		plusThree = addButton(new ExtendedButton(guiLeft + 130, buttonY, 24, buttonHeight, new StringTextComponent("+++"), b -> changeSignature(signature + 100)));
		//set correct signature
		changeSignature(signature);
		//link button
		addButton(new ExtendedButton(guiLeft + 8, guiTop + 126, 70, 20, linkText, b -> {
			previousSignature = signature;
			changeSignature(signature);
			SecurityCraft.channel.sendToServer(new SyncKeycardSettings(te.getPos(), acceptedLevels, signature, true));

			if(container.keycardSlot.getStack().getDisplayName().getString().equalsIgnoreCase("Zelda"))
				minecraft.getSoundHandler().play(SimpleSound.master(SCSounds.GET_ITEM.event, 1.0F));
		}));
		//button for saving the amount of limited uses onto the keycard
		setUsesButton = addButton(new PictureButton(-1, guiLeft + 62, guiTop + 106, 16, 17, RETURN_TEXTURE, 14, 14, 2, 2, 14, 14, 14, 14, b -> SecurityCraft.channel.sendToServer(new SetKeycardUses(te.getPos(), Integer.parseInt(usesTextField.getText())))) {
			@Override
			public ResourceLocation getTextureLocation()
			{
				return active ? RETURN_TEXTURE : RETURN_INACTIVE_TEXTURE;
			}
		});
		//text field for setting amount of limited uses
		usesTextField = addButton(new TextFieldWidget(font, guiLeft + 28, guiTop + 107, 30, 15, StringTextComponent.EMPTY));
		usesTextField.setValidator(s -> s.matches("[0-9]*"));
		usesTextField.setMaxStringLength(3);
		//info text when hovering over text field
		usesHoverChecker = new TextHoverChecker(guiTop + 107, guiTop + 122, guiLeft + 28, guiLeft + 58, limitedInfo);

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

			addButton(new ExtendedButton(guiLeft + 135, guiTop + 67, 18, 18, isExactLevel ? EQUALS : GREATER_THAN_EQUALS, b -> {
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

				b.setMessage(isExactLevel ? EQUALS : GREATER_THAN_EQUALS);
			})).active = isOwner;
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrix, int mouseX, int mouseY)
	{
		font.drawText(matrix, blockName, xSize / 2 - font.getStringPropertyWidth(blockName) / 2, 6, 4210752);
		font.drawText(matrix, signatureText, xSize / 2 - font.getStringPropertyWidth(signatureText) / 2, 23, 4210752);
		font.drawText(matrix, keycardLevelsText, 170 - font.getStringPropertyWidth(keycardLevelsText), 56, 4210752);

		//numbers infront of keycard levels buttons
		for(int i = 1; i <= 5; i++)
		{
			font.drawString(matrix, "" + i, 91, 55 + 17 * i, 4210752);
		}

		font.drawText(matrix, inventoryText, 8, ySize - 93, 4210752);
	}

	@Override
	public void tick()
	{
		super.tick();

		ItemStack stack = container.keycardSlot.getStack();
		boolean wasActive = usesTextField.active;
		boolean enabled = !stack.isEmpty() && stack.hasTag() && stack.getTag().getBoolean("limited");

		usesTextField.setEnabled(enabled);
		usesTextField.active = enabled;

		//set the text of the text field to the amount of uses on the keycard
		if(!wasActive && enabled)
			usesTextField.setText("" + stack.getTag().getInt("uses"));
		else if(wasActive && !enabled)
			usesTextField.setText("");

		//set return button depending on whether a different amount of uses compared to the keycard in the slot can be set
		setUsesButton.active = enabled && !("" + stack.getTag().getInt("uses")).equals(usesTextField.getText());
	}

	@Override
	public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks)
	{
		super.render(matrix, mouseX, mouseY, partialTicks);

		ItemStack stack = container.keycardSlot.getStack();

		//if the level of the keycard currently in the slot is not enabled in the keycard reader, show a warning
		if(!stack.isEmpty() && !acceptedLevels[((KeycardItem)stack.getItem()).getLevel()])
		{
			int left = guiLeft + 40;
			int top = guiTop + 60;

			minecraft.getTextureManager().bindTexture(WORLD_SELECTION_ICONS);
			blit(matrix, left, top, 22, 22, 70, 37, 22, 22, 256, 256);

			if(mouseX >= left - 7 && mouseX < left + 13 && mouseY >= top && mouseY <= top + 22)
				GuiUtils.drawHoveringText(matrix, Arrays.asList(levelMismatchInfo), mouseX, mouseY, width, height, -1, font);
		}

		if(!usesTextField.active && !stack.isEmpty() && usesHoverChecker.checkHover(mouseX, mouseY))
			GuiUtils.drawHoveringText(matrix, usesHoverChecker.getLines(), mouseX, mouseY, width, height, -1, font);

		renderHoveredTooltip(matrix, mouseX, mouseY);
		ClientUtils.renderSmartModuleInfo(matrix, smartModule, noSmartModule, isSmart, guiLeft, guiTop, width, height, mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrix, float partialTicks, int mouseX, int mouseY)
	{
		renderBackground(matrix);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(TEXTURE);
		blit(matrix, (width - xSize) / 2, (height - ySize) / 2, 0, 0, xSize, ySize);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta)
	{
		if(isOwner && mouseX >= guiLeft + signatureTextStartX && mouseY >= guiTop + 23 && mouseX <= guiLeft + signatureTextStartX + signatureTextLength && mouseY <= guiTop + 43)
			changeSignature(signature + (int)Math.signum(delta));

		return super.mouseScrolled(mouseX, mouseY, delta);
	}

	@Override
	public void onClose()
	{
		super.onClose();

		if(isOwner)
		{
			//write new data to client te and send that data to the server, which verifies and updates it on its side
			te.setAcceptedLevels(acceptedLevels);
			te.setSignature(signature);
			SecurityCraft.channel.sendToServer(new SyncKeycardSettings(te.getPos(), acceptedLevels, signature, false));
		}
	}

	public void changeSignature(int newSignature)
	{
		boolean enablePlusButtons;
		boolean enableMinusButtons;

		if(isOwner)
			signature = Math.max(0, Math.min(newSignature, Short.MAX_VALUE)); //keep between 0 and 32767 (disallow negative numbers)

		signatureText = new TranslationTextComponent("gui.securitycraft:keycard_reader.signature", StringUtils.leftPad("" + signature, 5, "0"));
		signatureTextLength = font.getStringPropertyWidth(signatureText);
		signatureTextStartX = xSize / 2 - signatureTextLength / 2;

		enablePlusButtons = isOwner && signature != Short.MAX_VALUE;
		enableMinusButtons = isOwner && signature != 0;
		minusThree.active = enableMinusButtons;
		minusTwo.active = enableMinusButtons;
		minusOne.active = enableMinusButtons;
		reset.active = isOwner && signature != previousSignature;
		plusOne.active = enablePlusButtons;
		plusTwo.active = enablePlusButtons;
		plusThree.active = enablePlusButtons;
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
