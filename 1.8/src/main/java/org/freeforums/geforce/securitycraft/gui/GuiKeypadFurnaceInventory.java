package org.freeforums.geforce.securitycraft.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.network.packets.PacketSetBlock;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadFurnace;

@SideOnly(Side.CLIENT)
public class GuiKeypadFurnaceInventory extends GuiContainer
{
    private static final ResourceLocation furnaceGuiTextures = new ResourceLocation("textures/gui/container/furnace.png");
    private final InventoryPlayer playerInventory;
    private TileEntityKeypadFurnace tileFurnace;

    public GuiKeypadFurnaceInventory(InventoryPlayer playerInv, TileEntityKeypadFurnace furnaceInv)
    {
        super(new ContainerFurnace(playerInv, furnaceInv));
        this.playerInventory = playerInv;
        this.tileFurnace = furnaceInv;
    }

    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.fontRendererObj.drawString("Protected Furnace", this.xSize / 2 - this.fontRendererObj.getStringWidth("Protected Furnace") / 2, 6, 4210752);
        this.fontRendererObj.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }

    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(furnaceGuiTextures);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
        int i1;

        if (TileEntityKeypadFurnace.isBurning(this.tileFurnace))
        {
            i1 = this.func_175382_i(13);
            this.drawTexturedModalRect(k + 56, l + 36 + 12 - i1, 176, 12 - i1, 14, i1 + 1);
        }

        i1 = this.func_175381_h(24);
        this.drawTexturedModalRect(k + 79, l + 34, 176, 14, i1 + 1, 16);
    }
    
    public void onGuiClosed(){
    	super.onGuiClosed();
		mod_SecurityCraft.network.sendToServer(new PacketSetBlock(this.tileFurnace.getPos().getX(), this.tileFurnace.getPos().getY(), this.tileFurnace.getPos().getZ(), "securitycraft:keypadFurnace", this.mc.theWorld.getBlockState(this.tileFurnace.getPos()).getBlock().getMetaFromState(this.mc.theWorld.getBlockState(this.tileFurnace.getPos())) - 6));
    }

    private int func_175381_h(int p_175381_1_)
    {
        int j = this.tileFurnace.getField(2);
        int k = this.tileFurnace.getField(3);
        return k != 0 && j != 0 ? j * p_175381_1_ / k : 0;
    }

    private int func_175382_i(int p_175382_1_)
    {
        int j = this.tileFurnace.getField(1);

        if (j == 0)
        {
            j = 200;
        }

        return this.tileFurnace.getField(0) * p_175382_1_ / j;
    }
}