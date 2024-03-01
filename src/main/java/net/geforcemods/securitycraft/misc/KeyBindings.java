package net.geforcemods.securitycraft.misc;

import java.util.function.Consumer;

import org.lwjgl.glfw.GLFW;

import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

/**
 * Custom {@link KeyBinding}s that SecurityCraft uses.
 *
 * @author Geforce
 */
public class KeyBindings {
	public static KeyBinding cameraZoomIn;
	public static KeyBinding cameraZoomOut;
	public static TickingKeyMapping<SecurityCamera> cameraEmitRedstone;
	public static TickingKeyMapping<SecurityCamera> cameraActivateNightVision;
	public static TickingKeyMapping<SecurityCamera> setDefaultViewingDirection;

	private KeyBindings() {}

	public static void init() {
		cameraZoomIn = register("cameraZoomIn", GLFW.GLFW_KEY_EQUAL);
		cameraZoomOut = register("cameraZoomOut", GLFW.GLFW_KEY_MINUS);
		cameraEmitRedstone = registerTicking("cameraEmitRedstone", GLFW.GLFW_KEY_R, CameraController::toggleRedstone);
		cameraActivateNightVision = registerTicking("cameraActivateNightVision", GLFW.GLFW_KEY_N, CameraController::toggleNightVision);
		setDefaultViewingDirection = registerTicking("setDefaultViewingDirection", GLFW.GLFW_KEY_U, CameraController::setDefaultViewingDirection);
	}

	private static KeyBinding register(String name, int defaultKey) {
		KeyBinding keyMapping = new SCKeyMapping(name, defaultKey);

		ClientRegistry.registerKeyBinding(keyMapping);
		return keyMapping;
	}

	private static <T> TickingKeyMapping<T> registerTicking(String name, int defaultKey, Consumer<T> action) {
		TickingKeyMapping<T> keyMapping = new TickingKeyMapping<>(name, defaultKey, action);

		ClientRegistry.registerKeyBinding(keyMapping);
		return keyMapping;
	}

	public static class SCKeyMapping extends KeyBinding {
		public SCKeyMapping(String name, int defaultKey) {
			super("key.securitycraft." + name, defaultKey, "key.categories.securitycraft");
		}
	}

	public static class TickingKeyMapping<T> extends SCKeyMapping {
		private static final int MAX_COOLDOWN = 30;
		private int cooldown = MAX_COOLDOWN;
		private Consumer<T> action;

		public TickingKeyMapping(String name, int defaultKey, Consumer<T> action) {
			super(name, defaultKey);
			this.action = action;
		}

		public void tick(T t) {
			cooldown--;

			if (consumeClick() && cooldown <= 0) {
				action.accept(t);
				cooldown = MAX_COOLDOWN;
			}
		}
	}
}
