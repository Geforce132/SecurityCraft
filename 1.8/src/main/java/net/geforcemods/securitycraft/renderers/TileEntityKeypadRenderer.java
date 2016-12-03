package net.geforcemods.securitycraft.renderers;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypad;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityKeypadRenderer extends TileEntitySpecialRenderer {
	
	private IBakedModel blockModel;
	
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f, int number){
		if(((TileEntityKeypad) tileEntity).hasModule(EnumCustomModules.DISGUISE)) {
		    ItemModule module = (ItemModule) ((TileEntityKeypad) tileEntity).getModule(EnumCustomModules.DISGUISE).getItem();
	        blockModel = getBlockModel(module.getBlockAddons(((TileEntityKeypad) tileEntity).getModule(EnumCustomModules.DISGUISE).getTagCompound()).get(0));
		}
		else {
			blockModel = getBlockModel(mod_SecurityCraft.keypad);
		}
		
		GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();

        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableBlend();
        
        GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_DST_COLOR);
        
        GlStateManager.translate(x, y, z);
        WorldRenderer wr = Tessellator.getInstance().getWorldRenderer();

        wr.startDrawingQuads();
        Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModel(tileEntity.getWorld(), blockModel, tileEntity.getWorld().getBlockState(tileEntity.getPos()), tileEntity.getPos(), wr);
        Tessellator.getInstance().draw();
        
        GlStateManager.disableBlend();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }
	
	private IBakedModel getBlockModel(Block blockToRenderAs) {
        if(blockModel == null) {        	
        	return Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(new ItemStack(blockToRenderAs));
        }
        
		return blockModel;
	}
	
}
