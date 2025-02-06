package net.geforcemods.securitycraft.api;

import java.util.Optional;

import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public interface IDisguisable {
	public default ItemStack getDisguisedStack(BlockGetter level, BlockPos pos) {
		if (level != null && level.getBlockEntity(pos) instanceof IModuleInventory be) {
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

	public static BlockState getDisguisedStateOrDefault(BlockState state, BlockGetter level, BlockPos pos) {
		return getDisguisedStateOrDefault(level.getBlockEntity(pos), state, level, pos);
	}

	public static BlockState getDisguisedStateOrDefault(BlockEntity be, BlockState state, BlockGetter level, BlockPos pos) {
		if (level instanceof LevelReader reader)
			return getDisguisedBlockState(be, reader).orElse(state);
		else
			return state;
	}

	public static Optional<BlockState> getDisguisedBlockState(BlockEntity be) {
		return be.hasLevel() ? getDisguisedBlockState(be, be.getLevel()) : Optional.empty();
	}

	public static Optional<BlockState> getDisguisedBlockState(BlockEntity be, LevelReader level) {
		if (be instanceof IModuleInventory moduleInv && moduleInv.isModuleEnabled(ModuleType.DISGUISE))
			return getDisguisedBlockStateFromStack(level.holderLookup(Registries.BLOCK), moduleInv.getModule(ModuleType.DISGUISE));

		return Optional.empty();
	}

	public static Optional<BlockState> getDisguisedBlockStateFromStack(Level level, ItemStack module) {
		return getDisguisedBlockStateFromStack(level.holderLookup(Registries.BLOCK), module);
	}

	public static Optional<BlockState> getDisguisedBlockStateFromStack(HolderGetter<Block> holderGetter, ItemStack module) {
		if (!module.isEmpty()) {
			BlockState disguisedState = NbtUtils.readBlockState(holderGetter, module.getOrCreateTag().getCompound("SavedState"));

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
