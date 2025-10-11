package net.geforcemods.securitycraft.inventory;

import net.neoforged.neoforge.transfer.DelegatingResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class ExtractOnlyResourceHandler<T extends Resource> extends DelegatingResourceHandler<T> {
	public ExtractOnlyResourceHandler(ResourceHandler<T> delegate) {
		super(delegate);
	}

	@Override
	public int insert(T resource, int amount, TransactionContext transaction) {
		return 0;
	}

	@Override
	public int insert(int index, T resource, int amount, TransactionContext transaction) {
		return 0;
	}

	@Override
	public boolean isValid(int index, T resource) {
		return false;
	}
}
