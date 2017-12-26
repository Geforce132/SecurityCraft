package net.geforcemods.securitycraft.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.TileEntitySCTE;
import net.geforcemods.securitycraft.gui.components.CustomHoverChecker;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
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
	int k = -1;
	boolean update = false;

	public GuiSCManual() {
		super();
	}

	@Override
	public void initGui(){
		byte b0 = 2;

		if((width - 256) / 2 != k && k != -1)
			update = true;

		k = (width - 256) / 2;
		Keyboard.enableRepeatEvents(true);
		GuiSCManual.NextPageButton nextButton = new GuiSCManual.NextPageButton(1, k + 210, b0 + 158, true);
		GuiSCManual.NextPageButton prevButton = new GuiSCManual.NextPageButton(2, k + 16, b0 + 158, false);

		buttonList.add(nextButton);
		buttonList.add(prevButton);
	}

	@Override
	public void drawScreen(int par1, int par2, float par3){
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

		drawTexturedModalRect(k, 5, 0, 0, 256, 250);

		if(currentPage > -1){
			if(mod_SecurityCraft.instance.manualPages.get(currentPage).getHelpInfo().equals("help.reinforced.info"))
				fontRendererObj.drawString(StatCollector.translateToLocal("gui.scManual.reinforced"), k + 39, 27, 0, false);
			else
				fontRendererObj.drawString(StatCollector.translateToLocal(mod_SecurityCraft.instance.manualPages.get(currentPage).getItem().getUnlocalizedName() + ".name"), k + 39, 27, 0, false);

			fontRendererObj.drawSplitString(StatCollector.translateToLocal(mod_SecurityCraft.instance.manualPages.get(currentPage).getHelpInfo()), k + 18, 45, 225, 0);
		}else{
			fontRendererObj.drawString(StatCollector.translateToLocal("gui.scManual.intro.1"), k + 39, 27, 0, false);
			fontRendererObj.drawString(StatCollector.translateToLocal("gui.scManual.intro.2"), k + 60, 159, 0, false);

			if(StatCollector.canTranslate("gui.scManual.author"))
				fontRendererObj.drawString(StatCollector.translateToLocal("gui.scManual.author"), k + 65, 170, 0, false);
		}

		for(int i = 0; i < buttonList.size(); i++)
			((GuiButton) buttonList.get(i)).drawButton(mc, par1, par2);

		if(currentPage > -1){
			Item item = mod_SecurityCraft.instance.manualPages.get(currentPage).getItem();
			GuiUtils.drawItemStackToGui(mc, item, k + 19, 22, !(mod_SecurityCraft.instance.manualPages.get(currentPage).getItem() instanceof ItemBlock));

			mc.getTextureManager().bindTexture(infoBookIcons);

			TileEntity te = ((item instanceof ItemBlock && ((ItemBlock) item).field_150939_a instanceof ITileEntityProvider) ? ((ITileEntityProvider) ((ItemBlock) item).field_150939_a).createNewTileEntity(Minecraft.getMinecraft().theWorld, 0) : null);
			Block itemBlock = ((item instanceof ItemBlock) ? ((ItemBlock) item).field_150939_a : null);

			if(itemBlock != null){
				if(itemBlock instanceof IExplosive)
					drawTexturedModalRect(k + 107, 117, 54, 1, 18, 18);

				if(te != null){
					if(te instanceof IOwnable)
						drawTexturedModalRect(k + 29, 118, 1, 1, 16, 16);

					if(te instanceof IPasswordProtected)
						drawTexturedModalRect(k + 55, 118, 18, 1, 17, 16);

					if(te instanceof TileEntitySCTE && ((TileEntitySCTE) te).isActivatedByView())
						drawTexturedModalRect(k + 81, 118, 36, 1, 17, 16);

					if(te instanceof CustomizableSCTE)
						drawTexturedModalRect(k + 213, 118, 72, 1, 16, 16);
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
							GuiUtils.drawItemStackToGui(mc, Block.getBlockFromItem(recipe[(i * 3) + j].getItem()), (k + 100) + (j * 20), 144 + (i * 20), !(recipe[(i * 3) + j].getItem() instanceof ItemBlock));
						else
							GuiUtils.drawItemStackToGui(mc, recipe[(i * 3) + j].getItem(), recipe[(i * 3) + j].getItemDamage(), (k + 100) + (j * 20), 144 + (i * 20), !(recipe[(i * 3) + j].getItem() instanceof ItemBlock));
					}
				}
			}

			for(CustomHoverChecker chc : hoverCheckers)
			{
				if(chc != null && chc.checkHover(par1, par2))
				{
					if(chc.getName() != null)
						drawHoveringText(mc.fontRenderer.listFormattedStringToWidth(chc.getName(), 220), par1, par2, mc.fontRenderer);
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
	protected void keyTyped(char par1, int par2){
		super.keyTyped(par1, par2);

		if(par2 == Keyboard.KEY_LEFT){
			currentPage--;

			if(currentPage < -1)
				currentPage = mod_SecurityCraft.instance.manualPages.size() - 1;

			Minecraft.getMinecraft().thePlayer.playSound("random.click", 0.15F, 1.0F);
			updateRecipeAndIcons();
		}else if(par2 == Keyboard.KEY_RIGHT){
			currentPage++;

			if(currentPage > mod_SecurityCraft.instance.manualPages.size() - 1)
				currentPage = -1;

			Minecraft.getMinecraft().thePlayer.playSound("random.click", 0.15F, 1.0F);
			updateRecipeAndIcons();
		}
	}

	@Override
	protected void actionPerformed(GuiButton par1GuiButton){
		if(par1GuiButton.id == 1){
			currentPage++;

			if(currentPage > mod_SecurityCraft.instance.manualPages.size() - 1)
				currentPage = -1;

			updateRecipeAndIcons();
		}else if(par1GuiButton.id == 2){
			currentPage--;

			if(currentPage < -1)
				currentPage = mod_SecurityCraft.instance.manualPages.size() - 1;

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

		if(mod_SecurityCraft.instance.manualPages.get(currentPage).hasCustomRecipe())
			recipe = mod_SecurityCraft.instance.manualPages.get(currentPage).getRecipe();
		else
			for(Object object : CraftingManager.getInstance().getRecipeList()){
				if(object instanceof ShapedRecipes){
					ShapedRecipes recipe = (ShapedRecipes) object;

					if(recipe.getRecipeOutput() != null && recipe.getRecipeOutput().getItem() == mod_SecurityCraft.instance.manualPages.get(currentPage).getItem()){
						this.recipe = recipe.recipeItems;
						break;
					}
				}else if(object instanceof ShapelessRecipes){
					ShapelessRecipes recipe = (ShapelessRecipes) object;

					if(recipe.getRecipeOutput() != null && recipe.getRecipeOutput().getItem() == mod_SecurityCraft.instance.manualPages.get(currentPage).getItem()){
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
							hoverCheckers.add(new CustomHoverChecker(144 + (i * 20), 144 + (i * 20) + 16, (k + 100) + (j * 20), (k + 100) + (j * 20) + 16, 20, recipe[(i * 3) + j].getDisplayName()));
					}
				}
		}
		else if(mod_SecurityCraft.instance.manualPages.get(currentPage).getHelpInfo().equals("help.reinforced.info"))
			hoverCheckers.add(new CustomHoverChecker(144, 144 + (2 * 20) + 16, k + 100, (k + 100) + (2 * 20) + 16, 20, StatCollector.translateToLocal("gui.scManual.recipe.reinforced")));
		else
		{
			String name = mod_SecurityCraft.instance.manualPages.get(currentPage).getItem().getUnlocalizedName().substring(5);

			hoverCheckers.add(new CustomHoverChecker(144, 144 + (2 * 20) + 16, k + 100, (k + 100) + (2 * 20) + 16, 20, StatCollector.translateToLocal("gui.scManual.recipe." + name)));
		}

		Item item = mod_SecurityCraft.instance.manualPages.get(currentPage).getItem();
		TileEntity te = ((item instanceof ItemBlock && ((ItemBlock) item).field_150939_a instanceof ITileEntityProvider) ? ((ITileEntityProvider) ((ItemBlock) item).field_150939_a).createNewTileEntity(Minecraft.getMinecraft().theWorld, 0) : null);
		Block itemBlock = ((item instanceof ItemBlock) ? ((ItemBlock) item).field_150939_a : null);

		if(te != null){
			if(te instanceof IOwnable)
				hoverCheckers.add(new CustomHoverChecker(118, 118 + 16, k + 29, (k + 29) + 16, 20, StatCollector.translateToLocal("gui.scManual.ownableBlock")));

			if(te instanceof IPasswordProtected)
				hoverCheckers.add(new CustomHoverChecker(118, 118 + 16, k + 55, (k + 55) + 16, 20, StatCollector.translateToLocal("gui.scManual.passwordProtectedBlock")));

			if(te instanceof TileEntitySCTE && ((TileEntitySCTE) te).isActivatedByView())
				hoverCheckers.add(new CustomHoverChecker(118, 118 + 16, k + 81, (k + 81) + 16, 20, StatCollector.translateToLocal("gui.scManual.viewActivatedBlock")));

			if(itemBlock instanceof IExplosive)
				hoverCheckers.add(new CustomHoverChecker(118, 118 + 16, k + 107, (k + 107) + 16, 20, StatCollector.translateToLocal("gui.scManual.explosiveBlock")));

			if(te instanceof CustomizableSCTE)
				hoverCheckers.add(new CustomHoverChecker(118, 118 + 16, k + 213, (k + 213) + 16, 20, StatCollector.translateToLocal("gui.scManual.customizableBlock")));
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
		private final boolean field_146151_o;

		public NextPageButton(int par1, int par2, int par3, boolean par4){
			super(par1, par2, par3, 23, 13, "");
			field_146151_o = par4;
		}

		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void drawButton(Minecraft p_146112_1_, int p_146112_2_, int p_146112_3_){
			if(visible){
				boolean flag = p_146112_2_ >= xPosition && p_146112_3_ >= yPosition && p_146112_2_ < xPosition + width && p_146112_3_ < yPosition + height;
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				p_146112_1_.getTextureManager().bindTexture(bookGuiTextures);
				int k = 0;
				int l = 192;

				if(flag)
					k += 23;

				if(!field_146151_o)
					l += 13;

				drawTexturedModalRect(xPosition, yPosition, k, l, 23, 13);
			}
		}
	}

}
