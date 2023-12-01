package carbonconfiglib.networking.carbon;

import java.io.IOException;

import carbonconfiglib.CarbonConfig;
import carbonconfiglib.config.ConfigHandler;
import carbonconfiglib.networking.ICarbonPacket;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.management.UserListOpsEntry;
import speiger.src.collections.objects.lists.ObjectArrayList;

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
public class SaveConfigPacket implements ICarbonPacket
{
	String identifier;
	String data;
	
	public SaveConfigPacket() {
	}
	
	public SaveConfigPacket(String identifier, String data) {
		this.identifier = identifier;
		this.data = data;
	}

	@Override
	public void write(PacketBuffer buffer) throws IOException {
		buffer.writeStringToBuffer(identifier);
		buffer.writeStringToBuffer(data);
	}
	
	@Override
	public void read(PacketBuffer buffer) throws IOException {
		identifier = buffer.readStringFromBuffer(32767);
		data = buffer.readStringFromBuffer(262144);
	}
	
	@Override
	public void process(EntityPlayer player) {
		if(!canIgnorePermissionCheck() && !hasPermissions(player, 4)) {
			CarbonConfig.LOGGER.warn("Don't have Permission to Change configs");
			return;
		}
		ConfigHandler handler = CarbonConfig.CONFIGS.getConfig(identifier);
		if(handler == null) return;
		try {
			ConfigHandler.load(handler, handler.getConfig(), ObjectArrayList.wrap(data.split("\n")), false);
			//TODO update
			handler.onSynced();
			handler.save();
			CarbonConfig.LOGGER.info("Saved ["+identifier+"] Config");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean hasPermissions(EntityPlayer player, int value) {
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		UserListOpsEntry entry = (UserListOpsEntry)server.getConfigurationManager().func_152603_m().func_152683_b(player.getGameProfile());
		return entry != null && entry.func_152644_a() >= value;
	}
	
	
	private boolean canIgnorePermissionCheck() {
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		return !server.isDedicatedServer() && (server instanceof IntegratedServer ? ((IntegratedServer)server).getPublic() : false);
	}
	
}
