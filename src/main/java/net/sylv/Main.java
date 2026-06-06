package net.sylv;

import com.lcv.elverapi.apis.mojang.MojangSkinAPI;
import net.sylv.Objects.Player;
import net.sylv.Renderer.Renderer;
import net.sylv.Util.*;
import net.sylv.Objects.Cube;
import net.sylv.Objects.Obj;
import net.sylv.Objects.Quad;
import net.sylv.Util.Shaders.Shader;
import net.sylv.Util.Shaders.ShaderProgram;
import net.sylv.Util.Shaders.Uniform;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

public class Main {
	public static final Camera camera = new Camera();

	public static final Window window = new Window(camera);

	public static ShaderProgram basicProgram;

	public static ShaderProgram directProgram;

	public static ArrayList<Consumer<Double>> tasks = new ArrayList<>(8);

	public static Uniform modelUniform;

	public static Uniform viewUniform;

	public static Uniform projectionUniform;

	public static Renderer renderer;

	public static void main(String[] args) throws IOException {
		window.init();

		Texture noTex = new Texture((String) null, 0);
		noTex.setTexture(1,1, BufferUtils.createIntBuffer(1).put(0xFFFFFFFF).flip());

		renderer = new Renderer();

		// setup shaders & program
		Shader basicVertex = Shader.fromFile(GL_VERTEX_SHADER,"shaders/vertex.vs.glsl");
		Shader basicFragment = Shader.fromFile(GL_FRAGMENT_SHADER, "shaders/frag.fs.glsl");

		Shader directVertex = Shader.fromFile(GL_VERTEX_SHADER, "shaders/direct.vs.glsl");
		Shader directFragment = Shader.fromFile(GL_FRAGMENT_SHADER, "shaders/direct.fs.glsl");

		directProgram = new ShaderProgram(directVertex, directFragment);

		directVertex.delete();
		directFragment.delete();

		basicProgram = new ShaderProgram(basicVertex, basicFragment);

		basicVertex.delete();
		basicFragment.delete();

		Texture defaultTex = new Texture("default.png", 0);
		Texture birdTex = new Texture("bird.png", 0);

		Uniform texUniform = new Uniform(basicProgram, "tex");
		Uniform texUniform1 = new Uniform(basicProgram, "tex1");

		basicProgram.use();

		Uniform overlayUnif = new Uniform(basicProgram, "overlayOpacity").set(0.45f);

		texUniform.set(0);
		texUniform1.set(1);

		Matrix4f viewMatrix = new Matrix4f()
			  .identity()
			  .translation(0, 0, -3f);

		// handle input i guess?
		Mouse mouse = new Mouse();
		mouse.init(window);

		glfwSetKeyCallback(window.id, (long win, int key, int scan, int action, int mods) -> {
			if (action == GLFW_RELEASE) return;

			switch(key) {
				case GLFW_KEY_ESCAPE -> {
					System.exit(0);
				}

				case GLFW_KEY_LEFT_CONTROL -> {
					if (mouse.enabled) mouse.disableCursor();
					else mouse.enableCursor();
				}

				// TODO: read from color attachment?
				case GLFW_KEY_U -> {
					long now = System.nanoTime();
					int w = window.mainBuffer.width;
					int h = window.mainBuffer.height;
					int[] pixels = new int[w*h];

					//glReadBuffer(GL_BACK);
					//glReadPixels(0, 0, w, h, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, pixels);
					glBindTexture(GL_TEXTURE_2D, window.mainBuffer.renderColor);
					glGetTexImage(GL_TEXTURE_2D, 0, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, pixels);

					new Thread(() -> {
						BufferedImage im = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

						int i = 0;
						for (int y = h-1; y >= 0; y--) {
							for (int x = 0; x < w; x++, i++) {
								im.setRGB(x, y, pixels[i]);
							}
						}

						try {
							ImageIO.write(im, "png", new File("uwu_test.png"));
						} catch (IOException e) {
							throw new RuntimeException(e);
						}

						System.out.printf("wrote texture to %s!%n", new File("uwu_test.png").getAbsolutePath());
					}).start();
				}

//				case GLFW_KEY_UP -> {
//					System.out.println(overlayUnif.getF());
//					overlayUnif.set(overlayUnif.getF() + .02f);
//				}
//
//				case GLFW_KEY_DOWN -> {
//					System.out.println(overlayUnif.getF());
//					overlayUnif.set(overlayUnif.getF() - .02f);
//				}
			}
		});

		modelUniform = new Uniform(basicProgram, "model");
		viewUniform = new Uniform(basicProgram, "view");
		projectionUniform = new Uniform(basicProgram, "projection");

		Player player = new Player(ImageIO.read(Main.class.getResourceAsStream("/Skins/gaycat.png")), true);
		Player player1 = new Player(ImageIO.read(Main.class.getResourceAsStream("/Skins/syl.png")), true);
		Player player2 = new Player(ImageIO.read(Main.class.getResourceAsStream("/Skins/11a3c5525ba97661.png")), true);

		player.updatePosition(new Vector3f(0, 0, 0));
		player1.updatePosition(new Vector3f(-5, 0, 0));
		player2.updatePosition(new Vector3f(5, 0, 0));

		double delta = 1d/120;
		double last = glfwGetTime()-delta;

		Quad p1 = new Quad().setSize(2, 2, 2).setPosition(0, 0, 9);
		Quad p2 = new Quad().setSize(2, 2, 2).setPosition(0, 0, 10);

		p1.texture = 1;
		p2.texture = 2;

		Cube cx = new Cube();

		cx.setPosition(0, 8, 0);
		cx.setSize(2, 2, 2);

		new Thread(() -> {
			String skinName = System.getenv("player");
			if (skinName == null) return;

			MojangSkinAPI skinLk = new MojangSkinAPI(skinName);

			// NOTE: This WILL work.
			tasks.add((dt) -> {
				player.texture.bind();
				player.texture.setTexture(skinLk.getSkin());
				player.setSlim(skinLk.isSlim());
			});
		}).start();

		while (!glfwWindowShouldClose(window.id)) {
			double now = glfwGetTime();

			delta = now - last;
			last = now;

			window.glRenderPre();

			// do any tasks we need to do
			{
				int len = tasks.size();
				for (int i = 0; i < len; i++) {
					Consumer<Double> task = tasks.get(0);
					task.accept(delta);
					tasks.remove(0);
				}
			}

			// handlde camera movement
			float moveDelta = (float) (camera.speed*delta);

			Vector3f camPos = camera.position;
			Vector3f leftMoveVector = new Vector3f(camera.lookVector).cross(camera.upVector);

			Vector3f moveVector = new Vector3f();

			if (glfwGetKey(window.id, GLFW_KEY_S) == GLFW_PRESS) moveVector.add(camera.lookVector);
			if (glfwGetKey(window.id, GLFW_KEY_W) == GLFW_PRESS) moveVector.sub(camera.lookVector);

			if (glfwGetKey(window.id, GLFW_KEY_A) == GLFW_PRESS) moveVector.add(leftMoveVector);
			if (glfwGetKey(window.id, GLFW_KEY_D) == GLFW_PRESS) moveVector.sub(leftMoveVector);

			if (glfwGetKey(window.id, GLFW_KEY_SPACE) == GLFW_PRESS) moveVector.sub(camera.upVector);
			if (glfwGetKey(window.id, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) moveVector.add(camera.upVector);

			if (moveVector.length() != 0) {
				camPos.add(moveVector.normalize(moveDelta));
			}

			// now camera mouse input
			camera.yaw += (float) ((mouse.x - mouse.lastX)*mouse.sensitivity);
			camera.pitch = Math.clamp(-89.9f, 89.9f, camera.pitch - (float) ((mouse.y - mouse.lastY)*mouse.sensitivity));

			mouse.lastX = mouse.x;
			mouse.lastY = mouse.y;

			camera.updateLookVector();
			camera.updateLookMatrix(viewMatrix);

			//camera.updateViewMatrix(viewMatrix);

			//window.mainBuffer.bind();

			// uwu
			basicProgram.use();
			//directProgram.use();

			defaultTex.bind();
			birdTex.bind(1);

			// setup gl rendering state
			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

			glEnable(GL_DEPTH_TEST);
			glEnable(GL_CULL_FACE);

			//glViewport(0, 0, window.width*2, window.height*2);

			//basicVao1.bind();

			// update projection matrix
			camera.updateProjectionMatrix(window, projectionUniform);

			// Apply camera transforms
			viewUniform.set(viewMatrix, false);

			// render thing (This def shouldn't be defined inside the loop)
			ArrayList<Obj> depthObjects = new ArrayList<>(8);

			for (Player p : new Player[]{player, player1, player2}) {
				for (Cube c : p.inner) {
					if (c == null) break;

					c.draw(modelUniform);
				}

				for (Cube c : p.outer) {
					if (c == null) break;

					depthObjects.add(c);
				}
			}

			depthObjects.sort((a, b) -> {
				if (a.position.equals(b.position)) return 0;

				return camera.position.distance(a.position) < camera.position.distance(b.position) ? -1 : 1;
			});

			// disable back face culling for outer layer
			glDisable(GL_CULL_FACE);

			for (int i = depthObjects.size()-1; i >= 0; i--) {
				depthObjects.get(i).draw(modelUniform);
			}

			p1.draw(modelUniform);
			p2.draw(modelUniform);
			cx.draw(modelUniform);

			// !! draw hud or somethiing realistically
			int width = window.width;
			int height = window.height;

			//renderer = new Renderer();

			window.mainBuffer.draw(0, true);

			basicProgram.use();

			camera.updateOrthoMatrix(window, projectionUniform);

			viewUniform.set(new Matrix4f().identity(), false);
			modelUniform.set(new Matrix4f().identity(), false);

			renderer.startRendering();

			glDisable(GL_CULL_FACE);
			glDisable(GL_DEPTH_TEST);

			//glBindTexture(GL_TEXTURE_2D, bird);
			//defaultTex.bind();
			//noTex.bind();
			glBindTexture(GL_TEXTURE_2D, window.mainBuffer.renderColor);

			{
				float w = 196f;
				float h = w/((float) width /height);

				float x = 24f;
				float y = height-24f-h;

				renderer.pos(x, y, 0).col(1, 1, 1).tex(0, 1).upload();
				renderer.pos(x, y + h, 0).col(1, 1, 1).tex(0, 0).upload();
				renderer.pos(x + w, y + h, 0).col(1, 1, 1).tex(1, 0).upload();

				renderer.pos(x, y, 0).col(1, 1, 1).tex(0, 1).upload();
				renderer.pos(x + w, y, 0).col(1, 1, 1).tex(1, 1).upload();
				renderer.pos(x + w, y + h, 0).col(1, 1, 1).tex(1, 0).upload();
			}

			renderer.draw();

			window.glRenderPost();
		}

		System.out.println("bye");
		glfwTerminate();
	}
}