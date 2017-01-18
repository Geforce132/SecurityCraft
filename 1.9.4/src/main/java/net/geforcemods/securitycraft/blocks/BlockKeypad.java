package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.imc.waila.ICustomWailaDisplay;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypad;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockKeypad extends BlockContainer implements ICustomWailaDisplay {
	
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyBool POWERED = PropertyBool.create("powered");

	public BlockKeypad(Material par2Material) {
		super(par2Material);
		setSoundType(SoundType.STONE);
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }
	
	@SideOnly(Side.CLIENT)
	@Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        if(blockAccess.getBlockState(pos).getBlock() != Blocks.AIR && (blockAccess.getBlockState(pos).getBlock().isFullCube(blockAccess.getBlockState(pos)) || !blockAccess.getBlockState(pos).getBlock().isOpaqueCube(blockAccess.getBlockState(pos)))) {
        	return false;
        }

        return true;
    }
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}
    
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ){
    	if(worldIn.isRemote){
    		return true;
    	}
    	else {
			if(state.getValue(POWERED).booleanValue()){
				return false;
			}

			if(ModuleUtils.checkForModule(worldIn, pos, playerIn, EnumCustomModules.WHITELIST) || ModuleUtils.checkForModule(worldIn, pos, playerIn, EnumCustomModules.BLACKLIST)){
				activate(worldIn, pos);
				return true;
			}

			((IPasswordProtected) worldIn.getTileEntity(pos)).openPasswordGUI(playerIn);

			return true;       		 	    	     	
		}
    }
    
    public static void activate(World par1World, BlockPos pos){
    	BlockUtils.setBlockProperty(par1World, pos, POWERED, true);
		par1World.notifyNeighborsOfStateChange(pos, mod_SecurityCraft.keypad);
		par1World.scheduleUpdate(pos, mod_SecurityCraft.keypad, 60);
	}
    
    @Override
	public void updateTick(World par1World, BlockPos pos, IBlockState state, Random par5Random){
    	BlockUtils.setBlockProperty(par1World, pos, POWERED, false);
		par1World.notifyNeighborsOfStateChange(pos, mod_SecurityCraft.keypad);
    }
    
    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    @Override
	public void onBlockAdded(World par1World, BlockPos pos, IBlockState state)
    {
        this.setDefaultFacing(par1World, pos, state);
    }   
    
    private void setDefaultFacing(World par1World, BlockPos pos, IBlockState state) {
    	Block block = par1World.getBlockState(pos.north()).getBlock();
        Block block1 = par1World.getBlockState(pos.south()).getBlock();
        Block block2 = par1World.getBlockState(pos.west()).getBlock();
        Block block3 = par1World.getBlockState(pos.east()).getBlock();
        EnumFacing enumfacing = state.getValue(FACING);

        if (enumfacing == EnumFacing.NORTH && block.isFullBlock(state) && !block1.isFullBlock(state))
        {
            enumfacing = EnumFacing.SOUTH;
        }
        else if (enumfacing == EnumFacing.SOUTH && block1.isFullBlock(state) && !block.isFullBlock(state))
        {
            enumfacing = EnumFacing.NORTH;
        }
        else if (enumfacing == EnumFacing.WEST && block2.isFullBlock(state) && !block3.isFullBlock(state))
        {
            enumfacing = EnumFacing.EAST;
        }
        else if (enumfacing == EnumFacing.EAST && block3.isFullBlock(state) && !block2.isFullBlock(state))
        {
            enumfacing = EnumFacing.WEST;
        }

        par1World.setBlockState(pos, state.withProperty(FACING, enumfacing), 2);		
	}

    @Override
	public boolean canProvidePower(IBlockState state){
        return true;
    }
    
    /**
     * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
     * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
     * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    @Override
    public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side){
    	if(blockState.getValue(POWERED).booleanValue()){
    		return 15;
    	}else{
    		return 0;
    	}
    }
    
    /**
     * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
     * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    @Override
    public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side){  	
    	if(blockState.getValue(POWERED).booleanValue()){
    		return 15;
    	}else{
    		return 0;
    	}
    }
    
    @Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(POWERED, false);
    }
    
    /* TODO: no clue about this
    @SideOnly(Side.CLIENT)
    public IBlockState getStateForEntityRender(IBlockState state)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
    }*/

    @Override
	public IBlockState getStateFromMeta(int meta)
    {
		if(meta == 15) return this.getDefaultState();

        if(meta <= 5){
        	return this.getDefaultState().withProperty(FACING, EnumFacing.values()[meta].getAxis() == EnumFacing.Axis.Y ? EnumFacing.NORTH : EnumFacing.values()[meta]).withProperty(POWERED, false);
        }else{
        	return this.getDefaultState().withProperty(FACING, EnumFacing.values()[meta - 6]).withProperty(POWERED, true);
        }
    }

    @Override
	public int getMetaFromState(IBlockState state)
    {
    	if(state.getProperties().containsKey(POWERED) && state.getValue(POWERED).booleanValue()){
    		return (state.getValue(FACING).getIndex() + 6);
    	}else{
    		if(!state.getProperties().containsKey(FACING)) return 15;
    		
    		return state.getValue(FACING).getIndex();
    	}
    }
    
    @Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IBlockState disguisedState = getDisguisedBlockState(world, pos);
    	
        return disguisedState != null ? disguisedState : state;
    }
    
    public IBlockState getDisguisedBlockState(IBlockAccess world, BlockPos pos) {
    	if(world.getTileEntity(pos) instanceof TileEntityKeypad) {
        	TileEntityKeypad te = (TileEntityKeypad) world.getTileEntity(pos);
            
        	ItemStack module = te.hasModule(EnumCustomModules.DISGUISE) ? te.getModule(EnumCustomModules.DISGUISE) : null;
            
        	if(module != null && !((ItemModule) module.getItem()).getBlockAddons(module.getTagCompound()).isEmpty()) {
                ItemStack disguisedStack = ((ItemModule) module.getItem()).getAddons(module.getTagCompound()).get(0);
                Block block = Block.getBlockFromItem(disguisedStack.getItem());
                boolean hasMeta = disguisedStack.getHasSubtypes();
                
                IBlockState disguisedModel = block.getStateFromMeta(hasMeta ? disguisedStack.getItemDamage() : getMetaFromState(world.getBlockState(pos)));
                
                if (block != this) {
                    return block.getActualState(disguisedModel, world, pos);
                }
            }     	
        }
        
        return null;
    }
    
    public ItemStack getDisguisedStack(IBlockAccess world, BlockPos pos) {
    	if(world.getTileEntity(pos) instanceof TileEntityKeypad) {
        	TileEntityKeypad te = (TileEntityKeypad) world.getTileEntity(pos);
            
        	ItemStack stack = te.hasModule(EnumCustomModules.DISGUISE) ? te.getModule(EnumCustomModules.DISGUISE) : null;
            
        	if(stack != null && !((ItemModule) stack.getItem()).getBlockAddons(stack.getTagCompound()).isEmpty()) {
                ItemStack disguisedStack = ((ItemModule) stack.getItem()).getAddons(stack.getTagCompound()).get(0);
                
                if(Block.getBlockFromItem(disguisedStack.getItem()) != this) {
                    return disguisedStack;
                }
            }      	
        }
        
        return null;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
    	return null;
    }

    @Override
	protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {FACING, POWERED});
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    @Override
	public TileEntity createNewTileEntity(World par1World, int par2){
        return new TileEntityKeypad();
    }

	@Override
	public ItemStack getDisplayStack(World world, IBlockState state, BlockPos pos) {
		ItemStack stack = getDisguisedStack(world, pos);
		
		return stack != null ? stack : new ItemStack(this);	
	}

	@Override
	public boolean shouldShowSCInfo(World world, IBlockState state, BlockPos pos) {
		return !(getDisguisedStack(world, pos) != null);
	}

}
