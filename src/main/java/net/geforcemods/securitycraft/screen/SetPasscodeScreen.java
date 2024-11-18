package net.geforcemods.securitycraft.screen;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.network.server.SetPasscode;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.util.InputMappings;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

@OnlyIn(Dist.CLIENT)
public class SetPasscodeScreen extends Screen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private int imageWidth = 176;
	private int imageHeight = 166;
	private int leftPos;
	private int topPos;
	private TileEntity be;
	private TranslationTextComponent setup;
	private IFormattableTextComponent combined;
	private TextFieldWidget keycodeTextbox;
	private Button saveAndContinueButton;

	public SetPasscodeScreen(TileEntity be, ITextComponent title) {
		super(title);
		this.be = be;
		setup = Utils.localize("gui.securitycraft:passcode.setup");
		combined = title.plainCopy().append(new StringTextComponent(" ")).append(setup);
	}

	@Override
	public void init() {
		super.init();

		leftPos = (width - imageWidth) / 2;
		topPos = (height - imageHeight) / 2;

		saveAndContinueButton = addButton(new ExtendedButton(width / 2 - 48, height / 2 + 30 + 10, 100, 20, Utils.localize("gui.securitycraft:passcode.save"), this::saveAndContinueButtonClicked));
		saveAndContinueButton.active = false;
		minecraft.keyboardHandler.setSendRepeatsToGui(true);
		keycodeTextbox = addButton(new TextFieldWidget(font, width / 2 - 37, height / 2 - 47, 77, 12, StringTextComponent.EMPTY));
		keycodeTextbox.setMaxLength(Integer.MAX_VALUE);
		keycodeTextbox.setFilter(s -> s.matches("\\d*"));
		keycodeTextbox.setResponder(text -> saveAndContinueButton.active = !text.isEmpty());
		setInitialFocus(keycodeTextbox);
	}

	@Override
	public void removed() {
		super.removed();
		minecraft.keyboardHandler.setSendRepeatsToGui(false);
	}

	@Override
	public void render(MatrixStack pose, int mouseX, int mouseY, float partialTicks) {
		renderBackground(pose);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(TEXTURE);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		super.render(pose, mouseX, mouseY, partialTicks);
		drawString(pose, font, "CODE:", width / 2 - 67, height / 2 - 47 + 2, 4210752);

		if (font.width(combined) < imageWidth - 10)
			font.draw(pose, combined, width / 2 - font.width(combined) / 2, topPos + 6, 4210752);
		else {
			font.draw(pose, title, width / 2 - font.width(title) / 2, topPos + 6, 4210752);
			font.draw(pose, setup, width / 2 - font.width(setup) / 2, topPos + 16, 4210752);
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (minecraft.options.keyInventory.isActiveAndMatches(InputMappings.getKey(keyCode, scanCode))) {
			onClose();
			return true;
		}
		else if (keyCode == GLFW.GLFW_KEY_KP_ENTER || keyCode == GLFW.GLFW_KEY_ENTER && saveAndContinueButton.active)
			saveAndContinueButtonClicked(saveAndContinueButton);

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private void saveAndContinueButtonClicked(Button button) {
		SecurityCraft.channel.sendToServer(new SetPasscode(be.getBlockPos().getX(), be.getBlockPos().getY(), be.getBlockPos().getZ(), keycodeTextbox.getValue()));
		Minecraft.getInstance().player.closeContainer();
	}
}
