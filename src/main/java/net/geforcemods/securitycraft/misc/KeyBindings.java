package net.geforcemods.securitycraft.misc;

import org.lwjgl.glfw.GLFW;

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
		cameraZoomIn = new KeyBinding("key.securitycraft.cameraZoomIn", GLFW.GLFW_KEY_EQUAL, "key.categories.securitycraft");
		cameraZoomOut = new KeyBinding("key.securitycraft.cameraZoomOut", GLFW.GLFW_KEY_MINUS, "key.categories.securitycraft");
		cameraEmitRedstone = new KeyBinding("key.securitycraft.cameraEmitRedstone", GLFW.GLFW_KEY_R, "key.categories.securitycraft");
		cameraActivateNightVision = new KeyBinding("key.securitycraft.cameraActivateNightVision", GLFW.GLFW_KEY_N, "key.categories.securitycraft");

		ClientRegistry.registerKeyBinding(cameraZoomIn);
		ClientRegistry.registerKeyBinding(cameraZoomOut);
		ClientRegistry.registerKeyBinding(cameraEmitRedstone);
		ClientRegistry.registerKeyBinding(cameraActivateNightVision);
	}
}
