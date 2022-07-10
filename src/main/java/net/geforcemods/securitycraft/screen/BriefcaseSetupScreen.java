package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.network.server.SetBriefcaseOwner;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fmlclient.gui.widget.ExtendedButton;

public class BriefcaseSetupScreen extends Screen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private final TranslatableComponent setupTitle = Utils.localize("gui.securitycraft:briefcase.setupTitle");
	private EditBox keycodeTextbox;
	private Button saveAndContinueButton;
	private int imageWidth = 176;
	private int imageHeight = 166;
	private int leftPos;
	private int topPos;

	public BriefcaseSetupScreen(Component title) {
		super(title);
	}

	@Override
	public void init() {
		super.init();

		leftPos = (width - imageWidth) / 2;
		topPos = (height - imageHeight) / 2;

		minecraft.keyboardHandler.setSendRepeatsToGui(true);
		addRenderableWidget(saveAndContinueButton = new ExtendedButton(width / 2 - 48, height / 2 + 30 + 10, 100, 20, Utils.localize("gui.securitycraft:password.save"), this::saveAndContinueButtonClicked));
		saveAndContinueButton.active = false;

		addRenderableWidget(keycodeTextbox = new EditBox(font, width / 2 - 37, height / 2 - 47, 77, 12, TextComponent.EMPTY));
		keycodeTextbox.setMaxLength(4);
		keycodeTextbox.setFilter(s -> s.matches("[0-9]*"));
		keycodeTextbox.setResponder(text -> saveAndContinueButton.active = text.length() == 4);
		setInitialFocus(keycodeTextbox);
	}

	@Override
	public void removed() {
		super.removed();
		minecraft.keyboardHandler.setSendRepeatsToGui(false);
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		renderBackground(pose);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem._setShaderTexture(0, TEXTURE);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		super.render(pose, mouseX, mouseY, partialTicks);
		drawString(pose, font, "CODE:", width / 2 - 67, height / 2 - 47 + 2, 4210752);
		font.draw(pose, setupTitle, width / 2 - font.width(setupTitle) / 2, topPos + 6, 4210752);
	}

	private void saveAndContinueButtonClicked(Button button) {
		if (PlayerUtils.isHoldingItem(Minecraft.getInstance().player, SCContent.BRIEFCASE, null)) {
			ItemStack briefcase = PlayerUtils.getSelectedItemStack(Minecraft.getInstance().player, SCContent.BRIEFCASE.get());
			String passcode = keycodeTextbox.getValue();

			briefcase.getOrCreateTag().putString("passcode", passcode);

			if (!briefcase.getTag().contains("owner")) {
				briefcase.getTag().putString("owner", Minecraft.getInstance().player.getName().getString());
				briefcase.getTag().putString("ownerUUID", Minecraft.getInstance().player.getUUID().toString());
			}

			SecurityCraft.channel.sendToServer(new SetBriefcaseOwner(passcode));
			ClientHandler.displayBriefcasePasswordScreen(getTitle());
		}
	}
}
