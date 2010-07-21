package net.sf.jtmdb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.sf.jtmdb.Log.Verbosity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This is the class that represents a Person. It also provides static methods
 * for searching for a person and for getting a specific person. Has two
 * "flavors". The normal one and a reduced one. The reduced is returned when
 * searching for people and is missing some fields that are present when getting
 * the info for a specific person.
 * 
 * @author Savvas Dalkitsis
 */
public class Person implements Serializable {

	private static final long serialVersionUID = -6310264485167208111L;

	/**
	 * The name of the person.
	 */
	private String name;
	/**
	 * The profile image of the person.
	 */
	private PersonProfile profile;
	/**
	 * The url of the person.
	 */
	private URL url;
	/**
	 * The ID of the person.
	 */
	private int ID;
	/**
	 * The biography of the person.
	 */
	private String biography;
	/**
	 * The popularity of the Person;
	 */
	private int popularity;
	/**
	 * The json string that created this Person object.
	 */
	private String jsonOrigin;

	/**
	 * Denotes whether the person object is reduced.
	 */
	private boolean isReduced;

	// Only in full profile

	/**
	 * The birthplace of the person. Not present in reduced form.
	 */
	private String birthPlace;
	/**
	 * The number of known movies of the person. Not present in reduced form.
	 */
	private int knownMovies;
	/**
	 * The filmography of the person. Not present in reduced form.
	 */
	private Set<FilmographyInfo> filmography = new LinkedHashSet<FilmographyInfo>();
	/**
	 * The birthday of the person. Not present in reduced form.
	 */
	private Date birthday;
	/**
	 * The aliases of the person. Not present in reduced form.
	 */
	private Set<String> aka = new LinkedHashSet<String>();

	/**
	 * Construct a person object from a JSON object. The supplied boolean
	 * denotes if the JSON object supplied contains reduced information about
	 * the Person (see class description {@link Person}).
	 * 
	 * @param jsonObject
	 *            The JSON object describing the Person.
	 * @param isReduced
	 *            If true, the JSON object contains reduced information (see
	 *            class description {@link Person}).
	 */
	public Person(JSONObject jsonObject, boolean isReduced) {
		Log.log("Creating Person object from JSONObject", Verbosity.VERBOSE);
		setReduced(isReduced);
		parseJSON(jsonObject);
	}

	/**
	 * 
	 * Construct a person object from a JSON array containing the JSON object
	 * describing the Person. The supplied boolean denotes if the JSON object
	 * supplied contains reduced information about the Person (see class
	 * description {@link Person}).
	 * 
	 * @param jsonObjectInArray
	 *            A JSON array containing the JSON object describing the Person.
	 * @param isReduced
	 *            If true, the JSON object contains reduced information (see
	 *            class description {@link Person}).
	 */
	public Person(JSONArray jsonObjectInArray, boolean isReduced) {
		Log.log("Creating Person object from JSONArray", Verbosity.VERBOSE);
		setReduced(isReduced);
		parseJSON(jsonObjectInArray);
	}

	/**
	 * The json string that created this Person object.
	 * 
	 * @return The json string that created this Person object.
	 */
	public String getJsonOrigin() {
		return jsonOrigin;
	}

	/**
	 * The prettyprinted json string that created this Person object.
	 * 
	 * @param indentFactor
	 *            The number of spaces to add to each level of indentation.
	 * @return The json string that created this Person object.
	 */
	public String getJsonOrigin(int indentFactor) {
		try {
			return new JSONObject(jsonOrigin).toString(indentFactor);
		} catch (JSONException e) {
			Log.log(e, Verbosity.ERROR);
			return null;
		}
	}

	/**
	 * The name of the person.
	 * 
	 * @return The name of the person.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the person.
	 * 
	 * @param name
	 *            The name of the person.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * The birthplace of the person. Not present in reduced form (see class
	 * description {@link Person} and method {@link #isReduced()}).
	 * 
	 * @return The birthplace of the person.
	 */
	public String getBirthPlace() {
		return birthPlace;
	}

