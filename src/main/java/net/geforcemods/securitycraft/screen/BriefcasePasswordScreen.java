package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.GenericContainer;
import net.geforcemods.securitycraft.network.server.OpenBriefcaseGui;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

@OnlyIn(Dist.CLIENT)
public class BriefcasePasswordScreen extends ContainerScreen<GenericContainer> {
	public static final String UP_ARROW = "\u2191";
	public static final String DOWN_ARROW = "\u2193";
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private TextFieldWidget[] keycodeTextboxes = new TextFieldWidget[4];
	private int[] digits = {
			0, 0, 0, 0
	};

	public BriefcasePasswordScreen(GenericContainer container, PlayerInventory inv, ITextComponent text) {
		super(container, inv, text);
	}

	@Override
	public void init() {
		super.init();

		for (int i = 0; i < 4; i++) {
			final int id = i;

			addButton(new ExtendedButton(width / 2 - 40 + (i * 20), height / 2 - 52, 20, 20, UP_ARROW, b -> keycodeButtonClicked(id)));
			addButton(new ExtendedButton(width / 2 - 40 + (i * 20), height / 2, 20, 20, DOWN_ARROW, b -> keycodeButtonClicked(4 + id)));
			//text boxes are not added via addButton because they should not be selectable
			keycodeTextboxes[i] = new TextFieldWidget(font, (width / 2 - 37) + (i * 20), height / 2 - 22, 14, 12, "");
			keycodeTextboxes[i].setMaxLength(1);
			keycodeTextboxes[i].setValue("0");
		}

		addButton(new ExtendedButton((width / 2 + 42), height / 2 - 26, 20, 20, ">", this::continueButtonClicked));
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		super.render(mouseX, mouseY, partialTicks);
		RenderSystem.disableLighting();

		for (TextFieldWidget textfield : keycodeTextboxes) {
			textfield.render(mouseX, mouseY, partialTicks);
		}
	}

	@Override
	protected void renderLabels(int mouseX, int mouseY) {
		font.draw(Utils.localize("gui.securitycraft:briefcase.enterPasscode").getColoredString(), imageWidth / 2 - font.width(Utils.localize("gui.securitycraft:briefcase.enterPasscode").getColoredString()) / 2, 6, 4210752);
	}

	@Override
	protected void renderBg(float partialTicks, int mouseX, int mouseY) {
		renderBackground();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(TEXTURE);
		blit(leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	protected void continueButtonClicked(Button button) {
		if (PlayerUtils.isHoldingItem(Minecraft.getInstance().player, SCContent.BRIEFCASE, null)) {
			CompoundNBT nbt = PlayerUtils.getSelectedItemStack(Minecraft.getInstance().player, SCContent.BRIEFCASE.get()).getTag();
			String code = digits[0] + "" + digits[1] + "" + digits[2] + "" + digits[3];

			if (nbt.getString("passcode").equals(code)) {
				if (!nbt.contains("owner")) {
					nbt.putString("owner", Minecraft.getInstance().player.getName().getString());
					nbt.putString("ownerUUID", Minecraft.getInstance().player.getUUID().toString());
				}

				SecurityCraft.channel.sendToServer(new OpenBriefcaseGui(SCContent.cTypeBriefcaseInventory.getRegistryName(), getTitle()));
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
