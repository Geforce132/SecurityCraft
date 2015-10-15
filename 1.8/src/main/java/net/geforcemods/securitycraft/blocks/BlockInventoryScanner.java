package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.main.Utils.BlockUtils;
import net.geforcemods.securitycraft.main.Utils.ModuleUtils;
import net.geforcemods.securitycraft.main.Utils.PlayerUtils;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockInventoryScanner extends BlockContainer {

    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    
	public BlockInventoryScanner(Material par1Material) {
		super(par1Material);
	}
	
	public int getRenderType(){
		return 3;
	}
    
    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(World par1World, BlockPos pos, IBlockState state)
    {
        super.onBlockAdded(par1World, pos, state);
        this.setDefaultFacing(par1World, pos, state);
    }

    /**
     * set a blocks direction
     */
    private void setDefaultFacing(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!worldIn.isRemote)
        {
            Block block = worldIn.getBlockState(pos.north()).getBlock();
            Block block1 = worldIn.getBlockState(pos.south()).getBlock();
            Block block2 = worldIn.getBlockState(pos.west()).getBlock();
            Block block3 = worldIn.getBlockState(pos.east()).getBlock();
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

            worldIn.setBlockState(pos, state.withProperty(FACING, enumfacing), 2);
        }
    }
    
	public boolean onBlockActivated(World par1World, BlockPos pos, IBlockState state, EntityPlayer par5EntityPlayer, EnumFacing side, float par7, float par8, float par9){    	
    	if(par1World.isRemote){
    		return true;
    	}else{
    		if(this.isFacingAnotherBlock(par1World, pos)){
    			par5EntityPlayer.openGui(mod_SecurityCraft.instance, 9, par1World, pos.getX(), pos.getY(), pos.getZ());
    		}else{
    			PlayerUtils.sendMessageToPlayer(par5EntityPlayer, "Inventory Scanner", "This is not connected to another inventory scanner block!", EnumChatFormatting.RED);
    		}
    		
    		return true;
    	}
    }

    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World par1World, BlockPos pos, IBlockState state, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack){    	
        ((TileEntityOwnable) par1World.getTileEntity(pos)).setOwner(((EntityPlayer) par5EntityLivingBase).getGameProfile().getId().toString(), par5EntityLivingBase.getName());       
    	
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
        
        this.checkAndPlaceAppropriately(par1World, pos);
        
    }
    
    private void checkAndPlaceAppropriately(World par1World, BlockPos pos) {
		if((EnumFacing) par1World.getBlockState(pos).getValue(FACING) == EnumFacing.WEST && BlockUtils.getBlock(par1World, pos.west(2)) == this && BlockUtils.getBlock(par1World, pos.west()) == Blocks.air && (EnumFacing) par1World.getBlockState(pos.west(2)).getValue(FACING) == EnumFacing.EAST){
			par1World.setBlockState(pos.west(), mod_SecurityCraft.inventoryScannerField.getDefaultState().withProperty(FACING, EnumFacing.WEST));
		}
		else if((EnumFacing) par1World.getBlockState(pos).getValue(FACING) == EnumFacing.EAST && BlockUtils.getBlock(par1World, pos.east(2)) == this && BlockUtils.getBlock(par1World, pos.east()) == Blocks.air && (EnumFacing) par1World.getBlockState(pos.east(2)).getValue(FACING) == EnumFacing.WEST){
			par1World.setBlockState(pos.east(), mod_SecurityCraft.inventoryScannerField.getDefaultState().withProperty(FACING, EnumFacing.EAST));
		}
		else if((EnumFacing) par1World.getBlockState(pos).getValue(FACING) == EnumFacing.NORTH && BlockUtils.getBlock(par1World, pos.north(2)) == this && BlockUtils.getBlock(par1World, pos.north()) == Blocks.air && (EnumFacing) par1World.getBlockState(pos.north(2)).getValue(FACING) == EnumFacing.SOUTH){
			par1World.setBlockState(pos.north(), mod_SecurityCraft.inventoryScannerField.getDefaultState().withProperty(FACING, EnumFacing.NORTH));
		}
		else if((EnumFacing) par1World.getBlockState(pos).getValue(FACING) == EnumFacing.SOUTH && BlockUtils.getBlock(par1World, pos.south(2)) == this && BlockUtils.getBlock(par1World, pos.south()) == Blocks.air && (EnumFacing) par1World.getBlockState(pos.south(2)).getValue(FACING) == EnumFacing.NORTH){
			par1World.setBlockState(pos.south(), mod_SecurityCraft.inventoryScannerField.getDefaultState().withProperty(FACING, EnumFacing.SOUTH));
		}
	}
    
    private boolean isFacingAnotherBlock(World par1World, BlockPos pos){
    	if((EnumFacing) par1World.getBlockState(pos).getValue(FACING) == EnumFacing.WEST && BlockUtils.getBlock(par1World, pos.west(2)) == this && BlockUtils.getBlock(par1World, pos.west()) == mod_SecurityCraft.inventoryScannerField && (EnumFacing) par1World.getBlockState(pos.west(2)).getValue(FACING) == EnumFacing.EAST){
			return true;
		}
		else if((EnumFacing) par1World.getBlockState(pos).getValue(FACING) == EnumFacing.EAST && BlockUtils.getBlock(par1World, pos.east(2)) == this && BlockUtils.getBlock(par1World, pos.east()) == mod_SecurityCraft.inventoryScannerField && (EnumFacing) par1World.getBlockState(pos.east(2)).getValue(FACING) == EnumFacing.WEST){
			return true;

		}
		else if((EnumFacing) par1World.getBlockState(pos).getValue(FACING) == EnumFacing.NORTH && BlockUtils.getBlock(par1World, pos.north(2)) == this && BlockUtils.getBlock(par1World, pos.north()) == mod_SecurityCraft.inventoryScannerField && (EnumFacing) par1World.getBlockState(pos.north(2)).getValue(FACING) == EnumFacing.SOUTH){
			return true;

		}
		else if((EnumFacing) par1World.getBlockState(pos).getValue(FACING) == EnumFacing.SOUTH && BlockUtils.getBlock(par1World, pos.south(2)) == this && BlockUtils.getBlock(par1World, pos.south()) == mod_SecurityCraft.inventoryScannerField && (EnumFacing) par1World.getBlockState(pos.south(2)).getValue(FACING) == EnumFacing.NORTH){
			return true;

		}else{
			return false;
		}
    }
    
    public void breakBlock(World par1World, BlockPos pos, IBlockState state){	    	
    	for(int i = 0; i < ((TileEntityInventoryScanner) par1World.getTileEntity(pos)).getContents().length; i++){
    		if(((TileEntityInventoryScanner) par1World.getTileEntity(pos)).getContents()[i] != null){
    			EntityItem item = new EntityItem(par1World, (double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), ((TileEntityInventoryScanner) par1World.getTileEntity(pos)).getContents()[i]);
    			par1World.spawnEntityInWorld(item);
    		}
    	}
    	
    	if(state.getValue(FACING) == EnumFacing.NORTH && par1World.getBlockState(pos.south()).getBlock() == mod_SecurityCraft.inventoryScannerField){
    		BlockUtils.destroyBlock(par1World, pos.south(), false);
    	}else if(state.getValue(FACING) == EnumFacing.SOUTH && par1World.getBlockState(pos.north()).getBlock() == mod_SecurityCraft.inventoryScannerField){
    		BlockUtils.destroyBlock(par1World, pos.north(), false);
    	}else if(state.getValue(FACING) == EnumFacing.EAST && par1World.getBlockState(pos.west()).getBlock() == mod_SecurityCraft.inventoryScannerField){
    		BlockUtils.destroyBlock(par1World, pos.west(), false);
    	}else if(state.getValue(FACING) == EnumFacing.WEST && par1World.getBlockState(pos.east()).getBlock() == mod_SecurityCraft.inventoryScannerField){
    		BlockUtils.destroyBlock(par1World, pos.east(), false);
    	}
    	
    	if(state.getValue(FACING) == EnumFacing.NORTH && par1World.getBlockState(pos.south(2)).getBlock() == mod_SecurityCraft.inventoryScanner && par1World.getBlockState(pos.south(2)).getValue(FACING) == EnumFacing.SOUTH){
    		ModuleUtils.insertModule(par1World, pos.south(2), null);
    	}else if(state.getValue(FACING) == EnumFacing.SOUTH && par1World.getBlockState(pos.north(2)).getBlock() == mod_SecurityCraft.inventoryScanner && par1World.getBlockState(pos.north(2)).getValue(FACING) == EnumFacing.NORTH){
    		ModuleUtils.insertModule(par1World, pos.north(2), null);
    	}else if(state.getValue(FACING) == EnumFacing.EAST && par1World.getBlockState(pos.west(2)).getBlock() == mod_SecurityCraft.inventoryScanner && par1World.getBlockState(pos.west(2)).getValue(FACING) == EnumFacing.WEST){
    		ModuleUtils.insertModule(par1World, pos.west(2), null);
    	}else if(state.getValue(FACING) == EnumFacing.WEST && par1World.getBlockState(pos.east(2)).getBlock() == mod_SecurityCraft.inventoryScanner && par1World.getBlockState(pos.east(2)).getValue(FACING) == EnumFacing.EAST){
    		ModuleUtils.insertModule(par1World, pos.east(2), null);
    	}
    	
    	par1World.removeTileEntity(pos);	
    	
    	super.breakBlock(par1World, pos, state);
    }
    
    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower()
    {
        return true;
    }
    
    /**
     * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
     * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
     * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, BlockPos pos, IBlockState state, EnumFacing side)
    {
    	if(((TileEntityInventoryScanner) par1IBlockAccess.getTileEntity(pos)).getType() == null){
    		mod_SecurityCraft.log("type is null on the " + FMLCommonHandler.instance().getEffectiveSide() + " side");
    		return 0 ;
    	}
    	    	
    	return (((TileEntityInventoryScanner) par1IBlockAccess.getTileEntity(pos)).getType().matches("redstone") && ((TileEntityInventoryScanner) par1IBlockAccess.getTileEntity(pos)).shouldProvidePower())? 15 : 0;
    }
    
    /**
     * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
     * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingStrongPower(IBlockAccess par1IBlockAccess, BlockPos pos, IBlockState state, EnumFacing side)
    {
    	if(((TileEntityInventoryScanner) par1IBlockAccess.getTileEntity(pos)).getType() == null){
    		return 0 ;
    	}
    	
    	return (((TileEntityInventoryScanner) par1IBlockAccess.getTileEntity(pos)).getType().matches("redstone") && ((TileEntityInventoryScanner) par1IBlockAccess.getTileEntity(pos)).shouldProvidePower())? 15 : 0;
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
		return new TileEntityInventoryScanner();
	}

}
