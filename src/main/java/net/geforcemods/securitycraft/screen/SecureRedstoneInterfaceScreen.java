package net.geforcemods.securitycraft.screen;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.input.Keyboard;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.SecureRedstoneInterfaceBlockEntity;
import net.geforcemods.securitycraft.network.server.SyncSecureRedstoneInterface;
import net.geforcemods.securitycraft.screen.components.ClickButton;
import net.geforcemods.securitycraft.screen.components.PictureButton;
import net.geforcemods.securitycraft.screen.components.Slider;
import net.geforcemods.securitycraft.screen.components.StringHoverChecker;
import net.geforcemods.securitycraft.screen.components.ToggleComponentButton;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiPageButtonList.GuiResponder;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

public class SecureRedstoneInterfaceScreen extends GuiScreen {
	public static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/secure_redstone_interface.png");
	private static final ResourceLocation RANDOM_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/random.png");
	private static final ResourceLocation RANDOM_INACTIVE_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/random_inactive.png");
	private final String frequencyText = new TextComponentTranslation("gui.securitycraft:secure_redstone_interface.frequency").getFormattedText();
	private final SecureRedstoneInterfaceBlockEntity be;
	private final boolean oldSender, oldProtectedSignal, oldSendExactPower, oldReceiveInvertedPower, oldHighlightConnections;
	private final int oldFrequency, oldSenderRange;
	private final int xSize = 176, ySize = 188;
	private final String title;
	private int leftPos;
	private int topPos;
	private GuiTextField frequencyBox;
	private StringHoverChecker[] hoverCheckers = new StringHoverChecker[3];
	private boolean sync = true;

	public SecureRedstoneInterfaceScreen(SecureRedstoneInterfaceBlockEntity be) {
		this(be, be.isSender(), be.isProtectedSignal(), be.getFrequency(), be.sendsExactPower(), be.receivesInvertedPower(), be.getSenderRange(), be.shouldHighlightConnections());
	}

	private SecureRedstoneInterfaceScreen(SecureRedstoneInterfaceBlockEntity be, boolean oldSender, boolean oldProtectedSignal, int oldFrequency, boolean oldSendExactPower, boolean oldReceiveInvertedPower, int oldSenderRange, boolean oldHighlightConnections) {
		title = be.getDisplayName().getFormattedText();
		this.be = be;
		this.oldSender = oldSender;
		this.oldProtectedSignal = oldProtectedSignal;
		this.oldFrequency = oldFrequency;
		this.oldSendExactPower = oldSendExactPower;
		this.oldReceiveInvertedPower = oldReceiveInvertedPower;
		this.oldSenderRange = oldSenderRange;
		this.oldHighlightConnections = oldHighlightConnections;
	}

