package net.geforcemods.securitycraft.api;

import java.util.Optional;

import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public interface IDisguisable {
	public default ItemStack getDisguisedStack(IBlockReader level, BlockPos pos) {
		if (level != null && level.getBlockEntity(pos) instanceof IModuleInventory) {
			IModuleInventory be = (IModuleInventory) level.getBlockEntity(pos);
			ItemStack stack = be.isModuleEnabled(ModuleType.DISGUISE) ? be.getModule(ModuleType.DISGUISE) : ItemStack.EMPTY;

			if (!stack.isEmpty()) {
				Block block = ModuleItem.getBlockAddon(stack);

				if (block != null)
					return new ItemStack(block);
			}
		}

		return getDefaultStack();
	}

	public default ItemStack getDefaultStack() {
		return new ItemStack((Block) this);
	}

	public static BlockState getDisguisedStateOrDefault(BlockState state, IBlockReader level, BlockPos pos) {
		return getDisguisedBlockState(level, pos).orElse(state);
	}

	public static Optional<BlockState> getDisguisedBlockState(IBlockReader level, BlockPos pos) {
		TileEntity te = level.getBlockEntity(pos);

		if (te instanceof IModuleInventory) {
			IModuleInventory be = (IModuleInventory) te;

			if (be.isModuleEnabled(ModuleType.DISGUISE))
				return IDisguisable.getDisguisedBlockStateFromStack(be.getModule(ModuleType.DISGUISE));
		}

		return Optional.empty();
	}

	public static Optional<BlockState> getDisguisedBlockStateFromStack(ItemStack module) {
		if (!module.isEmpty()) {
			BlockState disguisedState = NBTUtil.readBlockState(module.getOrCreateTag().getCompound("SavedState"));

			if (disguisedState != null && disguisedState.getBlock() != Blocks.AIR)
				return Optional.of(disguisedState);
			else { //fallback, mainly for upgrading old worlds from before the state selector existed
				Block block = ModuleItem.getBlockAddon(module);

				if (block != null)
					return Optional.of(block.defaultBlockState());
			}
		}

		return Optional.empty();
	}
}
