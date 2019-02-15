package net.geforcemods.securitycraft.misc;

import net.java.games.input.Keyboard;
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

	public static void init(){
		cameraZoomIn = new KeyBinding("key.cameraZoomIn", Keyboard.KEY_EQUALS, "key.categories.securitycraft");
		cameraZoomOut = new KeyBinding("key.cameraZoomOut", Keyboard.KEY_MINUS, "key.categories.securitycraft");
		cameraEmitRedstone = new KeyBinding("key.cameraEmitRedstone", Keyboard.KEY_R, "key.categories.securitycraft");
		cameraActivateNightVision = new KeyBinding("key.cameraActivateNightVision", Keyboard.KEY_N, "key.categories.securitycraft");

		ClientRegistry.registerKeyBinding(cameraZoomIn);
		ClientRegistry.registerKeyBinding(cameraZoomOut);
		ClientRegistry.registerKeyBinding(cameraEmitRedstone);
		ClientRegistry.registerKeyBinding(cameraActivateNightVision);
	}

}
