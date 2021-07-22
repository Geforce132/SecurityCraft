package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PaneBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ReinforcedPaneBlock extends PaneBlock implements IReinforcedBlock
{
	private final Block vanillaBlock;

	public ReinforcedPaneBlock(Block.Properties properties, Block vB)
	{
		super(properties);

		vanillaBlock = vB;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return getStateForPlacement(context.getLevel(), context.getClickedPos());
	}

	public BlockState getStateForPlacement(IBlockReader world, BlockPos pos)
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
		return defaultBlockState().setValue(NORTH, vanillaState.getValue(PaneBlock.NORTH)).setValue(EAST, vanillaState.getValue(PaneBlock.EAST)).setValue(WEST, vanillaState.getValue(PaneBlock.WEST)).setValue(SOUTH, vanillaState.getValue(PaneBlock.SOUTH)).setValue(WATERLOGGED, vanillaState.getValue(PaneBlock.WATERLOGGED));
	}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (PlayerEntity)placer));
	}

	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return new OwnableTileEntity();
	}
}