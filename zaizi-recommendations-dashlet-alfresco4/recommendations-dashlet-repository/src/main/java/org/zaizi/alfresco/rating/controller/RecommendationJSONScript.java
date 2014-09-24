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
package org.zaizi.alfresco.rating.controller;

import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.log4j.Logger;
import org.apache.mahout.cf.taste.common.TasteException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.*;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRequest;
import org.zaizi.mahout.alfresco.recommender.AlfrescoRecommendationService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jcarrey
 * Date: 08/09/2011
 * Time: 08:49
 * To change this template use File | Settings | File Templates.
 */
public class RecommendationJSONScript extends AbstractWebScript {

    private AlfrescoRecommendationService recommendationService;

    private Logger logger = Logger.getLogger(RecommendationJSONScript.class);

    public void setRecommendationService(AlfrescoRecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
        List<NodeRef> nodes = getNodeRefs(req);


        try {
            JSONObject object = JsonNodeRefUtil.transform(nodes);
            String jsonString = object.toString();
            res.getWriter().write(jsonString);
            res.setContentType("application/json");
            res.setStatus(Status.STATUS_OK);
        } catch (JSONException e) {
            throw new WebScriptException("Error writing JSON", e);
        }

    }


    private List<NodeRef> getNodeRefs(WebScriptRequest req) {

        String param = req.getParameter("maxResults");
        int maxResults;

        if (param == null || "".equals(param)) {
            maxResults = 50;
        } else {
            try {
                maxResults = Integer.parseInt(param);
            } catch (NumberFormatException nfe) {
                maxResults = 50;
            }
        }

        try {
            return recommendationService.getRecommendations(maxResults);
        } catch (TasteException e) {
            logger.error("Error getting recommendations." + e.getMessage(), e);
            return new ArrayList<NodeRef>(0);
        } catch (Exception e) {
            logger.error("Error getting recommendations." + e.getMessage(), e);
            return new ArrayList<NodeRef>(0);
        }
    }


}
