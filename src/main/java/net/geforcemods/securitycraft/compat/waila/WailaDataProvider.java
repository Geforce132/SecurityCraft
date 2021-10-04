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
import net.geforcemods.securitycraft.entity.SentryEntity.SentryMode;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.KeycardReaderTileEntity;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

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
		registrar.addSyncedConfig(SHOW_OWNER, true);
		registrar.addSyncedConfig(SHOW_MODULES, true);
		registrar.addSyncedConfig(SHOW_PASSWORDS, true);
		registrar.addSyncedConfig(SHOW_CUSTOM_NAME, true);
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
			body.add(new StringTextComponent(Utils.localize("waila.securitycraft:owner", PlayerUtils.getOwnerComponent(((IOwnable) te).getOwner().getName())).getFormattedText()));

		if(disguised)
			return;

		//if the te is ownable, show modules only when it's owned, otherwise always show
		if(config.get(SHOW_MODULES) && te instanceof IModuleInventory && (!(te instanceof IOwnable) || ((IOwnable)te).getOwner().isOwner(data.getPlayer()))){
			if(!((IModuleInventory) te).getInsertedModules().isEmpty())
				body.add(Utils.localize("waila.securitycraft:equipped"));

			for(ModuleType module : ((IModuleInventory) te).getInsertedModules())
				body.add(new StringTextComponent("- " + new TranslationTextComponent(module.getTranslationKey()).getFormattedText()));
		}

		if(config.get(SHOW_PASSWORDS) && te instanceof IPasswordProtected && !(te instanceof KeycardReaderTileEntity) && ((IOwnable) te).getOwner().isOwner(data.getPlayer())){
			String password = ((IPasswordProtected) te).getPassword();

			body.add(new StringTextComponent(Utils.localize("waila.securitycraft:password").getFormattedText() + " " + (password != null && !password.isEmpty() ? password : Utils.localize("waila.securitycraft:password.notSet").getFormattedText())));
		}

		if(config.get(SHOW_CUSTOM_NAME) && te instanceof INameable && ((INameable) te).canBeNamed()){
			ITextComponent text = ((INameable) te).getCustomSCName();
			String name = text == null ? "" : text.getFormattedText();

			body.add(new StringTextComponent(Utils.localize("waila.securitycraft:customName").getFormattedText() + " " + (((INameable) te).hasCustomSCName() ? name : Utils.localize("waila.securitycraft:customName.notSet").getFormattedText())));
		}
	}

	@Override
	public void appendBody(List<ITextComponent> body, IEntityAccessor data, IPluginConfig config) {
		Entity entity = data.getEntity();

		if(entity instanceof SentryEntity)
		{
			SentryEntity sentry = (SentryEntity)entity;
			SentryMode mode = sentry.getMode();

			if(config.get(SHOW_OWNER))
				body.add(new StringTextComponent(Utils.localize("waila.securitycraft:owner", PlayerUtils.getOwnerComponent(sentry.getOwner().getName())).getFormattedText()));

			if(config.get(SHOW_MODULES) && sentry.getOwner().isOwner(data.getPlayer()))
			{
				if(!sentry.getAllowlistModule().isEmpty() || !sentry.getDisguiseModule().isEmpty() || sentry.hasSpeedModule())
				{
					body.add(new StringTextComponent(Utils.localize("waila.securitycraft:equipped").getFormattedText()));

					if (!sentry.getAllowlistModule().isEmpty())
						body.add(new StringTextComponent("- " + new TranslationTextComponent(ModuleType.ALLOWLIST.getTranslationKey()).getFormattedText()));

					if (!sentry.getDisguiseModule().isEmpty())
						body.add(new StringTextComponent("- " + new TranslationTextComponent(ModuleType.DISGUISE.getTranslationKey()).getFormattedText()));

					if(sentry.hasSpeedModule())
						body.add(new StringTextComponent("- " + new TranslationTextComponent(ModuleType.SPEED.getTranslationKey()).getFormattedText()));
				}
			}

			String modeDescription = Utils.localize(mode.getModeKey()).getFormattedText();

			if(mode != SentryMode.IDLE)
				modeDescription += "- " + Utils.localize(mode.getTargetKey()).getFormattedText();

			body.add(new StringTextComponent(TextFormatting.GRAY + modeDescription));
		}
	}
}
