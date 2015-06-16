package mdc;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

@Entity
@Index
public class Player {

	// Variables
	@Id
	private String id;
	private String name;
	private String charclass;
	private String health;
	private long score;
	private String imageBlobKey;
	@Load
	private List<Ref<Mission>> missions = new ArrayList<Ref<Mission>>();


	// Constructor
	public Player(String id, String name, String charclass, String health,
			long score, String imageBlobKey) {
		super();
		this.id = id;
		this.name = name;
		this.charclass = charclass;
		this.health = health;
		this.score = score;
		this.imageBlobKey = imageBlobKey;
	}

	// non-arg constructor for objectify
	public Player() {
		super();
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

	
	public List<Ref<Mission>> getMissions() {
		return missions;
	}

	public void setMissions(List<Ref<Mission>> missions) {
		this.missions = missions;
	}

}
