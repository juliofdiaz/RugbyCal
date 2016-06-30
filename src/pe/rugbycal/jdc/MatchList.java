package pe.rugbycal.jdc;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.filter.Filter;
import net.fortuna.ical4j.filter.PeriodRule;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.property.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.*;
import java.util.Date;

/**
 *
 *
 * TEST CALENDAR FOR NEW DATA
 * IF NEW EVENTS, INCLUDE THEM
 * IF SAME EVENTS, CHECK IF SAME DATA
 * IF DIFFERENT DATA, UPDATE EVENTS
 *
 * Created by juliofdiaz on 6/13/16.
 */
class MatchList {
    private static final Integer EVENTS_SPAN = 21;

    public static void main(String [] args)
            throws IOException, ParserException, ParseException {
        /* Potential new events */
        FutureEvents futureEvents = new FutureEvents();

        /* Create calendar object from ical file */
        Calendar calendar = getIcal4jCalendarFromFile("/Users/juliofdiaz/Downloads/basic3.ics");
        Collection<Component> fullCalendar = calendar.getComponents();

        /* Create Collection from a period starting now with a duration of 21 days. */
        Collection<Component> subsetCalendar = getComponentCollectionFromIcal4jCalendar(calendar);


        /* Retrieve match components and create a ArrayList of them. */
        for(Component component : subsetCalendar) {
            PropertyList propertyList = component.getProperties();
            Match match = getMatchFromPropertyList(propertyList);

            for(Match futureMatch:futureEvents.getMatches()){
                if(match.getMatchId().equals(futureMatch.getMatchId())){
                    if(!match.getMatchVenue().equals(futureMatch.getMatchVenue())){
                        System.out.println("diff venue");
                    }
                    if(!match.getMatchStart().equals(futureMatch.getMatchStart())){
                        System.out.println("diff start");
                    }
                    if(!match.getTeamOne().equals(futureMatch.getTeamOne())){
                        System.out.println("diff team one");
                    }
                    if(!match.getTeamTwo().equals(futureMatch.getTeamTwo())){
                        System.out.println("diff team two");
                    }
                    System.out.println(match.getMatchId());
                }
            }
        }


    }

    /**
     * This method retrieves a Collection of Components in a time frame from an
     * ical4j Calendar.
     *
     * @param calendar An ical4j Calendar.
     * @return         A Collection of Components in a given time frame.
     */
    private static Collection<Component> getComponentCollectionFromIcal4jCalendar(
            Calendar calendar){
        java.util.Calendar today = java.util.Calendar.getInstance();
        Period period = new Period(new DateTime(today.getTime()),
                new Dur(EVENTS_SPAN, 0, 0, 0));
        PeriodRule[] periods = {new PeriodRule(period)};
        Filter filter = new Filter(periods,Filter.MATCH_ALL);
        return (Collection<Component>) filter.filter(calendar.getComponents(Component.VEVENT));
    }

    /**
     * This method retrieves the calendar information from a ics file and turn
     * it into a ical4j Calendar.
     *
     * @param fileName The name of the ics file and its location.
     * @return         An ical4j Calendar Object representing the data in the
     *                 ics file.
     * @throws IOException
     * @throws ParserException
     */
    private static Calendar getIcal4jCalendarFromFile( String fileName )
            throws IOException, ParserException {
        FileInputStream fin = new FileInputStream(fileName);
        CalendarBuilder builder = new CalendarBuilder();
        return builder.build(fin);
    }

    /**
     * This method takes a Collection of Components and turns it into a List
     * of pe.rugbycal.jdc.Match.
     *
     * @param collection The collection of Components where the components are
     *                   events.
     * @return           A list of pe.rugbycal.jdc.Match with the events in the
     *                   Collection of Components.
     * @throws ParseException
     */
    private static ArrayList<Match> getMatchListFromComponentCollection(
            Collection<Component> collection) throws ParseException {
        ArrayList<Match> matches = new ArrayList<>();
        for(Component component : collection) {
            PropertyList propertyList = component.getProperties();
            matches.add(getMatchFromPropertyList(propertyList));
        }
        return matches;
    }

