package com.nsg.nsgdtlibrary.Classes.newData;

import com.google.maps.model.LatLng;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class newData {

    //TODO have to test
    public BigDecimal truncateDecimal(double x, int numberOfDecimals) {
        if (x > 0) {
            return new BigDecimal(String.valueOf(x)).setScale(numberOfDecimals, BigDecimal.ROUND_FLOOR);
        } else {
            return new BigDecimal(String.valueOf(x)).setScale(numberOfDecimals, BigDecimal.ROUND_CEILING);
        }
    }

    /**
     * @param oldRoute
     * @param newRoute
     * @param numberOfDecimals no of decimal places needed to consider, default value is 5
     * @return {boolean}
     */
    public boolean containsCompletely(List<LatLng> oldRoute, List<LatLng> newRoute, int numberOfDecimals) {
        boolean flag = true;
        if (numberOfDecimals == 0) {
            numberOfDecimals = 5; // default value
        }

        for (int i = 0, lastFoundIndex = 0; i < newRoute.size(); i++) {
            String newLat = String.valueOf(truncateDecimal(newRoute.get(i).lat, numberOfDecimals));
            String newLng = String.valueOf(truncateDecimal(newRoute.get(i).lng, numberOfDecimals));
            boolean innerFlag = false;

            // search for match in old flag
            for (int j = lastFoundIndex; j < oldRoute.size(); ) {

                String oldLat = String.valueOf(truncateDecimal(oldRoute.get(j).lat, numberOfDecimals));
                String oldLng = String.valueOf(truncateDecimal(oldRoute.get(j).lng, numberOfDecimals));

                if (newLat.equals(oldLat) && newLng.equals(oldLng)) {
                    innerFlag = true;
                    lastFoundIndex = j; // here we found, so the next checking will start from here
                    break; // break inner loop
                } else {
                    j++;
                }
            }

            // point does not matched
            if (innerFlag == false) {
                flag = false;
                break; // break outer loop
            }

        }

        return flag;
    }

    public double distance(LatLng pointA, LatLng pointB) {
        //TODO calculate distance
        return 0;
    }

    /**
     * @param oldRoute       previous route data
     * @param newRoute       route data got from the server
     * @param deviationPoint point in which the route API request is send and successfully received
     * @return new list of LatLng merging both list of coordinates, considering the deviationPoint
     */
    public List<LatLng> verifyAndMergeRoutes(List<LatLng> oldRoute, List<LatLng> newRoute, LatLng deviationPoint) {
        List<LatLng> mergedRoute = new ArrayList<>();

        if (oldRoute == null || oldRoute.size() == 0) {
            return newRoute;
        }

        if (newRoute == null
                || deviationPoint == null
                || newRoute.size() == 0
                || containsCompletely(oldRoute, newRoute, 5)) {
            return mergedRoute;
        }

        // find perpendicular point on old route from deviationPoint
        LatLng perpendicularPoint = new LatLng(0d, 0d);

        // add the first point of old route
        mergedRoute.add(oldRoute.get(0));

        //truncate the old route till perpendicular point
        for (int i = 1; i < oldRoute.size(); i++) {
            LatLng previousPoint = oldRoute.get(i - 1);
            LatLng currentPoint = oldRoute.get(i);

            double distanceA = distance(previousPoint, perpendicularPoint);
            double distanceB = distance(previousPoint, currentPoint);

            if (distanceB > distanceA) {
                // we found the position
                mergedRoute.add(perpendicularPoint);
                break;
            }

            // no need else as we breaking the loop
            mergedRoute.add(currentPoint);
        }

        // add deviation point
        mergedRoute.add(deviationPoint);
        // add the new route coordinates
        mergedRoute.addAll(newRoute);

        return mergedRoute;
    }
}
