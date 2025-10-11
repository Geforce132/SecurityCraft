package net.geforcemods.securitycraft.inventory;

import net.neoforged.neoforge.transfer.DelegatingResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class InsertOnlyResourceHandler<T extends Resource> extends DelegatingResourceHandler<T> {
	public InsertOnlyResourceHandler(ResourceHandler<T> delegate) {
		super(delegate);
	}

	@Override
	public int extract(T resource, int amount, TransactionContext transaction) {
		return 0;
	}

	@Override
	public int extract(int index, T resource, int amount, TransactionContext transaction) {
		return 0;
	}
}
