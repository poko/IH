package net.ecoarttech.ihplus.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ActionType {
	@JsonProperty("photo") PHOTO,
	@JsonProperty("note") NOTE,
	@JsonProperty("text") TEXT,
	@JsonProperty("meditate") MEDITATE;
}
