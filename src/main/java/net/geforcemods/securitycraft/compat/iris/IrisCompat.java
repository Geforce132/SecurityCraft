package net.geforcemods.securitycraft.compat.iris;

import net.irisshaders.iris.layer.BufferSourceWrapper;
import net.minecraft.client.renderer.MultiBufferSource;

public class IrisCompat {
	/**
	 * Sometimes, when Iris is installed and shaders have been previously active, Iris will use its own wrapped buffer
	 * source, wrapping the original vanilla buffer. Since this wrapper does not have a functionality to end the batch of the
	 * wrapped buffer, this needs to be specially handled here.
	 */
	public static void endWrappedBufferBatch(MultiBufferSource buffer) {
		if (buffer instanceof BufferSourceWrapper wrapper && wrapper.getOriginal() instanceof MultiBufferSource.BufferSource originalBuffer)
			originalBuffer.endBatch();
	}
}
