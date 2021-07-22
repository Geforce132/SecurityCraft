package net.geforcemods.securitycraft.screen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.SecurityCraftTileEntity;
import net.geforcemods.securitycraft.items.SCManualItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.SCManualPage;
import net.geforcemods.securitycraft.screen.components.HoverChecker;
import net.geforcemods.securitycraft.screen.components.IdButton;
import net.geforcemods.securitycraft.screen.components.IngredientDisplay;
import net.geforcemods.securitycraft.screen.components.TextHoverChecker;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.ScrollPanel;
import net.minecraftforge.fmlclient.gui.GuiUtils;

@OnlyIn(Dist.CLIENT)
public class SCManualScreen extends Screen {

	private ResourceLocation infoBookTexture = new ResourceLocation("securitycraft:textures/gui/info_book_texture.png");
	private ResourceLocation infoBookTextureSpecial = new ResourceLocation("securitycraft:textures/gui/info_book_texture_special.png"); //for items without a recipe
	private ResourceLocation infoBookTitlePage = new ResourceLocation("securitycraft:textures/gui/info_book_title_page.png");
	private ResourceLocation infoBookIcons = new ResourceLocation("securitycraft:textures/gui/info_book_icons.png");
	private static ResourceLocation bookGuiTextures = new ResourceLocation("textures/gui/book.png");
	private List<HoverChecker> hoverCheckers = new ArrayList<>();
	private static int lastPage = -1;
	private int currentPage = lastPage;
	private NonNullList<Ingredient> recipe;
	private IngredientDisplay[] displays = new IngredientDisplay[9];
	private int startX = -1;
	private List<FormattedText> subpages = new ArrayList<>();
	private List<FormattedCharSequence> author = new ArrayList<>();
	private int currentSubpage = 0;
	private final int subpageLength = 1285;
	private final MutableComponent intro1 = Utils.localize("gui.securitycraft:scManual.intro.1").setStyle(Style.EMPTY.setUnderlined(true));
	private final TranslatableComponent ourPatrons = Utils.localize("gui.securitycraft:scManual.patreon.title");
	private List<FormattedCharSequence> intro2;
	private PatronList patronList;
	private Button patreonLinkButton;

	public SCManualScreen() {
		super(new TranslatableComponent(SCContent.SC_MANUAL.get().getDescriptionId()));
	}

	@Override
	public void init(){
		byte startY = 2;

		startX = (width - 256) / 2;
		minecraft.keyboardHandler.setSendRepeatsToGui(true);
		addButton(new SCManualScreen.ChangePageButton(1, startX + 210, startY + 188, true, this::actionPerformed)); //next page
		addButton(new SCManualScreen.ChangePageButton(2, startX + 16, startY + 188, false, this::actionPerformed)); //previous page
		addButton(new SCManualScreen.ChangePageButton(3, startX + 180, startY + 97, true, this::actionPerformed)); //next subpage
		addButton(new SCManualScreen.ChangePageButton(4, startX + 155, startY + 97, false, this::actionPerformed)); //previous subpage
		addButton(patreonLinkButton = new HyperlinkButton(startX + 225, 143, 16, 16, TextComponent.EMPTY, b -> handleComponentClicked(Style.EMPTY.withClickEvent(new ClickEvent(Action.OPEN_URL, "https://www.patreon.com/Geforce")))));
		children.add(patronList = new PatronList(minecraft, 115, 90, 50, startX + 125));

		for(int i = 0; i < 3; i++)
		{
			for(int j = 0; j < 3; j++)
			{
				displays[(i * 3) + j] = new IngredientDisplay((startX + 101) + (j * 19), 144 + (i * 19));
			}
		}

		updateRecipeAndIcons();
		SCManualItem.PAGES.sort((page1, page2) -> {
			String key1 = Utils.localize(page1.getItem().getDescriptionId()).getString();
			String key2 = Utils.localize(page2.getItem().getDescriptionId()).getString();

			return key1.compareTo(key2);
		});
	}

