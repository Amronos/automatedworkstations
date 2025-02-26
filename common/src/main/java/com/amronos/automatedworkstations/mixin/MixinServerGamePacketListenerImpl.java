package com.amronos.automatedworkstations.mixin;

import com.amronos.automatedworkstations.block.entity.CommonAutomatedWorkstationBlockEntity;
import com.amronos.automatedworkstations.inventory.CommonAutomatedWorkstationMenu;
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
            if (this.player.containerMenu instanceof CommonAutomatedWorkstationMenu automatedworkstationmenu && automatedworkstationmenu.getContainer() instanceof CommonAutomatedWorkstationBlockEntity automatedworkstationblockentity) {
                automatedworkstationblockentity.setSlotState(pPacket.slotId(), pPacket.newState());
            }
        }
    }
}
