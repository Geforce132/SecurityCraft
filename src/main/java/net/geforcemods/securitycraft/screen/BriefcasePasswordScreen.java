package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.GenericContainer;
import net.geforcemods.securitycraft.network.server.OpenGui;
import net.geforcemods.securitycraft.screen.components.ClickButton;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BriefcasePasswordScreen extends ContainerScreen<GenericContainer> {

	public static final String UP_ARROW  = "\u2191";
	public static final String DOWN_ARROW  = "\u2193";
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private Button[] keycodeTopButtons = new Button[4];
	private Button[] keycodeBottomButtons = new Button[4];
	private TextFieldWidget[] keycodeTextboxes = new TextFieldWidget[4];
	private Button continueButton;

	public BriefcasePasswordScreen(GenericContainer container, PlayerInventory inv, ITextComponent text) {
		super(container, inv, text);
	}

	@Override
	public void init() {
		super.init();

		for(int i = 0; i < keycodeTopButtons.length; i++) {
			keycodeTopButtons[i] = new ClickButton(i, width / 2 - 40 + (i * 20), height / 2 - 52, 20, 20, UP_ARROW, this::actionPerformed);
			addButton(keycodeTopButtons[i]);
		}

		for(int i = 0; i < keycodeBottomButtons.length; i++) {
			keycodeBottomButtons[i] = new ClickButton(4 + i, width / 2 - 40 + (i * 20), height / 2, 20, 20, DOWN_ARROW, this::actionPerformed);
			addButton(keycodeBottomButtons[i]);
		}

		continueButton = new ClickButton(8, (width / 2 + 42), height / 2 - 26, 20, 20, ">", this::actionPerformed);
		addButton(continueButton);

		for(int i = 0; i < keycodeTextboxes.length; i++) {
			keycodeTextboxes[i] = new TextFieldWidget(font, (width / 2 - 37) + (i * 20), height / 2 - 22, 14, 12, "");

			keycodeTextboxes[i].setTextColor(-1);
			keycodeTextboxes[i].setDisabledTextColour(-1);
			keycodeTextboxes[i].setEnableBackgroundDrawing(true);
			keycodeTextboxes[i].setMaxStringLength(1);
			keycodeTextboxes[i].setText("0");
		}
	}

	@Override
	public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
		super.render(matrix, mouseX, mouseY, partialTicks);
		RenderSystem.disableLighting();

		for(TextFieldWidget textfield : keycodeTextboxes)
			textfield.render(matrix, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void func_230451_b_(MatrixStack matrix, int mouseX, int mouseY) {
		font.drawString(ClientUtils.localize("gui.securitycraft:briefcase.enterPasscode"), xSize / 2 - font.getStringWidth(ClientUtils.localize("gui.securitycraft:briefcase.enterPasscode")) / 2, 6, 4210752);
	}

	@Override
	protected void func_230450_a_(MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
		renderBackground(matrix);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		this.blit(startX, startY, 0, 0, xSize, ySize);
	}

	protected void actionPerformed(ClickButton button) {
		int[] keys = new int[]{Integer.parseInt(keycodeTextboxes[0].getText()), Integer.parseInt(keycodeTextboxes[1].getText()), Integer.parseInt(keycodeTextboxes[2].getText()), Integer.parseInt(keycodeTextboxes[3].getText())};

		switch(button.id) {
			case 0:
				if(keys[0] == 9)
					keys[0] = 0;
				else
					keys[0]++;
				break;
			case 1:
				if(keys[1] == 9)
					keys[1] = 0;
				else
					keys[1]++;
				break;
			case 2:
				if(keys[2] == 9)
					keys[2] = 0;
				else
					keys[2]++;
				break;
			case 3:
				if(keys[3] == 9)
					keys[3] = 0;
				else
					keys[3]++;
				break;
			case 4:
				if(keys[0] == 0)
					keys[0] = 9;
				else
					keys[0]--;
				break;
			case 5:
				if(keys[1] == 0)
					keys[1] = 9;
				else
					keys[1]--;
				break;
			case 6:
				if(keys[2] == 0)
					keys[2] = 9;
				else
					keys[2]--;
				break;
			case 7:
				if(keys[3] == 0)
					keys[3] = 9;
				else
					keys[3]--;
				break;
			case 8:
				if(PlayerUtils.isHoldingItem(Minecraft.getInstance().player, SCContent.BRIEFCASE)) {
					CompoundNBT nbt = Minecraft.getInstance().player.inventory.getCurrentItem().getTag();
					String code = keys[0] + "" + keys[1] + "" +  keys[2] + "" + keys[3];

					if(nbt.getString("passcode").equals(code))
						SecurityCraft.channel.sendToServer(new OpenGui(SCContent.cTypeBriefcaseInventory.getRegistryName(), minecraft.world.getDimension().getType().getId(), minecraft.player.getPosition(), getTitle()));
				}

				break;
		}

		keycodeTextboxes[0].setText(String.valueOf(keys[0]));
		keycodeTextboxes[1].setText(String.valueOf(keys[1]));
		keycodeTextboxes[2].setText(String.valueOf(keys[2]));
		keycodeTextboxes[3].setText(String.valueOf(keys[3]));
	}
}
