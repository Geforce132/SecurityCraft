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
import java.util.stream.Collectors;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.IViewActivated;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.items.SCManualItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.PageGroup;
import net.geforcemods.securitycraft.misc.SCManualPage;
import net.geforcemods.securitycraft.screen.components.HoverChecker;
import net.geforcemods.securitycraft.screen.components.IngredientDisplay;
import net.geforcemods.securitycraft.screen.components.TextHoverChecker;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.client.gui.widget.ExtendedButton;
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
	private boolean explosive, ownable, passwordProtected, viewActivated, customizable, lockable, moduleInventory;
	private IngredientDisplay pageIcon;
	private Component pageTitle, designedBy;
	private PageGroup pageGroup = PageGroup.NONE;

	public SCManualScreen() {
		super(Component.translatable(SCContent.SC_MANUAL.get().getDescriptionId()));
	}

	@Override
	public void init() {
		byte startY = 2;

		startX = (width - 256) / 2;
		minecraft.keyboardHandler.setSendRepeatsToGui(true);
		addRenderableWidget(new ChangePageButton(startX + 210, startY + 188, true, b -> nextPage()));
		addRenderableWidget(new ChangePageButton(startX + 22, startY + 188, false, b -> previousPage()));
		addRenderableWidget(nextSubpage = new ChangePageButton(startX + 180, startY + 97, true, b -> nextSubpage()));
		addRenderableWidget(previousSubpage = new ChangePageButton(startX + 155, startY + 97, false, b -> previousSubpage()));
		addRenderableWidget(patreonLinkButton = new HyperlinkButton(startX + 225, 143, 16, 16, Component.empty(), b -> handleComponentClicked(Style.EMPTY.withClickEvent(new ClickEvent(Action.OPEN_URL, "https://www.patreon.com/Geforce")))));
		addRenderableWidget(patronList = new PatronList(minecraft, 115, 90, 50, startX + 125));

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
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

		if (currentPage == -1)
			RenderSystem._setShaderTexture(0, TITLE_PAGE);
		else if (recipe != null && recipe.size() > 0)
			RenderSystem._setShaderTexture(0, PAGE);
		else
			RenderSystem._setShaderTexture(0, PAGE_WITH_SCROLL);

		blit(pose, startX, 5, 0, 0, 256, 250);

		for (Widget widget : renderables) {
			widget.render(pose, mouseX, mouseY, partialTicks);
		}

		if (currentPage > -1) {
			String pageNumberText = (currentPage + 2) + "/" + (SCManualItem.PAGES.size() + 1); //+1 because the "welcome" page is not included

			if (subpages.size() > 1)
				font.draw(pose, (currentSubpage + 1) + "/" + subpages.size(), startX + 205, 102, 0x8E8270);

			if (designedBy != null)
				font.drawWordWrap(designedBy, startX + 18, 150, 75, 0);

			font.draw(pose, pageTitle, startX + 39, 27, 0);
			font.drawWordWrap(subpages.get(currentSubpage), startX + 18, 45, 225, 0);
			font.draw(pose, pageNumberText, startX + 240 - font.width(pageNumberText), 182, 0x8E8270);
			RenderSystem._setShaderTexture(0, ICONS);

			if (ownable)
				blit(pose, startX + 29, 118, 1, 1, 16, 16);

			if (passwordProtected)
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
		minecraft.keyboardHandler.setSendRepeatsToGui(false);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_LEFT)
			previousSubpage();
		else if (keyCode == GLFW.GLFW_KEY_RIGHT)
			nextSubpage();

		return super.keyPressed(keyCode, scanCode, modifiers);
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
		String designedBy = page.designedBy();
		Item item = page.item();

		if (designedBy != null && !designedBy.isEmpty())
			this.designedBy = Utils.localize("gui.securitycraft:scManual.designedBy", designedBy);
		else
			this.designedBy = null;

		recipe = null;
		pageGroup = page.group();

		if (pageGroup == PageGroup.NONE) {
			for (Recipe<?> object : Minecraft.getInstance().level.getRecipeManager().getRecipes()) {
				if (object instanceof ShapedRecipe recipe) {
					if (!recipe.getResultItem().isEmpty() && recipe.getResultItem().getItem() == item) {
						NonNullList<Ingredient> ingredients = recipe.getIngredients();
						NonNullList<Ingredient> recipeItems = NonNullList.<Ingredient>withSize(9, Ingredient.EMPTY);

						for (int i = 0; i < ingredients.size(); i++) {
							recipeItems.set(getCraftMatrixPosition(i, recipe.getWidth(), recipe.getHeight()), ingredients.get(i));
						}

						this.recipe = recipeItems;
						break;
					}
				}
				else if (object instanceof ShapelessRecipe recipe) {
					if (!recipe.getResultItem().isEmpty() && recipe.getResultItem().getItem() == item) {
						//don't show keycard reset recipes
						if (recipe.getId().getPath().endsWith("_reset"))
							continue;

						NonNullList<Ingredient> recipeItems = NonNullList.<Ingredient>withSize(recipe.getIngredients().size(), Ingredient.EMPTY);

						for (int i = 0; i < recipeItems.size(); i++) {
							recipeItems.set(i, recipe.getIngredients().get(i));
						}

						this.recipe = recipeItems;
						break;
					}
				}
			}
		}
		else if (pageGroup.hasRecipeGrid()) {
			Map<Integer, ItemStack[]> recipeStacks = new HashMap<>();
			List<Item> pageItems = Arrays.stream(pageGroup.getItems().getItems()).map(ItemStack::getItem).toList();
			int stacksLeft = pageItems.size();

			for (int i = 0; i < 9; i++) {
				recipeStacks.put(i, new ItemStack[pageItems.size()]);
			}

			for (Recipe<?> object : Minecraft.getInstance().level.getRecipeManager().getRecipes()) {
				if (stacksLeft == 0)
					break;

				if (object instanceof ShapedRecipe recipe) {
					if (!recipe.getResultItem().isEmpty() && pageItems.contains(recipe.getResultItem().getItem())) {
						NonNullList<Ingredient> ingredients = recipe.getIngredients();

						for (int i = 0; i < ingredients.size(); i++) {
							ItemStack[] items = ingredients.get(i).getItems();

							if (items.length == 0)
								continue;

							int indexToAddAt = pageItems.indexOf(recipe.getResultItem().getItem());

							//first item needs to suffice since multiple recipes are being cycled through
							recipeStacks.get(getCraftMatrixPosition(i, recipe.getWidth(), recipe.getHeight()))[indexToAddAt] = items[0];
						}

						stacksLeft--;
					}
				}
				else if (object instanceof ShapelessRecipe recipe) {
					if (!recipe.getResultItem().isEmpty() && pageItems.contains(recipe.getResultItem().getItem())) {
						//don't show keycard reset recipes
						if (recipe.getId().getPath().endsWith("_reset"))
							continue;

						NonNullList<Ingredient> ingredients = recipe.getIngredients();

						for (int i = 0; i < ingredients.size(); i++) {
							ItemStack[] items = ingredients.get(i).getItems();

							if (items.length == 0)
								continue;

							int indexToAddAt = pageItems.indexOf(recipe.getResultItem().getItem());

							//first item needs to suffice since multiple recipes are being cycled through
							recipeStacks.get(i)[indexToAddAt] = items[0];
						}

						stacksLeft--;
					}
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

			if (explosive = block instanceof IExplosive)
				hoverCheckers.add(new TextHoverChecker(118, 118 + 16, startX + 107, (startX + 107) + 16, Utils.localize("gui.securitycraft:scManual.explosiveBlock")));

			if (block.defaultBlockState().hasBlockEntity()) {
				BlockEntity te = ((EntityBlock) block).newBlockEntity(BlockPos.ZERO, block.defaultBlockState());

				if (ownable = te instanceof IOwnable)
					hoverCheckers.add(new TextHoverChecker(118, 118 + 16, startX + 29, (startX + 29) + 16, Utils.localize("gui.securitycraft:scManual.ownableBlock")));

				if (passwordProtected = te instanceof IPasswordProtected)
					hoverCheckers.add(new TextHoverChecker(118, 118 + 16, startX + 55, (startX + 55) + 16, Utils.localize("gui.securitycraft:scManual.passwordProtectedBlock")));

				if (viewActivated = te instanceof IViewActivated)
					hoverCheckers.add(new TextHoverChecker(118, 118 + 16, startX + 81, (startX + 81) + 16, Utils.localize("gui.securitycraft:scManual.viewActivatedBlock")));

				if (te instanceof ICustomizable customizableBe) {
					Option<?>[] options = customizableBe.customOptions();

					if (options != null && options.length > 0) {
						List<Component> display = new ArrayList<>();

						customizable = true;
						display.add(Utils.localize("gui.securitycraft:scManual.options"));
						display.add(Component.literal("---"));

						for (Option<?> option : options) {
							display.add(Component.literal("- ").append(Utils.localize(option.getDescriptionKey(block))));
							display.add(Component.empty());
						}

						display.remove(display.size() - 1);
						hoverCheckers.add(new TextHoverChecker(118, 118 + 16, startX + 136, (startX + 136) + 16, display));
					}
				}

				if (te instanceof IModuleInventory moduleInv && moduleInv.acceptedModules() != null && moduleInv.acceptedModules().length > 0) {
					List<Component> display = new ArrayList<>();

					moduleInventory = true;
					display.add(Utils.localize("gui.securitycraft:scManual.modules"));
					display.add(Component.literal("---"));

					for (ModuleType module : moduleInv.acceptedModules()) {
						display.add(Component.literal("- ").append(Utils.localize("module" + block.getDescriptionId().substring(5) + "." + module.getItem().getDescriptionId().substring(5).replace("securitycraft.", "") + ".description")));
						display.add(Component.empty());
					}

					display.remove(display.size() - 1);
					hoverCheckers.add(new TextHoverChecker(118, 118 + 16, startX + 163, (startX + 163) + 16, display));
				}

				if (lockable = te instanceof ILockable)
					hoverCheckers.add(new TextHoverChecker(118, 118 + 16, startX + 189, startX + 189 + 16, Utils.localize("gui.securitycraft:scManual.lockable")));

				if (customizable || moduleInventory)
					hoverCheckers.add(new TextHoverChecker(118, 118 + 16, startX + 213, (startX + 213) + 16, Utils.localize("gui.securitycraft:scManual.customizableBlock")));
			}
		}

		if (recipe != null && recipe.size() > 0) {
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
		passwordProtected = false;
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

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (patronList != null)
			patronList.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);

		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	class PatronList extends ScrollPanel {
		private static final String PATRON_LIST_LINK = FMLEnvironment.production ? "https://gist.githubusercontent.com/bl4ckscor3/bdda6596012b1206816db034350b5717/raw" : "https://gist.githubusercontent.com/bl4ckscor3/3196e6740774e386871a74a9606eaa61/raw";
		private final int slotHeight = 12;
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

			if (height < bottom - top - 8)
				height = bottom - top - 8;

			return height;
		}

		@Override
		public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
			if (currentPage == -1) {
				if (patronsAvailable) {
					super.render(pose, mouseX, mouseY, partialTicks);

					//draw tooltip for long patron names
					int mouseListY = (int) (mouseY - top + scrollDistance - border);
					int slotIndex = mouseListY / slotHeight;

					if (mouseX >= left && mouseX < right - 6 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < patrons.size() && mouseY >= top && mouseY <= bottom) {
						String patron = patrons.get(slotIndex);
						int length = font.width(patron);
						int baseY = top + border - (int) scrollDistance;

						if (length >= width - 6) //6 = barWidth
							renderTooltip(pose, List.of(Component.literal(patron)), Optional.empty(), left - 10, baseY + (slotHeight * slotIndex + slotHeight));
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
					font.draw(pose, patron, left + 2, relativeY + (slotHeight * i), 0);
			}
		}

		public void fetchPatrons() {
			if (!patronsRequested) {
				//create thread to fetch patrons. without this, and for example if the player has no internet connection, the game will hang
				patronRequestFuture = executor.submit(() -> {
					try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(PATRON_LIST_LINK).openStream()))) {
						return reader.lines().collect(Collectors.toList());
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

	static class ChangePageButton extends ExtendedButton {
		private final int textureY;

		public ChangePageButton(int xPos, int yPos, boolean forward, OnPress onPress) {
			super(xPos, yPos, 23, 13, Component.empty(), onPress);
			textureY = forward ? 192 : 205;
		}

		@Override
		public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
			if (visible) {
				isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
				RenderSystem._setShaderTexture(0, VANILLA_BOOK);
				blit(pose, x, y, isHoveredOrFocused() ? 23 : 0, textureY, 23, 13);
			}
		}
	}

	static class HyperlinkButton extends Button {
		public HyperlinkButton(int xPos, int yPos, int width, int height, Component displayString, OnPress handler) {
			super(xPos, yPos, width, height, displayString, handler);
		}

		@Override
		public void renderButton(PoseStack pose, int mouseX, int mouseY, float partial) {
			RenderSystem._setShaderTexture(0, ICONS);
			isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
			blit(pose, x, y, isHoveredOrFocused() ? 138 : 122, 1, 16, 16);
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
