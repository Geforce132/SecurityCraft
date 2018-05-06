package net.geforcemods.securitycraft.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.TileEntitySCTE;
import net.geforcemods.securitycraft.gui.components.CustomHoverChecker;
import net.geforcemods.securitycraft.util.GuiUtils;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class GuiSCManual extends GuiScreen {

	private ResourceLocation infoBookTexture = new ResourceLocation("securitycraft:textures/gui/infoBookTexture.png");
	private ResourceLocation infoBookTextureSpecial = new ResourceLocation("securitycraft:textures/gui/infoBookTextureSpecial.png"); //for items without a recipe
	private ResourceLocation infoBookTitlePage = new ResourceLocation("securitycraft:textures/gui/infoBookTitlePage.png");
	private ResourceLocation infoBookIcons = new ResourceLocation("securitycraft:textures/gui/infoBookIcons.png");
	private static ResourceLocation bookGuiTextures = new ResourceLocation("textures/gui/book.png");

	private List<CustomHoverChecker> hoverCheckers = new ArrayList<CustomHoverChecker>();
	private int currentPage = -1;
	private ItemStack[] recipe;
	int startY = -1;
	boolean update = false;

	public GuiSCManual() {
		super();
	}

	@Override
	public void initGui(){
		byte b0 = 2;

		if((width - 256) / 2 != startY && startY != -1)
			update = true;

		startY = (width - 256) / 2;
		Keyboard.enableRepeatEvents(true);
		GuiSCManual.NextPageButton nextButton = new GuiSCManual.NextPageButton(1, startY + 210, b0 + 158, true);
		GuiSCManual.NextPageButton prevButton = new GuiSCManual.NextPageButton(2, startY + 16, b0 + 158, false);

		buttonList.add(nextButton);
		buttonList.add(prevButton);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		if(update)
		{
			updateRecipeAndIcons();
			update = false;
		}

		if(currentPage == -1)
			mc.getTextureManager().bindTexture(infoBookTitlePage);
		else if(recipe != null)
			mc.getTextureManager().bindTexture(infoBookTexture);
		else
			mc.getTextureManager().bindTexture(infoBookTextureSpecial);

		drawTexturedModalRect(startY, 5, 0, 0, 256, 250);

		if(currentPage > -1){
			if(SecurityCraft.instance.manualPages.get(currentPage).getHelpInfo().equals("help.reinforced.info"))
				fontRendererObj.drawString(StatCollector.translateToLocal("gui.scManual.reinforced"), startY + 39, 27, 0, false);
			else
				fontRendererObj.drawString(StatCollector.translateToLocal(SecurityCraft.instance.manualPages.get(currentPage).getItem().getUnlocalizedName() + ".name"), startY + 39, 27, 0, false);

			fontRendererObj.drawSplitString(StatCollector.translateToLocal(SecurityCraft.instance.manualPages.get(currentPage).getHelpInfo()), startY + 18, 45, 225, 0);
		}else{
			fontRendererObj.drawString(StatCollector.translateToLocal("gui.scManual.intro.1"), startY + 39, 27, 0, false);
			fontRendererObj.drawString(StatCollector.translateToLocal("gui.scManual.intro.2"), startY + 60, 159, 0, false);

			if(StatCollector.canTranslate("gui.scManual.author"))
				fontRendererObj.drawString(StatCollector.translateToLocal("gui.scManual.author"), startY + 65, 170, 0, false);
		}

		for(int i = 0; i < buttonList.size(); i++)
			((GuiButton) buttonList.get(i)).drawButton(mc, mouseX, mouseY);

		if(currentPage > -1){
			Item item = SecurityCraft.instance.manualPages.get(currentPage).getItem();
			GuiUtils.drawItemStackToGui(mc, item, startY + 19, 22, !(SecurityCraft.instance.manualPages.get(currentPage).getItem() instanceof ItemBlock));

			mc.getTextureManager().bindTexture(infoBookIcons);

			TileEntity te = ((item instanceof ItemBlock && ((ItemBlock) item).blockInstance instanceof ITileEntityProvider) ? ((ITileEntityProvider) ((ItemBlock) item).blockInstance).createNewTileEntity(Minecraft.getMinecraft().theWorld, 0) : null);
			Block itemBlock = ((item instanceof ItemBlock) ? ((ItemBlock) item).blockInstance : null);

			if(itemBlock != null){
				if(itemBlock instanceof IExplosive)
					drawTexturedModalRect(startY + 107, 117, 54, 1, 18, 18);

				if(te != null){
					if(te instanceof IOwnable)
						drawTexturedModalRect(startY + 29, 118, 1, 1, 16, 16);

					if(te instanceof IPasswordProtected)
						drawTexturedModalRect(startY + 55, 118, 18, 1, 17, 16);

					if(te instanceof TileEntitySCTE && ((TileEntitySCTE) te).isActivatedByView())
						drawTexturedModalRect(startY + 81, 118, 36, 1, 17, 16);

					if(te instanceof CustomizableSCTE)
						drawTexturedModalRect(startY + 213, 118, 72, 1, 16, 16);
				}
			}

			if(recipe != null)
			{
				for(int i = 0; i < 3; i++)
				{
					for(int j = 0; j < 3; j++){
						if(((i * 3) + j) >= recipe.length)
							break;
						if(recipe[(i * 3) + j] == null)
							continue;

						if(recipe[(i * 3) + j].getItem() instanceof ItemBlock)
							GuiUtils.drawItemStackToGui(mc, Block.getBlockFromItem(recipe[(i * 3) + j].getItem()), (startY + 100) + (j * 20), 144 + (i * 20), !(recipe[(i * 3) + j].getItem() instanceof ItemBlock));
						else
							GuiUtils.drawItemStackToGui(mc, recipe[(i * 3) + j].getItem(), recipe[(i * 3) + j].getMetadata(), (startY + 100) + (j * 20), 144 + (i * 20), !(recipe[(i * 3) + j].getItem() instanceof ItemBlock));
					}
				}
			}

			for(CustomHoverChecker chc : hoverCheckers)
			{
				if(chc != null && chc.checkHover(mouseX, mouseY))
				{
					if(chc.getName() != null)
						drawHoveringText(mc.fontRendererObj.listFormattedStringToWidth(chc.getName(), 220), mouseX, mouseY, mc.fontRendererObj);
				}
			}
		}
	}

	@Override
	public void onGuiClosed(){
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode){
		super.keyTyped(typedChar, keyCode);

		if(keyCode == Keyboard.KEY_LEFT){
			currentPage--;

			if(currentPage < -1)
				currentPage = SecurityCraft.instance.manualPages.size() - 1;

			Minecraft.getMinecraft().thePlayer.playSound("random.click", 0.15F, 1.0F);
			updateRecipeAndIcons();
		}else if(keyCode == Keyboard.KEY_RIGHT){
			currentPage++;

			if(currentPage > SecurityCraft.instance.manualPages.size() - 1)
				currentPage = -1;

			Minecraft.getMinecraft().thePlayer.playSound("random.click", 0.15F, 1.0F);
			updateRecipeAndIcons();
		}
	}

	@Override
	protected void actionPerformed(GuiButton button){
		if(button.id == 1){
			currentPage++;

			if(currentPage > SecurityCraft.instance.manualPages.size() - 1)
				currentPage = -1;

			updateRecipeAndIcons();
		}else if(button.id == 2){
			currentPage--;

			if(currentPage < -1)
				currentPage = SecurityCraft.instance.manualPages.size() - 1;

			updateRecipeAndIcons();
		}
	}

	private void updateRecipeAndIcons(){
		if(currentPage < 0){
			recipe = null;
			hoverCheckers.clear();
			return;
		}

		hoverCheckers.clear();

		if(SecurityCraft.instance.manualPages.get(currentPage).hasCustomRecipe())
			recipe = SecurityCraft.instance.manualPages.get(currentPage).getRecipe();
		else
			for(Object object : CraftingManager.getInstance().getRecipeList()){
				if(object instanceof ShapedRecipes){
					ShapedRecipes recipe = (ShapedRecipes) object;

					if(recipe.getRecipeOutput() != null && recipe.getRecipeOutput().getItem() == SecurityCraft.instance.manualPages.get(currentPage).getItem()){
						this.recipe = recipe.recipeItems;
						break;
					}
				}else if(object instanceof ShapelessRecipes){
					ShapelessRecipes recipe = (ShapelessRecipes) object;

					if(recipe.getRecipeOutput() != null && recipe.getRecipeOutput().getItem() == SecurityCraft.instance.manualPages.get(currentPage).getItem()){
						this.recipe = toItemStackArray(recipe.recipeItems);
						break;
					}
				}

				recipe = null;
			}

		if(recipe != null)
		{
			outer:
				for(int i = 0; i < 3; i++)
				{
					for(int j = 0; j < 3; j++)
					{
						if((i * 3) + j == recipe.length)
							break outer;

						if(recipe[(i * 3) + j] != null)
							hoverCheckers.add(new CustomHoverChecker(144 + (i * 20), 144 + (i * 20) + 16, (startY + 100) + (j * 20), (startY + 100) + (j * 20) + 16, 20, recipe[(i * 3) + j].getDisplayName()));
					}
				}
		}
		else if(SecurityCraft.instance.manualPages.get(currentPage).getHelpInfo().equals("help.reinforced.info"))
			hoverCheckers.add(new CustomHoverChecker(144, 144 + (2 * 20) + 16, startY + 100, (startY + 100) + (2 * 20) + 16, 20, StatCollector.translateToLocal("gui.scManual.recipe.reinforced")));
		else
		{
			String name = SecurityCraft.instance.manualPages.get(currentPage).getItem().getUnlocalizedName().substring(5);

			hoverCheckers.add(new CustomHoverChecker(144, 144 + (2 * 20) + 16, startY + 100, (startY + 100) + (2 * 20) + 16, 20, StatCollector.translateToLocal("gui.scManual.recipe." + name)));
		}

		Item item = SecurityCraft.instance.manualPages.get(currentPage).getItem();
		TileEntity te = ((item instanceof ItemBlock && ((ItemBlock) item).blockInstance instanceof ITileEntityProvider) ? ((ITileEntityProvider) ((ItemBlock) item).blockInstance).createNewTileEntity(Minecraft.getMinecraft().theWorld, 0) : null);
		Block itemBlock = ((item instanceof ItemBlock) ? ((ItemBlock) item).blockInstance : null);

		if(te != null){
			if(te instanceof IOwnable)
				hoverCheckers.add(new CustomHoverChecker(118, 118 + 16, startY + 29, (startY + 29) + 16, 20, StatCollector.translateToLocal("gui.scManual.ownableBlock")));

			if(te instanceof IPasswordProtected)
				hoverCheckers.add(new CustomHoverChecker(118, 118 + 16, startY + 55, (startY + 55) + 16, 20, StatCollector.translateToLocal("gui.scManual.passwordProtectedBlock")));

			if(te instanceof TileEntitySCTE && ((TileEntitySCTE) te).isActivatedByView())
				hoverCheckers.add(new CustomHoverChecker(118, 118 + 16, startY + 81, (startY + 81) + 16, 20, StatCollector.translateToLocal("gui.scManual.viewActivatedBlock")));

			if(itemBlock instanceof IExplosive)
				hoverCheckers.add(new CustomHoverChecker(118, 118 + 16, startY + 107, (startY + 107) + 16, 20, StatCollector.translateToLocal("gui.scManual.explosiveBlock")));

			if(te instanceof CustomizableSCTE)
				hoverCheckers.add(new CustomHoverChecker(118, 118 + 16, startY + 213, (startY + 213) + 16, 20, StatCollector.translateToLocal("gui.scManual.customizableBlock")));
		}
	}

	private ItemStack[] toItemStackArray(List<?> items){
		ItemStack[] array = new ItemStack[9];

		for(int i = 0; i < items.size(); i++)
			array[i] = (ItemStack) items.get(i);

		return array;
	}

	@SideOnly(Side.CLIENT)
	static class NextPageButton extends GuiButton {
		private final boolean isForward;

		public NextPageButton(int id, int x, int y, boolean forward){
			super(id, x, y, 23, 13, "");
			isForward = forward;
		}

		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY){
			if(visible){
				boolean mouseInRange = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(bookGuiTextures);
				int startX = 0;
				int startY = 192;

				if(mouseInRange)
					startX += 23;

				if(!isForward)
					startY += 13;

				drawTexturedModalRect(xPosition, yPosition, startX, startY, 23, 13);
			}
		}
	}

}
