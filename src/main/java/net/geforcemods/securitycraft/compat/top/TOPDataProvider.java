package net.geforcemods.securitycraft.compat.top;

import java.util.function.Function;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeHitEntityData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoEntityProvider;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
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
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;

public class TOPDataProvider implements Function<ITheOneProbe, Void> {
	private static final MutableComponent EQUIPPED = Utils.localize("waila.securitycraft:equipped").withStyle(ChatFormatting.GRAY);
	private static final MutableComponent ALLOWLIST_MODULE = Component.literal("- ").append(Component.translatable(ModuleType.ALLOWLIST.getTranslationKey())).withStyle(ChatFormatting.GRAY);
	private static final MutableComponent DISGUISE_MODULE = Component.literal("- ").append(Component.translatable(ModuleType.DISGUISE.getTranslationKey())).withStyle(ChatFormatting.GRAY);
	private static final MutableComponent SPEED_MODULE = Component.literal("- ").append(Component.translatable(ModuleType.SPEED.getTranslationKey())).withStyle(ChatFormatting.GRAY);

	@Override
	public Void apply(ITheOneProbe theOneProbe) {
		theOneProbe.registerBlockDisplayOverride((mode, probeInfo, player, level, state, data) -> {
			ItemStack disguisedAs = ItemStack.EMPTY;

			if (state.getBlock() instanceof DisguisableBlock disguisedBlock)
				disguisedAs = disguisedBlock.getDisguisedStack(level, data.getPos());
			else if (state.getBlock() instanceof IOverlayDisplay display) {
				ItemStack displayStack = display.getDisplayStack(level, state, data.getPos());

				if (displayStack != null)
					disguisedAs = displayStack;
			}

			if (!disguisedAs.isEmpty()) {
				//@formatter:off
				probeInfo.horizontal()
				.item(disguisedAs)
				.vertical()
				.itemLabel(disguisedAs)
				.mcText(Component.literal(ModList.get().getModContainerById(Utils.getRegistryName(disguisedAs.getItem()).getNamespace()).get().getModInfo().getDisplayName()).withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC));
				return true;
				//@formatter:on
			}

			return false;
		});
		theOneProbe.registerProvider(new IProbeInfoProvider() {
			@Override
			public ResourceLocation getID() {
				return new ResourceLocation(SecurityCraft.MODID, SecurityCraft.MODID);
			}

			@Override
			public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level level, BlockState state, IProbeHitData data) {
				Block block = state.getBlock();

				if (block instanceof IOverlayDisplay display && !display.shouldShowSCInfo(level, state, data.getPos()))
					return;

				BlockEntity be = level.getBlockEntity(data.getPos());

				if (be instanceof IOwnable ownable)
					probeInfo.vertical().mcText(Utils.localize("waila.securitycraft:owner", PlayerUtils.getOwnerComponent(ownable.getOwner())).withStyle(ChatFormatting.GRAY));

				//if the te is ownable, show modules only when it's owned, otherwise always show
				if (be instanceof IModuleInventory inv && (!(be instanceof IOwnable ownable) || ownable.isOwnedBy(player)) && !inv.getInsertedModules().isEmpty()) {
					probeInfo.mcText(EQUIPPED);

					for (ModuleType module : inv.getInsertedModules()) {
						probeInfo.mcText(Component.literal("- ").append(Component.translatable(module.getTranslationKey())).withStyle(ChatFormatting.GRAY));
					}
				}

				if (be instanceof Nameable nameable && nameable.hasCustomName()) {
					Component text = nameable.getCustomName();

					probeInfo.mcText(Utils.localize("waila.securitycraft:customName", text == null ? Component.empty() : text).withStyle(ChatFormatting.GRAY));
				}
			}
		});
		theOneProbe.registerEntityProvider(new IProbeInfoEntityProvider() {
			@Override
			public String getID() {
				return SecurityCraft.MODID + ":" + SecurityCraft.MODID;
			}

			@Override
			public void addProbeEntityInfo(ProbeMode probeMode, IProbeInfo probeInfo, Player player, Level level, Entity entity, IProbeHitEntityData data) {
				if (entity instanceof Sentry sentry) {
					SentryMode mode = sentry.getMode();

					probeInfo.mcText(Utils.localize("waila.securitycraft:owner", PlayerUtils.getOwnerComponent(sentry.getOwner())).withStyle(ChatFormatting.GRAY));

					if (!sentry.getAllowlistModule().isEmpty() || !sentry.getDisguiseModule().isEmpty() || sentry.hasSpeedModule()) {
						probeInfo.mcText(EQUIPPED);

						if (!sentry.getAllowlistModule().isEmpty())
							probeInfo.mcText(ALLOWLIST_MODULE);

						if (!sentry.getDisguiseModule().isEmpty())
							probeInfo.mcText(DISGUISE_MODULE);

						if (sentry.hasSpeedModule())
							probeInfo.mcText(SPEED_MODULE);
					}

					MutableComponent modeDescription = Utils.localize(mode.getModeKey());

					if (mode != SentryMode.IDLE)
						modeDescription.append("- ").append(Utils.localize(mode.getTargetKey()));

					probeInfo.mcText(modeDescription.withStyle(ChatFormatting.GRAY));
				}
			}
		});
		return null;
	}
}