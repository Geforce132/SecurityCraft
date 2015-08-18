package org.freeforums.geforce.securitycraft.imc.waila;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.api.CustomizableSCTE;
import org.freeforums.geforce.securitycraft.api.IOwnable;
import org.freeforums.geforce.securitycraft.api.IPasswordProtected;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.misc.EnumCustomModules;

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

	public List<String> getWailaHead(ItemStack itemstack, List<String> head, IWailaDataAccessor data, IWailaConfigHandler config) {
		return head;
	}

	public List<String> getWailaBody(ItemStack itemstack, List<String> body, IWailaDataAccessor data, IWailaConfigHandler config) {
		if(config.getConfig("securitycraft.showowner") && data.getTileEntity() instanceof IOwnable){
			body.add("Owner: " + ((IOwnable) data.getTileEntity()).getOwnerName());
		}
		
		if(config.getConfig("securitycraft.showmodules") && data.getTileEntity() instanceof CustomizableSCTE && ((CustomizableSCTE) data.getTileEntity()).isOwner(data.getPlayer())){
			if(!((CustomizableSCTE) data.getTileEntity()).getModules().isEmpty()){
				body.add("Equipped with:");
			}
			
			for(EnumCustomModules module : ((CustomizableSCTE) data.getTileEntity()).getModules()){
				body.add("- " + module.getModuleName());
			}
		}
		
		if(config.getConfig("securitycraft.showpasswords") && data.getTileEntity() instanceof IPasswordProtected){			
			String password = ((IPasswordProtected) data.getTileEntity()).getPassword();
			
			body.add("Password: " + (password != null && !password.isEmpty() ? password : "????"));
		}
		
		return body;
	}
	
	public List<String> getWailaTail(ItemStack itemstack, List<String> tail, IWailaDataAccessor data, IWailaConfigHandler config) {
		return tail;
	}
	
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity tileEntity, NBTTagCompound tagCompound, World world, int arg4, int arg5, int arg6) {
		return tagCompound;
	}

}
