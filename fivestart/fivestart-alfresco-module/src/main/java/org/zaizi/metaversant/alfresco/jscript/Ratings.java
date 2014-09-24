package org.zaizi.metaversant.alfresco.jscript;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.jscript.ValueConverter;
import org.alfresco.repo.processor.BaseProcessorExtension;
import org.alfresco.service.cmr.repository.NodeRef;
import org.zaizi.metaversant.alfresco.service.RatingService;

/**
 * @author iarroyo
 *
 */
public class Ratings extends BaseProcessorExtension
{

    private RatingService ratingService;

    private final ValueConverter valueConverter = new ValueConverter();

    /**
     * @param scriptNode
     * @param rating
     * @param user
     */
    public void rate(ScriptNode scriptNode, int rating, String user)
    {
        ratingService.rate((NodeRef) valueConverter.convertValueForRepo(scriptNode), rating, user);
    }

    /**
     * @param scriptNode
     */
    public void deleteRatings(ScriptNode scriptNode)
    {
        ratingService.deleteRatings((NodeRef) valueConverter.convertValueForRepo(scriptNode));
    }

    /**
     * @param scriptNode
     * @return
     */
    public Object getRatingData(ScriptNode scriptNode)
    {
        return ratingService.getRatingData((NodeRef) valueConverter.convertValueForRepo(scriptNode));
    }

    /**
     * @param scriptNode
     * @param user
     * @return
     */
    public int getUserRating(ScriptNode scriptNode, String user)
    {
        return ratingService.getUserRating((NodeRef) valueConverter.convertValueForRepo(scriptNode), user);
    }

    /**
     * @param scriptNode
     * @return
     */
    public boolean hasRatings(ScriptNode scriptNode)
    {
        return ratingService.hasRatings((NodeRef) valueConverter.convertValueForRepo(scriptNode));
    }

    /**
     * @param ratingService
     */
    public void setRatingService(RatingService ratingService)
    {
        this.ratingService = ratingService;
    }

}
