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
import net.geforcemods.securitycraft.util.IBlockMine;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;

public class HudModHandler {
	protected static final String SHOW_OWNER = "securitycraft.showowner";
	protected static final String SHOW_MODULES = "securitycraft.showmodules";
	protected static final String SHOW_CUSTOM_NAME = "securitycraft.showcustomname";

	protected HudModHandler() {}

	public void addDisguisedOwnerModuleNameInfo(World level, BlockPos pos, IBlockState state, Block block, TileEntity be, EntityPlayer player, Consumer<String> lineAdder, Predicate<String> configGetter) {
		if (be == null)
			return;

		if (block instanceof IDisguisable) {
			IBlockState disguisedState = ((IDisguisable) block).getDisguisedBlockState(level, pos);

			if (disguisedState != null) {
				block = disguisedState.getBlock();

				if (block.hasTileEntity(disguisedState) && !(block instanceof IDisguisable) && block.createTileEntity(level, disguisedState) instanceof IOwnable)
					addOwnerInfo(be, lineAdder, configGetter);

				if (!(block instanceof IOverlayDisplay) || !((IOverlayDisplay) block).shouldShowSCInfo(level, state, pos))
					return;
			}
		}

		if (!(block instanceof IBlockMine))
			addOwnerModuleNameInfo(be, player, lineAdder, configGetter);
	}

	public void addOwnerInfo(Object obj, Consumer<String> lineAdder, Predicate<String> configGetter) {
		if (configGetter.test(SHOW_OWNER) && obj instanceof IOwnable)
			lineAdder.accept(Utils.localize("waila.securitycraft:owner", PlayerUtils.getOwnerComponent(((IOwnable) obj).getOwner())).getFormattedText());
	}

	public void addOwnerModuleNameInfo(Object obj, EntityPlayer player, Consumer<String> lineAdder, Predicate<String> configGetter) {
		addOwnerInfo(obj, lineAdder, configGetter);

		//if the te is ownable, show modules only when it's owned, otherwise always show
		if (configGetter.test(SHOW_MODULES) && obj instanceof IModuleInventory && !((IModuleInventory) obj).getInsertedModules().isEmpty() && (!(obj instanceof IOwnable) || ((IOwnable) obj).isOwnedBy(player))) {
			IModuleInventory inv = (IModuleInventory) obj;

			lineAdder.accept(Utils.localize("waila.securitycraft:equipped").getFormattedText());

			for (ModuleType module : inv.getInsertedModules()) {
				ITextComponent prefix;

				if (inv.isModuleEnabled(module))
					prefix = new TextComponentString("✔ ").setStyle(new Style().setColor(TextFormatting.GREEN));
				else
					prefix = new TextComponentString("✕ ").setStyle(new Style().setColor(TextFormatting.RED));

				lineAdder.accept(prefix.appendSibling(new TextComponentTranslation(module.getTranslationKey()).setStyle(Utils.GRAY_STYLE)).getFormattedText());
			}
		}

		if (configGetter.test(SHOW_CUSTOM_NAME) && obj instanceof IWorldNameable && ((IWorldNameable) obj).hasCustomName()) {
			String name = ((IWorldNameable) obj).getName();

			lineAdder.accept(Utils.localize("waila.securitycraft:customName").getFormattedText() + " " + name);
		}
	}

	public void addEntityInfo(Entity entity, EntityPlayer player, Consumer<String> lineAdder, Predicate<String> configGetter) {
		if (entity instanceof Sentry) {
			Sentry sentry = (Sentry) entity;
			SentryMode mode = sentry.getMode();

			addOwnerInfo(sentry, lineAdder, configGetter);

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
