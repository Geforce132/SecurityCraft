package net.geforcemods.securitycraft.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;

@EventBusSubscriber(modid = SecurityCraft.MODID)
public class SecurityCraftAPI {
	private static List<IExtractionBlock> registeredExtractionBlocks = new ArrayList<>();
	private static List<IPasscodeConvertible> registeredPasscodeConvertibles = new ArrayList<>();
	private static List<IAttackTargetCheck> registeredSentryAttackTargetChecks = new ArrayList<>();
	private static List<IDoorActivator> registeredDoorActivators = new ArrayList<>();
	public static final String IMC_EXTRACTION_BLOCK_MSG = "registerExtractionBlock";
	public static final String IMC_PASSCODE_CONVERTIBLE_MSG = "registerPasscodeConvertible";
	public static final String IMC_SENTRY_ATTACK_TARGET_MSG = "registerSentryAttackTargetCheck";
	public static final String IMC_DOOR_ACTIVATOR_MSG = "registerDoorActivator";

	private SecurityCraftAPI() {}

	@SubscribeEvent
	public static void onInterModProcess(InterModProcessEvent event) {
		event.getIMCStream(s -> s.equals(IMC_EXTRACTION_BLOCK_MSG)).forEach(msg -> registeredExtractionBlocks.add((IExtractionBlock) msg.messageSupplier().get()));
		event.getIMCStream(s -> s.equals(IMC_PASSCODE_CONVERTIBLE_MSG)).forEach(msg -> registeredPasscodeConvertibles.add((IPasscodeConvertible) msg.messageSupplier().get()));
		event.getIMCStream(s -> s.equals(IMC_SENTRY_ATTACK_TARGET_MSG)).forEach(msg -> registeredSentryAttackTargetChecks.add((IAttackTargetCheck) msg.messageSupplier().get()));
		event.getIMCStream(s -> s.equals(IMC_DOOR_ACTIVATOR_MSG)).forEach(msg -> registeredDoorActivators.add((IDoorActivator) msg.messageSupplier().get()));

		registeredExtractionBlocks = Collections.unmodifiableList(registeredExtractionBlocks);
		registeredPasscodeConvertibles = Collections.unmodifiableList(registeredPasscodeConvertibles);
		registeredSentryAttackTargetChecks = Collections.unmodifiableList(registeredSentryAttackTargetChecks);
		registeredDoorActivators = Collections.unmodifiableList(registeredDoorActivators);
	}

	public static List<IExtractionBlock> getRegisteredExtractionBlocks() {
		return registeredExtractionBlocks;
	}

	public static List<IPasscodeConvertible> getRegisteredPasscodeConvertibles() {
		return registeredPasscodeConvertibles;
	}

	public static List<IAttackTargetCheck> getRegisteredSentryAttackTargetChecks() {
		return registeredSentryAttackTargetChecks;
	}

	public static List<IDoorActivator> getRegisteredDoorActivators() {
		return registeredDoorActivators;
	}
}
