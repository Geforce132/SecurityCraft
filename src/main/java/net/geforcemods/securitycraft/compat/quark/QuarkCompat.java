package net.geforcemods.securitycraft.compat.quark;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.geforcemods.securitycraft.blocks.KeypadChestBlock;
import net.minecraft.block.Block;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ObjectHolder;

public class QuarkCompat
{
	@ObjectHolder("quark:oak_chest")
	private static Block oakChest;
	@ObjectHolder("quark:spruce_chest")
	private static Block spruceChest;
	@ObjectHolder("quark:birch_chest")
	private static Block birchChest;
	@ObjectHolder("quark:jungle_chest")
	private static Block jungleChest;
	@ObjectHolder("quark:acacia_chest")
	private static Block acaciaChest;
	@ObjectHolder("quark:dark_oak_chest")
	private static Block darkOakChest;
	@ObjectHolder("quark:crimson_chest")
	private static Block crimsonChest;
	@ObjectHolder("quark:warped_chest")
	private static Block warpedChest;

	public static void registerChestConversions()
	{
		InterModComms.sendTo(SecurityCraft.MODID, SecurityCraftAPI.IMC_PASSWORD_CONVERTIBLE_MSG, () -> new KeypadChestBlock.Convertible() {
			@Override
			public Block getOriginalBlock()
			{
				return oakChest;
			}
		});
		InterModComms.sendTo(SecurityCraft.MODID, SecurityCraftAPI.IMC_PASSWORD_CONVERTIBLE_MSG, () -> new KeypadChestBlock.Convertible() {
			@Override
			public Block getOriginalBlock()
			{
				return spruceChest;
			}
		});
		InterModComms.sendTo(SecurityCraft.MODID, SecurityCraftAPI.IMC_PASSWORD_CONVERTIBLE_MSG, () -> new KeypadChestBlock.Convertible() {
			@Override
			public Block getOriginalBlock()
			{
				return birchChest;
			}
		});
		InterModComms.sendTo(SecurityCraft.MODID, SecurityCraftAPI.IMC_PASSWORD_CONVERTIBLE_MSG, () -> new KeypadChestBlock.Convertible() {
			@Override
			public Block getOriginalBlock()
			{
				return jungleChest;
			}
		});
		InterModComms.sendTo(SecurityCraft.MODID, SecurityCraftAPI.IMC_PASSWORD_CONVERTIBLE_MSG, () -> new KeypadChestBlock.Convertible() {
			@Override
			public Block getOriginalBlock()
			{
				return acaciaChest;
			}
		});
		InterModComms.sendTo(SecurityCraft.MODID, SecurityCraftAPI.IMC_PASSWORD_CONVERTIBLE_MSG, () -> new KeypadChestBlock.Convertible() {
			@Override
			public Block getOriginalBlock()
			{
				return darkOakChest;
			}
		});
		InterModComms.sendTo(SecurityCraft.MODID, SecurityCraftAPI.IMC_PASSWORD_CONVERTIBLE_MSG, () -> new KeypadChestBlock.Convertible() {
			@Override
			public Block getOriginalBlock()
			{
				return crimsonChest;
			}
		});
		InterModComms.sendTo(SecurityCraft.MODID, SecurityCraftAPI.IMC_PASSWORD_CONVERTIBLE_MSG, () -> new KeypadChestBlock.Convertible() {
			@Override
			public Block getOriginalBlock()
			{
				return warpedChest;
			}
		});
	}
}
