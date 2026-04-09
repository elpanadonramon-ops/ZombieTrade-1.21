package me.miguel.zombietrade.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieVillagerEntity.class)
public abstract class ZombieVillagerTradeMixin extends ZombieEntity {
    
    // Estos @Shadow le dicen a Minecraft: "Yo sé que estos métodos existen dentro del objeto real"
    @Shadow public abstract void setVillagerData(VillagerData villagerData);
    @Shadow public abstract VillagerData getVillagerData();
    @Shadow public abstract TradeOfferList getOffers(); // ESTE FALTABA
    @Shadow public abstract int getExperience(); // ESTE TAMBIÉN

    public ZombieVillagerTradeMixin(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void onInteract(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (!this.getWorld().isClient) {
            // "this" ya es el objeto, pero lo casteamos para acceder a sus métodos
            ZombieVillagerEntity self = (ZombieVillagerEntity)(Object)this;

            // 1. LÓGICA DE PROFESIÓN
            if (self.getVillagerData().getProfession() == VillagerProfession.NONE) {
                // Aquí podrías añadir lógica para que busque una mesa, 
                // por ahora solo enviamos el mensaje.
            }

            // 2. ABRIR INTERFAZ DE TRADEO
            // Obtenemos la lista de ofertas
            TradeOfferList offers = this.getOffers();

            if (offers != null && !offers.isEmpty()) {
                player.sendTradeOffers(
                    this.getExperience(), 
                    offers, 
                    this.getVillagerData().getLevel(), 
                    this.getExperience(), 
                    true, 
                    true
                );
                
                this.playSound(net.minecraft.sound.SoundEvents.ENTITY_ZOMBIE_VILLAGER_AMBIENT, 1.0f, 0.8f);
                cir.setReturnValue(ActionResult.SUCCESS);
            } else {
                player.sendMessage(net.minecraft.text.Text.literal("§7Este zombi aún no tiene un oficio... ponle una mesa cerca."), true);
                cir.setReturnValue(ActionResult.CONSUME);
            }
        }
    }
}
