package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.IMSBlockEntity;
import net.geforcemods.securitycraft.blockentities.IMSBlockEntity.IMSTargetingMode;
import net.geforcemods.securitycraft.network.server.SyncIMSTargetingOption;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

@OnlyIn(Dist.CLIENT)
public class IMSScreen extends Screen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private final TranslationTextComponent target = Utils.localize("gui.securitycraft:ims.target");
	private int imageWidth = 176;
	private int imageHeight = 166;
	private int leftPos;
	private int topPos;
	private IMSBlockEntity tileEntity;
	private Button targetButton;
	private IMSTargetingMode targetMode;

	public IMSScreen(IMSBlockEntity te) {
		super(te.getName());
		tileEntity = te;
		targetMode = tileEntity.getTargetingMode();
	}

	@Override
	public void init() {
		super.init();

		leftPos = (width - imageWidth) / 2;
		topPos = (height - imageHeight) / 2;
		addButton(targetButton = new ExtendedButton(width / 2 - 75, height / 2 - 38, 150, 20, StringTextComponent.EMPTY, this::targetButtonClicked));
		updateButtonText();
	}

	@Override
	public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrix);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(TEXTURE);
		blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		super.render(matrix, mouseX, mouseY, partialTicks);
		font.draw(matrix, title, width / 2 - font.width(title) / 2, topPos + 6, 4210752);
		font.draw(matrix, target, width / 2 - font.width(target) / 2, topPos + 30, 4210752);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (minecraft.options.keyInventory.isActiveAndMatches(InputMappings.getKey(keyCode, scanCode))) {
			onClose();
			return true;
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	protected void targetButtonClicked(Button button) {
		targetMode = IMSTargetingMode.values()[(targetMode.ordinal() + 1) % IMSTargetingMode.values().length]; //next enum value
		tileEntity.setTargetingMode(targetMode);
		SecurityCraft.channel.sendToServer(new SyncIMSTargetingOption(tileEntity.getBlockPos(), tileEntity.getTargetingMode()));
		updateButtonText();
	}

	private void updateButtonText() {
		targetButton.setMessage(Utils.localize("gui.securitycraft:srat.targets" + (((targetMode.ordinal() + 2) % 3) + 1)));
	}
}
