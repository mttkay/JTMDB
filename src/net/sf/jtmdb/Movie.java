package net.sf.jtmdb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This is the class that represents a Movie. It also provides static methods
 * for searching for a movie and for getting a specific movie. Has two
 * "flavors". The normal one and a reduced one. The reduced is returned when
 * searching for movies and is missing some fields that are present when getting
 * the info for a specific movie.
 * 
 * @author Savvas Dalkitsis
 */
public class Movie implements Serializable {

	private static final long serialVersionUID = 6802995614207632594L;

	/**
	 * The name of the movie.
	 */
	private String name;
	/**
	 * The alternative name for the movie.
	 */
	private String alternativeName;
	/**
	 * The ID of the movie.
	 */
	private int ID;
	/**
	 * The imdb ID of the movie.
	 */
	private String imdbID;
	/**
	 * The url of the movie.
	 */
	private URL url;
	/**
	 * The movie overview.
	 */
	private String overview;
	/**
	 * The rating of the movie.
	 */
	private double rating;
	/**
	 * The release date of the movie.
	 */
	private Date releasedDate;
	/**
	 * The images of the movie.
	 */
	private MovieImages images = new MovieImages();

	/**
	 * Denotes whether the movie object is reduced.
	 */
	private boolean isReduced;

	// Only in full profile.

	/**
	 * The movie tagline. Not present in reduced form.
	 */
	private String tagline;
	/**
	 * The movie certification. Not present in reduced form.
	 */
	private String certification;
	/**
	 * The movie runtime. Not present in reduced form.
	 */
	private int runtime;
	/**
	 * The movie budget. Not present in reduced form.
	 */
	private int budget;
	/**
	 * The movie revenue. Not present in reduced form.
	 */
	private int revenue;
	/**
	 * The movie homepage. Not present in reduced form.
	 */
	private URL homepage;
	/**
	 * The movie trailer. Not present in reduced form.
	 */
	private URL trailer;
	/**
	 * The movie cast. Not present in reduced form.
	 */
	private Set<CastInfo> cast = new LinkedHashSet<CastInfo>();
	/**
	 * The movie Genres. Not present in reduced form.
	 */
	private Set<Genre> genres = new LinkedHashSet<Genre>();

	/**
	 * Construct a movie object from a JSON object. The supplied boolean denotes
	 * if the JSON object supplied contains reduced information about the Movie
	 * (see class description {@link Movie}).
	 * 
	 * @param jsonObject
	 *            The JSON object describing the Movie.
	 * @param isReduced
	 *            If true, the JSON object contains reduced information (see
	 *            class description {@link Movie}).
	 */
	public Movie(JSONObject jsonObject, boolean isReduced) {
		setReduced(isReduced);
		parseJSON(jsonObject);
	}

	/**
	 * Construct a movie object from a JSON array containing the JSON object
	 * describing the Movie. The supplied boolean denotes if the JSON object
	 * supplied contains reduced information about the Movie (see class
	 * description).
	 * 
	 * @param jsonObjectInArray
	 *            A JSON array containing the JSON object describing the Movie.
	 * @param isReduced
	 *            If true, the JSON object contains reduced information (see
	 *            class description).
	 */
	public Movie(JSONArray jsonObjectInArray, boolean isReduced) {
		setReduced(isReduced);
		parseJSON(jsonObjectInArray);
	}

	/**
	 * If true, the Movie object has reduced fields set (see class description
	 * {@link Movie}).
	 * 
	 * @return True if the Movie has reduced fields set.
	 */
	public boolean isReduced() {
		return isReduced;
	}

	/**
	 * Sets whether the Movie contains reduced information (see class
	 * description {@link Movie}).
	 * 
	 * @param isReduced
	 *            True if the Movie has reduced fields set.
	 */
	public void setReduced(boolean isReduced) {
		this.isReduced = isReduced;
	}

	/**
	 * The name of the Movie.
	 * 
	 * @return The name of the Movie.
	 */
	public String getName() {
		return name;
	}

	/**
	 * The Movie alternative name.
	 * 
	 * @return The Movie alternative name.
	 */
	public String getAlternativeName() {
		return alternativeName;
	}

	/**
	 * The Movie tagline. Not present in reduced form (see class description
	 * {@link Movie} and method {@link #isReduced()}).
	 * 
	 * @return The Movie tagline. Not present in reduced form (see class
	 *         description {@link Movie} and method {@link #isReduced()}).
	 */
	public String getTagline() {
		return tagline;
	}

