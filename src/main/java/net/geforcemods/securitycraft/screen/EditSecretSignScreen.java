package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.platform.GlStateManager;

import net.geforcemods.securitycraft.blocks.SecretStandingSignBlock;
import net.geforcemods.securitycraft.blocks.SecretWallSignBlock;
import net.geforcemods.securitycraft.tileentity.SecretSignTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.fonts.TextInputUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.CUpdateSignPacket;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EditSecretSignScreen extends Screen
{
	private final SecretSignTileEntity te;
	private int updateCounter;
	private int editLine;
	private TextInputUtil textInputUtil;

	public EditSecretSignScreen(SecretSignTileEntity te)
	{
		super(new TranslationTextComponent("sign.edit"));
		this.te = te;
	}

	@Override
	protected void init()
	{
		minecraft.keyboardListener.enableRepeatEvents(true);
		addButton(new Button(width / 2 - 100, height / 4 + 120, 200, 20, I18n.format("gui.done"), button -> close()));
		te.setEditable(false);
		textInputUtil = new TextInputUtil(minecraft, () -> te.getText(editLine).getString(), s -> te.setText(editLine, new StringTextComponent(s)), 90);
	}

	@Override
	public void removed()
	{
		minecraft.keyboardListener.enableRepeatEvents(false);

		if(minecraft.getConnection() != null)
			minecraft.getConnection().sendPacket(new CUpdateSignPacket(te.getPos(), te.getText(0), te.getText(1), te.getText(2), te.getText(3)));

		te.setEditable(true);
	}

	@Override
	public void tick()
	{
		++updateCounter;

		if(!te.getType().isValidBlock(te.getBlockState().getBlock()))
			close();
	}

	private void close()
	{
		te.markDirty();
		minecraft.displayGuiScreen((Screen)null);
	}

	@Override
	public boolean charTyped(char typedChar, int keyCode)
	{
		textInputUtil.func_216894_a(typedChar);
		return true;
	}

	@Override
	public void onClose()
	{
		close();
	}

	@Override
	public boolean keyPressed(int keyCode, int p_keyPressed_2_, int p_keyPressed_3_)
	{
		if(keyCode == 265)
		{
			editLine = editLine - 1 & 3;
			textInputUtil.func_216899_b();
			return true;
		}
		else if(keyCode != 264 && keyCode != 257 && keyCode != 335)
			return textInputUtil.func_216897_a(keyCode) ? true : super.keyPressed(keyCode, p_keyPressed_2_, p_keyPressed_3_);
		else
		{
			editLine = editLine + 1 & 3;
			textInputUtil.func_216899_b();
			return true;
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks)
	{
		BlockState state = te.getBlockState();
		float angle;

		renderBackground();
		drawCenteredString(font, title.getFormattedText(), width / 2, 40, 16777215);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();
		GlStateManager.translatef(width / 2, 0.0F, 50.0F);
		GlStateManager.scalef(-93.75F, -93.75F, -93.75F);
		GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);

		if(state.getBlock() instanceof SecretStandingSignBlock)
			angle = state.get(SecretStandingSignBlock.ROTATION) * 360 / 16.0F;
		else
			angle = state.get(SecretWallSignBlock.FACING).getHorizontalAngle();

		GlStateManager.rotatef(angle, 0.0F, 1.0F, 0.0F);
		GlStateManager.translatef(0.0F, -1.0625F, 0.0F);
		te.func_214062_a(editLine, textInputUtil.func_216896_c(), textInputUtil.func_216898_d(), updateCounter / 6 % 2 == 0);
		TileEntityRendererDispatcher.instance.render(te, -0.5D, -0.75D, -0.5D, 0.0F);
		te.func_214063_g();
		GlStateManager.popMatrix();
		super.render(mouseX, mouseY, partialTicks);
	}
}