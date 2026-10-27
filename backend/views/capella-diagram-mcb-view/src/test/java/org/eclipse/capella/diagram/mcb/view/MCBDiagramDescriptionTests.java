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

import org.eclipse.sirius.components.view.diagram.RectangularNodeStyleDescription;
import org.eclipse.sirius.components.view.diagram.SynchronizationPolicy;
import org.eclipse.sirius.components.view.diagram.UserResizableDirection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.eclipse.capella.tests.diagrams.AbstractEditableDiagramDescriptionTests;
import org.eclipse.sirius.components.view.View;
import org.eclipse.sirius.components.view.FixedColor;
import org.eclipse.sirius.components.view.builder.providers.IColorProvider;
import org.eclipse.sirius.components.view.builder.providers.IRepresentationDescriptionProvider;
import org.eclipse.sirius.components.view.emf.IJavaServiceProvider;

/**
 * Checks the structure of the Mission and Capabilities Blank diagram.
 *
 * @author tbezierslafosse
 */
public class MCBDiagramDescriptionTests extends AbstractEditableDiagramDescriptionTests {

    @Override
    protected IColorProvider getColorProvider(View view) {
        return new MCBViewDescriptionProvider().getColorProvider(view);
    }

    @Override
    protected IRepresentationDescriptionProvider getRepresentationDescriptionProvider() {
        return new MCBViewDiagramDescriptionProvider();
    }

    @Override
    protected IJavaServiceProvider getJavaServiceProvider() {
        return new MCBViewJavaServiceProvider();
    }
    @Test
    public void nodesShouldHaveCompletePalettesAndRemainUnsynchronized() {
        var nodes = this.diagramDescription.getNodeDescriptions();
        assertThat(nodes).extracting(node -> node.getName())
                .containsExactlyInAnyOrder("RequirementNodeDescription");
        assertThat(nodes).allSatisfy(node -> {
            assertThat(node.getSynchronizationPolicy()).isEqualTo(SynchronizationPolicy.UNSYNCHRONIZED);
            assertThat(node.getPalette().getLabelEditTool()).isNotNull();
            assertThat(node.getPalette().getDeleteTool()).isNotNull();
            assertThat(node.getPalette().getQuickAccessTools()).extracting(tool -> tool.getName()).contains("Delete from Diagram");
            assertThat(node.getEdgeTools()).isEmpty();
        });
        assertThat(nodes).filteredOn(node -> "RequirementNodeDescription".equals(node.getName()))
                .allSatisfy(node -> assertThat(node.getUserResizable()).isEqualTo(UserResizableDirection.BOTH));
        assertThat(this.diagramDescription.getPalette().getNodeTools()).extracting(tool -> tool.getName())
                .containsExactly("New Requirement");
    }

    @ParameterizedTest
    @CsvSource({ "RequirementNodeDescription, #D8C3D6, #72496E, #72496E" })
    public void nodesShouldUseTheStandardCapellaColors(String descriptionName, String background, String border, String label) {
        var node = this.diagramDescription.getNodeDescriptions().stream().filter(candidate -> descriptionName.equals(candidate.getName())).findFirst().orElseThrow();
        assertThat(node.getStyle()).isInstanceOfSatisfying(RectangularNodeStyleDescription.class, style -> {
            assertThat(style.getBackground()).isInstanceOfSatisfying(FixedColor.class, color -> assertThat(color.getValue()).isEqualTo(background));
            assertThat(style.getBorderColor()).isInstanceOfSatisfying(FixedColor.class, color -> assertThat(color.getValue()).isEqualTo(border));
        });
        assertThat(node.getInsideLabel().getStyle().getLabelColor()).isInstanceOfSatisfying(FixedColor.class, color -> assertThat(color.getValue()).isEqualTo(label));
    }

    @Test
    @Override
    public void eachRequirementDescribesEdgeHasTheCommonPresentation() {
        // Nodes are delivered independently from edges: no partial edge mapping or tool is exposed.
        assertThat(this.diagramDescription.getEdgeDescriptions()).isEmpty();
        assertThat(this.diagramDescription.getNodeDescriptions()).allSatisfy(node -> assertThat(node.getEdgeTools()).isEmpty());
    }
}
