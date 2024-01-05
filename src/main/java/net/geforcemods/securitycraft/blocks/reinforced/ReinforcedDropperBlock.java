package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.blockentities.ReinforcedDropperBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSourceImpl;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.VanillaInventoryCodeHooks;

public class ReinforcedDropperBlock extends ReinforcedDispenserBlock {
	private static final DispenseItemBehavior DISPENSE_BEHAVIOUR = new DefaultDispenseItemBehavior();

	public ReinforcedDropperBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	public DispenseItemBehavior getDispenseMethod(ItemStack stack) {
		return DISPENSE_BEHAVIOUR;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ReinforcedDropperBlockEntity(pos, state);
	}

	@Override
	protected void dispenseFrom(ServerLevel level, BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof ReinforcedDropperBlockEntity be) {
			BlockSourceImpl source = new BlockSourceImpl(level, pos);
			int randomSlot = be.getRandomSlot(level.random);

			if (randomSlot < 0)
				level.levelEvent(LevelEvent.SOUND_DISPENSER_FAIL, pos, 0);
			else {
				ItemStack dispenseStack = be.getItem(randomSlot);

				if (!dispenseStack.isEmpty() && VanillaInventoryCodeHooks.dropperInsertHook(level, pos, be, randomSlot, dispenseStack)) {
					Direction direction = level.getBlockState(pos).getValue(FACING);
					Container container = HopperBlockEntity.getContainerAt(level, pos.relative(direction));
					ItemStack afterDispenseStack;

					if (container == null)
						afterDispenseStack = DISPENSE_BEHAVIOUR.dispense(source, dispenseStack);
					else {
						afterDispenseStack = HopperBlockEntity.addItem(be, container, dispenseStack.copy().split(1), direction.getOpposite());

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
