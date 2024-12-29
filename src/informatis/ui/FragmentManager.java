package informatis.ui;

import informatis.ui.sidebar.WindowManager;

import static arc.Core.scene;
import static mindustry.Vars.ui;

public class FragmentManager {
    public static QuickSchemFragment quickSchemFragment;
    public static SidebarSwitcher sidebarSwitcherFragment;

    public static void init() {
        sidebarSwitcherFragment = new SidebarSwitcher(
                    WindowManager.body
            );
    }
}
