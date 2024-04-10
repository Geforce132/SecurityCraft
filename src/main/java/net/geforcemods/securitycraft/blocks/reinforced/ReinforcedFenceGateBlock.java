package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.blockentities.AllowlistOnlyBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedFenceGateBlockEntity;
import net.geforcemods.securitycraft.blocks.OwnableFenceGateBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class ReinforcedFenceGateBlock extends OwnableFenceGateBlock implements IReinforcedBlock {
	private final Block vanillaBlock;

	public ReinforcedFenceGateBlock(AbstractBlock.Properties properties, Block vanillaBlock) {
		super(properties, SoundEvents.FENCE_GATE_OPEN, SoundEvents.FENCE_GATE_CLOSE);
		this.vanillaBlock = vanillaBlock;
	}

	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		TileEntity te = level.getBlockEntity(pos);

		if (te instanceof AllowlistOnlyBlockEntity) {
			AllowlistOnlyBlockEntity be = (AllowlistOnlyBlockEntity) te;

			//only allow the owner or players on the allowlist to access a reinforced fence gate
			if (be.isOwnedBy(player) || be.isAllowed(player)) {
				if (state.getValue(OPEN)) {
					state = state.setValue(OPEN, false);
					level.setBlock(pos, state, 10);
				}
				else {
					Direction direction = player.getDirection();

					if (state.getValue(FACING) == direction.getOpposite())
						state = state.setValue(FACING, direction);

					state = state.setValue(OPEN, true);
					level.setBlock(pos, state, 10);
				}

				boolean isOpen = state.getValue(OPEN);

				level.playSound(null, pos, isOpen ? openSound : closeSound, SoundCategory.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
			}
		}

		return ActionResultType.sidedSuccess(level.isClientSide);
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
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new ReinforcedFenceGateBlockEntity();
	}

	@Override
	public Block getVanillaBlock() {
		return vanillaBlock;
	}
}
