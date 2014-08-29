package de.tlongo.unnecessarywizard.java.test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import com.google.common.base.Predicates;
import de.tlongo.unneccesarywizard.java.core.Wizard;
import de.tlongo.unnecessarywizard.java.test.objects.Ciao;
import de.tlongo.unnecessarywizard.java.test.objects.Hello;
import de.tlongo.unnecessarywizard.java.test.objects.TestInterface;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import static org.reflections.ReflectionUtils.*;

import java.net.URL;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by tolo on 16.05.2014.
 */
public class LearntestReflections {

    private static Reflections reflections;

    @BeforeClass
    public static void setup() {
//        reflections = new Reflections(new ConfigurationBuilder().setUrls(java.util.Arrays.asList(ClasspathHelper.forClass(LearntestReflections.class))).
//                              setScanners(new ResourcesScanner(), new SubTypesScanner()));
        reflections = new Reflections(new ConfigurationBuilder().
                                        addUrls(ClasspathHelper.forClass(LearntestReflections.class)).
                                        setScanners(new ResourcesScanner(), new SubTypesScanner()));

        //reflections = new Reflections("de.tlongo.unnecessarywizard.java.test", new ResourcesScanner(), new SubTypesScanner());

    }

    @Test
    public void testClasspathStuff() throws Exception {
        System.out.printf("URL for Wizard.class %s\n", ClasspathHelper.forClass(Wizard.class).getPath());
        System.out.printf("URL for LearntestReflections.class %s\n", ClasspathHelper.forClass(LearntestReflections.class).getPath());
        System.out.printf("URL for package'de.tlongo.unnecessarywizard.java.test': %s\n", ClasspathHelper.forPackage("de.tlongo.unnecessarywizard.java.test"));
        System.out.printf("URL for package'': %s\n", ClasspathHelper.forPackage(""));
        System.out.printf("URL for resource 'wizard.groovy': %s\n", ClasspathHelper.forResource("wizard.groovy", LearntestReflections.class.getClassLoader()));
    }

    @Test
    public void testInterfaceQuery() {
        Set<Class<? extends TestInterface>> objects = reflections.getSubTypesOf(TestInterface.class);

        assertThat(objects, notNullValue());
        assertThat(objects, hasSize(2));
        assertThat(objects, hasItems(equalTo(Ciao.class), equalTo(Hello.class)));
    }

    @Test
    public void testSpecificTypeQuery() throws Exception {
        Set<Class<?>> result = getAllSuperTypes(Hello.class, Predicates.equalTo(Hello.class));

        assertThat(result, notNullValue());
        assertThat(result, hasSize(1));
        assertThat(result, hasItem(Hello.class));
    }

    @Test
    public void testSpecificTypeQueryByName() throws Exception {
        Class klass = forName("de.tlongo.unnecessarywizard.java.test.objects.Hello", reflections.getConfiguration().getClassLoaders());

        assertThat(klass, notNullValue());
        assertThat(klass, equalTo(Hello.class));
    }

    @Test
    public void testFetchResources() throws Exception {
        // Get all resources
        Set<String> resources = reflections.getResources(Pattern.compile(".*\\.groovy"));

        assertThat(resources, notNullValue());
        assertThat(resources, hasSize(greaterThan(0)));

        System.out.printf("Found %d resources:\n", resources.size());
        resources.forEach(item -> System.out.println(item));

        // Get the default wizard config
        resources = reflections.getResources(Pattern.compile("wizard\\.groovy"));
        assertThat(resources, notNullValue());
        assertThat(resources, hasSize(1));
        assertThat(resources.contains("resources/wizard.groovy"), is(true));

    }
}