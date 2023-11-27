package carbonconfiglib.impl;

import carbonconfiglib.api.IReloadMode;
import net.minecraft.util.text.ITextComponent;
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
public enum ReloadMode implements IReloadMode
{
	WORLD(new TranslationTextComponent("gui.carbonconfig.reload.sync")),
	GAME(new TranslationTextComponent("gui.carbonconfig.restart.sync"));
	
	ITextComponent message;
	
	private ReloadMode(ITextComponent message) {
		this.message = message;
	}
	
	public ITextComponent getMessage() {
		return message;
	}
	
	public static ReloadMode or(ReloadMode original, IReloadMode other) {
		return getByIndex(Math.max(getModeIndex(original), getModeIndex(other instanceof ReloadMode ? (ReloadMode)other : null)));
	}
	
	private static int getModeIndex(ReloadMode mode) {
		return mode == null ? -1 : mode.ordinal();
	}
	
	private static ReloadMode getByIndex(int index) {
		return index == 0 ? ReloadMode.WORLD : (index == 1 ? ReloadMode.GAME : null);
	}
}
