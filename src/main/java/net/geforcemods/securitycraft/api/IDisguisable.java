package net.geforcemods.securitycraft.api;

import java.util.Optional;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.components.SavedBlockState;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
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

	public static boolean shouldPickBlockDisguise(BlockGetter level, BlockPos pos, Player player) {
		return !player.isCreative() && !(level != null && level.getBlockEntity(pos) instanceof IOwnable ownable && ownable.isOwnedBy(player));
	}

	public static Optional<BlockState> getDisguisedBlockState(BlockEntity be) {
		if (be instanceof IModuleInventory moduleInv && moduleInv.isModuleEnabled(ModuleType.DISGUISE))
			return getDisguisedBlockStateFromStack(moduleInv.getModule(ModuleType.DISGUISE));

		return Optional.empty();
	}

	public static Optional<BlockState> getDisguisedBlockStateFromStack(ItemStack module) {
		SavedBlockState savedBlockState = module.get(SCContent.SAVED_BLOCK_STATE);

		if (savedBlockState != null) {
			BlockState disguisedState = savedBlockState.state();

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
