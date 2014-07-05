package vswe.stevesvehicles.module.data;

import vswe.stevesvehicles.localization.entry.info.LocalizationLabel;
import vswe.stevesvehicles.module.ModuleBase;
import vswe.stevesvehicles.old.Helpers.ColorHelper;

import java.util.List;


public class ModuleDataHull extends ModuleData {

    private int modularCapacity;
    private int engineMaxCount;
    private int addonMaxCount;
    private int complexityMax;

    public ModuleDataHull(String unlocalizedName, Class<? extends ModuleBase> moduleClass, int modularCapacity, int engineMaxCount, int addonMaxCount, int complexityMax) {
        super(unlocalizedName, moduleClass, 0);
        this.modularCapacity = modularCapacity;
        this.engineMaxCount = engineMaxCount;
        this.addonMaxCount = addonMaxCount;
        this.complexityMax = complexityMax;
    }

    public int getModularCapacity() {
        return modularCapacity;
    }
    public int getEngineMaxCount() {
        return engineMaxCount;
    }
    public int getAddonMaxCount() {
        return addonMaxCount;
    }
    public int getComplexityMax() {
        return complexityMax;
    }

    @Override
    public void addSpecificInformation(List<String> list) {
        list.add(ColorHelper.YELLOW + LocalizationLabel.CAPACITY.translate(String.valueOf(modularCapacity)));
        list.add(ColorHelper.PURPLE + LocalizationLabel.COMPLEXITY_CAP.translate(String.valueOf(complexityMax)));
        list.add(ColorHelper.ORANGE + LocalizationLabel.MAX_ENGINES.translate(String.valueOf(engineMaxCount)));
        list.add(ColorHelper.GREEN + LocalizationLabel.MAX_ADDONS.translate(String.valueOf(addonMaxCount)));
    }

}
