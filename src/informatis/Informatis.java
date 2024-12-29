package informatis;

import informatis.ui.WindowManager;
import mindustry.Vars;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import arc.*;

public class Informatis extends Mod {
    @Override
    public void init(){
        Events.on(ClientLoadEvent.class, e -> {
            WindowManager.init();
            Vars.ui.hudGroup.fill(t -> {
                t.add(WindowManager.body);
                t.top();
                t.x = 300;
            });
        });
    }
}
