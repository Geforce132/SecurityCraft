package net.geforcemods.securitycraft.items;

import javax.annotation.Nullable;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.blockentities.SecretSignBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WallOrFloorItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SecretSignItem extends WallOrFloorItem {
	private final String translationKey;

	public SecretSignItem(Item.Properties properties, Block floor, Block wall, String translationKey) {
		super(floor, wall, properties);

		this.translationKey = translationKey;
	}

	@Override
	public String getDescriptionId() {
		return translationKey;
	}

	@Override
	public String getDescriptionId(ItemStack stack) {
		return getDescriptionId();
	}

	@Override
	public boolean updateCustomBlockEntityTag(BlockPos pos, World level, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
		boolean flag = super.updateCustomBlockEntityTag(pos, level, player, stack, state);

		if (!flag && player != null) {
			SecretSignBlockEntity be = (SecretSignBlockEntity) level.getBlockEntity(pos);

			be.setAllowedPlayerEditor(player);

			if (level.isClientSide)
				ClientHandler.displayEditSecretSignScreen(be);
		}

		return flag;
	}
}
