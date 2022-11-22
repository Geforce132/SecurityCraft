package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.network.server.OpenBriefcaseInventory;
import net.geforcemods.securitycraft.network.server.SetBriefcaseOwner;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.widget.ExtendedButton;

public class BriefcasePasswordScreen extends Screen {
	public static final String UP_ARROW = "\u2191";
	public static final String DOWN_ARROW = "\u2193";
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private final Component enterPasscode = Utils.localize("gui.securitycraft:briefcase.enterPasscode");
	private int imageWidth = 176;
	private int imageHeight = 166;
	private int leftPos;
	private int topPos;
	private EditBox[] keycodeTextboxes = new EditBox[4];
	private int[] digits = {
			0, 0, 0, 0
	};

	public BriefcasePasswordScreen(Component title) {
		super(title);
	}

	@Override
	public void init() {
		super.init();

		leftPos = (width - imageWidth) / 2;
		topPos = (height - imageHeight) / 2;

		for (int i = 0; i < 4; i++) {
			final int id = i;

			addRenderableWidget(new ExtendedButton(width / 2 - 40 + (i * 20), height / 2 - 52, 20, 20, Component.literal(UP_ARROW), b -> keycodeButtonClicked(id)));
			addRenderableWidget(new ExtendedButton(width / 2 - 40 + (i * 20), height / 2, 20, 20, Component.literal(DOWN_ARROW), b -> keycodeButtonClicked(4 + id)));
			//text boxes are not added via addRenderableWidget because they should not be selectable
			keycodeTextboxes[i] = addRenderableOnly(new EditBox(font, (width / 2 - 37) + (i * 20), height / 2 - 22, 14, 12, Component.empty()));
			keycodeTextboxes[i].setMaxLength(1);
			keycodeTextboxes[i].setValue("0");
		}

		addRenderableWidget(new ExtendedButton((width / 2 + 42), height / 2 - 26, 20, 20, Component.literal(">"), this::continueButtonClicked));
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTick) {
		renderBackground(pose);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem._setShaderTexture(0, TEXTURE);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		super.render(pose, mouseX, mouseY, partialTick);
		font.draw(pose, enterPasscode, width / 2 - font.width(enterPasscode) / 2, topPos + 6, 4210752);
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
	public boolean isPauseScreen() {
		return false;
	}

	private void continueButtonClicked(Button button) {
		if (PlayerUtils.isHoldingItem(Minecraft.getInstance().player, SCContent.BRIEFCASE, null)) {
			ItemStack briefcase = PlayerUtils.getSelectedItemStack(Minecraft.getInstance().player, SCContent.BRIEFCASE.get());
			CompoundTag nbt = briefcase.getTag();
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
