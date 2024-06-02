package net.geforcemods.securitycraft.screen;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.SecureRedstoneInterfaceBlockEntity;
import net.geforcemods.securitycraft.network.server.SyncSecureRedstoneInterface;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.LinearLayout.Orientation;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class SecureRedstoneInterfaceScreen extends Screen {
	private final SecureRedstoneInterfaceBlockEntity be;
	private final boolean oldSender;
	private final int oldFrequency;
	private boolean sender;
	private int frequency;

	public SecureRedstoneInterfaceScreen(SecureRedstoneInterfaceBlockEntity be) {
		super(Component.translatable(SCContent.SECURE_REDSTONE_INTERFACE.get().getDescriptionId()));
		this.be = be;
		sender = oldSender = be.isSender();
		frequency = oldFrequency = be.getFrequency();
		System.out.println(oldFrequency);
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
		//@formatter:on
		layout.addChild(addRenderableWidget(frequencyBox));
		layout.arrangeElements();
	}

	@Override
	public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		renderTransparentBackground(guiGraphics);
	}

	@Override
	public void onClose() {
		super.onClose();

		if (oldSender != sender || oldFrequency != frequency)
			PacketDistributor.sendToServer(new SyncSecureRedstoneInterface(be.getBlockPos(), sender, frequency));
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
