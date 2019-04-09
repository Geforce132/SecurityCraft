package net.geforcemods.securitycraft.misc;

import net.geforcemods.securitycraft.ConfigHandler.ServerConfig;
import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;

public class KeycardConditions
{
	public static void registerAll()
	{
		CraftingHelper.register(new ResourceLocation(SecurityCraft.MODID, "toggle_keycard_1"),  json -> () -> ServerConfig.CONFIG.ableToCraftKeycard1.get());
		CraftingHelper.register(new ResourceLocation(SecurityCraft.MODID, "toggle_keycard_2"),  json -> () -> ServerConfig.CONFIG.ableToCraftKeycard2.get());
		CraftingHelper.register(new ResourceLocation(SecurityCraft.MODID, "toggle_keycard_3"),  json -> () -> ServerConfig.CONFIG.ableToCraftKeycard3.get());
		CraftingHelper.register(new ResourceLocation(SecurityCraft.MODID, "toggle_keycard_4"),  json -> () -> ServerConfig.CONFIG.ableToCraftKeycard4.get());
		CraftingHelper.register(new ResourceLocation(SecurityCraft.MODID, "toggle_keycard_5"),  json -> () -> ServerConfig.CONFIG.ableToCraftKeycard5.get());
		CraftingHelper.register(new ResourceLocation(SecurityCraft.MODID, "toggle_lu_keycard"),  json -> () -> ServerConfig.CONFIG.ableToCraftLUKeycard.get());
	}
}
