package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.IMSBlockEntity;
import net.geforcemods.securitycraft.blockentities.IMSBlockEntity.IMSTargetingMode;
import net.geforcemods.securitycraft.containers.GenericTEContainer;
import net.geforcemods.securitycraft.network.server.SyncIMSTargetingOption;
import net.geforcemods.securitycraft.screen.components.IdButton;
import net.geforcemods.securitycraft.screen.components.ToggleComponentButton;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IMSScreen extends AbstractContainerScreen<GenericTEContainer>{

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private final TranslatableComponent imsName = Utils.localize(SCContent.IMS.get().getDescriptionId());
	private final TranslatableComponent target = Utils.localize("gui.securitycraft:ims.target");
	private IMSBlockEntity tileEntity;
	private IMSTargetingMode targetMode;

	public IMSScreen(GenericTEContainer container, Inventory inv, Component name) {
		super(container, inv, name);
		tileEntity = (IMSBlockEntity)container.te;
		targetMode = tileEntity.getTargetingMode();
	}

	@Override
	public void init(){
		super.init();

		addRenderableWidget(new ToggleComponentButton(0, width / 2 - 75, height / 2 - 38, 150, 20, this::updateButtonText, targetMode.ordinal(), 3, this::actionPerformed));
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void renderLabels(PoseStack matrix, int mouseX, int mouseY){

		font.draw(matrix, imsName, imageWidth / 2 - font.width(imsName) / 2, 6, 4210752);
		font.draw(matrix, target, imageWidth / 2 - font.width(target) / 2, 30, 4210752);
	}

	@Override
	protected void renderBg(PoseStack matrix, float partialTicks, int mouseX, int mouseY) {
		renderBackground(matrix);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem._setShaderTexture(0, TEXTURE);
		int startX = (width - imageWidth) / 2;
		int startY = (height - imageHeight) / 2;
		this.blit(matrix, startX, startY, 0, 0, imageWidth, imageHeight);
	}

	protected void actionPerformed(IdButton button){
		targetMode = IMSTargetingMode.values()[((ToggleComponentButton)button).getCurrentIndex()];
		tileEntity.setTargetingMode(targetMode);
		SecurityCraft.channel.sendToServer(new SyncIMSTargetingOption(tileEntity.getBlockPos(), tileEntity.getTargetingMode()));
	}

	private Component updateButtonText(int index) {
		return Utils.localize("gui.securitycraft:srat.targets" + ((index + 2) % 3 + 1));
	}

}
