package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.TrackMineTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RailBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class TrackMineBlock extends RailBlock implements IExplosive {

	public TrackMineBlock() {
		super(Block.Properties.create(Material.IRON).hardnessAndResistance(0.7F, 6000000.0F).doesNotBlockMovement().sound(SoundType.METAL));
	}

	@Override
	public float getBlockHardness(BlockState blockState, IBlockReader world, BlockPos pos)
	{
		return !ConfigHandler.CONFIG.ableToBreakMines.get() ? -1F : super.getBlockHardness(blockState, world, pos);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (PlayerEntity)placer));
	}

	@Override
	public void onMinecartPass(BlockState state, World world, BlockPos pos, AbstractMinecartEntity cart){
		TileEntity te = world.getTileEntity(pos);

		if(te instanceof TrackMineTileEntity && ((TrackMineTileEntity)te).isActive())
		{
			world.destroyBlock(pos, false);
			world.createExplosion(cart, pos.getX(), pos.getY() + 1, pos.getZ(), ConfigHandler.CONFIG.smallerMineExplosion.get() ? 4.0F : 8.0F, ConfigHandler.CONFIG.shouldSpawnFire.get(), Mode.BREAK);
			cart.remove();
		}
	}

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		super.onReplaced(state, world, pos, newState, isMoving);
		world.removeTileEntity(pos);
	}

	@Override
	public void explode(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);

		if(te instanceof TrackMineTileEntity && ((TrackMineTileEntity)te).isActive())
		{
			world.destroyBlock(pos, false);
			world.createExplosion((Entity) null, pos.getX(), pos.up().getY(), pos.getZ(), ConfigHandler.CONFIG.smallerMineExplosion.get() ? 4.0F : 8.0F, ConfigHandler.CONFIG.shouldSpawnFire.get(), Mode.BREAK);
		}
	}

	@Override
	public void activateMine(World world, BlockPos pos)
	{
		TileEntity te = world.getTileEntity(pos);

		if(te instanceof TrackMineTileEntity && !((TrackMineTileEntity)te).isActive())
			((TrackMineTileEntity)te).activate();
	}

	@Override
	public void defuseMine(World world, BlockPos pos)
	{
		TileEntity te = world.getTileEntity(pos);

		if(te instanceof TrackMineTileEntity && ((TrackMineTileEntity)te).isActive())
			((TrackMineTileEntity)te).deactivate();
	}

	@Override
	public boolean isActive(World world, BlockPos pos)
	{
		TileEntity te = world.getTileEntity(pos);

		return te instanceof TrackMineTileEntity && ((TrackMineTileEntity)te).isActive() && ((TrackMineTileEntity)te).isActive();
	}

	@Override
	public boolean isDefusable() {
		return true;
	}

	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TrackMineTileEntity();
	}

}
