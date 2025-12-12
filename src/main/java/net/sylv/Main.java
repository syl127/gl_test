package net.sylv;

import com.lcv.elverapi.apis.mojang.MojangSkinAPI;
import net.sylv.Objects.Player;
import net.sylv.Util.*;
import net.sylv.Objects.Cube;
import net.sylv.Objects.Obj;
import net.sylv.Objects.Plane;
import net.sylv.Util.Shaders.Shader;
import net.sylv.Util.Shaders.ShaderProgram;
import net.sylv.Util.Shaders.Uniform;
import net.sylv.Util.Vertex.ColoredVertex;
import net.sylv.Util.Vertex.TexturedVertex;
import net.sylv.Util.Vertex.Vertex;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

public class Main {
	public static final Camera camera = new Camera();

	public static final Window window = new Window(camera);

	public static ShaderProgram basicProgram;

	public static ArrayList<Consumer<Double>> tasks = new ArrayList<>(8);

	public static void matrix(StringBuilder s, int size, Random r) {
		for (int i = 0; i < size; i++) {
			s.append("[  ");

			for (int j= 0; j < size; j++) {
				int rN = r.nextInt(1000000);
				s.append(rN).append("  ");
			}

			s.append("]");

			if (i != size-1) {
				s.append("\n");
			}
		}
	}

