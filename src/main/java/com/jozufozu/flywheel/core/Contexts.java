package com.jozufozu.flywheel.core;

import com.jozufozu.flywheel.Flywheel;
import com.jozufozu.flywheel.backend.Backend;
import com.jozufozu.flywheel.backend.SpecMetaRegistry;
import com.jozufozu.flywheel.backend.pipeline.IShaderPipeline;
import com.jozufozu.flywheel.backend.pipeline.InstancingTemplate;
import com.jozufozu.flywheel.backend.pipeline.WorldShaderPipeline;
import com.jozufozu.flywheel.backend.source.FileResolution;
import com.jozufozu.flywheel.backend.source.Resolver;
import com.jozufozu.flywheel.core.crumbling.CrumblingProgram;
import com.jozufozu.flywheel.core.shader.WorldFog;
import com.jozufozu.flywheel.core.shader.WorldProgram;
import com.jozufozu.flywheel.core.shader.gamestate.FogStateProvider;
import com.jozufozu.flywheel.core.shader.gamestate.NormalDebugStateProvider;
import com.jozufozu.flywheel.event.GatherContextEvent;
import com.jozufozu.flywheel.util.ResourceUtil;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Contexts {

	public static WorldContext<WorldProgram> WORLD;
	public static WorldContext<CrumblingProgram> CRUMBLING;

	public static void flwInit(GatherContextEvent event) {
		Backend backend = event.getBackend();

		SpecMetaRegistry.register(FogStateProvider.INSTANCE);
		SpecMetaRegistry.register(NormalDebugStateProvider.INSTANCE);

		SpecMetaRegistry.register(WorldFog.LINEAR);
		SpecMetaRegistry.register(WorldFog.EXP2);

        FileResolution crumblingBuiltins = Resolver.INSTANCE.findShader(ResourceUtil.subPath(Names.CRUMBLING, ".glsl"));
        FileResolution worldBuiltins = Resolver.INSTANCE.findShader(ResourceUtil.subPath(Names.WORLD, ".glsl"));

		IShaderPipeline<CrumblingProgram> crumblingPipeline = new WorldShaderPipeline<>(CrumblingProgram::new, InstancingTemplate.INSTANCE, crumblingBuiltins);
		IShaderPipeline<WorldProgram> worldPipeline = new WorldShaderPipeline<>(WorldProgram::new, InstancingTemplate.INSTANCE, worldBuiltins);

		CRUMBLING = backend.register(WorldContext.builder(backend, Names.CRUMBLING).build(crumblingPipeline));
		WORLD = backend.register(WorldContext.builder(backend, Names.WORLD).build(worldPipeline));
	}

	public static class Names {
		public static final ResourceLocation CRUMBLING = new ResourceLocation(Flywheel.ID, "context/crumbling");
		public static final ResourceLocation WORLD = new ResourceLocation(Flywheel.ID, "context/world");
	}
}
