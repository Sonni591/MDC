package mdc;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.Query.SortDirection;

public class GeekQuestUtils {

	public static long getHighestScore() {

		List<Player> players = ofy().load().type(Player.class).order("-score")
				.limit(1).list();

		for (Player player : players) {
			return (long) player.getScore();
		}

		return 0;
	}

	public static List<Player> getManipulatedHighScoreQuery(Player character) {

		boolean moveInHighscore = false;
		boolean addToHighscore = true;
		int index = -1;
		int indexOld = -1;

		// get the top-10 highscore
		List<Player> players = ofy().load().type(Player.class).order("-score")
				.limit(10).list();

		// check if frodo is in the list
		for (Player player : players) {
			if (player.getId().equals(character.getId())
					&& player.getScore() == character.getScore()) {
				// Frodo is in the high score list - all ok
				// -> nothing to do
				break;
			} else if (player.getId().equals(character.getId())
					&& !(player.getScore() == character.getScore())) {
				// Frodo is in the high score list with the wrong score
				// -> set the correct score and move to the right position
				moveInHighscore = true;
				indexOld = players.indexOf(player);
			}

			// assume that we do not add Frodo to the highscore (and check this
			// later)
			addToHighscore = false;
			// check on which position in the high score Frodo has to be
			// (or skip the if-condition if Frodo is not in the high score)
			if ((long) character.getScore() > (long) player.getScore()) {
				// Frodo is not in the high score list and has to be added
				addToHighscore = true;
				index = players.indexOf(player);
			}
		}

		if (moveInHighscore == true && addToHighscore == true && index != -1
				&& indexOld != -1) {
			// write the better ones; write Frodo; write all except of the last
			// one
			players.remove(indexOld);

			if (indexOld < index) {
				index--;
			}
			players.add(index, character);

		}

		if (addToHighscore == true && index != -1) {
			// add Frodo to the highscore list on the correct index
			players.add(index, character);
			players.remove(10);
		}

		return players;

	}

}
