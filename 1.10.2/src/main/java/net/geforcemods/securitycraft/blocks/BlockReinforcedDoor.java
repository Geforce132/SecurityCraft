package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockReinforcedDoor extends BlockDoor implements ITileEntityProvider{
    
    public BlockReinforcedDoor(Material materialIn) {
		super(materialIn);
		this.isBlockContainer = true;
		setSoundType(SoundType.METAL);
	}

    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor)
	{
		World worldIn = (World)world;
		IBlockState state = worldIn.getBlockState(pos);
		Block neighborBlock = worldIn.getBlockState(neighbor).getBlock();
		
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
                this.onNeighborChange(world, blockpos1, neighbor);
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

            if (!worldIn.isSideSolid(pos.down(), EnumFacing.UP))
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
                
                if (((flag || neighborBlock.canProvidePower(iblockstate2))) && neighborBlock != this && flag != iblockstate2.getValue(POWERED).booleanValue())
                {
                    worldIn.setBlockState(blockpos2, iblockstate2.withProperty(POWERED, Boolean.valueOf(flag)), 2);

                    if (flag != state.getValue(OPEN).booleanValue())
                    {
                        worldIn.setBlockState(pos, state.withProperty(OPEN, Boolean.valueOf(flag)), 2);
                        worldIn.markBlockRangeForRenderUpdate(pos, pos);
                        worldIn.playEvent((EntityPlayer)null, flag ? 1005 : 1011, pos, 0);
                    }
                }
            }
        }
    }
    
    @Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        super.breakBlock(worldIn, pos, state);
        worldIn.removeTileEntity(pos);
    }

    @Override
    public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param)
    {
        super.eventReceived(state, worldIn, pos, id, param);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
    }
    
    private boolean hasActiveLaserNextTo(World par1World, BlockPos pos) {
    	if(BlockUtils.getBlock(par1World, pos.east()) == mod_SecurityCraft.laserBlock && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(par1World, pos.east(), BlockLaserBlock.POWERED)).booleanValue()){
    		return true;
    	}else if(BlockUtils.getBlock(par1World, pos.west()) == mod_SecurityCraft.laserBlock && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(par1World, pos.west(), BlockLaserBlock.POWERED)).booleanValue()){
    		return true;
    	}else if(BlockUtils.getBlock(par1World, pos.south()) == mod_SecurityCraft.laserBlock && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(par1World, pos.south(), BlockLaserBlock.POWERED)).booleanValue()){
    		return true;
    	}else if(BlockUtils.getBlock(par1World, pos.north()) == mod_SecurityCraft.laserBlock && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(par1World, pos.north(), BlockLaserBlock.POWERED)).booleanValue()){
    		return true;
    	}else{
    		return false;
    	}
	}
    
    private boolean hasActiveScannerNextTo(World par1World, BlockPos pos) {
    	if(BlockUtils.getBlock(par1World, pos.east()) == mod_SecurityCraft.retinalScanner && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(par1World, pos.east(), BlockRetinalScanner.POWERED)).booleanValue()){
    		return true;
    	}else if(BlockUtils.getBlock(par1World, pos.west()) == mod_SecurityCraft.retinalScanner && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(par1World, pos.west(), BlockRetinalScanner.POWERED)).booleanValue()){
    		return true;
    	}else if(BlockUtils.getBlock(par1World, pos.south()) == mod_SecurityCraft.retinalScanner && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(par1World, pos.south(), BlockRetinalScanner.POWERED)).booleanValue()){
    		return true;
    	}else if(BlockUtils.getBlock(par1World, pos.north()) == mod_SecurityCraft.retinalScanner && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(par1World, pos.north(), BlockRetinalScanner.POWERED)).booleanValue()){
    		return true;
    	}else{
    		return false;
    	}
	}

	private boolean hasActiveKeypadNextTo(World par1World, BlockPos pos){
    	if(BlockUtils.getBlock(par1World, pos.east()) == mod_SecurityCraft.keypad && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(par1World, pos.east(), BlockKeypad.POWERED)).booleanValue()){
    		return true;
    	}else if(BlockUtils.getBlock(par1World, pos.west()) == mod_SecurityCraft.keypad && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(par1World, pos.west(), BlockKeypad.POWERED)).booleanValue()){
    		return true;
    	}else if(BlockUtils.getBlock(par1World, pos.south()) == mod_SecurityCraft.keypad && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(par1World, pos.south(), BlockKeypad.POWERED)).booleanValue()){
    		return true;
    	}else if(BlockUtils.getBlock(par1World, pos.north()) == mod_SecurityCraft.keypad && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(par1World, pos.north(), BlockKeypad.POWERED)).booleanValue()){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    private boolean hasActiveReaderNextTo(World par1World, BlockPos pos){
    	if(BlockUtils.getBlock(par1World, pos.east()) == mod_SecurityCraft.keycardReader && BlockUtils.getBlockPropertyAsBoolean(par1World, pos.east(), BlockKeycardReader.POWERED)){
    		return true;
    	}else if(BlockUtils.getBlock(par1World, pos.west()) == mod_SecurityCraft.keycardReader && BlockUtils.getBlockPropertyAsBoolean(par1World, pos.west(), BlockKeycardReader.POWERED)){
    		return true;
    	}else if(BlockUtils.getBlock(par1World, pos.south()) == mod_SecurityCraft.keycardReader && BlockUtils.getBlockPropertyAsBoolean(par1World, pos.south(), BlockKeycardReader.POWERED)){
    		return true;
    	}else if(BlockUtils.getBlock(par1World, pos.north()) == mod_SecurityCraft.keycardReader && BlockUtils.getBlockPropertyAsBoolean(par1World, pos.north(), BlockKeycardReader.POWERED)){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    private boolean hasActiveInventoryScannerNextTo(World par1World, BlockPos pos){
    	if(BlockUtils.getBlock(par1World, pos.east()) == mod_SecurityCraft.inventoryScanner && ((TileEntityInventoryScanner) par1World.getTileEntity(pos.east())).getType().matches("redstone") && ((TileEntityInventoryScanner) par1World.getTileEntity(pos.east())).shouldProvidePower()){
    		return true;
    	}else if(BlockUtils.getBlock(par1World, pos.west()) == mod_SecurityCraft.inventoryScanner && ((TileEntityInventoryScanner) par1World.getTileEntity(pos.west())).getType().matches("redstone") && ((TileEntityInventoryScanner) par1World.getTileEntity(pos.west())).shouldProvidePower()){
    		return true;
    	}else if(BlockUtils.getBlock(par1World, pos.south()) == mod_SecurityCraft.inventoryScanner && ((TileEntityInventoryScanner) par1World.getTileEntity(pos.south())).getType().matches("redstone") && ((TileEntityInventoryScanner) par1World.getTileEntity(pos.south())).shouldProvidePower()){
    		return true;
    	}else if(BlockUtils.getBlock(par1World, pos.north()) == mod_SecurityCraft.inventoryScanner && ((TileEntityInventoryScanner) par1World.getTileEntity(pos.north())).getType().matches("redstone") && ((TileEntityInventoryScanner) par1World.getTileEntity(pos.north())).shouldProvidePower()){
    		return true;
    	}else{
    		return false;
    	}
    }

	@SideOnly(Side.CLIENT)
    public ItemStack getItem(World world, BlockPos pos, IBlockState state){
		return new ItemStack(mod_SecurityCraft.reinforcedDoorItem);
    }
	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune){
		return state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER ? null : mod_SecurityCraft.reinforcedDoorItem;
    }

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityOwnable();
	}

	
}