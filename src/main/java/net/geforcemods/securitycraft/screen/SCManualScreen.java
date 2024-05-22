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

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import net.geforcemods.securitycraft.SCContent;
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
import net.geforcemods.securitycraft.screen.components.IngredientDisplay;
import net.geforcemods.securitycraft.screen.components.TextHoverChecker;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.gui.widget.ScrollPanel;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class SCManualScreen extends Screen {
	private static final ResourceLocation PAGE = new ResourceLocation("securitycraft:textures/gui/info_book_texture.png");
	private static final ResourceLocation PAGE_WITH_SCROLL = new ResourceLocation("securitycraft:textures/gui/info_book_texture_special.png"); //for items without a recipe
	private static final ResourceLocation TITLE_PAGE = new ResourceLocation("securitycraft:textures/gui/info_book_title_page.png");
	private static final ResourceLocation ICONS = new ResourceLocation("securitycraft:textures/gui/info_book_icons.png");
	private static final ResourceLocation VANILLA_BOOK = new ResourceLocation("textures/gui/book.png");
	private static final int SUBPAGE_LENGTH = 1285;
	private static int lastPage = -1;
	private final MutableComponent intro1 = Utils.localize("gui.securitycraft:scManual.intro.1").setStyle(Style.EMPTY.withUnderlined(true));
	private final Component ourPatrons = Utils.localize("gui.securitycraft:scManual.patreon.title");
	private List<HoverChecker> hoverCheckers = new ArrayList<>();
	private int currentPage = lastPage;
	private NonNullList<Ingredient> recipe;
	private IngredientDisplay[] displays = new IngredientDisplay[9];
	private int startX = -1;
	private List<FormattedText> subpages = new ArrayList<>();
	private List<FormattedCharSequence> author = new ArrayList<>();
	private int currentSubpage = 0;
	private List<FormattedCharSequence> intro2;
	private PatronList patronList;
	private Button patreonLinkButton;
	private Button nextSubpage;
	private Button previousSubpage;
	private boolean explosive, ownable, passcodeProtected, viewActivated, customizable, lockable, moduleInventory;
	private IngredientDisplay pageIcon;
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
		previousSubpage = addRenderableWidget(new ChangePageButton(startX + 155, startY + 97, false, b -> previousSubpage()));
		nextSubpage = addRenderableWidget(new ChangePageButton(startX + 180, startY + 97, true, b -> nextSubpage()));
		addRenderableWidget(new ChangePageButton(startX + 22, startY + 188, false, b -> previousPage()));
		addRenderableWidget(new ChangePageButton(startX + 210, startY + 188, true, b -> nextPage()));

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				displays[(i * 3) + j] = addRenderableOnly(new IngredientDisplay((startX + 101) + (j * 19), 144 + (i * 19)));
			}
		}

		pageIcon = addRenderableOnly(new IngredientDisplay(startX + 19, 22));
		updateRecipeAndIcons();
		SCManualItem.PAGES.sort((page1, page2) -> {
			String key1 = page1.title().getString();
			String key2 = page2.title().getString();

			return key1.compareTo(key2);
		});
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		renderBackground(pose);

		if (currentPage == -1)
			RenderSystem._setShaderTexture(0, TITLE_PAGE);
		else if (recipe != null && !recipe.isEmpty())
			RenderSystem._setShaderTexture(0, PAGE);
		else
			RenderSystem._setShaderTexture(0, PAGE_WITH_SCROLL);

		blit(pose, startX, 5, 0, 0, 256, 250);

		for (Renderable renderable : renderables) {
			renderable.render(pose, mouseX, mouseY, partialTicks);
		}

		if (currentPage > -1) {
			String pageNumberText = (currentPage + 2) + "/" + (SCManualItem.PAGES.size() + 1); //+1 because the "welcome" page is not included

			if (subpages.size() > 1)
				font.draw(pose, (currentSubpage + 1) + "/" + subpages.size(), startX + 205, 102, 0x8E8270);

			if (designedBy != null)
				font.drawWordWrap(pose, designedBy, startX + 18, 150, 75, 0);

			font.draw(pose, pageTitle, startX + 39, 27, 0);
			font.drawWordWrap(pose, subpages.get(currentSubpage), startX + 18, 45, 225, 0);
			font.draw(pose, pageNumberText, startX + 240 - font.width(pageNumberText), 182, 0x8E8270);
			RenderSystem._setShaderTexture(0, ICONS);

			if (ownable)
				blit(pose, startX + 29, 118, 1, 1, 16, 16);

			if (passcodeProtected)
				blit(pose, startX + 55, 118, 18, 1, 17, 16);

			if (viewActivated)
				blit(pose, startX + 81, 118, 36, 1, 17, 16);

			if (explosive)
				blit(pose, startX + 107, 117, 54, 1, 18, 18);

			if (customizable)
				blit(pose, startX + 136, 118, 88, 1, 16, 16);

			if (moduleInventory)
				blit(pose, startX + 163, 118, 105, 1, 16, 16);

			if (lockable)
				blit(pose, startX + 189, 118, 154, 1, 16, 16);

			if (customizable || moduleInventory)
				blit(pose, startX + 213, 118, 72, 1, 16, 16);

			for (int i = 0; i < hoverCheckers.size(); i++) {
				HoverChecker chc = hoverCheckers.get(i);

				if (chc != null && chc.checkHover(mouseX, mouseY)) {
					if (chc instanceof TextHoverChecker thc && thc.getName() != null) {
						renderComponentTooltip(pose, thc.getLines(), mouseX, mouseY);
						break;
					}
					else if (i < displays.length && !displays[i].getCurrentStack().isEmpty()) {
						renderTooltip(pose, displays[i].getCurrentStack(), mouseX, mouseY);
						break;
					}
				}
			}
		}
		else { //"welcome" page
			String pageNumberText = "1/" + (SCManualItem.PAGES.size() + 1); //+1 because the "welcome" page is not included

			font.draw(pose, intro1, width / 2 - font.width(intro1) / 2, 22, 0);

			for (int i = 0; i < intro2.size(); i++) {
				FormattedCharSequence text = intro2.get(i);

				font.draw(pose, text, width / 2 - font.width(text) / 2, 150 + 10 * i, 0);
			}

			for (int i = 0; i < author.size(); i++) {
				FormattedCharSequence text = author.get(i);

				font.draw(pose, text, width / 2 - font.width(text) / 2, 180 + 10 * i, 0);
			}

			font.draw(pose, pageNumberText, startX + 240 - font.width(pageNumberText), 182, 0x8E8270);
			font.draw(pose, ourPatrons, width / 2 - font.width(ourPatrons) / 2 + 30, 40, 0);
		}
	}

	@Override
	public void removed() {
		super.removed();
		lastPage = currentPage;
	}

	private void hideSubpageButtonsOnMainPage() {
		nextSubpage.visible = currentPage != -1 && subpages.size() > 1;
		previousSubpage.visible = currentPage != -1 && subpages.size() > 1;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
		if (Screen.hasShiftDown()) {
			for (IngredientDisplay display : displays) {
				if (display != null)
					display.changeRenderingStack(-scroll);
			}

			if (pageIcon != null)
				pageIcon.changeRenderingStack(-scroll);

			return true;
		}

		if (currentPage == -1 && patronList != null && patronList.isMouseOver(mouseX, mouseY) && !patronList.patrons.isEmpty()) {
			patronList.mouseScrolled(mouseX, mouseY, scroll);
			return true;
		}

		if (Screen.hasControlDown() && subpages.size() > 1) {
			switch ((int) Math.signum(scroll)) {
				case -1:
					nextSubpage();
					break;
				case 1:
					previousSubpage();
					break;
			}

			return true;
		}

		switch ((int) Math.signum(scroll)) {
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
			for (IngredientDisplay display : displays) {
				display.setIngredient(Ingredient.EMPTY);
			}

			pageIcon.setIngredient(Ingredient.EMPTY);
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
			this.designedBy = Utils.localize("gui.securitycraft:scManual.designedBy", designerName);
		else
			this.designedBy = null;

		recipe = null;

		if (pageGroup == PageGroup.NONE) {
			Level level = Minecraft.getInstance().level;
			RegistryAccess registryAccess = level.registryAccess();

			for (Recipe<?> object : level.getRecipeManager().getRecipes()) {
				if (object instanceof ShapedRecipe shapedRecipe) {
					ItemStack resultItem = shapedRecipe.getResultItem(registryAccess);

					if (resultItem.is(item) && !(resultItem.is(SCContent.LENS.get()) && SCContent.LENS.get().hasCustomColor(resultItem))) {
						NonNullList<Ingredient> ingredients = shapedRecipe.getIngredients();
						NonNullList<Ingredient> recipeItems = NonNullList.<Ingredient>withSize(9, Ingredient.EMPTY);

						for (int i = 0; i < ingredients.size(); i++) {
							recipeItems.set(getCraftMatrixPosition(i, shapedRecipe.getWidth(), shapedRecipe.getHeight()), ingredients.get(i));
						}

						this.recipe = recipeItems;
						break;
					}
				}
				else if (object instanceof ShapelessRecipe shapelessRecipe && shapelessRecipe.getResultItem(registryAccess).is(item)) {
					//don't show keycard reset recipes
					if (shapelessRecipe.getId().getPath().endsWith("_reset"))
						continue;

					NonNullList<Ingredient> recipeItems = NonNullList.<Ingredient>withSize(shapelessRecipe.getIngredients().size(), Ingredient.EMPTY);

					for (int i = 0; i < recipeItems.size(); i++) {
						recipeItems.set(i, shapelessRecipe.getIngredients().get(i));
					}

					this.recipe = recipeItems;
					break;
				}
			}
		}
		else if (pageGroup.hasRecipeGrid()) {
			Level level = Minecraft.getInstance().level;
			RegistryAccess registryAccess = level.registryAccess();
			Map<Integer, ItemStack[]> recipeStacks = new HashMap<>();
			List<Item> pageItems = Arrays.stream(pageGroup.getItems().getItems()).map(ItemStack::getItem).toList();
			int stacksLeft = pageItems.size();

			for (int i = 0; i < 9; i++) {
				recipeStacks.put(i, new ItemStack[pageItems.size()]);
			}

			for (Recipe<?> object : Minecraft.getInstance().level.getRecipeManager().getRecipes()) {
				if (stacksLeft == 0)
					break;

				if (object instanceof ShapedRecipe shapedRecipe) {
					if (!shapedRecipe.getResultItem(registryAccess).isEmpty() && pageItems.contains(shapedRecipe.getResultItem(registryAccess).getItem())) {
						NonNullList<Ingredient> ingredients = shapedRecipe.getIngredients();

						for (int i = 0; i < ingredients.size(); i++) {
							ItemStack[] items = ingredients.get(i).getItems();

							if (items.length == 0)
								continue;

							int indexToAddAt = pageItems.indexOf(shapedRecipe.getResultItem(registryAccess).getItem());

							//first item needs to suffice since multiple recipes are being cycled through
							recipeStacks.get(getCraftMatrixPosition(i, shapedRecipe.getWidth(), shapedRecipe.getHeight()))[indexToAddAt] = items[0];
						}

						stacksLeft--;
					}
				}
				else if (object instanceof ShapelessRecipe shapelessRecipe && !shapelessRecipe.getResultItem(registryAccess).isEmpty() && pageItems.contains(shapelessRecipe.getResultItem(registryAccess).getItem())) {
					//don't show keycard reset recipes
					if (shapelessRecipe.getId().getPath().endsWith("_reset"))
						continue;

					NonNullList<Ingredient> ingredients = shapelessRecipe.getIngredients();

					for (int i = 0; i < ingredients.size(); i++) {
						ItemStack[] items = ingredients.get(i).getItems();

						if (items.length == 0)
							continue;

						int indexToAddAt = pageItems.indexOf(shapelessRecipe.getResultItem(registryAccess).getItem());

						//first item needs to suffice since multiple recipes are being cycled through
						recipeStacks.get(i)[indexToAddAt] = items[0];
					}

					stacksLeft--;
				}
			}

			recipe = NonNullList.withSize(9, Ingredient.EMPTY);
			recipeStacks.forEach((i, stackArray) -> recipe.set(i, Ingredient.of(Arrays.stream(stackArray).map(s -> s == null ? ItemStack.EMPTY : s))));
		}

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
			pageIcon.setIngredient(pageGroup.getItems());
		else
			pageIcon.setIngredient(Ingredient.of(page.item()));

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

					customizable = true;
					display.add(Utils.localize("gui.securitycraft:scManual.options"));
					display.add(Component.literal("---"));

					for (Option<?> option : options) {
						display.add(Component.translatable("gui.securitycraft:scManual.option_text", Component.translatable(option.getDescriptionKey(BlockUtils.getLanguageKeyDenotation(customizableObj))), option.getDefaultInfo()));
						display.add(Component.empty());
					}

					display.remove(display.size() - 1);
					hoverCheckers.add(new TextHoverChecker(118, 118 + 16, startX + 136, (startX + 136) + 16, display));
				}
			}

			if (inWorldObject instanceof IModuleInventory moduleInv && moduleInv.acceptedModules() != null && moduleInv.acceptedModules().length > 0) {
				List<Component> display = new ArrayList<>();

				moduleInventory = true;
				display.add(Utils.localize("gui.securitycraft:scManual.modules"));
				display.add(Component.literal("---"));

				for (ModuleType module : moduleInv.acceptedModules()) {
					display.add(Component.literal("- ").append(Utils.localize(moduleInv.getModuleDescriptionId(BlockUtils.getLanguageKeyDenotation(moduleInv), module))));
					display.add(Component.empty());
				}

				display.remove(display.size() - 1);
				hoverCheckers.add(new TextHoverChecker(118, 118 + 16, startX + 163, (startX + 163) + 16, display));
			}

			if (lockable)
				hoverCheckers.add(new TextHoverChecker(118, 118 + 16, startX + 189, startX + 189 + 16, Utils.localize("gui.securitycraft:scManual.lockable")));

			if (customizable || moduleInventory)
				hoverCheckers.add(new TextHoverChecker(118, 118 + 16, startX + 213, (startX + 213) + 16, Utils.localize("gui.securitycraft:scManual.customizableBlock")));
		}

		if (recipe != null && !recipe.isEmpty()) {
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					int index = (i * 3) + j;

					if (index >= recipe.size())
						displays[index].setIngredient(Ingredient.EMPTY);
					else
						displays[index].setIngredient(recipe.get(index));
				}
			}
		}
		else {
			for (IngredientDisplay display : displays) {
				display.setIngredient(Ingredient.EMPTY);
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
		customizable = false;
		lockable = false;
		moduleInventory = false;
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
			super(client, width, height, top, left, 4, 6, 0xC0BFBBB2, 0xD0BFBBB2, 0xFF8E8270, 0xFF807055, 0xFFD1BFA1);

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
		public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
			if (currentPage == -1) {
				if (patronsAvailable) {
					super.render(pose, mouseX, mouseY, partialTicks);

					//draw tooltip for long patron names
					int mouseListY = (int) (mouseY - top + scrollDistance - border);
					int slotIndex = mouseListY / SLOT_HEIGHT;

					if (mouseX >= left && mouseX < right - 6 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < patrons.size() && mouseY >= top && mouseY <= bottom) {
						String patron = patrons.get(slotIndex);
						int length = font.width(patron);
						int baseY = top + border - (int) scrollDistance;

						if (length >= width - 6) //6 = barWidth
							renderTooltip(pose, List.of(Component.literal(patron)), Optional.empty(), left - 10, baseY + (SLOT_HEIGHT * slotIndex + SLOT_HEIGHT));
					}

					if (patrons.isEmpty()) {
						for (int i = 0; i < noPatronsLines.size(); i++) {
							FormattedCharSequence line = noPatronsLines.get(i);

							font.draw(pose, line, left + width / 2 - font.width(line) / 2, top + 30 + i * 10, 0xFF333333);
						}
					}
				}
				else if (error) {
					for (int i = 0; i < fetchErrorLines.size(); i++) {
						FormattedCharSequence line = fetchErrorLines.get(i);

						font.draw(pose, line, left + width / 2 - font.width(line) / 2, top + 30 + i * 10, 0xFFB00101);
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
					font.draw(pose, loadingText, left + width / 2 - font.width(loadingText) / 2, top + 30, 0);
			}
		}

		@Override
		protected void drawPanel(PoseStack pose, int entryRight, int relativeY, Tesselator tesselator, int mouseX, int mouseY) {
			//draw entry strings
			for (int i = 0; i < patrons.size(); i++) {
				String patron = patrons.get(i);

				if (patron != null && !patron.isEmpty())
					font.draw(pose, patron, left + 2, relativeY + (SLOT_HEIGHT * i), 0);
			}
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
		private final int textureY;

		public ChangePageButton(int xPos, int yPos, boolean forward, OnPress onPress) {
			super(xPos, yPos, 23, 13, Component.empty(), onPress, DEFAULT_NARRATION);
			textureY = forward ? 192 : 205;
		}

		@Override
		public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
			if (visible) {
				isHovered = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
				RenderSystem._setShaderTexture(0, VANILLA_BOOK);
				blit(pose, getX(), getY(), isHoveredOrFocused() ? 23 : 0, textureY, 23, 13);
			}
		}
	}

	static class HyperlinkButton extends Button {
		public HyperlinkButton(int xPos, int yPos, int width, int height, Component displayString, OnPress handler) {
			super(xPos, yPos, width, height, displayString, handler, s -> Component.empty());
		}

		@Override
		public void renderWidget(PoseStack pose, int mouseX, int mouseY, float partial) {
			RenderSystem._setShaderTexture(0, ICONS);
			isHovered = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
			blit(pose, getX(), getY(), isHoveredOrFocused() ? 138 : 122, 1, 16, 16);
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
