package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemAdminTool extends Item {

	public ItemAdminTool() {
		super();

		if(SecurityCraft.config.allowAdminTool)
			setCreativeTab(SecurityCraft.tabSCTechnical);
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if(!world.isRemote) {
			if(world.getTileEntity(x, y, z) != null) {
				TileEntity te = world.getTileEntity(x, y, z);
				boolean hasInfo = false;

				if(te instanceof IOwnable) {
					PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.securitycraft:adminTool.name"), StatCollector.translateToLocal("messages.securitycraft:adminTool.owner.name").replace("#", (((IOwnable) te).getOwner().getName() == null ? "????" : ((IOwnable) te).getOwner().getName())), EnumChatFormatting.DARK_PURPLE);
					PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.securitycraft:adminTool.name"), StatCollector.translateToLocal("messages.securitycraft:adminTool.owner.uuid").replace("#", (((IOwnable) te).getOwner().getUUID() == null ? "????" : ((IOwnable) te).getOwner().getUUID())), EnumChatFormatting.DARK_PURPLE);
					hasInfo = true;
				}

				if(te instanceof IPasswordProtected) {
					PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.securitycraft:adminTool.name"), StatCollector.translateToLocal("messages.securitycraft:adminTool.password").replace("#", (((IPasswordProtected) te).getPassword() == null ? "????" : ((IPasswordProtected) te).getPassword())), EnumChatFormatting.DARK_PURPLE);
					hasInfo = true;
				}

				if(te instanceof CustomizableSCTE) {
					List<EnumCustomModules> modules = ((CustomizableSCTE) te).getModules();

					if(!modules.isEmpty()) {
						PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.securitycraft:adminTool.name"), StatCollector.translateToLocal("messages.securitycraft:adminTool.equippedModules"), EnumChatFormatting.DARK_PURPLE);

						for(EnumCustomModules module : modules)
							PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.securitycraft:adminTool.name"), "-" + module.getName(), EnumChatFormatting.DARK_PURPLE);

						hasInfo = true;
					}
				}

				if(!hasInfo)
					PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.securitycraft:adminTool.name"), StatCollector.translateToLocal("messages.securitycraft:adminTool.noInfo"), EnumChatFormatting.DARK_PURPLE);

				return false;
			}

			PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.securitycraft:adminTool.name"), StatCollector.translateToLocal("messages.securitycraft:adminTool.noInfo"), EnumChatFormatting.DARK_PURPLE);
		}

		return false;
	}

}
