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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
    public boolean isOpaqueCube()
    {
        return false;
    }
    
    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean isNormalCube()
    {
        return false;
    }
	
	public boolean onBlockActivated(World par1World, BlockPos pos, IBlockState state, EntityPlayer par5EntityPlayer, EnumFacing side, float par7, float par8, float par9){
		if(!par1World.isRemote){     
    		((TileEntityKeypadFurnace) par1World.getTileEntity(pos)).openPasswordGUI(par5EntityPlayer);
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
	
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(OPEN, false);
    }
    
    @SideOnly(Side.CLIENT)
    public IBlockState getStateForEntityRender(IBlockState state)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
    }

    public IBlockState getStateFromMeta(int meta)
    {
        if(meta <= 5){
        	return this.getDefaultState().withProperty(FACING, EnumFacing.values()[meta].getAxis() == EnumFacing.Axis.Y ? EnumFacing.NORTH : EnumFacing.values()[meta]).withProperty(OPEN, false);
        }else{
        	return this.getDefaultState().withProperty(FACING, EnumFacing.values()[meta - 6]).withProperty(OPEN, true);
        }
    }

    public int getMetaFromState(IBlockState state)
    {
    	if(((Boolean) state.getValue(OPEN)).booleanValue()){
    		return (((EnumFacing) state.getValue(FACING)).getIndex() + 6);
    	}else{
    		return ((EnumFacing) state.getValue(FACING)).getIndex();
    	}
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {FACING, OPEN});
    }
	
	public TileEntity createTileEntity(World var1, int var2) {
		return new TileEntityKeypadFurnace();
	}

}
