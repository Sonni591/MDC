package test;

/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


// [START LocalHighRepDatastoreTest]

import junit.framework.TestCase;
import mdc.GeekQuestUtils;
import mdc.Player;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
import com.google.appengine.api.datastore.dev.HighRepJobPolicy;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;
import static org.junit.Assert.assertEquals;

public class LocalHighRepDatastoreTestHighestScore extends TestCase {

	public static final class Policy implements HighRepJobPolicy {
	    static boolean shouldApply = false;

	    public static void applyAll() {
	      shouldApply = true;
	    }

	    public static void applyNone() {
	      shouldApply = false;
	    }

	    @Override
	    public boolean shouldApplyNewJob(Key entityGroup) {
	      return shouldApply;
	    }

	    @Override
	    public boolean shouldRollForwardExistingJob(Key entityGroup) {
	      return shouldApply;
	    }
	  }
	
	 public final LocalServiceTestHelper helper =
		      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
		          .setAlternateHighRepJobPolicyClass(Policy.class));
	
//	/**
//	 * here: set the level of consistency weakness
//	 * 	100: indexes never get updated
//	 * 	0:	 indexes always get updated
//	 */
//	// maximum eventual consistency
//	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
//			new LocalDatastoreServiceTestConfig()
//					.setDefaultHighRepJobPolicyUnappliedJobPercentage(0));


	private DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

	private QueryResultList<Entity> expectedResult;
	
	@Before
	public void setUp() {	
		helper.setUp();
		
	}

	@After
	public void tearDown() {
		helper.tearDown();
	}


	
	
	
	
	@Test
	public void testWeakConsistentQueryCharacter() {
		
		Policy.applyAll();
		// Do setup here, everything inside will be consistent.
		createTestCharacters();
		Entity character = createTestCharacterFrodoWithoutPut();
		expectedResult = getExpectedHighScoreList(character);
		
		
		Policy.applyNone();
		  // Run code under test here, without consistency.
		Entity putCharacter = createTestCharacterFrodo();
		Entity getCharacter = getTestCharacterFrodo();
		
		QueryResultList<Entity> actualResult = GeekQuestUtils.getManipulatedHighScoreQuery(getCharacter);
		
		for(Entity result : expectedResult) {
			System.out.println(
					result.getProperty("player_name") + "  "
							+ result.getProperty("player_score"));
		}
		
		System.out.println();
		System.out.println();
		
		for(Entity result : actualResult) {
			System.out.println(
					result.getProperty("player_name") + "  "
							+ result.getProperty("player_score"));
		}
		
		assertEquals(expectedResult, actualResult);
		
	}
	
	@Test
	public void testStrongConsistentQueryCharacter() {
		
		Policy.applyAll();
		// Do setup here, everything inside will be consistent.
		
		createTestCharacters();
		Entity character = createTestCharacterFrodoWithoutPut();
		expectedResult = getExpectedHighScoreList(character);
		

		Entity putCharacter = createTestCharacterFrodo();
		Entity getCharacter = getTestCharacterFrodo();
		
		QueryResultList<Entity> actualResult = GeekQuestUtils.getManipulatedHighScoreQuery(getCharacter);
		
		for(Entity result : expectedResult) {
			System.out.println(
					result.getProperty("player_name") + "  "
							+ result.getProperty("player_score"));
		}
		
		System.out.println();
		System.out.println();
		
		for(Entity result : actualResult) {
			System.out.println(
					result.getProperty("player_name") + "  "
							+ result.getProperty("player_score"));
		}
		
		assertEquals(expectedResult, actualResult);
		
	}
	
	public Entity createTestCharacterFrodo() {

		Entity character = new Entity("character", "jUnitTest@frodo.com");

		long highestScore = GeekQuestUtils.getHighestScore();
		
		Player player = new Player("jUnitTest@frodo.com", "Frodo", "Hobbit",
				"100", highestScore+1);

		character.setProperty("player_id", player.getId());
		character.setProperty("player_name", player.getName());
		character.setProperty("player_charclass", player.getCharclass());
		character.setProperty("player_health", player.getHealth());
		character.setProperty("player_score", player.getScore());

		ds.put(character);

		return character;

	}
	
	
	public Entity createTestCharacterFrodoWithoutPut() {

		Entity character = new Entity("character", "jUnitTest@frodo.com");

		long highestScore = GeekQuestUtils.getHighestScore();
		
		Player player = new Player("jUnitTest@frodo.com", "Frodo", "Hobbit",
				"100", highestScore+1);

		character.setProperty("player_id", player.getId());
		character.setProperty("player_name", player.getName());
		character.setProperty("player_charclass", player.getCharclass());
		character.setProperty("player_health", player.getHealth());
		character.setProperty("player_score", player.getScore());

		return character;

	}

	public Entity getTestCharacterFrodo() {

		Key key = KeyFactory.createKey("character", "jUnitTest@frodo.com");

		try {
			Entity character = ds.get(key);
			return character;
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}
	
	public Entity queryHighestScore() {
		Query q = new Query("character");
		q.addSort("player_score",SortDirection.DESCENDING);
		PreparedQuery pq = ds.prepare(q);
		QueryResultList<Entity> results = pq
				.asQueryResultList(FetchOptions.Builder.withLimit(1));
		for (Entity result : results) {
			return result;
		}
		return null;
	}
	
	/**
	 * returns a expected list
	 * 10 players in the highscore with Frodo (character) at the top
	 */
	public QueryResultList<Entity> getExpectedHighScoreList(Entity character) {
		Query q = new Query("character");
		q.addSort("player_score",SortDirection.DESCENDING);
		PreparedQuery pq = ds.prepare(q);
		QueryResultList<Entity> results = pq
				.asQueryResultList(FetchOptions.Builder.withLimit(10));
		
		results.add(0, character);
		results.remove(10);
		
		return results;
		
	}
	
	
	public void createTestCharacters() {
		
		for(int i=1; i<=20; i++) {
		
		Entity character = new Entity("character", "test" + i + "@junit.com");

		long highestScore = GeekQuestUtils.getHighestScore();
		
		Player player = new Player("test" + i + "@junit.com", "testCharacter"+i, "Hobbit",
				"100", i);

		character.setProperty("player_id", player.getId());
		character.setProperty("player_name", player.getName());
		character.setProperty("player_charclass", player.getCharclass());
		character.setProperty("player_health", player.getHealth());
		character.setProperty("player_score", player.getScore());

		ds.put(character);
		
		}
		
	}
	
	


}
// [END LocalHighRepDatastoreTest]
