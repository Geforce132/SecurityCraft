package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.MinecraftForge;

public class ReinforcedPaneBlock extends IronBarsBlock implements IReinforcedBlock, EntityBlock
{
	private final Block vanillaBlock;

	public ReinforcedPaneBlock(Block.Properties properties, Block vB)
	{
		super(properties);

		vanillaBlock = vB;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return getStateForPlacement(context.getLevel(), context.getClickedPos());
	}

	public BlockState getStateForPlacement(BlockGetter world, BlockPos pos)
	{
		FluidState fluidState = world.getFluidState(pos);
		BlockPos northPos = pos.north();
		BlockPos southPos = pos.south();
		BlockPos westPos = pos.west();
		BlockPos eastPos = pos.east();
		BlockState northState = world.getBlockState(northPos);
		BlockState southState = world.getBlockState(southPos);
		BlockState westState = world.getBlockState(westPos);
		BlockState eastState = world.getBlockState(eastPos);
		return defaultBlockState().setValue(NORTH, attachsTo(northState, northState.isFaceSturdy(world, northPos, Direction.SOUTH))).setValue(SOUTH, attachsTo(southState, southState.isFaceSturdy(world, southPos, Direction.NORTH))).setValue(WEST, attachsTo(westState, westState.isFaceSturdy(world, westPos, Direction.EAST))).setValue(EAST, attachsTo(eastState, eastState.isFaceSturdy(world, eastPos, Direction.WEST))).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
	}

	@Override
	public Block getVanillaBlock()
	{
		return vanillaBlock;
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState)
	{
		return defaultBlockState().setValue(NORTH, vanillaState.getValue(IronBarsBlock.NORTH)).setValue(EAST, vanillaState.getValue(IronBarsBlock.EAST)).setValue(WEST, vanillaState.getValue(IronBarsBlock.WEST)).setValue(SOUTH, vanillaState.getValue(IronBarsBlock.SOUTH)).setValue(WATERLOGGED, vanillaState.getValue(IronBarsBlock.WATERLOGGED));
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof Player player)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, player));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new OwnableBlockEntity(pos, state);
	}
}