package mdc;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;


@SuppressWarnings("serial")
public class GeekquestServlet extends HttpServlet {
	
	// register all entity classes for your application at application startup,
		// before Objectify is used
		static {
			ObjectifyService.register(Mission.class);
			ObjectifyService.register(Player.class);
		}
	
	private BlobstoreService blobstoreService = BlobstoreServiceFactory
			.getBlobstoreService();

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {

		String imageBlobKey = null;

		// UserService holen und initialisieren
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		String thisURL = req.getRequestURI();

		// Login und Logout URL generieren
		String loginUrl = userService.createLoginURL(thisURL);
		String logoutUrl = userService.createLogoutURL(thisURL);

		// Attribute für JSP setzen
		req.setAttribute("user", user);
		req.setAttribute("loginUrl", loginUrl);
		req.setAttribute("logoutUrl", logoutUrl);

		// Ist der User eingelogged?
		if (req.getUserPrincipal() != null) {

			// User ist eingeloggt

			// Userdaten anzeigen
			displayCharacterInfo(req, resp);

			String userId = userService.getCurrentUser().getEmail();
			Player player = null;
			try {
				player = ofy().load().type(Player.class).id(userId).safe(); // throws NotFoundException;
				if (player != null) {

					// Prüfe, ob Player mit Health Status existiert
					String health_value = null;
					if (player.getHealth() != null) {
						health_value = player.getHealth().toString();
					} else {
						health_value = "10";
					}

					req.setAttribute("player_name", player.getName());
					req.setAttribute("player_charclass", player.getCharclass());
					req.setAttribute("player_health", player.getHealth());
					req.setAttribute("player_score", player.getScore());
					
					List<String> missionDescriptions = new LinkedList<String>();
					List<Boolean> missionStatus = new LinkedList<Boolean>();
					for (Ref<Mission> m : player.getMissions()) {
						missionDescriptions.add(m.getValue().getDescription());
						missionStatus.add(m.getValue().isAccomplished());
					}
					req.setAttribute("mission_description", missionDescriptions);
					req.setAttribute("mission_isAccomplished", missionStatus);


					// Dropdown richtig setzen
					String charclass = player.getCharclass().toString();
					if (charclass.compareTo("hobbit") == 0) {
						req.setAttribute("selected1", "selected");
					} else if (charclass.compareTo("dwarf") == 0) {
						req.setAttribute("selected2", "selected");
					} else if (charclass.compareTo("mage") == 0) {
						req.setAttribute("selected3", "selected");
					} else if (charclass.compareTo("elf") == 0) {
						req.setAttribute("selected4", "selected");
					}

					// blob-key zum Image speichern
					if (player.getImageBlobKey() != null) {
						imageBlobKey = player.getImageBlobKey().toString();
					}
				}
			} catch (NotFoundException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();

				// Kein Charakter vorhanden, Health-Status vorbelegen
				req.setAttribute("player_health", 10);
			}

			// Blobstore
			if (imageBlobKey != null) {

				BlobKey blobKey = new BlobKey(imageBlobKey);
				if (blobKey != null) {
					// blobstoreService.serve(blobKey, resp);

					ImagesService services = ImagesServiceFactory
							.getImagesService();
					ServingUrlOptions serve = ServingUrlOptions.Builder
							.withBlobKey(blobKey); // Blobkey of the image
													// uploaded to BlobStore.
					String imageUrl = services.getServingUrl(serve);

					System.out.println(imageUrl);

					req.setAttribute("imageUrl", imageUrl);
				}
			}

			if (player != null) {
				// Print highscore when users is logged in
				//TODO
				List<Player> players = GeekQuestUtils
						.getManipulatedHighScoreQuery(player);
				for (Player p : players) {
					System.out.println(p);
					resp.getWriter().println(
							p.getName() + " "
									+ p.getScore()
									+ "<br>");
				}
			}

		} else {
			// User nicht eingeloggt - Highscore anzeigen

			List<Player> players = ofy().load().type(Player.class).order("-score").limit(10).list();
			
			for (Player player : players) {
				resp.getWriter().println(
						player.getName() + " "
								+ player.getScore() + "<br>");
			}

		}

		// JSP aufrufen
		resp.setContentType("text/html");
		RequestDispatcher jsp = req.getRequestDispatcher("character.jsp");
		jsp.forward(req, resp);


	}

	public void displayCharacterInfo(HttpServletRequest req,
			HttpServletResponse resp) throws IOException {

		// Ausgabe von Basisdaten des Users

		UserService userService = UserServiceFactory.getUserService();
		User currentUser = userService.getCurrentUser();

		if (currentUser != null) {
			resp.setContentType("text/html");
			resp.getWriter().println(
					"User Nickname: " + currentUser.getNickname() + "<br>");
			resp.getWriter().println(
					"User Mail: " + currentUser.getEmail() + "<br>");
			resp.getWriter().println(
					"User ID: " + currentUser.getUserId() + "<br>");
		} else {
			resp.sendRedirect(userService.createLoginURL(req.getRequestURI()));
		}

	}

}
