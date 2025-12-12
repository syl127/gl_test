package net.sylv.Objects;

import net.sylv.Util.Texture;
import org.joml.Vector3f;

import java.awt.image.BufferedImage;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11C.GL_NEAREST;
import static org.lwjgl.opengl.GL15C.GL_STATIC_DRAW;

public class Player {
	public Cube head = new Cube(0, 2.5f, 0).setSize(2, 2, 2);

	public Cube torso = new Cube().setSize(2, 3, 1);

	public Cube leftArm = new Cube(1.5f, 0, 0).setSize(1, 3, 1);

	public Cube rightArm = new Cube(-1.5f, 0, 0).setSize(1, 3, 1);

	public Cube leftLeg = new Cube(0.5f, -3, 0).setSize(1, 3, 1);

	public Cube rightLeg = new Cube(-0.5f, -3, 0).setSize(1, 3, 1);

	public Cube hat;

	public Cube jacket;

	public Cube leftSleeve;

	public Cube rightSleeve;

	public Cube leftPant;

	public Cube rightPant;

	public Cube[] inner = new Cube[6];

	public Cube[] outer = new Cube[6];

	public Texture texture;

	public boolean slim;

	public Player(BufferedImage skin, boolean slim) {
		texture = new Texture(skin, 0);
		texture.bind();
		texture.minTexFilter(GL_NEAREST);
		texture.maxTexFilter(GL_NEAREST);

		int i = 0;
		for (Cube c : new Cube[]{head, torso, leftArm, rightArm, leftLeg, rightLeg}) {
			c.texture = texture.id;
			inner[i++] = c;
		}

		setupUVs(head, 8f, 8f, 8f, 8f, 8f, 8f);

		setupUVs(torso, 20f, 20f, 8f, 12f, 4f, 4f);

		setupUVs(leftLeg, 20f, 52f, 4f, 12f, 4f, 4f);

		setupUVs(rightLeg, 4f, 20f, 4f, 12f, 4f, 4f);

		// now outer layer!!
		hat = new Cube(head);
		jacket = new Cube(torso);
		leftSleeve = new Cube(leftArm);
		rightSleeve = new Cube(rightArm);
		leftPant = new Cube(leftLeg);
		rightPant = new Cube(rightLeg);

		i = 0;
		for (Cube c : new Cube[]{hat, jacket, leftSleeve, rightSleeve, leftPant, rightPant}) {
			float add = c == jacket ? .249f : .2495f; // helps z fighting yYAYyyy

			c.size.add(add, add, add);

			outer[i++] = c;
		}

		setupUVs(hat, 40f, 8f, 8f, 8f, 8f, 8f);

		setupUVs(jacket, 20f, 36f, 8f, 12f, 4f, 4f);

		setupUVs(leftPant, 4f, 52f, 4f, 12f, 4f, 4f);

		setupUVs(rightPant, 4f, 36f, 4f, 12f, 4f, 4f);

		setSlim(slim);

		System.out.println("uwu");
	}

	public void setSlim(boolean isSlim) {
		this.slim = isSlim;

		if (slim) {
			leftArm.setSize(0.75f, 3, 1);
			leftArm.position.set(1.375f, 0, 0);

			rightArm.setSize(0.75f, 3, 1);
			rightArm.position.set(-1.375f, 0, 0);

			setupUVs(leftArm, 36f, 52f, 3f, 12f, 4f, 4f);

			setupUVs(rightArm, 44f, 20f, 3f, 12f, 4f, 4f);
		} else {
			leftArm.setSize(1f, 3, 1);
			leftArm.position.set(1.5f, 0, 0);

			rightArm.setSize(1f, 3, 1);
			rightArm.position.set(-1.5f, 0, 0);

			setupUVs(leftArm, 36f, 52f, 4f, 12f, 4f, 4f);

			setupUVs(rightArm, 44f, 20f, 4f, 12f, 4f, 4f);
		}

		leftSleeve.position.set(leftArm.position);
		leftSleeve.size.set(leftArm.size).add(.2495f, .2495f, .2495f);

		rightSleeve.position.set(rightArm.position);
		rightSleeve.size.set(rightArm.size).add(.2495f, .2495f, .2495f);

		if (slim) {
			setupUVs(leftSleeve, 52f, 52f, 3f, 12f, 4f, 4f);

			setupUVs(rightSleeve, 44f, 36f, 3f, 12f, 4f, 4f);
		} else {
			setupUVs(leftSleeve, 52f, 52f, 4f, 12f, 4f, 4f);

			setupUVs(rightSleeve, 44f, 36f, 4f, 12f, 4f, 4f);
		}
	}

	public void updatePosition(Vector3f pos) {
		head.position.set(0, 2.5f, 0).add(pos);
		torso.position.set(0, 0, 0).add(pos);
		leftArm.position.set(torso.size.x/2 + leftArm.size.x/2, 0, 0).add(pos);
		rightArm.position.set(-torso.size.x/2 - rightArm.size.x/2, 0, 0).add(pos);
		leftLeg.position.set(0.5f, -3f, 0).add(pos);
		rightLeg.position.set(-0.5f, -3f, 0).add(pos);

		hat.setPosition(head.position);
		jacket.setPosition(torso.position);
		leftSleeve.setPosition(leftArm.position);
		rightSleeve.setPosition(rightArm.position);
		leftPant.setPosition(leftLeg.position);
		rightPant.setPosition(rightLeg.position);
	}

	// w2 is different size for left/right (thanks slim model!!)
	//  h2 is height for the top/bottom
	// TODO: w2 support
	public static void setupUVs(Obj o, float x, float y, float w, float h, float w2, float h2) {
		o.uFrontStart = x/64;
		o.vFrontStart = (y+h)/64;
		o.uFrontEnd = (x+w)/64;
		o.vFrontEnd = y/64;

		o.uBackStart = (x+w2+w)/64;
		o.vBackStart = (y+h)/64;
		o.uBackEnd = (x+w2+w*2)/64;
		o.vBackEnd = y/64;

		o.uLeftStart = (x+w)/64;
		o.vLeftStart = (y+h)/64;
		o.uLeftEnd = (x+w+w2)/64;
		o.vLeftEnd = y/64;

		o.uRightStart = (x-w2)/64;
		o.vRightStart = (y+h)/64;
		o.uRightEnd = x/64;
		o.vRightEnd = y/64;

		o.uTopStart = x/64;
		o.vTopStart = y/64;
		o.uTopEnd = (x+w)/64;
		o.vTopEnd = (y-h2)/64;

		o.uBottomStart = (x+w)/64;
		o.vBottomStart = (y-h2)/64;
		o.uBottomEnd = (x+w*2)/64;
		o.vBottomEnd = y/64;

		// upload new vertices to gpu or wahtever
		o.vao.bind();
		o.vbo.bind();

		o.vbo.clear();
		Arrays.stream(o.getVertices()).forEach(o.vbo::addVertex);

		o.vbo.upload(GL_STATIC_DRAW);
	}
}
