package carbonconfiglib.gui.config;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.matrix.MatrixStack;

import carbonconfiglib.gui.api.IArrayNode;
import carbonconfiglib.gui.api.ICompoundNode;
import carbonconfiglib.gui.api.IValueNode;
import carbonconfiglib.gui.screen.ListSelectionScreen;
import carbonconfiglib.gui.screen.ListSelectionScreen.NodeSupplier;
import carbonconfiglib.gui.widgets.CarbonHoverIconButton;
import carbonconfiglib.gui.widgets.CarbonHoverIconButton.IconInfo;
import carbonconfiglib.gui.widgets.CarbonIconButton;
import carbonconfiglib.gui.widgets.GuiUtils;
import carbonconfiglib.gui.widgets.IOwnable;
import carbonconfiglib.gui.widgets.Icon;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

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
public class ConfigElement extends Element
{
	private static final ITextComponent DELETE = new TranslationTextComponent("gui.carbonconfig.delete");
	private static final ITextComponent REVERT = new TranslationTextComponent("gui.carbonconfig.revert");
	private static final ITextComponent DEFAULT = new TranslationTextComponent("gui.carbonconfig.default");
	private static final ITextComponent SUGGESTIONS = new TranslationTextComponent("gui.carbonconfig.suggestions");
	private static final ITextComponent RELOAD = new TranslationTextComponent("gui.carbonconfig.reload").withStyle(TextFormatting.YELLOW);
	private static final ITextComponent RESTART = new TranslationTextComponent("gui.carbonconfig.restart").withStyle(TextFormatting.YELLOW);
	protected List<IGuiEventListener> listeners = new ObjectArrayList<>();
	protected List<Map.Entry<Widget, AlignOffset>> mappedListeners = new ObjectArrayList<>();
	protected IValueNode value;
	protected IArrayNode array;
	protected ICompoundNode compound;
	protected int compoundIndex = -1;
	
	protected CarbonIconButton setReset;
	protected CarbonIconButton setDefault;
	protected CarbonIconButton suggestion;
	protected CarbonHoverIconButton moveDown;
	protected CarbonHoverIconButton moveUp;
	
	protected ConfigElement(ITextComponent name) {
		super(name);
	}
	
	public ConfigElement(IValueNode value) {
		super(value.getName());
		this.value = value;
	}
	
	public ConfigElement(IArrayNode array, IValueNode value) {
		super(value.getName());
		this.array = array;
		this.value = value;
	}
	
	public ConfigElement(IArrayNode array, ITextComponent name) {
		super(name);
		this.array = array;
	}
	
	public ConfigElement(ICompoundNode compound, ITextComponent name) {
		super(name);
		this.compound = compound;
	}
	
	public ConfigElement(ICompoundNode compound, IValueNode value) {
		super(value.getName());
		this.compound = compound;
		this.value = value;
	}
	
	protected <T extends Widget> T addChild(T element) {
		return addChild(element, GuiAlign.RIGHT, 0);
	}
	
	protected <T extends Widget> T addChild(T element, int xOffset) {
		return addChild(element, GuiAlign.RIGHT, xOffset);
	}
	
	protected <T extends Widget> T addChild(T element, GuiAlign align, int xOffset) {
		listeners.add(element);
		mappedListeners.add(new AbstractMap.SimpleEntry<>(element, new AlignOffset(align, -xOffset - 19)));
		if(owner != null && element instanceof IOwnable) {
			((IOwnable)element).setOwner(owner);
		}
		return element;
	}
	
