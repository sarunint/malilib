package malilib.gui.config;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

import malilib.MaLiLibConfigs;
import malilib.config.ConfigManagerImpl;
import malilib.config.option.ConfigInfo;
import malilib.gui.BaseListScreen;
import malilib.gui.BaseScreen;
import malilib.gui.tab.ScreenTab;
import malilib.gui.util.GuiUtils;
import malilib.gui.widget.button.GenericButton;
import malilib.gui.widget.button.KeyBindConfigButton;
import malilib.gui.widget.list.ConfigOptionListWidget;
import malilib.input.Keys;
import malilib.listener.EventListener;
import malilib.registry.Registry;
import malilib.util.data.ModInfo;

public class BaseConfigScreen extends BaseListScreen<ConfigOptionListWidget<? extends ConfigInfo>> implements ConfigScreen, KeybindEditScreen
{
    protected final ModInfo modInfo;
    @Nullable protected EventListener configSaveListener;
    @Nullable protected KeyBindConfigButton activeKeyBindButton;
    protected int configElementsWidth = 120;

    public BaseConfigScreen(ModInfo modInfo,
                            List<? extends ScreenTab> configTabs,
                            @Nullable ConfigTab defaultTab,
                            String titleKey, Object... args)
    {
        super(10, 46, 20, 62, modInfo.getModId(), configTabs, defaultTab);

        this.modInfo = modInfo;
        this.shouldRestoreScrollbarPosition = MaLiLibConfigs.Generic.REMEMBER_CONFIG_TAB_SCROLL_POSITIONS.getBooleanValue();

        this.addPreScreenCloseListener(this::saveConfigsOnScreenClose);
        this.createSwitchModConfigScreenDropDown(modInfo);
        this.setTitle(titleKey, args);
    }

    protected void saveConfigsOnScreenClose()
    {
        if (((ConfigManagerImpl) Registry.CONFIG_MANAGER).saveIfDirty())
        {
            this.onSettingsChanged();
        }
    }

    @Override
    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        if (this.activeKeyBindButton != null)
        {
            this.activeKeyBindButton.onKeyTyped(keyCode, scanCode, modifiers);
            return true;
        }
        else
        {
            if (this.getListWidget().onKeyTyped(keyCode, scanCode, modifiers))
            {
                return true;
            }

            if (keyCode == Keys.KEY_ESCAPE && this.getParent() != GuiUtils.getCurrentScreen())
            {
                BaseScreen.openScreen(this.getParent());
                return true;
            }

            return super.onKeyTyped(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (super.onMouseClicked(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        // When clicking on not-a-button, clear the selection
        if (this.activeKeyBindButton != null && mouseButton == 0)
        {
            this.setActiveKeyBindButton(null);
            return true;
        }

        return false;
    }

    public void setConfigSaveListener(@Nullable EventListener configSaveListener)
    {
        this.configSaveListener = configSaveListener;
    }

    public int getDefaultConfigElementWidth()
    {
        ScreenTab tab = this.getCurrentTab();

        if (tab instanceof ConfigTab)
        {
            return ((ConfigTab) tab).getConfigWidgetsWidth();
        }

        return this.configElementsWidth;
    }

    /**
     * Sets the requested config elements width for this screen.
     * Use -1 to indicate automatic/default width decided by the widgets.
     */
    public BaseConfigScreen setConfigElementsWidth(int configElementsWidth)
    {
        this.configElementsWidth = configElementsWidth;
        return this;
    }

    @Override
    public List<? extends ConfigInfo> getConfigs()
    {
        ScreenTab tab = this.getCurrentTab();

        if (tab instanceof ConfigTab)
        {
            return ((ConfigTab) tab).getConfigs();
        }

        return Collections.emptyList();
    }

    @Override
    public ModInfo getModInfo()
    {
        return this.modInfo;
    }

    @Override
    public void switchToTab(ScreenTab tab)
    {
        this.saveScrollBarPositionForCurrentTab();

        super.switchToTab(tab);

        this.restoreScrollBarPositionForCurrentTab();
        this.reCreateConfigWidgets();
    }

    public void reCreateConfigWidgets()
    {
        for (GenericButton tabButton : this.tabButtons)
        {
            tabButton.updateButtonState();
        }

        ConfigOptionListWidget<?> listWidget = this.getListWidget();

        if (listWidget != null)
        {
            listWidget.refreshEntries();
        }
    }

    protected void onSettingsChanged()
    {
        Registry.HOTKEY_MANAGER.updateUsedKeys();

        if (this.configSaveListener != null)
        {
            this.configSaveListener.onEvent();
        }
    }

    @Override
    protected ConfigOptionListWidget<? extends ConfigInfo> createListWidget()
    {
        ConfigWidgetContext ctx = new ConfigWidgetContext(this::getListWidget, this, 0);
        ConfigOptionListWidget<? extends ConfigInfo> widget = ConfigOptionListWidget.createWithExpandedGroups(
                this::getDefaultConfigElementWidth, this.modInfo, this::getConfigs, ctx);

        widget.addConfigSearchBarWidget(this);

        return widget;
    }

    @Override
    protected void clearElements()
    {
        super.clearElements();
        this.clearOptions();
    }

    @Override
    public void clearOptions()
    {
        this.setActiveKeyBindButton(null);
    }

    @Override
    public void setActiveKeyBindButton(@Nullable KeyBindConfigButton button)
    {
        if (this.activeKeyBindButton != null)
        {
            this.activeKeyBindButton.onClearSelection();
        }

        this.activeKeyBindButton = button;

        if (this.activeKeyBindButton != null)
        {
            this.activeKeyBindButton.onSelected();
        }
    }
}
