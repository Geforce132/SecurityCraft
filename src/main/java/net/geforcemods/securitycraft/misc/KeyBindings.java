package net.geforcemods.securitycraft.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.lwjgl.glfw.GLFW;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

/**
 * Custom {@link KeyMapping}s that SecurityCraft uses.
 *
 * @author Geforce
 */
@EventBusSubscriber(modid = SecurityCraft.MODID, value = Dist.CLIENT)
public class KeyBindings {
	private static final List<TickingKeyMapping> TICKING_KEY_MAPPINGS = new ArrayList<>();
	private static KeyMapping.Category category;
	private static KeyMapping cameraZoomIn;
	private static KeyMapping cameraZoomOut;
	private static KeyMapping cameraEmitRedstone;
	private static KeyMapping cameraActivateNightVision;
	private static KeyMapping setDefaultViewingDirection;

	private KeyBindings() {}

	@SubscribeEvent
	public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		category = KeyMapping.Category.register(SecurityCraft.resLoc(SecurityCraft.MODID));
		cameraZoomIn = register(event, "cameraZoomIn", GLFW.GLFW_KEY_EQUAL);
		cameraZoomOut = register(event, "cameraZoomOut", GLFW.GLFW_KEY_MINUS);
		cameraEmitRedstone = registerTicking(event, "cameraEmitRedstone", GLFW.GLFW_KEY_R, CameraController::toggleRedstone);
		cameraActivateNightVision = registerTicking(event, "cameraActivateNightVision", GLFW.GLFW_KEY_N, CameraController::toggleNightVision);
		setDefaultViewingDirection = registerTicking(event, "setDefaultViewingDirection", GLFW.GLFW_KEY_U, CameraController::setDefaultViewingDirection);
	}

	private static KeyMapping register(RegisterKeyMappingsEvent event, String name, int defaultKey) {
		KeyMapping keyMapping = new SCKeyMapping(name, defaultKey);

		event.register(keyMapping);
		return keyMapping;
	}

	private static KeyMapping registerTicking(RegisterKeyMappingsEvent event, String name, int defaultKey, Consumer<SecurityCamera> action) {
		TickingKeyMapping keyMapping = new TickingKeyMapping(name, defaultKey, action);

		event.register(keyMapping);
		TICKING_KEY_MAPPINGS.add(keyMapping);
		return keyMapping;
	}

	public static void tick(SecurityCamera cam) {
		TICKING_KEY_MAPPINGS.forEach(keyMapping -> keyMapping.tick(cam));
	}

	public static KeyMapping cameraZoomIn() {
		return cameraZoomIn;
	}

	public static KeyMapping cameraZoomOut() {
		return cameraZoomOut;
	}

	public static KeyMapping cameraEmitRedstone() {
		return cameraEmitRedstone;
	}

	public static KeyMapping cameraActivateNightVision() {
		return cameraActivateNightVision;
	}

	public static KeyMapping setDefaultViewingDirection() {
		return setDefaultViewingDirection;
	}

	private static class SCKeyMapping extends KeyMapping {
		public SCKeyMapping(String name, int defaultKey) {
			super("key.securitycraft." + name, defaultKey, category);
		}
	}

	private static class TickingKeyMapping extends SCKeyMapping {
		private static final int MAX_COOLDOWN = 30;
		private int cooldown = MAX_COOLDOWN;
		private Consumer<SecurityCamera> action;

		public TickingKeyMapping(String name, int defaultKey, Consumer<SecurityCamera> action) {
			super(name, defaultKey);
			this.action = action;
		}

		public void tick(SecurityCamera t) {
			cooldown--;

			if (consumeClick() && cooldown <= 0) {
				action.accept(t);
				cooldown = MAX_COOLDOWN;
			}
		}
	}
}
