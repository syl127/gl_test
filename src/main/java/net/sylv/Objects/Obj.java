package net.sylv.Objects;

import net.sylv.Util.EBO;
import net.sylv.Util.Shaders.Uniform;
import net.sylv.Util.VAO;
import net.sylv.Util.VBO;
import net.sylv.Util.Vertex.TexturedVertex;
import net.sylv.Util.Vertex.Vertex;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class Obj {
	public final Vector3f position = new Vector3f();

	public final Vector3f rotation = new Vector3f(); // TODO: use look vector, up vector?

	public final Vector3f size = new Vector3f(1, 1, 1);

	public int texture = 0;

	public VBO vbo = new VBO(new Vertex[0], TexturedVertex.SIZE);

	public EBO ebo = new EBO(vbo);

	// VAOs created by subclasses
	public VAO vao;

	public Matrix4f modelMatrix = new Matrix4f().identity();

	public float uFrontStart = 0f;
	public float uFrontEnd   = 1f;
	public float vFrontStart = 1f;
	public float vFrontEnd = 0f;

	public float uBackStart = 0f;
	public float uBackEnd   = 1f;
	public float vBackStart = 1f;
	public float vBackEnd = 0f;

	public float uLeftStart = 0f;
	public float uLeftEnd   = 1f;
	public float vLeftStart = 1f;
	public float vLeftEnd = 0f;

	public float uRightStart = 0f;
	public float uRightEnd   = 1f;
	public float vRightStart = 1f;
	public float vRightEnd = 0f;

	public float uTopStart = 0f;
	public float uTopEnd   = 1f;
	public float vTopStart = 1f;
	public float vTopEnd = 0f;

	public float uBottomStart = 0f;
	public float uBottomEnd   = 1f;
	public float vBottomStart = 1f;
	public float vBottomEnd = 0f;

	public void draw(Uniform modelMatrixUniform) {
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, texture);

		vao.bind();

		setupMatrixForRender(modelMatrix.identity());

		modelMatrixUniform.set(modelMatrix, false);

		ebo.draw();

		vao.unbind();
	}

	// Set up an identity matrix for rendering this cube
	public Matrix4f setupMatrixForRender(Matrix4f mat) {
		return mat.translate(position)
			  .rotate(rotation.x,  1, 0, 0)
			  .rotate(rotation.y,  0, 1, 0)
			  .rotate(rotation.z,  0, 0, 1)
			  .scale(size);
	}

	public TexturedVertex[] getVertices() {
		return new TexturedVertex[0];
	}

	public short[] getIndices() {
		return new short[]{};
	}
}
