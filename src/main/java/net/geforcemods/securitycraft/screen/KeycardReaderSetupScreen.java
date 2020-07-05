package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.GenericTEContainer;
import net.geforcemods.securitycraft.network.server.SetKeycardLevel;
import net.geforcemods.securitycraft.screen.components.ClickButton;
import net.geforcemods.securitycraft.tileentity.KeycardReaderTileEntity;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KeycardReaderSetupScreen extends ContainerScreen<GenericTEContainer>{

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private KeycardReaderTileEntity te;
	private ClickButton lvlOfSecurityButton;
	private ClickButton requiresExactCardButton;
	private boolean requiresExactCard = false;
	private int lvlOfSecurity = 0;

	public KeycardReaderSetupScreen(GenericTEContainer container, PlayerInventory inv, ITextComponent name) {
		super(container, inv, name);
		te = (KeycardReaderTileEntity)container.te;
	}

	@Override
	public void func_231160_c_(){
		super.func_231160_c_();

		func_230480_a_(lvlOfSecurityButton = new ClickButton(0, field_230708_k_ / 2 - (48 * 2 - 23), field_230709_l_ / 2 + 20, 150, 20, "", this::actionPerformed));
		func_230480_a_(requiresExactCardButton = new ClickButton(1, field_230708_k_ / 2 - (48 * 2 - 11), field_230709_l_ / 2 - 28, 125, 20, requiresExactCard ? ClientUtils.localize("gui.securitycraft:keycardSetup.equal") : ClientUtils.localize("gui.securitycraft:keycardSetup.equalOrHigher"), this::actionPerformed));
		func_230480_a_(new ClickButton(2, field_230708_k_ / 2 - 48, field_230709_l_ / 2 + 30 + 20, 100, 20, ClientUtils.localize("gui.securitycraft:keycardSetup.save"), this::actionPerformed));

		updateButtonText();
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void func_230451_b_(MatrixStack matrix, int mouseX, int mouseY)
	{
		field_230712_o_.drawString(ClientUtils.localize("gui.securitycraft:keycardSetup.explanation.1"), xSize / 2 - field_230712_o_.getStringWidth(ClientUtils.localize("gui.securitycraft:keycardSetup.explanation.1")) / 2, 6, 4210752);
		field_230712_o_.drawString(ClientUtils.localize("gui.securitycraft:keycardSetup.explanation.2"), xSize / 2 - field_230712_o_.getStringWidth(ClientUtils.localize("gui.securitycraft:keycardSetup.explanation.2")) / 2 - 2, 30 - 10, 4210752);
		field_230712_o_.drawString(ClientUtils.localize("gui.securitycraft:keycardSetup.explanation.3"), xSize / 2 - field_230712_o_.getStringWidth(ClientUtils.localize("gui.securitycraft:keycardSetup.explanation.3")) / 2 - 11, 42 - 10, 4210752);
		field_230712_o_.drawString(ClientUtils.localize("gui.securitycraft:keycardSetup.explanation.4"), xSize / 2 - field_230712_o_.getStringWidth(ClientUtils.localize("gui.securitycraft:keycardSetup.explanation.4")) / 2 - 10, 54 - 10, 4210752);
		field_230712_o_.drawString(ClientUtils.localize("gui.securitycraft:keycardSetup.explanation.5"), xSize / 2 + 45, 66 - 5, 4210752);
		field_230712_o_.drawString(ClientUtils.localize("gui.securitycraft:keycardSetup.explanation.6"), xSize / 2 - field_230712_o_.getStringWidth(ClientUtils.localize("gui.securitycraft:keycardSetup.explanation.6")) / 2 - 6, 78 - 1, 4210752);
		field_230712_o_.drawString(ClientUtils.localize("gui.securitycraft:keycardSetup.explanation.7"), xSize / 2 - field_230712_o_.getStringWidth(ClientUtils.localize("gui.securitycraft:keycardSetup.explanation.7")) / 2 - 20, 90 - 1, 4210752);
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

	private void updateButtonText(){
		lvlOfSecurity++;
		if(lvlOfSecurity <= 5)
			lvlOfSecurityButton.func_238482_a_(ClientUtils.localize("gui.securitycraft:keycardSetup.lvlNeeded") + " " + lvlOfSecurity);
		else{
			lvlOfSecurity = 1;
			lvlOfSecurityButton.func_238482_a_(ClientUtils.localize("gui.securitycraft:keycardSetup.lvlNeeded") + " " + lvlOfSecurity);

		}
	}

	protected void actionPerformed(ClickButton button){
		switch(button.id){
			case 0:
				updateButtonText();
				break;

			case 1:
				requiresExactCard = !requiresExactCard;
				requiresExactCardButton.func_238482_a_(requiresExactCard ? ClientUtils.localize("gui.securitycraft:keycardSetup.equal") : ClientUtils.localize("gui.securitycraft:keycardSetup.equalOrHigher"));
				break;

			case 2:
				saveLvls();
				break;
		}
	}

	private void saveLvls() {
		te.setPassword(String.valueOf(lvlOfSecurity));
		te.setRequiresExactKeycard(requiresExactCard);

		SecurityCraft.channel.sendToServer(new SetKeycardLevel(te.getPos().getX(), te.getPos().getY(), te.getPos().getZ(), lvlOfSecurity, requiresExactCard));

		Minecraft.getInstance().player.closeScreen();
	}

}
