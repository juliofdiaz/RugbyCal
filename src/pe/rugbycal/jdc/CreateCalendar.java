package pe.rugbycal.jdc;

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.*;
import java.util.Date;

/**
 *
 * Created by juliofdiaz on 6/16/16.
 */
public class CreateCalendar {
    private static final String ICAL_PRODUCT_ID = "-//Julio Diaz Caballero//RugbyCal 0.1//EN";
    private static final String ICAL_DESCRIPTION = "Calendar with every Rugby event.";
    private static final String ICAL_NAME = "Rugby Calendar";
    private static final String ICAL_CALNAME_PROPERTY_LABEL = "X-WR-CALNAME";

    private static final String OUTFILE_NAME = "/Users/juliofdiaz/Downloads/basic3.ics";

    public static void main( String[] args ) throws IOException, ParseException {
        Calendar calendar = new Calendar();

        /* Sets the ical4j calendar properties*/
        setAllCalendarProperties(calendar);

        /* Retrieves Future events and adds them to the calendar */
        setAllCalendarEvents(calendar);

        /* Print calendar to file */
        printCalendar(calendar, OUTFILE_NAME);
    }

    /**
     * This method adds all the events to an ical4j calendar
     *
     * @param calendar An ical4j calendar that will get new events.
     * @throws IOException
     */
    private static void setAllCalendarEvents(Calendar calendar) throws IOException {
        ArrayList<Match> events = new FutureEvents().getMatches();
        for(Match match:events){
            VEvent event = createEventFromMatch(match);
            calendar.getComponents().add(event);
        }
    }

    /**
     * This method prints an ical4j calendar in a file.
     *
     * @param calendar An ical4j calendar that will be printed to a file.
     * @param outFile  The name of the new file.
     * @throws FileNotFoundException
     */
    private static void printCalendar(Calendar calendar, String outFile)
            throws FileNotFoundException {
        PrintWriter out = new PrintWriter(outFile);
        out.print(calendar);
        out.close();
    }

    /**
     * This method sets all the properties pertaining a ical4j calendar.
     *
     * @param calendar The calendar that will receive the properties.
     */
    private static void setAllCalendarProperties(Calendar calendar){
        setCalendarConstantProperties(calendar);
        setCalendarProdId(calendar, ICAL_PRODUCT_ID);
        setCalendarDescription(calendar, ICAL_DESCRIPTION);
    }

    /**
     * This method sets the ProdId property in a ical4j calendar.
     *
     * @param calendar       An ical4j calendar whose ProdId property will be set.
     * @param propertyString A String with the value of the ProdId property.
     */
    private static void setCalendarProdId(Calendar calendar, String propertyString){
        setCalendarProperty(calendar, new ProdId(propertyString));
    }

    /**
     * This method sets the Description property in a ical4j calendar
     *
     * @param calendar       An ical4j calendar whose Description property will be set.
     * @param propertyString A String with the value of the Description property.
     */
    private static void setCalendarDescription(Calendar calendar, String propertyString){
        setCalendarProperty(calendar, new Description(propertyString));
    }

    /**
     * This method sets a property in an ical4j Calendar.
     *
     * @param calendar An ical4j calendar whose property will be set.
     * @param property A property that will be set in the ical4j calendar.
     */
    private static void setCalendarProperty(Calendar calendar, Property property){
        calendar.getProperties().add(property);
    }

