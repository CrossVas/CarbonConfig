package carbonconfiglib.networking.carbon;

import java.util.UUID;

import carbonconfiglib.gui.api.IRequestScreen;
import carbonconfiglib.networking.ICarbonPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
public class ConfigAnswerPacket implements ICarbonPacket
{
	UUID id;
	byte[] data;
	
	public ConfigAnswerPacket() {}
	
	public ConfigAnswerPacket(UUID id, byte[] data) {
		this.id = id;
		this.data = data;
	}

	@Override
	public void write(PacketBuffer buffer) {
		buffer.writeUuid(id);
		buffer.writeByteArray(data);
	}
	
	@Override
	public void read(PacketBuffer buffer) {
		id = buffer.readUuid();
		data = buffer.readByteArray();
	}
	
	@Override
	public void process(EntityPlayer player) {
		processClient();
	}
	
	@SideOnly(Side.CLIENT)
	private void processClient() {
		GuiScreen screen = Minecraft.getMinecraft().currentScreen;
		if(screen instanceof IRequestScreen) {
			((IRequestScreen)screen).receiveConfigData(id, new PacketBuffer(Unpooled.wrappedBuffer(data)));
		}
	}
}
