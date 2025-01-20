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
import arc.input.*;
import arc.math.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.ui.*;
import mindustry.world.*;

import static mindustry.Vars.*;

public class MapEditorWindow extends Window {
    TextField search;
    EditorTool tool;
    final Vec2[][] brushPolygons = new Vec2[MapEditor.brushSizes.length][0];
    float heat;
    float brushSize = -1;

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
            if (tile == null || tool == null || brushSize < 1) return;

            int index = 0;
            for (int i = 0; i < MapEditor.brushSizes.length; i++) {
                if (brushSize == MapEditor.brushSizes[i]) {
                    index = i;
                    break;
                }
            }
            Lines.stroke(Scl.scl(2f), Pal.accent);

            if ((!drawBlock.isMultiblock() || tool == EditorTool.eraser) && tool != EditorTool.fill) {
                if (tool == EditorTool.line && drawing) {
                    Lines.poly(brushPolygons[index], lastx, lasty, scaling);
                    Lines.poly(brushPolygons[index], tile.x * 8, tile.y * 8, scaling);
                }

                if ((tool.edit || (tool == EditorTool.line && !drawing)) && (!mobile || drawing)) {
                    if (tool == EditorTool.pencil && tool.mode == 1) {
                        Lines.square(tile.x * 8, tile.y * 8, scaling * (brushSize + 0.5f));
                    } else {
                        Lines.poly(brushPolygons[index], tile.x * 8 - 4, tile.y * 8 - 4, scaling);
                    }
                }
            } else {
                if ((tool.edit || tool == EditorTool.line) && (!mobile || drawing)) {
                    float offset = (drawBlock.size % 2 == 0 ? scaling / 2f : 0f);
                    Lines.square(
                            tile.x * 8 + scaling / 2f + offset,
                            tile.y * 8 + scaling / 2f + offset,
                            scaling * drawBlock.size / 2f);
                }
            }
        });

        Events.run(EventType.Trigger.update, () -> {

            //TODO make it more responsive, time -> width delta detect
            heat += Time.delta;
            if (heat >= 60f) {
                heat = 0f;

                lastw = width;
                lasth = height;
            }

            Tile tile = world.tileWorld(Core.input.mouseWorldX(), Core.input.mouseWorldY());
            if (tile == null || tool == null || brushSize < 1 || drawBlock == null || hasMouse()) return;
            if (Core.input.isTouched()) {
                if ((tool == EditorTool.line && drawing) || (!mobile && !Core.input.keyDown(KeyCode.mouseLeft))) return;
                drawing = true;
                lastx = tile.x;
                lasty = tile.y;
                tool.touched(lastx, lasty);
            } else {
                if (tool == EditorTool.line && drawing) tool.touchedLine(lastx, lasty, tile.x, tile.y);
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

        ObjectMap<Drawable, Element> displays = new ObjectMap<>();
        displays.put(Icon.map, new Table(display -> {
            display.table(t -> {
                t.left().background(Tex.underline2);
                t.label(() -> drawBlock == null ? "[gray]None[]" : "[accent]" + drawBlock.localizedName + "[] " + drawBlock.emoji());
                t.add(search).growX().pad(8).name("search");
            }).growX().row();
            display.pane(Styles.noBarPane, new Table()).grow().name("editor-pane").get().setScrollingDisabled(true, false);
            display.row();
        }));
        displays.put(Icon.settings, new Table(display -> {
            display.pane(Styles.noBarPane, new Table()).grow().name("rule-pane").get().setScrollingDisabled(true, false);
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
}