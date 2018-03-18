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
	public static KeyBinding cameraPrevious;
	public static KeyBinding cameraNext;

	public static void init(){
		cameraZoomIn = new KeyBinding("key.securitycraft.cameraZoomIn", Keyboard.KEY_EQUALS, "key.categories.securitycraft");
		cameraZoomOut = new KeyBinding("key.securitycraft.cameraZoomOut", Keyboard.KEY_MINUS, "key.categories.securitycraft");
		cameraEmitRedstone = new KeyBinding("key.securitycraft.cameraEmitRedstone", Keyboard.KEY_R, "key.categories.securitycraft");
		cameraActivateNightVision = new KeyBinding("key.securitycraft.cameraActivateNightVision", Keyboard.KEY_N, "key.categories.securitycraft");
		cameraPrevious = new KeyBinding("key.securitycraft.cameraPrevious", Keyboard.KEY_LEFT, "key.categories.securitycraft");
		cameraNext = new KeyBinding("key.securitycraft.cameraNext", Keyboard.KEY_RIGHT, "key.categories.securitycraft");

		ClientRegistry.registerKeyBinding(cameraZoomIn);
		ClientRegistry.registerKeyBinding(cameraZoomOut);
		ClientRegistry.registerKeyBinding(cameraEmitRedstone);
		ClientRegistry.registerKeyBinding(cameraActivateNightVision);
		ClientRegistry.registerKeyBinding(cameraPrevious);
		ClientRegistry.registerKeyBinding(cameraNext);
	}

}
