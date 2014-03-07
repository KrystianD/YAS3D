package pl.jakd.tg_project;

import java.util.Random;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Quaternion;


public class TGProject implements ApplicationListener {
	public PerspectiveCamera cam;
	public ModelBatch modelBatch;
	public Model model;
	public ModelInstance instance;
	public Environment environment;
	public CameraInputController camController;
	public BitmapFont font;
	public SpriteBatch batch;
	
	private Quaternion quat = new Quaternion();
    
	@Override
	public void create() {
		font = new BitmapFont();
		batch = new SpriteBatch();
		modelBatch = new ModelBatch();

		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(3f, 3f, 3f);
		cam.lookAt(0,0,0);
		cam.near = 0.1f;
		cam.far = 300f;
		cam.update();
		
		camController = new CameraInputController(cam);
		Gdx.input.setInputProcessor(camController);

        ModelBuilder modelBuilder = new ModelBuilder();
        model = modelBuilder.createBox(1f, 1f, 1f, new Material(ColorAttribute.createDiffuse(Color.GREEN)), Usage.Position | Usage.Normal);
        instance = new ModelInstance(model);
        
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
	}

	@Override
	public void dispose() {
		modelBatch.dispose();
		model.dispose();
	}

	@Override
	public void pause() {

	}

	@Override
	public void render() {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        
        quat.setEulerAngles(Gdx.input.getAzimuth(), Gdx.input.getPitch(), Gdx.input.getRoll());
        instance.transform.idt();
        instance.transform.rotate(quat);
        
        batch.begin();
		font.drawMultiLine(batch, getOrientationString(), 20, Gdx.graphics.getHeight() - 10);
		batch.end();
        
        
        modelBatch.begin(cam);
        modelBatch.render(instance, environment);
        modelBatch.end();
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void resume() {

	}
	
	private String getOrientationString() {
		StringBuilder builder = new StringBuilder();		
		builder.append("\nazimuth: ");
		builder.append((int)Gdx.input.getAzimuth());
		builder.append("\npitch: ");
		builder.append((int)Gdx.input.getPitch());
		builder.append("\nroll: ");
		builder.append((int)Gdx.input.getRoll());
		return builder.toString();
	}
}