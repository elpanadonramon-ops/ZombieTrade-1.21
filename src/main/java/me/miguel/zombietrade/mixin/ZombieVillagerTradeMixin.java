package me.miguel.zombietrade.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieVillagerEntity.class)
public abstract class ZombieVillagerTradeMixin extends ZombieEntity {

    public ZombieVillagerTradeMixin(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    // Interfaz interna para acceder a los datos privados del zombi
    @Mixin(ZombieVillagerEntity.class)
    public interface ZombieVillagerAccessor {
        @Accessor("offers")
        TradeOfferList getOffers();

        @Accessor("experience")
        int getExperience();
    }

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void onInteract(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (itemStack.isOf(Items.EMERALD)) {
            ZombieVillagerEntity self = (ZombieVillagerEntity)(Object)this;
            
            // Usamos la interfaz interna
            TradeOfferList offers = ((ZombieVillagerAccessor)self).getOffers();
            int exp = ((ZombieVillagerAccessor)self).getExperience();

            if (!this.getWorld().isClient) {
                if (offers != null && !offers.isEmpty()) {
                    player.sendTradeOffers(
                        player.currentScreenHandler.syncId, 
                        offers, 
                        1, 
                        exp, 
                        true, 
                        false
                    );
                } else {
                    player.sendMessage(Text.literal("§cEste zombi no tiene nada que vender todavía..."), true);
                }
            }
            cir.setReturnValue(ActionResult.success(this.getWorld().isClient));
        }
    }
}
