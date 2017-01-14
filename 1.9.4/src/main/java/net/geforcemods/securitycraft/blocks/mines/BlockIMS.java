package net.geforcemods.securitycraft.blocks.mines;

import java.util.Random;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blocks.BlockOwnable;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.tileentity.TileEntityIMS;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockIMS extends BlockOwnable {
	
	public static final PropertyInteger MINES = PropertyInteger.create("mines", 0, 4);

	public BlockIMS(Material par1) {
		super(par1);
		setSoundType(SoundType.METAL);
	}
	
	public boolean isOpaqueCube(){
        return false;
    }
    
    public boolean isNormalCube(){
        return false;
    } 
    
    public int getRenderType(){
    	return 3;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
    	return new AxisAlignedBB(0F, 0F, 0F, 1F, 0.45F, 1F);
    }
    
	public boolean onBlockActivated(World par1World, BlockPos pos, IBlockState state, EntityPlayer par5EntityPlayer, EnumFacing side, float par7, float par8, float par9){
		if(!par1World.isRemote){
			if(((IOwnable) par1World.getTileEntity(pos)).getOwner().isOwner(par5EntityPlayer)){
				par5EntityPlayer.openGui(mod_SecurityCraft.instance, GuiHandler.IMS_GUI_ID, par1World, pos.getX(), pos.getY(), pos.getZ());
				return true;
			}
		}
		
		return false;
	}
	
    public void updateTick(World par1World, BlockPos pos, IBlockState state, Random par5Random){
		if(!par1World.isRemote){
			BlockUtils.destroyBlock(par1World, pos, false);
		}                      
	}
	
	/**
     * A randomly called display update to be able to add particles or other items for display
     */
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World par1World, BlockPos pos, IBlockState state, Random par5Random){      
    	if(par1World.getTileEntity(pos) != null && par1World.getTileEntity(pos) instanceof TileEntityIMS && ((TileEntityIMS) par1World.getTileEntity(pos)).getBombsRemaining() == 0){
    		double d0 = pos.getX() + 0.5F + (par5Random.nextFloat() - 0.5F) * 0.2D;
    		double d1 = pos.getY() + 0.4F + (par5Random.nextFloat() - 0.5F) * 0.2D;
    		double d2 = pos.getZ() + 0.5F + (par5Random.nextFloat() - 0.5F) * 0.2D;
    		double d3 = 0.2199999988079071D;
    		double d4 = 0.27000001072883606D;

    		par1World.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 - d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
    		par1World.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D); 
    		par1World.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1 + d3, d2 - d4, 0.0D, 0.0D, 0.0D);
    		par1World.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1 + d3, d2 + d4, 0.0D, 0.0D, 0.0D);
    		par1World.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D);
    		
    		par1World.spawnParticle(EnumParticleTypes.FLAME, d0 - d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
    		par1World.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D); 
    	}
    }
    
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(MINES, 4);
    }
    
    @SideOnly(Side.CLIENT)
    public IBlockState getStateForEntityRender(IBlockState state)
    {
        return this.getDefaultState().withProperty(MINES, 4);
    }

    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(MINES, meta);
    }

    public int getMetaFromState(IBlockState state)
    {
    	return (((Integer) state.getValue(MINES)).intValue());
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {MINES});
    }
	
	public TileEntity createTileEntity(World var1, int var2) {
		return new TileEntityIMS();
	}

}
