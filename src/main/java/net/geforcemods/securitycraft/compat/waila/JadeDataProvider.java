package net.geforcemods.securitycraft.compat.waila;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.blocks.InventoryScannerFieldBlock;
import net.geforcemods.securitycraft.blocks.LaserFieldBlock;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.geforcemods.securitycraft.blocks.mines.BaseFullMineBlock;
import net.geforcemods.securitycraft.blocks.mines.FurnaceMineBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedCauldronBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedPaneBlock;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.entity.Sentry;
import net.geforcemods.securitycraft.entity.Sentry.SentryMode;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.RegistryObject;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.TooltipPosition;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElement.Align;
import snownee.jade.impl.Tooltip;
import snownee.jade.impl.ui.ItemStackElement;
import snownee.jade.impl.ui.TextElement;

@WailaPlugin(SecurityCraft.MODID)
public class JadeDataProvider extends WailaCompatConstants implements IWailaPlugin {
	public static final SpoofBlockIcon SPOOF_BLOCK_ICON = new SpoofBlockIcon();
	public static final SecurityCraftInfo SECURITYCRAFT_INFO = new SecurityCraftInfo();
	public static final SpoofBlockName SPOOF_BLOCK_NAME = new SpoofBlockName();
	public static final SpoofModName SPOOF_MOD_NAME = new SpoofModName();
	private final List<Class<?>> isRegistered = new ArrayList<>();

	@Override
	public void registerClient(IWailaClientRegistration registration) {
		registration.addConfig(SHOW_OWNER, true);
		registration.addConfig(SHOW_MODULES, true);
		registration.addConfig(SHOW_PASSWORDS, true);
		registration.addConfig(SHOW_CUSTOM_NAME, true);

		for (RegistryObject<Block> registryObject : SCContent.BLOCKS.getEntries()) {
			Block block = registryObject.get();

			if (!(block instanceof OwnableBlock) && !Utils.getRegistryName(block).getPath().matches("(?!(reinforced_)).*?crystal_.*") && !(block instanceof ReinforcedCauldronBlock) && !(block instanceof ReinforcedPaneBlock)) {
				if (!isRegistered.contains(block.getClass())) {
					registration.registerBlockComponent(SECURITYCRAFT_INFO, block.getClass());
					isRegistered.add(block.getClass());
				}
			}

			if (block instanceof IOverlayDisplay)
				registration.usePickedResult(block);
		}

		registration.addBeforeRenderCallback((tooltip, rect, poseStack, accessor, color) -> ClientHandler.isPlayerMountedOnCamera());
		registration.registerBlockComponent(SPOOF_BLOCK_NAME, BaseFullMineBlock.class);
		registration.registerBlockComponent(SPOOF_BLOCK_NAME, FurnaceMineBlock.class);
		registration.registerBlockComponent(SECURITYCRAFT_INFO, OwnableBlock.class);
		registration.registerBlockComponent(SECURITYCRAFT_INFO, InventoryScannerFieldBlock.class);
		registration.registerBlockComponent(SECURITYCRAFT_INFO, LaserFieldBlock.class);
		registration.registerBlockComponent(SECURITYCRAFT_INFO, ReinforcedCauldronBlock.class);
		registration.registerBlockComponent(SECURITYCRAFT_INFO, ReinforcedPaneBlock.class);
		registration.registerBlockIcon(SPOOF_BLOCK_ICON, DisguisableBlock.class);
		registration.registerBlockIcon(SPOOF_BLOCK_ICON, BaseFullMineBlock.class);
		registration.registerBlockIcon(SPOOF_BLOCK_ICON, FurnaceMineBlock.class);
		registration.registerEntityComponent(SECURITYCRAFT_INFO, Sentry.class);
		registration.registerBlockComponent(SPOOF_MOD_NAME, BaseFullMineBlock.class);
		registration.registerBlockComponent(SPOOF_MOD_NAME, FurnaceMineBlock.class);
		isRegistered.clear();
	}

	private static class SpoofBlockIcon implements IBlockComponentProvider {
		@Override
		public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {}

		@Override
		public IElement getIcon(BlockAccessor data, IPluginConfig config, IElement currentIcon) {
			if (data.getBlock() instanceof IOverlayDisplay display)
				return ItemStackElement.of(display.getDisplayStack(data.getLevel(), data.getBlockState(), data.getPosition()));

			return ItemStackElement.EMPTY;
		}

		@Override
		public ResourceLocation getUid() {
			return new ResourceLocation(SecurityCraft.MODID, "spoof_block_icon");
		}
	}

