package net.geforcemods.securitycraft.gui;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.network.server.UpdateNBTTagOnServer;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiPageButtonList.GuiResponder;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.GuiScrollingList;

public class GuiEditModule extends GuiContainer implements GuiResponder
{
	private static NBTTagCompound savedModule;
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/edit_module.png");
	private final String editModule = Utils.localize("gui.securitycraft:editModule").getFormattedText();
	private ItemStack module;
	private GuiTextField inputField;
	private GuiButton addButton, removeButton, copyButton, pasteButton, clearButton;
	private PlayerList playerList;

	public GuiEditModule(InventoryPlayer inventory, ItemStack item, TileEntity te)
	{
		super(new ContainerGeneric(inventory, te));

		module = item;
		xSize = 247;
	}

	@Override
	public void initGui()
	{
		super.initGui();

		int controlsStartX = (int)(guiLeft + xSize * (3.0F / 4.0F)) - 43;

		Keyboard.enableRepeatEvents(true);
		inputField = new GuiTextField(5, fontRenderer, controlsStartX - 17, height / 2 - 65, 110, 15);
		buttonList.add(addButton = new GuiButton(0, controlsStartX, height / 2 - 45, 76, 20, Utils.localize("gui.securitycraft:editModule.add").getFormattedText()));
		buttonList.add(removeButton = new GuiButton(1, controlsStartX, height / 2 - 20, 76, 20, Utils.localize("gui.securitycraft:editModule.remove").getFormattedText()));
		buttonList.add(copyButton = new GuiButton(2, controlsStartX, height / 2 + 5, 76, 20, Utils.localize("gui.securitycraft:editModule.copy").getFormattedText()));
		buttonList.add(pasteButton = new GuiButton(3, controlsStartX, height / 2 + 30, 76, 20, Utils.localize("gui.securitycraft:editModule.paste").getFormattedText()));
		buttonList.add(clearButton = new GuiButton(4, controlsStartX, height / 2 + 55, 76, 20, Utils.localize("gui.securitycraft:editModule.clear").getFormattedText()));
		playerList = new PlayerList(mc, 110, 141, height / 2 - 66, guiLeft + 10, width, height);

		addButton.enabled = false;
		removeButton.enabled = false;

		if (module.getTagCompound() == null || module.getTagCompound().isEmpty() || (module.getTagCompound() != null && module.getTagCompound().equals(savedModule)))
			copyButton.enabled = false;

		if (savedModule == null || savedModule.isEmpty() || (module.getTagCompound() != null && module.getTagCompound().equals(savedModule)))
			pasteButton.enabled = false;

		if (module.getTagCompound() == null || module.getTagCompound().isEmpty())
			clearButton.enabled = false;

		inputField.setGuiResponder(this);
		inputField.setMaxStringLength(16);
		inputField.setFocused(true);
	}

