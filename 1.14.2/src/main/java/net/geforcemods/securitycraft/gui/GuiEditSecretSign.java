package net.geforcemods.securitycraft.gui;

import com.mojang.blaze3d.platform.GlStateManager;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.BlockSecretSignStanding;
import net.geforcemods.securitycraft.blocks.BlockSecretSignWall;
import net.geforcemods.securitycraft.gui.components.GuiButtonClick;
import net.geforcemods.securitycraft.tileentity.TileEntitySecretSign;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.CUpdateSignPacket;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiEditSecretSign extends GuiScreen
{
	private final TileEntitySecretSign te;
	private int updateCounter;
	private int editLine;

	public GuiEditSecretSign(TileEntitySecretSign te)
	{
		this.te = te;
	}

	@Override
	protected void initGui()
	{
		mc.keyboardListener.enableRepeatEvents(true);
		addButton(new GuiButtonClick(0, width / 2 - 100, height / 4 + 120, I18n.format("gui.done"), button -> close()));
		te.setEditable(false);
	}

	@Override
	public void onGuiClosed()
	{
		mc.keyboardListener.enableRepeatEvents(false);

		if(mc.getConnection() != null)
			mc.getConnection().sendPacket(new CUpdateSignPacket(te.getPos(), te.getText(0), te.getText(1), te.getText(2), te.getText(3)));

		te.setEditable(true);
	}

	@Override
	public void tick()
	{
		++updateCounter;
	}

	@Override
	public boolean charTyped(char typedChar, int keyCode)
	{
		if(editLine >= 0 && editLine <= 3)
		{
			String line = te.getText(editLine).getString();

			if(SharedConstants.isAllowedCharacter(typedChar) && this.fontRenderer.getStringWidth(line + typedChar) <= 90)
				line += typedChar;

			te.setText(editLine, new StringTextComponent(line));
			return true;
		}

		return false;
	}

	@Override
	public void close()
	{
		te.markDirty();
		mc.displayGuiScreen((GuiScreen)null);
	}

	@Override
	public boolean keyPressed(int keyCode, int p_keyPressed_2_, int p_keyPressed_3_)
	{
		if(keyCode == 265)
		{
			editLine = editLine - 1 & 3;
			return true;
		}
		else if(keyCode != 264 && keyCode != 257 && keyCode != 335)
		{
			if(keyCode == 259)
			{
				String line = te.getText(editLine).getString();

				if(!line.isEmpty())
				{
					line = line.substring(0, line.length() - 1);
					te.setText(editLine, new StringTextComponent(line));
				}

				return true;
			}
			else
				return super.keyPressed(keyCode, p_keyPressed_2_, p_keyPressed_3_);
		}
		else
		{
			editLine = editLine + 1 & 3;
			return true;
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks)
	{
		BlockState state = te.getBlockState();
		float angle;

		drawDefaultBackground();
		drawCenteredString(fontRenderer, I18n.format("sign.edit"), width / 2, 40, 16777215);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();
		GlStateManager.translatef(width / 2, 0.0F, 50.0F);
		GlStateManager.scalef(-93.75F, -93.75F, -93.75F);
		GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);

		if(state.getBlock() == SCContent.secretSignStanding)
			angle = state.get(BlockSecretSignStanding.ROTATION) * 360 / 16.0F;
		else
			angle = state.get(BlockSecretSignWall.FACING).getHorizontalAngle();

		GlStateManager.rotatef(angle, 0.0F, 1.0F, 0.0F);
		GlStateManager.translatef(0.0F, -1.0625F, 0.0F);

		if(updateCounter / 6 % 2 == 0)
			te.func_214062_a(editLine, te.func_214065_t(), te.func_214067_u(), te.func_214069_r());

		TileEntityRendererDispatcher.instance.render(te, -0.5D, -0.75D, -0.5D, 0.0F);
		te.func_214062_a(-1, te.func_214065_t(), te.func_214067_u(), te.func_214069_r());
		GlStateManager.popMatrix();
		super.render(mouseX, mouseY, partialTicks);
	}
}