package net.geforcemods.securitycraft.compat.waila;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

public sealed class WailaCompatConstants permits JadeDataProvider, WTHITDataProvider {
	protected static final ResourceLocation SHOW_OWNER = new ResourceLocation(SecurityCraft.MODID, "showowner");
	protected static final ResourceLocation SHOW_MODULES = new ResourceLocation(SecurityCraft.MODID, "showmodules");
	protected static final ResourceLocation SHOW_CUSTOM_NAME = new ResourceLocation(SecurityCraft.MODID, "showcustomname");
	protected static final Style MOD_NAME_STYLE = Style.EMPTY.applyFormat(ChatFormatting.BLUE).withItalic(true);
	protected static final Style ITEM_NAME_STYLE = Style.EMPTY.applyFormat(ChatFormatting.WHITE);
	protected static final MutableComponent EQUIPPED = Utils.localize("waila.securitycraft:equipped").withStyle(Utils.GRAY_STYLE);
	protected static final MutableComponent ALLOWLIST_MODULE = Component.literal("- ").append(Component.translatable(ModuleType.ALLOWLIST.getTranslationKey())).withStyle(Utils.GRAY_STYLE);
	protected static final MutableComponent DISGUISE_MODULE = Component.literal("- ").append(Component.translatable(ModuleType.DISGUISE.getTranslationKey())).withStyle(Utils.GRAY_STYLE);
	protected static final MutableComponent SPEED_MODULE = Component.literal("- ").append(Component.translatable(ModuleType.SPEED.getTranslationKey())).withStyle(Utils.GRAY_STYLE);

	protected WailaCompatConstants() {}
}
