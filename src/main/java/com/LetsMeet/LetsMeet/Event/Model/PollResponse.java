//-----------------------------------------------------------------
// PollResponse.java
// Let's Meet 2021
//
// Models the response to a Poll (Poll.java). PollResponse is an array of n booleans representing whether choice n in Poll was selected. 
// PollResponse can have multiple true choices. PollResponse may have more/less choices than the Poll has options, the mapping here is 1:1 from n=0

package com.LetsMeet.LetsMeet.Event.Model;

//-----------------------------------------------------------------

import java.util.ArrayList;
import java.util.UUID;


//-----------------------------------------------------------------

public class PollResponse {

    private UUID pollUUID;
    private UUID uuid;

    ArrayList<Boolean> choices;

    public PollResponse(UUID pollUUID, UUID uuid, ArrayList<Boolean> choices) {
        this.pollUUID = pollUUID;
        this.uuid = uuid;
        this.choices = choices;
    }

    public UUID getPollUUID() {
        return pollUUID;
    }

    public void setPollUUID(UUID pollUUID) {
        this.pollUUID = pollUUID;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public ArrayList<Boolean> getChoices() {
        return choices;
    }

    public void setChoices(ArrayList<Boolean> choices) {
        this.choices = choices;
    }

}
