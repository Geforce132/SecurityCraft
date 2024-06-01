package net.geforcemods.securitycraft.screen;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.SecureRedstoneInterfaceBlockEntity;
import net.geforcemods.securitycraft.network.server.SyncSecureRedstoneInterface;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class SecureRedstoneInterfaceScreen extends Screen {
	private final SecureRedstoneInterfaceBlockEntity be;

	public SecureRedstoneInterfaceScreen(SecureRedstoneInterfaceBlockEntity be) {
		super(Component.translatable(SCContent.SECURE_REDSTONE_INTERFACE.get().getDescriptionId()));
		this.be = be;
	}

	@Override
	protected void init() {
		super.init();
		//@formatter:off
		addRenderableWidget(
				CycleButton.<Boolean>builder(sender -> Component.translatable("gui.securitycraft:secure_redstone_interface.mode." + (sender ? "sender" : "receiver")))
				.withValues(true, false)
				.withInitialValue(be.isSender())
				.create(0, 0, 210, 20, Component.translatable("gui.securitycraft:secure_redstone_interface.mode"), (button, newValue) -> PacketDistributor.sendToServer(new SyncSecureRedstoneInterface(be.getBlockPos(), newValue))));
		//@formatter:on
	}

	@Override
	public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		renderTransparentBackground(guiGraphics);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
