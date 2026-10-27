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
package org.eclipse.capella.diagram.mcb.view.nodes.mission;

import org.eclipse.capella.diagram.common.view.nodes.AbstractNodeDescriptionProvider;
import org.eclipse.capella.diagram.mcb.view.MCBNodePaletteProvider;
import org.eclipse.capella.diagram.mcb.view.MCBViewQueryService;
import org.eclipse.sirius.components.view.builder.IViewDiagramElementFinder;
import org.eclipse.sirius.components.view.builder.providers.IColorProvider;
import org.eclipse.sirius.components.view.diagram.DiagramDescription;
import org.eclipse.sirius.components.view.diagram.InsideLabelPosition;
import org.eclipse.sirius.components.view.diagram.LabelOverflowStrategy;
import org.eclipse.sirius.components.view.diagram.LabelTextAlign;
import org.eclipse.sirius.components.view.diagram.NodeDescription;
import org.eclipse.sirius.components.view.diagram.OutsideLabelPosition;
import org.eclipse.sirius.components.view.diagram.SynchronizationPolicy;
import org.eclipse.sirius.components.view.diagram.UserResizableDirection;
import org.eclipse.syson.sysml.SysmlPackage;
import org.eclipse.syson.util.ServiceMethod;
import org.eclipse.syson.util.SysMLMetamodelHelper;

/**
 * Describes Mission nodes in MCB diagrams.
 *
 * @author tbezierslafosse
 */
public class MissionNodeDescriptionProvider extends AbstractNodeDescriptionProvider {

    public static final String NODE_DESCRIPTION_NAME = "MissionNodeDescription";

    public MissionNodeDescriptionProvider(IColorProvider colorProvider) {
        super(colorProvider);
    }

    @Override
    public NodeDescription create() {
        return this.diagramBuilderHelper.newNodeDescription().name(NODE_DESCRIPTION_NAME)
                .domainType(SysMLMetamodelHelper.buildQualifiedName(SysmlPackage.eINSTANCE.getOccurrenceUsage()))
                .defaultWidthExpression("72").defaultHeightExpression("72").userResizable(UserResizableDirection.NONE)
                .synchronizationPolicy(SynchronizationPolicy.UNSYNCHRONIZED)
                .semanticCandidatesExpression(ServiceMethod.of0(MCBViewQueryService::getMissionCandidates).aqlSelf())
                .insideLabel(this.diagramBuilderHelper.newInsideLabelDescription().labelExpression("aql:'M'").position(InsideLabelPosition.MIDDLE_CENTER)
                        .overflowStrategy(LabelOverflowStrategy.WRAP).textAlign(LabelTextAlign.CENTER)
                        .style(this.diagramBuilderHelper.newInsideLabelStyle().bold(true).borderSize(0).fontSize(16)
                                .labelColor(this.colorProvider.getColor("PURPOSE_LABEL")).showIconExpression("aql:false").withHeader(false).build()).build())
                .outsideLabels(this.diagramBuilderHelper.newOutsideLabelDescription().labelExpression("aql:self.name").position(OutsideLabelPosition.BOTTOM_CENTER)
                        .overflowStrategy(LabelOverflowStrategy.NONE).textAlign(LabelTextAlign.CENTER)
                        .style(this.diagramBuilderHelper.newOutsideLabelStyle().labelColor(this.colorProvider.getColor("PURPOSE_LABEL"))
                                .borderSize(0).fontSize(12).showIconExpression("aql:false").labelIcon("/icons/full/obj16/Mission.svg").build()).build())
                .style(this.diagramBuilderHelper.newRectangularNodeStyleDescription().background(this.colorProvider.getColor("PURPOSE_BACKGROUND"))
                        .borderColor(this.colorProvider.getColor("PURPOSE_BORDER")).borderSize(2).borderRadius(100).build())
                .build();
    }

    @Override
    public void link(DiagramDescription diagramDescription, IViewDiagramElementFinder cache) {
        var description = cache.getNodeDescription(NODE_DESCRIPTION_NAME).orElseThrow();
        description.setPalette(new MCBNodePaletteProvider().createNodePalette());
        diagramDescription.getNodeDescriptions().add(description);
    }
}
