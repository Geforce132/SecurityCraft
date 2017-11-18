package net.geforcemods.securitycraft.blocks.mines;

import java.util.Random;

import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.tileentity.TileEntityClaymore;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockClaymore extends BlockContainer implements IExplosive {

	private final boolean isActive;

	public BlockClaymore(Material materialIn, boolean isActive) {
		super(materialIn);
		this.isActive = isActive;
	}

	@Override
	public boolean isOpaqueCube(){
		return false;
	}

	@Override
	public boolean isNormalCube(){
		return false;
	}

	@Override
	public int getRenderType(){
		return -1;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4){
		return null;
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, int par2, int par3, int par4){
		return worldIn.getBlock(par2, par3 - 1, par4).isSideSolid(worldIn, par2, par3 - 1, par4, ForgeDirection.UP);
	}

	@Override
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9){
		if(!par1World.isRemote)
			if(par5EntityPlayer.getCurrentEquippedItem() != null && par5EntityPlayer.getCurrentEquippedItem().getItem() == mod_SecurityCraft.wireCutters){
				par1World.setBlock(par2, par3, par4, mod_SecurityCraft.claymoreDefused, par1World.getBlockMetadata(par2, par3, par4), 3);
				return true;
			}else if(par5EntityPlayer.getCurrentEquippedItem() != null && par5EntityPlayer.getCurrentEquippedItem().getItem() == Items.flint_and_steel){
				par1World.setBlock(par2, par3, par4, mod_SecurityCraft.claymoreActive, par1World.getBlockMetadata(par2, par3, par4), 3);
				return true;
			}

		return false;
	}

	@Override
	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack){
		int l = MathHelper.floor_double(par5EntityLivingBase.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

		if(l == 0)
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 1, 2);

		if(l == 1)
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 4, 2);

		if(l == 2)
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 3, 2);

		if(l == 3)
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 2, 2);
		else
			return;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int x, int y, int z){
		int meta = par1IBlockAccess.getBlockMetadata(x, y, z);

		if(meta == 3)
			setBlockBounds(0.225F, 0.000F, 0.175F, 0.775F, 0.325F, 0.450F);
		else if(meta == 1)
			setBlockBounds(0.225F, 0.000F, 0.550F, 0.775F, 0.325F, 0.825F);
		else if(meta == 2)
			setBlockBounds(0.550F, 0.0F, 0.225F, 0.825F, 0.335F, 0.775F);
		else
			setBlockBounds(0.175F, 0.0F, 0.225F, 0.450F, 0.335F, 0.775F);

	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer par2EntityPlayer, int par3, int par4, int par5, boolean willHarvest){
		if (!world.isRemote && world.getBlock(par3, par4, par5) != mod_SecurityCraft.claymoreDefused){
			BlockUtils.destroyBlock(world, par3, par4, par5, false);
			world.createExplosion((Entity) null, (double) par3 + 0.5F, (double) par4 + 0.5F, (double) par5 + 0.5F, 3.5F, true);
		}

		return super.removedByPlayer(world, par2EntityPlayer, par3, par4, par5, willHarvest);
	}

	@Override
	public void onBlockDestroyedByExplosion(World worldIn, int par2, int par3, int par4, Explosion explosionIn){
		if (!worldIn.isRemote && worldIn.getBlock(par2, par3, par4) instanceof IExplosive && worldIn.getBlock(par2, par3, par4) == mod_SecurityCraft.claymoreActive)
		{
			BlockUtils.destroyBlock(worldIn, par2, par3, par4, false);
			worldIn.createExplosion((Entity) null, (double) par2 + 0.5F, (double) par3 + 0.5F, (double) par4 + 0.5F, 3.5F, true);
		}
	}

	@Override
	public void activateMine(World world, int par2, int par3, int par4) {
		if(!world.isRemote){
			Owner owner = ((IOwnable)world.getTileEntity(par2, par3, par4)).getOwner();
			world.setBlock(par2, par3, par4, mod_SecurityCraft.claymoreActive);
			((IOwnable)world.getTileEntity(par2, par3, par4)).setOwner(owner.getUUID(), owner.getName());
		}
	}

	@Override
	public void defuseMine(World world, int par2, int par3, int par4) {
		if(!world.isRemote){
			Owner owner = ((IOwnable)world.getTileEntity(par2, par3, par4)).getOwner();
			world.setBlock(par2, par3, par4, mod_SecurityCraft.claymoreDefused);
			((IOwnable)world.getTileEntity(par2, par3, par4)).setOwner(owner.getUUID(), owner.getName());
		}
	}

	@Override
	public void explode(World world, int par2, int par3, int par4) {
		if(!world.isRemote){
			BlockUtils.destroyBlock(world, par2, par3, par4, false);
			world.createExplosion((Entity) null, par2, par3, par4, 3.5F, true);
		}
	}

	@Override
	public boolean isActive(World world, int par2, int par3, int par4) {
		return isActive;
	}

	@Override
	public boolean isDefusable() {
		return true;
	}

	@Override
	public Item getItemDropped(int par1, Random par2Random, int par3){
		return Item.getItemFromBlock(mod_SecurityCraft.claymoreActive);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityClaymore();
	}

}
