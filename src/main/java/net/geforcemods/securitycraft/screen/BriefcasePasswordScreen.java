package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.inventory.GenericMenu;
import net.geforcemods.securitycraft.network.server.OpenBriefcaseGui;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.gui.widget.ExtendedButton;

public class BriefcasePasswordScreen extends AbstractContainerScreen<GenericMenu> {
	public static final String UP_ARROW = "\u2191";
	public static final String DOWN_ARROW = "\u2193";
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private final TranslatableComponent enterPasscode = Utils.localize("gui.securitycraft:briefcase.enterPasscode");
	private EditBox[] keycodeTextboxes = new EditBox[4];
	private int[] digits = {
			0, 0, 0, 0
	};

	public BriefcasePasswordScreen(GenericMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);
	}

	@Override
	public void init() {
		super.init();

		for (int i = 0; i < 4; i++) {
			final int id = i;

			addRenderableWidget(new ExtendedButton(width / 2 - 40 + (i * 20), height / 2 - 52, 20, 20, new TextComponent(UP_ARROW), b -> keycodeButtonClicked(id)));
			addRenderableWidget(new ExtendedButton(width / 2 - 40 + (i * 20), height / 2, 20, 20, new TextComponent(DOWN_ARROW), b -> keycodeButtonClicked(4 + id)));
			//text boxes are not added via addRenderableWidget because they should not be selectable
			keycodeTextboxes[i] = addRenderableOnly(new EditBox(font, (width / 2 - 37) + (i * 20), height / 2 - 22, 14, 12, TextComponent.EMPTY));
			keycodeTextboxes[i].setMaxLength(1);
			keycodeTextboxes[i].setValue("0");
		}

		addRenderableWidget(new ExtendedButton((width / 2 + 42), height / 2 - 26, 20, 20, new TextComponent(">"), this::continueButtonClicked));
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		font.draw(pose, enterPasscode, imageWidth / 2 - font.width(enterPasscode) / 2, 6, 4210752);
	}

	@Override
	protected void renderBg(PoseStack pose, float partialTicks, int mouseX, int mouseY) {
		renderBackground(pose);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem._setShaderTexture(0, TEXTURE);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	private void continueButtonClicked(Button button) {
		if (PlayerUtils.isHoldingItem(Minecraft.getInstance().player, SCContent.BRIEFCASE, null)) {
			CompoundTag nbt = PlayerUtils.getSelectedItemStack(Minecraft.getInstance().player, SCContent.BRIEFCASE.get()).getTag();
			String code = digits[0] + "" + digits[1] + "" + digits[2] + "" + digits[3];

			if (nbt.getString("passcode").equals(code)) {
				if (!nbt.contains("owner")) {
					nbt.putString("owner", Minecraft.getInstance().player.getName().getString());
					nbt.putString("ownerUUID", Minecraft.getInstance().player.getUUID().toString());
				}

				SecurityCraft.channel.sendToServer(new OpenBriefcaseGui(SCContent.BRIEFCASE_INVENTORY_MENU.get().getRegistryName(), getTitle()));
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