    /**
     * This method takes a Calendar Event and turns it into a Match.
     *
     * @param propertyList A property list of a Calendar Event.
     * @return             A pe.rugbycal.pe.Match including the information
     *                     extracted from  the Calendar Event.
     * @throws ParseException
     */
    private static Match getMatchFromPropertyList(PropertyList propertyList)
            throws ParseException {
        Match match = new Match();

        /* Set pe.rugbycal.jdc.Match Id */
        Double uid = getDoubleValueOfUid(propertyList);
        match.setMatchId(uid);

        /* Set pe.rugbycal.jdc.Match pe.rugbycal.jdc.Venue */
        String location = getStringValueOfLocation(propertyList);
        match.setMatchVenue(getVenueFromLocationString(location));

        /*Set pe.rugbycal.jdc.Match Date*/
        java.util.Calendar dtStart = getCalendarValueOfDtStart(propertyList);
        match.setMatchStart(dtStart);

        java.util.Calendar dtEnd = getCalendarValueOfDtEnd(propertyList);

        /* Set pe.rugbycal.jdc.Match Teams */
        String summary = getStringValueOfSummary(propertyList);
        Participant participantOne = new Participant();
        participantOne.setName( getTeamOneFromDescriptionString(summary) );
        participantOne.setAbbreviation( getTeamOneAbbreviationFromDescriptionString(summary) );
        match.setTeamOne(participantOne);

        Participant participantTwo = new Participant();
        participantTwo.setName( getTeamTwoFromDescriptionString(summary) );
        participantTwo.setAbbreviation( getTeamTwoAbbreviationFromDescriptionString(summary) );
        match.setTeamTwo(participantTwo);

        /* Set pe.rugbycal.jdc.Match Tournament */
        String description = getStringValueOfDescription(propertyList);
        match.setMatchTournament(description);

        return match;
    }

    /**
     * This method takes a String with the value of the Summary Property
     * and it extracts the name of team one as a String.
     *
     * @param description The Summary Property as a String.
     * @return            The name of the team one as a String.
     */
    private static String getTeamOneFromDescriptionString(String description){
        String[] strings = description.split(" v ");
        String[] teamOneInfo = strings[0].split(" \\(");
        return teamOneInfo[0];
    }

    /**
     * This method takes a String with the value of the Summary Property
     * and it extracts the abbreviation of team one as a String.
     *
     * @param description The Summary Property as a String.
     * @return            The abbreviation of the team one as a String.
     */
    private static String getTeamOneAbbreviationFromDescriptionString(String description){
        String[] strings = description.split(" v ");
        String[] teamOneInfo = strings[0].split(" \\(");
        return teamOneInfo[1].substring(0,3);
    }

    /**
     * This method takes a String with the value of the Summary Property
     * and it extracts the name of team two as a String.
     *
     * @param description The Summary Property as a String.
     * @return            The name of the team two as a String.
     */
    private static String getTeamTwoFromDescriptionString(String description){
        String[] strings = description.split(" v ");
        String[] teamTwoInfo = strings[1].split(" \\(");
        return teamTwoInfo[0];
    }

    /**
     * This method takes a String with the value of the Summary Property
     * and it extracts the abbreviation of team two as a String.
     *
     * @param description The Summary Property as a String.
     * @return            The abbreviation of the team two as a String.
     */
    private static String getTeamTwoAbbreviationFromDescriptionString(String description){
        String[] strings = description.split(" v ");
        String[] teamTwoInfo = strings[1].split(" \\(");
        return teamTwoInfo[1].substring(0,3);
    }

    /**
     * This method takes a String with the value of the Location Property
     * and it converts the information into a Venue object.
     *
     * @param location The Location Property as a String.
     * @return         A pe.rugbycal.jdc.Venue containing information about the location.
     */
    private static Venue getVenueFromLocationString(String location){
        Venue venue = new Venue();
        String[] strings = location.split(" - ");
        venue.setVenueName(strings[0]);
        venue.setVenueCity(strings[1]);
        venue.setVenueCountry(strings[2]);
        return venue;
    }

