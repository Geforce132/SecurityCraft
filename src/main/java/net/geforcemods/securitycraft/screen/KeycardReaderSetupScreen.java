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
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KeycardReaderSetupScreen extends ContainerScreen<GenericTEContainer>{

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private final TranslationTextComponent explanation1 = ClientUtils.localize("gui.securitycraft:keycardSetup.explanation.1");
	private final TranslationTextComponent explanation2 = ClientUtils.localize("gui.securitycraft:keycardSetup.explanation.2");
	private final TranslationTextComponent explanation3 = ClientUtils.localize("gui.securitycraft:keycardSetup.explanation.3");
	private final TranslationTextComponent explanation4 = ClientUtils.localize("gui.securitycraft:keycardSetup.explanation.4");
	private final TranslationTextComponent explanation5 = ClientUtils.localize("gui.securitycraft:keycardSetup.explanation.5");
	private final TranslationTextComponent explanation6 = ClientUtils.localize("gui.securitycraft:keycardSetup.explanation.6");
	private final TranslationTextComponent explanation7 = ClientUtils.localize("gui.securitycraft:keycardSetup.explanation.7");
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
	public void init(){
		super.init();

		addButton(lvlOfSecurityButton = new ClickButton(0, width / 2 - (48 * 2 - 23), height / 2 + 20, 150, 20, "", this::actionPerformed));
		addButton(requiresExactCardButton = new ClickButton(1, width / 2 - (48 * 2 - 11), height / 2 - 28, 125, 20, requiresExactCard ? ClientUtils.localize("gui.securitycraft:keycardSetup.equal") : ClientUtils.localize("gui.securitycraft:keycardSetup.equalOrHigher"), this::actionPerformed));
		addButton(new ClickButton(2, width / 2 - 48, height / 2 + 30 + 20, 100, 20, ClientUtils.localize("gui.securitycraft:keycardSetup.save"), this::actionPerformed));

		updateButtonText();
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrix, int mouseX, int mouseY)
	{
		font.func_243248_b(matrix, explanation1, xSize / 2 - font.func_238414_a_(explanation1) / 2, 6, 4210752);
		font.func_243248_b(matrix, explanation2, xSize / 2 - font.func_238414_a_(explanation2) / 2 - 2, 30 - 10, 4210752);
		font.func_243248_b(matrix, explanation3, xSize / 2 - font.func_238414_a_(explanation3) / 2 - 11, 42 - 10, 4210752);
		font.func_243248_b(matrix, explanation4, xSize / 2 - font.func_238414_a_(explanation4) / 2 - 10, 54 - 10, 4210752);
		font.func_243248_b(matrix, explanation5, xSize / 2 + 45, 66 - 5, 4210752);
		font.func_243248_b(matrix, explanation6, xSize / 2 - font.func_238414_a_(explanation6) / 2 - 6, 78 - 1, 4210752);
		font.func_243248_b(matrix, explanation7, xSize / 2 - font.func_238414_a_(explanation7) / 2 - 20, 90 - 1, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
		renderBackground(matrix);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		this.blit(matrix, startX, startY, 0, 0, xSize, ySize);
	}

	private void updateButtonText(){
		lvlOfSecurity++;
		if(lvlOfSecurity <= 5)
			lvlOfSecurityButton.setMessage(ClientUtils.localize("gui.securitycraft:keycardSetup.lvlNeeded", lvlOfSecurity));
		else{
			lvlOfSecurity = 1;
			lvlOfSecurityButton.setMessage(ClientUtils.localize("gui.securitycraft:keycardSetup.lvlNeeded", lvlOfSecurity));

		}
	}

	protected void actionPerformed(ClickButton button){
		switch(button.id){
			case 0:
				updateButtonText();
				break;

			case 1:
				requiresExactCard = !requiresExactCard;
				requiresExactCardButton.setMessage(requiresExactCard ? ClientUtils.localize("gui.securitycraft:keycardSetup.equal") : ClientUtils.localize("gui.securitycraft:keycardSetup.equalOrHigher"));
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
