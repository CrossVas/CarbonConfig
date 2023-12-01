package carbonconfiglib.networking.snyc;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import carbonconfiglib.config.ConfigHandler;
import carbonconfiglib.impl.ReloadMode;
import carbonconfiglib.networking.ICarbonPacket;
import carbonconfiglib.utils.SyncType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
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
public class BulkSyncPacket implements ICarbonPacket
{
	List<SyncPacket> packets = new ObjectArrayList<>();
	
	public BulkSyncPacket() {
	}
	
	public BulkSyncPacket(List<SyncPacket> packets) {
		this.packets = packets;
	}
	
	public static BulkSyncPacket create(Collection<ConfigHandler> toSync, SyncType type, boolean forceSync) {
		List<SyncPacket> result = new ObjectArrayList<>();
		for(ConfigHandler handler : toSync) {
			SyncPacket packet = SyncPacket.create(handler, type, forceSync);
			if(packet != null) result.add(packet);
		}
		return result.isEmpty() ? null : new BulkSyncPacket(result);
	}

	@Override
	public void write(PacketBuffer buffer) throws IOException {
		buffer.writeVarIntToBuffer(packets.size());
		for(SyncPacket packet : packets) {
			packet.write(buffer);
		}
	}
	
	@Override
	public void read(PacketBuffer buffer) throws IOException {
		int size = buffer.readVarIntFromBuffer();
		for(int i = 0;i<size;i++) {
			SyncPacket packet = new SyncPacket();
			packet.read(buffer);
			packets.add(packet);
		}
	}
	
	@Override
	public void process(EntityPlayer player) {
		ReloadMode result = null;
		for(SyncPacket packet : packets) {
			result = ReloadMode.or(result, packet.processEntry(player));
		}
		if(result != null) {
			player.addChatMessage(result.getName());
		}
	}
	
}
