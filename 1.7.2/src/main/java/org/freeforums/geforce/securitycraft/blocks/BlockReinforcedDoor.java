package org.freeforums.geforce.securitycraft.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.IconFlipped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.main.HelpfulMethods;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityInventoryScanner;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeycardReader;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityReinforcedDoor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockReinforcedDoor extends BlockContainer
{
    @SideOnly(Side.CLIENT)
    private IIcon[] field_150017_a;
    @SideOnly(Side.CLIENT)
    private IIcon[] field_150016_b;

    public BlockReinforcedDoor(Material p_i45402_1_)
    {
        super(p_i45402_1_);
        float f = 0.5F;
        float f1 = 1.0F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f1, 0.5F + f);
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int p_149691_1_, int p_149691_2_)
    {
        return this.field_150016_b[0];
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess p_149673_1_, int p_149673_2_, int p_149673_3_, int p_149673_4_, int p_149673_5_)
    {
        if (p_149673_5_ != 1 && p_149673_5_ != 0)
        {
            int i1 = this.func_150012_g(p_149673_1_, p_149673_2_, p_149673_3_, p_149673_4_);
            int j1 = i1 & 3;
            boolean flag = (i1 & 4) != 0;
            boolean flag1 = false;
            boolean flag2 = (i1 & 8) != 0;

            if (flag)
            {
                if (j1 == 0 && p_149673_5_ == 2)
                {
                    flag1 = !flag1;
                }
                else if (j1 == 1 && p_149673_5_ == 5)
                {
                    flag1 = !flag1;
                }
                else if (j1 == 2 && p_149673_5_ == 3)
                {
                    flag1 = !flag1;
                }
                else if (j1 == 3 && p_149673_5_ == 4)
                {
                    flag1 = !flag1;
                }
            }
            else
            {
                if (j1 == 0 && p_149673_5_ == 5)
                {
                    flag1 = !flag1;
                }
                else if (j1 == 1 && p_149673_5_ == 3)
                {
                    flag1 = !flag1;
                }
                else if (j1 == 2 && p_149673_5_ == 4)
                {
                    flag1 = !flag1;
                }
                else if (j1 == 3 && p_149673_5_ == 2)
                {
                    flag1 = !flag1;
                }

                if ((i1 & 16) != 0)
                {
                    flag1 = !flag1;
                }
            }

            return flag2 ? this.field_150017_a[flag1?1:0] : this.field_150016_b[flag1?1:0];
        }
        else
        {
            return this.field_150016_b[0];
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister p_149651_1_)
    {
        this.field_150017_a = new IIcon[2];
        this.field_150016_b = new IIcon[2];
        this.field_150017_a[0] = p_149651_1_.registerIcon("securitycraft:reinforcedDoorUpper");
        this.field_150016_b[0] = p_149651_1_.registerIcon("securitycraft:reinforcedDoorLower");
        this.field_150017_a[1] = new IconFlipped(this.field_150017_a[0], true, false);
        this.field_150016_b[1] = new IconFlipped(this.field_150016_b[0], true, false);
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean getBlocksMovement(IBlockAccess p_149655_1_, int p_149655_2_, int p_149655_3_, int p_149655_4_)
    {
        int l = this.func_150012_g(p_149655_1_, p_149655_2_, p_149655_3_, p_149655_4_);
        return (l & 4) != 0;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 7;
    }

    /**
     * Returns the bounding box of the wired rectangular prism to render.
     */
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World p_149633_1_, int p_149633_2_, int p_149633_3_, int p_149633_4_)
    {
        this.setBlockBoundsBasedOnState(p_149633_1_, p_149633_2_, p_149633_3_, p_149633_4_);
        return super.getSelectedBoundingBoxFromPool(p_149633_1_, p_149633_2_, p_149633_3_, p_149633_4_);
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_)
    {
        this.setBlockBoundsBasedOnState(p_149668_1_, p_149668_2_, p_149668_3_, p_149668_4_);
        return super.getCollisionBoundingBoxFromPool(p_149668_1_, p_149668_2_, p_149668_3_, p_149668_4_);
    }

    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void setBlockBoundsBasedOnState(IBlockAccess p_149719_1_, int p_149719_2_, int p_149719_3_, int p_149719_4_)
    {
        this.func_150011_b(this.func_150012_g(p_149719_1_, p_149719_2_, p_149719_3_, p_149719_4_));
    }

    public int func_150013_e(IBlockAccess p_150013_1_, int p_150013_2_, int p_150013_3_, int p_150013_4_)
    {
        return this.func_150012_g(p_150013_1_, p_150013_2_, p_150013_3_, p_150013_4_) & 3;
    }

    public boolean func_150015_f(IBlockAccess p_150015_1_, int p_150015_2_, int p_150015_3_, int p_150015_4_)
    {
        return (this.func_150012_g(p_150015_1_, p_150015_2_, p_150015_3_, p_150015_4_) & 4) != 0;
    }

    private void func_150011_b(int p_150011_1_)
    {
        float f = 0.1875F;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F);
        int j = p_150011_1_ & 3;
        boolean flag = (p_150011_1_ & 4) != 0;
        boolean flag1 = (p_150011_1_ & 16) != 0;

        if (j == 0)
        {
            if (flag)
            {
                if (!flag1)
                {
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
                }
                else
                {
                    this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
                }
            }
            else
            {
                this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
            }
        }
        else if (j == 1)
        {
            if (flag)
            {
                if (!flag1)
                {
                    this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                }
                else
                {
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
                }
            }
            else
            {
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
            }
        }
        else if (j == 2)
        {
            if (flag)
            {
                if (!flag1)
                {
                    this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
                }
                else
                {
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
                }
            }
            else
            {
                this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            }
        }
        else if (j == 3)
        {
            if (flag)
            {
                if (!flag1)
                {
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
                }
                else
                {
                    this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                }
            }
            else
            {
                this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
            }
        }
    }

