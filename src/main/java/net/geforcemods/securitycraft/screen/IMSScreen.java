package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.containers.GenericTEContainer;
import net.geforcemods.securitycraft.screen.components.ClickButton;
import net.geforcemods.securitycraft.tileentity.IMSTileEntity;
import net.geforcemods.securitycraft.tileentity.IMSTileEntity.IMSTargetingMode;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IMSScreen extends ContainerScreen<GenericTEContainer>{

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");

	private IMSTileEntity tileEntity;
	private ClickButton targetButton;
	private int targetingOptionIndex = 0;

	public IMSScreen(GenericTEContainer container, PlayerInventory inv, ITextComponent name) {
		super(container, inv, name);
		tileEntity = (IMSTileEntity)container.te;
		targetingOptionIndex = tileEntity.getTargetingOption().modeIndex;
	}

	@Override
	public void func_231160_c_(){
		super.func_231160_c_();

		func_230480_a_(targetButton = new ClickButton(0, field_230708_k_ / 2 - 38, field_230709_l_ / 2 - 58, 120, 20, tileEntity.getTargetingOption() == IMSTargetingMode.PLAYERS_AND_MOBS ? ClientUtils.localize("gui.securitycraft:ims.hostileAndPlayers") : ClientUtils.localize("tooltip.securitycraft:module.players"), this::actionPerformed));
		updateButtonText();
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void func_230451_b_(MatrixStack matrix, int mouseX, int mouseY){
		String imsName = ClientUtils.localize(SCContent.IMS.get().getTranslationKey());

		field_230712_o_.drawString(imsName, xSize / 2 - field_230712_o_.getStringWidth(imsName) / 2, 6, 4210752);
		field_230712_o_.drawString(ClientUtils.localize("gui.securitycraft:ims.target"), xSize / 2 - 78, 30, 4210752);
	}

	@Override
	protected void func_230450_a_(MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
		func_230446_a_(matrix);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		field_230706_i_.getTextureManager().bindTexture(TEXTURE);
		int startX = (field_230708_k_ - xSize) / 2;
		int startY = (field_230709_l_ - ySize) / 2;
		this.blit(startX, startY, 0, 0, xSize, ySize);
	}

	protected void actionPerformed(ClickButton button){
		if(button.id == 0){
			targetingOptionIndex++;

			if(targetingOptionIndex > (IMSTargetingMode.values().length - 1))
				targetingOptionIndex = 0;

			tileEntity.setTargetingOption(IMSTargetingMode.values()[targetingOptionIndex]);

			ClientUtils.syncTileEntity(tileEntity);

			updateButtonText();
		}
	}

	private void updateButtonText() {
		if(IMSTargetingMode.values()[targetingOptionIndex] == IMSTargetingMode.PLAYERS)
			targetButton.func_238482_a_(ClientUtils.localize("tooltip.securitycraft:module.playerCustomization.players"));
		else if(IMSTargetingMode.values()[targetingOptionIndex] == IMSTargetingMode.PLAYERS_AND_MOBS)
			targetButton.func_238482_a_(ClientUtils.localize("gui.securitycraft:ims.hostileAndPlayers"));
		else if(IMSTargetingMode.values()[targetingOptionIndex] == IMSTargetingMode.MOBS)
			targetButton.func_238482_a_(ClientUtils.localize("gui.securitycraft:ims.hostile"));
	}

}
