package net.geforcemods.securitycraft.compat.waila;

import java.util.List;
import java.util.Optional;

import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import mcp.mobius.waila.api.event.WailaRenderEvent;
import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.entity.sentry.Sentry.SentryMode;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;

@WailaPlugin(SecurityCraft.MODID)
public class WailaDataProvider implements IWailaPlugin, IComponentProvider, IEntityComponentProvider {
	public static final WailaDataProvider INSTANCE = new WailaDataProvider();
	public static final ResourceLocation SHOW_OWNER = new ResourceLocation(SecurityCraft.MODID, "showowner");
	public static final ResourceLocation SHOW_MODULES = new ResourceLocation(SecurityCraft.MODID, "showmodules");
	public static final ResourceLocation SHOW_CUSTOM_NAME = new ResourceLocation(SecurityCraft.MODID, "showcustomname");
	private static final Style MOD_NAME_STYLE = Style.EMPTY.withColor(TextFormatting.BLUE).withItalic(true);
	private static final Style ITEM_NAME_STYLE = Style.EMPTY.applyFormat(TextFormatting.WHITE);

	static {
		if (FMLEnvironment.dist == Dist.CLIENT)
			MinecraftForge.EVENT_BUS.addListener(WailaDataProvider::onWailaRender);
	}

	@Override
	public void register(IRegistrar registration) {
		registration.addSyncedConfig(SHOW_OWNER, true);
		registration.addSyncedConfig(SHOW_MODULES, true);
		registration.addSyncedConfig(SHOW_CUSTOM_NAME, true);
		registration.registerComponentProvider((IComponentProvider) INSTANCE, TooltipPosition.HEAD, IOverlayDisplay.class);
		registration.registerComponentProvider((IComponentProvider) INSTANCE, TooltipPosition.BODY, IOwnable.class);
		registration.registerComponentProvider((IComponentProvider) INSTANCE, TooltipPosition.TAIL, IOverlayDisplay.class);
		registration.registerStackProvider(INSTANCE, IOverlayDisplay.class);
		registration.registerComponentProvider((IEntityComponentProvider) INSTANCE, TooltipPosition.BODY, Sentry.class);
	}

	@Override
	public ItemStack getStack(IDataAccessor data, IPluginConfig config) {
		ItemStack displayStack = ((IOverlayDisplay) data.getBlock()).getDisplayStack(data.getWorld(), data.getBlockState(), data.getPosition());

		if (displayStack != null)
			return displayStack;
		else
			return IComponentProvider.super.getStack(data, config);
	}

	@Override
	public void appendHead(List<ITextComponent> head, IDataAccessor data, IPluginConfig config) {
		ItemStack displayStack = ((IOverlayDisplay) data.getBlock()).getDisplayStack(data.getWorld(), data.getBlockState(), data.getPosition());

		if (displayStack != null)
			head.set(0, new TranslationTextComponent(displayStack.getDescriptionId()).setStyle(ITEM_NAME_STYLE));
	}

	@Override
	public void appendBody(List<ITextComponent> body, IDataAccessor data, IPluginConfig config) {
		Block block = data.getBlock();
		boolean disguised = false;

		if (block instanceof DisguisableBlock) {
			Optional<BlockState> disguisedBlockState = DisguisableBlock.getDisguisedBlockState(data.getWorld(), data.getPosition());

			if (disguisedBlockState.isPresent()) {
				disguised = true;
				block = disguisedBlockState.get().getBlock();
			}
		}

		if (block instanceof IOverlayDisplay && !((IOverlayDisplay) block).shouldShowSCInfo(data.getWorld(), data.getBlockState(), data.getPosition()))
			return;

		TileEntity be = data.getTileEntity();

		//last part is a little cheaty to prevent owner info from being displayed on non-sc blocks
		if (config.get(SHOW_OWNER) && be instanceof IOwnable && block.getRegistryName().getNamespace().equals(SecurityCraft.MODID))
			body.add(Utils.localize("waila.securitycraft:owner", PlayerUtils.getOwnerComponent(((IOwnable) be).getOwner())));

		if (disguised)
			return;

		//if the te is ownable, show modules only when it's owned, otherwise always show
		if (config.get(SHOW_MODULES) && be instanceof IModuleInventory && (!(be instanceof IOwnable) || ((IOwnable) be).isOwnedBy(data.getPlayer()))) {
			if (!((IModuleInventory) be).getInsertedModules().isEmpty())
				body.add(Utils.localize("waila.securitycraft:equipped"));

			for (ModuleType module : ((IModuleInventory) be).getInsertedModules()) {
				body.add(new StringTextComponent("- ").append(new TranslationTextComponent(module.getTranslationKey())));
			}
		}
	}

	@Override
	public void appendTail(List<ITextComponent> tail, IDataAccessor data, IPluginConfig config) {
		ItemStack disguisedAs = ((IOverlayDisplay) data.getBlock()).getDisplayStack(data.getWorld(), data.getBlockState(), data.getPosition());

		if (disguisedAs != null)
			tail.set(0, new StringTextComponent(ModList.get().getModContainerById(disguisedAs.getItem().getRegistryName().getNamespace()).get().getModInfo().getDisplayName()).setStyle(MOD_NAME_STYLE));
	}

	@Override
	public void appendBody(List<ITextComponent> body, IEntityAccessor data, IPluginConfig config) {
		Entity entity = data.getEntity();

		if (entity instanceof Sentry) {
			Sentry sentry = (Sentry) entity;
			SentryMode mode = sentry.getMode();

			if (config.get(SHOW_OWNER))
				body.add(Utils.localize("waila.securitycraft:owner", PlayerUtils.getOwnerComponent(sentry.getOwner())));

			if (config.get(SHOW_MODULES) && sentry.isOwnedBy(data.getPlayer()) && (!sentry.getAllowlistModule().isEmpty() || !sentry.getDisguiseModule().isEmpty() || sentry.hasSpeedModule())) {
				body.add(Utils.localize("waila.securitycraft:equipped"));

				if (!sentry.getAllowlistModule().isEmpty())
					body.add(new StringTextComponent("- ").append(new TranslationTextComponent(ModuleType.ALLOWLIST.getTranslationKey())));

				if (!sentry.getDisguiseModule().isEmpty())
					body.add(new StringTextComponent("- ").append(new TranslationTextComponent(ModuleType.DISGUISE.getTranslationKey())));

				if (sentry.hasSpeedModule())
					body.add(new StringTextComponent("- ").append(new TranslationTextComponent(ModuleType.SPEED.getTranslationKey())));
			}

			IFormattableTextComponent modeDescription = Utils.localize(mode.getModeKey());

			if (mode != SentryMode.IDLE)
				modeDescription.append("- ").append(Utils.localize(mode.getTargetKey()));

			body.add(modeDescription);
		}
	}

	public static void onWailaRender(WailaRenderEvent.Pre event) {
		if (ClientHandler.isPlayerMountedOnCamera())
			event.setCanceled(true);
	}
}
