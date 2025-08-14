package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
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
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.NeoForge;

public class ReinforcedPaneBlock extends IronBarsBlock implements IReinforcedBlock, EntityBlock {
	private final Block vanillaBlock;
	private final float destroyTimeForOwner;

	public ReinforcedPaneBlock(BlockBehaviour.Properties properties, Block vB) {
		super(OwnableBlock.withReinforcedDestroyTime(properties));

		vanillaBlock = vB;
		destroyTimeForOwner = OwnableBlock.getStoredDestroyTime();
	}

	@Override
	public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
		return BlockUtils.getDestroyProgress(super::getDestroyProgress, destroyTimeForOwner, state, player, level, pos);
	}

	@Override
	public boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player) {
		return ConfigHandler.SERVER.alwaysDrop.get() || super.canHarvestBlock(state, level, pos, player);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return getStateForPlacement(context.getLevel(), context.getClickedPos());
	}

	public BlockState getStateForPlacement(BlockGetter level, BlockPos pos) {
		FluidState fluidState = level.getFluidState(pos);
		BlockPos northPos = pos.north();
		BlockPos southPos = pos.south();
		BlockPos westPos = pos.west();
		BlockPos eastPos = pos.east();
		BlockState northState = level.getBlockState(northPos);
		BlockState southState = level.getBlockState(southPos);
		BlockState westState = level.getBlockState(westPos);
		BlockState eastState = level.getBlockState(eastPos);
		return defaultBlockState().setValue(NORTH, attachsTo(northState, northState.isFaceSturdy(level, northPos, Direction.SOUTH))).setValue(SOUTH, attachsTo(southState, southState.isFaceSturdy(level, southPos, Direction.NORTH))).setValue(WEST, attachsTo(westState, westState.isFaceSturdy(level, westPos, Direction.EAST))).setValue(EAST, attachsTo(eastState, eastState.isFaceSturdy(level, eastPos, Direction.WEST))).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
	}

	@Override
	public Block getVanillaBlock() {
		return vanillaBlock;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof Player player)
			NeoForge.EVENT_BUS.post(new OwnershipEvent(level, pos, player));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new OwnableBlockEntity(pos, state);
	}
}