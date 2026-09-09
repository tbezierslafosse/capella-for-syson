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

package org.eclipse.capella.model.transverse.services;

import java.util.List;
import java.util.Optional;

import org.eclipse.syson.sysml.ActionUsage;
import org.eclipse.syson.sysml.Element;
import org.eclipse.syson.sysml.Package;
import org.eclipse.syson.sysml.PartUsage;
import org.eclipse.syson.sysml.metamodel.helper.EMFUtils;

/**
 * Checks that Capella elements are compatible with a given semantic context.
 * <p>
 * This checker validates owners, relationship ends, and content before an operation mutates the semantic model.
 *
 * @author gdaniel
 */
public class CapellaElementCompatibilityChecker {

    private final CommonQueryService commonQueryService;

    public CapellaElementCompatibilityChecker() {
        this.commonQueryService = new CommonQueryService();
    }

    public boolean isValidActorOwner(Element owner) {
        return this.commonQueryService.isComponent(owner) || this.commonQueryService.getStructurePackage(owner).isPresent();
    }

    public boolean isValidComponentOwner(Element owner) {
        return this.commonQueryService.isComponent(owner) || this.commonQueryService.getStructurePackage(owner).isPresent();
    }

    public boolean areValidComponentExchangeEnds(Element source, Element target) {
        boolean result = false;
        // TODO PR#103 changes this feature
        Optional<Package> optionalSourceStructurePackage = this.commonQueryService.getStructurePackage(source);
        Optional<Package> optionalTargetStructurePackage = this.commonQueryService.getStructurePackage(target);
        if (optionalSourceStructurePackage.isPresent() && optionalSourceStructurePackage.equals(optionalTargetStructurePackage)) {
            Optional<PartUsage> sourceComponent = EMFUtils.getFirstAncestor(PartUsage.class, source, this.commonQueryService::isComponent);
            Optional<PartUsage> targetComponent = EMFUtils.getFirstAncestor(PartUsage.class, target, this.commonQueryService::isComponent);
            result = sourceComponent.isPresent() && targetComponent.isPresent() && !sourceComponent.equals(targetComponent);
        }
        return result;
    }

    public boolean isValidComponentPortOwner(Element owner) {
        return this.commonQueryService.isComponent(owner);
    }

    public boolean areValidDescribesEnds(Element source, Element target) {
        return this.commonQueryService.isRequirement(source);
    }

    public boolean isValidFunctionOwner(Element owner) {
        return this.commonQueryService.isFunction(owner) || this.commonQueryService.getFunctionsPackage(owner).flatMap(this.commonQueryService::getRootFunction).isPresent();
    }

    public boolean isValidFunctionalChain(Element owner, List<Object> content) {
        // A functional chain is created in the common ancestor of the functional exchanges involved in it, so it cannot be created if there is no content.
        return !content.isEmpty();
    }

    public boolean areValidFunctionalExchangeEnds(Element source, Element target) {
        boolean result = false;
        // TODO PR#103 changes this feature
        Optional<Package> optionalSourceFunctionsPackage = this.commonQueryService.getFunctionsPackage(source);
        Optional<Package> optionalTargetFunctionsPackage = this.commonQueryService.getFunctionsPackage(target);
        if (optionalSourceFunctionsPackage.isPresent() && optionalSourceFunctionsPackage.equals(optionalTargetFunctionsPackage)) {
            Optional<ActionUsage> sourceFunction = EMFUtils.getFirstAncestor(ActionUsage.class, source, this.commonQueryService::isFunction);
            Optional<ActionUsage> targetFunction = EMFUtils.getFirstAncestor(ActionUsage.class, target, this.commonQueryService::isFunction);
            result = sourceFunction.isPresent() && targetFunction.isPresent() && !sourceFunction.equals(targetFunction);
        }
        return result;
    }

    public boolean isValidFunctionPortOwner(Element owner) {
        return this.commonQueryService.isFunction(owner);
    }

    public boolean isValidOperationalCapabilityOwner(Element owner) {
        return this.commonQueryService.getCapabilitiesPackage(owner).isPresent();
    }

    public boolean isValidRequirementOwner(Element owner) {
        return this.commonQueryService.getRequirementsPackage(owner).isPresent();
    }
}
