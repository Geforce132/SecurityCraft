package net.geforcemods.securitycraft.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;

@EventBusSubscriber(modid=SecurityCraft.MODID, bus=Bus.MOD)
public class SecurityCraftAPI
{
	private static List<IExtractionBlock> registeredExtractionBlocks = new ArrayList<>();
	private static List<IPasswordConvertible> registeredPasswordConvertibles = new ArrayList<>();
	private static List<IAttackTargetCheck> registeredSentryAttackTargetChecks = new ArrayList<>();
	private static List<IDoorActivator> registeredDoorActivators = new ArrayList<>();
	public static final String IMC_EXTRACTION_BLOCK_MSG = "registerExtractionBlock";
	public static final String IMC_PASSWORD_CONVERTIBLE_MSG = "registerPasswordConvertible";
	public static final String IMC_SENTRY_ATTACK_TARGET_MSG = "registerSentryAttackTargetCheck";
	public static final String IMC_DOOR_ACTIVATOR_MSG = "registerDoorActivator";

	@SubscribeEvent
	public static void onInterModProcess(InterModProcessEvent event)
	{
		event.getIMCStream(s -> s.equals(IMC_EXTRACTION_BLOCK_MSG)).forEach(msg -> registeredExtractionBlocks.add((IExtractionBlock)msg.getMessageSupplier().get()));
		event.getIMCStream(s -> s.equals(IMC_PASSWORD_CONVERTIBLE_MSG)).forEach(msg -> registeredPasswordConvertibles.add((IPasswordConvertible)msg.getMessageSupplier().get()));
		event.getIMCStream(s -> s.equals(IMC_SENTRY_ATTACK_TARGET_MSG)).forEach(msg -> registeredSentryAttackTargetChecks.add((IAttackTargetCheck)msg.getMessageSupplier().get()));
		event.getIMCStream(s -> s.equals(IMC_DOOR_ACTIVATOR_MSG)).forEach(msg -> registeredDoorActivators.add((IDoorActivator)msg.getMessageSupplier().get()));

		registeredExtractionBlocks = Collections.unmodifiableList(registeredExtractionBlocks);
		registeredPasswordConvertibles = Collections.unmodifiableList(registeredPasswordConvertibles);
		registeredSentryAttackTargetChecks = Collections.unmodifiableList(registeredSentryAttackTargetChecks);
		registeredDoorActivators = Collections.unmodifiableList(registeredDoorActivators);
	}

	public static List<IExtractionBlock> getRegisteredExtractionBlocks()
	{
		return registeredExtractionBlocks;
	}

	public static List<IPasswordConvertible> getRegisteredPasswordConvertibles()
	{
		return registeredPasswordConvertibles;
	}

	public static List<IAttackTargetCheck> getRegisteredSentryAttackTargetChecks()
	{
		return registeredSentryAttackTargetChecks;
	}

	public static List<IDoorActivator> getRegisteredDoorActivators()
	{
		return registeredDoorActivators;
	}
}
