package informatis.ui;

import arc.graphics.g2d.*;
import arc.math.geom.*;
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

    boolean drawing;
    int lastx, lasty;

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
                if (!mobile || drawing) {
                    Lines.poly(brushPolygons[2], tile.x * 8 - 4, tile.y * 8 - 4, scaling);
                }
            }
        });

        Events.run(EventType.Trigger.update, () -> {
            Tile tile = world.tileWorld(Core.input.mouseWorldX(), Core.input.mouseWorldY());
            if (tile == null || tool == null|| drawBlock == null || hasMouse()) return;
            if (Core.input.isTouched()) {
                if (!mobile && !Core.input.keyDown(KeyCode.mouseLeft)) return;
                drawing = true;
                lastx = tile.x;
                lasty = tile.y;
            } else {
                drawing = false;
                lastx = -1;
                lasty = -1;
            }
        });
    }

    @Override
    public void buildBody(Table table) {

        table.left();
        table.top().background(Styles.black8);

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
        }).left().width(100).margin(8f).growY();

    }

    enum EditorTool {
        eraser() {

        }
    }
}
