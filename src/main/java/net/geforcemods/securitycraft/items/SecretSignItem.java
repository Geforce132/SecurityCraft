package net.geforcemods.securitycraft.items;

import javax.annotation.Nullable;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.blockentities.SecretSignBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SecretSignItem extends StandingAndWallBlockItem {
	public SecretSignItem(Item.Properties properties, Block floor, Block wall) {
		super(floor, wall, properties, Direction.DOWN);
	}

	@Override
	public boolean updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player, ItemStack stack, BlockState state) {
		boolean flag = super.updateCustomBlockEntityTag(pos, level, player, stack, state);

		if (!flag && player != null) {
			SecretSignBlockEntity be = (SecretSignBlockEntity) level.getBlockEntity(pos);

			be.setAllowedPlayerEditor(player.getUUID());

			if (level.isClientSide)
				ClientHandler.displayEditSecretSignScreen(be, true);
		}

		return flag;
	}
}
