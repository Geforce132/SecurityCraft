package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.network.server.OpenBriefcaseInventory;
import net.geforcemods.securitycraft.network.server.SetBriefcaseOwner;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

@OnlyIn(Dist.CLIENT)
public class BriefcasePasswordScreen extends Screen {
	public static final String UP_ARROW = "\u2191";
	public static final String DOWN_ARROW = "\u2193";
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private final TranslationTextComponent enterPasscode = Utils.localize("gui.securitycraft:briefcase.enterPasscode");
	private int imageWidth = 176;
	private int imageHeight = 166;
	private int leftPos;
	private int topPos;
	private TextFieldWidget[] keycodeTextboxes = new TextFieldWidget[4];
	private int[] digits = {
			0, 0, 0, 0
	};

	public BriefcasePasswordScreen(ITextComponent title) {
		super(title);
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
			//text boxes are not added via addButton because they should not be selectable
			keycodeTextboxes[i] = new TextFieldWidget(font, (width / 2 - 37) + (i * 20), height / 2 - 22, 14, 12, StringTextComponent.EMPTY);
			keycodeTextboxes[i].setMaxLength(1);
			keycodeTextboxes[i].setValue("0");
		}

		addButton(new ExtendedButton((width / 2 + 42), height / 2 - 26, 20, 20, new StringTextComponent(">"), this::continueButtonClicked));
	}

	@Override
	public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrix);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(TEXTURE);
		blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		super.render(matrix, mouseX, mouseY, partialTicks);
		RenderSystem.disableLighting();

		for (TextFieldWidget textfield : keycodeTextboxes) {
			textfield.render(matrix, mouseX, mouseY, partialTicks);
		}

		font.draw(matrix, enterPasscode, width / 2 - font.width(enterPasscode) / 2, topPos + 6, 4210752);
	}

	protected void continueButtonClicked(Button button) {
		if (PlayerUtils.isHoldingItem(Minecraft.getInstance().player, SCContent.BRIEFCASE, null)) {
			ItemStack briefcase = PlayerUtils.getSelectedItemStack(Minecraft.getInstance().player, SCContent.BRIEFCASE.get());
			CompoundNBT nbt = briefcase.getTag();
			String code = digits[0] + "" + digits[1] + "" + digits[2] + "" + digits[3];

			if (nbt.getString("passcode").equals(code)) {
				if (!nbt.contains("owner")) {
					nbt.putString("owner", Minecraft.getInstance().player.getName().getString());
					nbt.putString("ownerUUID", Minecraft.getInstance().player.getUUID().toString());
					SecurityCraft.channel.sendToServer(new SetBriefcaseOwner(""));
				}

				SecurityCraft.channel.sendToServer(new OpenBriefcaseInventory(getTitle()));
			}
		}
	}

	private void keycodeButtonClicked(int id) {
		int index = id % 4;

		//java's modulo operator % does not handle negative numbers like it should for some reason, so floorMod needs to be used
		digits[index] = Math.floorMod((id > 3 ? --digits[index] : ++digits[index]), 10);
		keycodeTextboxes[index].setValue(String.valueOf(digits[index]));
	}
}
