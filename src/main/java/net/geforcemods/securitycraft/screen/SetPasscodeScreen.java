package net.geforcemods.securitycraft.screen;

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
	private TileEntity tileEntity;
	private TranslationTextComponent setup;
	private IFormattableTextComponent combined;
	private TextFieldWidget keycodeTextbox;

	public SetPasscodeScreen(TileEntity te, ITextComponent title) {
		super(title);
		tileEntity = te;
		setup = Utils.localize("gui.securitycraft:passcode.setup");
		combined = title.plainCopy().append(new StringTextComponent(" ")).append(setup);
	}

	@Override
	public void init() {
		super.init();

		leftPos = (width - imageWidth) / 2;
		topPos = (height - imageHeight) / 2;

		Button saveAndContinueButton = addButton(new ExtendedButton(width / 2 - 48, height / 2 + 30 + 10, 100, 20, Utils.localize("gui.securitycraft:passcode.save"), this::saveAndContinueButtonClicked));

		minecraft.keyboardHandler.setSendRepeatsToGui(true);
		saveAndContinueButton.active = false;

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
	public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrix);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(TEXTURE);
		blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		super.render(matrix, mouseX, mouseY, partialTicks);
		drawString(matrix, font, "CODE:", width / 2 - 67, height / 2 - 47 + 2, 4210752);

		if (font.width(combined) < imageWidth - 10)
			font.draw(matrix, combined, width / 2 - font.width(combined) / 2, topPos + 6, 4210752);
		else {
			font.draw(matrix, title, width / 2 - font.width(title) / 2, topPos + 6, 4210752);
			font.draw(matrix, setup, width / 2 - font.width(setup) / 2, topPos + 16, 4210752);
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (minecraft.options.keyInventory.isActiveAndMatches(InputMappings.getKey(keyCode, scanCode))) {
			onClose();
			return true;
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private void saveAndContinueButtonClicked(Button button) {
		SecurityCraft.channel.sendToServer(new SetPasscode(tileEntity.getBlockPos().getX(), tileEntity.getBlockPos().getY(), tileEntity.getBlockPos().getZ(), keycodeTextbox.getValue()));
		Minecraft.getInstance().player.closeContainer();
	}
}