	public static void main(String[] args) throws IOException {
		window.init();

		Vertex[] vertices0 = {
			  new Vertex(0.0f, 0.0f, 0.0f),
			  new Vertex(-.5f, 0.0f, 0.0f),
			  new Vertex(0.5f, 0.0f, 0.0f),
			  new Vertex(0.0f, -.75f, 0.0f),
			  new Vertex(0.0f, 0.75f, 0.0f),
		};

		ColoredVertex[] vertices1 = {
			  new ColoredVertex(0.0f, 0.0f, 0.0f, 1f, 1f, 1f),
			  new ColoredVertex(-.5f, 0.0f, 0.0f, 1f, 1f, 0f),
			  new ColoredVertex(0.5f, 0.0f, 0.0f, 0f, 1f, 0f),
			  new ColoredVertex(0.0f, -.75f, 0.0f, 1f, 0f, 0f),
			  new ColoredVertex(0.0f, 0.75f, 0.0f, 0f, 0f, 1f),
		};

		TexturedVertex[] texturedTri = {
			  new TexturedVertex(-0.5f, -0.5f, 0.0f, 1f, 0f, 0f, 0f, 1f),
			  new TexturedVertex(0.5f, -0.5f, 0.0f, 0f, 1f, 0f, 1f, 1f),
			  new TexturedVertex(-0.5f, 0.5f, 0.0f, 0f, 0f, 1f, 0f, 0f),
			  new TexturedVertex(0.5f, 0.5f, 0.0f, 1f, 1f, 0f, 1f, 0f)
		};
//		short[] indices = {
////			  0, 1, 3,   // top left triangle
////			  0, 2, 3,  // top right triangle
////			  0, 1, 4,  // bottom left triangle
////			  0, 2, 4  // bottom right triangle
//
//			  0, 1, 2,
//			  1, 2, 3
//		};

		Cube[] cubes = new Cube[]{
			  new Cube( 0.0f,  0.0f,  0.0f),
			  new Cube( 2.0f,  5.0f, -15.0f),
			  new Cube(-1.5f, -2.2f, -2.5f),
			  new Cube(-3.8f, -2.0f, -12.3f),
			  new Cube( 2.4f, -0.4f, -3.5f),
			  new Cube(-1.7f,  3.0f, -7.5f),
			  new Cube( 1.3f, -2.0f, -2.5f),
			  new Cube( 1.5f,  2.0f, -2.5f),
			  new Cube( 1.5f,  0.2f, -1.5f),
			  new Cube(-1.3f,  1.0f, -1.5f)
		};

//		texturedTri = Cube.getVertices();
//
//		short[] indices = Cube.getIndices();


		// VBO, EBO, VAO
//		VBO vbo = new VBO(new Vertex[0], TexturedVertex.SIZE);
//		EBO ebo = new EBO(vbo);
//		Arrays.stream(texturedTri).forEach(vbo::addVertex);
//
//		VAO basicVao1 = new VAO(() -> {
//			vbo.bind();
//			vbo.upload(GL_STATIC_DRAW);
//
//			ebo.bind();
//			ebo.upload(indices, GL_STATIC_DRAW);
//
//			glVertexAttribPointer(0, 3, GL_FLOAT, false, TexturedVertex.BYTES, 0);
//			glVertexAttribPointer(1, 3, GL_FLOAT, false, TexturedVertex.BYTES, 12);
//			glVertexAttribPointer(2, 2, GL_FLOAT, false, TexturedVertex.BYTES, 24);
//
//			glEnableVertexAttribArray(0);
//			glEnableVertexAttribArray(1);
//			glEnableVertexAttribArray(2);
//		});

		// setup shaders & program
		Shader basicVertex = Shader.fromFile(GL_VERTEX_SHADER, new File("vertex.vs.glsl"));
		Shader basicFragment = Shader.fromFile(GL_FRAGMENT_SHADER, new File("frag.fs.glsl"));

		basicProgram = new ShaderProgram(basicVertex, basicFragment);

		basicVertex.delete();
		basicFragment.delete();

		Texture defaultTex = new Texture(new File("/default.png"), 0);
		Texture birdTex = new Texture(new File("/bird.png"), 0);

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

				case GLFW_KEY_UP -> {
					basicProgram.use();
					System.out.println(overlayUnif.getF());
					overlayUnif.set(overlayUnif.getF() + .02f);
				}

				case GLFW_KEY_DOWN -> {
					basicProgram.use();
					System.out.println(overlayUnif.getF());
					overlayUnif.set(overlayUnif.getF() - .02f);
				}
			}
		});

		Uniform modelUniform = new Uniform(basicProgram, "model");
		Uniform viewUniform = new Uniform(basicProgram, "view");
		Uniform projectionUniform = new Uniform(basicProgram, "projection");

		for (int i = 0; i < cubes.length; i++) {
			cubes[i].rotation.add(i * 20, 0, 0);
		}

		Matrix4f projectionMatrix = camera.updateProjectionMatrix(window);

		projectionUniform.set(projectionMatrix, false);

		Player player = new Player(ImageIO.read(Main.class.getResourceAsStream("/Skins/gaycat.png")), true);
		Player player1 = new Player(ImageIO.read(Main.class.getResourceAsStream("/Skins/syl.png")), true);
		Player player2 = new Player(ImageIO.read(Main.class.getResourceAsStream("/Skins/11a3c5525ba97661.png")), true);

		player.updatePosition(new Vector3f(0, 0, 0));
		player1.updatePosition(new Vector3f(-5, 0, 0));
		player2.updatePosition(new Vector3f(5, 0, 0));

		double delta = 1d/120;
		double last = glfwGetTime()-delta;

		Plane p1 = new Plane().setSize(2, 2, 2).setPosition(0, 0, 9);
		Plane p2 = new Plane().setSize(2, 2, 2).setPosition(0, 0, 10);

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

			// uwu
			basicProgram.use();

			defaultTex.bind();
			birdTex.bind(1);

			// setup gl rendering state

			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

			glEnable(GL_DEPTH_TEST);
			glEnable(GL_CULL_FACE);

			//basicVao1.bind();

			// update projection matrix (if necessary)
			if (camera.dirtyMatrix) {
				camera.dirtyMatrix = false;
				projectionUniform.set(camera.matrix, false);
			}

			// Apply camera transforms
			viewUniform.set(viewMatrix, false);

			// render thing
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

//			for (Cube cube : cubes) {
//				cube.rotation.add((float) Math.toRadians(25f*delta), (float) Math.toRadians(25f*delta), 0);
//				//cube.position.add(0, (float) (Math.sin(now)*delta), 0);
//
//				cube.setupMatrixForRender(modelMatrix.identity());
//
//				modelUniform.set(modelMatrix, false);
//
//				ebo.draw();
//			}

//			transUniform.set(translationMatrix
//					    .rotate((float) Math.toRadians(-30f*delta), 0f ,0f, 1f),
//				  false
//			);
//
//			glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_SHORT, 0);
//
//			float scale = (float) Math.sin(now);
//			Matrix4f tm2 = new Matrix4f(translationMatrix2);
//			transUniform.set(tm2
//					    .scale(scale),
//				  false
//			);
//
//			glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_SHORT, 0);


			window.glRenderPost();
		}

		System.out.println("bye");
		glfwTerminate();
	}
}