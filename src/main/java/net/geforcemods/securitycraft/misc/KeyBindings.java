package net.geforcemods.securitycraft.misc;

import org.lwjgl.input.Keyboard;

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
	public static KeyBinding cameraEmitRedstone;
	public static KeyBinding cameraActivateNightVision;
	public static KeyBinding setDefaultViewingDirection;

	private KeyBindings() {}

	public static void init() {
		cameraZoomIn = register("cameraZoomIn", Keyboard.KEY_EQUALS);
		cameraZoomOut = register("cameraZoomOut", Keyboard.KEY_MINUS);
		cameraEmitRedstone = register("cameraEmitRedstone", Keyboard.KEY_R);
		cameraActivateNightVision = register("cameraActivateNightVision", Keyboard.KEY_N);
		setDefaultViewingDirection = register("setDefaultViewingDirection", Keyboard.KEY_U);
	}

	private static KeyBinding register(String name, int defaultKey) {
		KeyBinding keyMapping = new KeyBinding("key.securitycraft." + name, defaultKey, "key.categories.securitycraft");

		ClientRegistry.registerKeyBinding(keyMapping);
		return keyMapping;
	}
}
