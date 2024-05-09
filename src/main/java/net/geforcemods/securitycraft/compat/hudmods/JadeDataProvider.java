package net.geforcemods.securitycraft.compat.hudmods;

import java.util.List;

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
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;

@WailaPlugin(SecurityCraft.MODID)
public final class JadeDataProvider extends HudModHandler implements IWailaPlugin, IComponentProvider, IEntityComponentProvider {
	static {
		if (FMLEnvironment.dist == Dist.CLIENT)
			MinecraftForge.EVENT_BUS.addListener(JadeDataProvider::onWailaRender);
	}

	@Override
	public void register(IRegistrar registration) {
		registration.addSyncedConfig(SHOW_OWNER, true);
		registration.addSyncedConfig(SHOW_MODULES, true);
		registration.addSyncedConfig(SHOW_CUSTOM_NAME, true);
		registration.registerComponentProvider((IComponentProvider) this, TooltipPosition.HEAD, IOverlayDisplay.class);
		registration.registerComponentProvider((IComponentProvider) this, TooltipPosition.BODY, IOwnable.class);
		registration.registerComponentProvider((IComponentProvider) this, TooltipPosition.TAIL, IOverlayDisplay.class);
		registration.registerStackProvider(this, IOverlayDisplay.class);
		registration.registerComponentProvider((IEntityComponentProvider) this, TooltipPosition.BODY, Sentry.class);
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
	public void appendBody(List<ITextComponent> tooltip, IDataAccessor data, IPluginConfig config) {
		World level = data.getWorld();
		BlockPos pos = data.getPosition();
		BlockState state = data.getBlockState();
		Block block = data.getBlock();

		addOwnerModuleNameInfo(level, pos, state, block, data.getTileEntity(), data.getPlayer(), tooltip::add, config::get);
	}

	@Override
	public void appendTail(List<ITextComponent> tail, IDataAccessor data, IPluginConfig config) {
		ItemStack disguisedAs = ((IOverlayDisplay) data.getBlock()).getDisplayStack(data.getWorld(), data.getBlockState(), data.getPosition());

		if (disguisedAs != null)
			tail.set(0, new StringTextComponent(ModList.get().getModContainerById(disguisedAs.getItem().getRegistryName().getNamespace()).get().getModInfo().getDisplayName()).setStyle(MOD_NAME_STYLE));
	}

	@Override
	public void appendBody(List<ITextComponent> tooltip, IEntityAccessor data, IPluginConfig config) {
		addEntityInfo(data.getEntity(), data.getPlayer(), tooltip::add, config::get);
	}

	public static void onWailaRender(WailaRenderEvent.Pre event) {
		if (ClientHandler.isPlayerMountedOnCamera())
			event.setCanceled(true);
	}
}
