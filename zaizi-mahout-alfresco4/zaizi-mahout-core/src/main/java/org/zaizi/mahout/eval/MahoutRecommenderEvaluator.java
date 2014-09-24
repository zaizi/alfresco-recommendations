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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.zaizi.mahout.config.RecommenderConfiguration;

/**
 * Created by IntelliJ IDEA.
 * User: jcarrey
 * Date: 12/09/2011
 * Time: 12:26
 * To change this template use File | Settings | File Templates.
 */
public class MahoutRecommenderEvaluator {

    private static Logger logger = Logger.getLogger(MahoutRecommenderEvaluator.class);


    /**
     * Evaluate recommender
     *
     * @param recommender - The recommender to be evaluated
     * @param evaluator   - The evaluator
     * @return Score of the recommender
     * @throws TasteException
     */
    public static RecommenderScore evaluate(Recommender recommender, MahoutEvaluator evaluator) throws TasteException {

        RecommenderBuilder builder = new DefaultRecommenderBuilder(recommender);

        double score = evaluator.evaluate(builder, recommender.getDataModel());

        return new RecommenderScoreImpl(recommender, score);
    }

    /**
     * Evaluate recommenders with evaluator
     *
     * @param recommenders - List of recommenders to evaluate
     * @param evaluator    - Evaluator implementation
     * @return Ordered list of recommenders by evaluation score
     */
    public static List<RecommenderScore> evaluate(List<Recommender> recommenders, MahoutEvaluator evaluator) {
        List<RecommenderScore> recommenderScores = new ArrayList<RecommenderScore>(recommenders.size());

        for (Recommender recommender : recommenders) {
            try {
                recommenderScores.add(evaluate(recommender, evaluator));

            } catch (TasteException e) {
                logger.warn("Cant evaluate recommender : " + e, e);
            }
        }

        Collections.sort(recommenderScores);
        return recommenderScores;
    }

    
    public static RecommenderConfigScore evaluate(RecommenderConfiguration configuration,
    		MahoutEvaluator evaluator,
    		DataModel dataModel) {

    		RecommenderConfigScore result = null;
    	
    		try {

    			result = new RecommenderConfigScoreImpl(evaluator.evaluate(configuration, dataModel), configuration);

    		} catch (TasteException e) {
    			logger.warn("Cant evaluate recommender : " + e, e);
    		}
    		
    	return result;
    }


    public static List<RecommenderConfigScore> evaluate(List<RecommenderConfiguration> configurations,
                                                        MahoutEvaluator evaluator,
                                                        DataModel dataModel) {

        List<RecommenderConfigScore> recommenderScores = new ArrayList<RecommenderConfigScore>(configurations.size());

        for (RecommenderConfiguration recommender : configurations) {
            try {

                recommenderScores.add(new RecommenderConfigScoreImpl(evaluator.evaluate(recommender, dataModel), recommender));

            } catch (TasteException e) {
                logger.warn("Cant evaluate recommender : " + e, e);
            }
        }

        Collections.sort(recommenderScores);
        return recommenderScores;
    }
}