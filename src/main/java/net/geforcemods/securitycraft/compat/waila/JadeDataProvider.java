package net.geforcemods.securitycraft.compat.waila;

import java.util.Optional;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.blocks.FakeLavaBlock;
import net.geforcemods.securitycraft.blocks.FakeWaterBlock;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.entity.sentry.Sentry.SentryMode;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement.Align;
import snownee.jade.impl.Tooltip;
import snownee.jade.impl.ui.TextElement;

@WailaPlugin(SecurityCraft.MODID)
public final class JadeDataProvider extends WailaCompatConstants implements IWailaPlugin {
	@Override
	public void registerClient(IWailaClientRegistration registration) {
		registration.addConfig(SHOW_OWNER, true);
		registration.addConfig(SHOW_MODULES, true);
		registration.addConfig(SHOW_CUSTOM_NAME, true);

		registration.registerBlockComponent(new SecurityCraftBlockInfo(), Block.class);
		registration.registerEntityComponent(new SecurityCraftEntityInfo(), Sentry.class);

		registration.addBeforeRenderCallback((tooltip, rect, guiGraphics, accessor) -> ClientHandler.isPlayerMountedOnCamera());
		registration.addRayTraceCallback((hit, accessor, original) -> {
			if (accessor instanceof BlockAccessor blockAccessor) {
				Block block = blockAccessor.getBlock();

				if (block instanceof IOverlayDisplay overlayDisplay) {
					Level level = blockAccessor.getLevel();
					BlockState state = blockAccessor.getBlockState();
					BlockPos pos = blockAccessor.getPosition();

					if (!overlayDisplay.shouldShowSCInfo(level, state, pos))
						return registration.blockAccessor().from(blockAccessor).fakeBlock(overlayDisplay.getDisplayStack(level, state, pos)).build();
				}
				else if (block instanceof FakeWaterBlock)
					return registration.blockAccessor().from(blockAccessor).blockState(Blocks.WATER.defaultBlockState()).build();
				else if (block instanceof FakeLavaBlock)
					return registration.blockAccessor().from(blockAccessor).blockState(Blocks.LAVA.defaultBlockState()).build();
			}

			return accessor;
		});
	}

	private static class SecurityCraftBlockInfo implements IBlockComponentProvider {
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
				if (tooltip instanceof Tooltip t && data.getBlock() instanceof IOverlayDisplay overlayDisplay)
					t.lines.get(0).alignedElements(Align.LEFT).set(0, new TextElement(Component.translatable(overlayDisplay.getDisplayStack(data.getLevel(), data.getBlockState(), data.getPosition()).getDescriptionId()).setStyle(ITEM_NAME_STYLE)));

				//last part is a little cheaty to prevent owner info from being displayed on non-sc blocks
				if (config.get(SHOW_OWNER) && be instanceof IOwnable ownable && Utils.getRegistryName(block).getNamespace().equals(SecurityCraft.MODID))
					tooltip.add(Utils.localize("waila.securitycraft:owner", PlayerUtils.getOwnerComponent(ownable.getOwner())));

				if (disguised)
					return;

				//if the te is ownable, show modules only when it's owned, otherwise always show
				if (config.get(SHOW_MODULES) && be instanceof IModuleInventory inv && (!(be instanceof IOwnable ownable) || ownable.isOwnedBy(data.getPlayer()))) {
					if (!inv.getInsertedModules().isEmpty())
						tooltip.add(EQUIPPED);

					for (ModuleType module : inv.getInsertedModules()) {
						tooltip.add(Component.literal("- ").append(Component.translatable(module.getTranslationKey())));
					}
				}
			}
		}

		@Override
		public ResourceLocation getUid() {
			return new ResourceLocation(SecurityCraft.MODID, "block_info");
		}
	}

	private static class SecurityCraftEntityInfo implements IEntityComponentProvider {
		@Override
		public void appendTooltip(ITooltip tooltip, EntityAccessor data, IPluginConfig config) {
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

		@Override
		public ResourceLocation getUid() {
			return new ResourceLocation(SecurityCraft.MODID, "entity_info");
		}
	}
}
