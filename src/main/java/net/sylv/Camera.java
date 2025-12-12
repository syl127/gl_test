package net.sylv;

import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
	public Vector3f position = new Vector3f(0, 0, -1);

	public Vector3f rotation = new Vector3f(0, 0, 0);

	public Vector3f lookVector = new Vector3f(0, 0, -1);

	public Vector3f upVector = new Vector3f(0, 1, 0);

	public float yaw = -90f;

	public float pitch = 0f;

	public float fov = 90f;

	public float speed = 16f;

	public Matrix4f matrix = new Matrix4f();

	public boolean dirtyMatrix = false;

	/**
	 * Modifies the lookVector to match the currently set yaw and pitch
	 *
	 * @return Vector3f - euler angles
	 */
	public Vector3f updateLookVector() {
		float yawRad = org.joml.Math.toRadians(yaw);
		float pitchRad = org.joml.Math.toRadians(pitch);


		rotation.set(
			  org.joml.Math.cos(yawRad) * org.joml.Math.cos(pitchRad),
			  org.joml.Math.sin(pitchRad),
			  org.joml.Math.sin(yawRad) * org.joml.Math.cos(pitchRad)
		);

		lookVector.set(rotation).normalize();

		return rotation;
	}

	/**
	 * Sets fov, and updates a matrix that was passed
	 *
	 * @return Vector3f - euler angles
	 */
	public Camera setFov(float fov, Window w) {
		this.fov = fov;

		updateProjectionMatrix(w);

		return this;
	}

	public Matrix4f updateLookMatrix(Matrix4f mat) {
		return mat.
			  identity()
			  .lookAlong(lookVector, upVector)
			  .translate(position);
	}

	public Matrix4f updateProjectionMatrix(Window w) {
		dirtyMatrix = true;

		return matrix.identity().perspective(Math.toRadians(fov), (float) w.width/w.height, 0.1f, 100f);
	}
}
