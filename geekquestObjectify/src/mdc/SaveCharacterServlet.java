package mdc;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Ref;

@SuppressWarnings("serial")
public class SaveCharacterServlet extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {

		// Userdaten holen
		UserService userService = UserServiceFactory.getUserService();
		String userId = userService.getCurrentUser().getEmail();

		// Player mit Parametern aus dem JSP anlegen
		// Player(UserID, Name, Charakterklasse, Health-Status)
		Player player = new Player(userId, req.getParameter("name"),
				req.getParameter("charclass"), req.getParameter("health"),
				setRandomScore(), req.getParameter("image-blob-key"));

		// set list of missions hard coded
		setTemporaryMissions(player);
		
		ofy().save().entity(player).now();

		resp.sendRedirect("/geekquest");

	}

	public void setTemporaryMissions(Player player) {

		Mission mission1 = new Mission("destroy ring", false);
		Mission mission2 = new Mission("defend helms deep", false);
		Mission mission3 = new Mission("leave the shire", true);

		// if missions are not there, add the missions to the datastore
		Mission getMission = ofy().load().type(Mission.class).id("destroy ring").now();
		if(getMission==null) {
			ofy().save().entity(mission1).now();
			ofy().save().entity(mission2).now();
			ofy().save().entity(mission3).now();
		}
		
		ArrayList<Ref<Mission>> missions = new ArrayList<Ref<Mission>>();
		missions.add(Ref.create(mission1));
		missions.add(Ref.create(mission2));
		missions.add(Ref.create(mission3));

		player.setMissions(missions);

		
		
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
