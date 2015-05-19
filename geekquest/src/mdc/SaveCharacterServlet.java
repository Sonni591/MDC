package mdc;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.apphosting.datastore.*;

@SuppressWarnings("serial")

public class SaveCharacterServlet extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {

		// Userdaten holen
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		String userId = userService.getCurrentUser().getEmail();
		
		// Datastore initialisieren
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		
		Entity character = new Entity("character", userId);

		// Player mit Parametern aus dem JSP anlegen
		// Player(UserID, Name, Charakterklasse, Health-Status)
		
		Player player = new Player(userId, req.getParameter("name"), req.getParameter("charclass"), req.getParameter("health"), setRandomScore());
		// Mission 
//		Mission mission = new Mission("destroy ring", false);
		

		character.setProperty("player_id", player.getId());
		character.setProperty("player_name", player.getName());
		character.setProperty("player_charclass", player.getCharclass());
		character.setProperty("player_health", player.getHealth());
		character.setProperty("player_score", player.getScore());
		

		// hard coded test mission
//		character.setProperty("mission_description", mission.getDescription());
//		character.setProperty("mission_isAccomplished",
//				mission.isAccomplished());

		// set list of missions hard coded
		setTemporaryMissions(character);
		
		datastore.put(character);

		resp.sendRedirect("/geekquest");
		
	}
	
	public void setTemporaryMissions(Entity character) {
				
		List<Mission> missions = new LinkedList<Mission>();
		missions.add(new Mission("destroy ring", false));
		missions.add(new Mission("destroyRing", false));
		missions.add(new Mission("leave the shire", true));
		
		
		List<String> missionDescriptions = new LinkedList<String>();
		List<Boolean> missionStatus = new LinkedList<Boolean>();
		
		for (Mission m : missions) {
			missionDescriptions.add(m.getDescription());
			missionStatus.add(m.isAccomplished());
		}
		character.setProperty("mission_description", missionDescriptions );
		character.setProperty("mission_isAccomplished", missionStatus);
		
	}

	public static long setRandomScore() {

	    // NOTE: Usually this should be a field rather than a method
	    // variable so that it is not re-seeded every call.
	    Random rand = new Random();

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((100 - 1) + 1) + 1;

	    return randomNum;
	}
	
	
	
}
