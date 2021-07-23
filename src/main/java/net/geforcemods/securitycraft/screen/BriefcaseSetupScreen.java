package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.GenericContainer;
import net.geforcemods.securitycraft.network.server.OpenBriefcaseGui;
import net.geforcemods.securitycraft.screen.components.IdButton;
import net.geforcemods.securitycraft.util.ClientUtils;
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
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BriefcaseSetupScreen extends AbstractContainerScreen<GenericContainer> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private final TranslatableComponent setupTitle = Utils.localize("gui.securitycraft:briefcase.setupTitle");
	private EditBox keycodeTextbox;
	private Button saveAndContinueButton;

	public BriefcaseSetupScreen(GenericContainer container, Inventory inv, Component text) {
		super(container, inv, text);
	}

	@Override
	public void init() {
		super.init();
		minecraft.keyboardHandler.setSendRepeatsToGui(true);
		addRenderableWidget(saveAndContinueButton = new IdButton(0, width / 2 - 48, height / 2 + 30 + 10, 100, 20, Utils.localize("gui.securitycraft:password.save"), this::actionPerformed));
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
	public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		super.render(matrix, mouseX, mouseY, partialTicks);
		drawString(matrix, font, "CODE:", width / 2 - 67, height / 2 - 47 + 2, 4210752);
	}

	@Override
	protected void renderLabels(PoseStack matrix, int mouseX, int mouseY) {
		font.draw(matrix, setupTitle, imageWidth / 2 - font.width(setupTitle) / 2, 6, 4210752);
	}

	@Override
	protected void renderBg(PoseStack matrix, float partialTicks, int mouseX, int mouseY) {
		renderBackground(matrix);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(TEXTURE);
		int startX = (width - imageWidth) / 2;
		int startY = (height - imageHeight) / 2;
		this.blit(matrix, startX, startY, 0, 0, imageWidth, imageHeight);
	}

	protected void actionPerformed(IdButton button) {
		if(PlayerUtils.isHoldingItem(Minecraft.getInstance().player, SCContent.BRIEFCASE, null)) {
			ItemStack briefcase = PlayerUtils.getSelectedItemStack(Minecraft.getInstance().player, SCContent.BRIEFCASE.get());

			if(!briefcase.hasTag())
				briefcase.setTag(new CompoundTag());

			briefcase.getTag().putString("passcode", keycodeTextbox.getValue());

			if (!briefcase.getTag().contains("owner")) {
				briefcase.getTag().putString("owner", Minecraft.getInstance().player.getName().getString());
				briefcase.getTag().putString("ownerUUID", Minecraft.getInstance().player.getUUID().toString());
			}

			ClientUtils.syncItemNBT(briefcase);
			SecurityCraft.channel.sendToServer(new OpenBriefcaseGui(SCContent.cTypeBriefcase.getRegistryName(), getTitle()));
		}
	}
}
