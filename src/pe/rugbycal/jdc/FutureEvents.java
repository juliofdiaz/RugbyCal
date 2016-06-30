package pe.rugbycal.jdc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


/**
 *
 * Created by juliofdiaz on 6/12/16.
 */
class FutureEvents {
    private static final Integer FUTURE_SPAN = 21;
    private static final Integer JSON_SIZE = FUTURE_SPAN*(80/7);

    private static final String JSON_ROOT_LABEL = "content";
    private static final String JSON_MATCHID_LABEL = "matchId";
    private static final String JSON_TIME_ROOT_LABEL = "time";
    private static final String JSON_TEAMS_ROOT_LABEL = "teams";
    private static final String JSON_TOURNAMENT_LABEL = "events";
    private static final String JSON_TOURNAMENT_NAME_LABEL = "label";
    private static final String JSON_TEAM_NAME_LABEL = "name";
    private static final String JSON_TEAM_ABBREVIATION_LABEL = "abbreviation";
    private static final String JSON_TIME_LABEL = "millis";
    private static final String JSON_VENUE_ROOT_LABEL = "venue";
    private static final String JSON_VENUE_NAME_LABEL = "name";
    private static final String JSON_VENUE_CITY_LABEL = "city";
    private static final String JSON_VENUE_COUNTRY_LABEL = "country";

    private static final String JSON_URL_DATEFORMAT = "yyyy-MM-dd";

    private ArrayList<Match> matches;

    public FutureEvents() throws IOException {
        String dateToday = getTodayDate();
        String dateFuture = getFutureDate(FUTURE_SPAN);

        //String url = "http://cmsapi.pulselive.com/rugby/match?page=0&startDate="+
        //        dateToday+"&endDate="+dateFuture+"&states=U,L&pageSize=+" +
        //        JSON_SIZE+"&sort=asc&altId=hgv&client=pulse";
        String url = "http://142.150.214.90/test.json";

        /* Retrieve JSON String fom url */
        String jsonData =  getJson( url );
        /* Retrieves information about matches from a JSON String */
        ArrayList<Match> matches = getMatchesFromJson(jsonData);
        /* Set the information about matches to this Future events */
        this.setMatches(matches);
    }

    /**
     * This method takes a JSON String with information about the matches found in that
     * String.
     *
     * @param jsonString The information as a JSON String.
     * @return           The list of per.rugbycal.jdc.Match with information about the
     *                   matched.
     */
    private static ArrayList<Match> getMatchesFromJson(String jsonString){
        ArrayList<Match> matches = new ArrayList<>();
        JSONObject obj = new JSONObject(jsonString);
        JSONArray arr = obj.getJSONArray( JSON_ROOT_LABEL );
        for (int i = 0; i < arr.length(); i++)
        {
            Match tempMatch = createMatchFromJsonObject(arr.getJSONObject(i));
            matches.add( tempMatch );
        }
        return matches;
    }

    /**
     * This method takes a JSONObject from a JSONArray of matches then uses
     * their information to create a pe.rugbycal.jdc.Match object.
     *
     * @param item A JSONObject item from a JSONArray of matches
     * @return     The information in the JSONObject
     */
    private static Match createMatchFromJsonObject(JSONObject item){
        Match match = new Match();

        /* Pulse id of the event */
        match.setMatchId( item.getDouble(JSON_MATCHID_LABEL) );
        /* Information about the venue of the event */
        match.setMatchVenue( getMatchVenue(item) );
        /* Information about the time of the event */
        match.setMatchStart( getMatchStart( item.getJSONObject(JSON_TIME_ROOT_LABEL) ) );
        /* Information about competing teams */
        Participant[] teams = getParticipants( item.getJSONArray(JSON_TEAMS_ROOT_LABEL) );
        match.setTeamOne(teams[0]);
        match.setTeamTwo(teams[1]);
        /* Information about event */
        match.setMatchTournament( getTournamentName( item ) );

        return match;
    }

    /**
     * This method takes a JSONArray object with information about the teams participating
     * in a match and it returns an array of the two teams as pe.rugbycal.jdc.Participant objects.
     *
     * @param array A JSONArray object containing the information about the teams.
     * @return      The two participant teams involved in the match.
     */
    private static Participant[] getParticipants( JSONArray array ){
        Participant[] participants = new Participant[2];

        JSONObject teamOne = array.getJSONObject(0);
        Participant participantOne = new Participant();
        participantOne.setName( getTeamName( teamOne ) );
        participantOne.setAbbreviation( getTeamAbbreviation( teamOne));
        participants[0] = participantOne;

        JSONObject teamTwo = array.getJSONObject(1);
        Participant participantTwo = new Participant();
        participantTwo.setName( getTeamName( teamTwo ) );
        participantTwo.setAbbreviation( getTeamAbbreviation( teamTwo ) );
        participants[1] = participantTwo;

        return participants;
    }

