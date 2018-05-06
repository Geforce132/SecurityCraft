package net.geforcemods.securitycraft.blocks.mines;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlockMine extends BlockExplosive {

	public boolean cut;

	public BlockMine(Material material, boolean cut) {
		super(material);
		float f = 0.2F;
		float g = 0.1F;
		this.cut = cut;
		setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, (g * 2.0F) / 2 + 0.1F, 0.5F + f);
	}

	@Override
	public boolean isOpaqueCube(){
		return false;
	}

	@Override
	public boolean renderAsNormalBlock(){
		return false;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block){
		if(world.getBlock(x, y - 1, z).getMaterial() != Material.air)
			return;
		else
			explode(world, x, y, z);
	}

	/**
	 * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
	 */
	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z){
		if(world.getBlock(x, y - 1, z).getMaterial() == Material.glass || world.getBlock(x, y - 1, z).getMaterial() == Material.cactus || world.getBlock(x, y - 1, z).getMaterial() == Material.air || world.getBlock(x, y - 1, z).getMaterial() == Material.cake || world.getBlock(x, y - 1, z).getMaterial() == Material.plants)
			return false;
		else
			return true;
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest){
		if(!world.isRemote)
			if(player != null && player.capabilities.isCreativeMode && !SecurityCraft.config.mineExplodesWhenInCreative)
				return super.removedByPlayer(world, player, x, y, z, willHarvest);
			else{
				explode(world, x, y, z);
				return super.removedByPlayer(world, player, x, y, z, willHarvest);
			}

		return super.removedByPlayer(world, player, x, y, z, willHarvest);
	}

	/**
	 * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
	 */
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity){
		if(world.isRemote)
			return;
		else if(entity instanceof EntityCreeper || entity instanceof EntityOcelot || entity instanceof EntityEnderman || entity instanceof EntityItem)
			return;
		else if(entity instanceof EntityLivingBase && !PlayerUtils.isPlayerMountedOnCamera((EntityLivingBase)entity))
			explode(world, x, y, z);
	}

	/**
	 * returns a new explosion. Does initiation (at time of writing Explosion is not finished)
	 */
	public Explosion newExplosion(Entity entity, double size, double x, double y, float z, boolean smoke, World world){
		Explosion explosion = new Explosion(world, entity, size, x, y, z);
		if(SecurityCraft.config.shouldSpawnFire)
			explosion.isFlaming = true;
		else
			explosion.isFlaming = false;
		explosion.isSmoking = smoke;
		explosion.doExplosionA();


		explosion.doExplosionB(true);
		return explosion;
	}

	@Override
	public Item getItemDropped(int meta, Random random, int fortune){
		return Item.getItemFromBlock(SCContent.mine);
	}

	@Override
	public Item getItem(World world, int x, int y, int z){
		return Item.getItemFromBlock(SCContent.mine);
	}

	@Override
	public void activateMine(World world, int x, int y, int z) {
		if(!world.isRemote){
			Owner owner = ((IOwnable)world.getTileEntity(x, y, z)).getOwner();
			world.setBlock(x, y, z, SCContent.mine);
			((IOwnable)world.getTileEntity(x, y, z)).setOwner(owner.getUUID(), owner.getName());
		}
	}

	@Override
	public void defuseMine(World world, int x, int y, int z) {
		if(!world.isRemote){
			Owner owner = ((IOwnable)world.getTileEntity(x, y, z)).getOwner();
			world.setBlock(x, y, z, SCContent.mineCut);
			((IOwnable)world.getTileEntity(x, y, z)).setOwner(owner.getUUID(), owner.getName());
		}
	}

	@Override
	public void explode(World world, int x, int y, int z) {
		if(!cut){
			world.breakBlock(x, y, z, false);
			if(SecurityCraft.config.smallerMineExplosion)
				newExplosion((Entity)null, x, y, z, 1.0F, true, world);
			else
				newExplosion((Entity)null, x, y, z, 3.0F, true, world);
		}
	}

	@Override
	public boolean isActive(World world, int x, int y, int z) {
		return !cut;
	}

	@Override
	public void registerIcons(IIconRegister register){
		if(cut)
			blockIcon = register.registerIcon("securitycraft:mineCut");
		else
			blockIcon = register.registerIcon("securitycraft:mine");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityOwnable();
	}

}
