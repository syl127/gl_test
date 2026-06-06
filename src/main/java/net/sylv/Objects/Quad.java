package net.sylv.Objects;

import net.sylv.Util.VAO;
import net.sylv.Util.Vertex.TexturedVertex;
import org.joml.Vector3f;

import java.util.Arrays;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class Quad extends Obj {
	public Quad() {
		Arrays.stream(getVertices()).forEach(vbo::addVertex);

		vao = new VAO(vbo, ebo, () -> {
			vbo.upload(GL_STATIC_DRAW);

			ebo.upload(getIndices(), GL_STATIC_DRAW);

			glVertexAttribPointer(0, 3, GL_FLOAT, false, TexturedVertex.BYTES, 0);
			glVertexAttribPointer(1, 3, GL_FLOAT, false, TexturedVertex.BYTES, 12);
			glVertexAttribPointer(2, 2, GL_FLOAT, false, TexturedVertex.BYTES, 24);

			glEnableVertexAttribArray(0);
			glEnableVertexAttribArray(1);
			glEnableVertexAttribArray(2);
		});
	}

	public Quad(Quad c) {
		this.setSize(c.size);
		this.setPosition(c.position);
		this.setRotation(c.rotation);

		this.texture = c.texture;

		this.uFrontStart = c.uFrontStart;
		this.uFrontEnd   = c.uFrontEnd;
		this.vFrontStart = c.vFrontStart;
		this.vFrontEnd = c.vFrontEnd;

		this.uBackStart = c.uBackStart;
		this.uBackEnd   = c.uBackEnd;
		this.vBackStart = c.vBackStart;
		this.vBackEnd = c.vBackEnd;

		Arrays.stream(getVertices()).forEach(vbo::addVertex);

		vao = new VAO(vbo, ebo, () -> {
			vbo.upload(GL_STATIC_DRAW);

			ebo.upload(getIndices(), GL_STATIC_DRAW);

			glVertexAttribPointer(0, 3, GL_FLOAT, false, TexturedVertex.BYTES, 0);
			glVertexAttribPointer(1, 3, GL_FLOAT, false, TexturedVertex.BYTES, 12);
			glVertexAttribPointer(2, 2, GL_FLOAT, false, TexturedVertex.BYTES, 24);

			glEnableVertexAttribArray(0);
			glEnableVertexAttribArray(1);
			glEnableVertexAttribArray(2);
		});
	}

	public Quad(float x, float y, float z) {
		this();

		this.position.set(x, y, z);
	}

	public Quad setPosition(float x, float y, float z) {
		position.set(x, y, z);

		return this;
	}

	public Quad setPosition(Vector3f vec) {
		position.set(vec);

		return this;
	}

	public Quad setRotation(float x, float y, float z) {
		rotation.set(x, y, z);

		return this;
	}

	public Quad setRotation(Vector3f vec) {
		rotation.set(vec);

		return this;
	}

	public Quad setSize(float x, float y, float z) {
		size.set(x, y, z);

		return this;
	}

	public Quad setSize(Vector3f vec) {
		size.set(vec);

		return this;
	}

	// TODO: color? texture? per face textures?
	// TODO: also better rotation

	@Override
	public TexturedVertex[] getVertices() {
		float x = 0.5f;
		float y = 0.5f;
		float z = 0.5f;

		return new TexturedVertex[] {
			  new TexturedVertex(-x, -y,  z, 1,1,1, uFrontStart, vFrontStart),
			  new TexturedVertex( x, -y,  z, 1,1,1, uFrontEnd,   vFrontStart),
			  new TexturedVertex( x,  y,  z, 1,1,1, uFrontEnd,   vFrontEnd),
			  new TexturedVertex(-x,  y,  z, 1,1,1, uFrontStart, vFrontEnd),
		};
	}

	@Override
	public short[] getIndices() {
		return new short[]{
			  0, 1, 2,
			  2, 3, 0
		};
	}
}
