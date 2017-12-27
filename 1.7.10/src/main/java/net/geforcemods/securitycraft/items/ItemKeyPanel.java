package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.blocks.IPasswordConvertible;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.packets.PacketCPlaySoundAtPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemKeyPanel extends Item {

	public ItemKeyPanel(){
		super();
	}

	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10){
		if(!par3World.isRemote){
			IPasswordConvertible.BLOCKS.forEach((pc) -> {
				if(par3World.getBlock(par4, par5, par6) == ((IPasswordConvertible)pc).getOriginalBlock())
				{
					if(((IPasswordConvertible)pc).convert(par2EntityPlayer, par3World, par4, par5, par6) && !par2EntityPlayer.capabilities.isCreativeMode)
						par1ItemStack.stackSize--;
					mod_SecurityCraft.network.sendToAll(new PacketCPlaySoundAtPos(par2EntityPlayer.posX, par2EntityPlayer.posY, par2EntityPlayer.posZ, SCSounds.LOCK.path, 1.0F));
				}
			});
			return true;
		}

		return false;
	}
}