	@Override
	public void init() {
		super.init();
		if(createResetButtons(value)) {
			if(isArray()) {
				setReset = addChild(new CarbonIconButton(0, 0, 18, 18, Icon.DELETE, new StringTextComponent	(""), this::onDeleted).setIconOnly(), -31);
				setReset.active = isReset();
				moveDown = new CarbonHoverIconButton(0, 0, 15, 8, new IconInfo(0, -3, 16, 16), Icon.MOVE_DOWN, Icon.MOVE_DOWN_HOVERED, this::onMoveDown);
				listeners.add(moveDown);
				moveUp = new CarbonHoverIconButton(0, 0, 15, 8, new IconInfo(0, -3, 16, 16), Icon.MOVE_UP, Icon.MOVE_UP_HOVERED, this::onMoveUp);
				listeners.add(moveUp);
			}
			else {
				setReset = addChild(new CarbonIconButton(0, 0, 18, 18, Icon.REVERT, new StringTextComponent(""), this::onReset).setIconOnly(), -21);
				setDefault = addChild(new CarbonIconButton(0, 0, 18, 18, Icon.SET_DEFAULT, new StringTextComponent(""), this::onDefault).setIconOnly(), -40);
				suggestion = addChild(new CarbonIconButton(0, 0, 18, 18, Icon.SUGGESTIONS, new StringTextComponent(""), this::onSuggestion).setIconOnly(), -59);
				setReset.active = isReset();
				setDefault.active = !isDefault();
				suggestion.visible = false;
			}
		}
		if(owner != null) {
			for(IGuiEventListener entry : listeners) {
				if(entry instanceof IOwnable) {
					((IOwnable)entry).setOwner(owner);
				}
			}
		}
	}
	
	@Override
	public void tick() {
		super.tick();
		if(setReset != null) {
			setReset.active = isReset();
		}
		if(setDefault != null) {
			setDefault.active = !isDefault();
		}
	}
	
	@Override
	public void render(MatrixStack poseStack, int x, int top, int left, int width, int height, int mouseX, int mouseY, boolean selected, float partialTicks) {
		if(renderName() && !isArray()) {
			renderName(poseStack, left, top, isChanged(), getMaxTextWidth(), height);
			if(!isCompound() && value != null) {
				if(value.requiresReload()) {
					GuiUtils.drawTextureRegion(poseStack, left-16, top+(height/2)-6, 12, 12, Icon.RELOAD, 16, 16);
					if(mouseX >= left-16 && mouseX <= left-4 && mouseY >= top && mouseY <= top+height && owner.isInsideList(mouseX, mouseY)) {
						owner.addTooltips(RELOAD);
					}
				}
				else if(value.requiresRestart()) {
					GuiUtils.drawTextureRegion(poseStack, left-16, top+(height/2)-6, 12, 12, Icon.RESTART, 16, 16);
					if(mouseX >= left-16 && mouseX <= left-4 && mouseY >= top && mouseY <= top+height && owner.isInsideList(mouseX, mouseY)) {
						owner.addTooltips(RESTART);
					}
				}
			}
		}
		int maxX = Integer.MAX_VALUE;
		if(renderChildren()) {
			if(isArray()) {
				moveUp.x = left + width - 16;
				moveUp.y = top;
				moveUp.visible = canMoveUp();
				moveUp.render(poseStack, mouseX, mouseY, partialTicks);
				moveDown.x = left + width - 16;
				moveDown.y = top + 10;
				moveDown.visible = canMoveDown();
				moveDown.render(poseStack, mouseX, mouseY, partialTicks);
				if(moveDown.visible || moveUp.visible) {
					left -= 8;
				}
			}
			for(Map.Entry<Widget, AlignOffset> entry : mappedListeners) {
				Widget widget = entry.getKey();
				AlignOffset offset = entry.getValue();
				widget.x = offset.align.align(left, width, widget.getWidth()) + offset.offset;
				widget.y = top;
				widget.render(poseStack, mouseX, mouseY, partialTicks);
				maxX = Math.min(maxX, widget.x);
			}
		}
		maxX = getMaxX(maxX);
		if(isArray()) {
			ITextComponent comp = new StringTextComponent(indexOf()+":");
			renderText(poseStack, comp, maxX-115, top-1, 105, height, GuiAlign.RIGHT, -1);
		}
		if(value != null && mouseY >= top && mouseY <= top + height && mouseX >= left && mouseX <= maxX-2 && owner.isInsideList(mouseX, mouseY)) {
			owner.addTooltips(value.getTooltip());
		}
		if(isArray()) {
			if(setReset.isHovered() && owner.isInsideList(mouseX, mouseY)) {
				owner.addTooltips(DELETE);
			}
		}
		else {
			if(setReset.isHovered() && owner.isInsideList(mouseX, mouseY)) {
				owner.addTooltips(REVERT);
			}
			if(setDefault.isHovered() && owner.isInsideList(mouseX, mouseY)) {
				owner.addTooltips(DEFAULT);
			}
			suggestion.visible = hasSuggestions();
			if(suggestion.visible && suggestion.isHovered() & owner.isInsideList(mouseX, mouseY)) {
				owner.addTooltips(SUGGESTIONS);
			}
		}
	}
	
