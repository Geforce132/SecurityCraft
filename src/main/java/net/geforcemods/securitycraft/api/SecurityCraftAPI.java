package net.geforcemods.securitycraft.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blocks.BlockKeypad;
import net.geforcemods.securitycraft.blocks.BlockKeypadChest;
import net.geforcemods.securitycraft.blocks.BlockKeypadFurnace;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedHopper;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;

public class SecurityCraftAPI
{
	private static List<IExtractionBlock> registeredExtractionBlocks = new ArrayList<>();
	private static List<IAttackTargetCheck> registeredSentryAttackTargetChecks = new ArrayList<>();
	private static List<IPasswordConvertible> registeredPasswordConvertibles = new ArrayList<>();
	public static final String IMC_EXTRACTION_BLOCK_MSG = "registerExtractionBlock";
	public static final String IMC_SENTRY_ATTACK_TARGET_MSG = "registerSentryAttackTargetCheck";
	public static final String IMC_PASSWORD_CONVERTIBLE_MSG = "registerPasswordConvertible";

	public static void init()
	{
		FMLInterModComms.sendFunctionMessage(SecurityCraft.MODID, IMC_EXTRACTION_BLOCK_MSG, BlockReinforcedHopper.ExtractionBlock.class.getName());
		FMLInterModComms.sendFunctionMessage(SecurityCraft.MODID, IMC_PASSWORD_CONVERTIBLE_MSG, BlockKeypad.Convertible.class.getName());
		FMLInterModComms.sendFunctionMessage(SecurityCraft.MODID, IMC_PASSWORD_CONVERTIBLE_MSG, BlockKeypadChest.Convertible.class.getName());
		FMLInterModComms.sendFunctionMessage(SecurityCraft.MODID, IMC_PASSWORD_CONVERTIBLE_MSG, BlockKeypadFurnace.Convertible.class.getName());
	}

	public static void onIMC(IMCEvent event)
	{
		for(IMCMessage msg : event.getMessages())
		{
			if(msg.key.equals(IMC_EXTRACTION_BLOCK_MSG))
			{
				Optional<Function<Object,IExtractionBlock>> value = msg.getFunctionValue(Object.class, IExtractionBlock.class);

				if(value.isPresent())
					registeredExtractionBlocks.add(value.get().apply(null));
				else
					System.out.println(String.format("[ERROR] Mod %s did not supply sufficient extraction block information.", msg.getSender()));
			}
			else if(msg.key.equals(IMC_SENTRY_ATTACK_TARGET_MSG))
			{
				Optional<Function<Object,IAttackTargetCheck>> value = msg.getFunctionValue(Object.class, IAttackTargetCheck.class);

				if(value.isPresent())
					registeredSentryAttackTargetChecks.add(value.get().apply(null));
				else
					System.out.println(String.format("[ERROR] Mod %s did not supply sufficient sentry attack target information.", msg.getSender()));
			}
			else if(msg.key.equals(IMC_PASSWORD_CONVERTIBLE_MSG))
			{
				Optional<Function<Object,IPasswordConvertible>> value = msg.getFunctionValue(Object.class, IPasswordConvertible.class);

				if(value.isPresent())
					registeredPasswordConvertibles.add(value.get().apply(null));
				else
					System.out.println(String.format("[ERROR] Mod %s did not supply sufficient password convertible information.", msg.getSender()));
			}
		}

		registeredExtractionBlocks = Collections.unmodifiableList(registeredExtractionBlocks);
		registeredPasswordConvertibles = Collections.unmodifiableList(registeredPasswordConvertibles);
		registeredSentryAttackTargetChecks = Collections.unmodifiableList(registeredSentryAttackTargetChecks);
	}

	public static List<IExtractionBlock> getRegisteredExtractionBlocks()
	{
		return registeredExtractionBlocks;
	}

	public static List<IAttackTargetCheck> getRegisteredSentryAttackTargetChecks()
	{
		return registeredSentryAttackTargetChecks;
	}

	public static List<IPasswordConvertible> getRegisteredPasswordConvertibles()
	{
		return registeredPasswordConvertibles;
	}
}