//    /**
//     * Called when a player hits the block. Args: world, x, y, z, player
//     */
//    public void onBlockClicked(World par2World, int p_149699_2_, int p_149699_3_, int p_149699_4_, EntityPlayer par5) {
//    	if(par2World.isRemote){
//    		return;
//    	}else{
//    		if(par5.getCurrentEquippedItem() != null && par5.getCurrentEquippedItem().getItem() == mod_SecurityCraft.universalBlockRemover){
//    			HelpfulMethods.sendMessageToPlayer(par5, "Right-click the top of the door to remove it.", null);
//    		}
//    	}
//    
//    }
//    
//    public void onBlockClicked(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer) {
//    	if(par1World.isRemote){
//    		return;
//    	}
//    	
//    	
//    	if(par5EntityPlayer.getCurrentEquippedItem() != null && par5EntityPlayer.getCurrentEquippedItem().getItem() == mod_SecurityCraft.universalBlockRemover){
//    		if(par5EntityPlayer.getCurrentEquippedItem() != null && par5EntityPlayer.getCurrentEquippedItem().getItem() == mod_SecurityCraft.universalBlockRemover && ((TileEntityOwnable)par1World.getTileEntity(par2, par3, par4)).getOwner() != null && ((TileEntityOwnable)par1World.getTileEntity(par2, par3, par4)).getOwner().matches(par5EntityPlayer.getCommandSenderName())){
//				HelpfulMethods.destroyBlock(par1World, par2, par3, par4, true);
//    			return;
//			}else if(par5EntityPlayer.getCurrentEquippedItem() != null && par5EntityPlayer.getCurrentEquippedItem().getItem() == mod_SecurityCraft.universalBlockRemover && ((TileEntityOwnable)par1World.getTileEntity(par2, par3, par4)).getOwner() != null && !((TileEntityOwnable)par1World.getTileEntity(par2, par3, par4)).getOwner().matches(par5EntityPlayer.getCommandSenderName())){
//	    		HelpfulMethods.sendMessageToPlayer(par5EntityPlayer, "I'm sorry, you can not remove this block. This block is owned by " + ((TileEntityOwnable) par1World.getTileEntity(par2, par3, par4)).getOwner() + ".", EnumChatFormatting.RED);
//	    		return;
//	    	}
//    	}
//    }

    public void func_150014_a(World p_150014_1_, int p_150014_2_, int p_150014_3_, int p_150014_4_, boolean p_150014_5_)
    {
        int l = this.func_150012_g(p_150014_1_, p_150014_2_, p_150014_3_, p_150014_4_);
        boolean flag1 = (l & 4) != 0;

        if (flag1 != p_150014_5_)
        {
            int i1 = l & 7;
            i1 ^= 4;

            if ((l & 8) == 0)
            {
                p_150014_1_.setBlockMetadataWithNotify(p_150014_2_, p_150014_3_, p_150014_4_, i1, 2);
                p_150014_1_.markBlockRangeForRenderUpdate(p_150014_2_, p_150014_3_, p_150014_4_, p_150014_2_, p_150014_3_, p_150014_4_);
            }
            else
            {
                p_150014_1_.setBlockMetadataWithNotify(p_150014_2_, p_150014_3_ - 1, p_150014_4_, i1, 2);
                p_150014_1_.markBlockRangeForRenderUpdate(p_150014_2_, p_150014_3_ - 1, p_150014_4_, p_150014_2_, p_150014_3_, p_150014_4_);
            }

            p_150014_1_.playAuxSFXAtEntity((EntityPlayer)null, 1003, p_150014_2_, p_150014_3_, p_150014_4_, 0);
        }
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor Block
     */
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block par5Block)
    {
        int l = par1World.getBlockMetadata(par2, par3, par4);

        if ((l & 8) == 0)
        {
            boolean flag = false;

            if (par1World.getBlock(par2, par3 + 1, par4) != this)
            {
            	par1World.setBlockToAir(par2, par3, par4);
                flag = true;
            }

//            if (!World.doesBlockHaveSolidTopSurface(par1World, par2, par3 - 1, par4))
//            {
//            	par1World.setBlockToAir(par2, par3, par4);
//                flag = true;
//
//                if (par1World.getBlock(par2, par3 + 1, par4) == this)
//                {
//                	par1World.setBlockToAir(par2, par3 + 1, par4);
//                }
//            }

            if (flag)
            {
                if (!par1World.isRemote)
                {
                    this.dropBlockAsItem(par1World, par2, par3, par4, l, 0);
                }
            }
            else
            {
                boolean flag1 = par1World.isBlockIndirectlyGettingPowered(par2, par3, par4) || par1World.isBlockIndirectlyGettingPowered(par2, par3 + 1, par4);

                if ((flag1 || par5Block.canProvidePower()) && par5Block != this)
                {
                	if(hasActiveKeypadNextTo(par1World, par2, par3, par4) || hasActiveKeypadNextTo(par1World, par2, par3 + 1, par4) || hasActiveInventoryScannerNextTo(par1World, par2, par3, par4) || hasActiveInventoryScannerNextTo(par1World, par2, par3 + 1, par4) || hasActiveReaderNextTo(par1World, par2, par3, par4) || hasActiveReaderNextTo(par1World, par2, par3 + 1, par4) || hasActiveScannerNextTo(par1World, par2, par3, par4) || hasActiveScannerNextTo(par1World, par2, par3 + 1, par4) || hasActiveLaserNextTo(par1World, par2, par3, par4) || hasActiveLaserNextTo(par1World, par2, par3 + 1, par4)){
                		this.func_150014_a(par1World, par2, par3, par4, flag1);         
                	}else if(!flag1){
                		this.func_150014_a(par1World, par2, par3, par4, flag1);         
                	}
                }
            }
        }
        else
        {
            if (par1World.getBlock(par2, par3 - 1, par4) != this)
            {
            	par1World.setBlockToAir(par2, par3, par4);
            }

            if (par5Block != this)
            {
                this.onNeighborBlockChange(par1World, par2, par3 - 1, par4, par5Block);
            }
        }
    }
    
    private boolean hasActiveLaserNextTo(World par1World, int par2, int par3, int par4) {
    	if(par1World.getBlock(par2 + 1, par3, par4) == mod_SecurityCraft.LaserBlock && par1World.getBlockMetadata(par2 + 1, par3, par4) == 2){
    		return true;
    	}else if(par1World.getBlock(par2 - 1, par3, par4) == mod_SecurityCraft.LaserBlock && par1World.getBlockMetadata(par2 - 1, par3, par4) == 2){
    		return true;
    	}else if(par1World.getBlock(par2, par3, par4 + 1) == mod_SecurityCraft.LaserBlock && par1World.getBlockMetadata(par2, par3, par4 + 1) == 2){
    		return true;
    	}else if(par1World.getBlock(par2, par3, par4 - 1) == mod_SecurityCraft.LaserBlock && par1World.getBlockMetadata(par2, par3, par4 - 1) == 2){
    		return true;
    	}else{
    		return false;
    	}
	}
    
    private boolean hasActiveScannerNextTo(World par1World, int par2, int par3, int par4) {
    	if(par1World.getBlock(par2 + 1, par3, par4) == mod_SecurityCraft.retinalScanner && par1World.getBlockMetadata(par2 + 1, par3, par4) > 6 && par1World.getBlockMetadata(par2 + 1, par3, par4) < 11){
    		return true;
    	}else if(par1World.getBlock(par2 - 1, par3, par4) == mod_SecurityCraft.retinalScanner && par1World.getBlockMetadata(par2 - 1, par3, par4) > 6 && par1World.getBlockMetadata(par2 - 1, par3, par4) < 11){
    		return true;
    	}else if(par1World.getBlock(par2, par3, par4 + 1) == mod_SecurityCraft.retinalScanner && par1World.getBlockMetadata(par2, par3, par4 + 1) > 6 && par1World.getBlockMetadata(par2, par3, par4 + 1) < 11){
    		return true;
    	}else if(par1World.getBlock(par2, par3, par4 - 1) == mod_SecurityCraft.retinalScanner && par1World.getBlockMetadata(par2, par3, par4 - 1) > 6 && par1World.getBlockMetadata(par2, par3, par4 - 1) < 11){
    		return true;
    	}else{
    		return false;
    	}
	}

	private boolean hasActiveKeypadNextTo(World par1World, int par2, int par3, int par4){
    	if(par1World.getBlock(par2 + 1, par3, par4) == mod_SecurityCraft.Keypad && par1World.getBlockMetadata(par2 + 1, par3, par4) > 6 && par1World.getBlockMetadata(par2 + 1, par3, par4) < 11){
    		return true;
    	}else if(par1World.getBlock(par2 - 1, par3, par4) == mod_SecurityCraft.Keypad && par1World.getBlockMetadata(par2 - 1, par3, par4) > 6 && par1World.getBlockMetadata(par2 - 1, par3, par4) < 11){
    		return true;
    	}else if(par1World.getBlock(par2, par3, par4 + 1) == mod_SecurityCraft.Keypad && par1World.getBlockMetadata(par2, par3, par4 + 1) > 6 && par1World.getBlockMetadata(par2, par3, par4 + 1) < 11){
    		return true;
    	}else if(par1World.getBlock(par2, par3, par4 - 1) == mod_SecurityCraft.Keypad && par1World.getBlockMetadata(par2, par3, par4 - 1) > 6 && par1World.getBlockMetadata(par2, par3, par4 - 1) < 11){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    private boolean hasActiveReaderNextTo(World par1World, int par2, int par3, int par4){
    	if(par1World.getBlock(par2 + 1, par3, par4) == mod_SecurityCraft.keycardReader && ((TileEntityKeycardReader)par1World.getTileEntity(par2 + 1, par3, par4)).getIsProvidingPower()){
    		return true;
    	}else if(par1World.getBlock(par2 - 1, par3, par4) == mod_SecurityCraft.keycardReader && ((TileEntityKeycardReader)par1World.getTileEntity(par2 - 1, par3, par4)).getIsProvidingPower()){
    		return true;
    	}else if(par1World.getBlock(par2, par3, par4 + 1) == mod_SecurityCraft.keycardReader && ((TileEntityKeycardReader)par1World.getTileEntity(par2, par3, par4 + 1)).getIsProvidingPower()){
    		return true;
    	}else if(par1World.getBlock(par2, par3, par4 - 1) == mod_SecurityCraft.keycardReader && ((TileEntityKeycardReader)par1World.getTileEntity(par2, par3, par4 - 1)).getIsProvidingPower()){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    private boolean hasActiveInventoryScannerNextTo(World par1World, int par2, int par3, int par4){
    	if(par1World.getBlock(par2 + 1, par3, par4) == mod_SecurityCraft.inventoryScanner && ((TileEntityInventoryScanner) par1World.getTileEntity(par2 + 1, par3, par4)).getType().matches("redstone") && ((TileEntityInventoryScanner) par1World.getTileEntity(par2 + 1, par3, par4)).shouldProvidePower()){
    		return true;
    	}else if(par1World.getBlock(par2 - 1, par3, par4) == mod_SecurityCraft.inventoryScanner && ((TileEntityInventoryScanner) par1World.getTileEntity(par2 - 1, par3, par4)).getType().matches("redstone") && ((TileEntityInventoryScanner) par1World.getTileEntity(par2 - 1, par3, par4)).shouldProvidePower()){
    		return true;
    	}else if(par1World.getBlock(par2, par3, par4 + 1) == mod_SecurityCraft.inventoryScanner && ((TileEntityInventoryScanner) par1World.getTileEntity(par2, par3, par4 + 1)).getType().matches("redstone") && ((TileEntityInventoryScanner) par1World.getTileEntity(par2, par3, par4 + 1)).shouldProvidePower()){
    		return true;
    	}else if(par1World.getBlock(par2, par3, par4 + 1) == mod_SecurityCraft.inventoryScanner && ((TileEntityInventoryScanner) par1World.getTileEntity(par2, par3, par4 - 1)).getType().matches("redstone") && ((TileEntityInventoryScanner) par1World.getTileEntity(par2, par3, par4 - 1)).shouldProvidePower()){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    private void notifyPlayers(String username, EntityPlayer par2EntityPlayer, int par3, int par4, int par5) {
    	HelpfulMethods.sendMessageToPlayer(par2EntityPlayer, username + " destroyed a reinforced door with a door remover at X: " + par3 + " Y: " + par4 + " Z: " + par5, null);
	}

	
    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
    {
		return mod_SecurityCraft.doorIndestructableIronItem;
    }

    /**
     * Ray traces through the blocks collision from start vector to end vector returning a ray trace hit. Args: world,
     * x, y, z, startVec, endVec
     */
    public MovingObjectPosition collisionRayTrace(World p_149731_1_, int p_149731_2_, int p_149731_3_, int p_149731_4_, Vec3 p_149731_5_, Vec3 p_149731_6_)
    {
        this.setBlockBoundsBasedOnState(p_149731_1_, p_149731_2_, p_149731_3_, p_149731_4_);
        return super.collisionRayTrace(p_149731_1_, p_149731_2_, p_149731_3_, p_149731_4_, p_149731_5_, p_149731_6_);
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlaceBlockAt(World p_149742_1_, int p_149742_2_, int p_149742_3_, int p_149742_4_)
    {
        return p_149742_3_ >= 255 ? false : World.doesBlockHaveSolidTopSurface(p_149742_1_, p_149742_2_, p_149742_3_ - 1, p_149742_4_) && super.canPlaceBlockAt(p_149742_1_, p_149742_2_, p_149742_3_, p_149742_4_) && super.canPlaceBlockAt(p_149742_1_, p_149742_2_, p_149742_3_ + 1, p_149742_4_);
    }

    /**
     * Returns the mobility information of the block, 0 = free, 1 = can't push but can move over, 2 = total immobility
     * and stop pistons
     */
    public int getMobilityFlag()
    {
        return 1;
    }

    public int func_150012_g(IBlockAccess p_150012_1_, int p_150012_2_, int p_150012_3_, int p_150012_4_)
    {
        int l = p_150012_1_.getBlockMetadata(p_150012_2_, p_150012_3_, p_150012_4_);
        boolean flag = (l & 8) != 0;
        int i1;
        int j1;

        if (flag)
        {
            i1 = p_150012_1_.getBlockMetadata(p_150012_2_, p_150012_3_ - 1, p_150012_4_);
            j1 = l;
        }
        else
        {
            i1 = l;
            j1 = p_150012_1_.getBlockMetadata(p_150012_2_, p_150012_3_ + 1, p_150012_4_);
        }

        boolean flag1 = (j1 & 1) != 0;
        return i1 & 7 | (flag ? 8 : 0) | (flag1 ? 16 : 0);
    }

    /**
     * Gets an item for the block being called on. Args: world, x, y, z
     */
    @SideOnly(Side.CLIENT)
    public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_)
    {
        return mod_SecurityCraft.doorIndestructableIronItem;
    }

    /**
     * Called when the block is attempted to be harvested
     */
    public void onBlockHarvested(World p_149681_1_, int p_149681_2_, int p_149681_3_, int p_149681_4_, int p_149681_5_, EntityPlayer p_149681_6_)
    {
        if (p_149681_6_.capabilities.isCreativeMode && (p_149681_5_ & 8) != 0 && p_149681_1_.getBlock(p_149681_2_, p_149681_3_ - 1, p_149681_4_) == this)
        {
            p_149681_1_.setBlockToAir(p_149681_2_, p_149681_3_ - 1, p_149681_4_);
        }
    }

	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityReinforcedDoor();
	}
}