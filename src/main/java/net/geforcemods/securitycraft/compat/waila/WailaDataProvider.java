package net.geforcemods.securitycraft.compat.waila;

import java.util.List;

import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.INameable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.entity.SentryEntity;
import net.geforcemods.securitycraft.misc.CustomModules;
import net.geforcemods.securitycraft.tileentity.KeycardReaderTileEntity;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

@WailaPlugin(SecurityCraft.MODID)
public class WailaDataProvider implements IWailaPlugin, IComponentProvider, IEntityComponentProvider {
	public static final WailaDataProvider INSTANCE = new WailaDataProvider();
	public static final ResourceLocation SHOW_OWNER = new ResourceLocation(SecurityCraft.MODID, "showowner");
	public static final ResourceLocation SHOW_MODULES = new ResourceLocation(SecurityCraft.MODID, "showmodules");
	public static final ResourceLocation SHOW_PASSWORDS = new ResourceLocation(SecurityCraft.MODID, "showpasswords");
	public static final ResourceLocation SHOW_CUSTOM_NAME = new ResourceLocation(SecurityCraft.MODID, "showcustomname");

	@Override
	public void register(IRegistrar registrar)
	{
		registrar.addConfig(SHOW_OWNER, true);
		registrar.addConfig(SHOW_MODULES, true);
		registrar.addConfig(SHOW_PASSWORDS, true);
		registrar.addConfig(SHOW_CUSTOM_NAME, true);
		registrar.registerComponentProvider((IComponentProvider) INSTANCE, TooltipPosition.BODY, IOwnable.class);
		registrar.registerStackProvider(INSTANCE, IOverlayDisplay.class);
		registrar.registerComponentProvider((IEntityComponentProvider) INSTANCE, TooltipPosition.BODY, SentryEntity.class);
	}

	@Override
	public ItemStack getStack(IDataAccessor data, IPluginConfig config) {
		if(data.getBlock() instanceof IOverlayDisplay)
			return ((IOverlayDisplay) data.getBlock()).getDisplayStack(data.getWorld(), data.getBlockState(), data.getPosition());

		return ItemStack.EMPTY;
	}

	@Override
	public void appendBody(List<ITextComponent> body, IDataAccessor data, IPluginConfig config) {
		Block block = data.getBlock();
		boolean disguised = false;

		if(block instanceof DisguisableBlock)
		{
			BlockState disguisedBlockState = ((DisguisableBlock)block).getDisguisedBlockState(data.getWorld(), data.getPosition());

			if(disguisedBlockState != null)
			{
				disguised = true;
				block = disguisedBlockState.getBlock();
			}
		}

		if(block instanceof IOverlayDisplay && !((IOverlayDisplay) block).shouldShowSCInfo(data.getWorld(), data.getBlockState(), data.getPosition())) return;

		TileEntity te = data.getTileEntity();

		//last part is a little cheaty to prevent owner info from being displayed on non-sc blocks
		if(config.get(SHOW_OWNER) && te instanceof IOwnable && block.getRegistryName().getNamespace().equals(SecurityCraft.MODID))
			body.add(new StringTextComponent(ClientUtils.localize("waila.securitycraft:owner") + " " + ((IOwnable) te).getOwner().getName()));

		if(disguised)
			return;

		//if the te is ownable, show modules only when it's owned, otherwise always show
		if(config.get(SHOW_MODULES) && te instanceof IModuleInventory && (!(te instanceof IOwnable) || ((IOwnable)te).getOwner().isOwner(data.getPlayer()))){
			if(!((IModuleInventory) te).getInsertedModules().isEmpty())
				body.add(new StringTextComponent(ClientUtils.localize("waila.securitycraft:equipped")));

			for(CustomModules module : ((IModuleInventory) te).getInsertedModules())
				body.add(new StringTextComponent("- " + module.getName()));
		}

		if(config.get(SHOW_PASSWORDS) && te instanceof IPasswordProtected && !(te instanceof KeycardReaderTileEntity) && ((IOwnable) te).getOwner().isOwner(data.getPlayer())){
			String password = ((IPasswordProtected) te).getPassword();

			body.add(new StringTextComponent(ClientUtils.localize("waila.securitycraft:password") + " " + (password != null && !password.isEmpty() ? password : ClientUtils.localize("waila.securitycraft:password.notSet"))));
		}

		if(config.get(SHOW_CUSTOM_NAME) && te instanceof INameable && ((INameable) te).canBeNamed()){
			ITextComponent text = ((INameable) te).getCustomSCName();
			String name = text == null ? "" : text.getFormattedText();

			body.add(new StringTextComponent(ClientUtils.localize("waila.securitycraft:customName") + " " + (((INameable) te).hasCustomSCName() ? name : ClientUtils.localize("waila.securitycraft:customName.notSet"))));
		}
	}

	@Override
	public void appendBody(List<ITextComponent> body, IEntityAccessor data, IPluginConfig config) {
		Entity entity = data.getEntity();

		if(config.get(SHOW_OWNER) && data.getEntity() instanceof SentryEntity) {
			body.add(new StringTextComponent(ClientUtils.localize("waila.securitycraft:owner") + " " + ((SentryEntity) entity).getOwner().getName()));
		}

		if(config.get(SHOW_MODULES) && entity instanceof SentryEntity && ((SentryEntity) entity).getOwner().isOwner(data.getPlayer())){
			SentryEntity sentry = (SentryEntity)entity;

			if(!sentry.getWhitelistModule().isEmpty() || !sentry.getDisguiseModule().isEmpty())
			{
				body.add(new StringTextComponent(ClientUtils.localize("waila.securitycraft:equipped")));

				if (!sentry.getWhitelistModule().isEmpty())
					body.add(new StringTextComponent("- " + CustomModules.WHITELIST.getName()));

				if (!sentry.getDisguiseModule().isEmpty())
					body.add(new StringTextComponent("- " + CustomModules.DISGUISE.getName()));
			}
		}

		if (entity instanceof SentryEntity)
		{
			SentryEntity sentry = (SentryEntity)entity;
			SentryEntity.SentryMode mode = sentry.getMode();

			if (mode == SentryEntity.SentryMode.AGGRESSIVE)
				body.add(new StringTextComponent(ClientUtils.localize("messages.securitycraft:sentry.mode1")));
			else if (mode == SentryEntity.SentryMode.CAMOUFLAGE)
				body.add(new StringTextComponent(ClientUtils.localize("messages.securitycraft:sentry.mode2")));
			else
				body.add(new StringTextComponent(ClientUtils.localize("messages.securitycraft:sentry.mode3")));
		}
	}
}
