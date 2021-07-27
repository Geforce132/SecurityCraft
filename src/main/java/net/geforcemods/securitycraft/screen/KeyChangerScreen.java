package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.containers.GenericTEContainer;
import net.geforcemods.securitycraft.network.server.SetPassword;
import net.geforcemods.securitycraft.screen.components.IdButton;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KeyChangerScreen extends AbstractContainerScreen<GenericTEContainer> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private final TranslatableComponent ukcName = Utils.localize(SCContent.UNIVERSAL_KEY_CHANGER.get().getDescriptionId());
	private final TranslatableComponent enterPasscode = Utils.localize("gui.securitycraft:universalKeyChanger.enterNewPasscode");
	private final TranslatableComponent confirmPasscode = Utils.localize("gui.securitycraft:universalKeyChanger.confirmNewPasscode");
	private EditBox textboxNewPasscode;
	private EditBox textboxConfirmPasscode;
	private IdButton confirmButton;
	private BlockEntity tileEntity;

	public KeyChangerScreen(GenericTEContainer container, Inventory inv, Component name) {
		super(container, inv, name);
		tileEntity = container.te;
	}

	@Override
	public void init(){
		super.init();
		minecraft.keyboardHandler.setSendRepeatsToGui(true);
		addRenderableWidget(confirmButton = new IdButton(0, width / 2 - 52, height / 2 + 52, 100, 20, Utils.localize("gui.securitycraft:universalKeyChanger.confirm"), this::actionPerformed));
		confirmButton.active = false;

		addRenderableWidget(textboxNewPasscode = new EditBox(font, width / 2 - 57, height / 2 - 47, 110, 12, TextComponent.EMPTY));
		textboxNewPasscode.setMaxLength(20);
		setInitialFocus(textboxNewPasscode);
		textboxNewPasscode.setFilter(s -> s.matches("[0-9]*"));
		textboxNewPasscode.setResponder(s -> updateConfirmButtonState());

		addRenderableWidget(textboxConfirmPasscode = new EditBox(font, width / 2 - 57, height / 2 - 7, 110, 12, TextComponent.EMPTY));
		textboxConfirmPasscode.setMaxLength(20);
		textboxConfirmPasscode.setFilter(s -> s.matches("[0-9]*"));
		textboxConfirmPasscode.setResponder(s -> updateConfirmButtonState());
	}

	@Override
	public void removed(){
		super.removed();
		minecraft.keyboardHandler.setSendRepeatsToGui(false);
	}

	@Override
	protected void renderLabels(PoseStack matrix, int mouseX, int mouseY){
		font.draw(matrix, ukcName, imageWidth / 2 - font.width(ukcName) / 2, 6, 4210752);
		font.draw(matrix, enterPasscode, imageWidth / 2 - font.width(enterPasscode) / 2, 25, 4210752);
		font.draw(matrix, confirmPasscode, imageWidth / 2 - font.width(confirmPasscode) / 2, 65, 4210752);
	}

	@Override
	protected void renderBg(PoseStack matrix, float partialTicks, int mouseX, int mouseY){
		renderBackground(matrix);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem._setShaderTexture(0, TEXTURE);
		int startX = (width - imageWidth) / 2;
		int startY = (height - imageHeight) / 2;
		this.blit(matrix, startX, startY, 0, 0, imageWidth, imageHeight);
	}

	private void updateConfirmButtonState() {
		String newPasscode = textboxNewPasscode.getValue();
		String confirmPasscode = textboxConfirmPasscode.getValue();

		confirmButton.active = confirmPasscode != null && newPasscode != null && !confirmPasscode.isEmpty() && !newPasscode.isEmpty() && newPasscode.equals(confirmPasscode);
	}

	protected void actionPerformed(IdButton button){
		((IPasswordProtected) tileEntity).setPassword(textboxNewPasscode.getValue());
		SecurityCraft.channel.sendToServer(new SetPassword(tileEntity.getBlockPos().getX(), tileEntity.getBlockPos().getY(), tileEntity.getBlockPos().getZ(), textboxNewPasscode.getValue()));

		Minecraft.getInstance().player.closeContainer();
		PlayerUtils.sendMessageToPlayer(Minecraft.getInstance().player, Utils.localize(SCContent.UNIVERSAL_KEY_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalKeyChanger.passcodeChanged"), ChatFormatting.GREEN, true);
	}
}
