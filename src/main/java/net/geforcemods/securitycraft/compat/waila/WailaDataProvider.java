package net.geforcemods.securitycraft.compat.waila;

//@WailaPlugin(SecurityCraft.MODID)
public class WailaDataProvider// implements IWailaPlugin, IComponentProvider
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
	//		registrar.registerComponentProvider(INSTANCE, TooltipPosition.BODY, IOwnable.class);
	//		registrar.registerStackProvider(INSTANCE, IOverlayDisplay.class);
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
	//	public void appendBody(List<ITextComponent> body, IDataAccessor data, IPluginConfig config) {
	//		if(data.getBlock() instanceof IOverlayDisplay && !((IOverlayDisplay) data.getBlock()).shouldShowSCInfo(data.getWorld(), data.getBlockState(), data.getPosition())) return;
	//
	//		if(config.get(SHOW_OWNER) && data.getTileEntity() instanceof IOwnable)
	//			body.add(new StringTextComponent(ClientUtils.localize("waila.securitycraft:owner") + " " + ((IOwnable) data.getTileEntity()).getOwner().getName()));
	//
	//		if(config.get(SHOW_MODULES) && data.getTileEntity() instanceof CustomizableTileEntity && ((CustomizableTileEntity) data.getTileEntity()).getOwner().isOwner(data.getPlayer())){
	//			if(!((CustomizableTileEntity) data.getTileEntity()).getModules().isEmpty())
	//				body.add(new StringTextComponent(ClientUtils.localize("waila.securitycraft:equipped")));
	//
	//			for(CustomModules module : ((CustomizableTileEntity) data.getTileEntity()).getModules())
	//				body.add(new StringTextComponent("- " + module.getName()));
	//		}
	//
	//		if(config.get(SHOW_PASSWORDS) && data.getTileEntity() instanceof IPasswordProtected && !(data.getTileEntity() instanceof KeycardReaderTileEntity) && ((IOwnable) data.getTileEntity()).getOwner().isOwner(data.getPlayer())){
	//			String password = ((IPasswordProtected) data.getTileEntity()).getPassword();
	//
	//			body.add(new StringTextComponent(ClientUtils.localize("waila.securitycraft:password") + " " + (password != null && !password.isEmpty() ? password : ClientUtils.localize("waila.securitycraft:password.notSet"))));
	//		}
	//
	//		if(config.get(SHOW_CUSTOM_NAME) && data.getTileEntity() instanceof INameable && ((INameable) data.getTileEntity()).canBeNamed()){
	//			String name = ((INameable) data.getTileEntity()).getCustomSCName().getFormattedText();
	//
	//			body.add(new StringTextComponent(ClientUtils.localize("waila.securitycraft:customName") + " " + (((INameable) data.getTileEntity()).hasCustomSCName() ? name : ClientUtils.localize("waila.securitycraft:customName.notSet"))));
	//		}
	//	}
}
