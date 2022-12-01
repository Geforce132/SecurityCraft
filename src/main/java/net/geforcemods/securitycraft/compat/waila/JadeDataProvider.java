package net.geforcemods.securitycraft.compat.waila;

import java.util.*;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.blocks.*;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
	public static final SecurityCraftInfo SECURITYCRAFT_INFO = new SecurityCraftInfo();

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerClient(IWailaClientRegistration registration) {
		registration.addConfig(SHOW_OWNER, true);
		registration.addConfig(SHOW_MODULES, true);
		registration.addConfig(SHOW_PASSWORDS, true);
		registration.addConfig(SHOW_CUSTOM_NAME, true);

		registration.registerBlockComponent(SECURITYCRAFT_INFO, Block.class);
		registration.registerEntityComponent(SECURITYCRAFT_INFO, Sentry.class);

		registration.addBeforeRenderCallback((tooltip, rect, poseStack, accessor, color) -> ClientHandler.isPlayerMountedOnCamera());
		registration.addRayTraceCallback((hit, accessor, original) -> {
			if (accessor instanceof BlockAccessor blockAccessor) {
				if (blockAccessor.getBlock() instanceof IOverlayDisplay block) {
					ItemStack fake = block.getDisplayStack(blockAccessor.getLevel(), blockAccessor.getBlockState(), blockAccessor.getPosition());
					return registration.blockAccessor().from(blockAccessor).fakeBlock(fake).build();
				}
				if (blockAccessor.getBlock() instanceof FakeWaterBlock) {
					BlockState camo = Blocks.WATER.defaultBlockState();
					return registration.blockAccessor().from(blockAccessor).blockState(camo).build();
				}
				if (blockAccessor.getBlock() instanceof FakeLavaBlock) {
					BlockState camo = Blocks.LAVA.defaultBlockState();
					return registration.blockAccessor().from(blockAccessor).blockState(camo).build();
				}
			}
			return accessor;
		});
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

}
