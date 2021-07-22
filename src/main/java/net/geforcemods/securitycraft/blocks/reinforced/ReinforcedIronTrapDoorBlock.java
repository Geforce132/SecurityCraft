package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.Half;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ReinforcedIronTrapDoorBlock extends TrapDoorBlock implements IReinforcedBlock {

	public ReinforcedIronTrapDoorBlock(Block.Properties properties) {
		super(properties);
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos neighbor, boolean flag)
	{
		boolean hasActiveSCBlock = BlockUtils.hasActiveSCBlockNextTo(world, pos);

		if(hasActiveSCBlock != state.getValue(OPEN))
		{
			world.setBlock(pos, state.setValue(OPEN, hasActiveSCBlock), 2);
			playSound((PlayerEntity)null, world, pos, hasActiveSCBlock);
		}
	}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (PlayerEntity)placer));
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		BlockState blockstate = this.defaultBlockState();
		FluidState fluidstate = ctx.getLevel().getFluidState(ctx.getClickedPos());
		Direction direction = ctx.getClickedFace();

		if (!ctx.replacingClickedOnBlock() && direction.getAxis().isHorizontal()) {
			blockstate = blockstate.setValue(FACING, direction).setValue(HALF, ctx.getClickLocation().y - ctx.getClickedPos().getY() > 0.5D ? Half.TOP : Half.BOTTOM);
		} else {
			blockstate = blockstate.setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(HALF, direction == Direction.UP ? Half.BOTTOM : Half.TOP);
		}

		if (BlockUtils.hasActiveSCBlockNextTo(ctx.getLevel(), ctx.getClickedPos())) {
			blockstate = blockstate.setValue(OPEN, true).setValue(POWERED, true);
		}

		return blockstate.setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
	{
		return ActionResultType.FAIL;
	}

	@Override
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		super.onRemove(state, world, pos, newState, isMoving);

		if(!(newState.getBlock() instanceof ReinforcedIronTrapDoorBlock))
			world.removeBlockEntity(pos);
	}

	@Override
	public boolean triggerEvent(BlockState state, World world, BlockPos pos, int id, int param)
	{
		super.triggerEvent(state, world, pos, id, param);
		TileEntity tileentity = world.getBlockEntity(pos);
		return tileentity == null ? false : tileentity.triggerEvent(id, param);
	}

	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new OwnableTileEntity();
	}

	@Override
	public Block getVanillaBlock()
	{
		return Blocks.IRON_TRAPDOOR;
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState)
	{
		return defaultBlockState().setValue(FACING, vanillaState.getValue(FACING)).setValue(OPEN, false).setValue(HALF, vanillaState.getValue(HALF)).setValue(POWERED, false).setValue(WATERLOGGED, vanillaState.getValue(WATERLOGGED));
	}
}