package informatis.ui;

import arc.Core;
import arc.scene.Element;
import arc.scene.style.Drawable;
import arc.scene.ui.Label;
import arc.scene.ui.Slider;
import arc.scene.ui.TextField;
import arc.scene.ui.layout.Table;
import arc.scene.utils.Elem;
import arc.struct.ObjectMap;
import mindustry.gen.Groups;
import mindustry.gen.Icon;
import mindustry.ui.Styles;

public class MapEditorWindow extends Window {
    TextField search;

    public MapEditorWindow() {
        super(Icon.map, "editor");
        height = 300;
        width = 300;
    }

    @Override
    public void buildBody(Table table) {
        search = Elem.newField(null, f -> {
        });
        search.setMessageText(Core.bundle.get("players.search") + "...");

        table.left();
        table.top().background(Styles.black8);

        ObjectMap<Drawable, Element> displays = new ObjectMap<>();
        displays.put(Icon.settings, new Table(display -> {
            display.pane(Styles.noBarPane, rebuildRule()).grow().name("rule-pane").get().setScrollingDisabled(true, false);
            display.row();
        }));

//        table.table(buttons -> {
//            buttons.top().left();
//
//            displays.each((icon, display) -> buttons.button(icon, Styles.clearTogglei, () -> {
//                if (table.getChildren().size > 1) table.getChildren().get(table.getChildren().size - 1).remove();
//                table.add(display).grow();
//            }).row());
//        }).growY();
    }

    Table rebuildRule() {
        return new Table(table -> {
            table.top().left();

            table.table(rules -> {
                rules.top().left();

                Label label = rules.add("Block Health: ").get();
                Slider slider = new Slider(0, 100, 1, false);
                slider.changed(() -> label.setText("Block Health: " + (int) slider.getValue() + "%"));
                slider.change();
                slider.moved(hp -> Groups.build.each(b -> b.health(b.block.health * hp / 100)));
                rules.add(slider);
            }).grow();
        });
    }
}
