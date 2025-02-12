package malilib.gui.config;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

import malilib.config.option.ConfigInfo;
import malilib.gui.BaseListScreen;
import malilib.gui.util.GuiUtils;
import malilib.gui.widget.list.ConfigOptionListWidget;
import malilib.util.data.ModInfo;

public class BaseConfigGroupEditScreen extends BaseListScreen<ConfigOptionListWidget<? extends ConfigInfo>>
{
    protected final ArrayList<ConfigInfo> configs = new ArrayList<>();
    protected final ModInfo modInfo;
    @Nullable protected KeybindEditScreen keyBindEditScreen;
    protected int elementsWidth = 200;

    public BaseConfigGroupEditScreen(ModInfo modInfo, @Nullable Runnable saveListener)
    {
        super(8, 30, 14, 36);

        this.modInfo = modInfo;
        this.shouldCenter = true;
        this.renderBorder = true;
        this.useTitleHierarchy = false;
        this.backgroundColor = 0xFF000000;
        this.screenWidth = Math.min(350, GuiUtils.getScaledWindowWidth() - 40);
        this.screenHeight = GuiUtils.getScaledWindowHeight() - 90;

        if (saveListener != null)
        {
            this.addPreScreenCloseListener(saveListener);
        }
    }

    public void setConfigs(List<? extends ConfigInfo> configs)
    {
        this.configs.clear();
        this.configs.addAll(configs);
    }

    protected List<? extends ConfigInfo> getConfigs()
    {
        return this.configs;
    }

    protected int getElementsWidth()
    {
        return this.elementsWidth;
    }

    @Nullable
    protected KeybindEditScreen getKeybindEditingScreen()
    {
        return this.keyBindEditScreen;
    }

    @Override
    protected ConfigOptionListWidget<? extends ConfigInfo> createListWidget()
    {
        ConfigWidgetContext ctx = new ConfigWidgetContext(this::getListWidget, this.getKeybindEditingScreen(), 0);
        return ConfigOptionListWidget.createWithExpandedGroups(this::getElementsWidth, this.modInfo, this::getConfigs, ctx);
    }
}
