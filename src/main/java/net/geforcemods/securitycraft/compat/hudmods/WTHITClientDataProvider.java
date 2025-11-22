package net.geforcemods.securitycraft.compat.hudmods;

public final class WTHITClientDataProvider extends HudModHandler /*implements IWailaClientPlugin, IBlockComponentProvider, IEntityComponentProvider, IEventListener*/ {
	/*@Override
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
	}*/
}
