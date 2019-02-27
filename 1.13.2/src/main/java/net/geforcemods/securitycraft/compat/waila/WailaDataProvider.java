package net.geforcemods.securitycraft.compat.waila;

import java.util.List;

import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.INameable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntityKeycardReader;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

@WailaPlugin(SecurityCraft.MODID)
public class WailaDataProvider implements IWailaPlugin, IComponentProvider {
	public static final WailaDataProvider INSTANCE = new WailaDataProvider();
	public static final ResourceLocation SHOW_OWNER = new ResourceLocation(SecurityCraft.MODID, "showowner");
	public static final ResourceLocation SHOW_MODULES = new ResourceLocation(SecurityCraft.MODID, "showmodules");
	public static final ResourceLocation SHOW_PASSWORDS = new ResourceLocation(SecurityCraft.MODID, "showpasswords");
	public static final ResourceLocation SHOW_CUSTOM_NAME = new ResourceLocation(SecurityCraft.MODID, "showcustomname");

	@Override
	public void register(IRegistrar registrar)
	{
		SecurityCraft.log("Adding Waila support!");
		registrar.addConfig(SHOW_OWNER, true);
		registrar.addConfig(SHOW_MODULES, true);
		registrar.addConfig(SHOW_PASSWORDS, true);
		registrar.addConfig(SHOW_CUSTOM_NAME, true);
		registrar.registerComponentProvider(INSTANCE, TooltipPosition.BODY, IOwnable.class);
		registrar.registerStackProvider(INSTANCE, ICustomWailaDisplay.class);
	}

	@Override
	public ItemStack getStack(IDataAccessor data, IPluginConfig config) {
		if(data.getBlock() instanceof ICustomWailaDisplay)
			return ((ICustomWailaDisplay) data.getBlock()).getDisplayStack(data.getWorld(), data.getBlockState(), data.getPosition());

		return ItemStack.EMPTY;
	}

	@Override
	public void appendBody(List<ITextComponent> body, IDataAccessor data, IPluginConfig config) {
		if(data.getBlock() instanceof ICustomWailaDisplay && !((ICustomWailaDisplay) data.getBlock()).shouldShowSCInfo(data.getWorld(), data.getBlockState(), data.getPosition())) return;

		if(config.get(SHOW_OWNER) && data.getTileEntity() instanceof IOwnable)
			body.add(new TextComponentString(ClientUtils.localize("waila.securitycraft:owner") + " " + ((IOwnable) data.getTileEntity()).getOwner().getName()));

		if(config.get(SHOW_MODULES) && data.getTileEntity() instanceof CustomizableSCTE && ((CustomizableSCTE) data.getTileEntity()).getOwner().isOwner(data.getPlayer())){
			if(!((CustomizableSCTE) data.getTileEntity()).getModules().isEmpty())
				body.add(new TextComponentString(ClientUtils.localize("waila.securitycraft:equipped")));

			for(EnumCustomModules module : ((CustomizableSCTE) data.getTileEntity()).getModules())
				body.add(new TextComponentString("- " + module.getName()));
		}

		if(config.get(SHOW_PASSWORDS) && data.getTileEntity() instanceof IPasswordProtected && !(data.getTileEntity() instanceof TileEntityKeycardReader) && ((IOwnable) data.getTileEntity()).getOwner().isOwner(data.getPlayer())){
			String password = ((IPasswordProtected) data.getTileEntity()).getPassword();

			body.add(new TextComponentString(ClientUtils.localize("waila.securitycraft:password") + " " + (password != null && !password.isEmpty() ? password : ClientUtils.localize("waila.securitycraft:password.notSet"))));
		}

		if(config.get(SHOW_CUSTOM_NAME) && data.getTileEntity() instanceof INameable && ((INameable) data.getTileEntity()).canBeNamed()){
			String name = ((INameable) data.getTileEntity()).getCustomName().getFormattedText();

			body.add(new TextComponentString(ClientUtils.localize("waila.securitycraft:customName") + " " + (((INameable) data.getTileEntity()).hasCustomName() ? name : ClientUtils.localize("waila.securitycraft:customName.notSet"))));
		}
	}
}
