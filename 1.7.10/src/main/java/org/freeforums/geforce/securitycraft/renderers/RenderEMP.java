package org.freeforums.geforce.securitycraft.renderers;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.freeforums.geforce.securitycraft.entity.EntityEMP;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderEMP extends Render
{
    private final RenderBlocks sandRenderBlocks = new RenderBlocks();

    public RenderEMP()
    {
        this.shadowSize = 0.5F;
    }

    /**
     * The actual render method that is used in doRender
     */
    public void doRenderFallingSand(EntityEMP par1EntityFallingSand, double par2, double par4, double par6, float par8, float par9)
    {                     
            GL11.glPushMatrix();
            GL11.glTranslatef((float)par2, (float)par4, (float)par6);
            this.bindEntityTexture(par1EntityFallingSand);
            GL11.glDisable(GL11.GL_LIGHTING);

            this.sandRenderBlocks.renderBlockAsItem(mod_SecurityCraft.empEntity, 0, par1EntityFallingSand.getBrightness(par9));
            
            

            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glPopMatrix();
        
    }

    protected ResourceLocation getFallingSandTextures(EntityEMP par1EntityFallingSand)
    {
        return TextureMap.locationBlocksTexture;
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity par1Entity)
    {
        return this.getFallingSandTextures((EntityEMP)par1Entity);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.doRenderFallingSand((EntityEMP)par1Entity, par2, par4, par6, par8, par9);
    }
}