	/**
	 * Sets the birthplace of the person.
	 * 
	 * @param birthPlace
	 *            The birthplace of the person.
	 */
	public void setBirthPlace(String birthPlace) {
		this.birthPlace = birthPlace;
	}

	/**
	 * The profile image of the person.
	 * 
	 * @return The profile image of the person.
	 */
	public PersonProfile getProfile() {
		return profile;
	}

	/**
	 * Sets the profile image of the person.
	 * 
	 * @param profile
	 *            The profile image of the person.
	 */
	public void setProfile(PersonProfile profile) {
		this.profile = profile;
	}

	/**
	 * The number of known movies of the person. Not present in reduced form
	 * (see class description {@link Person} and method {@link #isReduced()}).
	 * 
	 * @return The number of known movies of the person.
	 */
	public int getKnownMovies() {
		return knownMovies;
	}

	/**
	 * Sets the number of known movies of the person.
	 * 
	 * @param knownMovies
	 *            The number of known movies of the person.
	 */
	public void setKnownMovies(int knownMovies) {
		this.knownMovies = knownMovies;
	}

	/**
	 * The filmography of the person. Not present in reduced form (see class
	 * description {@link Person} and method {@link #isReduced()}).
	 * 
	 * @return The filmography of the person.
	 */
	public Set<FilmographyInfo> getFilmography() {
		return filmography;
	}

	/**
	 * Sets the filmography of the person.
	 * 
	 * @param filmography
	 *            The filmography of the person.
	 */
	public void setFilmography(Set<FilmographyInfo> filmography) {
		this.filmography = filmography;
	}

	/**
	 * The url of the person.
	 * 
	 * @return The url of the person.
	 */
	public URL getUrl() {
		return url;
	}

	/**
	 * Sets the url of the person.
	 * 
	 * @param url
	 *            The url of the person.
	 */
	public void setUrl(URL url) {
		this.url = url;
	}

	/**
	 * The ID of the person.
	 * 
	 * @return The ID of the person.
	 */
	public int getID() {
		return ID;
	}

	/**
	 * Sets the popularity of the person.
	 * 
	 * @param popularity
	 *            The popularity of the person.
	 */
	public void setPopularity(int popularity) {
		this.popularity = popularity;
	}

	/**
	 * The popularity of the person.
	 * 
	 * @return The popularity of the person.
	 */
	public int getPopularity() {
		return popularity;
	}

	/**
	 * Sets the ID of the person.
	 * 
	 * @param iD
	 *            The ID of the person.
	 */
	public void setID(int iD) {
		ID = iD;
	}

	/**
	 * The birthday of the person. Not present in reduced form (see class
	 * description {@link Person} and method {@link #isReduced()}).
	 * 
	 * @return The birthday of the person.
	 */
	public Date getBirthday() {
		return birthday;
	}

	/**
	 * Sets the birthday of the person.
	 * 
	 * @param birthday
	 *            The birthday of the person.
	 */
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	/**
	 * The aliases of the person. Not present in reduced form (see class
	 * description {@link Person} and method {@link #isReduced()}).
	 * 
	 * @return The aliases of the person.
	 */
	public Set<String> getAka() {
		return aka;
	}

	/**
	 * Sets the aliases of the person.
	 * 
	 * @param aka
	 *            The aliases of the person.
	 */
	public void setAka(Set<String> aka) {
		this.aka = aka;
	}

	/**
	 * Sets whether the Person contains reduced information (see class
	 * description {@link Person}).
	 * 
	 * @param isReduced
	 *            True if the Person has reduced fields set.
	 */
	public void setReduced(boolean isReduced) {
		this.isReduced = isReduced;
	}

	/**
	 * If true, the Person object has reduced fields set (see class description
	 * {@link Person}).
	 * 
	 * @return True if the Person has reduced fields set.
	 */
	public boolean isReduced() {
		return isReduced;
	}

