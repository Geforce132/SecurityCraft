package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.main.Utils.PlayerUtils;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class ItemAdminTool extends Item {

	public ItemAdminTool() {
		super();
		
		if(mod_SecurityCraft.configHandler.allowAdminTool){
			this.setCreativeTab(mod_SecurityCraft.tabSCTechnical);
		}
	}
	
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10){
		if(!par3World.isRemote){
			if(par3World.getTileEntity(par4, par5, par6) != null){
				TileEntity te = par3World.getTileEntity(par4, par5, par6);
				PlayerUtils.sendMessageToPlayer(par2EntityPlayer, "Block info:", EnumChatFormatting.GRAY);
				
				if(te instanceof IOwnable){
					PlayerUtils.sendMessageToPlayer(par2EntityPlayer, "Owner: " + (((IOwnable) te).getOwnerName() == null ? "????" : ((IOwnable) te).getOwnerName()), null);
					PlayerUtils.sendMessageToPlayer(par2EntityPlayer, "Owner's UUID: " + (((IOwnable) te).getOwnerUUID() == null ? "????" : ((IOwnable) te).getOwnerUUID()), null);
				}
				
				if(te instanceof IPasswordProtected){
					PlayerUtils.sendMessageToPlayer(par2EntityPlayer, "Password: " + (((IPasswordProtected) te).getPassword() == null ? "????" : ((IPasswordProtected) te).getPassword()), null);
				}
				
				if(te instanceof CustomizableSCTE){
					List<EnumCustomModules> modules = ((CustomizableSCTE) te).getModules();
					
					if(!modules.isEmpty()){
						PlayerUtils.sendMessageToPlayer(par2EntityPlayer, "Equipped modules: ", null);
						
						for(EnumCustomModules module : modules){
							PlayerUtils.sendMessageToPlayer(par2EntityPlayer, "-" + module.getModuleName(), null);
						}
					}
				}
				
				return true;
			}
		}
		
		return false;
	}

}
