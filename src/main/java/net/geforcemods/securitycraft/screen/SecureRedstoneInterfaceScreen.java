package net.geforcemods.securitycraft.screen;

import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.SecureRedstoneInterfaceBlockEntity;
import net.geforcemods.securitycraft.network.server.SyncSecureRedstoneInterface;
import net.geforcemods.securitycraft.screen.components.ActiveBasedTextureButton;
import net.geforcemods.securitycraft.screen.components.NamedSlider;
import net.geforcemods.securitycraft.screen.components.TextHoverChecker;
import net.geforcemods.securitycraft.screen.components.ToggleComponentButton;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class SecureRedstoneInterfaceScreen extends Screen {
	public static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/secure_redstone_interface.png");
	private static final ResourceLocation RANDOM_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/random.png");
	private static final ResourceLocation RANDOM_INACTIVE_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/random_inactive.png");
	private final ITextComponent frequencyText = new TranslationTextComponent("gui.securitycraft:secure_redstone_interface.frequency");
	private final SecureRedstoneInterfaceBlockEntity be;
	private final boolean oldSender, oldProtectedSignal, oldSendExactPower, oldReceiveInvertedPower, oldHighlightConnections;
	private final int oldFrequency, oldSenderRange;
	private final int xSize = 176, ySize = 188;
	private int leftPos;
	private int topPos;
	private TextFieldWidget frequencyBox;
	private TextHoverChecker[] hoverCheckers = new TextHoverChecker[3];

	public SecureRedstoneInterfaceScreen(SecureRedstoneInterfaceBlockEntity be) {
		this(be, be.isSender(), be.isProtectedSignal(), be.getFrequency(), be.sendsExactPower(), be.receivesInvertedPower(), be.getSenderRange(), be.shouldHighlightConnections());
	}

	private SecureRedstoneInterfaceScreen(SecureRedstoneInterfaceBlockEntity be, boolean oldSender, boolean oldProtectedSignal, int oldFrequency, boolean oldSendExactPower, boolean oldReceiveInvertedPower, int oldSenderRange, boolean oldHighlightConnections) {
		super(be.getDisplayName());
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
	protected void init() {
		int widgetWidth = 150, widgetHeight = 20;
		int hoverCheckerId = 0;

		super.init();
		leftPos = (width - xSize) / 2;
		topPos = (height - ySize) / 2;

		String powerSettingKey = "gui.securitycraft:secure_redstone_interface." + (be.isSender() ? "send_exact_power" : "receive_inverted_power");
		int widgetX = leftPos + 13, widgetY = topPos + 38;
		Button powerSettingButton, protectedSignalButton, highlightButton;

		frequencyBox = new TextFieldWidget(font, widgetX, widgetY, widgetWidth - 23, widgetHeight, frequencyText);
		frequencyBox.setValue(be.getFrequency() + "");
		frequencyBox.setMaxLength(9);
		frequencyBox.setFilter(s -> s.matches("\\d*")); //any amount of digits);
		frequencyBox.setResponder(s -> {
			if (!s.isEmpty())
				be.setFrequency(Integer.parseInt(s));
			else
				be.setFrequency(0);
		});
		addButton(frequencyBox);
		addButton(new ActiveBasedTextureButton(widgetX + widgetWidth - 20, widgetY, 20, widgetHeight, RANDOM_TEXTURE, RANDOM_INACTIVE_TEXTURE, 16, 16, 3, 3, 16, 16, 16, 16, b -> frequencyBox.setValue("" + SecurityCraft.RANDOM.nextInt(999999999))));
		widgetY += 23;
		addButton(new ToggleComponentButton(widgetX, widgetY, widgetWidth, widgetHeight, i -> formatButtonText("gui.securitycraft:secure_redstone_interface.mode", "gui.securitycraft:secure_redstone_interface.mode." + (i == 0 ? "sender" : "receiver")), initial(be.isSender()), 2, button -> {
			boolean isNowASender = !be.isSender();

			be.setSender(isNowASender);

			if (isNowASender)
				be.setReceiveInvertedPower(oldReceiveInvertedPower);
			else {
				be.setProtectedSignal(oldProtectedSignal);
				be.setSendExactPower(oldSendExactPower);
				be.setSenderRange(oldSenderRange);
			}

			minecraft.setScreen(new SecureRedstoneInterfaceScreen(be, oldSender, oldProtectedSignal, oldFrequency, oldSendExactPower, oldReceiveInvertedPower, oldSenderRange, oldHighlightConnections));
		}));
		widgetY += 23;
		powerSettingButton = addButton(new ToggleComponentButton(widgetX, widgetY, widgetWidth, widgetHeight, i -> formatButtonText(powerSettingKey, yesOrNo(i)), initial(be.isSender() ? be.sendsExactPower() : be.receivesInvertedPower()), 2, button -> {
			if (be.isSender())
				be.setSendExactPower(!be.sendsExactPower());
			else
				be.setReceiveInvertedPower(!be.receivesInvertedPower());
		}));
		hoverCheckers[hoverCheckerId++] = new TextHoverChecker(powerSettingButton, yesOrNoTooltip(powerSettingKey + ".tooltip"));

		if (be.isSender()) {
			widgetY += 23;
			protectedSignalButton = addButton(new ToggleComponentButton(widgetX, widgetY, widgetWidth, widgetHeight, i -> formatButtonText("gui.securitycraft:secure_redstone_interface.protected_signal", yesOrNo(i)), initial(be.isProtectedSignal()), 2, button -> be.setProtectedSignal(!be.isProtectedSignal())));
			hoverCheckers[hoverCheckerId++] = new TextHoverChecker(protectedSignalButton, yesOrNoTooltip("gui.securitycraft:secure_redstone_interface.protected_signal.tooltip"));
			widgetY += 23;
			addButton(new NamedSlider(Utils.localize("gui.securitycraft:projector.range", be.getSenderRange()), Utils.getLanguageKeyDenotation(SCContent.SECURE_REDSTONE_INTERFACE.get()), widgetX, widgetY, widgetWidth, widgetHeight, Utils.localize("gui.securitycraft:projector.range", ""), "", 1, 64, be.getSenderRange(), false, true, null, slider -> be.setSenderRange(slider.getValueInt()))).setFGColor(0xE0E0E0);
		}

		widgetY += 23;
		highlightButton = addButton(new ToggleComponentButton(widgetX, widgetY, widgetWidth, widgetHeight, i -> formatButtonText("gui.securitycraft:secure_redstone_interface.highlight_connections", yesOrNo(i)), initial(be.shouldHighlightConnections()), 2, button -> be.setHighlightConnections(!be.shouldHighlightConnections())));
		hoverCheckers[hoverCheckerId++] = new TextHoverChecker(highlightButton, Utils.localize("gui.securitycraft:secure_redstone_interface.highlight_connections.tooltip"));
	}

	@Override
	public void render(MatrixStack pose, int mouseX, int mouseY, float partialTicks) {
		renderBackground(pose);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(TEXTURE);
		blit(pose, leftPos, topPos, 0, 0, xSize, ySize);
		super.render(pose, mouseX, mouseY, partialTicks);
		font.draw(pose, frequencyText, frequencyBox.x, frequencyBox.y - font.lineHeight - 1, 0x404040);
		font.draw(pose, title, width / 2 - font.width(title) / 2, topPos + 6, 0x404040);

		for (TextHoverChecker thc : hoverCheckers) {
			if (thc != null && thc.checkHover(mouseX, mouseY)) {
				renderTooltip(pose, thc.getName(), mouseX, mouseY);
				break;
			}
		}
	}

	@Override
	public void onClose() {
		super.onClose();

		boolean sender = be.isSender();
		boolean protectedSignal = be.isProtectedSignal();
		int frequency = be.getFrequency();
		boolean sendsExactPower = be.sendsExactPower();
		boolean receivesInvertedPower = be.receivesInvertedPower();
		int senderRange = be.getSenderRange();
		boolean highlightConnections = be.shouldHighlightConnections();

		if (sender != oldSender || protectedSignal != oldProtectedSignal || frequency != oldFrequency || sendsExactPower != oldSendExactPower || receivesInvertedPower != oldReceiveInvertedPower || senderRange != oldSenderRange || highlightConnections != oldHighlightConnections)
			SecurityCraft.channel.sendToServer(new SyncSecureRedstoneInterface(be.getBlockPos(), sender, protectedSignal, frequency, sendsExactPower, receivesInvertedPower, senderRange, highlightConnections));
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private ITextComponent formatButtonText(String message, String value) {
		return Utils.localize("options.generic_value", Utils.localize(message), Utils.localize(value));
	}

	private String yesOrNo(int index) {
		return "gui.securitycraft:invScan." + (index == 0 ? "yes" : "no");
	}

	private List<ITextComponent> yesOrNoTooltip(String key) {
		return Arrays.asList(Utils.localize(key + ".true"), Utils.localize(key + ".false"));
	}

	private int initial(boolean value) {
		return value ? 0 : 1;
	}
}
