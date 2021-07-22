package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.TrackMineTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RailBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class TrackMineBlock extends RailBlock implements IExplosive {

	public TrackMineBlock(Block.Properties properties) {
		super(properties);
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if(PlayerUtils.isHoldingItem(player, SCContent.REMOTE_ACCESS_MINE, hand))
			return ActionResultType.SUCCESS;

		if(isActive(world, pos) && isDefusable() && player.getItemInHand(hand).getItem() == SCContent.WIRE_CUTTERS.get()) {
			if(defuseMine(world, pos))
			{
				if(!player.isCreative())
					player.getItemInHand(hand).hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));

				world.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundCategory.BLOCKS, 1.0F, 1.0F);
				return ActionResultType.SUCCESS;
			}
		}

		if(!isActive(world, pos) && PlayerUtils.isHoldingItem(player, Items.FLINT_AND_STEEL, hand)) {
			if(activateMine(world, pos))
			{
				if(!player.isCreative())
					player.getItemInHand(hand).hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));

				world.playSound(null, pos, SoundEvents.TRIPWIRE_CLICK_ON, SoundCategory.BLOCKS, 1.0F, 1.0F);
				return ActionResultType.SUCCESS;
			}
		}

		return ActionResultType.PASS;
	}

	@Override
	public float getDestroyProgress(BlockState state, PlayerEntity player, IBlockReader world, BlockPos pos)
	{
		return !ConfigHandler.SERVER.ableToBreakMines.get() ? -1F : super.getDestroyProgress(state, player, world, pos);
	}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (PlayerEntity)placer));
	}

	@Override
	public void onMinecartPass(BlockState state, World world, BlockPos pos, AbstractMinecartEntity cart){
		TileEntity te = world.getBlockEntity(pos);

		if(te instanceof TrackMineTileEntity && ((TrackMineTileEntity)te).isActive())
		{
			world.destroyBlock(pos, false);
			world.explode(cart, pos.getX(), pos.getY() + 1, pos.getZ(), ConfigHandler.SERVER.smallerMineExplosion.get() ? 4.0F : 8.0F, ConfigHandler.SERVER.shouldSpawnFire.get(), BlockUtils.getExplosionMode());
			cart.remove();
		}
	}

	@Override
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		super.onRemove(state, world, pos, newState, isMoving);
		world.removeBlockEntity(pos);
	}

	@Override
	public void explode(World world, BlockPos pos) {
		TileEntity te = world.getBlockEntity(pos);

		if(te instanceof TrackMineTileEntity && ((TrackMineTileEntity)te).isActive())
		{
			world.destroyBlock(pos, false);
			world.explode((Entity) null, pos.getX(), pos.above().getY(), pos.getZ(), ConfigHandler.SERVER.smallerMineExplosion.get() ? 4.0F : 8.0F, ConfigHandler.SERVER.shouldSpawnFire.get(), BlockUtils.getExplosionMode());
		}
	}

	@Override
	public boolean activateMine(World world, BlockPos pos)
	{
		TileEntity te = world.getBlockEntity(pos);

		if(te instanceof TrackMineTileEntity && !((TrackMineTileEntity)te).isActive())
		{
			((TrackMineTileEntity)te).activate();
			return true;
		}
		else return false;
	}

	@Override
	public boolean defuseMine(World world, BlockPos pos)
	{
		TileEntity te = world.getBlockEntity(pos);

		if(te instanceof TrackMineTileEntity && ((TrackMineTileEntity)te).isActive())
		{
			((TrackMineTileEntity)te).deactivate();
			return true;
		}
		else return false;
	}

	@Override
	public boolean isActive(World world, BlockPos pos)
	{
		TileEntity te = world.getBlockEntity(pos);

		return te instanceof TrackMineTileEntity && ((TrackMineTileEntity)te).isActive();
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
