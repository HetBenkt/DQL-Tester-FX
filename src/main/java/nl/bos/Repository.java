package nl.bos;

import com.documentum.bpm.IDfWorkflowEx;
import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.*;
import com.documentum.fc.client.content.IDfContent;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.IDfLoginInfo;
import javafx.scene.control.TreeItem;
import nl.bos.Constants.Version;
import nl.bos.beans.AttachmentObject;
import nl.bos.beans.PackageObject;
import nl.bos.beans.WorkflowObject;
import nl.bos.beans.WorkflowVariable;
import nl.bos.utils.AppAlert;
import nl.bos.utils.Resources;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static nl.bos.Constants.MSG_TITLE_INFO_DIALOG;

public class Repository {
    private static final Logger LOGGER = Logger.getLogger(Repository.class.getName());

    private static Repository repository;
    private final IDfClientX clientX;
    private String errorMessage = "";
    private IDfSessionManager sessionManager;
    private IDfSession session;
    private String repositoryName;
    private String userName;
    private String passkey;
    private String domain;
    private String secureMode;
    private IDfClient client = null;

    private Repository() {
        clientX = new DfClientX();
    }

    public static synchronized Repository getInstance() {
        if (repository == null) {
            repository = new Repository();
        }
        return repository;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public IDfSession getSession() {
        return session;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public String getUserName() {
        return userName;
    }

    public IDfClient getClient() {
        return client;
    }

    public void disconnect() {
        if (repository != null && isConnected()) {
            sessionManager.release(session);
            sessionManager.clearIdentity(repositoryName);
        }
    }

    public IDfDocbaseMap obtainRepositoryMap() {
        IDfDocbaseMap repositoryMap = null;

        try {
            if (client == null) {
                client = clientX.getLocalClient();
            }

            repositoryMap = client.getDocbaseMap();

        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return repositoryMap;
    }

    void setCredentials(String repositoryName, String userName, String passkey, String domain) {
        setCredentials(repositoryName, userName, passkey, domain, "default");
    }

    public void setCredentials(String repositoryName, String userName, String passkey, String domain,
                               String secureMode) {
        this.repositoryName = repositoryName;
        this.userName = userName;
        this.passkey = passkey;
        this.domain = domain;
        switch (secureMode == null ? "" : secureMode) {
            case "native":
                this.secureMode = IDfLoginInfo.SECURITY_MODE_NATIVE;
                break;
            case "secure":
                this.secureMode = IDfLoginInfo.SECURITY_MODE_SECURE;
                break;
            case "try_native_first":
                this.secureMode = IDfLoginInfo.SECURITY_MODE_TRY_NATIVE_FIRST;
                break;
            case "try_secure_first":
                this.secureMode = IDfLoginInfo.SECURITY_MODE_TRY_SECURE_FIRST;
                break;
            case "default":
            default:
                this.secureMode = null;
        }
    }

    public IDfTypedObject obtainServerMap(String selectedRepository) {
        IDfTypedObject serverMap = null;

        try {
            if (client == null) {
                client = clientX.getLocalClient();
            }

            serverMap = client.getServerMap(selectedRepository);

        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return serverMap;
    }

    public void createSession() {
        try {
            session = sessionManager.getSession(repositoryName);
        } catch (DfServiceException e) {
            LOGGER.finest(e.getMessage());
            errorMessage = e.getMessage();
            sessionManager.clearIdentity(repositoryName);
        }
    }

    public void setClient() {
        try {
            client = clientX.getLocalClient();

        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void createSessionManager() {
        try {
            if (sessionManager == null) {
                client = clientX.getLocalClient();
                sessionManager = client.newSessionManager();
            }

            IDfLoginInfo loginInfoObj = clientX.getLoginInfo();
            loginInfoObj.setUser(userName);
            loginInfoObj.setPassword(passkey);
            loginInfoObj.setDomain(domain);

            if (secureMode != null) {
                loginInfoObj.setSecurityMode(secureMode);
            }

            sessionManager.setIdentity(repositoryName, loginInfoObj);

        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public IDfCollection query(String query) {
        IDfQuery q = clientX.getQuery();
        q.setDQL(query);

        IDfCollection collection = null;

        try {
            collection = q.execute(session, IDfQuery.DF_READ_QUERY);

        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            AppAlert.warning(MSG_TITLE_INFO_DIALOG, e.getMessage());
        }

        String message = MessageFormat.format("Query executed: {0}", query);
        LOGGER.finest(message);

        return collection;
    }

    public boolean isTypeName(String name) {
        return hasResults(String.format("SELECT 1 FROM dm_type WHERE name = '%s'", name));
    }

    public boolean isTableName(String name) {
        return hasResults(String.format("SELECT 1 FROM dm_registered WHERE object_name = '%s'", name));
    }

    private boolean hasResults(String dqlStatement) {
        IDfCollection collection = null;

        try {
            collection = query(dqlStatement);
            return collection.next();

        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

        } finally {
            try {
                assert collection != null;
                collection.close();

            } catch (DfException ignored) {
                // ignored
            }
        }

        return false;
    }

    public boolean isObjectId(String id) {
        return new DfId(id).isObjectId();
    }

    public boolean isConnected() {
        return session != null && session.isConnected();
    }

    public IDfPersistentObject getObjectById(String objectId) {
        try {
            return session.getObject(new DfId(objectId));
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    public String getIdFromObject(IDfPersistentObject object) {
        String result = "";
        try {
            result = object.getObjectId().getId();
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return result;
    }

    public List<String> getFilteredUserList(String userFilter) {
        String query = String.format(
                "SELECT user_name FROM dm_user WHERE r_is_group = false AND user_name LIKE '%s%%' ORDER BY 1 ASC",
                userFilter);
        return getSingleAttributeList(query, "user_name");
    }

    public List<String> getFilteredUserList(String userFilter, String groupFilter) {
        String query = String.format(
                "SELECT DISTINCT u.user_name FROM dm_user u, dm_group g WHERE u.r_is_group = false AND u.user_name LIKE '%s%%' AND ANY g.users_names = u.user_name ORDER BY 1 ASC",
                userFilter, groupFilter);
        return getSingleAttributeList(query, "user_name");
    }

    public IDfUser getUserByName(String userName) throws DfException {
        return session.getUser(userName);
    }

    public String getDocbaseOwner() {
        IDfCollection collection = null;

        try {
            collection = query("SELECT owner_name FROM dm_docbase_config");
            collection.next();
            return collection.getString("owner_name");

        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

        } finally {
            try {
                assert collection != null;
                collection.close();

            } catch (DfException ignored) {
            }
        }

        return null;
    }

    public List<String> getAliasSets() {
        return getSingleAttributeList("SELECT object_name FROM dm_alias_set ORDER BY 1 ASC", "object_name");
    }

    public List<String> getFilteredGroupList(String groupFilter) {
        String query = String.format("SELECT group_name FROM dm_group WHERE group_name LIKE '%s%%' ORDER BY 1 ASC",
                groupFilter);
        return getSingleAttributeList(query, "group_name");
    }

    private List<String> getSingleAttributeList(String query, String attributeName) {
        List<String> result = new ArrayList<>();

        IDfCollection collection = null;

        try {
            collection = query(query);

            while (collection.next()) {
                result.add(collection.getString(attributeName));
            }

        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

        } finally {
            try {
                assert collection != null;
                collection.close();

            } catch (DfException ignored) {
            }
        }

        return result;
    }

    public boolean isDocumentType(IDfPersistentObject object) {
        try {
            return object.isInstanceOf("dm_sysobject") && !object.isInstanceOf("dm_folder");
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return false;
    }

    public boolean canCheckOut(String objectId) {
        LOGGER.log(Level.FINE, "Test if user can checkout" + objectId);
        IDfPersistentObject object = repository.getObjectById(objectId);
        return canCheckOut(object);

    }

    public boolean canCheckOut(IDfPersistentObject object) {
        try {
            if (object.isInstanceOf("dm_sysobject") && !object.isInstanceOf("dm_folder")) {
                IDfSysObject sysObj = (IDfSysObject) object;
                if (IDfACL.DF_PERMIT_VERSION <= sysObj.getPermit() && !sysObj.isCheckedOut()) {
                    return true;
                }
            } else {
                return false;
            }
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return false;
    }

    public boolean isCheckedOut(String objectId) {
        IDfPersistentObject object = repository.getObjectById(objectId);
        LOGGER.log(Level.FINE, "Test if user can checkout" + objectId);
        return isCheckedOut(object);

    }

    public boolean isCheckedOut(IDfPersistentObject object) {
        try {

            if (object.isInstanceOf("dm_sysobject")) {
                IDfSysObject sysObj = (IDfSysObject) object;
                return sysObj.isCheckedOut();
            } else {
                return false;
            }
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return false;
    }

    public boolean checkin(String objectId, File content, Version version, boolean keepLock) {
        IDfPersistentObject object = repository.getObjectById(objectId);
        try {
            checkin(object, content, version, keepLock);
        } catch (DfException e) {
            return false;
        }
        return true;
    }

    private void checkin(IDfPersistentObject sysObject, File content, Version version, boolean keepLock)
            throws DfException {
        try {
            if (sysObject.isInstanceOf("dm_sysobject")) {
                IDfSysObject sysObj = (IDfSysObject) sysObject;
                if (sysObj.isCheckedOut()) {
                    sysObj.setFile(content.getAbsolutePath());
                    // should offer choice to user if they want to keep lock, which version number
                    switch (version) {
                        case SAMEVER:
                            sysObj.save();
                            if (!keepLock) {
                                sysObj.cancelCheckout();
                            }
                            break;
                        case MAJOR:
                            sysObj.checkin(keepLock, sysObj.getVersionPolicy().getNextMajorLabel() + ", CURRENT");
                            break;
                        default:
                            sysObj.checkin(keepLock, null);
                    }
                    //if the document stays checked out, we keep the checkout path
                    if (!keepLock) {
                        Resources.removeContentPathFromCheckoutFile(sysObject.getObjectId().getId());
                    }
                }
            }
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        }
    }

    public String getObjectName(String id) {
        try {
            IDfPersistentObject objectById = getObjectById(id);
            return objectById.getString("object_name");
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    public void cancelCheckout(String id) {
        try {
            LOGGER.info(id);

            IDfSysObject sysObject = (IDfSysObject) repository.getSession().getObject(new DfId(id));
            cancelCheckout(sysObject);
        } catch (DfException e) {
            AppAlert.error("Unable to cancel Checkout", id);
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void cancelCheckout(IDfSysObject sysObject) throws DfException {
        try {
            sysObject.cancelCheckout();
            Resources.removeContentPathFromCheckoutFile(sysObject.getObjectId().getId());
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        }
    }

    public void checkoutContent(String id) {
        try {
            LOGGER.info(id);

            IDfSysObject sysObject = (IDfSysObject) repository.getSession().getObject(new DfId(id));
            checkoutContent(sysObject);
        } catch (DfException e) {
            AppAlert.error("Error while trying to retrieve content", id);
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void checkoutContent(IDfSysObject sysObject) throws DfException {
        try {
            sysObject.checkout();
            String path = downloadContent(sysObject);
            Resources.putContentPathToCheckoutFile(sysObject.getObjectId().getId(), path);
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Get document primary content
     */
    public String downloadContent(String id) {
        LOGGER.info(id);
        IDfSysObject sysObject;
        try {
            sysObject = (IDfSysObject) repository.getSession().getObject(new DfId(id));
            return downloadContent(sysObject);
        } catch (DfException e) {
            AppAlert.error("Error while trying to retrieve content", id);
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    /**
     * Get document primary content
     */
    public String downloadContent(IDfSysObject sysObject) throws DfException {
        try {
            String objectName = sysObject.getObjectName();
            if (!(sysObject.getContentSize() > 0)) {
                AppAlert.warning("Content is empty", objectName);
            } else {
                final String extension = repository.getSession().getFormat(sysObject.getContentType())
                        .getDOSExtension();
                final String path = sysObject.getFile(new File(Resources.getExportPath(),
                        objectName.replaceAll("[^a-zA-Z0-9._]", "-") + "." + extension).getAbsolutePath());
                LOGGER.info("Downloaded document to path " + path);
                return path;
            }
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        }
        return null;
    }

    public String getParentId(String id) {
        try {
            IDfContent contentObject = (IDfContent) repository.getSession().getObject(new DfId(id));
            return contentObject.getParentId(0).getId();
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    public String downloadContent(String id, String format) {
        IDfSysObject sysObject = (IDfSysObject) repository.getObjectById(id);
        try {
            String objectName = sysObject.getObjectName();
            if (!(sysObject.getContentSize() > 0)) {
                AppAlert.warning("Content is empty", objectName);
            } else {
                final String extension = repository.getSession().getFormat(format)
                        .getDOSExtension();
                final String path = sysObject.getFileEx2(new File(Resources.getExportPath(),
                        objectName.replaceAll("[^a-zA-Z0-9._]", "-") + "." + extension).getAbsolutePath(), format, 0, "", false);
                LOGGER.info("Downloaded document to path " + path);
                return path;
            }
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    private List<WorkflowObject> getWorkflows(String query) {
        List<WorkflowObject> workflows = new ArrayList<>();
        IDfCollection collection = null;

        try {
            collection = repository.query(query);
            while (collection.next()) {
                WorkflowObject workflowObject = new WorkflowObject();
                workflowObject.setWorkflowId(collection.getString("workflow_id"));
                workflowObject.setWorkflowName(collection.getString("workflow_name"));
                workflowObject.setWorkitemId(collection.getString("workitem_id"));
                workflowObject.setProcessName(collection.getString("pname"));
                workflowObject.setActivityName(collection.getString("actname"));
                workflowObject.setActivitySeqNo(collection.getString("act_seqno"));
                workflowObject.setRuntimeState(collection.getString("rs"));
                workflowObject.setPerformerName(collection.getString("r_performer_name"));
                workflowObject.setSupervisorName(collection.getString("supervisor_name"));
                workflowObject.setEvent(collection.getString("event"));
                workflowObject.setWorkqueueName(collection.getString("a_wq_name"));
                workflowObject.setStartDate(collection.getString("r_start_date"));
                workflowObject.setCreationDae(collection.getString("r_creation_date"));
                workflowObject.setActivityId(collection.getString("actid"));
                workflowObject.setProcessId(collection.getString("pid"));
                workflowObject.setParentId(collection.getString("parent_id"));
                workflowObject.setQueueItemId(collection.getString("qid"));
                workflowObject.setExecOsError(collection.getString("r_exec_os_error"));
                workflows.add(workflowObject);
            }
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            if (collection != null) {
                try {
                    collection.close();
                } catch (DfException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }
        return workflows;
    }

    public List<WorkflowObject> getAllWorkflows(String supervisor, String object, boolean oneRowPerWflSeqNo) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("select distinct ");
        stringBuilder.append("wfl.r_object_id as workflow_id, wfl.object_name as workflow_name, wi.r_object_id as workitem_id, pr.object_name as pname, qi.task_name as actname, qi.task_number as act_seqno, wi.r_runtime_state as rs, wi.r_performer_name, wfl.supervisor_name, qi.event, wi.a_wq_name, wfl.r_start_date, wi.r_creation_date, act.r_object_id as actid, pr.r_object_id as pid, wfl.parent_id, qi.r_object_id as qid, wi.r_exec_os_error ");
        stringBuilder.append("from dm_workflow wfl, dm_process pr, dmi_workitem wi, dmi_queue_item qi, dm_activity act ");
        stringBuilder.append("where pr.r_object_id = wfl.process_id ");
        stringBuilder.append("and wi.r_workflow_id = wfl.r_object_id ");
        stringBuilder.append("and qi.router_id = wfl.r_object_id ");
        stringBuilder.append("and qi.item_id = wi.r_object_id ");
        stringBuilder.append("and act.r_object_id = wi.r_act_def_id ");
        if (oneRowPerWflSeqNo) {
            stringBuilder.append("and qi.delete_flag = FALSE ");
        }
        if (supervisor.length() > 0) {
            stringBuilder.append(String.format("and wfl.supervisor_name like '%s%s' ", supervisor, "%"));
        }
        if (object.length() > 0) {
            stringBuilder.append(String.format("and (wfl.r_object_id like '%s%s' or wfl.object_name like '%s%s') ", object, "%", object, "%"));
        }
        stringBuilder.append("order by wfl.r_start_date desc, qi.task_number desc");

        return getWorkflows(stringBuilder.toString());
    }

    public List<WorkflowObject> getTodayWorkflows(String supervisor, String object, boolean oneRowPerWflSeqNo) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("select distinct ");
        stringBuilder.append("wfl.r_object_id as workflow_id, wfl.object_name as workflow_name, wi.r_object_id as workitem_id, pr.object_name as pname, qi.task_name as actname, qi.task_number as act_seqno, wi.r_runtime_state as rs, wi.r_performer_name, wfl.supervisor_name, qi.event, wi.a_wq_name, wfl.r_start_date, wi.r_creation_date, act.r_object_id as actid, pr.r_object_id as pid, wfl.parent_id, qi.r_object_id as qid, wi.r_exec_os_error ");
        stringBuilder.append("from dm_workflow wfl, dm_process pr, dmi_workitem wi, dmi_queue_item qi, dm_activity act ");
        stringBuilder.append("where pr.r_object_id = wfl.process_id ");
        stringBuilder.append("and wi.r_workflow_id = wfl.r_object_id ");
        stringBuilder.append("and qi.router_id = wfl.r_object_id ");
        stringBuilder.append("and qi.item_id = wi.r_object_id ");
        stringBuilder.append("and act.r_object_id = wi.r_act_def_id ");
        if (oneRowPerWflSeqNo) {
            stringBuilder.append("and qi.delete_flag = FALSE ");
        }
        stringBuilder.append("and wfl.r_start_date > date(today) ");
        if (supervisor.length() > 0) {
            stringBuilder.append(String.format("and wfl.supervisor_name like '%s%s' ", supervisor, "%"));
        }
        if (object.length() > 0) {
            stringBuilder.append(String.format("and (wfl.r_object_id like '%s%s' or wfl.object_name like '%s%s') ", object, "%", object, "%"));
        }
        stringBuilder.append("order by wfl.r_start_date desc, qi.task_number desc");

        return getWorkflows(stringBuilder.toString());
    }

    public List<WorkflowObject> getPausedWorkflows(String supervisor, String object, boolean oneRowPerWflSeqNo) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("select distinct ");
        stringBuilder.append("wfl.r_object_id as workflow_id, wfl.object_name as workflow_name, wi.r_object_id as workitem_id, pr.object_name as pname, qi.task_name as actname, qi.task_number as act_seqno, wi.r_runtime_state as rs, wi.r_performer_name, wfl.supervisor_name, qi.event, wi.a_wq_name, wfl.r_start_date, wi.r_creation_date, act.r_object_id as actid, pr.r_object_id as pid, wfl.parent_id, qi.r_object_id as qid, wi.r_exec_os_error ");
        stringBuilder.append("from dm_workflow wfl, dm_process pr, dmi_workitem wi, dmi_queue_item qi, dm_activity act ");
        stringBuilder.append("where pr.r_object_id = wfl.process_id ");
        stringBuilder.append("and wi.r_workflow_id = wfl.r_object_id ");
        stringBuilder.append("and qi.router_id = wfl.r_object_id ");
        stringBuilder.append("and qi.item_id = wi.r_object_id ");
        stringBuilder.append("and act.r_object_id = wi.r_act_def_id ");
        if (oneRowPerWflSeqNo) {
            stringBuilder.append("and qi.delete_flag = FALSE ");
        }
        stringBuilder.append("and wi.r_runtime_state = 5 ");
        if (supervisor.length() > 0) {
            stringBuilder.append(String.format("and wfl.supervisor_name like '%s%s' ", supervisor, "%"));
        }
        if (object.length() > 0) {
            stringBuilder.append(String.format("and (wfl.r_object_id like '%s%s' or wfl.object_name like '%s%s') ", object, "%", object, "%"));
        }
        stringBuilder.append("order by wfl.r_start_date desc, qi.task_number desc");

        return getWorkflows(stringBuilder.toString());
    }

    public List<PackageObject> getPackages(String workitemId) {
        List<PackageObject> packages = new ArrayList<>();

        IDfCollection packagesCollection = null;

        try {
            IDfWorkitem workitem = (IDfWorkitem) repository.getObjectById(workitemId);
            packagesCollection = workitem.getPackages("r_component_name");
            while (packagesCollection.next()) {
                String componentId = packagesCollection.getString("r_component_id");
                String packageName = packagesCollection.getString("r_package_name");
                String packageType = packagesCollection.getString("r_package_type");
                String componentName = packagesCollection.getString("r_component_name");
                PackageObject packageObject = new PackageObject();
                packageObject.setPackageName(packageName);
                packageObject.setPackageType(packageType);
                packageObject.setComponentId(componentId);
                packageObject.setComponentName(componentName);
                if (repository.getObjectById(componentId) != null) {
                    packageObject.setPackageExists(true);
                    packageObject.setPackageIsLocked(((IDfSysObject) repository.getObjectById(componentId)).isCheckedOut());
                } else {
                    packageObject.setPackageExists(false);
                    packageObject.setPackageIsLocked(false);
                }
                packages.add(packageObject);
            }
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            if (packagesCollection != null) {
                try {
                    packagesCollection.close();
                } catch (DfException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }

        return packages;
    }

    public List<AttachmentObject> getAttachments(String workitemId) {
        List<AttachmentObject> attachments = new ArrayList<>();

        IDfCollection attachmentsCollection = null;

        try {
            IDfWorkitem workitem = (IDfWorkitem) repository.getObjectById(workitemId);
            attachmentsCollection = workitem.getAttachments();
            while (attachmentsCollection.next()) {
                String componentId = attachmentsCollection.getString("r_component_id");
                String componentName = attachmentsCollection.getString("r_component_name");
                String componentType = attachmentsCollection.getString("r_component_type");
                AttachmentObject attachmentObject = new AttachmentObject();
                attachmentObject.setName(componentName);
                attachmentObject.setType(componentType);
                attachmentObject.setId(componentId);
                if (repository.getObjectById(componentId) != null) {
                    attachmentObject.setExists(true);
                    attachmentObject.setLocked(((IDfSysObject) repository.getObjectById(componentId)).isCheckedOut());
                } else {
                    attachmentObject.setExists(false);
                    attachmentObject.setLocked(false);
                }
                attachments.add(attachmentObject);
            }
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            if (attachmentsCollection != null) {
                try {
                    attachmentsCollection.close();
                } catch (DfException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }

        return attachments;
    }

    public TreeItem getVariables(String workflowId) {
        TreeItem variables = new TreeItem(new WorkflowVariable("Variables", "..."));
        variables.setExpanded(true);

        try {
            IDfPersistentObject object = session.getObject(new DfId(workflowId));
            if (object instanceof IDfWorkflowEx) {
                TreeItem stringVariables = makeTreeItem("String", String.format("SELECT object_name, string_value as var_value FROM dm_dbo.dmc_wfsd_element_string_lsp where workflow_id = '%s' and string_value is not nullstring", workflowId));
                TreeItem integerVariables = makeTreeItem("Integer", String.format("SELECT object_name, int_value as var_value FROM dm_dbo.dmc_wfsd_element_integer_lsp where workflow_id = '%s' and int_value is not nullstring", workflowId));
                TreeItem dateVariables = makeTreeItem("Date", String.format("SELECT object_name, date_value as var_value FROM dm_dbo.dmc_wfsd_element_date_lsp where workflow_id = '%s' and date_value is not nulldate", workflowId));
                TreeItem doubleVariables = makeTreeItem("Double", String.format("SELECT object_name, double_value as var_value FROM dm_dbo.dmc_wfsd_element_double_lsp where workflow_id = '%s' and double_value is not nullstring", workflowId));
                TreeItem booleanVariables = makeTreeItem("Boolean", String.format("SELECT object_name, boolean_value as var_value FROM dm_dbo.dmc_wfsd_element_boolean_lsp where workflow_id = '%s' and boolean_value is not nullstring", workflowId));

                variables.getChildren().addAll(stringVariables, integerVariables, dateVariables, doubleVariables, booleanVariables);
            }
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return variables;
    }

    private TreeItem makeTreeItem(String type, String query) throws DfException {
        TreeItem treeItem = new TreeItem(new WorkflowVariable(String.format("%s variables", type), "..."));
        treeItem.setExpanded(true);

        IDfCollection variablesColl = repository.query(query);
        while (variablesColl.next()) {
            String varName = variablesColl.getString("object_name");
            String varValue = variablesColl.getString("var_value");
            TreeItem item = new TreeItem(new WorkflowVariable(varName, varValue));
            treeItem.getChildren().add(item);
        }

        return treeItem;
    }
}
