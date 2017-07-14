package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockReinforcedDoor extends BlockDoor implements ITileEntityProvider{
    
    public BlockReinforcedDoor(Material materialIn) {
		super(materialIn);
		this.isBlockContainer = true;
	}

	/**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor Block
     */
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock){
        if (state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER)
        {
            BlockPos blockpos1 = pos.down();
            IBlockState iblockstate1 = worldIn.getBlockState(blockpos1);

            if (iblockstate1.getBlock() != this)
            {
                worldIn.setBlockToAir(pos);
            }
            else if (neighborBlock != this)
            {
                this.onNeighborBlockChange(worldIn, blockpos1, iblockstate1, neighborBlock);
            }
        }
        else
        {
            boolean flag1 = false;
            BlockPos blockpos2 = pos.up();
            IBlockState iblockstate2 = worldIn.getBlockState(blockpos2);

            if (iblockstate2.getBlock() != this)
            {
                worldIn.setBlockToAir(pos);
                flag1 = true;
            }

            if (!World.doesBlockHaveSolidTopSurface(worldIn, pos.down()))
            {
                worldIn.setBlockToAir(pos);
                flag1 = true;

                if (iblockstate2.getBlock() == this)
                {
                    worldIn.setBlockToAir(blockpos2);
                }
            }

            if (flag1)
            {
                if (!worldIn.isRemote)
                {
                    this.dropBlockAsItem(worldIn, pos, state, 0);
                }
            }
            else
            {
                boolean flag = hasActiveKeypadNextTo(worldIn, pos) || hasActiveKeypadNextTo(worldIn, pos.up()) || hasActiveInventoryScannerNextTo(worldIn, pos) || hasActiveInventoryScannerNextTo(worldIn, pos.up()) || hasActiveReaderNextTo(worldIn, pos) || hasActiveReaderNextTo(worldIn, pos.up()) || hasActiveScannerNextTo(worldIn, pos) || hasActiveScannerNextTo(worldIn, pos.up()) || hasActiveLaserNextTo(worldIn, pos) || hasActiveLaserNextTo(worldIn, pos.up());
                
//                if(flag && !(hasActiveKeypadNextTo(worldIn, pos) || hasActiveKeypadNextTo(worldIn, pos.up()) || hasActiveInventoryScannerNextTo(worldIn, pos) || hasActiveInventoryScannerNextTo(worldIn, pos.up()) || hasActiveReaderNextTo(worldIn, pos) || hasActiveReaderNextTo(worldIn, pos.up()) || hasActiveScannerNextTo(worldIn, pos) || hasActiveScannerNextTo(worldIn, pos.up()) || hasActiveLaserNextTo(worldIn, pos) || hasActiveLaserNextTo(worldIn, pos.up()) && neighborBlock != this)){
//                	System.out.println("Powered by vanilla block");
//                }else if(hasActiveKeypadNextTo(worldIn, pos) || hasActiveKeypadNextTo(worldIn, pos.up()) || hasActiveInventoryScannerNextTo(worldIn, pos) || hasActiveInventoryScannerNextTo(worldIn, pos.up()) || hasActiveReaderNextTo(worldIn, pos) || hasActiveReaderNextTo(worldIn, pos.up()) || hasActiveScannerNextTo(worldIn, pos) || hasActiveScannerNextTo(worldIn, pos.up()) || hasActiveLaserNextTo(worldIn, pos) || hasActiveLaserNextTo(worldIn, pos.up()) && neighborBlock != this){
//                	System.out.println("Powered by SC block");
//                }
                
                if (((flag || neighborBlock.canProvidePower())) && neighborBlock != this && flag != ((Boolean)iblockstate2.getValue(POWERED)).booleanValue())
                {
                    worldIn.setBlockState(blockpos2, iblockstate2.withProperty(POWERED, Boolean.valueOf(flag)), 2);

                    if (flag != ((Boolean)state.getValue(OPEN)).booleanValue())
                    {
                        worldIn.setBlockState(pos, state.withProperty(OPEN, Boolean.valueOf(flag)), 2);
                        worldIn.markBlockRangeForRenderUpdate(pos, pos);
                        worldIn.playAuxSFXAtEntity((EntityPlayer)null, flag ? 1003 : 1006, pos, 0);
                    }
                }
            }
        }
    }
    
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        super.breakBlock(worldIn, pos, state);
        worldIn.removeTileEntity(pos);
    }

    public boolean onBlockEventReceived(World worldIn, BlockPos pos, IBlockState state, int eventID, int eventParam)
    {
        super.onBlockEventReceived(worldIn, pos, state, eventID, eventParam);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity == null ? false : tileentity.receiveClientEvent(eventID, eventParam);
    }
    
    private boolean hasActiveLaserNextTo(World par1World, BlockPos pos) {
    	if(BlockUtils.getBlock(par1World, pos.east()) == mod_SecurityCraft.laserBlock && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(par1World, pos.east(), BlockLaserBlock.POWERED)).booleanValue() && ((IOwnable) par1World.getTileEntity(pos.east())).getOwner().owns((IOwnable) par1World.getTileEntity(pos))){
    		return true;
    	}else if(BlockUtils.getBlock(par1World, pos.west()) == mod_SecurityCraft.laserBlock && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(par1World, pos.west(), BlockLaserBlock.POWERED)).booleanValue() && ((IOwnable) par1World.getTileEntity(pos.west())).getOwner().owns((IOwnable) par1World.getTileEntity(pos))){
    		return true;
    	}else if(BlockUtils.getBlock(par1World, pos.south()) == mod_SecurityCraft.laserBlock && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(par1World, pos.south(), BlockLaserBlock.POWERED)).booleanValue() && ((IOwnable) par1World.getTileEntity(pos.south())).getOwner().owns((IOwnable) par1World.getTileEntity(pos))){
    		return true;
    	}else if(BlockUtils.getBlock(par1World, pos.north()) == mod_SecurityCraft.laserBlock && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(par1World, pos.north(), BlockLaserBlock.POWERED)).booleanValue() && ((IOwnable) par1World.getTileEntity(pos.north())).getOwner().owns((IOwnable) par1World.getTileEntity(pos))){
    		return true;
    	}else{
    		return false;
    	}
	}
    
    private boolean hasActiveScannerNextTo(World par1World, BlockPos pos) {
    	if(BlockUtils.getBlock(par1World, pos.east()) == mod_SecurityCraft.retinalScanner && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(par1World, pos.east(), BlockRetinalScanner.POWERED)).booleanValue() && ((IOwnable) par1World.getTileEntity(pos.east())).getOwner().owns((IOwnable) par1World.getTileEntity(pos))){
    		return true;
    	}else if(BlockUtils.getBlock(par1World, pos.west()) == mod_SecurityCraft.retinalScanner && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(par1World, pos.west(), BlockRetinalScanner.POWERED)).booleanValue() && ((IOwnable) par1World.getTileEntity(pos.west())).getOwner().owns((IOwnable) par1World.getTileEntity(pos))){
    		return true;
    	}else if(BlockUtils.getBlock(par1World, pos.south()) == mod_SecurityCraft.retinalScanner && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(par1World, pos.south(), BlockRetinalScanner.POWERED)).booleanValue() && ((IOwnable) par1World.getTileEntity(pos.south())).getOwner().owns((IOwnable) par1World.getTileEntity(pos))){
    		return true;
    	}else if(BlockUtils.getBlock(par1World, pos.north()) == mod_SecurityCraft.retinalScanner && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(par1World, pos.north(), BlockRetinalScanner.POWERED)).booleanValue() && ((IOwnable) par1World.getTileEntity(pos.north())).getOwner().owns((IOwnable) par1World.getTileEntity(pos))){
    		return true;
    	}else{
    		return false;
    	}
	}

	private boolean hasActiveKeypadNextTo(World par1World, BlockPos pos){
    	if(BlockUtils.getBlock(par1World, pos.east()) == mod_SecurityCraft.keypad && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(par1World, pos.east(), BlockKeypad.POWERED)).booleanValue() && ((IOwnable) par1World.getTileEntity(pos.east())).getOwner().owns((IOwnable) par1World.getTileEntity(pos))){
    		return true;
    	}else if(BlockUtils.getBlock(par1World, pos.west()) == mod_SecurityCraft.keypad && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(par1World, pos.west(), BlockKeypad.POWERED)).booleanValue() && ((IOwnable) par1World.getTileEntity(pos.west())).getOwner().owns((IOwnable) par1World.getTileEntity(pos))){
    		return true;
    	}else if(BlockUtils.getBlock(par1World, pos.south()) == mod_SecurityCraft.keypad && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(par1World, pos.south(), BlockKeypad.POWERED)).booleanValue() && ((IOwnable) par1World.getTileEntity(pos.south())).getOwner().owns((IOwnable) par1World.getTileEntity(pos))){
    		return true;
    	}else if(BlockUtils.getBlock(par1World, pos.north()) == mod_SecurityCraft.keypad && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(par1World, pos.north(), BlockKeypad.POWERED)).booleanValue() && ((IOwnable) par1World.getTileEntity(pos.north())).getOwner().owns((IOwnable) par1World.getTileEntity(pos))){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    private boolean hasActiveReaderNextTo(World par1World, BlockPos pos){
    	if(BlockUtils.getBlock(par1World, pos.east()) == mod_SecurityCraft.keycardReader && BlockUtils.getBlockPropertyAsBoolean(par1World, pos.east(), BlockKeycardReader.POWERED) && ((IOwnable) par1World.getTileEntity(pos.east())).getOwner().owns((IOwnable) par1World.getTileEntity(pos))){
    		return true;
    	}else if(BlockUtils.getBlock(par1World, pos.west()) == mod_SecurityCraft.keycardReader && BlockUtils.getBlockPropertyAsBoolean(par1World, pos.west(), BlockKeycardReader.POWERED) && ((IOwnable) par1World.getTileEntity(pos.west())).getOwner().owns((IOwnable) par1World.getTileEntity(pos))){
    		return true;
    	}else if(BlockUtils.getBlock(par1World, pos.south()) == mod_SecurityCraft.keycardReader && BlockUtils.getBlockPropertyAsBoolean(par1World, pos.south(), BlockKeycardReader.POWERED) && ((IOwnable) par1World.getTileEntity(pos.south())).getOwner().owns((IOwnable) par1World.getTileEntity(pos))){
    		return true;
    	}else if(BlockUtils.getBlock(par1World, pos.north()) == mod_SecurityCraft.keycardReader && BlockUtils.getBlockPropertyAsBoolean(par1World, pos.north(), BlockKeycardReader.POWERED) && ((IOwnable) par1World.getTileEntity(pos.north())).getOwner().owns((IOwnable) par1World.getTileEntity(pos))){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    private boolean hasActiveInventoryScannerNextTo(World par1World, BlockPos pos){
    	if(BlockUtils.getBlock(par1World, pos.east()) == mod_SecurityCraft.inventoryScanner && ((TileEntityInventoryScanner) par1World.getTileEntity(pos.east())).getType().matches("redstone") && ((TileEntityInventoryScanner) par1World.getTileEntity(pos.east())).shouldProvidePower() && ((IOwnable) par1World.getTileEntity(pos.east())).getOwner().owns((IOwnable) par1World.getTileEntity(pos))){
    		return true;
    	}else if(BlockUtils.getBlock(par1World, pos.west()) == mod_SecurityCraft.inventoryScanner && ((TileEntityInventoryScanner) par1World.getTileEntity(pos.west())).getType().matches("redstone") && ((TileEntityInventoryScanner) par1World.getTileEntity(pos.west())).shouldProvidePower() && ((IOwnable) par1World.getTileEntity(pos.west())).getOwner().owns((IOwnable) par1World.getTileEntity(pos))){
    		return true;
    	}else if(BlockUtils.getBlock(par1World, pos.south()) == mod_SecurityCraft.inventoryScanner && ((TileEntityInventoryScanner) par1World.getTileEntity(pos.south())).getType().matches("redstone") && ((TileEntityInventoryScanner) par1World.getTileEntity(pos.south())).shouldProvidePower() && ((IOwnable) par1World.getTileEntity(pos.south())).getOwner().owns((IOwnable) par1World.getTileEntity(pos))){
    		return true;
    	}else if(BlockUtils.getBlock(par1World, pos.north()) == mod_SecurityCraft.inventoryScanner && ((TileEntityInventoryScanner) par1World.getTileEntity(pos.north())).getType().matches("redstone") && ((TileEntityInventoryScanner) par1World.getTileEntity(pos.north())).shouldProvidePower() && ((IOwnable) par1World.getTileEntity(pos.north())).getOwner().owns((IOwnable) par1World.getTileEntity(pos))){
    		return true;
    	}else{
    		return false;
    	}
    }

	@SideOnly(Side.CLIENT)
    public Item getItem(World world, BlockPos pos){
		return mod_SecurityCraft.reinforcedDoorItem;
    }
	
	public Item getItemDropped(IBlockState state, Random rand, int fortune){
		return state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER ? null : mod_SecurityCraft.reinforcedDoorItem;
    }

	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityOwnable();
	}

	
}