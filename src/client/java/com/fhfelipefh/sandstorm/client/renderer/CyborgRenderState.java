package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgRoutine;
import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgSpecialty;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class CyborgRenderState extends LivingEntityRenderState {
    public CyborgRoutine routine = CyborgRoutine.AUTONOMOUS_WORK;
    public int visorColor = 0;
    public CyborgSpecialty specialty = CyborgSpecialty.EXCAVATOR;
    public boolean isWorking = false;
    public float animationTicks = 0.0f;
}
