package net.geforcemods.securitycraft.compat.hudmods;

import java.awt.Rectangle;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IClientRegistrar;
import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IEventListener;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.IWailaClientPlugin;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.api.component.ItemComponent;
import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.entity.AbstractSecuritySeaBoat;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

public final class WTHITClientDataProvider extends HudModHandler implements IWailaClientPlugin, IBlockComponentProvider, IEntityComponentProvider, IEventListener {
	@Override
	public void register(IClientRegistrar registrar) {
		registrar.eventListener(this);
		registrar.head((IBlockComponentProvider) this, IOverlayDisplay.class);
		registrar.body((IBlockComponentProvider) this, IOwnable.class);
		registrar.tail((IBlockComponentProvider) this, IOverlayDisplay.class);
		registrar.icon((IBlockComponentProvider) this, IOverlayDisplay.class);
		registrar.body((IEntityComponentProvider) this, Sentry.class);
		registrar.body((IEntityComponentProvider) this, AbstractSecuritySeaBoat.class);
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
			tooltip.setLine(WailaConstants.OBJECT_NAME_TAG, MutableComponent.create(displayStack.getItemName().getContents()).setStyle(ITEM_NAME_STYLE));
	}

	@Override
	public void appendBody(ITooltip tooltip, IBlockAccessor data, IPluginConfig config) {
		addDisguisedOwnerModuleNameInfo(data.getWorld(), data.getPosition(), data.getBlockState(), data.getBlock(), data.getBlockEntity(), data.getPlayer(), tooltip::addLine, config::getBoolean);
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
