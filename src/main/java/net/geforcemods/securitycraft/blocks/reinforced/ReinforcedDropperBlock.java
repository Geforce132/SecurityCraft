package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.blockentities.ReinforcedDropperBlockEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.dispenser.ProxyBlockSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.VanillaInventoryCodeHooks;

public class ReinforcedDropperBlock extends ReinforcedDispenserBlock {
	private static final IDispenseItemBehavior DISPENSE_BEHAVIOUR = new DefaultDispenseItemBehavior();

	public ReinforcedDropperBlock(Properties properties) {
		super(properties);
	}

	@Override
	public float getDestroyProgress(BlockState state, PlayerEntity player, IBlockReader level, BlockPos pos) {
		return BlockUtils.getDestroyProgress(super::getDestroyProgress, state, player, level, pos);
	}

	@Override
	public boolean canHarvestBlock(BlockState state, IBlockReader level, BlockPos pos, PlayerEntity player) {
		return ConfigHandler.SERVER.alwaysDrop.get() || super.canHarvestBlock(state, level, pos, player);
	}

	@Override
	public ToolType getHarvestTool(BlockState state) {
		return getVanillaBlock().getHarvestTool(convertToVanilla(null, null, state));
	}

	@Override
	public int getHarvestLevel(BlockState state) {
		return getVanillaBlock().getHarvestLevel(convertToVanilla(null, null, state));
	}

	@Override
	public IDispenseItemBehavior getDispenseMethod(ItemStack stack) {
		return DISPENSE_BEHAVIOUR;
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader level) {
		return new ReinforcedDropperBlockEntity();
	}

	@Override
	protected void dispenseFrom(ServerWorld level, BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof ReinforcedDropperBlockEntity) {
			ProxyBlockSource source = new ProxyBlockSource(level, pos);
			ReinforcedDropperBlockEntity be = source.getEntity();
			int randomSlot = be.getRandomSlot();

			if (randomSlot < 0)
				level.levelEvent(Constants.WorldEvents.DISPENSER_FAIL_SOUND, pos, 0);
			else {
				ItemStack dispenseStack = be.getItem(randomSlot);

				if (!dispenseStack.isEmpty() && VanillaInventoryCodeHooks.dropperInsertHook(level, pos, be, randomSlot, dispenseStack)) {
					Direction direction = level.getBlockState(pos).getValue(FACING);
					IInventory inventory = HopperTileEntity.getContainerAt(level, pos.relative(direction));
					ItemStack afterDispenseStack;

					if (inventory == null)
						afterDispenseStack = DISPENSE_BEHAVIOUR.dispense(source, dispenseStack);
					else {
						afterDispenseStack = HopperTileEntity.addItem(be, inventory, dispenseStack.copy().split(1), direction.getOpposite());

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
