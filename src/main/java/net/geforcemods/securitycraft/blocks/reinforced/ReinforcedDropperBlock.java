package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.blockentities.ReinforcedDropperBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSourceImpl;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.VanillaInventoryCodeHooks;

public class ReinforcedDropperBlock extends ReinforcedDispenserBlock {
	private final IBehaviorDispenseItem dropBehavior = new BehaviorDefaultDispenseItem();

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new ReinforcedDropperBlockEntity();
	}

	@Override
	protected void dispense(World level, BlockPos pos) {
		if (level.getTileEntity(pos) instanceof ReinforcedDropperBlockEntity) {
			BlockSourceImpl source = new BlockSourceImpl(level, pos);
			ReinforcedDropperBlockEntity be = source.getBlockTileEntity();
			int randomSlot = be.getDispenseSlot();

			if (randomSlot < 0)
				level.playEvent(Constants.WorldEvents.DISPENSER_FAIL_SOUND, pos, 0);
			else {
				ItemStack dispenseStack = be.getStackInSlot(randomSlot);

				if (!dispenseStack.isEmpty() && VanillaInventoryCodeHooks.dropperInsertHook(level, pos, be, randomSlot, dispenseStack)) {
					EnumFacing direction = level.getBlockState(pos).getValue(FACING);
					BlockPos offsetPos = pos.offset(direction);
					IInventory inventory = TileEntityHopper.getInventoryAtPosition(level, offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());
					ItemStack afterDispenseStack;

					if (inventory == null)
						afterDispenseStack = dropBehavior.dispense(source, dispenseStack);
					else {
						afterDispenseStack = TileEntityHopper.putStackInInventoryAllSlots(be, inventory, dispenseStack.copy().splitStack(1), direction.getOpposite());

						if (afterDispenseStack.isEmpty()) {
							afterDispenseStack = dispenseStack.copy();
							afterDispenseStack.shrink(1);
						}
						else
							afterDispenseStack = dispenseStack.copy();
					}

					be.setInventorySlotContents(randomSlot, afterDispenseStack);
				}
			}
		}
	}

	@Override
	public List<Block> getVanillaBlocks() {
		return Arrays.asList(Blocks.DROPPER);
	}
}
