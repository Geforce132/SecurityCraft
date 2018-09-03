package net.geforcemods.securitycraft.renderers;

import java.util.Calendar;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelLargeChest;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class TileEntityKeypadChestRenderer extends TileEntitySpecialRenderer
{
	private static final ResourceLocation christmasDouble = new ResourceLocation("securitycraft:textures/entity/chest/christmas_double.png");
	private static final ResourceLocation normalDoubleUnactive = new ResourceLocation("securitycraft:textures/entity/chest/doubleChestUnactive.png");
	private static final ResourceLocation normalDoubleActive = new ResourceLocation("securitycraft:textures/entity/chest/doubleChestActive.png");
	private static final ResourceLocation christmasNormal = new ResourceLocation("securitycraft:textures/entity/chest/christmas.png");
	private static final ResourceLocation normalSingleUnactive = new ResourceLocation("securitycraft:textures/entity/chest/chestUnactive.png");
	private static final ResourceLocation normalSingleActive = new ResourceLocation("securitycraft:textures/entity/chest/chestActive.png");
	private static final ModelChest smallModel = new ModelChest();
	private static final ModelChest largeModel = new ModelLargeChest();
	private boolean isChristmas;

	public TileEntityKeypadChestRenderer()
	{
		Calendar calendar = Calendar.getInstance();

		if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26)
			isChristmas = true;
	}

	public void renderTileEntityAt(TileEntityChest te, double x, double y, double z, float partialTicks)
	{
		int meta;

		if (!te.hasWorldObj())
			meta = 0;
		else
		{
			Block block = te.getBlockType();
			meta = te.getBlockMetadata();

			if (block instanceof BlockChest && meta == 0)
			{
				try
				{
					((BlockChest)block).initMetadata(te.getWorld(), te.xCoord, te.yCoord, te.zCoord);
				}
				catch (ClassCastException e)
				{
					FMLLog.severe("Attempted to render a chest at %d,  %d, %d that was not a chest", te.xCoord, te.yCoord, te.zCoord);
				}
				meta = te.getBlockMetadata();
			}

			te.checkForAdjacentChests();
		}

		if (te.adjacentChestZNeg == null && te.adjacentChestXNeg == null)
		{
			ModelChest model;

			if (te.adjacentChestXPos == null && te.adjacentChestZPos == null)
			{
				model = smallModel;

				if (isChristmas)
					bindTexture(christmasNormal);
				else if(te.lidAngle >= 0.9)
					bindTexture(normalSingleActive);
				else
					bindTexture(normalSingleUnactive);
			}
			else
			{
				model = largeModel;

				if (isChristmas)
					bindTexture(christmasDouble);
				else if(te.lidAngle >= 0.9)
					bindTexture(normalDoubleActive);
				else
					bindTexture(normalDoubleUnactive);
			}

			GL11.glPushMatrix();
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glTranslatef((float)x, (float)y + 1.0F, (float)z + 1.0F);
			GL11.glScalef(1.0F, -1.0F, -1.0F);
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
			short short1 = 0;

			if (meta == 2)
				short1 = 180;

			if (meta == 3)
				short1 = 0;

			if (meta == 4)
				short1 = 90;

			if (meta == 5)
				short1 = -90;

			if (meta == 2 && te.adjacentChestXPos != null)
				GL11.glTranslatef(1.0F, 0.0F, 0.0F);

			if (meta == 5 && te.adjacentChestZPos != null)
				GL11.glTranslatef(0.0F, 0.0F, -1.0F);

			GL11.glRotatef(short1, 0.0F, 1.0F, 0.0F);
			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
			float angle = te.prevLidAngle + (te.lidAngle - te.prevLidAngle) * partialTicks;
			float adjacentAngle;

			if (te.adjacentChestZNeg != null)
			{
				adjacentAngle = te.adjacentChestZNeg.prevLidAngle + (te.adjacentChestZNeg.lidAngle - te.adjacentChestZNeg.prevLidAngle) * partialTicks;

				if (adjacentAngle > angle)
					angle = adjacentAngle;
			}

			if (te.adjacentChestXNeg != null)
			{
				adjacentAngle = te.adjacentChestXNeg.prevLidAngle + (te.adjacentChestXNeg.lidAngle - te.adjacentChestXNeg.prevLidAngle) * partialTicks;

				if (adjacentAngle > angle)
					angle = adjacentAngle;
			}

			angle = 1.0F - angle;
			angle = 1.0F - angle * angle * angle;
			model.chestLid.rotateAngleX = -(angle * (float)Math.PI / 2.0F);
			model.renderAll();
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glPopMatrix();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		}
	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks)
	{
		this.renderTileEntityAt((TileEntityChest)te, x, y, z, partialTicks);
	}
}