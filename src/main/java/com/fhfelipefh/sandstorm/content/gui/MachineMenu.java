package com.fhfelipefh.sandstorm.content.gui;

public interface MachineMenu {
    int getEnergy();
    int getMaxEnergy();
    int getProgress();
    int getMaxProgress();
    boolean isWptConnected();
    boolean isProcessing();
    int getEnergyScaled(int pixels);
    int getProgressScaled(int pixels);
}
