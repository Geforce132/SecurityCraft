package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.network.server.SetExplosiveState;
import net.geforcemods.securitycraft.network.server.UpdateNBTTagOnServer;
import net.geforcemods.securitycraft.screen.components.ClickButton;
import net.geforcemods.securitycraft.screen.components.PictureButton;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MineRemoteAccessToolScreen extends Screen{

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/mrat.png");
	private static final ResourceLocation INFO_BOOK_ICONS = new ResourceLocation("securitycraft:textures/gui/info_book_icons.png"); //for the explosion icon
	private ItemStack mrat;
	private ClickButton[][] guiButtons = new ClickButton[6][4]; //6 mines, 4 actions (defuse, prime, detonate, unbind)
	private static final int DEFUSE = 0, ACTIVATE = 1, DETONATE = 2, UNBIND = 3;
	private int xSize = 256, ySize = 184;

	public MineRemoteAccessToolScreen(ItemStack item) {
		super(new TranslationTextComponent(item.getTranslationKey()));

		mrat = item;
	}

	@Override
	public void init(){
		super.init();

		int padding = 25;
		int y = padding;
		int[] coords = null;
		int id = 0;

		for(int i = 0; i < 6; i++)
		{
			y += 30;
			coords = getMineCoordinates(i);

			BlockPos minePos = new BlockPos(coords[0], coords[1], coords[2]);
			Block block = minecraft.world.getBlockState(minePos).getBlock();
			boolean active = block instanceof IExplosive && ((IExplosive) block).isActive(minecraft.world, minePos);
			boolean defusable = (block instanceof IExplosive && ((IExplosive) block).isDefusable());
			boolean bound = !(coords[0] == 0 && coords[1] == 0 && coords[2] == 0);
			int startX = (width - xSize) / 2;
			int startY = (height - ySize) / 2;

			for(int j = 0; j < 4; j++)
			{
				int btnX = startX + j * padding + 154;
				int btnY = startY + y - 48;

				switch(j)
				{
					case DEFUSE:
						guiButtons[i][j] = new PictureButton(id++, btnX, btnY, 20, 20, itemRenderer, new ItemStack(SCContent.wireCutters), this::actionPerformed);
						guiButtons[i][j].active = active && bound && defusable;
						break;
					case ACTIVATE:
						guiButtons[i][j] = new PictureButton(id++, btnX, btnY, 20, 20, itemRenderer, new ItemStack(Items.FLINT_AND_STEEL), this::actionPerformed);
						guiButtons[i][j].active = !active && bound && defusable;
						break;
					case DETONATE:
						guiButtons[i][j] = new PictureButton(id++, btnX, btnY, 20, 20, INFO_BOOK_ICONS, 54, 1, 18, 18, this::actionPerformed);
						guiButtons[i][j].active = active && bound;
						break;
					case UNBIND:
						guiButtons[i][j] = new ClickButton(id++, btnX, btnY, 20, 20, "X", this::actionPerformed);
						guiButtons[i][j].active = bound;
						break;
				}

				addButton(guiButtons[i][j]);

				if(!(block instanceof IExplosive))
				{
					removeTagFromToolAndUpdate(mrat, coords[0], coords[1], coords[2], minecraft.player);
					guiButtons[i][j].active = false;
				}
			}
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks)
	{
		renderBackground();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		this.blit(startX, startY, 0, 0, xSize, ySize);
		super.render(mouseX, mouseY, partialTicks);
		font.drawString(ClientUtils.localize(SCContent.remoteAccessMine.getTranslationKey()), startX + xSize / 2 - font.getStringWidth(ClientUtils.localize(SCContent.remoteAccessMine.getTranslationKey())), startY + -25 + 13, 0xFF0000);

		for(int i = 0; i < 6; i++)
		{
			int[] coords = getMineCoordinates(i);
			String line;

			if(coords[0] == 0 && coords[1] == 0 && coords[2] == 0)
				line = ClientUtils.localize("gui.securitycraft:mrat.notBound");
			else
				line = ClientUtils.localize("gui.securitycraft:mrat.mineLocations").replace("#location", Utils.getFormattedCoordinates(new BlockPos(coords[0], coords[1], coords[2])));

			font.drawString(line, startX + xSize / 2 - font.getStringWidth(line) + 25, startY + i * 30 + 13, 4210752);
		}
	}

	protected void actionPerformed(ClickButton button){
		int mine = button.id / 4;
		int action = button.id % 4;

		int[] coords = getMineCoordinates(mine);

		switch(action)
		{
			case DEFUSE:
				((IExplosive)Minecraft.getInstance().player.world.getBlockState(new BlockPos(coords[0], coords[1], coords[2])).getBlock()).defuseMine(Minecraft.getInstance().player.world, new BlockPos(coords[0], coords[1], coords[2]));
				SecurityCraft.channel.sendToServer(new SetExplosiveState(coords[0], coords[1], coords[2], "defuse"));
				guiButtons[mine][DEFUSE].active = false;
				guiButtons[mine][ACTIVATE].active = true;
				guiButtons[mine][DETONATE].active = false;
				break;
			case ACTIVATE:
				((IExplosive)Minecraft.getInstance().player.world.getBlockState(new BlockPos(coords[0], coords[1], coords[2])).getBlock()).activateMine(Minecraft.getInstance().player.world, new BlockPos(coords[0], coords[1], coords[2]));
				SecurityCraft.channel.sendToServer(new SetExplosiveState(coords[0], coords[1], coords[2], "activate"));
				guiButtons[mine][DEFUSE].active = true;
				guiButtons[mine][ACTIVATE].active = false;
				guiButtons[mine][DETONATE].active = true;
				break;
			case DETONATE:
				SecurityCraft.channel.sendToServer(new SetExplosiveState(coords[0], coords[1], coords[2], "detonate"));
				removeTagFromToolAndUpdate(mrat, coords[0], coords[1], coords[2], Minecraft.getInstance().player);

				for(int i = 0; i < 4; i++)
				{
					guiButtons[mine][i].active = false;
				}

				break;
			case UNBIND:
				removeTagFromToolAndUpdate(mrat, coords[0], coords[1], coords[2], Minecraft.getInstance().player);

				for(int i = 0; i < 4; i++)
				{
					guiButtons[mine][i].active = false;
				}
		}
	}

	/**
	 * @param mine 0 based
	 */
	private int[] getMineCoordinates(int mine)
	{
		mine++; //mines are stored starting by mine1 up to mine6

		if(mrat.getItem() != null && mrat.getItem() == SCContent.remoteAccessMine && mrat.getTag() != null &&  mrat.getTag().getIntArray("mine" + mine) != null && mrat.getTag().getIntArray("mine" + mine).length > 0)
			return mrat.getTag().getIntArray("mine" + mine);
		else
			return new int[] {0,0,0};
	}

	private void removeTagFromToolAndUpdate(ItemStack stack, int x, int y, int z, PlayerEntity player)
	{
		if(stack.getTag() == null)
			return;

		for(int i = 1; i <= 6; i++)
		{
			if(stack.getTag().getIntArray("mine" + i).length > 0)
			{
				int[] coords = stack.getTag().getIntArray("mine" + i);

				if(coords[0] == x && coords[1] == y && coords[2] == z && !(coords[0] == 0 && coords[1] == 0 && coords[2] == 0))
				{
					stack.getTag().putIntArray("mine" + i, new int[]{0, 0, 0});
					SecurityCraft.channel.sendToServer(new UpdateNBTTagOnServer(stack));
					return;
				}
			}
		}
	}

	@Override
	public boolean isPauseScreen()
	{
		return false;
	}
}
