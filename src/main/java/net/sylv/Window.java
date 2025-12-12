package net.sylv;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
	public long id;

	public int width;

	public int height;

	public Camera camera;

	public Window(Camera camera) {
		this.camera = camera;
	}

	public void init() {
		GLFWErrorCallback.createPrint(System.err).set();

		if (!glfwInit()) {
			throw new RuntimeException("Couldn't initialize GLFW sobsob");
		}

		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		//glfwWindowHint(GLFW_SAMPLES, 8);
		// ^ breaks edges onn literally everything?

		id = glfwCreateWindow(width = 768, height = 512, "uwu meow", NULL, NULL);

		if (id == NULL) {
			throw new RuntimeException("failed to make window");
		}

		// input thing
		glfwSetKeyCallback(id, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
		});

		// Make the OpenGL context current
		glfwMakeContextCurrent(id);

		// Enable v-sync
		glfwSwapInterval(1);

		GL.createCapabilities();

		// listen for resizing
		glfwSetFramebufferSizeCallback(id, (id, w, h) -> {
			width = w;
			height = h;
			onResize(w, h);

			System.out.printf("Resized to (%d, %d)%n", w, h);
		});

		onResize(width, height);
	}

	public void onResize(int w, int h) {
		glViewport(0, 0, width = w, height = h);
		camera.updateProjectionMatrix(this);
	}

	public void glRenderPre() {
		glClearColor(0.2f, 0.2f, 0.22f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
	}

	public void glRenderPost() {
		glfwSwapBuffers(id); // swap the color buffers

		glfwPollEvents();
	}
}
