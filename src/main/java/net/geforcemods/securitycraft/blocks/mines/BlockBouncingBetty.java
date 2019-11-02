package net.geforcemods.securitycraft.blocks.mines;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.entity.EntityBouncingBetty;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockBouncingBetty extends BlockExplosive implements IExplosive {

	@SideOnly(Side.CLIENT)
	private IIcon defusedIcon;

	@SideOnly(Side.CLIENT)
	private IIcon activeIcon;

	public BlockBouncingBetty(Material material) {
		super(material);
		setBlockBounds(0.200F, 0.000F, 0.200F, 0.800F, 0.200F, 0.800F);
	}

	@Override
	public boolean isOpaqueCube(){
		return false;
	}

	@Override
	public boolean renderAsNormalBlock(){
		return false;
	}

	/**
	 * Sets the block's bounds for rendering it as an item
	 */
	@Override
	public void setBlockBoundsForItemRender(){
		setBlockBounds(0.200F, 0.000F, 0.200F, 0.800F, 0.200F, 0.800F);
	}

	/**
	 * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
	 */
	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z){
		return world.isSideSolid(x, y - 1, z, ForgeDirection.UP);
	}

	/**
	 * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
	 */
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity){
		if(entity instanceof EntityLivingBase)
			if(!PlayerUtils.isPlayerMountedOnCamera((EntityLivingBase)entity))
				explode(world, x, y, z);
	}

	/**
	 * Called when the block is clicked by a player. Args: x, y, z, entityPlayer
	 */
	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player){
		explode(world, x, y, z);
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

	@Override
	public void activateMine(World world, int x, int y, int z) {
		world.setBlockMetadataWithNotify(x, y, z, 0, 3);
	}

	@Override
	public void defuseMine(World world, int x, int y, int z) {
		world.setBlockMetadataWithNotify(x, y, z, 1, 3);
	}

	@Override
	public void explode(World world, int x, int y, int z) {
		if(world.getBlockMetadata(x, y, z) == 1) return;

		world.setBlockToAir(x, y, z);
		EntityBouncingBetty bouncingBetty = new EntityBouncingBetty(world, x + 0.5F, y + 0.5F, z + 0.5F);
		bouncingBetty.fuse = 15;
		bouncingBetty.motionY = 0.50D;
		world.spawnEntityInWorld(bouncingBetty);
		world.playSoundAtEntity(bouncingBetty, "game.tnt.primed", 1.0F, 1.0F);
	}

	/**
	 * Returns the ID of the items to drop on destruction.
	 */
	@Override
	public Item getItemDropped(int meta, Random random, int fortune){
		return Item.getItemFromBlock(this);
	}

	/**
	 * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
	 */
	@Override
	public Item getItem(World world, int x, int y, int z){
		return Item.getItemFromBlock(this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int par1, int x) {
		return x == 0 ? activeIcon : defusedIcon;
	}

	@Override
	public void registerIcons(IIconRegister register){
		activeIcon = register.registerIcon("securitycraft:bouncingBettyActive");
		defusedIcon = register.registerIcon("securitycraft:bouncingBettyDefused");
	}

	@Override
	public boolean isActive(World world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z) == 0;
	}

	@Override
	public boolean isDefusable() {
		return true;
	}

}
