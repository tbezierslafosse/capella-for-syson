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

import java.util.List;

import org.eclipse.capella.model.transverse.services.ArcadiaEngineeringPerspective;
import org.eclipse.capella.model.transverse.services.CommonQueryService;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.syson.services.UtilService;
import org.eclipse.syson.sysml.Element;
import org.eclipse.syson.sysml.SysmlPackage;

/**
 * Read-only semantic candidates and parent paths used by MCB nodes.
 *
 * @author tbezierslafosse
 */
public class MCBViewQueryService {

    private final CommonQueryService commonQueryService = new CommonQueryService();

    private final UtilService utilService = new UtilService();

    public List<Element> getRequirementCandidates(EObject context) {
        return this.utilService.getAllReachable(context, SysmlPackage.eINSTANCE.getRequirementUsage()).stream()
                .filter(Element.class::isInstance).map(Element.class::cast).filter(this::isInSystemAnalysis)
                .filter(element -> this.commonQueryService.getRequirementsPackage(element).filter(parent -> EcoreUtil.isAncestor(parent, element)).isPresent())
                .toList();
    }

    private boolean isInSystemAnalysis(Element element) {
        return this.commonQueryService.getArcadiaPerspective(element).filter(ArcadiaEngineeringPerspective.SystemAnalysis::equals).isPresent();
    }
}
