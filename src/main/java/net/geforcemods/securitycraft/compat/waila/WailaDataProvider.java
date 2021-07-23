package net.geforcemods.securitycraft.compat.waila;

//@WailaPlugin(SecurityCraft.MODID)
public class WailaDataProvider// implements IWailaPlugin, IComponentProvider, IEntityComponentProvider
{
	//	public static final WailaDataProvider INSTANCE = new WailaDataProvider();
	//	public static final ResourceLocation SHOW_OWNER = new ResourceLocation(SecurityCraft.MODID, "showowner");
	//	public static final ResourceLocation SHOW_MODULES = new ResourceLocation(SecurityCraft.MODID, "showmodules");
	//	public static final ResourceLocation SHOW_PASSWORDS = new ResourceLocation(SecurityCraft.MODID, "showpasswords");
	//	public static final ResourceLocation SHOW_CUSTOM_NAME = new ResourceLocation(SecurityCraft.MODID, "showcustomname");
	//
	//	@Override
	//	public void register(IRegistrar registrar)
	//	{
	//		registrar.addConfig(SHOW_OWNER, true);
	//		registrar.addConfig(SHOW_MODULES, true);
	//		registrar.addConfig(SHOW_PASSWORDS, true);
	//		registrar.addConfig(SHOW_CUSTOM_NAME, true);
	//		registrar.registerComponentProvider((IComponentProvider) INSTANCE, TooltipPosition.BODY, IOwnable.class);
	//		registrar.registerStackProvider(INSTANCE, IOverlayDisplay.class);
	//		registrar.registerComponentProvider((IEntityComponentProvider) INSTANCE, TooltipPosition.BODY, SentryEntity.class);
	//	}
	//
	//	@Override
	//	public ItemStack getStack(IDataAccessor data, IPluginConfig config) {
	//		if(data.getBlock() instanceof IOverlayDisplay)
	//			return ((IOverlayDisplay) data.getBlock()).getDisplayStack(data.getWorld(), data.getBlockState(), data.getPosition());
	//
	//		return ItemStack.EMPTY;
	//	}
	//
	//	@Override
	//	public void appendBody(List<Component> body, IDataAccessor data, IPluginConfig config) {
	//		Block block = data.getBlock();
	//		boolean disguised = false;
	//
	//		if(block instanceof DisguisableBlock)
	//		{
	//			BlockState disguisedBlockState = ((DisguisableBlock)block).getDisguisedBlockState(data.getWorld(), data.getPosition());
	//
	//			if(disguisedBlockState != null)
	//			{
	//				disguised = true;
	//				block = disguisedBlockState.getBlock();
	//			}
	//		}
	//
	//		if(block instanceof IOverlayDisplay && !((IOverlayDisplay) block).shouldShowSCInfo(data.getWorld(), data.getBlockState(), data.getPosition())) return;
	//
	//		BlockEntity te = data.getTileEntity();
	//
	//		//last part is a little cheaty to prevent owner info from being displayed on non-sc blocks
	//		if(config.get(SHOW_OWNER) && te instanceof IOwnable && block.getRegistryName().getNamespace().equals(SecurityCraft.MODID))
	//			body.add(Utils.localize("waila.securitycraft:owner", ((IOwnable) te).getOwner().getName()));
	//
	//		if(disguised)
	//			return;
	//
	//		//if the te is ownable, show modules only when it's owned, otherwise always show
	//		if(config.get(SHOW_MODULES) && te instanceof IModuleInventory && (!(te instanceof IOwnable) || ((IOwnable)te).getOwner().isOwner(data.getPlayer()))){
	//			if(!((IModuleInventory) te).getInsertedModules().isEmpty())
	//				body.add(Utils.localize("waila.securitycraft:equipped"));
	//
	//			for(ModuleType module : ((IModuleInventory) te).getInsertedModules())
	//				body.add(new TextComponent("- ").append(new TranslatableComponent(module.getTranslationKey())));
	//		}
	//
	//		if(config.get(SHOW_PASSWORDS) && te instanceof IPasswordProtected && !(te instanceof KeycardReaderTileEntity) && ((IOwnable) te).getOwner().isOwner(data.getPlayer())){
	//			String password = ((IPasswordProtected) te).getPassword();
	//
	//			body.add(Utils.localize("waila.securitycraft:password", (password != null && !password.isEmpty() ? password : Utils.localize("waila.securitycraft:password.notSet"))));
	//		}
	//
	//		if(config.get(SHOW_CUSTOM_NAME) && te instanceof INameable && ((INameable) te).canBeNamed()){
	//			Component text = ((INameable) te).getCustomSCName();
	//
	//			body.add(Utils.localize("waila.securitycraft:customName", (((INameable) te).hasCustomSCName() ? text : Utils.localize("waila.securitycraft:customName.notSet"))));
	//		}
	//	}
	//
	//	@Override
	//	public void appendBody(List<Component> body, IEntityAccessor data, IPluginConfig config) {
	//		Entity entity = data.getEntity();
	//
	//		if(entity instanceof SentryEntity)
	//		{
	//			SentryEntity sentry = (SentryEntity)entity;
	//			SentryMode mode = sentry.getMode();
	//
	//			if(config.get(SHOW_OWNER))
	//				body.add(Utils.localize("waila.securitycraft:owner", sentry.getOwner().getName()));
	//
	//			if(config.get(SHOW_MODULES) && sentry.getOwner().isOwner(data.getPlayer())){
	//
	//				if(!sentry.getAllowlistModule().isEmpty() || !sentry.getDisguiseModule().isEmpty() || sentry.hasSpeedModule())
	//				{
	//					body.add(Utils.localize("waila.securitycraft:equipped"));
	//
	//					if(!sentry.getAllowlistModule().isEmpty())
	//						body.add(new TextComponent("- ").append(new TranslatableComponent(ModuleType.ALLOWLIST.getTranslationKey())));
	//
	//					if(!sentry.getDisguiseModule().isEmpty())
	//						body.add(new TextComponent("- ").append(new TranslatableComponent(ModuleType.DISGUISE.getTranslationKey())));
	//
	//					if(sentry.hasSpeedModule())
	//						body.add(new TextComponent("- ").append(new TranslatableComponent(ModuleType.SPEED.getTranslationKey())));
	//				}
	//			}
	//
	//			MutableComponent modeDescription = Utils.localize(mode.getModeKey());
	//
	//			if(mode != SentryMode.IDLE)
	//				modeDescription.append("- ").append(Utils.localize(mode.getTargetKey()));
	//
	//			body.add(modeDescription);
	//		}
	//	}
}
