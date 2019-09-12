package nl.bos;

import com.documentum.fc.client.IDfDocbaseMap;
import javafx.concurrent.Task;

import java.text.MessageFormat;
import java.util.logging.Logger;

public class InitRepositoryList extends Task<Boolean> {
    private static final Logger LOGGER = Logger.getLogger(InitRepositoryList.class.getName());

    @Override
    protected Boolean call() throws Exception {
        Repository repository = Repository.getInstance();
        IDfDocbaseMap repositoryMap = repository.obtainRepositoryMap();
        for (int i = 0; i < repositoryMap.getDocbaseCount(); i++) {
            LOGGER.info(MessageFormat.format("Repository {0}: {1}", i + 1, repositoryMap.getDocbaseName(i)));
        }
        return true;
    }
}
