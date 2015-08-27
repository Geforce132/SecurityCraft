package net.breakinbad.securitycraft.renderers;

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
    private static final ResourceLocation trappedDouble = new ResourceLocation("textures/entity/chest/trapped_double.png");
    private static final ResourceLocation christmasDouble = new ResourceLocation("securitycraft:textures/entity/chest/christmas_double.png");
    private static final ResourceLocation normalDoubleUnactive = new ResourceLocation("securitycraft:textures/entity/chest/doubleChestUnactive.png");
    private static final ResourceLocation normalDoubleActive = new ResourceLocation("securitycraft:textures/entity/chest/doubleChestActive.png");
    private static final ResourceLocation trappedNormal = new ResourceLocation("textures/entity/chest/trapped.png");
    private static final ResourceLocation christmasNormal = new ResourceLocation("securitycraft:textures/entity/chest/christmas.png");
    private static final ResourceLocation normalSingleUnactive = new ResourceLocation("securitycraft:textures/entity/chest/chestUnactive.png");
    private static final ResourceLocation normalSingleActive = new ResourceLocation("securitycraft:textures/entity/chest/chestActive.png");
    private ModelChest field_147510_h = new ModelChest();
    private ModelChest field_147511_i = new ModelLargeChest();
    private boolean field_147509_j;

    public TileEntityKeypadChestRenderer()
    {
        Calendar calendar = Calendar.getInstance();

        if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26)
        {
            this.field_147509_j = true;
        }
    }

    public void renderTileEntityAt(TileEntityChest p_147502_1_, double p_147502_2_, double p_147502_4_, double p_147502_6_, float p_147502_8_)
    {
        int i;

        if (!p_147502_1_.hasWorldObj())
        {
            i = 0;
        }
        else
        {
            Block block = p_147502_1_.getBlockType();
            i = p_147502_1_.getBlockMetadata();

            if (block instanceof BlockChest && i == 0)
            {
                try
                {
                ((BlockChest)block).func_149954_e(p_147502_1_.getWorldObj(), p_147502_1_.xCoord, p_147502_1_.yCoord, p_147502_1_.zCoord);
                }
                catch (ClassCastException e)
                {
                    FMLLog.severe("Attempted to render a chest at %d,  %d, %d that was not a chest", p_147502_1_.xCoord, p_147502_1_.yCoord, p_147502_1_.zCoord);
                }
                i = p_147502_1_.getBlockMetadata();
            }

            p_147502_1_.checkForAdjacentChests();
        }

        if (p_147502_1_.adjacentChestZNeg == null && p_147502_1_.adjacentChestXNeg == null)
        {
            ModelChest modelchest;

            if (p_147502_1_.adjacentChestXPos == null && p_147502_1_.adjacentChestZPos == null)
            {
                modelchest = this.field_147510_h;

                if (p_147502_1_.func_145980_j() == 1)
                {
                    this.bindTexture(trappedNormal);
                }
                else if (this.field_147509_j)
                {
                    this.bindTexture(christmasNormal);
                }
                else
                {
                	if(p_147502_1_.lidAngle >= 0.9){
                		this.bindTexture(normalSingleActive);
                	}else{
                		this.bindTexture(normalSingleUnactive);
                	}
                }
            }
            else
            {
                modelchest = this.field_147511_i;

                if (p_147502_1_.func_145980_j() == 1)
                {
                    this.bindTexture(trappedDouble);
                }
                else if (this.field_147509_j)
                {
                    this.bindTexture(christmasDouble);
                }
                else
                {
                	if(p_147502_1_.lidAngle >= 0.9){
                		this.bindTexture(normalDoubleActive);
                	}else{
                		this.bindTexture(normalDoubleUnactive);
                	}
                }
            }

            GL11.glPushMatrix();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glTranslatef((float)p_147502_2_, (float)p_147502_4_ + 1.0F, (float)p_147502_6_ + 1.0F);
            GL11.glScalef(1.0F, -1.0F, -1.0F);
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            short short1 = 0;

            if (i == 2)
            {
                short1 = 180;
            }

            if (i == 3)
            {
                short1 = 0;
            }

            if (i == 4)
            {
                short1 = 90;
            }

            if (i == 5)
            {
                short1 = -90;
            }

            if (i == 2 && p_147502_1_.adjacentChestXPos != null)
            {
                GL11.glTranslatef(1.0F, 0.0F, 0.0F);
            }

            if (i == 5 && p_147502_1_.adjacentChestZPos != null)
            {
                GL11.glTranslatef(0.0F, 0.0F, -1.0F);
            }

            GL11.glRotatef((float)short1, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            float f1 = p_147502_1_.prevLidAngle + (p_147502_1_.lidAngle - p_147502_1_.prevLidAngle) * p_147502_8_;
            float f2;

            if (p_147502_1_.adjacentChestZNeg != null)
            {
                f2 = p_147502_1_.adjacentChestZNeg.prevLidAngle + (p_147502_1_.adjacentChestZNeg.lidAngle - p_147502_1_.adjacentChestZNeg.prevLidAngle) * p_147502_8_;

                if (f2 > f1)
                {
                    f1 = f2;
                }
            }

            if (p_147502_1_.adjacentChestXNeg != null)
            {
                f2 = p_147502_1_.adjacentChestXNeg.prevLidAngle + (p_147502_1_.adjacentChestXNeg.lidAngle - p_147502_1_.adjacentChestXNeg.prevLidAngle) * p_147502_8_;

                if (f2 > f1)
                {
                    f1 = f2;
                }
            }

            f1 = 1.0F - f1;
            f1 = 1.0F - f1 * f1 * f1;
            modelchest.chestLid.rotateAngleX = -(f1 * (float)Math.PI / 2.0F);
            modelchest.renderAll();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glPopMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    public void renderTileEntityAt(TileEntity p_147500_1_, double p_147500_2_, double p_147500_4_, double p_147500_6_, float p_147500_8_)
    {
        this.renderTileEntityAt((TileEntityChest)p_147500_1_, p_147500_2_, p_147500_4_, p_147500_6_, p_147500_8_);
    }
}