package informatis;

import arc.Events;
import informatis.ui.fragments.sidebar.windows.WindowManager;
import informatis.ui.fragments.sidebar.windows.tools.tools.ToolManager;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.mod.Mod;

public class Informatis extends Mod {
    @Override
    public void init(){
        Events.on(ClientLoadEvent.class, e -> {
            WindowManager.init();
            ToolManager.init();
        });
    }
}
