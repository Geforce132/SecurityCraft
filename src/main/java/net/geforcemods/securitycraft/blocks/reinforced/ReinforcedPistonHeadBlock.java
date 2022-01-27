package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
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
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (PlayerEntity) placer));

		super.setPlacedBy(world, pos, state, placer, stack);
	}

	@Override
	public void playerWillDestroy(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (!world.isClientSide && player.abilities.instabuild) {
			BlockPos behindPos = pos.relative(state.getValue(FACING).getOpposite());
			Block behindBlock = world.getBlockState(behindPos).getBlock();

			if (behindBlock == SCContent.REINFORCED_PISTON.get() || behindBlock == SCContent.REINFORCED_STICKY_PISTON.get())
				world.removeBlock(behindPos, false);
		}

		super.playerWillDestroy(world, pos, state, player);
	}

	@Override
	public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			super.onRemove(state, worldIn, pos, newState, isMoving);

			Direction behind = state.getValue(FACING).getOpposite();
			pos = pos.relative(behind);
			BlockState behindState = worldIn.getBlockState(pos);

			if (behindState.getBlock() == SCContent.REINFORCED_PISTON.get() || behindState.getBlock() == SCContent.REINFORCED_STICKY_PISTON.get() && behindState.getValue(PistonBlock.EXTENDED)) {
				dropResources(behindState, worldIn, pos);
				worldIn.removeBlock(pos, false);
			}
		}
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
		Block behindState = world.getBlockState(pos.relative(state.getValue(FACING).getOpposite())).getBlock();

		return behindState == SCContent.REINFORCED_PISTON.get() || behindState == SCContent.REINFORCED_STICKY_PISTON.get() || behindState == SCContent.REINFORCED_MOVING_PISTON.get();
	}

	@Override
	public ItemStack getCloneItemStack(IBlockReader world, BlockPos pos, BlockState state) {
		return new ItemStack(state.getValue(TYPE) == PistonType.STICKY ? SCContent.REINFORCED_STICKY_PISTON.get() : SCContent.REINFORCED_PISTON.get());
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new OwnableBlockEntity();
	}

	@Override
	public Block getVanillaBlock() {
		return Blocks.PISTON_HEAD;
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState) {
		return defaultBlockState().setValue(FACING, vanillaState.getValue(FACING)).setValue(TYPE, vanillaState.getValue(TYPE)).setValue(SHORT, vanillaState.getValue(SHORT));
	}
}
