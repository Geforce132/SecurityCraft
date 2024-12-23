package net.geforcemods.securitycraft.compat.hudmods;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

import net.geforcemods.securitycraft.SecurityCraft;
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
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.INameable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class HudModHandler {
	protected static final ResourceLocation SHOW_OWNER = new ResourceLocation(SecurityCraft.MODID, "showowner");
	protected static final ResourceLocation SHOW_MODULES = new ResourceLocation(SecurityCraft.MODID, "showmodules");
	protected static final ResourceLocation SHOW_CUSTOM_NAME = new ResourceLocation(SecurityCraft.MODID, "showcustomname");
	protected static final Style MOD_NAME_STYLE = Style.EMPTY.applyFormat(TextFormatting.BLUE).withItalic(true);
	protected static final Style ITEM_NAME_STYLE = Style.EMPTY.applyFormat(TextFormatting.WHITE);
	protected static final IFormattableTextComponent EQUIPPED = Utils.localize("waila.securitycraft:equipped").withStyle(TextFormatting.GRAY);
	protected static final IFormattableTextComponent ALLOWLIST_MODULE = new StringTextComponent("- ").append(new TranslationTextComponent(ModuleType.ALLOWLIST.getTranslationKey())).withStyle(TextFormatting.GRAY);
	protected static final IFormattableTextComponent DISGUISE_MODULE = new StringTextComponent("- ").append(new TranslationTextComponent(ModuleType.DISGUISE.getTranslationKey())).withStyle(TextFormatting.GRAY);
	protected static final IFormattableTextComponent SPEED_MODULE = new StringTextComponent("- ").append(new TranslationTextComponent(ModuleType.SPEED.getTranslationKey())).withStyle(TextFormatting.GRAY);

	protected HudModHandler() {}

	public void addDisguisedOwnerModuleNameInfo(World level, BlockPos pos, BlockState state, Block block, TileEntity be, PlayerEntity player, Consumer<ITextComponent> lineAdder, Predicate<ResourceLocation> configGetter) {
		if (be == null)
			return;

		if (block instanceof IDisguisable) {
			Optional<BlockState> disguisedBlockState = IDisguisable.getDisguisedBlockState(be);

			if (disguisedBlockState.isPresent()) {
				BlockState disguisedState = disguisedBlockState.get();

				block = disguisedState.getBlock();

				if (block.hasTileEntity(disguisedState) && !(block instanceof IDisguisable) && block.createTileEntity(disguisedState, level) instanceof IOwnable)
					addOwnerInfo(be, lineAdder, configGetter);

				if (!(block instanceof IOverlayDisplay) || !((IOverlayDisplay) block).shouldShowSCInfo(level, state, pos))
					return;
			}
		}

		if (!(block instanceof IBlockMine))
			addOwnerModuleNameInfo(be, player, lineAdder, configGetter);
	}

	public void addOwnerInfo(Object obj, Consumer<ITextComponent> lineAdder, Predicate<ResourceLocation> configGetter) {
		if (configGetter.test(SHOW_OWNER) && obj instanceof IOwnable)
			lineAdder.accept(Utils.localize("waila.securitycraft:owner", PlayerUtils.getOwnerComponent(((IOwnable) obj).getOwner())).withStyle(TextFormatting.GRAY));
	}

	public void addOwnerModuleNameInfo(Object obj, PlayerEntity player, Consumer<ITextComponent> lineAdder, Predicate<ResourceLocation> configGetter) {
		addOwnerInfo(obj, lineAdder, configGetter);

		//if the te is ownable, show modules only when it's owned, otherwise always show
		if (configGetter.test(SHOW_MODULES) && obj instanceof IModuleInventory && !((IModuleInventory) obj).getInsertedModules().isEmpty() && (!(obj instanceof IOwnable) || ((IOwnable) obj).isOwnedBy(player))) {
			IModuleInventory inv = (IModuleInventory) obj;

			lineAdder.accept(EQUIPPED);

			for (ModuleType module : inv.getInsertedModules()) {
				IFormattableTextComponent prefix;

				if (inv.isModuleEnabled(module))
					prefix = new StringTextComponent("✔ ").withStyle(TextFormatting.GREEN);
				else
					prefix = new StringTextComponent("✕ ").withStyle(TextFormatting.RED);

				lineAdder.accept(prefix.append(new TranslationTextComponent(module.getTranslationKey()).withStyle(TextFormatting.GRAY)));
			}
		}

		if (configGetter.test(SHOW_CUSTOM_NAME) && obj instanceof INameable && ((INameable) obj).hasCustomName()) {
			ITextComponent text = ((INameable) obj).getCustomName();
			ITextComponent name = text == null ? StringTextComponent.EMPTY : text;

			lineAdder.accept(Utils.localize("waila.securitycraft:customName", name).withStyle(TextFormatting.GRAY));
		}
	}

	public void addEntityInfo(Entity entity, PlayerEntity player, Consumer<ITextComponent> lineAdder, Predicate<ResourceLocation> configGetter) {
		if (entity instanceof Sentry) {
			Sentry sentry = (Sentry) entity;
			SentryMode mode = sentry.getMode();

			if (configGetter.test(SHOW_OWNER))
				lineAdder.accept(Utils.localize("waila.securitycraft:owner", PlayerUtils.getOwnerComponent(sentry.getOwner())).withStyle(TextFormatting.GRAY));

			if (configGetter.test(SHOW_MODULES) && sentry.isOwnedBy(player) && (!sentry.getAllowlistModule().isEmpty() || !sentry.getDisguiseModule().isEmpty() || sentry.hasSpeedModule())) {
				lineAdder.accept(EQUIPPED);

				if (!sentry.getAllowlistModule().isEmpty())
					lineAdder.accept(ALLOWLIST_MODULE);

				if (!sentry.getDisguiseModule().isEmpty())
					lineAdder.accept(DISGUISE_MODULE);

				if (sentry.hasSpeedModule())
					lineAdder.accept(SPEED_MODULE);
			}

			IFormattableTextComponent modeDescription = Utils.localize(mode.getModeKey());

			if (mode != SentryMode.IDLE)
				modeDescription.append("- ").append(Utils.localize(mode.getTargetKey()));

			lineAdder.accept(modeDescription.withStyle(TextFormatting.GRAY));
		}
	}
}
