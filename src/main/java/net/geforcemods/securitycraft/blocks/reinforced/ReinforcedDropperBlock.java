package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.blockentities.ReinforcedDropperBlockEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ContainerOrHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class ReinforcedDropperBlock extends ReinforcedDispenserBlock {
	private static final DispenseItemBehavior DISPENSE_BEHAVIOUR = new DefaultDispenseItemBehavior();

	public ReinforcedDropperBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
		return BlockUtils.getDestroyProgress(super::getDestroyProgress, state, player, level, pos);
	}

	@Override
	public DispenseItemBehavior getDispenseMethod(Level level, ItemStack stack) {
		return DISPENSE_BEHAVIOUR;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ReinforcedDropperBlockEntity(pos, state);
	}

	@Override
	protected void dispenseFrom(ServerLevel level, BlockState state, BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof ReinforcedDropperBlockEntity be) {
			BlockSource source = new BlockSource(level, pos, state, be);
			int randomSlot = be.getRandomSlot(level.random);

			if (randomSlot < 0)
				level.levelEvent(LevelEvent.SOUND_DISPENSER_FAIL, pos, 0);
			else {
				ItemStack dispenseStack = be.getItem(randomSlot);

				if (!dispenseStack.isEmpty()) {
					Direction direction = level.getBlockState(pos).getValue(FACING);
					ContainerOrHandler containerOrHandler = HopperBlockEntity.getContainerOrHandlerAt(level, pos.relative(direction), direction.getOpposite());
					ItemStack afterDispenseStack;

					if (containerOrHandler.isEmpty())
						afterDispenseStack = DISPENSE_BEHAVIOUR.dispense(source, dispenseStack);
					else {
						if (containerOrHandler.container() != null)
							afterDispenseStack = HopperBlockEntity.addItem(be, containerOrHandler.container(), dispenseStack.copyWithCount(1), direction.getOpposite());
						else
							afterDispenseStack = ItemHandlerHelper.insertItem(containerOrHandler.itemHandler(), dispenseStack.copyWithCount(1), false);

						if (afterDispenseStack.isEmpty()) {
							afterDispenseStack = dispenseStack.copy();
							afterDispenseStack.shrink(1);
						}
						else
							afterDispenseStack = dispenseStack.copy();
					}

					be.setItem(randomSlot, afterDispenseStack);
				}
			}
		}
	}

	@Override
	public Block getVanillaBlock() {
		return Blocks.DROPPER;
	}
}
