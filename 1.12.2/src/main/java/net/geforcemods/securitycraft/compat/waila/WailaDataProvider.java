package net.geforcemods.securitycraft.compat.waila;

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
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntityKeycardReader;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WailaDataProvider implements IWailaDataProvider {

	public static void callbackRegister(IWailaRegistrar registrar){
		SecurityCraft.log("Adding Waila support!");

		registrar.addConfig("SecurityCraft", "securitycraft.showowner", ClientUtils.localize("waila.securitycraft:displayOwner"));
		registrar.addConfig("SecurityCraft", "securitycraft.showmodules", ClientUtils.localize("waila.securitycraft:showModules"));
		registrar.addConfig("SecurityCraft", "securitycraft.showpasswords", ClientUtils.localize("waila.securitycraft:showPasswords"));
		registrar.addConfig("SecurityCraft", "securitycraft.showcustomname", ClientUtils.localize("waila.securitycraft:showCustomName"));
		registrar.registerBodyProvider(new WailaDataProvider(), IOwnable.class);
		registrar.registerStackProvider(new WailaDataProvider(), IOverlayDisplay.class);
	}

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor data, IWailaConfigHandler config) {
		if(data.getBlock() instanceof IOverlayDisplay)
			return ((IOverlayDisplay) data.getBlock()).getDisplayStack(data.getWorld(), data.getBlockState(), data.getPosition());

		return ItemStack.EMPTY;
	}

	@Override
	public List<String> getWailaHead(ItemStack stack, List<String> head, IWailaDataAccessor data, IWailaConfigHandler config) {
		return head;
	}

	@Override
	public List<String> getWailaBody(ItemStack stack, List<String> body, IWailaDataAccessor data, IWailaConfigHandler config) {
		if(data.getBlock() instanceof IOverlayDisplay && !((IOverlayDisplay) data.getBlock()).shouldShowSCInfo(data.getWorld(), data.getBlockState(), data.getPosition())) return body;

		if(config.getConfig("securitycraft.showowner") && data.getTileEntity() instanceof IOwnable)
			body.add(ClientUtils.localize("waila.securitycraft:owner") + " " + ((IOwnable) data.getTileEntity()).getOwner().getName());

		if(config.getConfig("securitycraft.showmodules") && data.getTileEntity() instanceof CustomizableSCTE && ((CustomizableSCTE) data.getTileEntity()).getOwner().isOwner(data.getPlayer())){
			if(!((CustomizableSCTE) data.getTileEntity()).getModules().isEmpty())
				body.add(ClientUtils.localize("waila.securitycraft:equipped"));

			for(EnumCustomModules module : ((CustomizableSCTE) data.getTileEntity()).getModules())
				body.add("- " + module.getName());
		}

		if(config.getConfig("securitycraft.showpasswords") && data.getTileEntity() instanceof IPasswordProtected && !(data.getTileEntity() instanceof TileEntityKeycardReader) && ((IOwnable) data.getTileEntity()).getOwner().isOwner(data.getPlayer())){
			String password = ((IPasswordProtected) data.getTileEntity()).getPassword();

			body.add(ClientUtils.localize("waila.securitycraft:password") + " " + (password != null && !password.isEmpty() ? password : ClientUtils.localize("waila.securitycraft:password.notSet")));
		}

		if(config.getConfig("securitycraft.showcustomname") && data.getTileEntity() instanceof INameable && ((INameable) data.getTileEntity()).canBeNamed()){
			String name = ((INameable) data.getTileEntity()).getCustomName();

			body.add(ClientUtils.localize("waila.securitycraft:customName") + " " + (((INameable) data.getTileEntity()).hasCustomName() ? name : ClientUtils.localize("waila.securitycraft:customName.notSet")));
		}

		return body;
	}

	@Override
	public List<String> getWailaTail(ItemStack stack, List<String> tail, IWailaDataAccessor data, IWailaConfigHandler config) {
		return tail;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity tileEntity, NBTTagCompound tag, World world, BlockPos pos) {
		return tag;
	}

}
