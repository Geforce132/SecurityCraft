package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.GenericContainer;
import net.geforcemods.securitycraft.network.server.OpenGui;
import net.geforcemods.securitycraft.screen.components.IdButton;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BriefcaseSetupScreen extends ContainerScreen<GenericContainer> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private final String setupTitle = ClientUtils.localize("gui.securitycraft:briefcase.setupTitle").getFormattedText();
	private TextFieldWidget keycodeTextbox;
	private Button saveAndContinueButton;

	public BriefcaseSetupScreen(GenericContainer container, PlayerInventory inv, ITextComponent text) {
		super(container, inv, text);
	}

	@Override
	public void init() {
		super.init();
		minecraft.keyboardListener.enableRepeatEvents(true);
		addButton(saveAndContinueButton = new IdButton(0, width / 2 - 48, height / 2 + 30 + 10, 100, 20, ClientUtils.localize("gui.securitycraft:keycardSetup.save").getFormattedText(), this::actionPerformed));
		saveAndContinueButton.active = false;

		addButton(keycodeTextbox = new TextFieldWidget(font, width / 2 - 37, height / 2 - 47, 77, 12, ""));
		keycodeTextbox.setMaxStringLength(4);
		keycodeTextbox.setResponder(text -> saveAndContinueButton.active = text.length() == 4);
		setFocusedDefault(keycodeTextbox);
	}

	@Override
	public void onClose() {
		super.onClose();
		minecraft.keyboardListener.enableRepeatEvents(false);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		super.render(mouseX, mouseY, partialTicks);
		RenderSystem.disableLighting();
		keycodeTextbox.render(mouseX, mouseY, partialTicks);
		drawString(font, "CODE:", width / 2 - 67, height / 2 - 47 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		font.drawString(setupTitle, xSize / 2 - font.getStringWidth(setupTitle) / 2, 6, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		renderBackground();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		this.blit(startX, startY, 0, 0, xSize, ySize);
	}
	protected void actionPerformed(IdButton button) {
		if(PlayerUtils.isHoldingItem(Minecraft.getInstance().player, SCContent.BRIEFCASE, null)) {
			ItemStack briefcase = PlayerUtils.getSelectedItemStack(Minecraft.getInstance().player, SCContent.BRIEFCASE.get());

			if(!briefcase.hasTag())
				briefcase.setTag(new CompoundNBT());

			briefcase.getTag().putString("passcode", keycodeTextbox.getText());

			if (!briefcase.getTag().contains("owner")) {
				briefcase.getTag().putString("owner", Minecraft.getInstance().player.getName().getString());
				briefcase.getTag().putString("ownerUUID", Minecraft.getInstance().player.getUniqueID().toString());
			}

			ClientUtils.syncItemNBT(briefcase);
			SecurityCraft.channel.sendToServer(new OpenGui(SCContent.cTypeBriefcase.getRegistryName(), getTitle()));
		}
	}
}
