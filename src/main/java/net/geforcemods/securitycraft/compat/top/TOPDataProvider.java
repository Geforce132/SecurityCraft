package net.geforcemods.securitycraft.compat.top;

public class TOPDataProvider// implements Function<ITheOneProbe, Void>
{
	//	@Nullable
	//	@Override
	//	public Void apply(ITheOneProbe theOneProbe)
	//	{
	//		theOneProbe.registerBlockDisplayOverride((mode, probeInfo, player, world, blockState, data) -> {
	//			ItemStack disguisedAs = ItemStack.EMPTY;
	//
	//			if(blockState.getBlock() instanceof DisguisableBlock)
	//				disguisedAs = ((DisguisableBlock)blockState.getBlock()).getDisguisedStack(world, data.getPos());
	//			else if(blockState.getBlock() instanceof IOverlayDisplay)
	//				disguisedAs = ((IOverlayDisplay)blockState.getBlock()).getDisplayStack(world, blockState, data.getPos());
	//
	//			if(!disguisedAs.isEmpty())
	//			{
	//				probeInfo.horizontal()
	//				.item(disguisedAs)
	//				.vertical()
	//				.itemLabel(disguisedAs)
	//				.text(new TextComponent("" + ChatFormatting.BLUE + ChatFormatting.ITALIC + ModList.get().getModContainerById(disguisedAs.getItem().getRegistryName().getNamespace()).get().getModInfo().getDisplayName()));
	//				return true;
	//			}
	//
	//			return false;
	//		});
	//		theOneProbe.registerProvider(new IProbeInfoProvider() {
	//			@Override
	//			public String getID()
	//			{
	//				return SecurityCraft.MODID + ":" + SecurityCraft.MODID;
	//			}
	//
	//			@Override
	//			public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level world, BlockState blockState, IProbeHitData data)
	//			{
	//				Block block = blockState.getBlock();
	//
	//				if(block instanceof IOverlayDisplay && !((IOverlayDisplay) block).shouldShowSCInfo(world, blockState, data.getPos()))
	//					return;
	//
	//				BlockEntity te = world.getBlockEntity(data.getPos());
	//
	//				if(te instanceof IOwnable)
	//					probeInfo.vertical().text(new TextComponent(ChatFormatting.GRAY + Utils.localize("waila.securitycraft:owner", ((IOwnable) te).getOwner().getName()).getString()));
	//
	//				//if the te is ownable, show modules only when it's owned, otherwise always show
	//				if(te instanceof IModuleInventory && (!(te instanceof IOwnable) || ((IOwnable)te).getOwner().isOwner(player)))
	//				{
	//					if(!((IModuleInventory)te).getInsertedModules().isEmpty())
	//					{
	//						probeInfo.text(new TextComponent(ChatFormatting.GRAY + Utils.localize("waila.securitycraft:equipped").getString()));
	//
	//						for(ModuleType module : ((IModuleInventory) te).getInsertedModules())
	//							probeInfo.text(new TextComponent(ChatFormatting.GRAY + "- ").append(new TranslatableComponent(module.getTranslationKey())));
	//					}
	//				}
	//
	//				if(te instanceof IPasswordProtected && !(te instanceof KeycardReaderTileEntity) && ((IOwnable)te).getOwner().isOwner(player))
	//				{
	//					String password = ((IPasswordProtected) te).getPassword();
	//
	//					probeInfo.text(new TextComponent(ChatFormatting.GRAY + Utils.localize("waila.securitycraft:password", (password != null && !password.isEmpty() ? password : Utils.localize("waila.securitycraft:password.notSet"))).getString()));
	//				}
	//
	//				if(te instanceof INameable && ((INameable) te).canBeNamed()){
	//					Component text = ((INameable) te).getCustomSCName();
	//					Component name = text == null ? TextComponent.EMPTY : text;
	//
	//					probeInfo.text(new TextComponent(ChatFormatting.GRAY + Utils.localize("waila.securitycraft:customName", (((INameable) te).hasCustomSCName() ? name : Utils.localize("waila.securitycraft:customName.notSet"))).getString()));
	//				}
	//			}
	//		});
	//		theOneProbe.registerEntityProvider(new IProbeInfoEntityProvider() {
	//			@Override
	//			public String getID() {
	//				return SecurityCraft.MODID + ":" + SecurityCraft.MODID;
	//			}
	//
	//			@Override
	//			public void addProbeEntityInfo(ProbeMode probeMode, IProbeInfo probeInfo, Player player, Level world, Entity entity, IProbeHitEntityData data) {
	//				if (entity instanceof SentryEntity)
	//				{
	//					SentryEntity sentry = (SentryEntity)entity;
	//					SentryMode mode = sentry.getMode();
	//
	//					probeInfo.text(new TextComponent(ChatFormatting.GRAY + Utils.localize("waila.securitycraft:owner", ((SentryEntity) entity).getOwner().getName()).getString()));
	//
	//					if(!sentry.getAllowlistModule().isEmpty() || !sentry.getDisguiseModule().isEmpty() || sentry.hasSpeedModule())
	//					{
	//						probeInfo.text(new TextComponent(ChatFormatting.GRAY + Utils.localize("waila.securitycraft:equipped").getString()));
	//
	//						if(!sentry.getAllowlistModule().isEmpty())
	//							probeInfo.text(new TextComponent(ChatFormatting.GRAY + "- ").append(new TranslatableComponent(ModuleType.ALLOWLIST.getTranslationKey())));
	//
	//						if(!sentry.getDisguiseModule().isEmpty())
	//							probeInfo.text(new TextComponent(ChatFormatting.GRAY + "- ").append(new TranslatableComponent(ModuleType.DISGUISE.getTranslationKey())));
	//
	//						if(sentry.hasSpeedModule())
	//							probeInfo.text(new TextComponent(ChatFormatting.GRAY + "- ").append(new TranslatableComponent(ModuleType.SPEED.getTranslationKey())));
	//					}
	//
	//					MutableComponent modeDescription = Utils.localize(mode.getModeKey());
	//
	//					if(mode != SentryMode.IDLE)
	//						modeDescription.append("- ").append(Utils.localize(mode.getTargetKey()));
	//
	//					probeInfo.text(new TextComponent(ChatFormatting.GRAY + modeDescription.getString()));
	//				}
	//			}
	//		});
	//		return null;
	//	}
}