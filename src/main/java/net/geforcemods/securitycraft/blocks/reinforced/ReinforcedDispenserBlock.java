package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.blockentities.ReinforcedDispenserBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;

public class ReinforcedDispenserBlock extends DispenserBlock implements IReinforcedBlock {
	public ReinforcedDispenserBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void setPlacedBy(World level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(level, pos, (PlayerEntity) placer));

		super.setPlacedBy(level, pos, state, placer, stack);
	}

	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		//only allow the owner or players on the allowlist to access a reinforced dispenser
		if (!level.isClientSide) {
			TileEntity te = level.getBlockEntity(pos);

			if (te instanceof ReinforcedDispenserBlockEntity) {
				ReinforcedDispenserBlockEntity be = (ReinforcedDispenserBlockEntity) te;

				if (be.isOwnedBy(player) || be.isAllowed(player))
					player.openMenu(be);
			}
		}

		return ActionResultType.SUCCESS;
	}

	@Override
	protected void dispenseFrom(ServerWorld level, BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof ReinforcedDispenserBlockEntity)
			super.dispenseFrom(level, pos);
	}

	@Override
	public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			TileEntity te = level.getBlockEntity(pos);

			if (te instanceof ReinforcedDispenserBlockEntity) {
				if (isMoving)
					((ReinforcedDispenserBlockEntity) te).clearContent();

				level.updateNeighbourForOutputSignal(pos, this);
			}
		}

		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader level) {
		return new ReinforcedDispenserBlockEntity();
	}

	@Override
	public Block getVanillaBlock() {
		return Blocks.DISPENSER;
	}
}
