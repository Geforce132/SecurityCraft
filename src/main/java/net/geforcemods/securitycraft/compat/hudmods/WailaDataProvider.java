package net.geforcemods.securitycraft.compat.hudmods;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;
import mcp.mobius.waila.api.event.WailaRenderEvent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.network.ClientProxy;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@WailaPlugin(SecurityCraft.MODID)
public final class WailaDataProvider extends HudModHandler implements IWailaPlugin, IWailaDataProvider, IWailaEntityProvider {
	static {
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT)
			MinecraftForge.EVENT_BUS.register(WailaDataProvider.class);
	}

	@Override
	public void register(IWailaRegistrar registrar) {
		registrar.addConfigRemote("SecurityCraft", SHOW_OWNER, Utils.localize("waila.securitycraft:displayOwner").getFormattedText());
		registrar.addConfigRemote("SecurityCraft", SHOW_MODULES, Utils.localize("waila.securitycraft:showModules").getFormattedText());
		registrar.addConfigRemote("SecurityCraft", SHOW_CUSTOM_NAME, Utils.localize("waila.securitycraft:showCustomName").getFormattedText());
		registrar.registerBodyProvider((IWailaDataProvider) this, IOwnable.class);
		registrar.registerStackProvider(this, IOverlayDisplay.class);
		registrar.registerBodyProvider((IWailaEntityProvider) this, Sentry.class);
	}

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor data, IWailaConfigHandler config) {
		if (data.getBlock() instanceof IOverlayDisplay) {
			ItemStack displayStack = ((IOverlayDisplay) data.getBlock()).getDisplayStack(data.getWorld(), data.getBlockState(), data.getPosition());

			if (displayStack != null)
				return displayStack;
		}

		return ItemStack.EMPTY;
	}

	@Override
	public List<String> getWailaBody(ItemStack stack, List<String> tooltip, IWailaDataAccessor data, IWailaConfigHandler config) {
		World level = data.getWorld();
		BlockPos pos = data.getPosition();
		IBlockState state = data.getBlockState();
		Block block = data.getBlock();

		addOwnerModuleNameInfo(level, pos, state, block, data.getTileEntity(), data.getPlayer(), tooltip::add, config::getConfig);
		return tooltip;
	}

	@Override
	public List<String> getWailaBody(Entity entity, List<String> tooltip, IWailaEntityAccessor data, IWailaConfigHandler config) {
		addEntityInfo(data.getEntity(), data.getPlayer(), tooltip::add, config::getConfig);
		return tooltip;
	}

	@SubscribeEvent
	public static void onWailaRender(WailaRenderEvent.Pre event) {
		if (ClientProxy.isPlayerMountedOnCamera())
			event.setCanceled(true);
	}
}
