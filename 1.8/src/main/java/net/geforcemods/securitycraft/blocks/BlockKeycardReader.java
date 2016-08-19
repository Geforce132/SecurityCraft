package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.items.ItemKeycardBase;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntityKeycardReader;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ItemUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
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
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockKeycardReader extends BlockOwnable  {
	
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyBool POWERED = PropertyBool.create("powered");

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
		if(ModuleUtils.checkForModule(par1World, pos, par6EntityPlayer, EnumCustomModules.WHITELIST) || ModuleUtils.checkForModule(par1World, pos, par6EntityPlayer, EnumCustomModules.BLACKLIST)){ return; }
		
		int securityLevel = 0;
		
		if(((TileEntityKeycardReader)par1World.getTileEntity(pos)).getPassword() != null){
			securityLevel = Integer.parseInt(((TileEntityKeycardReader)par1World.getTileEntity(pos)).getPassword());
		}
		
		if((!((TileEntityKeycardReader)par1World.getTileEntity(pos)).doesRequireExactKeycard() && securityLevel <= ((ItemKeycardBase) par5ItemStack.getItem()).getKeycardLV(par5ItemStack) || ((TileEntityKeycardReader)par1World.getTileEntity(pos)).doesRequireExactKeycard() && securityLevel == ((ItemKeycardBase) par5ItemStack.getItem()).getKeycardLV(par5ItemStack))){
			if(((ItemKeycardBase) par5ItemStack.getItem()).getKeycardLV(par5ItemStack) == 6 && par5ItemStack.getTagCompound() != null && !par6EntityPlayer.capabilities.isCreativeMode){
				par5ItemStack.getTagCompound().setInteger("Uses", par5ItemStack.getTagCompound().getInteger("Uses") - 1);
				
				if(par5ItemStack.getTagCompound().getInteger("Uses") <= 0){
					par5ItemStack.stackSize--;
				}
			}
			
			BlockKeycardReader.activate(par1World, pos);
		}else{
			if(Integer.parseInt(((TileEntityKeycardReader)par1World.getTileEntity(pos)).getPassword()) != 0){
				PlayerUtils.sendMessageToPlayer(par6EntityPlayer, StatCollector.translateToLocal("tile.keycardReader.name"), StatCollector.translateToLocal("messages.keycardReader.required").replace("#r", ((IPasswordProtected) par1World.getTileEntity(pos)).getPassword()).replace("#c", "" + ((ItemKeycardBase) par5ItemStack.getItem()).getKeycardLV(par5ItemStack)), EnumChatFormatting.RED);
			}else{
				PlayerUtils.sendMessageToPlayer(par6EntityPlayer, StatCollector.translateToLocal("tile.keycardReader.name"), StatCollector.translateToLocal("messages.keycardReader.notSet"), EnumChatFormatting.RED);
			}
		}
		
	}
	
    public boolean onBlockActivated(World par1World, BlockPos pos, IBlockState state, EntityPlayer par5EntityPlayer, EnumFacing side, float par7, float par8, float par9){
    	if(par1World.isRemote){
    		return true;
    	}
    	
    	if(par5EntityPlayer.getCurrentEquippedItem() == null || (!(par5EntityPlayer.getCurrentEquippedItem().getItem() instanceof ItemKeycardBase) && par5EntityPlayer.getCurrentEquippedItem().getItem() != mod_SecurityCraft.adminTool)){
    		((TileEntityKeycardReader) par1World.getTileEntity(pos)).openPasswordGUI(par5EntityPlayer);
    	}
    	else if(par5EntityPlayer.getCurrentEquippedItem().getItem() == mod_SecurityCraft.adminTool) {
    		((BlockKeycardReader) BlockUtils.getBlock(par1World, pos)).insertCard(par1World, pos, ItemUtils.toItemStack(mod_SecurityCraft.limitedUseKeycard), par5EntityPlayer);
    	}
    	else {
    		((BlockKeycardReader) BlockUtils.getBlock(par1World, pos)).insertCard(par1World, pos, par5EntityPlayer.getCurrentEquippedItem(), par5EntityPlayer);
    	}
    	
		return false;
    }
    
    public static void activate(World par1World, BlockPos pos){
    	BlockUtils.setBlockProperty(par1World, pos, POWERED, true);
		par1World.notifyNeighborsOfStateChange(pos, mod_SecurityCraft.keycardReader);
		par1World.scheduleUpdate(pos, mod_SecurityCraft.keycardReader, 60);
	}
    
    public void updateTick(World par1World, BlockPos pos, IBlockState state, Random par5Random){
    	if(!par1World.isRemote){
    		BlockUtils.setBlockProperty(par1World, pos, POWERED, false);
			par1World.notifyNeighborsOfStateChange(pos, mod_SecurityCraft.keycardReader);
    	}
    }
    
    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World par1World, BlockPos pos, IBlockState state, Random par5Random){
    	if(((Boolean) state.getValue(POWERED))){
            double d0 = pos.getX() + 0.5F + (par5Random.nextFloat() - 0.5F) * 0.2D;
            double d1 = pos.getY() + 0.7F + (par5Random.nextFloat() - 0.5F) * 0.2D;
            double d2 = pos.getZ() + 0.5F + (par5Random.nextFloat() - 0.5F) * 0.2D;
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
    	if(((Boolean) state.getValue(POWERED))){
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
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(POWERED, false);
    }
    
    @SideOnly(Side.CLIENT)
    public IBlockState getStateForEntityRender(IBlockState state)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
    }

    public IBlockState getStateFromMeta(int meta)
    {
        if(meta <= 5){
        	return this.getDefaultState().withProperty(FACING, EnumFacing.values()[meta].getAxis() == EnumFacing.Axis.Y ? EnumFacing.NORTH : EnumFacing.values()[meta]).withProperty(POWERED, false);
        }else{
        	return this.getDefaultState().withProperty(FACING, EnumFacing.values()[meta - 6]).withProperty(POWERED, true);
        }
    }

    public int getMetaFromState(IBlockState state)
    {
    	if(((Boolean) state.getValue(POWERED)).booleanValue()){
    		return (((EnumFacing) state.getValue(FACING)).getIndex() + 6);
    	}else{
    		return ((EnumFacing) state.getValue(FACING)).getIndex();
    	}
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {FACING, POWERED});
    }
    
    public TileEntity createNewTileEntity(World world, int par2) {
		return new TileEntityKeycardReader();
	}

}
