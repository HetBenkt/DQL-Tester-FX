package nl.bos;

import javafx.application.Application;

public class Main {

	public static void main(String[] args) {
        System.setProperty("javafx.preloader", SplashScreenLoader.class.getCanonicalName());
        Application.launch(MyApplication.class, args);

        //The old way
        //LauncherImpl.launchApplication(MyApplication.class, SplashScreenLoader.class, args);
        //add this JVM parameter: --add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED
	}
}
