package mms.movement;

import mms.world.LawState;
import mms.world.Vector;

public class MovementState extends LawState {
	
	public Vector position;
	public Vector velocity;
	public Vector acceleration;
	public Vector orientation;
	public Vector angularVelocity;
	
	public MovementState(int dimensions) {
		this.position = new Vector(dimensions);
		this.velocity = new Vector(dimensions);
		this.acceleration = new Vector(dimensions);
		this.orientation = new Vector(dimensions);
		this.angularVelocity = new Vector(dimensions);
	}
	
	public void copy(MovementState newState) {
		
		position.copy(newState.position);
		velocity.copy(newState.velocity);
		acceleration.copy(newState.acceleration);
		orientation.copy(newState.orientation);
		angularVelocity.copy(newState.angularVelocity);
		
	}

}
