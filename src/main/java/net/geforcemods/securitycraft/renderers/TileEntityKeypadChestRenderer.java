package net.geforcemods.securitycraft.renderers;

import java.util.Calendar;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelLargeChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage){
		doRender((TileEntityChest) te, x, y, z, partialTicks, destroyStage);
	}

	public void doRender(TileEntityChest te, double x, double y, double z, float partialTicks, int destroyStage)
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
				((BlockChest)block).checkForSurroundingChests(te.getWorld(), te.getPos(), te.getWorld().getBlockState(te.getPos()));
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

				if (destroyStage >= 0)
				{
					bindTexture(DESTROY_STAGES[destroyStage]);
					GlStateManager.matrixMode(5890);
					GlStateManager.pushMatrix();
					GlStateManager.scale(4.0F, 4.0F, 1.0F);
					GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
					GlStateManager.matrixMode(5888);
				}
				else if (isChristmas)
					bindTexture(christmasNormal);
				else if(te.lidAngle >= 0.9)
					bindTexture(normalSingleActive);
				else
					bindTexture(normalSingleUnactive);
			}
			else
			{
				model = largeModel;

				if (destroyStage >= 0)
				{
					bindTexture(DESTROY_STAGES[destroyStage]);
					GlStateManager.matrixMode(5890);
					GlStateManager.pushMatrix();
					GlStateManager.scale(8.0F, 4.0F, 1.0F);
					GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
					GlStateManager.matrixMode(5888);
				}
				else if (isChristmas)
					bindTexture(christmasDouble);
				else if(te.lidAngle >= 0.9)
					bindTexture(normalDoubleActive);
				else
					bindTexture(normalDoubleUnactive);
			}

			GlStateManager.pushMatrix();
			GlStateManager.enableRescaleNormal();

			if (destroyStage < 0)
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

			GlStateManager.translate((float)x, (float)y + 1.0F, (float)z + 1.0F);
			GlStateManager.scale(1.0F, -1.0F, -1.0F);
			GlStateManager.translate(0.5F, 0.5F, 0.5F);
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
				GlStateManager.translate(1.0F, 0.0F, 0.0F);

			if (meta == 5 && te.adjacentChestZPos != null)
				GlStateManager.translate(0.0F, 0.0F, -1.0F);

			GlStateManager.rotate(short1, 0.0F, 1.0F, 0.0F);
			GlStateManager.translate(-0.5F, -0.5F, -0.5F);
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
			GlStateManager.disableRescaleNormal();
			GlStateManager.popMatrix();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

			if (destroyStage >= 0)
			{
				GlStateManager.matrixMode(5890);
				GlStateManager.popMatrix();
				GlStateManager.matrixMode(5888);
			}
		}
	}
}