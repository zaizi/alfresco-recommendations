/**
 * Alfresco Content Recommendation. Copyright (C) 2014 Zaizi Limited.
 *
 * ——————-
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 * ———————
 */
package org.zaizi.mahout.config;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.neighborhood.CachingUserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

/**
 * Created by IntelliJ IDEA.
 * User: jcarrey
 * Date: 12/09/2011
 * Time: 12:51
 * To change this template use File | Settings | File Templates.
 */
public class ClassNameNeighborHoodConfiguration implements NeighborhoodConfiguration{

    private String neighborHoodClassName;

    // Sampling Rate  (if 0.d not used)
    private double samplingRate = 0d;


    //*******************************************************************************************
    // Threshold Neighborhood - Configuration
    //*******************************************************************************************
    private double threshold = 0.8d;

    //*******************************************************************************************



    //*******************************************************************************************
    // Nearest N User - Configuration
    //*******************************************************************************************

    /**
     * Size of neighborhood
     */
    private int neighborhoodSize = 10;
    /**
     * Min similarity between two users to be available to belong to the same Neigborhood
     */
    private double minSimilarity = Double.NEGATIVE_INFINITY;

    //*******************************************************************************************


    public void setNeighborHoodClassName(String neighborHoodClassName) {
        this.neighborHoodClassName = neighborHoodClassName;
    }

    public UserNeighborhood getNeighborhood(DataModel dataModel, UserSimilarity userSimilarity)
            throws TasteException {

        UserNeighborhood neighborhood = null;
        if (ThresholdUserNeighborhood.class.getName().equals(neighborHoodClassName)) {
            if (samplingRate > 0d) {
                neighborhood = new ThresholdUserNeighborhood(threshold, userSimilarity,
                        dataModel, samplingRate);
            } else {
                neighborhood = new ThresholdUserNeighborhood(threshold, userSimilarity, dataModel);
            }
        } else {
            if (samplingRate > 0d) {
                neighborhood = new NearestNUserNeighborhood(neighborhoodSize, minSimilarity,
                        userSimilarity, dataModel, samplingRate);
            } else {
                neighborhood = new NearestNUserNeighborhood(neighborhoodSize, minSimilarity,
                        userSimilarity, dataModel);
            }
        }
        return new CachingUserNeighborhood(neighborhood, dataModel);
    }


    public void setSamplingRate(double samplingRate) {
        this.samplingRate = samplingRate;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public void setNeighborhoodSize(int neighborhoodSize) {
        this.neighborhoodSize = neighborhoodSize;
    }

    public void setMinSimilarity(double minSimilarity) {
        this.minSimilarity = minSimilarity;
    }

    @Override
    public String toString() {
        return new StringBuilder("ClassNameNeighborHoodConfiguration{").append(
                "neighborHoodClassName='").append( neighborHoodClassName).append( '\'').append(
                ", samplingRate=").append( samplingRate).append(
                ", threshold=").append( threshold).append(
                ", neighborhoodSize=").append( neighborhoodSize).append(
                ", minSimilarity=").append( minSimilarity).append(
                '}').toString();
    }
}