	protected boolean hasSuggestions() {
		return value != null && value.getSuggestions().size() > 0;
	}
	
	protected int getMaxX(int prevMaxX) {
		return prevMaxX;
	}
	
	protected int getMaxTextWidth() {
		return isCompound() ? 190 : 200;
	}
	
	protected boolean isArray() {
		return array != null;
	}
	
	protected boolean isCompound() {
		return compound != null;
	}
	
	protected void onMoveDown(CarbonHoverIconButton button) {
		if(!isArray()) return;
		array.moveDown(indexOf());
		owner.updateInformation();
	}
	
	protected void onMoveUp(CarbonHoverIconButton button) {
		if(!isArray()) return;
		array.moveUp(indexOf());
		owner.updateInformation();		
	}
	
	protected boolean canMove() {
		return isArray() && (canMoveDown() || canMoveUp());
	}
	
	protected boolean canMoveUp() {
		return indexOf() > 0;
	}
	
	protected boolean canMoveDown() {
		return indexOf() < array.size() - 1;
	}
	
	protected boolean renderName() {
		return true;
	}
	
	protected boolean renderChildren() {
		return true;
	}
	
	protected boolean createResetButtons(IValueNode value) {
		return value != null;
	}
	
	protected int indexOf() {
		return array.indexOf(value);
	}
	
	protected boolean isReset() {
		return isArray() || value.isChanged();
	}
	
	public boolean isChanged() {
		return value.isChanged();
	}
	
	public boolean isDefault() {
		return value.isDefault();
	}
	
	protected void onDeleted(CarbonIconButton button) {
		if(!isArray()) return;
		owner.removeEntry(this);
	}
	
	protected void onReset(CarbonIconButton button) {
		value.setPrevious();
		updateValues();
	}
	
	protected void onDefault(CarbonIconButton button) {
		if(value == null) return;
		value.setDefault();
		updateValues();
	}
	
	protected void onSuggestion(CarbonIconButton button) {
		if(value == null) return;
		mc.setScreen(new ListSelectionScreen(mc.screen, value, NodeSupplier.ofValue(), owner.getCustomTexture()));
	}
	
	@Override
	public void updateValues() {
	}
	
	@Override
	public List<? extends IGuiEventListener> children() {
		return listeners;
	}
	
	public static class AlignOffset {
		GuiAlign align;
		int offset;
		
		public AlignOffset(GuiAlign align, int offset) {
			this.align = align;
			this.offset = offset;
		}
	}
	
	public static enum GuiAlign {
		LEFT,
		CENTER,
		RIGHT;
		
		public int align(int left, int bounds, int width) {
			switch(this) {
				case LEFT: return left;
				case CENTER: return left + (bounds/2) - (width / 2);
				case RIGHT: return left + bounds - width - 42;
				default: return left;
			}
		}
		
		public float align(float width) {
			switch(this) {
				case CENTER: return width * -0.5F;
				case RIGHT: return -width;
				default: return 0F;
			}
		}
		
		public float alignCenter() {
			switch(this) {
				case CENTER: return 0F;
				case RIGHT: return 0.5F;
				default: return -0.5F;
			}
		}
	}
}
