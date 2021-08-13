package net.geforcemods.securitycraft.items;

import javax.annotation.Nullable;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.blockentities.SecretSignBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SecretSignItem extends StandingAndWallBlockItem
{
	private final String translationKey;

	public SecretSignItem(Item.Properties properties, Block floor, Block wall, String translationKey)
	{
		super(floor, wall, properties);

		this.translationKey = translationKey;
	}

	@Override
	public String getDescriptionId()
	{
		return translationKey;
	}

	@Override
	public String getDescriptionId(ItemStack stack)
	{
		return getDescriptionId();
	}

	@Override
	public boolean updateCustomBlockEntityTag(BlockPos pos, Level world, @Nullable Player player, ItemStack stack, BlockState state)
	{
		boolean flag = super.updateCustomBlockEntityTag(pos, world, player, stack, state);

		if(!flag && player != null)
		{
			SecretSignBlockEntity te = (SecretSignBlockEntity)world.getBlockEntity(pos);

			te.setAllowedPlayerEditor(player.getUUID());

			if(world.isClientSide)
				ClientHandler.displayEditSecretSignGui(te);
		}

		return flag;
	}
}
