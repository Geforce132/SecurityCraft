package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IPasswordConvertible;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class KeyPanelItem extends BlockItem {

	public KeyPanelItem(Item.Properties properties){
		super(SCContent.KEY_PANEL_BLOCK.get(), properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext ctx)
	{
		Level level = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		Block block = level.getBlockState(pos).getBlock();
		Player player = ctx.getPlayer();
		ItemStack stack = ctx.getItemInHand();

		for (IPasswordConvertible pc : SecurityCraftAPI.getRegisteredPasswordConvertibles()) {
			if(block == pc.getOriginalBlock())
			{
				if(pc.convert(player, level, pos))
				{
					if(!player.isCreative())
						stack.shrink(1);

					level.playSound(player, pos, SCSounds.LOCK.event, SoundSource.BLOCKS, 1.0F, 1.0F);

					return InteractionResult.SUCCESS;
				}
			}
		}

		return super.useOn(ctx); //allow key panel to be placed when it did not convert anything
	}
}
