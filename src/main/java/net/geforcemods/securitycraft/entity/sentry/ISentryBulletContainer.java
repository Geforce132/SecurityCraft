package net.geforcemods.securitycraft.entity.sentry;

import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.items.IItemHandler;

public interface ISentryBulletContainer {
	public LazyOptional<IItemHandler> getHandlerForSentry(Sentry sentry);
}
