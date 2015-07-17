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
	
	public static KeyBinding cameraNext;
	public static KeyBinding cameraPrevious;

	public static void init(){
		cameraNext = new KeyBinding("key.cameraNext", Keyboard.KEY_LBRACKET, "key.categories.securitycraft");
		cameraPrevious = new KeyBinding("key.cameraZoomOut", Keyboard.KEY_RBRACKET, "key.categories.securitycraft");

		ClientRegistry.registerKeyBinding(cameraNext);
		ClientRegistry.registerKeyBinding(cameraPrevious);
	}

}
