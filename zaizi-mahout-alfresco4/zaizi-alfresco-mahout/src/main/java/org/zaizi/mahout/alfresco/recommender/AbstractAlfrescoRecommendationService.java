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

import java.util.List;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.log4j.Logger;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.recommender.NullRescorer;
import org.apache.mahout.cf.taste.model.DataModel;
import org.zaizi.mahout.alfresco.ZaiziAlfrescoServiceUtil;
import org.zaizi.mahout.config.RecommenderConfiguration;
import org.zaizi.mahout.eval.MahoutEvaluator;
import org.zaizi.mahout.recommendation.ItemService;
import org.zaizi.mahout.recommendation.RecommendationFactory;
import org.zaizi.mahout.recommendation.RecommendationService;

/**
 * Created by IntelliJ IDEA.
 * User: jcarrey
 * Date: 12/09/2011
 * Time: 14:22
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractAlfrescoRecommendationService implements AlfrescoRecommendationService {

    private RecommendationService<NodeRef> recommendationService;

    private DataModel dataModel;
    
    private MahoutEvaluator evaluator;

      private ItemService<NodeRef> getItemService() {
          return new ZaiziAlfrescoItemService();
      }

    public void setDataModel(DataModel dataModel) {
        this.dataModel = dataModel;
    }

    public DataModel getDataModel() {
        return dataModel;
    }
    
    public void setEvaluator(MahoutEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    public MahoutEvaluator getEvaluator() {
        return evaluator;
    }

    public void initialize() throws TasteException {
        try {

            RecommenderConfiguration configuration = getConfiguration();

            recommendationService
                    = new RecommendationFactory<NodeRef>().getRecommender(getItemService(),
                    configuration, dataModel, evaluator, false);
            if (getLogger().isInfoEnabled()) {
                getLogger().info("RecommendationsService - Implementation Available");
                getLogger().info("Using configuration: " + configuration);
                getLogger().info("DataModel implementation: " + dataModel.getClass().getName());
            }
        } catch (TasteException e) {
            getLogger().error("RecommendationsService failed to initialize : " + e.getMessage(), e);
            throw e;
        }
    }

    public RecommendationService<NodeRef> getRecommendationService() throws TasteException {
        initialize();
        return recommendationService;
    }
    
    public List<NodeRef> getRecommendations(int numResults) throws TasteException {
        String username = AuthenticationUtil.getFullyAuthenticatedUser();
        long userId = ZaiziAlfrescoServiceUtil.getUserId(username);

        List<NodeRef> nodeRefs = null;
        try {
            nodeRefs = getRecommendationService().getRecommendations(userId, numResults, NullRescorer.getUserInstance());
        } catch (TasteException e) {
            getLogger().error("Error obtaining recommendations: " + e.getMessage(), e);
            throw e;
        }
        return nodeRefs;
    }

    protected abstract Logger getLogger();

    protected abstract RecommenderConfiguration getConfiguration();

}
