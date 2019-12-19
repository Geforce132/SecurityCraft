package net.geforcemods.securitycraft.compat.top;

public class TOPDataProvider// implements Function<ITheOneProbe, Void>
{
	//	private final String formatting = TextFormatting.BLUE.toString() + TextFormatting.ITALIC.toString();
	//
	//	@Nullable
	//	@Override
	//	public Void apply(ITheOneProbe theOneProbe)
	//	{
	//		theOneProbe.registerBlockDisplayOverride(new IBlockDisplayOverride() {
	//			@Override
	//			public boolean overrideStandardInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data)
	//			{
	//				if(blockState.getBlock() instanceof KeypadBlock)
	//				{
	//					ItemStack disguisedAs = KeypadBlock.getDisguisedStack(world, data.getPos());
	//
	//					probeInfo.horizontal()
	//					.item(disguisedAs)
	//					.vertical()
	//					.itemLabel(disguisedAs)
	//					.text(formatting + ModList.get().getModContainerById(disguisedAs.getItem().getRegistryName().getNamespace()).get().getModInfo().getDisplayName());
	//					return true;
	//				}
	//
	//				return false;
	//			}
	//		});
	//		theOneProbe.registerProvider(new IProbeInfoProvider() {
	//			@Override
	//			public String getID()
	//			{
	//				return SecurityCraft.MODID + ":" + SecurityCraft.MODID;
	//			}
	//
	//			@Override
	//			public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data)
	//			{
	//				Block block = blockState.getBlock();
	//
	//				if(block instanceof IOverlayDisplay && !((IOverlayDisplay) block).shouldShowSCInfo(world, blockState, data.getPos()))
	//					return;
	//
	//				TileEntity te = world.getTileEntity(data.getPos());
	//
	//				if(te instanceof IOwnable)
	//					probeInfo.vertical().text(TextFormatting.GRAY + ClientUtils.localize("waila.securitycraft:owner") + " " + ((IOwnable) te).getOwner().getName());
	//
	//				if(te instanceof CustomizableTileEntity && ((CustomizableTileEntity)te).getOwner().isOwner(player))
	//				{
	//					if(!((CustomizableTileEntity)te).getModules().isEmpty())
	//					{
	//						probeInfo.text(TextFormatting.GRAY + ClientUtils.localize("waila.securitycraft:equipped"));
	//
	//						for(CustomModules module : ((CustomizableTileEntity) te).getModules())
	//							probeInfo.text(TextFormatting.GRAY + "- " + module.getName());
	//					}
	//				}
	//
	//				if(te instanceof IPasswordProtected && !(te instanceof KeycardReaderTileEntity) && ((IOwnable)te).getOwner().isOwner(player))
	//				{
	//					String password = ((IPasswordProtected) te).getPassword();
	//
	//					probeInfo.text(TextFormatting.GRAY + ClientUtils.localize("waila.securitycraft:password") + " " + (password != null && !password.isEmpty() ? password : ClientUtils.localize("waila.securitycraft:password.notSet")));
	//				}
	//
	//				if(te instanceof INameable && ((INameable) te).canBeNamed()){
	//					String name = ((INameable) te).getCustomSCName().toString();
	//
	//					probeInfo.text(TextFormatting.GRAY + ClientUtils.localize("waila.securitycraft:customName") + " " + (((INameable) te).hasCustomSCName() ? name : ClientUtils.localize("waila.securitycraft:customName.notSet")));
	//				}
	//			}
	//		});
	//		return null;
	//	}
}