package net.geforcemods.securitycraft.compat.waila;

public final class WTHITDataProvider extends WailaCompatConstants// implements IWailaPlugin, IBlockComponentProvider, IEntityComponentProvider, IEventListener
{
	//	public static final WTHITDataProvider INSTANCE = new WTHITDataProvider();
	//
	//	@Override
	//	public void register(IRegistrar registrar) {
	//		registrar.addEventListener(INSTANCE);
	//		registrar.addSyncedConfig(SHOW_OWNER, true, true);
	//		registrar.addSyncedConfig(SHOW_MODULES, true, true);
	//		registrar.addSyncedConfig(SHOW_CUSTOM_NAME, true, true);
	//		registrar.addComponent((IBlockComponentProvider) INSTANCE, TooltipPosition.HEAD, IOverlayDisplay.class);
	//		registrar.addComponent((IBlockComponentProvider) INSTANCE, TooltipPosition.BODY, IOwnable.class);
	//		registrar.addComponent((IBlockComponentProvider) INSTANCE, TooltipPosition.TAIL, IOverlayDisplay.class);
	//		registrar.addIcon((IBlockComponentProvider) INSTANCE, IOverlayDisplay.class);
	//		registrar.addComponent((IEntityComponentProvider) INSTANCE, TooltipPosition.BODY, Sentry.class);
	//	}
	//
	//	@Override
	//	public ITooltipComponent getIcon(IBlockAccessor data, IPluginConfig config) {
	//		ItemStack displayStack = ((IOverlayDisplay) data.getBlock()).getDisplayStack(data.getWorld(), data.getBlockState(), data.getPosition());
	//
	//		if (displayStack != null)
	//			return new ItemComponent(displayStack);
	//		else
	//			return IBlockComponentProvider.super.getIcon(data, config);
	//	}
	//
	//	@Override
	//	public void appendHead(ITooltip tooltip, IBlockAccessor data, IPluginConfig config) {
	//		ItemStack displayStack = ((IOverlayDisplay) data.getBlock()).getDisplayStack(data.getWorld(), data.getBlockState(), data.getPosition());
	//
	//		if (displayStack != null)
	//			tooltip.setLine(WailaConstants.OBJECT_NAME_TAG, Component.translatable(displayStack.getDescriptionId()).setStyle(ITEM_NAME_STYLE));
	//	}
	//
	//	@Override
	//	public void appendBody(ITooltip tooltip, IBlockAccessor data, IPluginConfig config) {
	//		Block block = data.getBlock();
	//		boolean disguised = false;
	//
	//		if (block instanceof DisguisableBlock) {
	//			Optional<BlockState> disguisedBlockState = DisguisableBlock.getDisguisedBlockState(data.getWorld(), data.getPosition());
	//
	//			if (disguisedBlockState.isPresent()) {
	//				disguised = true;
	//				block = disguisedBlockState.get().getBlock();
	//			}
	//		}
	//
	//		if (block instanceof IOverlayDisplay overlayDisplay && !overlayDisplay.shouldShowSCInfo(data.getWorld(), data.getBlockState(), data.getPosition()))
	//			return;
	//
	//		BlockEntity be = data.getBlockEntity();
	//
	//		//last part is a little cheaty to prevent owner info from being displayed on non-sc blocks
	//		if (config.getBoolean(SHOW_OWNER) && be instanceof IOwnable ownable && Utils.getRegistryName(block).getNamespace().equals(SecurityCraft.MODID))
	//			tooltip.addLine(Utils.localize("waila.securitycraft:owner", PlayerUtils.getOwnerComponent(ownable.getOwner())));
	//
	//		if (disguised)
	//			return;
	//
	//		//if the te is ownable, show modules only when it's owned, otherwise always show
	//		if (config.getBoolean(SHOW_MODULES) && be instanceof IModuleInventory moduleInv && (!(be instanceof IOwnable ownable) || ownable.isOwnedBy(data.getPlayer()))) {
	//			if (!moduleInv.getInsertedModules().isEmpty())
	//				tooltip.addLine(Utils.localize("waila.securitycraft:equipped"));
	//
	//			for (ModuleType module : moduleInv.getInsertedModules()) {
	//				tooltip.addLine(Component.literal("- ").append(Component.translatable(module.getTranslationKey())));
	//			}
	//		}
	//
	//		if (config.getBoolean(SHOW_CUSTOM_NAME) && be instanceof Nameable nameable && nameable.hasCustomName()) {
	//			Component text = nameable.getCustomName();
	//			Component name = text == null ? Component.empty() : text;
	//
	//			tooltip.addLine(Utils.localize("waila.securitycraft:customName", name));
	//		}
	//	}
	//
	//	@Override
	//	public void appendTail(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
	//		ItemStack disguisedAs = ((IOverlayDisplay) accessor.getBlock()).getDisplayStack(accessor.getWorld(), accessor.getBlockState(), accessor.getPosition());
	//
	//		if (disguisedAs != null)
	//			tooltip.setLine(WailaConstants.MOD_NAME_TAG, Component.literal(ModList.get().getModContainerById(Utils.getRegistryName(disguisedAs.getItem()).getNamespace()).get().getModInfo().getDisplayName()).setStyle(MOD_NAME_STYLE));
	//	}
	//
	//	@Override
	//	public void appendBody(ITooltip tooltip, IEntityAccessor data, IPluginConfig config) {
	//		Entity entity = data.getEntity();
	//
	//		if (entity instanceof Sentry sentry) {
	//			SentryMode mode = sentry.getMode();
	//
	//			if (config.getBoolean(SHOW_OWNER))
	//				tooltip.addLine(Utils.localize("waila.securitycraft:owner", PlayerUtils.getOwnerComponent(sentry.getOwner())));
	//
	//			if (config.getBoolean(SHOW_MODULES) && sentry.isOwnedBy(data.getPlayer()) && (!sentry.getAllowlistModule().isEmpty() || !sentry.getDisguiseModule().isEmpty() || sentry.hasSpeedModule())) {
	//				tooltip.addLine(EQUIPPED);
	//
	//				if (!sentry.getAllowlistModule().isEmpty())
	//					tooltip.addLine(ALLOWLIST_MODULE);
	//
	//				if (!sentry.getDisguiseModule().isEmpty())
	//					tooltip.addLine(DISGUISE_MODULE);
	//
	//				if (sentry.hasSpeedModule())
	//					tooltip.addLine(SPEED_MODULE);
	//			}
	//
	//			MutableComponent modeDescription = Utils.localize(mode.getModeKey());
	//
	//			if (mode != SentryMode.IDLE)
	//				modeDescription.append("- ").append(Utils.localize(mode.getTargetKey()));
	//
	//			tooltip.addLine(modeDescription);
	//		}
	//	}
	//
	//	@Override
	//	public void onBeforeTooltipRender(GuiGraphics guiGraphics, Rectangle rectangle, ICommonAccessor accessor, IPluginConfig config, Canceller canceller) {
	//		if (ClientHandler.isPlayerMountedOnCamera())
	//			canceller.cancel();
	//	}
}
