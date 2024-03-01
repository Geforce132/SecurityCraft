package net.geforcemods.securitycraft.misc;

import javax.swing.text.JTextComponent.KeyBinding;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;

/**
 * Custom {@link KeyBinding}s that SecurityCraft uses.
 *
 * @author Geforce
 */
public class KeyBindings {
	public static KeyMapping cameraZoomIn;
	public static KeyMapping cameraZoomOut;
	public static KeyMapping cameraEmitRedstone;
	public static KeyMapping cameraActivateNightVision;

	private KeyBindings() {}

	public static void init() {
		cameraZoomIn = register("cameraZoomIn", GLFW.GLFW_KEY_EQUAL);
		cameraZoomOut = register("cameraZoomOut", GLFW.GLFW_KEY_MINUS);
		cameraEmitRedstone = register("cameraEmitRedstone", GLFW.GLFW_KEY_R);
		cameraActivateNightVision = register("cameraActivateNightVision", GLFW.GLFW_KEY_N);
	}

	private static KeyMapping register(String name, int defaultKey) {
		KeyMapping keyMapping = new KeyMapping("key.securitycraft." + name, defaultKey, "key.categories.securitycraft");

		ClientRegistry.registerKeyBinding(keyMapping);
		return keyMapping;
	}
}
