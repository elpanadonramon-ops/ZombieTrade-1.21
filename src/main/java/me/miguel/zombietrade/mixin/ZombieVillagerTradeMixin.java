package me.miguel.zombietrade.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.village.Merchant;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieVillagerEntity.class)
public abstract class ZombieVillagerTradeMixin extends ZombieEntity implements Merchant {

    @Shadow public abstract TradeOfferList getOffers();
    @Shadow public abstract void setOffers(TradeOfferList offers);
    @Shadow public abstract void trade(TradeOffer offer);
    @Shadow public abstract void onTrade(TradeOffer offer);
    @Shadow public abstract int getExperience();
    @Shadow public abstract void setExperience(int experience);

    public ZombieVillagerTradeMixin(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void onInteract(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack itemStack = player.getStackInHand(hand);

        // Si el jugador tiene una esmeralda
        if (itemStack.isOf(Items.EMERALD)) {
            if (!this.getWorld().isClient) {
                TradeOfferList offers = this.getOffers();
                
                if (offers != null && !offers.isEmpty()) {
                    // Abrimos la interfaz usando el método de Merchant
                    player.sendTradeOffers(
                        player.currentScreenHandler.syncId, 
                        offers, 
                        1, 
                        this.getExperience(), 
                        true, 
                        false
                    );
                } else {
                    player.sendMessage(net.minecraft.text.Text.literal("§cEste zombi no tiene nada que vender..."), true);
                }
            }
            // Importante: SUCCESS para que la mano haga la animación y el juego sepa que pasó algo
            cir.setReturnValue(ActionResult.success(this.getWorld().isClient));
        }
    }

    // Métodos obligatorios de la interfaz Merchant que hay que implementar 
    // para que el juego no crashee al abrir la interfaz
    @Override public void setCustomer(@Nullable PlayerEntity customer) {}
    @Nullable @Override public PlayerEntity getCustomer() { return null; }
    @Override public void sendOffers(PlayerEntity player, net.minecraft.text.Text name, int levelProgress) {}
    @Override public boolean isClient() { return this.getWorld().isClient; }
    @Override public SoundEvent getYesSound() { return net.minecraft.sound.SoundEvents.ENTITY_VILLAGER_YES; }
    @Override public boolean canRefreshTrades() { return true; }
}
