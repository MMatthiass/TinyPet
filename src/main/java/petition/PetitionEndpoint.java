package petition;

import java.text.DateFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.lang.Boolean;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.PreparedQuery.TooManyResultsException;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query.*;



@Api(name = "petitionApi",
	version = "v1",
	audiences = "927375242383-t21v9ml38tkh2pr30m4hqiflkl3jfohl.apps.googleusercontent.com",
	 clientIds = "927375242383-t21v9ml38tkh2pr30m4hqiflkl3jfohl.apps.googleusercontent.com",
	namespace =
	@ApiNamespace(
		   ownerDomain = "https://sitedepetition.ew.r.appspot.com",
		   ownerName = "https://sitedepetition.ew.r.appspot.com",
		   packagePath = "")
	)
public class PetitionEndpoint {
	
	@ApiMethod(name = "getAllPetition", path = "getAllPetition", httpMethod = HttpMethod.GET)
	public CollectionResponse<Entity> getAllPetition(@Nullable @Named("next") String cursor) {

		FetchOptions fetchOptions = FetchOptions.Builder.withLimit(15); 
		if (cursor != null && !cursor.equals("")) {
		    fetchOptions.startCursor(Cursor.fromWebSafeString(cursor)); // Where we left off
		  }
	
		Query q = new Query("Petition")
				 .addSort("dateCreation",SortDirection.DESCENDING);;

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
		
		QueryResultList<Entity> result = pq.asQueryResultList(fetchOptions);  
		cursor = result.getCursor().toWebSafeString();
		
		return CollectionResponse.<Entity>builder().setItems(result).setNextPageToken(cursor).build();
		}
	
	@ApiMethod(name = "getMyPetition", path = "getMyPetition", httpMethod = HttpMethod.GET)
	public List<Entity> getMyPetition(@Named("userEmail") String userEmail) {

		Query q = new Query("Petition").setFilter(new FilterPredicate("createur", FilterOperator.EQUAL, userEmail));
	
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
		List<Entity> retour = pq.asList(FetchOptions.Builder.withLimit(15));
		
		return retour;
	}
	
	
	
	@ApiMethod(name = "getpetitionbysign", path = "getPetitionBySign", httpMethod = HttpMethod.GET)
	public List<Entity> getpetitionbysign() {
		Query q = new Query("Petition").addSort("compteur", Query.SortDirection.DESCENDING);

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
		List<Entity> retour = pq.asList(FetchOptions.Builder.withLimit(15));
		
		return retour;
	}
	
	@ApiMethod(name = "voterPetition", httpMethod = HttpMethod.GET)
	public Entity voterPetition(@Named("name") String name, @Named("userEmail") String userEmail) {

		Query q = new Query("Petition").setFilter(new FilterPredicate("namePet", FilterOperator.EQUAL, name));
		
		System.out.println("q : "+ q);
		System.out.println("q : "+ q);
		System.out.println("q : "+ q);
		System.out.println("q : "+ q);
		
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
		
		System.out.println("pq : "+ pq);
		System.out.println("pq : "+ pq);
		System.out.println("pq : "+ pq);
		System.out.println("pq : "+ pq);
		Entity entity = pq.asSingleEntity();
		
		System.out.println("entity : "+ entity);
		System.out.println("entity : "+ entity);
		System.out.println("entity : "+ entity);
		System.out.println("entity : "+ entity);
		
		if (entity != null) {
			System.out.println("Pause 0 ");
			long nombrevote = (long)entity.getProperty("compteur");
			System.out.println("Pause 1 " + nombrevote);
			entity.setProperty("compteur", nombrevote + 1);
			System.out.println("Pause 2 " + entity);
			ArrayList<String> votants = new ArrayList();
			if (entity.getProperty("votants") != null)
			{
				votants = (ArrayList<String>)entity.getProperty("votants");
			}
			System.out.println("Pause 3 " + votants);
			votants.add(userEmail);
			System.out.println("Pause 4 ");
			entity.setProperty("votants", votants);
			System.out.println("Pause 5 ");
			
		
			datastore.put(entity);
		}
		
		return entity;
		
	}
	
	@ApiMethod(name = "checkVotePetition", httpMethod = HttpMethod.GET)
	public Entity checkVotePetition(@Named("name") String name, @Named("userEmail") String userEmail) {

		Query q = new Query("Petition").setFilter(CompositeFilterOperator.and(
				new FilterPredicate("namePet", FilterOperator.EQUAL, name),
				new FilterPredicate("votants", FilterOperator.EQUAL, userEmail)));
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
		Entity entity = pq.asSingleEntity();

		System.out.println("entity : "+ entity);
		System.out.println("entity : "+ entity);
		System.out.println("entity : "+ entity);
		System.out.println("entity : "+ entity);
		
		return entity;
	}
	
	
	@ApiMethod(name = "savePetition", httpMethod = HttpMethod.POST)
	public Entity savePetition(@Named("userEmail") String userEmail, @Named("titre") String titre, @Named("description") String description, @Named("tags") String tags) {

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		DateFormat format = new SimpleDateFormat("ss:mm:HHdd/MM/yyyy");
		DateFormat format2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		
		Date date = Calendar.getInstance().getTime();
		
		
		Entity pet = new Entity("Petition", format.format(date) + "User" +userEmail);
		pet.setProperty("namePet", format.format(date) + userEmail);
		pet.setProperty("titre", titre);
		pet.setProperty("description", description);
		pet.setProperty("tags",  tags);
		pet.setProperty("createur", userEmail);
		pet.setProperty("compteur", 0);
		pet.setProperty("votants", new ArrayList<String>());
		pet.setProperty("dateCreation", format2.format(date));
		datastore.put(pet);
		
		return pet;
		
	}
	
	@ApiMethod(name = "cleanRepository", path = "cleanRepository", httpMethod = HttpMethod.GET)
	public List<Key> cleanRepository() {
		List<Key> retour = new ArrayList<>();
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Query q = new Query("Petition");
		PreparedQuery pq = datastore.prepare(q);
		List<Entity> result = pq.asList(FetchOptions.Builder.withDefaults());
		for (Entity entity : result) {
			retour.add(entity.getKey());
			datastore.delete(entity.getKey());	
		}
		return retour;
	}
	
	@ApiMethod(name = "populateRepository", httpMethod = HttpMethod.GET)
	public List<Key> populateRepository() {
		List<Key> retour = new ArrayList<>();

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		// Create users
		for (int i = 0; i < 100; i++) {
			
			int compteur = 0;
			
			Random r = new Random();
			int nbVotes = r.nextInt(20);
			
			for (int j = 0; j < nbVotes; j++) {
				compteur = compteur + 1;
			}

			DateFormat format = new SimpleDateFormat("ss:mm:HH dd/MM/yyyy");
			DateFormat format2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			
			Date date = Calendar.getInstance().getTime();
			
			
			Entity pet = new Entity("Petition", format.format(date) + "P" + i + "_" + "U"+i);
			pet.setProperty("namePet", format.format(date) + "U"+i);
			pet.setProperty("titre", "My name is petition" + i);
			pet.setProperty("description", "Cette petition a pour but de " + i);
			pet.setProperty("tags",  "tags 1, tags 2, tags 3");
			pet.setProperty("createur", "user" + i + "@gmail.com");
			pet.setProperty("compteur", compteur);
			pet.setProperty("votants", new ArrayList<String>());
			pet.setProperty("dateCreation", format2.format(date));
			datastore.put(pet);
			retour.add(pet.getKey());
		}
		return retour;
	}
}
