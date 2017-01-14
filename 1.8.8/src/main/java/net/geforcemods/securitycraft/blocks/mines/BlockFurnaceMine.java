package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.blocks.BlockOwnable;
import net.geforcemods.securitycraft.imc.waila.ICustomWailaDisplay;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFurnaceMine extends BlockOwnable implements IExplosive, ICustomWailaDisplay {

    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

	public BlockFurnaceMine(Material par1Material) {
		super(par1Material);
	}

	/**
	 * Called upon the block being destroyed by an explosion
	 */
	public void onBlockDestroyedByExplosion(World par1World, BlockPos pos, Explosion par5Explosion)
	{
		if (!par1World.isRemote)
		{
			this.explode(par1World, pos);
		}
	}

	public void onBlockDestroyedByPlayer(World par1World, BlockPos pos, IBlockState state){
		if (!par1World.isRemote)
		{
			this.explode(par1World, pos);
		}
	}	

	public boolean onBlockActivated(World par1World, BlockPos pos, IBlockState state, EntityPlayer par5EntityPlayer, EnumFacing side, float par7, float par8, float par9){
		if(par1World.isRemote){
			return true;
		}else{
			if(par5EntityPlayer.getCurrentEquippedItem() == null || par5EntityPlayer.getCurrentEquippedItem().getItem() != mod_SecurityCraft.remoteAccessMine){
				this.explode(par1World, pos);
				return true;
			}else{
				return false;	   		
			}
		}
	}	
	
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }
	
	public void activateMine(World world, BlockPos pos) {}

	public void defuseMine(World world, BlockPos pos) {}
	
	public void explode(World par1World, BlockPos pos) {
		par1World.destroyBlock(pos, false);

		if(mod_SecurityCraft.configHandler.smallerMineExplosion){
			par1World.createExplosion((Entity)null, pos.getX(), pos.getY(), pos.getZ(), 2.5F, true);
		}else{
			par1World.createExplosion((Entity)null, pos.getX(), pos.getY(), pos.getZ(), 5.0F, true);
		}

	}

	/**
	 * Return whether this block can drop from an explosion.
	 */
	public boolean canDropFromExplosion(Explosion par1Explosion)
	{
		return false;
	}
	
	@SideOnly(Side.CLIENT)
    public IBlockState getStateForEntityRender(IBlockState state)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
    }

	public IBlockState getStateFromMeta(int meta){
        return this.getDefaultState().withProperty(FACING, EnumFacing.values()[meta].getAxis() == EnumFacing.Axis.Y ? EnumFacing.NORTH : EnumFacing.values()[meta]);
    }

    public int getMetaFromState(IBlockState state){
        return state.getValue(FACING).getIndex();
    }

    protected BlockState createBlockState(){
        return new BlockState(this, new IProperty[] {FACING});
    }
	
	public boolean isActive(World world, BlockPos pos) {
		return true;
	}
	
	public boolean isDefusable() {
		return false;
	}
	
	public ItemStack getDisplayStack(World world, IBlockState state, BlockPos pos) {
		return new ItemStack(Blocks.furnace);
	}

	public boolean shouldShowSCInfo(World world, IBlockState state, BlockPos pos) {
		return false;
	}

}
