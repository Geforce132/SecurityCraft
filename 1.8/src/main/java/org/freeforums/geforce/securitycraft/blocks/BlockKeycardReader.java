package org.freeforums.geforce.securitycraft.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.freeforums.geforce.securitycraft.enums.EnumCustomModules;
import org.freeforums.geforce.securitycraft.interfaces.IHelpInfo;
import org.freeforums.geforce.securitycraft.items.ItemKeycardBase;
import org.freeforums.geforce.securitycraft.main.HelpfulMethods;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeycardReader;
import org.freeforums.geforce.securitycraft.timers.ScheduleKeycardUpdate;

public class BlockKeycardReader extends BlockOwnable implements IHelpInfo{
	
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    
	public BlockKeycardReader(Material par2Material) {
		super(par2Material);
	}
	
	public int getRenderType(){
		return 3;
	}
	    	     
    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World par1World, BlockPos pos, IBlockState state, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack){
        super.onBlockPlacedBy(par1World, pos, state, par5EntityLivingBase, par6ItemStack);
        
        Block block = par1World.getBlockState(pos.north()).getBlock();
        Block block1 = par1World.getBlockState(pos.south()).getBlock();
        Block block2 = par1World.getBlockState(pos.west()).getBlock();
        Block block3 = par1World.getBlockState(pos.east()).getBlock();
        EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);

        if (enumfacing == EnumFacing.NORTH && block.isFullBlock() && !block1.isFullBlock())
        {
            enumfacing = EnumFacing.SOUTH;
        }
        else if (enumfacing == EnumFacing.SOUTH && block1.isFullBlock() && !block.isFullBlock())
        {
            enumfacing = EnumFacing.NORTH;
        }
        else if (enumfacing == EnumFacing.WEST && block2.isFullBlock() && !block3.isFullBlock())
        {
            enumfacing = EnumFacing.EAST;
        }
        else if (enumfacing == EnumFacing.EAST && block3.isFullBlock() && !block2.isFullBlock())
        {
            enumfacing = EnumFacing.WEST;
        }

        par1World.setBlockState(pos, state.withProperty(FACING, enumfacing), 2);
    }
	    	
	public void insertCard(World par1World, BlockPos pos, ItemStack par5ItemStack, EntityPlayer par6EntityPlayer) {		
		if(HelpfulMethods.checkForModule(par1World, pos, par6EntityPlayer, EnumCustomModules.WHITELIST) || HelpfulMethods.checkForModule(par1World, pos, par6EntityPlayer, EnumCustomModules.BLACKLIST)){ return; }
		
		if(((TileEntityKeycardReader)par1World.getTileEntity(pos)).getPassLV() != 0 && (!((TileEntityKeycardReader)par1World.getTileEntity(pos)).doesRequireExactKeycard() && ((TileEntityKeycardReader)par1World.getTileEntity(pos)).getPassLV() <= ((ItemKeycardBase) par5ItemStack.getItem()).getKeycardLV(par5ItemStack) || ((TileEntityKeycardReader)par1World.getTileEntity(pos)).doesRequireExactKeycard() && ((TileEntityKeycardReader)par1World.getTileEntity(pos)).getPassLV() == ((ItemKeycardBase) par5ItemStack.getItem()).getKeycardLV(par5ItemStack))){
			if(((ItemKeycardBase) par5ItemStack.getItem()).getKeycardLV(par5ItemStack) == 6 && par5ItemStack.getTagCompound() != null && !par6EntityPlayer.capabilities.isCreativeMode){
				par5ItemStack.getTagCompound().setInteger("Uses", par5ItemStack.getTagCompound().getInteger("Uses") - 1);
				
				if(par5ItemStack.getTagCompound().getInteger("Uses") <= 0){
					par5ItemStack.stackSize--;
				}
			}
			
			((TileEntityKeycardReader)par1World.getTileEntity(pos)).setIsProvidingPower(true);
			new ScheduleKeycardUpdate(3, par1World, pos.getX(), pos.getY(), pos.getZ());
			par1World.notifyNeighborsOfStateChange(pos, this);
		}else{
			if(((TileEntityKeycardReader)par1World.getTileEntity(pos)).getPassLV() != 0){
				HelpfulMethods.sendMessageToPlayer(par6EntityPlayer, "Required security level: " + ((TileEntityKeycardReader)par1World.getTileEntity(pos)).getPassLV() + " Your keycard's level: " + ((ItemKeycardBase) par5ItemStack.getItem()).getKeycardLV(par5ItemStack), null);
			}else{
				HelpfulMethods.sendMessageToPlayer(par6EntityPlayer, "Keycard reader's security level not set!", EnumChatFormatting.RED);
			}
		}
		
	}
	
    public boolean onBlockActivated(World par1World, BlockPos pos, IBlockState state, EntityPlayer par5EntityPlayer, EnumFacing side, float par7, float par8, float par9){
    	if(par1World.isRemote){
    		return true;
    	}
    	
    	if(par5EntityPlayer.getCurrentEquippedItem() == null || par5EntityPlayer.getCurrentEquippedItem().getItem() != mod_SecurityCraft.keycardLV1 || par5EntityPlayer.getCurrentEquippedItem().getItem() != mod_SecurityCraft.keycardLV2 || par5EntityPlayer.getCurrentEquippedItem().getItem() != mod_SecurityCraft.keycardLV3){
    		if(((TileEntityKeycardReader) par1World.getTileEntity(pos)).getPassLV() == 0){    	
		    	par5EntityPlayer.openGui(mod_SecurityCraft.instance, 4, par1World, pos.getX(), pos.getY(), pos.getZ());
		    	return true;
    		}
    	
    	}
    	
		return false;
    }
    
    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World par1World, BlockPos pos, IBlockState state, Random par5Random){
    	if(((TileEntityKeycardReader)par1World.getTileEntity(pos)).getIsProvidingPower()){
            double d0 = (double)((float)pos.getX() + 0.5F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2D;
            double d1 = (double)((float)pos.getY() + 0.7F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2D;
            double d2 = (double)((float)pos.getZ() + 0.5F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2D;
            double d3 = 0.2199999988079071D;
            double d4 = 0.27000001072883606D;

            
            par1World.spawnParticle(EnumParticleTypes.REDSTONE, d0 - d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
            par1World.spawnParticle(EnumParticleTypes.REDSTONE, d0 + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D); 
            par1World.spawnParticle(EnumParticleTypes.REDSTONE, d0, d1 + d3, d2 - d4, 0.0D, 0.0D, 0.0D);
            par1World.spawnParticle(EnumParticleTypes.REDSTONE, d0, d1 + d3, d2 + d4, 0.0D, 0.0D, 0.0D);
            par1World.spawnParticle(EnumParticleTypes.REDSTONE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        } 
    }
    
    /**
     * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
     * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
     * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, BlockPos pos, IBlockState state, EnumFacing side)
    {
    	if(((TileEntityKeycardReader)par1IBlockAccess.getTileEntity(pos)).getIsProvidingPower()){
    		return 15;
    	}else{
    		return 0;
    	}
    }
    
    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower()
    {
    	return true;
    }
    
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }
    
    @SideOnly(Side.CLIENT)
    public IBlockState getStateForEntityRender(IBlockState state)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
    }

    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.values()[meta].getAxis() == EnumFacing.Axis.Y ? EnumFacing.NORTH : EnumFacing.values()[meta]);    
    }

    public int getMetaFromState(IBlockState state)
    {   	
    	return ((EnumFacing) state.getValue(FACING)).getIndex(); 	
    }
    
    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {FACING});
    }
    
    public TileEntity createNewTileEntity(World world, int par2) {
		return new TileEntityKeycardReader();
	}

	public String getHelpInfo() {
		return "The keycard reader emits a 15-block redstone redstone signal if you insert a keycard with a security level equal to or higher then (as specified in the GUI) the level selected in the reader's GUI.";
	}

	public String[] getRecipe() {
		return new String[]{"The keycard reader requires: 8 stone, 1 hopper", "XXX", "XYX", "XXX", "X = stone, Y = hopper"};
	}

}