	/**
	 * The biography of the person.
	 * 
	 * @return The biography of the person.
	 */
	public String getBiography() {
		return biography;
	}

	/**
	 * Sets the biography of the person.
	 * 
	 * @param biography
	 *            The biography of the person.
	 */
	public void setBiography(String biography) {
		this.biography = biography;
	}

	/**
	 * Parses a JSON object wrapped in a JSON array and sets the Person fields.
	 * 
	 * @param jsonArray
	 *            The JSON array containing the JSON object that describes the
	 *            Person.
	 */
	public boolean parseJSON(JSONArray jsonArray) {
		try {
			return parseJSON(jsonArray.getJSONObject(0));
		} catch (JSONException e) {
			Log.log(e, Verbosity.ERROR);
		}
		return false;
	}

	/**
	 * Parses a JSON object and sets the Person fields.
	 * 
	 * @param jsonObject
	 *            The JSON object that describes the Person.
	 */
	public boolean parseJSON(JSONObject jsonObject) {
		try {
			jsonOrigin = jsonObject.toString();
			setPopularity(jsonObject.getInt("popularity"));
			setName(jsonObject.getString("name"));
			try {
				setUrl(new URL(jsonObject.getString("url")));
			} catch (MalformedURLException e) {
				Log.log(e, Verbosity.ERROR);
				setUrl(null);
			}
			setID(jsonObject.getInt("id"));
			JSONArray profile = jsonObject.getJSONArray("profile");
			for (int i = 0; i < profile.length(); i++) {
				JSONObject p = profile.getJSONObject(i).getJSONObject("image");
				String id = p.getString("id");
				URL url = null;
				try {
					url = new URL(p.getString("url"));
				} catch (MalformedURLException e) {
					Log.log(e, Verbosity.ERROR);
					e.printStackTrace();
				}
				String size = p.getString("size");
				PersonProfile.Size ps = PersonProfile.Size.ORIGINAL;
				if (size.equalsIgnoreCase("thumb")) {
					ps = PersonProfile.Size.THUMB;
				} else if (size.equalsIgnoreCase("profile")) {
					ps = PersonProfile.Size.PROFILE;
				}
				PersonProfile prof = getProfile();
				if (prof == null) {
					prof = new PersonProfile(id);
				}
				prof.setImage(ps, url);
				setProfile(prof);
			}

			if (!isReduced()) {
				setBiography(jsonObject.getString("biography"));
				setBirthPlace(jsonObject.getString("birthplace"));
				setKnownMovies(jsonObject.getInt("known_movies"));
				String date = jsonObject.getString("birthday");
				if (date != null && !date.equals("")) {
					String year = date.substring(0, date.indexOf("-"));
					date = date.substring(date.indexOf("-") + 1);
					String month = date.substring(0, date.indexOf("-"));
					date = date.substring(date.indexOf("-") + 1);
					Calendar c = Calendar.getInstance();
					try {
						c.set(Calendar.YEAR, Integer.parseInt(year));
						c.set(Calendar.MONTH, Integer.parseInt(month) - 1);
						c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date));
					} catch (NumberFormatException e) {
						Log.log(e, Verbosity.ERROR);
					}
					setBirthday(c.getTime());
				}
				JSONArray knownAs = jsonObject.getJSONArray("known_as");
				for (int i = 0; i < knownAs.length(); i++) {
					getAka().add(knownAs.getJSONObject(i).getString("name"));
				}
				JSONArray filmArray = jsonObject.getJSONArray("filmography");
				for (int i = 0; i < filmArray.length(); i++) {
					JSONObject film = filmArray.getJSONObject(i);
					String filmName = film.getString("name");
					String filmCharacter = film.getString("character");
					URL filmUrl = null;
					try {
						filmUrl = new URL(film.getString("url"));
					} catch (MalformedURLException e) {
						Log.log(e, Verbosity.ERROR);
					}
					int filmID = film.getInt("id");
					String filmJob = film.getString("job");
					String filmDepartment = film.getString("department");
					getFilmography().add(
							new FilmographyInfo(filmName, filmCharacter,
									filmUrl, filmID, filmJob, filmDepartment,
									film.toString()));
				}
			}
		} catch (JSONException e) {
			Log.log(e, Verbosity.ERROR);
		}
		return false;
	}

	/**
	 * Searches for people and returns full flavors. The string supplied can
	 * contain spaces. Returns a list of Person objects with the full form (see
	 * class description {@link Person} and method {@link #isReduced()}). Will
	 * return null if a valid API key was not supplied to the
	 * {@link GeneralSettings}
	 * 
	 * @param name
	 *            The name of the person to search for.
	 * @return A list of Person objects with the full form (see class
	 *         description {@link Person} and method {@link #isReduced()}).Will
	 *         return null if a valid API key was not supplied to the
	 *         {@link GeneralSettings}
	 * @throws IOException
	 * @throws JSONException
	 */
	public static List<Person> deepSearch(String name) throws IOException,
			JSONException {
		Log.log("Performing a deep Person search for \"" + name + "\"",
				Verbosity.NORMAL);
		try {
			name = URLEncoder.encode(name, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			name = name.replaceAll(" ", "%20");
		}
		if (GeneralSettings.getApiKey() != null
				&& !GeneralSettings.getApiKey().equals("")) {
			if (name != null && !name.equals("")) {
				try {
					URL call = new URL(GeneralSettings.BASE_URL
							+ GeneralSettings.PERSON_SEARCH_URL
							+ GeneralSettings.getAPILanguage() + "/"
							+ GeneralSettings.API_MODE_URL
							+ GeneralSettings.getApiKey() + "/" + name);
					URLConnection yc = call.openConnection();
					BufferedReader in = new BufferedReader(
							new InputStreamReader(yc.getInputStream()));
					String inputLine;
					StringBuffer jsonString = new StringBuffer();
					while ((inputLine = in.readLine()) != null) {
						jsonString.append(inputLine);
					}
					in.close();
					List<Person> results = new LinkedList<Person>();
					if (!jsonString.toString().equals("[\"Nothing found.\"]")) {
						JSONArray jsonArray = new JSONArray(jsonString
								.toString());
						for (int i = 0; i < jsonArray.length(); i++) {
							results.add(getInfo(jsonArray.getJSONObject(i)
									.getInt("id")));
						}
					} else {
						Log.log("Search for \"" + name
								+ "\" returned no results", Verbosity.NORMAL);
					}
					return results;
				} catch (IOException e) {
					Log.log(e, Verbosity.ERROR);
					throw e;
				} catch (JSONException e) {
					Log.log(e, Verbosity.ERROR);
					throw e;
				}
			} else {
				Log.log("Cannot search for a null or empty string",
						Verbosity.ERROR);
			}
		} else {
			Log.log("Error with the API key", Verbosity.ERROR);
		}
		return null;
	}

	/**
	 * Searches for people. The string supplied can contain spaces. Returns a
	 * list of Person objects with the reduced form (see class description
	 * {@link Person} and method {@link #isReduced()}). Will return null if a
	 * valid API key was not supplied to the {@link GeneralSettings}
	 * 
	 * @param name
	 *            The name of the person to search for.
	 * @return A list of Person objects with the reduced form (see class
	 *         description {@link Person} and method {@link #isReduced()}).Will
	 *         return null if a valid API key was not supplied to the
	 *         {@link GeneralSettings}
	 * @throws IOException
	 * @throws JSONException
	 */
	public static List<Person> search(String name) throws IOException,
			JSONException {
		Log.log("Performing a Person search for \"" + name + "\"",
				Verbosity.NORMAL);
		try {
			name = URLEncoder.encode(name, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			name = name.replaceAll(" ", "%20");
		}
		if (GeneralSettings.getApiKey() != null
				&& !GeneralSettings.getApiKey().equals("")) {
			if (name != null && !name.equals("")) {
				try {
					URL call = new URL(GeneralSettings.BASE_URL
							+ GeneralSettings.PERSON_SEARCH_URL
							+ GeneralSettings.getAPILanguage() + "/"
							+ GeneralSettings.API_MODE_URL
							+ GeneralSettings.getApiKey() + "/" + name);
					URLConnection yc = call.openConnection();
					BufferedReader in = new BufferedReader(
							new InputStreamReader(yc.getInputStream()));
					String inputLine;
					StringBuffer jsonString = new StringBuffer();
					while ((inputLine = in.readLine()) != null) {
						jsonString.append(inputLine);
					}
					in.close();
					List<Person> results = new LinkedList<Person>();
					if (!jsonString.toString().equals("[\"Nothing found.\"]")) {
						JSONArray jsonArray = new JSONArray(jsonString
								.toString());
						for (int i = 0; i < jsonArray.length(); i++) {
							results.add(new Person(jsonArray.getJSONObject(i),
									true));
						}
					} else {
						Log.log("Search for \"" + name
								+ "\" returned no results", Verbosity.NORMAL);
					}
					return results;
				} catch (IOException e) {
					Log.log(e, Verbosity.ERROR);
					throw e;
				} catch (JSONException e) {
					Log.log(e, Verbosity.ERROR);
					throw e;
				}
			} else {
				Log.log("Cannot search for a null or empty string",
						Verbosity.ERROR);
			}
		} else {
			Log.log("Error with the API key", Verbosity.ERROR);
		}
		return null;
	}

	/**
	 * Gets the info for a specific Person (by ID). Returns a Person object with
	 * the normal form (see class description {@link Person} and method
	 * {@link #isReduced()}). Will return null if a valid API key was not
	 * supplied to the {@link GeneralSettings} or if the supplied ID did not
	 * correspond to a Person.
	 * 
	 * @param ID
	 *            The ID of the Person.
	 * @return A Person object with the normal form (see class description
	 *         {@link Person} and method {@link #isReduced()}). Will return null
	 *         if a valid API key was not supplied to the
	 *         {@link GeneralSettings} or if the supplied ID did not correspond
	 *         to a Person.
	 * @throws IOException
	 * @throws JSONException
	 */
	public static Person getInfo(int ID) throws IOException, JSONException {
		Log.log("Getting info for Person with id " + ID, Verbosity.NORMAL);
		if (GeneralSettings.getApiKey() != null
				&& !GeneralSettings.getApiKey().equals("")) {
			try {
				URL call = new URL(GeneralSettings.BASE_URL
						+ GeneralSettings.PERSON_GETINFO_URL
						+ GeneralSettings.getAPILanguage() + "/"
						+ GeneralSettings.API_MODE_URL
						+ GeneralSettings.getApiKey() + "/" + ID);
				URLConnection yc = call.openConnection();
				BufferedReader in = new BufferedReader(new InputStreamReader(yc
						.getInputStream()));
				String inputLine;
				StringBuffer jsonString = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					jsonString.append(inputLine);
				}
				in.close();
				if (!jsonString.toString().equals("[\"Nothing found.\"]")) {
					JSONArray jsonArray = new JSONArray(jsonString.toString());
					return new Person(jsonArray, false);
				} else {
					Log.log("Getting info for Person with ID " + ID
							+ " returned no results", Verbosity.NORMAL);
				}
			} catch (IOException e) {
				Log.log(e, Verbosity.ERROR);
				throw e;
			} catch (JSONException e) {
				Log.log(e, Verbosity.ERROR);
				throw e;
			}
		} else {
			Log.log("Error with the API key", Verbosity.ERROR);
		}
		return null;
	}
}
