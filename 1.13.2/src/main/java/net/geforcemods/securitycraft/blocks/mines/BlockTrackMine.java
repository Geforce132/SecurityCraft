package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.ConfigHandler.ServerConfig;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.TileEntityTrackMine;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRail;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class BlockTrackMine extends BlockRail implements IExplosive, ITileEntityProvider {

	public BlockTrackMine() {
		super(Block.Properties.create(Material.IRON).hardnessAndResistance(0.7F, 6000000.0F).doesNotBlockMovement().sound(SoundType.METAL));
	}

	@Override
	public float getBlockHardness(IBlockState blockState, IBlockReader world, BlockPos pos)
	{
		return !ServerConfig.CONFIG.ableToBreakMines.get() ? -1F : super.getBlockHardness(blockState, world, pos);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		if(placer instanceof EntityPlayer)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (EntityPlayer)placer));
	}

	@Override
	public void onMinecartPass(IBlockState state, World world, BlockPos pos, EntityMinecart cart){
		TileEntity te = world.getTileEntity(pos);

		if(te instanceof TileEntityTrackMine && ((TileEntityTrackMine)te).isActive())
		{
			BlockUtils.destroyBlock(world, pos, false);
			world.createExplosion(cart, pos.getX(), pos.getY() + 1, pos.getZ(), ServerConfig.CONFIG.smallerMineExplosion.get() ? 4.0F : 8.0F, true);
			cart.remove();
		}
	}

	@Override
	public void onReplaced(IBlockState state, World world, BlockPos pos, IBlockState newState, boolean isMoving)
	{
		super.onReplaced(state, world, pos, newState, isMoving);
		world.removeTileEntity(pos);
	}

	@Override
	public void explode(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);

		if(te instanceof TileEntityTrackMine && ((TileEntityTrackMine)te).isActive())
		{
			BlockUtils.destroyBlock(world, pos, false);
			world.createExplosion((Entity) null, pos.getX(), pos.up().getY(), pos.getZ(), ServerConfig.CONFIG.smallerMineExplosion.get() ? 4.0F : 8.0F, true);
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
	public TileEntity createNewTileEntity(IBlockReader reader) {
		return new TileEntityTrackMine();
	}

}
