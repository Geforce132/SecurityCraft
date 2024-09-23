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
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class HudModHandler {
	protected static final ResourceLocation SHOW_OWNER = new ResourceLocation(SecurityCraft.MODID, "showowner");
	protected static final ResourceLocation SHOW_MODULES = new ResourceLocation(SecurityCraft.MODID, "showmodules");
	protected static final ResourceLocation SHOW_CUSTOM_NAME = new ResourceLocation(SecurityCraft.MODID, "showcustomname");
	protected static final Style MOD_NAME_STYLE = Style.EMPTY.applyFormat(ChatFormatting.BLUE).withItalic(true);
	protected static final Style ITEM_NAME_STYLE = Style.EMPTY.applyFormat(ChatFormatting.WHITE);
	protected static final MutableComponent EQUIPPED = Utils.localize("waila.securitycraft:equipped").withStyle(ChatFormatting.GRAY);
	protected static final MutableComponent ALLOWLIST_MODULE = new TextComponent("- ").append(new TranslatableComponent(ModuleType.ALLOWLIST.getTranslationKey())).withStyle(ChatFormatting.GRAY);
	protected static final MutableComponent DISGUISE_MODULE = new TextComponent("- ").append(new TranslatableComponent(ModuleType.DISGUISE.getTranslationKey())).withStyle(ChatFormatting.GRAY);
	protected static final MutableComponent SPEED_MODULE = new TextComponent("- ").append(new TranslatableComponent(ModuleType.SPEED.getTranslationKey())).withStyle(ChatFormatting.GRAY);

	protected HudModHandler() {}

	public void addDisguisedOwnerModuleNameInfo(Level level, BlockPos pos, BlockState state, Block block, BlockEntity be, Player player, Consumer<Component> lineAdder, Predicate<ResourceLocation> configGetter) {
		if (be == null)
			return;

		if (block instanceof IDisguisable) {
			Optional<BlockState> disguisedBlockState = IDisguisable.getDisguisedBlockState(level, pos);

			if (disguisedBlockState.isPresent()) {
				BlockState disguisedState = disguisedBlockState.get();

				block = disguisedState.getBlock();

				if (block instanceof EntityBlock entityBlock && !(block instanceof IDisguisable) && entityBlock.newBlockEntity(pos, disguisedState) instanceof IOwnable)
					addOwnerInfo(be, lineAdder, configGetter);

				if (!(block instanceof IOverlayDisplay display) || !display.shouldShowSCInfo(level, state, pos))
					return;
			}
		}

		addOwnerModuleNameInfo(be, player, lineAdder, configGetter);
	}

	public void addOwnerInfo(Object obj, Consumer<Component> lineAdder, Predicate<ResourceLocation> configGetter) {
		if (configGetter.test(SHOW_OWNER) && obj instanceof IOwnable ownable)
			lineAdder.accept(Utils.localize("waila.securitycraft:owner", PlayerUtils.getOwnerComponent(ownable.getOwner())).withStyle(ChatFormatting.GRAY));
	}

	public void addOwnerModuleNameInfo(Object obj, Player player, Consumer<Component> lineAdder, Predicate<ResourceLocation> configGetter) {
		addOwnerInfo(obj, lineAdder, configGetter);

		//if the te is ownable, show modules only when it's owned, otherwise always show
		if (configGetter.test(SHOW_MODULES) && obj instanceof IModuleInventory inv && !inv.getInsertedModules().isEmpty() && (!(obj instanceof IOwnable ownable) || ownable.isOwnedBy(player))) {
			lineAdder.accept(EQUIPPED);

			for (ModuleType module : inv.getInsertedModules()) {
				MutableComponent prefix;

				if (inv.isModuleEnabled(module))
					prefix = new TextComponent("✔ ").withStyle(ChatFormatting.GREEN);
				else
					prefix = new TextComponent("✕ ").withStyle(ChatFormatting.RED);

				lineAdder.accept(prefix.append(new TranslatableComponent(module.getTranslationKey()).withStyle(ChatFormatting.GRAY)));
			}
		}

		if (configGetter.test(SHOW_CUSTOM_NAME) && obj instanceof Nameable nameable && nameable.hasCustomName()) {
			Component text = nameable.getCustomName();
			Component name = text == null ? TextComponent.EMPTY : text;

			lineAdder.accept(Utils.localize("waila.securitycraft:customName", name).withStyle(ChatFormatting.GRAY));
		}
	}

	public void addEntityInfo(Entity entity, Player player, Consumer<Component> lineAdder, Predicate<ResourceLocation> configGetter) {
		if (entity instanceof Sentry sentry) {
			SentryMode mode = sentry.getMode();

			addOwnerInfo(sentry, lineAdder, configGetter);

			if (configGetter.test(SHOW_MODULES) && sentry.isOwnedBy(player) && (!sentry.getAllowlistModule().isEmpty() || !sentry.getDisguiseModule().isEmpty() || sentry.hasSpeedModule())) {
				lineAdder.accept(EQUIPPED);

				if (!sentry.getAllowlistModule().isEmpty())
					lineAdder.accept(ALLOWLIST_MODULE);

				if (!sentry.getDisguiseModule().isEmpty())
					lineAdder.accept(DISGUISE_MODULE);

				if (sentry.hasSpeedModule())
					lineAdder.accept(SPEED_MODULE);
			}

			MutableComponent modeDescription = Utils.localize(mode.getModeKey());

			if (mode != SentryMode.IDLE)
				modeDescription.append("- ").append(Utils.localize(mode.getTargetKey()));

			lineAdder.accept(modeDescription.withStyle(ChatFormatting.GRAY));
		}
	}
}