	@Override
	public void onGuiClosed(){
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		super.drawScreen(mouseX, mouseY, partialTicks);
		GlStateManager.disableLighting();
		inputField.drawTextBox();

		if(playerList != null)
			playerList.drawScreen(mouseX, mouseY, partialTicks);
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		fontRenderer.drawSplitString(editModule, xSize / 2 - fontRenderer.getStringWidth(editModule) / 2, 6, xSize, 4210752);
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the items)
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		drawTexturedModalRect(startX, startY, 0, 0, xSize, ySize);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException{
		if(keyCode != Keyboard.KEY_ESCAPE && inputField.isFocused())
		{
			if(keyCode == Keyboard.KEY_SPACE)
				return;

			inputField.textboxKeyTyped(typedChar, keyCode);
		}
		else
			super.keyTyped(typedChar, keyCode);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException{
		super.mouseClicked(mouseX, mouseY, mouseButton);
		inputField.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void actionPerformed(GuiButton button){
		switch(button.id){
			case 0: //add
				if(inputField.getText().isEmpty())
					return;

				if(module.getTagCompound() == null)
					module.setTagCompound(new NBTTagCompound());

				for(int i = 1; i <= ItemModule.MAX_PLAYERS; i++)
				{
					if(module.getTagCompound().hasKey("Player" + i) && module.getTagCompound().getString("Player" + i).equals(inputField.getText()))
					{
						if (i == 9)
							addButton.enabled = false;
						return;
					}
				}

				module.getTagCompound().setString("Player" + getNextSlot(module.getTagCompound()), inputField.getText());

				if(module.getTagCompound() != null && module.getTagCompound().hasKey("Player" + ItemModule.MAX_PLAYERS))
					addButton.enabled = false;

				inputField.setText("");
				break;
			case 1: //remove
				if(inputField.getText().isEmpty())
					return;

				if(module.getTagCompound() == null)
					module.setTagCompound(new NBTTagCompound());

				for(int i = 1; i <= ItemModule.MAX_PLAYERS; i++)
				{
					if(module.getTagCompound().hasKey("Player" + i) && module.getTagCompound().getString("Player" + i).equals(inputField.getText()))
						module.getTagCompound().removeTag("Player" + i);
				}

				inputField.setText("");
				defragmentTag(module.getTagCompound());
				break;
			case 2: //copy
				savedModule = module.getTagCompound().copy();
				copyButton.enabled = false;
				return;
			case 3: //paste
				module.setTagCompound(savedModule.copy());
				break;
			case 4: //clear
				module.setTagCompound(new NBTTagCompound());
				inputField.setText("");
				break;
			default: return;
		}

		if(module.getTagCompound() != null)
			SecurityCraft.network.sendToServer(new UpdateNBTTagOnServer(module));

		addButton.enabled = module.getTagCompound() != null && !module.getTagCompound().hasKey("Player" + ItemModule.MAX_PLAYERS) && !inputField.getText().isEmpty();
		removeButton.enabled = !(module.getTagCompound() == null || module.getTagCompound().isEmpty() || inputField.getText().isEmpty());
		copyButton.enabled = !(module.getTagCompound() == null || module.getTagCompound().isEmpty() || (module.getTagCompound() != null && module.getTagCompound().equals(savedModule)));
		pasteButton.enabled = !(savedModule == null || savedModule.isEmpty() || (module.getTagCompound() != null && module.getTagCompound().equals(savedModule)));
		clearButton.enabled = !(module.getTagCompound() == null || module.getTagCompound().isEmpty());
	}

	private int getNextSlot(NBTTagCompound tag) {
		for(int i = 1; i <= ItemModule.MAX_PLAYERS; i++)
		{
			if(!tag.hasKey("Player" + i) || tag.getString("Player" + i).isEmpty())
				return i;
		}

		return 0;
	}

	private void defragmentTag(NBTTagCompound tag)
	{
		Deque<Integer> freeIndices = new ArrayDeque<>();

		for(int i = 1; i <= ItemModule.MAX_PLAYERS; i++)
		{
			if(!tag.hasKey("Player" + i) || tag.getString("Player" + i).isEmpty())
				freeIndices.add(i);
			else if(!freeIndices.isEmpty())
			{
				String player = tag.getString("Player" + i);
				int nextFreeIndex = freeIndices.poll();

				tag.setString("Player" + nextFreeIndex, player);
				tag.removeTag("Player" + i);
				freeIndices.add(i);
			}
		}
	}

	@Override
	public void handleMouseInput() throws IOException
	{
		super.handleMouseInput();

		int mouseX = Mouse.getEventX() * width / mc.displayWidth;
		int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;

		playerList.handleMouseInput(mouseX, mouseY);
		setEntryValue(inputField.getId(), inputField.getText());
	}

	@Override
	public void setEntryValue(int id, String text)
	{
		if(text.isEmpty())
			addButton.enabled = false;
		else
		{
			if(module.hasTagCompound())
			{
				for(int i = 1; i <= ItemModule.MAX_PLAYERS; i++)
				{
					if(text.equals(module.getTagCompound().getString("Player" + i)))
					{
						addButton.enabled = false;
						removeButton.enabled = true;
						playerList.setSelectedIndex(i - 1);
						return;
					}
				}
			}

			addButton.enabled = true;
		}

		removeButton.enabled = false;
		playerList.setSelectedIndex(-1);
	}

	@Override
	public void setEntryValue(int id, boolean value) {}

	@Override
	public void setEntryValue(int id, float value) {}

	class PlayerList extends GuiScrollingList
	{
		public PlayerList(Minecraft client, int width, int height, int top, int left, int screenWidth, int screenHeight)
		{
			super(client, width, height, top, top + height, left, 12, screenWidth, screenHeight);
		}

		@Override
		protected int getSize()
		{
			return ItemModule.MAX_PLAYERS;
		}

		@Override
		protected void elementClicked(int index, boolean doubleClick)
		{
			if(module.hasTagCompound() && module.getTagCompound().hasKey("Player" + (index + 1)))
				inputField.setText(module.getTagCompound().getString("Player" + (index + 1)));
		}

		@Override
		protected boolean isSelected(int index)
		{
			return index == selectedIndex && module.hasTagCompound() && module.getTagCompound().hasKey("Player" + (index + 1)) && !module.getTagCompound().getString("Player" + (index + 1)).isEmpty();
		}

		@Override
		protected void drawBackground() {}

		@Override
		protected void drawSlot(int slotIndex, int entryRight, int slotTop, int slotBuffer, Tessellator tessellator)
		{
			if(module.hasTagCompound())
			{
				NBTTagCompound tag = module.getTagCompound();

				slotBuffer--;

				//highlighted selected slot
				if(isSelected(slotIndex))
					renderBox(tessellator, left, entryRight + 1, slotTop, slotBuffer, 0xFF);
				//highlight hovered slot
				else if(mouseX >= left && mouseX <= entryRight && slotIndex >= 0 && slotIndex < getSize() && mouseY >= slotTop - 1 && mouseY <= slotTop + slotBuffer + 2)
				{
					if(tag.hasKey("Player" + (slotIndex + 1)) && !tag.getString("Player" + (slotIndex + 1)).isEmpty())
						renderBox(tessellator, left, entryRight + 1, slotTop, slotBuffer, 0x80);
				}

				//draw name
				if(tag.hasKey("Player" + (slotIndex + 1)))
				{
					String name = tag.getString("Player" + (slotIndex + 1));

					if(!name.isEmpty())
						fontRenderer.drawString(name, width / 2 - 110 + listWidth / 2 - fontRenderer.getStringWidth(name) / 2 - 4, slotTop, 0xC6C6C6);
				}
			}
		}

		private void renderBox(Tessellator tessellator, int min, int max, int slotTop, int slotBuffer, int borderColor)
		{
			BufferBuilder bufferBuilder = tessellator.getBuffer();

			GlStateManager.disableTexture2D();
			bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
			bufferBuilder.pos(min, slotTop + slotBuffer + 3, 0).tex(0, 1).color(borderColor, borderColor, borderColor, 0xFF).endVertex();
			bufferBuilder.pos(max, slotTop + slotBuffer + 3, 0).tex(1, 1).color(borderColor, borderColor, borderColor, 0xFF).endVertex();
			bufferBuilder.pos(max, slotTop - 2, 0).tex(1, 0).color(borderColor, borderColor, borderColor, 0xFF).endVertex();
			bufferBuilder.pos(min, slotTop - 2, 0).tex(0, 0).color(borderColor, borderColor, borderColor, 0xFF).endVertex();
			bufferBuilder.pos(min + 1, slotTop + slotBuffer + 2, 0).tex(0, 1).color(0x00, 0x00, 0x00, 0xFF).endVertex();
			bufferBuilder.pos(max - 1, slotTop + slotBuffer + 2, 0).tex(1, 1).color(0x00, 0x00, 0x00, 0xFF).endVertex();
			bufferBuilder.pos(max - 1, slotTop - 1, 0).tex(1, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
			bufferBuilder.pos(min + 1, slotTop - 1, 0).tex(0, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
			tessellator.draw();
			GlStateManager.enableTexture2D();
		}

		public void setSelectedIndex(int selectedIndex)
		{
			this.selectedIndex = selectedIndex;
		}
	}
}
