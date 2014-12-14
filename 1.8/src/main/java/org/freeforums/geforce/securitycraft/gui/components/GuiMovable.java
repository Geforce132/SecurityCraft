package org.freeforums.geforce.securitycraft.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.input.Mouse;

@SideOnly(Side.CLIENT)
public class GuiMovable extends Gui{
	
	private final int x, y, width, height;
	private final ResourceLocation texture;
	
	public GuiMovable(int x, int y, int width, int height, ResourceLocation texture){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.texture = texture;
		
		//ImageIcon image = new ImageIcon(texture.getResourcePath());
		
		//this.width = image.getIconWidth();
		//this.height = image.getIconHeight();
	}
	
	
	public void drawComponent(Minecraft par1){
		par1.getTextureManager().bindTexture(texture);
		this.drawTexturedModalRect(x, y, 0, 0, width, height);
		
		System.out.println(isMouseOn(par1) + " | X: " + this.x + " Y: " + this.y + "  Mouse X: " + Mouse.getX() + " Mouse Y: " + Math.abs(Mouse.getY() - par1.displayHeight) + ("  Found image: " + (this.texture == null ? "false" : "true")));
	}
	
	public boolean isMouseOn(Minecraft par1){
		int mouseX = Mouse.getX();
		int mouseY = Mouse.getY();
		
		//if(mouseX >= this.x && mouseY >= this.y && mouseX <= (this.x + this.width) && mouseY <= (this.y + this.height)){
		if(mouseX >= this.x && Math.abs(Mouse.getY() - par1.displayHeight) >= this.y){
			return true;
		}else{
			return false;
		}
	}

}
