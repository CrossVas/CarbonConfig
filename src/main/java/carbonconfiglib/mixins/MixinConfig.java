package carbonconfiglib.mixins;

import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class MixinConfig implements IMixinConfigPlugin
{
	@Override
	public void onLoad(String mixinPackage) {
	}
	
	@Override
	public String getRefMapperConfig() {
		return null;
	}
	
	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		if(!mixinClassName.startsWith("carbonconfiglib.mixins.")) return true;
		return isClassLoaded();
	}
	
	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}
	@Override
	public List<String> getMixins() { return null; }
	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
	
	private boolean isClassLoaded() {
		try {
			Class<?> clz = Class.forName("com.terraformersmc.modmenu.ModMenu");
			return clz != null;
		}
		catch(ClassNotFoundException e)
		{
			return false;
		}
	}
}