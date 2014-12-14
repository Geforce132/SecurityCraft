package org.freeforums.geforce.securitycraft.gui;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import org.freeforums.geforce.securitycraft.containers.ContainerSecurityCamera;
import org.freeforums.geforce.securitycraft.tileentity.TileEntitySecurityCamera;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

//TODO
public class GuiSecurityCamera extends GuiContainer{

	private static final ResourceLocation field_110410_t = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private TileEntitySecurityCamera TESC;
    private GuiTextField[] textFields = new GuiTextField[10];

	public GuiSecurityCamera(Container par1Container) {
		super(par1Container);
	}

	public GuiSecurityCamera(InventoryPlayer par1InventoryPlayer, TileEntitySecurityCamera par2TileEntityFurnace) {
		super(new ContainerSecurityCamera(par1InventoryPlayer, par2TileEntityFurnace));
		this.TESC = par2TileEntityFurnace;
		
		
	}
	
	public void initGui(){
    	super.initGui();	
    	Keyboard.enableRepeatEvents(true);
    	
    	this.buttonList.add(new GuiButton(0, 100, 100, 100, 20, "Save."));
		
    	for(int x = 1; x <= 5; x++){
    		this.textFields[x - 1] = new GuiTextField(x, this.fontRendererObj, this.width / 2 - 52, this.height / 2 - 67 + (x * 20), 40, 12);    		
    		
    		this.textFields[x - 1].setTextColor(-1);
    		this.textFields[x - 1].setDisabledTextColour(-1);
    		this.textFields[x - 1].setEnableBackgroundDrawing(true);
    		this.textFields[x - 1].setMaxStringLength(25);
    		
    		if(this.TESC.getId(x) != ""){
    			this.textFields[x - 1].setText(this.TESC.getId(x));
    			//System.out.println(x - 1);
    		}
    		
    	}   		
    	
    	for(int x = 1; x <= 5; x++){
    		this.textFields[(x + 5) - 1] = new GuiTextField((x + 5), this.fontRendererObj, this.width / 2 + 20, this.height / 2 - 67 + (x * 20), 40, 12);    		
    		
    		this.textFields[(x + 5) - 1].setTextColor(-1);
    		this.textFields[(x + 5) - 1].setDisabledTextColour(-1);
    		this.textFields[(x + 5) - 1].setEnableBackgroundDrawing(true);
    		this.textFields[(x + 5) - 1].setMaxStringLength(5);
    		
    		if(this.TESC.getId((x + 5)) != ""){
    			this.textFields[(x + 5) - 1].setText(this.TESC.getId((x + 5)));
    		}
    	}
	}
	
	public void onGuiClosed(){
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}
	
	public void drawScreen(int par1, int par2, float par3){
		super.drawScreen(par1, par2, par3);
		GL11.glDisable(GL11.GL_LIGHTING);
		
		for(int x = 0; x <= 9; x++){
			this.textFields[x].drawTextBox();
		}
    }
	
	protected void keyTyped(char par1, int par2) throws IOException{
		if(this.textFields[0].textboxKeyTyped(par1, par2)){
			//this.mc.thePlayer.sendQueue.addToSendQueue(new Packet250CustomPayload("MC|ItemName", this.textFields[0].getText().getBytes()));
		}
		
		else if(this.textFields[1].textboxKeyTyped(par1, par2)){
			//this.mc.thePlayer.sendQueue.addToSendQueue(new Packet250CustomPayload("MC|ItemName", this.textFields[1].getText().getBytes()));
		}
		
		else if(this.textFields[2].textboxKeyTyped(par1, par2)){
			//this.mc.thePlayer.sendQueue.addToSendQueue(new Packet250CustomPayload("MC|ItemName", this.textFields[2].getText().getBytes()));
		}
		
		else if(this.textFields[3].textboxKeyTyped(par1, par2)){
			//this.mc.thePlayer.sendQueue.addToSendQueue(new Packet250CustomPayload("MC|ItemName", this.textFields[3].getText().getBytes()));
		}
		
		else if(this.textFields[4].textboxKeyTyped(par1, par2)){
			//this.mc.thePlayer.sendQueue.addToSendQueue(new Packet250CustomPayload("MC|ItemName", this.textFields[4].getText().getBytes()));
		}
		
		else if(this.textFields[5].textboxKeyTyped(par1, par2)){
			//this.mc.thePlayer.sendQueue.addToSendQueue(new Packet250CustomPayload("MC|ItemName", this.textFields[5].getText().getBytes()));
		}
		
		else if(this.textFields[6].textboxKeyTyped(par1, par2)){
			//this.mc.thePlayer.sendQueue.addToSendQueue(new Packet250CustomPayload("MC|ItemName", this.textFields[6].getText().getBytes()));
		}
		
		else if(this.textFields[7].textboxKeyTyped(par1, par2)){
			//this.mc.thePlayer.sendQueue.addToSendQueue(new Packet250CustomPayload("MC|ItemName", this.textFields[7].getText().getBytes()));
		}
		
		else if(this.textFields[8].textboxKeyTyped(par1, par2)){
			//this.mc.thePlayer.sendQueue.addToSendQueue(new Packet250CustomPayload("MC|ItemName", this.textFields[8].getText().getBytes()));
		}
		
		else if(this.textFields[9].textboxKeyTyped(par1, par2)){
			//this.mc.thePlayer.sendQueue.addToSendQueue(new Packet250CustomPayload("MC|ItemName", this.textFields[9].getText().getBytes()));
		}
		
		else{
			super.keyTyped(par1, par2);
		}
	
	}
    
    protected void mouseClicked(int par1, int par2, int par3) throws IOException{
		super.mouseClicked(par1, par2, par3);

		for(int x = 1; x <= 10; x++){
			this.textFields[x - 1].mouseClicked(par1, par2, par3);
		}

	}
	    

	/**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        this.fontRendererObj.drawString("Security Camera", this.xSize / 2 - this.fontRendererObj.getStringWidth("Security Camera") / 2, 6, 4210752);
    }
	
	/**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(field_110410_t);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
      
    }
    
	protected void actionPerformed(GuiButton guibutton){
		switch(guibutton.id){
			case 0:								
				ByteArrayOutputStream BOS = new ByteArrayOutputStream(400);
				DataOutputStream outputStream = new DataOutputStream(BOS);

				for(int i = 1; i <= 10; i++){
					this.TESC.setId(this.textFields[i - 1].getText(), i);
			}			
		}	
	}

}
