package net.ecoarttech.ihplus.api;

import net.ecoarttech.ihplus.model.HikeV2;
import java.util.List;

public class HikesResponse {

    private List<HikeV2> hikes;

    public List<HikeV2> getHikes(){
        return hikes;
    }
}
