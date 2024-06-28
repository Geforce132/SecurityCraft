package net.geforcemods.securitycraft.compat.hudmods;

import java.util.function.Consumer;
import java.util.function.Predicate;

import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.entity.sentry.Sentry.SentryMode;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;

public class HudModHandler {
	protected static final String SHOW_OWNER = "securitycraft.showowner";
	protected static final String SHOW_MODULES = "securitycraft.showmodules";
	protected static final String SHOW_CUSTOM_NAME = "securitycraft.showcustomname";

	protected HudModHandler() {}

	public void addOwnerModuleNameInfo(World level, BlockPos pos, IBlockState state, Block block, TileEntity be, EntityPlayer player, Consumer<String> lineAdder, Predicate<String> configGetter) {
		boolean disguised = false;

		if (block instanceof IDisguisable) {
			IBlockState disguisedBlockState = ((IDisguisable) block).getDisguisedBlockState(level, pos);

			if (disguisedBlockState != null) {
				disguised = true;
				block = disguisedBlockState.getBlock();
			}
		}

		if (be == null || disguised || block instanceof IOverlayDisplay && !((IOverlayDisplay) block).shouldShowSCInfo(level, state, pos))
			return;

		if (configGetter.test(SHOW_OWNER) && be instanceof IOwnable)
			lineAdder.accept(Utils.localize("waila.securitycraft:owner", PlayerUtils.getOwnerComponent(((IOwnable) be).getOwner())).getFormattedText());

		//if the te is ownable, show modules only when it's owned, otherwise always show
		if (configGetter.test(SHOW_MODULES) && be instanceof IModuleInventory && !((IModuleInventory) be).getInsertedModules().isEmpty() && (!(be instanceof IOwnable) || ((IOwnable) be).isOwnedBy(player))) {
			lineAdder.accept(Utils.localize("waila.securitycraft:equipped").getFormattedText());

			for (ModuleType module : ((IModuleInventory) be).getInsertedModules()) {
				lineAdder.accept("- " + Utils.localize(module.getTranslationKey()).getFormattedText());
			}
		}

		if (configGetter.test(SHOW_CUSTOM_NAME) && be instanceof IWorldNameable && ((IWorldNameable) be).hasCustomName()) {
			String name = ((IWorldNameable) be).getName();

			lineAdder.accept(Utils.localize("waila.securitycraft:customName").getFormattedText() + " " + name);
		}
	}

	public void addEntityInfo(Entity entity, EntityPlayer player, Consumer<String> lineAdder, Predicate<String> configGetter) {
		if (entity instanceof Sentry) {
			Sentry sentry = (Sentry) entity;
			SentryMode mode = sentry.getMode();

			if (configGetter.test(SHOW_OWNER))
				lineAdder.accept(Utils.localize("waila.securitycraft:owner", PlayerUtils.getOwnerComponent(sentry.getOwner())).getFormattedText());

			if (configGetter.test(SHOW_MODULES) && sentry.isOwnedBy(player) && (!sentry.getAllowlistModule().isEmpty() || !sentry.getDisguiseModule().isEmpty() || sentry.hasSpeedModule())) {
				lineAdder.accept(Utils.localize("waila.securitycraft:equipped").getFormattedText());

				if (!sentry.getAllowlistModule().isEmpty())
					lineAdder.accept("- " + Utils.localize(ModuleType.ALLOWLIST.getTranslationKey()).getFormattedText());

				if (!sentry.getDisguiseModule().isEmpty())
					lineAdder.accept("- " + Utils.localize(ModuleType.DISGUISE.getTranslationKey()).getFormattedText());

				if (sentry.hasSpeedModule())
					lineAdder.accept("- " + Utils.localize(ModuleType.SPEED.getTranslationKey()).getFormattedText());
			}

			String modeDescription = Utils.localize(mode.getModeKey()).getFormattedText();

			if (mode != SentryMode.IDLE)
				modeDescription += " - " + Utils.localize(mode.getTargetKey()).getFormattedText();

			lineAdder.accept(modeDescription);
		}
	}
}
