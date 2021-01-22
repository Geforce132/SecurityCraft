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
	@ObjectHolder("quark:bamboo_chest")
	private static Block bambooChest;
	@ObjectHolder("quark:driftwood_chest")
	private static Block driftwoodChest;
	@ObjectHolder("quark:poise_chest")
	private static Block poiseChest;
	@ObjectHolder("quark:willow_chest")
	private static Block willowChest;
	@ObjectHolder("quark:wisteria_chest")
	private static Block wisteriaChest;

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
				return bambooChest;
			}
		});
		InterModComms.sendTo(SecurityCraft.MODID, SecurityCraftAPI.IMC_PASSWORD_CONVERTIBLE_MSG, () -> new KeypadChestBlock.Convertible() {
			@Override
			public Block getOriginalBlock()
			{
				return driftwoodChest;
			}
		});
		InterModComms.sendTo(SecurityCraft.MODID, SecurityCraftAPI.IMC_PASSWORD_CONVERTIBLE_MSG, () -> new KeypadChestBlock.Convertible() {
			@Override
			public Block getOriginalBlock()
			{
				return poiseChest;
			}
		});
		InterModComms.sendTo(SecurityCraft.MODID, SecurityCraftAPI.IMC_PASSWORD_CONVERTIBLE_MSG, () -> new KeypadChestBlock.Convertible() {
			@Override
			public Block getOriginalBlock()
			{
				return willowChest;
			}
		});
		InterModComms.sendTo(SecurityCraft.MODID, SecurityCraftAPI.IMC_PASSWORD_CONVERTIBLE_MSG, () -> new KeypadChestBlock.Convertible() {
			@Override
			public Block getOriginalBlock()
			{
				return wisteriaChest;
			}
		});
	}
}
