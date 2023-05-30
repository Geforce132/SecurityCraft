package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.network.server.CheckBriefcasePasscode;
import net.geforcemods.securitycraft.network.server.SetBriefcasePasscodeAndOwner;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

@OnlyIn(Dist.CLIENT)
public class BriefcasePasscodeScreen extends Screen {
	public static final String UP_ARROW = "\u2191";
	public static final String RIGHT_ARROW = "\u2192";
	public static final String DOWN_ARROW = "\u2193";
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private final boolean isSetup;
	private int imageWidth = 176;
	private int imageHeight = 166;
	private int leftPos;
	private int topPos;
	private TextFieldWidget[] keycodeTextboxes = new TextFieldWidget[4];
	private int[] digits = {
			0, 0, 0, 0
	};

	public BriefcasePasscodeScreen(ITextComponent title, boolean isSetup) {
		super(title);
		this.isSetup = isSetup;
	}

	@Override
	public void init() {
		super.init();

		leftPos = (width - imageWidth) / 2;
		topPos = (height - imageHeight) / 2;

		for (int i = 0; i < 4; i++) {
			final int id = i;

			addButton(new ExtendedButton(width / 2 - 40 + (i * 20), height / 2 - 52, 20, 20, new StringTextComponent(UP_ARROW), b -> keycodeButtonClicked(id)));
			addButton(new ExtendedButton(width / 2 - 40 + (i * 20), height / 2, 20, 20, new StringTextComponent(DOWN_ARROW), b -> keycodeButtonClicked(4 + id)));
			//text boxes are not added via addRenderableWidget because they should not be accessible by the player
			keycodeTextboxes[i] = new TextFieldWidget(font, (width / 2 - 37) + (i * 20), height / 2 - 22, 14, 12, StringTextComponent.EMPTY);
			keycodeTextboxes[i].setMaxLength(1);
			keycodeTextboxes[i].setValue("0");
		}

		addButton(new ExtendedButton((width / 2 + 42), height / 2 - 26, 20, 20, new StringTextComponent(">"), this::continueButtonClicked));
	}

	@Override
	public void render(MatrixStack pose, int mouseX, int mouseY, float partialTicks) {
		renderBackground(pose);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(TEXTURE);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		super.render(pose, mouseX, mouseY, partialTicks);
		RenderSystem.disableLighting();

		for (TextFieldWidget textfield : keycodeTextboxes) {
			textfield.render(pose, mouseX, mouseY, partialTicks);
		}

		font.draw(pose, title, width / 2 - font.width(title) / 2, topPos + 6, 4210752);
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

	private void continueButtonClicked(Button button) {
		ItemStack briefcase = PlayerUtils.getSelectedItemStack(ClientHandler.getClientPlayer(), SCContent.BRIEFCASE.get());

		if (!briefcase.isEmpty()) {
			String passcode = digits[0] + "" + digits[1] + "" + digits[2] + "" + digits[3];

			if (isSetup) {
				SecurityCraft.channel.sendToServer(new SetBriefcasePasscodeAndOwner(passcode));
				ClientHandler.displayBriefcasePasscodeScreen(briefcase.getHoverName());
			}
			else
				SecurityCraft.channel.sendToServer(new CheckBriefcasePasscode(passcode));
		}
	}

	private void keycodeButtonClicked(int id) {
		int index = id % 4;

		//java's modulo operator % does not handle negative numbers like it should for some reason, so floorMod needs to be used
		digits[index] = Math.floorMod((id > 3 ? --digits[index] : ++digits[index]), 10);
		keycodeTextboxes[index].setValue(String.valueOf(digits[index]));
	}
}