	@Override
	public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks){
		renderBackground(matrix);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

		if(currentPage == -1)
			minecraft.getTextureManager().bind(infoBookTitlePage);
		else if(recipe != null && recipe.size() > 0)
			minecraft.getTextureManager().bind(infoBookTexture);
		else
			minecraft.getTextureManager().bind(infoBookTextureSpecial);

		blit(matrix, startX, 5, 0, 0, 256, 250);

		for(int i = 0; i < buttons.size(); i++)
		{
			buttons.get(i).render(matrix, mouseX, mouseY, partialTicks);
		}

		if(currentPage > -1)
		{
			Item item = SCManualItem.PAGES.get(currentPage).getItem();
			String pageNumberText = (currentPage + 2) + "/" + (SCManualItem.PAGES.size() + 1); //+1 because the "welcome" page is not included
			String designedBy = SCManualItem.PAGES.get(currentPage).getDesignedBy();

			if(subpages.size() > 1)
				font.draw(matrix, (currentSubpage + 1) + "/" + subpages.size(), startX + 205, 102, 0x8E8270);

			if(designedBy != null && !designedBy.isEmpty())
				font.drawWordWrap(Utils.localize("gui.securitycraft:scManual.designedBy", designedBy), startX + 18, 150, 75, 0);

			if(SCManualItem.PAGES.get(currentPage).getHelpInfo().getKey().equals("help.securitycraft:reinforced.info"))
				font.draw(matrix, Utils.localize("gui.securitycraft:scManual.reinforced"), startX + 39, 27, 0);
			else
				font.draw(matrix, Utils.localize(SCManualItem.PAGES.get(currentPage).getItem().getDescriptionId()), startX + 39, 27, 0);

			font.drawWordWrap(subpages.get(currentSubpage), startX + 18, 45, 225, 0);
			font.draw(matrix, pageNumberText, startX + 240 - font.width(pageNumberText), 182, 0x8E8270);
			minecraft.getItemRenderer().renderAndDecorateItem(new ItemStack(item), startX + 19, 22);
			minecraft.getTextureManager().bind(infoBookIcons);

			if(item instanceof BlockItem){
				Block block = ((BlockItem) item).getBlock();

				if(block instanceof IExplosive)
					blit(matrix, startX + 107, 117, 54, 1, 18, 18);

				if(block.hasTileEntity(block.defaultBlockState()))
				{
					BlockEntity te = block.createTileEntity(block.defaultBlockState(), Minecraft.getInstance().level);

					if(te instanceof IOwnable)
						blit(matrix, startX + 29, 118, 1, 1, 16, 16);

					if(te instanceof IPasswordProtected)
						blit(matrix, startX + 55, 118, 18, 1, 17, 16);

					if(te instanceof SecurityCraftTileEntity && ((SecurityCraftTileEntity) te).isActivatedByView())
						blit(matrix, startX + 81, 118, 36, 1, 17, 16);

					if(te instanceof ICustomizable)
					{
						ICustomizable scte = (ICustomizable)te;

						blit(matrix, startX + 213, 118, 72, 1, 16, 16);

						if(scte.customOptions() != null && scte.customOptions().length > 0)
							blit(matrix, startX + 136, 118, 88, 1, 16, 16);
					}

					if(te instanceof IModuleInventory)
					{
						if(((IModuleInventory)te).acceptedModules() != null && ((IModuleInventory)te).acceptedModules().length > 0)
							blit(matrix, startX + 163, 118, 105, 1, 16, 16);
					}
				}
			}

			for(IngredientDisplay display : displays)
			{
				display.render(minecraft, partialTicks);
			}

			for(int i = 0; i < hoverCheckers.size(); i++)
			{
				HoverChecker chc = hoverCheckers.get(i);

				if(chc != null && chc.checkHover(mouseX, mouseY))
				{
					if(chc instanceof TextHoverChecker && ((TextHoverChecker)chc).getName() != null)
						GuiUtils.drawHoveringText(matrix, ((TextHoverChecker)chc).getLines(), mouseX, mouseY, width, height, -1, font);
					else if(i < displays.length && !displays[i].getCurrentStack().isEmpty())
						renderTooltip(matrix, displays[i].getCurrentStack(), mouseX, mouseY);
				}
			}
		}
		else //"welcome" page
		{
			String pageNumberText = "1/" + (SCManualItem.PAGES.size() + 1); //+1 because the "welcome" page is not included

			font.draw(matrix, intro1, width / 2 - font.width(intro1) / 2, 22, 0);

			for(int i = 0; i < intro2.size(); i++)
			{
				FormattedCharSequence text = intro2.get(i);

				font.draw(matrix, text, width / 2 - font.width(text) / 2, 150 + 10 * i, 0);
			}

			for(int i = 0; i < author.size(); i++)
			{
				FormattedCharSequence text = author.get(i);

				font.draw(matrix, text, width / 2 - font.width(text) / 2, 180 + 10 * i, 0);
			}

			//the patreon link button may overlap with a name tooltip from the list, so draw the list after the buttons
			if(patronList != null)
				patronList.render(matrix, mouseX, mouseY, partialTicks);

			font.draw(matrix, pageNumberText, startX + 240 - font.width(pageNumberText), 182, 0x8E8270);
			font.draw(matrix, ourPatrons, width / 2 - font.width(ourPatrons) / 2 + 30, 40, 0);
		}
	}

	@Override
	public void removed(){
		super.removed();
		lastPage = currentPage;
		minecraft.keyboardHandler.setSendRepeatsToGui(false);
	}

	@Override
	public boolean charTyped(char typedChar, int keyCode){
		if(keyCode == GLFW.GLFW_KEY_LEFT)
			previousSubpage();
		else if(keyCode == GLFW.GLFW_KEY_RIGHT)
			nextSubpage();

		return super.charTyped(typedChar, keyCode);
	}

	protected void actionPerformed(IdButton button){
		if(button.id == 1)
			nextPage();
		else if(button.id == 2)
			previousPage();
		else if(button.id == 3)
			nextSubpage();
		else if(button.id == 4)
			previousSubpage();

		//hide subpage buttons on main page
		buttons.get(2).visible = currentPage != -1 && subpages.size() > 1;
		buttons.get(3).visible = currentPage != -1 && subpages.size() > 1;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scroll)
	{
		super.mouseScrolled(mouseX, mouseY, scroll);

		if(currentPage == -1 && patronList != null && patronList.isMouseOver(mouseX, mouseY) && !patronList.patrons.isEmpty())
		{
			patronList.mouseScrolled(mouseX, mouseY, scroll);
			return true;
		}

		switch((int)Math.signum(scroll))
		{
			case -1: nextPage(); break;
			case 1: previousPage(); break;
		}

		//hide subpage buttons on main page
		buttons.get(2).visible = currentPage != -1 && subpages.size() > 1;
		buttons.get(3).visible = currentPage != -1 && subpages.size() > 1;
		return true;
	}

	private void nextPage()
	{
		currentPage++;

		if(currentPage > SCManualItem.PAGES.size() - 1)
			currentPage = -1;

		updateRecipeAndIcons();
	}

	private void previousPage()
	{
		currentPage--;

		if(currentPage < -1)
			currentPage = SCManualItem.PAGES.size() - 1;

		updateRecipeAndIcons();
	}

	private void nextSubpage()
	{
		currentSubpage++;

		if(currentSubpage == subpages.size())
			currentSubpage = 0;
	}

	private void previousSubpage()
	{
		currentSubpage--;

		if(currentSubpage == -1)
			currentSubpage = subpages.size() - 1;
	}

	private void updateRecipeAndIcons(){
		currentSubpage = 0;
		hoverCheckers.clear();
		patreonLinkButton.visible = currentPage == -1;

		if(currentPage < 0){
			recipe = null;
			buttons.get(2).visible = false;
			buttons.get(3).visible = false;

			if(I18n.exists("gui.securitycraft:scManual.author"))
				author = font.split(Utils.localize("gui.securitycraft:scManual.author"), 180);
			else
				author.clear();

			intro2 = font.split(Utils.localize("gui.securitycraft:scManual.intro.2"), 225);

			patronList.fetchPatrons();
			return;
		}

		SCManualPage page = SCManualItem.PAGES.get(currentPage);

		recipe = null;

		for(Recipe<?> object : Minecraft.getInstance().level.getRecipeManager().getRecipes())
		{
			if(object instanceof ShapedRecipe){
				ShapedRecipe recipe = (ShapedRecipe) object;

				if(!recipe.getResultItem().isEmpty() && recipe.getResultItem().getItem() == page.getItem()){
					NonNullList<Ingredient> ingredients = recipe.getIngredients();
					NonNullList<Ingredient> recipeItems = NonNullList.<Ingredient>withSize(9, Ingredient.EMPTY);

					for(int i = 0; i < ingredients.size(); i++)
					{
						recipeItems.set(getCraftMatrixPosition(i, recipe.getWidth(), recipe.getHeight()), ingredients.get(i));
					}

					this.recipe = recipeItems;
					break;
				}
			}else if(object instanceof ShapelessRecipe){
				ShapelessRecipe recipe = (ShapelessRecipe) object;

				if(!recipe.getResultItem().isEmpty() && recipe.getResultItem().getItem() == page.getItem()){
					//don't show keycard reset recipes
					if(recipe.getId().getPath().endsWith("_reset"))
						continue;

					NonNullList<Ingredient> recipeItems = NonNullList.<Ingredient>withSize(recipe.getIngredients().size(), Ingredient.EMPTY);

					for(int i = 0; i < recipeItems.size(); i++)
						recipeItems.set(i, recipe.getIngredients().get(i));

					this.recipe = recipeItems;
					break;
				}
			}
		}

		TranslatableComponent helpInfo = page.getHelpInfo();
		boolean reinforcedPage = helpInfo.getKey().equals("help.securitycraft:reinforced.info") || helpInfo.getKey().contains("reinforced_hopper");

		if(page.hasRecipeDescription())
		{
			String name = page.getItem().getRegistryName().getPath();

			hoverCheckers.add(new TextHoverChecker(144, 144 + (2 * 20) + 16, startX + 100, (startX + 100) + (2 * 20) + 16, Utils.localize("gui.securitycraft:scManual.recipe." + name)));
		}
		else if(reinforcedPage)
		{
			recipe = null;
			hoverCheckers.add(new TextHoverChecker(144, 144 + (2 * 20) + 16, startX + 100, (startX + 100) + (2 * 20) + 16, Utils.localize("gui.securitycraft:scManual.recipe.reinforced")));
		}
		else if(recipe != null)
		{
			for(int i = 0; i < 3; i++)
			{
				for(int j = 0; j < 3; j++)
				{
					hoverCheckers.add(new HoverChecker(144 + (i * 19), 144 + (i * 19) + 16, (startX + 101) + (j * 19), (startX + 101) + (j * 19) + 16));
				}
			}
		}
		else
			hoverCheckers.add(new TextHoverChecker(144, 144 + (2 * 20) + 16, startX + 100, (startX + 100) + (2 * 20) + 16, Utils.localize("gui.securitycraft:scManual.disabled")));

		Item item = page.getItem();

		if(item instanceof BlockItem){
			Block block = ((BlockItem) item).getBlock();

			if(block instanceof IExplosive)
				hoverCheckers.add(new TextHoverChecker(118, 118 + 16, startX + 107, (startX + 107) + 16, Utils.localize("gui.securitycraft:scManual.explosiveBlock")));

			if(block.hasTileEntity(block.defaultBlockState()))
			{
				BlockEntity te = block.createTileEntity(block.defaultBlockState(), Minecraft.getInstance().level);

				if(te instanceof IOwnable)
					hoverCheckers.add(new TextHoverChecker(118, 118 + 16, startX + 29, (startX + 29) + 16, Utils.localize("gui.securitycraft:scManual.ownableBlock")));

				if(te instanceof IPasswordProtected)
					hoverCheckers.add(new TextHoverChecker(118, 118 + 16, startX + 55, (startX + 55) + 16, Utils.localize("gui.securitycraft:scManual.passwordProtectedBlock")));

				if(te instanceof SecurityCraftTileEntity && ((SecurityCraftTileEntity) te).isActivatedByView())
					hoverCheckers.add(new TextHoverChecker(118, 118 + 16, startX + 81, (startX + 81) + 16, Utils.localize("gui.securitycraft:scManual.viewActivatedBlock")));

				if(te instanceof ICustomizable)
				{
					ICustomizable scte = (ICustomizable)te;

					hoverCheckers.add(new TextHoverChecker(118, 118 + 16, startX + 213, (startX + 213) + 16, Utils.localize("gui.securitycraft:scManual.customizableBlock")));

					if(scte.customOptions() != null && scte.customOptions().length > 0)
					{
						List<Component> display = new ArrayList<>();

						display.add(Utils.localize("gui.securitycraft:scManual.options"));
						display.add(new TextComponent("---"));

						for(Option<?> option : scte.customOptions())
						{
							display.add(new TextComponent("- ").append(Utils.localize("option" + block.getDescriptionId().substring(5) + "." + option.getName() + ".description")));
							display.add(TextComponent.EMPTY);
						}

						display.remove(display.size() - 1);
						hoverCheckers.add(new TextHoverChecker(118, 118 + 16, startX + 136, (startX + 136) + 16, display));
					}
				}

				if(te instanceof IModuleInventory)
				{
					IModuleInventory moduleInv = (IModuleInventory)te;

					if(moduleInv.acceptedModules() != null && moduleInv.acceptedModules().length > 0)
					{
						List<Component> display = new ArrayList<>();

						display.add(Utils.localize("gui.securitycraft:scManual.modules"));
						display.add(new TextComponent("---"));

						for(ModuleType module : moduleInv.acceptedModules())
						{
							display.add(new TextComponent("- ").append(Utils.localize("module" + block.getDescriptionId().substring(5) + "." + module.getItem().getDescriptionId().substring(5).replace("securitycraft.", "") + ".description")));
							display.add(TextComponent.EMPTY);
						}

						display.remove(display.size() - 1);
						hoverCheckers.add(new TextHoverChecker(118, 118 + 16, startX + 163, (startX + 163) + 16, display));
					}
				}
			}
		}

		if(recipe != null && recipe.size() > 0)
		{
			for(int i = 0; i < 3; i++)
			{
				for(int j = 0; j < 3; j++)
				{
					int index = (i * 3) + j;

					if(index >= recipe.size())
						displays[index].setIngredient(Ingredient.EMPTY);
					else
						displays[index].setIngredient(recipe.get(index));
				}
			}
		}
		else
		{
			for(IngredientDisplay display : displays)
			{
				display.setIngredient(Ingredient.EMPTY);
			}
		}

		//set up subpages
		subpages = font.getSplitter().splitLines(helpInfo, subpageLength, Style.EMPTY);
		buttons.get(2).visible = currentPage != -1 && subpages.size() > 1;
		buttons.get(3).visible = currentPage != -1 && subpages.size() > 1;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		if(patronList != null)
			patronList.mouseClicked(mouseX, mouseY, button);

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button)
	{
		if(patronList != null)
			patronList.mouseReleased(mouseX, mouseY, button);

		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
	{
		if(patronList != null)
			patronList.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);

		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	class PatronList extends ScrollPanel
	{
		private final int slotHeight = 12;
		private final ExecutorService executor = Executors.newSingleThreadExecutor();
		private Future<List<String>> patronRequestFuture;
		private List<String> patrons = new ArrayList<>();
		private boolean patronsAvailable = false;
		private boolean error = false;
		private boolean patronsRequested;
		private final int barWidth = 6;
		private final int barLeft;
		private final List<FormattedCharSequence> fetchErrorLines;
		private final List<FormattedCharSequence> noPatronsLines;
		private final Component loadingText = Utils.localize("gui.securitycraft:scManual.patreon.loading");

		public PatronList(Minecraft client, int width, int height, int top, int left)
		{
			super(client, width, height, top, left);

			barLeft = left + width - barWidth;
			fetchErrorLines = font.split(Utils.localize("gui.securitycraft:scManual.patreon.error"), width);
			noPatronsLines = font.split(Utils.localize("advancements.empty"), width - 10);
		}

		@Override
		protected int getContentHeight()
		{
			int height = 50 + (patrons.size() * font.lineHeight);

			if(height < bottom - top - 8)
				height = bottom - top - 8;

			return height;
		}

		@Override
		public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks)
		{
			if(patronsAvailable) //code from ScrollPanel to be able to change colors
			{
				Tesselator tess = Tesselator.getInstance();
				BufferBuilder buffer = tess.getBuilder();
				Minecraft client = Minecraft.getInstance();
				double scale = client.getWindow().getGuiScale();
				int baseY = top + border - (int)scrollDistance;
				int extraHeight = getContentHeight() + border - height;

				GL11.glEnable(GL11.GL_SCISSOR_TEST);
				GL11.glScissor((int)(left  * scale), (int)(client.getWindow().getHeight() - (bottom * scale)),
						(int)(width * scale), (int)(height * scale));
				drawGradientRect(matrix, left, top, right, bottom, 0xC0BFBBB2, 0xD0BFBBB2); //list background
				drawPanel(matrix, right, baseY, tess, mouseX, mouseY);
				RenderSystem.disableDepthTest();

				if(extraHeight > 0)
				{
					int barHeight = getBarHeight();
					int barTop = (int)scrollDistance * (height - barHeight) / extraHeight + top;

					if(barTop < top)
						barTop = top;

					//scrollbar background
					buffer.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION_COLOR);
					buffer.vertex(barLeft,            bottom, 0.0D).color(0x8E, 0x82, 0x70, 0xFF).endVertex();
					buffer.vertex(barLeft + barWidth, bottom, 0.0D).color(0x8E, 0x82, 0x70, 0xFF).endVertex();
					buffer.vertex(barLeft + barWidth, top,    0.0D).color(0x8E, 0x82, 0x70, 0xFF).endVertex();
					buffer.vertex(barLeft,            top,    0.0D).color(0x8E, 0x82, 0x70, 0xFF).endVertex();
					tess.end();
					//scrollbar border
					buffer.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION_COLOR);
					buffer.vertex(barLeft,            barTop + barHeight, 0.0D).color(0x80, 0x70, 0x55, 0xFF).endVertex();
					buffer.vertex(barLeft + barWidth, barTop + barHeight, 0.0D).color(0x80, 0x70, 0x55, 0xFF).endVertex();
					buffer.vertex(barLeft + barWidth, barTop,             0.0D).color(0x80, 0x70, 0x55, 0xFF).endVertex();
					buffer.vertex(barLeft,            barTop,             0.0D).color(0x80, 0x70, 0x55, 0xFF).endVertex();
					tess.end();
					//scrollbar
					buffer.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION_COLOR);
					buffer.vertex(barLeft,                barTop + barHeight - 1, 0.0D).color(0xD1, 0xBF, 0xA1, 0xFF).endVertex();
					buffer.vertex(barLeft + barWidth - 1, barTop + barHeight - 1, 0.0D).color(0xD1, 0xBF, 0xA1, 0xFF).endVertex();
					buffer.vertex(barLeft + barWidth - 1, barTop,                 0.0D).color(0xD1, 0xBF, 0xA1, 0xFF).endVertex();
					buffer.vertex(barLeft,                barTop,                 0.0D).color(0xD1, 0xBF, 0xA1, 0xFF).endVertex();
					tess.end();
				}

				RenderSystem.enableTexture();
				RenderSystem.shadeModel(GL11.GL_FLAT);
				RenderSystem.enableAlphaTest();
				RenderSystem.disableBlend();
				GL11.glDisable(GL11.GL_SCISSOR_TEST);

				//draw tooltip for long patron names
				int mouseListY = (int)(mouseY - top + scrollDistance - border);
				int slotIndex = mouseListY / slotHeight;

				if(mouseX >= left && mouseX < right - 6 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < patrons.size() && mouseY >= top && mouseY <= bottom)
				{
					String patron = patrons.get(slotIndex);
					int length = font.width(patron);

					if(length >= width - barWidth)
						renderTooltip(matrix, new TextComponent(patron), left - 10, baseY + (slotHeight * slotIndex + slotHeight));
				}

				if (patrons.isEmpty()) {
					for(int i = 0; i < noPatronsLines.size(); i++) {
						FormattedCharSequence line = noPatronsLines.get(i);

						font.draw(matrix, line, left + width / 2 - font.width(line) / 2, top + 30 + i * 10, 0xFF333333);
					}
				}
			}
			else if(error)
			{
				for(int i = 0; i < fetchErrorLines.size(); i++)
				{
					FormattedCharSequence line = fetchErrorLines.get(i);

					font.draw(matrix, line, left + width / 2 - font.width(line) / 2, top + 30 + i * 10, 0xFFB00101);
				}
			}
			else if(patronRequestFuture != null && patronRequestFuture.isDone())
			{
				try
				{
					patrons = patronRequestFuture.get();
					executor.shutdown();
					patronsAvailable = true;
				}
				catch(InterruptedException | ExecutionException e)
				{
					error = true;
				}
			}
			else
				font.draw(matrix, loadingText, left + width / 2 - font.width(loadingText) / 2, top + 30, 0);
		}

		@Override
		protected void drawPanel(PoseStack matrix, int entryRight, int relativeY, Tesselator tesselator, int mouseX, int mouseY)
		{
			//draw entry strings
			for(int i = 0; i < patrons.size(); i++)
			{
				String patron = patrons.get(i);

				if(patron != null && !patron.isEmpty())
					font.draw(matrix, patron, left + 2, relativeY + (slotHeight * i), 0);
			}
		}

		public void fetchPatrons()
		{
			if(!patronsRequested)
			{
				//create thread to fetch patrons. without this, and for example if the player has no internet connection, the game will hang
				patronRequestFuture = executor.submit(() -> {
					try(BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("https://gist.githubusercontent.com/bl4ckscor3/bdda6596012b1206816db034350b5717/raw").openStream())))
					{
						return reader.lines().collect(Collectors.toList());
					}
					catch(IOException e)
					{
						error = true;
						return new ArrayList<>();
					}
				});
				patronsRequested = true;
			}
		}

		public int getBarHeight()
		{
			int barHeight = (height * height) / getContentHeight();

			if(barHeight < 32)
				barHeight = 32;

			if(barHeight > height - border * 2)
				barHeight = height - border * 2;

			return barHeight;
		}
	}

	static class ChangePageButton extends IdButton {
		private final int textureY;

		public ChangePageButton(int index, int xPos, int yPos, boolean forward, Consumer<IdButton> onClick){
			super(index, xPos, yPos, 23, 13, "", onClick);
			textureY = forward ? 192 : 205;
		}

		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks){
			if(visible){
				boolean isHovering = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;

				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				Minecraft.getInstance().getTextureManager().bind(bookGuiTextures);
				blit(matrix, x, y, isHovering ? 23 : 0, textureY, 23, 13);
			}
		}
	}

	class HyperlinkButton extends Button
	{
		public HyperlinkButton(int xPos, int yPos, int width, int height, Component displayString, OnPress handler)
		{
			super(xPos, yPos, width, height, displayString, handler);
		}

		@Override
		public void renderButton(PoseStack matrix, int mouseX, int mouseY, float partial)
		{
			minecraft.getTextureManager().bind(infoBookIcons);
			isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;

			if(isHovered)
				blit(matrix, x, y, 138, 1, 16, 16);
			else
				blit(matrix, x, y, 122, 1, 16, 16);
		}
	}

	//from JEI
	private int getCraftMatrixPosition(int i, int width, int height)
	{
		int index;

		if(width == 1)
		{
			if(height == 3)
				index = (i * 3) + 1;
			else if(height == 2)
				index = (i * 3) + 1;
			else
				index = 4;

		}
		else if(height == 1)
			index = i + 3;
		else if(width == 2)
		{
			index = i;

			if(i > 1)
			{
				index++;

				if(i > 3)
					index++;
			}
		}
		else if(height == 2)
			index = i + 3;
		else
			index = i;

		return index;
	}
}
