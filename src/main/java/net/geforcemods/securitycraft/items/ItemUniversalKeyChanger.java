package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemUniversalKeyChanger extends Item {

	public ItemUniversalKeyChanger() {
		super();
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(!world.isRemote)
			if(world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof IPasswordProtected) {
				if(((IOwnable) world.getTileEntity(pos)).getOwner().isOwner(player))
					player.openGui(SecurityCraft.instance, GuiHandler.KEY_CHANGER_GUI_ID, world, pos.getX(), pos.getY(), pos.getZ());
				else
					PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.securitycraft:universalKeyChanger.name"), StatCollector.translateToLocal("messages.securitycraft:notOwned").replace("#", ((IOwnable) world.getTileEntity(pos)).getOwner().getName()), EnumChatFormatting.RED);

				return true;
			}

		return false;
	}

}
