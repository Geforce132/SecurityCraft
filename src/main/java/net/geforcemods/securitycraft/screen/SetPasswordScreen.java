package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.inventory.GenericTEMenu;
import net.geforcemods.securitycraft.network.server.SetPassword;
import net.geforcemods.securitycraft.screen.components.IdButton;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SetPasswordScreen extends AbstractContainerScreen<GenericTEMenu> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private BlockEntity tileEntity;
	private TranslatableComponent blockName;
	private TranslatableComponent setup;
	private MutableComponent combined;
	private EditBox keycodeTextbox;
	private IdButton saveAndContinueButton;

	public SetPasswordScreen(GenericTEMenu container, Inventory inv, Component name){
		super(container, inv, name);
		this.tileEntity = container.te;
		blockName = Utils.localize(tileEntity.getBlockState().getBlock().getDescriptionId());
		setup = Utils.localize("gui.securitycraft:password.setup");
		combined = blockName.plainCopy().append(new TextComponent(" ")).append(setup);
	}

	@Override
	public void init(){
		super.init();

		minecraft.keyboardHandler.setSendRepeatsToGui(true);
		addRenderableWidget(saveAndContinueButton = new IdButton(0, width / 2 - 48, height / 2 + 30 + 10, 100, 20, Utils.localize("gui.securitycraft:password.save"), this::actionPerformed));
		saveAndContinueButton.active = false;

		addRenderableWidget(keycodeTextbox = new EditBox(font, width / 2 - 37, height / 2 - 47, 77, 12, TextComponent.EMPTY));
		keycodeTextbox.setMaxLength(20);
		keycodeTextbox.setFilter(s -> s.matches("[0-9]*"));
		keycodeTextbox.setResponder(text -> saveAndContinueButton.active = !text.isEmpty());
		setInitialFocus(keycodeTextbox);
	}

	@Override
	public void removed(){
		super.removed();
		minecraft.keyboardHandler.setSendRepeatsToGui(false);
	}

	@Override
	public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks){
		super.render(matrix, mouseX, mouseY, partialTicks);
		drawString(matrix, font, "CODE:", width / 2 - 67, height / 2 - 47 + 2, 4210752);
	}

	@Override
	protected void renderLabels(PoseStack matrix, int mouseX, int mouseY){
		if(font.width(combined) < imageWidth - 10)
			font.draw(matrix, combined, imageWidth / 2 - font.width(combined) / 2, 6, 4210752);
		else
		{
			font.draw(matrix, blockName, imageWidth / 2 - font.width(blockName) / 2, 6.0F, 4210752);
			font.draw(matrix, setup, imageWidth / 2 - font.width(setup) / 2, 16, 4210752);
		}
	}

	@Override
	protected void renderBg(PoseStack matrix, float partialTicks, int mouseX, int mouseY){
		renderBackground(matrix);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem._setShaderTexture(0, TEXTURE);
		int startX = (width - imageWidth) / 2;
		int startY = (height - imageHeight) / 2;
		this.blit(matrix, startX, startY, 0, 0, imageWidth, imageHeight);
	}

	protected void actionPerformed(IdButton button){
		((IPasswordProtected) tileEntity).setPassword(keycodeTextbox.getValue());
		SecurityCraft.channel.sendToServer(new SetPassword(tileEntity.getBlockPos().getX(), tileEntity.getBlockPos().getY(), tileEntity.getBlockPos().getZ(), keycodeTextbox.getValue()));
		Minecraft.getInstance().player.closeContainer();
	}
}
