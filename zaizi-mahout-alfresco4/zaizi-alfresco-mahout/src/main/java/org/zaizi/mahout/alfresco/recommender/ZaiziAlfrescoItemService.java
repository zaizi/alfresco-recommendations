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

import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.zaizi.mahout.alfresco.ZaiziAlfrescoServiceUtil;
import org.zaizi.mahout.recommendation.ItemService;

/**
 * Created by IntelliJ IDEA.
 * User: jcarrey
 * Date: 12/09/2011
 * Time: 13:19
 * To change this template use File | Settings | File Templates.
 */
public class ZaiziAlfrescoItemService implements ItemService<NodeRef> {


    public NodeRef getItemById(long itemId, float score) {
        return ZaiziAlfrescoServiceUtil.getNodeRefByEntryId(itemId);
    }

    public NodeRef getItemById(RecommendedItem item) {
        return ZaiziAlfrescoServiceUtil.getNodeRefByEntryId(item.getItemID());
    }
}
