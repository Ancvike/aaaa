package informatis;

import informatis.core.SharSettingUI;
import informatis.ui.FragmentManager;

import informatis.ui.sidebar.Window;
import informatis.ui.sidebar.WindowManager;
import mindustry.Vars;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import arc.*;

public class Informatis extends Mod {
    @Override
    public void init(){
        Events.on(ClientLoadEvent.class, e -> {
//            SharSettingUI.init();
            WindowManager.init();
//            FragmentManager.init();
            Vars.ui.hudGroup.fill(t -> {
                t.add(WindowManager.body);
            });
        });
    }
}
