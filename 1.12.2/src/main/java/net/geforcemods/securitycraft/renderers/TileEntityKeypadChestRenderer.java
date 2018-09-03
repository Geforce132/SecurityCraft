package net.geforcemods.securitycraft.renderers;

import java.util.Calendar;

import net.geforcemods.securitycraft.tileentity.TileEntityKeypadChest;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelLargeChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityKeypadChestRenderer extends TileEntitySpecialRenderer<TileEntityKeypadChest>
{
	private static final ResourceLocation christmasDouble = new ResourceLocation("securitycraft:textures/entity/chest/christmas_double.png");
	private static final ResourceLocation normalDoubleUnactive = new ResourceLocation("securitycraft:textures/entity/chest/double_chest_unactive.png");
	private static final ResourceLocation normalDoubleActive = new ResourceLocation("securitycraft:textures/entity/chest/double_chest_active.png");
	private static final ResourceLocation christmasNormal = new ResourceLocation("securitycraft:textures/entity/chest/christmas.png");
	private static final ResourceLocation normalSingleUnactive = new ResourceLocation("securitycraft:textures/entity/chest/chest_unactive.png");
	private static final ResourceLocation normalSingleActive = new ResourceLocation("securitycraft:textures/entity/chest/chest_active.png");
	private static final ModelChest field_147510_h = new ModelChest();
	private static final ModelChest field_147511_i = new ModelLargeChest();
	private boolean field_147509_j;

	public TileEntityKeypadChestRenderer()
	{
		Calendar calendar = Calendar.getInstance();

		if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26)
			field_147509_j = true;
	}

	@Override
	public void render(TileEntityKeypadChest te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
	{
		int j;

		if (!te.hasWorld())
			j = 0;
		else
		{
			Block block = te.getBlockType();
			j = te.getBlockMetadata();

			if (block instanceof BlockChest && j == 0)
			{
				((BlockChest)block).checkForSurroundingChests(te.getWorld(), te.getPos(), te.getWorld().getBlockState(te.getPos()));
				j = te.getBlockMetadata();
			}

			te.checkForAdjacentChests();
		}

		if (te.adjacentChestZNeg == null && te.adjacentChestXNeg == null)
		{
			ModelChest modelchest;

			if (te.adjacentChestXPos == null && te.adjacentChestZPos == null)
			{
				modelchest = field_147510_h;

				if (destroyStage >= 0)
				{
					bindTexture(DESTROY_STAGES[destroyStage]);
					GlStateManager.matrixMode(5890);
					GlStateManager.pushMatrix();
					GlStateManager.scale(4.0F, 4.0F, 1.0F);
					GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
					GlStateManager.matrixMode(5888);
				}
				else if (field_147509_j)
					bindTexture(christmasNormal);
				else if(te.lidAngle >= 0.9)
					bindTexture(normalSingleActive);
				else
					bindTexture(normalSingleUnactive);
			}
			else
			{
				modelchest = field_147511_i;

				if (destroyStage >= 0)
				{
					bindTexture(DESTROY_STAGES[destroyStage]);
					GlStateManager.matrixMode(5890);
					GlStateManager.pushMatrix();
					GlStateManager.scale(8.0F, 4.0F, 1.0F);
					GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
					GlStateManager.matrixMode(5888);
				}
				else if (field_147509_j)
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

			if (j == 2)
				short1 = 180;

			if (j == 3)
				short1 = 0;

			if (j == 4)
				short1 = 90;

			if (j == 5)
				short1 = -90;

			if (j == 2 && te.adjacentChestXPos != null)
				GlStateManager.translate(1.0F, 0.0F, 0.0F);

			if (j == 5 && te.adjacentChestZPos != null)
				GlStateManager.translate(0.0F, 0.0F, -1.0F);

			GlStateManager.rotate(short1, 0.0F, 1.0F, 0.0F);
			GlStateManager.translate(-0.5F, -0.5F, -0.5F);
			float f1 = te.prevLidAngle + (te.lidAngle - te.prevLidAngle) * partialTicks;
			float f2;

			if (te.adjacentChestZNeg != null)
			{
				f2 = te.adjacentChestZNeg.prevLidAngle + (te.adjacentChestZNeg.lidAngle - te.adjacentChestZNeg.prevLidAngle) * partialTicks;

				if (f2 > f1)
					f1 = f2;
			}

			if (te.adjacentChestXNeg != null)
			{
				f2 = te.adjacentChestXNeg.prevLidAngle + (te.adjacentChestXNeg.lidAngle - te.adjacentChestXNeg.prevLidAngle) * partialTicks;

				if (f2 > f1)
					f1 = f2;
			}

			f1 = 1.0F - f1;
			f1 = 1.0F - f1 * f1 * f1;
			modelchest.chestLid.rotateAngleX = -(f1 * (float)Math.PI / 2.0F);
			modelchest.renderAll();
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