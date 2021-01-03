package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class ControlStage {

    private Stage stage;
    private Skin skin;
    
    public ControlStage(float x, float y) {
        stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		skin = new Skin();
		Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.fill();
        skin.add("white", new Texture(pixmap));

        BitmapFont defaultFont = new BitmapFont();
        //defaultFont.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
        defaultFont.getData().setScale(1.5f);
		skin.add("default", defaultFont);
		
		SliderStyle sliderStyle = new SliderStyle();
		sliderStyle.background = skin.newDrawable("white", Color.BLUE);
		sliderStyle.knob = skin.newDrawable("white", Color.RED);
		sliderStyle.knob.setMinWidth(30);
        sliderStyle.knob.setMinHeight(30);
        
        SelectBoxStyle selectBoxStyle = new SelectBoxStyle();
        selectBoxStyle.fontColor = Color.WHITE;
        selectBoxStyle.background = skin.newDrawable("white", Color.BLUE);
        selectBoxStyle.font = skin.getFont("default");
        selectBoxStyle.scrollStyle = new ScrollPaneStyle();
        selectBoxStyle.scrollStyle.background = skin.newDrawable("white", Color.LIGHT_GRAY);
        selectBoxStyle.listStyle = new ListStyle();
        selectBoxStyle.listStyle.font = skin.getFont("default");
        selectBoxStyle.listStyle.fontColorSelected = Color.ORANGE;
        selectBoxStyle.listStyle.fontColorUnselected = Color.GRAY;
        selectBoxStyle.listStyle.selection = skin.newDrawable("white", Color.BLACK);
        selectBoxStyle.listStyle.background = skin.newDrawable("white", Color.BLACK);
        
		Table table = new Table();
		table.setFillParent(true);
		stage.addActor(table);
        final Slider slider = new Slider(0, 10, 0.1f, false, sliderStyle);
        final Slider boidSlider = new Slider(0, 500, 1, false, sliderStyle);
        final SelectBox<BoidAttribute> selectBox = new SelectBox<BoidAttribute>(selectBoxStyle); 
        selectBox.setItems(BoidAttribute.WIDTH, BoidAttribute.HEIGHT, BoidAttribute.MAXSPEED, BoidAttribute.SEPARATIONSTRENGTH,
         BoidAttribute.SEPARATIONRANGE, BoidAttribute.ALIGNMENTSTRENGTH, BoidAttribute.ALIGNMENTRANGE, BoidAttribute.COHESIONSTRENGTH, BoidAttribute.COHESIONRANGE);
        
        table.add(slider);
        table.add(new Label("Select attribute to edit:", new LabelStyle(skin.getFont("default"), Color.WHITE)));
        table.add(selectBox);
        table.add(boidSlider);
        table.add(new Label("Edit amount of boids", new LabelStyle(skin.getFont("default"), Color.WHITE)));
        
        slider.setValue(5f);
        boidSlider.setValue(50f);
        Boids.setBoidAmount(Math.round(50f));
        boidSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Boids.setBoidAmount(Math.round(boidSlider.getValue()));
            }
        });

		slider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
                Boid.setAttr(selectBox.getSelected(), slider.getValue());
			}
        });

        selectBox.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
                System.out.println(selectBox.getSelected());
                float value = Boid.getAttr(selectBox.getSelected());
                value = value/Boid.getScale(selectBox.getSelected());
                slider.setValue(value);
                System.out.println(value);
			}
        });

        table.setPosition(x, y);
    }

    public void draw() {
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		stage.draw();
    }
    public void dispose() {
        stage.dispose();
    }
}
