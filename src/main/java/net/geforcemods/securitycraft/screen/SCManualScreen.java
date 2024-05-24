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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

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
import net.geforcemods.securitycraft.screen.components.ColorableScrollPanel;
import net.geforcemods.securitycraft.screen.components.HoverChecker;
import net.geforcemods.securitycraft.screen.components.IngredientDisplay;
import net.geforcemods.securitycraft.screen.components.StringHoverChecker;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.ClickEvent.Action;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class SCManualScreen extends GuiScreen {
	private static final ResourceLocation PAGE = new ResourceLocation("securitycraft:textures/gui/info_book_texture.png");
	private static final ResourceLocation PAGE_WITH_SCROLL = new ResourceLocation("securitycraft:textures/gui/info_book_texture_special.png"); //for items without a recipe
	private static final ResourceLocation TITLE_PAGE = new ResourceLocation("securitycraft:textures/gui/info_book_title_page.png");
	private static final ResourceLocation BOOK_ICONS = new ResourceLocation("securitycraft:textures/gui/info_book_icons.png");
	private static final ResourceLocation VANILLA_BOOK = new ResourceLocation("textures/gui/book.png");
	private static final int SUBPAGE_LENGTH = 1285;
	private static int lastPage = -1;
	private final String intro1 = Utils.localize("gui.securitycraft:scManual.intro.1").setStyle(new Style().setUnderlined(true)).getFormattedText();
	private final String ourPatrons = Utils.localize("gui.securitycraft:scManual.patreon.title").getFormattedText();
	private final Style crowdinLinkStyle = new Style().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://crowdin.com/project/securitycraft"));
	private List<HoverChecker> hoverCheckers = new ArrayList<>();
	private int currentPage = lastPage;
	private NonNullList<Ingredient> recipe;
	private IngredientDisplay[] displays = new IngredientDisplay[9];
	private int startX = -1;
	private List<String> subpages = new ArrayList<>();
	private List<String> translationCredits;
	private HoverChecker translationCreditsArea;
	private int currentSubpage = 0;
	private List<String> intro2;
	private PatronList patronList;
	private GuiButton patreonLinkButton;
	private boolean explosive, ownable, passcodeProtected, viewActivated, customizable, lockable, moduleInventory;
	private IngredientDisplay pageIcon;
	private String pageTitle, designedBy;

	@Override
	public void initGui() {
		byte startY = 2;

		startX = (width - 256) / 2;
		Keyboard.enableRepeatEvents(true);
		addButton(new SCManualScreen.ChangePageButton(1, startX + 210, startY + 188, true)); //next page
		addButton(new SCManualScreen.ChangePageButton(2, startX + 16, startY + 188, false)); //previous page
		addButton(new SCManualScreen.ChangePageButton(3, startX + 180, startY + 97, true)); //next subpage
		addButton(new SCManualScreen.ChangePageButton(4, startX + 155, startY + 97, false)); //previous subpage
		patreonLinkButton = addButton(new HyperlinkButton(5, startX + 225, 143, 16, 16, ""));
		patronList = new PatronList(mc, 115, 90, 50, startX + 125);

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				displays[(i * 3) + j] = new IngredientDisplay((startX + 101) + (j * 19), 144 + (i * 19));
			}
		}

		pageIcon = new IngredientDisplay(startX + 19, 22);
		updateRecipeAndIcons();
		SCManualItem.PAGES.sort((page1, page2) -> {
			String key1 = page1.getTitle().getFormattedText();
			String key2 = page2.getTitle().getFormattedText();

			return key1.compareTo(key2);
		});
		translationCredits = fontRenderer.listFormattedStringToWidth(Utils.localize("gui.securitycraft:scManual.crowdin").setStyle(new Style().setColor(TextFormatting.BLUE).setUnderlined(true)).getFormattedText(), 180);
		translationCreditsArea = new HoverChecker(180, 200, width / 2 - 90, width / 2 + 90);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		if (currentPage == -1)
			mc.getTextureManager().bindTexture(TITLE_PAGE);
		else if (recipe != null || SCManualItem.PAGES.get(currentPage).isRecipeDisabled())
			mc.getTextureManager().bindTexture(PAGE);
		else
			mc.getTextureManager().bindTexture(PAGE_WITH_SCROLL);

		drawTexturedModalRect(startX, 5, 0, 0, 256, 250);

		for (int i = 0; i < buttonList.size(); i++) {
			buttonList.get(i).drawButton(mc, mouseX, mouseY, 0);
		}

		if (currentPage > -1) {
			String pageNumberText = (currentPage + 2) + "/" + (SCManualItem.PAGES.size() + 1); //+1 because the "welcome" page is not included

			if (subpages.size() > 1)
				fontRenderer.drawString((currentSubpage + 1) + "/" + subpages.size(), startX + 205, 102, 0x8E8270);

			if (designedBy != null)
				fontRenderer.drawSplitString(designedBy, startX + 18, 150, 75, 0);

			fontRenderer.drawString(pageTitle, startX + 39, 27, 0, false);
			fontRenderer.drawString(pageNumberText, startX + 240 - fontRenderer.getStringWidth(pageNumberText), 182, 0x8E8270);
			fontRenderer.drawSplitString(subpages.get(currentSubpage), startX + 18, 45, 225, 0);
			Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			pageIcon.render(partialTicks);

			for (IngredientDisplay display : displays) {
				if (display != null)
					display.render(partialTicks);
			}

			mc.getTextureManager().bindTexture(BOOK_ICONS);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

			if (explosive)
				drawTexturedModalRect(startX + 107, 117, 54, 1, 18, 18);

			if (ownable)
				drawTexturedModalRect(startX + 29, 118, 1, 1, 16, 16);

			if (passcodeProtected)
				drawTexturedModalRect(startX + 55, 118, 18, 1, 17, 16);

			if (viewActivated)
				drawTexturedModalRect(startX + 81, 118, 36, 1, 17, 16);

			if (customizable)
				drawTexturedModalRect(startX + 136, 118, 88, 1, 16, 16);

			if (moduleInventory)
				drawTexturedModalRect(startX + 163, 118, 105, 1, 16, 16);

			if (lockable)
				drawTexturedModalRect(startX + 189, 118, 154, 1, 16, 16);

			if (customizable || moduleInventory)
				drawTexturedModalRect(startX + 213, 118, 72, 1, 16, 16);

			for (int i = 0; i < hoverCheckers.size(); i++) {
				HoverChecker chc = hoverCheckers.get(i);

				if (chc != null && chc.checkHover(mouseX, mouseY)) {
					if (chc instanceof StringHoverChecker && ((StringHoverChecker) chc).getName() != null)
						drawHoveringText(((StringHoverChecker) chc).getLines(), mouseX, mouseY);
					else if (i < displays.length && !displays[i].getCurrentStack().isEmpty())
						renderToolTip(displays[i].getCurrentStack(), mouseX, mouseY);
				}
			}
		}
		else { //"welcome" page
			String pageNumberText = "1/" + (SCManualItem.PAGES.size() + 1); //+1 because the "welcome" page is not included

			fontRenderer.drawString(intro1, width / 2 - fontRenderer.getStringWidth(intro1) / 2, 22, 0, false);

			for (int i = 0; i < intro2.size(); i++) {
				String text = intro2.get(i);

				fontRenderer.drawString(text, width / 2 - fontRenderer.getStringWidth(text) / 2, 150 + 10 * i, 0);
			}

			for (int i = 0; i < translationCredits.size(); i++) {
				String text = translationCredits.get(i);

				fontRenderer.drawString(text, width / 2 - fontRenderer.getStringWidth(text) / 2, 180 + 10 * i, 0);
			}

			//the patreon link button may overlap with a name tooltip from the list, so draw the list after the buttons
			if (patronList != null)
				patronList.drawScreen(mouseX, mouseY);

			fontRenderer.drawString(pageNumberText, startX + 240 - fontRenderer.getStringWidth(pageNumberText), 182, 0x8E8270);
			fontRenderer.drawString(ourPatrons, width / 2 - fontRenderer.getStringWidth(ourPatrons) / 2 + 30, 40, 0);
		}
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		lastPage = currentPage;
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);

		if (keyCode == Keyboard.KEY_LEFT)
			previousSubpage();
		else if (keyCode == Keyboard.KEY_RIGHT)
			nextSubpage();
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.id == 1)
			nextPage();
		else if (button.id == 2)
			previousPage();
		else if (button.id == 3)
			nextSubpage();
		else if (button.id == 4)
			previousSubpage();
		else if (button.id == patreonLinkButton.id) {
			handleComponentClick(new TextComponentString("").setStyle(new Style().setClickEvent(new ClickEvent(Action.OPEN_URL, "https://www.patreon.com/Geforce"))));
			return;
		}

		//hide subpage buttons on main page
		buttonList.get(2).visible = currentPage != -1 && subpages.size() > 1;
		buttonList.get(3).visible = currentPage != -1 && subpages.size() > 1;
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();

		if (ClientUtils.hasShiftDown()) {
			for (IngredientDisplay display : displays) {
				if (display != null)
					display.changeRenderingStack(-Mouse.getEventDWheel());
			}

			if (pageIcon != null)
				pageIcon.changeRenderingStack(-Mouse.getEventDWheel());

			return;
		}

		if (currentPage == -1) {
			int mouseX = Mouse.getEventX() * width / mc.displayWidth;
			int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;

			if (Mouse.isButtonDown(0) && translationCreditsArea.checkHover(mouseX, mouseY)) {
				handleComponentClick(new TextComponentString("").setStyle(crowdinLinkStyle));
				mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
				return;
			}
			else if (patronList != null && patronList.isHovering() && !patronList.patrons.isEmpty()) {
				patronList.handleMouseInput(mouseX, mouseY);
				return;
			}
		}

		if (ClientUtils.hasCtrlDown() && subpages.size() > 1) {
			switch ((int) Math.signum(Mouse.getEventDWheel())) {
				case -1:
					nextSubpage();
					break;
				case 1:
					previousSubpage();
					break;
			}

			return;
		}

		switch ((int) Math.signum(Mouse.getEventDWheel())) {
			case -1:
				nextPage();
				break;
			case 1:
				previousPage();
				break;
		}

		//hide subpage buttons on main page
		buttonList.get(2).visible = currentPage != -1 && subpages.size() > 1;
		buttonList.get(3).visible = currentPage != -1 && subpages.size() > 1;
	}

	private void nextPage() {
		currentPage++;

		if (currentPage > SCManualItem.PAGES.size() - 1)
			currentPage = -1;

		updateRecipeAndIcons();
	}

	private void previousPage() {
		currentPage--;

		if (currentPage < -1)
			currentPage = SCManualItem.PAGES.size() - 1;

		updateRecipeAndIcons();
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
			buttonList.get(2).visible = false;
			buttonList.get(3).visible = false;
			recipe = null;
			intro2 = fontRenderer.listFormattedStringToWidth(Utils.localize("gui.securitycraft:scManual.intro.2").getFormattedText(), 203);
			patronList.fetchPatrons();
			return;
		}

		SCManualPage page = SCManualItem.PAGES.get(currentPage);
		String designerName = page.getDesignedBy();
		Item item = page.getItem();
		PageGroup pageType = page.getPageType();

		if (designerName != null && !designerName.isEmpty())
			designedBy = Utils.localize("gui.securitycraft:scManual.designedBy", designerName).getFormattedText();
		else
			designedBy = null;

		recipe = null;

		if (pageType == PageGroup.SINGLE_ITEM) {
			for (int o = 0; o < CraftingManager.REGISTRY.getKeys().size(); o++) {
				IRecipe object = CraftingManager.REGISTRY.getObjectById(o);

				if (object instanceof ShapedRecipes) {
					ShapedRecipes shapedRecipe = (ShapedRecipes) object;
					ItemStack resultStack = shapedRecipe.getRecipeOutput();
					Item resultItem = resultStack.getItem();

					if (resultItem == item && !(resultItem == SCContent.lens && SCContent.lens.hasColor(resultStack))) {
						NonNullList<Ingredient> ingredients = shapedRecipe.getIngredients();
						NonNullList<Ingredient> recipeItems = NonNullList.<Ingredient>withSize(9, Ingredient.EMPTY);

						for (int i = 0; i < ingredients.size(); i++) {
							recipeItems.set(getCraftMatrixPosition(i, shapedRecipe.getWidth(), shapedRecipe.getHeight()), ingredients.get(i));
						}

						this.recipe = recipeItems;
						break;
					}
				}
				else if (object instanceof ShapelessRecipes) {
					ShapelessRecipes shapelessRecipe = (ShapelessRecipes) object;

					if (shapelessRecipe.getRecipeOutput().getItem() == page.getItem()) {
						//don't show keycard reset recipes
						if (shapelessRecipe.getRegistryName().getPath().endsWith("_reset"))
							continue;

						NonNullList<Ingredient> recipeItems = NonNullList.<Ingredient>withSize(shapelessRecipe.recipeItems.size(), Ingredient.EMPTY);

						for (int i = 0; i < recipeItems.size(); i++) {
							recipeItems.set(i, shapelessRecipe.recipeItems.get(i));
						}

						this.recipe = recipeItems;
						break;
					}
				}
			}
		}
		else if (pageType.hasRecipeGrid()) {
			recipe = null;

			Map<Integer, ItemStack[]> recipeStacks = new HashMap<>();
			List<Item> pageItems = Arrays.stream(pageType.getItems().getMatchingStacks()).map(ItemStack::getItem).collect(Collectors.toList());
			int stacksLeft = pageItems.size();

			for (int i = 0; i < 9; i++) {
				recipeStacks.put(i, new ItemStack[pageItems.size()]);
			}

			for (int o = 0; o < CraftingManager.REGISTRY.getKeys().size(); o++) {
				if (stacksLeft == 0)
					break;

				IRecipe object = CraftingManager.REGISTRY.getObjectById(o);

				if (object instanceof ShapedRecipes) {
					ShapedRecipes shapedRecipe = (ShapedRecipes) object;

					if (!shapedRecipe.getRecipeOutput().isEmpty() && pageItems.contains(shapedRecipe.getRecipeOutput().getItem())) {
						NonNullList<Ingredient> ingredients = shapedRecipe.getIngredients();

						for (int i = 0; i < ingredients.size(); i++) {
							ItemStack[] items = ingredients.get(i).getMatchingStacks();

							if (items.length == 0)
								continue;

							int indexToAddAt = pageItems.indexOf(shapedRecipe.getRecipeOutput().getItem());

							//first item needs to suffice since multiple recipes are being cycled through
							recipeStacks.get(getCraftMatrixPosition(i, shapedRecipe.getWidth(), shapedRecipe.getHeight()))[indexToAddAt] = items[0];
						}

						stacksLeft--;
					}
				}
				else if (object instanceof ShapelessRecipes) {
					ShapelessRecipes shapelessRecipe = (ShapelessRecipes) object;

					if (!shapelessRecipe.getRecipeOutput().isEmpty() && pageItems.contains(shapelessRecipe.getRecipeOutput().getItem())) {
						//don't show keycard reset recipes
						if (shapelessRecipe.getRegistryName().getPath().endsWith("_reset"))
							continue;

						NonNullList<Ingredient> ingredients = shapelessRecipe.getIngredients();

						for (int i = 0; i < ingredients.size(); i++) {
							ItemStack[] items = ingredients.get(i).getMatchingStacks();

							if (items.length == 0)
								continue;

							int indexToAddAt = pageItems.indexOf(shapelessRecipe.getRecipeOutput().getItem());

							//first item needs to suffice since multiple recipes are being cycled through
							recipeStacks.get(i)[indexToAddAt] = items[0];
						}

						stacksLeft--;
					}
				}
			}

			recipe = NonNullList.withSize(9, Ingredient.EMPTY);
			recipeStacks.forEach((i, stackArray) -> recipe.set(i, Ingredient.fromStacks(Arrays.stream(stackArray).map(s -> s == null ? ItemStack.EMPTY : s).collect(Collectors.toList()).toArray(stackArray))));
		}

		if (pageType == PageGroup.REINFORCED || item == Item.getItemFromBlock(SCContent.reinforcedHopper)) {
			recipe = null;
			hoverCheckers.add(new StringHoverChecker(144, 144 + (2 * 20) + 16, startX + 100, (startX + 100) + (2 * 20) + 16, Utils.localize("gui.securitycraft:scManual.recipe.reinforced").getFormattedText()));
		}
		else if (recipe != null) {
			for (int row = 0; row < 3; row++) {
				for (int column = 0; column < 3; column++) {
					hoverCheckers.add(new HoverChecker(144 + (row * 19), 144 + (row * 19) + 16, (startX + 101) + (column * 19), (startX + 101) + (column * 19) + 16));
				}
			}
		}
		else if (page.isRecipeDisabled())
			hoverCheckers.add(new StringHoverChecker(144, 144 + (2 * 20) + 16, startX + 100, (startX + 100) + (2 * 20) + 16, Utils.localize("gui.securitycraft:scManual.disabled").getFormattedText()));
		else {
			String name = page.getItem().getRegistryName().getPath();

			hoverCheckers.add(new StringHoverChecker(144, 144 + (2 * 20) + 16, startX + 100, (startX + 100) + (2 * 20) + 16, Utils.localize("gui.securitycraft:scManual.recipe." + name).getFormattedText()));
		}

		pageTitle = page.getTitle().getFormattedText();

		if (pageType != PageGroup.SINGLE_ITEM)
			pageIcon.setIngredient(pageType.getItems());
		else
			pageIcon.setIngredient(Ingredient.fromItem(page.getItem()));

		resetTileEntityInfo();

		if (item instanceof ItemBlock) {
			Block block = ((ItemBlock) item).getBlock();

			explosive = block instanceof IExplosive;

			if (explosive)
				hoverCheckers.add(new StringHoverChecker(118, 118 + 16, startX + 107, (startX + 107) + 16, Utils.localize("gui.securitycraft:scManual.explosiveBlock").getFormattedText()));
		}

		Object inWorldObj = page.getInWorldObject();

		if (inWorldObj != null) {
			ownable = inWorldObj instanceof IOwnable;
			passcodeProtected = inWorldObj instanceof IPasscodeProtected;
			viewActivated = inWorldObj instanceof IViewActivated;
			lockable = inWorldObj instanceof ILockable;

			if (ownable)
				hoverCheckers.add(new StringHoverChecker(118, 118 + 16, startX + 29, (startX + 29) + 16, Utils.localize("gui.securitycraft:scManual.ownableBlock").getFormattedText()));

			if (passcodeProtected)
				hoverCheckers.add(new StringHoverChecker(118, 118 + 16, startX + 55, (startX + 55) + 16, Utils.localize("gui.securitycraft:scManual.passcodeProtectedBlock").getFormattedText()));

			if (viewActivated)
				hoverCheckers.add(new StringHoverChecker(118, 118 + 16, startX + 81, (startX + 81) + 16, Utils.localize("gui.securitycraft:scManual.viewActivatedBlock").getFormattedText()));

			if (inWorldObj instanceof ICustomizable) {
				ICustomizable scte = (ICustomizable) inWorldObj;
				Option<?>[] options = scte.customOptions();

				if (options.length > 0) {
					List<String> display = new ArrayList<>();

					customizable = true;
					display.add(Utils.localize("gui.securitycraft:scManual.options").getFormattedText());
					display.add("---");

					for (Option<?> option : options) {
						display.add(new TextComponentTranslation("gui.securitycraft:scManual.option_text", Utils.localize(option.getDescriptionKey(BlockUtils.getLanguageKeyDenotation(scte))), option.getDefaultInfo()).getFormattedText());
						display.add("");
					}

					display.remove(display.size() - 1);
					hoverCheckers.add(new StringHoverChecker(118, 118 + 16, startX + 136, (startX + 136) + 16, display));
				}
			}

			if (inWorldObj instanceof IModuleInventory) {
				IModuleInventory moduleInv = (IModuleInventory) inWorldObj;

				if (moduleInv.acceptedModules() != null && moduleInv.acceptedModules().length > 0) {
					List<String> display = new ArrayList<>();

					moduleInventory = true;
					display.add(Utils.localize("gui.securitycraft:scManual.modules").getFormattedText());
					display.add("---");

					for (ModuleType module : moduleInv.acceptedModules()) {
						display.add(Utils.localize(moduleInv.getModuleDescriptionId(BlockUtils.getLanguageKeyDenotation(inWorldObj), module)).getFormattedText());
						display.add("");
					}

					display.remove(display.size() - 1);
					hoverCheckers.add(new StringHoverChecker(118, 118 + 16, startX + 163, (startX + 163) + 16, display));
				}
			}

			if (lockable)
				hoverCheckers.add(new StringHoverChecker(118, 118 + 16, startX + 189, startX + 189 + 16, Utils.localize("gui.securitycraft:scManual.lockable").getFormattedText()));

			if (customizable || moduleInventory)
				hoverCheckers.add(new StringHoverChecker(118, 118 + 16, startX + 213, (startX + 213) + 16, Utils.localize("gui.securitycraft:scManual.customizableBlock").getFormattedText()));
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
		String helpInfo = page.getHelpInfo().getFormattedText();

		subpages.clear();

		while (fontRenderer.getStringWidth(helpInfo) > SUBPAGE_LENGTH) {
			String trimmed = fontRenderer.trimStringToWidth(helpInfo, SUBPAGE_LENGTH);
			int temp = trimmed.lastIndexOf(' ');

			if (temp > 0)
				trimmed = trimmed.trim().substring(0, temp); //remove last word to remove the possibility to break it up onto multiple pages

			trimmed = trimmed.trim();
			subpages.add(trimmed);
			helpInfo = helpInfo.replace(trimmed, "").trim();
		}

		subpages.add(helpInfo);
	}

	private void resetTileEntityInfo() {
		explosive = false;
		ownable = false;
		passcodeProtected = false;
		viewActivated = false;
		customizable = false;
		lockable = false;
		moduleInventory = false;
	}

	class PatronList extends ColorableScrollPanel {
		private final ExecutorService executor = Executors.newSingleThreadExecutor();
		private Future<List<String>> patronRequestFuture;
		private List<String> patrons = new ArrayList<>();
		private boolean patronsAvailable = false;
		private boolean error = false;
		private boolean patronsRequested;
		private final List<String> fetchErrorLines;
		private final List<String> noPatronsLines;
		private final String loadingText = Utils.localize("gui.securitycraft:scManual.patreon.loading").getFormattedText();

		public PatronList(Minecraft client, int width, int height, int top, int left) {
			super(client, width, height, top, top + height, left, 12, new Color(0xC0, 0xBF, 0xBB, 0xB2), new Color(0xD0, 0xBF, 0xBB, 0xB2), new Color(0x8E, 0x82, 0x70, 0xFF), new Color(0x80, 0x70, 0x55, 0xFF), new Color(0xD1, 0xBF, 0xA1, 0xFF));

			fetchErrorLines = fontRenderer.listFormattedStringToWidth(Utils.localize("gui.securitycraft:scManual.patreon.error").getFormattedText(), listWidth);
			noPatronsLines = fontRenderer.listFormattedStringToWidth(Utils.localize("advancements.empty").getFormattedText(), listWidth - 10);
		}

		@Override
		public int getSize() {
			return patrons.size();
		}

		@Override
		public int getContentHeight() {
			int height = patrons.size() * (fontRenderer.FONT_HEIGHT + 3);

			if (height < bottom - top - 4)
				height = bottom - top - 4;

			return height;
		}

		@Override
		public void drawScreen(int mouseX, int mouseY) {
			if (patronsAvailable) {
				super.drawScreen(mouseX, mouseY);

				//draw tooltip for long patron names
				int mouseListY = (int) (mouseY - top + scrollDistance - BORDER);
				int slotIndex = mouseListY / slotHeight;
				int baseY = top + BORDER - (int) scrollDistance;

				if (mouseX >= left && mouseX < right - 6 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < patrons.size() && mouseY >= top && mouseY <= bottom) {
					String patron = patrons.get(slotIndex);
					int length = fontRenderer.getStringWidth(patron);

					if (length >= listWidth - SCROLL_BAR_WIDTH) {
						drawHoveringText(patron, left - 10, baseY + (slotHeight * slotIndex + slotHeight));
						GlStateManager.disableLighting();
					}
				}

				if (patrons.isEmpty()) {
					for (int i = 0; i < noPatronsLines.size(); i++) {
						String line = noPatronsLines.get(i);

						fontRenderer.drawString(line, left + listWidth / 2 - fontRenderer.getStringWidth(line) / 2, top + 30 + i * 10, 0xFF333333);
					}
				}
			}
			else if (error) {
				for (int i = 0; i < fetchErrorLines.size(); i++) {
					String line = fetchErrorLines.get(i);

					fontRenderer.drawString(line, left + listWidth / 2 - fontRenderer.getStringWidth(line) / 2, top + 30 + i * 10, 0xFFB00101);
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
			else {
				fontRenderer.drawString(loadingText, left + listWidth / 2 - fontRenderer.getStringWidth(loadingText) / 2, top + 30, 0);
			}
		}

		@Override
		public void drawPanel(int entryRight, int relativeY, Tessellator tesselator, int mouseX, int mouseY) {
			//draw entry strings
			for (int i = 0; i < patrons.size(); i++) {
				String patron = patrons.get(i);

				if (patron != null && !patron.isEmpty())
					fontRenderer.drawString(patron, left + 2, relativeY + (slotHeight * i), 0);
			}
		}

		public void fetchPatrons() {
			if (!patronsRequested) {
				//create thread to fetch patrons. without this, and for example if the player has no internet connection, the game will hang
				patronRequestFuture = executor.submit(() -> {
					try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("https://gist.githubusercontent.com/bl4ckscor3/bdda6596012b1206816db034350b5717/raw").openStream()))) {
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
	}

	static class ChangePageButton extends GuiButton {
		private final int textureY;

		public ChangePageButton(int index, int xPos, int yPos, boolean forward) {
			super(index, xPos, yPos, 23, 13, "");
			textureY = forward ? 192 : 205;
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
			if (visible) {
				boolean isHovering = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;

				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(VANILLA_BOOK);
				drawTexturedModalRect(x, y, isHovering ? 23 : 0, textureY, 23, 13);
			}
		}
	}

	class HyperlinkButton extends GuiButtonExt {
		public HyperlinkButton(int id, int xPos, int yPos, int width, int height, String displayString) {
			super(id, xPos, yPos, width, height, displayString);
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
			if (visible) {
				mc.getTextureManager().bindTexture(BOOK_ICONS);
				hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;

				if (hovered)
					drawTexturedModalRect(x, y, 138, 1, 16, 16);
				else
					drawTexturedModalRect(x, y, 122, 1, 16, 16);
			}
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
