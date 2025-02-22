package carbonconfiglib.impl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import carbonconfiglib.CarbonConfig;
import carbonconfiglib.api.ConfigType;
import carbonconfiglib.api.IConfigProxy;
import carbonconfiglib.api.SimpleConfigProxy.SimpleTarget;
import carbonconfiglib.config.ConfigSettings;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
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
public final class PerWorldProxy implements IConfigProxy
{
	public static final LevelResource SERVERCONFIG = new LevelResource("serverconfig");
	public static final IConfigProxy INSTANCE = new PerWorldProxy(FMLPaths.GAMEDIR.get().resolve("multiplayerconfigs"), FMLPaths.GAMEDIR.get().resolve("defaultconfigs"), FMLPaths.GAMEDIR.get().resolve("saves"));
	Path baseClientPath;
	Path baseServerPath;
	Path saveFolders;
	
	private PerWorldProxy(Path baseClientPath, Path baseServerPath, Path saveFolders) {
		this.baseClientPath = baseClientPath;
		this.baseServerPath = baseServerPath;
		this.saveFolders = saveFolders;
	}
	
	public static boolean isProxy(IConfigProxy proxy) {
		return proxy instanceof PerWorldProxy;
	}
	
	public static ConfigSettings perWorld() {
		return ConfigSettings.withFolderProxy(INSTANCE).withType(ConfigType.SERVER);
	}
	
	@Override
	public Path getBasePaths(Path relativeFile) {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if(server != null) {
			Path path = server.getWorldPath(SERVERCONFIG);
			if(Files.exists(path.resolve(relativeFile))) return path;
		}
		else if(FMLEnvironment.dist.isClient() && CarbonConfig.NETWORK.isInWorld()) return baseClientPath;
		return baseServerPath;
	}
	
	@Override
	public List<? extends IPotentialTarget> getPotentialConfigs() {
		if(FMLEnvironment.dist.isClient()) return getLevels();
		else return Collections.singletonList(new SimpleTarget(ServerLifecycleHooks.getCurrentServer().getWorldPath(SERVERCONFIG), "server"));
	}
	
	@OnlyIn(Dist.CLIENT)
	private List<IPotentialTarget> getLevels() {
		LevelStorageSource storage = Minecraft.getInstance().getLevelSource();
		List<IPotentialTarget> folders = new ObjectArrayList<>();
		if(Files.exists(baseServerPath)) {
			folders.add(new SimpleTarget(baseServerPath, "Default Config"));
		}
		for(LevelSummary sum : storage.loadLevelSummaries(storage.findLevelCandidates()).join()) {
			try(LevelStorageSource.LevelStorageAccess access = Minecraft.getInstance().getLevelSource().createAccess(sum.getLevelId()))
			{
				Path path = access.getLevelPath(SERVERCONFIG);
				if(Files.exists(path)) folders.add(new WorldTarget(sum, access.getLevelPath(LevelResource.ROOT), path));
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return folders;
	}
	
	@Override
	public boolean isDynamicProxy() {
		return true;
	}
	
	public static class WorldTarget implements IPotentialTarget {
		LevelSummary summary;
		Path worldFile;
		Path folder;
		
		public WorldTarget(LevelSummary summary, Path worldFile, Path folder) {
			this.summary = summary;
			this.worldFile = worldFile;
			this.folder = folder;
		}

		@Override
		public Path getFolder() {
			return folder;
		}

		@Override
		public String getName() {
			return summary.getLevelName();
		}
		
		public Path getWorldFile() {
			return worldFile;
		}
		
		public LevelSummary getSummary() {
			return summary;
		}
	}
}
