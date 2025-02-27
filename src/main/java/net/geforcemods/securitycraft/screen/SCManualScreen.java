package net.geforcemods.securitycraft.screen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.mojang.blaze3d.vertex.Tesselator;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.api.IViewActivated;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.items.SCManualItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.PageGroup;
import net.geforcemods.securitycraft.misc.SCManualPage;
import net.geforcemods.securitycraft.screen.components.HoverChecker;
import net.geforcemods.securitycraft.screen.components.ItemStacksDisplay;
import net.geforcemods.securitycraft.screen.components.TextHoverChecker;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay.Empty;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.widget.ScrollPanel;

public class SCManualScreen extends Screen {
	private static final ResourceLocation PAGE = SecurityCraft.resLoc("textures/gui/info_book_texture.png");
	private static final ResourceLocation PAGE_WITH_SCROLL = SecurityCraft.resLoc("textures/gui/info_book_texture_special.png"); //for items without a recipe
	private static final ResourceLocation TITLE_PAGE = SecurityCraft.resLoc("textures/gui/info_book_title_page.png");
	private static final ResourceLocation OWNABLE_SPRITE = SecurityCraft.resLoc("sc_manual/ownable");
	private static final ResourceLocation OWNABLE_HIGHLIGHTED_SPRITE = SecurityCraft.resLoc("sc_manual/ownable_highlighted");
	private static final ResourceLocation PASSCODE_PROTECTED_SPRITE = SecurityCraft.resLoc("sc_manual/passcode_protected");
	private static final ResourceLocation PASSCODE_PROTECTED_HIGHLIGHTED_SPRITE = SecurityCraft.resLoc("sc_manual/passcode_protected_highlighted");
	private static final ResourceLocation VIEW_ACTIVATED_SPRITE = SecurityCraft.resLoc("sc_manual/view_activated");
	private static final ResourceLocation VIEW_ACTIVATED_HIGHLIGHTED_SPRITE = SecurityCraft.resLoc("sc_manual/view_activated_highlighted");
	private static final ResourceLocation EXPLOSIVE_SPRITE = SecurityCraft.resLoc("sc_manual/explosive");
	private static final ResourceLocation EXPLOSIVE_HIGHLIGHTED_SPRITE = SecurityCraft.resLoc("sc_manual/explosive_highlighted");
	private static final ResourceLocation HAS_OPTIONS_SPRITE = SecurityCraft.resLoc("sc_manual/has_options");
	private static final ResourceLocation HAS_OPTIONS_HIGHLIGHTED_SPRITE = SecurityCraft.resLoc("sc_manual/has_options_highlighted");
	private static final ResourceLocation HAS_MODULES_SPRITE = SecurityCraft.resLoc("sc_manual/has_modules");
	private static final ResourceLocation HAS_MODULES_HIGHLIGHTED_SPRITE = SecurityCraft.resLoc("sc_manual/has_modules_highlighted");
	private static final ResourceLocation LOCKABLE_SPRITE = SecurityCraft.resLoc("sc_manual/lockable");
	private static final ResourceLocation LOCKABLE_HIGHLIGHTED_SPRITE = SecurityCraft.resLoc("sc_manual/lockable_highlighted");
	private static final ResourceLocation CUSTOMIZABLE_SPRITE = SecurityCraft.resLoc("sc_manual/customizable");
	private static final ResourceLocation CUSTOMIZABLE_HIGHLIGHTED_SPRITE = SecurityCraft.resLoc("sc_manual/customizable_highlighted");
	private static final ResourceLocation LINK_OUT_HIGHLIGHTED_SPRITE = SecurityCraft.resLoc("sc_manual/link_out_highlighted");
	private static final ResourceLocation LINK_OUT_SPRITE = SecurityCraft.resLoc("sc_manual/link_out");
	private static final ResourceLocation PAGE_FORWARD_HIGHLIGHTED_SPRITE = SecurityCraft.mcResLoc("widget/page_forward_highlighted");
	private static final ResourceLocation PAGE_FORWARD_SPRITE = SecurityCraft.mcResLoc("widget/page_forward");
	private static final ResourceLocation PAGE_BACKWARD_HIGHLIGHTED_SPRITE = SecurityCraft.mcResLoc("widget/page_backward_highlighted");
	private static final ResourceLocation PAGE_BACKWARD_SPRITE = SecurityCraft.mcResLoc("widget/page_backward");
	private static final int SUBPAGE_LENGTH = 1285;
	private final MutableComponent intro1 = Utils.localize("gui.securitycraft:scManual.intro.1").setStyle(Style.EMPTY.withUnderlined(true));
	private final Component ourPatrons = Utils.localize("gui.securitycraft:scManual.patreon.title");
	private List<HoverChecker> hoverCheckers = new ArrayList<>();
	private int currentPage = SCManualItem.lastOpenPage;
	private List<SlotDisplay> recipe;
	private ItemStacksDisplay[] displays = new ItemStacksDisplay[9];
	private int startX = -1;
	private List<FormattedText> subpages = new ArrayList<>();
	private List<FormattedCharSequence> author = new ArrayList<>();
	private int currentSubpage = 0;
	private List<FormattedCharSequence> intro2;
	private PatronList patronList;
	private Button patreonLinkButton;
	private Button nextSubpage;
	private Button previousSubpage;
	private boolean explosive, ownable, passcodeProtected, viewActivated, hasOptions, lockable, hasModules;
	private ItemStacksDisplay pageIcon;
	private Component pageTitle, designedBy;

