package carbonconfiglib.gui.widgets;

import java.util.function.Consumer;

import org.lwjgl.opengl.GL11;

import carbonconfiglib.gui.config.ConfigElement.GuiAlign;
import carbonconfiglib.gui.widgets.screen.IWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

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
public class CarbonIconButton extends GuiButton implements IWidget
{
	Consumer<CarbonIconButton> listener;
	Icon icon;
	boolean iconOnly = false;
	int hash;
	
	public CarbonIconButton(int x, int y, int width, int height, Icon icon, String name, Consumer<CarbonIconButton> listener) {
		super(0, x, y, width, height, name);
		this.listener = listener;
		this.icon = icon;
		this.hash = name.hashCode();
	}
	
	public CarbonIconButton setIconOnly() {
		iconOnly = true;
		return this;
	}
	
	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		if(this.enabled && this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height) {
			func_146113_a(Minecraft.getMinecraft().getSoundHandler());
			if(listener != null) listener.accept(this);
			return true;
		}
		return false;
	}
	
	@Override
	public void render(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if(!visible) return;
		this.field_146123_n = mousePressed(mc, mouseX, mouseY);
        int k = this.getHoverState(this.field_146123_n);
        GuiUtils.drawTextureWithBorder(buttonTextures, xPosition, yPosition, 0, 46 + k * 20, this.width, this.height, 200, 20, 2, 3, 2, 2, 0);
        if(iconOnly) {
    		int j = this.enabled ? 16777215 : 10526880;
            GL11.glColor4f(((j >> 16) & 0xFF) / 255F, ((j >> 8) & 0xFF) / 255F, (j & 0xFF) / 255F, 1F);
    		GuiUtils.drawTextureRegion(xPosition + (width / 2) - 5.5F, yPosition+height/2-5.5F, 11, 11, icon, 16, 16);
    		GL11.glColor4f(1F, 1F, 1F, 1F);
        	return;
        }
        
		FontRenderer font = mc.fontRenderer;
		int width = font.getStringWidth(displayString) + 21;
		float minX = xPosition + 4 + (this.width / 2) - (width / 2);
		int j = this.enabled ? 16777215 : 10526880;
		GL11.glColor4f(((j >> 16) & 0xFF) / 255F, ((j >> 8) & 0xFF) / 255F, (j & 0xFF) / 255F, 1F);
		GuiUtils.drawTextureRegion(minX, yPosition+(height-8)/2, 11, 11, icon, 16, 16);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		GuiUtils.drawScrollingShadowString(font, displayString, minX+15, yPosition, width, height-2, GuiAlign.CENTER, j, hash);
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
