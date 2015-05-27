package mdc;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
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

@SuppressWarnings("serial")
public class GeekquestServlet extends HttpServlet {

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

			// check if the user is already in the datastore
			DatastoreService datastore = DatastoreServiceFactory
					.getDatastoreService();

			String userId = userService.getCurrentUser().getEmail();
			Key key = KeyFactory.createKey("character", userId);
			Entity existingCharacter = null;
			try {
				existingCharacter = datastore.get(key);
				if (existingCharacter != null) {

					// Prüfe, ob Player mit Health Status existiert
					String health_value = null;
					if (existingCharacter.getProperty("player_health") != null) {
						health_value = existingCharacter.getProperty(
								"player_health").toString();
					} else {
						health_value = "10";
					}

					Player player = new Player(existingCharacter.getProperty(
							"player_id").toString(), existingCharacter
							.getProperty("player_name").toString(),
							existingCharacter.getProperty("player_charclass")
									.toString(), health_value,
							(long) existingCharacter
									.getProperty("player_score"),"");
					// Mission mission = new Mission(existingCharacter
					// .getProperty("mission_description").toString(),
					// (Boolean) existingCharacter
					// .getProperty("mission_isAccomplished"));
					req.setAttribute("player_name", player.getName());
					req.setAttribute("player_charclass", player.getCharclass());
					req.setAttribute("player_health", player.getHealth());
					req.setAttribute("player_score", player.getScore());
					req.setAttribute("mission_description", existingCharacter
							.getProperty("mission_description"));
					req.setAttribute("mission_isAccomplished",
							existingCharacter
									.getProperty("mission_isAccomplished"));

					System.out.println(existingCharacter
							.getProperty("mission_description"));

					// Dropdown richtig setzen
					String charclass = existingCharacter.getProperty(
							"player_charclass").toString();
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
					if (existingCharacter.getProperty("image-blob-key") != null) {
						imageBlobKey = existingCharacter.getProperty(
								"image-blob-key").toString();
					}
				}
			} catch (EntityNotFoundException e) {
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

			if (existingCharacter != null) {
				// Print highscore when users is logged in
				QueryResultList<Entity> results = GeekQuestUtils
						.getManipulatedHighScoreQuery(existingCharacter);
				for (Entity result : results) {
					System.out.println(result);
					resp.getWriter().println(
							result.getProperty("player_name") + ""
									+ result.getProperty("player_score")
									+ "<br>");
				}
			}

		} else {
			// User nicht eingeloggt - Highscore anzeigen

			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

			Query q = new Query("character");
			q.addSort("player_score", SortDirection.DESCENDING);
			PreparedQuery pq = ds.prepare(q);
			QueryResultList<Entity> results = pq
					.asQueryResultList(FetchOptions.Builder.withLimit(10));
			Cursor cursor = results.getCursor();
			resp.getWriter().println("Highscore:<br><br>");
			for (Entity result : results) {
				System.out.println(result);
				resp.getWriter().println(
						result.getProperty("player_name") + ""
								+ result.getProperty("player_score") + "<br>");
			}

		}

		// JSP aufrufen
		resp.setContentType("text/html");
		RequestDispatcher jsp = req.getRequestDispatcher("character.jsp");
		jsp.forward(req, resp);

		System.out.println("Test!");

	}

	public void displayCharacterInfo(HttpServletRequest req,
			HttpServletResponse resp) throws IOException {

		// Ausgabe von Basisdaten des Users

		UserService userService = UserServiceFactory.getUserService();
		String thisURL = req.getRequestURI();
		User currentUser = userService.getCurrentUser();

		if (currentUser != null) {
			resp.setContentType("text/html");
			resp.getWriter().println(
					"User Nickname:, " + currentUser.getNickname() + "<br>");
			resp.getWriter().println(
					"User Mail:, " + currentUser.getEmail() + "<br>");
			resp.getWriter().println(
					"User ID:, " + currentUser.getUserId() + "<br>");
		} else {
			resp.sendRedirect(userService.createLoginURL(req.getRequestURI()));
		}

	}

}
