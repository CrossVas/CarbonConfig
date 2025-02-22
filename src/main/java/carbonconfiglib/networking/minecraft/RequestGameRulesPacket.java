package carbonconfiglib.networking.minecraft;

import java.util.UUID;

import carbonconfiglib.CarbonConfig;
import carbonconfiglib.networking.ICarbonPacket;
import carbonconfiglib.networking.carbon.ConfigAnswerPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.server.ServerLifecycleHooks;

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
public class RequestGameRulesPacket implements ICarbonPacket
{
	UUID requestId;
	
	public RequestGameRulesPacket() {}
	
	public RequestGameRulesPacket(UUID requestId) {
		this.requestId = requestId;
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeUUID(requestId);
	}
	
	@Override
	public void read(FriendlyByteBuf buffer) {
		requestId = buffer.readUUID();
	}
	
	@Override
	public void process(Player player) {
		if(!CarbonConfig.hasPermission(player, 4)) {
			return;
		}
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if(server == null) return;
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		buf.writeNbt(server.getGameRules().createTag());
		byte[] data = new byte[buf.writerIndex()];
		buf.readBytes(data);
		CarbonConfig.NETWORK.sendToPlayer(new ConfigAnswerPacket(requestId, data), player);
	}
}
