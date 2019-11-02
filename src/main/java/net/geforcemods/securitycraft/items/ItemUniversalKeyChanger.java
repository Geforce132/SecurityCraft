package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemUniversalKeyChanger extends Item {

	public ItemUniversalKeyChanger() {
		super();
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if(!world.isRemote)
			if(world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof IPasswordProtected) {
				if(((IOwnable) world.getTileEntity(x, y, z)).getOwner().isOwner(player))
					player.openGui(SecurityCraft.instance, GuiHandler.KEY_CHANGER_GUI_ID, world, x, y, z);
				else
					PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.securitycraft:universalKeyChanger.name"), StatCollector.translateToLocal("messages.securitycraft:notOwned").replace("#", ((IOwnable) world.getTileEntity(x, y, z)).getOwner().getName()), EnumChatFormatting.RED);

				return true;
			}

		return false;
	}

}
