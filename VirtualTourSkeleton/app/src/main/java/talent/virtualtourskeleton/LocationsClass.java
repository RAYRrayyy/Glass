package talent.virtualtourskeleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.maps.model.LatLng;

/**
 * A class to contain all the relevant location data
 *
 * Created by Talent on 6/06/2017
 */

public class LocationsClass {

    // Declare Hotspots name and coordinates for map

    public final static String spotNames[] = {
            // Transports
            "UQ Chancellor's Place",
            "UQ Lakes Bus Stop",
            "Citycat Stop - Brisbane River",
            //Help
            "Student Services",
            "UQ Security",
            //Landmarks
            "UQ Great Court",
            "UQ Lakes Area",
            "Coop Bookshop",
            "Schonell Theater - Pizza Cafe",
            "UQ Centre",
            "GP South - 3010 Rover Memorial",
            // Buildings with Libraries
            "Forgan Smith - Law Library",
            "Biological Science Library",
            "Hawken Engineering Library",
            "Zelman Cowen - Architecture/Music Library",
            "Duhig North - Social Sciences Building Library",
            // Buildings with Museums
            "Parnell Building - Physics Museum",
            "Michie Building - Anthropology Museum",
            "UQ Art Museum",
            "Chemistry Building - Centre for Organic Photonoics",
            "Advanced Engineering Building - Superior Centre for Electronic Material Manufacture",
            // Food and Drinks
            "Physiol Cafeteria",
            "Main Refectory - Main Course, Pizza Cafe, Red Room",
            // Recreation and Sporting
            "Fitness Centre",
            "Aquatic Centre",
            "Tennis Centre/Basketball Courts",
            "Athletics and Playing Fields"
    };

    public static final LatLng spotsCoordinates[] = {
            new LatLng(-27.495431, 153.012030), // Chancellors
            new LatLng(-27.497704, 153.017949), // Lakes Bus Stop
            new LatLng(-27.496761, 153.019534), // Ferry Terminal
            new LatLng(-27.495431, 153.012030), // Student Services
            new LatLng(-27.498879, 153.013759), // UQ Security
            new LatLng(-27.497542, 153.013302), // Great Court
            new LatLng(-27.498756, 153.015768), // Lakes Area
            new LatLng(-27.497968, 153.014384), // Coop Bookshop
            new LatLng(-27.497485, 153.016564), // Schonell Theater
            new LatLng(-27.495977, 153.016281), // UQ Centre
            new LatLng(-27.499996, 153.015170), // GPSouth 3010 Rover Memorial
            new LatLng(-27.496752, 153.013700), // Forgan Smith
            new LatLng(-27.496990, 153.011403), // BioScience
            new LatLng(-27.499999, 153.013676), // Hawken Engineering
            new LatLng(-27.499014, 153.014724), // Zelman Cowen
            new LatLng(-27.496040, 153.013634), // Duhig North Library
            new LatLng(-27.498204, 153.013039), // Parnell
            new LatLng(-27.497224, 153.011762), // Michie
            new LatLng(-27.496499, 153.012020), // Art Museum
            new LatLng(-27.499648, 153.013039), // Chemistry Building
            new LatLng(-27.499464, 153.015045), // Advanced Engineering
            new LatLng(-27.499075, 153.012261), // Physiol Cafeteria
            new LatLng(-27.497405, 153.015882), // Main Refectory
            new LatLng(-27.496000, 153.015631), // UQ Fitness Centre
            new LatLng(-27.494987, 153.016422), // Aquatic Centre
            new LatLng(-27.494567, 153.015145), // Tennis and Basketbal Courts
            new LatLng(-27.493431, 153.012188) // Athletics Field
    };

    public static List<String> createGroupList() {
        List<String> groupList = new ArrayList<String>();
        groupList.add("Transport");
        groupList.add("Help");
        groupList.add("Landmarks");
        groupList.add("Libraries");
        groupList.add("Museums");
        groupList.add("Beverages");
        groupList.add("Recreation/Sporting");
        return groupList;
    }

    public static Map<String, List<String>> createCollection() {
        Map<String, List<String>> locationCollection;
        List<String> childList = new ArrayList<String>();

        // preparing laptops collection(child)
        String[] transportLocations = new String[3];
        String[] helpLocations = new String[2];
        String[] landmarkLocations = new String[6];
        String[] libraryLocations = new String[5];
        String[] museumLocations = new String[5];
        String[] beverageLocations = new String[2];
        String[] recreationLocations = new String[4];

        for (int i = 0; i < spotNames.length; i++) {
            if (i < 3) {
                transportLocations[i] = spotNames[i];
            } else if (i < 5) {
                helpLocations[i-3] = spotNames[i];
            } else if (i < 11) {
                landmarkLocations[i-5] = spotNames[i];
            } else if (i < 16) {
                libraryLocations[i-11] = spotNames[i];
            } else if (i < 21) {
                museumLocations[i-16] = spotNames[i];
            } else if (i < 23) {
                beverageLocations[i-21] = spotNames[i];
            } else if (i < 27) {
                recreationLocations[i-23] = spotNames[i];
            }
        }

        locationCollection = new LinkedHashMap<String, List<String>>();

        for (String location : createGroupList()) {
            if (location.equals("Transport"))
                childList = loadChild(transportLocations);
            else if (location.equals("Help"))
                childList = loadChild(helpLocations);
            else if (location.equals("Landmarks"))
                childList = loadChild(landmarkLocations);
            else if (location.equals("Libraries"))
                childList = loadChild(libraryLocations);
            else if (location.equals("Museums"))
                childList = loadChild(museumLocations);
            else if (location.equals("Beverages"))
                childList = loadChild(beverageLocations);
            else if (location.equals("Recreation/Sporting"))
                childList = loadChild(recreationLocations);

            locationCollection.put(location, childList);
        }
        return locationCollection;
    }

    private static List<String> loadChild(String[] locationModels) {
        List<String> childList = new ArrayList<String>();
        for (String model : locationModels)
            childList.add(model);
        return childList;
    }
}
