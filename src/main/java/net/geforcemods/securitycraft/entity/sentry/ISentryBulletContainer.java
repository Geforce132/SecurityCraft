package net.geforcemods.securitycraft.entity.sentry;

import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

public interface ISentryBulletContainer {
	public LazyOptional<IItemHandler> getHandlerForSentry(Sentry sentry);
}
