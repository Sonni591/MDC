package mdc;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
@Index
public class  Mission {

// Variables
	@Id
	private String description;
	private boolean isAccomplished;

// Constructor
	public Mission(String description, boolean isAccomplished) {
		super();
		this.description = description;
		this.isAccomplished = isAccomplished;
	}
	
	// non-arg constructor for objectify
	public Mission() {
		super();
	}

// Getters and Setters
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isAccomplished() {
		return isAccomplished;
	}
	public void setAccomplished(boolean isAccomplished) {
		this.isAccomplished = isAccomplished;
	}

// Functions
	public void accomplished() {
		this.isAccomplished = true;
	}
	
}
