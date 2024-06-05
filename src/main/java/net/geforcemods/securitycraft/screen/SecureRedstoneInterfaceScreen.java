package net.geforcemods.securitycraft.screen;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.SecureRedstoneInterfaceBlockEntity;
import net.geforcemods.securitycraft.network.server.SyncSecureRedstoneInterface;
import net.geforcemods.securitycraft.screen.components.CallbackSlider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.LinearLayout.Orientation;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;

public class SecureRedstoneInterfaceScreen extends Screen {
	public static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/blank.png");
	private final Component frequencyText = Component.translatable("gui.securitycraft:secure_redstone_interface.frequency");
	private final SecureRedstoneInterfaceBlockEntity be;
	private final boolean oldSender, oldProtectedSignal, oldSendExactPower, oldReceiveInvertedPower;
	private final int oldFrequency, oldSenderRange;
	private final int xSize = 176, ySize = 166;
	private int leftPos;
	private int topPos;
	private EditBox frequencyBox;

	public SecureRedstoneInterfaceScreen(SecureRedstoneInterfaceBlockEntity be) {
		this(be, be.isSender(), be.isProtectedSignal(), be.getFrequency(), be.sendsExactPower(), be.receivesInvertedPower(), be.getSenderRange());
	}

	private SecureRedstoneInterfaceScreen(SecureRedstoneInterfaceBlockEntity be, boolean oldSender, boolean oldProtectedSignal, int oldFrequency, boolean oldSendExactPower, boolean oldReceiveInvertedPower, int oldSenderRange) {
		super(be.getDisplayName());
		this.be = be;
		this.oldSender = oldSender;
		this.oldProtectedSignal = oldProtectedSignal;
		this.oldFrequency = oldFrequency;
		this.oldSendExactPower = oldSendExactPower;
		this.oldReceiveInvertedPower = oldReceiveInvertedPower;
		this.oldSenderRange = oldSenderRange;
	}

	@Override
	protected void init() {
		int widgetWidth = 150, widgetHeight = 20;

		super.init();
		leftPos = (width - xSize) / 2;
		topPos = (height - ySize) / 2;

		LinearLayout layout = new LinearLayout(0, 0, Orientation.VERTICAL).spacing(3);
		String powerSettingKey = "gui.securitycraft:secure_redstone_interface." + (be.isSender() ? "send_exact_power" : "receive_inverted_power");

		frequencyBox = new EditBox(font, widgetWidth, widgetHeight, frequencyText);
		frequencyBox.setValue(be.getFrequency() + "");
		frequencyBox.setMaxLength(9);
		frequencyBox.setFilter(s -> s.matches("\\d*")); //any amount of digits);
		frequencyBox.setResponder(s -> {
			if (!s.isEmpty())
				be.setFrequency(Integer.parseInt(s));
			else
				be.setFrequency(0);
		});
		layout.addChild(addRenderableWidget(frequencyBox));
		//@formatter:off
		layout.addChild(addRenderableWidget(
				CycleButton.<Boolean>builder(value -> Component.translatable("gui.securitycraft:secure_redstone_interface.mode." + (value ? "sender" : "receiver")))
				.withValues(true, false)
				.withInitialValue(be.isSender())
				.create(0, 0, widgetWidth, widgetHeight, Component.translatable("gui.securitycraft:secure_redstone_interface.mode"), (button, isNowASender) -> {
					be.setSender(isNowASender);

					if (isNowASender)
						be.setReceiveInvertedPower(oldReceiveInvertedPower);
					else {
						be.setProtectedSignal(oldProtectedSignal);
						be.setSendExactPower(oldSendExactPower);
						be.setSenderRange(oldSenderRange);
					}

					minecraft.setScreen(new SecureRedstoneInterfaceScreen(be, oldSender, oldProtectedSignal, oldFrequency, oldSendExactPower, oldReceiveInvertedPower, oldSenderRange));
				})));
		layout.addChild(addRenderableWidget(
				CycleButton.<Boolean>builder(value -> Component.translatable("gui.securitycraft:invScan." + (value ? "yes" : "no")))
				.withValues(true, false)
				.withInitialValue(be.isSender() ? be.sendsExactPower() : be.receivesInvertedPower())
				.withTooltip(value -> Tooltip.create(Component.translatable(powerSettingKey + ".tooltip." + value)))
				.create(0, 0, widgetWidth, widgetHeight, Component.translatable(powerSettingKey), (button, newValue) -> {
					if (be.isSender())
						be.setSendExactPower(newValue);
					else
						be.setReceiveInvertedPower(newValue);
				})));

		if (be.isSender()) {
			layout.addChild(addRenderableWidget(
					CycleButton.<Boolean>builder(value -> Component.translatable("gui.securitycraft:invScan." + (value ? "yes" : "no")))
					.withValues(true, false)
					.withInitialValue(be.isProtectedSignal())
					.withTooltip(value -> Tooltip.create(Component.translatable("gui.securitycraft:secure_redstone_interface.protected_signal.tooltip." + value)))
					.create(0, 0, widgetWidth, widgetHeight, Component.translatable("gui.securitycraft:secure_redstone_interface.protected_signal"), (button, newValue) -> be.setProtectedSignal(newValue))));
			//@formatter:on
			layout.addChild(addRenderableWidget(new CallbackSlider(0, 0, widgetWidth, widgetHeight, Component.translatable("gui.securitycraft:projector.range", ""), Component.empty(), 1, 64, be.getSenderRange(), true, slider -> be.setSenderRange(slider.getValueInt()))));
		}

		layout.arrangeElements();
		FrameLayout.centerInRectangle(layout, leftPos, topPos, xSize, 24 + (be.isSender() ? ySize : ySize - 46));
	}

	@Override
	public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		renderTransparentBackground(guiGraphics);
		guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, xSize, ySize);
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		guiGraphics.drawString(font, frequencyText, frequencyBox.getX(), frequencyBox.getY() - font.lineHeight - 1, 0x404040, false);
		guiGraphics.drawString(font, title, width / 2 - font.width(title) / 2, topPos + 6, 0x404040, false);
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

		if (sender != oldSender || protectedSignal != oldProtectedSignal || frequency != oldFrequency || sendsExactPower != oldSendExactPower || receivesInvertedPower != oldReceiveInvertedPower || senderRange != oldSenderRange)
			PacketDistributor.sendToServer(new SyncSecureRedstoneInterface(be.getBlockPos(), sender, protectedSignal, frequency, sendsExactPower, receivesInvertedPower, senderRange));
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
