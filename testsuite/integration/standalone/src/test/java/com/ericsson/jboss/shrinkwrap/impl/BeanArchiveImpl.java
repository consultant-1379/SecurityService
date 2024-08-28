/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.jboss.shrinkwrap.impl;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.impl.base.spec.JavaArchiveImpl;

import com.ericsson.jboss.shrinkwrap.api.BeanArchive;

public class BeanArchiveImpl extends JavaArchiveImpl implements BeanArchive {

    /**
     * Beans XML object
     */
    private final BeansXml descriptor;

    public BeanArchiveImpl(final Archive<?> delegate) {
        super(delegate);

        // add beans.xml descriptor
        descriptor = new BeansXml();
        addAsManifestResource(descriptor, ArchivePaths.create("beans.xml"));
    }

    //-------------------------------------------------------------------------------------||
    // Required Implementations -----------------------------------------------------------||
    //-------------------------------------------------------------------------------------||


    //-------------------------------------------------------------------------------------||
    // Required Implementations - BeanArchive ---------------------------------------------||
    //-------------------------------------------------------------------------------------||

    @Override
    public BeanArchive decorate(final Class<?>... classes) {
        descriptor.decorators(classes);
        addClasses(classes);
        return covarientReturn();
    }

    @Override
    public BeanArchive intercept(final Class<?>... classes) {
        descriptor.interceptors(classes);
        addClasses(classes);
        return covarientReturn();
    }

    @Override
    public BeanArchive alternate(final Class<?>... classes) {
        descriptor.alternatives(classes);
        addClasses(classes);
        return covarientReturn();
    }

    @Override
    public BeanArchive stereotype(final Class<?>... classes) {
        descriptor.stereotype(classes);
        addClasses(classes);
        return covarientReturn();
    }

    @Override
    protected BeanArchive covarientReturn() {
        return (BeanArchive) super.covarientReturn();
    }
}