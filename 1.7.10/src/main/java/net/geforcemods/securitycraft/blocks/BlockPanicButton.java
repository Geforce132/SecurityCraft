package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPanicButton extends BlockButton implements ITileEntityProvider {

	@SideOnly(Side.CLIENT)
	private IIcon buttonPowered;

	public BlockPanicButton() {
		super(false);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		if(world.isRemote)
			return true;
		else if(world.getBlockMetadata(x, y, z) > 4 && world.getBlockMetadata(x, y, z) < 10){
			world.setBlockMetadataWithNotify(x, y, z, world.getBlockMetadata(x, y, z) - 5, 3);
			world.markBlockForUpdate(x, y, z);
			world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.4D, "random.click", 0.3F, 0.5F);
			world.scheduleBlockUpdate(x, y, z, this, 1);
			notifyNeighbors(world, x, y, z);
			return true;
		}else{
			world.setBlockMetadataWithNotify(x, y, z, world.getBlockMetadata(x, y, z) + 5, 3);
			world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
			world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.click", 0.3F, 0.6F);
			world.scheduleBlockUpdate(x, y, z, this, 1);
			notifyNeighbors(world, x, y, z);
			return true;
		}
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random random){}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		if(world.isRemote)
			return;
		else
			notifyNeighbors(world, x, y, z);

		super.breakBlock(world, x, y, z, block, meta);
	}

	@Override
	public boolean onBlockEventReceived(World world, int x, int y, int z, int eventID, int eventData){
		super.onBlockEventReceived(world, x, y, z, eventID, eventData);
		TileEntity tileentity = world.getTileEntity(x, y, z);
		return tileentity == null ? false : tileentity.receiveClientEvent(eventID, eventData);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess access, int x, int y, int z)
	{
		updateBlockBounds(access.getBlockMetadata(x, y, z));
	}

	private void updateBlockBounds(int meta) {
		boolean isOn = (meta == 6 || meta == 7 || meta == 8 || meta == 9);
		float f2 = (isOn ? 1 : 2) / 16.0F;

		if(meta == 0)
			setBlockBounds(0.1800F, 0.300F, 0.95F, 0.8150F, 0.700F, 1.0F);
		else if (meta == 1)
			setBlockBounds(0.0F, 0.30F, 0.18F, f2, 0.70F, 0.82F);
		else if (meta == 2)
			setBlockBounds(1.0F - f2, 0.30F, 0.18F, 1.0F, 0.70F, 0.82F);
		else if (meta == 3)
			setBlockBounds(0.1800F, 0.300F, 0.0F, 0.8150F, 0.700F, f2);
		else if (meta == 4)
			setBlockBounds(0.1800F, 0.300F, 1.0F - f2, 0.8150F, 0.700F, 1.0F);
		else if (meta == 5)
			setBlockBounds(1.0F - f2, 0.30F, 0.18F, 1.0F, 0.70F, 0.82F);
		else if (meta == 6)
			setBlockBounds(0.0F, 0.30F, 0.18F, f2, 0.70F, 0.82F);
		else if (meta == 7)
			setBlockBounds(1.0F - f2, 0.30F, 0.18F, 1.0F, 0.70F, 0.82F);
		else if (meta == 8)
			setBlockBounds(0.1800F, 0.300F, 0.0F, 0.8150F, 0.700F, f2);
		else if (meta == 9)
			setBlockBounds(0.1800F, 0.300F, 1.0F - f2, 0.8150F, 0.700F, 1.0F);
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess access, int x, int y, int z, int par5)
	{
		return (access.getBlockMetadata(x, y, z) > 4 && access.getBlockMetadata(x, y, z) < 10) ? 15 : 0;
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess access, int x, int y, int z, int par5)
	{
		return (access.getBlockMetadata(x, y, z) > 4 && access.getBlockMetadata(x, y, z) < 10) ? 15 : 0;
	}

	@Override
	public void onNeighborBlockChange(World world, int p_149695_2_, int p_149695_3_, int p_149695_4_, Block p_149695_5_){}

	private void notifyNeighbors(World world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);

		if(meta == 1 || meta == 6)
			world.notifyBlocksOfNeighborChange(x - 1, y, z, this);
		else if(meta == 2 || meta == 7)
			world.notifyBlocksOfNeighborChange(x + 1, y, z, this);
		else if(meta == 3 || meta == 8)
			world.notifyBlocksOfNeighborChange(x, y, z - 1, this);
		else if(meta == 4 || meta == 9)
			world.notifyBlocksOfNeighborChange(x, y, z + 1, this);
		else{
			world.notifyBlocksOfNeighborChange(x - 1, y, z, this);
			world.notifyBlocksOfNeighborChange(x + 1, y, z, this);
			world.notifyBlocksOfNeighborChange(x, y, z - 1, this);
			world.notifyBlocksOfNeighborChange(x, y, z + 1, this);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		if(meta > 4 && meta < 10)
			return buttonPowered;
		else
			return blockIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		blockIcon = register.registerIcon("securitycraft:panicButton");
		buttonPowered = register.registerIcon("securitycraft:panicButtonPowered");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityOwnable();
	}

}
