package net.geforcemods.securitycraft.entity.sentry;

import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

public interface ISentryBulletContainer {
	public ResourceHandler<ItemResource> getHandlerForSentry(Sentry sentry);
}
