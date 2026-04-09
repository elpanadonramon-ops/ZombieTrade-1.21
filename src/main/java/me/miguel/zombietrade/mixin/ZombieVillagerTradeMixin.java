package me.miguel.zombietrade.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.village.SimpleMerchant;
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
            ZombieVillagerEntity self = (ZombieVillagerEntity)(Object)this;
            // Usamos el Accessor para sacar las ofertas sin que el Mixin se rompa
            TradeOfferList offers = ((ZombieVillagerEntityAccessor)self).getOffers();
            int exp = ((ZombieVillagerEntityAccessor)self).getExperience();

            if (!this.getWorld().isClient && offers != null && !offers.isEmpty()) {
                SimpleMerchant merchant = new SimpleMerchant(player);
                merchant.setOffers(offers);
                player.sendTradeOffers(player.currentScreenHandler.syncId, offers, 1, exp, true, false);
                cir.setReturnValue(ActionResult.SUCCESS);
            } else if (!this.getWorld().isClient) {
                player.sendMessage(net.minecraft.text.Text.literal("§cEste zombi no tiene nada que vender..."), true);
                cir.setReturnValue(ActionResult.SUCCESS);
            }
        }
    }
}
