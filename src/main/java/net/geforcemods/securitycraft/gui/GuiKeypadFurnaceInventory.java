package net.geforcemods.securitycraft.gui;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.network.packets.PacketSetBlockMetadata;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

@SideOnly(Side.CLIENT)
public class GuiKeypadFurnaceInventory extends GuiContainer{

	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/furnace.png");
	private TileEntityFurnace tileFurnace;
	private boolean gurnace = false;

	public GuiKeypadFurnaceInventory(InventoryPlayer playerInv, TileEntityFurnace te){
		super(new ContainerFurnace(playerInv, te));
		tileFurnace = te;

		if(new Random().nextInt(100) < 5)
			gurnace = true;
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		String s = gurnace ? "Keypad Gurnace" : (tileFurnace.isCustomInventoryName() ? tileFurnace.getInventoryName() : I18n.format(StatCollector.translateToLocal("gui.securitycraft:protectedFurnace.name"), new Object[0]));
		fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		drawTexturedModalRect(startX, startY, 0, 0, xSize, ySize);

		if (tileFurnace.isBurning())
		{
			int burnTime = tileFurnace.getBurnTimeRemainingScaled(13);
			drawTexturedModalRect(startX + 56, startY + 36 + 12 - burnTime, 176, 12 - burnTime, 14, burnTime + 1);
			burnTime = tileFurnace.getCookProgressScaled(24);
			drawTexturedModalRect(startX + 79, startY + 34, 176, 14, burnTime + 1, 16);
		}
	}

	@Override
	public void onGuiClosed(){
		super.onGuiClosed();
		SecurityCraft.network.sendToServer(new PacketSetBlockMetadata(tileFurnace.xCoord, tileFurnace.yCoord, tileFurnace.zCoord, mc.theWorld.getBlockMetadata(tileFurnace.xCoord, tileFurnace.yCoord, tileFurnace.zCoord) - 5, false, 1, "", ""));
	}

}