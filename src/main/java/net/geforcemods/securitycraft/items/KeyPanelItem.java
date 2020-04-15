package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blocks.IPasswordConvertible;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.client.PlaySoundAtPos;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

public class KeyPanelItem extends Item {

	public KeyPanelItem(){
		super(new Item.Properties().group(SecurityCraft.groupSCTechnical));
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext ctx)
	{
		return onItemUse(ctx.getPlayer(), ctx.getWorld(), ctx.getPos(), ctx.getItem(), ctx.getFace(), ctx.getHitVec().x, ctx.getHitVec().y, ctx.getHitVec().z);
	}

	public ActionResultType onItemUse(PlayerEntity player, World world, BlockPos pos, ItemStack stack, Direction facing, double hitX, double hitY, double hitZ){
		if(!world.isRemote){
			IPasswordConvertible.BLOCKS.forEach(pc -> {
				if(BlockUtils.getBlock(world, pos) == ((IPasswordConvertible)pc).getOriginalBlock())
				{
					if(((IPasswordConvertible)pc).convert(player, world, pos))
					{
						if(!player.isCreative())
							stack.shrink(1);

						SecurityCraft.channel.send(PacketDistributor.ALL.noArg(), new PlaySoundAtPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SCSounds.LOCK.location.toString(), 1.0F, "blocks"));
					}
				}
			});
			return ActionResultType.SUCCESS;
		}

		return ActionResultType.FAIL;
	}
}
