package nl.bos.tools;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfWorkflowBuilder;
import com.documentum.fc.common.*;

import java.util.Date;

public class StartWorkflowMultiPackage extends StartWorkflow {

    private StartWorkflowMultiPackage(String repository, String username, String password) {
        super(repository, username, password);
    }

    public static void main(String[] args) {
        new StartWorkflowMultiPackage(args[0], args[1], args[2]);
    }

    @Override
    protected IDfId startWorkflow() throws DfException {
        IDfWorkflowBuilder wfBldrObj = session.newWorkflowBuilder(new DfId("4b12d5918000312c")); //dm_process.r_object_id
        wfBldrObj.initWorkflow();
        IDfId workflowId = wfBldrObj.runWorkflow();

        IDfList objList = new DfList();
        IDfSysObject sysObject = (IDfSysObject) session.newObject("dm_sysobject");
        sysObject.setObjectName(String.format("test_m1_%s", String.valueOf(new Date().getTime())));
        sysObject.setSubject("process_test");
        sysObject.save();

        IDfSysObject sysObject2 = (IDfSysObject) session.newObject("dm_sysobject");
        sysObject2.setObjectName(String.format("test_m2_%s", String.valueOf(new Date().getTime())));
        sysObject2.setSubject("process_test");
        sysObject2.save();

        //select r_object_id, object_name, subject from dm_sysobject where subject = 'process_test'
        //delete dm_sysobject objects where subject = 'process_test'

        objList.append(sysObject.getObjectId());
        objList.append(sysObject2.getObjectId());

        wfBldrObj.addPackage("Auto-Activity-1", "Input:0", "Package0",
                "dm_sysobject", null, false, objList);
        wfBldrObj.addPackage("Auto-Activity-1", "Input:0", "Package1",
                "dm_sysobject", null, false, objList);

        return workflowId;
    }
}
