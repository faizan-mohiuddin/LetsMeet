package com.LetsMeet.LetsMeet.Event.Service;

import java.util.ArrayList;
import java.util.List;

import com.LetsMeet.LetsMeet.Event.Model.EventProperties;
import com.LetsMeet.LetsMeet.Event.Model.EventResponse;
import com.LetsMeet.LetsMeet.Event.Model.Properties.GradedProperty;
import com.LetsMeet.LetsMeet.Event.Model.Properties.Location;

public class EventLocationSolver {
    
    private List<GradedProperty<Location>> solution;
    private List<Location> locations;


    public EventLocationSolver(EventProperties event, List<EventResponse> responses){
        this.solution = new ArrayList<>();
        this.locations = new ArrayList<>();


        locations.add(event.getLocation());
        for (EventResponse er : responses)
            locations.add(er.getEventProperties().getLocation());
    }

    public List<GradedProperty<Location>> solve(){
        locations.sort(null);

        for (int i = 0; i < locations.size(); i++)
            solution.add(new GradedProperty<>(locations.get(i), scan(i, -1) + scan(i, 1)));     // Scan for locations within this one to the left (-1) and right (1). Optimality is sum of locations inside.

        return solution;
    }

    private int scan(int origin, int step){
        int pos = origin + step;
        int hitCount = 0;

        while (pos >= 0 && pos < locations.size() 
            &&(locations.get(pos).projectX() + locations.get(pos).projectY() > locations.get(origin).projectX() + locations.get(origin).projectY() - locations.get(origin).getRadius())
            &&(locations.get(pos).projectX() + locations.get(pos).projectY() < locations.get(origin).projectX() + locations.get(origin).projectY() + locations.get(origin).getRadius())){//TODO use line projection for tighter bounds and greater efficiency
            
            if(distance(locations.get(origin), locations.get(pos)) <= locations.get(origin).getRadius())
                hitCount++;

            pos += step;
        }

        return hitCount;
    }

    public List<GradedProperty<Location>> getSolution(){
        return this.solution;
    }

    public double distance(Location a, Location b){

        // Convert to points on 2D plane
        double ax = a.projectX();
        double ay = a.projectY();
        double bx = b.projectX();
        double by = b.projectY();

        return Math.sqrt(Math.pow(ax-bx,2) - Math.pow(ay-by,2));
    }
}