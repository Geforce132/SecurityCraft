package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.blockentities.ReinforcedDispenserBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.NeoForge;

public class ReinforcedDispenserBlock extends DispenserBlock implements IReinforcedBlock {
	public ReinforcedDispenserBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof Player player)
			NeoForge.EVENT_BUS.post(new OwnershipEvent(level, pos, player));

		super.setPlacedBy(level, pos, state, placer, stack);
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		//only allow the owner or players on the allowlist to access a reinforced dispenser
		if (!level.isClientSide && level.getBlockEntity(pos) instanceof ReinforcedDispenserBlockEntity be && (be.isOwnedBy(player) || be.isAllowed(player)))
			player.openMenu(be);

		return InteractionResult.SUCCESS;
	}

	@Override
	protected void dispenseFrom(ServerLevel level, BlockState state, BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof ReinforcedDispenserBlockEntity be) {
			BlockSource source = new BlockSource(level, pos, state, be);
			int randomSlot = be.getRandomSlot(level.random);

			if (randomSlot < 0) {
				level.levelEvent(LevelEvent.SOUND_DISPENSER_FAIL, pos, 0);
				level.gameEvent(GameEvent.BLOCK_ACTIVATE, pos, GameEvent.Context.of(be.getBlockState()));
			}
			else {
				ItemStack dispenseStack = be.getItem(randomSlot);
				DispenseItemBehavior dispenseBehavior = getDispenseMethod(dispenseStack);

				if (dispenseBehavior != DispenseItemBehavior.NOOP)
					be.setItem(randomSlot, dispenseBehavior.dispense(source, dispenseStack));
			}
		}
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof ReinforcedDispenserBlockEntity be) {
			if (isMoving)
				be.clearContent(); //Clear the items from the block before it is moved by a piston to prevent duplication

			level.updateNeighbourForOutputSignal(pos, this);
		}

		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ReinforcedDispenserBlockEntity(pos, state);
	}

	@Override
	public Block getVanillaBlock() {
		return Blocks.DISPENSER;
	}
}