    /**
     * This method returns the value of the a DtStart Property as a java Calendar
     * given a PropertyList.
     *
     * @param propertyList The PropertyList containing the DtStart Property.
     * @return             The value of the DtStart Property as a java Calendar.
     * @throws ParseException
     */
    private static java.util.Calendar getCalendarValueOfDtStart( PropertyList propertyList )
            throws ParseException {
        Property property = propertyList.getProperty(Property.DTSTART);
        DtStart dtStartValue = new DtStart(property.getValue());
        return getCalendarFromDateProperty(dtStartValue);
    }

    /**
     * This method returns the value of the a DtEnd Property as a java Calendar
     * given a PropertyList.
     *
     * @param propertyList The PropertyList containing the DtEnd Property.
     * @return             The value of the DtEnd Property as a java Calendar.
     * @throws ParseException
     */
    private static java.util.Calendar getCalendarValueOfDtEnd( PropertyList propertyList )
            throws ParseException {
        Property property = propertyList.getProperty(Property.DTEND);
        DtEnd dtEndValue = new DtEnd(property.getValue());
        return getCalendarFromDateProperty(dtEndValue);
    }

    /**
     * This method returns the value of the a Property of interest as a java Calendar
     * given a PropertyList.
     *
     * @param dateProperty The PropertyList containing the Property of interest.
     * @return             The value of the Property of interest as a java Calendar.
     */
    private static java.util.Calendar getCalendarFromDateProperty(DateProperty dateProperty){
        Date date = dateProperty.getDate();
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * This method returns the value of the UID Property as a Double given
     * a PropertyList.
     *
     * @param propertyList The PropertyList containing the UID Property.
     * @return             The value of the UID Property as a java Double.
     */
    private static Double getDoubleValueOfUid(PropertyList propertyList){
        return getDoubleValueOfProperty(propertyList, Property.UID);
    }

    /**
     * This method returns the value of a Property of interest as a Double given
     * a PropertyList.
     *
     * @param propertyList The PropertyList containing the Property of interest.
     * @param propertyName The name of the Property to ge recovered.
     * @return             The value of the Property of interest as a java Double.
     */
    private static Double getDoubleValueOfProperty(PropertyList propertyList, String propertyName){
        String value = getStringValueOfProperty(propertyList, propertyName);
        return Double.parseDouble(value);
    }

    /**
     * This method returns the value of the Location Property as a String given
     * a PropertyList.
     *
     * @param propertyList The PropertyList containing the Location Property.
     * @return             The value of the Location Property as a java String.
     */
    private static String getStringValueOfLocation(PropertyList propertyList){
        return getStringValueOfProperty(propertyList, Property.LOCATION);
    }

    /**
     * This method returns the value of the Summary Property as a String given
     * a PropertyList.
     *
     * @param propertyList The PropertyList containing the Summary Property.
     * @return             The value of the Summary Property as a java String.
     */
    private static String getStringValueOfSummary(PropertyList propertyList){
        return getStringValueOfProperty(propertyList, Property.SUMMARY);
    }

    /**
     * This method returns the value of the Description Property as a String given
     * a PropertyList.
     *
     * @param propertyList The PropertyList containing the Description Property.
     * @return             The value of the Description Property as a java String.
     */
    private static String getStringValueOfDescription(PropertyList propertyList){
        return getStringValueOfProperty(propertyList, Property.DESCRIPTION);
    }

    /**
     * This method returns the value of a Property of interest as a String given
     * a PropertyList.
     *
     * @param propertyList The PropertyList containing the Property of interest.
     * @param propertyName The name of the Property to ge recovered.
     * @return             The value of the Property of interest as a java String.
     */
    private static String getStringValueOfProperty(PropertyList propertyList, String propertyName){
        Property property = propertyList.getProperty(propertyName);
        return property.getValue();
    }












































    /**
     * This is the basic method to modify a property given a property list.
     *
     * @param propertyList A list of properties of a given component.
     * @param propertyName The name of the property to be modified.
     * @param newValue     The new value to be given to the property
     * @throws ParseException
     * @throws IOException
     * @throws URISyntaxException
     */
    private static void updateProperty(PropertyList propertyList, String propertyName,
                                       String newValue)
            throws ParseException, IOException, URISyntaxException {
        Property property = propertyList.getProperty(propertyName);
        property.setValue(newValue);
    }

    /**
     * This method updates the created property from a property list.
     *
     * @param propertyList The list of properties to be modified.
     * @param newCreated   The new created to be given to the created property.
     * @throws ParseException
     * @throws IOException
     * @throws URISyntaxException
     */
    private static void updateCreated(PropertyList propertyList, String newCreated)
            throws ParseException, IOException, URISyntaxException {
        updateProperty(propertyList, Property.CREATED, newCreated);
    }

    /**
     * This method updates the UID property from a property list.
     *
     * @param propertyList The list of properties to be modified.
     * @param newUID       The new UID to be given to the UID property.
     * @throws ParseException
     * @throws IOException
     * @throws URISyntaxException
     */
    private static void updateUID(PropertyList propertyList, String newUID)
            throws ParseException, IOException, URISyntaxException {
        updateProperty(propertyList, Property.UID, newUID);
    }

    /**
     * This method updates the last-modified property from a property list.
     *
     * @param propertyList    The list of properties to be modified.
     * @param newLastModified The new last-modified to be given to the last-modified
     *                        property.
     * @throws ParseException
     * @throws IOException
     * @throws URISyntaxException
     */
    private static void updateLastModified(PropertyList propertyList, String newLastModified)
            throws ParseException, IOException, URISyntaxException {
        updateProperty(propertyList, Property.LAST_MODIFIED, newLastModified);
    }

    /**
     * This method updates the DtStart property from a property list.
     *
     * @param propertyList The list of properties to be modified.
     * @param newDtStart   The new DtStart to be given to the DtStart property.
     * @throws ParseException
     * @throws IOException
     * @throws URISyntaxException
     */
    private static void updateDtStart(PropertyList propertyList, String newDtStart)
            throws ParseException, IOException, URISyntaxException {
        updateProperty(propertyList, Property.DTSTART, newDtStart);
    }

    /**
     * This method updates the DtEnd property from a property list.
     *
     * @param propertyList The list of properties to be modified.
     * @param newDtEnd     The new DtEnd to be given to the DtEnd property.
     * @throws ParseException
     * @throws IOException
     * @throws URISyntaxException
     */
    private static void updateDtEnd(PropertyList propertyList, String newDtEnd)
            throws ParseException, IOException, URISyntaxException {
        updateProperty(propertyList, Property.DTEND, newDtEnd);
    }

    /**
     * This method updates the summary property from a property list.
     *
     * @param propertyList The list of properties to be modified.
     * @param newSummary   The new summary to be given to the summary property.
     * @throws ParseException
     * @throws IOException
     * @throws URISyntaxException
     */
    private static void updateSummary(PropertyList propertyList, String newSummary)
            throws ParseException, IOException, URISyntaxException {
        updateProperty(propertyList, Property.SUMMARY, newSummary);
    }

    /**
     * This method updates the description property from a property list.
     *
     * @param propertyList   The list of properties to be modified.
     * @param newDescription The new description to be given to the description property.
     * @throws ParseException
     * @throws IOException
     * @throws URISyntaxException
     */
    private static void updateDescription(PropertyList propertyList, String newDescription)
            throws ParseException, IOException, URISyntaxException {
        updateProperty(propertyList, Property.DESCRIPTION, newDescription);
    }

    /**
     * This method updates the location property from a property list.
     *
     * @param propertyList The list of properties to be modified.
     * @param newLocation  The new location to be given to the location property.
     * @throws ParseException
     * @throws IOException
     * @throws URISyntaxException
     */
    private static void updateLocation(PropertyList propertyList, String newLocation)
            throws ParseException, IOException, URISyntaxException {
        updateProperty(propertyList, Property.LOCATION, newLocation);
    }
}
