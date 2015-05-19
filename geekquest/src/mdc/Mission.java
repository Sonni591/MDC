package mdc;

public class  Mission {

// Variables
	private String description;
	private boolean isAccomplished;

// Constructor
	public Mission(String description, boolean isAccomplished) {
		super();
		this.description = description;
		this.isAccomplished = isAccomplished;
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
