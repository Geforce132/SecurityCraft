package net.geforcemods.securitycraft.imc.waila;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.INameable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntityKeycardReader;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class WailaDataProvider implements IWailaDataProvider {

	public static void callbackRegister(IWailaRegistrar registrar){
		mod_SecurityCraft.log("Adding Waila support!");
		
		registrar.addConfig("SecurityCraft", "securitycraft.showowner", StatCollector.translateToLocal("waila.displayOwner"));
		registrar.addConfig("SecurityCraft", "securitycraft.showmodules", StatCollector.translateToLocal("waila.showModules"));
		registrar.addConfig("SecurityCraft", "securitycraft.showpasswords", StatCollector.translateToLocal("waila.showPasswords"));
		registrar.addConfig("SecurityCraft", "securitycraft.showcustomname", StatCollector.translateToLocal("waila.showCustomName"));
		registrar.registerBodyProvider(new WailaDataProvider(), IOwnable.class);
		registrar.registerStackProvider(new WailaDataProvider(), ICustomWailaDisplay.class);
	}
	
	public ItemStack getWailaStack(IWailaDataAccessor data, IWailaConfigHandler config) {
		if(data.getBlock() instanceof ICustomWailaDisplay) {			
            return ((ICustomWailaDisplay) data.getBlock()).getDisplayStack(data.getWorld(), data.getPosition().blockX, data.getPosition().blockY, data.getPosition().blockZ);
		}
		
		return null;
	}

	public List<String> getWailaHead(ItemStack itemstack, List<String> head, IWailaDataAccessor data, IWailaConfigHandler config) {
		return head;
	}

	public List<String> getWailaBody(ItemStack itemstack, List<String> body, IWailaDataAccessor data, IWailaConfigHandler config) {
		if(data.getBlock() instanceof ICustomWailaDisplay && !((ICustomWailaDisplay) data.getBlock()).shouldShowSCInfo(data.getWorld(), data.getPosition().blockX, data.getPosition().blockY, data.getPosition().blockZ)) return body;

		if(config.getConfig("securitycraft.showowner") && data.getTileEntity() instanceof IOwnable){
			body.add(StatCollector.translateToLocal("waila.owner") + " " + ((IOwnable) data.getTileEntity()).getOwner().getName());
		}
		
		if(config.getConfig("securitycraft.showmodules") && data.getTileEntity() instanceof CustomizableSCTE && ((CustomizableSCTE) data.getTileEntity()).getOwner().isOwner(data.getPlayer())){
			if(!((CustomizableSCTE) data.getTileEntity()).getModules().isEmpty()){
				body.add(StatCollector.translateToLocal("waila.equipped"));
			}
			
			for(EnumCustomModules module : ((CustomizableSCTE) data.getTileEntity()).getModules()){
				body.add("- " + module.getName());
			}
		}
		
		if(config.getConfig("securitycraft.showpasswords") && data.getTileEntity() instanceof IPasswordProtected && !(data.getTileEntity() instanceof TileEntityKeycardReader) && ((IOwnable) data.getTileEntity()).getOwner().isOwner(data.getPlayer())){
			String password = ((IPasswordProtected) data.getTileEntity()).getPassword();
			
			body.add(StatCollector.translateToLocal("waila.password") + " " + (password != null && !password.isEmpty() ? password : StatCollector.translateToLocal("waila.password.notSet")));
		}
		
		if(config.getConfig("securitycraft.showcustomname") && data.getTileEntity() instanceof INameable && ((INameable) data.getTileEntity()).canBeNamed()){
			String name = ((INameable) data.getTileEntity()).getCustomName();
			
			body.add(StatCollector.translateToLocal("waila.customName") + " " + (((INameable) data.getTileEntity()).hasCustomName() ? name : StatCollector.translateToLocal("waila.customName.notSet")));
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
