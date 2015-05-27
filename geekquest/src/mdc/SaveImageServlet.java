package mdc;

import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;

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
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class SaveImageServlet extends HttpServlet {

	private BlobstoreService blobstoreService = BlobstoreServiceFactory
			.getBlobstoreService();

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
		List<BlobKey> blobKeys = blobs.get("myFile");

		if (blobKeys == null || blobKeys.isEmpty()) {
			res.sendRedirect("/");
		} else {
			// res.sendRedirect("/serve?blob-key=" +
			// blobKeys.get(0).getKeyString());
			res.sendRedirect("/geekquest");
		}

		// blob-key zum User abspeicher
		if (blobKeys.get(0).getKeyString() != null) {

			// use transactions
//			int retries = 3;
//			while (true) {
//				Transaction txn = datastore.beginTransaction();
//				try {

					UserService userService = UserServiceFactory
							.getUserService();
					String userId = userService.getCurrentUser().getEmail();
					Key key = KeyFactory.createKey("character", userId);
					Entity character;
					try {
						character = datastore.get(key);
						character.setProperty("image-blob-key", blobKeys.get(0)
								.getKeyString());
						datastore.put(character);
					} catch (EntityNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
						
			
//				} catch (ConcurrentModificationException e) {
//					if (retries == 0) {
//						throw e;
//					}
//					// Allow retry to occur
//					--retries;
//					System.out.println("Transaction - ConcurrentModificationException");
//				} finally {
//					if (txn.isActive()) {
//						txn.rollback();
//					}
//				}
//			}

		}

	}
}
