package org.freeforums.geforce.securitycraft.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.enums.EnumCustomModules;
import org.freeforums.geforce.securitycraft.interfaces.IPasswordProtected;
import org.freeforums.geforce.securitycraft.main.HelpfulMethods;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.tileentity.CustomizableSCTE;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadChest;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityOwnable;

public class ItemAdminTool extends ItemWithInfo {

	public ItemAdminTool(String info, String[] recipe) {
		super(info, recipe);
		
		if(mod_SecurityCraft.configHandler.allowAdminTool){
			this.setCreativeTab(mod_SecurityCraft.tabSCTechnical);
		}
	}
	
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10){
		if(!par3World.isRemote){
			if(par3World.getTileEntity(par4, par5, par6) != null){
				TileEntity te = par3World.getTileEntity(par4, par5, par6);
				HelpfulMethods.sendMessageToPlayer(par2EntityPlayer, "Block info:", EnumChatFormatting.GRAY);
				
				if(te instanceof TileEntityOwnable){
					HelpfulMethods.sendMessageToPlayer(par2EntityPlayer, "Owner: " + (((TileEntityOwnable) te).getOwnerName() == null ? "????" : ((TileEntityOwnable) te).getOwnerName()), null);
					HelpfulMethods.sendMessageToPlayer(par2EntityPlayer, "Owner's UUID: " + (((TileEntityOwnable) te).getOwnerUUID() == null ? "????" : ((TileEntityOwnable) te).getOwnerUUID()), null);
				}else if(te instanceof TileEntityKeypadChest){
					HelpfulMethods.sendMessageToPlayer(par2EntityPlayer, "Owner: " + (((TileEntityKeypadChest) te).getOwnerName() == null ? "????" : ((TileEntityKeypadChest) te).getOwnerName()), null);
					HelpfulMethods.sendMessageToPlayer(par2EntityPlayer, "Owner's UUID: " + (((TileEntityKeypadChest) te).getOwnerUUID() == null ? "????" : ((TileEntityKeypadChest) te).getOwnerUUID()), null);
				}
				
				if(te instanceof IPasswordProtected){
					HelpfulMethods.sendMessageToPlayer(par2EntityPlayer, "Password: " + (((IPasswordProtected) te).getPassword() == null ? "????" : ((IPasswordProtected) te).getPassword()), null);
				}
				
				if(te instanceof CustomizableSCTE){
					List<EnumCustomModules> modules = ((CustomizableSCTE) te).getModules();
					
					if(!modules.isEmpty()){
						HelpfulMethods.sendMessageToPlayer(par2EntityPlayer, "Equipped modules: ", null);
						
						for(EnumCustomModules module : modules){
							HelpfulMethods.sendMessageToPlayer(par2EntityPlayer, "-" + module.getModuleName(), null);
						}
					}
				}
				
				return true;
			}
		}
		
		return false;
	}

}
