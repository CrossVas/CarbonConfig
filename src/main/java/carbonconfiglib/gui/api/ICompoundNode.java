package carbonconfiglib.gui.api;

import java.util.List;

import carbonconfiglib.api.ISuggestionProvider.Suggestion;
import carbonconfiglib.utils.ParseResult;
import net.minecraft.network.chat.Component;

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
public interface ICompoundNode extends INode
{
	public List<IValueNode> getValues();
	public Component getName(int index);
	public boolean isValid();
	public String get();
	public ParseResult<Boolean> isValid(String value);
	public void set(String value);
	public boolean isForcedSuggestion(int index);
	public List<Suggestion> getValidValues(int index);
}
