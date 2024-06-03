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

// TODO: change widgets when the mode is changed
// TODO: fix layout not always being in the middle of the screen depending on the window size
public class SecureRedstoneInterfaceScreen extends Screen {
	private final SecureRedstoneInterfaceBlockEntity be;
	private final boolean oldSender, oldSendExactPower, oldReceiveInvertedPower;
	private final int oldFrequency, oldSenderRange;
	private boolean sender, sendExactPower, receiveInvertedPower;
	private int frequency, senderRange;

	public SecureRedstoneInterfaceScreen(SecureRedstoneInterfaceBlockEntity be) {
		super(Component.translatable(SCContent.SECURE_REDSTONE_INTERFACE.get().getDescriptionId()));
		this.be = be;
		sender = oldSender = be.isSender();
		frequency = oldFrequency = be.getFrequency();
		sendExactPower = oldSendExactPower = be.sendsExactPower();
		receiveInvertedPower = oldReceiveInvertedPower = be.receivesInvertedPower();
		senderRange = oldSenderRange = be.getSenderRange();
	}

	@Override
	protected void init() {
		super.init();
		LinearLayout layout = new LinearLayout(210, 60, Orientation.VERTICAL).spacing(10);
		EditBox frequencyBox = new EditBox(font, 210, 20, Component.translatable("gui.securitycraft:secure_redstone_interface.frequency"));

		frequencyBox.setValue(be.getFrequency() + "");
		frequencyBox.setFilter(s -> s.matches("\\d*")); //any amount of digits
		frequencyBox.setResponder(s -> { //TODO: input sanitization
			if (!s.isEmpty())
				frequency = Integer.parseInt(s);
			else
				frequency = 0;
		});
		//@formatter:off
		layout.addChild(addRenderableWidget(
				CycleButton.<Boolean>builder(value -> Component.translatable("gui.securitycraft:secure_redstone_interface.mode." + (value ? "sender" : "receiver")))
				.withValues(true, false)
				.withInitialValue(be.isSender())
				.create(0, 0, 210, 20, Component.translatable("gui.securitycraft:secure_redstone_interface.mode"), (button, newValue) -> sender = newValue)));
		layout.addChild(addRenderableWidget(
				CycleButton.<Boolean>builder(value -> Component.translatable("gui.securitycraft:invScan." + (value ? "yes" : "no")))
				.withValues(true, false)
				.withInitialValue(be.isSender() ? be.sendsExactPower() : be.receivesInvertedPower())
				.create(0, 0, 210, 20, Component.translatable("gui.securitycraft:secure_redstone_interface." + (be.isSender() ? "send_exact_power" : "receive_inverted_power")), (button, newValue) -> {
					if (be.isSender())
						sendExactPower = newValue;
					else
						receiveInvertedPower = newValue;
				})));
		//@formatter:on
		layout.addChild(addRenderableWidget(frequencyBox));

		if (be.isSender())
			layout.addChild(addRenderableWidget(new CallbackSlider(0, 0, 210, 20, Component.translatable("gui.securitycraft:projector.range", ""), Component.empty(), 1, 64, be.getSenderRange(), true, slider -> senderRange = slider.getValueInt())));

		layout.arrangeElements();
	}

	@Override
	public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		renderTransparentBackground(guiGraphics);
	}

	@Override
	public void onClose() {
		super.onClose();

		if (oldSender != sender || oldFrequency != frequency || sendExactPower != oldSendExactPower || receiveInvertedPower != oldReceiveInvertedPower || senderRange != oldSenderRange)
			PacketDistributor.sendToServer(new SyncSecureRedstoneInterface(be.getBlockPos(), sender, frequency, sendExactPower, receiveInvertedPower, senderRange));
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
