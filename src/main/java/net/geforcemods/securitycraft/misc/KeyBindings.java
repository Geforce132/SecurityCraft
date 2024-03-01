package net.geforcemods.securitycraft.misc;

import java.util.function.Consumer;

import javax.swing.text.JTextComponent.KeyBinding;

import org.lwjgl.glfw.GLFW;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

/**
 * Custom {@link KeyBinding}s that SecurityCraft uses.
 *
 * @author Geforce
 */
@EventBusSubscriber(modid = SecurityCraft.MODID, value = Dist.CLIENT, bus = Bus.MOD)
public class KeyBindings {
	public static KeyMapping cameraZoomIn;
	public static KeyMapping cameraZoomOut;
	public static TickingKeyMapping<SecurityCamera> cameraEmitRedstone;
	public static TickingKeyMapping<SecurityCamera> cameraActivateNightVision;
	public static TickingKeyMapping<SecurityCamera> setDefaultViewingDirection;

	private KeyBindings() {}

	@SubscribeEvent
	public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
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

	private static <T> TickingKeyMapping<T> registerTicking(RegisterKeyMappingsEvent event, String name, int defaultKey, Consumer<T> action) {
		TickingKeyMapping<T> keyMapping = new TickingKeyMapping<>(name, defaultKey, action);

		event.register(keyMapping);
		return keyMapping;
	}

	public static class SCKeyMapping extends KeyMapping {
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
