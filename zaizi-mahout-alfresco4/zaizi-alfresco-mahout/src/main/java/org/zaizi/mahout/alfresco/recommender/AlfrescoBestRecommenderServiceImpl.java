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
package org.zaizi.mahout.alfresco.recommender;

import org.apache.log4j.Logger;
import org.zaizi.mahout.config.RecommenderConfiguration;
import org.zaizi.mahout.eval.EvaluatorEngine;

/**
 * Created by IntelliJ IDEA.
 * User: jcarrey
 * Date: 05/09/2011
 * Time: 17:04
 * To change this template use File | Settings | File Templates.
 */
public class AlfrescoBestRecommenderServiceImpl extends AbstractAlfrescoRecommendationService
        implements AlfrescoRecommendationService {

    private static Logger logger = Logger.getLogger(AlfrescoBestRecommenderServiceImpl.class);

    private EvaluatorEngine evaluatorEngine;


    public void setEvaluatorEngine(EvaluatorEngine evaluatorEngine) {
        this.evaluatorEngine = evaluatorEngine;
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected RecommenderConfiguration getConfiguration() {
        return evaluatorEngine.getBestRecommender(getDataModel());
    }
}
