package net.geforcemods.securitycraft.compat.jei;

public class InventoryScannerGhostIngredientHandler// implements IGhostIngredientHandler<InventoryScannerScreen>
{
	//	@Override
	//	public <I> List<Target<I>> getTargetsTyped(InventoryScannerScreen screen, ITypedIngredient<I> ingredient, boolean doStart) {
	//		if (!screen.be.isOwnedBy(Minecraft.getInstance().player))
	//			return List.of();
	//
	//		List<Target<I>> targets = new ArrayList<>();
	//
	//		for (Slot slot : screen.getMenu().slots) {
	//			if (slot instanceof OwnerRestrictedSlot) {
	//				Rect2i area = new Rect2i(screen.getGuiLeft() + slot.x, screen.getGuiTop() + slot.y, 16, 16);
	//
	//				targets.add(new Target<>() {
	//					@Override
	//					public Rect2i getArea() {
	//						return area;
	//					}
	//
	//					@Override
	//					public void accept(I ingredient) {
	//						screen.be.getContents().set(slot.index, (ItemStack) ingredient);
	//						PacketDistributor.SERVER.noArg().send(new SetGhostSlot(slot.index, (ItemStack) ingredient));
	//					}
	//				});
	//			}
	//		}
	//
	//		return targets;
	//	}
	//
	//	@Override
	//	public void onComplete() {}
}
