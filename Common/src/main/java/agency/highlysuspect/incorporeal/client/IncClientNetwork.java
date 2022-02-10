package agency.highlysuspect.incorporeal.client;

import agency.highlysuspect.incorporeal.net.FunnyEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.NoteParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import vazkii.botania.common.item.ItemTwigWand;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class IncClientNetwork {
	public static Map<String, BiConsumer<Minecraft, FriendlyByteBuf>> handlers = new HashMap<>();
	
	static {
		handlers.put("funny", (mc, buf) -> {
			FunnyEffect effect = FunnyEffect.unpack(buf);
			
			mc.doRunTask(() -> {
				Level level = mc.level;
				if(level == null) return;
				
				BlockPos srcPos = effect.src();
				double sparkleHeight = effect.sparkleHeight();
				Vec3 src = level.getBlockState(srcPos).getOffset(level, srcPos).add(srcPos.getX() + .5, srcPos.getY() + sparkleHeight, srcPos.getZ() + .5);
				
				for(FunnyEffect.Line line : effect.lines()) {
					Vec3 dst = Vec3.atCenterOf(line.dst());
					//todo: port my own particle beam mayb?
					ItemTwigWand.doParticleBeam(level, src, dst);
					
					byte[] notes = line.notes();
					if(notes.length == 1) {
						level.addParticle(ParticleTypes.NOTE, dst.x, dst.y + 0.7, dst.z, notes[0] / 24d, 0, 0);
					} else if(notes.length == 2) {
						level.addParticle(ParticleTypes.NOTE, dst.x - 0.2, dst.y + 0.7, dst.z, notes[0] / 24d, 0, 0);
						level.addParticle(ParticleTypes.NOTE, dst.x + 0.2, dst.y + 0.7, dst.z, notes[1] / 24d, 0, 0);
					}
				}
			});
		});
	}
	
	public static void initialize() {
		//Classload
	}
	
	//called on the network thread - Watch out
	public static void handle(Minecraft client, FriendlyByteBuf buf) {
		String which = buf.readUtf();
		BiConsumer<Minecraft, FriendlyByteBuf> handler = handlers.get(which);
		if(handler != null) {
			//give it the rest of the buf
			handler.accept(client, buf);
		}
	}
}