import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import org.json.JSONObject;

public class MovieTracker 
{
    public static void main(String[] args) 
    {
        boolean loggedIn = false;    
        MovieTrackerCLI.welcomeMessage();
    
        while (true)
        {
            while (loggedIn)
            {
                MovieTrackerCLI.displayUserDashboard();
                String userInput= "";
                try {
                    userInput= MovieTrackerCLI.getUserDashboardChoice();
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }

                if (userInput.equals("1")) // Search
                {
                    String movieName;
                    try {
                        movieName = MovieTrackerCLI.promptMovieSearch();
                        ArrayList<String> userInput2 = MovieTrackerCLI.fetchMovieDetailsWithActions(movieName); 
                        if (userInput2.get(1).equals("1")) // Add to Favorites
                        {
                            Users.getCurrentUser().addToFavorites(userInput2.get(0));
                        }
                        else if (userInput2.get(1).equals("2")) // Add to Watched
                        {
                            Users.getCurrentUser().addToWatched(userInput2.get(0));
                        }
                        else if (userInput2.get(1).equals("3")) // Add to ToWatch
                        {
                            Users.getCurrentUser().addTowatch(userInput2.get(0));
                        }
                        else if (userInput2.get(1).equals("4")) // Add Review
                        {
                            MovieTrackerCLI.addMovieReview(userInput2.get(0));
                        }
                        else if (userInput2.get(1).equals("5")) // Rate
                        {
                            MovieTrackerCLI.rateMovie(userInput2.get(0));
                        }
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }
                else if (userInput.equals("2"))
                {
                    ArrayList<String> favorites = Users.getCurrentUser().getFavoriteMovies();
                    MovieTrackerCLI.displayUserList(favorites, "Favorite Movies");
                }
                else if (userInput.equals("3"))
                {
                    ArrayList<String> watched = Users.getCurrentUser().getWatchedMovies();;
                    MovieTrackerCLI.displayUserList(watched, "Watched Movies");
                }
                else if (userInput.equals("4"))
                {
                    ArrayList<String> toWatch = Users.getCurrentUser().getToWatchMovies();
                    MovieTrackerCLI.displayUserList(toWatch, "To Watch");
                }
                else if (userInput.equals("5"))
                {
                    JSONObject rates = Users.getCurrentUser().getRatings();
                    MovieTrackerCLI.displayUserRatings(rates);
                }
                else if (userInput.equals("6"))
                {
                    JSONObject reviews = Users.getCurrentUser().getReviews();
                    MovieTrackerCLI.displayUserReviews(reviews);
                }
                else if (userInput.equals("7"))
                {
                    loggedIn = false;
                    Users.logOut();
                }
                else if (userInput.equals("8"))
                {
                    return;
                }
            }
            //*********************************************NOT LOGGED IN**********************************************************/
            ArrayList<String> userInfo;
            MovieTrackerCLI.displayMainMenu();
            String userInput = "";
            try {
                userInput = MovieTrackerCLI.getMainMenuChoice();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

            if (userInput.equals("1")) // Search
            {
                String movieName;
                try {
                    movieName = MovieTrackerCLI.promptMovieSearch();
                    MovieTrackerCLI.fetchAndDisplayMovieDetails(movieName);
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
            else if (userInput.equals("2")) // Log In
            {
                try {
                    userInfo = MovieTrackerCLI.getLoginDetails();
                    Users.logIn(userInfo.get(0), userInfo.get(1));
                    loggedIn = true;

                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
            else if (userInput.equals("3")) // Register
            {
                try {
                    userInfo = MovieTrackerCLI.getRegistrationDetails();
                    Users.register(userInfo.get(0), userInfo.get(1), userInfo.get(2));
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
                
            }
            else if (userInput.equals("4")) // Exit
            {
                break;
            }
            //***********************************************NOT LOGGED IN********************************************************/
        }
    }
}

class MovieTrackerCLI 
{
    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";

    private static Scanner scanner = new Scanner(System.in);

    public static void welcomeMessage()
    {
        System.out.println(BOLD + RED + "=".repeat(40) + RESET);
        System.out.println(BOLD + RED + "*** WELCOME TO YOUR MOVIE TRACKER! ***" + RESET);
    }

    public static void displayMainMenu() 
    {
        System.out.println(BOLD + RED + "=".repeat(40) + RESET);
        System.out.println(BOLD + GREEN + "1. Search Movies" + RESET);
        System.out.println(BOLD + GREEN + "2. Log IN" + RESET);
        System.out.println(BOLD + GREEN + "3. Register" + RESET);
        System.out.println(BOLD + GREEN + "4. Exit Program" + RESET);
    }

    public static String getMainMenuChoice()
    {
        
        System.out.println(BOLD + "Your choice: ");
        String userInput = scanner.nextLine();

        if (!userInput.equals("1") && !userInput.equals("2") && 
                !userInput.equals("3") && !userInput.equals("4"))
        {
            throw new IllegalArgumentException("This Wasn't in the Menu. Try again please");
        } 

        return userInput;        
    }

    public static void displayUserDashboard() 
    {
        System.out.println(BOLD + RED + "=".repeat(40) + RESET);
        System.out.println(BOLD + GREEN + "1. Search Movies" + RESET);
        System.out.println(BOLD + GREEN + "2. Favorite Movies" + RESET);
        System.out.println(BOLD + GREEN + "3. Watched" + RESET);
        System.out.println(BOLD + GREEN + "4. To Watch" + RESET);
        // System.out.println(BOLD + GREEN + "5. Create New List" + RESET);
        System.out.println(BOLD + GREEN + "5. My Ratings" + RESET);
        System.out.println(BOLD + GREEN + "6. My Reviews" + RESET);
        System.out.println(BOLD + GREEN + "7. Log Out" + RESET);
        System.out.println(BOLD + GREEN + "8. Exit Program" + RESET);
    }

    public static String getUserDashboardChoice()
    {
        System.out.println(BOLD + "Your choice: ");
        String userInput = scanner.nextLine();

        if (!userInput.equals("1") && 
            !userInput.equals("2") && 
            !userInput.equals("3") && 
            !userInput.equals("4") && 
            !userInput.equals("5") && 
            !userInput.equals("6") &&
            !userInput.equals("7") && 
            !userInput.equals("8"))
        {
            throw new IllegalArgumentException("This wasn't in the menu. Try again please");
        } 

        return userInput;
        
    }

    public static String promptMovieSearch()
    {
        System.out.println(BOLD + RED +"=".repeat(40) + RESET);
        System.out.println(BOLD + GREEN + "Enter the Movie Name: " + RESET);

        String movieName = scanner.nextLine();

        System.out.println(BOLD + RED + "=".repeat(40) + RESET);

        if (movieName.isEmpty()) 
        {
            throw new IllegalArgumentException("You Want TO Search For NOTHING?");
        }

        return movieName;
    }

    public static String fetchAndDisplayMovieDetails(String movieName)
    {
        try
        {
            System.out.println(BOLD + RED + "=".repeat(40));
            
            final String APIKEY = "697d91ea";
            final String baseURL = "https://www.omdbapi.com/";

            // Make the url 
            String urlString = baseURL + "?t=" + movieName.replace(" ", "+") + "&apikey=" + APIKEY;
            URL url = new URL(urlString); 

            // Open a connection 
            HttpURLConnection httpconnection = (HttpURLConnection) url.openConnection();
            httpconnection.setRequestMethod("GET");

            // Store the response coming from the API
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpconnection.getInputStream()));

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) 
            {
                response.append(line);
            }

            reader.close();


            JSONObject movieInfoJson = new JSONObject(response.toString());


            System.out.println(GREEN + "Title: " + RESET + movieInfoJson.getString("Title"));
            System.out.println(GREEN + "Year: " + RESET + movieInfoJson.getString("Year"));
            System.out.println(GREEN + "Run Time: " + RESET + movieInfoJson.getString("Runtime"));
            System.out.println(GREEN + "IMDb rate: " + RESET + movieInfoJson.getString("imdbRating"));
            System.out.println(GREEN + "Votes: " + RESET + movieInfoJson.getString("imdbVotes"));
            System.out.println(GREEN + "Language: " + RESET + movieInfoJson.getString("Language"));
            System.out.println(GREEN + "Release Date: " + RESET + movieInfoJson.getString("Released"));
            System.out.println(GREEN + "Plot: " + RESET + movieInfoJson.getString("Plot"));
            System.out.println(GREEN + "Director: " + RESET + movieInfoJson.getString("Director"));
            System.out.println(GREEN + "Actors: " + RESET + movieInfoJson.getString("Actors"));
            System.out.println(GREEN + "Awards: " + RESET + movieInfoJson.getString("Awards"));
            System.out.println(GREEN + "Writer: " + RESET + movieInfoJson.getString("Writer"));
            System.out.println(GREEN + "Country: " + RESET + movieInfoJson.getString("Country"));
            System.out.println(GREEN + "Genre: " + RESET + movieInfoJson.getString("Genre"));

            
            System.out.println(BOLD + RED + "=".repeat(40));
            return movieInfoJson.getString("Title");
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException(BOLD + RED + "Sorry we didn't find this movie. Try again" + RESET);
            //return "Movie Not Found";
        }

    }

    public static ArrayList<String> fetchMovieDetailsWithActions(String movieName)
    {  
        ArrayList<String> movieChoice = new ArrayList<>(2);
        
        String correctMovieName = MovieTrackerCLI.fetchAndDisplayMovieDetails(movieName);

        System.out.println(BOLD + GREEN + "1. Add to Favorites" + RESET);
        System.out.println(BOLD + GREEN + "2. Add to Watched" + RESET);
        System.out.println(BOLD + GREEN + "3. Add to ToWatch" + RESET);
        System.out.println(BOLD + GREEN + "4. Add Review" + RESET);
        System.out.println(BOLD + GREEN + "5. Rate" + RESET);
        System.out.println(BOLD + GREEN + "6. Do Nothing" + RESET);

        System.out.println(BOLD + "Your Choice: " + RESET);
        String userInput = scanner.nextLine();

        System.out.println(BOLD + RED + "=".repeat(40) + RESET);

        String[] menu = {"1", "2", "3", "4", "5", "6"};
        for (String string : menu) {
            if (string.equals(userInput))
            {
                movieChoice.add(correctMovieName);
                movieChoice.add(userInput);
                return movieChoice;
            }
        }

        throw new IllegalArgumentException("tany m3lesh");
    }

    public static void displayUserList(ArrayList<String> list, String listName)
    {
        System.out.println(BOLD + RED + "=".repeat(40) + RESET);
        System.out.println(listName +": ");

        if (list.isEmpty())
        {
            System.out.println("Oops! Your list '" + listName + "' is empty. Hurry up and add some movies!");
        }
        else
        {
            int order = 1;
            for (String string : list) {
                System.out.println(order + ". " + string);
            }
        }
    }

    public static void addMovieReview(String movieName)
    {
        System.out.println("Add Review for " + movieName + ": ");
        String review  = scanner.nextLine();
        Users.getCurrentUser().addReview(movieName, review);
    }

    public static void rateMovie(String movieName)
    {
        System.out.println("Add Rate for " + movieName + " out of 10" + ": ");
        String rate  = scanner.nextLine();
        try {
            int intRate = Integer.parseInt(rate);
            if (intRate > 10)
            {
                System.out.println("Glad you enjoyed the movie! But the rating maxes out at 10.");
            }
            else if (intRate < 0)
            {
                System.out.println("Looks like you really didn’t like this one, but ratings can’t be negative! Try a score from 0 to 10");
            }

            Users.getCurrentUser().addRate(movieName, rate);
        } catch (Exception e) {
            throw new IllegalArgumentException("Try rating with numbers!");
        }

    }

    public static void displayUserReviews(JSONObject reviews)
    {
        int order = 1;

        Iterator<String> keys = reviews.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            System.out.println(order + ". " + key + ": " + reviews.get(key));
        }
    }

    public static void displayUserRatings(JSONObject rates)
    {
        int order = 1;

        Iterator<String> keys = rates.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            System.out.println(order + ". " + key + ": " + rates.get(key));
        }
    }

    public static ArrayList<String> getLoginDetails()
    {
        System.out.println(BOLD + RED + "=".repeat(16) + " LOG IN " + "=".repeat(16));
        System.out.println(BOLD + RED + "UserName: ");

        String userName = scanner.nextLine();

        System.out.println(BOLD + RED + "Password: ");
        String password = scanner.nextLine();


        System.out.println(BOLD + RED + "=".repeat(40));

        if (userName.isEmpty() || password.isEmpty())
        {
            throw new IllegalArgumentException("Something is missing");
        }

        ArrayList<String> userInfo = new ArrayList<String>(2);
        userInfo.add(userName);
        userInfo.add(password);

        return userInfo;
    }

    public static ArrayList<String> getRegistrationDetails()
    {
        System.out.println(BOLD + RED + "=".repeat(15) + " REGISTER " + "=".repeat(15));
        System.out.println(BOLD + RED + "UserName: ");

        String userName = scanner.nextLine();

        System.out.println(BOLD + RED + "Password: ");
        String password = scanner.nextLine();

        System.out.println(BOLD + RED + "Confirm Password: ");
        String confirmPassword = scanner.nextLine();


        System.out.println(BOLD + RED + "=".repeat(40));

        ArrayList<String> userInfo = new ArrayList<String>(3);
        userInfo.add(userName);
        userInfo.add(password);
        userInfo.add(confirmPassword);

        return userInfo;
    }
}

class User
{
    private String userName;
    private String password;
    private ArrayList<String> favoriteMovies;
    private ArrayList<String> watched;
    private ArrayList<String> toWatch;
    private JSONObject userRatings;
    private JSONObject userReviews;

    public User(String name, String pass)
    {
        this.userName = name;
        this.password = pass;
        this.favoriteMovies = new ArrayList<>();
        this.watched = new ArrayList<>();
        this.toWatch = new ArrayList<>();
        this.userRatings = new JSONObject();
        this.userReviews = new JSONObject();
    }

    public String getUserName()
    {
        return userName;
    }

    public String getPassword()
    {
        return password;
    }

    public void addToFavorites(String movieName)
    {
        favoriteMovies.add(movieName);
    }
    
    public void addToWatched(String movieName)
    {
        watched.add(movieName);
    }

    public void addTowatch(String movieName)
    {
        toWatch.add(movieName);
    }

    public void addRate(String movieName, String rate)
    {
        userRatings.put(movieName, rate);
    }

    public void addReview(String movieName, String review)
    {
        userReviews.put(movieName, review);
    }

    public ArrayList<String> getFavoriteMovies()
    {
        return favoriteMovies;
    }

    public ArrayList<String> getWatchedMovies()
    {
        return watched;
    }

    public ArrayList<String> getToWatchMovies()
    {
        return toWatch;
    }

    public JSONObject getReviews()
    {
        return userReviews;
    }

    public JSONObject getRatings()
    {
        return userRatings;
    }

}

class Users 
{
    private static ArrayList<User> users  = new ArrayList<>();;
    private static User logedInUser = null;
 
    public static void logIn(String username, String pass)
    {
        for (User user : users) 
        {
            if (user.getUserName().equals(username))
            {
                if (user.getPassword().equals(pass)) 
                {
                    logedInUser = user;
                    System.out.println("Login successful. Welcome, " + username + "!"); 
                    return;   
                }
            }    
        }

        throw new IllegalArgumentException("Incorrect Username or Password");
    }

    public static void register(String username, String pass, String confirmpass)
    {
        for (User user : users) {
            if (user.getUserName().equals(username))
            {
                throw new IllegalArgumentException("Username Already Exists!");
            }
        }

        if (username.isEmpty()) {
            throw new IllegalArgumentException("Username is Empty!");
        }

        if (!pass.equals(confirmpass))
        {
            throw new IllegalArgumentException("Passwords Don't Match!");
        }

        System.out.println("Register successful. Hurry up and log in!");
        users.add(new User(username, pass));
    }

    public static User getCurrentUser()
    {
        return logedInUser;
    }

    public static void logOut()
    {
        logedInUser = null;
    }

}