package net.geforcemods.securitycraft.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;

public class SimpleListenerContainer extends SimpleContainer {
	private ContainerListener listener;

	public SimpleListenerContainer(int size) {
		super(size);
	}

	public void setListener(ContainerListener listener) {
		this.listener = listener;
	}

	@Override
	public void setChanged() {
		super.setChanged();

		if (listener != null)
			listener.containerChanged(this);
	}

	public interface ContainerListener {
		void containerChanged(Container container);
	}
}
