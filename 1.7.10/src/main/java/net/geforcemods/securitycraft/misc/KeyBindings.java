package net.geforcemods.securitycraft.misc;

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
	public static KeyBinding cameraZoomIn;
	public static KeyBinding cameraZoomOut;
	public static KeyBinding cameraEmitRedstone;
	public static KeyBinding cameraActivateNightVision;

	public static void init(){
		cameraNext = new KeyBinding("key.cameraNext", Keyboard.KEY_LBRACKET, "key.categories.securitycraft");
		cameraPrevious = new KeyBinding("key.cameraZoomOut", Keyboard.KEY_RBRACKET, "key.categories.securitycraft");
		cameraZoomIn = new KeyBinding("key.cameraZoomIn", Keyboard.KEY_EQUALS, "key.categories.securitycraft");
		cameraZoomOut = new KeyBinding("key.cameraZoomOut", Keyboard.KEY_MINUS, "key.categories.securitycraft");
		cameraEmitRedstone = new KeyBinding("key.cameraEmitRedstone", Keyboard.KEY_R, "key.categories.securitycraft");
		cameraActivateNightVision = new KeyBinding("key.cameraActivateNightVision", Keyboard.KEY_N, "key.categories.securitycraft");
		
		ClientRegistry.registerKeyBinding(cameraNext);
		ClientRegistry.registerKeyBinding(cameraPrevious);
		ClientRegistry.registerKeyBinding(cameraZoomIn);
		ClientRegistry.registerKeyBinding(cameraZoomOut);
		ClientRegistry.registerKeyBinding(cameraEmitRedstone);
		ClientRegistry.registerKeyBinding(cameraActivateNightVision);
	}

}
