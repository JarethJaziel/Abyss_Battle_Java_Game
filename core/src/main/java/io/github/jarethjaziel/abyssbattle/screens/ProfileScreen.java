package io.github.jarethjaziel.abyssbattle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import io.github.jarethjaziel.abyssbattle.AbyssBattle;
import io.github.jarethjaziel.abyssbattle.database.entities.Stats;
import io.github.jarethjaziel.abyssbattle.database.entities.User;
import io.github.jarethjaziel.abyssbattle.database.systems.PlayerStatsSystem;
import io.github.jarethjaziel.abyssbattle.gameutil.manager.SessionManager;

public class ProfileScreen extends ScreenAdapter {

    private final AbyssBattle game;
    private Stage stage;
    private Texture background;
    private User currentUser;
    private PlayerStatsSystem statsSystem;

    public ProfileScreen(AbyssBattle game) {
        this.game = game;
        this.statsSystem = new PlayerStatsSystem(game.getDbManager());
        this.currentUser = SessionManager.getInstance().getCurrentUser();
        
        stage = new Stage(new ScreenViewport());
        background = new Texture("images/MenuBackGround.png");
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        VisTable root = new VisTable();
        root.setFillParent(true);
        root.center();

        // 1. Título
        Label title = new Label("PERFIL DE JUGADOR", VisUI.getSkin());
        title.setFontScale(2f);
        title.setColor(Color.CYAN);
        root.add(title).padBottom(30).colspan(2).row();

        // 2. Información del Usuario
        Label userLabel = new Label("Comandante: " + currentUser.getUsername(), VisUI.getSkin());
        userLabel.setFontScale(1.5f);
        root.add(userLabel).padBottom(20).colspan(2).row();

        // 3. Obtener Stats de la DB
        // Nota: Asegúrate de que User tenga el objeto Stats cargado o búscalo manualmente
        Stats stats = statsSystem.getStats(currentUser); 
        
        // Panel de Estadísticas (Grid)
        VisTable statsTable = new VisTable();
        statsTable.setBackground(VisUI.getSkin().newDrawable("white", new Color(0, 0, 0, 0.6f))); // Fondo semitransparente
        statsTable.pad(30);

        if (stats != null) {
            addStatRow(statsTable, "Partidas Jugadas:", "" + stats.getPlayed(), Color.WHITE);
            addStatRow(statsTable, "Victorias:", "" + stats.getWon(), Color.GREEN);
            addStatRow(statsTable, "Derrotas:", "" + stats.getLost(), Color.RED);
            addStatRow(statsTable, "Daño Total:", "" + stats.getDamageTotal(), Color.YELLOW);
            addStatRow(statsTable, "Precisión (Hits):", "" + stats.getHits(), Color.CYAN);
        } else {
            statsTable.add(new Label("No hay estadísticas disponibles.", VisUI.getSkin())).row();
        }

        root.add(statsTable).width(600).padBottom(30).colspan(2).row();

        // 4. Botón de Ranking (Futuro)
        VisTextButton rankingBtn = new VisTextButton("Ver Ranking Global (Próximamente)");
        rankingBtn.setDisabled(true);
        root.add(rankingBtn).width(300).padBottom(20).colspan(2).row();

        // 5. Botón Volver
        VisTextButton backBtn = new VisTextButton("Regresar");
        backBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
        root.add(backBtn).width(200).colspan(2);

        stage.addActor(root);
    }

    private void addStatRow(VisTable table, String label, String value, Color valueColor) {
        Label lbl = new Label(label, VisUI.getSkin());
        Label val = new Label(value, VisUI.getSkin());
        val.setColor(valueColor);
        
        table.add(lbl).left().padRight(20);
        table.add(val).right().row();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        stage.getBatch().begin();
        stage.getBatch().draw(background, 0,0, stage.getWidth(), stage.getHeight());
        stage.getBatch().end();
        
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        background.dispose();
    }
}