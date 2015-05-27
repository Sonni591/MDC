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

package test;

// [START LocalHighRepDatastoreTest]

import junit.framework.TestCase;
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
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;
import static org.junit.Assert.assertEquals;

public class LocalHighRepDatastoreTest extends TestCase {

	/**
	 * here: set the level of consistency weakness
	 * 	100: indexes never get updated
	 * 	0:	 indexes always get updated
	 */
	// maximum eventual consistency
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig()
					.setDefaultHighRepJobPolicyUnappliedJobPercentage(0));

	private DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

	@Before
	public void setUp() {
		helper.setUp();
	}

	@After
	public void tearDown() {
		helper.tearDown();
	}

	/**
	 * (1) create a character "Frodo" with the highest-score and put in into the datastore (ds.put())
	 * (2) get the character "Frodo" from the datastore (ds.get())
	 * (3) compare both characters
	 *  -> Result: jUnit Test is always true, because put() and get() have strong consistency
	 */
	@Test
	public void testEventuallyConsistentGetCharacter() {
		Entity putCharacter = createTestCharacterFrodo();
		Entity getCharacter = getTestCharacterFrodo();
		
		// compare characters
		assertEquals(putCharacter, getCharacter);
		
		// compare scores of the characters
		assertEquals(putCharacter.getProperty("player_score"), getCharacter.getProperty("player_score"));
		
	}
	
	@Test
	public void test1() {
		testEventuallyConsistentQueryCharacter();
	}
	@Test
	public void test2() {
		testEventuallyConsistentQueryCharacter();
	}
	@Test
	public void test3() {
		testEventuallyConsistentQueryCharacter();
	}
	
	@Test
	public void testEventuallyConsistentQueryCharacter() {
		Entity putCharacter = createTestCharacterFrodo();
		Entity getQueryCharacter = queryHighestScore();
		
		// compare characters
//		assertEquals(putCharacter, getQueryCharacter);
		
		// compare scores of the characters
		assertEquals(putCharacter.getProperty("player_score"), getQueryCharacter.getProperty("player_score"));
		
	}
	

	public Entity createTestCharacterFrodo() {

		Entity character = new Entity("character", "jUnitTest@frodo.com");

		Player player = new Player("jUnitTest@frodo.com", "Frodo", "Hobbit",
				"100", 9999, "");

		character.setProperty("player_id", player.getId());
		character.setProperty("player_name", player.getName());
		character.setProperty("player_charclass", player.getCharclass());
		character.setProperty("player_health", player.getHealth());
		character.setProperty("player_score", player.getScore());

		ds.put(character);

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

}
// [END LocalHighRepDatastoreTest]