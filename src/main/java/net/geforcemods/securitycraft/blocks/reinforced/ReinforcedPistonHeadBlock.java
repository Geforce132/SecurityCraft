package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.PistonType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ReinforcedPistonHeadBlock extends PistonHeadBlock {

	public ReinforcedPistonHeadBlock(Block.Properties properties) {
		super(properties);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if(placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(worldIn, pos, (PlayerEntity)placer));

		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
	}

	/**
	 * Called before the Block is set to air in the world. Called regardless of if the player's tool can actually collect
	 * this block
	 */

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		if (!worldIn.isRemote && player.abilities.isCreativeMode) {
			BlockPos blockpos = pos.offset(state.get(FACING).getOpposite());
			Block block = worldIn.getBlockState(blockpos).getBlock();
			if (block == SCContent.REINFORCED_PISTON.get() || block == SCContent.REINFORCED_STICKY_PISTON.get()) {
				worldIn.removeBlock(blockpos, false);
			}
		}

		super.onBlockHarvested(worldIn, pos, state, player);
	}

	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			super.onReplaced(state, worldIn, pos, newState, isMoving);
			Direction direction = state.get(FACING).getOpposite();
			pos = pos.offset(direction);
			BlockState blockstate = worldIn.getBlockState(pos);
			if ((blockstate.getBlock() == SCContent.REINFORCED_PISTON.get() || blockstate.getBlock() == SCContent.REINFORCED_STICKY_PISTON.get()) && blockstate.get(PistonBlock.EXTENDED)) {
				spawnDrops(blockstate, worldIn, pos);
				worldIn.removeBlock(pos, false);
			}

		}
	}

	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		Block block = worldIn.getBlockState(pos.offset(state.get(FACING).getOpposite())).getBlock();
		return block == SCContent.REINFORCED_PISTON.get() || block == SCContent.REINFORCED_STICKY_PISTON.get() || block == SCContent.REINFORCED_MOVING_PISTON.get();
	}

	@Override
	public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
		return new ItemStack(state.get(TYPE) == PistonType.STICKY ? SCContent.REINFORCED_STICKY_PISTON.get() : SCContent.REINFORCED_PISTON.get());
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new OwnableTileEntity();
	}
}
