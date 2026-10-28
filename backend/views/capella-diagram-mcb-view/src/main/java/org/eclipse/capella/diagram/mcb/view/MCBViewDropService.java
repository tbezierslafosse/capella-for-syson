/*******************************************************************************
 * Copyright (c) 2026 Obeo.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.capella.diagram.mcb.view;

import java.util.Map;
import java.util.Objects;

import org.eclipse.capella.diagram.mcb.view.nodes.requirement.RequirementNodeDescriptionProvider;
import org.eclipse.capella.model.transverse.services.CommonQueryService;
import org.eclipse.sirius.components.collaborative.diagrams.DiagramContext;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IIdentityService;
import org.eclipse.sirius.components.core.api.IObjectSearchService;
import org.eclipse.sirius.components.diagrams.ViewCreationRequest;
import org.eclipse.sirius.components.diagrams.components.NodeContainmentKind;
import org.eclipse.sirius.components.diagrams.description.NodeDescription;
import org.eclipse.syson.sysml.Element;
import org.eclipse.syson.util.NodeFinder;
import org.springframework.stereotype.Service;

/**
 * Graphical reveal for MCB requirements.
 *
 * @author tbezierslafosse
 */
@Service
public class MCBViewDropService {

    private final IObjectSearchService objectSearchService;

    private final IIdentityService identityService;

    private final CommonQueryService commonQueryService = new CommonQueryService();

    public MCBViewDropService(IObjectSearchService objectSearchService, IIdentityService identityService) {
        this.objectSearchService = Objects.requireNonNull(objectSearchService);
        this.identityService = Objects.requireNonNull(identityService);
    }

    public Element dropFromExplorer(Element element, Object selectedNode, IEditingContext editingContext, DiagramContext diagramContext,
            Map<org.eclipse.sirius.components.view.diagram.NodeDescription, NodeDescription> convertedNodes) {
        var context = this.objectSearchService.getObject(editingContext, diagramContext.diagram().getTargetObjectId())
                .filter(Element.class::isInstance).map(Element.class::cast).filter(this.commonQueryService::isSystemAnalysisCapabilitiesPackage);
        if (context.isPresent() && this.commonQueryService.getRequirements(context.get()).contains(element)) {
            String id = this.identityService.getId(element);
            var existing = new NodeFinder(diagramContext.diagram()).getOneNodeMatching(node -> id.equals(node.getTargetObjectId()));
            boolean pending = diagramContext.viewCreationRequests().stream().anyMatch(request -> id.equals(request.getTargetObjectId()));
            if (existing.isEmpty() && !pending) {
                convertedNodes.entrySet().stream().filter(entry -> RequirementNodeDescriptionProvider.NODE_DESCRIPTION_NAME.equals(entry.getKey().getName())).findFirst()
                        .ifPresent(entry -> diagramContext.viewCreationRequests().add(ViewCreationRequest.newViewCreationRequest()
                                .parentElementId(diagramContext.diagram().getId()).descriptionId(entry.getValue().getId()).targetObjectId(id)
                                .containmentKind(NodeContainmentKind.CHILD_NODE).build()));
            }
        }
        return element;
    }

}
