package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.GenericContainer;
import net.geforcemods.securitycraft.network.server.OpenBriefcaseGui;
import net.geforcemods.securitycraft.screen.components.IdButton;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BriefcaseSetupScreen extends ContainerScreen<GenericContainer> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private final TranslationTextComponent setupTitle = Utils.localize("gui.securitycraft:briefcase.setupTitle");
	private TextFieldWidget keycodeTextbox;
	private Button saveAndContinueButton;

	public BriefcaseSetupScreen(GenericContainer container, PlayerInventory inv, ITextComponent text) {
		super(container, inv, text);
	}

	@Override
	public void init() {
		super.init();
		minecraft.keyboardHandler.setSendRepeatsToGui(true);
		addButton(saveAndContinueButton = new IdButton(0, width / 2 - 48, height / 2 + 30 + 10, 100, 20, Utils.localize("gui.securitycraft:password.save"), this::actionPerformed));
		saveAndContinueButton.active = false;

		addButton(keycodeTextbox = new TextFieldWidget(font, width / 2 - 37, height / 2 - 47, 77, 12, StringTextComponent.EMPTY));
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
	public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
		super.render(matrix, mouseX, mouseY, partialTicks);
		drawString(matrix, font, "CODE:", width / 2 - 67, height / 2 - 47 + 2, 4210752);
	}

	@Override
	protected void renderLabels(MatrixStack matrix, int mouseX, int mouseY) {
		font.draw(matrix, setupTitle, imageWidth / 2 - font.width(setupTitle) / 2, 6, 4210752);
	}

	@Override
	protected void renderBg(MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
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
				briefcase.setTag(new CompoundNBT());

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
