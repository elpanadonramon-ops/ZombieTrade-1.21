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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// IMPORTANTE: Este import le dice al Mixin dónde está el Accessor que creaste
import me.miguel.zombietrade.mixin.ZombieVillagerEntityAccessor;

@Mixin(ZombieVillagerEntity.class)
public abstract class ZombieVillagerTradeMixin extends ZombieEntity {

    public ZombieVillagerTradeMixin(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void onInteract(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (itemStack.isOf(Items.EMERALD)) {
            ZombieVillagerEntity self = (ZombieVillagerEntity)(Object)this;
            
            // Accedemos a los datos ocultos del zombi
            TradeOfferList offers = ((ZombieVillagerEntityAccessor)self).getOffers();
            int exp = ((ZombieVillagerEntityAccessor)self).getExperience();

            if (!this.getWorld().isClient) {
                if (offers != null && !offers.isEmpty()) {
                    // Abrimos la interfaz directamente con los datos del zombi
                    player.sendTradeOffers(
                        player.currentScreenHandler.syncId, 
                        offers, 
                        1, 
                        exp, 
                        true, 
                        false
                    );
                } else {
                    player.sendMessage(Text.literal("§cEste zombi no tiene nada que vender..."), true);
                }
            }
            // Retornamos SUCCESS para que el juego sepa que la esmeralda "activó" algo
            cir.setReturnValue(ActionResult.success(this.getWorld().isClient));
        }
    }
}
