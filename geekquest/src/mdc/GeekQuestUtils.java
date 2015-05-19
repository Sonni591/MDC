package mdc;

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

		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		Query q = new Query("character");
		q.addSort("player_score", SortDirection.DESCENDING);
		PreparedQuery pq = ds.prepare(q);
		QueryResultList<Entity> results = pq
				.asQueryResultList(FetchOptions.Builder.withLimit(1));
		for (Entity result : results) {
			return (long) result.getProperty("player_score");
		}
		return 0;
	}

	public static QueryResultList<Entity> getManipulatedHighScoreQuery(
			Entity character) {

		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		boolean moveInHighscore = false;
		boolean addToHighscore = true;
		int index = -1;
		int indexOld = -1;

		// get the top-10 highscore
		Query q = new Query("character");
		q.addSort("player_score", SortDirection.DESCENDING);
		PreparedQuery pq = ds.prepare(q);
		QueryResultList<Entity> results = pq
				.asQueryResultList(FetchOptions.Builder.withLimit(10));

		// check if frodo is in the list
		for (Entity result : results) {
			if (result.getProperty("player_id").equals(
					character.getProperty("player_id"))
					&& result.getProperty("player_score").equals(
							character.getProperty("player_score"))) {
				// Frodo is in the high score list - all ok
				// -> nothing to do
				break;
			} else if (result.getProperty("player_id").equals(
					character.getProperty("player_id"))
					&& !(result.getProperty("player_score").equals(character
							.getProperty("player_score")))) {
				// Frodo is in the high score list with the wrong score
				// -> set the correct score and move to the right position
				moveInHighscore = true;
				indexOld = results.indexOf(result);
			}

			// assume that we do not add Frodo to the highscore (and check this
			// later)
			addToHighscore = false;
			// check on which position in the high score Frodo has to be
			// (or skip the if-condition if Frodo is not in the high score)
			if ((long) character.getProperty("player_score") > (long) result
					.getProperty("player_score")) {
				// Frodo is not in the high score list and has to be added
				addToHighscore = true;
				index = results.indexOf(result);
			}
		}

		if (moveInHighscore == true && addToHighscore == true && index != -1
				&& indexOld != -1) {
			// write the better ones; write Frodo; write all except of the last
			// one
			results.remove(indexOld);

			if (indexOld < index) {
				index--;
			}
			results.add(index, character);

		}

		if (addToHighscore == true && index != -1) {
			// add Frodo to the highscore list on the correct index
			results.add(index, character);
			results.remove(10);
		}

		return results;

	}

}
