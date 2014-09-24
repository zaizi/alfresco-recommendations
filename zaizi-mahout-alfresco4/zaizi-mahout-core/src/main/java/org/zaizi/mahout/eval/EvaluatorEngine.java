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

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.mahout.cf.taste.model.DataModel;
import org.zaizi.mahout.config.RecommenderConfiguration;

public class EvaluatorEngine{
	
private List<RecommenderConfiguration> configurations;
    
    private MahoutEvaluator evaluator;

    private static Logger logger = Logger.getLogger(EvaluatorEngine.class);


    public void setConfigurations(List<RecommenderConfiguration> configurations) {
        this.configurations = configurations;
    }
    
    public void setRecommenderEvaluator(MahoutEvaluator evaluator){
    	this.evaluator = evaluator;
    }

	public RecommenderConfiguration getBestRecommender(DataModel dataModel) {
		 List<RecommenderConfigScore> scores = runEvaluation(dataModel);
	        if (logger.isInfoEnabled()) {
	            logger.info("- Evaluation Results -  \n" + scores);
	            logger.info("Evaluation finished - Best score (proximity to 0) = " + scores.get(0));
	        }
	        return scores.get(0).getConfiguration();
	}

	public List<RecommenderConfigScore> runEvaluation(DataModel dataModel) {
		if (logger.isInfoEnabled()) {
            logger.info("Evaluating configurations.. [Evaluation of data over configurations must be done just 1 time]");
        }
        return MahoutRecommenderEvaluator.evaluate(configurations, evaluator, dataModel);
	}

}
