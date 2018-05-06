package net.geforcemods.securitycraft.blocks.mines;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blocks.BlockOwnable;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.tileentity.TileEntityIMS;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockIMS extends BlockOwnable {

	public BlockIMS(Material material) {
		super(material);
		setBlockBounds(0F, 0F, 0F, 1F, 0.45F, 1F);
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
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ){
		if(!world.isRemote)
			if(((IOwnable) world.getTileEntity(x, y, z)).getOwner().isOwner(player)){
				player.openGui(SecurityCraft.instance, GuiHandler.IMS_GUI_ID, world, x, y, z);
				return true;
			}

		return false;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random random){
		if(!world.isRemote)
			BlockUtils.destroyBlock(world, x, y, z, false);
	}

	/**
	 * A randomly called display update to be able to add particles or other items for display
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random random){
		if(world.getTileEntity(x, y, z) != null && ((TileEntityIMS) world.getTileEntity(x, y, z)).getBombsRemaining() == 0){
			double d0 = x + 0.5F + (random.nextFloat() - 0.5F) * 0.2D;
			double d1 = y + 0.4F + (random.nextFloat() - 0.5F) * 0.2D;
			double d2 = z + 0.5F + (random.nextFloat() - 0.5F) * 0.2D;
			double d3 = 0.2199999988079071D;
			double d4 = 0.27000001072883606D;

			world.spawnParticle("smoke", d0 - d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
			world.spawnParticle("smoke", d0 + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
			world.spawnParticle("smoke", d0, d1 + d3, d2 - d4, 0.0D, 0.0D, 0.0D);
			world.spawnParticle("smoke", d0, d1 + d3, d2 + d4, 0.0D, 0.0D, 0.0D);
			world.spawnParticle("smoke", d0, d1, d2, 0.0D, 0.0D, 0.0D);

			world.spawnParticle("flame", d0 - d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
			world.spawnParticle("flame", d0 + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityIMS();
	}

}
