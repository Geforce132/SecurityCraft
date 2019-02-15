package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.tileentity.TileEntityTrackMine;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.BlockRail;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockTrackMine extends BlockRail implements IExplosive, ITileEntityProvider {

	public BlockTrackMine() {
		super();
		setSoundType(SoundType.METAL);
	}

	@Override
	public void onMinecartPass(World world, EntityMinecart cart, BlockPos pos){
		TileEntity te = world.getTileEntity(pos);

		if(te instanceof TileEntityTrackMine && ((TileEntityTrackMine)te).isActive())
		{
			BlockUtils.destroyBlock(world, pos, false);
			world.createExplosion(cart, pos.getX(), pos.getY() + 1, pos.getZ(), ConfigHandler.smallerMineExplosion ? 4.0F : 8.0F, true);
			cart.setDead();
		}
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state){
		super.breakBlock(world, pos, state);
		world.removeTileEntity(pos);
	}

	@Override
	public void explode(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);

		if(te instanceof TileEntityTrackMine && ((TileEntityTrackMine)te).isActive())
		{
			BlockUtils.destroyBlock(world, pos, false);
			world.createExplosion((Entity) null, pos.getX(), pos.up().getY(), pos.getZ(), ConfigHandler.smallerMineExplosion ? 4.0F : 8.0F, true);
		}
	}

	@Override
	public void activateMine(World world, BlockPos pos)
	{
		TileEntity te = world.getTileEntity(pos);

		if(te instanceof TileEntityTrackMine && !((TileEntityTrackMine)te).isActive())
			((TileEntityTrackMine)te).activate();
	}

	@Override
	public void defuseMine(World world, BlockPos pos)
	{
		TileEntity te = world.getTileEntity(pos);

		if(te instanceof TileEntityTrackMine && ((TileEntityTrackMine)te).isActive())
			((TileEntityTrackMine)te).deactivate();
	}

	@Override
	public boolean isActive(World world, BlockPos pos)
	{
		TileEntity te = world.getTileEntity(pos);

		return te instanceof TileEntityTrackMine && ((TileEntityTrackMine)te).isActive() && ((TileEntityTrackMine)te).isActive();
	}

	@Override
	public boolean isDefusable() {
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityTrackMine();
	}

}