    /**
     *
     *
     * @param calendar ical4j calendar to get properties added to.
     */
    private static void setCalendarConstantProperties(Calendar calendar){
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);
        calendar.getProperties().add(Method.PUBLISH);
        calendar.getProperties().add(new XProperty(ICAL_CALNAME_PROPERTY_LABEL, ICAL_NAME));
    }

    /**
     * This method takes the information in a pe.rugbycal.jdc.Match and populates a VEvent
     * with that. This method also populates the VEvent with appropriate additional
     * information.
     *
     * @param match The match that will provide the information to create a new VEvent.
     * @return      The information of match in the VEvent.
     */
    private static VEvent createEventFromMatch(Match match){
        VEvent event = new VEvent();

        Venue venue = match.getMatchVenue();
        Participant teamOne = match.getTeamOne();
        Participant teamTwo = match.getTeamTwo();

        setEventUid(event, match.getMatchId());
        setEventLocation(event, Application.LocationStringCreator(venue));
        setEventSummary(event, Application.SummaryStringCreator(teamOne, teamTwo));
        setEventDescription(event, match.getMatchTournament());
        setEventDtStart(event, match.getMatchStart());
        setEventDtEnd(event, match.getMatchStart());


        setEventProperty(event, Status.VEVENT_CONFIRMED);
        setEventProperty(event, Transp.OPAQUE);
        setEventLastModified(event);
        setEventCreated(event);
        setEventProperty(event, new Sequence(0));

        return event;
    }

    /**
     * This method adds the property 'UID' to an event and sets its value
     * to a given Double.
     *
     * @param event The event that will get the 'UID' property.
     * @param id    The new 'UID' value.
     */
    private static void setEventUid(VEvent event, Double id){
        setEventProperty(event, new Uid(Double.toString(id)));
    }

    /**
     * This method adds the property 'LOCATION' to an event and sets its value
     * to a given String.
     *
     * @param event          The event that will get the 'LOCATION' property.
     * @param propertyString The new 'LOCATION' value.
     */
    private static void setEventLocation(VEvent event, String propertyString){
        setEventProperty(event, new Location(propertyString));
    }

    /**
     * This method adds the property 'SUMMARY' to an event and sets its value
     * to a given String.
     *
     * @param event          The event that will get the 'SUMMARY' property.
     * @param propertyString The new 'SUMMARY' value.
     */
    private static void setEventSummary(VEvent event, String propertyString){
        setEventProperty(event, new Summary(propertyString));
    }

    /**
     * This method adds the property 'DESCRIPTION' to an event and sets its value
     * to a given String.
     *
     * @param event          The event that will get the 'DESCRIPTION' property.
     * @param propertyString The new 'DESCRIPTION' value.
     */
    private static void setEventDescription(VEvent event, String propertyString){
        setEventProperty(event, new Description(propertyString));
    }

    /**
     * This method adds the property 'DTSTART' to an event and sets its value
     * to the time and date of te beginning of the match.
     *
     * @param event    The event that will get the 'DTSTART' property
     * @param calendar The date and time of the beginning of the event.
     */
    private static void setEventDtStart(VEvent event, java.util.Calendar calendar){
        DateTime dateTimeStart = getDateTimeFromCalendar(calendar);
        dateTimeStart.setUtc(true);
        setEventProperty(event, new DtStart(dateTimeStart));
    }

    /**
     * This method adds the property 'DTEND' to an event and sets its value
     * to a time '150' minutes after the beginning of the match
     *
     * @param event    The event that will get the 'DTEND' property.
     * @param calendar The date and time of the beginning of the event.
     */
    private static void setEventDtEnd(VEvent event, java.util.Calendar calendar){
        calendar.add(java.util.Calendar.MINUTE,Application.MATCH_LENGTH);
        DateTime dateTimeEmd = getDateTimeFromCalendar(calendar);
        dateTimeEmd.setUtc(true);
        setEventProperty(event, new DtEnd(dateTimeEmd));
    }

    /**
     * This method adds the property 'LAST-MODIFIED'to an event and sets its
     * value to the current time and date.
     *
     * @param event The event that will get a 'LAST-MODIFIED' property.
     */
    private static void setEventLastModified(VEvent event){
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        DateTime dateTime = getDateTimeFromCalendar(calendar);
        setEventProperty(event, new LastModified(dateTime));
    }

    /**
     * This method adds the property 'CREATED' to an event and sets its value
     * to the current time and date.
     *
     * @param event The event that will get a 'CREATED' property.
     */
    private static void setEventCreated(VEvent event){
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        DateTime dateTime = getDateTimeFromCalendar(calendar);
        setEventProperty(event, new Created(dateTime));
    }

    /**
     * This method adds a property and sets its value in a VEvent.
     *
     * @param event    The event that will get a property added.
     * @param property The new property to add to the event.
     */
    private static void setEventProperty(VEvent event, Property property){
        event.getProperties().add(property);
    }

    /**
     * This method takes a java Calendar object and it returns a ical4j DateTime.
     *
     * @param calendar The input java Calendar object.
     * @return         The time and date from the calendar in the form of the
     *                 ical4j DataTime object.
     */
    private static DateTime getDateTimeFromCalendar(java.util.Calendar calendar){
        Date date = calendar.getTime();
        return new DateTime(date);
    }
}
