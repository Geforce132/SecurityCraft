package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.SecureRedstoneInterfaceBlockEntity;
import net.geforcemods.securitycraft.network.server.SyncSecureRedstoneInterface;
import net.geforcemods.securitycraft.screen.components.ActiveBasedTextureButton;
import net.geforcemods.securitycraft.screen.components.CallbackSlider;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class SecureRedstoneInterfaceScreen extends Screen {
	public static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/secure_redstone_interface.png");
	private static final ResourceLocation RANDOM_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/random.png");
	private static final ResourceLocation RANDOM_INACTIVE_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/random_inactive.png");
	private final Component frequencyText = Component.translatable("gui.securitycraft:secure_redstone_interface.frequency");
	private final SecureRedstoneInterfaceBlockEntity be;
	private final boolean oldSender, oldProtectedSignal, oldSendExactPower, oldReceiveInvertedPower, oldHighlightConnections;
	private final int oldFrequency, oldSenderRange;
	private final int xSize = 176, ySize = 188;
	private int leftPos;
	private int topPos;
	private EditBox frequencyBox;

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
		int widgetWidth = 150, widgetHeight = 20, row = 0;

		super.init();
		leftPos = (width - xSize) / 2;
		topPos = (height - ySize) / 2;

		GridLayout layout = new GridLayout(0, 0);
		GridLayout frequencyLayout = new GridLayout(0, 0);
		String powerSettingKey = "gui.securitycraft:secure_redstone_interface." + (be.isSender() ? "send_exact_power" : "receive_inverted_power");

		layout.rowSpacing(3);
		frequencyLayout.columnSpacing(3);
		frequencyBox = new EditBox(font, 0, 0, widgetWidth - 23, widgetHeight, frequencyText);
		frequencyBox.setValue(be.getFrequency() + "");
		frequencyBox.setMaxLength(9);
		frequencyBox.setFilter(s -> s.matches("\\d*")); //any amount of digits);
		frequencyBox.setResponder(s -> {
			if (!s.isEmpty())
				be.setFrequency(Integer.parseInt(s));
			else
				be.setFrequency(0);
		});
		frequencyLayout.addChild(frequencyBox, 0, 0);
		frequencyLayout.addChild(new ActiveBasedTextureButton(0, 0, 20, widgetHeight, RANDOM_TEXTURE, RANDOM_INACTIVE_TEXTURE, 16, 16, 3, 3, 16, 16, 16, 16, b -> frequencyBox.setValue("" + SecurityCraft.RANDOM.nextInt(999999999))), 0, 1);
		layout.addChild(frequencyLayout, row++, 0);
		//@formatter:off
		layout.addChild(CycleButton.<Boolean>builder(value -> Component.translatable("gui.securitycraft:secure_redstone_interface.mode." + (value ? "sender" : "receiver")))
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

					minecraft.setScreen(new SecureRedstoneInterfaceScreen(be, oldSender, oldProtectedSignal, oldFrequency, oldSendExactPower, oldReceiveInvertedPower, oldSenderRange,oldHighlightConnections));
				}), row++, 0);
		layout.addChild(CycleButton.<Boolean>builder(value -> Component.translatable("gui.securitycraft:invScan." + (value ? "yes" : "no")))
				.withValues(true, false)
				.withInitialValue(be.isSender() ? be.sendsExactPower() : be.receivesInvertedPower())
				.withTooltip(value -> Tooltip.create(Component.translatable(powerSettingKey + ".tooltip." + value)))
				.create(0, 0, widgetWidth, widgetHeight, Component.translatable(powerSettingKey), (button, newValue) -> {
					if (be.isSender())
						be.setSendExactPower(newValue);
					else
						be.setReceiveInvertedPower(newValue);
				}), row++, 0);

		if (be.isSender()) {
			layout.addChild(CycleButton.<Boolean>builder(value -> Component.translatable("gui.securitycraft:invScan." + (value ? "yes" : "no")))
					.withValues(true, false)
					.withInitialValue(be.isProtectedSignal())
					.withTooltip(value -> Tooltip.create(Component.translatable("gui.securitycraft:secure_redstone_interface.protected_signal.tooltip." + value)))
					.create(0, 0, widgetWidth, widgetHeight, Component.translatable("gui.securitycraft:secure_redstone_interface.protected_signal"), (button, newValue) -> be.setProtectedSignal(newValue)), row++, 0);
			layout.addChild(new CallbackSlider(0, 0, widgetWidth, widgetHeight, Component.translatable("gui.securitycraft:projector.range", ""), Component.empty(), 1, 64, be.getSenderRange(), true, slider -> be.setSenderRange(slider.getValueInt())), row++, 0);
		}

		layout.addChild(CycleButton.<Boolean>builder(value -> Component.translatable("gui.securitycraft:invScan." + (value ? "yes" : "no")))
				.withValues(true, false)
				.withInitialValue(be.shouldHighlightConnections())
				.withTooltip(value -> Tooltip.create(Component.translatable("gui.securitycraft:secure_redstone_interface.highlight_connections.tooltip")))
				.create(0, 0, widgetWidth, widgetHeight, Component.translatable("gui.securitycraft:secure_redstone_interface.highlight_connections"), (button, newValue) -> be.setHighlightConnections(newValue)), row++, 0);
		//@formatter:on
		layout.arrangeElements();
		layout.visitWidgets(this::addRenderableWidget);
		FrameLayout.centerInRectangle(layout, leftPos, topPos, xSize, 24 + (be.isSender() ? ySize : ySize - 46));
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		renderBackground(pose);
		RenderSystem._setShaderTexture(0, TEXTURE);
		blit(pose, leftPos, topPos, 0, 0, xSize, ySize);
		super.render(pose, mouseX, mouseY, partialTicks);
		font.draw(pose, frequencyText, frequencyBox.getX(), frequencyBox.getY() - font.lineHeight - 1, 0x404040);
		font.draw(pose, title, width / 2 - font.width(title) / 2, topPos + 6, 0x404040);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (minecraft.options.keyInventory.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode))) {
			onClose();
			return true;
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
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
			SecurityCraft.CHANNEL.sendToServer(new SyncSecureRedstoneInterface(be.getBlockPos(), sender, protectedSignal, frequency, sendsExactPower, receivesInvertedPower, senderRange, highlightConnections));
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
