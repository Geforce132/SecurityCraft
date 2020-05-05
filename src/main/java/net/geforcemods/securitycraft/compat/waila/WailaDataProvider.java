package net.geforcemods.securitycraft.compat.waila;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.INameable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.blocks.BlockDisguisable;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.entity.EntitySentry;
import net.geforcemods.securitycraft.entity.EntitySentry.EnumSentryMode;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntityKeycardReader;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

public class WailaDataProvider implements IWailaDataProvider, IWailaEntityProvider {

	private static final String SHOW_OWNER = "securitycraft.showowner";
	private static final String SHOW_MODULES = "securitycraft.showmodules";
	private static final String SHOW_PASSWORDS = "securitycraft.showpasswords";
	private static final String SHOW_CUSTOM_NAME = "securitycraft.showcustomname";

	public static void callbackRegister(IWailaRegistrar registrar){
		registrar.addConfig("SecurityCraft", SHOW_OWNER, ClientUtils.localize("waila.securitycraft:displayOwner"));
		registrar.addConfig("SecurityCraft", SHOW_MODULES, ClientUtils.localize("waila.securitycraft:showModules"));
		registrar.addConfig("SecurityCraft", SHOW_PASSWORDS, ClientUtils.localize("waila.securitycraft:showPasswords"));
		registrar.addConfig("SecurityCraft", SHOW_CUSTOM_NAME, ClientUtils.localize("waila.securitycraft:showCustomName"));
		registrar.registerBodyProvider((IWailaDataProvider)new WailaDataProvider(), IOwnable.class);
		registrar.registerStackProvider(new WailaDataProvider(), IOverlayDisplay.class);
		registrar.registerBodyProvider((IWailaEntityProvider)new WailaDataProvider(), EntitySentry.class);
	}

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor data, IWailaConfigHandler config) {
		if(data.getBlock() instanceof IOverlayDisplay)
			return ((IOverlayDisplay) data.getBlock()).getDisplayStack(data.getWorld(), data.getBlockState(), data.getPosition());

		return ItemStack.EMPTY;
	}

	@Override
	public List<String> getWailaBody(ItemStack stack, List<String> body, IWailaDataAccessor data, IWailaConfigHandler config) {
		Block block = data.getBlock();
		boolean disguised = false;

		if(block instanceof BlockDisguisable)
		{
			IBlockState disguisedBlockState = ((BlockDisguisable)block).getDisguisedBlockState(data.getWorld(), data.getPosition());

			if(disguisedBlockState != null)
			{
				disguised = true;
				block = disguisedBlockState.getBlock();
			}
		}

		if(block instanceof IOverlayDisplay && !((IOverlayDisplay) block).shouldShowSCInfo(data.getWorld(), data.getBlockState(), data.getPosition())) return body;

		//last part is a little cheaty to prevent owner info from being displayed on non-sc blocks
		if(config.getConfig(SHOW_OWNER) && data.getTileEntity() instanceof IOwnable && block.getRegistryName().getNamespace().equals(SecurityCraft.MODID))
			body.add(ClientUtils.localize("waila.securitycraft:owner") + " " + ((IOwnable) data.getTileEntity()).getOwner().getName());

		if(!disguised)
		{
			if(config.getConfig(SHOW_MODULES) && data.getTileEntity() instanceof CustomizableSCTE && ((CustomizableSCTE) data.getTileEntity()).getOwner().isOwner(data.getPlayer())){
				if(!((CustomizableSCTE) data.getTileEntity()).getModules().isEmpty())
					body.add(ClientUtils.localize("waila.securitycraft:equipped"));

				for(EnumCustomModules module : ((CustomizableSCTE) data.getTileEntity()).getModules())
					body.add("- " + ClientUtils.localize(module.getTranslationKey()));
			}

			if(config.getConfig(SHOW_PASSWORDS) && data.getTileEntity() instanceof IPasswordProtected && !(data.getTileEntity() instanceof TileEntityKeycardReader) && ((IOwnable) data.getTileEntity()).getOwner().isOwner(data.getPlayer())){
				String password = ((IPasswordProtected) data.getTileEntity()).getPassword();

				body.add(ClientUtils.localize("waila.securitycraft:password") + " " + (password != null && !password.isEmpty() ? password : ClientUtils.localize("waila.securitycraft:password.notSet")));
			}

			if(config.getConfig(SHOW_CUSTOM_NAME) && data.getTileEntity() instanceof INameable && ((INameable) data.getTileEntity()).canBeNamed()){
				String name = ((INameable) data.getTileEntity()).getCustomName();

				body.add(ClientUtils.localize("waila.securitycraft:customName") + " " + (((INameable) data.getTileEntity()).hasCustomName() ? name : ClientUtils.localize("waila.securitycraft:customName.notSet")));
			}
		}

		return body;
	}

	@Override
	public List<String> getWailaBody(Entity entity, List<String> body, IWailaEntityAccessor data, IWailaConfigHandler config)
	{
		if(config.getConfig(SHOW_OWNER) && data.getEntity() instanceof EntitySentry)
			body.add(ClientUtils.localize("waila.securitycraft:owner") + " " + ((EntitySentry) entity).getOwner().getName());

		if(config.getConfig(SHOW_MODULES) && entity instanceof EntitySentry && ((EntitySentry) entity).getOwner().isOwner(data.getPlayer()))
		{
			EntitySentry sentry = (EntitySentry)entity;

			if(!sentry.getWhitelistModule().isEmpty() || !sentry.getDisguiseModule().isEmpty())
			{
				body.add(ClientUtils.localize("waila.securitycraft:equipped"));

				if (!sentry.getWhitelistModule().isEmpty())
					body.add("- " + ClientUtils.localize(EnumCustomModules.WHITELIST.getTranslationKey()));

				if (!sentry.getDisguiseModule().isEmpty())
					body.add("- " + ClientUtils.localize(EnumCustomModules.DISGUISE.getTranslationKey()));
			}
		}

		if (entity instanceof EntitySentry)
		{
			EntitySentry sentry = (EntitySentry)entity;
			EnumSentryMode mode = sentry.getMode();

			if (mode == EnumSentryMode.AGGRESSIVE)
				body.add(ClientUtils.localize("messages.securitycraft:sentry.mode1"));
			else if (mode == EnumSentryMode.CAMOUFLAGE)
				body.add(ClientUtils.localize("messages.securitycraft:sentry.mode2"));
			else
				body.add(ClientUtils.localize("messages.securitycraft:sentry.mode3"));
		}

		return body;
	}
}