	/**
	 * The Movie certification. Not present in reduced form (see class
	 * description {@link Movie} and method {@link #isReduced()}).
	 * 
	 * @return The Movie certification. Not present in reduced form (see class
	 *         description {@link Movie} and method {@link #isReduced()}).
	 */
	public String getCertification() {
		return certification;
	}

	/**
	 * The Movie Genres. Not present in reduced form (see class description
	 * {@link Movie} and method {@link #isReduced()}).
	 * 
	 * @return The Movie Genres. Not present in reduced form (see class
	 *         description {@link Movie} and method {@link #isReduced()}).
	 */
	public Set<Genre> getGenres() {
		return genres;
	}

	/**
	 * The Movie ID.
	 * 
	 * @return The Movie ID.
	 */
	public int getID() {
		return ID;
	}

	/**
	 * The Movie Imdb ID.
	 * 
	 * @return The Movie Imdb ID.
	 */
	public String getImdbID() {
		return imdbID;
	}

	/**
	 * The Movie Url.
	 * 
	 * @return The Movie Url.
	 */
	public URL getUrl() {
		return url;
	}

	/**
	 * The overview of the Movie.
	 * 
	 * @return The overview of the Movie.
	 */
	public String getOverview() {
		return overview;
	}

	/**
	 * The Movie rating.
	 * 
	 * @return The Movie rating.
	 */
	public double getRating() {
		return rating;
	}

	/**
	 * The Movie release Date.
	 * 
	 * @return The Movie release Date.
	 */
	public Date getReleasedDate() {
		return releasedDate;
	}

	/**
	 * The Movie runtime. Not present in reduced form (see class description
	 * {@link Movie} and method {@link #isReduced()}).
	 * 
	 * @return The Movie runtime. Not present in reduced form (see class
	 *         description {@link Movie} and method {@link #isReduced()}).
	 */
	public int getRuntime() {
		return runtime;
	}

	/**
	 * The Movie budget. Not present in reduced form (see class description
	 * {@link Movie} and method {@link #isReduced()}).
	 * 
	 * @return The Movie budget. Not present in reduced form (see class
	 *         description {@link Movie} and method {@link #isReduced()}).
	 */
	public int getBudget() {
		return budget;
	}

	/**
	 * The Movie revenue. Not present in reduced form (see class description
	 * {@link Movie} and method {@link #isReduced()}).
	 * 
	 * @return The Movie revenue. Not present in reduced form (see class
	 *         description {@link Movie} and method {@link #isReduced()}).
	 */
	public int getRevenue() {
		return revenue;
	}

	/**
	 * The Movie home page. Not present in reduced form (see class description
	 * {@link Movie} and method {@link #isReduced()}).
	 * 
	 * @return The Movie home page. Not present in reduced form (see class
	 *         description {@link Movie} and method {@link #isReduced()}).
	 */
	public URL getHomepage() {
		return homepage;
	}

	/**
	 * The Movie trailer. Not present in reduced form (see class description
	 * {@link Movie} and method {@link #isReduced()}).
	 * 
	 * @return The Movie trailer. Not present in reduced form (see class
	 *         description {@link Movie} and method {@link #isReduced()}).
	 */
	public URL getTrailer() {
		return trailer;
	}

	/**
	 * The Movie cast. Not present in reduced form (see class description
	 * {@link Movie} and method {@link #isReduced()}).
	 * 
	 * @return The Movie cast. Not present in reduced form (see class
	 *         description {@link Movie} and method {@link #isReduced()}).
	 */
	public Set<CastInfo> getCast() {
		return cast;
	}

	/**
	 * The Movie images.
	 * 
	 * @return The Movie images.
	 */
	public MovieImages getImages() {
		return images;
	}

	/**
	 * Sets the name of the Movie.
	 * 
	 * @param name
	 *            The name of the Movie.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the alternative name of the Movie.
	 * 
	 * @param alternativeName
	 *            The alternative name of the Movie.
	 */
	public void setAlternativeName(String alternativeName) {
		this.alternativeName = alternativeName;
	}

	/**
	 * Sets the tagline of the Movie.
	 * 
	 * @param tagline
	 *            The tagline of the Movie.
	 */
	public void setTagline(String tagline) {
		this.tagline = tagline;
	}

