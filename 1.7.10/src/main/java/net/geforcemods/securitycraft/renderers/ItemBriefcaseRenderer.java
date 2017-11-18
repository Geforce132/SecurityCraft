package net.geforcemods.securitycraft.renderers;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.models.ModelBriefcase;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

public class ItemBriefcaseRenderer implements IItemRenderer {

	private ResourceLocation briefcaseTexture = new ResourceLocation("securitycraft:textures/items/briefcase.png");

	public ModelBriefcase modelClosed;

	public ItemBriefcaseRenderer() {
		modelClosed = new ModelBriefcase();
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return type != ItemRenderType.FIRST_PERSON_MAP;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		if(type == ItemRenderType.EQUIPPED_FIRST_PERSON){
			GL11.glPushMatrix();
			Minecraft.getMinecraft().renderEngine.bindTexture(briefcaseTexture);

			GL11.glRotatef(180F, -1.9F, 0F, 0.6F);

			GL11.glTranslatef(0.1F, -2.3F, -0.6F);
			GL11.glScalef(1.5F, 1.5F, 1.5F);

			modelClosed.render((Entity) data[1], 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
			GL11.glPopMatrix();
		}
		else if(type == ItemRenderType.EQUIPPED) {
			GL11.glPushMatrix();
			Minecraft.getMinecraft().renderEngine.bindTexture(briefcaseTexture);

			GL11.glRotatef(180F, -3F, 0.1F, -1F);

			GL11.glTranslatef(1.3F, -1.95F, -0.2F);
			GL11.glScalef(2.0F, 2.0F, 2.0F);

			modelClosed.render((Entity) data[1], 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
			GL11.glPopMatrix();
		}
		else if(type == ItemRenderType.INVENTORY || type == ItemRenderType.ENTITY){
			GL11.glPushMatrix();
			Minecraft.getMinecraft().renderEngine.bindTexture(briefcaseTexture);

			GL11.glRotatef(180F, 5F, 0F, 0F);
			GL11.glScalef(1.45F, 1.45F, 1.45F);
			GL11.glTranslatef(0.0F, -1.1F, 0.0F);

			modelClosed.render((Entity) null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
			GL11.glPopMatrix();
		}
	}

}
