package net.geforcemods.securitycraft.compat.waila;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.INameable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.blocks.BlockDisguisable;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.entity.EntitySentry;
import net.geforcemods.securitycraft.entity.EntitySentry.EnumSentryMode;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.tileentity.TileEntityKeycardReader;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WailaDataProvider implements IWailaDataProvider, IWailaEntityProvider {

	private static final String SHOW_OWNER = "securitycraft.showowner";
	private static final String SHOW_MODULES = "securitycraft.showmodules";
	private static final String SHOW_PASSWORDS = "securitycraft.showpasswords";
	private static final String SHOW_CUSTOM_NAME = "securitycraft.showcustomname";

	public static void callbackRegister(IWailaRegistrar registrar){
		registrar.addConfig("SecurityCraft", SHOW_OWNER, ClientUtils.localize("waila.securitycraft:displayOwner").getFormattedText());
		registrar.addConfig("SecurityCraft", SHOW_MODULES, ClientUtils.localize("waila.securitycraft:showModules").getFormattedText());
		registrar.addConfig("SecurityCraft", SHOW_PASSWORDS, ClientUtils.localize("waila.securitycraft:showPasswords").getFormattedText());
		registrar.addConfig("SecurityCraft", SHOW_CUSTOM_NAME, ClientUtils.localize("waila.securitycraft:showCustomName").getFormattedText());
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
		World world = data.getWorld();
		BlockPos pos = data.getPosition();
		IBlockState state = data.getBlockState();
		Block block = data.getBlock();
		boolean disguised = false;

		if(block instanceof BlockDisguisable)
		{
			IBlockState disguisedBlockState = ((BlockDisguisable)block).getDisguisedBlockState(world, pos);

			if(disguisedBlockState != null)
			{
				disguised = true;
				block = disguisedBlockState.getBlock();
			}
		}

		if(block instanceof IOverlayDisplay && !((IOverlayDisplay) block).shouldShowSCInfo(world, state, pos))
			return body;

		TileEntity te = data.getTileEntity();

		//last part is a little cheaty to prevent owner info from being displayed on non-sc blocks
		if(config.getConfig(SHOW_OWNER) && te instanceof IOwnable && block.getRegistryName().getNamespace().equals(SecurityCraft.MODID))
			body.add(ClientUtils.localize("waila.securitycraft:owner").getFormattedText() + " " + ((IOwnable) te).getOwner().getName());

		if(!disguised)
		{
			//if the te is ownable, show modules only when it's owned, otherwise always show
			if(config.getConfig(SHOW_MODULES) && te instanceof IModuleInventory && (!(te instanceof IOwnable) || ((IOwnable)te).getOwner().isOwner(data.getPlayer()))){
				if(!((IModuleInventory) te).getInsertedModules().isEmpty())
					body.add(ClientUtils.localize("waila.securitycraft:equipped").getFormattedText());

				for(EnumModuleType module : ((IModuleInventory) te).getInsertedModules())
					body.add("- " + ClientUtils.localize(module.getTranslationKey()).getFormattedText());
			}

			if(config.getConfig(SHOW_PASSWORDS) && te instanceof IPasswordProtected && !(te instanceof TileEntityKeycardReader) && ((IOwnable) te).getOwner().isOwner(data.getPlayer())){
				String password = ((IPasswordProtected) te).getPassword();

				body.add(ClientUtils.localize("waila.securitycraft:password").getFormattedText() + " " + (password != null && !password.isEmpty() ? password : ClientUtils.localize("waila.securitycraft:password.notSet").getFormattedText()));
			}

			if(config.getConfig(SHOW_CUSTOM_NAME) && te instanceof INameable && ((INameable) te).canBeNamed()){
				String name = ((INameable) te).getCustomName();

				body.add(ClientUtils.localize("waila.securitycraft:customName").getFormattedText() + " " + (((INameable) te).hasCustomName() ? name : ClientUtils.localize("waila.securitycraft:customName.notSet").getFormattedText()));
			}
		}

		return body;
	}

	@Override
	public List<String> getWailaBody(Entity entity, List<String> body, IWailaEntityAccessor data, IWailaConfigHandler config)
	{
		if(entity instanceof EntitySentry)
		{
			EntitySentry sentry = (EntitySentry)entity;
			EnumSentryMode mode = sentry.getMode();

			if(config.getConfig(SHOW_OWNER))
				body.add(ClientUtils.localize("waila.securitycraft:owner").getFormattedText() + " " + sentry.getOwner().getName());

			if(config.getConfig(SHOW_MODULES) && sentry.getOwner().isOwner(data.getPlayer()))
			{

				if(!sentry.getWhitelistModule().isEmpty() || !sentry.getDisguiseModule().isEmpty())
				{
					body.add(ClientUtils.localize("waila.securitycraft:equipped").getFormattedText());

					if (!sentry.getWhitelistModule().isEmpty())
						body.add("- " + ClientUtils.localize(EnumModuleType.WHITELIST.getTranslationKey()).getFormattedText());

					if (!sentry.getDisguiseModule().isEmpty())
						body.add("- " + ClientUtils.localize(EnumModuleType.DISGUISE.getTranslationKey()).getFormattedText());
				}
			}

			String modeDescription = ClientUtils.localize(mode.getModeKey()).getFormattedText();

			if(mode != EnumSentryMode.IDLE)
				modeDescription += " - " + ClientUtils.localize(mode.getTargetKey()).getFormattedText();

			body.add(modeDescription);
		}

		return body;
	}
}
