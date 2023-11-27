package carbonconfiglib.gui.impl.carbon;

import java.util.List;

import carbonconfiglib.config.Config;
import carbonconfiglib.config.ConfigSection;
import carbonconfiglib.gui.api.IConfigFolderNode;
import carbonconfiglib.gui.api.IConfigNode;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.text.ITextComponent;

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
public class ConfigRoot implements IConfigFolderNode
{
	Config config;
	List<IConfigNode> children;
	
	public ConfigRoot(Config config) {
		this.config = config;
	}

	@Override
	public List<IConfigNode> getChildren() {
		if(children == null) {
			children = new ObjectArrayList<>();
			for(ConfigSection section : config.getChildren()) {
				children.add(new ConfigNode(section));
			}
		}
		return children;
	}
	@Override
	public String getNodeName() { return null; }
	@Override
	public ITextComponent getName() { return IConfigNode.createLabel(config.getName()); }
	@Override
	public boolean isRoot() { return true; }
}
