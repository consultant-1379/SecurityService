/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package test.integration;

import java.io.File;

import org.jboss.arquillian.protocol.servlet.arq514hack.descriptors.api.application.ApplicationDescriptor;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.*;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.descriptor.api.DescriptorImporter;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.resolver.api.Resolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;

/**
 * Maven artifact constants and deployment methods.
 * 
 * @author xromsza
 */
public class Artifact {

    public static final String COM_ERICSSON_OSS_SECURITY_SERVICE___TEST_EAR = "SecurityService-test";

    public static final String COM_ERICSSON_OSS_SECURITY_SERVICE___EAR = "com.ericsson.nms.services:SecurityService-ear:ear:?";    

    private static final String BEANS_XML = "beans.xml";

    private static final String PACKAGE_ROOT = "test.integration";

    private static final String ORG_SLF4J___SLF4J_API_JAR = "org.slf4j:slf4j-api:jar:?";

    private static final String ORG_SLF4J___SLF4J_LOG4J12_JAR = "org.slf4j:slf4j-log4j12:jar:?";

    private static final String SFWK_DIST_JAR = "com.ericsson.oss.itpf.sdk:service-framework-dist:jar:?";

    private static final String SHRINKWRAP_RESOLVER_API_JAR = "org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api:jar:?";

    private static final String SHRINKWRAP_RESOLVER_API_MAVEN_JAR = "org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api-maven:jar:?";

    private static final String SHRINKWRAP_RESOLVER_MAVEN_JAR = "org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-impl-maven:jar:?";

    private static final String SHRINKWRAP_DESCRIPTORS_BASE_JAR = "org.jboss.shrinkwrap.descriptors:shrinkwrap-descriptors-impl-base:jar:?";

    private static final String SHRINKWRAP_DESCRIPTORS_JAVAEE_JAR = "org.jboss.shrinkwrap.descriptors:shrinkwrap-descriptors-impl-javaee:jar:?";

    // A single instance of enterprise archive containing both test and code files
    private static EnterpriseArchive ear = null;

    /**
     * Get a single instance of enterprise archive containing both test and code files.
     * 
     * @return The single instance of enterprise archive containing both test and code files
     */
    public static EnterpriseArchive getSecurityServiceTestArchive() {
        if (ear == null) {
            ear = createSecurityServiceTestArchive();
        }

        return ear;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static EnterpriseArchive createSecurityServiceTestArchive() {
        return modifyApplicationXML(
                COM_ERICSSON_OSS_SECURITY_SERVICE___TEST_EAR,
                ShrinkWrap
                        .createFromZipFile(EnterpriseArchive.class,
                                resolveArtifactWithoutDependencies(COM_ERICSSON_OSS_SECURITY_SERVICE___EAR))
                        .addAsModule(
                                ShrinkWrap.create(JavaArchive.class, COM_ERICSSON_OSS_SECURITY_SERVICE___TEST_EAR)
                                        .addPackages(true, PACKAGE_ROOT)
                                        /*
                                         * .merge(ShrinkWrap.create(GenericArchive.class).as(ExplodedImporter.class)
                                         * .importDirectory("src/test/resources").as(GenericArchive.class), "/",
                                         * Filters.includeAll())
                                         */
                                        .addAsManifestResource(EmptyAsset.INSTANCE, BEANS_XML))
                        .addAsLibraries(
                                ShrinkWrap.createFromZipFile(JavaArchive.class,
                                        resolveArtifactWithoutDependencies(SFWK_DIST_JAR)),
                                ShrinkWrap.createFromZipFile(JavaArchive.class,
                                        resolveArtifactWithoutDependencies(ORG_SLF4J___SLF4J_API_JAR)),
                                ShrinkWrap.createFromZipFile(JavaArchive.class,
                                        resolveArtifactWithoutDependencies(ORG_SLF4J___SLF4J_LOG4J12_JAR)),
                                ShrinkWrap.createFromZipFile(JavaArchive.class,
                                        resolveArtifactWithoutDependencies(SHRINKWRAP_RESOLVER_API_JAR)),
                                ShrinkWrap.createFromZipFile(JavaArchive.class,
                                        resolveArtifactWithoutDependencies(SHRINKWRAP_RESOLVER_API_MAVEN_JAR)),
                                ShrinkWrap.createFromZipFile(JavaArchive.class,
                                        resolveArtifactWithoutDependencies(SHRINKWRAP_RESOLVER_MAVEN_JAR)),
                                ShrinkWrap.createFromZipFile(JavaArchive.class,
                                        resolveArtifactWithoutDependencies(SHRINKWRAP_DESCRIPTORS_BASE_JAR)),
                                ShrinkWrap.createFromZipFile(JavaArchive.class,
                                        resolveArtifactWithoutDependencies(SHRINKWRAP_DESCRIPTORS_JAVAEE_JAR))));
    }

    private static EnterpriseArchive modifyApplicationXML(final String archiveName, final EnterpriseArchive ear) {
        final Node node = ear.get("META-INF/application.xml");

        final DescriptorImporter<ApplicationDescriptor> importer = Descriptors.importAs(ApplicationDescriptor.class,
                COM_ERICSSON_OSS_SECURITY_SERVICE___EAR);
        ApplicationDescriptor desc = importer.fromStream(node.getAsset().openStream());

        String xml = desc.exportAsString();
        xml = xml.replaceAll("<library-directory>.*<\\/library-directory>", "");

        desc = (ApplicationDescriptor) importer.fromString(xml);
        desc.ejbModule(archiveName);
        final Asset asset = new StringAsset(desc.exportAsString());

        ear.delete(node.getPath());
        ear.setApplicationXML(asset);
        return ear;
    }

    private static File resolveArtifactWithoutDependencies(final String artifactCoordinates) {
        final File artifact = getMavenResolver().resolve(artifactCoordinates).withoutTransitivity().asSingleFile();
        if (artifact == null) {
            throw new IllegalStateException("Artifact with coordinates " + artifactCoordinates + " was not resolved");
        }
        return artifact;
    }

    private static PomEquippedResolveStage getMavenResolver() {
        return Resolvers.use(MavenResolverSystem.class).loadPomFromFile("pom.xml");
    }

}