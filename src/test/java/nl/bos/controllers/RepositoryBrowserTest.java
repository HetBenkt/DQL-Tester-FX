package nl.bos.controllers;

import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.common.DfException;
import com.sun.javafx.application.PlatformImpl;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;

@RunWith(PowerMockRunner.class)
public class RepositoryBrowserTest {

    @Mock
    private IDfPersistentObject object;
    @InjectMocks
    private RepositoryBrowser repositoryBrowser;

    @BeforeClass
    public static void initToolkit() {
        PlatformImpl.startup(() -> {
        });
    }

    @Test
    public void getRepeatingValue() throws DfException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        PowerMockito.when(object.getValueCount(any())).thenReturn(3);
        PowerMockito.when(object.getRepeatingString(any(), anyInt())).thenReturn("dummy");

        Class<? extends RepositoryBrowser> repositoryBrowserClass = repositoryBrowser.getClass();
        Method getRepeatingValue = repositoryBrowserClass.getDeclaredMethod("getRepeatingValue", IDfPersistentObject.class, String.class);
        getRepeatingValue.setAccessible(true);
        String value = (String) getRepeatingValue.invoke(repositoryBrowser, object, "r_version_label");
        assertEquals("dummy, dummy, dummy", value);
    }
}