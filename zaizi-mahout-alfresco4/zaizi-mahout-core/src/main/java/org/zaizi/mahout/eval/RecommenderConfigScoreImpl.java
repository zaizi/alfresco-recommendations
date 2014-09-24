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
package org.zaizi.mahout.eval;

import org.zaizi.mahout.config.RecommenderConfiguration;

/**
 * Created by IntelliJ IDEA.
 * User: jcarrey
 * Date: 12/09/2011
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class RecommenderConfigScoreImpl implements RecommenderConfigScore {

    private double score;

    private RecommenderConfiguration configuration;

    public double getScore() {
        return score;
    }

    public RecommenderConfiguration getConfiguration() {
        return configuration;
    }

    public RecommenderConfigScoreImpl(double score, RecommenderConfiguration configuration) {
        this.score = score;
        this.configuration = configuration;
    }

    public int compareTo(RecommenderConfigScore recommenderConfigScore) {
        return Double.valueOf(score).compareTo(recommenderConfigScore.getScore());
    }

    @Override
    public String toString() {
        return new StringBuilder("\nRecommenderConfigScoreImpl{").append(
                "score=").append( score).append(
                ", configuration=").append(configuration).append(
                '}').toString();
    }
}
