package net.geforcemods.securitycraft.misc;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.fml.client.registry.ClientRegistry;

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

	public static void init(){
		cameraZoomIn = new KeyMapping("key.securitycraft.cameraZoomIn", GLFW.GLFW_KEY_EQUAL, "key.categories.securitycraft");
		cameraZoomOut = new KeyMapping("key.securitycraft.cameraZoomOut", GLFW.GLFW_KEY_MINUS, "key.categories.securitycraft");
		cameraEmitRedstone = new KeyMapping("key.securitycraft.cameraEmitRedstone", GLFW.GLFW_KEY_R, "key.categories.securitycraft");
		cameraActivateNightVision = new KeyMapping("key.securitycraft.cameraActivateNightVision", GLFW.GLFW_KEY_N, "key.categories.securitycraft");

		ClientRegistry.registerKeyBinding(cameraZoomIn);
		ClientRegistry.registerKeyBinding(cameraZoomOut);
		ClientRegistry.registerKeyBinding(cameraEmitRedstone);
		ClientRegistry.registerKeyBinding(cameraActivateNightVision);
	}
}
