package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadFurnace;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockKeypadFurnace extends BlockOwnable {
	
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool OPEN = PropertyBool.create("open");
	
	public BlockKeypadFurnace(Material materialIn) {
		super(materialIn);
		setSoundType(SoundType.METAL);
	}
	
	/**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    @Override
	public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }
    
    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    @Override
	public boolean isNormalCube(IBlockState state)
    {
        return false;
    }
	
    @Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ){
		if(!worldIn.isRemote){     
    		((TileEntityKeypadFurnace) worldIn.getTileEntity(pos)).openPasswordGUI(playerIn);
    	}
        
        return true;
    }
	
	public static void activate(World par1World, BlockPos pos, EntityPlayer player){
		if(!BlockUtils.getBlockPropertyAsBoolean(par1World, pos, BlockKeypadFurnace.OPEN)){
			BlockUtils.setBlockProperty(par1World, pos, BlockKeypadFurnace.OPEN, true, false);
		}
		
		par1World.playEvent((EntityPlayer)null, 1006, pos, 0);
		player.openGui(mod_SecurityCraft.instance, GuiHandler.KEYPAD_FURNACE_GUI_ID, par1World, pos.getX(), pos.getY(), pos.getZ());
	}
	
	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(OPEN, false);
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
        if(meta <= 5){
        	return this.getDefaultState().withProperty(FACING, EnumFacing.values()[meta].getAxis() == EnumFacing.Axis.Y ? EnumFacing.NORTH : EnumFacing.values()[meta]).withProperty(OPEN, false);
        }else{
        	return this.getDefaultState().withProperty(FACING, EnumFacing.values()[meta - 6]).withProperty(OPEN, true);
        }
    }

    @Override
	public int getMetaFromState(IBlockState state)
    {
    	if(state.getValue(OPEN).booleanValue()){
    		return (state.getValue(FACING).getIndex() + 6);
    	}else{
    		return state.getValue(FACING).getIndex();
    	}
    }

    @Override
	protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {FACING, OPEN});
    }
	
	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityKeypadFurnace();
	}

}