	private static class SecurityCraftInfo implements IBlockComponentProvider, IEntityComponentProvider {
		@Override
		public void appendTooltip(ITooltip tooltip, BlockAccessor data, IPluginConfig config) {
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
				if (config.get(SHOW_OWNER) && be instanceof IOwnable ownable && Utils.getRegistryName(block).getNamespace().equals(SecurityCraft.MODID))
					tooltip.add(Utils.localize("waila.securitycraft:owner", PlayerUtils.getOwnerComponent(ownable.getOwner().getName())));

				if (disguised)
					return;

				//if the te is ownable, show modules only when it's owned, otherwise always show
				if (config.get(SHOW_MODULES) && be instanceof IModuleInventory inv && (!(be instanceof IOwnable ownable) || ownable.getOwner().isOwner(data.getPlayer()))) {
					if (!inv.getInsertedModules().isEmpty())
						tooltip.add(EQUIPPED);

					for (ModuleType module : inv.getInsertedModules()) {
						tooltip.add(Component.literal("- ").append(Component.translatable(module.getTranslationKey())));
					}
				}

				if (config.get(SHOW_PASSWORDS) && be instanceof IPasswordProtected ipp && ((IOwnable) be).getOwner().isOwner(data.getPlayer())) {
					String password = ipp.getPassword();

					tooltip.add(Utils.localize("waila.securitycraft:password", (password != null && !password.isEmpty() ? password : Utils.localize("waila.securitycraft:password.notSet"))));
				}
			}
		}

		@Override
		public void appendTooltip(ITooltip tooltip, EntityAccessor data, IPluginConfig config) {
			Entity entity = data.getEntity();

			if (entity instanceof Sentry sentry) {
				SentryMode mode = sentry.getMode();

				if (config.get(SHOW_OWNER))
					tooltip.add(Utils.localize("waila.securitycraft:owner", PlayerUtils.getOwnerComponent(sentry.getOwner().getName())));

				if (config.get(SHOW_MODULES) && sentry.getOwner().isOwner(data.getPlayer())) {
					if (!sentry.getAllowlistModule().isEmpty() || !sentry.getDisguiseModule().isEmpty() || sentry.hasSpeedModule()) {
						tooltip.add(EQUIPPED);

						if (!sentry.getAllowlistModule().isEmpty())
							tooltip.add(ALLOWLIST_MODULE);

						if (!sentry.getDisguiseModule().isEmpty())
							tooltip.add(DISGUISE_MODULE);

						if (sentry.hasSpeedModule())
							tooltip.add(SPEED_MODULE);
					}
				}

				MutableComponent modeDescription = Utils.localize(mode.getModeKey());

				if (mode != SentryMode.IDLE)
					modeDescription.append("- ").append(Utils.localize(mode.getTargetKey()));

				tooltip.add(modeDescription);
			}
		}

		@Override
		public ResourceLocation getUid() {
			return new ResourceLocation(SecurityCraft.MODID, "info");
		}
	}

	private static class SpoofBlockName implements IBlockComponentProvider {
		@Override
		public void appendTooltip(ITooltip tooltip, BlockAccessor data, IPluginConfig config) {
			if (tooltip instanceof Tooltip head)
				head.lines.get(0).getAlignedElements(Align.LEFT).set(0, new TextElement(Component.translatable(((IOverlayDisplay) data.getBlock()).getDisplayStack(data.getLevel(), data.getBlockState(), data.getPosition()).getDescriptionId()).setStyle(ITEM_NAME_STYLE)));
		}

		@Override
		public int getDefaultPriority() {
			return TooltipPosition.HEAD;
		}

		@Override
		public ResourceLocation getUid() {
			return new ResourceLocation(SecurityCraft.MODID, "spoof_block_name");
		}
	}

	private static class SpoofModName implements IBlockComponentProvider {
		@Override
		public void appendTooltip(ITooltip tooltip, BlockAccessor data, IPluginConfig config) {
			if (tooltip instanceof Tooltip tail) {
				ItemStack disguisedAs = ((IOverlayDisplay) data.getBlock()).getDisplayStack(data.getLevel(), data.getBlockState(), data.getPosition());
				Component modName = Component.literal(ModList.get().getModContainerById(Utils.getRegistryName(disguisedAs.getItem()).getNamespace()).get().getModInfo().getDisplayName()).setStyle(MOD_NAME_STYLE);

				tail.lines.get(tail.lines.size() - 1).getAlignedElements(Align.LEFT).set(0, new TextElement(modName));
			}
		}

		@Override
		public int getDefaultPriority() {
			return TooltipPosition.TAIL;
		}

		@Override
		public ResourceLocation getUid() {
			return new ResourceLocation(SecurityCraft.MODID, "spoof_mod_name");
		}
	}
}