	/**
	 * Sets the certification of the Movie.
	 * 
	 * @param certification
	 *            The certification of the Movie.
	 */
	public void setCertification(String certification) {
		this.certification = certification;
	}

	/**
	 * Sets the ID of the Movie.
	 * 
	 * @param iD
	 *            The ID of the Movie.
	 */
	public void setID(int iD) {
		ID = iD;
	}

	/**
	 * Sets the Imdb ID of the Movie.
	 * 
	 * @param imdbID
	 *            The Imdb ID of the Movie.
	 */
	public void setImdbID(String imdbID) {
		this.imdbID = imdbID;
	}

	/**
	 * Sets the Url of the Movie.
	 * 
	 * @param url
	 *            The Url of the Movie.
	 */
	public void setUrl(URL url) {
		this.url = url;
	}

	/**
	 * Sets the overview of the Movie.
	 * 
	 * @param overview
	 *            The overview of the Movie.
	 */
	public void setOverview(String overview) {
		this.overview = overview;
	}

	/**
	 * Sets the rating of the Movie.
	 * 
	 * @param rating
	 *            The rating of the Movie.
	 */
	public void setRating(double rating) {
		this.rating = rating;
	}

	/**
	 * Sets the release date of the Movie.
	 * 
	 * @param releasedDate
	 *            The release date of the Movie.
	 */
	public void setReleasedDate(Date releasedDate) {
		this.releasedDate = releasedDate;
	}

	/**
	 * Sets the runtime of the Movie.
	 * 
	 * @param runtime
	 *            The runtime of the Movie.
	 */
	public void setRuntime(int runtime) {
		this.runtime = runtime;
	}

	/**
	 * Sets the budget of the Movie.
	 * 
	 * @param budget
	 *            The budget of the Movie.
	 */
	public void setBudget(int budget) {
		this.budget = budget;
	}

	/**
	 * Sets the revenue of the Movie.
	 * 
	 * @param revenue
	 *            The revenue of the Movie.
	 */
	public void setRevenue(int revenue) {
		this.revenue = revenue;
	}

	/**
	 * Sets the home page of the Movie.
	 * 
	 * @param homepage
	 *            The home page of the Movie.
	 */
	public void setHomepage(URL homepage) {
		this.homepage = homepage;
	}

	/**
	 * Sets the trailer of the Movie.
	 * 
	 * @param trailer
	 *            The trailer of the Movie.
	 */
	public void setTrailer(URL trailer) {
		this.trailer = trailer;
	}

	/**
	 * Sets the cast of the Movie.
	 * 
	 * @param cast
	 *            The cast of the Movie.
	 */
	public void setCast(Set<CastInfo> cast) {
		this.cast.clear();
		this.cast.addAll(cast);
	}

	/**
	 * Adds the cast to the Movie.
	 * 
	 * @param cast
	 *            The cast of the Movie to add.
	 */
	public void addCast(Set<CastInfo> cast) {
		this.cast.addAll(cast);
	}

	/**
	 * Set the genres of the Movie.
	 * 
	 * @param genres
	 *            The genres of the Movie.
	 */
	public void setGenres(Set<Genre> genres) {
		this.genres.clear();
		this.genres.addAll(genres);
	}

	/**
	 * Adds the genres to the Movie.
	 * 
	 * @param genres
	 *            The genres of the Movie to add.
	 */
	public void addGenres(Set<Genre> genres) {
		this.genres.addAll(genres);
	}

	/**
	 * Sets the images of the Movie.
	 * 
	 * @param images
	 *            The images of the Movie.
	 */
	public void setImages(MovieImages images) {
		this.images = images;
	}

	/**
	 * Returns a list of movies in the box office (full flavors). Returns a list
	 * of Movie objects with the full form (see class description {@link Movie}
	 * and method {@link #isReduced()}). Will return null if a valid API key was
	 * not supplied to the {@link GeneralSettings}<br/>
	 * <br/>
	 * <strong>This method relies on parsing the home page HTML of
	 * themoviedb.org. So it is not 100% stable as the syntax of the web page
	 * may change.</strong>
	 * 
	 * @return A list of Movie objects in the box office with the full form (see
	 *         class description {@link Movie} and method {@link #isReduced()}
	 *         ).Will return null if a valid API key was not supplied to the
	 *         {@link GeneralSettings}
	 * @throws JSONException
	 * @throws IOException
	 */
	public static List<Movie> boxOffice() throws IOException, JSONException {
		Set<Integer> ids = parseHTML(0);

		List<Movie> movies = null;
		if (!ids.isEmpty()) {
			movies = new LinkedList<Movie>();
			for (int id : ids.toArray(new Integer[0])) {
				movies.add(getInfo(id));
			}
		}
		return movies;
	}

