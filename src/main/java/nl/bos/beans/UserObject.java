package nl.bos.beans;

import java.util.ArrayList;
import java.util.List;

public class UserObject {
	public boolean isGlobally_managed() {
		return globally_managed;
	}

	public void setGlobally_managed(boolean globally_managed) {
		this.globally_managed = globally_managed;
	}

	public boolean isDocbase_owner() {
		return docbase_owner;
	}

	public void setDocbase_owner(boolean docbase_owner) {
		this.docbase_owner = docbase_owner;
	}

	public String getR_object_id() {
		return r_object_id;
	}

	public void setR_object_id(String r_object_id) {
		this.r_object_id = r_object_id;
	}

	public String getR_modify_date() {
		return r_modify_date;
	}

	public void setR_modify_date(String r_modify_date) {
		this.r_modify_date = r_modify_date;
	}

	public int getUser_state() {
		return user_state;
	}

	public void setUser_state(int user_state) {
		this.user_state = user_state;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getUser_os_name() {
		return user_os_name;
	}

	public void setUser_os_name(String user_os_name) {
		this.user_os_name = user_os_name;
	}

	public String getUser_os_domain() {
		return user_os_domain;
	}

	public void setUser_os_domain(String user_os_domain) {
		this.user_os_domain = user_os_domain;
	}

	public String getUser_source() {
		return user_source;
	}

	public void setUser_source(String user_source) {
		this.user_source = user_source;
	}

	public String getUser_address() {
		return user_address;
	}

	public void setUser_address(String user_address) {
		this.user_address = user_address;
	}

	public String getUser_db_name() {
		return user_db_name;
	}

	public void setUser_db_name(String user_db_name) {
		this.user_db_name = user_db_name;
	}

	public int getUser_privileges() {
		return user_privileges;
	}

	public void setUser_privileges(int user_privileges) {
		this.user_privileges = user_privileges;
	}

	public String getUser_group_name() {
		return user_group_name;
	}

	public void setUser_group_name(String user_group_name) {
		this.user_group_name = user_group_name;
	}

	public String getDefault_folder() {
		return default_folder;
	}

	public void setDefault_folder(String default_folder) {
		this.default_folder = default_folder;
	}

	public String getAcl_name() {
		return acl_name;
	}

	public void setAcl_name(String acl_name) {
		this.acl_name = acl_name;
	}

	public String getAcl_domain() {
		return acl_domain;
	}

	public void setAcl_domain(String acl_domain) {
		this.acl_domain = acl_domain;
	}

	public String getHome_docbase() {
		return home_docbase;
	}

	public void setHome_docbase(String home_docbase) {
		this.home_docbase = home_docbase;
	}

	public int getClient_capability() {
		return client_capability;
	}

	public void setClient_capability(int client_capability) {
		this.client_capability = client_capability;
	}

	public String getAlias_set_id() {
		return alias_set_id;
	}

	public void setAlias_set_id(String alias_set_id) {
		this.alias_set_id = alias_set_id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isWorkflow_disabled() {
		return workflow_disabled;
	}

	public void setWorkflow_disabled(boolean workflow_disabled) {
		this.workflow_disabled = workflow_disabled;
	}

	public String getUser_delegation() {
		return user_delegation;
	}

	public void setUser_delegation(String user_delegation) {
		this.user_delegation = user_delegation;
	}

	public String getUser_ldap_dn() {
		return user_ldap_dn;
	}

	public void setUser_ldap_dn(String user_ldap_dn) {
		this.user_ldap_dn = user_ldap_dn;
	}

	public int getUser_xprivileges() {
		return user_xprivileges;
	}

	public void setUser_xprivileges(int user_xprivileges) {
		this.user_xprivileges = user_xprivileges;
	}

	public int getFailed_auth_attempt() {
		return failed_auth_attempt;
	}

	public void setFailed_auth_attempt(int failed_auth_attempt) {
		this.failed_auth_attempt = failed_auth_attempt;
	}

	public boolean isHas_events() {
		return has_events;
	}

	public void setHas_events(boolean has_events) {
		this.has_events = has_events;
	}

	public int getOwner_permit() {
		return owner_permit;
	}

	public void setOwner_permit(int owner_permit) {
		this.owner_permit = owner_permit;
	}

	public int getGroup_permit() {
		return group_permit;
	}

	public void setGroup_permit(int group_permit) {
		this.group_permit = group_permit;
	}

	public int getWorld_permit() {
		return world_permit;
	}

	public void setWorld_permit(int world_permit) {
		this.world_permit = world_permit;
	}

	public String getUser_admin() {
		return user_admin;
	}

	public void setUser_admin(String user_admin) {
		this.user_admin = user_admin;
	}

	public String getUser_global_unique_id() {
		return user_global_unique_id;
	}

	public void setUser_global_unique_id(String user_global_unique_id) {
		this.user_global_unique_id = user_global_unique_id;
	}

	public String getUser_login_name() {
		return user_login_name;
	}

	public void setUser_login_name(String user_login_name) {
		this.user_login_name = user_login_name;
	}

	public String getUser_login_domain() {
		return user_login_domain;
	}

	public void setUser_login_domain(String user_login_domain) {
		this.user_login_domain = user_login_domain;
	}

	public String getUser_initials() {
		return user_initials;
	}

	public void setUser_initials(String user_initials) {
		this.user_initials = user_initials;
	}

	public String getUser_password() {
		return user_password;
	}

	public void setUser_password(String user_password) {
		this.user_password = user_password;
	}

	public String getUser_web_page() {
		return user_web_page;
	}

	public void setUser_web_page(String user_web_page) {
		this.user_web_page = user_web_page;
	}

	public String getFirst_failed_auth_utc_time() {
		return first_failed_auth_utc_time;
	}

	public void setFirst_failed_auth_utc_time(String first_failed_auth_utc_time) {
		this.first_failed_auth_utc_time = first_failed_auth_utc_time;
	}

	public String getLast_login_utc_time() {
		return last_login_utc_time;
	}

	public void setLast_login_utc_time(String last_login_utc_time) {
		this.last_login_utc_time = last_login_utc_time;
	}

	public String getDeactivated_utc_time() {
		return deactivated_utc_time;
	}

	public void setDeactivated_utc_time(String deactivated_utc_time) {
		this.deactivated_utc_time = deactivated_utc_time;
	}

	public String getDeactivated_ip_addr() {
		return deactivated_ip_addr;
	}

	public void setDeactivated_ip_addr(String deactivated_ip_addr) {
		this.deactivated_ip_addr = deactivated_ip_addr;
	}

	public List<String> getRestricted_folder_ids() {
		return restricted_folder_ids;
	}

	public void setRestricted_folder_ids(List<String> restricted_folder_ids) {
		this.restricted_folder_ids = restricted_folder_ids;
	}

	private boolean globally_managed;
	private boolean docbase_owner;
	private String r_object_id;
	private String r_modify_date;
	private int user_state;
	private String user_name;
	private String user_os_name;
	private String user_os_domain;
	private String user_source;
	private String user_address;
	private String user_db_name;
	private int user_privileges;
	private String user_group_name;
	private String default_folder;
	private String acl_name;
	private String acl_domain;
	private String home_docbase;
	private int client_capability;
	private String alias_set_id;
	private String description;
	private boolean workflow_disabled;
	private String user_delegation;
	private String user_ldap_dn;
	private int user_xprivileges;
	private int failed_auth_attempt;
	private boolean has_events;

	private int owner_permit;
	private int group_permit;
	private int world_permit;
	private String user_admin;
	private String user_global_unique_id;
	private String user_login_name;
	private String user_login_domain;
	private String user_initials;
	private String user_password;
	private String user_web_page;
	private String first_failed_auth_utc_time;
	private String last_login_utc_time;
	private String deactivated_utc_time;
	private String deactivated_ip_addr;
	private List<String> restricted_folder_ids = new ArrayList<>();
}
