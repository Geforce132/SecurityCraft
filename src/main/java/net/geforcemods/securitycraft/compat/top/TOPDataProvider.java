package net.geforcemods.securitycraft.compat.top;

import java.util.function.Function;

import javax.annotation.Nullable;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeHitEntityData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoEntityProvider;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.blockentities.KeycardReaderBlockEntity;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.entity.Sentry;
import net.geforcemods.securitycraft.entity.Sentry.SentryMode;
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
import net.minecraft.world.scores.PlayerTeam;
import net.minecraftforge.fml.ModList;

public class TOPDataProvider implements Function<ITheOneProbe, Void> {
	private static final MutableComponent EQUIPPED = Component.literal("" + ChatFormatting.GRAY).append(Utils.localize("waila.securitycraft:equipped"));
	private static final MutableComponent ALLOWLIST_MODULE = Component.literal(ChatFormatting.GRAY + "- ").append(Component.translatable(ModuleType.ALLOWLIST.getTranslationKey()));
	private static final MutableComponent DISGUISE_MODULE = Component.literal(ChatFormatting.GRAY + "- ").append(Component.translatable(ModuleType.DISGUISE.getTranslationKey()));
	private static final MutableComponent SPEED_MODULE = Component.literal(ChatFormatting.GRAY + "- ").append(Component.translatable(ModuleType.SPEED.getTranslationKey()));

	@Nullable
	@Override
	public Void apply(ITheOneProbe theOneProbe) {
		theOneProbe.registerBlockDisplayOverride((mode, probeInfo, player, level, blockState, data) -> {
			ItemStack disguisedAs = ItemStack.EMPTY;

			if (blockState.getBlock() instanceof DisguisableBlock disguisedBlock)
				disguisedAs = disguisedBlock.getDisguisedStack(level, data.getPos());
			else if (blockState.getBlock() instanceof IOverlayDisplay display) {
				ItemStack displayStack = display.getDisplayStack(level, blockState, data.getPos());

				if (displayStack != null)
					disguisedAs = displayStack;
			}

			if (!disguisedAs.isEmpty()) {
				//@formatter:off
				probeInfo.horizontal()
				.item(disguisedAs)
				.vertical()
				.itemLabel(disguisedAs)
				.text(Component.literal("" + ChatFormatting.BLUE + ChatFormatting.ITALIC + ModList.get().getModContainerById(Utils.getRegistryName(disguisedAs.getItem()).getNamespace()).get().getModInfo().getDisplayName()));
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
			public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level level, BlockState blockState, IProbeHitData data) {
				Block block = blockState.getBlock();

				if (block instanceof IOverlayDisplay display && !display.shouldShowSCInfo(level, blockState, data.getPos()))
					return;

				BlockEntity be = level.getBlockEntity(data.getPos());

				if (be instanceof IOwnable ownable) {
					String ownerName = ownable.getOwner().getName();

					if (ConfigHandler.SERVER.enableTeamOwnership.get()) {
						PlayerTeam team = PlayerUtils.getPlayersTeam(ownerName);

						if (team != null)
							ownerName = Utils.localize("messages.securitycraft:teamOwner", team.getColor() + team.getDisplayName().getString() + ChatFormatting.GRAY).getString(); //TOP does not work with normal component formatting
					}

					probeInfo.vertical().text(Component.literal(ChatFormatting.GRAY + Utils.localize("waila.securitycraft:owner", ownerName).getString()));
				}

				//if the te is ownable, show modules only when it's owned, otherwise always show
				if (be instanceof IModuleInventory inv && (!(be instanceof IOwnable ownable) || ownable.getOwner().isOwner(player))) {
					if (!inv.getInsertedModules().isEmpty()) {
						probeInfo.text(EQUIPPED);

						for (ModuleType module : inv.getInsertedModules()) {
							probeInfo.text(Component.literal(ChatFormatting.GRAY + "- ").append(Component.translatable(module.getTranslationKey())));
						}
					}
				}

				if (be instanceof IPasswordProtected passwordProtected && !(be instanceof KeycardReaderBlockEntity) && ((IOwnable) be).getOwner().isOwner(player)) {
					String password = passwordProtected.getPassword();

					probeInfo.text(Component.literal(ChatFormatting.GRAY + Utils.localize("waila.securitycraft:password", (password != null && !password.isEmpty() ? password : Utils.localize("waila.securitycraft:password.notSet"))).getString()));
				}

				if (be instanceof Nameable nameable && nameable.hasCustomName()) {
					Component text = nameable.getCustomName();
					Component name = text == null ? Component.empty() : text;

					probeInfo.text(Component.literal(ChatFormatting.GRAY + Utils.localize("waila.securitycraft:customName", name).getString()));
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
					String ownerName = sentry.getOwner().getName();

					if (ConfigHandler.SERVER.enableTeamOwnership.get()) {
						PlayerTeam team = PlayerUtils.getPlayersTeam(ownerName);

						if (team != null)
							ownerName = Utils.localize("messages.securitycraft:teamOwner", team.getColor() + team.getDisplayName().getString() + ChatFormatting.GRAY).getString(); //TOP does not work with normal component formatting
					}

					probeInfo.text(Component.literal(ChatFormatting.GRAY + Utils.localize("waila.securitycraft:owner", ownerName).getString()));

					if (!sentry.getAllowlistModule().isEmpty() || !sentry.getDisguiseModule().isEmpty() || sentry.hasSpeedModule()) {
						probeInfo.text(EQUIPPED);

						if (!sentry.getAllowlistModule().isEmpty())
							probeInfo.text(ALLOWLIST_MODULE);

						if (!sentry.getDisguiseModule().isEmpty())
							probeInfo.text(DISGUISE_MODULE);

						if (sentry.hasSpeedModule())
							probeInfo.text(SPEED_MODULE);
					}

					MutableComponent modeDescription = Utils.localize(mode.getModeKey());

					if (mode != SentryMode.IDLE)
						modeDescription.append("- ").append(Utils.localize(mode.getTargetKey()));

					probeInfo.text(Component.literal(ChatFormatting.GRAY + modeDescription.getString()));
				}
			}
		});
		return null;
	}
}