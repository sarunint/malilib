package fi.dy.masa.malilib.gui;

import javax.annotation.Nullable;

public class TabbedScreenState
{
    @Nullable public ScreenTab currentTab;
    public int visibleTabsStartIndex;

    public TabbedScreenState(@Nullable ScreenTab currentTab)
    {
        this.currentTab = currentTab;
    }
}
