package net.geforcemods.securitycraft.compat.quark;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.geforcemods.securitycraft.blocks.BlockKeypadChest;
import net.minecraft.block.Block;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

public class QuarkCompat
{
	@ObjectHolder("quark:custom_chest")
	private static Block customChest;

	public static void registerChestConversion()
	{
		FMLInterModComms.sendFunctionMessage(SecurityCraft.MODID, SecurityCraftAPI.IMC_PASSWORD_CONVERTIBLE_MSG, Convertible.class.getName());
	}

	public static class Convertible extends BlockKeypadChest.Convertible
	{
		@Override
		public Block getOriginalBlock()
		{
			return customChest;
		}
	}
}
