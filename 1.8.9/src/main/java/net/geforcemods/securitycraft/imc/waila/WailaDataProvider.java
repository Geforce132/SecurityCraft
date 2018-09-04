package net.geforcemods.securitycraft.imc.waila;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.INameable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class WailaDataProvider implements IWailaDataProvider {

	public static void callbackRegister(IWailaRegistrar registrar){
		SecurityCraft.log("Adding Waila support!");

		registrar.addConfig("SecurityCraft", "securitycraft.showowner", StatCollector.translateToLocal("waila.securitycraft:displayOwner"));
		registrar.addConfig("SecurityCraft", "securitycraft.showmodules", StatCollector.translateToLocal("waila.securitycraft:showModules"));
		registrar.addConfig("SecurityCraft", "securitycraft.showpasswords", StatCollector.translateToLocal("waila.securitycraft:showPasswords"));
		registrar.addConfig("SecurityCraft", "securitycraft.showcustomname", StatCollector.translateToLocal("waila.securitycraft:showCustomName"));
		registrar.registerBodyProvider(new WailaDataProvider(), IOwnable.class);
		registrar.registerStackProvider(new WailaDataProvider(), ICustomWailaDisplay.class);
	}

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor data, IWailaConfigHandler config) {
		if(data.getBlock() instanceof ICustomWailaDisplay)
			return ((ICustomWailaDisplay) data.getBlock()).getDisplayStack(data.getWorld(), data.getBlockState(), data.getPosition());

		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack stack, List<String> head, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return head;
	}

	@Override
	public List<String> getWailaBody(ItemStack stack, List<String> body, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		if(accessor.getBlock() instanceof ICustomWailaDisplay && !((ICustomWailaDisplay) accessor.getBlock()).shouldShowSCInfo(accessor.getWorld(), accessor.getBlockState(), accessor.getPosition())) return body;

		if(config.getConfig("securitycraft.showowner") && accessor.getTileEntity() instanceof IOwnable)
			body.add(StatCollector.translateToLocal("waila.securitycraft:owner") + " " + ((IOwnable) accessor.getTileEntity()).getOwner().getName());

		if(config.getConfig("securitycraft.showmodules") && accessor.getTileEntity() instanceof CustomizableSCTE && ((CustomizableSCTE) accessor.getTileEntity()).getOwner().isOwner(accessor.getPlayer())){
			if(!((CustomizableSCTE) accessor.getTileEntity()).getModules().isEmpty())
				body.add(StatCollector.translateToLocal("waila.securitycraft:equipped"));

			for(EnumCustomModules module : ((CustomizableSCTE) accessor.getTileEntity()).getModules())
				body.add("- " + module.getName());
		}

		if(config.getConfig("securitycraft.showpasswords") && accessor.getTileEntity() instanceof IPasswordProtected && ((IOwnable) accessor.getTileEntity()).getOwner().isOwner(accessor.getPlayer())){
			String password = ((IPasswordProtected) accessor.getTileEntity()).getPassword();

			body.add(StatCollector.translateToLocal("waila.securitycraft:password") + " " + (password != null && !password.isEmpty() ? password : StatCollector.translateToLocal("waila.securitycraft:password.notSet")));
		}

		if(config.getConfig("securitycraft.showcustomname") && accessor.getTileEntity() instanceof INameable && ((INameable) accessor.getTileEntity()).canBeNamed()){
			String name = ((INameable) accessor.getTileEntity()).getCustomName();

			body.add(StatCollector.translateToLocal("waila.securitycraft:customName") + " " + (((INameable) accessor.getTileEntity()).hasCustomName() ? name : StatCollector.translateToLocal("waila.securitycraft:customName.notSet")));
		}

		return body;
	}

	@Override
	public List<String> getWailaTail(ItemStack stack, List<String> tail, IWailaDataAccessor data, IWailaConfigHandler config) {
		return tail;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity tileEntity, NBTTagCompound tagCompound, World world, BlockPos pos) {
		return tagCompound;
	}

}