	/**
	 * Returns a set of the IDs of the movies in the box office. Will return
	 * null if a valid API key was not supplied to the {@link GeneralSettings}<br/>
	 * <br/>
	 * <strong>This method relies on parsing the home page HTML of
	 * themoviedb.org. So it is not 100% stable as the syntax of the web page
	 * may change.</strong>
	 * 
	 * @return A set of the IDs of the movies in the box office. Will return
	 *         null if a valid API key was not supplied to the
	 *         {@link GeneralSettings}
	 * @throws IOException
	 * @throws JSONException
	 */
	public static Set<Integer> boxOfficeIDs() throws IOException, JSONException {
		return parseHTML(0);
	}

	/**
	 * Returns a list of the most popular movies (full flavors). Returns a list
	 * of Movie objects with the full form (see class description {@link Movie}
	 * and method {@link #isReduced()}). Will return null if a valid API key was
	 * not supplied to the {@link GeneralSettings}<br/>
	 * <br/>
	 * <strong>This method relies on parsing the home page HTML of
	 * themoviedb.org. So it is not 100% stable as the syntax of the web page
	 * may change.</strong>
	 * 
	 * @return A list of the most popular Movie objects with the full form (see
	 *         class description {@link Movie} and method {@link #isReduced()}
	 *         ).Will return null if a valid API key was not supplied to the
	 *         {@link GeneralSettings}
	 * @throws JSONException
	 * @throws IOException
	 */
	public static List<Movie> mostPopular() throws IOException, JSONException {
		Set<Integer> ids = parseHTML(1);

		List<Movie> movies = null;
		if (!ids.isEmpty()) {
			movies = new LinkedList<Movie>();
			for (int id : ids.toArray(new Integer[0])) {
				movies.add(getInfo(id));
			}
		}
		return movies;
	}

	/**
	 * Returns a set of the IDs of the most popular movies. Will return null if
	 * a valid API key was not supplied to the {@link GeneralSettings}<br/>
	 * <br/>
	 * <strong>This method relies on parsing the home page HTML of
	 * themoviedb.org. So it is not 100% stable as the syntax of the web page
	 * may change.</strong>
	 * 
	 * @return A set of the IDs of the most popular movies. Will return null if
	 *         a valid API key was not supplied to the {@link GeneralSettings}
	 * @throws IOException
	 * @throws JSONException
	 */
	public static Set<Integer> mostPopularIDs() throws IOException,
			JSONException {
		return parseHTML(1);
	}

