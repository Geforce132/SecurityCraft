package net.geforcemods.securitycraft.blocks.mines;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.blocks.BlockOwnable;
import net.geforcemods.securitycraft.compat.waila.ICustomWailaDisplay;
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
	private IIcon topIcon;
	@SideOnly(Side.CLIENT)
	private IIcon frontIcon;

	public BlockFurnaceMine(Material material) {
		super(material);
	}

	/**
	 * Called upon the block being destroyed by an explosion
	 */
	@Override
	public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion explosion)
	{
		if (!world.isRemote)
		{
			if(x == explosion.explosionX && y == explosion.explosionY && z == explosion.explosionZ)
				return;

			explode(world, x, y, z);
		}
	}

	@Override
	public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int meta){
		if (!world.isRemote)
			explode(world, x, y, z);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ){
		if(world.isRemote)
			return true;
		else if(player.getCurrentEquippedItem() == null || player.getCurrentEquippedItem().getItem() != SCContent.remoteAccessMine){
			explode(world, x, y, z);
			return true;
		}
		else
			return false;
	}

	@Override
	public void activateMine(World world, int x, int y, int z) {}

	@Override
	public void defuseMine(World world, int x, int y, int z) {}

	@Override
	public void explode(World world, int x, int y, int z) {
		world.breakBlock(x, y, z, false);

		if(SecurityCraft.config.smallerMineExplosion)
			world.createExplosion((Entity)null, x, y, z, 2.5F, true);
		else
			world.createExplosion((Entity)null, x, y, z, 5.0F, true);

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
	public IIcon getIcon(int side, int meta){
		if(side == 3 && meta == 0)
			return frontIcon;

		return side == 1 ? topIcon : (side == 0 ? topIcon : (side != meta ? blockIcon : frontIcon));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister p_149651_1_){
		blockIcon = p_149651_1_.registerIcon("furnace_side");
		frontIcon = p_149651_1_.registerIcon("furnace_front_off");
		topIcon = p_149651_1_.registerIcon("furnace_top");
	}

	@Override
	public boolean isActive(World world, int x, int y, int z) {
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
