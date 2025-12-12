package net.sylv.Objects;

import net.sylv.Util.VAO;
import net.sylv.Util.Vertex.TexturedVertex;
import org.joml.Vector3f;

import java.util.Arrays;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class Cube extends Obj {
	public Cube() {
		Arrays.stream(getVertices()).forEach(vbo::addVertex);

		vao = new VAO(() -> {
			vbo.bind();
			vbo.upload(GL_STATIC_DRAW);

			ebo.bind();
			ebo.upload(getIndices(), GL_STATIC_DRAW);

			glVertexAttribPointer(0, 3, GL_FLOAT, false, TexturedVertex.BYTES, 0);
			glVertexAttribPointer(1, 3, GL_FLOAT, false, TexturedVertex.BYTES, 12);
			glVertexAttribPointer(2, 2, GL_FLOAT, false, TexturedVertex.BYTES, 24);

			glEnableVertexAttribArray(0);
			glEnableVertexAttribArray(1);
			glEnableVertexAttribArray(2);
		});
	}

	public Cube(Cube c) {
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

		this.uLeftStart = c.uLeftStart;
		this.uLeftEnd   = c.uLeftEnd;
		this.vLeftStart = c.vLeftStart;
		this.vLeftEnd = c.vLeftEnd;

		this.uRightStart = c.uRightStart;
		this.uRightEnd   = c.uRightEnd;
		this.vRightStart = c.vRightStart;
		this.vRightEnd = c.vRightEnd;

		this.uTopStart = c.uTopStart;
		this.uTopEnd   = c.uTopEnd;
		this.vTopStart = c.vTopStart;
		this.vTopEnd = c.vTopEnd;

		this.uBottomStart = c.uBottomStart;
		this.uBottomEnd   = c.uBottomEnd;
		this.vBottomStart = c.vBottomStart;
		this.vBottomEnd = c.vBottomEnd;

		Arrays.stream(getVertices()).forEach(vbo::addVertex);

		vao = new VAO(() -> {
			vbo.bind();
			vbo.upload(GL_STATIC_DRAW);

			ebo.bind();
			ebo.upload(getIndices(), GL_STATIC_DRAW);

			glVertexAttribPointer(0, 3, GL_FLOAT, false, TexturedVertex.BYTES, 0);
			glVertexAttribPointer(1, 3, GL_FLOAT, false, TexturedVertex.BYTES, 12);
			glVertexAttribPointer(2, 2, GL_FLOAT, false, TexturedVertex.BYTES, 24);

			glEnableVertexAttribArray(0);
			glEnableVertexAttribArray(1);
			glEnableVertexAttribArray(2);
		});
	}

	public Cube(float x, float y, float z) {
		this();

		this.position.set(x, y, z);
	}

	public Cube setPosition(float x, float y, float z) {
		position.set(x, y, z);

		return this;
	}

	public Cube setPosition(Vector3f vec) {
		position.set(vec);

		return this;
	}

	public Cube setRotation(float x, float y, float z) {
		rotation.set(x, y, z);

		return this;
	}

	public Cube setRotation(Vector3f vec) {
		rotation.set(vec);

		return this;
	}

	public Cube setSize(float x, float y, float z) {
		size.set(x, y, z);

		return this;
	}

	public Cube setSize(Vector3f vec) {
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

			  // ===== FRONT (+Z)
			  new TexturedVertex(-x, -y,  z, 1,1,1, uFrontStart, vFrontStart),
			  new TexturedVertex( x, -y,  z, 1,1,1, uFrontEnd,   vFrontStart),
			  new TexturedVertex( x,  y,  z, 1,1,1, uFrontEnd,   vFrontEnd),
			  new TexturedVertex(-x,  y,  z, 1,1,1, uFrontStart, vFrontEnd),

			  // ===== BACK (-Z)
			  new TexturedVertex( x, -y, -z, 1,1,1, uBackStart, vBackStart),
			  new TexturedVertex(-x, -y, -z, 1,1,1, uBackEnd,   vBackStart),
			  new TexturedVertex(-x,  y, -z, 1,1,1, uBackEnd,   vBackEnd),
			  new TexturedVertex( x,  y, -z, 1,1,1,  uBackStart, vBackEnd),

			  // ===== RIGHT (-X)
			  new TexturedVertex(-x, -y, -z, 1,1,1, uRightStart, vRightStart),
			  new TexturedVertex(-x, -y,  z, 1,1,1, uRightEnd,   vRightStart),
			  new TexturedVertex(-x,  y,  z, 1,1,1, uRightEnd,   vRightEnd),
			  new TexturedVertex(-x,  y, -z, 1,1,1, uRightStart, vRightEnd),

			  // ===== LEFT (+X)
			  new TexturedVertex( x, -y,  z, 1,1,1, uLeftStart, vLeftStart),
			  new TexturedVertex( x, -y, -z, 1,1,1, uLeftEnd,   vLeftStart),
			  new TexturedVertex( x,  y, -z, 1,1,1, uLeftEnd,   vLeftEnd),
			  new TexturedVertex( x,  y,  z, 1,1,1, uLeftStart, vLeftEnd),

			  // ===== TOP (+Y)
			  new TexturedVertex(-x,  y,  z, 1,1,1, uTopStart, vTopStart),
			  new TexturedVertex( x,  y,  z, 1,1,1, uTopEnd,   vTopStart),
			  new TexturedVertex( x,  y, -z, 1,1,1, uTopEnd,   vTopEnd),
			  new TexturedVertex(-x,  y, -z, 1,1,1, uTopStart, vTopEnd),

			  // ===== BOTTOM (-Y)
			  new TexturedVertex(-x, -y, -z, 1,1,1, uBottomStart, vBottomStart),
			  new TexturedVertex( x, -y, -z, 1,1,1, uBottomEnd,   vBottomStart),
			  new TexturedVertex( x, -y,  z, 1,1,1, uBottomEnd,   vBottomEnd),
			  new TexturedVertex(-x, -y,  z, 1,1,1, uBottomStart, vBottomEnd),
		};
	}

	@Override
	public short[] getIndices() {
		return new short[]{
			  0, 1, 2,   2, 3, 0,

			  4, 5, 6,   6, 7, 4,

			  8, 9,10,  10,11, 8,

			  12,13,14,  14,15,12,

			  16,17,18,  18,19,16,

			  20,21,22,  22,23,20

//			  20,23,22,  22,21,20
		};
	}
}
