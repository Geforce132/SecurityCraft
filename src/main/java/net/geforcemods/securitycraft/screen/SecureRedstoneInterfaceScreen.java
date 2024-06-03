package net.geforcemods.securitycraft.screen;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.SecureRedstoneInterfaceBlockEntity;
import net.geforcemods.securitycraft.network.server.SyncSecureRedstoneInterface;
import net.geforcemods.securitycraft.screen.components.CallbackSlider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.LinearLayout.Orientation;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

// TODO: fix layout not always being in the middle of the screen depending on the window size
public class SecureRedstoneInterfaceScreen extends Screen {
	private final SecureRedstoneInterfaceBlockEntity be;
	private final boolean oldSender, oldSendExactPower, oldReceiveInvertedPower;
	private final int oldFrequency, oldSenderRange;

	public SecureRedstoneInterfaceScreen(SecureRedstoneInterfaceBlockEntity be) {
		this(Component.translatable(SCContent.SECURE_REDSTONE_INTERFACE.get().getDescriptionId()), be, be.isSender(), be.getFrequency(), be.sendsExactPower(), be.receivesInvertedPower(), be.getSenderRange());
	}

	private SecureRedstoneInterfaceScreen(Component title, SecureRedstoneInterfaceBlockEntity be, boolean oldSender, int oldFrequency, boolean oldSendExactPower, boolean oldReceiveInvertedPower, int oldSenderRange) {
		super(title);
		this.be = be;
		this.oldSender = oldSender;
		this.oldFrequency = oldFrequency;
		this.oldSendExactPower = oldSendExactPower;
		this.oldReceiveInvertedPower = oldReceiveInvertedPower;
		this.oldSenderRange = oldSenderRange;
	}

	@Override
	protected void init() {
		super.init();
		LinearLayout layout = new LinearLayout(210, 60, Orientation.VERTICAL).spacing(10);
		EditBox frequencyBox = new EditBox(font, 210, 20, Component.translatable("gui.securitycraft:secure_redstone_interface.frequency"));

		frequencyBox.setValue(be.getFrequency() + "");
		frequencyBox.setMaxLength(9);
		frequencyBox.setFilter(s -> s.matches("\\d*")); //any amount of digits);
		frequencyBox.setResponder(s -> {
			if (!s.isEmpty())
				be.setFrequency(Integer.parseInt(s));
			else
				be.setFrequency(0);
		});
		//@formatter:off
		layout.addChild(addRenderableWidget(
				CycleButton.<Boolean>builder(value -> Component.translatable("gui.securitycraft:secure_redstone_interface.mode." + (value ? "sender" : "receiver")))
				.withValues(true, false)
				.withInitialValue(be.isSender())
				.create(0, 0, 210, 20, Component.translatable("gui.securitycraft:secure_redstone_interface.mode"), (button, isNowASender) -> {
					be.setSender(isNowASender);

					if (isNowASender)
						be.setReceiveInvertedPower(oldReceiveInvertedPower);
					else {
						be.setSendExactPower(oldSendExactPower);
						be.setSenderRange(oldSenderRange);
					}

					minecraft.setScreen(new SecureRedstoneInterfaceScreen(title, be, oldSender, oldFrequency, oldSendExactPower, oldReceiveInvertedPower, oldSenderRange));
				})));
		layout.addChild(addRenderableWidget(
				CycleButton.<Boolean>builder(value -> Component.translatable("gui.securitycraft:invScan." + (value ? "yes" : "no")))
				.withValues(true, false)
				.withInitialValue(be.isSender() ? be.sendsExactPower() : be.receivesInvertedPower())
				.create(0, 0, 210, 20, Component.translatable("gui.securitycraft:secure_redstone_interface." + (be.isSender() ? "send_exact_power" : "receive_inverted_power")), (button, newValue) -> {
					if (be.isSender())
						be.setSendExactPower(newValue);
					else
						be.setReceiveInvertedPower(newValue);
				})));
		//@formatter:on
		layout.addChild(addRenderableWidget(frequencyBox));

		if (be.isSender())
			layout.addChild(addRenderableWidget(new CallbackSlider(0, 0, 210, 20, Component.translatable("gui.securitycraft:projector.range", ""), Component.empty(), 1, 64, be.getSenderRange(), true, slider -> be.setSenderRange(slider.getValueInt()))));

		layout.arrangeElements();
	}

	@Override
	public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		renderTransparentBackground(guiGraphics);
	}

	@Override
	public void onClose() {
		super.onClose();

		boolean sender = be.isSender();
		int frequency = be.getFrequency();
		boolean sendsExactPower = be.sendsExactPower();
		boolean receivesInvertedPower = be.receivesInvertedPower();
		int senderRange = be.getSenderRange();

		if (sender != oldSender || frequency != oldFrequency || sendsExactPower != oldSendExactPower || receivesInvertedPower != oldReceiveInvertedPower || senderRange != oldSenderRange)
			PacketDistributor.sendToServer(new SyncSecureRedstoneInterface(be.getBlockPos(), sender, frequency, sendsExactPower, receivesInvertedPower, senderRange));
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
