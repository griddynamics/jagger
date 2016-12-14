package com.griddynamics.jagger.engine.e1.collector.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listener, executed before and after a test and periodically during a test
 * @author Gribov Kirill
 * @n
 * @par Details:
 * @details Possible applications for test listener: @n
 * @li Collect some parameters during test run and save as metrics
 * @li Get some internal metrics from SUT after test is over and store this data as metrics to Jagger DB
 *
 * @n
 * To view test listener implementations click here @ref Main_Listeners_group
 * @n
 * @ingroup Main_Listeners_Base_group */
public abstract class TestListener {

    /** Method is executed before test starts
     * @param testInfo - describes start test information*/
    public void onStart(TestInfo testInfo){
    }

    /** Executes after test stops
     * @param testInfo - describes stop test information */
    public void onStop(TestInfo testInfo){
    }

    /** This method is periodically called while test is running. It shows current Jagger execution status(number of Jagger threads, etc)
     * @param status - contains info about current number of threads, samples and etc.*/
    public void onRun(TestInfo status){
    }

    /** Class is used by Jagger for sequential execution of several listeners @n
     *  Not required for custom test listeners */
    public static class Composer extends TestListener {
        private static Logger log = LoggerFactory.getLogger(Composer.class);

        private Iterable<TestListener> listeners;

        public Composer(Iterable<TestListener> listeners) {
            this.listeners = listeners;
        }

        public static TestListener compose(Iterable<TestListener> collectors){
            return new Composer(collectors);
        }

        @Override
        public void onStart(TestInfo testInfo) {
            for (TestListener listener : listeners){
                try{
                    listener.onStart(testInfo);
                }catch (RuntimeException ex){
                    log.error("Failed to call on start in {} test-listener", listener.toString(), ex);
                }
            }
        }

        @Override
        public void onStop(TestInfo testInfo) {
            for (TestListener listener : listeners){
                try{
                    listener.onStop(testInfo);
                }catch (RuntimeException ex){
                    log.error("Failed to call on stop in {} test-listener", listener.toString(), ex);
                }
            }
        }

        @Override
        public void onRun(TestInfo testInfo) {
            for (TestListener listener : listeners){
                try{
                    listener.onRun(testInfo);
                }catch (RuntimeException ex){
                    log.error("Failed to call on run in {} test-listener", listener.toString(), ex);
                }
            }
        }
    }
}

/* **************** Listeners page ************************* */
/// @defgroup Main_Listeners_General_group Listeners main page
///
/// @li General information about interfaces: @ref Main_Listeners_Base_group
/// @li Available implementations: @ref Main_Listeners_group
/// @li Usage of Jagger services in listeners: @ref Main_ListenersAndServices_group
/// @li How to customize: @ref Main_HowToCustomizeListeners_group
/// @n
/// @n
/// @details
/// @par General info
/// Listeners give possibility to provide some user actions during test flow defined by XML configuration @n
/// User can implement custom listener and Jagger will trigger it on some events (f.e. test suit start/stop, test group start/stop, test start/run/stop, invocation start/stop) @n
/// You can find example of custom listeners under link: @ref Main_CustomListenersExamples_group
///

/* **************** Relation of listeners and Jagger services ************************* */
/// @defgroup Main_ListenersAndServices_group Relation of listeners and Jagger services
///
/// @details Jagger is providing set of services to simplify custom code development. @n
/// Services allow to access Jagger internal components with small effort. What services exist you can find under link: @ref Main_Services_group @n
/// All custom listeners have access to Jagger services. But list of available services can be different for different types of listeners. @n
/// Please see table below to find what services can be used in what listeners. @n
/// @dontinclude  ServicesAware.java
/// @skip  begin: following section is used for docu generation - listeners to services relation
/// @until end: following section is used for docu generation - listeners to services relation
/// @n
/// If you will try to use unavailable service in some listener - nothing will happen and warning will be logged @n
/// Example of services usage in listeners are available in maven archetype-examples or here: @ref Main_CustomListenersExamples_group

/* **************** How to customize listeners ************************* */
/// @defgroup Main_HowToCustomizeListeners_group Custom listeners
///
/// @details
/// @ref Main_Listeners_General_group
/// @n
/// @n
/// You can implement different types of listeners (@ref Main_Listeners_Base_group) depending on your requirements. @n
/// Approach for implementation will be always the same like described below. @n
/// @n
/// To add custom listener (f.e. test suite listener) you need to do -
/// 1. Create class which extends @ref com.griddynamics.jagger.engine.e1.services.ServicesAware "ServicesAware" and implements interface @ref Provider<T> @n
/// Where @b T is listener type (all types: @ref Main_Listeners_Base_group)
/// @dontinclude  ProviderOfLoadScenarioListener.java
/// @skipline  public class ProviderOfLoadScenarioListener
/// @n
///
/// 2. Create bean in XML file with some id
/// @dontinclude  listeners.conf.xml
/// @skip  begin: following section is used for docu generation - listener usage
/// @until end: following section is used for docu generation - listener usage
/// @n
/// @b Important. For invocation listener bean scope is important. @n
/// When @e scope="prototype" is set separate object will be created for exevy test in your XML configuration @n
/// @dontinclude  listeners.conf.xml
/// @skip  begin: following section is used for docu generation - invocation listener usage
/// @until end: following section is used for docu generation - invocation listener usage
/// @n
///
/// 3. Add reference to your listener to appropriate block of your XML schema @n
/// in your configuration XML file and set id of listener to attribute @xlink_complex{listener-test-suite-ref,ref}.
/// @dontinclude  suite.conf.xml
/// @skip  begin: following section is used for docu generation - test suite listener usage
/// @until end: following section is used for docu generation - test suite listener usage
/// @n
/// @xlink_complex{listener-test-suite-ref} belongs to @xlink{test-suite} block in XML @n
/// @xlink_complex{listener-test-group-ref} belongs to @xlink{test-group} block in XML @n
/// @xlink_complex{listener-test-group-decision-maker-ref} belongs to @xlink{test-group} block in XML @n
/// @xlink_complex{listener-test-ref} belongs to @xlink{test} block in XML @n
/// @xlink_complex{listener-invocation-ref} belongs to @xlink{test-description,info-collectors} block in XML @n
/// @n
/// @b Note:
/// @li full examples of the code are available in maven archetype-examples or here: @ref Main_CustomListenersExamples_group
/// @li instead of ${package} write the name of your package

/* **************** Custom listeners examples ************************* */
/// @defgroup Main_CustomListenersExamples_group Custom listener examples code
///
/// @details
/// @ref Main_Listeners_General_group
///
/// @example_begin
/// @example_addmenu{0,Custom test listener}
/// @example_addmenu{1,Custom test suite listener}
/// @example_addmenu{2,Custom invocation listener}
/// @example_begin_content{0}
/// @dontinclude  ProviderOfTestListener.java
/// @skip  begin: following section is used for docu generation - custom test listener
/// @until end: following section is used for docu generation - custom test listener
/// @example_end_content
/// @example_begin_content{1}
/// @dontinclude  ProviderOfLoadScenarioListener.java
/// @skip  begin: following section is used for docu generation - custom test suite listener
/// @until end: following section is used for docu generation - custom test suite listener
/// @example_end_content
/// @example_begin_content{2}
/// @dontinclude  ProviderOfInvocationListener.java
/// @skip  begin: following section is used for docu generation - custom invocation listener
/// @until end: following section is used for docu generation - custom invocation listener
/// @example_end_content
/// @example_end
