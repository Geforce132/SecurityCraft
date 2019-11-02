package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blocks.IPasswordConvertible;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.packets.PacketCPlaySoundAtPos;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemKeyPanel extends Item {

	public ItemKeyPanel(){
		super();
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ){
		if(!world.isRemote){
			IPasswordConvertible.BLOCKS.forEach((pc) -> {
				if(BlockUtils.getBlock(world, pos) == ((IPasswordConvertible)pc).getOriginalBlock())
				{
					if(((IPasswordConvertible)pc).convert(player, world, pos) && !player.capabilities.isCreativeMode)
						stack.stackSize--;
					SecurityCraft.network.sendToAll(new PacketCPlaySoundAtPos(player.posX, player.posY, player.posZ, SCSounds.LOCK.path, 1.0F));
				}
			});
			return true;
		}

		return false;
	}
}
