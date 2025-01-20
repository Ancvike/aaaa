package informatis;

import informatis.core.setting.SharSettingUI;
import informatis.core.UpdateChecker;
import informatis.ui.fragments.sidebar.windows.tools.draws.OverDrawManager;
import informatis.ui.fragments.sidebar.windows.tools.tools.ToolManager;
import informatis.ui.fragments.sidebar.dialogs.DialogManager;
import informatis.ui.fragments.sidebar.windows.*;
import informatis.ui.fragments.FragmentManager;
import informatis.core.ModMetadata;

import mindustry.game.EventType.*;
import mindustry.mod.*;
import arc.*;

public class Informatis extends Mod {
    @Override
    public void init(){
        Events.on(ClientLoadEvent.class, e -> {
            WindowManager.init();
            ToolManager.init();
        });
    }
}
