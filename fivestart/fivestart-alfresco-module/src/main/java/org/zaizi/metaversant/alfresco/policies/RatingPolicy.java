/**
 * 
 */
package org.zaizi.metaversant.alfresco.policies;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.node.NodeServicePolicies.OnCreateNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnDeleteNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnUpdateNodePolicy;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zaizi.metaversant.alfresco.model.RatingsModel;

/**
 * @author iarroyo
 *
 */
public class RatingPolicy implements OnCreateNodePolicy, OnUpdateNodePolicy, OnDeleteNodePolicy
{

    private Log logger = LogFactory.getLog(RatingPolicy.class);

    private PolicyComponent policyComponent;
    private BehaviourFilter behaviourFilter;
    private NodeService nodeService;

    /**
     * Class initialization
     */
    public void init()
    {
        this.policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
                RatingsModel.TYPE_MR_RATING, new JavaBehaviour(this, "onCreateNode",
                        NotificationFrequency.TRANSACTION_COMMIT));

        this.policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdateNodePolicy.QNAME,
                RatingsModel.TYPE_MR_RATING, new JavaBehaviour(this, "onUpdateNode",
                        NotificationFrequency.TRANSACTION_COMMIT));

        this.policyComponent.bindClassBehaviour(NodeServicePolicies.OnDeleteNodePolicy.QNAME,
                RatingsModel.TYPE_MR_RATING, new JavaBehaviour(this, "onDeleteNode",
                        NotificationFrequency.TRANSACTION_COMMIT));

        if (logger.isDebugEnabled())
        {
            logger.debug("Initialized policy for rating");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.alfresco.repo.node.NodeServicePolicies.OnCreateNodePolicy#onCreateNode(org.alfresco.service.cmr.repository
     * .ChildAssociationRef)
     */
    @Override
    public void onCreateNode(ChildAssociationRef childAssocRef)
    {
        computeAverage(childAssocRef.getParentRef());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.alfresco.repo.node.NodeServicePolicies.OnUpdateNodePolicy#onUpdateNode(org.alfresco.service.cmr.repository
     * .NodeRef)
     */
    @Override
    public void onUpdateNode(NodeRef nodeRef)
    {
        computeAverage(nodeService.getPrimaryParent(nodeRef).getParentRef());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.alfresco.repo.node.NodeServicePolicies.OnDeleteNodePolicy#onDeleteNode(org.alfresco.service.cmr.repository
     * .ChildAssociationRef, boolean)
     */
    @Override
    public void onDeleteNode(ChildAssociationRef childAssocRef, boolean isNodeArchived)
    {
        computeAverage(childAssocRef.getParentRef());
    }

    private void computeAverage(NodeRef nodeRef)
    {
        if (nodeService.exists(nodeRef) && nodeService.hasAspect(nodeRef, RatingsModel.ASPECT_MR_RATEABLE))
        {
            boolean isWorkingCopy = nodeService.hasAspect(nodeRef, ContentModel.ASPECT_WORKING_COPY);

            if (!isWorkingCopy)
            {

                // Disable auditable behaviour
                behaviourFilter.disableBehaviour(nodeRef, ContentModel.ASPECT_AUDITABLE);

                try
                {
                    Float average = 0.0F;
                    Integer total = 0, count = 0;

                    // List<ChildAssociationRef> children = nodeService.getChildAssocs(nodeRef,
                    // RatingsModel.ASSN_MR_RATINGS, RatingsModel.TYPE_MR_RATING);

                    Set<QName> childNodeTypeQNames = new HashSet<QName>();
                    childNodeTypeQNames.add(RatingsModel.TYPE_MR_RATING);
                    List<ChildAssociationRef> children = nodeService.getChildAssocs(nodeRef, childNodeTypeQNames);

                    if (children != null && children.size() > 0)
                    {
                        count = children.size();

                        for (ChildAssociationRef car : children)
                        {
                            NodeRef childRef = car.getChildRef();
                            Integer rating = (Integer) nodeService.getProperty(childRef, RatingsModel.PROP_RATING);
                            total += rating;
                        }

                        // Compute the average
                        average = total.floatValue() / count.floatValue();
                    }

                    if (logger.isInfoEnabled())
                    {
                        logger.info("Computed average " + average);
                    }

                    // Store the average on the parent node
                    nodeService.setProperty(nodeRef, RatingsModel.PROP_AVERAGE_RATING, average);
                    nodeService.setProperty(nodeRef, RatingsModel.PROP_TOTAL_RATING, total);
                    nodeService.setProperty(nodeRef, RatingsModel.PROP_RATING_COUNT, count);
                }
                finally
                {
                    behaviourFilter.enableBehaviour(nodeRef, ContentModel.ASPECT_AUDITABLE);
                }

            }
            else
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("The nodeRef " + nodeRef + " doesn't exist");
                }
            }
        }
    }

    /**
     * Inject Alfresco's policy component
     * 
     * @param policyComponent Alfresco's policy component
     */
    public void setPolicyComponent(PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }

    /**
     * Set the service registry.
     * 
     * @param services Service registry
     */
    public void setServiceRegistry(ServiceRegistry services)
    {
        this.nodeService = services.getNodeService();
    }

    /**
     * @param behaviourFilter the behaviourFilter to set
     */
    public void setBehaviourFilter(BehaviourFilter behaviourFilter)
    {
        this.behaviourFilter = behaviourFilter;
    }

}
