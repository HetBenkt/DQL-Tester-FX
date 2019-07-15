package nl.bos.tools;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfWorkflowBuilder;
import com.documentum.fc.common.*;

import java.util.Date;

public class StartWorkflowSinglePackage extends StartWorkflow {

    private StartWorkflowSinglePackage(String repository, String username, String password) {
        super(repository, username, password);
    }

    public static void main(String[] args) {
        new StartWorkflowSinglePackage(args[0], args[1], args[2]);
    }

    @Override
    protected IDfId startWorkflow() throws DfException {
        IDfWorkflowBuilder wfBldrObj = session.newWorkflowBuilder(new DfId("4b12d591800015e0")); //dm_process.r_object_id
        wfBldrObj.initWorkflow();
        IDfId workflowId = wfBldrObj.runWorkflow();

        IDfList objList = new DfList();
        IDfSysObject sysObject = (IDfSysObject) session.newObject("dm_sysobject");
        sysObject.setObjectName(String.format("test_s1_%s", String.valueOf(new Date().getTime())));
        sysObject.setSubject("process_test");
        sysObject.save();

        //select r_object_id, object_name, subject from dm_sysobject where subject = 'process_test'
        //delete dm_sysobject objects where subject = 'process_test'

        objList.append(sysObject.getObjectId());
        wfBldrObj.addPackage("Auto-Activity-1", "Input:0", "Package0",
                "dm_sysobject", null, false, objList);

        return workflowId;
    }
}
