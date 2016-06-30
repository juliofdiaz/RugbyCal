package pe.rugbycal.jdc;

/**
 *
 * Created by juliofdiaz on 6/18/16.
 */
class Application {
    public static final String DEVELOPER_NAME = "Julio Diaz Caballero";
    public static final String APPLICATION_VERSION = "2.0";
    public static final String APPLICATION_NAME = "RugbyCal";
    public static final Integer MATCH_LENGTH = 115;

    static String SummaryStringCreator(Participant teamOne,
                                       Participant teamTwo){
        return teamOne.getName() + " (" + teamOne.getAbbreviation() + ") v "
                + teamTwo.getName()+ " (" + teamTwo.getAbbreviation() + ")";
    }

    static String LocationStringCreator(Venue venue){
        return venue.getVenueName() + " - " + venue.getVenueCity() + " - "
                + venue.getVenueCountry();
    }
}
