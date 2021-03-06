package mdc;

public class Player {

// Variables
	private String id;
	private String name;
	private String charclass;
	private String health;
	private long score;
	private String imageBlobKey;
		
// Constructor
	public Player(String id, String name, String charclass, String health, long score, String imageBlobKey) {
		super();
		this.id = id;
		this.name = name;
		this.charclass = charclass;
		this.health = health;
		this.score = score;
		this.imageBlobKey = imageBlobKey;
	}

// Getters and Setters
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCharclass() {
		return charclass;
	}
	public void setCharclass(String charclass) {
		this.charclass = charclass;
	}
	public String getHealth() {
		return health;
	}
	public void setHealth(String health) {
		this.health = health;
	}

	public long getScore() {
		return score;
	}

	public void setScore(long score) {
		this.score = score;
	}

	public String getImageBlobKey() {
		return imageBlobKey;
	}

	public void setImageBlobKey(String imageBlobKey) {
		this.imageBlobKey = imageBlobKey;
	}

// Functions
//	public void heal(int points) {
//		this.health = health + points;
//	}
	
//	public void hurt(int points) {
//	this.health = health - points;
//  }
	
}
