package nl.sijpesteijn.testing.fitnesse.plugins;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import nl.sijpesteijn.testing.fitnesse.plugins.managers.PluginManager;
import nl.sijpesteijn.testing.fitnesse.plugins.managers.PluginManagerFactory;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.ReporterPluginConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.ReporterPluginConfig.Builder;

import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

/**
 * This mojo will collect the test results from the run tests.
 * 
 * @phase site
 * @execute goal="test"
 * @goal report
 */
public class FitNesseReportMojo extends AbstractMavenReport {
    public static final String OUTPUT_NAME = "fitnesse";

    /**
     * FitNesse output directory.
     * 
     * @parameter expression="${project.reporting.outputDirectory}"
     * @required
     * @readonly
     */
    private File outputDirectory;

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @component
     * @required
     * @readonly
     */
    private Renderer siteRenderer;

    /**
     * The directory where the FitNesse report were generated by the runner mojo
     * 
     * @parameter expression="${report.mafiaTestResultsDirectory}"
     *            default-value=
     *            "${basedir}/FitNesseRoot/files/mafiaTestResults"
     * @required
     */
    private String mafiaTestResultsDirectory;

    /**
     * List of test to be run.
     * 
     * @parameter expression="${report.tests}"
     */
    private String[] tests;

    /**
     * List of suites to be run.
     * 
     * @parameter expression="${report.suites}"
     */
    private String[] suites;

    /**
     * Name of the suite page name.
     * 
     * @parameter expression="${report.suitePageName}"
     */
    private String suitePageName;

    /**
     * Suite filter to run in the specified suite (=suitePageName).
     * 
     * @parameter expression="${report.suiteFilter}"
     */
    private String suiteFilter;

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    protected void executeReport(final Locale locale) throws MavenReportException {
        try {
            final ReporterPluginConfig reporterPluginConfig = getPluginConfig(locale);
            getLog().info("Report config: " + reporterPluginConfig.toString());
            final PluginManager pluginManager = PluginManagerFactory.getPluginManager(reporterPluginConfig);
            pluginManager.run();
        } catch (final MojoExecutionException e) {
            throw new MavenReportException("" + e);
        } catch (final MojoFailureException e) {
            throw new MavenReportException("" + e);
        }
    }

    /**
     * Collect the plugin configuration settings
     * 
     * @param locale
     * 
     * @return {@link nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.ReporterPluginConfig}
     * @throws MojoExecutionException
     */
    private ReporterPluginConfig getPluginConfig(final Locale locale) throws MojoExecutionException {
        final Builder builder = PluginManagerFactory.getPluginConfigBuilder(ReporterPluginConfig.class);
        builder.setMafiaTestResultsDirectory(this.mafiaTestResultsDirectory);
        builder.setOutputDirectory(this.outputDirectory.getAbsolutePath());
        builder.setName(OUTPUT_NAME);
        builder.setTests(createList(tests));
        builder.setSuites(createList(suites));
        builder.setSuiteFilter(suiteFilter);
        builder.setSuitePageName(suitePageName);
        builder.setSink(this.getSink());
        builder.setResourceBundle(getBundle(locale));
        return builder.build();
    }

    private List<String> createList(final String[] array) {
        final List<String> list = new ArrayList<String>();
        if (array != null) {
            for (final String element : array) {
                list.add(element);
            }
        }
        return list;
    }

    private ResourceBundle getBundle(final Locale locale) {
        return ResourceBundle.getBundle("mafia-report", locale, FitNesseReportMojo.class.getClassLoader());
    }

    /**
     * @return Get the report output directory. Passed by Maven.
     */
    @Override
    protected String getOutputDirectory() {
        return outputDirectory.getAbsolutePath();
    }

    /**
     * @return Get the mavenproject. Passed by Maven
     */
    @Override
    public MavenProject getProject() {
        return this.project;
    }

    /**
     * @return Get the site renderer. Passed by Maven.
     */
    @Override
    protected Renderer getSiteRenderer() {
        return this.siteRenderer;
    }

    /**
     * Get the description for this report mojo.
     */
    @Override
    public String getDescription(final Locale arg0) {
        return "Maven mafia plugin - reporting: Generate a report of the fitnessetests that have run";
    }

    /**
     * Get the name for this report mojo.
     */
    @Override
    public String getName(final Locale arg0) {
        return "Mafia Report";
    }

    /**
     * Get the output name for this report mojo.
     * 
     * @return the output name of this report.
     */
    @Override
    public String getOutputName() {
        return OUTPUT_NAME;
    }

    @Override
    public boolean isExternalReport() {
        return false;
    }
}