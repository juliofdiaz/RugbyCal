package pe.rugbycal.jdc;

import java.util.Calendar;

/**
 * Created by juliofdiaz on 6/18/16.
 */
class Match{
    private Double matchId;
    private Venue matchVenue;
    private Calendar matchStart;
    private Participant teamOne;
    private Participant teamTwo;
    private String matchTournament;

    public Match(){
        this.setMatchId(null);
        this.setMatchVenue(null);
        this.setMatchStart(null);
        this.setTeamOne(null);
        this.setTeamTwo(null);

    }

    public Double getMatchId() {
        return matchId;
    }

    public void setMatchId(Double matchId) {
        this.matchId = matchId;
    }

    public Venue getMatchVenue() {
        return matchVenue;
    }

    public void setMatchVenue(Venue matchVenue) {
        this.matchVenue = matchVenue;
    }

    public Calendar getMatchStart() {
        return matchStart;
    }

    public void setMatchStart(Calendar matchStart) {
        this.matchStart = matchStart;
    }

    public Participant getTeamOne() {
        return teamOne;
    }

    public void setTeamOne(Participant teamOne) {
        this.teamOne = teamOne;
    }

    public Participant getTeamTwo() {
        return teamTwo;
    }

    public void setTeamTwo(Participant teamTwo) {
        this.teamTwo = teamTwo;
    }

    public String getMatchTournament() {
        return matchTournament;
    }

    public void setMatchTournament(String matchTournament) {
        this.matchTournament = matchTournament;
    }
}
