package net.geforcemods.securitycraft.compat.hudmods;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blocks.FakeLavaBlock;
import net.geforcemods.securitycraft.blocks.FakeWaterBlock;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.entity.SecuritySeaBoat;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
public final class JadeDataProvider extends HudModHandler implements IWailaPlugin {
	@Override
	public void registerClient(IWailaClientRegistration registration) {
		registration.addConfig(SHOW_OWNER, true);
		registration.addConfig(SHOW_MODULES, true);
		registration.addConfig(SHOW_CUSTOM_NAME, true);

		registration.registerBlockComponent(new SecurityCraftBlockInfo(), Block.class);
		registration.registerEntityComponent(new SecurityCraftEntityInfo(), Sentry.class);
		registration.registerEntityComponent(new SecurityCraftEntityInfo(), SecuritySeaBoat.class);

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

	private class SecurityCraftBlockInfo implements IBlockComponentProvider {
		@Override
		public void appendTooltip(ITooltip tooltip, BlockAccessor data, IPluginConfig config) {
			Level level = data.getLevel();
			BlockPos pos = data.getPosition();
			BlockState state = data.getBlockState();
			Block block = data.getBlock();

			addDisguisedOwnerModuleNameInfo(level, pos, state, block, data.getBlockEntity(), data.getPlayer(), tooltip::add, config::get);

			if (tooltip instanceof Tooltip t && block instanceof IOverlayDisplay overlayDisplay)
				t.lines.get(0).alignedElements(Align.LEFT).set(0, new TextElement(Component.translatable(overlayDisplay.getDisplayStack(level, state, pos).getDescriptionId()).setStyle(ITEM_NAME_STYLE)));
		}

		@Override
		public ResourceLocation getUid() {
			return new ResourceLocation(SecurityCraft.MODID, "block_info");
		}
	}

	private class SecurityCraftEntityInfo implements IEntityComponentProvider {
		@Override
		public void appendTooltip(ITooltip tooltip, EntityAccessor data, IPluginConfig config) {
			addEntityInfo(data.getEntity(), data.getPlayer(), tooltip::add, config::get);
		}

		@Override
		public ResourceLocation getUid() {
			return new ResourceLocation(SecurityCraft.MODID, "entity_info");
		}
	}
}