    /**
     * This method retrieves the name of the event from a JSONObject
     * format.
     *
     * @param item The JSONObject containing the array of tournaments.
     * @return     The name of the event as a Java String.
     */
    private static String getTournamentName( JSONObject item ) {
        try {
            JSONArray tournament = item.getJSONArray(JSON_TOURNAMENT_LABEL);
            JSONObject obj = tournament.getJSONObject(0);
            return obj.getString(JSON_TOURNAMENT_NAME_LABEL);
        } catch( JSONException e ){
            return null;
        }
    }

    /**
     * This method retrieves the name of the team from a JSONObject
     * format.
     *
     * @param team The JSONObject containing information about a team.
     * @return     The name of the team as a Java String.
     */
    private static String getTeamName(JSONObject team){
        return getStringFromJSONObject(team, JSON_TEAM_NAME_LABEL);
    }

    /**
     * This method retrieves the abbreviation of team from a JSONObject
     * format.
     *
     * @param team The JSONObject containing information about a team.
     * @return     The abbreviation of the team as a Java String.
     */
    private static String getTeamAbbreviation(JSONObject team){
        return getStringFromJSONObject(team, JSON_TEAM_ABBREVIATION_LABEL);
    }

    /**
     * This method turns retrieves the time information of each match
     * and returns a Java Calendar.
     *
     * @param time The JSONObject that contains the information about the
     *             time of the match.
     * @return     The time information of the match in the Java Calendar
     *             format.
     */
    private static Calendar getMatchStart(JSONObject time){
        Calendar calendar = Calendar.getInstance();
        try{
            double millisTime = time.getDouble(JSON_TIME_LABEL);
            calendar.setTimeInMillis((long) millisTime);
            return calendar;
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @param item The match item containing the information about the venue.
     * @return     The information about the match venue in a pe.rugbycal.jdc.Venue object.
     */
    private static Venue getMatchVenue(JSONObject item){
        Venue result = new Venue();
        try {
            JSONObject venue = item.getJSONObject(JSON_VENUE_ROOT_LABEL);
            result.setVenueName( venue.getString(JSON_VENUE_NAME_LABEL) );
            result.setVenueCity( venue.getString(JSON_VENUE_CITY_LABEL) );
            result.setVenueCountry( venue.getString(JSON_VENUE_COUNTRY_LABEL) );
        }catch (JSONException e){
            //Send info about exception
        }
        return result;
    }

    /**
     * This method retrieves a String item from a JSONObject. If the item does not
     * exist, then the method handles the exception and returns a null.
     *
     * @param object The JSONObject containing the item we wat to retrieve.
     * @param item   The label of the item we want to retrieve.
     * @return       The item we want to retrieve from the JSONObject.
     */
    private static String getStringFromJSONObject( JSONObject object, String item ){
        try{
            return object.getString(item);
        }catch (JSONException e){
            return null;
        }
    }

    /**
     * This method returns the date a week from the current date in the yyy-MM-dd
     * format as a String.
     *
     * @return The date of a week from the current date as a String in the
     *         yyyy-MM-dd format.
     */
    private static String getFutureDate(Integer futureSpan) {
        DateFormat dateFormat = new SimpleDateFormat(JSON_URL_DATEFORMAT);
        Calendar cal = Calendar.getInstance();
        cal.add( Calendar.DATE, futureSpan );
        return dateFormat.format(cal.getTime());
    }

    /**
     * This method returns the current date as a String.
     *
     * @return The current date as a String in the yyyy-MM-dd format.
     */
    private static String getTodayDate() {
        DateFormat dateFormat = new SimpleDateFormat(JSON_URL_DATEFORMAT);
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    /**
     * This method retrieves the information requested from a url as a String. The
     * information is supposed to be in the json format.
     *
     * @param url The link to the location of the json information
     * @return    The json information in Java String.
     * @throws IOException
     */
    private static String getJson( String url ) throws IOException {
        URL link = new URL( url );
        InputStreamReader isr = new InputStreamReader(link.openStream());
        BufferedReader in = new BufferedReader( isr );

        String inputLine;
        StringBuilder temp = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            temp.append(inputLine);
        }
        in.close();

        return temp.toString();
    }

    /**
     * Getter method for the list of matches.
     *
     * @return The ArrayList of matches in the future.
     */
    public ArrayList<Match> getMatches() {
        return matches;
    }

    /**
     * Setter method for the list of matches.
     *
     * @param matches An ArrayList of matches in the future.
     */
    public void setMatches(ArrayList<Match> matches) {
        this.matches = matches;
    }

}
