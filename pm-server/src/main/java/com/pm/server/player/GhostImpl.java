package com.pm.server.player;

import org.springframework.stereotype.Component;

import com.pm.server.datatype.Coordinate;

@Component
public class GhostImpl implements Ghost {

	int id = 0;
	Coordinate location;

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setLocation(Coordinate location) {
		this.location = location;
	}

	public Coordinate getLocation() {
		return location;
	}

}