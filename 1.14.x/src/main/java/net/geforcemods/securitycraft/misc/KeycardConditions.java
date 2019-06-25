package net.geforcemods.securitycraft.misc;

import net.geforcemods.securitycraft.ConfigHandler.CommonConfig;
import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;

public class KeycardConditions
{
	public static void registerAll()
	{
		CraftingHelper.register(new ResourceLocation(SecurityCraft.MODID, "toggle_keycard_1"),  json -> () -> CommonConfig.CONFIG.ableToCraftKeycard1.get());
		CraftingHelper.register(new ResourceLocation(SecurityCraft.MODID, "toggle_keycard_2"),  json -> () -> CommonConfig.CONFIG.ableToCraftKeycard2.get());
		CraftingHelper.register(new ResourceLocation(SecurityCraft.MODID, "toggle_keycard_3"),  json -> () -> CommonConfig.CONFIG.ableToCraftKeycard3.get());
		CraftingHelper.register(new ResourceLocation(SecurityCraft.MODID, "toggle_keycard_4"),  json -> () -> CommonConfig.CONFIG.ableToCraftKeycard4.get());
		CraftingHelper.register(new ResourceLocation(SecurityCraft.MODID, "toggle_keycard_5"),  json -> () -> CommonConfig.CONFIG.ableToCraftKeycard5.get());
		CraftingHelper.register(new ResourceLocation(SecurityCraft.MODID, "toggle_lu_keycard"),  json -> () -> CommonConfig.CONFIG.ableToCraftLUKeycard.get());
	}
}
