package carbonconfiglib.networking.carbon;

import carbonconfiglib.CarbonConfig;
import carbonconfiglib.config.ConfigHandler;
import carbonconfiglib.networking.ICarbonPacket;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

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
    public static final StreamCodec<FriendlyByteBuf, SaveConfigPacket> STREAM_CODEC = CustomPacketPayload.codec(SaveConfigPacket::write, ICarbonPacket.readPacket(SaveConfigPacket::new));
	public static final Type<SaveConfigPacket> ID = CustomPacketPayload.createType("carbonconfig:save_carbon");
	String identifier;
	String data;
	
	public SaveConfigPacket(String identifier, String data) {
		this.identifier = identifier;
		this.data = data;
	}
	
	public SaveConfigPacket(FriendlyByteBuf buffer) {
		identifier = buffer.readUtf(32767);
		data = buffer.readUtf(262144);
	}
	
	public void write(FriendlyByteBuf buffer) {
		buffer.writeUtf(identifier, 32767);
		buffer.writeUtf(data, 262144);
	}
		
	@Override
	public Type<? extends CustomPacketPayload> type() { return ID; }
	
	@Override
	public void process(Player player) {
		if(!CarbonConfig.hasPermission(player, 4)) {
			CarbonConfig.LOGGER.warn("Don't have Permission to Change configs");
			return;
		}
		ConfigHandler handler = CarbonConfig.getConfigs().getConfig(identifier);
		if(handler == null) return;
		try {
			ConfigHandler.load(handler, handler.getConfig(), ObjectArrayList.wrap(data.split("\n")), false);
			handler.save();
			CarbonConfig.LOGGER.info("Saved ["+identifier+"] Config");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
