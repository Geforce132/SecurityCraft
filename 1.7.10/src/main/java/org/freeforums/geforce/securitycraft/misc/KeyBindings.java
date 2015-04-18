package org.freeforums.geforce.securitycraft.misc;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;

import net.minecraft.client.settings.KeyBinding;

/**
 * Custom {@link KeyBinding}s that SecurityCraft uses.
 * 
 * @author Geforce
 */
public class KeyBindings {
	
	public static KeyBinding cameraRotateUp;
	public static KeyBinding cameraRotateDown;
	public static KeyBinding cameraRotateLeft;
	public static KeyBinding cameraRotateRight;
	public static KeyBinding cameraZoomIn;
	public static KeyBinding cameraZoomOut;

	public static void init(){
		cameraRotateUp = new KeyBinding("key.cameraRotateUp", Keyboard.KEY_UP, "key.categories.securitycraft");
		cameraRotateDown = new KeyBinding("key.cameraRotateDown", Keyboard.KEY_DOWN, "key.categories.securitycraft");
		cameraRotateLeft = new KeyBinding("key.cameraRotateLeft", Keyboard.KEY_LEFT, "key.categories.securitycraft");
		cameraRotateRight = new KeyBinding("key.cameraRotateRight", Keyboard.KEY_RIGHT, "key.categories.securitycraft");
		cameraZoomIn = new KeyBinding("key.cameraZoomIn", Keyboard.KEY_EQUALS, "key.categories.securitycraft");
		cameraZoomOut = new KeyBinding("key.cameraZoomOut", Keyboard.KEY_MINUS, "key.categories.securitycraft");

		ClientRegistry.registerKeyBinding(cameraRotateUp);
		ClientRegistry.registerKeyBinding(cameraRotateDown);
		ClientRegistry.registerKeyBinding(cameraRotateLeft);
		ClientRegistry.registerKeyBinding(cameraRotateRight);
		ClientRegistry.registerKeyBinding(cameraZoomIn);
		ClientRegistry.registerKeyBinding(cameraZoomOut);
	}

}
