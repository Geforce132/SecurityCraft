package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.containers.GenericTEContainer;
import net.geforcemods.securitycraft.network.server.SetPassword;
import net.geforcemods.securitycraft.screen.components.IdButton;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SetPasswordScreen extends ContainerScreen<GenericTEContainer> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private TileEntity tileEntity;
	private String blockName;
	private TextFieldWidget keycodeTextbox;
	private IdButton saveAndContinueButton;

	public SetPasswordScreen(GenericTEContainer container, PlayerInventory inv, ITextComponent name){
		super(container, inv, name);
		this.tileEntity = container.te;
		blockName = Utils.localize(tileEntity.getBlockState().getBlock().getTranslationKey()).getFormattedText();
	}

	@Override
	public void init(){
		super.init();

		minecraft.keyboardListener.enableRepeatEvents(true);
		addButton(saveAndContinueButton = new IdButton(0, width / 2 - 48, height / 2 + 30 + 10, 100, 20, Utils.localize("gui.securitycraft:keycardSetup.save").getFormattedText(), this::actionPerformed));
		saveAndContinueButton.active = false;

		addButton(keycodeTextbox = new TextFieldWidget(font, width / 2 - 37, height / 2 - 47, 77, 12, ""));
		keycodeTextbox.setMaxStringLength(20);
		keycodeTextbox.setValidator(s -> s.matches("[0-9]*"));
		keycodeTextbox.setResponder(text -> saveAndContinueButton.active = !text.isEmpty());
		setFocusedDefault(keycodeTextbox);
	}

	@Override
	public void onClose(){
		super.onClose();
		minecraft.keyboardListener.enableRepeatEvents(false);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks){
		super.render(mouseX, mouseY, partialTicks);
		drawString(font, "CODE:", width / 2 - 67, height / 2 - 47 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		String setup = Utils.localize("gui.securitycraft:password.setup").getFormattedText();
		String combined = blockName + " " + setup;

		if(font.getStringWidth(combined) < xSize - 10)
			font.drawString(combined, xSize / 2 - font.getStringWidth(combined) / 2, 6, 4210752);
		else
		{
			font.drawString(blockName, xSize / 2 - font.getStringWidth(blockName) / 2, 6, 4210752);
			font.drawString(setup, xSize / 2 - font.getStringWidth(setup) / 2, 16, 4210752);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		renderBackground();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		this.blit(startX, startY, 0, 0, xSize, ySize);
	}
	protected void actionPerformed(IdButton button){
		((IPasswordProtected) tileEntity).setPassword(keycodeTextbox.getText());
		SecurityCraft.channel.sendToServer(new SetPassword(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ(), keycodeTextbox.getText()));
		ClientUtils.closePlayerScreen();
	}
}
