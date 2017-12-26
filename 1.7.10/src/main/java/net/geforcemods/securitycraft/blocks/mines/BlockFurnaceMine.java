package net.geforcemods.securitycraft.blocks.mines;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.blocks.BlockOwnable;
import net.geforcemods.securitycraft.imc.waila.ICustomWailaDisplay;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlockFurnaceMine extends BlockOwnable implements IExplosive, ICustomWailaDisplay {

	@SideOnly(Side.CLIENT)
	private IIcon field_149935_N;
	@SideOnly(Side.CLIENT)
	private IIcon field_149936_O;

	public BlockFurnaceMine(Material par2Material) {
		super(par2Material);
	}

	/**
	 * Called upon the block being destroyed by an explosion
	 */
	@Override
	public void onBlockDestroyedByExplosion(World par1World, int par2, int par3, int par4, Explosion par5Explosion)
	{
		if (!par1World.isRemote)
			explode(par1World, par2, par3, par4);
	}

	@Override
	public void onBlockDestroyedByPlayer(World par1World, int par2, int par3, int par4, int par5){
		if (!par1World.isRemote)
			explode(par1World, par2, par3, par4);
	}

	@Override
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9){
		if(par1World.isRemote)
			return true;
		else if(par5EntityPlayer.getCurrentEquippedItem() == null || par5EntityPlayer.getCurrentEquippedItem().getItem() != mod_SecurityCraft.remoteAccessMine){
			explode(par1World, par2, par3, par4);
			return true;
		}
		else
			return false;
	}

	@Override
	public void activateMine(World world, int par2, int par3, int par4) {}

	@Override
	public void defuseMine(World world, int par2, int par3, int par4) {}

	@Override
	public void explode(World par1World, int par2, int par3, int par4) {
		par1World.breakBlock(par2, par3, par4, false);

		if(mod_SecurityCraft.configHandler.smallerMineExplosion)
			par1World.createExplosion((Entity)null, par2, par3, par4, 2.5F, true);
		else
			par1World.createExplosion((Entity)null, par2, par3, par4, 5.0F, true);

	}

	/**
	 * Return whether this block can drop from an explosion.
	 */
	@Override
	public boolean canDropFromExplosion(Explosion par1Explosion)
	{
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int par1, int par2){
		if(par1 == 3 && par2 == 0)
			return field_149936_O;

		return par1 == 1 ? field_149935_N : (par1 == 0 ? field_149935_N : (par1 != par2 ? blockIcon : field_149936_O));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister p_149651_1_){
		blockIcon = p_149651_1_.registerIcon("furnace_side");
		field_149936_O = p_149651_1_.registerIcon("furnace_front_off");
		field_149935_N = p_149651_1_.registerIcon("furnace_top");
	}

	@Override
	public boolean isActive(World world, int par2, int par3, int par4) {
		return true;
	}

	@Override
	public boolean isDefusable() {
		return false;
	}

	@Override
	public ItemStack getDisplayStack(World world, int x, int y, int z) {
		return new ItemStack(Blocks.furnace);
	}

	@Override
	public boolean shouldShowSCInfo(World world, int x, int y, int z) {
		return false;
	}

}
