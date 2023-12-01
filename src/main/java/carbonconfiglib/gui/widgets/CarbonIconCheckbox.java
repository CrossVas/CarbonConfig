package carbonconfiglib.gui.widgets;

import org.lwjgl.opengl.GL11;

import carbonconfiglib.gui.config.IListOwner;
import carbonconfiglib.gui.widgets.screen.IWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;

/**
 * Copyright 2023 Speiger, Meduris
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class CarbonIconCheckbox extends GuiButton implements IWidget
{
	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/checkbox.png");
	private boolean selected;
	Icon selectedIcon;
	Icon unselectedIcon;
	Runnable listener;
	IChatComponent tooltip;
	IListOwner owner;
	
	public CarbonIconCheckbox(int x, int y, int width, int height, Icon selectedIcon, Icon unselectedIcon, boolean selected) {
		super(0, x, y, width, height, "");
		this.selectedIcon = selectedIcon;
		this.unselectedIcon = unselectedIcon;
		this.selected = selected;
	}
	
	public CarbonIconCheckbox withListener(Runnable listener) {
		this.listener = listener;
		return this;
	}
	
	public CarbonIconCheckbox setTooltip(IListOwner owner, String tooltips) {
		this.owner = owner;
		tooltip = new ChatComponentTranslation(tooltips);
		return this;
	}
	
	public void setSelected(boolean value) {
		this.selected = value;
	}
	
	public boolean selected() {
		return this.selected;
	}
	
	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		if(this.enabled && this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height) {
			func_146113_a(Minecraft.getMinecraft().getSoundHandler());
			selected = !selected;
			if(listener != null) listener.run();
			return true;
		}
		return false;
	}
	
	@Override
	public void render(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if(!visible) return;
		this.field_146123_n = mousePressed(mc, mouseX, mouseY);
		Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		
		GuiUtils.drawTextureRegion(xPosition, yPosition, field_146123_n ? 20F : 0F, 0F, width, height, 20F, 20F, 64F, 64F);
		GuiUtils.drawTextureRegion(xPosition+2, yPosition+2, width-4, height-4, this.selected ? selectedIcon : unselectedIcon, 16, 16);
		if(owner != null && field_146123_n) {
			owner.addTooltips(tooltip);
		}
	}
	
	@Override
	public void setX(int x) { this.xPosition = x; }
	@Override
	public void setY(int y) { this.yPosition = y; }
	@Override
	public int getX() { return xPosition; }
	@Override
	public int getY() { return yPosition; }
	@Override
	public int getWidgetWidth() { return width; }
	@Override
	public int getWidgetHeight() { return height; }
	@Override
	public boolean isHovered() { return field_146123_n; }
}