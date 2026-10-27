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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.capella.model.transverse.services.CommonCreationService;
import org.eclipse.capella.tests.fixtures.CapellaModel;
import org.eclipse.capella.tests.fixtures.SemanticDataTestFixture;
import org.eclipse.sirius.components.collaborative.diagrams.DiagramContext;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IIdentityService;
import org.eclipse.sirius.components.core.api.IObjectSearchService;
import org.eclipse.sirius.components.diagrams.Diagram;
import org.eclipse.sirius.components.diagrams.DiagramStyle;
import org.eclipse.sirius.components.diagrams.Node;
import org.eclipse.sirius.components.diagrams.description.NodeDescription;
import org.eclipse.sirius.components.view.diagram.DiagramFactory;
import org.eclipse.syson.sysml.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Exercises MCB reveal and move services against real semantic models.
 *
 * @author tbezierslafosse
 */
public class MCBViewServicesTests {

    private static final SemanticDataTestFixture FIXTURE = new SemanticDataTestFixture();

    private final CommonCreationService creation = new CommonCreationService();

    private final MCBViewQueryService queries = new MCBViewQueryService();

    private final IEditingContext editingContext = mock(IEditingContext.class);

    private final IObjectSearchService objectSearchService = mock(IObjectSearchService.class);

    private final IIdentityService identityService = mock(IIdentityService.class);

    private CapellaModel model;

    private MCBViewDropService dropService;

    @BeforeEach
    public void setUp() {
        this.model = FIXTURE.createCapellaModel();
        this.dropService = new MCBViewDropService(this.objectSearchService, this.identityService);
        when(this.identityService.getId(any())).thenAnswer(invocation -> ((Element) invocation.getArgument(0)).getElementId());
        when(this.objectSearchService.getObject(this.editingContext, "context"))
                .thenReturn(Optional.of(this.model.getSystemAnalysisPerspective().getCapabilitiesPackage().getElement()));
    }

    @Test
    public void dropFromExplorerShouldAcceptSupportedNodesAndRejectOtherPerspectives() {
        var context = this.model.getSystemAnalysisPerspective().getCapabilitiesPackage().getElement();
        var elements = List.of(this.creation.createRequirement(context));
        var diagramContext = this.diagramContext(List.of());
        var descriptions = this.descriptions("RequirementNodeDescription");
        elements.forEach(element -> this.dropService.dropFromExplorer(element, null, this.editingContext, diagramContext, descriptions));
        assertThat(diagramContext.viewCreationRequests()).hasSize(1);
        var foreignActor = this.creation.createActor(this.model.getLogicalArchitecturePerspective().getStructurePackage().getElement());
        this.dropService.dropFromExplorer(foreignActor, null, this.editingContext, diagramContext, descriptions);
        assertThat(diagramContext.viewCreationRequests()).hasSize(1);
        assertThat(this.queries.getRequirementCandidates(context)).hasSize(1);
    }

    private DiagramContext diagramContext(List<Node> nodes) {
        var diagram = Diagram.newDiagram("diagram").descriptionId("description").targetObjectId("context").nodes(nodes).edges(List.of())
                .style(DiagramStyle.newDiagramStyle().build()).build();
        return new DiagramContext(diagram, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    private Map<org.eclipse.sirius.components.view.diagram.NodeDescription, NodeDescription> descriptions(String... names) {
        Map<org.eclipse.sirius.components.view.diagram.NodeDescription, NodeDescription> result = new HashMap<>();
        for (String name : names) {
            var view = DiagramFactory.eINSTANCE.createNodeDescription();
            view.setName(name);
            var runtime = mock(NodeDescription.class);
            when(runtime.getId()).thenReturn(name);
            result.put(view, runtime);
        }
        return result;
    }

}
