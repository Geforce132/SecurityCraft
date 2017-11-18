package net.geforcemods.securitycraft.gui;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.network.packets.PacketSetExplosiveState;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class GuiMRATActivate extends GuiContainer{

	private static final ResourceLocation field_110410_t = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private ItemStack item;
	private GuiButton[] buttons = new GuiButton[6];

	public GuiMRATActivate(InventoryPlayer inventory, ItemStack item) {
		super(new ContainerGeneric(inventory, null));
		this.item = item;
	}

	@Override
	public void initGui(){
		super.initGui();
		for(int i = 1; i < 7; i++){
			buttons[i - 1] = new GuiButton(i - 1, width / 2 - 49 - 25, height / 2 - 7 - 60  + ((i - 1) * 25), 149, 20, ClientUtils.localize("gui.mrat.notBound"));
			buttons[i - 1].enabled = false;

			if(item.getItem() != null && item.getItem() == mod_SecurityCraft.remoteAccessMine && item.getTagCompound() != null &&  item.getTagCompound().getIntArray("mine" + i) != null && item.getTagCompound().getIntArray("mine" + i).length > 0){
				int[] coords = item.getTagCompound().getIntArray("mine" + i);

				if(coords[0] == 0 && coords[1] == 0 && coords[2] == 0){
					buttonList.add(buttons[i - 1]);
					continue;
				}

				buttons[i - 1].displayString = ClientUtils.localize("gui.mrat.mineLocations").replace("#location", Utils.getFormattedCoordinates(new BlockPos(coords[0], coords[1], coords[2])));
				buttons[i - 1].enabled = (BlockUtils.getBlock(mc.theWorld, coords[0], coords[1], coords[2]) instanceof IExplosive && ((IExplosive) BlockUtils.getBlock(mc.theWorld, coords[0], coords[1], coords[2])).isDefusable() && !((IExplosive) BlockUtils.getBlock(mc.theWorld, coords[0], coords[1], coords[2])).isActive(mc.theWorld, BlockUtils.toPos(coords[0], coords[1], coords[2]))) ? true : false;
				buttons[i - 1].id = i - 1;
			}

			buttonList.add(buttons[i - 1]);
		}
	}


	@Override
	public void onGuiClosed(){
		super.onGuiClosed();
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2){
		fontRendererObj.drawString(TextFormatting.UNDERLINE + ClientUtils.localize("gui.mrat.activate"), xSize / 2 - fontRendererObj.getStringWidth(ClientUtils.localize("gui.mrat.detonate")) / 2, 6, 4210752);
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the items)
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3){
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(field_110410_t);
		int k = (width - xSize) / 2;
		int l = (height - ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
	}

	@Override
	protected void actionPerformed(GuiButton guibutton){
		int[] coords = item.getTagCompound().getIntArray("mine" + (guibutton.id + 1));

		if(BlockUtils.getBlock(mc.theWorld, coords[0], coords[1], coords[2]) instanceof IExplosive)
			mod_SecurityCraft.network.sendToServer(new PacketSetExplosiveState(coords[0], coords[1], coords[2], "activate"));

		updateButton(guibutton);
	}

	private void updateButton(GuiButton guibutton) {
		guibutton.enabled = false;
		guibutton.displayString = guibutton.enabled ? "" : ClientUtils.localize("gui.mrat.activated");
	}

}
