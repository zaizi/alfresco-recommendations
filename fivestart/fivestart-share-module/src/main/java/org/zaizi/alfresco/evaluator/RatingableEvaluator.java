package org.zaizi.alfresco.evaluator;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.evaluator.BaseEvaluator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * @author Ivan Arroyo
 * 
 */
public class RatingableEvaluator extends BaseEvaluator
{

    // Some constants
    private static final String NODE = "node";
    private static final String TYPE = "type";
    private static final String TYPE_CONTENT = "cm:content";
    private static final String ASPECT_MR_RATEABLE = "mr:rateable";

    @Override
    public boolean evaluate(JSONObject json)
    {
        boolean res = false;
        try
        {
            JSONArray nodeAspects = getNodeAspects(json);
            JSONObject nodeJSON = (JSONObject) json.get(NODE);
            String type = (String) nodeJSON.get(TYPE);

            if (nodeAspects != null)
            {
                res = TYPE_CONTENT.equals(type) && nodeAspects.contains(ASPECT_MR_RATEABLE);
            }
        }
        catch (Exception err)
        {
            throw new AlfrescoRuntimeException("Failed to run action evaluator: " + err.getMessage());
        }

        return res;

    }

}
