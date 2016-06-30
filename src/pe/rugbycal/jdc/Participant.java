package pe.rugbycal.jdc;

import net.fortuna.ical4j.filter.PeriodRule;

/**
 *
 * Created by juliofdiaz on 6/18/16.
 */
class Participant {
    private String name;
    private String abbreviation;

    public Participant(){
        this.name = null;
        this.abbreviation = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if(obj instanceof Participant){
            Participant newParticipant = (Participant) obj;
            if(newParticipant.getName().equals(this.getName())){
                if(newParticipant.getAbbreviation().equals(this.getAbbreviation())){
                    result = true;
                }
            }
        }
        return result;
    }
}
