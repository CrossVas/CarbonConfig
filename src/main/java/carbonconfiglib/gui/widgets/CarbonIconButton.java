package carbonconfiglib.gui.widgets;

import java.util.function.Consumer;

import com.mojang.blaze3d.platform.GlStateManager;

import carbonconfiglib.gui.config.ConfigElement.GuiAlign;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.AbstractButton;

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
public class CarbonIconButton extends AbstractButton
{
	Consumer<CarbonIconButton> listener;
	Icon icon;
	boolean iconOnly = false;
	int hash;
	
	public CarbonIconButton(int x, int y, int width, int height, Icon icon, String name, Consumer<CarbonIconButton> listener) {
		super(x, y, width, height, name);
		this.listener = listener;
		this.icon = icon;
		this.hash = name.hashCode();
	}
	
	public CarbonIconButton setIconOnly() {
		iconOnly = true;
		return this;
	}
	
	@Override
	public void renderButton(int mouseX, int mouseY, float p_93679_) {
        int k = this.getYImage(this.isHovered());
        GuiUtils.drawTextureWithBorder(WIDGETS_LOCATION, x, y, 0, 46 + k * 20, this.width, this.height, 200, 20, 2, 3, 2, 2, blitOffset);
        if(iconOnly) {
    		int j = getFGColor();
            GlStateManager.color4f(((j >> 16) & 0xFF) / 255F, ((j >> 8) & 0xFF) / 255F, (j & 0xFF) / 255F, 1F);
    		GuiUtils.drawTextureRegion(x + (width / 2) - 5.5F, y+height/2-5.5F, 11, 11, icon, 16, 16);
    		GlStateManager.color4f(1F, 1F, 1F, 1F);
        	return;
        }
        
		Minecraft minecraft = Minecraft.getInstance();
		FontRenderer font = minecraft.fontRenderer;
		int width = font.getStringWidth(getMessage()) + 21;
		float minX = x + 4 + (this.width / 2) - (width / 2);
		int j = getFGColor();
		GlStateManager.color4f(((j >> 16) & 0xFF) / 255F, ((j >> 8) & 0xFF) / 255F, (j & 0xFF) / 255F, 1F);
		GuiUtils.drawTextureRegion(minX, y+(height-8)/2, 11, 11, icon, 16, 16);
		GlStateManager.color4f(1F, 1F, 1F, 1F);
		GuiUtils.drawScrollingShadowString(font, getMessage(), minX+15, y, width, height-2, GuiAlign.CENTER, getFGColor(), hash);

	}
	
	@Override
	public void onPress() {
		if(listener == null) return;
		listener.accept(this);
	}
}
