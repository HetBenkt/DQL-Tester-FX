package nl.bos.controllers;

import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.common.DfException;
import javafx.embed.swing.JFXPanel;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryBrowserTest {

    @Mock
    private IDfPersistentObject object;

    @BeforeClass
    public static void initToolkit()
            throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(() -> {
            new JFXPanel();
            latch.countDown();
        });

        if (!latch.await(5L, TimeUnit.SECONDS))
            throw new ExceptionInInitializerError();
    }

    @Test
    public void getRepeatingValue() throws DfException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        when(object.getValueCount(any())).thenReturn(3);
        when(object.getRepeatingString(any(), anyInt())).thenReturn("dummy");

        RepositoryBrowser repositoryBrowser = new RepositoryBrowser();
        Class<? extends RepositoryBrowser> repositoryBrowserClass = repositoryBrowser.getClass();
        Method getRepeatingValue = repositoryBrowserClass.getDeclaredMethod("getRepeatingValue", IDfPersistentObject.class, String.class);
        getRepeatingValue.setAccessible(true);
        String value = (String) getRepeatingValue.invoke(repositoryBrowser, object, "r_version_label");
        assertEquals("dummy, dummy, dummy", value);
    }
}