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

	private static final ResourceLocation furnaceGuiTextures = new ResourceLocation("textures/gui/container/furnace.png");
	private TileEntityFurnace tileFurnace;
	private boolean gurnace = false;

	public GuiKeypadFurnaceInventory(InventoryPlayer p_i1091_1_, TileEntityFurnace p_i1091_2_){
		super(new ContainerFurnace(p_i1091_1_, p_i1091_2_));
		tileFurnace = p_i1091_2_;

		if(new Random().nextInt(100) < 5)
			gurnace = true;
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_)
	{
		String s = gurnace ? "Keypad Gurnace" : (tileFurnace.isCustomInventoryName() ? tileFurnace.getInventoryName() : I18n.format(StatCollector.translateToLocal("gui.protectedFurnace.name"), new Object[0]));
		fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(furnaceGuiTextures);
		int k = (width - xSize) / 2;
		int l = (height - ySize) / 2;
		drawTexturedModalRect(k, l, 0, 0, xSize, ySize);

		if (tileFurnace.isBurning())
		{
			int i1 = tileFurnace.getBurnTimeRemainingScaled(13);
			drawTexturedModalRect(k + 56, l + 36 + 12 - i1, 176, 12 - i1, 14, i1 + 1);
			i1 = tileFurnace.getCookProgressScaled(24);
			drawTexturedModalRect(k + 79, l + 34, 176, 14, i1 + 1, 16);
		}
	}

	@Override
	public void onGuiClosed(){
		super.onGuiClosed();
		SecurityCraft.network.sendToServer(new PacketSetBlockMetadata(tileFurnace.xCoord, tileFurnace.yCoord, tileFurnace.zCoord, mc.theWorld.getBlockMetadata(tileFurnace.xCoord, tileFurnace.yCoord, tileFurnace.zCoord) - 5, false, 1, "", ""));
	}

}