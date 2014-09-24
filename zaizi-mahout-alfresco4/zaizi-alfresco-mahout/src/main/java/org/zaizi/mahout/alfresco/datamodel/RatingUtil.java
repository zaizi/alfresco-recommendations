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
package org.zaizi.mahout.alfresco.datamodel;

import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QNamePattern;
import org.alfresco.service.namespace.RegexQNamePattern;

/**
 * Created by IntelliJ IDEA.
 * User: jcarrey
 * Date: 05/09/2011
 * Time: 16:40
 * To change this template use File | Settings | File Templates.
 */
public class RatingUtil {

    private static final String RATING_ASSOC_SEPARATOR = "__";

    public static QNamePattern getRatingAssocNameFor(String username, String ratingSchemeName) {
        if (username == null) {
            username = "*";
        }

        return new RegexQNamePattern(NamespaceService.CONTENT_MODEL_1_0_URI, username);

    }

    /*
    public static QNamePattern getRatingAssocPatternForUser(String username)
    {
        if (username == null)
        {
            username = ".*";
        }

        String ratingSchemeName = "fiveStarRatingScheme";

        return new RegexQNamePattern(NamespaceService.CONTENT_MODEL_1_0_URI, username
        //+ RATING_ASSOC_SEPARATOR + ratingSchemeName
        );
    }
    */

    /*
    public static QName getRollupAspectNameFor(String ratingSchemeName)
    {
        String result = "cm:" + ratingSchemeName + "Rollups";
        return QName.createQName(result, ZaiziAlfrescoServiceUtil.getServiceRegistry().getNamespaceService());
    }


    public static QName getRollupAspectNameFor(RatingScheme ratingScheme)
    {
        return getRollupAspectNameFor(ratingScheme.getName());
    }*/

    /*
    public static QName getRollupPropertyNameFor(String ratingSchemeName, String rollupName)
    {
        String result = "cm:" + ratingSchemeName + rollupName;
        return QName.createQName(result, ZaiziAlfrescoServiceUtil.getServiceRegistry().getNamespaceService());
    }


    public static QName getRollupPropertyNameFor(RatingScheme ratingScheme, String rollupName)
    {
        return getRollupPropertyNameFor(ratingScheme.getName(), rollupName);
    }  */
}
