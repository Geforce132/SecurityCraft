package net.geforcemods.securitycraft.compat.waila;

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
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.entity.sentry.Sentry.SentryMode;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.ClientProxy;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@WailaPlugin(SecurityCraft.MODID)
public class WailaDataProvider implements IWailaPlugin, IWailaDataProvider, IWailaEntityProvider {
	private static final String SHOW_OWNER = "securitycraft.showowner";
	private static final String SHOW_MODULES = "securitycraft.showmodules";
	private static final String SHOW_CUSTOM_NAME = "securitycraft.showcustomname";

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
	public List<String> getWailaBody(ItemStack stack, List<String> body, IWailaDataAccessor data, IWailaConfigHandler config) {
		World world = data.getWorld();
		BlockPos pos = data.getPosition();
		IBlockState state = data.getBlockState();
		Block block = data.getBlock();
		boolean disguised = false;

		if (block instanceof DisguisableBlock) {
			IBlockState disguisedBlockState = ((DisguisableBlock) block).getDisguisedBlockState(world, pos);

			if (disguisedBlockState != null) {
				disguised = true;
				block = disguisedBlockState.getBlock();
			}
		}

		if (block instanceof IOverlayDisplay && !((IOverlayDisplay) block).shouldShowSCInfo(world, state, pos))
			return body;

		TileEntity te = data.getTileEntity();

		//last part is a little cheaty to prevent owner info from being displayed on non-sc blocks
		if (config.getConfig(SHOW_OWNER) && te instanceof IOwnable && block.getRegistryName().getNamespace().equals(SecurityCraft.MODID))
			body.add(Utils.localize("waila.securitycraft:owner", PlayerUtils.getOwnerComponent(((IOwnable) te).getOwner())).getFormattedText());

		if (!disguised) {
			//if the te is ownable, show modules only when it's owned, otherwise always show
			if (config.getConfig(SHOW_MODULES) && te instanceof IModuleInventory && (!(te instanceof IOwnable) || ((IOwnable) te).isOwnedBy(data.getPlayer()))) {
				if (!((IModuleInventory) te).getInsertedModules().isEmpty())
					body.add(Utils.localize("waila.securitycraft:equipped").getFormattedText());

				for (ModuleType module : ((IModuleInventory) te).getInsertedModules()) {
					body.add("- " + Utils.localize(module.getTranslationKey()).getFormattedText());
				}
			}

			if (config.getConfig(SHOW_CUSTOM_NAME) && te instanceof IWorldNameable && ((IWorldNameable) te).hasCustomName()) {
				String name = ((IWorldNameable) te).getName();

				body.add(Utils.localize("waila.securitycraft:customName").getFormattedText() + " " + name);
			}
		}

		return body;
	}

	@Override
	public List<String> getWailaBody(Entity entity, List<String> body, IWailaEntityAccessor data, IWailaConfigHandler config) {
		if (entity instanceof Sentry) {
			Sentry sentry = (Sentry) entity;
			SentryMode mode = sentry.getMode();

			if (config.getConfig(SHOW_OWNER))
				body.add(Utils.localize("waila.securitycraft:owner", PlayerUtils.getOwnerComponent(sentry.getOwner())).getFormattedText());

			if (config.getConfig(SHOW_MODULES) && sentry.isOwnedBy(data.getPlayer()) && (!sentry.getAllowlistModule().isEmpty() || !sentry.getDisguiseModule().isEmpty() || sentry.hasSpeedModule())) {
				body.add(Utils.localize("waila.securitycraft:equipped").getFormattedText());

				if (!sentry.getAllowlistModule().isEmpty())
					body.add("- " + Utils.localize(ModuleType.ALLOWLIST.getTranslationKey()).getFormattedText());

				if (!sentry.getDisguiseModule().isEmpty())
					body.add("- " + Utils.localize(ModuleType.DISGUISE.getTranslationKey()).getFormattedText());

				if (sentry.hasSpeedModule())
					body.add("- " + Utils.localize(ModuleType.SPEED.getTranslationKey()).getFormattedText());
			}

			String modeDescription = Utils.localize(mode.getModeKey()).getFormattedText();

			if (mode != SentryMode.IDLE)
				modeDescription += " - " + Utils.localize(mode.getTargetKey()).getFormattedText();

			body.add(modeDescription);
		}

		return body;
	}

	@SubscribeEvent
	public static void onWailaRender(WailaRenderEvent.Pre event) {
		if (ClientProxy.isPlayerMountedOnCamera())
			event.setCanceled(true);
	}
}
