package nl.bos;

public final class Constants {
    //Types
    public static final String TYPE_REPOSITORY = "repository";
    public static final String ATTR_R_VERSION_LABEL = "r_version_label";
    public static final String TYPE_CABINET = "dm_cabinet";
    public static final String TYPE_DOCUMENT = "dm_document";

    //Attributes
    public static final String ATTR_OBJECT_NAME = "object_name";
    public static final String ATTR_SUBJECT = "subject";
    public static final String ATTR_TITLE = "title";
    public static final String ATTR_R_CONTENT_SIZE = "r_content_size";

    public static final String ATTR_R_OBJECT_ID = "r_object_id";
    public static final String ATTR_R_HOST_NAME = "r_host_name";
    public static final String ATTR_R_LOCK_OWNER = "r_lock_owner";
    public static final String ATTR_A_CONTENT_TYPE = "a_content_type";
    public static final String ATTR_R_CREATION_DATE = "r_creation_date";
    public static final String ATTR_R_MODIFY_DATE = "r_modify_date";
    public static final String ATTR_R_LOCK_MACHINE = "r_lock_machine";
    public static final String ATTR_R_LOCK_DATE = "r_lock_date";
    public static final String ATTR_ACL_NAME = "acl_name";
    public static final String ATTR_OWNER_PERMIT = "owner_permit";
    static final String TYPE_FOLDER = "dm_folder";

    public static final String ATTR_IS_INACTIVE = "is_inactive";
    public static final String ATTR_IS_PRIVATE = "is_private";

    public static final String ATTR_A_CURRENT_STATUS = "a_current_status";
    public static final String ATTR_A_LAST_COMPLETION = "a_last_completion";
    public static final String ATTR_A_NEXT_INVOCATION = "a_next_invocation";
    public static final String ATTR_A_ITERATIONS = "a_iterations";
    public static final String ATTR_A_LAST_RETURN_CODE = "a_last_return_code";
    public static final String ATTR_A_IS_CONTINUED = "a_is_continued";
    public static final String ATTR_A_CONTINUATION_INTERVAL = "a_continuation_interval";

    public static final String ATTR_METHOD_TRACE_LEVEL = "method_trace_level";
    public static final String ATTR_INACTIVATE_AFTER_FAILURE = "inactivate_after_failure";
    public static final String ATTR_TARGET_SERVER = "target_server";
    public static final String ATTR_START_DATE = "start_date";
    public static final String ATTR_RUN_MODE = "run_mode";
    public static final String ATTR_RUN_NOW = "run_now";
    public static final String ATTR_RUN_INTERVAL = "run_interval";
    public static final String ATTR_PASS_STANDARD_ARGUMENTS = "pass_standard_arguments";
    public static final String ATTR_METHOD_NAME = "method_name";
    public static final String ATTR_METHOD_ARGUMENTS = "method_arguments";
    public static final String ATTR_MAX_ITERATIONS = "max_iterations";
    public static final String ATTR_EXPIRATION_DATE = "expiration_date";

    //Others
    static final String PATH_FORMAT = "%s/%s";

    public static final String TYPE = "TYPE";
    public static final String TABLE = "TABLE";

    public static final String HISTORY_JSON = "history.json";
    public static final String QUERIES = "queries";
    static final String NR_OF_TABLES = "nr_of_tables";
    static final String NR_OF_TYPES = "nr_of_types";

    private Constants() {
    }
}