	/**
	 * This method gets the HTML of the home page of themoviedatabase.org and
	 * parses it for a list of movies in the box office or in the most popular
	 * list.
	 * 
	 * @param part
	 *            The part of the HTML to parse. 0 is for the box office and 1
	 *            is for the most popular.
	 * @return A list of movies.
	 * @throws IOException
	 * @throws JSONException
	 */
	private static Set<Integer> parseHTML(int part) throws IOException,
			JSONException {
		URL call = new URL("http://www.themoviedb.org/");
		URLConnection yc = call.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(yc
				.getInputStream()));
		String inputLine;
		StringBuffer xmlString = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			xmlString.append(inputLine);
		}
		in.close();

		String[] parts = xmlString.toString().split("first most-popular");

		Pattern p = Pattern.compile("/movie/(\\d+)");
		Matcher match = p.matcher(parts[part]);
		Set<Integer> ids = new LinkedHashSet<Integer>();
		while (match.find()) {
			try {
				int id = Integer.parseInt(match.group(1));
				ids.add(id);
			} catch (NumberFormatException e) {

			}
		}
		return ids;
	}

	/**
	 * Searches for movies and returns full flavors. The string supplied can
	 * contain spaces. Returns a list of Movie objects with the full form (see
	 * class description {@link Movie} and method {@link #isReduced()}). Will
	 * return null if a valid API key was not supplied to the
	 * {@link GeneralSettings}
	 * 
	 * @param name
	 *            The name of the movie to search for.
	 * @return A list of Movie objects with the full form (see class description
	 *         {@link Movie} and method {@link #isReduced()}).Will return null
	 *         if a valid API key was not supplied to the
	 *         {@link GeneralSettings}
	 * @throws JSONException
	 * @throws IOException
	 */
	public static List<Movie> deepSearch(String name) throws JSONException,
			IOException {
		name = name.replaceAll(" ", "%20");
		if (GeneralSettings.getApiKey() != null
				&& !GeneralSettings.getApiKey().equals("") && name != null
				&& !name.equals("")) {
			URL call = new URL(
					"http://api.themoviedb.org/2.1/Movie.search/en/json/"
							+ GeneralSettings.getApiKey() + "/" + name);
			URLConnection yc = call.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(yc
					.getInputStream()));
			String inputLine;
			StringBuffer jsonString = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				jsonString.append(inputLine);
			}
			in.close();
			List<Movie> results = new LinkedList<Movie>();
			if (!jsonString.toString().equals("[\"Nothing found.\"]")) {
				JSONArray jsonArray = new JSONArray(jsonString.toString());
				for (int i = 0; i < jsonArray.length(); i++) {
					results
							.add(getInfo(jsonArray.getJSONObject(i)
									.getInt("id")));
				}
			}
			return results;
		}
		return null;
	}

	/**
	 * Searches for movies. The string supplied can contain spaces. Returns a
	 * list of Movie objects with the reduced form (see class description
	 * {@link Movie} and method {@link #isReduced()}). Will return null if a
	 * valid API key was not supplied to the {@link GeneralSettings}
	 * 
	 * @param name
	 *            The name of the movie to search for.
	 * @return A list of Movie objects with the reduced form (see class
	 *         description {@link Movie} and method {@link #isReduced()}).Will
	 *         return null if a valid API key was not supplied to the
	 *         {@link GeneralSettings}
	 * @throws JSONException
	 * @throws IOException
	 */
	public static List<Movie> search(String name) throws JSONException,
			IOException {
		name = name.replaceAll(" ", "%20");
		if (GeneralSettings.getApiKey() != null
				&& !GeneralSettings.getApiKey().equals("") && name != null
				&& !name.equals("")) {
			URL call = new URL(
					"http://api.themoviedb.org/2.1/Movie.search/en/json/"
							+ GeneralSettings.getApiKey() + "/" + name);
			URLConnection yc = call.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(yc
					.getInputStream()));
			String inputLine;
			StringBuffer jsonString = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				jsonString.append(inputLine);
			}
			in.close();
			List<Movie> results = new LinkedList<Movie>();
			if (!jsonString.toString().equals("[\"Nothing found.\"]")) {
				JSONArray jsonArray = new JSONArray(jsonString.toString());
				for (int i = 0; i < jsonArray.length(); i++) {
					results.add(new Movie(jsonArray.getJSONObject(i), true));
				}
			}
			return results;
		}
		return null;
	}

	/**
	 * Gets the info for a specific Movie (by ID). Returns a Movie object with
	 * the normal form (see class description {@link Movie} and method
	 * {@link #isReduced()}). Will return null if a valid API key was not
	 * supplied to the {@link GeneralSettings} or if the supplied ID did not
	 * correspond to a Movie.
	 * 
	 * @param ID
	 *            The ID of the Movie.
	 * @return A Movie object with the normal form (see class description
	 *         {@link Movie} and method {@link #isReduced()}). Will return null
	 *         if a valid API key was not supplied to the
	 *         {@link GeneralSettings} or if the supplied ID did not correspond
	 *         to a Movie.
	 * @throws JSONException
	 * @throws Exception
	 */
	public static Movie getInfo(int ID) throws IOException, JSONException {
		if (GeneralSettings.getApiKey() != null
				&& !GeneralSettings.getApiKey().equals("")) {
			URL call = new URL(
					"http://api.themoviedb.org/2.1/Movie.getInfo/en/json/"
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
				return new Movie(jsonArray, false);
			}
		}
		return null;
	}

	/**
	 * Gets the images for a specific Movie (by ID). Will return null if a valid
	 * API key was not supplied to the {@link GeneralSettings} or if the
	 * supplied ID did not correspond to a Movie.
	 * 
	 * @param ID
	 *            The ID of the Movie.
	 * @return The images for a specific Movie (by ID). Will return null if a
	 *         valid API key was not supplied to the {@link GeneralSettings} or
	 *         if the supplied ID did not correspond to a Movie.
	 * @throws IOException
	 * @throws JSONException
	 */
	public static MovieImages getImages(int ID) throws IOException,
			JSONException {
		if (GeneralSettings.getApiKey() != null
				&& !GeneralSettings.getApiKey().equals("")) {
			URL call = new URL(
					"http://api.themoviedb.org/2.1/Movie.getImages/en/json/"
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
				JSONObject json = new JSONArray(jsonString.toString())
						.getJSONObject(0);
				JSONArray postersArray = json.getJSONArray("posters");
				MovieImages images = new MovieImages();
				for (int i = 0; i < postersArray.length(); i++) {
					JSONObject image = postersArray.getJSONObject(i)
							.getJSONObject("image");
					URL posterURL = null;
					try {
						posterURL = new URL(image.getString("url"));
					} catch (MalformedURLException e) {
					}
					String posterID = image.getString("id");
					String posterSize = image.getString("size");
					MoviePoster.Size posterSizeEnum = MoviePoster.Size.ORIGINAL;
					if (posterSize.equalsIgnoreCase("thumb")) {
						posterSizeEnum = MoviePoster.Size.THUMB;
					} else if (posterSize.equalsIgnoreCase("mid")) {
						posterSizeEnum = MoviePoster.Size.MID;
					} else if (posterSize.equalsIgnoreCase("cover")) {
						posterSizeEnum = MoviePoster.Size.COVER;
					}
					MoviePoster poster = null;
					for (MoviePoster p : images.posters) {
						if (p.getID().equals(posterID)) {
							poster = p;
						}
					}
					if (poster == null) {
						poster = new MoviePoster(posterID);
						images.posters.add(poster);
					}
					poster.setImage(posterSizeEnum, posterURL);
				}
				postersArray = json.getJSONArray("backdrops");
				for (int i = 0; i < postersArray.length(); i++) {
					JSONObject image = postersArray.getJSONObject(i)
							.getJSONObject("image");
					URL posterURL = null;
					try {
						posterURL = new URL(image.getString("url"));
					} catch (MalformedURLException e) {
					}
					String posterID = image.getString("id");
					String posterSize = image.getString("size");
					MovieBackdrop.Size posterSizeEnum = MovieBackdrop.Size.ORIGINAL;
					if (posterSize.equalsIgnoreCase("thumb")) {
						posterSizeEnum = MovieBackdrop.Size.THUMB;
					} else if (posterSize.equalsIgnoreCase("poster")) {
						posterSizeEnum = MovieBackdrop.Size.POSTER;
					}
					MovieBackdrop backdrop = null;
					for (MovieBackdrop p : images.backdrops) {
						if (p.getID().equals(posterID)) {
							backdrop = p;
						}
					}
					if (backdrop == null) {
						backdrop = new MovieBackdrop(posterID);
						images.backdrops.add(backdrop);
					}
					backdrop.setImage(posterSizeEnum, posterURL);
				}
				return images;
			}
		}
		return null;
	}

	/**
	 * Parses a JSON object wrapped in a JSON array and sets the Movie fields.
	 * 
	 * @param jsonArray
	 *            The JSON array containing the JSON object that describes the
	 *            Movie.
	 */
	public void parseJSON(JSONArray jsonArray) {
		try {
			parseJSON(jsonArray.getJSONObject(0));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Parses a JSON object and sets the Movie fields.
	 * 
	 * @param jsonObject
	 *            The JSON object that describes the Movie.
	 */
	public boolean parseJSON(JSONObject jsonObject) {
		try {
			setRating(jsonObject.getDouble("rating"));
			setAlternativeName(jsonObject.getString("alternative_name"));
			setName(jsonObject.getString("name"));
			setOverview(jsonObject.getString("overview"));
			setID(jsonObject.getInt("id"));
			try {
				setUrl(new URL(jsonObject.getString("url")));
			} catch (MalformedURLException e) {
				setUrl(null);
			}
			JSONArray postersArray = jsonObject.getJSONArray("posters");
			for (int i = 0; i < postersArray.length(); i++) {
				JSONObject image = postersArray.getJSONObject(i).getJSONObject(
						"image");
				URL posterURL = null;
				try {
					posterURL = new URL(image.getString("url"));
				} catch (MalformedURLException e) {
				}
				String posterID = image.getString("id");
				String posterSize = image.getString("size");
				MoviePoster.Size posterSizeEnum = MoviePoster.Size.ORIGINAL;
				if (posterSize.equalsIgnoreCase("thumb")) {
					posterSizeEnum = MoviePoster.Size.THUMB;
				} else if (posterSize.equalsIgnoreCase("mid")) {
					posterSizeEnum = MoviePoster.Size.MID;
				} else if (posterSize.equalsIgnoreCase("cover")) {
					posterSizeEnum = MoviePoster.Size.COVER;
				}
				MoviePoster poster = null;
				for (MoviePoster p : getImages().posters) {
					if (p.getID().equals(posterID)) {
						poster = p;
					}
				}
				if (poster == null) {
					poster = new MoviePoster(posterID);
					getImages().posters.add(poster);
				}
				poster.setImage(posterSizeEnum, posterURL);
			}
			postersArray = jsonObject.getJSONArray("backdrops");
			for (int i = 0; i < postersArray.length(); i++) {
				JSONObject image = postersArray.getJSONObject(i).getJSONObject(
						"image");
				URL posterURL = null;
				try {
					posterURL = new URL(image.getString("url"));
				} catch (MalformedURLException e) {
				}
				String posterID = image.getString("id");
				String posterSize = image.getString("size");
				MovieBackdrop.Size posterSizeEnum = MovieBackdrop.Size.ORIGINAL;
				if (posterSize.equalsIgnoreCase("thumb")) {
					posterSizeEnum = MovieBackdrop.Size.THUMB;
				} else if (posterSize.equalsIgnoreCase("poster")) {
					posterSizeEnum = MovieBackdrop.Size.POSTER;
				}
				MovieBackdrop backdrop = null;
				for (MovieBackdrop p : getImages().backdrops) {
					if (p.getID().equals(posterID)) {
						backdrop = p;
					}
				}
				if (backdrop == null) {
					backdrop = new MovieBackdrop(posterID);
					getImages().backdrops.add(backdrop);
				}
				backdrop.setImage(posterSizeEnum, posterURL);
			}
			setImdbID(jsonObject.getString("imdb_id"));
			String date = jsonObject.getString("released");
			if (!date.equals("")) {
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
				}
				setReleasedDate(c.getTime());
			}

			if (!isReduced()) {
				JSONArray genresArray = jsonObject.getJSONArray("genres");
				for (int i = 0; i < genresArray.length(); i++) {
					JSONObject genreObject = genresArray.getJSONObject(i);
					String genreName = genreObject.getString("name");
					URL genreUrl = null;
					try {
						genreUrl = new URL(genreObject.getString("url"));
					} catch (MalformedURLException e) {
					}
					getGenres().add(new Genre(genreUrl, genreName));
				}
				setTagline(jsonObject.getString("tagline"));
				setCertification(jsonObject.getString("certification"));
				try {
					setTrailer(new URL(jsonObject.getString("trailer")));
				} catch (MalformedURLException e) {
					setTrailer(null);
				}
				try {
					setRuntime(jsonObject.getInt("runtime"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					setHomepage(new URL(jsonObject.getString("homepage")));
				} catch (MalformedURLException e) {
					setHomepage(null);
				}
				JSONArray castArray = jsonObject.getJSONArray("cast");
				for (int i = 0; i < castArray.length(); i++) {
					JSONObject castObject = castArray.getJSONObject(i);
					String castName = castObject.getString("name");
					URL castThumb = null;
					try {
						castThumb = new URL(castObject.getString("profile"));
					} catch (MalformedURLException e) {
					}
					String castCharacter = castObject.getString("character");
					URL castUrl = null;
					try {
						castUrl = new URL(castObject.getString("url"));
					} catch (MalformedURLException e) {
					}
					String castJob = castObject.getString("job");
					int castID = castObject.getInt("id");
					String castDept = castObject.getString("department");
					CastInfo castInfo = new CastInfo(castUrl, castName,
							castCharacter, castJob, castID, castThumb, castDept);
					getCast().add(castInfo);
				}
				try {
					setBudget(jsonObject.getInt("budget"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					setRevenue(jsonObject.getInt("revenue"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return true;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

}
