package net.geforcemods.securitycraft.imc.waila;

import mcp.mobius.waila.api.ITaggedList.ITipList;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataAccessorServer;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;

public class WailaDataProvider implements IWailaDataProvider {

	public static void callbackRegister(IWailaRegistrar registrar){
		mod_SecurityCraft.log("Adding Waila support!");

		registrar.addConfig("SecurityCraft", "securitycraft.showowner", StatCollector.translateToLocal("waila.displayOwner"));
		registrar.addConfig("SecurityCraft", "securitycraft.showmodules", StatCollector.translateToLocal("waila.showModules"));
		registrar.addConfig("SecurityCraft", "securitycraft.showpasswords", StatCollector.translateToLocal("waila.showPasswords"));
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
			tipList.add(StatCollector.translateToLocal("waila.owner") + " " + ((IOwnable) iDataAccessor.getTileEntity()).getOwner().getName());
		}
		
		if(iConfigHandler.getConfig("securitycraft.showmodules") && iDataAccessor.getTileEntity() instanceof CustomizableSCTE && ((CustomizableSCTE) iDataAccessor.getTileEntity()).getOwner().isOwner(iDataAccessor.getPlayer())){
			if(!((CustomizableSCTE) iDataAccessor.getTileEntity()).getModules().isEmpty()){
				tipList.add(StatCollector.translateToLocal("waila.equipped"));
			}
			
			for(EnumCustomModules module : ((CustomizableSCTE) iDataAccessor.getTileEntity()).getModules()){
				tipList.add("- " + module.getName());
			}
		}
		
		if(iConfigHandler.getConfig("securitycraft.showpasswords") && iDataAccessor.getTileEntity() instanceof IPasswordProtected && ((IOwnable) iDataAccessor.getTileEntity()).getOwner().isOwner(iDataAccessor.getPlayer())){			
			String password = ((IPasswordProtected) iDataAccessor.getTileEntity()).getPassword();
			
			tipList.add(StatCollector.translateToLocal("waila.password") + " " + (password != null && !password.isEmpty() ? password : StatCollector.translateToLocal("waila.password.notSet")));
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
