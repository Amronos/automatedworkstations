package com.amronos.automatedworkstations.mixin;

import com.amronos.automatedworkstations.block.entity.CommonSmitherBlockEntity;
import com.amronos.automatedworkstations.inventory.CommonSmitherMenu;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.protocol.game.ServerboundContainerSlotStateChangedPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class MixinServerGamePacketListenerImpl {

    @Shadow
    public ServerPlayer player;

    @Inject(at = @At("TAIL"), method = "handleContainerSlotStateChanged")
    public void handleContainerSlotStateChanged(CallbackInfo info, @Local(argsOnly = true) ServerboundContainerSlotStateChangedPacket pPacket) {
        if (!this.player.isSpectator() && pPacket.containerId() == this.player.containerMenu.containerId) {
            if (this.player.containerMenu instanceof CommonSmitherMenu smithermenu && smithermenu.getContainer() instanceof CommonSmitherBlockEntity smitherblockentity) {
                smitherblockentity.setSlotState(pPacket.slotId(), pPacket.newState());
            }
        }
    }
}
