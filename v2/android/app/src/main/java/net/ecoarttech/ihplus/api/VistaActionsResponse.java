package net.ecoarttech.ihplus.api;

import net.ecoarttech.ihplus.model.Action;
import net.ecoarttech.ihplus.model.HikeV2;

import java.util.List;

public class VistaActionsResponse {

    private List<Action> vista_actions;

    public List<Action> getActions(){
        return vista_actions;
    }
}
