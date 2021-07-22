package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IPasswordConvertible;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.client.PlaySoundAtPos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

public class KeyPanelItem extends Item {

	public KeyPanelItem(Item.Properties properties){
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext ctx)
	{
		return onItemUse(ctx.getPlayer(), ctx.getLevel(), ctx.getClickedPos(), ctx.getItemInHand(), ctx.getClickedFace(), ctx.getClickLocation().x, ctx.getClickLocation().y, ctx.getClickLocation().z);
	}

	public InteractionResult onItemUse(Player player, Level world, BlockPos pos, ItemStack stack, Direction facing, double hitX, double hitY, double hitZ){
		for (IPasswordConvertible pc : SecurityCraftAPI.getRegisteredPasswordConvertibles()) {
			if(world.getBlockState(pos).getBlock() == pc.getOriginalBlock())
			{
				if(pc.convert(player, world, pos))
				{
					if(!player.isCreative())
						stack.shrink(1);

					if (!world.isClientSide)
						SecurityCraft.channel.send(PacketDistributor.ALL.noArg(), new PlaySoundAtPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SCSounds.LOCK.location.toString(), 1.0F, "blocks"));

					return InteractionResult.SUCCESS;
				}
			}
		}

		return InteractionResult.PASS;
	}
}
