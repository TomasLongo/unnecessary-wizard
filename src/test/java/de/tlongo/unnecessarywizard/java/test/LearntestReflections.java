package de.tlongo.unnecessarywizard.java.test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import com.google.common.base.Predicates;
import de.tlongo.unnecessarywizard.java.test.objects.Ciao;
import de.tlongo.unnecessarywizard.java.test.objects.Hello;
import de.tlongo.unnecessarywizard.java.test.objects.TestInterface;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reflections.Reflections;
import static org.reflections.ReflectionUtils.*;

import java.util.Set;

/**
 * Created by tolo on 16.05.2014.
 */
public class LearntestReflections {

    private static Reflections reflections;

    @BeforeClass
    public static void setup() {
        reflections = new Reflections("de.tlongo.unnecessarywizard.java.test.objects");
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
}
