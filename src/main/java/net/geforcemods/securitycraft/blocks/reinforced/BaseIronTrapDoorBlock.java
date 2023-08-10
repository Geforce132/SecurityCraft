package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.Half;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class BaseIronTrapDoorBlock extends TrapDoorBlock {
	public BaseIronTrapDoorBlock(AbstractBlock.Properties properties) {
		super(properties);
	}

	@Override
	public void setPlacedBy(World level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(level, pos, (PlayerEntity) placer));
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		BlockState state = defaultBlockState();
		FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
		Direction direction = ctx.getClickedFace();

		if (!ctx.replacingClickedOnBlock() && direction.getAxis().isHorizontal())
			state = state.setValue(FACING, direction).setValue(HALF, ctx.getClickLocation().y - ctx.getClickedPos().getY() > 0.5D ? Half.TOP : Half.BOTTOM);
		else
			state = state.setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(HALF, direction == Direction.UP ? Half.BOTTOM : Half.TOP);

		if (BlockUtils.hasActiveSCBlockNextTo(ctx.getLevel(), ctx.getClickedPos()))
			state = state.setValue(OPEN, true).setValue(POWERED, true);

		return state.setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
	}

	@Override
	public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
		super.onRemove(state, level, pos, newState, isMoving);

		if (!(newState.getBlock() instanceof BaseIronTrapDoorBlock))
			level.removeBlockEntity(pos);
	}

	@Override
	public boolean triggerEvent(BlockState state, World level, BlockPos pos, int id, int param) {
		TileEntity be = level.getBlockEntity(pos);

		return be != null && be.triggerEvent(id, param);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new OwnableBlockEntity();
	}
}