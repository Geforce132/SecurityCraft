package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IPasswordConvertible;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.client.PlaySoundAtPos;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

public class KeyPanelItem extends BlockItem {

	public KeyPanelItem(Item.Properties properties){
		super(SCContent.KEY_PANEL_BLOCK.get(), properties);
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext ctx)
	{
		World world = ctx.getWorld();
		BlockPos pos = ctx.getPos();
		Block block = world.getBlockState(pos).getBlock();
		PlayerEntity player = ctx.getPlayer();
		ItemStack stack = ctx.getItem();

		for (IPasswordConvertible pc : SecurityCraftAPI.getRegisteredPasswordConvertibles()) {
			if(block == pc.getOriginalBlock())
			{
				if(pc.convert(player, world, pos))
				{
					if(!player.isCreative())
						stack.shrink(1);

					if (!world.isRemote)
						SecurityCraft.channel.send(PacketDistributor.ALL.noArg(), new PlaySoundAtPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SCSounds.LOCK.location.toString(), 1.0F, "blocks"));

					return ActionResultType.SUCCESS;
				}
			}
		}

		return super.onItemUse(ctx); //allow key panel to be placed when it did not convert anything
	}
}
