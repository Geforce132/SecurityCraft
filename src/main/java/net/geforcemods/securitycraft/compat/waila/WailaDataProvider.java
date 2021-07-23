package net.geforcemods.securitycraft.compat.waila;

import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.EntityAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import mcp.mobius.waila.api.config.IPluginConfig;
import mcp.mobius.waila.api.ui.IElement;
import mcp.mobius.waila.impl.ui.ItemStackElement;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.INameable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.blocks.BlockPocketWallBlock;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.blocks.mines.BaseFullMineBlock;
import net.geforcemods.securitycraft.blocks.mines.FurnaceMineBlock;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.entity.SentryEntity;
import net.geforcemods.securitycraft.entity.SentryEntity.SentryMode;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@WailaPlugin(SecurityCraft.MODID)
public class WailaDataProvider implements IWailaPlugin, IComponentProvider, IEntityComponentProvider
{
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
		//TODO: registrar.registerComponentProvider(INSTANCE, TooltipPosition.BODY, IOwnable.class);
		//TODO: check if all sub classes of these work as well
		registrar.registerIconProvider(INSTANCE, BaseFullMineBlock.class);
		registrar.registerIconProvider(INSTANCE, BlockPocketWallBlock.class);
		registrar.registerIconProvider(INSTANCE, DisguisableBlock.class);
		registrar.registerIconProvider(INSTANCE, FurnaceMineBlock.class);
		registrar.registerComponentProvider(INSTANCE, TooltipPosition.BODY, SentryEntity.class);
	}

	@Override
	public IElement getIcon(BlockAccessor data, IPluginConfig config, IElement currentIcon) {
		if(data.getBlock() instanceof IOverlayDisplay display)
			return ItemStackElement.of(display.getDisplayStack(data.getLevel(), data.getBlockState(), data.getPosition()));

		return ItemStackElement.EMPTY;
	}

	@Override
	public void appendTooltip(ITooltip tooltip, BlockAccessor data, IPluginConfig config) {
		if(data.getTooltipPosition() == TooltipPosition.BODY)
		{
			Block block = data.getBlock();
			boolean disguised = false;

			if(block instanceof DisguisableBlock disguisedBlock)
			{
				BlockState disguisedBlockState = disguisedBlock.getDisguisedBlockState(data.getLevel(), data.getPosition());

				if(disguisedBlockState != null)
				{
					disguised = true;
					block = disguisedBlockState.getBlock();
				}
			}

			if(block instanceof IOverlayDisplay display && !display.shouldShowSCInfo(data.getLevel(), data.getBlockState(), data.getPosition()))
				return;

			BlockEntity te = data.getBlockEntity();

			//last part is a little cheaty to prevent owner info from being displayed on non-sc blocks
			if(config.get(SHOW_OWNER) && te instanceof IOwnable ownable && block.getRegistryName().getNamespace().equals(SecurityCraft.MODID))
				tooltip.add(Utils.localize("waila.securitycraft:owner", ownable.getOwner().getName()));

			if(disguised)
				return;

			//if the te is ownable, show modules only when it's owned, otherwise always show
			if(config.get(SHOW_MODULES) && te instanceof IModuleInventory moduleInv && (!(te instanceof IOwnable ownable) || ownable.getOwner().isOwner(data.getPlayer()))){
				if(!moduleInv.getInsertedModules().isEmpty())
					tooltip.add(Utils.localize("waila.securitycraft:equipped"));

				for(ModuleType module : moduleInv.getInsertedModules())
					tooltip.add(new TextComponent("- ").append(new TranslatableComponent(module.getTranslationKey())));
			}

			if(config.get(SHOW_PASSWORDS) && te instanceof IPasswordProtected ipp && ((IOwnable) te).getOwner().isOwner(data.getPlayer())){
				String password = ipp.getPassword();

				tooltip.add(Utils.localize("waila.securitycraft:password", (password != null && !password.isEmpty() ? password : Utils.localize("waila.securitycraft:password.notSet"))));
			}

			if(config.get(SHOW_CUSTOM_NAME) && te instanceof INameable nameable && nameable.canBeNamed()){
				Component text = nameable.getCustomSCName();

				tooltip.add(Utils.localize("waila.securitycraft:customName", nameable.hasCustomSCName() ? text : Utils.localize("waila.securitycraft:customName.notSet")));
			}
		}
	}

	@Override
	public void appendTooltip(ITooltip tooltip, EntityAccessor data, IPluginConfig config) {
		if(data.getTooltipPosition() == TooltipPosition.BODY)
		{
			Entity entity = data.getEntity();

			if(entity instanceof SentryEntity sentry)
			{
				SentryMode mode = sentry.getMode();

				if(config.get(SHOW_OWNER))
					tooltip.add(Utils.localize("waila.securitycraft:owner", sentry.getOwner().getName()));

				if(config.get(SHOW_MODULES) && sentry.getOwner().isOwner(data.getPlayer())){

					if(!sentry.getAllowlistModule().isEmpty() || !sentry.getDisguiseModule().isEmpty() || sentry.hasSpeedModule())
					{
						tooltip.add(Utils.localize("waila.securitycraft:equipped"));

						if(!sentry.getAllowlistModule().isEmpty())
							tooltip.add(new TextComponent("- ").append(new TranslatableComponent(ModuleType.ALLOWLIST.getTranslationKey())));

						if(!sentry.getDisguiseModule().isEmpty())
							tooltip.add(new TextComponent("- ").append(new TranslatableComponent(ModuleType.DISGUISE.getTranslationKey())));

						if(sentry.hasSpeedModule())
							tooltip.add(new TextComponent("- ").append(new TranslatableComponent(ModuleType.SPEED.getTranslationKey())));
					}
				}

				MutableComponent modeDescription = Utils.localize(mode.getModeKey());

				if(mode != SentryMode.IDLE)
					modeDescription.append("- ").append(Utils.localize(mode.getTargetKey()));

				tooltip.add(modeDescription);
			}
		}
	}
}
