package net.geforcemods.securitycraft.screen;

import java.util.ArrayList;

import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.SecurityCraftTileEntity;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.items.CameraMonitorItem;
import net.geforcemods.securitycraft.misc.CameraView;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.MountCamera;
import net.geforcemods.securitycraft.network.server.RemoveCameraTag;
import net.geforcemods.securitycraft.screen.components.ClickButton;
import net.geforcemods.securitycraft.screen.components.HoverChecker;
import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CameraMonitorScreen extends Screen {

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private PlayerInventory playerInventory;
	private CameraMonitorItem cameraMonitor;
	private CompoundNBT nbtTag;
	private Button prevPageButton;
	private Button nextPageButton;
	private ClickButton[] cameraButtons = new ClickButton[10];
	private ClickButton[] unbindButtons = new ClickButton[10];
	private HoverChecker[] hoverCheckers = new HoverChecker[10];
	private SecurityCraftTileEntity[] cameraTEs = new SecurityCraftTileEntity[10];
	private int[] cameraViewDim = new int[10];
	private int xSize = 176, ySize = 166;
	private int page = 1;

	public CameraMonitorScreen(PlayerInventory inventory, CameraMonitorItem item, CompoundNBT itemNBTTag) {
		super(new TranslationTextComponent(SCContent.CAMERA_MONITOR.get().getTranslationKey()));
		playerInventory = inventory;
		cameraMonitor = item;
		nbtTag = itemNBTTag;
	}

	public CameraMonitorScreen(PlayerInventory inventory, CameraMonitorItem item, CompoundNBT itemNBTTag, int page) {
		this(inventory, item, itemNBTTag);
		this.page = page;
	}

	@Override
	public void func_231160_c_(){
		super.func_231160_c_();

		prevPageButton = new ClickButton(-1, field_230708_k_ / 2 - 68, field_230709_l_ / 2 + 40, 20, 20, "<", this::actionPerformed);
		nextPageButton = new ClickButton(0, field_230708_k_ / 2 + 52, field_230709_l_ / 2 + 40, 20, 20, ">", this::actionPerformed);
		func_230480_a_(prevPageButton);
		func_230480_a_(nextPageButton);

		cameraButtons[0] = new ClickButton(1, field_230708_k_ / 2 - 38, field_230709_l_ / 2 - 60 + 10, 20, 20, "", this::actionPerformed);
		cameraButtons[1] = new ClickButton(2, field_230708_k_ / 2 - 8, field_230709_l_ / 2 - 60 + 10, 20, 20, "", this::actionPerformed);
		cameraButtons[2] = new ClickButton(3, field_230708_k_ / 2 + 22, field_230709_l_ / 2 - 60 + 10, 20, 20, "", this::actionPerformed);
		cameraButtons[3] = new ClickButton(4, field_230708_k_ / 2 - 38, field_230709_l_ / 2 - 30 + 10, 20, 20, "", this::actionPerformed);
		cameraButtons[4] = new ClickButton(5, field_230708_k_ / 2 - 8, field_230709_l_ / 2 - 30 + 10, 20, 20, "", this::actionPerformed);
		cameraButtons[5] = new ClickButton(6, field_230708_k_ / 2 + 22, field_230709_l_ / 2 - 30 + 10, 20, 20, "", this::actionPerformed);
		cameraButtons[6] = new ClickButton(7, field_230708_k_ / 2 - 38, field_230709_l_ / 2 + 10, 20, 20, "", this::actionPerformed);
		cameraButtons[7] = new ClickButton(8, field_230708_k_ / 2 - 8, field_230709_l_ / 2 + 10, 20, 20, "", this::actionPerformed);
		cameraButtons[8] = new ClickButton(9, field_230708_k_ / 2 + 22, field_230709_l_ / 2 + 10, 20, 20, "", this::actionPerformed);
		cameraButtons[9] = new ClickButton(10, field_230708_k_ / 2 - 38, field_230709_l_ / 2 + 40, 80, 20, "", this::actionPerformed);

		unbindButtons[0] = new ClickButton(11, field_230708_k_ / 2 - 19, field_230709_l_ / 2 - 68 + 10, 8, 8, "x", this::actionPerformed);
		unbindButtons[1] = new ClickButton(12, field_230708_k_ / 2 + 11, field_230709_l_ / 2 - 68 + 10, 8, 8, "x", this::actionPerformed);
		unbindButtons[2] = new ClickButton(13, field_230708_k_ / 2 + 41, field_230709_l_ / 2 - 68 + 10, 8, 8, "x", this::actionPerformed);
		unbindButtons[3] = new ClickButton(14, field_230708_k_ / 2 - 19, field_230709_l_ / 2 - 38 + 10, 8, 8, "x", this::actionPerformed);
		unbindButtons[4] = new ClickButton(15, field_230708_k_ / 2 + 11, field_230709_l_ / 2 - 38 + 10, 8, 8, "x", this::actionPerformed);
		unbindButtons[5] = new ClickButton(16, field_230708_k_ / 2 + 41, field_230709_l_ / 2 - 38 + 10, 8, 8, "x", this::actionPerformed);
		unbindButtons[6] = new ClickButton(17, field_230708_k_ / 2 - 19, field_230709_l_ / 2 + 2, 8, 8, "x", this::actionPerformed);
		unbindButtons[7] = new ClickButton(18, field_230708_k_ / 2 + 11, field_230709_l_ / 2 + 2, 8, 8, "x", this::actionPerformed);
		unbindButtons[8] = new ClickButton(19, field_230708_k_ / 2 + 41, field_230709_l_ / 2 + 2, 8, 8, "x", this::actionPerformed);
		unbindButtons[9] = new ClickButton(20, field_230708_k_ / 2 + 41, field_230709_l_ / 2 + 32, 8, 8, "x", this::actionPerformed);

		for(int i = 0; i < 10; i++) {
			ClickButton button = cameraButtons[i];
			int camID = (button.id + ((page - 1) * 10));
			ArrayList<CameraView> views = cameraMonitor.getCameraPositions(nbtTag);
			CameraView view;

			button.setMessage(button.getMessage() + camID);
			func_230480_a_(button);

			if((view = views.get(camID - 1)) != null) {
				if(view.dimension != Minecraft.getInstance().player.dimension.getId()) {
					hoverCheckers[button.id - 1] = new HoverChecker(button);
					cameraViewDim[button.id - 1] = view.dimension;
				}

				TileEntity te = Minecraft.getInstance().world.getTileEntity(view.getLocation());

				if(BlockUtils.getBlock(Minecraft.getInstance().world, view.getLocation()) != SCContent.SECURITY_CAMERA.get() || (te instanceof SecurityCameraTileEntity && !((SecurityCameraTileEntity)te).getOwner().isOwner(Minecraft.getInstance().player) && !((SecurityCameraTileEntity)te).hasModule(ModuleType.SMART)))
				{
					button.field_230693_o_ = false;
					cameraTEs[button.id - 1] = null;
					continue;
				}

				cameraTEs[button.id - 1] = (SecurityCraftTileEntity) Minecraft.getInstance().world.getTileEntity(view.getLocation());
				hoverCheckers[button.id - 1] = new HoverChecker(button);
			}
			else
			{
				button.field_230693_o_ = false;
				unbindButtons[button.id - 1].field_230693_o_ = false;
				cameraTEs[button.id - 1] = null;
				continue;
			}
		}

		for(int i = 0; i < 10; i++)
			func_230480_a_(unbindButtons[i]);

		if(page == 1)
			prevPageButton.field_230693_o_ = false;

		if(page == 3 || cameraMonitor.getCameraPositions(nbtTag).size() < (page * 10) + 1)
			nextPageButton.field_230693_o_ = false;

		for(int i = cameraMonitor.getCameraPositions(nbtTag).size() + 1; i <= (page * 10); i++)
			cameraButtons[(i - 1) - ((page - 1) * 10)].field_230693_o_ = false;

	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		func_230446_a_();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		field_230706_i_.getTextureManager().bindTexture(TEXTURE);
		int startX = (field_230708_k_ - xSize) / 2;
		int startY = (field_230709_l_ - ySize) / 2;
		this.blit(startX, startY, 0, 0, xSize, ySize);

		super.render(mouseX, mouseY, partialTicks);

		field_230712_o_.drawString(ClientUtils.localize("gui.securitycraft:monitor.selectCameras"), startX + xSize / 2 - field_230712_o_.getStringWidth(ClientUtils.localize("gui.securitycraft:monitor.selectCameras")) / 2, startY + 6, 4210752);

		for(int i = 0; i < hoverCheckers.length; i++)
			if(hoverCheckers[i] != null && hoverCheckers[i].checkHover(mouseX, mouseY)){
				if(cameraTEs[i] == null)
					this.renderTooltip(field_230712_o_.listFormattedStringToWidth(ClientUtils.localize("gui.securitycraft:monitor.cameraInDifferentDim").replace("#", cameraViewDim[i] + ""), 150), mouseX, mouseY, field_230712_o_);

				if(cameraTEs[i] != null && cameraTEs[i].hasCustomSCName())
					this.renderTooltip(field_230712_o_.listFormattedStringToWidth(ClientUtils.localize("gui.securitycraft:monitor.cameraName").replace("#", cameraTEs[i].getCustomSCName().getString()), 150), mouseX, mouseY, field_230712_o_);
			}
	}

	protected void actionPerformed(ClickButton button) {
		if(button.id == -1)
			field_230706_i_.displayGuiScreen(new CameraMonitorScreen(playerInventory, cameraMonitor, nbtTag, page - 1));
		else if(button.id == 0)
			field_230706_i_.displayGuiScreen(new CameraMonitorScreen(playerInventory, cameraMonitor, nbtTag, page + 1));
		else if (button.id < 11){
			int camID = button.id + ((page - 1) * 10);

			CameraView view = (cameraMonitor.getCameraPositions(nbtTag).get(camID - 1));

			if(BlockUtils.getBlock(Minecraft.getInstance().world, view.getLocation()) == SCContent.SECURITY_CAMERA.get()) {
				((SecurityCameraBlock) BlockUtils.getBlock(Minecraft.getInstance().world, view.getLocation())).mountCamera(Minecraft.getInstance().world, view.x, view.y, view.z, camID, Minecraft.getInstance().player);
				SecurityCraft.channel.sendToServer(new MountCamera(view.x, view.y, view.z, camID));
				Minecraft.getInstance().player.closeScreen();
			}
			else
				button.field_230693_o_ = false;
		}
		else
		{
			int camID = (button.id - 10) + ((page - 1) * 10);

			SecurityCraft.channel.sendToServer(new RemoveCameraTag(playerInventory.getCurrentItem(), camID));
			nbtTag.remove(CameraMonitorItem.getTagNameFromPosition(nbtTag, cameraMonitor.getCameraPositions(nbtTag).get(camID - 1)));
			button.field_230693_o_ = false;
			cameraButtons[(camID - 1) % 10].field_230693_o_ = false;
		}
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

}