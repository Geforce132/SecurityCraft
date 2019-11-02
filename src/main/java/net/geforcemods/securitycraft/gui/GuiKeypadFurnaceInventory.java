package net.geforcemods.securitycraft.gui;

import java.util.Random;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.network.packets.PacketSetBlock;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadFurnace;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiKeypadFurnaceInventory extends GuiContainer{

	private static final ResourceLocation furnaceGuiTextures = new ResourceLocation("textures/gui/container/furnace.png");
	private TileEntityKeypadFurnace tileFurnace;
	private boolean gurnace = false;

	public GuiKeypadFurnaceInventory(InventoryPlayer inventory, TileEntityKeypadFurnace te){
		super(new ContainerFurnace(inventory, te));
		tileFurnace = te;

		if(new Random().nextInt(100) < 5)
			gurnace = true;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		super.drawScreen(mouseX, mouseY, partialTicks);

		if(getSlotUnderMouse() != null && !getSlotUnderMouse().getStack().isEmpty())
			renderToolTip(getSlotUnderMouse().getStack(), mouseX, mouseY);
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		String s = gurnace ? "Keypad Gurnace" : (tileFurnace.hasCustomName() ? tileFurnace.getName() : ClientUtils.localize(ClientUtils.localize("gui.securitycraft:protectedFurnace.name"), new Object[0]));
		fontRenderer.drawString(s, xSize / 2 - fontRenderer.getStringWidth(s) / 2, 6, 4210752);
		fontRenderer.drawString(ClientUtils.localize("container.inventory"), 8, ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(furnaceGuiTextures);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		this.drawTexturedModalRect(startX, startY, 0, 0, xSize, ySize);

		if (tileFurnace.isBurning())
		{
			int burnTime = tileFurnace.getBurnTimeRemainingScaled(13);
			this.drawTexturedModalRect(startX + 56, startY + 36 + 12 - burnTime, 176, 12 - burnTime, 14, burnTime + 1);
			burnTime = tileFurnace.getCookProgressScaled(24);
			this.drawTexturedModalRect(startX + 79, startY + 34, 176, 14, burnTime + 1, 16);
		}
	}

	@Override
	public void onGuiClosed(){
		super.onGuiClosed();
		SecurityCraft.network.sendToServer(new PacketSetBlock(tileFurnace.getPos().getX(), tileFurnace.getPos().getY(), tileFurnace.getPos().getZ(), "securitycraft:keypad_furnace", mc.world.getBlockState(tileFurnace.getPos()).getBlock().getMetaFromState(mc.world.getBlockState(tileFurnace.getPos())) - 6));
	}

}