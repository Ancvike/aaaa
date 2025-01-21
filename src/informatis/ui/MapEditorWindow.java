package informatis.ui;

import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.scene.*;
import arc.scene.style.*;
import arc.struct.*;
import mindustry.editor.*;
import mindustry.game.*;
import mindustry.graphics.*;
import arc.*;
import arc.func.*;
import arc.input.*;
import arc.math.*;
import arc.scene.event.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.ui.*;
import mindustry.world.*;

import static mindustry.Vars.*;

public class MapEditorWindow extends Window {
    EditorTool tool;
    final Vec2[][] brushPolygons = new Vec2[MapEditor.brushSizes.length][0];
    float heat;

    boolean drawing;
    int lastx, lasty;
    float lastw, lasth;

    public static Block drawBlock = Blocks.router;

    public MapEditorWindow() {
        super(Icon.map, "editor");
        height = 300;
        width = 300;

        for (int i = 0; i < MapEditor.brushSizes.length; i++) {
            float size = MapEditor.brushSizes[i];
            float mod = size % 1f;
            brushPolygons[i] = Geometry.pixelCircle(size, (index, x, y) -> Mathf.dst(x, y, index - mod, index - mod) <= size - 0.5f);
        }

        Events.run(EventType.Trigger.draw, () -> {
            float cx = Core.camera.position.x, cy = Core.camera.position.y;
            float scaling = 8;

            Draw.z(Layer.max);

            if (Core.settings.getBool("grid")) {
                Lines.stroke(1f);
                Draw.color(Pal.accent);
                for (int i = (int) (-0.5f * Core.camera.height / 8); i < (int) (0.5f * Core.camera.height / 8); i++) {
                    Lines.line(Mathf.floor((cx - 0.5f * Core.camera.width) / 8) * 8 + 4, Mathf.floor((cy + i * 8) / 8) * 8 + 4, Mathf.floor((cx + 0.5f * Core.camera.width) / 8) * 8 + 4, Mathf.floor((cy + i * 8) / 8) * 8 + 4);
                }
                for (int i = (int) (-0.5f * Core.camera.width / 8); i < (int) (0.5f * Core.camera.width / 8); i++) {
                    Lines.line(Mathf.floor((cx + i * 8) / 8) * 8 + 4, Mathf.floor((cy + 0.5f * Core.camera.height) / 8) * 8 + 4, Mathf.floor((cx + i * 8) / 8) * 8 + 4, Mathf.floor((cy - 0.5f * Core.camera.height) / 8) * 8 + 4);
                }
                Draw.reset();
            }

            Tile tile = world.tileWorld(Core.input.mouseWorldX(), Core.input.mouseWorldY());
            if (tile == null || tool == null) return;

            Lines.stroke(Scl.scl(2f), Pal.accent);

            if (!drawBlock.isMultiblock() || tool == EditorTool.eraser) {
                if (tool.edit && (!mobile || drawing)) {
                    Lines.poly(brushPolygons[2], tile.x * 8 - 4, tile.y * 8 - 4, scaling);
                }
            }
        });

//        Events.run(EventType.Trigger.update, () -> {
//
//            //TODO make it more responsive, time -> width delta detect
//            heat += Time.delta;
//            if (heat >= 60f) {
//                heat = 0f;
//
//                if (lastw != getWidth() || lasth != getHeight()) resetPane();
//                lastw = width;
//                lasth = height;
//            }
//
//            Tile tile = world.tileWorld(Core.input.mouseWorldX(), Core.input.mouseWorldY());
//            if (tile == null || tool == null|| drawBlock == null || hasMouse()) return;
//            if (Core.input.isTouched()) {
//                if (!mobile && !Core.input.keyDown(KeyCode.mouseLeft)) return;
//                drawing = true;
//                lastx = tile.x;
//                lasty = tile.y;
//            } else {
//                drawing = false;
//                lastx = -1;
//                lasty = -1;
//            }
//        });
    }

    @Override
    public void buildBody(Table table) {

        table.left();
        table.top().background(Styles.black8);

        ObjectMap<Drawable, Element> displays = new ObjectMap<>();
        displays.put(Icon.map, new Table(display -> {
            display.pane(Styles.noBarPane, rebuildEditor()).grow().name("editor-pane").get().setScrollingDisabled(true, false);
            display.row();
        }));

        table.table(buttons -> {
            buttons.top().left();

            displays.each((icon, display) -> buttons.button(icon, Styles.clearTogglei, () -> {
                if (table.getChildren().size > 1) table.getChildren().get(table.getChildren().size - 1).remove();
                table.add(display).grow();
            }).row());
        }).growY();
    }

    void resetPane() {
        ScrollPane pane = find("editor-pane");
        if (pane != null) pane.setWidget(rebuildEditor());
    }

    Table rebuildEditor() {
        return new Table(table -> {
            table.top();
            table.table(tools -> {
                tools.top().left();
                tools.table(title -> title.left().background(Tex.underline2).add("Tools [accent]" + (tool == null ? "" : tool.name()) + "[]")).growX().row();
                tools.table(bt -> {
                    Cons<EditorTool> addTool = tool -> {
                        ImageButton button = new ImageButton(ui.getIcon(tool.name()), Styles.clearTogglei);
                        button.clicked(() -> {
                            button.toggle();
                            if (this.tool == tool) this.tool = null;
                            else this.tool = tool;
                            resetPane();
                        });
                        button.update(() -> button.setChecked(this.tool == tool));

                        Label mode = new Label("");
                        mode.setColor(Pal.remove);
                        mode.update(() -> mode.setText(""));
                        mode.setAlignment(Align.bottomRight, Align.bottomRight);
                        mode.touchable = Touchable.disabled;

                        bt.stack(button, mode);
                    };

                    addTool.get(EditorTool.eraser);
                });
            }).left().width(getDisplayWidth() / 2).margin(8f).growY();
        });
    }

    float getDisplayWidth() {
        return getWidth() - (find("buttons") == null ? 1 : find("buttons").getWidth());
    }

    enum EditorTool {
        eraser("eraseores") {
            {
                edit = true;
            }
        };

        /**
         * All the internal alternate placement modes of this tool.
         */
        public final String[] altModes;
        /**
         * Whether this tool causes canvas changes when touched.
         */
        public boolean edit;

        EditorTool(String... altModes) {
            this.altModes = altModes;
        }
    }
}
