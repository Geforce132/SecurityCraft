package net.geforcemods.securitycraft.blocks.mines;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
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

	public BlockClaymore(Material material, boolean isActive) {
		super(material);
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
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z){
		return null;
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, int x, int y, int z){
		return worldIn.getBlock(x, y - 1, z).isSideSolid(worldIn, x, y - 1, z, ForgeDirection.UP);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ){
		if(!world.isRemote)
			if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == SCContent.wireCutters){
				world.setBlock(x, y, z, SCContent.claymoreDefused, world.getBlockMetadata(x, y, z), 3);
				return true;
			}else if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.flint_and_steel){
				world.setBlock(x, y, z, SCContent.claymoreActive, world.getBlockMetadata(x, y, z), 3);
				return true;
			}

		return false;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack){
		int entityRotation = MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

		if(entityRotation == 0)
			world.setBlockMetadataWithNotify(x, y, z, 1, 2);

		if(entityRotation == 1)
			world.setBlockMetadataWithNotify(x, y, z, 4, 2);

		if(entityRotation == 2)
			world.setBlockMetadataWithNotify(x, y, z, 3, 2);

		if(entityRotation == 3)
			world.setBlockMetadataWithNotify(x, y, z, 2, 2);
		else
			return;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess access, int x, int y, int z){
		int meta = access.getBlockMetadata(x, y, z);

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
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest){
		if (!world.isRemote && world.getBlock(x, y, z) != SCContent.claymoreDefused){
			BlockUtils.destroyBlock(world, x, y, z, false);
			world.createExplosion((Entity) null, (double) x + 0.5F, (double) y + 0.5F, (double) z + 0.5F, 3.5F, true);
		}

		return super.removedByPlayer(world, player, x, y, z, willHarvest);
	}

	@Override
	public void onBlockDestroyedByExplosion(World worldIn, int x, int y, int z, Explosion explosion){
		if (!worldIn.isRemote && worldIn.getBlock(x, y, z) instanceof IExplosive && worldIn.getBlock(x, y, z) == SCContent.claymoreActive)
		{
			if(x == explosion.explosionX && y == explosion.explosionY && z == explosion.explosionZ)
				return;

			BlockUtils.destroyBlock(worldIn, x, y, z, false);
			worldIn.createExplosion((Entity) null, (double) x + 0.5F, (double) y + 0.5F, (double) z + 0.5F, 3.5F, true);
		}
	}

	@Override
	public void activateMine(World world, int x, int y, int z) {
		if(!world.isRemote){
			Owner owner = ((IOwnable)world.getTileEntity(x, y, z)).getOwner();
			world.setBlock(x, y, z, SCContent.claymoreActive);
			((IOwnable)world.getTileEntity(x, y, z)).setOwner(owner.getUUID(), owner.getName());
		}
	}

	@Override
	public void defuseMine(World world, int x, int y, int z) {
		if(!world.isRemote){
			Owner owner = ((IOwnable)world.getTileEntity(x, y, z)).getOwner();
			world.setBlock(x, y, z, SCContent.claymoreDefused);
			((IOwnable)world.getTileEntity(x, y, z)).setOwner(owner.getUUID(), owner.getName());
		}
	}

	@Override
	public void explode(World world, int x, int y, int z) {
		if(!world.isRemote){
			BlockUtils.destroyBlock(world, x, y, z, false);
			world.createExplosion((Entity) null, x, y, z, 3.5F, true);
		}
	}

	@Override
	public boolean isActive(World world, int x, int y, int z) {
		return isActive;
	}

	@Override
	public boolean isDefusable() {
		return true;
	}

	@Override
	public Item getItemDropped(int meta, Random random, int fortune){
		return Item.getItemFromBlock(SCContent.claymoreActive);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityClaymore();
	}

}
