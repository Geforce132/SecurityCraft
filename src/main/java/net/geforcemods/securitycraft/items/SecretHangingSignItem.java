package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.blockentities.SecretHangingSignBlockEntity;
import net.geforcemods.securitycraft.blocks.SecretWallHangingSignBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SecretHangingSignItem extends StandingAndWallBlockItem {
	public SecretHangingSignItem(Item.Properties properties, Block block, Block wallBlock) {
		super(block, wallBlock, properties, Direction.UP);
	}

	@Override
	protected boolean canPlace(LevelReader level, BlockState state, BlockPos pos) {
		Block block = state.getBlock();

		if (block instanceof SecretWallHangingSignBlock wallBlock) {
			if (!wallBlock.canPlace(state, level, pos))
				return false;
		}

		return super.canPlace(level, state, pos);
	}

	@Override
	protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, Player player, ItemStack stack, BlockState state) {
		boolean flag = super.updateCustomBlockEntityTag(pos, level, player, stack, state);

		if (!flag && player != null && level.getBlockEntity(pos) instanceof SecretHangingSignBlockEntity be) {
			be.setAllowedPlayerEditor(player.getUUID());

			if (level.isClientSide)
				ClientHandler.displayEditSecretHangingSignScreen(be);
		}

		return flag;
	}
}
