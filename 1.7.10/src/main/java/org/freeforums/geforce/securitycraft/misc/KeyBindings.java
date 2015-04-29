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
	
	public static KeyBinding cameraZoomIn;
	public static KeyBinding cameraZoomOut;
	public static KeyBinding cameraEmitRedstone;
	public static KeyBinding cameraActivateNightVision;
	public static KeyBinding cameraEmitLight;

	public static void init(){
		cameraZoomIn = new KeyBinding("key.cameraZoomIn", Keyboard.KEY_EQUALS, "key.categories.securitycraft");
		cameraZoomOut = new KeyBinding("key.cameraZoomOut", Keyboard.KEY_MINUS, "key.categories.securitycraft");
		cameraEmitRedstone = new KeyBinding("key.cameraEmitRedstone", Keyboard.KEY_R, "key.categories.securitycraft");
		cameraActivateNightVision = new KeyBinding("key.cameraActivateNightVision", Keyboard.KEY_N, "key.categories.securitycraft");
		cameraEmitLight = new KeyBinding("key.cameraEmitLight", Keyboard.KEY_L, "key.categories.securitycraft");

		ClientRegistry.registerKeyBinding(cameraZoomIn);
		ClientRegistry.registerKeyBinding(cameraZoomOut);
		ClientRegistry.registerKeyBinding(cameraEmitRedstone);
		ClientRegistry.registerKeyBinding(cameraActivateNightVision);
		ClientRegistry.registerKeyBinding(cameraEmitLight);
	}

}
