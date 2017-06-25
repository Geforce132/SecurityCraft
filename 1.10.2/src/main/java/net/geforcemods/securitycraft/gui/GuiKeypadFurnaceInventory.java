package net.geforcemods.securitycraft.gui;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.network.packets.PacketSetBlock;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadFurnace;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiKeypadFurnaceInventory extends GuiContainer{
	
    private static final ResourceLocation furnaceGuiTextures = new ResourceLocation("textures/gui/container/furnace.png");
    private TileEntityKeypadFurnace tileFurnace;

    public GuiKeypadFurnaceInventory(InventoryPlayer p_i1091_1_, TileEntityKeypadFurnace p_i1091_2_){
        super(new ContainerFurnace(p_i1091_1_, p_i1091_2_));
        this.tileFurnace = p_i1091_2_;
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
	protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_)
    {
        String s = this.tileFurnace.hasCustomName() ? this.tileFurnace.getName() : ClientUtils.localize(ClientUtils.localize("gui.protectedFurnace.name"), new Object[0]);
        this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
        this.fontRendererObj.drawString(ClientUtils.localize("gui.protectedFurnace.name"), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(furnaceGuiTextures);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

        if (this.tileFurnace.isBurning())
        {
            int i1 = this.tileFurnace.getBurnTimeRemainingScaled(13);
            this.drawTexturedModalRect(k + 56, l + 36 + 12 - i1, 176, 12 - i1, 14, i1 + 1);
            i1 = this.tileFurnace.getCookProgressScaled(24);
            this.drawTexturedModalRect(k + 79, l + 34, 176, 14, i1 + 1, 16);
        }
    }
    
    @Override
	public void onGuiClosed(){
    	super.onGuiClosed();
    	mod_SecurityCraft.network.sendToServer(new PacketSetBlock(this.tileFurnace.getPos().getX(), this.tileFurnace.getPos().getY(), this.tileFurnace.getPos().getZ(), "securitycraft:keypadFurnace", this.mc.theWorld.getBlockState(this.tileFurnace.getPos()).getBlock().getMetaFromState(this.mc.theWorld.getBlockState(this.tileFurnace.getPos())) - 6));
    }
    
}