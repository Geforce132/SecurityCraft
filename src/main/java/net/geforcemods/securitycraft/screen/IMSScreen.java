package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.IMSBlockEntity;
import net.geforcemods.securitycraft.blockentities.IMSBlockEntity.IMSTargetingMode;
import net.geforcemods.securitycraft.inventory.GenericTEMenu;
import net.geforcemods.securitycraft.network.server.SyncIMSTargetingOption;
import net.geforcemods.securitycraft.screen.components.ToggleComponentButton;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class IMSScreen extends AbstractContainerScreen<GenericTEMenu> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private final TranslatableComponent imsName = Utils.localize(SCContent.IMS.get().getDescriptionId());
	private final TranslatableComponent target = Utils.localize("gui.securitycraft:ims.target");
	private IMSBlockEntity be;
	private IMSTargetingMode targetMode;

	public IMSScreen(GenericTEMenu menu, Inventory inv, Component text) {
		super(menu, inv, text);
		be = (IMSBlockEntity) menu.be;
		targetMode = be.getTargetingMode();
	}

	@Override
	public void init() {
		super.init();

		addRenderableWidget(new ToggleComponentButton(width / 2 - 75, height / 2 - 38, 150, 20, this::updateButtonText, targetMode.ordinal(), 3, this::modeButtonClicked));
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		font.draw(pose, imsName, imageWidth / 2 - font.width(imsName) / 2, 6, 4210752);
		font.draw(pose, target, imageWidth / 2 - font.width(target) / 2, 30, 4210752);
	}

	@Override
	protected void renderBg(PoseStack pose, float partialTicks, int mouseX, int mouseY) {
		renderBackground(pose);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem._setShaderTexture(0, TEXTURE);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	private void modeButtonClicked(Button button) {
		targetMode = IMSTargetingMode.values()[((ToggleComponentButton) button).getCurrentIndex()];
		be.setTargetingMode(targetMode);
		SecurityCraft.channel.sendToServer(new SyncIMSTargetingOption(be.getBlockPos(), be.getTargetingMode()));
	}

	private Component updateButtonText(int index) {
		return Utils.localize("gui.securitycraft:srat.targets" + ((index + 2) % 3 + 1));
	}
}
