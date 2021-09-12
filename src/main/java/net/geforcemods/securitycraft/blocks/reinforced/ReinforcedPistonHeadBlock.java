package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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

public class ReinforcedPistonHeadBlock extends PistonHeadBlock implements IReinforcedBlock {

	public ReinforcedPistonHeadBlock(Block.Properties properties) {
		super(properties);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if(placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (PlayerEntity)placer));

		super.onBlockPlacedBy(world, pos, state, placer, stack);
	}

	public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (!world.isRemote && player.abilities.isCreativeMode) {
			BlockPos behindPos = pos.offset(state.get(FACING).getOpposite());
			Block behindBlock = world.getBlockState(behindPos).getBlock();

			if (behindBlock == SCContent.REINFORCED_PISTON.get() || behindBlock == SCContent.REINFORCED_STICKY_PISTON.get()) {
				world.removeBlock(behindPos, false);
			}
		}

		super.onBlockHarvested(world, pos, state, player);
	}

	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			super.onReplaced(state, worldIn, pos, newState, isMoving);

			Direction behind = state.get(FACING).getOpposite();
			pos = pos.offset(behind);
			BlockState behindState = worldIn.getBlockState(pos);

			if (behindState.getBlock() == SCContent.REINFORCED_PISTON.get() || behindState.getBlock() == SCContent.REINFORCED_STICKY_PISTON.get() && behindState.get(PistonBlock.EXTENDED)) {
				spawnDrops(behindState, worldIn, pos);
				worldIn.removeBlock(pos, false);
			}
		}
	}

	@Override
	public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos) {
		Block behindState = world.getBlockState(pos.offset(state.get(FACING).getOpposite())).getBlock();

		return behindState == SCContent.REINFORCED_PISTON.get() || behindState == SCContent.REINFORCED_STICKY_PISTON.get() || behindState == SCContent.REINFORCED_MOVING_PISTON.get();
	}

	@Override
	public ItemStack getItem(IBlockReader world, BlockPos pos, BlockState state) {
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

	@Override
	public Block getVanillaBlock() {
		return Blocks.PISTON_HEAD;
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState) {
		return getDefaultState().with(FACING, vanillaState.get(FACING)).with(TYPE, vanillaState.get(TYPE)).with(SHORT, vanillaState.get(SHORT));
	}
}
