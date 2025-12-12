package net.sylv.Util;

import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class VAO {
	public final int id;

	public VAO(Runnable run) {
		id = glGenVertexArrays();

		bind();
		run.run();
		unbind();
	}

	public void bind() {
		glBindVertexArray(id);
	}

	public void unbind() {
		glBindVertexArray(0);
	}
}