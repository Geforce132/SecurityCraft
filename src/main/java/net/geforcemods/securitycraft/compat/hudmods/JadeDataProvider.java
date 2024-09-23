package net.geforcemods.securitycraft.compat.hudmods;

import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.EntityAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.IWailaClientRegistration;
import mcp.mobius.waila.api.IWailaCommonRegistration;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import mcp.mobius.waila.api.config.IPluginConfig;
import mcp.mobius.waila.api.event.WailaRenderEvent;
import mcp.mobius.waila.api.ui.IElement;
import mcp.mobius.waila.impl.ui.ItemStackElement;
import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.blocks.InventoryScannerFieldBlock;
import net.geforcemods.securitycraft.blocks.LaserFieldBlock;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.geforcemods.securitycraft.blocks.mines.BaseFullMineBlock;
import net.geforcemods.securitycraft.blocks.mines.FurnaceMineBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedCauldronBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedPaneBlock;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.RegistryObject;

@WailaPlugin(SecurityCraft.MODID)
public final class JadeDataProvider extends HudModHandler implements IWailaPlugin, IComponentProvider, IEntityComponentProvider {
	static {
		if (FMLEnvironment.dist == Dist.CLIENT)
			MinecraftForge.EVENT_BUS.addListener(JadeDataProvider::onWailaRender);
	}

	@Override
	public void register(IWailaCommonRegistration registration) {
		registration.addSyncedConfig(SHOW_OWNER, true);
		registration.addSyncedConfig(SHOW_MODULES, true);
		registration.addSyncedConfig(SHOW_CUSTOM_NAME, true);
	}

	@Override
	public void registerClient(IWailaClientRegistration registration) {
		for (RegistryObject<Block> registryObject : SCContent.BLOCKS.getEntries()) {
			Block block = registryObject.get();

			if (!(block instanceof OwnableBlock) && !block.getRegistryName().getPath().matches("(?!(reinforced_)).*?crystal_.*") && !(block instanceof ReinforcedCauldronBlock) && !(block instanceof ReinforcedPaneBlock))
				registration.registerComponentProvider(this, TooltipPosition.BODY, block.getClass());

			if (block instanceof IOverlayDisplay)
				registration.usePickedResult(block);
		}

		registration.registerComponentProvider(this, TooltipPosition.HEAD, BaseFullMineBlock.class);
		registration.registerComponentProvider(this, TooltipPosition.HEAD, FurnaceMineBlock.class);
		registration.registerComponentProvider(this, TooltipPosition.BODY, OwnableBlock.class);
		registration.registerComponentProvider(this, TooltipPosition.BODY, InventoryScannerFieldBlock.class);
		registration.registerComponentProvider(this, TooltipPosition.BODY, LaserFieldBlock.class);
		registration.registerComponentProvider(this, TooltipPosition.BODY, ReinforcedCauldronBlock.class);
		registration.registerComponentProvider(this, TooltipPosition.BODY, ReinforcedPaneBlock.class);
		registration.registerIconProvider(this, DisguisableBlock.class);
		registration.registerIconProvider(this, BaseFullMineBlock.class);
		registration.registerIconProvider(this, FurnaceMineBlock.class);
		registration.registerComponentProvider(this, TooltipPosition.BODY, Sentry.class);
		registration.registerComponentProvider(this, TooltipPosition.TAIL, BaseFullMineBlock.class);
		registration.registerComponentProvider(this, TooltipPosition.TAIL, FurnaceMineBlock.class);
	}

	@Override
	public IElement getIcon(BlockAccessor data, IPluginConfig config, IElement currentIcon) {
		if (data.getBlock() instanceof IOverlayDisplay display)
			return ItemStackElement.of(display.getDisplayStack(data.getLevel(), data.getBlockState(), data.getPosition()));

		return ItemStackElement.EMPTY;
	}

	@Override
	public void appendTooltip(ITooltip tooltip, BlockAccessor data, IPluginConfig config) {
		Level level = data.getLevel();
		BlockPos pos = data.getPosition();
		BlockState state = data.getBlockState();
		Block block = data.getBlock();

		addDisguisedOwnerModuleNameInfo(level, pos, state, block, data.getBlockEntity(), data.getPlayer(), tooltip::add, config::get);
	}

	@Override
	public void appendTooltip(ITooltip tooltip, EntityAccessor data, IPluginConfig config) {
		addEntityInfo(data.getEntity(), data.getPlayer(), tooltip::add, config::get);
	}

	public static void onWailaRender(WailaRenderEvent.Pre event) {
		if (ClientHandler.isPlayerMountedOnCamera())
			event.setCanceled(true);
	}
}
