package net.breakinbad.securitycraft.imc.waila;

import mcp.mobius.waila.api.ITaggedList.ITipList;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataAccessorServer;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.breakinbad.securitycraft.api.CustomizableSCTE;
import net.breakinbad.securitycraft.api.IOwnable;
import net.breakinbad.securitycraft.api.IPasswordProtected;
import net.breakinbad.securitycraft.main.mod_SecurityCraft;
import net.breakinbad.securitycraft.main.Utils.BlockUtils;
import net.breakinbad.securitycraft.misc.EnumCustomModules;
import net.breakinbad.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class WailaDataProvider implements IWailaDataProvider {

	public static void callbackRegister(IWailaRegistrar registrar){
		mod_SecurityCraft.log("Adding Waila support!");
		
		registrar.addConfig("SecurityCraft", "securitycraft.showowner", "Display Owner?");
		registrar.addConfig("SecurityCraft", "securitycraft.showmodules", "Show Modules?");
		registrar.addConfig("SecurityCraft", "securitycraft.showpasswords", "Show Passwords?");
		registrar.registerBodyProvider(new WailaDataProvider(), IOwnable.class);
	}
	
	public ItemStack getWailaStack(IWailaDataAccessor data, IWailaConfigHandler config) {
		return null;
	}

	public ITipList getWailaHead(ItemStack itemStack, ITipList tipList, IWailaDataAccessor iDataAccessor, IWailaConfigHandler iConfigHandler) {
		return tipList;
	}

	public ITipList getWailaBody(ItemStack itemStack, ITipList tipList, IWailaDataAccessor iDataAccessor, IWailaConfigHandler iConfigHandler) {
		if(iConfigHandler.getConfig("securitycraft.showowner") && iDataAccessor.getTileEntity() instanceof IOwnable){
			tipList.add("Owner: " + ((IOwnable) iDataAccessor.getTileEntity()).getOwnerName());
		}
		
		if(iConfigHandler.getConfig("securitycraft.showmodules") && iDataAccessor.getTileEntity() instanceof CustomizableSCTE && ((CustomizableSCTE) iDataAccessor.getTileEntity()).isOwner(iDataAccessor.getPlayer())){
			if(!((CustomizableSCTE) iDataAccessor.getTileEntity()).getModules().isEmpty()){
				tipList.add("Equipped with:");
			}
			
			for(EnumCustomModules module : ((CustomizableSCTE) iDataAccessor.getTileEntity()).getModules()){
				tipList.add("- " + module.getModuleName());
			}
		}
		
		if(iConfigHandler.getConfig("securitycraft.showpasswords") && iDataAccessor.getTileEntity() instanceof IPasswordProtected && BlockUtils.isOwnerOfBlock((TileEntityOwnable) iDataAccessor.getTileEntity(), iDataAccessor.getPlayer())){			
			String password = ((IPasswordProtected) iDataAccessor.getTileEntity()).getPassword();
			
			tipList.add("Password: " + (password != null && !password.isEmpty() ? password : "????"));
		}
		
		return tipList;
	}
	
	public ITipList getWailaTail(ItemStack itemStack, ITipList tipList, IWailaDataAccessor iDataAccessor, IWailaConfigHandler iConfigHandler) {
		return tipList;
	}
	
	public NBTTagCompound getNBTData(TileEntity tileEntity, NBTTagCompound tagCompound, IWailaDataAccessorServer iDataAccessor) {
		return tagCompound;
	}

}