	@Override
	public void initGui() {
		int widgetWidth = 150, widgetHeight = 20;
		int id = 0, hoverCheckerId = 0;

		super.initGui();
		leftPos = (width - xSize) / 2;
		topPos = (height - ySize) / 2;

		String powerSettingKey = "gui.securitycraft:secure_redstone_interface." + (be.isSender() ? "send_exact_power" : "receive_inverted_power");
		int widgetX = leftPos + 13, widgetY = topPos + 38;
		GuiButton powerSettingButton, protectedSignalButton, highlightButton;
		GuiResponder frequencyBoxResponder = new GuiResponder() {
			@Override
			public void setEntryValue(int id, String s) {
				if (!s.isEmpty())
					be.setFrequency(Integer.parseInt(s));
				else
					be.setFrequency(0);
			}

			@Override
			public void setEntryValue(int id, float value) {}

			@Override
			public void setEntryValue(int id, boolean value) {}
		};

		frequencyBox = new GuiTextField(id++, fontRenderer, widgetX, widgetY, widgetWidth - 23, widgetHeight);
		frequencyBox.setEnableBackgroundDrawing(true);
		frequencyBox.setText(be.getFrequency() + "");
		frequencyBox.setMaxStringLength(9);
		frequencyBox.setValidator(s -> s.matches("\\d*")); //any amount of digits);
		frequencyBox.setGuiResponder(frequencyBoxResponder);
		addButton(new PictureButton(id++, widgetX + widgetWidth - 20, widgetY, 20, widgetHeight, RANDOM_INACTIVE_TEXTURE, 16, 16, 3, 3, 16, 16, 16, 16, b -> {
			String newText = "" + SecurityCraft.RANDOM.nextInt(999999999);

			frequencyBox.setText(newText);
			frequencyBoxResponder.setEntryValue(frequencyBox.getId(), newText);
		}) {
			@Override
			public ResourceLocation getTextureLocation() {
				return enabled ? RANDOM_TEXTURE : RANDOM_INACTIVE_TEXTURE;
			}
		});
		widgetY += 23;
		addButton(new ToggleComponentButton(id++, widgetX, widgetY, widgetWidth, widgetHeight, i -> formatButtonText("gui.securitycraft:secure_redstone_interface.mode", "gui.securitycraft:secure_redstone_interface.mode." + (i == 0 ? "sender" : "receiver")), initial(be.isSender()), 2, button -> {
			boolean isNowASender = !be.isSender();

			be.setSender(isNowASender);

			if (isNowASender)
				be.setReceiveInvertedPower(oldReceiveInvertedPower);
			else {
				be.setProtectedSignal(oldProtectedSignal);
				be.setSendExactPower(oldSendExactPower);
				be.setSenderRange(oldSenderRange);
			}

			sync = false;
			mc.displayGuiScreen(new SecureRedstoneInterfaceScreen(be, oldSender, oldProtectedSignal, oldFrequency, oldSendExactPower, oldReceiveInvertedPower, oldSenderRange, oldHighlightConnections));
		}));
		widgetY += 23;
		powerSettingButton = addButton(new ToggleComponentButton(id++, widgetX, widgetY, widgetWidth, widgetHeight, i -> formatButtonText(powerSettingKey, yesOrNo(i)), initial(be.isSender() ? be.sendsExactPower() : be.receivesInvertedPower()), 2, button -> {
			if (be.isSender())
				be.setSendExactPower(!be.sendsExactPower());
			else
				be.setReceiveInvertedPower(!be.receivesInvertedPower());
		}));
		hoverCheckers[hoverCheckerId++] = new StringHoverChecker(powerSettingButton, yesOrNoTooltip(powerSettingKey + ".tooltip"));

		if (be.isSender()) {
			widgetY += 23;
			protectedSignalButton = addButton(new ToggleComponentButton(id++, widgetX, widgetY, widgetWidth, widgetHeight, i -> formatButtonText("gui.securitycraft:secure_redstone_interface.protected_signal", yesOrNo(i)), initial(be.isProtectedSignal()), 2, button -> be.setProtectedSignal(!be.isProtectedSignal())));
			hoverCheckers[hoverCheckerId++] = new StringHoverChecker(protectedSignalButton, yesOrNoTooltip("gui.securitycraft:secure_redstone_interface.protected_signal.tooltip"));
			widgetY += 23;
			addButton(new Slider(Utils.localize("gui.securitycraft:projector.range", be.getSenderRange()).getFormattedText(), Utils.getLanguageKeyDenotation(SCContent.secureRedstoneInterface), id++, widgetX, widgetY, widgetWidth, widgetHeight, Utils.localize("gui.securitycraft:projector.range", "").getFormattedText(), 1, 64, be.getSenderRange(), true, new Slider.ISlider() {
				@Override
				public void onMouseRelease(int id) {}

				@Override
				public void onChangeSliderValue(Slider slider, String denotation, int id) {
					int value = slider.getValueInt();

					be.setSenderRange(value);
					slider.displayString = Utils.localize("gui.securitycraft:projector.range", value).getFormattedText();
				}
			}));
		}

		widgetY += 23;
		highlightButton = addButton(new ToggleComponentButton(id++, widgetX, widgetY, widgetWidth, widgetHeight, i -> formatButtonText("gui.securitycraft:secure_redstone_interface.highlight_connections", yesOrNo(i)), initial(be.shouldHighlightConnections()), 2, button -> be.setHighlightConnections(!be.shouldHighlightConnections())));
		hoverCheckers[hoverCheckerId] = new StringHoverChecker(highlightButton, Arrays.asList(Utils.localize("gui.securitycraft:secure_redstone_interface.highlight_connections.tooltip").getFormattedText().split("/n")));
		hoverCheckers[hoverCheckerId].singleTooltip();
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button instanceof ClickButton)
			((ClickButton) button).onClick();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		drawTexturedModalRect(leftPos, topPos, 0, 0, xSize, ySize);
		super.drawScreen(mouseX, mouseY, partialTicks);
		frequencyBox.drawTextBox();
		fontRenderer.drawString(frequencyText, frequencyBox.x, frequencyBox.y - fontRenderer.FONT_HEIGHT - 1, 0x404040);
		fontRenderer.drawString(title, width / 2 - fontRenderer.getStringWidth(title) / 2, topPos + 6, 0x404040);

		for (StringHoverChecker thc : hoverCheckers) {
			if (thc != null && thc.checkHover(mouseX, mouseY)) {
				if (thc.isSingleTooltip())
					drawHoveringText(thc.getLines(), mouseX, mouseY);
				else
					drawHoveringText(thc.getName(), mouseX, mouseY);

				break;
			}
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode != Keyboard.KEY_ESCAPE && frequencyBox.isFocused())
			frequencyBox.textboxKeyTyped(typedChar, keyCode);
		else
			super.keyTyped(typedChar, keyCode);

		if (mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode))
			mc.player.closeScreen();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		frequencyBox.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();

		if (sync) {
			boolean sender = be.isSender();
			boolean protectedSignal = be.isProtectedSignal();
			int frequency = be.getFrequency();
			boolean sendsExactPower = be.sendsExactPower();
			boolean receivesInvertedPower = be.receivesInvertedPower();
			int senderRange = be.getSenderRange();
			boolean highlightConnections = be.shouldHighlightConnections();

			if (sender != oldSender || protectedSignal != oldProtectedSignal || frequency != oldFrequency || sendsExactPower != oldSendExactPower || receivesInvertedPower != oldReceiveInvertedPower || senderRange != oldSenderRange || highlightConnections != oldHighlightConnections)
				SecurityCraft.network.sendToServer(new SyncSecureRedstoneInterface(be.getPos(), sender, protectedSignal, frequency, sendsExactPower, receivesInvertedPower, senderRange, highlightConnections));
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	private String formatButtonText(String message, String value) {
		return Utils.localize("securitycraft.generic_value", Utils.localize(message), Utils.localize(value)).getFormattedText();
	}

	private String yesOrNo(int index) {
		return "gui.securitycraft:invScan." + (index == 0 ? "yes" : "no");
	}

	private List<String> yesOrNoTooltip(String key) {
		return Arrays.asList(Utils.localize(key + ".true").getFormattedText(), Utils.localize(key + ".false").getFormattedText());
	}

	private int initial(boolean value) {
		return value ? 0 : 1;
	}
}
