package agency.highlysuspect.incorporeal.block.entity;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.IncConfig;
import agency.highlysuspect.incorporeal.corporea.IndexFinder;
import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import agency.highlysuspect.incorporeal.item.IncItems;
import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.subtile.RadiusDescriptor;
import vazkii.botania.api.subtile.TileEntityFunctionalFlower;
import vazkii.botania.common.block.tile.corporea.TileCorporeaIndex;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class SanvocaliaBlockEntity extends TileEntityFunctionalFlower {
	public SanvocaliaBlockEntity(int radius, BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.radius = radius;
	}
	
	public static SanvocaliaBlockEntity big(BlockPos pos, BlockState state) {
		return new SanvocaliaBlockEntity(3, IncBlockEntityTypes.SANVOCALIA_BIG, pos, state);
	}
	
	public static SanvocaliaBlockEntity small(BlockPos pos, BlockState state) {
		return new SanvocaliaBlockEntity(1, IncBlockEntityTypes.SANVOCALIA_BIG, pos, state);
	}
	
	private final int radius;
	private int cooldown;
	
	private @Nullable UUID placerUuid = null;
	private @Nullable Component name = null;
	
	private static final UUID CHAT_SEND_UUID = UUID.fromString("00000000-0000-0000-0000-000000000069");
	private static final int CHAT_COST = 100;
	private static final int REDEEM_COST = 20;
	
	@Override
	public void tickFlower() {
		super.tickFlower();
		if(level == null || level.isClientSide()) return;
		
		if(cooldown > 0) {
			cooldown--;
			return;
		}
		
		BlockPos pos = getEffectivePos();
		
		AABB itemDetectionBox = new AABB(pos.offset(-radius, 0, -radius), pos.offset(radius + 1, 1, radius + 1));
		List<ItemEntity> nearbyTicketEnts = level.getEntitiesOfClass(ItemEntity.class, itemDetectionBox, ent -> {
			if(ent == null || !ent.isAlive()) return false;
			ItemStack stack = ent.getItem();
			return !stack.isEmpty() && stack.getItem() == IncItems.CORPOREA_TICKET && IncItems.CORPOREA_TICKET.tryGetRequest(stack).isPresent();
		});
		if(nearbyTicketEnts.isEmpty()) return;
		
		//Pick one at random and get its request
		ItemEntity ticketEnt = Inc.choose(nearbyTicketEnts, level.getRandom());
		@SuppressWarnings("OptionalGetWithoutIsPresent") //Checked above
		SolidifiedRequest request = IncItems.CORPOREA_TICKET.tryGetRequest(ticketEnt.getItem()).get();
		
		List<TileCorporeaIndex> nearbyIndices = IndexFinder.findNearBlock(level, pos, radius);
		
		if(nearbyIndices.isEmpty()) {
			//Nod to when people accidentally talk in chat while being too far from a corporea index
			MinecraftServer server = level.getServer();
			if(server != null && getMana() >= CHAT_COST) {
				Component msg = new TranslatableComponent("chat.type.text",
					name == null ? new TranslatableComponent("block.incorporeal.sanvocalia") : name,
					request.toComponent());
				
				Inc.LOGGER.info("Sanvocalia message triggered at {} in dimension {}", pos.toShortString(), level.dimension().location().toString());
				
				if(IncConfig.INSTANCE.everyoneHearsSanvocalia) {
					server.getPlayerList().broadcastMessage(msg, ChatType.CHAT, CHAT_SEND_UUID);
				} else if(placerUuid != null) {
					ServerPlayer player = server.getPlayerList().getPlayer(placerUuid);
					if(player != null) player.sendMessage(msg, CHAT_SEND_UUID);
				}
				
				addMana(-CHAT_COST);
				consumeTicket(ticketEnt, null);
				cooldown = 3;
				sync();
			}
		} else {
			boolean didAnything = false;
			Set<BlockPos> indexPositions = new HashSet<>();
			for(TileCorporeaIndex index : nearbyIndices) {
				if(getMana() < REDEEM_COST) break;
				addMana(-REDEEM_COST);
				
				index.doCorporeaRequest(request.matcher(), request.count(), index.getSpark());
				
				indexPositions.add(index.getBlockPos());
				didAnything = true;
			}
			
			if(didAnything) {
				consumeTicket(ticketEnt, indexPositions);
				cooldown = 3;
				sync();
			}
		}
	}
	
	@SuppressWarnings("unused") //Working on it
	private void consumeTicket(ItemEntity ticket, @Nullable Collection<BlockPos> indexPositions) {
		Preconditions.checkNotNull(level);
		Preconditions.checkArgument(!level.isClientSide(), "call on server level only pls.");
		ServerLevel levelS = (ServerLevel) level;
		
		BlockPos pos = getEffectivePos();
		
		//Burp
		SoundEvent evt = level.getRandom().nextInt(10) == 0 ? SoundEvents.PLAYER_BURP : SoundEvents.GENERIC_EAT;
		level.playSound(null, pos, evt, SoundSource.BLOCKS, .5f, 1f);
		
		//Show eating particles
		levelS.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, ticket.getItem()), ticket.getX(), ticket.getY(), ticket.getZ(), 1, 0, 0.1, 0, 0.03);
		
		//Show sparkle lines
		//TODO
		
		//Shrink itemstack
		if(ticket.getItem().getCount() > 1) {
			ticket.getItem().shrink(1);
			ticket.setItem(ticket.getItem()); //forces a sync?
		} else {
			ticket.discard();
		}
	}
	
	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		placerUuid = placer == null ? null : placer.getUUID();
		if(stack.hasCustomHoverName()) name = stack.getHoverName();
	}
	
	@Override
	public void readFromPacketNBT(CompoundTag tag) {
		super.readFromPacketNBT(tag);
		
		placerUuid = tag.contains("Placer") ? tag.getUUID("Placer") : null;
		name = tag.contains("Name") ? Component.Serializer.fromJson(tag.getString("Name")) : null;
		cooldown = tag.getInt("Cooldown");
	}
	
	@Override
	public void writeToPacketNBT(CompoundTag tag) {
		super.writeToPacketNBT(tag);
		
		if(placerUuid != null) tag.putUUID("Placer", placerUuid);
		if(name != null) tag.putString("Name", Component.Serializer.toJson(name));
		tag.putInt("Cooldown", cooldown);
	}
	
	@Override
	public int getMaxMana() {
		return 200;
	}
	
	@Override
	public int getColor() {
		return 0xed9625;
	}
	
	@Nullable
	@Override
	public RadiusDescriptor getRadius() {
		return new RadiusDescriptor.Square(getEffectivePos(), radius);
	}
}
