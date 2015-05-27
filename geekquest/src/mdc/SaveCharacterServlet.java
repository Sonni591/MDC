package mdc;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
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
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.apphosting.datastore.*;

@SuppressWarnings("serial")

public class SaveCharacterServlet extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {

		// Datastore initialisieren
				DatastoreService datastore = DatastoreServiceFactory
						.getDatastoreService();
		
		// use transactions - make sure to use retries
		int retries = 3;
		while (true) {
		    Transaction txn = datastore.beginTransaction();
		    try {
		            	
		    	// Userdaten holen
				UserService userService = UserServiceFactory.getUserService();
				String userId = userService.getCurrentUser().getEmail();

				Entity character = new Entity("character", userId);

				// Player mit Parametern aus dem JSP anlegen
				// Player(UserID, Name, Charakterklasse, Health-Status)
				Player player = new Player(userId, req.getParameter("name"), req.getParameter("charclass"), req.getParameter("health"), setRandomScore(), req.getParameter("image-blob-key"));

				character.setProperty("player_id", player.getId());
				character.setProperty("player_name", player.getName());
				character.setProperty("player_charclass", player.getCharclass());
				character.setProperty("player_health", player.getHealth());
				character.setProperty("player_score", player.getScore());
				character.setProperty("image-blob-key", player.getImageBlobKey());

				// set list of missions hard coded
				setTemporaryMissions(character);
				
				datastore.put(character);

				resp.sendRedirect("/geekquest");
		    	
		        txn.commit();
		        break;
		    } catch (ConcurrentModificationException e) {
		        if (retries == 0) {
		            throw e;
		        }
		        // Allow retry to occur
		        --retries;
		        System.out.println("Transaction - ConcurrentModificationException");
		    } finally {
		        if (txn.isActive()) {
		            txn.rollback();
		        }
		    }
		}
		
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
