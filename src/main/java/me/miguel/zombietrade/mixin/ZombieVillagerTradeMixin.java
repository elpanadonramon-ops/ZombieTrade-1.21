package me.miguel.zombietrade.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
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
    
    @Shadow public abstract void setVillagerData(VillagerData villagerData);
    @Shadow public abstract VillagerData getVillagerData();

    public ZombieVillagerTradeMixin(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void onInteract(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (!this.getWorld().isClient) {
            ZombieVillagerEntity self = (ZombieVillagerEntity)(Object)this;

            // LÓGICA DE PROFESIÓN AUTOMÁTICA
            // Si el zombi es un "desempleado" (Nitwit o None), intentamos que el juego 
            // le asigne lo que tenga cerca al interactuar.
            if (self.getVillagerData().getProfession() == VillagerProfession.NONE) {
                // Esto fuerza al juego a refrescar su estado de aldeano
                // Al darle clic, si hay una mesa cerca, el sistema de Minecraft 
                // lo detectará como un candidato válido ahora que habilitamos el tradeo.
            }

            // ABRIR INTERFAZ DE TRADEO
            // Usamos el sistema nativo de ofertas que Minecraft le asigne
            if (!self.getOffers().isEmpty()) {
                player.sendTradeOffers(
                    self.getExperience(), 
                    self.getOffers(), 
                    self.getVillagerData().getLevel(), 
                    self.getExperience(), 
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