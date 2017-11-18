package net.geforcemods.securitycraft.renderers;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.models.ModelTaser;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

public class ItemTaserRenderer implements IItemRenderer {

	private ResourceLocation texture = new ResourceLocation("securitycraft:textures/items/taser.png");

	public ModelTaser model;

	public ItemTaserRenderer() {
		model = new ModelTaser();
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		if(type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON || type == ItemRenderType.INVENTORY || type == ItemRenderType.ENTITY)
			return true;
		else
			return false;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		if(type == ItemRenderType.EQUIPPED_FIRST_PERSON){
			GL11.glPushMatrix();
			Minecraft.getMinecraft().renderEngine.bindTexture(texture);

			GL11.glRotatef(180F, -1.9F, 0F, -0.9F);
			//GL11.glRotatef(40F, 0F, 0F, 0F);

			GL11.glTranslatef(0.1F, -3.4F, -0.5F);
			GL11.glScalef(2.0F, 2.0F, 2.0F);

			model.render((Entity) data[1], 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
			GL11.glPopMatrix();
		}else if(type == ItemRenderType.EQUIPPED){
			GL11.glPushMatrix();
			Minecraft.getMinecraft().renderEngine.bindTexture(texture);

			GL11.glRotatef(240F, -2.8F, -0.6F, 1F);
			//GL11.glRotatef(40F, 0F, 0F, 0F);

			GL11.glTranslatef(0.1F, -1.9F, -0.9F);
			GL11.glScalef(2.0F, 2.0F, 2.0F);

			model.render((Entity) data[1], 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
			GL11.glPopMatrix();
		}else if(type == ItemRenderType.INVENTORY || type == ItemRenderType.ENTITY){
			GL11.glPushMatrix();
			Minecraft.getMinecraft().renderEngine.bindTexture(texture);

			GL11.glRotatef(180F, 5F, 0F, 0F);
			GL11.glScalef(2.0F, 2.0F, 2.0F);
			GL11.glTranslatef(0.0F, -1.18F, 0.0F);

			model.render((Entity) null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
			GL11.glPopMatrix();
		}
	}

}
