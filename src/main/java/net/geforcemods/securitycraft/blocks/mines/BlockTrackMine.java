package net.geforcemods.securitycraft.blocks.mines;

import java.util.Collections;
import java.util.List;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.tileentity.TileEntityTrackMine;
import net.geforcemods.securitycraft.util.IBlockWithNoDrops;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.BlockRail;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTrackMine extends BlockRail implements IExplosive, ITileEntityProvider, IBlockWithNoDrops {

	public BlockTrackMine() {
		super();
		setSoundType(SoundType.METAL);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(!world.isRemote){
			if(PlayerUtils.isHoldingItem(player, SCContent.remoteAccessMine))
				return false;

			if(isActive(world, pos) && isDefusable() && PlayerUtils.isHoldingItem(player, SCContent.wireCutters)) {
				defuseMine(world, pos);
				player.inventory.getCurrentItem().damageItem(1, player);
				return false;
			}

			if(!isActive(world, pos) && PlayerUtils.isHoldingItem(player, Items.FLINT_AND_STEEL)) {
				activateMine(world, pos);
				return false;
			}
		}

		return false;
	}

	@Override
	public void onMinecartPass(World world, EntityMinecart cart, BlockPos pos){
		TileEntity te = world.getTileEntity(pos);

		if(te instanceof TileEntityTrackMine && ((TileEntityTrackMine)te).isActive())
		{
			world.destroyBlock(pos, false);
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
			world.destroyBlock(pos, false);
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

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
	{
		return Collections.EMPTY_LIST;
	}
}
