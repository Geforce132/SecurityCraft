package net.geforcemods.securitycraft.compat.hudmods;

import java.awt.Rectangle;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IEventListener;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.api.component.ItemComponent;
import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

public final class WTHITDataProvider extends HudModHandler implements IWailaPlugin, IBlockComponentProvider, IEntityComponentProvider, IEventListener {
	@Override
	public void register(IRegistrar registrar) {
		registrar.addEventListener(this);
		registrar.addSyncedConfig(SHOW_OWNER, true, false);
		registrar.addSyncedConfig(SHOW_MODULES, true, false);
		registrar.addSyncedConfig(SHOW_CUSTOM_NAME, true, false);
		registrar.addComponent((IBlockComponentProvider) this, TooltipPosition.HEAD, IOverlayDisplay.class);
		registrar.addComponent((IBlockComponentProvider) this, TooltipPosition.BODY, IOwnable.class);
		registrar.addComponent((IBlockComponentProvider) this, TooltipPosition.TAIL, IOverlayDisplay.class);
		registrar.addIcon((IBlockComponentProvider) this, IOverlayDisplay.class);
		registrar.addComponent((IEntityComponentProvider) this, TooltipPosition.BODY, Sentry.class);
	}

	@Override
	public ITooltipComponent getIcon(IBlockAccessor data, IPluginConfig config) {
		ItemStack displayStack = ((IOverlayDisplay) data.getBlock()).getDisplayStack(data.getWorld(), data.getBlockState(), data.getPosition());

		if (displayStack != null)
			return new ItemComponent(displayStack);
		else
			return IBlockComponentProvider.super.getIcon(data, config);
	}

	@Override
	public void appendHead(ITooltip tooltip, IBlockAccessor data, IPluginConfig config) {
		ItemStack displayStack = ((IOverlayDisplay) data.getBlock()).getDisplayStack(data.getWorld(), data.getBlockState(), data.getPosition());

		if (displayStack != null)
			tooltip.setLine(WailaConstants.OBJECT_NAME_TAG, Component.translatable(displayStack.getDescriptionId()).setStyle(ITEM_NAME_STYLE));
	}

	@Override
	public void appendBody(ITooltip tooltip, IBlockAccessor data, IPluginConfig config) {
		addOwnerModuleNameInfo(data.getWorld(), data.getPosition(), data.getBlockState(), data.getBlock(), data.getBlockEntity(), data.getPlayer(), tooltip::addLine, config::getBoolean);
	}

	@Override
	public void appendTail(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
		ItemStack disguisedAs = ((IOverlayDisplay) accessor.getBlock()).getDisplayStack(accessor.getWorld(), accessor.getBlockState(), accessor.getPosition());

		if (disguisedAs != null)
			tooltip.setLine(WailaConstants.MOD_NAME_TAG, Component.literal(ModList.get().getModContainerById(Utils.getRegistryName(disguisedAs.getItem()).getNamespace()).get().getModInfo().getDisplayName()).setStyle(MOD_NAME_STYLE));
	}

	@Override
	public void appendBody(ITooltip tooltip, IEntityAccessor data, IPluginConfig config) {
		addEntityInfo(data.getEntity(), data.getPlayer(), tooltip::addLine, config::getBoolean);
	}

	@Override
	public void onBeforeTooltipRender(GuiGraphics guiGraphics, Rectangle rectangle, ICommonAccessor accessor, IPluginConfig config, Canceller canceller) {
		if (ClientHandler.isPlayerMountedOnCamera())
			canceller.cancel();
	}
}
