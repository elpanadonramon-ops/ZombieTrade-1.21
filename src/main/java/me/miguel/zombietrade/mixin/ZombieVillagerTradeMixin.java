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
import net.minecraft.village.Merchant;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieVillagerEntity.class)
public abstract class ZombieVillagerTradeMixin extends ZombieEntity {

    public ZombieVillagerTradeMixin(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void onInteract(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (itemStack.isOf(Items.EMERALD)) {
            if (!this.getWorld().isClient) {
                // EXPLICACIÓN: Casteamos el 'this' (que es un zombi) a la interfaz Merchant 
                // que es la que sí tiene los métodos de tradeo en la 1.21.11
                Object self = (Object) this;
                
                if (self instanceof Merchant merchant) {
                    TradeOfferList offers = merchant.getOffers();
                    
                    if (offers != null && !offers.isEmpty()) {
                        player.sendTradeOffers(
                            player.currentScreenHandler.syncId, 
                            offers, 
                            1, 
                            merchant.getExperience(), 
                            true, 
                            false
                        );
                    } else {
                        player.sendMessage(Text.literal("§cEste zombi no tiene nada que vender todavía..."), true);
                    }
                }
            }
            cir.setReturnValue(ActionResult.success(this.getWorld().isClient));
        }
    }
}
