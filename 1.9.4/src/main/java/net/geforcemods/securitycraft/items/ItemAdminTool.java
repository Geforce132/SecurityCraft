package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

public class ItemAdminTool extends Item {

	public ItemAdminTool() {
		super();
		
		if(mod_SecurityCraft.configHandler.allowAdminTool) {
			this.setCreativeTab(mod_SecurityCraft.tabSCTechnical);
		}
	}
	
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, BlockPos pos, EnumFacing par5EnumFacing, float hitX, float hitY, float hitZ) {
		if(!par3World.isRemote) {
			if(par3World.getTileEntity(pos) != null) {
				TileEntity te = par3World.getTileEntity(pos);
				boolean hasInfo = false;

				if(te instanceof IOwnable) {
					PlayerUtils.sendMessageToPlayer(par2EntityPlayer, I18n.translateToLocal("item.adminTool.name"), I18n.translateToLocal("messages.adminTool.owner.name").replace("#", (((IOwnable) te).getOwner().getName() == null ? "????" : ((IOwnable) te).getOwner().getName())), TextFormatting.DARK_PURPLE);
					PlayerUtils.sendMessageToPlayer(par2EntityPlayer, I18n.translateToLocal("item.adminTool.name"), I18n.translateToLocal("messages.adminTool.owner.uuid").replace("#", (((IOwnable) te).getOwner().getUUID() == null ? "????" : ((IOwnable) te).getOwner().getUUID())), TextFormatting.DARK_PURPLE);
					hasInfo = true;
				}
				
				if(te instanceof IPasswordProtected) {
					PlayerUtils.sendMessageToPlayer(par2EntityPlayer, I18n.translateToLocal("item.adminTool.name"), I18n.translateToLocal("messages.adminTool.password").replace("#", (((IPasswordProtected) te).getPassword() == null ? "????" : ((IPasswordProtected) te).getPassword())), TextFormatting.DARK_PURPLE);
					hasInfo = true;
				}
				
				if(te instanceof CustomizableSCTE) {
					List<EnumCustomModules> modules = ((CustomizableSCTE) te).getModules();
					
					if(!modules.isEmpty()) {
						PlayerUtils.sendMessageToPlayer(par2EntityPlayer, I18n.translateToLocal("item.adminTool.name"), I18n.translateToLocal("messages.adminTool.equippedModules"), TextFormatting.DARK_PURPLE);
						
						for(EnumCustomModules module : modules) {
							PlayerUtils.sendMessageToPlayer(par2EntityPlayer, I18n.translateToLocal("item.adminTool.name"), "-" + module.getName(), TextFormatting.DARK_PURPLE);
						}
						
						hasInfo = true;
					}
				}
				
				if(!hasInfo) {
					PlayerUtils.sendMessageToPlayer(par2EntityPlayer, I18n.translateToLocal("item.adminTool.name"), I18n.translateToLocal("messages.adminTool.noInfo"), TextFormatting.DARK_PURPLE);
				}
				
				return false;
			}
			
			PlayerUtils.sendMessageToPlayer(par2EntityPlayer, I18n.translateToLocal("item.adminTool.name"), I18n.translateToLocal("messages.adminTool.noInfo"), TextFormatting.DARK_PURPLE);
		}
		
		return false;
	}

}
