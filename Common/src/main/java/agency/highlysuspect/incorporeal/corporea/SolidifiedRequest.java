package agency.highlysuspect.incorporeal.corporea;

import agency.highlysuspect.incorporeal.IncItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.corporea.CorporeaHelper;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;

import java.util.Optional;

/**
 * A corporea matcher + item count pair. "5x stone", "dozen all", that sort of thing.
 * Can be thought of as "the thing a corporea retainer holds" minus the information about where the request came from.
 * Note that requests for nothing with non-zero count, and requests for something with zero count, are both valid SolidifiedRequests.
 */
public record SolidifiedRequest(@NotNull ICorporeaRequestMatcher matcher, int count) {
	public static final SolidifiedRequest EMPTY = new SolidifiedRequest(EmptyCorporeaRequestMatcher.INSTANCE, 0);
	
	public static SolidifiedRequest create(@Nullable ICorporeaRequestMatcher matcher, int count) {
		//Try to avoid using vanilla bota's dummy matcher, it doesn't have an ID via CorporeaHelper#registerRequestMatcher.
		ICorporeaRequestMatcher matcher2 = matcher == null || matcher == ICorporeaRequestMatcher.Dummy.INSTANCE ? EmptyCorporeaRequestMatcher.INSTANCE : matcher;
		return new SolidifiedRequest(matcher2, count);
	}
	
	//Convenience. For most gameplay purposes (i.e. not the corporea keybind) vanilla bota always uses matchNbt: true when making requests from itemstacks.
	public static SolidifiedRequest create(ItemStack stack, int count) {
		ICorporeaRequestMatcher matcher = stack.isEmpty() ? EmptyCorporeaRequestMatcher.INSTANCE : CorporeaHelper.instance().createMatcher(stack, true);
		return new SolidifiedRequest(matcher, count);
	}
	
	public CompoundTag save() {
		CompoundTag tag = MatcherUtils.toTag(matcher);
		tag.putInt("count", count);
		return tag;
	}
	
	public static Optional<SolidifiedRequest> tryLoad(CompoundTag tag) {
		return MatcherUtils.tryFromTag(tag).map(matcher -> {
			int count = tag.getInt("count");
			return create(matcher, count);
		});
	}
	
	public static SolidifiedRequest loadOrEmpty(CompoundTag tag) {
		return tryLoad(tag).orElse(EMPTY);
	}
	
	public boolean isEmpty() {
		//"&&", not "||"
		return matcher == EmptyCorporeaRequestMatcher.INSTANCE && count == 0;
	}
	
	public Component toComponent() {
		return new TranslatableComponent("incorporeal.solidified_request", count, matcher.getRequestName());
	}
	
	public ItemStack toTicket() {
		return IncItems.CORPOREA_TICKET.produce(this);
	}
	
	public int signalStrength() {
		return CorporeaHelper.instance().signalStrengthForRequestSize(count);
	}
	
	public SolidifiedRequest withCount(int newCount) {
		return new SolidifiedRequest(matcher, newCount);
	}
}