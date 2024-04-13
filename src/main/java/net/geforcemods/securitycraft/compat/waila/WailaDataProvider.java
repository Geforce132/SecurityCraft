package net.geforcemods.securitycraft.compat.waila;

import java.util.Optional;

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
import mcp.mobius.waila.api.ui.IElement.Align;
import mcp.mobius.waila.impl.Tooltip;
import mcp.mobius.waila.impl.ui.ItemStackElement;
import mcp.mobius.waila.impl.ui.TextElement;
import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
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
import net.geforcemods.securitycraft.entity.sentry.Sentry.SentryMode;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.RegistryObject;

@WailaPlugin(SecurityCraft.MODID)
public final class WailaDataProvider implements IWailaPlugin, IComponentProvider, IEntityComponentProvider {
	public static final ResourceLocation SHOW_OWNER = new ResourceLocation(SecurityCraft.MODID, "showowner");
	public static final ResourceLocation SHOW_MODULES = new ResourceLocation(SecurityCraft.MODID, "showmodules");
	public static final ResourceLocation SHOW_CUSTOM_NAME = new ResourceLocation(SecurityCraft.MODID, "showcustomname");
	private static final Style MOD_NAME_STYLE = Style.EMPTY.applyFormat(ChatFormatting.BLUE).withItalic(true);
	private static final Style ITEM_NAME_STYLE = Style.EMPTY.applyFormat(ChatFormatting.WHITE);
	private static final MutableComponent EQUIPPED = Utils.localize("waila.securitycraft:equipped").withStyle(Utils.GRAY_STYLE);
	private static final MutableComponent ALLOWLIST_MODULE = new TextComponent("- ").append(new TranslatableComponent(ModuleType.ALLOWLIST.getTranslationKey())).withStyle(Utils.GRAY_STYLE);
	private static final MutableComponent DISGUISE_MODULE = new TextComponent("- ").append(new TranslatableComponent(ModuleType.DISGUISE.getTranslationKey())).withStyle(Utils.GRAY_STYLE);
	private static final MutableComponent SPEED_MODULE = new TextComponent("- ").append(new TranslatableComponent(ModuleType.SPEED.getTranslationKey())).withStyle(Utils.GRAY_STYLE);

	static {
		if (FMLEnvironment.dist == Dist.CLIENT)
			MinecraftForge.EVENT_BUS.addListener(WailaDataProvider::onWailaRender);
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
		switch (data.getTooltipPosition()) {
			case HEAD: {
				if (tooltip instanceof Tooltip head)
					head.lines.get(0).getAlignedElements(Align.LEFT).set(0, new TextElement(new TranslatableComponent(((IOverlayDisplay) data.getBlock()).getDisplayStack(data.getLevel(), data.getBlockState(), data.getPosition()).getDescriptionId()).setStyle(ITEM_NAME_STYLE)));

				break;
			}
			case BODY: {
				Block block = data.getBlock();
				boolean disguised = false;

				if (block instanceof DisguisableBlock) {
					Optional<BlockState> disguisedBlockState = DisguisableBlock.getDisguisedBlockState(data.getLevel(), data.getPosition());

					if (disguisedBlockState.isPresent()) {
						disguised = true;
						block = disguisedBlockState.get().getBlock();
					}
				}

				if (block instanceof IOverlayDisplay display && !display.shouldShowSCInfo(data.getLevel(), data.getBlockState(), data.getPosition()))
					return;

				BlockEntity be = data.getBlockEntity();

				if (be != null) {
					//last part is a little cheaty to prevent owner info from being displayed on non-sc blocks
					if (config.get(SHOW_OWNER) && be instanceof IOwnable ownable && block.getRegistryName().getNamespace().equals(SecurityCraft.MODID))
						tooltip.add(Utils.localize("waila.securitycraft:owner", PlayerUtils.getOwnerComponent(ownable.getOwner())));

					if (disguised)
						return;

					//if the te is ownable, show modules only when it's owned, otherwise always show
					if (config.get(SHOW_MODULES) && be instanceof IModuleInventory inv && (!(be instanceof IOwnable ownable) || ownable.isOwnedBy(data.getPlayer()))) {
						if (!inv.getInsertedModules().isEmpty())
							tooltip.add(EQUIPPED);

						for (ModuleType module : inv.getInsertedModules()) {
							tooltip.add(new TextComponent("- ").append(new TranslatableComponent(module.getTranslationKey())));
						}
					}
				}

				break;
			}
			case TAIL: {
				if (tooltip instanceof Tooltip tail) {
					ItemStack disguisedAs = ((IOverlayDisplay) data.getBlock()).getDisplayStack(data.getLevel(), data.getBlockState(), data.getPosition());
					Component modName = new TextComponent(ModList.get().getModContainerById(disguisedAs.getItem().getRegistryName().getNamespace()).get().getModInfo().getDisplayName()).setStyle(MOD_NAME_STYLE);

					tail.lines.get(tail.lines.size() - 1).getAlignedElements(Align.LEFT).set(0, new TextElement(modName));
				}
			}
		}
	}

	@Override
	public void appendTooltip(ITooltip tooltip, EntityAccessor data, IPluginConfig config) {
		if (data.getTooltipPosition() == TooltipPosition.BODY) {
			Entity entity = data.getEntity();

			if (entity instanceof Sentry sentry) {
				SentryMode mode = sentry.getMode();

				if (config.get(SHOW_OWNER))
					tooltip.add(Utils.localize("waila.securitycraft:owner", PlayerUtils.getOwnerComponent(sentry.getOwner())));

				if (config.get(SHOW_MODULES) && sentry.isOwnedBy(data.getPlayer()) && (!sentry.getAllowlistModule().isEmpty() || !sentry.getDisguiseModule().isEmpty() || sentry.hasSpeedModule())) {
					tooltip.add(EQUIPPED);

					if (!sentry.getAllowlistModule().isEmpty())
						tooltip.add(ALLOWLIST_MODULE);

					if (!sentry.getDisguiseModule().isEmpty())
						tooltip.add(DISGUISE_MODULE);

					if (sentry.hasSpeedModule())
						tooltip.add(SPEED_MODULE);
				}

				MutableComponent modeDescription = Utils.localize(mode.getModeKey());

				if (mode != SentryMode.IDLE)
					modeDescription.append("- ").append(Utils.localize(mode.getTargetKey()));

				tooltip.add(modeDescription);
			}
		}
	}

	public static void onWailaRender(WailaRenderEvent.Pre event) {
		if (ClientHandler.isPlayerMountedOnCamera())
			event.setCanceled(true);
	}
}
