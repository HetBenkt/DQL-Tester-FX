package nl.bos;

import javafx.application.Application;

public class Main {

	public static void main(String[] args) {
        System.setProperty("javafx.preloader", SplashScreenLoader.class.getCanonicalName());
        Application.launch(MyApplication.class, args);
	}
}