	public SCManualScreen() {
		super(Component.translatable(SCContent.SC_MANUAL.get().getDescriptionId()));
	}

	@Override
	public void init() {
		byte startY = 2;

		startX = (width - 256) / 2;
		patreonLinkButton = addRenderableWidget(new HyperlinkButton(startX + 225, 143, 16, 16, Component.empty(), b -> handleComponentClicked(Style.EMPTY.withClickEvent(new ClickEvent(Action.OPEN_URL, "https://www.patreon.com/Geforce")))));
		patronList = addRenderableWidget(new PatronList(minecraft, 115, 90, 50, startX + 125));
		previousSubpage = addRenderableWidget(new ChangePageButton(startX + 155, startY + 95, PAGE_BACKWARD_SPRITE, PAGE_BACKWARD_HIGHLIGHTED_SPRITE, b -> previousSubpage()));
		nextSubpage = addRenderableWidget(new ChangePageButton(startX + 180, startY + 95, PAGE_FORWARD_SPRITE, PAGE_FORWARD_HIGHLIGHTED_SPRITE, b -> nextSubpage()));
		addRenderableWidget(new ChangePageButton(startX + 22, startY + 188, PAGE_BACKWARD_SPRITE, PAGE_BACKWARD_HIGHLIGHTED_SPRITE, b -> previousPage()));
		addRenderableWidget(new ChangePageButton(startX + 210, startY + 188, PAGE_FORWARD_SPRITE, PAGE_FORWARD_HIGHLIGHTED_SPRITE, b -> nextPage()));

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				displays[(i * 3) + j] = addRenderableOnly(new ItemStacksDisplay((startX + 101) + (j * 19), 145 + (i * 19)));
			}
		}

		pageIcon = addRenderableOnly(new ItemStacksDisplay(startX + 19, 22));
		updateRecipeAndIcons();
		SCManualItem.PAGES.sort((page1, page2) -> {
			String key1 = page1.title().getString();
			String key2 = page2.title().getString();

			return key1.compareTo(key2);
		});
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		renderTransparentBackground(guiGraphics);
		guiGraphics.blit(RenderType::guiTextured, currentPage == -1 ? TITLE_PAGE : (recipe != null && !recipe.isEmpty() ? PAGE : PAGE_WITH_SCROLL), startX, 5, 0.0F, 0.0F, 256, 250, 256, 256);

		for (Renderable renderable : renderables) {
			renderable.render(guiGraphics, mouseX, mouseY, partialTicks);
		}

		if (currentPage > -1) {
			String pageNumberText = (currentPage + 2) + "/" + (SCManualItem.PAGES.size() + 1); //+1 because the "welcome" page is not included

			if (subpages.size() > 1)
				guiGraphics.drawString(font, (currentSubpage + 1) + "/" + subpages.size(), startX + 205, 100, 0x8E8270, false);

			if (designedBy != null)
				guiGraphics.drawWordWrap(font, designedBy, startX + 18, 150, 75, 0);

			guiGraphics.drawString(font, pageTitle, startX + 39, 27, 0, false);
			guiGraphics.drawWordWrap(font, subpages.get(currentSubpage), startX + 18, 45, 225, 0);
			guiGraphics.drawString(font, pageNumberText, startX + 240 - font.width(pageNumberText), 182, 0x8E8270, false);
			guiGraphics.blitSprite(RenderType::guiTextured, ownable ? OWNABLE_HIGHLIGHTED_SPRITE : OWNABLE_SPRITE, startX + 29, 118, 16, 16);
			guiGraphics.blitSprite(RenderType::guiTextured, passcodeProtected ? PASSCODE_PROTECTED_HIGHLIGHTED_SPRITE : PASSCODE_PROTECTED_SPRITE, startX + 56, 118, 16, 16);
			guiGraphics.blitSprite(RenderType::guiTextured, viewActivated ? VIEW_ACTIVATED_HIGHLIGHTED_SPRITE : VIEW_ACTIVATED_SPRITE, startX + 82, 118, 16, 16);
			guiGraphics.blitSprite(RenderType::guiTextured, explosive ? EXPLOSIVE_HIGHLIGHTED_SPRITE : EXPLOSIVE_SPRITE, startX + 107, 116, 18, 18);
			guiGraphics.blitSprite(RenderType::guiTextured, hasOptions ? HAS_OPTIONS_HIGHLIGHTED_SPRITE : HAS_OPTIONS_SPRITE, startX + 136, 118, 16, 16);
			guiGraphics.blitSprite(RenderType::guiTextured, hasModules ? HAS_MODULES_HIGHLIGHTED_SPRITE : HAS_MODULES_SPRITE, startX + 163, 118, 16, 16);
			guiGraphics.blitSprite(RenderType::guiTextured, lockable ? LOCKABLE_HIGHLIGHTED_SPRITE : LOCKABLE_SPRITE, startX + 189, 118, 16, 16);
			guiGraphics.blitSprite(RenderType::guiTextured, hasOptions || hasModules ? CUSTOMIZABLE_HIGHLIGHTED_SPRITE : CUSTOMIZABLE_SPRITE, startX + 213, 117, 16, 16);

			for (int i = 0; i < hoverCheckers.size(); i++) {
				HoverChecker chc = hoverCheckers.get(i);

				if (chc != null && chc.checkHover(mouseX, mouseY)) {
					if (chc instanceof TextHoverChecker thc && thc.getName() != null) {
						guiGraphics.renderComponentTooltip(font, thc.getLines(), mouseX, mouseY);
						break;
					}
					else if (i < displays.length && !displays[i].getCurrentStack().isEmpty()) {
						guiGraphics.renderTooltip(font, displays[i].getCurrentStack(), mouseX, mouseY);
						break;
					}
				}
			}
		}
		else { //"welcome" page
			String pageNumberText = "1/" + (SCManualItem.PAGES.size() + 1); //+1 because the "welcome" page is not included

			guiGraphics.drawString(font, intro1, width / 2 - font.width(intro1) / 2, 22, 0, false);

			for (int i = 0; i < intro2.size(); i++) {
				FormattedCharSequence text = intro2.get(i);

				guiGraphics.drawString(font, text, width / 2 - font.width(text) / 2, 150 + 10 * i, 0, false);
			}

			for (int i = 0; i < author.size(); i++) {
				FormattedCharSequence text = author.get(i);

				guiGraphics.drawString(font, text, width / 2 - font.width(text) / 2, 180 + 10 * i, 0, false);
			}

			guiGraphics.drawString(font, pageNumberText, startX + 240 - font.width(pageNumberText), 182, 0x8E8270, false);
			guiGraphics.drawString(font, ourPatrons, width / 2 - font.width(ourPatrons) / 2 + 30, 40, 0, false);
		}
	}

	@Override
	public void tick() {
		super.tick();
		pageIcon.tick();

		for (ItemStacksDisplay display : displays) {
			display.tick();
		}
	}

	@Override
	public void removed() {
		super.removed();
		SCManualItem.lastOpenPage = currentPage;
	}

	private void hideSubpageButtonsOnMainPage() {
		nextSubpage.visible = currentPage != -1 && subpages.size() > 1;
		previousSubpage.visible = currentPage != -1 && subpages.size() > 1;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		if (Screen.hasShiftDown()) {
			for (ItemStacksDisplay display : displays) {
				if (display != null)
					display.changeRenderingStack(-scrollY);
			}

			if (pageIcon != null)
				pageIcon.changeRenderingStack(-scrollY);

			return true;
		}

		if (currentPage == -1 && patronList != null && patronList.isMouseOver(mouseX, mouseY) && !patronList.patrons.isEmpty()) {
			patronList.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
			return true;
		}

		if (Screen.hasControlDown() && subpages.size() > 1) {
			switch ((int) Math.signum(scrollY)) {
				case -1:
					nextSubpage();
					break;
				case 1:
					previousSubpage();
					break;
			}

			return true;
		}

		switch ((int) Math.signum(scrollY)) {
			case -1:
				nextPage();
				break;
			case 1:
				previousPage();
				break;
		}

		//hide subpage buttons on main page
		nextSubpage.visible = currentPage != -1 && subpages.size() > 1;
		previousSubpage.visible = currentPage != -1 && subpages.size() > 1;
		return true;
	}

	private void nextPage() {
		currentPage++;

		if (currentPage > SCManualItem.PAGES.size() - 1)
			currentPage = -1;

		updateRecipeAndIcons();
		hideSubpageButtonsOnMainPage();
	}

	private void previousPage() {
		currentPage--;

		if (currentPage < -1)
			currentPage = SCManualItem.PAGES.size() - 1;

		updateRecipeAndIcons();
		hideSubpageButtonsOnMainPage();
	}

	private void nextSubpage() {
		currentSubpage++;

		if (currentSubpage == subpages.size())
			currentSubpage = 0;
	}

	private void previousSubpage() {
		currentSubpage--;

		if (currentSubpage == -1)
			currentSubpage = subpages.size() - 1;
	}

	private void updateRecipeAndIcons() {
		currentSubpage = 0;
		hoverCheckers.clear();
		patreonLinkButton.visible = currentPage == -1;

		if (currentPage < 0) {
			for (ItemStacksDisplay display : displays) {
				display.setStacks(null);
			}

			pageIcon.setStacks(null);
			recipe = null;
			nextSubpage.visible = false;
			previousSubpage.visible = false;

			if (I18n.exists("gui.securitycraft:scManual.author"))
				author = font.split(Utils.localize("gui.securitycraft:scManual.author"), 180);
			else
				author.clear();

			intro2 = font.split(Utils.localize("gui.securitycraft:scManual.intro.2"), 202);
			patronList.fetchPatrons();
			return;
		}

		SCManualPage page = SCManualItem.PAGES.get(currentPage);
		String designerName = page.designedBy();
		Item item = page.item();
		PageGroup pageGroup = page.group();

		if (designerName != null && !designerName.isEmpty())
			designedBy = Utils.localize("gui.securitycraft:scManual.designedBy", designerName);
		else
			designedBy = null;

		recipe = null;
		page.recipes().get().ifPresent(displayList -> {
			if (pageGroup == PageGroup.NONE) {
				RecipeDisplay display = displayList.get(0);

				if (display instanceof ShapedCraftingRecipeDisplay shapedRecipe) {
					List<SlotDisplay> ingredients = shapedRecipe.ingredients();
					List<SlotDisplay> recipeItems = Arrays.asList(Util.make(new SlotDisplay[9], array -> Arrays.fill(array, Empty.INSTANCE)));

					for (int i = 0; i < ingredients.size(); i++) {
						recipeItems.set(getCraftMatrixPosition(i, shapedRecipe.width(), shapedRecipe.height()), ingredients.get(i));
					}

					this.recipe = recipeItems;
				}
				else if (display instanceof ShapelessCraftingRecipeDisplay shapelessRecipe)
					this.recipe = new ArrayList<>(shapelessRecipe.ingredients());
			}
			else if (pageGroup.hasRecipeGrid()) {
				ContextMap contextMap = SlotDisplayContext.fromLevel(Minecraft.getInstance().level);
				Map<Integer, ItemStack[]> recipeStacks = new HashMap<>();
				List<Item> pageItems = pageGroup.getItems().stream().map(ItemStack::getItem).toList();

				for (int i = 0; i < 9; i++) {
					recipeStacks.put(i, new ItemStack[pageItems.size()]);
				}

				int stacksLeft = pageItems.size();

				for (RecipeDisplay recipeDisplay : displayList) {
					if (stacksLeft == 0)
						break;

					if (recipeDisplay instanceof ShapedCraftingRecipeDisplay shapedRecipe) {
						List<SlotDisplay> ingredients = shapedRecipe.ingredients();

						for (int i = 0; i < ingredients.size(); i++) {
							List<ItemStack> items = ingredients.get(i).resolveForStacks(contextMap);

							if (items.isEmpty())
								continue;

							int indexToAddAt = pageItems.indexOf(shapedRecipe.result().resolveForFirstStack(contextMap).getItem());

							//first item needs to suffice since multiple recipes are being cycled through
							recipeStacks.get(getCraftMatrixPosition(i, shapedRecipe.width(), shapedRecipe.height()))[indexToAddAt] = items.get(0);
						}

						stacksLeft--;
					}
					else if (recipeDisplay instanceof ShapelessCraftingRecipeDisplay shapelessRecipe) {
						List<SlotDisplay> ingredients = shapelessRecipe.ingredients();

						for (int i = 0; i < ingredients.size(); i++) {
							ItemStack firstItem = ingredients.get(i).resolveForFirstStack(contextMap);

							if (firstItem.isEmpty())
								continue;

							int indexToAddAt = pageItems.indexOf(shapelessRecipe.result().resolveForFirstStack(contextMap).getItem());

							//first item needs to suffice since multiple recipes are being cycled through
							recipeStacks.get(i)[indexToAddAt] = firstItem;
						}

						stacksLeft--;
					}
				}

				recipe = Arrays.asList(Util.make(new SlotDisplay[9], array -> Arrays.fill(array, Empty.INSTANCE)));
				recipeStacks.forEach((i, stackArray) -> recipe.set(i, new SlotDisplay.Composite(Arrays.stream(stackArray).map(stack -> stack == null ? Empty.INSTANCE : new SlotDisplay.ItemStackSlotDisplay(stack)).toList())));
			}
		});

		if (page.hasRecipeDescription()) {
			String name = Utils.getRegistryName(page.item()).getPath();

			hoverCheckers.add(new TextHoverChecker(144, 144 + (2 * 20) + 16, startX + 100, (startX + 100) + (2 * 20) + 16, Utils.localize("gui.securitycraft:scManual.recipe." + name)));
		}
		else if (pageGroup == PageGroup.REINFORCED || item == SCContent.REINFORCED_HOPPER.get().asItem()) {
			recipe = null;
			hoverCheckers.add(new TextHoverChecker(144, 144 + (2 * 20) + 16, startX + 100, (startX + 100) + (2 * 20) + 16, Utils.localize("gui.securitycraft:scManual.recipe.reinforced")));
		}
		else if (recipe != null) {
			for (int row = 0; row < 3; row++) {
				for (int column = 0; column < 3; column++) {
					hoverCheckers.add(new HoverChecker(144 + (row * 19), 144 + (row * 19) + 16, (startX + 101) + (column * 19), (startX + 101) + (column * 19) + 16));
				}
			}
		}
		else
			hoverCheckers.add(new TextHoverChecker(144, 144 + (2 * 20) + 16, startX + 100, (startX + 100) + (2 * 20) + 16, Utils.localize("gui.securitycraft:scManual.disabled")));

		pageTitle = page.title();

		if (pageGroup != PageGroup.NONE)
			pageIcon.setStacks(pageGroup.getItems());
		else
			pageIcon.setStacks(List.of(new ItemStack(page.item())));

		resetBlockEntityInfo();

		if (item instanceof BlockItem blockItem) {
			Block block = blockItem.getBlock();

			explosive = block instanceof IExplosive;

			if (explosive)
				hoverCheckers.add(new TextHoverChecker(118, 118 + 16, startX + 107, (startX + 107) + 16, Utils.localize("gui.securitycraft:scManual.explosiveBlock")));
		}

		Object inWorldObject = page.getInWorldObject();

		if (inWorldObject != null) {
			ownable = inWorldObject instanceof IOwnable;
			passcodeProtected = inWorldObject instanceof IPasscodeProtected;
			viewActivated = inWorldObject instanceof IViewActivated;
			lockable = inWorldObject instanceof ILockable;

			if (ownable)
				hoverCheckers.add(new TextHoverChecker(118, 118 + 16, startX + 29, (startX + 29) + 16, Utils.localize("gui.securitycraft:scManual.ownableBlock")));

			if (passcodeProtected)
				hoverCheckers.add(new TextHoverChecker(118, 118 + 16, startX + 55, (startX + 55) + 16, Utils.localize("gui.securitycraft:scManual.passcodeProtectedBlock")));

			if (viewActivated)
				hoverCheckers.add(new TextHoverChecker(118, 118 + 16, startX + 81, (startX + 81) + 16, Utils.localize("gui.securitycraft:scManual.viewActivatedBlock")));

			if (inWorldObject instanceof ICustomizable customizableObj) {
				Option<?>[] options = customizableObj.customOptions();

				if (options.length > 0) {
					List<Component> display = new ArrayList<>();

					hasOptions = true;
					display.add(Utils.localize("gui.securitycraft:scManual.options"));
					display.add(Component.literal("---"));

					for (Option<?> option : options) {
						display.add(Component.translatable("gui.securitycraft:scManual.option_text", Component.translatable(option.getDescriptionKey(Utils.getLanguageKeyDenotation(customizableObj))), option.getDefaultInfo()));
						display.add(Component.empty());
					}

					display.remove(display.size() - 1);
					hoverCheckers.add(new TextHoverChecker(118, 118 + 16, startX + 136, (startX + 136) + 16, display));
				}
			}

			if (inWorldObject instanceof IModuleInventory moduleInv && moduleInv.acceptedModules() != null && moduleInv.acceptedModules().length > 0) {
				List<Component> display = new ArrayList<>();

				hasModules = true;
				display.add(Utils.localize("gui.securitycraft:scManual.modules"));
				display.add(Component.literal("---"));

				for (ModuleType module : moduleInv.acceptedModules()) {
					display.add(Component.literal("- ").append(Utils.localize(moduleInv.getModuleDescriptionId(Utils.getLanguageKeyDenotation(moduleInv), module))));
					display.add(Component.empty());
				}

				display.remove(display.size() - 1);
				hoverCheckers.add(new TextHoverChecker(118, 118 + 16, startX + 163, (startX + 163) + 16, display));
			}

			if (lockable)
				hoverCheckers.add(new TextHoverChecker(118, 118 + 16, startX + 189, startX + 189 + 16, Utils.localize("gui.securitycraft:scManual.lockable")));

			if (hasOptions || hasModules)
				hoverCheckers.add(new TextHoverChecker(118, 118 + 16, startX + 213, (startX + 213) + 16, Utils.localize("gui.securitycraft:scManual.customizableBlock")));
		}

		if (recipe != null && !recipe.isEmpty()) {
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					int index = (i * 3) + j;

					if (index >= recipe.size())
						displays[index].setStacks(null);
					else
						displays[index].setStacks(recipe.get(index).resolveForStacks(SlotDisplayContext.fromLevel(Minecraft.getInstance().level)));
				}
			}
		}
		else {
			for (ItemStacksDisplay display : displays) {
				display.setStacks(null);
			}
		}

		//set up subpages
		subpages = font.getSplitter().splitLines(page.helpInfo(), SUBPAGE_LENGTH, Style.EMPTY);
		nextSubpage.visible = currentPage != -1 && subpages.size() > 1;
		previousSubpage.visible = currentPage != -1 && subpages.size() > 1;
	}

	private void resetBlockEntityInfo() {
		explosive = false;
		ownable = false;
		passcodeProtected = false;
		viewActivated = false;
		hasOptions = false;
		lockable = false;
		hasModules = false;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (patronList != null)
			patronList.mouseClicked(mouseX, mouseY, button);

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (patronList != null)
			patronList.mouseReleased(mouseX, mouseY, button);

		return super.mouseReleased(mouseX, mouseY, button);
	}

	class PatronList extends ScrollPanel {
		private static final String PATRON_LIST_LINK = FMLEnvironment.production ? "https://gist.githubusercontent.com/bl4ckscor3/bdda6596012b1206816db034350b5717/raw" : "https://gist.githubusercontent.com/bl4ckscor3/3196e6740774e386871a74a9606eaa61/raw";
		private static final int SLOT_HEIGHT = 12;
		private final ExecutorService executor = Executors.newSingleThreadExecutor();
		private Future<List<String>> patronRequestFuture;
		private List<String> patrons = new ArrayList<>();
		private boolean patronsAvailable = false;
		private boolean error = false;
		private boolean patronsRequested;
		private final List<FormattedCharSequence> fetchErrorLines;
		private final List<FormattedCharSequence> noPatronsLines;
		private final Component loadingText = Utils.localize("gui.securitycraft:scManual.patreon.loading");

		public PatronList(Minecraft client, int width, int height, int top, int left) {
			super(client, width, height, top, left, 4, 6, 0xFF8E8270, 0xFF807055, 0xFFD1BFA1);

			fetchErrorLines = font.split(Utils.localize("gui.securitycraft:scManual.patreon.error"), width);
			noPatronsLines = font.split(Utils.localize("advancements.empty"), width - 10);
		}

		@Override
		protected int getContentHeight() {
			int height = patrons.size() * (font.lineHeight + 3);

			if (height < bottom - top - 4)
				height = bottom - top - 4;

			return height;
		}

		@Override
		public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
			if (currentPage == -1) {
				if (patronsAvailable) {
					super.render(guiGraphics, mouseX, mouseY, partialTicks);

					//draw tooltip for long patron names
					int mouseListY = (int) (mouseY - top + scrollDistance - border);
					int slotIndex = mouseListY / SLOT_HEIGHT;

					if (mouseX >= left && mouseX < right - 6 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < patrons.size() && mouseY >= top && mouseY <= bottom) {
						String patron = patrons.get(slotIndex);
						int length = font.width(patron);
						int baseY = top + border - (int) scrollDistance;

						if (length >= width - 6) //6 = barWidth
							guiGraphics.renderTooltip(font, List.of(Component.literal(patron)), Optional.empty(), left - 10, baseY + (SLOT_HEIGHT * slotIndex + SLOT_HEIGHT));
					}

					if (patrons.isEmpty()) {
						for (int i = 0; i < noPatronsLines.size(); i++) {
							FormattedCharSequence line = noPatronsLines.get(i);

							guiGraphics.drawString(font, line, left + width / 2 - font.width(line) / 2, top + 30 + i * 10, 0xFF333333, false);
						}
					}
				}
				else if (error) {
					for (int i = 0; i < fetchErrorLines.size(); i++) {
						FormattedCharSequence line = fetchErrorLines.get(i);

						guiGraphics.drawString(font, line, left + width / 2 - font.width(line) / 2, top + 30 + i * 10, 0xFFB00101, false);
					}
				}
				else if (patronRequestFuture != null && patronRequestFuture.isDone()) {
					try {
						patrons = patronRequestFuture.get();
						executor.shutdown();
						patronsAvailable = true;
					}
					catch (InterruptedException | ExecutionException e) {
						error = true;
					}
				}
				else
					guiGraphics.drawString(font, loadingText, left + width / 2 - font.width(loadingText) / 2, top + 30, 0, false);
			}
		}

		@Override
		protected void drawPanel(GuiGraphics guiGraphics, int entryRight, int relativeY, Tesselator tesselator, int mouseX, int mouseY) {
			//draw entry strings
			for (int i = 0; i < patrons.size(); i++) {
				String patron = patrons.get(i);

				if (patron != null && !patron.isEmpty())
					guiGraphics.drawString(font, patron, left + 2, relativeY + (SLOT_HEIGHT * i), 0, false);
			}

			guiGraphics.flush();
		}

		public void fetchPatrons() {
			if (!patronsRequested) {
				//create thread to fetch patrons. without this, and for example if the player has no internet connection, the game will hang
				patronRequestFuture = executor.submit(() -> {
					try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(PATRON_LIST_LINK).openStream()))) {
						return reader.lines().toList();
					}
					catch (IOException e) {
						error = true;
						return new ArrayList<>();
					}
				});
				patronsRequested = true;
			}
		}

		@Override
		public NarrationPriority narrationPriority() {
			return NarrationPriority.NONE;
		}

		@Override
		public void updateNarration(NarrationElementOutput narrationElementOutput) {}
	}

	static class ChangePageButton extends Button {
		private final ResourceLocation normalSprite, highlightedSprite;

		public ChangePageButton(int xPos, int yPos, ResourceLocation normalSprite, ResourceLocation highlightedSprite, OnPress onPress) {
			super(xPos, yPos, 23, 13, Component.empty(), onPress, DEFAULT_NARRATION);
			this.normalSprite = normalSprite;
			this.highlightedSprite = highlightedSprite;
		}

		@Override
		public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
			guiGraphics.blitSprite(RenderType::guiTextured, isHoveredOrFocused() ? highlightedSprite : normalSprite, getX(), getY(), 23, 13);
		}
	}

	static class HyperlinkButton extends Button {
		public HyperlinkButton(int xPos, int yPos, int width, int height, Component displayString, OnPress handler) {
			super(xPos, yPos, width, height, displayString, handler, s -> Component.empty());
		}

		@Override
		public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
			isHovered = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
			guiGraphics.blitSprite(RenderType::guiTextured, isHoveredOrFocused() ? LINK_OUT_HIGHLIGHTED_SPRITE : LINK_OUT_SPRITE, getX(), getY(), 16, 16);
		}
	}

	//from JEI
	private int getCraftMatrixPosition(int i, int width, int height) {
		int index;

		if (width == 1) {
			if (height == 3)
				index = (i * 3) + 1;
			else if (height == 2)
				index = (i * 3) + 1;
			else
				index = 4;
		}
		else if (height == 1)
			index = i + 3;
		else if (width == 2) {
			index = i;

			if (i > 1) {
				index++;

				if (i > 3)
					index++;
			}
		}
		else if (height == 2)
			index = i + 3;
		else
			index = i;

		return index;
	}
}
