package net.geforcemods.securitycraft.gui;

import java.util.HashMap;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class GuiWhiteBlacklistModule extends GuiContainer
{
	private EntityPlayer player;
	private String name;
	private GuiTextField input;
	public static HashMap<EntityPlayer,NBTTagCompound> copies = new HashMap<EntityPlayer,NBTTagCompound>();
	
	public GuiWhiteBlacklistModule(EntityPlayer p, String n)
	{
		super(new ContainerGeneric(null, null));
		player = p;
		name = n;
	}

	@Override
	public void initGui()
	{
		super.initGui();
		input = new GuiTextField(fontRendererObj, width / 2 - 68, height / 3, 137, 12);
		input.setMaxStringLength(16);
		input.setTextColor(-1);
		input.setDisabledTextColour(-1);
		input.setEnableBackgroundDrawing(true);
		input.setFocused(true);
		buttonList.add(new GuiButton(0, width / 4 - 21, guiTop, guiLeft - (width / 4 - 20), 20, "Add"));
		buttonList.add(new GuiButton(1, width / 4 - 21, guiTop + 21, guiLeft - (width / 4 - 20), 20, "Remove"));
		buttonList.add(new GuiButton(2, width / 4 - 21, guiTop + 42, guiLeft - (width / 4 - 20), 20, "Copy"));
		buttonList.add(new GuiButton(3, width / 4 - 21, guiTop + 63, guiLeft - (width / 4 - 20), 20, "Paste"));
		buttonList.add(new GuiButton(4, width / 4 - 21, guiTop + 84, guiLeft - (width / 4 - 20), 20, "Clear"));
	}
	
	@Override
	public void drawScreen(int i, int j, float f)
	{
		super.drawScreen(i, j, f);
		GL11.glDisable(GL11.GL_LIGHTING);
		input.drawTextBox();
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		ItemStack module = player.getCurrentEquippedItem();
		int k = 1;
		
		fontRendererObj.drawString(name, xSize / 2 - fontRendererObj.getStringWidth(name) / 2, 6, 4210752);
		
		if(module.getTagCompound() == null)
			module.setTagCompound(new NBTTagCompound());
		
		for(int i = 1; i <= 1000; i++)
		{
			if(!module.getTagCompound().hasKey("Player" + i))
				continue;
			
			fontRendererObj.drawString(module.getTagCompound().getString("Player" + i), 19, 30 + 10 * k, 4210752);
			k++;
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{
		int k = (width - xSize) / 2;
        int l = (height - ySize) / 2;
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(new ResourceLocation("securitycraft:textures/gui/container/blank.png"));
        drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
	}
	
	@Override
	protected void actionPerformed(GuiButton button)
	{
		ItemStack module = player.getCurrentEquippedItem();
		
		switch(button.id)
		{
			case 0: //add
				if(module.getTagCompound() == null)
					module.setTagCompound(new NBTTagCompound());
				
				for(int i = 1; i <= 1000; i++)
				{
					if(module.getTagCompound().hasKey("Player" + i) && module.getTagCompound().getString("Player" + i).equals(input.getText()))
						return;
				}
				
				module.getTagCompound().setString("Player" + getNextSlot(module.getTagCompound()), input.getText());
				break;
			case 1: //remove
				if(module.getTagCompound() == null)
					module.setTagCompound(new NBTTagCompound());				
				
				for(int i = 1; i <= 1000; i++)
				{
					if(module.getTagCompound().hasKey("Player" + i) && player.getCurrentEquippedItem().getTagCompound().getString("Player" + i).equals(input.getText()))
					{
						module.getTagCompound().removeTag("Player" + i);
						break;
					}
				}

				break;
			case 2: //copy
				copies.put(player, module.getTagCompound() == null ? new NBTTagCompound() : module.getTagCompound());
				break;
			case 3: //paste
				module.setTagCompound(copies.get(player));
				break;
			case 4: //clear
				module.setTagCompound(new NBTTagCompound());
		}
	}
	
	@Override
	protected void keyTyped(char c, int i)
	{
		input.textboxKeyTyped(c, i);

		if(c != mc.gameSettings.keyBindInventory.getKeyCode() && !input.isFocused())
			super.keyTyped(c, i);
	}
	
	@Override
	public void updateScreen()
	{
		super.updateScreen();
		input.updateCursorCounter();
	}
	
	@Override
	protected void mouseClicked(int x, int y, int button)
	{
		super.mouseClicked(x, y, button);
		input.mouseClicked(x, y, button);
	}
	
	private int getNextSlot(NBTTagCompound stackTagCompound)
	{
		for(int i = 1; i <= 1000; i++)
		{
			if(stackTagCompound.getString("Player" + i) != null && !stackTagCompound.getString("Player" + i).isEmpty())
				continue;
			else
				return i;
		}
		
		return 0;
	}
}
