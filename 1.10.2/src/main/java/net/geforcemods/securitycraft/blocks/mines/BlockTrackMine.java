package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
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
		BlockUtils.destroyBlock(world, pos, false);

		world.createExplosion(cart, pos.getX(), pos.getY() + 1, pos.getZ(), mod_SecurityCraft.configHandler.smallerMineExplosion ? 4.0F : 8.0F, true);

		cart.setDead();
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state){
		super.breakBlock(worldIn, pos, state);
		worldIn.removeTileEntity(pos);
	}

	@Override
	public void explode(World world, BlockPos pos) {
		BlockUtils.destroyBlock(world, pos, false);
		world.createExplosion((Entity) null, pos.getX(), pos.up().getY(), pos.getZ(), mod_SecurityCraft.configHandler.smallerMineExplosion ? 4.0F : 8.0F, true);
	}

	@Override
	public void activateMine(World world, BlockPos pos) {}

	@Override
	public void defuseMine(World world, BlockPos pos) {}

	@Override
	public boolean isActive(World world, BlockPos pos) {
		return true;
	}

	@Override
	public boolean isDefusable() {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityOwnable();
	}

}
