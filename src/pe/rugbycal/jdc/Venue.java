package pe.rugbycal.jdc;

/**
 * Created by juliofdiaz on 6/18/16.
 */
class Venue{
    private String venueName;
    private String venueCity;
    private String venueCountry;

    public Venue(){
        this.setVenueName(null);
        this.setVenueCity(null);
        this.setVenueCountry(null);
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getVenueCity() {
        return venueCity;
    }

    public void setVenueCity(String venueCity) {
        this.venueCity = venueCity;
    }

    public String getVenueCountry() {
        return venueCountry;
    }

    public void setVenueCountry(String venueCountry) {
        this.venueCountry = venueCountry;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if(obj instanceof Venue){
            Venue newVenue = (Venue) obj;
            if(newVenue.getVenueName().equals(this.getVenueName())){
                if(newVenue.getVenueCity().equals(this.getVenueCity())){
                    if(newVenue.getVenueCountry().equals(this.getVenueCountry())){
                        result = true;
                    }
                }
            }
        }
        return result;
    }
}
